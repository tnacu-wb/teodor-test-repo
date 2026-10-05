//
//  BookingDetails+Analytics.swift
//  PremierInn
//
//  Created by Freddie Parks on 17/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import SimpleNetwork

extension BookingDetails {
    var trackingProductString: String {
        if let hotelCode = hotel?.code {
            let event20 = roomLettings?.totalCost?.amount ?? 0
            let event37 = mealTotalCost?.amount ?? 0
            let event31 = "\(criteria.rooms.count)"
            let event36 = extrasTotalCost?.amount ?? 0

            return ";\(hotelCode);\(criteria.nights);\(totalCost.amount);event37=\(event37)|event20=\(event20)|event31=\(event31)|event36=\(event36);"
        }

        return ""
    }
}
