//
//  Company+Extensions.swift
//  PremierInn
//
//  Created by Nick Jones on 26/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork

extension Company {
    func allowance(for hotel: Hotel?) -> Cost? {
        if hotel?.county?.lowercased() == Constants.DinnerAllowanceLocations.greaterLondonCounty {
            return bookingAllowances?.maxDinnerBudgets?.greaterLondon
        } else if [
            Constants.DinnerAllowanceLocations.irelandCountry,
            Constants.DinnerAllowanceLocations.irelandCountryDE,
            Constants.DinnerAllowanceLocations.germanyCountry,
            Constants.DinnerAllowanceLocations.germanyCountryDE
        ].contains(hotel?.county?.lowercased()) == true {
            // budget for ireland has now been re-purposed for EU (to include Germany) - rename this asap
            return bookingAllowances?.maxDinnerBudgets?.ireland
        }

        return bookingAllowances?.maxDinnerBudgets?.uKWide
    }

    var isUltimateWifiAllowed: Bool {
        bookingAllowances?.upsellItemsAllowed?.first(where: { $0 == String(UpsellItemsCode.ultimateWifi.rawValue) }) != nil
    }

    var isMealDealAllowed: Bool {
        bookingAllowances?.upsellItemsAllowed?.first(where: { $0 == String(UpsellItemsCode.mealDeal.rawValue) }) != nil
    }
}
