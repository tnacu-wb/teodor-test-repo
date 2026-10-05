//
//  DeleteAccountPresenter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 28/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

protocol DeleteAccountPresenterInput {
    func viewIsReady()
    func submitButtonTapped()
    func forgotPasswordTapped()
    func formSubmissionErrorOccured(at rowIndex: IndexPath?)
    func genericFormSubmissionErrorOccured(with error: Error)

    func deleteAccount(password: String)
}

class DeleteAccountPresenter {
    var view: DeleteAccountViewInput?
    var interactor: DeleteAccountInteractorInput?
    var router: DeleteAccountRouterInput?
}

extension DeleteAccountPresenter: DeleteAccountPresenterInput {
    func viewIsReady() {
        view?.updateScreenTitle(with: PILocalizedString("deleteAccountViewTitle", comment: ""))

        view?.registerRows()
        view?.loadModelForView()
    }

    func submitButtonTapped() {
        view?.validateForm()
    }

    func forgotPasswordTapped() {
         router?.goToForgotPassword()
    }

    func formSubmissionErrorOccured(at rowIndex: IndexPath?) {
        guard let index = rowIndex else { return }

        view?.stopLoadingAnimation()
        view?.scrollToAndFocusRow(at: index)
    }

    func genericFormSubmissionErrorOccured(with error: Error) {
        view?.stopLoadingAnimation()
        view?.showAlert(with: PILocalizedString("deleteAccountGenericAlertTitle"), error: error)
    }

    func deleteAccount(password: String) {
        interactor?.deleteAccountRequest(withPassword: password, completion: { _, success in
            if success {
                self.router?.goToUserDetails()
            } else {
                DispatchQueue.main.async {
                self.view?.showAlert(withTitle: PILocalizedString("deleteAccountErrorAlertTitle"), message: "")

                self.view?.stopLoadingAnimation()
                }
            }
        })
    }
}
