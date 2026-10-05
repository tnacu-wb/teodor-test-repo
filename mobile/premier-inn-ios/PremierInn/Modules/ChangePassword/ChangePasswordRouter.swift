//
//  ChangePasswordRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 14/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka

protocol ChangePasswordRouterProtocol {
    func passwordDidChange()
    func showForgotPasswordModule(email: String?, isBusiness: Bool)
}

protocol ChangePasswordCompletionInput: AnyObject {
    func passwordWasUpdated()
}


class ChangePasswordRouter {
    var presenter: ChangePasswordPresenter?
    private weak var viewController: ChangePasswordView?

    weak var changePasswordCompletionInput: ChangePasswordCompletionInput?

    static func buildController(
        with changePasswordCompletionInput: ChangePasswordCompletionInput? = nil,
        asBusinessLogin: Bool
    ) -> UIViewController {
        let controller = ChangePasswordView(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.presenter = {
            let presenter = ChangePasswordPresenter()
            presenter.view = controller

            let interactor = ChangePasswordInteractor(asBusiness: asBusinessLogin)
            interactor.delegate = presenter
            presenter.interactor = interactor

            let router = ChangePasswordRouter()
            router.viewController = controller
            router.changePasswordCompletionInput = changePasswordCompletionInput

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

extension ChangePasswordRouter: ChangePasswordRouterProtocol {
    func passwordDidChange() {
        changePasswordCompletionInput?.passwordWasUpdated()
        viewController?.navigationController?.popViewController(animated: true)
    }
    func showForgotPasswordModule(email: String?, isBusiness: Bool) {
        let controller = ResetPasswordRouter.buildController(withEmailAddress: email, isBusiness: isBusiness, and: self)
        viewController?.navigationController?.pushViewController(controller, animated: true)
    }
}

extension ChangePasswordRouter: ResetPasswordDelegate {
    func resetPassword(for email: String) {}
}
