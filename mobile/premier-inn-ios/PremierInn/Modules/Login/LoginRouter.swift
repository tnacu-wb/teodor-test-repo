//
//  LoginRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 11/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol LoginRouterInput {
    var viewController: UIViewController? { get }

    func dismissLoginView()
    func showForgotPasswordModule(email: String?, isBusiness: Bool)
    func goHome()
}

class LoginRouter {
    weak var viewController: UIViewController?

    var presenter: LoginPresenterInput?
    private var sender: UINavigationController?

    func presentLoginInterface(
        from sender: UIViewController,
        asBusinessLogin: Bool,
        comingFromSplashScreen: Bool,
        andIsFromBookingFlow fromBookingFlow: Bool
    ) {
        let controller = LoginViewController(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.presenter = {
            let presenter = LoginPresenter()
            presenter.view = controller

            let interactor = LoginInteractor(
                asBusiness: asBusinessLogin,
                fromSplashScreen: comingFromSplashScreen,
                andFromBookingFlow: fromBookingFlow
            )
            interactor.dataManager = RequestsManager()
            presenter.interactor = interactor

            viewController = controller
            presenter.router = self

            self.presenter = presenter
            self.sender = sender.navigationController

            return presenter
        }()

        let navController = UINavigationController(rootViewController: controller)

        sender.present(navController, animated: true)
    }
}

extension LoginRouter: LoginRouterInput {
    func dismissLoginView() {
        viewController?.dismiss(animated: true)
    }

    func showForgotPasswordModule(email: String?, isBusiness: Bool) {
        let controller = ResetPasswordRouter.buildController(withEmailAddress: email, isBusiness: isBusiness, and: self)
        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func goHome() {
        viewController?.dismiss(animated: true) { [weak self] in
            self?.sender?.popToRootViewController(animated: true)
        }
    }
}

extension LoginRouter: ResetPasswordDelegate {
    func resetPassword(for email: String) {
        presenter?.updateEmail(with: email)
    }
}
