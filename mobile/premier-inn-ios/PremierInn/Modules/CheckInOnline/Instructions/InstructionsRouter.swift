//
//  RoomKeyInstructionsRouter.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 25.06.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import PassKit

class InstructionsRouter: InstructionsRouterProtocol {
    weak var viewController: UIViewController?
    weak var presenter: InstructionsViewEventHandler?

    func close() {
        viewController?.dismiss(animated: true, completion: nil)
    }

    func showAddPassViewController(pass: PKPass) {
        guard let controller = PKAddPassesViewController(pass: pass) else { return }
        controller.delegate = presenter as? PKAddPassesViewControllerDelegate
        viewController?.present(controller, animated: true)
    }

    func showExistingPass(url: URL) {
        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }

    func showUsingYourDigitalKeyAnimation() {
        let controller = UsingYourDigitalKeyAnimationView()
            .embedInHostingController()
        controller.modalPresentationStyle = .overFullScreen
        viewController?.present(controller, animated: true)
    }
}
