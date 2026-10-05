//
//  CCCPPaymentDetails.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 23/04/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation

public enum CCCPBookingJourney: String {
    case AMEND
    case BOOKING
    case CIOL
}

public enum CCCPPaymentType: String {
    case CARD
    case PIBA
    case APPLEPAY = "WALLET_APPLE"
    case PAYPAL
    case RESERVE_WITHOUT_CARD
}

public protocol CCCPPaymentDetails {
    var acceptedCardCodes: [String]? { get }
    var card: Card? { get }
    var billingDetails: BillingDetails { get }
    var journey: CCCPBookingJourney { get }
    var paymentInterval: PaymentIntervalOption { get }
}

public struct CCCPPaymentParams: CCCPPaymentDetails {
    public let acceptedCardCodes: [String]?
    public let card: Card?
    public let billingDetails: BillingDetails
    public let journey: CCCPBookingJourney
    public let paymentInterval: PaymentIntervalOption
    public let payingWithPIBA: Bool
    public let bbQuestionAndAnswers: [OperaBusinessCardQuestionAndAnswer]?
    public let paymentType: CCCPPaymentType?
    public let paypalNonce: String?
    public let paypalDeviceData: String?
    public let usePaypalInitiatePayment: Bool?
    public let donationPackage: String?

    public init(
        acceptedCardCodes: [String]?,
        card: Card? = nil,
        billingDetails: BillingDetails,
        journey: CCCPBookingJourney,
        paymentInterval: PaymentIntervalOption,
        payingWithPIBA: Bool,
        bbQuestionAndAnswers: [OperaBusinessCardQuestionAndAnswer]?,
        paymentType: CCCPPaymentType?,
        paypalNonce: String?,
        paypalDeviceData: String?,
        usePaypalInitiatePayment: Bool?,
        donationPackage: String?
    ) {
        self.acceptedCardCodes = acceptedCardCodes
        self.card = card
        self.billingDetails = billingDetails
        self.journey = journey
        self.paymentInterval = paymentInterval
        self.payingWithPIBA = payingWithPIBA
        self.bbQuestionAndAnswers = bbQuestionAndAnswers
        self.paymentType = paymentType
        self.paypalNonce = paypalNonce
        self.paypalDeviceData = paypalDeviceData
        self.usePaypalInitiatePayment = usePaypalInitiatePayment
        self.donationPackage = donationPackage
    }
}
