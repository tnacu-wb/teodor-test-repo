//
//  EditDetailsModel.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 25.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

enum EditDetailsFlow: Equatable {
    case leadGuestTitleNameInfo(indexPath: IndexPath)
    case secondGuestTitleNameInfo(indexPath: IndexPath)
    case paymentMethod
    case regCard(index: Int)
    case emailAddress
    case phoneNumber
    case address
}

struct EditDetailsModel {
    var flow: EditDetailsFlow
    var title: String?
    var firstName: String?
    var lastName: String?
    var country: CountryItem?
    var passportNumber: String?
    var hotelBrand: HotelBrand?
    var bookingReference: String?
    var emailAddress: String?
    var phoneNumber: String?
    var address: StoredAddressModel?
    var dateOfBirth: Date?
    var isLeadGuest: Bool = false
    var isLastNameDisabled = false
}

extension EditDetailsModel {
    var shouldShowIdentificationDocumentsSection: Bool {
        hotelBrand == .premierInnGermany ? !(country?.isGerman ?? true) : country?.isPassportRequired ?? false
    }

    var isDERegCard: Bool {
        hotelBrand == .premierInnGermany
    }

    var showFindAddressButton: Bool {
        if case .regCard = flow { return false }
        let countryItem = CountryItem(country: address?.country)
        return countryItem?.isGreatBritain ?? false
    }
}
