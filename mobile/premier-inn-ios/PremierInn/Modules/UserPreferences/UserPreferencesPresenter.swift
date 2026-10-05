//
//  UserPreferencesPresenter.swift
//  PremierInn
//
//  Created by Nick Jones on 30/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

protocol UserPreferencesPresenterInput {
    var numberOfRows: Int { get }
    var userPreferencesTracking: UserPreferencesTracking { get }

    func viewIsReady()
    func viewDidDisappear()
    func reloadRows()
    func selectedRow(atIndex index: IndexPath)
    func userPreferenceRow(forPath path: IndexPath) -> UserPreferenceRow?
    func userPreferenceUpdated(preferenceUpdated: UserPreferencesPresenter.Preference)
    func userPreferenceChangedMessage() -> String?
    func shouldShowUserUpdateFailedError() -> Bool
    func errorOccuredWhenUpdatingPreferences()
}

class UserPreferencesPresenter {
    enum Preference {
        case Room
        case Meal
    }

    var router: UserPreferencesRouterInput?
    weak var view: UserPreferencesViewInput?
    var interactor: UserPreferencesInteractorInput?

    var shouldShowPreferencesChangedMessage = false
}

extension UserPreferencesPresenter: UserPreferencesPresenterInput {
    var numberOfRows: Int { interactor?.numberOfRows ?? 0 }
    var userPreferencesTracking: UserPreferencesTracking {
        interactor?.userPreferencesTracking ?? ("", "")
    }

    func viewIsReady() {
        view?.set(title: interactor?.viewTitle)
    }

    func viewDidDisappear() {
        interactor?.resetErrorOccuredWhenUpdatingUserPreferencesSettings()
    }

    func reloadRows() {
        interactor?.updateAllRows()
        view?.refreshRows()
    }

    func selectedRow(atIndex index: IndexPath) {
        guard let row = interactor?.userPreferenceRow(forPath: index) else { return }
        router?.selectedRow(ofUserPreferenceType: row.userPreferenceType)
    }

    func userPreferenceRow(forPath path: IndexPath) -> UserPreferenceRow? {
        interactor?.userPreferenceRow(forPath: path) ?? nil
    }

    func userPreferenceUpdated(preferenceUpdated: UserPreferencesPresenter.Preference) {
        shouldShowPreferencesChangedMessage = true
    }

    func userPreferenceChangedMessage() -> String? {
        let messageToShow = shouldShowPreferencesChangedMessage ? PILocalizedString(
            "userPreferencesChangedMessage",
            comment: "User Preferences Changed Message"
        ) : nil
        shouldShowPreferencesChangedMessage = false

        return messageToShow
    }

    func errorOccuredWhenUpdatingPreferences() {
        interactor?.errorOccuredWhenUpdatingUserPreferences()
    }

    func shouldShowUserUpdateFailedError() -> Bool {
        interactor?.shouldShowUserPreferenceUpdateErrorRow() ?? false
    }
}
