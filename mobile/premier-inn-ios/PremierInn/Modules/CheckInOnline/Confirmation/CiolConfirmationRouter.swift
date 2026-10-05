//
//  CiolConfirmationRouter.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI
import SimpleNetwork

class CiolConfirmationRouter: CiolConfirmationRouterProtocol {
    weak var viewController: UIViewController?

    private let ciolFlow: CIOLStartFlow

    init(ciolFlow: CIOLStartFlow) {
        self.ciolFlow = ciolFlow
    }

    func navigateToMyBookings() {
        viewController?.navigationController?.setNavigationBarHidden(false, animated: false)

        switch ciolFlow {
        case .bookingConfirmation:
            if let controller = viewController?.navigationController?.viewControllers
               .first(where: { $0 is BookingConfirmationViewController }) {
                viewController?.navigationController?.popToViewController(controller, animated: true)
            } else {
                viewController?.navigationController?.popToRootViewController(animated: true)
            }
        case .myBookings:
            if let controller = viewController?.navigationController?.viewControllers
               .first(where: { $0 is ReservationsListViewController }) {
                viewController?.navigationController?.popToViewController(controller, animated: true)
            } else {
                viewController?.navigationController?.popToRootViewController(animated: true)
            }
        }
    }

    func showRoomKeyInstructions(model: InstructionsViewModel) {
        let roomKeyInstructionsController = InstructionsModule.build(model: model)
        roomKeyInstructionsController.modalPresentationStyle = .pageSheet

        if let sheet = roomKeyInstructionsController.sheetPresentationController {
			switch model.size {
			case .fullSize:
				sheet.detents = [.large()]
			case .custom(let height):
				sheet.detents = [.custom(identifier: .init("medium")) { _ in height }]
			}
        }
        viewController?.present(roomKeyInstructionsController, animated: true)
    }
}
