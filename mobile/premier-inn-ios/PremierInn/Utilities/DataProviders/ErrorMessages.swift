//
//  ErrorMessages.swift
//  PremierInn
//
//  Created by Freddie Parks on 06/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

enum ErrorMessages {
    enum CIOL {
        static let noReservation = PILocalizedString("Insufficient reservation data")
        static let clearCIOLSession = PILocalizedString("Something went wrong with the checkin process")
        static let noSessionId =
            PILocalizedString("checkInStartFailedRetryAfterDelayError")
        static let noBooker = PILocalizedString("No booker")
        static let missingRoomsAndNextDestinations = PILocalizedString("Missing rooms and next destinations")
        static let addGuestDetailsFailed =
            PILocalizedString("Something went wrong. Please check your guest details and try again.")
        static let addUpsellsFailed =
            PILocalizedString("Something went wrong. Please check your room details and try again.")
        static let userLoginRequired =
            PILocalizedString("You need to login via the account section to check-in as the booker.")
    }

    struct Booking {
        func errorMessage(for code: Int) -> String {
            switch code {
            case 1:
                return PILocalizedString("")
            default:
                return PILocalizedString("Something went wrong")
            }
        }
    }
}

enum MSMappedError: String {
    case paymentContactBankError = "PAYMENT_CONTACT_BANK_EXCEPTION_VALUE"
    case paymentIncorrectCardError = "PAYMENT_INCORRECT_CARD_EXCEPTION_VALUE"
    case paymentTryAgainError = "PAYMENT_TRY_AGAIN_EXCEPTION_VALUE"

    var errorMessage: String {
        switch self {
        case .paymentContactBankError:
            return PILocalizedString("paymentDeclinedByBankError")
        case .paymentIncorrectCardError:
            return PILocalizedString("paymentIncorrectCardDetailsError")
        case .paymentTryAgainError:
            return PILocalizedString("paymentDeclinedError")
        }
    }
}
