//
//  AmendAndPayPresenter.swift
//  PremierInn
//
//  Created by Santa Gurung on 16/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import SimpleNetwork

protocol AmendAndPayPresenterProtocol {
    func viewIsReady()
    func didSelectPaymentMethod(selectedPaymentMethodViewModel: AmendPaymentMethodViewModel)
    func ctaButtonDidTap()
}

class AmendAndPayPresenter {
    weak var view: AmendAndPayViewProtocol?
    var router: AmendAndPayRouter?
    var interactor: AmendAndPayInteractorProtocol?
    var pollingController: PollingController?
}

extension AmendAndPayPresenter: AmendAndPayPresenterProtocol {
    func viewIsReady() {
        guard let viewModel = interactor?.amendAndPayViewModel else {
            view?.showAlertError(
                title: PILocalizedString("errorMessage"),
                message: nil,
                error: AmendAndPayError.viewModelNil,
                handler: { [weak self] _ in
                    self?.router?.goBack()
                }
            )
            return
        }
        view?.updateWith(amendAndPayViewModel: viewModel)
    }

    func ctaButtonDidTap() {
        view?.toggleLock(processing: true)

        interactor?.amendBooking(completion: { [weak self] result in
            guard let self = self else { return }
            view?.toggleLock(processing: false)

            switch result {
            case .success(let cccParams):
                router?.showIframe(cccpiPageParams: cccParams, threeCiPageDelegate: self, webDelegate: self)
                pollingController = PollingController(delegate: self)

            case .failure(let error):
                AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendConfirmError)

                view?.showAlertError(
                    title: PILocalizedString("somethingWentWrongMessage"),
                    message: PILocalizedString("amendPaymentFailureTryAgain"),
                    error: error,
                    handler: { _ in /* Use the default 'dismiss alert' when OK is pressed */ }
                )
            }
        })
    }

    func didSelectPaymentMethod(selectedPaymentMethodViewModel: AmendPaymentMethodViewModel) {
        guard let interactor = interactor else { return }
        let updatedViewModel = interactor
            .updatePaymentMethods(selectedPaymentMethodViewModel: selectedPaymentMethodViewModel)
        view?.updateWith(amendAndPayViewModel: updatedViewModel)
    }
}

extension AmendAndPayPresenter: PollingDelegate {
    func startConfirmationPolling() {
        view?.startConfirmationPolling()
    }

    func stopConfirmationPolling() {
        view?.stopConfirmationPolling()
    }

    func checkBasketStatus(
        transactionID: String,
        completion: @escaping (SimpleNetwork.Result<SimpleNetwork.BookingConfirmation>) -> Void
    ) {
        interactor?.checkBasketStatus(completion: completion)
    }

    func handleBookingResult(confirmation: BookingConfirmation, confirmationPollingFinished: Bool) {
        interactor?.trackBookingConfirmation()

        if let bookingReference = interactor?.amendOperaDetails?.bookingReference {
            interactor?.updateAmendedStay(bookingReference: bookingReference)
            router?.amendCompleted()
        }
    }

    func handleCCCPaymentFailure(with error: Error) {
        switch error {
        case AmendAndPayPaymentError.amendFailed:
            AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendPollingFailedError)

            view?.showAlertError(
                title: PILocalizedString("somethingWentWrongMessage"),
                message: PILocalizedString("amendFailedStatusMessage"),
                error: error,
                handler: { [weak self] _ in
                    self?.router?.goBackToBookingConfirmation()
                }
            )

        case PollingError.open:
            AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendPollingOpenError)

            view?.showAlertError(
                title: PILocalizedString("somethingWentWrongMessage"),
                message: PILocalizedString("amendOpenStatusMessage"),
                error: error,
                handler: { [weak self] _ in
                    self?.router?.goBackToAmendReview()
                }
            )

        case PollingError.maxAttempts:
            AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendPollingReachedMaxAttemptsError)

            view?.showAlertError(
                title: PILocalizedString("somethingWentWrongMessage"),
                message: String.localizedStringWithFormat(
                    PILocalizedString("amendPaymentPollingReachedMaxAttemptsMessage"),
                    interactor?.amendOperaDetails?.bookerEmail ?? PILocalizedString("your email address")
                ),
                error: error,
                handler: { [weak self] _ in
                    self?.router?.goBackToBookingConfirmation()
                }
            )

        default:
            AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendPollingFailedError)

            view?.showAlertError(
                title: PILocalizedString("somethingWentWrongMessage"),
                message: error.localizedDescription,
                error: error,
                handler: { _ in /* Use the default 'dismiss alert' when OK is pressed */ }
            )
        }
    }
}

extension AmendAndPayPresenter: ThreeCiPageDelegate {
    func finishedAuth(cardType: String?) {
        // TODO: Analytics tracking for card type
    }

    func startPolling(sender: ThreeCiPageViewController?, transactionID: String) {
        sender?.dismiss()
        pollingController?.startPollingForBasketComplete()
    }
}

extension AmendAndPayPresenter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true) {
            self.view?.stopDisplayingLoadingElements()
        }
    }
}
