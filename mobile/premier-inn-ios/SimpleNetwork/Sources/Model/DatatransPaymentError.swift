//
//  DatatransPaymentError.swift
//  SimpleNetwork
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

public enum DatatransPaymentErrorCode: String, Decodable {
    case invalidRequest = "INVALID_REQUEST"
    case basketNotFound = "BASKET_NOT_FOUND"
    case bookingAlreadyPaid = "BOOKING_ALREADY_PAID"
    case bookingAlreadyConfirmed = "BOOKING_ALREADY_CONFIRMED"
    case paymentMethodNotAvailable = "PAYMENT_METHOD_NOT_AVAILABLE"
    case gatewayError = "GATEWAY_ERROR"
    case serviceUnavailable = "SERVICE_UNAVAILABLE"
}

public struct DatatransPaymentErrorResponse: Decodable {
    public let error: DatatransPaymentErrorDetail
}

public struct DatatransPaymentErrorDetail: Decodable {
    public let code: String
    public let message: String

    public var typed: DatatransPaymentErrorCode? {
        DatatransPaymentErrorCode(rawValue: code)
    }
}
