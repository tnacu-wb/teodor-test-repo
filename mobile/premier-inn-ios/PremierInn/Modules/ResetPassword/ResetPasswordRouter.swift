//
//  ResetPasswordRouter.swift
//  PremierInn
//
//  Created by Nick Jones on 08/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

protocol ResetPasswordRouterInput {
    func popModule(with accountResetEmail: String?)
}
protocol ResetPasswordDelegate: AnyObject {
    func resetPassword(for email: String)
}

class ResetPasswordRouter {
    private weak var viewController: UIViewController?
    private weak var delegate: ResetPasswordDelegate?

    static func buildController(
        withEmailAddress emailAddress: String?,
        isBusiness: Bool = false,
        and delegate: ResetPasswordDelegate? = nil
    ) -> UIViewController {
        let controller = ResetPasswordView()
        controller.presenter = {
            let presenter = ResetPasswordPresenter()
            presenter.view = controller

            let interactor = ResetPasswordInteractor(withEmailAddress: emailAddress, isBusiness: isBusiness)
            presenter.interactor = interactor

            let router = ResetPasswordRouter()
            router.viewController = controller
            router.delegate = delegate

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

extension ResetPasswordRouter: ResetPasswordRouterInput {
    func popModule(with accountResetEmail: String?) {
        if let email = accountResetEmail {
            delegate?.resetPassword(for: email)
        }
        viewController?.navigationController?.popViewController(animated: true)
    }
}
