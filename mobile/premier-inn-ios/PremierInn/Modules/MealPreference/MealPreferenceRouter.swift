//
//  MealPreferenceRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit

protocol MealPreferenceRouterInput {
    func finishedUpdatingPreference()
    func errorOccuredWhenUpdatingPreferences()
}

class MealPreferenceRouter {
    private weak var viewController: UIViewController?
    weak var userPreferencesCompletionDelegate: UserPreferenceCompletionDelegate?

    static func buildController(
        withUserPreferencesCompletionDelegate userPreferencesCompletionDelegate: UserPreferenceCompletionDelegate? =
        nil
    ) -> UIViewController {
        let controller = MealPreferenceView()
        controller.presenter = {
            let presenter = MealPreferencePresenter()
            presenter.view = controller

            let interactor = MealPreferenceInteractor()
            interactor.output = presenter
            presenter.interactor = interactor

            let router = MealPreferenceRouter()
            router.viewController = controller
            router.userPreferencesCompletionDelegate = userPreferencesCompletionDelegate

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

extension MealPreferenceRouter: MealPreferenceRouterInput {
    func finishedUpdatingPreference() {
        userPreferencesCompletionDelegate?.userPreferencesUpdated(succesfully: true)
        viewController?.navigationController?.popViewController(animated: true)
    }

    func errorOccuredWhenUpdatingPreferences() {
        userPreferencesCompletionDelegate?.errorOccuredWhenUpdatingPreferences()
        viewController?.navigationController?.popViewController(animated: true)
    }
}
