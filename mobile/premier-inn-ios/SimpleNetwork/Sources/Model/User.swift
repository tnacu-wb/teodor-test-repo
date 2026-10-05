//
//  User.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

public enum MealOption: Int, Codable {
    case premierInnBreakfast = 11
    case continentalBreakfast = 12
    case freeChildBreakfast = 15
    case mealDeal = 17
    case hubBreakfast = 18
    case none = 0

    public var id: String {
        switch self {
        case .premierInnBreakfast: "BFADBF"
        case .continentalBreakfast: "BFADCT"
        case .freeChildBreakfast: "BFCHDF"
        case .mealDeal: "MDP"
        case .hubBreakfast: "BFADBF"
        case .none: ""
        }
    }
}

enum UserError: LocalizedError {
    case missingTitle
    case missingFirstName
    case missingLastName
    case missingUserDictionary
    case missingAccount
    case missingContactDetails
    case missingUserIdentifier
    case missingAddress
    case missingRoomRequirements

    var errorDescription: String? {
        String(describing: self)
    }
}

public enum AccessLevel: String {
    // Why'd two of the access levels have to be reserved namespaces? 😅
    case stayer = "STAYER"
    case selfBooker = "SELF"
    case booker = "BOOKER"
    case superUser = "SUPER"
}

public class User {
    public var title: String?
    public var firstName: String?
    public var lastName: String?
    public var address: Address?
    public var emailAddress: String?
    public var contactNumber: String?
    public var country: Country?
    public var passport: Passport?
    public var dob: String?
    public var isAccompanyingGuest: Bool?
    public var profileId: String?
    public var carRegistration: String?
    public var isBusiness: Bool
    public var bookingPreference: BookingPreference?
    public var paymentPreference: PaymentPreference?
    public var additionalGuests: [AdditionalGuest]?
    public var guestHistoryNumber: String?
    public var customerAccountId: String?
    public var company: Company? {
        didSet {
            BookingDetails.sharedInstance.resetPaymentMethod()
            BookingDetails.sharedInstance.booker = self
        }
    }
    public var business: BBUserConfig?
    public var companyId: String?
    public var accessLevel: AccessLevel?

    public var marketingPreferences: MarketingPreferences?
    var guestHistoryCreation: String?
    var totalStays: Int?
    public var contactChannelId: String? { marketingPreferences?.contactChannelId }

    public var suppressMarketingBox: Bool {
        self.marketingPreferences?.permissions?.contains(where: { $0.suppressMarketingCheckbox == true }) ?? false
    }

    var outputDOB: String? {
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "dd/MM/yyyy"
        guard let date = outputFormatter.date(from: dob ?? "") else { return nil }
        outputFormatter.dateFormat = "yyyy-MM-dd"
        return outputFormatter.string(from: date)
    }

    public init(
        title: String?,
        firstName: String?,
        lastName: String?,
        email: String? = nil,
        telephone: String? = nil,
        regCardUser: Bool = false
    ) throws {
		self.title = title
        self.firstName = firstName
        self.lastName = lastName
        self.emailAddress = email
        self.contactNumber = telephone
        self.country = nil
        self.passport = nil
        self.carRegistration = nil
        self.guestHistoryNumber = nil
        self.isBusiness = false
        self.bookingPreference = BookingPreference.empty
        self.companyId = nil
        self.accessLevel = nil
    }

    private init() {
        self.title = ""
        self.firstName = ""
        self.lastName = ""
        self.isBusiness = false
        self.bookingPreference = BookingPreference.empty
    }

    public static func emptyGuest() -> User {
        User()
    }

    public init(dictionary: PIDictionary?, sessionId: String?) throws {
        guard let dictionary else { throw UserError.missingUserDictionary }
        guard let contactDictionary = dictionary["contactDetail"] as? PIDictionary
            else { throw UserError.missingContactDetails }

        self.title = contactDictionary["title"] as? String
        self.firstName = contactDictionary["firstName"] as? String
        self.lastName = contactDictionary["lastName"] as? String

        self.emailAddress = contactDictionary.value(forKeys: ["emailAddress", "email"])
        self.contactNumber = contactDictionary.value(forKeys: ["mobileNumber", "mobile"])
        if let telephone = contactDictionary["telephone"] as? String, self.contactNumber?.isEmpty ?? true {
            self.contactNumber = telephone
        }
        self.country = User.getCountry(contactDictionary: contactDictionary)
        self.passport = User.getPassport(contactDictionary: contactDictionary)
        self.address = try? Address(dictionary: contactDictionary["address"] as? PIDictionary)
        self.carRegistration = contactDictionary["carRegistration"] as? String

        self.paymentPreference = PaymentPreference(dict: dictionary["paymentPreference"] as? PIDictionary)
        self.bookingPreference = User.getBookingPreference(dictionary: dictionary)

        if let additionalGuestsDic = dictionary["additionalGuests"] as? [PIDictionary] {
            self.additionalGuests = additionalGuestsDic.compactMap { try? AdditionalGuest(dictionary: $0) }
        } else {
            self.additionalGuests = nil
        }

        self.customerAccountId = dictionary["customerAccountId"] as? String
        self
            .guestHistoryNumber = dictionary["guestHistoryNumber"] as? String ??
            contactDictionary["guestHistoryNumber"] as? String
        self.isBusiness = (dictionary["business"] is PIDictionary) ? true : dictionary["businessUse"] as? Bool ?? false
        self.companyId = dictionary["companyId"] as? String
        self.business = User.getBusinessConfig(dictionary: dictionary)
        self.accessLevel = User.getAccessLevel(dictionary: dictionary)
        self
            .guestHistoryCreation = dictionary["guestHistoryCreation"] as? String ??
            contactDictionary["guestHistoryCreation"] as? String
        self.totalStays = dictionary["totalStays"] as? Int ?? contactDictionary["totalStays"] as? Int
    }

    private static func getCountry(contactDictionary: PIDictionary) -> Country? {
        guard let code = contactDictionary["nationality"] as? String,
              code.isEmpty == false else { return nil }

        return Country.countriesList.first(where: { $0.code == code })
    }

    private static func getPassport(contactDictionary: PIDictionary) -> Passport? {
        guard let dict = contactDictionary["passport"] as? PIDictionary,
              let number = dict["number"] as? String,
              !number.isEmpty,
              let countryOfIssue = dict["countryOfIssue"] as? String,
              !countryOfIssue.isEmpty else { return nil }

        return Passport(number: number, countryOfIssue: countryOfIssue)
    }

    private static func getAccessLevel(dictionary: PIDictionary) -> AccessLevel? {
        guard let business = (dictionary["business"] as? PIDictionary),
              let accessLevel = business["accessLevel"] as? String else { return nil }
        return AccessLevel(rawValue: accessLevel)
    }

    private static func getBookingPreference(dictionary: PIDictionary) -> BookingPreference {
        guard let bookingPrefenceDic = dictionary["bookingPreference"] as? PIDictionary,
              let data = try? JSONSerialization.data(withJSONObject: bookingPrefenceDic, options: .prettyPrinted)
        else { return BookingPreference.empty }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(BookingPreference.self, from: data)
        } catch {
            print(error)
            return BookingPreference.empty
        }
    }

    private static func getBusinessConfig(dictionary: PIDictionary) -> BBUserConfig? {
        guard let businessDictionary = dictionary["business"] as? PIDictionary else { return nil }
        guard let data = try? JSONSerialization.data(withJSONObject: businessDictionary, options: .prettyPrinted)
            else { return nil }

        return try? JSONDecoder().decode(BBUserConfig.self, from: data)
    }

    public func optedIn(for brandCode: MarketingBrandCode) -> Bool? {
        guard let brandPermission = marketingPreferences?.permissions?.first(where: { $0.brandCode == brandCode.rawValue })
            else { return nil }

        return brandPermission.optIn
    }
}

extension User: Equatable {
	public static func == (lhs: User, rhs: User) -> Bool {
		lhs.title == rhs.title &&
        lhs.firstName == rhs.firstName &&
        lhs.lastName == rhs.lastName
	}
}

extension User: CustomStringConvertible {
	public var description: String {
		(title ?? "") + " " + (firstName ?? "") + " " + (lastName ?? "")
	}
}

public extension User {
    func copyCompany(from user: User) {
        self.companyId = user.companyId
        self.business = user.business
        self.company = user.company
    }

    var centrallyStoredBusinessCard: PaymentCard? {
        guard company?.allowCentralCreditCard == true else { return nil }
        guard let cardIdString = business?.centralCard else { return nil }

        return company?.paymentDetails?.paymentCards?.first(where: { $0.cardId == cardIdString })
    }
}

extension User {
    var leadGuestDic: PIDictionary? {
        var dic: PIDictionary = [
            "name": description,
            "registered": self == UserSessionManager.sharedInstance.currentUser
        ]

        if let registeredSince = guestHistoryCreation {
            dic["registeredSince"] = registeredSince
        }
        if let previousBookings = totalStays {
            dic["previousBookings"] = previousBookings
        }

        return dic
    }
}
