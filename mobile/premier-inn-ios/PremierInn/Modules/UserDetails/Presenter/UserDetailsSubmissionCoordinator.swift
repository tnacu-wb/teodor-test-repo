//
//  UserDetailsSubmitHandler.swift
//  PremierInn
//
//  Created by Clint Mengolli on 23/12/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import SimpleNetwork

final class UserDetailsSubmissionCoordinator {
    private enum Constants {
        static let genericErrorTitle = "userDetailsGenericErrorTitle"
        static let somethingWentWrongErrorTitle = "somethingWentWrongAlertTitle"
        static let guestDetailsErrorTitle = "guestDetailsErrorMessage"
    }

    private weak var view: UserDetailsViewProtocol?
    private weak var reviewBookDelegate: UserDetailsRouterDelegate?
    private weak var router: UserDetailsRouterProtocol?

    private let interactor: UserDetailsInteractorProtocol?

    private let getConfiguration: () -> UserDetailsConfiguration?
    private let updateMarketing: () -> Void

    init(
        view: UserDetailsViewProtocol?,
        router: UserDetailsRouterProtocol?,
        reviewBookDelegate: UserDetailsRouterDelegate?,
        interactor: UserDetailsInteractorProtocol?,
        getConfiguration: @escaping () -> UserDetailsConfiguration?,
        updateMarketing: @escaping () -> Void
    ) {
        self.view = view
        self.router = router
        self.reviewBookDelegate = reviewBookDelegate
        self.interactor = interactor
        self.getConfiguration = getConfiguration
        self.updateMarketing = updateMarketing
    }

    func submitButtonDidTap() {
        guard let scope = interactor?.scope else { return }

        beginSubmission()

        do {
            if scope == .userPreferences {
                try submitUserPreferences()
            } else {
                try submitBookingFlow()
            }
        } catch let error as RowValidatorError {
            handleRowValidationError(error)
        } catch {
            handleGenericError(error)
        }
    }
}

private extension UserDetailsSubmissionCoordinator {
    func beginSubmission() {
        view?.endEditing()
        lockForm()
    }

    func lockForm() {
        view?.toggleFormLock(locked: true, submitButtonTitle: nil)
    }

    func unlockForm() {
        view?.toggleFormLock(locked: false, submitButtonTitle: getConfiguration()?.submitButtonTitle)
    }

    func submitUserPreferences() throws {
        try view?.validateViewModel()

        let values = view?.getViewModelValues()
        updateMarketing()

        try interactor?.updateUser(with: values) { error in
            BARTDowntimeHandler.handle(error: error, withParentNavigationController: self.view?.parentNavigationController)

            self.unlockForm()

            let marketingOptIn = values?[Step1Row.marketing.rawValue] as? Bool ?? false
            let carRegistration = values?[Step1Row.carRegistration.rawValue] as? String

            AnalyticsManager.shared.trackAction(PIAnalytics.Action.updateUserDetails, userInfo: [
                PIAnalytics.Keys.accountChanged: true,
                PIAnalytics.Keys.userDetailsMarketingOptIn: marketingOptIn,
                PIAnalytics.Keys.userDetailsCar: (carRegistration ?? "").isNotEmpty
            ])

            if let error = error {
                DispatchQueue.main.async {
                    self.view?.showErrorMessage(
                        title: PILocalizedString(Constants.genericErrorTitle),
                        error: error
                    )
                }
            } else {
                DispatchQueue.main.async {
                    self.router?.goBackToMyAccount()
                }
            }
        }
    }

    func submitBookingFlow() throws {
        let values = view?.getViewModelValues()

        if values?[CountryActionableRow.addressLine1.rawValue] == nil {
            view?.addAddressManually()
        }

        try view?.validateViewModel()

        try self.continueWithBookingFlow(values: values)
    }

    func continueWithBookingFlow(values: PIDictionary?) throws {
        self.unlockForm()

        guard let output = try interactor?.getBookerGuestsAndPurpose(from: view?.getViewModelValues()) else {
            return
        }

        DispatchQueue.main.async {
            // Maybe fix for GraphQL
            self.lockForm()

            // Wait for hold booking to complete, for Bart in RequestsManager this just goes straight through.
            DispatchGroupManager.sharedInstance.holdBookingDispatchGroup.notify(queue: .main) {
                self.unlockForm()

                self.holdBookingWithGuests(output: output)
                self.router?.continueToNextScreen(output: output)
            }
        }
    }

    func handleRowValidationError(_ error: RowValidatorError) {
        view?.showErrorFor(row: error.row)
        unlockForm()
    }

    func handleGenericError(_ error: Error) {
        DispatchQueue.main.async {
            self.view?.showErrorMessage(title: PILocalizedString(Constants.genericErrorTitle), error: error)
            self.unlockForm()
        }
    }

    func holdBookingWithGuests(output: FormStep1Output) {
        interactor?.holdBookingWithGuests(output: output, isCiolFlow: false) { _, error in
            guard let error else { return }

            if let reviewBookDelegate = self.reviewBookDelegate {
                reviewBookDelegate.showError(
                    title: PILocalizedString(Constants.somethingWentWrongErrorTitle),
                    message: PILocalizedString(Constants.guestDetailsErrorTitle),
                    handler: { _ in
                        reviewBookDelegate.goBackToUserDetailsScreen()
                    }
                )
            } else {
                self.view?.showErrorMessage(
                    title: PILocalizedString(Constants.somethingWentWrongErrorTitle),
                    message: PILocalizedString(Constants.guestDetailsErrorTitle),
                    error: error,
                    handler: { _ in
                        self.router?.goBack()
                    }
                )
            }
        }
    }
}
