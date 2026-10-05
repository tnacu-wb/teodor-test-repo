//
//  DashboardRouter.swift
//  PremierInn
//
//  Created by Filippo Minelle on 11/12/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

class DashboardRouter: NSObject {
    // MARK: - Properties

    private weak var delegate: DashboardRouterDelegate?
    var view: DashboardViewController?
    var presenter: DashboardPresenter?
    var interactor: DashboardInteractor?

    // MARK: - Lifecycle

    init(dashboardRouterDelegate: DashboardRouterDelegate?) {
        delegate = dashboardRouterDelegate
    }
}

extension DashboardRouter: DashboardRouterProtocol {
    func showHotelCalendar(with dashboardHotelDetails: DashboardHotelDetails) {
        delegate?.showHotelCalendar(with: dashboardHotelDetails)
    }

    func dashboardDidUpdateWithResults(viewModel: DashboardViewModel) {
//        delegate?.dashboardDidUpdateWithResults(viewModel: viewModel)
    }

    func showBooking(with identifier: String) {
        delegate?.showBooking(with: identifier)
    }

    func showCheckInOnline(with identifier: String) {
        delegate?.showCheckInOnline(with: identifier)
    }

    func showAmendBooking(with identifier: String) {
        delegate?.showAmendBooking(with: identifier)
    }

    func navigateToSRP(with suggestion: Suggestion) {
        delegate?.navigateToSRP(with: suggestion)
    }
}
