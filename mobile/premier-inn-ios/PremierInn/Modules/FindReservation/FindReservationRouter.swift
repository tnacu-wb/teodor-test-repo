//
//  FindReservationRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleCalendar
import SimpleNetwork

protocol FindReservationRouterProtocol {
    func showCalendarPicker(withSelectedDate: Date, startDate: Date)
    func showBartError()
    func navigateToBookingConfirmation(stay: Stay?)
}

class FindReservationRouter {
    private weak var viewController: UIViewController?

    weak var presenter: FindReservationPresenterProtocol?

    static func build(
        arrivalDate: String? = nil,
        reservationNumber: String? = nil,
        lastName: String? = nil
    ) -> UIViewController {
        let controller = FindReservationViewController()
        controller.presenter = {
            let router = FindReservationRouter()
            router.viewController = controller

            let interactor = FindReservationInteractor(
                arrivalDate: arrivalDate,
                reservationNumber: reservationNumber,
                lastName: lastName
            )

            let presenter = FindReservationPresenter()
            presenter.view = controller
            presenter.router = router
            presenter.interactor = interactor

            router.presenter = presenter

            return presenter
        }()

        return controller
    }
}

extension FindReservationRouter: FindReservationRouterProtocol {
    func navigateToBookingConfirmation(stay: Stay?) {
        guard let navigationController = self.viewController?.navigationController else {
            return
        }

        var viewControllers = navigationController.viewControllers

        if viewControllers.isNotEmpty {
            viewControllers.removeLast()
        }

        if let stay {
            let controller = BookingConfirmationModule.build(summary: stay, isBookingFlowEnd: false)
            controller.hidesBottomBarWhenPushed = true

            viewControllers.append(controller)
        }

        navigationController.setViewControllers(viewControllers, animated: true)
    }

    func showBartError() {
        let controller = BARTDowntimeModal()
        controller.parentNavigationController = viewController?.navigationController

        viewController?.present(controller, animated: true)
    }

    func showCalendarPicker(withSelectedDate selectedDate: Date, startDate: Date) {
        let settings = SimpleCalendarSettings(
            colors: Constants.PICalendarSettings.colors,
            fonts: Constants.PICalendarSettings.fonts,
            selectedDate: selectedDate,
            startDate: startDate,
            selectableDatesOffset: 100,
            monthSpan: 19,
            freeSelection: true,
            andCustomTitle: PILocalizedString("calendarHeaderTitle", comment: "Calendar header title"),
            maxDepartureDateCount: SettingsManager.sharedInstance.activeRules.maxDepartureDateCount
        )

        let controller = CalendarViewController(settings: settings)
        controller.delegate = self

        viewController?.present(UINavigationController(rootViewController: controller), animated: true) {
            var dict = [
                PIAnalytics.Keys.environment: AnalyticsConstants.environment,
                PIAnalytics.Keys.userLogin: UserSessionManager.sharedInstance.currentUser != nil ? LoggedInAnalytic.loggedIn
                .rawValue : LoggedInAnalytic.notLoggedIn.rawValue,
                PIAnalytics.Keys.timeZone: TimeZone.current.description,
                PIAnalytics.Keys.language: Locale.current.language.languageCode?.identifier ?? "n/a",
                PIAnalytics.Keys.screenType: PIAnalytics.StateTypes.myBookings
            ]

            if let user = UserSessionManager.sharedInstance.currentUser, let companyId = user.companyId,
               let accessLevel = user.accessLevel?.rawValue {
                dict[PIAnalytics.Keys.companyID] = companyId
                dict[PIAnalytics.Keys.businessUserLevel] = accessLevel
            }

            AnalyticsManager.shared.trackState(PIAnalytics.StateNames.datePickerB, data: dict)
        }
    }
}

extension FindReservationRouter: SimpleCalendarDelegate {
    func selectedDate(date: Date) {
        presenter?.calendarDidSelect(date: date)

        viewController?.dismiss(animated: true)
    }
}
