//
//  DashboardInteractor.swift
//  PremierInn
//
//  Created by Filippo Minelle on 11/12/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import SimpleNetwork
import CoreLocation
import MapKit

private enum DashboardError: LocalizedError {
    case missingDashboardComponents

    var errorDescription: String? {
        String(describing: self)
    }
}

extension UpcomingBooking: DirectionsAlertConfig {
    var coordinate: CLLocationCoordinate2D {
        map
    }
    var placeMark: MKPlacemark? {
        guard let addressDictionary = address?.postalAddressDictionary else { return nil }
        return MKPlacemark(coordinate: map, addressDictionary: addressDictionary)
    }
}

struct DashboardViewModel {
    var order: Int?
    var upcomingBooking: DashboardUpcomingBooking?
    var frequentBookings: [FrequentBookingViewModel]?
    var recentSearches: Bool?
    var pastSearches: Bool?
    var recentSearchesViewModel: RecentSearchesViewModel?
    let destinationHeading: String?
    let destinationCards: [DestinationCardViewModel]?
    let contentCards: [ContentCardViewModel]?
    let promoCards: [PromoCardViewModel]?
    let notification: NotificationViewModel?
    let notificationIsDismissed: Bool?
}

public struct FrequentBookingViewModel {
    var name: String
    var code: String
    var image: String // URL?
    var brand: HotelBrand
}

struct NotificationViewModel {
    var style: NotificationStyle
    var message: NSAttributedString
    var link: URL?
    var openLinkInApp: Bool
    var dismissible: Bool
}

public typealias DashboardHotelDetails = (code: String, brand: HotelBrand)

public extension FrequentBookingViewModel {
    var dashboardHotelDetails: DashboardHotelDetails {
        (code: code, brand: brand)
    }
}

struct SearchesViewModel: RecentSearchesViewModel {
    let searchesComponentType: SearchesComponentType
    let recentSearchesViewModels: [RecentSearchViewModel]
}

struct SearchViewModel: RecentSearchViewModel {
    let title: String
    let criteriaSummary: NSAttributedString
    let iconOptions: IconOptions
}

extension DashboardViewModel {
    // need to move all the content to the view model as components to be able to do things like this
    var isNotEmpty: Bool {
        let result = upcomingBooking != nil
        || frequentBookings?.isNotEmpty == true
        || recentSearchesViewModel?.recentSearchesViewModels.isNotEmpty == true
        || promoCards?.isNotEmpty == true
        || notification != nil

        return result
    }
}

class DashboardInteractor {
    // MARK: - Properties
    private var dataProvider: DashboardDataProvider?
    private weak var delegate: DashboardInteractorDelegate?

    var analytics: AnalyticsType = AnalyticsManager.shared

    var viewModel: DashboardViewModel? {
        let upcomingBooking = dataProvider?.dashboardUpcomingBooking
        let frequentlyBooked = dataProvider?.dashboardFrequentlyBooked
        let recentSearches = dataProvider?.dashboardRecentSearches
        let pastSearches = dataProvider?.dashboardPastSearches

        return DashboardViewModel(
            upcomingBooking: upcomingBooking,
            frequentBookings: frequentlyBooked,
            recentSearches: recentSearches,
            pastSearches: pastSearches,
            recentSearchesViewModel: recentSearchesViewModel,
            destinationHeading: dataProvider?.destinationHeading,
            destinationCards: dataProvider?.destinationCards,
            contentCards: dataProvider?.contentCards,
            promoCards: dataProvider?.promoCards,
            notification: dataProvider?.notification,
            notificationIsDismissed: self.notificationIsDismissed
        )
    }

    var directionsViewModel: DirectionsViewModel? {
        guard let upcomingBooking = dataProvider?.upcomingBooking else { return nil }
        return DirectionsViewModel.create(from: upcomingBooking)
    }

    var upcomingBookingHotelCode: String? {
        viewModel?.upcomingBooking?.hotelCode
    }

    var notificationIsDismissed = false

    private var recentSearchesViewModel: RecentSearchesViewModel {
        SearchesViewModel(
            searchesComponentType: dataProvider?.searchesComponentType ?? .recent,
            recentSearchesViewModels: recentSearchViewModels
        )
    }

    private var recentSearchViewModels: [RecentSearchViewModel] {
        let recentSearches = dataProvider?.searches ?? []

        return recentSearches.compactMap { recentSearch in
            guard let title = recentSearch.title,
                  let summary = recentSearch.criteria?.attributedSummaryString else { return nil }
            let iconOptions: IconOptions = {
                guard recentSearch.isHotel else { return (#imageLiteral(resourceName: "search.pdf"), .ColourDL3) }
                guard let brand = recentSearch.brand else { return (#imageLiteral(resourceName: "logo"), .ColourDL7) }
                switch brand {
                case .zip:
                    return (#imageLiteral(resourceName: "zipWidgetLogo"), .zipRed)
                case .hub:
                    return (#imageLiteral(resourceName: "hubWidgetLogo"), .hubGreen)
                default:
                    return (#imageLiteral(resourceName: "logo"), .ColourDL7)
                }
            }()

            return SearchViewModel(title: PILocalizedString("\(title)"), criteriaSummary: summary, iconOptions: iconOptions)
        }
    }

    // MARK: - Lifecycle

    init(with dataProvider: DashboardDataProvider? = nil, and delegate: DashboardInteractorDelegate? = nil) {
        self.delegate = delegate
        self.dataProvider = dataProvider
        self.dataProvider?.delegate = self
    }

    func clearSearches() {
        // use the dataProvider instead of directly accessing this manager
        let recentSearchesManager = RecentSearchesManager(dataSource: UserDefaults.standard)
        recentSearchesManager.reset(userDefaults: UserDefaults.standard)

        finishedRefreshingData()
    }

    func dismissNotification() {
        self.notificationIsDismissed = true
        finishedRefreshingData()
    }
}

extension DashboardInteractor: DashboardDataProviderDelegate {
    func finishedRefreshingData() {
        if let viewModel = viewModel {
            delegate?.updated(viewModel: viewModel)
        }
    }
}
