//
//  BookingConfirmationViewModelKeyLimitTests.swift
//  PremierInn-DEV-Unit-Tests
//
//  Created by Cascade on 30/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
import UIKit
import SimpleNetwork
@testable import PremierInn

final class BookingConfirmationViewModelKeyLimitTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
    }
    
    override func tearDown() {
        super.tearDown()
    }
    
    // MARK: - Helper Methods
    
    private func createMockHotel(brand: HotelBrand) -> Hotel {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "BERLNR"
        dictionary["hotelName"] = "Premier Inn Berlin"
        dictionary["hotelLatitude"] = 52.520008
        dictionary["hotelLongitude"] = 13.404954
        dictionary["brand"] = brand.rawValue
        dictionary["address"] = [
            "addressLine1": "Test Street 1",
            "city": "Berlin",
            "postcode": "10115",
            "country": "Germany"
        ]
        
        return try! Hotel(dictionary: dictionary)
    }
    
    func createMockStay(
        isDigitalKeyEnabled: Bool,
        userHasPassInWallet: Bool,
        hotelCode: String = "BERLNR"
    ) -> Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = hotelCode
        dictionary["hotelName"] = "Premier Inn Berlin"
        dictionary["hotelLatitude"] = 52.520008
        dictionary["hotelLongitude"] = 13.404954
        dictionary["lastName"] = "TestUser"
        dictionary["identifier"] = "TEST123456"
        dictionary["arrivalDate"] = "2026-07-01"
        dictionary["checkOutDate"] = "2026-07-03"
        dictionary["isDigitalKey"] = isDigitalKeyEnabled
        dictionary["basketStatus"] = "PRE_CHECKED_IN"
        
        // Set digitalKeyIdentifier if we want the pass in wallet
        if userHasPassInWallet {
            dictionary["digitalKeyIdentifier"] = "TEST_KEY_ID_123"
        }
        
        let stay = try! Stay(dictionary: dictionary)
        
        // Mock the keyManager to return the desired value
        if userHasPassInWallet {
            stay.keyManager = MockPassManager(isInPassManager: true)
        }
        
        return stay
    }
    
    func createViewModelParams(
        hotel: Hotel,
        stay: Stay
    ) -> BookingConfirmationViewModelParams {
        return BookingConfirmationViewModelParams(
            hotel,
            summary: stay,
            reservation: nil,
            bookedUpsells: nil,
            isCheckInOnlineAvailable: false,
            isCheckOutOnlineAvailable: false,
            shouldShowBannerInfo: false,
            shouldShowInfo: false,
            passSaved: false,
            isAmended: false,
            isOutOfDate: false,
            resendInvoiceStatus: .notRequested
        )
    }
    
    // MARK: - Tests: shouldShowKeyLimitMessage Logic
    
    func testKeyLimitMessageShouldShowWhenAllConditionsMet() {
        // Skip test if not running on iPhone (digital keys are phone-only)
        guard UIDevice.current.userInterfaceIdiom == .phone else {
            return
        }
        
        // Given: Enable digital keys feature flag
        let remoteConfig = MockRemoteConfig(featureDigitalKeys: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        
        // Given: German hotel, digital keys enabled, pass in wallet
        let hotel = createMockHotel(brand: .premierInnGermany)
        let stay = createMockStay(
            isDigitalKeyEnabled: true,
            userHasPassInWallet: true
        )
        let params = createViewModelParams(hotel: hotel, stay: stay)
        
        // When
        let viewModel = BookingConfirmationViewModel.createFrom(params)
        
        // Then
        XCTAssertNotNil(viewModel)
        XCTAssertTrue(viewModel!.shouldShowKeyLimitMessage, "Key limit message should be shown for German ASSA hotels with digital keys enabled and pass in wallet")
    }
    
    func testKeyLimitMessageShouldNotShowWhenDigitalKeysDisabled() {
        // Given: German hotel, digital keys DISABLED, pass in wallet
        let hotel = createMockHotel(brand: .premierInnGermany)
        let stay = createMockStay(
            isDigitalKeyEnabled: false,
            userHasPassInWallet: true
        )
        let params = createViewModelParams(hotel: hotel, stay: stay)
        
        // When
        let viewModel = BookingConfirmationViewModel.createFrom(params)
        
        // Then
        XCTAssertNotNil(viewModel)
        XCTAssertFalse(viewModel!.shouldShowKeyLimitMessage, "Key limit message should NOT be shown when digital keys are disabled")
    }
    
    func testKeyLimitMessageShouldNotShowWhenNoPassInWallet() {
        // Given: German hotel, digital keys enabled, NO pass in wallet
        let hotel = createMockHotel(brand: .premierInnGermany)
        let stay = createMockStay(
            isDigitalKeyEnabled: true,
            userHasPassInWallet: false
        )
        let params = createViewModelParams(hotel: hotel, stay: stay)
        
        // When
        let viewModel = BookingConfirmationViewModel.createFrom(params)
        
        // Then
        XCTAssertNotNil(viewModel)
        XCTAssertFalse(viewModel!.shouldShowKeyLimitMessage, "Key limit message should NOT be shown when user has no pass in wallet")
    }
    
    func testKeyLimitMessageShouldNotShowForUKHotel() {
        // Given: UK hotel (Premier Inn), digital keys enabled, pass in wallet
        let ukHotel = createMockHotel(brand: .premierInn)
        let stay = createMockStay(
            isDigitalKeyEnabled: true,
            userHasPassInWallet: true,
            hotelCode: "LONBLA"
        )
        let params = createViewModelParams(hotel: ukHotel, stay: stay)
        
        // When
        let viewModel = BookingConfirmationViewModel.createFrom(params)
        
        // Then
        XCTAssertNotNil(viewModel)
        XCTAssertFalse(viewModel!.shouldShowKeyLimitMessage, "Key limit message should NOT be shown for UK Premier Inn hotels")
    }
    
    func testKeyLimitMessageShouldNotShowForHubHotel() {
        // Given: Hub hotel, digital keys enabled, pass in wallet
        let hubHotel = createMockHotel(brand: .hub)
        let stay = createMockStay(
            isDigitalKeyEnabled: true,
            userHasPassInWallet: true,
            hotelCode: "HUBLON"
        )
        let params = createViewModelParams(hotel: hubHotel, stay: stay)
        
        // When
        let viewModel = BookingConfirmationViewModel.createFrom(params)
        
        // Then
        XCTAssertNotNil(viewModel)
        XCTAssertFalse(viewModel!.shouldShowKeyLimitMessage, "Key limit message should NOT be shown for Hub hotels")
    }
    
    func testKeyLimitMessageShouldNotShowForZipHotel() {
        // Given: Zip hotel, digital keys enabled, pass in wallet
        let zipHotel = createMockHotel(brand: .zip)
        let stay = createMockStay(
            isDigitalKeyEnabled: true,
            userHasPassInWallet: true,
            hotelCode: "ZIPLON"
        )
        let params = createViewModelParams(hotel: zipHotel, stay: stay)
        
        // When
        let viewModel = BookingConfirmationViewModel.createFrom(params)
        
        // Then
        XCTAssertNotNil(viewModel)
        XCTAssertFalse(viewModel!.shouldShowKeyLimitMessage, "Key limit message should NOT be shown for Zip hotels")
    }
    
    func testKeyLimitMessageShouldNotShowWhenMultipleConditionsFail() {
        // Given: German hotel, digital keys DISABLED, NO pass in wallet
        let hotel = createMockHotel(brand: .premierInnGermany)
        let stay = createMockStay(
            isDigitalKeyEnabled: false,
            userHasPassInWallet: false
        )
        let params = createViewModelParams(hotel: hotel, stay: stay)
        
        // When
        let viewModel = BookingConfirmationViewModel.createFrom(params)
        
        // Then
        XCTAssertNotNil(viewModel)
        XCTAssertFalse(viewModel!.shouldShowKeyLimitMessage, "Key limit message should NOT be shown when multiple conditions fail")
    }
    
}
