//
//  ReviewAndBookErrors.swift
//  PremierInn
//
//  Created by Marcello Mascia on 11/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

enum ReviewAndBookMakeBookingError: Error {
    case checkBasketGenericError
    case storedBookingConfirmationResultMissing
    case confirmationNotAvailable
    case sessionTimeout
    case cnpPasswordIncorrect
    case maintenanceMode
    case errorUI(ErrorUI)
    case unknownError(Error)
}

extension ReviewAndBookMakeBookingError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .unknownError(let error):
            return error.localizedDescription
        case .cnpPasswordIncorrect:
            guard let accessLevel = UserSessionManager.sharedInstance.currentUser?.accessLevel
                else { return PILocalizedString("CNP_VALIDATION_FAILED_AUTHENTICATION_UNAVAILABLE_DESCRIPTION") }
            return accessLevel == .superUser ? PILocalizedString("reviewAndBookIncorrectCNPPasswordBBManager") :
                PILocalizedString("reviewAndBookIncorrectCNPPasswordBBNonManager")
        default:
            return String(describing: self)
        }
    }
}

enum ReviewAndBookCCCPaymentError: Error {
    case missingResponse
    case sessionTimeout
    case cnpPasswordIncorrect
    case maintenanceMode
    case errorUI(ErrorUI)
    case unknownError(Error)
    case stopCCCPPaymentProccess
    case genericError
}

enum UpdateAvailabilityError: Error {
    case missingHotel
    case noMoreAvailability
    case offerNewRate(Rate)
    case maintenanceMode
    case unknownError(Error)
}

extension UpdateAvailabilityError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .unknownError(let error):
            return error.localizedDescription
        default:
            return String(describing: self)
        }
    }
}

enum HoldError: Error {
    case maintenanceMode
    case unknownError(Error)
}

extension HoldError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .unknownError(let error):
            return error.localizedDescription
        default:
            return String(describing: self)
        }
    }
}

private extension String {
    static let timeout = "SESSION_NO_LONGER_VALID_TIMEOUT"
    static let cnpIncorrectPassword = "CNP_VALIDATION_FAILED_INCORRECT_PASSWORD"
}

extension Error {
    var serverErrorForMakeBooking: ReviewAndBookMakeBookingError {
        // Special cases
        if let specialCase = specialCase {
            return specialCase
        }

        // BART maintenance mode
        if BARTDowntimeHandler.canHandle(error: self) {
            return .maintenanceMode
        }

        // Search for server error messages in AEM dictionary
        if let errorUI = NetworkErrorManager.errorUI(forError: self) {
            return .errorUI(errorUI)
        }

        // Give up
        return .unknownError(self)
    }

    var serverErrorForCCCPayment: ReviewAndBookCCCPaymentError {
        // BART maintenance mode
        if BARTDowntimeHandler.canHandle(error: self) {
            return .maintenanceMode
        }

        // Search for server error messages in AEM dictionary
        if let errorUI = NetworkErrorManager.errorUI(forError: self) {
            return .errorUI(errorUI)
        }

        // Give up
        return .unknownError(self)
    }

    var serverErrorForUpdateAvailability: UpdateAvailabilityError {
        if BARTDowntimeHandler.canHandle(error: self) {
            return .maintenanceMode
        }

        return .unknownError(self)
    }

    var serverErrorForHold: HoldError {
        if BARTDowntimeHandler.canHandle(error: self) {
            return .maintenanceMode
        }

        return .unknownError(self)
    }

    private var specialCase: ReviewAndBookMakeBookingError? {
        switch self as? RequestsManagerError {
        case .serverError(let dict)?:

            guard let messages = dict?["details"] as? [String] else { return nil }

            for message in messages {
                if message.uppercased().contains(String.timeout) {
                    return .sessionTimeout
                }
                if message.uppercased().contains(String.cnpIncorrectPassword) {
                    return .cnpPasswordIncorrect
                }
            }

        default:
            break
        }

        return nil
    }
}
