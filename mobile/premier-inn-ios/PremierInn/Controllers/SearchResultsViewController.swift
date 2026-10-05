//
//  SearchResultsViewController.swift
//  PremierInn
//
//  Created by Freddie Parks on 08/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import CoreLocation
import UIKit
import Formeka

protocol SearchResultsViewControllerOutput: class {
    func resultDidTap(sender: UIViewController, hotel: Hotel, suggestion: Suggestion, searchIndex: Int)
    func previewingContext(sender: UIViewController, hotel: Hotel, suggestion: Suggestion) -> UIViewController
    func updateResults(withSuggestion suggestion: Suggestion, criteria: Criteria, sender: UIViewController)
}

enum HotelSearchError: Error {
    case loadingStartedAlready
    case loadMoreNotNeeded(String)
    case firstViewModelRowDoesNotExist
    case firstCellDoesNotExist
    case showingErrorMessageAlready
}

class SimpleHeader: UITableViewHeaderFooterView {
    @IBOutlet weak var titleLabel: UILabel!
}

class SearchResultsViewController: BaseViewController {
    weak var controllerOutput: SearchResultsViewControllerOutput?

    override var screenName: String { PIAnalytics.StateNames.searchResults }
    override var screenType: String { PIAnalytics.StateTypes.lookToBook }
    override var trackScreen: Bool { false }
    override var event: String? { "scOpen" }

    @IBOutlet var errorMessageView: UIView!
    @IBOutlet weak var errorMessageLabel: UILabel! {
        didSet {
            errorMessageLabel.text = PILocalizedString(
                "searchResultsFullyBookedFallbackMessage",
                comment: "Search results: hotel fully booked message fallback"
            )
            errorMessageLabel.textColor = .pinkishRed
            errorMessageLabel.font = UIFont.premierInn(ofSize: 16)
        }
    }
    @IBOutlet weak var table: UITableView! {
        didSet {
            table.backgroundColor = .whiteTwo
            table.rowHeight = UITableViewAutomaticDimension
            table.estimatedRowHeight = 325

            registerForPreviewing(with: self, sourceView: table)

            registerTableElements()
        }
    }
    @IBOutlet weak var loadingIndicator: UIActivityIndicatorView!

    internal var viewModel: TableViewModel<TableViewModelRow>?
    internal var selectedHotel: Hotel?
    internal var suggestion: Suggestion
    internal var isLoadingMoreResults = false
    internal var shouldShowTimeoutError = false
    internal var connectionStatusTimer: Timer?
    internal var requestsManager = RequestsManager()
    internal var sortingType: Int = 0
    internal var sorting: AvailabilitiesSorting {
        guard let result = AvailabilitiesSorting(rawValue: sortingType) else { return .distance }

        return result
    }
    internal var isPaginationAvailable = true
    private(set) var availabilitiesResponse: AvailabilitiesResponse?
    internal var unavailableHotelCode: String?
    internal var criteria: Criteria

    private var criteriaSummaryView: CriteriaSummaryView? {
        let view: CriteriaSummaryView? = .fromNib()
        if let bounds = navigationController?.navigationBar.bounds {
            view?.frame = bounds
            view?.frame.size.height = 34 // Arbitrary size because it looks pretty
        }
        view?.titleLabel.text = suggestion.title
        view?.subtitleLabel.text = criteria.titleSummary

        return view
    }

    deinit {
        requestsManager.cancelConnections()
        connectionStatusTimer?.invalidate()
    }

    init(
        availabilitiesResponse: AvailabilitiesResponse?,
        suggestion: Suggestion,
        unavailableHotelCode: String?,
        criteria: Criteria = BookingDetails.sharedInstance.criteria
    ) {
        self.availabilitiesResponse = availabilitiesResponse
        self.suggestion = suggestion
        self.unavailableHotelCode = unavailableHotelCode
        self.criteria = criteria

        super.init(nibName: String(describing: SearchResultsViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.rightBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .edit,
            target: self,
            action: #selector(editButtonDidTap)
        )

        showErrorMessageIfNeeded()

        if let response = availabilitiesResponse, !response.hotels.isEmpty {
            handleAvailabilitiesResponse(response, error: nil)
        } else {
            if loadingIndicator != nil {
                loadingIndicator.startAnimating()
            }

            requestsManager.searchHotelsAvailabilities(
                bookingDetails: BookingDetails.sharedInstance,
                suggestion: suggestion,
                page: 0,
                sorting: sorting
            ) { response, error in
                self.loadingIndicator.stopAnimating()

                self.handleAvailabilitiesResponse(response, error: error)
            }
        }

        navigationItem.titleView = criteriaSummaryView
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: animated)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        if unavailableHotelCode != nil {
            trackState(withName: PIAnalytics.StateNames.searchResultsUnavailable)
        }
    }

    internal func registerTableElements() {
        table.registerCellNib(with: HotelFullyBookedCell.self)
        table.registerCellNib(with: HotelPhotoPriceCell.self)
        table.registerCellNib(with: SpinnerCell.self)
        table.registerCellNib(with: ErrorCell.self)
        table.registerCellNib(with: SortCell.self)
        table.registerCellNib(with: GDPRBannerHeaderRow.self)
        table.registerHeaderFooterNib(with: SimpleHeader.self)
    }


    // MARK: Utilities

    func update() {
        if self.criteria.arrivalDate < Date() {
            self.criteria.arrivalDate = Date()
        }
        controllerOutput?.updateResults(withSuggestion: self.suggestion, criteria: self.criteria, sender: self)
    }

    func update(suggestion: Suggestion) {
        updateResponse(availabilitiesResponse, suggestion: suggestion, unavailableHotelCode: unavailableHotelCode)
    }

    func updateResponse(
        _ response: AvailabilitiesResponse?,
        suggestion: Suggestion,
        unavailableHotelCode: String?,
        criteria: Criteria = BookingDetails.sharedInstance.criteria
    ) {
        self.criteria = criteria
        self.unavailableHotelCode = unavailableHotelCode
        self.suggestion = suggestion

        handleAvailabilitiesResponse(response, error: nil)

        navigationItem.titleView = criteriaSummaryView

        if table.numberOfRows(inSection: 0) > 0 {
            table.scrollToRow(at: IndexPath(row: 0, section: 0), at: .top, animated: false)
        }
    }

    func update(criteria: Criteria) {
        if self.criteria.arrivalDate.isOnTheSameDateAs(date: criteria.arrivalDate) && self.criteria.nights == criteria
           .nights { return }

        guard let checkoutDate = BookingDetails.sharedInstance.criteria.checkOutDate else { return }

        let arrivalDate = BookingDetails.sharedInstance.criteria.arrivalDate
        let message = String(
            format: PILocalizedString(
                "searchResultsCriteriaUpdateMessage",
                comment: "Search results: criteria update request message (don't remove placeholders)"
            ),
            arrivalDate.localizedShortDayMonthStringFormat,
            checkoutDate.localizedShortDayMonthStringFormat
        )

        let alert = UIAlertController(
            title: PILocalizedString(
                "searchResultsCriteriaUpdateTitle",
                comment: "Search results: criteria update request title"
            ),
            message: message,
            preferredStyle: .alert
        )
        alert.addAction(UIAlertAction(
            title: PILocalizedString("Cancel", comment: "Title for alert cancel button"),
            style: .cancel
        ) { _ in
            BookingDetails.sharedInstance.criteria = self.criteria
        })
        alert.addAction(UIAlertAction(
            title: PILocalizedString(
                "searchResultsCriteriaUpdateConfirm",
                comment: "Search results: criteria update request confirm action"
            ),
            style: .default
        ) { _ in
            self.criteria = BookingDetails.sharedInstance.criteria
            self.update()
        })

        present(alert, animated: true, completion: nil)
    }

    internal func handleAvailabilitiesResponse(_ response: AvailabilitiesResponse?, error: Error?) {
        isLoadingMoreResults = false

        connectionStatusTimer?.invalidate()

        if let response = response, !response.hotels.isEmpty {
            availabilitiesResponse = response

            trackAvailability(withResponse: response, firstTen: response.hotels.isEmpty)
            logFirebase()
        } else if let error = error as NSError?, error.code == URLError.cancelled.rawValue {
            shouldShowTimeoutError = true
        } else {
            if let response = availabilitiesResponse {
                availabilitiesResponse = AvailabilitiesResponse(
                    hotels: response.hotels,
                    total: response.hotels.count,
                    pageNumber: response.pageNumber,
                    shouldPaginate: response.shouldPaginate
                )
            }
        }

        reloadData(response: response)
    }

    @objc func editButtonDidTap() {
		let controller = FlowController.sharedInstance.criteriaViewController(
		    with: criteria,
		    suggestion: suggestion,
		    delegate: self
		)

		present(UINavigationController(rootViewController: controller), animated: true, completion: nil)
    }

    private func showErrorMessageIfNeeded() {
        guard table != nil else { return }
        guard unavailableHotelCode != nil else {
            table.tableHeaderView = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: CGFloat.leastNormalMagnitude))
            return
        }

        table.tableHeaderView = errorMessageView
    }
}

extension SearchResultsViewController: CriteriaViewControllerDelegate {
	func criteriaController(
	    _ sender: CriteriaViewController,
	    didFinishWithSuggestion suggestion: Suggestion,
	    criteria: Criteria
	) {
		FlowController.sharedInstance.performSearch(with: suggestion, criteria: criteria) { (nextController, error) in
			if let error = error {
				sender.dismiss(animated: true) {
					let controller = UIAlertController(
					    title: PILocalizedString("hotelSearchAlertErrorTitle", comment: "Hotel search alert error title"),
					    message: error.localizedDescription,
					    preferredStyle: .alert
					)
					controller.addAction(UIAlertAction(
					    title: PILocalizedString("hotelSearchAlertErrorAction", comment: "Hotel search alert error action"),
					    style: .cancel
					) { _ in
					})
					self.present(controller, animated: true, completion: nil)
				}
			} else {
				sender.dismiss(animated: true, completion: nil)

				guard let nextController = nextController else { return }
				guard let navController = self.navigationController else { return }

				var stack = navController.viewControllers
				stack.removeLast()
				stack.append(nextController)

				// Animated is false to prevent a weird push effect coming from nowhere
				navController.setViewControllers(stack, animated: false)
			}
		}
	}

	func criteriaControllerDidCancel(_ sender: CriteriaViewController) {
		sender.dismiss(animated: true, completion: nil)
	}
}
