//
//  KeyRouter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import UIKit
import SimpleNetwork
import CoreLocation

class KeyRouter: KeyRouterProtocol {
    weak var viewController: UIViewController?
    weak var presenter: KeyPresenter?

    private let passManager = PassManager()

    func openMFAScreen(stay: Stay, roomId: String, keyAddedDelegate: KeyAddedProtocol?) {
        let controller = OTPEmailCaptureRouter.buildController(with: stay, roomId: roomId, delegate: keyAddedDelegate)
        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func openMessagingScreen(type: RoomAllocationState, stay: Stay) {
        let view = KeyMessagingModule.createModule(allocationState: type, stay: stay)
        viewController?.navigationController?.pushViewController(view, animated: true)
    }

    func openPassInWallet(id: String) {
		guard let passURL = self.passManager.fetchPassFromLibrary(passId: id)?.passURL else {
			AnalyticsManager.shared.track(errorName: PIAnalytics.Error.dkKeyNotFoundInWallet)
			return
		}
        UIApplication.shared.open(passURL, options: [:], completionHandler: nil)
    }

    func closeButtonClicked(digitalKeyIdentifier: String?) {
        NotificationCenter.default.post(name: Notification.Name.digitalKeyFlowDidClose, object: digitalKeyIdentifier)

        if let controller = viewController?.navigationController?.viewControllers
           .first(where: { $0 is BookingConfirmationViewController }) {
            viewController?.navigationController?.popToViewController(controller, animated: true)
        } else if let controller = viewController?.navigationController?.viewControllers
                    .first(where: { $0 is ReservationsListViewController }) {
			NotificationCenter.default.post(name: .refreshReservationsList, object: nil)
            viewController?.navigationController?.popToViewController(controller, animated: true)
        } else {
            viewController?.navigationController?.popToRootViewController(animated: true)
        }
    }

    func instructionsDidTap(model: InstructionsViewModel) {
        let instructionsController = InstructionsModule.build(model: model)
        instructionsController.modalPresentationStyle = .pageSheet
        if let sheet = instructionsController.sheetPresentationController {
            if model.type == .usingDigitalKey {
                sheet.detents = [.custom(identifier: .init("medium")) { _ in 600 }]
            } else {
                sheet.detents = [.custom(identifier: .init("medium")) { _ in 450 }]
            }
        }
        viewController?.tabBarController?.present(instructionsController, animated: true)
    }
}
