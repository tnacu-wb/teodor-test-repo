//
//  MealPreferencePresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol MealPreferencePresenterInput {
    var mealPreferenceTracking: MealPreferenceTracking { get }

    func reloadRows()
    func numberOfRows(forSection section: Int) -> Int
    func numberOfSections() -> Int
    func selectMealOption(option: MealOption)
    func allMealOptions() -> [MealOptionRowModel]?
    func saveChanges()
}

class MealPreferencePresenter {
    var router: MealPreferenceRouterInput?
    weak var view: MealPreferenceViewInput?
    var interactor: MealPreferenceInteractorInput?
    var analytics: AnalyticsType = AnalyticsManager.shared
}

extension MealPreferencePresenter: MealPreferencePresenterInput {
    var mealPreferenceTracking: MealPreferenceTracking {
        interactor?.mealPreferenceTracking ?? ("", "")
    }

    func reloadRows() {
        view?.refreshRows()
    }

    func numberOfRows(forSection section: Int) -> Int {
        interactor?.numberOfRows(forSection: section) ?? 0
    }

    func numberOfSections() -> Int {
        interactor?.numberOfSections() ?? 1
    }

    func selectMealOption(option: MealOption) {
        interactor?.selectMealOption(option: option)
    }

    func allMealOptions() -> [MealOptionRowModel]? {
        interactor?.allMealOptions()
    }

    func saveChanges() {
        view?.isProcessing = true
        interactor?.saveChanges()
    }
}

extension MealPreferencePresenter: MealPreferenceInteractorOutput {
    func mealPreferenceUpdated() {
        var data: PIDictionary?

        if let foodPreference = UserSessionManager.sharedInstance.currentUser?.bookingPreference?.foodPreference {
             data = [PIAnalytics.Keys.mealPreference: foodPreference]
        }

        analytics.trackAction(PIAnalytics.Action.bookingPreferencesChanged, userInfo: data)

        router?.finishedUpdatingPreference()
    }

    func mealPreferenceUpdateFailed(with error: Error?) {
        DispatchQueue.main.async {
            BARTDowntimeHandler.handle(error: error, withParentNavigationController: self.view?.parentNavigationController)

            self.view?.isProcessing = false
            self.router?.errorOccuredWhenUpdatingPreferences()
        }
    }
}
