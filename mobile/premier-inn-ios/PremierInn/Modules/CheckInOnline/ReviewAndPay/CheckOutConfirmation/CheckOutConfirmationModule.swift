//
//  CiolConfirmationModule.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei (Cognizant) on 21.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

enum CheckOutConfirmationModule {
    static func build(checkOutDetails: CheckOutDetails) -> UIViewController {
        let viewController = CheckOutConfirmationViewController()
        let presenter = CheckOutConfirmationPresenter()
        let interactor = CheckOutConfirmationInteractor(checkOutDetails: checkOutDetails)
        let router = CheckOutConfirmationRouter()

        viewController.presenter = presenter
        presenter.view = viewController
        presenter.interactor = interactor
        presenter.router = router
        router.viewController = viewController

        return viewController
    }
}

protocol CheckOutConfirmationInteractorProtocol: AnyObject {
    var checkOutDetails: CheckOutDetails { get }
}

protocol CheckOutConfirmationPresenterProtocol: AnyObject {
    func viewIsReady()
    func dismissView()
}

protocol CheckOutConfirmationViewProtocol {
    func displayConfirmation(details: CheckOutDetails)
}

protocol CheckOutConfirmationRouterProtocol: AnyObject {
    func dismissView()
}
