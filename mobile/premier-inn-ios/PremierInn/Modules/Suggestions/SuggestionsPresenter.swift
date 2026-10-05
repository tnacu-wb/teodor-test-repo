//
//  SuggestionsPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/12/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import CoreLocation
import SimpleNetwork
import UIKit

enum SuggestionsSource {
	case remote
	case topDestinations
	case history
}

private enum TableConfig {
	static let cellBorderWidth: CGFloat = 0.5
	static let sectionHeaderHeight: CGFloat = 34
	static let sectionFooterHeight: CGFloat = 10
	static let resultCellHeight: CGFloat = 55
	static let expandCellHeight: CGFloat = 39
	static let closedRowCount = 1
	static let openRowsLimit = 10
}

protocol SuggestionsPresenterProtocol {
    func searchTextDidChange(_ text: String)
    func viewIsReady()
    var selectedSuggestion: Suggestion? { get }

    var tableDelegate: UITableViewDelegate? { get }
    var tableDatasource: UITableViewDataSource? { get }
}

protocol SuggestionsViewProtocol: AnyObject {
    var parentNavigationController: UINavigationController? { get }

	func showSuggestionsTable()
	func hideSuggestionsTable()
	func reloadSection(at indexSet: IndexSet)
	func showLoadingIndicator()
	func hideLoadingIndicator()
	func trackAnalytics(searchTerm: String)
	func toggleNoResultsMessage(visible: Bool)
	func reloadSuggestionsTable()
	func suggestionDidSelect(withText text: String?)
	func presentAlertController(_ controller: UIAlertController)
	func setSuggestionTitle(_ title: String?)
	func deselectSelectedCellIfAny()
	func showError(message: String)
}

protocol SuggestionsInteractorProtocol {
	func cancelConnections()
	func loadRemoteSuggestions(
		searchTerm: String,
		completion: @escaping (_ suggestions: [PISuggestion]?, _ error: Error?) -> Void
	)
	func loadLocalSuggestions(completion: @escaping (_ suggestions: [Suggestion]?, _ source: SuggestionsSource) -> Void)
	func saveSuggestionToRecentSearches(_ suggestion: Suggestion)
	func clearRecenteSearches()
    func updateCoordinates(
    	for googlePlacesSuggestion: Suggestion,
    	completion: @escaping (_ suggestion: Suggestion?, _ success: Bool) -> Void
    )
}

class SuggestionsPresenter {
	weak var view: SuggestionsViewProtocol?

	var interactor: SuggestionsInteractorProtocol?

	static let searchDelay: TimeInterval = 0.5

	private var suggestion: Suggestion?
	private var viewModel: FormekaViewModel?
	private let locationManager = LocationManager()
	private var shouldRespondToLocationUpdates = false
	private var timer: Timer?

	init(suggestion: Suggestion?) {
		self.suggestion = suggestion
	}

	func didSelect(suggestion: Suggestion) {
		// Do not save user's location in recent searches
		// Not very proud of this check, but could not find a better way...
		if suggestion.title != PILocalizedString("userCoordinateName", comment: "User's coordinate name") {
			interactor?.saveSuggestionToRecentSearches(suggestion)
		}

		self.suggestion = suggestion

        guard CLLocationCoordinate2DIsValid(suggestion.coordinate) else {
            updateCoordinatesUsingPlaceId(for: suggestion)
            return
        }

		view?.suggestionDidSelect(withText: suggestion.title)
		view?.hideSuggestionsTable()
	}

    private func updateCoordinatesUsingPlaceId(for suggestion: Suggestion) {
        interactor?.updateCoordinates(for: suggestion) { [weak self] suggestion, success in
            guard success, let updatedSuggestion = suggestion else {
                DispatchQueue.main.async {
                    self?.view?.showError(message: PILocalizedString(
                    	"Failed to load results for selected option. Please try again.",
                    	comment: ""
                    ))
                }
                return
            }

            self?.suggestion = updatedSuggestion
            DispatchQueue.main.async {
                self?.view?.suggestionDidSelect(withText: updatedSuggestion.title)
                self?.view?.hideSuggestionsTable()
            }
        }
    }

	@objc private func loadRemoteSuggestions(timer: Timer) {
		guard let searchTerm = timer.userInfo as? String else { return }

		view?.showLoadingIndicator()

		interactor?.cancelConnections()
		interactor?.loadRemoteSuggestions(searchTerm: searchTerm) { (results, error) in
            BARTDowntimeHandler.handle(error: error, withParentNavigationController: self.view?.parentNavigationController)

			self.view?.hideLoadingIndicator()

			guard let results = results else { return }

			let suggestions = results.map { $0 as Suggestion }

			self.viewModel = self.loadViewModel(source: .remote, suggestions: suggestions)

			if suggestions.isEmpty {
				self.view?.trackAnalytics(searchTerm: searchTerm)
			}

            self.view?.toggleNoResultsMessage(visible: suggestions.isNotEmpty ? false : true)
			self.view?.reloadSuggestionsTable()
		}
	}

	private func loadLocalSuggestions() {
		interactor?.cancelConnections()
		interactor?.loadLocalSuggestions { (results, source) in
			guard let results = results else { return }

			let suggestions = results.map { $0 as Suggestion }

			self.viewModel = self.loadViewModel(source: source, suggestions: suggestions)

            self.view?.toggleNoResultsMessage(visible: suggestions.isNotEmpty ? false : true)
			self.view?.reloadSuggestionsTable()
		}
	}

	private func findUserLocation() {
		shouldRespondToLocationUpdates = true

		locationManager.delegate = self
		locationManager.findUserLocation()
	}

	private func handleLocationManagerUpdates(completion: @escaping () -> Void) {
		guard shouldRespondToLocationUpdates else { return }

		shouldRespondToLocationUpdates = false

		locationManager.delegate = nil

		DispatchQueue.main.async {
			completion()
		}
	}

	private func showHiddenSuggestions(_ hiddenSuggestions: [Suggestion], tag: String) {
		guard let indexPath = viewModel?.remove(rowNamed: tag) else { return }

		for (index, suggestionRow) in suggestionRows(forSuggestions: hiddenSuggestions).enumerated() {
			viewModel?.add(row: suggestionRow, at: IndexPath(row: indexPath.row + index, section: indexPath.section))
		}

		view?.reloadSection(at: [indexPath.section])
	}
}

// MARK: - Table View Model

extension SuggestionsPresenter {
	private func loadViewModel(source: SuggestionsSource, suggestions: [Suggestion]) -> FormekaViewModel {
		var sections: [FormekaModelSection] = []

		sections.append(nearMeSection())

		switch source {
		case .history, .topDestinations:
			let title = source == .history ? PILocalizedString("recentSearchesTitle", comment: "") : PILocalizedString(
				"topDestinationsTitle",
				comment: ""
			)
			sections.append(suggestionsSection(
				withSuggestions: suggestions,
				header: title,
				expanded: true,
				hasAction: source == .history
			))

		case .remote:
			let hotels = suggestions.filter { $0.isHotel == true }

            if hotels.isNotEmpty {
				sections.append(suggestionsSection(
					withSuggestions: hotels,
					header: PILocalizedString("hotelsTitle", comment: ""),
					expanded: false
				))
			}

			let places = suggestions.filter { $0.isHotel == false }

            if places.isNotEmpty {
				sections.append(suggestionsSection(
					withSuggestions: places,
					header: PILocalizedString("placesTitle", comment: ""),
					expanded: true
				))
			}
		}

		return FormekaViewModel(sections: sections)
	}

	private func nearMeSection() -> FormekaModelSection {
		let row = FormekaModelRow(
			cellSetup: { indexPath, _, table in
			guard let cell: SuggestionTableViewCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.textLabel?.font = UIFont.Body()
			cell.textLabel?.text = PILocalizedString("landingFindHotelsNearMe", comment: "Landing screen: find hotels button title")
			cell.detailTextLabel?.text = nil
			cell.imageView?.image = #imageLiteral(resourceName: "location")
			cell.imageView?.tintColor = .BasePurple
			cell.borders.width = TableConfig.cellBorderWidth
            cell.contentView.backgroundColor = .BaseWhite
            cell.backgroundColor = .BaseGrey
            cell.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.searchHotelsNearMeButton
            cell.accessibilityTraits = .button

			return cell
			},
			didSelect: { [unowned self] _, _ in
				findUserLocation()
            }
		)

		return FormekaModelSection(header: simpleFooter(), rows: [row], footer: simpleFooter())
	}

	private func suggestionsSection(
		withSuggestions suggestions: [Suggestion],
		header: String?,
		expanded: Bool,
		hasAction: Bool = false
	) -> FormekaModelSection {
		var rows: [FormekaModelRow] = []

		if expanded {
			rows.append(contentsOf: suggestionRows(forSuggestions: suggestions))
		} else if let suggestion = suggestions.first {
			let hiddenSuggestions = Array(suggestions.dropFirst(1))
			let dropCount = (0...Int.max).clamp(suggestions.count - TableConfig.openRowsLimit)

			rows.append(contentsOf: suggestionRows(forSuggestions: [suggestion]))
			rows.append(expandRow(tag: String(describing: header), hiddenSuggestions: Array(hiddenSuggestions.dropLast(dropCount))))
		}

		return FormekaModelSection(header: simpleHeader(title: header, hasAction: hasAction), rows: rows, footer: simpleFooter())
	}

	private func suggestionRows(forSuggestions suggestions: [Suggestion]) -> [FormekaModelRow] {
		suggestions.compactMap { suggestion -> FormekaModelRow? in
			FormekaModelRow(
				cellSetup: { indexPath, _, table in
				guard let cell: SuggestionTableViewCell = table.dequeueCell(for: indexPath) else { return nil }

				cell.textLabel?.numberOfLines = 1
                cell.textLabel?.font = UIFont.Body()
				cell.textLabel?.text = suggestion.title
                cell.textLabel?.boldenText(
                	exceptTextInRange: suggestion.rangeOfSearchTerm,
                	font: .Body(),
                	boldFont: .Body_Semibold()
                )
                cell.detailTextLabel?.font = UIFont.SubtextSmall()
                cell.detailTextLabel?.text = suggestion.subtitle
                cell.contentView.backgroundColor = .BaseWhite
                cell.backgroundColor = .BaseGrey
                cell.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.searchResult
                cell.accessibilityTraits = .button

                switch suggestion.type {
                case .place:
                    cell.textLabel?.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.placeLabel
                case .location:
                    cell.textLabel?.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.hotelLabel
                    cell.imageView?.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.PIHotelIcon
                }

				cell.borders.width = TableConfig.cellBorderWidth
				if indexPath.row > 0 {
					cell.borders.top.width = 0
				}

				if let iconName = suggestion.iconName {
					cell.imageView?.image = UIImage(named: iconName)
				} else {
					cell.imageView?.image = nil
				}

				return cell
				},
				didSelect: { [unowned self] _, _ in
					didSelect(suggestion: suggestion)
                }
			)
		}
	}

	private func expandRow(tag: String, hiddenSuggestions: [Suggestion]) -> FormekaModelRow {
        FormekaModelRow(
        	tag: tag,
        	cellSetup: { indexPath, _, table in
                guard let cell: SuggestionExpandCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.borders.width = TableConfig.cellBorderWidth
                cell.borders.top.width = 0
                cell.contentView.backgroundColor = .BaseWhite
                cell.backgroundColor = .BaseGrey

                return cell
            },
        	didSelect: { [unowned self] _, _ in
				showHiddenSuggestions(hiddenSuggestions, tag: tag)
            }
        )
	}

	private func simpleHeader(title: String?, hasAction: Bool) -> FormekaModelHeaderFooter {
		FormekaModelHeaderFooter(height: TableConfig.sectionHeaderHeight, viewSetup: { [unowned self] _, table in
            guard let header: SimpleHeaderWithActionLabel = table.headerFooterView() else { return nil }

			header.delegate = self
			header.titleLabel.text = title
			header.actionButton.setTitle(PILocalizedString("clearSearchesButtonTitle", comment: ""), for: .normal)
			header.actionButton.isHidden = hasAction ? false : true
            header.contentView.backgroundColor = .BaseGrey

            switch title {
            case PILocalizedString("recentSearchesTitle", comment: ""):
                header.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.recentSearchesHeader
                header.actionButton.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.clearSearchButton
            case PILocalizedString("hotelsTitle", comment: ""):
                header.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.hotelsHeader
            case PILocalizedString("placesTitle", comment: ""):
                header.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.placesHeader
            default:
                break
            }

			return header
		})
	}

	private func simpleFooter() -> FormekaModelHeaderFooter {
		FormekaModelHeaderFooter(height: TableConfig.sectionFooterHeight, viewSetup: { _, table in
			let view: SimpleFooter? = table.headerFooterView()
			view?.lineView.backgroundColor = UIColor.BaseGrey

			return view
		})
	}
}

extension SuggestionsPresenter: SuggestionsPresenterProtocol {
	var selectedSuggestion: Suggestion? { suggestion }
	var tableDelegate: UITableViewDelegate? { viewModel }
	var tableDatasource: UITableViewDataSource? { viewModel }

	func viewIsReady() {
		view?.setSuggestionTitle(suggestion?.title)

		if suggestion == nil {
			searchTextDidChange("")
		} else if let suggestion = suggestion {
			if CLLocationCoordinate2DIsValid(suggestion.coordinate) {
				view?.hideSuggestionsTable()
			} else {
				searchTextDidChange(suggestion.title ?? "")
			}
		} else {
			view?.hideSuggestionsTable()
		}
	}

	func searchTextDidChange(_ text: String) {
		timer?.invalidate()

		if text.count >= Constants.remoteSuggestionsMinCharacters {
            timer = Timer.scheduledTimer(
            	timeInterval: SuggestionsPresenter.searchDelay,
            	target: self,
            	selector: #selector(loadRemoteSuggestions),
            	userInfo: text,
            	repeats: false
            )
		} else {
            self.suggestion = nil
			loadLocalSuggestions()
		}

		view?.showSuggestionsTable()
	}
}

extension SuggestionsPresenter: SimpleHeaderWithActionLabelDelegate {
	func actionLabelDidTap(header: SimpleHeaderWithActionLabel) {
		interactor?.clearRecenteSearches()

		searchTextDidChange(suggestion?.title ?? "")
	}
}

extension SuggestionsPresenter: LocationManagerDelegate {
	func locationManagerDidFind(userLocation location: CLLocation, locationManager: LocationManager) {
		handleLocationManagerUpdates { [weak self] in
			let suggestion = PISuggestion(coordinate: location.coordinate)

			self?.didSelect(suggestion: suggestion)
		}
	}

	func locationManagerAuthorizationDenied(locationManager: LocationManager, shouldShowAlert: Bool) {
		handleLocationManagerUpdates { [weak self] in
			self?.view?.deselectSelectedCellIfAny()

			if shouldShowAlert {
				self?.view?.presentAlertController(locationManager.authDeniedSettingsAlertController())
			}
		}
	}

	func locationManagerDidFailWithError(locationManager: LocationManager, error: NSError) {
		handleLocationManagerUpdates { [weak self] in
			self?.view?.deselectSelectedCellIfAny()
			self?.view?.presentAlertController(locationManager.alertControllerWithError(error))
		}
	}
}
