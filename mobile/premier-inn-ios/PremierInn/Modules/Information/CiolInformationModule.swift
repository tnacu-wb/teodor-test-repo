//
//  CiolInformationModule.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 08.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

enum CiolInformationModule {
    static func build(model: CiolInformationModel) -> UIViewController {
        let viewController = CiolInformationViewController()

        viewController.eventHandler = {
            let presenter = CiolInformationPresenter()
            let interactor = CiolInformationInteractor(viewModel: model)
            let router = CiolInformationRouter()

            presenter.view = viewController
            presenter.interactor = interactor
            presenter.router = router

            router.viewController = viewController
            return presenter
        }()

        return viewController
    }
}

protocol CiolInformationInteractorProtocol {
    var viewModel: CiolInformationModel { get }
}

protocol CiolInformationEventHandler {
    func close()
}

protocol CiolInformationRouterProtocol {
    func close()
}

protocol CiolInformationViewProtocol: AnyObject {
    func reloadData(with viewModel: CiolInformationModel)
}

struct CiolInformationModel {
    var image: UIImage?
    var title: String
    var subtitle: String?
    var showSubtitle: Bool
    var description: String
    var showCTA: Bool
    var ctaTitle: String?
}
