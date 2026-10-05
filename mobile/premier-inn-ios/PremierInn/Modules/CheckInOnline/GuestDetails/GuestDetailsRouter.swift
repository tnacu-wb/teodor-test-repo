//
//  GuestDetailsRouterBlueprint.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

class GuestDetailsRouter: GuestDetailsRouterBlueprint {
    weak var viewController: UIViewController?

    func goToUpsell(ciolUpsellInputParams: CiolUpsellInputParams, prestayDelegate: PrestayDelegate) {
        let ciolUpsellViewController = CiolUpsellModule.build(
            inputParams: ciolUpsellInputParams,
            prestayDelegate: prestayDelegate
        )
        viewController?.navigationController?.pushViewController(ciolUpsellViewController, animated: true)
    }

    func goToPayment(inputParams: CiolReviewAndPayInputParams) {
        let ciolReviewAndPayViewController = CiolReviewAndPayModule.build(inputParams: inputParams)
        viewController?.navigationController?.pushViewController(ciolReviewAndPayViewController, animated: true)
    }

    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails) {
        if ciolConfirmationDetails.stay.isDigitalKeyEnabled {
            let keyViewController = KeyModule.createModule(stay: ciolConfirmationDetails.stay, disableBackButton: true)
            viewController?.navigationController?.pushViewController(keyViewController, animated: true)
            return
        }
        let ciolConfirmationViewController = CiolConfirmationModule.build(ciolConfirmationDetails: ciolConfirmationDetails)
        viewController?.navigationController?.pushViewController(ciolConfirmationViewController, animated: true)
    }

    func showEdit(with input: EditDetailsInputParams, delegate: EditDetailsViewDelegate) {
        guard let edit = EditDetailsModule.build(inputParams: input) as? EditDetailsViewController else {
            return
        }
        edit.editDetailsViewDelegate = delegate
        viewController?.navigationController?.pushViewController(edit, animated: true)
    }

    // this doesn't take a payment - it goes through the authorisation flow for DE reg card
    func processPayment(
        with cccpiPageParams: ThreeCiPageParams,
        using threeCiPageDelegate: ThreeCiPageDelegate,
        and webDelegate: WebViewControllerDelegate,
        authorizationDelegate: AuthorizationDelegate,
        webviewLayout: WebViewControllerLayout
    ) {
        let parameters: ThreeCWebPageInitVariables = (
            cccpiPageParams.html,
            PIAnalytics.StateNames.pay3CiPage,
            PIAnalytics.StateTypes.ciolFlow,
            [ThreeCEvent.authorizationMessage],
            cccpiPageParams.trackingParams ?? [:],
            webviewLayout
        )
        let controller = ThreeCiPageViewController(parameters: parameters)
        controller.delegate = webDelegate
        controller.threeCiPageDelegate = threeCiPageDelegate
        controller.auhorizationDelegate = authorizationDelegate
        let navigationController = UINavigationController(rootViewController: controller)
        navigationController.modalPresentationStyle = .fullScreen

        viewController?.present(navigationController, animated: true)
    }
}
