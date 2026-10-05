//
//  ChangePasswordPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 14/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

protocol ChangePasswordPresenterProtocol {
    var changePasswordTracking: ChangePasswordTracking { get }

    func viewIsReady()
    func changePasswordButtonDidTap(existingPassword: String, newPassword: String, newPasswordConfirm: String)
    func forgotPasswordDidTap(email: String?)
    var isBusinessLogin: Bool { get }
}

class ChangePasswordPresenter {
    weak var view: ChangePasswordViewProtocol?
    var router: ChangePasswordRouterProtocol?
    var interactor: ChangePasswordInteractorProtocol?
    var analytics: AnalyticsType = AnalyticsManager.shared
}

extension ChangePasswordPresenter: ChangePasswordPresenterProtocol {
    var isBusinessLogin: Bool { interactor?.isBusinessLogin ?? false }

    var changePasswordTracking: ChangePasswordTracking {
        interactor?.changePasswordTracking ?? ("", "")
    }

    func viewIsReady() {
        view?.setTitle(title: interactor?.viewTitle ?? "")
        view?.loadViewModel()
    }

    func forgotPasswordDidTap(email: String?) {
        router?.showForgotPasswordModule(email: email, isBusiness: interactor?.isBusinessLogin ?? false)
    }
    func changePasswordButtonDidTap(existingPassword: String, newPassword: String, newPasswordConfirm: String) {
        do {
            try interactor?.changePassword(
                existingPassword: existingPassword,
                newPassword: newPassword,
                newPasswordConfirm: newPasswordConfirm
            )
        } catch let error as ChangePasswordInteractorError {
            switch error {
            case .newPasswordInvalidLength:
                view?.invalidFormatPassword()
                return
            default:
                view?.showError(string: error.localizedDescription)
            }
        } catch let error as NSError {
            print(error)
        }
    }
}

extension ChangePasswordPresenter: ChangePasswordInteractorDelegate {
    func passwordChanged(message: String) {
        analytics.trackAction(PIAnalytics.Action.passwordChanged, userInfo: nil)

        router?.passwordDidChange()
    }

    func passwordChangeFailed(message: String) {
        DispatchQueue.main.async { self.view?.passwordChangeFailed(message: message) }
    }

    func errorOccuredWhenChangingPassword(error: Error) {
        DispatchQueue.main.async { self.view?.errorOccuredWhenChangingPassword(error: error) }
    }
}
