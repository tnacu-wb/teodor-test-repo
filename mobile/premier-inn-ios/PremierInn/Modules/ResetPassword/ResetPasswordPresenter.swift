//
//  ResetPasswordPresenter.swift
//  PremierInn
//
//  Created by Nick Jones on 08/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

protocol ResetPasswordPresenterInput {
    var screenName: String { get }
    var screenType: String { get }

    var emailAddress: String { get set }
    var shouldShowError: Bool { get set }

    func viewIsReady()
    func submitButtonTapped()
    func userTappedEmailAddressRow(at indexPath: IndexPath)
    func formSubmissionErrorOccured(at rowIndex: IndexPath?)
    func genericFormSubmissionErrorOccured(with error: Error)

    func userSubmitted(emailAddress: String)
    func userClosedSuccessAlert()
}

class ResetPasswordPresenter {
    var view: ResetPasswordViewInput?
    var interactor: ResetPasswordInteractorInput?
    var router: ResetPasswordRouterInput?


    func emailAddressSuccesfullyUpdated() {
        view?.stopLoadingAnimation()

        let message = String(format: PILocalizedString("resetPasswordSuccessAlertMessage"), emailAddress)

        view?.showAlert(
            withTitle: PILocalizedString("resetPasswordAlertTitle", comment: "Reset password: alert title"),
            message: message
        )
    }

    internal var errorShouldShow = false
}

extension ResetPasswordPresenter: ResetPasswordPresenterInput {
    var screenName: String {
        guard let tabBarIsHidden = view?.tabBarIsHidden else { return "" }
        return tabBarIsHidden ? PIAnalytics.StateNames.resetPassword : PIAnalytics.StateNames.myPiResetPassword
    }

    var screenType: String {
        guard let tabBarIsHidden = view?.tabBarIsHidden else { return "" }
        return tabBarIsHidden ? PIAnalytics.StateTypes.bookingFlow : PIAnalytics.StateTypes.myPI
    }

    var emailAddress: String {
        get {
            interactor?.emailAddress ?? ""
        }
        set {
            interactor?.emailAddress = newValue
        }
    }

    var shouldShowError: Bool {
        get {
            errorShouldShow
        }
        set {
            errorShouldShow = newValue
            view?.loadModelForView()
        }
    }

    func viewIsReady() {
        view?.updateScreenTitle(with: PILocalizedString("resetPasswordScreenTitle", comment: "Reset password: screen title"))

        view?.registerRows()
        view?.loadModelForView()
    }

    func submitButtonTapped() {
        view?.validateForm()
    }

    func userTappedEmailAddressRow(at indexPath: IndexPath) {
        view?.makeRowFirstResponder(at: indexPath)
    }

    func formSubmissionErrorOccured(at rowIndex: IndexPath?) {
        guard let index = rowIndex else { return }

        view?.stopLoadingAnimation()
        view?.scrollToAndFocusRow(at: index)
    }

    func userClosedSuccessAlert() {
        router?.popModule(with: emailAddress)
    }

    func genericFormSubmissionErrorOccured(with error: Error) {
        view?.stopLoadingAnimation()
        view?.showAlert(
            with: PILocalizedString("resetPasswordAlertTitle", comment: "Reset password: alert title"),
            error: error
        )
    }

    func userSubmitted(emailAddress: String) {
        interactor?.submitPasswordResetRequest(withEmailAddress: emailAddress, completion: { [weak self] (error, success) in
            self?.emailAddress = emailAddress
            self?.shouldShowError = error != nil

            guard error == nil, success == true else {
                return BARTDowntimeHandler.handle(
                    error: error,
                    withParentNavigationController: self?.view?.parentNavigationController
                )
            }

            self?.emailAddressSuccesfullyUpdated()
        })
    }
}
