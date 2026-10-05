//
//  Hotel+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import MapKit

extension Hotel {
	var userActivity: NSUserActivity? {
		let activity = NSUserActivity(activityType: Constants.hotelDetailUserActivityType)
		activity.title = title
		activity.webpageURL = webURL

        activity.keywords = ["hotel"]
        activity.isEligibleForHandoff = true
        activity.isEligibleForSearch = true
        activity.isEligibleForPublicIndexing = true

        if let placeMark = placeMark {
            activity.mapItem = MKMapItem(placemark: placeMark)
        }

		return activity
	}

	func hotelDetailsProductString(includeDistance: Bool = true) -> String {
        let merchandisingEventsString = available ? "event90=1" : "event84=1"

		var merchandisingEVarsString: [String] = []

        merchandisingEVarsString.append(self.merchandisingEventsStringForHotelDetails(from: rates))

        if let messagingFlag = messagingFlag {
            merchandisingEVarsString.append("eVar51=\(messagingFlag.text)")
        }

		return ";\(code);;;\(merchandisingEventsString);\(merchandisingEVarsString.joined(separator: "|"))"
	}

    func roomSelectionProductString(includeDistance: Bool = true) -> String {
        let merchandisingEventsString = available ? "event90=1" : "event84=1"

        var merchandisingEVarsString: [String] = []

        merchandisingEVarsString.append(self.merchandisingEventsStringForRoomSelection(from: rates))

        if let messagingFlag = messagingFlag {
            merchandisingEVarsString.append("eVar51=\(messagingFlag.text)")
        }

        return ";\(code);;;\(merchandisingEventsString);\(merchandisingEVarsString.joined(separator: "|"))"
    }

    func searchResultsProductStringFromCheapestRate(atIndex index: Int?, includeDistance: Bool = true) -> String {
        let merchandisingEventsString = available ? "event90=1" : "event84=1"

        var merchandisingEVarsString: [String] = []

        // this doesn't come back from GraphQL availabilities
        if let cheapestRate = cheapestRate {
            merchandisingEVarsString.append(self.merchandisingEventsStringForSearchResults(fromCheapestRate: cheapestRate))
        } else if let lowestCost = lowestRoomRateCost {
            merchandisingEVarsString.append(self.merchandisingEventsStringForSearchResults(lowestCost: lowestCost))
        }

        merchandisingEVarsString.append("eVar50=\(distance)")

        if let index = index {
            merchandisingEVarsString.append("eVar55=\(index)")
        }

        if let messagingFlag = messagingFlag {
            merchandisingEVarsString.append("eVar51=\(messagingFlag.text)")
        }

        return ";\(code);;;\(merchandisingEventsString);\(merchandisingEVarsString.joined(separator: "|"))"
    }

    func searchResultProductStringShort(index: Int?) -> String {
        var merchandisingEVarsString: [String] = []

        merchandisingEVarsString.append("eVar50=\(distance)")

        if let index = index {
            merchandisingEVarsString.append("eVar55=\(index)")
        }

        if let messagingFlag = messagingFlag {
            merchandisingEVarsString.append("eVar51=\(messagingFlag.text)")
        }
        return "\(merchandisingEVarsString.joined(separator: "|"))"
    }

	func eventString(existingEvents: [String] = []) -> String {
		var merchandisingEventsString: [String] = []

		merchandisingEventsString.append(contentsOf: existingEvents)

		return merchandisingEventsString.joined(separator: ",")
	}

    func update(with response: HotelAvailabilityResponse?, ratesContent: [RateInformation]?) {
        let rates = response?.rates ?? []
        update(rates: rates)
        update(prepaymentAllowed: response?.prepaymentAllowed ?? false)
        update(available: response?.available ?? false)
        update(limitedAvailability: response?.limitedAvailability ?? false)
        update(cityTaxResponse: response?.cityTaxResponse)
        update(paymentProvider: response?.paymentProvider)

        // store the content in SettingsManager
        SettingsManager.sharedInstance.roomTypesContent = response?.roomTypeContent
        SettingsManager.sharedInstance.ratesContent = {
            if let ratesContent = ratesContent, ratesContent.isNotEmpty {
                return ratesContent + (response?.ratesContent ?? [])
            }

            return response?.ratesContent
        }()

        self.cnpAuthorisation = response?.cnpAuthorisation
    }

    private func merchandisingEventsStringForHotelDetails(from rates: [Rate]) -> String {
        var merchandisingEventString: [String] = []

        for rate in rates {
            guard let rooms = rate.rooms else { continue }

            rooms.forEach {
                $0.options?.forEach { option in
                        let costAsDouble = Double(truncating: option.totalCost?.amount ?? 0)
                        let twoDecimalPlaceFormattedCost = String(format: "%.2f", costAsDouble)
                    merchandisingEventString
                        .append(
                            "\(rate.rateCode().withNumbersRemoved())-\(option.lettingType ?? "")-\(twoDecimalPlaceFormattedCost)"
                        )
                }
            }
        }

        return "evar65=\(merchandisingEventString.joined(separator: ">"))"
    }

    private func merchandisingEventsStringForRoomSelection(from rates: [Rate]) -> String {
        var merchandisingEventString: [String] = []

        guard let selectedRate = rates.first(where: { $0.code == BookingDetails.sharedInstance.rate?.code })
            else { return "" }
        guard let rooms = selectedRate.rooms else { return "" }

        var roomLettingTypes: [String] = []

        let firstRoomWithMultipleOptions = rooms.first(where: { $0.options?.count ?? 1 > 1 })

        rooms.forEach { roomLettingTypes.append($0.options?.first?.lettingType ?? "") }

        let concatonatedLettingTypes = roomLettingTypes.joined(separator: "_")

        let costAsDouble = Double(truncating: selectedRate.totalCost(for: nil)?.amount ?? 0)
        let twoDecimalPlaceFormattedCost = String(format: "%.2f", costAsDouble)
        merchandisingEventString
            .append(
                "\(selectedRate.rateCode().withNumbersRemoved())-\(concatonatedLettingTypes)-\(twoDecimalPlaceFormattedCost)"
            )

        // add another set with the second options - if there are any...
        roomLettingTypes = []
        if firstRoomWithMultipleOptions != nil {
            rooms.forEach { roomLettingTypes.append($0.options?.last?.lettingType ?? $0.options?.first?.lettingType ?? "") }

            let concatonatedLettingTypes = roomLettingTypes.joined(separator: "_")

            let costAsDouble = Double(truncating: selectedRate
                .totalCost(for: firstRoomWithMultipleOptions?.options?.last?.lettingType)?.amount ?? 0)
            let twoDecimalPlaceFormattedCost = String(format: "%.2f", costAsDouble)
            merchandisingEventString
                .append(
                    "\(selectedRate.rateCode().withNumbersRemoved())-\(concatonatedLettingTypes)-\(twoDecimalPlaceFormattedCost)"
                )
        }

        return "evar65=\(merchandisingEventString.joined(separator: ">"))"
    }

    private func merchandisingEventsStringForSearchResults(fromCheapestRate rate: Rate) -> String {
        guard let rooms = rate.rooms else { return "" }

        var roomLettingTypes: [String] = []

        rooms.forEach { roomLettingTypes.append($0.lettingType ?? "") }

        let concatonatedLettingTypes = roomLettingTypes.joined(separator: "_")

        let costAsDouble = Double(truncating: rate.totalCost.amount)
        let twoDecimalPlaceFormattedCost = String(format: "%.2f", costAsDouble)
        let merchandisingEventString = "\(rate.rateCode().withNumbersRemoved())-\(concatonatedLettingTypes)-\(twoDecimalPlaceFormattedCost)"

        return "evar62=\(merchandisingEventString)"
    }

    private func merchandisingEventsStringForSearchResults(lowestCost: Cost) -> String {
        let costAsDouble = Double(truncating: lowestCost.amount)
        let twoDecimalPlaceFormattedCost = String(format: "%.2f", costAsDouble)
        let merchandisingEventString = "--\(twoDecimalPlaceFormattedCost)"

        return "evar62=\(merchandisingEventString)"
    }

    public var alternativeTwinRoomImages: [URL] {
        guard let imagesBaseURL = URL(string: "https://www.premierinn.com") else { return [] }

        return [
            imagesBaseURL.appendingPathComponent(PILocalizedString("trueTwinOptionImage")),
            imagesBaseURL.appendingPathComponent(PILocalizedString("premierInnTwinOptionImage"))
        ]
    }

    public func notesToShow(arrivalDate: Date, departureDate: Date?) -> [Note]? {
        let notesFiltered = notes?.filter({ note in
            note.datesOverlap(arrivalDate: arrivalDate, departureDate: departureDate)
        })
        return notesFiltered
    }

    public func upsellsAvailable(arrivalDate: Date?, departureDate: Date?, upsells: [UpsellItem]) -> [UpsellItem] {
        upsells.filter({
            !shouldHideUpsell(arrivalDate: arrivalDate, departureDate: departureDate, upsell: $0)
        })
    }

    public func closeoutUpsells(arrivalDate: Date?, departureDate: Date?, upsells: [UpsellItem]) -> [UpsellItem] {
        upsells.filter {
            shouldHideUpsell(
                arrivalDate: arrivalDate,
                departureDate: departureDate,
                upsell: $0
            )
        }
    }

    private func shouldHideUpsell(arrivalDate: Date?, departureDate: Date?, upsell: UpsellItem) -> Bool {
        guard let arrivalDate = arrivalDate?.dateNormalised, let departureDate = departureDate?.dateNormalised,
              let closeOutItems = ancillaryCloseout else { return false }
        for closeOut in closeOutItems {
            guard closeOut.datesOverlap(arrivalDate: arrivalDate, departureDate: departureDate) else { continue }
            guard let arraySplit = closeOut.upsellCodes?.components(separatedBy: ",") else { continue }
            guard arraySplit.contains(where: {$0 == upsell.id}) else { continue }
            return true
        }
        return false
    }

    var isAvailableAndHasRates: Bool {
        available && rates.isNotEmpty
    }
}
