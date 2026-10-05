//
//  Microservices+User.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

extension Microservices {
    static func registerParameters(registerParameters: RegisterParameters) throws -> PIDictionary? {
        var dict = PIDictionary()
        dict["contactDetail"] = try getContactDetailDictionary(user: registerParameters.user)
        dict["password"] = registerParameters.password
        dict["acceptFutureMailing"] = registerParameters.marketingOptIn

        return dict
    }

    static func userDetailsParams(with user: User) throws -> PIDictionary {
        var dict = PIDictionary()
        dict["contactDetail"] = try getContactDetailDictionary(user: user)
        dict["bookingPreference"] = try getTemporaryNonPartialBookingPreferencesDictionary(for: user)

        return dict
    }

    static func savePaymentCardParams(with user: User) throws -> PIDictionary {
        var dict = PIDictionary()
        dict["contactDetail"] = try getContactDetailDictionary(user: user)
        dict["paymentPreference"] = getPaymentPreferenceDictionary(
            user: user,
            updateCardNumber: true,
            isBusiness: user.company != nil
        )
        dict["bookingPreference"] = try getTemporaryNonPartialBookingPreferencesDictionary(for: user)

        if let companyId = user.companyId {
            dict["companyId"] = companyId
            dict["business"] = getBusinessDictionary(user: user)
        }

        return dict
    }

    static func changePasswordParams(with user: User, currentPassword: String, newPassword: String ) throws -> PIDictionary {
        var dict = PIDictionary()
        dict["password"] = currentPassword
        dict["newPassword"] = newPassword
        dict["contactDetail"] = try getContactDetailDictionary(user: user)
        dict["paymentPreference"] = getPaymentPreferenceDictionary(
            user: user,
            updateCardNumber: true,
            isBusiness: user.company != nil
        )
        dict["bookingPreference"] = try getTemporaryNonPartialBookingPreferencesDictionary(for: user)

        if let companyId = user.companyId {
            dict["companyId"] = companyId
            dict["business"] = getBusinessDictionary(user: user)
        }

        return dict
    }

    static func deleteCardParams(with user: User) throws -> PIDictionary {
        var dict = PIDictionary()
        dict["contactDetail"] = try getContactDetailDictionary(user: user)
        dict["bookingPreference"] = try getTemporaryNonPartialBookingPreferencesDictionary(for: user)
        dict["paymentPreference"] = ["paymentCard": [:]]

        if let companyId = user.companyId {
            dict["companyId"] = companyId
            dict["business"] = getBusinessDictionary(user: user)
        }

        return dict
    }

    static func additionalGuestsParams(with user: User) throws -> PIDictionary {
        var dict = PIDictionary()
        dict["contactDetail"] = try getContactDetailDictionary(user: user)
        dict["bookingPreference"] = try getTemporaryNonPartialBookingPreferencesDictionary(for: user)

        if let additionalGuests = user.additionalGuests, !additionalGuests.isEmpty {
            dict["additionalGuests"] = additionalGuests.map { $0.dictionary }
        }

        if let companyId = user.companyId {
            dict["companyId"] = companyId
            dict["business"] = getBusinessDictionary(user: user)
        }

        return dict
    }

    static func getFoodPreferencesDictionary(with user: User) throws -> PIDictionary {
        var dict = PIDictionary()
        dict["contactDetail"] = try getContactDetailDictionary(user: user)
        dict["bookingPreference"] = try getTemporaryNonPartialBookingPreferencesDictionary(for: user)

        if let companyId = user.companyId {
            dict["companyId"] = companyId
            dict["business"] = getBusinessDictionary(user: user)
        }

        return dict
    }

    static func getRoomPreferencesDictionary(with user: User) throws -> PIDictionary {
        var dict = PIDictionary()
        dict["contactDetail"] = try getContactDetailDictionary(user: user)
        dict["bookingPreference"] = try getTemporaryNonPartialBookingPreferencesDictionary(for: user)

        if let companyId = user.companyId {
            dict["companyId"] = companyId
            dict["business"] = getBusinessDictionary(user: user)
        }

        return dict
    }

    static func getContactDetailDictionary(user: User) throws -> PIDictionary {
        var dict = PIDictionary()

        let title = try? Title(title: user.title ?? "").rawValue

        dict["title"] = title ?? user.title
        dict["firstName"] = user.firstName
        dict["lastName"] = user.lastName
        dict["mobile"] = user.contactNumber
        dict["email"] = user.emailAddress
        dict["carRegistration"] = user.carRegistration
        dict["nationality"] = user.country?.isoCode

        if let passportNumber = user.passport?.number,
           let countryOfIssue = user.passport?.countryOfIssue {
            dict["passport"] = [
                "number": passportNumber,
                "countryOfIssue": countryOfIssue
            ]
        }

        dict["address"] = try {
            guard let address = user.address else { throw UserError.missingAddress }

            return getAddressDictionary(address: address, shouldCapitalisePostCode: true)
        }()

        return dict
    }

    static func getTemporaryNonPartialBookingPreferencesDictionary(for user: User) throws -> PIDictionary {
        let requirements = user.bookingPreference?.roomRequirements ?? RoomRequirements.standard

        var dict = PIDictionary()
        let roomRequirements: [String: Any] = [
            "hotelBrand": "PI",
            "adults": requirements.adults,
            "children": requirements.children,
            "cotRequired": requirements.cotRequired,
            "smoking": false,
            "type": requirements.type?.rawValue ?? RoomType.double.rawValue
        ]
        dict["roomRequirements"] = roomRequirements
        dict["foodPreference"] = user.bookingPreference?.foodPreference?.rawValue

        return dict
    }
}

private extension Microservices {
    static func getPaymentPreferenceDictionary(
        user: User,
        updateCardNumber: Bool,
        isBusiness: Bool = false
    ) -> PIDictionary {
        var dict = PIDictionary()
        dict["electronicInvoiceRequired"] = user.paymentPreference?.electronicInvoiceRequired
        dict["prepaymentRequired"] = user.paymentPreference?.prepaymentRequired
        if let card = user.paymentPreference?.card {
            dict["paymentCard"] = {
                var dict = PIDictionary()
                dict["cardHolderName"] = card.cardholderName
                if updateCardNumber {
                    dict["cardNumber"] = card.cardNumber
                }
                if let cardLabel = card.cardLabel {
                    dict["cardLabel"] = cardLabel
                }
                // Fallback to "VI" to avoid save card errors on empty string
                dict["cardType"] = card.cardType.cardCode.isEmpty ? "VI" : card.cardType.cardCode
                dict["expiryDate"] = isBusiness ? card.expiryDate?.creditCardDateFormatBusinessBooker : card.expiryDate?
                    .creditCardDateFormat
                dict["issueNumber"] = card.issueNumber
                dict["startDate"] = card.startDate?.creditCardDateFormat
                dict["useExistingCard"] = true
                // When we can remove 3ds code, we can just remove the card.address bit and set all to user.address.
                if let address = isBusiness ? user.address : card.address ?? user.address {
                    dict["billingAddress"] = getAddressDictionary(address: address, shouldCapitalisePostCode: true)
                }

                return dict
            }()
        } else if isBusiness {
            dict["paymentCard"] = [String: String]()
            return dict
        }
        return dict
    }

    static func getBusinessDictionary(user: User) -> PIDictionary {
        var dict = PIDictionary()

        dict["centralCard"] = user.business?.centralCard

        return dict
    }
}
