//
//  DeleteAccountRouter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 28/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import UIKit

protocol DeleteAccountRouterInput {
    func goToForgotPassword()
    func goToUserDetails()
}

protocol DeleteAccountDelegate: AnyObject {
    func userHasDeletedAccount()
}

class DeleteAccountRouter {
    private weak var viewController: UIViewController?
    private weak var delegate: DeleteAccountDelegate?

    static func buildController(delegate: DeleteAccountDelegate? = nil) -> UIViewController {
        let controller = DeleteAccountView()
        controller.presenter = {
            let presenter = DeleteAccountPresenter()
            presenter.view = controller

            let interactor = DeleteAccountInteractor()
            presenter.interactor = interactor

            let router = DeleteAccountRouter()
            router.viewController = controller
            router.delegate = delegate

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

extension DeleteAccountRouter: DeleteAccountRouterInput {
    func goToUserDetails() {
        delegate?.userHasDeletedAccount()
        viewController?.navigationController?.popToRootViewController(animated: true)
    }

    func goToForgotPassword() {
        let resetPasswordVC = ResetPasswordRouter.buildController(withEmailAddress: "")
        viewController?.navigationController?.pushViewController(resetPasswordVC, animated: true)
    }
}
