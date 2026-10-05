//
//  PaymentDetailsInteractor.swift
//  PremierInn
//
//
//  Created Freddie Parks on 07/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//
//

import Foundation
import SimpleNetwork
import UIKit

protocol PaymentDetailsCheckInDataProvider {
    func checkInOnlinePayment(
        with sessionId: String,
        confirmationNumber: String,
        paymentDetails: PaymentDetails,
        completion: @escaping (_ response: CheckInPaymentResponse?, _ error: Error?) -> Void
    )
    func completePayment(
        with sessionId: String,
        and paRes: String,
        completion: @escaping (_ response: Bool?, _ error: Error?) -> Void
    )
}

typealias CIOLPaymentParams = (
    sessionId: String,
    confirmationNumber: String,
    confirmationEmailAddress: String,
    reservation: Reservation?,
    canChangeCard: Bool
)

extension RequestsManager: PaymentDetailsCheckInDataProvider {}

class PaymentDetailsInteractor {
    private struct ViewModel: PaymentDetailsViewModel {
        let infoMessage: String?
        let cardViewModel: PaymentCardSummaryViewModel
        let confirmationViewModel: PaymentDetailsConfirmationViewModel
    }

    private struct CVVModel: PaymentCardCVVModel {
        let cvvLength: Int
        let cvvInputHelperDescription: String
    }

    private struct CardViewModel: PaymentCardSummaryViewModel {
        let sectionTitle: String?
        let canChangeCard: Bool
        let cardTitle: String
        let cardType: String
        let cardIconUrl: URL?
        let cardNumberDescription: String
        let cvvModel: PaymentCardCVVModel?
        let showCardAuthenticationMessage: Bool
    }

    private struct ConfirmViewModel: PaymentDetailsConfirmationViewModel {
        let termsMessage: NSAttributedString?
        let bookingManagementTermsMessage: NSAttributedString?
        let totalLabel: String
        let total: String
        let ctaTitle: String
        let ctaIcon: UIImage?
    }

    private let scope: PaymentDetailsScope

    private var paymentCard: PaymentCard
    private var dataProvider: PaymentDetailsCheckInDataProvider
    private var paymentSessionID: String?

    var ciolParams: CIOLPaymentParams?
    var totalCost: Cost
    var delegate: PaymentDetailsInteractorDelegate?
    var cancelableText: String?

    init(
        with scope: PaymentDetailsScope = .ciol,
        using paymentCard: PaymentCard,
        for totalCost: Cost,
        and dataProvider: PaymentDetailsCheckInDataProvider = RequestsManager(),
        ciolPaymentParams: CIOLPaymentParams? = nil,
        cancelableText: String? = nil
    ) {
        self.scope = scope
        self.paymentCard = paymentCard
        self.totalCost = totalCost
        self.dataProvider = dataProvider
        self.ciolParams = ciolPaymentParams
        self.cancelableText = cancelableText
    }

    private func makeCIOLPayment(
        using cvv: String?,
        completion: @escaping (_ checkedIn: Bool, _ threeDSRequest: URLRequest?) -> Void
    ) {
        guard let sessionId = ciolParams?.sessionId,
              let confirmationNumber = ciolParams?.confirmationNumber,
              let confirmationEmail = ciolParams?.confirmationEmailAddress else {
            completion(false, nil)
            return
        }

        let paymentDetails = PaymentDetails(
            card: paymentCard,
            address: paymentCard.address,
            useStoredCard: true,
            confirmationEmailAddress: confirmationEmail,
            cvv: cvv ?? ""
        )

        dataProvider.checkInOnlinePayment(
            with: sessionId,
            confirmationNumber: confirmationNumber,
            paymentDetails: paymentDetails
        ) { response, error in
            if let error = error {
                self.delegate?.paymentFailed(with: error.localizedDescription)
                return
            }

            guard let response = response else {
                self.delegate?.paymentFailed(with: "No response")
                return
            }

            switch (response.checkInComplete, response.threeDSecureRequired) {
            case (true, false):
                completion(true, nil)
                return
            case (false, true):
                guard let url = response.redirectURL else { return completion(false, nil) }
                guard let token = response.pareq else { return completion(false, nil) }

                self.paymentSessionID = response.sessionId

                do {
                    let parameters = ["PaReq": token, "TermUrl": Constants.threeDeeSecureCallbackURLString, "MD": ""]
                    let request = try RequestsManager.postRequest(with: url, parameters: parameters)

                    return completion(false, request)
                } catch {
                    return completion(false, nil)
                }
            default:
                self.delegate?.paymentFailed(with: "Unexpected response")
                return
            }
        }
    }
}

extension PaymentDetailsInteractor: PaymentDetailsInteractorProtocol {
    var viewModel: PaymentDetailsViewModel? {
        ViewModel(
            infoMessage: infoMessage,
            cardViewModel: cardViewModel,
            confirmationViewModel: confirmationViewModel
        )
    }

    func makePayment(using cvv: String?, completion: @escaping (_ checkedIn: Bool, _ threeDSRequest: URLRequest?) -> Void) {
        switch scope {
        case .ciol:
            makeCIOLPayment(using: cvv, completion: completion)
        }
    }

    func completePayment(
        with pares: String,
        completion: @escaping (_ paymentSuccessful: Bool, _ errorMessage: String?) -> Void
    ) {
        guard let sessionId = ciolParams?.sessionId else {
            completion(false, PILocalizedString("Missing session id"))
            return
        }

        dataProvider.completePayment(with: sessionId, and: pares) { success, error in
            if let error = error {
                return completion(false, error.localizedDescription)
            }

            guard success == true else {
                return completion(false, PILocalizedString("Payment unsuccessful"))
            }

            completion(true, nil)
        }
    }
}

extension PaymentDetailsInteractor {
    private var infoMessage: String? {
        guard scope == .ciol else { return nil }
        guard ciolParams?.canChangeCard == false else { return nil }

        return PILocalizedString("checkInMustUseStoredCardMessage")
    }

    private var cardViewModel: CardViewModel {
        CardViewModel(
            sectionTitle: PILocalizedString("Payment method"),
            canChangeCard: ciolParams?.canChangeCard ?? false,
            cardTitle: paymentCard.cardNameDescription,
            cardType: paymentCard.cardType.cardCode,
            cardIconUrl: paymentCard.cardType.imageURL,
            cardNumberDescription: paymentCard.cardNumberSummary,
            cvvModel: cvvModel,
            showCardAuthenticationMessage: false
        )
    }

    private var cvvModel: CVVModel? {
        guard paymentCard.isBusiness == false else { return nil }

        let cardIsAmex = paymentCard.cardType.cardCode == SimpleNetwork.Constants.PaymentCardCodes.amex
        let cvvLength = cardIsAmex ? Constants.PaymentCardCVVLength.amex : Constants.PaymentCardCVVLength.common
        let cvvHelperDescription = cardIsAmex ? PILocalizedString(
            "reviewCV2HelpAmex",
            comment: "Review and Book: cv2 code location explanation for Amex cards"
        ) : PILocalizedString(
            "reviewCV2HelpCommon",
            comment: "Review and Book: cv2 code location explanation for common cards"
        )

        return CVVModel(
            cvvLength: cvvLength,
            cvvInputHelperDescription: cvvHelperDescription
        )
    }

    private var confirmationViewModel: ConfirmViewModel {
        ConfirmViewModel(
            termsMessage: termsMessage,
            bookingManagementTermsMessage: bookingManagementTermsMessage,
            totalLabel: PILocalizedString("Total to pay"),
            total: totalCost.localizedValue,
            ctaTitle: PILocalizedString("Pay and check-in"),
            ctaIcon: #imageLiteral(resourceName: "padlock")
        )
    }

    private var subTextParagraphStyle: NSParagraphStyle {
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = .center
        paragraphStyle.lineSpacing = 6

        return paragraphStyle
    }

    private var termsMessage: NSAttributedString {
        let mutableString = NSMutableAttributedString(
            string: PILocalizedString("checkInOnlineConfirmTermsDescription"),
            attributes: [
                NSMutableAttributedString.Key.paragraphStyle: subTextParagraphStyle,
                NSMutableAttributedString.Key.foregroundColor: UIColor.TintD1,
                NSMutableAttributedString.Key.font: UIFont.BodySmall()
            ]
        )

        guard let range = mutableString.string.ranges(of: PILocalizedString("checkInOnlineConfirmTermsHighlight")).first
            else { return NSAttributedString(attributedString: mutableString) }
        mutableString.addAttributes(
            [
                NSMutableAttributedString.Key.foregroundColor: UIColor.BasePurple,
                NSMutableAttributedString.Key.font: UIFont.BodySmall()
            ],
            range: range
        )

        return NSAttributedString(attributedString: mutableString)
    }

    private var bookingManagementTermsMessage: NSAttributedString? {
        NSAttributedString(
            string: cancelableText ?? PILocalizedString("checkInOnlineConfirmDescription", comment: ""),
            attributes: [
                NSMutableAttributedString.Key.paragraphStyle: subTextParagraphStyle,
                NSMutableAttributedString.Key.foregroundColor: UIColor.TintD1,
                NSMutableAttributedString.Key.font: UIFont.BodySmall()
            ]
        )
    }
}
