//
//  SuggestionsViewModel.swift
//  PremierInn
//
//  Created by Marcello Mascia on 10/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import CoreLocation
import Foundation
import MapKit
import UIKit

protocol Suggestion: MKAnnotation {
	var title: String? { get }
	var subtitle: String? { get }
    var iconName: String? { get }
    var rangeOfSearchTerm: NSRange? { get }
	var coordinate: CLLocationCoordinate2D { get }
    var isHotel: Bool { get }
    var identifier: String? { get }
}

protocol SuggestionsViewModelDelegate: class {
    func suggestionsViewWillBeginDragging(_ viewModel: SuggestionsViewModel)
    func suggestionsViewDidSelectSuggestion(_ suggestion: Suggestion)
    func resetRecentSearches()
    func findHotelNearMe()
}

struct SuggestionGroup {
    let suggestions: [Suggestion]
    let title: String
    let expanded: Bool
    let hasAction: Bool
}

class SuggestionsViewModel: NSObject {
    private let cellBorderWidth: CGFloat = 0.5
    private let sectionHeaderHeight: CGFloat = 35
    private let resultCellHeight: CGFloat = 55
    private let expandCellHeight: CGFloat = 39
    private let closedRowCount = 1
    private let openRowsLimit = 10

    weak var delegate: SuggestionsViewModelDelegate?
    var suggestionGroups: [SuggestionGroup]?
    var suggestions: [Suggestion]? {
        didSet {
            var groups = [SuggestionGroup]()

            if let hotels = suggestions?.filter({ $0.isHotel == true }), !hotels.isEmpty {
                groups.append(SuggestionGroup(
                    suggestions: hotels,
                    title: PILocalizedString("Hotels", comment: ""),
                    expanded: false,
                    hasAction: false
                ))
            }
            if let places = suggestions?.filter({ $0.isHotel == false }), !places.isEmpty {
                groups.append(SuggestionGroup(
                    suggestions: places,
                    title: PILocalizedString("Places", comment: ""),
                    expanded: true,
                    hasAction: false
                ))
            }

            suggestionGroups = groups
        }
    }
	var headerTitle: String?

    private func indexPathIsExpandCell(indexPath: IndexPath) -> Bool {
        guard indexPath.section > 0 else { return false }
        guard let suggestionGroup = suggestionGroups?[(indexPath.section - 1)] else { return false }

        return indexPath.row == 1 && suggestionGroup.expanded == false ? true : false
    }

    private func expandSection(section: Int) {
        guard let suggestionGroup = suggestionGroups?[(section - 1)] else { return }

        suggestionGroups?[(section - 1)] = SuggestionGroup(
            suggestions: suggestionGroup.suggestions,
            title: suggestionGroup.title,
            expanded: true,
            hasAction: suggestionGroup.hasAction
        )
    }
}

extension SuggestionsViewModel: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        guard section > 0 else { return UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 14)) }
        guard let header = tableView
              .dequeueReusableHeaderFooterView(withIdentifier: String(describing: SimpleHeaderWithActionLabel
            .self)) as? SimpleHeaderWithActionLabel else { return nil }
        guard let group = suggestionGroups?[(section - 1)] else { return nil }

        header.delegate = self
        header.titleLabel.text = group.title
        header.actionButton.setTitle(PILocalizedString("Clear", comment: ""), for: .normal)
        header.actionButton.isHidden = group.hasAction ? false : true

        return header
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        guard section > 0 else { return 14 }
        return sectionHeaderHeight
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        ((suggestionGroups?.count ?? 0) + 1)
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard section > 0 else { return 1 }
        guard let suggestionGroup = suggestionGroups?[(section - 1)] else { return 0 }

        if suggestionGroup.expanded == false {
            return suggestionGroup.suggestions.count > closedRowCount ? closedRowCount + 1 : suggestionGroup.suggestions
                .count
        }

        return (0...openRowsLimit).clamp(suggestionGroup.suggestions.count)
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPathIsExpandCell(indexPath: indexPath) == true {
            if let cell: SuggestionExpandCell = tableView.dequeueCell(for: indexPath) {
                cell.borders.width = cellBorderWidth
                cell.borders.top.width = 0

                return cell
            }
        }

        guard let cell: SuggestionTableViewCell = tableView.dequeueCell(for: indexPath) else { return UITableViewCell() }

        switch indexPath.row {
        case 0:
            cell.borders.width = cellBorderWidth

        default:
            cell.borders.width = cellBorderWidth
            cell.borders.top.width = 0
        }

        if indexPath.section == 0 {
            cell.textLabel?.text = PILocalizedString("Find hotels near me", comment: "")
            cell.detailTextLabel?.text = nil
            cell.imageView?.image = UIImage(named: "location")
            cell.imageView?.tintColor = .premierInnPurple

            return cell
        }

        if let suggestion = suggestionGroups?[(indexPath.section - 1)].suggestions[indexPath.row] {
            cell.textLabel?.text = suggestion.title
            cell.textLabel?.boldenText(exceptTextInRange: suggestion.rangeOfSearchTerm)
            cell.detailTextLabel?.text = suggestion.subtitle
            cell.imageView?.image = nil

            if let iconName = suggestion.iconName {
                cell.imageView?.image = UIImage(named: iconName)
            }

            if let accIDName = suggestion.title?.capitalized.replacingOccurrences(of: " ", with: "") {
                cell.accessibilityIdentifier = accIDName + "SuggestionAcc"
            }
        }

        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard indexPath.section > 0 else {
            delegate?.findHotelNearMe()
            return
        }

        guard !indexPathIsExpandCell(indexPath: indexPath) else {
            expandSection(section: indexPath.section)
            tableView.reloadSections([indexPath.section], with: .bottom)

            return
        }

        guard let suggestion = suggestionGroups?[(indexPath.section - 1)].suggestions[indexPath.row] else { return }

        delegate?.suggestionsViewDidSelectSuggestion(suggestion)
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        indexPathIsExpandCell(indexPath: indexPath) ? expandCellHeight : resultCellHeight
    }
}

extension SuggestionsViewModel: UIScrollViewDelegate {
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        delegate?.suggestionsViewWillBeginDragging(self)
    }
}

extension SuggestionsViewModel: SimpleHeaderWithActionLabelDelegate {
    func actionLabelDidTap(header: SimpleHeaderWithActionLabel) {
        delegate?.resetRecentSearches()
    }
}
