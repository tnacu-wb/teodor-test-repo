//
//  AmendAndPayRouter.swift
//  PremierInn
//
//  Created by Santa Gurung on 16/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

protocol AmendAndPayRouterProcotol {
    func showIframe(
        cccpiPageParams: ThreeCiPageParams,
        threeCiPageDelegate: ThreeCiPageDelegate,
        webDelegate: WebViewControllerDelegate
    )
    func goBackToBookingConfirmation()
    func goBack()
    func amendCompleted()
    func goBackToAmendReview()
}

protocol AmendAndPayRouterDelegate: AnyObject {
    func amendCompleted()
}

class AmendAndPayRouter {
    var view: UIViewController?
    private weak var delegate: AmendAndPayRouterDelegate?

    static func buildController(
        amendUpdateModel: AmendUpdateModel,
        amendAndPayViewModel: AmendAndPayViewModel,
        amendOperaDetails: AmendOperaDetails,
        reservationDetails: ReservationDetails,
        amendAndPayRouterDelegate: AmendAndPayRouterDelegate?,
        existingStay: Stay
    ) -> UIViewController {
        let controller = AmendAndPayView(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.presenter = {
            let router = AmendAndPayRouter()
            router.view = controller
            router.delegate = amendAndPayRouterDelegate
            let interactor = AmendAndPayInteractor(
                amendUpdateModel: amendUpdateModel,
                amendAndPayViewModel: amendAndPayViewModel,
                amendOperaDetails: amendOperaDetails,
                reservationDetails: reservationDetails,
                existingStay: existingStay
            )
            let presenter = AmendAndPayPresenter()
            presenter.router = router
            presenter.interactor = interactor
            presenter.view = controller

            return presenter
        }()

        return controller
    }
}

extension AmendAndPayRouter: AmendAndPayRouterProcotol {
    func showIframe(
        cccpiPageParams: ThreeCiPageParams,
        threeCiPageDelegate: ThreeCiPageDelegate,
        webDelegate: WebViewControllerDelegate
    ) {
        let parameters: ThreeCWebPageInitVariables = (
            cccpiPageParams.html,
            PIAnalytics.StateNames.pay3CiPage,
            PIAnalytics.StateTypes.bookingFlow,
            cccpiPageParams.allowedEvents,
            cccpiPageParams.trackingParams ?? [:],
            .general
        )
        let controller = ThreeCiPageViewController(parameters: parameters)
        controller.delegate = webDelegate
        controller.threeCiPageDelegate = threeCiPageDelegate
        let navigationController = UINavigationController(rootViewController: controller)
        navigationController.modalPresentationStyle = .fullScreen

        view?.present(navigationController, animated: true)
    }

    func goBackToBookingConfirmation() {
        if let bookingConfirmationVC = view?.navigationController?.viewControllers
           .first(where: { $0 is BookingConfirmationViewController }) {
            view?.navigationController?.popToViewController(bookingConfirmationVC, animated: true)
            return
        }
        view?.navigationController?.popToRootViewController(animated: true)
    }

    func goBackToAmendReview() {
        if let amendBookingVC = view?.navigationController?.viewControllers.first(where: { $0 is AmendReviewView }) {
            view?.navigationController?.popToViewController(amendBookingVC, animated: true)
            return
        }
        view?.navigationController?.popToRootViewController(animated: true)
    }

    func goBack() {
        view?.navigationController?.popViewController(animated: true)
    }

    func amendCompleted() {
        goBackToBookingConfirmation()
        delegate?.amendCompleted()
    }
}
