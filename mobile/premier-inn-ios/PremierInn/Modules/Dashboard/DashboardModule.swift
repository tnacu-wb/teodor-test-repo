//
//  DashboardModule.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 23/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

enum DashboardModule {
    static func build(
        with stay: Stay?,
        recentSearchesViewDelegate: RecentSearchesViewDelegate?,
        dashboardRouterDelegate: DashboardRouterDelegate?
    ) -> UIViewController {
        let controller = DashboardViewController(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.eventHandler = {
            let dataProvider = RequestsManagerDashboardDataProvider(
                with: stay,
                and: Constants.Dashboard.defaultRefreshInterval
            )

            let presenter = DashboardPresenter()
            let interactor = DashboardInteractor(with: dataProvider, and: presenter)
            let router = DashboardRouter(dashboardRouterDelegate: dashboardRouterDelegate)

            router.view = controller
            router.interactor = interactor

            presenter.view = controller
            presenter.interactor = interactor
            presenter.router = router
            presenter.recentSearchesViewDelegate = recentSearchesViewDelegate

            return presenter
        }()

        return controller
    }
}

protocol DashboardRouterDelegate: AnyObject {
    func showBooking(with identifier: String)
    func showCheckInOnline(with identifier: String)
    func showAmendBooking(with identifier: String)
    func showHotelCalendar(with dashboardHotelDetails: DashboardHotelDetails)
    func navigateToSRP(with suggestion: Suggestion)
}

protocol DashboardRouterProtocol {
    func dashboardDidUpdateWithResults(viewModel: DashboardViewModel)
    func showBooking(with identifier: String)
    func showCheckInOnline(with identifier: String)
    func showAmendBooking(with identifier: String)
    func showHotelCalendar(with dashboardHotelDetails: DashboardHotelDetails)
    func navigateToSRP(with suggestion: Suggestion)
}

protocol DashboardDataProviderDelegate {
    func finishedRefreshingData()
}

protocol DashboardDataProvider {
    var delegate: DashboardDataProviderDelegate? { get set }
    var upcomingBooking: UpcomingBooking? { get }
    var dashboardUpcomingBooking: DashboardUpcomingBooking? { get }
    var dashboardFrequentlyBooked: [FrequentBookingViewModel]? { get }
    var dashboardRecentSearches: Bool? { get }
    var dashboardPastSearches: Bool? { get }
    var searches: [RecentSearch]? { get }
    var searchesComponentType: SearchesComponentType { get }
    var destinationHeading: String? { get }
    var destinationCards: [DestinationCardViewModel]? { get }
    var contentCards: [ContentCardViewModel]? { get }
    var promoCards: [PromoCardViewModel]? { get }
    var notification: NotificationViewModel? { get }

    func refreshDashboardData(with stay: Stay?)
}

protocol DashboardInteractorDelegate: AnyObject {
    func updated(viewModel: DashboardViewModel)
}

protocol RecentSearchesViewModel {
    var searchesComponentType: SearchesComponentType { get }
    var recentSearchesViewModels: [RecentSearchViewModel] { get }
}

protocol RecentSearchViewModel {
    var title: String { get }
    var criteriaSummary: NSAttributedString { get }
    var iconOptions: IconOptions { get }
}

protocol DashboardViewProtocol {
    func update(with dashboardViewModel: DashboardViewModel)
}

protocol DashboardViewEventHandler: AnyObject {
    func viewIsReady()
    func selectedRecentSearch(at index: Int)
    func selectedSearch()
    func showBooking(with identifier: String)
    func showCheckInOnline(with identifier: String)
    func showAmendBooking(with identifier: String)
    func showHotelDirections()
    func showHotelCalendar(with dashboardHotelDetails: DashboardHotelDetails)
    func clearRecentSearchesDidTap()
    func dismissNotification()
}
