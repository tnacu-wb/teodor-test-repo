//
//  BookingDetails+CityTax.swift
//  PremierInn
//
//  Created by Freddie Parks on 18/01/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import SimpleNetwork

extension BookingDetails {
    var cityTaxTotal: Cost? {
        guard let rooms = roomLettings else { return nil }
        guard let currency = rooms.first?.options?.first?.cityTax?.currencyCode else { return nil }

        let cityTaxCostsPerRoom = rooms.compactMap { $0.options?.first?.cityTax?.amount.doubleValue }
        let total = cityTaxCostsPerRoom.reduce(0.0, +)

        return Cost(amount: total, currencyCode: currency)
    }

    var totalCostWithoutCityTax: Cost? {
        guard let total = totalCostWithCityTaxAndExtras else { return nil }
        guard let cityTaxCost = cityTaxTotal else { return total }

        return total - cityTaxCost
    }

    var totalCostWithCityTaxAndExtras: Cost? {
        guard let fullRoomcost = roomAndMealCost else { return nil }
        let extrasCost = extrasTotalCost ?? Cost(amount: 0, currencyCode: fullRoomcost.currencyCode)

        return Cost(
            amount: fullRoomcost.amount.doubleValue + extrasCost.amount.doubleValue,
            currencyCode: fullRoomcost.currencyCode
        )
    }

    var cityTaxRequired: Bool {
        guard let purpose = purpose else { return true }
        return purpose == .leisure ? hotel?.cityTaxForLeisure ?? true : hotel?.cityTaxForBusiness ?? true
    }

    var roomCost: Cost? {
        if cityTaxRequired { return roomLettings?.totalCost }

        guard let fullRoomcost = roomLettings?.totalCost else { return nil }
        let cityTaxCost = cityTaxTotal ?? Cost(amount: 0, currencyCode: fullRoomcost.currencyCode)

        return Cost(
            amount: fullRoomcost.amount.doubleValue - cityTaxCost.amount.doubleValue,
            currencyCode: fullRoomcost.currencyCode
        )
    }
}
