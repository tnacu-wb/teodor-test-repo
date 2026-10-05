//
//  PlanYourTripInfoInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import MapKit
import CoreLocation

class PlanYourTripInfoInteractor {
    private var hotel: Hotel

    init(hotel: Hotel) {
        self.hotel = hotel
    }
}

extension PlanYourTripInfoInteractor: PlanYourTripInfoInteractorProtocol {
    private struct ViewModel: PlanYourTripInfoViewModel {
        let title: String?
        let hotelAddress: String?
        let directions: String?
        let hotelParking: String?
    }

    var viewModel: PlanYourTripInfoViewModel? {
        ViewModel(
            title: PILocalizedString("reservationsPlanShortcut"),
            hotelAddress: hotel.address?.description,
            directions: hotel.directions,
            hotelParking: hotel.parkingDescription
        )
    }
}
