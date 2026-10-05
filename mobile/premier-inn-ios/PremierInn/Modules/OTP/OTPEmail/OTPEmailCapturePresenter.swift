//
//  OneTimePasswordCapturePresenter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 01/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

protocol OTPEmailCapturePresenterInput {
    var screenName: String { get }
    var screenType: String { get }

    var shouldShowError: Bool { get set }

    func viewIsReady()
    func submitButtonTapped()
    func userTappedEmailAddressRow(at indexPath: IndexPath)
    func formSubmissionErrorOccured(at rowIndex: IndexPath?)
    func genericFormSubmissionErrorOccured(with error: Error)

    func userSubmitted(emailAddress: String)
}

enum EmailCaptureError: Error {
	case generateOTPError
}

class OTPEmailCapturePresenter {
    var view: OTPEmailCaptureViewInput?
    var interactor: OTPEmailCaptureInteractorInput?
    var router: OTPEmailCaptureRouterInput?

    internal var errorShouldShow = false
}

extension OTPEmailCapturePresenter: OTPEmailCapturePresenterInput {
    var screenName: String {
        PIAnalytics.StateNames.otpEmailCapture
    }

    var screenType: String {
        ""
    }

    var shouldShowError: Bool {
        get {
            errorShouldShow
        }
        set {
            errorShouldShow = newValue
            view?.loadModelForView(defaultEmail: nil)
        }
    }

    func viewIsReady() {
        view?.updateScreenTitle(with: PILocalizedString("verifyEmailScreenTitle", comment: "Reset password: screen title"))

        view?.registerRows()
		view?.loadModelForView(defaultEmail: interactor?.stay.bookerEmail)
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters
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

    func genericFormSubmissionErrorOccured(with error: Error) {
        view?.stopLoadingAnimation()
        view?.showAlert(
            with: PILocalizedString("Something went wrong", comment: "Reset password: alert title"),
            error: error
        )
    }

    func userSubmitted(emailAddress: String) {
        interactor?.generateOTP(withEmailAddress: emailAddress, completion: { [weak self] (success, error) in
            self?.view?.stopLoadingAnimation()
            guard error == nil, success == true, let stay = self?.interactor?.stay,
                  let roomId = self?.interactor?.roomId else {
				AnalyticsManager.shared.track(
				    error: error ?? EmailCaptureError.generateOTPError,
				    name: PIAnalytics.Error.dkGenerateOTPFailed,
				    extraData: ["Success": success ?? false]
				)
                self?.view?.showAlert(
                    withTitle: PILocalizedString("Something went wrong"),
                    message: PILocalizedString("Please try again")
                )
                return
            }

            self?.router?.openOTPScreen(with: emailAddress, stay: stay, roomId: roomId)
        })
    }
}
