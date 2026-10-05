//
//  DashboardViewController+Formeka.swift
//  PremierInn
//
//  Created by Santa Gurung on 03/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

enum DashboardViewRow: String {
    case notification
}

extension DashboardViewController {
    func viewModelSections(with dashboardViewModel: DashboardViewModel) -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        sections.append(dragIndicatorSection())

        if let notificationViewModel = dashboardViewModel.notification, dashboardViewModel.notificationIsDismissed == false {
            sections.append(notificationSection(notificationViewModel: notificationViewModel))
        }

        if let upcomingBooking = dashboardViewModel.upcomingBooking {
            sections.append(upcomingBookingSection(upcomingBooking: upcomingBooking))
        }

        if let recentSearchesViewModel = dashboardViewModel.recentSearchesViewModel,
           recentSearchesViewModel.recentSearchesViewModels.isNotEmpty,
           dashboardViewModel.recentSearches == true || dashboardViewModel.pastSearches == true {
            sections.append(recentSearchesSection(recentSearchesViewModel: recentSearchesViewModel))
        }

        if let destinationCards = dashboardViewModel.destinationCards {
            sections.append(destinationSection(title: dashboardViewModel.destinationHeading, destinations: destinationCards))
        }
        if let contentCards = dashboardViewModel.contentCards {
            sections.append(contentSection(contents: contentCards))
        }
        if let promoCards = dashboardViewModel.promoCards {
            sections.append(promoSection(items: promoCards))
        }

        if let frequentBookings = dashboardViewModel.frequentBookings,
           let frequentBookingsSection = frequentlyBookedHotelsSection(frequentBookings: frequentBookings) {
            sections.append(frequentBookingsSection)
        }

        return sections
    }

    func dragIndicatorSection() -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        let dragIndicatorRow = FormekaModelRow { indexPath, _, table in
            guard let cell: SwiftUIContainerTableViewCell<DragIndicatorView> = table.dequeueCell(for: indexPath)
                else { return nil }
            cell.configure(with: DragIndicatorView())
            return cell
        }
        rows.append(dragIndicatorRow)

        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: spacerHeaderFooter(height: 8, backgroundColor: UIColor.BaseWhite)
        )
    }

    // MARK: Notification/banner

    func notificationSection(notificationViewModel: NotificationViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(iconInfoRow(
            tag: DashboardViewRow.notification.rawValue,
            attributedString: notificationViewModel.message,
            backgroundColor: .BaseWhite,
            style: notificationViewModel.style,
            url: notificationViewModel.link,
            openLinkExternally: notificationViewModel.openLinkInApp == false,
            isDismissible: notificationViewModel.dismissible,
            isDismissed: {
            self.eventHandler?.dismissNotification()
        }
        ))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    // MARK: Content

    func promoSection(items: [PromoCardViewModel]) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        let row = FormekaModelRow { indexPath, _, table in
            guard let cell: SwiftUIContainerTableViewCell<PromoListView> = table.dequeueCell(for: indexPath)
                else { return nil }
            cell.configure(with: PromoListView(items: items))
            return cell
        }
        rows.append(row)

        return FormekaModelSection(
            header: spacerHeaderFooter(height: 8, backgroundColor: UIColor.BaseWhite),
            rows: rows,
            footer: spacerHeaderFooter(height: 16, backgroundColor: UIColor.BaseWhite)
        )
    }

    func contentSection(contents: [ContentCardViewModel]) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        let storyRow = FormekaModelRow { indexPath, _, table in
            guard let cell: SwiftUIContainerTableViewCell<ContentListView> = table.dequeueCell(for: indexPath)
                else { return nil }
            cell.configure(with: ContentListView(items: contents))
            return cell
        }
        rows.append(storyRow)

        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: spacerHeaderFooter(height: 16, backgroundColor: UIColor.BaseWhite)
        )
    }

    func destinationSection(title: String?, destinations: [DestinationCardViewModel]) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        let carouselRow = FormekaModelRow { [weak self] indexPath, _, table in
            guard let cell: SwiftUIContainerTableViewCell<DestinationListView> = table.dequeueCell(for: indexPath)
                else { return nil }

            var destinationListView = DestinationListView(title: title, destinations: destinations)
            destinationListView.onNavigateToSRP = { [weak self] suggestion in
                (self?.eventHandler as? DashboardPresenter)?.navigateToSRP(with: suggestion)
            }

            cell.configure(with: destinationListView)
            return cell
        }
        rows.append(carouselRow)

        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: spacerHeaderFooter(height: 16, backgroundColor: UIColor.BaseWhite)
        )
    }

    // MARK: - Upcoming Booking

    func upcomingBookingSection(upcomingBooking: DashboardUpcomingBooking) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(upcomingBookingRow(upcomingBooking: upcomingBooking))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func upcomingBookingRow(upcomingBooking: DashboardUpcomingBooking) -> FormekaModelRow {
        FormekaModelRow { indexPath, _, table in
            guard let cell: DashboardUpcomingBookingView = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.update(with: upcomingBooking)

            return cell
        }
    }

    // MARK: - Recent searches

    func recentSearchesSection(recentSearchesViewModel: RecentSearchesViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        let recentSearches = recentSearchesViewModel.recentSearchesViewModels
        rows.append(contentsOf: recentSearches.enumerated().compactMap { (index, search) in
            searchRow(index: index, search: search)
        })

        return FormekaModelSection(
            header: recentSearchesHeader(recentSearchesViewModel: recentSearchesViewModel),
            rows: rows,
            footer: nil
        )
    }

    func recentSearchesHeader(recentSearchesViewModel: RecentSearchesViewModel) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 44, viewSetup: {  _, table in
            guard let header: SimpleHeaderWithActionLabel = table.headerFooterView() else { return nil }

            header.delegate = self
            header.titleLabel.text = recentSearchesViewModel
                .searchesComponentType == .recent ? PILocalizedString("recentSearchesTitle") :
                PILocalizedString("pastSearchesTitle")
            header.titleLabel.textColor = .BasePurple
            header.titleLabel.font = .Heading2_ExtraBold()
            header.titleLabel.accessibilityIdentifier = "recentSearchTitle"
            header.titleLabel.accessibilityTraits.insert(.header)
            header.actionButton.configurationUpdateHandler = { button in
                var config = UIButton.Configuration.plain()
                config.title = PILocalizedString("clearSearchesButtonTitle")
                config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                    var title = attribute
                    title.font = UIFont.BodySmall_Medium()
                    return title
                }
                config.baseForegroundColor = .BasePurple
                button.configuration = config
            }
            header.actionButton.accessibilityIdentifier = "recentSearchClear"
            header.contentView.backgroundColor = .BaseWhite

            return header
        })
    }

    func searchRow(index: Int, search: RecentSearchViewModel) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: RecentSearchCell = table.dequeueCell(for: indexPath) else { return UITableViewCell() }

            cell.textLabel?.text = search.title
            cell.textLabel?.font = UIFont.Body_Medium()
            cell.textLabel?.textColor = .TintD1

            cell.backgroundColor = .BaseWhite
            cell.contentView.backgroundColor = .BaseWhite

            cell.imageView?.image = search.iconOptions.image
            cell.imageView?.tintColor = search.iconOptions.tintColor

            cell.borders.width = 0
            cell.borders.bottom.width = 0.3

            cell.detailTextLabel?.attributedText = search.criteriaSummary
            cell.detailTextLabel?.font = .BodySmall()
            cell.detailTextLabel?.textColor = .TintD1

            return cell
        }, didSelect: { [unowned self] _, _ in
            eventHandler?.selectedRecentSearch(at: index)
        })
    }

    // MARK: - Frequently Booked Hotels

    func frequentlyBookedHotelsSection(frequentBookings: [FrequentBookingViewModel]) -> FormekaModelSection? {
        var rows: [FormekaModelRow] = []

        guard frequentBookings.isNotEmpty else { return nil }

        let row = FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: DashboardFrequentBookingView = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.update(array: frequentBookings)

            return cell
        })

        rows.append(row)

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }
}
