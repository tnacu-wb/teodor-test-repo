//
//  DatatransPaymentFlowError.swift
//  PremierInn
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

enum DatatransPaymentFlowError: Error {
    case invalidRequest
    case basketNotFound
    case bookingAlreadyPaid
    case bookingAlreadyConfirmed
    case paymentMethodNotAvailable
    case gatewayError
    case serviceUnavailable
    case timeout
    case noConnectivity
    case unknown

    var localizedMessage: String {
        switch self {
        case .invalidRequest:
            PILocalizedString("datatransPaymentErrorInvalidRequest")
        case .basketNotFound:
            PILocalizedString("datatransPaymentErrorBasketNotFound")
        case .bookingAlreadyPaid:
            PILocalizedString("datatransPaymentErrorAlreadyPaid")
        case .bookingAlreadyConfirmed:
            PILocalizedString("datatransPaymentErrorAlreadyConfirmed")
        case .paymentMethodNotAvailable:
            PILocalizedString("datatransPaymentErrorMethodNotAvailable")
        case .gatewayError, .serviceUnavailable:
            PILocalizedString("datatransPaymentErrorTemporary")
        case .timeout:
            PILocalizedString("datatransPaymentErrorTimeout")
        case .noConnectivity:
            PILocalizedString("datatransPaymentErrorNoConnectivity")
        case .unknown:
            PILocalizedString("datatransPaymentErrorUnknown")
        }
    }

    static func from(error: Error) -> DatatransPaymentFlowError {
        if let urlError = error as? URLError {
            switch urlError.code {
            case .timedOut:
                return .timeout
            case .notConnectedToInternet, .networkConnectionLost:
                return .noConnectivity
            default:
                break
            }
        }

        if let responseData = (error as NSError).userInfo["responseData"] as? Data,
           let errorResponse = try? JSONDecoder().decode(
               DatatransPaymentErrorResponse.self,
               from: responseData
           ) {
            switch errorResponse.error.typed {
            case .invalidRequest:
                return .invalidRequest
            case .basketNotFound:
                return .basketNotFound
            case .bookingAlreadyPaid:
                return .bookingAlreadyPaid
            case .bookingAlreadyConfirmed:
                return .bookingAlreadyConfirmed
            case .paymentMethodNotAvailable:
                return .paymentMethodNotAvailable
            case .gatewayError:
                return .gatewayError
            case .serviceUnavailable:
                return .serviceUnavailable
            case .none:
                return .unknown
            }
        }

        return .unknown
    }
}
