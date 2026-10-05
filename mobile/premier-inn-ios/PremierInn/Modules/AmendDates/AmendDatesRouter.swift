//
//  AmendDatesRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 18/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

protocol AmendDatesRouterProtocol: AnyObject {
    func finishedAmend()
}

protocol AmendDatesRouterDelegate: AnyObject {
    func finishedAmend()
}

protocol AmendDatesCalendarViewControllerProtocol: AlternateCalendarViewControllerProtocol {
    func showContinueButton(with viewModel: AmendDatesConfirmViewModel)
    func hideContinueButton()
}

class AmendDatesRouter {
    weak var delegate: AmendDatesRouterDelegate?

    deinit {
        print("DEINIT: \(self)")
    }

    static func buildController(
        with stayDetails: AmendStayDatesDetails,
        and delegate: AmendDatesRouterDelegate
    ) -> AlternateCalendarViewController {
        let departureDate = stayDetails.criteria.arrivalDate.dateByAddingUnit(
            unitType: .day,
            number: stayDetails.criteria.nights
        )
        let controller = AmendDatesView(
            arrivalDate: stayDetails.criteria.arrivalDate,
            departureDate: departureDate,
            overRideRules: stayDetails.rulesToFollow,
            closeButtonTitle: PILocalizedString("Done")
        )
        let nightsRestriction = stayDetails.nightsRestriction
        controller.presenter = {
            let presenter = AmendDatesPresenter()
            presenter.calendar = controller
            presenter.interactor = AmendDatesInteractor(with: stayDetails, nightsRestriction: nightsRestriction)
            presenter.router = {
                let router = AmendDatesRouter()
                router.delegate = delegate

                return router
            }()

            return presenter
        }()

        return controller
    }
}

extension AmendDatesRouter: AmendDatesRouterProtocol {
    func finishedAmend() {
        delegate?.finishedAmend()
    }
}
