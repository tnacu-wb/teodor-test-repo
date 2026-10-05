//
//  RoomRequirementsRouter.swift
//  PremierInn
//
//  Created by Nick Jones on 01/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import SimpleNetwork

protocol RoomRequirementsRouterInput {
    func removeModule()
    func errorOccuredWhenUpdatingPreferences()
    func showRoomTypePicker(selectedType: RoomType, availableTypes: [RoomType], delegate: RoomTypeSelectRouterDelegate?)
}

protocol RoomRequirementsCompletionInput {
    func roomRequirementsDidUpdate(succesfully: Bool)
    func errorOccuredWhenUpdatingPreferences()
}

class RoomRequirementsRouter {
    private weak var viewController: UIViewController?
    var roomRequirementsCompletionInput: RoomRequirementsCompletionInput?

    static func buildController(
        withRoomRequirementsCompletionInput roomRequirementsCompletionInput: RoomRequirementsCompletionInput? =
        nil
    ) -> UIViewController {
        let controller = RoomRequirementsView()
        controller.presenter = {
            let presenter = RoomRequirementsPresenter()
            presenter.view = controller

            let currentUser = UserSessionManager.sharedInstance.currentUser
            let roomRequirements = currentUser?.bookingPreference?.roomRequirements
            let model = RoomRequirementsModel(
                adults: roomRequirements?.room.adults ?? 1,
                children: roomRequirements?.room.children ?? 0,
                shouldIncludeCot: roomRequirements?.cotRequired ?? false,
                roomType: roomRequirements?.room.type ?? .double
            )

            let interactor = RoomRequirementsInteractor(model: model)
            interactor.presenter = presenter
            presenter.interactor = interactor

            let router = RoomRequirementsRouter()
            router.viewController = controller
            router.roomRequirementsCompletionInput = roomRequirementsCompletionInput

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

extension RoomRequirementsRouter: RoomRequirementsRouterInput {
    func showRoomTypePicker(selectedType: RoomType, availableTypes: [RoomType], delegate: RoomTypeSelectRouterDelegate?) {
        guard let delegate = delegate else { return }

        let controller = RoomTypeSelectModule.build(with: availableTypes, selectedRoomType: selectedType, and: delegate)

        viewController?.presentNonFullScreenViewController(controller, animated: true)
    }

    func removeModule() {
        roomRequirementsCompletionInput?.roomRequirementsDidUpdate(succesfully: true)

        DispatchQueue.main.async {
            self.viewController?.navigationController?.popViewController(animated: true)
        }
    }

    func errorOccuredWhenUpdatingPreferences() {
        roomRequirementsCompletionInput?.errorOccuredWhenUpdatingPreferences()

        DispatchQueue.main.async {
            self.viewController?.navigationController?.popViewController(animated: true)
        }
    }
}
