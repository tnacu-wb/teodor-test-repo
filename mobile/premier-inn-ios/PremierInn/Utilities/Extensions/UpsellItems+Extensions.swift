//
//  UpsellItems+Extensions.swift
//  PremierInn
//
//  Created by Nick Jones on 11/04/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork

extension UpsellItem {
    var guestCount: Int {
        (adults ?? 0) + (children ?? 0)
    }
}

extension Sequence where Iterator.Element == UpsellItem {
    func squashedById() -> [UpsellItem] {
        var newSquashedArray: [UpsellItem] = []

        for upsellItem in self {
            guard let roomId = upsellItem.roomId else { continue }
            if newSquashedArray.contains(where: { $0.roomId == roomId }) == false {
                newSquashedArray.append(upsellItem)
            }
        }

        return newSquashedArray
    }
}
