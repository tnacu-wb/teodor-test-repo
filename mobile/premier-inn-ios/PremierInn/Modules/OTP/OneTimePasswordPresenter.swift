//
//  OneTimePasswordPresenter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 08/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

class OneTimePasswordPresenter: NSObject {
    weak var view: OneTimePasswordViewInput?
    var interactor: OneTimePasswordInteractorInput?
    var router: OneTimePasswordRouterInput?
	private var analytics: AnalyticsType?
    var keyAddedDelegate: KeyAddedProtocol?

	init(analytics: AnalyticsType = AnalyticsManager.shared) {
		self.analytics = analytics
	}

    func otpErrorHandle() {
        view?.stopLoadingAnimation()

        let message = String(format: PILocalizedString("Error handle for OTP"))

        view?.showAlert(
            withTitle: PILocalizedString("Error handle for OTP", comment: "Reset password: alert title"),
            message: message
        )
    }

    internal var errorShouldShow = false
}

extension OneTimePasswordPresenter: OneTimePasswordPresenterInput {
    var screenName: String {
        PIAnalytics.StateNames.otpEmailVerify
    }

    var screenType: String {
        ""
    }

    func viewIsReady() {
        view?.updateScreenTitle(with: PILocalizedString("otpScreenTitle", comment: ""))

        view?.registerRows()
        view?.loadModelForView()
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters
    }

    func submitButtonTapped() {
        view?.validateForm()
    }

    func resendButtonTapped() {
        interactor?.otpResend(completion: { [weak self] (response, _) in
            if response == true {
                self?.view?.resendButtonCountdown()
            } else {
				self?.analytics?.track(errorName: PIAnalytics.Error.dkResendOTPFailed)
                self?.view?.updateTableError(error: PILocalizedString(
                    "otpGenericErrorMessage",
                    comment: "OTP generic error message"
                ))
            }
        })
    }

    func userTappedEmailAddressRow(at indexPath: IndexPath) {
        view?.makeRowFirstResponder(at: indexPath)
    }

    func formSubmissionErrorOccured(at rowIndex: IndexPath?) {
        guard let index = rowIndex else { return }

        view?.stopLoadingAnimation()
        view?.scrollToAndFocusRow(at: index)
    }


    func genericFormSubmissionErrorOccured(with error: Error) {
        view?.stopLoadingAnimation()
        view?.showAlert(
            with: PILocalizedString("otpGenericErrorMessage", comment: "Reset password: alert title"),
            error: error
        )
    }

    func codeSubmitted(code: String) {
        Task {
            await self.provisionPass(otpCode: code)
        }
    }

    @MainActor
    func provisionPass(otpCode: String) async {
        do {
            guard let pass = try await interactor?.provisionAppleWalletKey(otpCode: otpCode) else {
                view?.showAlert(
                    withTitle: PILocalizedString("addPassErrorTitle"),
                    message: PILocalizedString("addPassErrorMessage")
                )
                return }
            interactor?.trackStateAnalyticsForPKAddSecurePass(pageName: PIAnalytics.StateNames.digitalKeyAddPass)

            presentPassViewController(configuration: pass, delegate: self)
        } catch {
            DispatchQueue.main.async {
				self.analytics?.track(
				    error: error,
				    name: PIAnalytics.Error.dkProvisionFailed,
				    extraData: [
				        "booking reference": self.interactor?.stay.identifier ?? "",
				        "reservation Id": self.interactor?.roomId ?? ""
				    ]
				)
				self.view?.updateTableError(error: PILocalizedString(
				    "otpGenericErrorMessage",
				    comment: "OTP generic error message"
				))
            }
        }
    }

    func updateStayWithPassId(with id: String) {
        interactor?.updateStayWithPassId(with: id)

        // triggering the allocation process before going back to the key screen, otherwise the view will be reloaded into a redundant state while the room is being allocated
        keyAddedDelegate?.didFinishAddingKey()

        router?.goBackToKeyScreen()
    }
}
