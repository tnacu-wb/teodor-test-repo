//
//  RegCardOutput.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 08.04.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

struct AuthorizationMessage: Codable {
    let cardType: String
    let tokenNo: String
    let paymentId: String
    let paymentStatus: PaymentStatus
    let transactionId: String
    let merchantReference: String
    let authCode: String

    enum PaymentStatus: String, Codable {
        case success = "SUCCESS"
        case failure = "FAILURE"
    }
}

protocol RegCardPaymentDelegate: AnyObject {
    func didFinish(result: Result<Void>)
}
protocol RegCardOutput {
    @MainActor
    func startLoading()
    @MainActor
    func stopLoadingUI(error: Error?)
    @MainActor
    func setupThreeCIpage(for response: CCCPPaymentResponse)
    func goToCompletion(error: Error?, ciolConfirmationDetails: CiolConfirmationDetails)
}

protocol RegCardProvider {
    func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool,
        completion: @escaping (_ response: ConfirmPreCheckInOut?, _ error: Error?) -> Void
    )
    func authorizePayment() async throws -> CCCPPaymentProviderResponse?
    func attachFileToReservation(params: AuthorizationFileAttachmentParams) async throws -> StatusResult?
    func updatePreCheckInStatus(params: UpdatePrecheckInParams) async throws -> StatusResult?
}

protocol CanCompleteRegCard: AnyObject {
    func confirmPreCheckIn(provider: some RegCardProvider, basketReference: String, isCiol: Bool) async throws
    func authorizePayment(provider: some RegCardProvider) async throws -> CCCPPaymentProviderResponse?
    func attachFileToReservation(
        provider: some RegCardProvider,
        params: AuthorizationFileAttachmentParams
    ) async throws -> StatusResult.Status?
    func updatePreCheckInStatus(provider: some RegCardProvider, params: UpdatePrecheckInParams) async throws -> StatusResult
        .Status?
    var authorizationBannerMessage: NSAttributedString { get }
}
struct RegCardInput {
    var pdfInput: PDFBookingDetails
    let fileAttachmentInput: AuthorizationFileAttachmentParams
    let updatePrecheckinInput: UpdatePrecheckInParams
    let confirmInput: CiolConfirmationDetails?
    let shouldAttachPDF: Bool
}
extension CanCompleteRegCard {
    var authorizationBannerMessage: NSAttributedString {
        let muttableAttributedString = NSMutableAttributedString()

        muttableAttributedString
            .append(NSAttributedString(string: PILocalizedString("deRegCardAuthorizationDisclaimerMessagePart1")))
        muttableAttributedString.append(NSAttributedString(
            string: PILocalizedString("deRegCardAuthorizationDisclaimerMessagePart2"),
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        ))
        muttableAttributedString
            .append(NSAttributedString(string: PILocalizedString("deRegCardAuthorizationDisclaimerMessagePart3")))
        muttableAttributedString.append(NSAttributedString(
            string: "\n\n" + PILocalizedString("deRegCardAuthorizationDisclaimerMessagePart4"),
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        ))
        muttableAttributedString
            .append(NSAttributedString(string: PILocalizedString("deRegCardAuthorizationDisclaimerMessagePart5")))

        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 4

        muttableAttributedString.addAttribute(
            .paragraphStyle,
            value: paragraphStyle,
            range: NSRange(location: 0, length: muttableAttributedString.string.count)
        )
        return muttableAttributedString
    }

    func updatePreCheckInStatus(provider: some RegCardProvider, params: UpdatePrecheckInParams) async throws -> StatusResult
        .Status? {
        do {
            guard let statusResult = try await provider.updatePreCheckInStatus(params: params) else {
                throw CIOLError.performPrestayChecks
            }
            return statusResult.status
        } catch {
            throw CIOLError.performPrestayChecks
        }
    }
    func attachFileToReservation(
        provider: some RegCardProvider,
        params: AuthorizationFileAttachmentParams
    ) async throws -> StatusResult.Status? {
        do {
            guard let statusResult = try await provider.attachFileToReservation(params: params) else {
                throw CIOLError.performPrestayChecks
            }
            return statusResult.status
        } catch {
            throw error
        }
    }

    func authorizePayment(provider: some RegCardProvider) async throws -> CCCPPaymentProviderResponse? {
        do {
            guard let response = try await provider.authorizePayment() else { throw CIOLError.performPrestayChecks }
            return response
        } catch {
            throw CIOLError.performPrestayChecks
        }
    }

    func confirmPreCheckIn(provider: some RegCardProvider, basketReference: String, isCiol: Bool = false) async throws {
        try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
            provider.confirmPreCheckInOut(basketReference: basketReference, type: .checkIn, isCiol: isCiol) { _, error in
                guard error == nil else {
                    continuation.resume(throwing: CIOLError.performPrestayChecks)
                    return
                }
                continuation.resume()
            }
        }
    }

    @MainActor
    func finishRegCardWithoutOutstanding(
        regcardInput: RegCardInput,
        provider: some RegCardProvider,
        output: some RegCardOutput
    ) {
        var generateResult: PDFGeneratorCiol.Result?
        if regcardInput.shouldAttachPDF {
            do {
                generateResult = try generatePDF(details: regcardInput.pdfInput)
            } catch {
                output.stopLoadingUI(error: CIOLError.performPrestayChecks)
                return
            }
        }

        Task { @MainActor in
            output.startLoading()
            do {
                if regcardInput.shouldAttachPDF {
                    guard let generateResult else {
                        throw CIOLError.performPrestayChecks
                    }
                    // Perform request to attach pdf to reservation
                    let attachParams = AuthorizationFileAttachmentParams.init(
                        fileName: generateResult.fileName,
                        reservationId: regcardInput.fileAttachmentInput
                                                                              .reservationId,
                        hotelId: regcardInput.fileAttachmentInput
                                                                              .hotelId,
                        fileAttachment: generateResult.data
                    )
                    let attachResult = try await attachFileToReservation(provider: provider, params: attachParams)
                    guard attachResult == .success else {
                        throw CIOLError.performPrestayChecks
                    }
                }

                // Perform request to update precheckin status

                let updatePreCheckinResult = try await updatePreCheckInStatus(
                    provider: provider,
                    params: regcardInput.updatePrecheckinInput
                )
                guard updatePreCheckinResult == .success else {
                    throw CIOLError.performPrestayChecks
                }

                // Perform request to finalize checkin
                guard let confirmInput = regcardInput.confirmInput else {
                    throw CIOLError.performPrestayChecks
                }
                try await checkIn(
                    provider: provider,
                    confirmInput: confirmInput,
                    reservationID: regcardInput.pdfInput.reservationID,
                    output: output
                )
            } catch {
                output.stopLoadingUI(error: CIOLError.performPrestayChecks)
            }
        }
    }

    func finishRegCardWithOutstanding(
        regcardInput: RegCardInput,
        provider: some RegCardProvider,
        bookingConfirmation: BookingConfirmation
    ) async throws {
        var generateResult: PDFGeneratorCiol.Result?
        do {
            if regcardInput.shouldAttachPDF {
                generateResult = try generatePDF(details: regcardInput.pdfInput)
                guard let generateResult else {
                    throw CIOLError.genericPayment
                }
                // Perform request to attach pdf to reservation
                let attachParams = AuthorizationFileAttachmentParams.init(
                    fileName: generateResult.fileName,
                    reservationId: regcardInput.fileAttachmentInput
                                                                          .reservationId,
                    hotelId: regcardInput.fileAttachmentInput.hotelId,
                    fileAttachment: generateResult.data
                )
                let attachResult = try await attachFileToReservation(provider: provider, params: attachParams)
                guard attachResult == .success else {
                    throw CIOLError.genericPayment
                }
            }

            // Perform request to update precheckin status

            let updatePreCheckinResult = try await updatePreCheckInStatus(
                provider: provider,
                params: regcardInput.updatePrecheckinInput
            )
            guard updatePreCheckinResult == .success else {
                throw CIOLError.genericPayment
            }
        }
    }

    private func generatePDF(details: PDFBookingDetails) throws -> PDFGeneratorCiol.Result {
        try PDFGeneratorCiol(pdfBookingDetails: details).generateCiolPDF()
    }

    func showAuthorize(provider: some RegCardProvider, output: some RegCardOutput) {
        Task {
            do {
                let authConfig = try await authorizePayment(provider: provider)
                let response = CCCPPaymentResponse(paymentRequiredDetails: authConfig, status: nil)
                await output.setupThreeCIpage(for: response)
            } catch {
                await output.stopLoadingUI(error: CIOLError.performPrestayChecks)
            }
        }
    }

    func checkIn(
        provider: some RegCardProvider,
        confirmInput: CiolConfirmationDetails,
        reservationID: String,
        output: some RegCardOutput
    ) async throws {
        let completion = { error in
            _ = Task { @MainActor in
                output.goToCompletion(error: error, ciolConfirmationDetails: confirmInput)
            }
        }
        do {
            try await confirmPreCheckIn(provider: provider, basketReference: reservationID)
            completion(nil)
        } catch {
            completion(CIOLError.performPrestayChecks)
        }
    }
}
