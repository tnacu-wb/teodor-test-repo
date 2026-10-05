//
//  Guest.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

struct Guest {
    private enum Constants {
        static let whitespace = " "
    }

    var name: String?
    var firstName: String?
    var lastName: String?
    var composedAddress: String
    var nationality: String
    var dateOfBirth: String
    var type: GuestType
    var status: GuestStatus = .empty
    var passport: String?
    var isValid = false
    var profileId: String?
    private let address: Address?
    private let country: Country?

    func value(for field: GuestField) -> String? {
        switch field {
        case .name: return name
        case .address: return composedAddress
        case .dateOfBirth: return dateOfBirth
        case .nationality: return nationality
        case .passport: return passport
        case .add: return status.displayValue
        }
    }

    static func displayAddress(address: Address?) -> String {
        guard let address else { return ""}
        return [
            address.line1,
            address.line2,
            address.line3,
            address.postcode,
            address.cityName ?? address.line4,
            address.country?.name
        ]
            .compactMap { $0 }
            .joined(separator: ", ")
    }

    private static func createFormattedName(firstName: String?, lastName: String?) -> String {
        [firstName, lastName]
            .compactMap { $0 }
            .joined(separator: Constants.whitespace)
    }

    init(with user: User) {
        self.country = user.country
        self.profileId = user.profileId
        self.firstName = user.firstName
        self.lastName = user.lastName
        self.name = Self.createFormattedName(firstName: firstName, lastName: lastName)
        self.composedAddress = Self.displayAddress(address: user.address)
        self.nationality = user.country?.nationality ?? user.country?.name ?? ""
        self.dateOfBirth = user.dob ?? ""
        self.passport = user.passport?.number
        self.type = user.isAccompanyingGuest == true ? .additional : .lead
        self.address = user.address
        self.status = setStatus(for: user)
    }

    var displayedFields: [GuestField] {
        var fields: [GuestField] = [.name, .address, .dateOfBirth, .nationality]
        if let editedPassport = passport, editedPassport.isNotEmpty {
            fields.append(.passport)
        }
        fields.append(.add)
        if type == .additional {
            fields = fields.filter { $0 != .address }
        }
        return fields
    }

    private func setStatus(for user: User) -> GuestStatus {
        guard let countryItem = CountryItem(country: user.country) else {
            return .empty
        }

        if countryItem.isGerman {
            return user.dob != nil && user.country != nil ? .edited : .empty
        } else {
            return user.dob != nil && user.passport != nil && user.country != nil ? .edited : .empty
        }
    }
}

extension Guest {
    var isNotGerman: Bool {
        country != .germany
    }
    var countryName: String {
        country?.name ?? ""
    }

    var city: String {
        address?.cityName ?? address?.line4 ?? ""
    }

    var postCode: String {
        address?.postcode ?? ""
    }
}

enum GuestStatus {
    case empty, edited, error
    var displayValue: String {
        switch self {
        case .empty, .error: return "guestDetailsFieldAdd"
        case .edited: return "guestDetailsFieldEdit"
        }
    }

    var displayColor: UIColor {
        switch self {
        case .edited, .empty: return .Tint2
        case .error: return .Tint8
        }
    }
}

enum GuestField: String, CaseIterable {
    case name = "guestDetailsFieldName"
    case address = "guestDetailsFieldAddress"
    case nationality = "guestDetailsFieldNationality"
    case dateOfBirth = "guestDetailsFieldBirth"
    case passport = "ciolPassportNumber"
    case add
}

enum GuestType: String {
    case lead = "guestDetailsLead"
    case additional = "guestDetailsAdditional"
}
