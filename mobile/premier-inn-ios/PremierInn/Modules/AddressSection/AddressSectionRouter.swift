//
//  AddressSectionRouter.swift
//  PremierInn
//
//  Created by Nick Jones on 20/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

enum AddressSectionDataProvider {
    static func address(for values: PIDictionary) -> Address? {
        try? Address(
            dictionary: AddressSectionDataProvider.cardAddressDictionary(values: values)
        )
    }

    private static func cardAddressDictionary(values: PIDictionary) -> PIDictionary {
        var dictionary = PIDictionary()

        if let country = values[CountryActionableRow.country.rawValue] as? Country {
            dictionary["countryCode"] = country.code
        }
        dictionary["companyName"] = values[BillingAddressRow.companyName.rawValue]
        dictionary["postcode"] = values[CountryActionableRow.postCode.rawValue]
        dictionary["line1"] = values[CountryActionableRow.addressLine1.rawValue]
        dictionary["line2"] = values[CountryActionableRow.addressLine2.rawValue]
        dictionary["line3"] = values[CountryActionableRow.addressLine3.rawValue]
        dictionary["type"] = values[GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix]

        return dictionary
    }
}

struct AddressSectionRequirements {
    let address: Address?
    let storedAddress: Address?
    let shouldShowAddressSwitch: Bool
    let useStoredAddressSwitchDescription: String?
    let addressSwitchInitialState: Bool
    let shouldShowAddressForm: Bool
    let shouldShowHeader: Bool
    let shouldShowFooter: Bool
    let shouldShowAddressSummary: Bool
}

enum AddressSectionRouter {
    static func buildSection(with requirements: AddressSectionRequirements) -> AddressSectionView {
        let view = AddressSectionView(with: requirements)
        view.presenter = {
            let presenter = AddressSectionPresenter()
            presenter.view = view

            return presenter
        }()

        return view
    }
}
