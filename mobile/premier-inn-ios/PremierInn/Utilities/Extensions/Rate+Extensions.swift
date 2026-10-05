//
//  Rate+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

protocol GermanCityTaxViewModelValues {
    var cityTaxRequired: Bool { get }
}

extension Rate {
    func substitutions() -> [RoomSubstitution]? {
        guard let rooms = rooms else { return nil }
        var subs = [RoomSubstitution]()
        for (index, room) in rooms.enumerated() {
            let nonSilentRoomOptions = room.options?.filter { $0.silentSubstitution == false }
            guard nonSilentRoomOptions?.isNotEmpty == true else { continue }

            let desiredRoomType = rooms[index].type
            let substitutedRoomsConcatenated = nonSilentRoomOptions?
                .compactMap { $0.lettingType }
                .map { SettingsManager.sharedInstance.roomLabelFor(lettingType: $0 )}
                .joined(separator: " \(PILocalizedString("or")) ")

            let roomName = PILocalizedString("genericRoomTitle", comment: "") + " " + String(describing: index + 1)

            subs.append(RoomSubstitution(
                desired: desiredRoomType,
                substituted: room.type,
                substitutedRoomsConcatenated: substitutedRoomsConcatenated,
                roomName: roomName
            ))
        }
        return subs
    }

    func name(with brand: HotelBrand? = .premierInn) -> String {
        if cellCode == "EMP01" {
            return PILocalizedString("employeeRatesDescription", comment: "Employee Rates Description")
        }

        guard let classification = classification else { return name ?? "" }
        if let name = SettingsManager.sharedInstance.rateContent(for: classification)?.name {
            return name
        }

        if let rateContent = SettingsManager.sharedInstance.rateContent(for: classification, and: brand) {
            return rateContent.rateName
        }

        return name ?? ""
    }

    func longDescription(with brand: HotelBrand? = .premierInn) -> String {
        guard let classification = classification else { return description ?? "" }

        if let longDescription = SettingsManager.sharedInstance.rateContent(for: classification)?.description {
            return longDescription
        }

        if let rateContent = SettingsManager.sharedInstance.rateContent(for: classification, and: brand) {
            return rateContent.rateDescription ?? description ?? ""
        }

        return description ?? ""
    }

    func bookingTermsMessage(with brand: HotelBrand? = .premierInn) -> String? {
        guard let classification = classification else { return nil }
        if let message = SettingsManager.sharedInstance.rateContent(for: classification)?.bookingTermsMessage {
            return message
        }

        let rateContent = SettingsManager.sharedInstance.rateContent(for: classification, and: brand)
        return rateContent?.rateNotes?.htmlStripped()
    }

    var backgroundColor: UIColor {
        UIColor.BaseGrey
    }

    var borderColor: UIColor {
        UIColor.TintL3
    }

    public func rateCode() -> String {
        classification ?? ""
    }
}

extension Rate: GermanCityTaxViewModelValues {
    var cityTaxRequired: Bool {
        let roomsCityTaxes: [Cost]? = self.rooms?.compactMap { $0.options?.first?.cityTax }

        return roomsCityTaxes?.filter { $0.amount.doubleValue > 0 }.count ?? 0 > 0
    }
}

typealias LettingOptionRates = (roomClassOptions: [RoomLettingOption], rates: [Rate])

extension Array where Element == Rate {
    var ratesSeparatedByTieredRooms: [LettingOptionRates] {
        let standardRooms = first?.rooms
        // if there is a room without multiple options, then we skip everything
        if let lonelyRoom = standardRooms?.first(where: { $0.options?.count ?? 0 < 2 }) {
            guard let option = lonelyRoom.options?.first else { return [] }
            return [([option], self)]
        }

        guard let roomOptions = standardRooms?.first?.options else { return [] }
        let roomClasses = Set(roomOptions.compactMap { $0.roomClass })

        // group rooms by room class
        let roomOptionsByClass = roomClasses.compactMap { roomClass in
            roomOptions.filter({ $0.roomClass == roomClass })
        }

        let sortingOrder = MVTManager.sharedInstance.groupType == .control
        let lettingOptionRates: [[RoomLettingOption]] = roomOptionsByClass
            .sortRoomOptionsByRoomClassOrderFromAPI(ascending: sortingOrder)

        return lettingOptionRates.map { ($0, self) }
    }

    // filter out the rates for which we have the rateContent already
    func ratesWithoutContent(ratesContent: [RateInformation]) -> [Rate] {
        self.filter({
            guard let classification = $0.classification else { return false }

            return ratesContent.contains(where: { $0.rateClassification == classification }) == false
        })
    }
}

extension Rate {
    func totalCost(for lettingType: String?) -> Cost? {
        let roomsLettingTypes = roomOptionsForLettingType(lettingType)

        guard let currencyCode = roomsLettingTypes?.first?.totalCost?.currencyCode,
              let totalAmount = (roomsLettingTypes?.compactMap { $0.totalCost?.amount.doubleValue }.reduce(0, +))
        else { return nil }

        return Cost(amount: totalAmount, currencyCode: currencyCode)
    }

    func nonDiscountedCost(for lettingType: String?) -> Cost? {
        let roomsLettingTypes = roomOptionsForLettingType(lettingType)
        let baseRateCost = roomsLettingTypes?.nonDiscountedRateCost

        return baseRateCost
    }

    private func roomOptionsForLettingType(_ lettingType: String?) -> [RoomLettingOption]? {
        // use the lettingType to get the class of the selected room
        guard let roomClass = rooms?.flatMap({ $0.options ?? [] }).first(where: { $0.lettingType == lettingType })?
              .roomClass else { return nil }
        // randomly use one of the options for the total cost - if these are not the same, we'll need to maybe pick the cheapest and show "from" in the UI?
        let roomsLettingTypes: [RoomLettingOption]? = rooms?.compactMap { room in
            room.options?.first(where: { $0.roomClass == roomClass }) ?? room.options?.first
        }
        return roomsLettingTypes
    }

    var extraUpsellsExcludingECiLco: [UpsellItem]? {
        let extras = extraUpsells
        return extras?.filter { $0.upsellOperaId != .earlyCheckIn && $0.upsellOperaId != .lateCheckOut }
    }

    private var combinedUserPurchasableUpsells: [UpsellItem] {
        var extras = extraUpsells ?? []
        if !SettingsManager.sharedInstance.featureAllowEciLco {
            extras = extraUpsellsExcludingECiLco ?? []
        }
        let foodAndExtraUpsells = (foodUpsells ?? []) + (extras)
        return foodAndExtraUpsells + (wifiUpsells ?? [])
    }

    var hasUserPurchasableUpsells: Bool {
        combinedUserPurchasableUpsells.isNotEmpty
    }
}

// 💍🚘💰🦞 BusinessBookingsZone 💍🚘💰🦞

extension Rate {
    /**
    Returns a boolean if a single upsell is found in rate.foodUpsells or rate.wifiUpsells that matches company upsells allowed.

    - Parameters:
       - companyAllowances: An array of upsell item codes approved by the user's company.
    */
    func upsellsAvailable(for companyAllowances: [Int]) -> Bool {
        guard companyAllowances.isNotEmpty else { return false }
        guard combinedUserPurchasableUpsells.isNotEmpty else { return false }

        let rateCodes = Set(combinedUserPurchasableUpsells.compactMap { $0.code })
        let companyCodes = Set(companyAllowances)

        return rateCodes.intersection(companyCodes).isNotEmpty
    }

    /**
        Returns a filtered list of wifi upsells (from rate) based on company booking allowances
     */
    func wifiUpsells(for company: Company?) -> [UpsellItem] {
        // wifi doesn't have the bartId
        guard let company = company else { return wifiUpsells ?? [] }
        guard let allowedCompanyUpsellsCodes = (company.bookingAllowances?.upsellItemsAllowed?.compactMap { Int($0) })
            else { return [] }

        return wifiUpsells?.filter { allowedCompanyUpsellsCodes.contains($0.code ?? 0) } ?? []
    }

    /**
       Returns a filtered list of food upsells (from rate) based on company booking allowances
    */
    func foodUpsells(for company: Company?) -> [UpsellItem] {
        guard let company = company else { return foodUpsells ?? [] }
        guard let allowedCompanyUpsellsCodes = (company.bookingAllowances?.upsellItemsAllowed?.compactMap { Int($0) })
            else { return [] }

        return foodUpsells?.filter { allowedCompanyUpsellsCodes.contains($0.code ?? 0) } ?? []
    }
}
