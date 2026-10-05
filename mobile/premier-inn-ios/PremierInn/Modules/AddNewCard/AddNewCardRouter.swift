//
//  AddNewCardRouter.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 03/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import UIKit

class AddNewCardRouter {
    var view: UIViewController?
    var delegate: AddNewCardRouterDelegate?
}

extension AddNewCardRouter: AddNewCardRouterProtocol {
    func showAddCardWebView(cccpiPageParams: ThreeCiPageParams, threeCiPageDelegate: ThreeCiPageDelegate) {
        let parameters: ThreeCWebPageInitVariables = (
            cccpiPageParams.html,
            PIAnalytics.StateNames.pay3CiPage,
            PIAnalytics.StateTypes.bookingFlow,
            cccpiPageParams.allowedEvents,
            cccpiPageParams.trackingParams ?? [:],
            .general
        )

        let controller = ThreeCiPageViewController(parameters: parameters)
        controller.delegate = self
        controller.threeCiPageDelegate = threeCiPageDelegate

        let navigationController = UINavigationController(rootViewController: controller)
        navigationController.modalPresentationStyle = .fullScreen

        view?.present(navigationController, animated: true)
    }

    func goBackToMyAccount() {
        guard let view else { return }

        // If theres My Account go back to that if not pop to root as a fail safe.
        if let myAccountController = view.navigationController?.viewControllers
           .first(where: { $0 is AccountViewController }) {
            view.navigationController?.popToViewController(myAccountController, animated: true)

            delegate?.cardUpdated()
            return
        }

        // Otherwise pop to the root
        view.navigationController?.popToRootViewController(animated: true)
    }
}

extension AddNewCardRouter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true)
    }
}
