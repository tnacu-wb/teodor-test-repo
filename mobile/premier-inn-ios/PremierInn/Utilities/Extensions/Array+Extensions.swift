//
//  Array+Extensions.swift
//  PremierInn
//
//  Created by Nick Jones on 22/01/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import SimpleNetwork

extension Array {
    var isNotEmpty: Bool {
        !isEmpty
    }

    subscript(safe index: Int) -> Element? {
        indices.contains(index) ? self[index] : nil
    }
}

/// the self array here is [[RoomLettingOption]]: we have grouped the options by room class
/// so here we need the room class order from any element in each array in order to sort them
extension Array where Element == [RoomLettingOption] {
    func sortRoomOptionsByRoomClassOrderFromAPI(ascending: Bool) -> [[RoomLettingOption]] {
        guard let roomClassOrders = self.first?.compactMap({ $0.roomClassOrder }),
              roomClassOrders.isNotEmpty else {
            return sortRoomOptionsByDefaultRoomClassOrder(ascending: ascending)
        }

        return sorted {
            let lhs = $0.first?.roomClassOrder ?? Int.max
            let rhs = $1.first?.roomClassOrder ?? Int.max
            return ascending ? lhs < rhs : lhs > rhs
        }
    }

    func sortRoomOptionsByDefaultRoomClassOrder(ascending: Bool) -> [[RoomLettingOption]] {
        let defaultRoomClassOrder: [String: Int] = [
            Constants.RoomClass.standardRoom: 1,
            Constants.RoomClass.standardFamilyRoom: 2,
            Constants.RoomClass.standardRoomWithAView: 3,
            Constants.RoomClass.standardRoomWithSeaView: 4,
            Constants.RoomClass.standardRoomWithCityView: 5,
            Constants.RoomClass.premierPlusRoom: 6,
            Constants.RoomClass.premierPlusSuite: 7,
            Constants.RoomClass.premierPlusRoomWithAView: 8,
            Constants.RoomClass.premierPlusRoomWithSeaView: 9,
            Constants.RoomClass.premierPlusRoomWithCityView: 10,
            Constants.RoomClass.standardExtraRoom: 11,
            Constants.RoomClass.biggerRoom: 12,
            Constants.RoomClass.pseudoRoom: 13
        ]

        return sorted {
            let firstIndex = defaultRoomClassOrder[$0.first?.roomClass ?? ""] ?? Int.max
            let secondIndex = defaultRoomClassOrder[$1.first?.roomClass ?? ""] ?? Int.max
            return ascending ? firstIndex < secondIndex : firstIndex > secondIndex
        }
    }
}

extension Array where Element == RoomLettingOption {
    var nonDiscountedRateCost: Cost? {
        guard let currencyCode = first?.baseRateAmount?.currencyCode else { return nil }

        let baseRateAmount = compactMap({ $0.baseRateAmount?.amount.doubleValue }).reduce(0, +)

        return Cost(amount: baseRateAmount, currencyCode: currencyCode)
    }
}

extension Array where Element == Room {
    /// V2 : Total city Tax where room contain options (options contain bookable room models)
    var totalCityTax: Cost? {
        guard let currencyCode = first?.options?.first?.cityTax?.currencyCode else { return nil }

        let startingAmount: Double = 0

        let totalAmount: Double = compactMap { room in
            // This currently defaults to the room.options?.first (we need to fix P+ rooms for amend🤒)

            let selection = room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first

            return selection?.cityTax?.amount.doubleValue
        }.reduce(startingAmount, +)

        return Cost(amount: totalAmount, currencyCode: currencyCode)
    }


    /// V2 : Total cost where rooms contain options (options contain bookable room models)
    var totalCost: Cost? {
        guard let currencyCode = first?.options?.first?.totalCost?.currencyCode else { return nil }

        let startingAmount: Double = 0

        let totalAmount: Double = compactMap { room in
            // This currently defaults to the room.options?.first(Amend Flow) (we need to fix P+ rooms for amend🤒)

            let selection = room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first

            return selection?.totalCost?.amount.doubleValue
        }.reduce(startingAmount, +)

        return Cost(amount: totalAmount, currencyCode: currencyCode)
    }

    /// V1: Total cost where root represents a bookable room model (still used in amend)
    var totalCostV1: Cost? {
        guard let currencyCode = first?.totalCost?.currencyCode else { return nil }

        let startingAmount: Double = 0

        let totalAmount: Double = compactMap { room in
            room.totalCost?.amount.doubleValue
        }.reduce(startingAmount, +)

        return Cost(amount: totalAmount, currencyCode: currencyCode)
    }

    func upsellsCostAmount(for upsells: [UpsellItem], availableUpsells: [UpsellItem]?, nights: Int) -> Double {
        var upsellAmount: Double = 0

        for room in self where room.roomId != nil {
            for upsellItem in upsells where room.roomId == upsellItem.roomId {
                guard let upsellItemIndividualCost = upsellItem.individualCost else { continue }

                if upsellItem.foodUpsell {
                    guard let fullBreakfastModel = availableUpsells?.first(where: { $0.code == upsellItem.code })
                        else { continue }
                    guard let kidsHaveToPay = fullBreakfastModel.kidsHaveToPay else { continue }
                    guard let existingAdultsWithMeals = upsellItem.adults else { continue }
                    guard let existingChildrenWithMeals = upsellItem.children else { continue }

                    var guestBreakfasts: Int = room.adults < existingAdultsWithMeals ? room.adults : existingAdultsWithMeals
                    guestBreakfasts += kidsHaveToPay ?
                        (room.children >= existingChildrenWithMeals ? existingChildrenWithMeals : room.children) : 0

                    upsellAmount += (Double(nights) * Double(guestBreakfasts) * upsellItemIndividualCost.amount.doubleValue)
                } else {
                    // for wifi the cost takes into consideration the nights
                    upsellAmount += upsellItemIndividualCost.amount.doubleValue
                }
            }
        }

        return upsellAmount
    }

    func nonFoodUpsellsCostAmount(for upsells: [UpsellItem], nights: Int) -> Double {
        var upsellAmount: Double = 0

        for room in self where room.roomId != nil {
            for upsellItem in upsells where room.roomId == upsellItem.roomId && upsellItem.foodUpsell == false {
                guard let upsellItemIndividualCost = upsellItem.individualCost else { continue }
                upsellAmount += (Double(nights) * upsellItemIndividualCost.amount.doubleValue)
            }
        }

        return upsellAmount
    }

    func foodUpsellsCostAmount(for upsells: [UpsellItem], availableUpsells: [UpsellItem]?, nights: Int) -> Double {
        var upsellAmount: Double = 0

        for room in self where room.roomId != nil {
            for upsellItem in upsells where room.roomId == upsellItem.roomId && upsellItem.foodUpsell == true {
                guard let upsellItemIndividualCost = upsellItem.individualCost else { continue }
                guard let fullBreakfastModel = availableUpsells?.first(where: { $0.code == upsellItem.code })
                    else { continue }
                guard let kidsHaveToPay = fullBreakfastModel.kidsHaveToPay else { continue }
                guard let existingAdultsWithMeals = upsellItem.adults else { continue }
                guard let existingChildrenWithMeals = upsellItem.children else { continue }

                var guestBreakfasts: Int = room.adults < existingAdultsWithMeals ? room.adults : existingAdultsWithMeals
                guestBreakfasts += kidsHaveToPay ?
                    (room.children >= existingChildrenWithMeals ? existingChildrenWithMeals : room.children) : 0

                upsellAmount += (Double(nights) * Double(guestBreakfasts) * upsellItemIndividualCost.amount.doubleValue)
            }
        }

        return upsellAmount
    }
}
