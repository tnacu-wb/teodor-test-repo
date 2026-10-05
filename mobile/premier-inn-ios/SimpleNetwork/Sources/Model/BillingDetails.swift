//
//  BillingDetails.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 12/04/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation

public typealias FullGuestName = (title: String, firstName: String, lastName: String)

public struct BillingDetails {
    let email: String?
    let telephone: String?
    let address: Address?
    let fullName: FullGuestName

    public init (email: String?, telephone: String?, address: Address, fullName: FullGuestName) {
        self.email = email
        self.telephone = telephone
        self.address = address
        self.fullName = fullName
    }

    var toDictionary: PIDictionary {
        let city = (address?.line2?.isEmpty == false ? address?.line2 : address?.line1) ?? ""
        let country = address?.country?.isoCode ?? ""

        let addressDictionary: PIDictionary = [
            "line1": address?.line1 ?? "",
            "line2": address?.line2 ?? "",
            "line3": address?.line3 ?? "",
            "line4": address?.line4 ?? "",
            "postalCode": address?.postcode ?? "",
            "state": address?.line4 ?? "",
            "city": city,
            "countryCode": country
        ]

        var dic: PIDictionary = [
            "title": fullName.title,
            "firstName": fullName.firstName,
            "lastName": fullName.lastName,
            "address": addressDictionary
        ]

        if let email = email {
            dic["email"] = email
        }

        dic["telephone"] = telephone ?? ""
        dic["country"] = country

        return dic
    }

    var toGraphQLDictionary: PIDictionary {
        let country = address?.country?.isoCode ?? ""

        let addressDictionary: PIDictionary = [
            "addressLine1": address?.line1 ?? "",
            "addressLine2": address?.line2 ?? "",
            "addressLine3": address?.line3 ?? "",
            "addressLine4": address?.line4 ?? "",
            "postalCode": address?.postcode ?? "",
            "country": country
        ]

        var dic: PIDictionary = [
            "title": fullName.title,
            "firstName": fullName.firstName,
            "lastName": fullName.lastName,
            "address": addressDictionary
        ]

        if let email = email {
            dic["email"] = email
        }

        dic["telephone"] = telephone ?? ""

        return dic
    }
}
