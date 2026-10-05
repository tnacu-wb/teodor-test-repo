//
//  DashboardPresenter.swift
//  PremierInn
//
//  Created by Filippo Minelle on 11/12/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

class DashboardPresenter: NSObject {
    // MARK: - Properties

    var view: DashboardViewController?
    var interactor: DashboardInteractor?
    var router: DashboardRouter?
    var recentSearchesViewDelegate: RecentSearchesViewDelegate?
}

extension DashboardPresenter: DashboardViewEventHandler {
    func viewIsReady() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.update(with: viewModel)
    }

    func selectedRecentSearch(at index: Int) {
        recentSearchesViewDelegate?.selectedRecentSearch(at: index)
    }

    func selectedSearch() {
    }

    func showBooking(with identifier: String) {
        router?.showBooking(with: identifier)
    }

    func showCheckInOnline(with identifier: String) {
        router?.showCheckInOnline(with: identifier)
    }

    func showAmendBooking(with identifier: String) {
        router?.showAmendBooking(with: identifier)
    }

    func showHotelDirections() {
        guard let directionsViewModel = interactor?.directionsViewModel else { return }
        view?.showMapsDirectionsOptions(with: directionsViewModel)
    }

    func showHotelCalendar(with dashboardHotelDetails: DashboardHotelDetails) {
        router?.showHotelCalendar(with: dashboardHotelDetails)
    }

    func clearRecentSearchesDidTap() {
        interactor?.clearSearches()
    }

    func dismissNotification() {
        interactor?.dismissNotification()
    }

    func navigateToSRP(with suggestion: Suggestion) {
        router?.navigateToSRP(with: suggestion)
    }
}

extension DashboardPresenter: DashboardInteractorDelegate {
    func updated(viewModel: DashboardViewModel) {
        view?.update(with: viewModel)
//        router?.dashboardDidUpdateWithResults(viewModel: viewModel)
    }
}
