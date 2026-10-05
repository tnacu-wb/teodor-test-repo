//
//  AmendBookingInteractor+Amendments.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/10/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation
import UIKit

extension AmendBookingInteractor {
    // Analytics changes string is kind of pointless given everything else we're sending so we'll try to make it a simpler version
    func analyticsAmendsChanges(
        for existingReservation: Reservation,
        and newReservationModel: AmendReservationModel
    ) -> [AmendmentDetails] {
        var amendmentDetails: [AmendmentDetails] = []

        if existingReservation.arrivalDate != newReservationModel.criteria.arrivalDate {
            amendmentDetails.append(AmendmentDetails(title: subtitleString(with: "Dates changed"), description: nil))
        }

        if let nightsValueChange = analyticsValueChangeAmendment(
            for: existingReservation.nights,
            newValue: newReservationModel.criteria.nights,
            descriptor: "nights"
        ) {
            amendmentDetails.append(nightsValueChange)
        }

        if let roomsValueChange = analyticsValueChangeAmendment(
            for: existingReservation.rooms.count,
            newValue: newReservationModel.criteria.rooms.count,
            descriptor: "rooms"
        ) {
            amendmentDetails.append(roomsValueChange)
        }

        if let adultsValueChange = analyticsValueChangeAmendment(
            for: existingReservation.adultsCount,
            newValue: newReservationModel.criteria.adultsCount,
            descriptor: "adults"
        ) {
            amendmentDetails.append(adultsValueChange)
        }

        if let childrenValueChange = analyticsValueChangeAmendment(
            for: existingReservation.childrenCount,
            newValue: newReservationModel.criteria.childrenCount,
            descriptor: "children"
        ) {
            amendmentDetails.append(childrenValueChange)
        }

        let existingMealCount = Set(existingReservation.upsellItems.compactMap { $0.roomId }).count
        let newMealsCount = Set(
            newReservationModel.reservation.upsellItems.filter { upsell in
            upsell.foodUpsell == true
            }.compactMap { $0.roomUniqueID }
        ).count
        if let mealsValueChange = analyticsValueChangeAmendment(
            for: existingMealCount,
            newValue: newMealsCount,
            descriptor: "meals in rooms"
        ) {
            amendmentDetails.append(mealsValueChange)
        }

        let existingWifiCount = Set(existingReservation.wifiUpsells.compactMap { $0.roomId }).count
        let newWifiCount = newReservationModel.reservation.upsellItems
            .filter { $0.id == UpsellItemOperaId.ultimateWifi24Hours.rawValue }.count
        if let wifiValueChange = analyticsValueChangeAmendment(
            for: existingWifiCount,
            newValue: newWifiCount,
            descriptor: "wifi in rooms"
        ) {
            amendmentDetails.append(wifiValueChange)
        }

        return amendmentDetails
    }

    private func analyticsValueChangeAmendment(
        for originalValue: Int,
        newValue: Int,
        descriptor: String
    ) -> AmendmentDetails? {
        guard originalValue != newValue else { return nil }

        let valueChange = newValue - originalValue
        let valueChangeString = valueChange < 0 ? "Removed \(valueChange * -1) \(descriptor)" : "Added \(valueChange) \(descriptor)"
        return AmendmentDetails(title: subtitleString(with: valueChangeString), description: nil)
    }

    func changesFor(
        existingReservation: Reservation,
        and newReservationModel: AmendReservationModel,
        with identifiableDataRemoved: Bool
    ) -> [AmendmentDetails] {
        var amendments = [AmendmentDetails]()

        amendments.append(contentsOf: nightChangeAmendments(for: existingReservation, and: newReservationModel))
        amendments.append(contentsOf: existingRoomChanges(
            for: existingReservation,
            and: newReservationModel,
            withIdentifiableDataRemoved: identifiableDataRemoved
        ))
        amendments.append(contentsOf: newRoomChanges(
            for: existingReservation,
            and: newReservationModel,
            withIdentifiableDataRemoved: identifiableDataRemoved
        ))

        return amendments
    }

    private func nightChangeAmendments(
        for existingReservation: Reservation,
        and newReservationModel: AmendReservationModel
    ) -> [AmendmentDetails] {
        var amendDetails = [AmendmentDetails]()

        if (existingReservation.arrivalDate != newReservationModel.criteria.arrivalDate) ||
           (existingReservation.nights != newReservationModel.criteria.nights) {
            let datesChangedHeader = headerString(with: PILocalizedString("amendReviewDatesChangedLabel"))
            let newDatesDescription = "\(newReservationModel.criteria.rateDatesSummary) (\(newReservationModel.criteria.nightsCountDescription))"
            let datesDescription = subtitleString(with: newDatesDescription)

            // Get nights total for only rooms still in the booking
            let newRoomsIds = newReservationModel.criteria.rooms.compactMap { $0.roomId }
            let existingStayTotalAmount = existingReservation.rooms.filter {
                guard let roomId = $0.roomId else { return false }
                return newRoomsIds.contains(roomId)
            }.compactMap { $0.totalCost?.amount.doubleValue ?? 0.0 }.reduce(0.0, +)

            let existingRoomsIds = existingReservation.rooms.compactMap { $0.roomId }
            let newStayTotalAmountForExistingRooms = newReservationModel.criteria.rooms.filter {
                guard let roomId = $0.roomId else { return false }
                return existingRoomsIds.contains(roomId)
            }.compactMap { $0.totalCost?.amount.doubleValue ?? 0.0 }.reduce(0.0, +)

            let nightPriceChange = Cost(
                amount: newStayTotalAmountForExistingRooms - existingStayTotalAmount,
                currencyCode: existingReservation.totalCost?.currencyCode ?? "GBP"
            )
            let nightPriceChangeDescription: String? = nightPriceChange.amount
                .doubleValue > 0.0 ? "+\(nightPriceChange.localizedValue)" : nightPriceChange.amount
                .doubleValue == 0.0 ? nil : nightPriceChange.localizedValue

            amendDetails.append(AmendmentDetails(title: datesChangedHeader, description: nightPriceChangeDescription))
            amendDetails.append(AmendmentDetails(title: datesDescription, description: nil))
        }

        return amendDetails
    }

    private func existingRoomChanges(
        for existingReservation: Reservation,
        and newReservationModel: AmendReservationModel,
        withIdentifiableDataRemoved: Bool
    ) -> [AmendmentDetails] {
        var amendDetails = [AmendmentDetails]()

        for (index, room) in existingReservation.rooms.enumerated() {
            guard let updatedRoom = newReservationModel.criteria.rooms
                  .first(where: { $0.roomId == room.roomId && $0.roomId != $0.uniqueID.uuidString }) else {
                guard let displayName = room.leadGuest?.displayName else { continue }

                let nameOrNoName = withIdentifiableDataRemoved ? "" : " (\(displayName))"
                let roomNumberOrNoRoomNumber = withIdentifiableDataRemoved ? " \(index + 1)" : ""
                let header =
                    headerString(
                        with: "\(PILocalizedString("Room"))\(roomNumberOrNoRoomNumber) \(PILocalizedString("removedLowerCaseLabel"))\(nameOrNoName)"
                    )

                amendDetails.append(AmendmentDetails(
                    title: header,
                    description: "-\(roomTotalCost(for: room, in: existingReservation)?.localizedValue ?? "")"
                ))
                continue
            }

            let roomDifferences = RoomCouple(
                newRoom: updatedRoom,
                oldRoom: room,
                isOnlyRoom: roomCount == 1,
                delimeter: "\n"
            )
                .differences(withNamesRemoved: withIdentifiableDataRemoved, andRoomNumber: index + 1)

            if let roomDifferences = roomDifferences {
                amendDetails.append(AmendmentDetails(title: roomDifferences, description: nil))
            }

            if let roomId = room.roomId {
                if let mealChanges = mealChangesFor(roomWith: roomId, in: existingReservation, and: newReservationModel) {
                    amendDetails.append(mealChanges)
                }

                if let extraUpsellsChanges = extraUpsellsChangesFor(
                    roomId: roomId,
                    existingReservation: existingReservation,
                    updatedReservationModel: newReservationModel
                ) {
                    amendDetails.append(contentsOf: extraUpsellsChanges)
                }

                if let wiFiChanges = wifiChangesFor(roomWith: roomId, in: existingReservation, and: newReservationModel) {
                    amendDetails.append(wiFiChanges)
                }
            }
        }

        return amendDetails
    }

    private func roomTotalCost(for room: Room, in existingReservation: Reservation) -> Cost? {
        guard let roomCost = room.totalCost else { return nil }

        var totalAmount = roomCost.amount.doubleValue
        let currencyCode = roomCost.currencyCode

        if let id = room.roomId,
           existingReservation.upsellItems.contains(where: { $0.roomId == id }),
           let combinedUpsellsCost = upsellCost(
               in: existingReservation.upsellItems,
               for: id,
               nights: existingReservation.nights
           ) {
            totalAmount += combinedUpsellsCost.amount.doubleValue
        }

        return Cost(amount: totalAmount, currencyCode: currencyCode)
    }

    private func newRoomChanges(
        for existingReservation: Reservation,
        and newReservationModel: AmendReservationModel,
        withIdentifiableDataRemoved: Bool
    ) -> [AmendmentDetails] {
        var amendDetails = [AmendmentDetails]()

        for (index, room) in newReservationModel.criteria.rooms.enumerated() {
            guard existingReservation.rooms.contains(where: { $0.roomId == room.roomId }) == false else { continue }

            guard let displayName = room.leadGuest?.displayName else { continue }

            let nameOrNoName = withIdentifiableDataRemoved ? "" : " (\(displayName))"
            let roomNumberOrNoRoomNumber = withIdentifiableDataRemoved ? " \(index + 1)" : ""

            let header =
                headerString(
                    with: "\(PILocalizedString("Room"))\(roomNumberOrNoRoomNumber) \(PILocalizedString("addedLowerCaseLabel"))\(nameOrNoName)"
                )
            amendDetails.append(AmendmentDetails(title: header, description: "+\(room.totalCost?.localizedValue ?? "")"))

            if let roomId = room.roomId {
                if let mealChanges = mealChangesFor(roomWith: roomId, in: existingReservation, and: newReservationModel) {
                    amendDetails.append(mealChanges)
                }

                if let extraUpsellsChanges = extraUpsellsChangesFor(
                    roomId: roomId,
                    existingReservation: existingReservation,
                    updatedReservationModel: newReservationModel,
                    isNewRoom: true
                ) {
                    amendDetails.append(contentsOf: extraUpsellsChanges)
                }

                if let wiFiChanges = wifiChangesFor(
                    roomWith: roomId,
                    in: existingReservation,
                    and: newReservationModel,
                    isNewRoom: true
                ) {
                    amendDetails.append(wiFiChanges)
                }
            }
        }

        return amendDetails
    }

    func extraUpsellsChangesFor(
        roomId: String,
        existingReservation: Reservation,
        updatedReservationModel: AmendReservationModel,
        isNewRoom: Bool = false
    ) -> [AmendmentDetails]? {
        var amendmentDetails: [AmendmentDetails]? = []

        let originalExtras: [UpsellItem]? = existingReservation.extraUpsells.filter { $0.roomId == roomId }
        let originalExtrasCosts: [Cost]? = getExtraUpsellsCosts(items: originalExtras)

        let updatedExtras: [UpsellItem]? = updatedReservationModel.reservation.upsellItems
            .filter { $0.roomId == roomId && $0.isExtraUpsell && $0.wifiUpsell == false }
        let updatedExtrasCosts: [Cost]? = getExtraUpsellsCosts(items: updatedExtras)

        // Creating a tuple of (upsells and cost), to have it ready for the upsellChanges(...) function
        var originalExtraAndTotalCostArray: [UpsellAndTotalCost] = []
        for (originalExtra, originalExtraCost) in zip(originalExtras ?? [], originalExtrasCosts ?? []) {
            let originalExtraAndTotalCost = UpsellAndTotalCost(originalExtra, originalExtraCost)
            originalExtraAndTotalCostArray.append(originalExtraAndTotalCost)
        }

        var updatedExtraAndTotalCostArray: [UpsellAndTotalCost] = []
        for (updatedExtra, updatedExtraCost) in zip(updatedExtras ?? [], updatedExtrasCosts ?? []) {
            let updatedExtraAndTotalCost = UpsellAndTotalCost(updatedExtra, updatedExtraCost)
            updatedExtraAndTotalCostArray.append(updatedExtraAndTotalCost)
        }

        guard let updatedRoom = updatedReservationModel.criteria.rooms.first(where: { $0.roomId == roomId }) else {
            return nil
        }

        let maxCount = max(originalExtraAndTotalCostArray.count, updatedExtraAndTotalCostArray.count)

        for index in 0..<maxCount {
            let original = originalExtraAndTotalCostArray[safe: index]
            let updated = updatedExtraAndTotalCostArray[safe: index]

            guard let changeSummary = upsellChanges(
                for: (original?.upsell, original?.totalCost),
                and: (updated?.upsell, updated?.totalCost),
                in: updatedRoom,
                using: "\(PILocalizedString("extraChangesLabel")) (\(updatedRoom.leadGuest?.displayName ?? ""))\n",
                isNewRoom: isNewRoom
            ) else {
                return nil
            }

            let priceChangeString: String? = priceDifferenceString(for: changeSummary.priceChange)
            guard !(changeSummary.upsellChanged == false && priceChangeString == nil) else { return nil }

            amendmentDetails?.append(AmendmentDetails(
                title: subtitleString(with: changeSummary.title),
                description: priceChangeString
            ))
        }

        return amendmentDetails
    }

    private func getExtraUpsellsCosts(items: [UpsellItem]?) -> [Cost]? {
        items?.compactMap { extraUpsell in
            guard let individualCost = extraUpsell.individualCost else { return nil }
            return Cost(amount: individualCost.amount.doubleValue, currencyCode: individualCost.currencyCode)
        }
    }

    private func mealChangesFor(
        roomWith id: String,
        in existingReservation: Reservation,
        and updatedReservationModel: AmendReservationModel
    ) -> AmendmentDetails? {
        let didNightsChange = existingReservation.nights != updatedReservationModel.criteria.nights

        let originalMeals: [UpsellItem]? = {
            let originalMeals = existingReservation.foodUpsells.filter({ $0.roomId == id && $0.foodUpsell == true })
            return originalMeals.isEmpty ? nil : originalMeals
        }()

        let originalMealCost: Cost? = {
            guard let originalRoom = existingReservation.rooms.first(where: { $0.roomId == id }) else { return nil }
            guard let originalMeals = originalMeals else { return nil }
            return mealCost(in: originalRoom, during: existingReservation.nights, for: originalMeals)
        }()

        let updatedMeals: [UpsellItem]? = {
            let updatedMeals = updatedReservationModel.reservation.upsellItems
                .filter({ $0.roomId == id && $0.foodUpsell == true })

            return updatedMeals.isEmpty ? nil : updatedMeals
        }()

        guard !(originalMeals == nil && updatedMeals == nil) else { return nil }
        if originalMeals?.isEqual(rhs: updatedMeals) == true && !didNightsChange { return nil }

        guard let updatedRoom = updatedReservationModel.criteria.rooms.first(where: { $0.roomId == id }) else { return nil }

        // Get the meal cost for the updated meals if there are no new meals then the cost will be 0.0.
        let updatedMealCost: Cost = {
            guard let updatedMeals = updatedMeals else { return Cost(
                amount: 0.0,
                currencyCode: originalMealCost?.currencyCode ?? "GBP"
            ) }

            return mealCost(in: updatedRoom, during: updatedReservationModel.criteria.nights, for: updatedMeals) ?? Cost(
                amount: 0.0,
                currencyCode: originalMealCost?.currencyCode ?? "GBP"
            )
        }()

        // If the original meal cost is null it is a likely a new room so use the cost as 0.0 when we compare the two values.
        let priceDifferenceString = self.priceDifferenceString(
            for: originalMealCost ?? Cost(amount: 0.0, currencyCode: updatedMealCost.currencyCode),
            and: updatedMealCost
        )

        return AmendmentDetails(
            title: subtitleString(
                with: "\(PILocalizedString("mealChangesLabel")) (\(updatedRoom.leadGuest?.displayName ?? ""))"
            ),
            description: priceDifferenceString
        )
    }

    private func wifiChangesFor(
        roomWith id: String,
        in existingReservation: Reservation,
        and updatedReservationModel: AmendReservationModel,
        isNewRoom: Bool = false
    ) -> AmendmentDetails? {
        let originalWiFi: UpsellItem? = {
            guard let originalWiFi = existingReservation.wifiUpsells.first(where: { $0.roomId == id }) else { return nil }
            return existingReservation.availableUpsells?.first(where: { $0.id == originalWiFi.id })
        }()
        let updatedWiFi: UpsellItem? = {
            guard let updatedWiFi = updatedReservationModel.reservation.upsellItems
                  .first(where: { $0.roomId == id && $0.id == UpsellItemOperaId.ultimateWifi24Hours.rawValue })
            else { return nil }
            return updatedWiFi
        }()

        guard !(originalWiFi == nil && updatedWiFi == nil) else { return nil }

        let originalWiFiCost: Cost? = {
            guard let originalWiFi = originalWiFi else { return nil }
            return wifiCost(using: originalWiFi, during: existingReservation.nights)
        }()

        guard let updatedRoom = updatedReservationModel.criteria.rooms.first(where: { $0.roomId == id }) else { return nil }

        let updatedWiFiCost: Cost? = {
            guard let updatedWiFi = updatedWiFi else { return nil }
            return wifiCost(using: updatedWiFi, during: updatedReservationModel.criteria.nights)
        }()

        guard let wiFiChangeSummary = upsellChanges(
            for: (originalWiFi, originalWiFiCost),
            and: (updatedWiFi, updatedWiFiCost),
            in: updatedRoom,
            using: "\(PILocalizedString("wifiChangesLabel")) (\(updatedRoom.leadGuest?.displayName ?? ""))\n",
            isNewRoom: isNewRoom
        ) else { return nil }

        let priceChangeString: String? = priceDifferenceString(for: wiFiChangeSummary.priceChange)
        guard !(wiFiChangeSummary.upsellChanged == false && priceChangeString == nil) else { return nil }

        return AmendmentDetails(title: subtitleString(with: wiFiChangeSummary.title), description: priceChangeString)
    }

    private typealias UpsellAndTotalCost = (upsell: UpsellItem?, totalCost: Cost?)
    private typealias UpsellChangeSummary = (title: String, priceChange: Cost?, upsellChanged: Bool)

    private func upsellChanges(
        for originalUpsellAndCost: UpsellAndTotalCost,
        and updatedUpsellAndCost: UpsellAndTotalCost,
        in updatedRoom: Room,
        using prefix: String,
        isNewRoom: Bool = false
    ) -> UpsellChangeSummary? {
        switch (originalUpsellAndCost.upsell, updatedUpsellAndCost.upsell) {
        case let (original, nil) as (UpsellItem, UpsellItem?):

            guard var cost = originalUpsellAndCost.totalCost else { return nil }
            cost.amount = cost.amount.multiplying(by: NSDecimalNumber(mantissa: 1, exponent: 0, isNegative: true))

            return ("\(prefix)\(PILocalizedString("removedLabel")) \(original.legend)", cost, true)
        case let (nil, updated) as (UpsellItem?, UpsellItem):

            let label = isNewRoom == true ? "\(updated.legend)" : "\(prefix)\(PILocalizedString("addedLabel")) \(updated.legend)"

            return (label, updatedUpsellAndCost.totalCost, true)
        case let (original, updated) as (UpsellItem, UpsellItem):

            let currencyCode = originalUpsellAndCost.totalCost?.currencyCode ?? "GBP"
            let priceDifference = (updatedUpsellAndCost.totalCost?.amount.doubleValue ?? 0.0) -
                (originalUpsellAndCost.totalCost?.amount.doubleValue ?? 0.0)
            let costDifference = Cost(amount: priceDifference, currencyCode: currencyCode)

            if original.code == updated.code {
                return ("\(prefix)\(updated.legend)", costDifference, false)
            } else {
                return (
                    "\(prefix)\(PILocalizedString("changedFromLabel")) \(original.legend) \(PILocalizedString("changedFromIsolatedToLabel")) \(updated.legend)",
                    costDifference,
                    true
                )
            }
        default:
            return nil
        }
    }

    private func upsellCost(in breakdown: [UpsellItem], for roomId: String, nights: Int) -> Cost? {
        let upsells = breakdown.filter { $0.roomId == roomId }
        let currencyCode = upsells.first?.individualCost?.currencyCode ?? "GBP"

        let costAmounts = upsells.compactMap { upsellItem in
            let costByQuantity = upsellItem.individualCost?.amount.doubleValue ?? 0.0 * Double(upsellItem.quantity ?? 0)

            guard UpsellItemOperaId.extras.contains(where: { $0 == upsellItem.upsellOperaId }) == false
                else { return costByQuantity }

            return costByQuantity * Double(nights)
        }

        return Cost(amount: costAmounts.reduce(0.0, +), currencyCode: currencyCode)
    }

    private func mealCost(in room: Room, during nights: Int, for upsell: [UpsellItem]) -> Cost? {
        guard let currencyCode = upsell.first?.individualCost?.currencyCode else { return nil }

        return Cost(
            amount: upsell.map({ $0.price.amount.doubleValue }).reduce(0.0, +) * Double(nights),
            currencyCode: currencyCode
        )
    }

    private func wifiCost(using wifi: UpsellItem, during nights: Int) -> Cost? {
        wifi.price // price includes the nights calculation
    }

    private func priceDifferenceString(for cost: Cost?) -> String? {
        guard let cost = cost else { return nil }
        guard cost.amount.doubleValue != 0.0 else { return nil }

        return cost.amount.doubleValue > 0.0 ? "+\(cost.localizedValue)" : cost.localizedValue
    }

    private func headerString(with string: String) -> NSAttributedString {
        let header = NSMutableAttributedString(string: string, attributes: [NSAttributedString.Key.font: UIFont.Body()])
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 7.5
        header.addAttributes([.paragraphStyle: paragraphStyle], range: NSRange(location: 0, length: header.length))

        return header
    }

    private func subtitleString(with string: String) -> NSAttributedString {
        let subtitle = NSMutableAttributedString(
            string: string + "\n",
            attributes: [NSAttributedString.Key.font: UIFont.Body()]
        )
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 7.5
        subtitle.addAttributes([.paragraphStyle: paragraphStyle], range: NSRange(location: 0, length: subtitle.length))

        return (subtitle)
    }
}
