//
//  PollingController.swift
//  PremierInn
//
//  Created by Santa Gurung on 09/12/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol PollingDelegate: AnyObject {
    func startConfirmationPolling()
    func stopConfirmationPolling()
    func checkBasketStatus(transactionID: String, completion: @escaping (Result<BookingConfirmation>) -> Void)
    func handleCCCPaymentFailure(with error: Error)
    func handleBookingResult(confirmation: BookingConfirmation, confirmationPollingFinished: Bool)
}

enum PollingError: Error {
    case open
    case maxAttempts
    case bookingConfirmationFailed
    case errorUI(ErrorUI)
}

class PollingController {
    private var confirmationPollingMaxTime: DispatchTime?
    private var transactionID: String?
    weak var delegate: PollingDelegate?

    init(delegate: PollingDelegate?) {
        self.delegate = delegate
    }

    func startPollingForBasketComplete(transactionID: String = "") {
        self.transactionID = transactionID
        let confirmationPollingStartTime = DispatchTime.now()
        let confirmationPollingMaxDuration = DispatchTimeInterval
            .seconds(SettingsManager.sharedInstance.confirmationPollingMessagesConfig.maxDurationOfPolling)
        confirmationPollingMaxTime = confirmationPollingStartTime + confirmationPollingMaxDuration

        delegate?.startConfirmationPolling()

        DispatchQueue.main
            .asyncAfter(deadline: .now() +
            .seconds(SettingsManager.sharedInstance.piRemoteConfig.confirmationPollingDelay)) { [self] in
            delegate?.checkBasketStatus(transactionID: transactionID, completion: handleConfirmationPollingResult)
        }
    }

    private func handleConfirmationPollingResult(result: Result<BookingConfirmation>) {
        switch result {
        case .success(let confirmation):
            switch confirmation.bookingConfirmationOperaStatus?.basketStatus {
            case .pending, .payPending, .amending, .none:
                retryConfirmationPolling()

            case .complete, .amended, .preCheckedIn, .preCheckedOut:
                delegate?.stopConfirmationPolling()
                delegate?.handleBookingResult(confirmation: confirmation, confirmationPollingFinished: true)

            case .failed:
                delegate?.stopConfirmationPolling()
                if let description = confirmation.bookingConfirmationOperaStatus?.basketError?.description,
                   let errorUI = cccpErrorUI(
                       errorDescription: description,
                       errorCode: confirmation.bookingConfirmationOperaStatus?.basketError?.code ?? ""
                   ) {
                    delegate?.handleCCCPaymentFailure(with: PollingError.errorUI(errorUI))
                } else {
                    delegate?.handleCCCPaymentFailure(with: PollingError.bookingConfirmationFailed)
                }

            case .ciolFailed:
                delegate?.stopConfirmationPolling()
                delegate?.handleCCCPaymentFailure(with: CiolReviewAndPayPaymentError.ciolFailed)

            case .amendFailed:
                delegate?.stopConfirmationPolling()
                delegate?.handleCCCPaymentFailure(with: AmendAndPayPaymentError.amendFailed)

            case .open:
                delegate?.stopConfirmationPolling()
                delegate?.handleCCCPaymentFailure(with: PollingError.open)
            }

        case .failure:
            retryConfirmationPolling()
        }
    }

    private func retryConfirmationPolling() {
        guard let confirmationPollingMaxTime = confirmationPollingMaxTime,
              DispatchTime.now() < confirmationPollingMaxTime else {
            delegate?.stopConfirmationPolling()
            delegate?.handleCCCPaymentFailure(with: PollingError.maxAttempts)
            return
        }
        DispatchQueue.main
            .asyncAfter(deadline: .now() +
            .seconds(SettingsManager.sharedInstance.piRemoteConfig.confirmationPollingInterval)) {
            self.delegate?.checkBasketStatus(
                transactionID: self.transactionID ?? "",
                completion: self.handleConfirmationPollingResult
            )
        }
    }

    private func cccpErrorUI(errorDescription: String, errorCode: String) -> ErrorUI? {
        // We use the description to map the errors, not the code
        guard let mappedError = MSMappedError(rawValue: errorDescription) else { return nil }

        return ErrorUI(
            title: PILocalizedString("Something went wrong"),
            description: mappedError.errorMessage,
            presentationStyle: .customAlert,
            actions: nil,
            code: errorCode
        )
    }
}
