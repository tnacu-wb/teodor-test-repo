//
//  AdditionalInfoInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork

class AdditionalInfoInteractor {
    private let hotel: Hotel
    private let type: AdditionalInfoType

    init(with hotel: Hotel, and type: AdditionalInfoType) {
        self.hotel = hotel
        self.type = type
    }

    private var parkingDescription: String? {
        var content = [String]()

        if let summary = hotel.parkings.first?.summary {
            content.append(summary.text)
        }

        if let description = hotel.parkings.first?.description(with: hotel.parkingDescription) {
            content.append(description.text)
        } else if let description = hotel.parkingDescription?.htmlStripped()
                    .trimmingCharacters(in: .whitespacesAndNewlines) {
            content.append(description)
        }

        guard !content.isEmpty else { return hotel.parkingDescription }

        return content.joined(separator: "\n")
    }

    private var facilityDescriptions: [String] {
        var descriptions = [String]()

        guard let facilities = hotel.facilities else { return descriptions }

        for facility in facilities {
            descriptions.append(facility.legend)
        }

        return descriptions
    }

    private var roomFeatureDescriptions: [String] {
        switch hotel.brand {
        case .premierInn, .premierInnGermany:
            return [
                PILocalizedString("premierInnRoomFacilityHighPoweredShowers"),
                PILocalizedString("premierInnRoomFacilityFreeWiFi"),
                PILocalizedString("premierInnRoomFacilityDesk"),
                PILocalizedString("premierInnRoomFacilityPillows"),
                PILocalizedString("premierInnRoomFacilityTeaAndCoffee"),
                PILocalizedString("premierInnRoomFacilityBed"),
                PILocalizedString("premierInnRoomFacilityDuvet"),
                PILocalizedString("premierInnRoomFacilityFreeview"),
                PILocalizedString("premierInnRoomFacilityCurtains"),
                PILocalizedString("premierInnRoomFacilityAirConditioning")
            ]
        case .hub:
            return [
                PILocalizedString("hubRoomFacilityShower"),
                PILocalizedString("hubRoomFacilityWifi"),
                PILocalizedString("hubRoomFacilityBed"),
                PILocalizedString("hubRoomFacilityRoomControls"),
                PILocalizedString("hubRoomFacilityAirConditioning"),
                PILocalizedString("hubRoomFacilityDuvet"),
                PILocalizedString("hubRoomFacilityTV"),
                PILocalizedString("hubRoomFacilityDesk"),
                PILocalizedString("hubRoomFacilityChargingPoints"),
                PILocalizedString("hubRoomFacilityMirror"),
                PILocalizedString("hubRoomFacilityUnderbedStorage"),
                PILocalizedString("hubRoomFacilityHairdryer"),
                PILocalizedString("hubRoomFacilityCurtains")
            ]
        case .zip:
            return [
                PILocalizedString("zipRoomFacilityShower"),
                PILocalizedString("zipRoomFacilityTowelAndBodywash"),
                PILocalizedString("zipRoomFacilityWifi"),
                PILocalizedString("zipRoomFacilityBed"),
                PILocalizedString("zipRoomFacilityAirConditioning"),
                PILocalizedString("zipRoomFacilityTV"),
                PILocalizedString("zipRoomFacilityStorage"),
                PILocalizedString("zipRoomFacilityMirror"),
                PILocalizedString("zipRoomFacilityWindow")
            ]
        }
    }
}

extension AdditionalInfoInteractor: AdditionalInfoInteractorProtocol {
    struct ViewModel: AdditionalInfoViewModel {
        let infoType: AdditionalInfoType
        let hotelName: String
        let hotelNotes: [String]?
        let hotelDescription: String?
        let hotelDirections: String?
        let parkingDetails: String?

        var facilityDescriptions: [String]?
        var facilityTitle: String?
        var roomFeatureDescriptions: [String]?
        var roomFeatureTitle: String?
    }

    var viewModel: AdditionalInfoViewModel {
        ViewModel(
            infoType: type,
            hotelName: hotel.name,
            hotelNotes: hotel.notesToShow(
                arrivalDate: BookingDetails.sharedInstance.criteria.arrivalDate,
                departureDate: BookingDetails.sharedInstance.departureDate
            )?.compactMap { $0.text },
            hotelDescription: hotel.hotelDescription,
            hotelDirections: hotel.directions,
            parkingDetails: parkingDescription,
            facilityDescriptions: facilityDescriptions,
            facilityTitle: PILocalizedString("hotelFacilitiesAndRoomFeaturesFacilityTitle"),
            roomFeatureDescriptions: roomFeatureDescriptions,
            roomFeatureTitle: PILocalizedString("hotelFacilitiesAndRoomFeaturesRoomFeaturesTitle")
        )
    }
}
