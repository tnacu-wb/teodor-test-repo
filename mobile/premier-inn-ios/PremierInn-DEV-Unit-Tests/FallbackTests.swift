//
//  FallbackTests.swift
//  PremierInnTests
//
//  Created by Louis Faria-Softly on 07/10/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//


import Foundation
@testable import SimpleNetwork
import XCTest
@testable import PremierInn

class FallBackTest: XCTestCase {

    fileprivate var requestsManager: RequestsManager = RequestsManager()

    override func setUp() {
        super.setUp()
    }

    var fireBaseArray: [PIDictionary] {
        let dict = ["code":"MANOLD", "brand": "PI"]
        return [dict]
    }

    var operaFallbackDict = ["name": "London","lat": 51.512238,"long": -0.1059152,"isHotel": true, "hotelId":"MANOLD", "brand": "PI"] as [String : Any]
    var bartDict = ["name": "London","lat": 51.512238,"long": -0.1059152,"isHotel": true, "hotelId":"BANBRI"] as [String : Any]

    var user: User? {

        do {
            let user = try User(title: "mr", firstName: "justin", lastName: "pogg")
            return user
        } catch {
            XCTFail("\(error)")
            return nil
        }
    }

    var company: Company? {

        var requestCompanyDictionary: PIDictionary = [
            "companyDetails": [
                "companyName": "Pogg Inc"
            ]
        ]
        requestCompanyDictionary["allowCentralCreditCard"] = true

        guard let companyData = try? JSONSerialization.data(withJSONObject: requestCompanyDictionary, options: .prettyPrinted) else { return nil }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(Company.self, from: companyData)
        } catch {
            return nil
        }
    }

//    var migrationStatusMockedResponse = HotelType.Bart

    func testWithHotelInFirebase_operaRedirect_skipMigration_operaHotel(){
        // Scenario1a

        let remoteConfig = MockRemoteConfig(shouldOperaRedirectToWeb: true, skipHotelMigrationServiceCheck: true, operaHotels: fireBaseArray)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let suggestion = PISuggestion(dictionary: operaFallbackDict)
        requestsManager.loadResultsWith(suggestion: suggestion, sorting: AvailabilitiesSorting.price) { availabilitiesResponse, hotelAvailability, error, unavailableHotelCode in

            XCTAssert(error as? MigrationServiceError == MigrationServiceError.fallbackToBePresented)
        }
    }
    
    func testWithHotelInFirebase_operaRedirect_doNotskipMigration_operaHotel(){
        // Scenario3a

        let remoteConfig = MockRemoteConfig(shouldOperaRedirectToWeb: true, skipHotelMigrationServiceCheck: false, operaHotels: fireBaseArray)
//
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        let suggestion = PISuggestion(dictionary: operaFallbackDict)

        requestsManager.loadResultsWith(suggestion: suggestion, sorting: AvailabilitiesSorting.price) { availabilitiesResponse, hotelAvailability, error, unavailableHotelCode in

            XCTAssert(error as? MigrationServiceError == MigrationServiceError.fallbackToBePresented)
        }
    }

//    CAN NOT TEST FOLLOWING SCENARIO DUE TO ISSUE WITH MOCKING MIGRATION RESPONSE
//    func testWithHotelNotInFirebase_operaRedirect_doNotskipMigration_operaHotel(){
//        // Scenario4a
//
//        let remoteConfig = SettingsManagerTests.MockRemoteConfig(shouldOperaRedirectToWeb: true, skipHotelMigrationServiceCheck: false, operaHotels: fireBaseArray)
//        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
//
//        let suggestion = PISuggestion(dictionary: bartDict)
//
//        requestsManager.loadResultsWith(suggestion: suggestion, sorting: AvailabilitiesSorting.price) { availabilitiesResponse, hotelAvailability, error, unavailableHotelCode in
//
//            XCTAssert(error as! MigrationServiceError == MigrationServiceError.fallbackToBePresented)
//        }
//    }
}
