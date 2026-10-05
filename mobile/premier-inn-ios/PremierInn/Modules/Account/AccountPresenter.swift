//
//  AccountPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 19/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation
import UIKit

class AccountPresenter {
    weak var view: AccountViewProtocol?

    var router: AccountRouterProtocol?
    var interactor: AccountInteractorProtocol?

    private var userWasJustRegistered = false
    private var userHasJustChangedPassword = false
    private var userHasJustChangedTheirDetails = false
    private var userHasDeletedCard = false
    private var userHasSavedCard = false
    private var userHasUpdatedCard = false

    deinit {
        print("DEINIT: \(self)")
        NotificationCenter.default.removeObserver(self)
    }

    @objc private func userDidChange(notification: Notification) {
        reloadViewModel(and: true)
    }

    private func reloadViewModel(and shouldScrollToTop: Bool = false) {
        guard let viewModel = interactor?.accountViewModel else { return }

        view?.reloadData(with: viewModel)

        if shouldScrollToTop { view?.scrollToTop() }
    }
}

extension AccountPresenter: AccountPresenterProtocol {}

extension AccountPresenter: AccountViewEventHandler {
    func viewIsReady() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(userDidChange(notification:)),
            name: .userDidChange,
            object: nil
        )

        reloadViewModel(and: false)
    }

    func viewDidAppear() {
        if userHasJustChangedPassword.reset() {
            guard let message = interactor?.passwordChangedMessage else { return }
            view?.showSuccessConfirmation(withMessage: message)

            return
        }

        if userWasJustRegistered.reset() {
            guard let message = interactor?.accountCreatedMessage else { return }
            view?.showSuccessConfirmation(withMessage: message)

            return
        }

        if userHasJustChangedTheirDetails.reset() {
            guard let message = interactor?.detailsChangedMessage else { return }
            view?.showSuccessConfirmation(withMessage: message)

            reloadViewModel(and: false)
        }

        if userHasDeletedCard.reset() {
            guard let message = interactor?.deletedCardMessage else { return }
            view?.showSuccessConfirmation(withMessage: message)

            reloadViewModel(and: false)
        }

        if userHasSavedCard.reset() {
            guard let message = interactor?.savedCardMessage else { return }
            view?.showSuccessConfirmation(withMessage: message)

            reloadViewModel(and: false)
        }

        if userHasUpdatedCard.reset() {
            guard let message = interactor?.updatedCardMessage else { return }
            view?.showSuccessConfirmation(withMessage: message)

            reloadViewModel(and: false)
        }
    }

    func myDetailsButtonDidTap() {
        router?.openMyDetails(withCompletionDelegate: self, deleteAccountDelegate: self)
    }

    func logoutButtonDidTap() {
        interactor?.userLoggedOut()
    }

    func registerButtonDidTap() {
        router?.openRegister(withCompletionDelegate: self)
    }

    func loginButtonDidTap() {
        router?.openLogin()
    }

    func bookingPreferencesButtonDidTap() {
        router?.openBookingPreferences()
    }

    func newsletterUpdatesButtonDidTap() {
        guard let emailAddress = interactor?.accountViewModel?.email else { return }
        router?.openNewsletterPreferences(with: emailAddress)
    }

    func changePasswordButtonDidTap() {
        router?.openChangePassword(withCompletionDelegate: self)
    }

    func paymentMethodsButtonDidTap() {
        interactor?.updateUser(shouldAttemptLogin: true) {
            self.router?.openPaymentMethods(
                paymentMethodDelegate: self,
                cardDetailsDelegate: self,
                addNewCardRouterDelegate: self
            )
        }
    }
}

extension AccountPresenter: RegisterCompletionInput {
    func userWasRegistered() {
        userWasJustRegistered = true
    }
}

extension AccountPresenter: ChangePasswordCompletionInput {
    func passwordWasUpdated() {
        userHasJustChangedPassword = true
    }
}

extension AccountPresenter: UserDetailsRouterDelegate {
    func userDetailsDidFinish(with bookingDetails: BookingDetails, output: FormStep1Output, sender: UIViewController) {}

    func userDetailsWereUpdated() {
        userHasJustChangedTheirDetails = true
    }
}

extension AccountPresenter: DeleteAccountDelegate {
    func userHasDeletedAccount() {
        interactor?.userLoggedOut()
        userHasJustChangedTheirDetails = true
    }
}

extension AccountPresenter: CardDetailsRouterDelegate {
    func newSavedCard() {
        userHasSavedCard = true
    }

    func updatedStoredCard() {
    }
}

extension AccountPresenter: AddNewCardRouterDelegate {
    func cardUpdated() {
        userHasUpdatedCard = true
    }
}

extension AccountPresenter: PaymentMethodsRouterDelegate {
    func selectedCard() {
    }

    func cardDelete() {
        userHasDeletedCard = true
    }
}
