//
//  PaymentResponse.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

public enum PaymentResponseError: LocalizedError {
    case cardError
    case cnpPasswordIncorrect
    case missingSessionIdentifier
    case missingAuthenticationToken
    case missingRedirectURL
    case noAvailability
    case sessionTimeout
    case unexpectedSessionIdentifier
    case missingCheckInComplete

	public var errorDescription: String? { String(describing: self)	}
}

public struct PaymentResponse: Codable {
    public let threeDSecureRequired: Bool
    public let authenticationToken: String?
    public let validationPageHTMLContent: String?
    public let xid: String?
    public let ccToken: String?
    public let pri: String?
    public let ccCompany: String?
    public let fri: String?
    public let cvvToken: String?
    public let veres: String?
    public let sessionId: String
    public let redirectURL: String?
    public let redirectHtml: String?
}

public struct CheckInPaymentResponse {
    public let sessionId: String?
    public let threeDSecureRequired: Bool
    public let redirectURLString: String?
    public let checkInComplete: Bool
    public let pareq: String?

    public var redirectURL: URL? {
        guard let redirectURLString = redirectURLString else { return nil }
        return URL.secureURL(from: redirectURLString)
    }

    init(dictionary: PIDictionary) throws {
        self.sessionId = dictionary["sessionId"] as? String

        guard let checkInComplete = dictionary["checkInComplete"] as? Bool
            else { throw PaymentResponseError.missingCheckInComplete }

        self.checkInComplete = checkInComplete

        let threeDS = dictionary["threeDSecure"] as? PIDictionary

        self.pareq = threeDS?["pareq"] as? String
        self.redirectURLString = threeDS?["redirectURL"] as? String
        self.threeDSecureRequired = self.pareq != nil
    }
}

public struct CCCPPaymentResponse: Codable {
    public let paymentRequiredDetails: CCCPPaymentProviderResponse?
    public let status: PaymentIntervalStatus?

    public init(paymentRequiredDetails: CCCPPaymentProviderResponse?, status: PaymentIntervalStatus?) {
        self.paymentRequiredDetails = paymentRequiredDetails
        self.status = status
    }
}

public enum PaymentIntervalStatus: String, Codable {
    case required = "PAYMENT_REQUIRED"
    case notRequired = "NOT_REQUIRED"
}

public protocol HTMLRepresentable {
    var htmlString: String? { get }
}

public enum CCCPPaymentError: LocalizedError {
    case generic
    case timeout

    public var localizedDescription: String {
        switch self {
        case .generic:
            return NSLocalizedString("Something went wrong. Please check your payment details and try again.", comment: "")
        case .timeout:
            return NSLocalizedString(
                "Sorry, your booking session timed out and we were unable to process your payment.",
                comment: ""
            )
        }
    }
}

public struct CCCPPaymentProviderResponse: Codable, HTMLRepresentable {
    public let sessionId: String?
    public let template: String?
    public let providerUrl: String?
    public let paymentRedirect: String?

    public var htmlString: String? {
        guard let iPageHtml = paymentRedirect else { return nil }
        guard let base64Data = Data(base64Encoded: iPageHtml) else { return nil }

        return String(data: base64Data, encoding: .utf8)
    }
}
