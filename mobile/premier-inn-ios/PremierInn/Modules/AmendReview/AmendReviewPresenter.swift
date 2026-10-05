//
//  AmendReviewPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

class AmendReviewPresenter {
    weak var view: AmendReviewViewProtocol?
    var interactor: AmendReviewInteractorProtocol?
    var router: AmendReviewRouterProtocol?
    var pollingController: PollingController?

    deinit {
        print("DEINIT: \(self)")
    }
}

extension AmendReviewPresenter: AmendReviewViewEventHandler {
    func viewIsReady() {
        guard let viewModel = interactor?.amendReviewViewModel else { return }
        view?.update(with: viewModel)
    }

    func updatePaymentOptionSelection(paymentOption: PaymentIntervalOption) -> AmendReviewViewViewModel? {
        interactor?.paymentRequired = paymentOption == .now

        return interactor?.amendReviewViewModel
    }

    func confirmChangesButtonDidTap() {
        do {
            try interactor?.confirmChanges()
        } catch {
            view?.showError(
                with: PILocalizedString("errorMessage"),
                and: PILocalizedString("somethingWentWrongMessage"),
                completion: { _ in /* left empty intentionally to use default OK action */ }
            )
        }
    }
}

extension AmendReviewPresenter: AmendReviewInteractorOutput {
    func amendPayOnArrival() {
        view?.set(processing: true)

        interactor?.amendBooking { [weak self] result in
            self?.view?.set(processing: false)

            switch result {
            case .success:
                self?.pollingController = PollingController(delegate: self)
                self?.pollingController?.startPollingForBasketComplete()

            case .failure(let error as AmendReviewError):
                AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendConfirmError)
                self?.view?.amendFailed()

            case .failure(let error as NSError):
                AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendConfirmError)
                self?.view?.amendFailed()

            default:
                AnalyticsManager.shared.track(errorName: PIAnalytics.Error.amendConfirmError)
                self?.view?.amendFailed()
            }
        }
    }

    func goToAmendAndPayNowView(
        amendUpdateModel: AmendUpdateModel,
        amendAndPayViewModel: AmendAndPayViewModel,
        amendOperaDetails: AmendOperaDetails,
        reservationDetails: ReservationDetails,
        existingStay: Stay
    ) {
        router?.showAmendAndPayView(
            amendUpdateModel: amendUpdateModel,
            amendAndPayViewModel: amendAndPayViewModel,
            amendOperaDetails: amendOperaDetails,
            reservationDetails: reservationDetails,
            existingStay: existingStay
        )
    }
}

extension AmendReviewPresenter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true)
    }
}

extension AmendReviewPresenter: PollingDelegate {
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
                handler: { _ in /* left empty intentionally to use default OK action */ }
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

            view?.showCustomUIError(
                title: PILocalizedString("somethingWentWrongMessage"),
                message: error.localizedDescription,
                dismissButton: PILocalizedString("amendPaymentFailureTryAgain")
            )
        }
    }
}
