//
//  CiolUpsellRouter.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

class CiolUpsellRouter: CiolUpsellRouterProtocol {
    weak var viewController: UIViewController?

    func showPayment() { }

    func showUpsellDetails(input: CiolUpsellDetailsInputParams, outputDelegate: CiolUpsellDetailsOutputDelegate) {
        let controller = CiolUpsellDetailsModule.build(
            inputParams: input,
            outputDelegate: outputDelegate
        )
        viewController?.present(controller, animated: true)
    }

    func showUpsellRooms(_ roomsUpsellInputParams: RoomsUpsellInputParams, roomsOutputDelegate: RoomsUpsellOutputDelegate) {
        let controller = RoomsUpsellModule.build(roomsUpsellInputParams, outputDelegate: roomsOutputDelegate)
        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func goToPayment(inputParams: CiolReviewAndPayInputParams, failedPaymentDelegate: CiolUpsellPayDelegate) {
        let ciolReviewAndPayViewController = CiolReviewAndPayModule.build(
            inputParams: inputParams,
            failedPaymentDelegate: failedPaymentDelegate
        )
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
