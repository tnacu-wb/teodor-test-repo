//
//  RegisterRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 02/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import UIKit

class RegisterRouter {
    var onDismissOnboarding: (() -> Void)?
    weak var viewController: UIViewController?
    private var registerCompletionInput: RegisterCompletionInput?

    func presentRegisterInterface(
        from sender: UIViewController,
        with registerCompletionInput: RegisterCompletionInput? = nil
    ) {
        self.registerCompletionInput = registerCompletionInput

        let controller = RegisterViewController(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.presenter = {
            let presenter = RegisterPresenter()
            presenter.view = controller

			let interactor = RegisterInteractor()
            presenter.interactor = interactor

            viewController = controller
            presenter.router = self

            return presenter
        }()

        sender.present(UINavigationController(rootViewController: controller), animated: true, completion: nil)
    }
}

extension RegisterRouter: RegisterRouterInput {
    func selectedSalutation(completion: @escaping (String?) -> Void) {
        guard let viewController = viewController else { return }

        let viewModel = SalutationsListViewModel(salutations: Constants.CMS.salutations)

        let controller = ListViewController(viewModel: viewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString(
            "salutationListPlaceholder",
            comment: "Salutation list placeholder"
        )
        controller.textFieldValue = nil
        controller.shouldDelaySearchRequest = false
        controller.selectedObjectOutput = { sender, salutation in
            sender.dismiss(animated: true, completion: nil)
            completion(salutation as? String)
        }
        controller.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true, completion: nil)
            completion(nil)
        }

        viewController.present(controller, animated: true, completion: nil)
    }

    func dismissRegisterView() {
        viewController?.dismiss(animated: true, completion: nil)
    }

    func completeRegister() {
        registerCompletionInput?.userWasRegistered()
        onDismissOnboarding?()
        dismissRegisterView()
    }
}
