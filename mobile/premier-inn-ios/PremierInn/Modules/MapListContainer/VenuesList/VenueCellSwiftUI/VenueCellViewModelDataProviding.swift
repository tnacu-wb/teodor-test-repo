//
//  VenueCellViewModelDataProviding.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 19/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

// MARK: - Venue Cell Data Provider

protocol VenueCellViewModelDataProviding {
    var name: String { get }
    var brand: HotelBrand { get }
    var limitedAvailability: Bool { get }
    var parkings: [ParkingType] { get }
    var primaryImages: [URL] { get }
    var messagingFlag: MessagingFlag? { get }
    var offersPremierPlus: Bool { get }
    var cheapestRate: Rate? { get }

    func getDistanceString(formatter: LengthFormatter) -> String?
    func getLowestCost() -> Cost?
}

// MARK: - VenueCellViewModelDataProviding

extension Hotel: VenueCellViewModelDataProviding { }

// MARK: - Constants

extension VenueCellViewModelDataProviding {
    var employeeOfferRateCode: String {
        SimpleNetwork.Constants.EmployeeOffer.rateCode
    }
}
