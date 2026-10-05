//
//  ErrorCodes.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 25/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation

// MS error codes
extension Int {
    // /payments -- 3C Payment Micro-Service
    static let threeCMSUnknownErrorCode = 0
    static let threeCMSValidationErrorCode = 1
    static let threeCMSProviderAccountNotFoundErrorCode = 2
    // Problem with template config in MS
    static let threeCMSTemplateErrorCode = 3
    // Parsing 3C response failed
    static let threeCMSProviderResponseParseErrorCode = 4
    static let threeCMSHandlingRequestErrorCode = 5
    // Problem contacting 3C
    static let threeCMSProviderErrorCode = 6
    // Problem with auth to 3C
    static let threeCMSUnauthorisedErrorCode = 7
    // No payment in MS DB
    static let threeCMSPaymentNotFoundErrorCode = 8
    // Refund not working in MS -> 3C
    static let threeCMSUnableToRefundErrorCode = 9
    // BART booking session timed out
    static let threeCMSBookingSessionTimeoutErrorCode = 10

    // /booking -- BART booking Micro-Service
    static let somethingRandomBookingError = 11
}
