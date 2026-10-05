//
//  LoginViewWrapper.swift
//  PremierInn
//
//  Created by Santa Gurung on 22/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI
import Formeka
import SimpleNetwork

struct LoginViewWrapper: UIViewControllerRepresentable {
    let onDismissOnboarding: () -> Void

    func makeUIViewController(context: Context) -> UINavigationController {
        let controller = LoginViewController(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.onDismissOnboarding = onDismissOnboarding

        let presenter = LoginPresenter()
        presenter.view = controller

        let isBusiness = UserDefaults.standard.bool(forKey: .storedBusinessKey)
        let interactor = LoginInteractor(asBusiness: isBusiness, fromSplashScreen: true, andFromBookingFlow: false)
        interactor.dataManager = RequestsManager()
        presenter.interactor = interactor

        let router = LoginRouter()
        router.viewController = controller
        router.presenter = presenter
        presenter.router = router

        controller.presenter = presenter

        return UINavigationController(rootViewController: controller)
    }

    func updateUIViewController(_ uiViewController: UINavigationController, context: Context) {
        // not needed
    }
}
