//
//  OTPEmailCaptureRouter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 01/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
protocol OTPEmailCaptureRouterInput {
    func openOTPScreen(with email: String, stay: Stay, roomId: String)
}

class OTPEmailCaptureRouter {
    private weak var viewController: UIViewController?
    private var keyAddedDelegate: KeyAddedProtocol?

    static func buildController(with stay: Stay, roomId: String, delegate: KeyAddedProtocol?) -> UIViewController {
        let controller = OTPEmailCaptureView()
        controller.presenter = {
            let presenter = OTPEmailCapturePresenter()
            presenter.view = controller

            let interactor = OneTimePasswordCaptureInteractor(with: stay, roomId: roomId)
            presenter.interactor = interactor

            let router = OTPEmailCaptureRouter()
            router.viewController = controller
            router.keyAddedDelegate = delegate

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

extension OTPEmailCaptureRouter: OTPEmailCaptureRouterInput {
    func openOTPScreen(with email: String, stay: Stay, roomId: String) {
        let controller = OneTimePasswordModule.buildController(
            stay: stay,
            otpEmail: email,
            roomId: roomId,
            delegate: keyAddedDelegate
        )
        viewController?.navigationController?.pushViewController(controller, animated: true)
    }
}
