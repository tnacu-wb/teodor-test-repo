//
//  FullScreenImageViewerModule.swift
//  PremierInn
//
//  Created by Freddie Parks on 12/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation
import UIKit

enum FullScreenImageViewerModule {
    static func build(
        roundelDesigns: [RoundelDesign],
        startIndex: Int,
        and delegate: FullScreenImageViewerRouterDelegate?
    ) -> UIViewController? {
        let controller = FullscreenImageSetViewController()
        controller.eventHandler = {
            let router = FullScreenImageViewerRouter(with: delegate)
            router.viewController = controller

            let interactor = FullScreenImageViewerInteractor(with: roundelDesigns, and: startIndex)

            let presenter = FullScreenImageViewerPresenter()
            presenter.view = controller
            presenter.interactor = interactor
            presenter.router = router

            return presenter
        }()

        return controller
    }
}

protocol FullScreenImageViewerViewModel {
    var carouselRoundelDesigns: [RoundelDesign] { get }
    var startIndex: Int { get }
}

protocol FullScreenImageViewerViewProtocol: AnyObject {
    func update(with viewModel: FullScreenImageViewerViewModel)
}

protocol FullScreenImageViewerViewEventHandler {
    func viewIsReady()
    func closeButtonDidTap()
    func imageSetDidChangePicture(atIndex index: Int)
}

protocol FullScreenImageViewerInteractorProtocol {
    var viewModel: FullScreenImageViewerViewModel? { get }

    func trackState()
}

protocol FullScreenImageViewerRouterProtocol {
    func fullscreenImageSetCloseButtonDidTap()
    func fullscreenImageSetDidChangePicture(atIndex index: Int)
}

protocol FullScreenImageViewerRouterDelegate: AnyObject {
    func fullscreenImageSetCloseButtonDidTap(viewController: UIViewController)
    func fullscreenImageSetDidChangePicture(atIndex index: Int)
}
