//
//  AmendReviewRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 21/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Formeka
import UIKit

protocol AmendReviewRouterProtocol {
    func amendCompleted()
    func showAmendAndPayView(
        amendUpdateModel: AmendUpdateModel,
        amendAndPayViewModel: AmendAndPayViewModel,
        amendOperaDetails: AmendOperaDetails,
        reservationDetails: ReservationDetails,
        existingStay: Stay
    )
    func goBackToBookingConfirmation()
}

protocol AmendReviewRouterDelegate: AnyObject {
    func amendCompleted()
}

struct AmendReservationReviewModel {
    let existingStay: Stay
    let amendUpdateModel: AmendUpdateModel
    let hotel: Hotel?
    let amendOperaDetails: AmendOperaDetails?
    let analyticsUpdates: [AmendmentDetailsViewModel]
}

class AmendReviewRouter {
    private weak var view: UIViewController?
    private weak var delegate: AmendReviewRouterDelegate?

    deinit {
        print("DEINIT: \(self)")
    }

    static func buildController(
        with amendReviewModel: AmendReservationReviewModel,
        and delegate: AmendReviewRouterDelegate?
    ) -> UIViewController {
        let controller = AmendReviewView(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.eventHandler = {
            let router = AmendReviewRouter()
            router.view = controller
            router.delegate = delegate

            let presenter = AmendReviewPresenter()
            presenter.router = router

            let interactor = AmendReviewInteractor(
                existingStay: amendReviewModel.existingStay,
                updateModel: amendReviewModel.amendUpdateModel,
                hotel: amendReviewModel.hotel,
                analyticsUpdates: amendReviewModel.analyticsUpdates,
                amendOperaDetails: amendReviewModel.amendOperaDetails
            )
            interactor.presenter = presenter

            presenter.interactor = interactor
            presenter.view = controller

            return presenter
        }()

        return controller
    }
}

extension AmendReviewRouter: AmendReviewRouterProtocol {
    func showAmendAndPayView(
        amendUpdateModel: AmendUpdateModel,
        amendAndPayViewModel: AmendAndPayViewModel,
        amendOperaDetails: AmendOperaDetails,
        reservationDetails: ReservationDetails,
        existingStay: Stay
    ) {
        let amendPayViewController = AmendAndPayRouter.buildController(
            amendUpdateModel: amendUpdateModel,
            amendAndPayViewModel: amendAndPayViewModel,
            amendOperaDetails: amendOperaDetails,
            reservationDetails: reservationDetails,
            amendAndPayRouterDelegate: self,
            existingStay: existingStay
        )
        view?.navigationController?.pushViewController(amendPayViewController, animated: true)
    }

    func amendCompleted() {
        goBackToBookingConfirmation()
        delegate?.amendCompleted()
    }

    func goBackToBookingConfirmation() {
        if let bookingConfirmationVC = view?.navigationController?.viewControllers
           .first(where: { $0 is BookingConfirmationViewController }) {
            view?.navigationController?.popToViewController(bookingConfirmationVC, animated: true)
            return
        }
        view?.navigationController?.popToRootViewController(animated: true)
    }
}

extension AmendReviewRouter: AmendAndPayRouterDelegate { }
