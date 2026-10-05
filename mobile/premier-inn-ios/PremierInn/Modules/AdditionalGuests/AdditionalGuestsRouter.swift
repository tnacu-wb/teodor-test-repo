//
//  AdditionalGuestsRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol AdditionalGuestsRouterProtocol {
    func addAdditionalGuest()
    func edit(additionalGuest guest: AdditionalGuest, atIndex index: Int)
}

class AdditionalGuestsRouter {
    private weak var viewController: AdditionalGuestsView?

    static func buildController() -> UIViewController {
        let controller = AdditionalGuestsView(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.eventHandler = {
            let presenter = AdditionalGuestsPresenter()
            presenter.view = controller

            let interactor = AdditionalGuestsInteractor()
            interactor.delegate = presenter
            presenter.interactor = interactor

            let router = AdditionalGuestsRouter()
            router.viewController = controller

            presenter.router = router

            return presenter
        }()

        return controller
    }

    private func showAddGuest(withGuest guest: AdditionalGuest?, index: Int?) {
        let controller = AdditionalGuestFormRouter.buildController(user: guest, index: index)
        let navController = UINavigationController(rootViewController: controller)

        viewController?.present(navController, animated: true)
    }
}

extension AdditionalGuestsRouter: AdditionalGuestsRouterProtocol {
    func addAdditionalGuest() {
        showAddGuest(withGuest: nil, index: nil)
    }

    func edit(additionalGuest guest: AdditionalGuest, atIndex index: Int) {
        showAddGuest(withGuest: guest, index: index)
    }
}
