//
//  RegisterViewWrapper.swift
//  PremierInn
//
//  Created by Santa Gurung on 22/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI
import Formeka
import SimpleNetwork

struct RegisterViewWrapper: UIViewControllerRepresentable {
    let onDismissOnboarding: () -> Void

    func makeUIViewController(context: Context) -> UINavigationController {
        let controller = RegisterViewController(nibName: String(describing: FormekaViewController.self), bundle: nil)

        let presenter = RegisterPresenter()
        presenter.view = controller

        let interactor = RegisterInteractor()
        let requestsManager = RequestsManager()
        interactor.registerDataManager = requestsManager
        interactor.loginDataManager = requestsManager
        presenter.interactor = interactor

        let router = RegisterRouter()
        router.viewController = controller
        router.onDismissOnboarding = onDismissOnboarding
        presenter.router = router

        controller.presenter = presenter

        return UINavigationController(rootViewController: controller)
    }

    func updateUIViewController(_ uiViewController: UINavigationController, context: Context) {
        // not needed
    }
}
