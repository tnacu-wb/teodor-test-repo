//
//  UserPreferencesRouter.swift
//  PremierInn
//
//  Created by Nick Jones on 30/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit

protocol UserPreferenceCompletionDelegate: AnyObject {
    func userPreferencesUpdated(succesfully: Bool)
    func errorOccuredWhenUpdatingPreferences()
}

protocol UserPreferencesRouterInput {
    func selectedRow(ofUserPreferenceType userPreferenceType: UserPreferenceType)
}

class UserPreferencesRouter {
    private weak var viewController: UIViewController?
    private var preferencePresenter: UserPreferencesPresenter?

    static func buildController() -> UIViewController {
        let controller = UserPreferencesView()
        controller.presenter = {
            let presenter = UserPreferencesPresenter()
            presenter.view = controller

            let interactor = UserPreferencesInteractor()
            presenter.interactor = interactor

            let router = UserPreferencesRouter()
            router.preferencePresenter = presenter
            router.viewController = controller

            presenter.router = router

            return presenter
        }()


        return controller
    }

    private func controller(forPreferenceType preferenceType: UserPreferenceType) -> UIViewController? {
        switch preferenceType {
        case .room:
            return RoomRequirementsRouter.buildController(withRoomRequirementsCompletionInput: self)
        case .meal:
            return MealPreferenceRouter.buildController(withUserPreferencesCompletionDelegate: self)
        default:
            return nil
        }
    }
}

extension UserPreferencesRouter: RoomRequirementsCompletionInput {
    func roomRequirementsDidUpdate(succesfully: Bool) {
        preferencePresenter?.userPreferenceUpdated(preferenceUpdated: .Room)
    }

    func errorOccuredWhenUpdatingPreferences() {
        preferencePresenter?.errorOccuredWhenUpdatingPreferences()
    }
}

extension UserPreferencesRouter: UserPreferenceCompletionDelegate {
    func userPreferencesUpdated(succesfully: Bool) {
        preferencePresenter?.userPreferenceUpdated(preferenceUpdated: .Meal)
    }
}

extension UserPreferencesRouter: UserPreferencesRouterInput {
    func selectedRow(ofUserPreferenceType userPreferenceType: UserPreferenceType) {
        guard let preferenceController = controller(forPreferenceType: userPreferenceType) else { return }
        viewController?.navigationController?.pushViewController(preferenceController, animated: true)
    }
}
