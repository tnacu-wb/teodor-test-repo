//
//  ForceUpdateRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 10/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

protocol ForceUpdateRouterProtocol {
    func openUrl(url: URL)
}

final class ForceUpdateRouter {
    init() { }

    static func build() -> UIViewController {
        let controller = ForceUpdateView(nibName: String(describing: ForceUpdateView.self), bundle: nil)
        controller.presenter = {
            let router = ForceUpdateRouter()
            let interactor = ForceUpdateInteractor()

            let presenter = ForceUpdatePresenter()
            presenter.router = router
            presenter.interactor = interactor
            presenter.view = controller

            return presenter
        }()

        return controller
    }
}

extension ForceUpdateRouter: ForceUpdateRouterProtocol {
    func openUrl(url: URL) {
        if UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }
}
