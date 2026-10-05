//
//  PreStayInteractorTests.swift
//  PremierInnTests
//
//  Created by Florin Velesca on 30.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class PreStayInteractorTests: XCTestCase {

    var interactor: PreStayInteractor!

    var mockAddress = try! Address(
        dictionary: [
            "addressline1": "Royal Victoria Dock",
            "addressline2": "2 Festoon Way",
            "addressline3": "London",
            "countryCode": "GB",
            "postcode": "E16 1SJ"
        ]
    )

    var stay: Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-09"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"
        dictionary["paymentOption"] = "CC"  // Explicitly set as Credit Card to prevent PIBA CNP detection
        return try! Stay(dictionary: dictionary)
    }

    override func setUp() {
        super.setUp()

        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams())
    }

    override func tearDown() {
        interactor = nil
        super.tearDown()
    }

    override class func setUp() {
        super.setUp()
        Country.countriesList = [
            makeCountry(code: "GB", name: "Great Britain"),
            makeCountry(code: "D", name: "Germany"),
            makeCountry(code: "USA", name: "United States of America"),
            makeCountry(code: "UAE", name: "United Arab Emirates")
        ]
    }

    override class func tearDown() {
        Country.countriesList = []
        super.tearDown()
    }
    
    func setupPrestayParams(
        accompanyingGuest: User? = nil,
        hotelBrand: HotelBrand? = nil,
        selectedPreference: HotelPreferenceViewModel? = nil,
        hasDERegCard: Bool = false,
        isBusinessTrip: Bool = false,
        isDirect: Bool = true,
        paymentActions: CiolPaymentActionsResponse? = nil
    ) -> PreStayInputParams {

        var roomDictionary = PIDictionary()
        roomDictionary["roomId"] = "123"
        roomDictionary["adults"] = 2
        roomDictionary["roomId"] = "XVCBS"

        let room1 = Room(dictionary: roomDictionary)
        let room2 = Room(dictionary: roomDictionary)

        room1.accompanyingGuest = accompanyingGuest
        room2.accompanyingGuest = accompanyingGuest
        room1.leadGuest = accompanyingGuest
        room2.leadGuest = accompanyingGuest

        return PreStayInputParams(
            flow: .myBookings,
            stay: stay,
            hotelCode: "LONEUS",
            hotelBrand: hotelBrand,
            reservationId: "324543",
            bookingFlowId: "3668429",
            arrivalDate: Date(),
            checkOutDate: Date(),
            adultsCountDescription: "",
            adultsCount: 3,
            childrenCountDescription: "",
            childrenCount: 0,
            nightsCountDescription: "",
            roomsCountDescription: "",
            rooms: [room1, room2],
            ciolPaymentActions: paymentActions,
            selectedPreference: selectedPreference,
            isBusinessTrip: isBusinessTrip,
            hasDERegCard: hasDERegCard,
            isDirect: isDirect
        )
    }
    
    private func setupRemoteConfig(featureCiolUpsells: Bool) {
        let remoteConfig = MockRemoteConfig(featureCIOLUpsells: featureCiolUpsells)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
    }

    func testAvailableWifiUpsellsCallsAvailableWifiUpsellsWhenHotelHasWifiUpsellAndNoBookedPackages() {
        var hotelWifiDictionary = PIDictionary()
        hotelWifiDictionary["operaId"] = UpsellItemOperaId.ultimateWifi24Hours.rawValue
        hotelWifiDictionary["legend"] = "Ultimate Wi-Fi"
        
        let hotelPackage = try! UpsellItem(dictionary: hotelWifiDictionary)
        
        // Act
        let availableWifiUpsells = CiolUpsellConfigurator.availableWifiUpsells(hotelPackages: [hotelPackage], bookedPackages: [], roomCount: 2).0

        // Assert
        XCTAssertEqual(availableWifiUpsells.count, 1)
    }
    
    func testAvailableWifiUpsellsCallsAvailableWifiUpsellsWhenHotelHasWifiUpsellAndBookedWifiPackages() {
        var wifiDictionary = PIDictionary()
        wifiDictionary["operaId"] = UpsellItemOperaId.ultimateWifi24Hours.rawValue
        wifiDictionary["legend"] = "Ultimate Wi-Fi"
        
        let wifiPackage = try! UpsellItem(dictionary: wifiDictionary)
        
        // Act
        let availableWifiUpsells = CiolUpsellConfigurator.availableWifiUpsells(hotelPackages: [wifiPackage], bookedPackages: [wifiPackage, wifiPackage], roomCount: 1).1

        // Assert
        XCTAssertEqual(availableWifiUpsells, false)
    }

    func testAvailableFoodUpsellsCallsAvailableFoodUpsellsWhenHotelHasContinentalAndPIBreakfastUpsellsAndNoBookedFoodPackages() {
        var continentalBreakfastDictionary = PIDictionary()
        continentalBreakfastDictionary["operaId"] = UpsellItemOperaId.continentalBreakfast.rawValue
        continentalBreakfastDictionary["legend"] = "Continental Breakfast"
        continentalBreakfastDictionary["foodUpsell"] = true
        
        var piBreakfastDictionary = PIDictionary()
        piBreakfastDictionary["operaId"] = UpsellItemOperaId.premierInnBreakfast.rawValue
        piBreakfastDictionary["legend"] = "Premier Inn Breakfast"
        piBreakfastDictionary["foodUpsell"] = true
        
        let continentalBreakfastPackage = try! UpsellItem(dictionary: continentalBreakfastDictionary)
        
        let piBreakfastPackage = try! UpsellItem(dictionary: piBreakfastDictionary)
        
        // Act
        let availableFoodUpsells = CiolUpsellConfigurator.availableFoodUpsells(hotelPackages: [continentalBreakfastPackage, piBreakfastPackage], bookedPackages: [], numberOfAdults: 2).0

        // Assert
        XCTAssertEqual(availableFoodUpsells.count, 2)
    }
    
    func testAvailableFoodUpsellsCallsAvailableFoodUpsellsWhenHotelHasContinentalAndPIBreakfastUpsellsAndOneBookedFoodPackages() {
        var continentalBreakfastDictionary = PIDictionary()
        continentalBreakfastDictionary["operaId"] = UpsellItemOperaId.continentalBreakfast.rawValue
        continentalBreakfastDictionary["legend"] = "Continental Breakfast"
        continentalBreakfastDictionary["foodUpsell"] = true
        
        var piBreakfastDictionary = PIDictionary()
        piBreakfastDictionary["operaId"] = UpsellItemOperaId.premierInnBreakfast.rawValue
        piBreakfastDictionary["legend"] = "Premier Inn Breakfast"
        piBreakfastDictionary["foodUpsell"] = true
        
        var piBreakfastBookedDictionary = PIDictionary()
        piBreakfastBookedDictionary["operaId"] = UpsellItemOperaId.premierInnBreakfast.rawValue
        piBreakfastBookedDictionary["legend"] = "Premier Inn Breakfast"
        piBreakfastBookedDictionary["quantity"] = 1
        piBreakfastBookedDictionary["foodUpsell"] = true
        
        let continentalBreakfastPackage = try! UpsellItem(dictionary: continentalBreakfastDictionary)
        
        let piBreakfastPackage = try! UpsellItem(dictionary: piBreakfastDictionary)
        
        let piBreakfastBookedPackage = try! UpsellItem(dictionary: piBreakfastBookedDictionary)
        
        // Act
        let availableFoodUpsells = CiolUpsellConfigurator.availableFoodUpsells(hotelPackages: [continentalBreakfastPackage, piBreakfastPackage], bookedPackages: [piBreakfastBookedPackage], numberOfAdults: 2).0

        // Assert
        XCTAssertEqual(availableFoodUpsells.count, 2)
    }
    
    func testAvailableFoodUpsellsCallsAvailableFoodUpsellsWhenHotelHasContinentalAndPIBreakfastUpsellsAndFullyBookedFoodPackagesForThreePersons() {
        var continentalBreakfastDictionary = PIDictionary()
        continentalBreakfastDictionary["operaId"] = UpsellItemOperaId.continentalBreakfast.rawValue
        continentalBreakfastDictionary["legend"] = "Continental Breakfast"
        continentalBreakfastDictionary["foodUpsell"] = true
        
        var piBreakfastDictionary = PIDictionary()
        piBreakfastDictionary["operaId"] = UpsellItemOperaId.premierInnBreakfast.rawValue
        piBreakfastDictionary["legend"] = "Premier Inn Breakfast"
        piBreakfastDictionary["foodUpsell"] = true
        
        var piBreakfastBookedDictionary = PIDictionary()
        piBreakfastBookedDictionary["operaId"] = UpsellItemOperaId.premierInnBreakfast.rawValue
        piBreakfastBookedDictionary["legend"] = "Premier Inn Breakfast"
        piBreakfastBookedDictionary["quantity"] = 1
        piBreakfastBookedDictionary["foodUpsell"] = true
        
        var mealDealBookedDictionary = PIDictionary()
        mealDealBookedDictionary["operaId"] = UpsellItemOperaId.mealDeal.rawValue
        mealDealBookedDictionary["legend"] = "Premier Inn Breakfast"
        mealDealBookedDictionary["quantity"] = 2
        mealDealBookedDictionary["foodUpsell"] = true
        
        let continentalBreakfastPackage = try! UpsellItem(dictionary: continentalBreakfastDictionary)
        
        let piBreakfastPackage = try! UpsellItem(dictionary: piBreakfastDictionary)
        let piBreakfastBookedPackage = try! UpsellItem(dictionary: piBreakfastBookedDictionary)
        let mealDealBookedPackage = try! UpsellItem(dictionary: mealDealBookedDictionary)
        
        // Act
        let availableFoodUpsells = CiolUpsellConfigurator.availableFoodUpsells(hotelPackages: [continentalBreakfastPackage, piBreakfastPackage], bookedPackages: [piBreakfastBookedPackage, mealDealBookedPackage], numberOfAdults: 2).0

        // Assert
        XCTAssertEqual(availableFoodUpsells.count, 2)
    }
    
    func testAvailableExtraUpsellsCallsAvailableEciLcoUpsellsWhenHotelHasEciAndLcoUpsellAndNoBookedExtraPackages() {
        var eciDictionary = PIDictionary()
        eciDictionary["operaId"] = UpsellItemOperaId.earlyCheckIn.rawValue
        eciDictionary["legend"] = "Early Check In"
        eciDictionary["isExtraUpsell"] = true
        
        var lcoDictionary = PIDictionary()
        lcoDictionary["operaId"] = UpsellItemOperaId.lateCheckOut.rawValue
        lcoDictionary["legend"] = "Late Check Out"
        lcoDictionary["isExtraUpsell"] = true

        let eciPackage = try! UpsellItem(dictionary: eciDictionary)
        let lcoPackage = try! UpsellItem(dictionary: lcoDictionary)

        // Act
        let availableExtraUpsells = CiolUpsellConfigurator.availableEciLcoUpsells(hotelPackages: [eciPackage, lcoPackage], bookedPackages: []).1

        // Assert
        XCTAssertEqual(availableExtraUpsells, false)
    }
    
    func testAvailableExtraUpsellsCallsAvailableEciLcoUpsellsWhenHotelHasEciAndLcoUpsellAndEciBookedExtraPackages() {
        var eciDictionary = PIDictionary()
        eciDictionary["operaId"] = UpsellItemOperaId.earlyCheckIn.rawValue
        eciDictionary["legend"] = "Early Check In"
        eciDictionary["isExtraUpsell"] = true
        
        var lcoDictionary = PIDictionary()
        lcoDictionary["operaId"] = UpsellItemOperaId.lateCheckOut.rawValue
        lcoDictionary["legend"] = "Late Check Out"
        lcoDictionary["isExtraUpsell"] = true

        let eciPackage = try! UpsellItem(dictionary: eciDictionary)
        let eciBookedPackage = try! UpsellItem(dictionary: eciDictionary)
        let lcoPackage = try! UpsellItem(dictionary: lcoDictionary)

        // Act
        let availableExtraUpsells = CiolUpsellConfigurator.availableEciLcoUpsells(hotelPackages: [eciPackage, lcoPackage], bookedPackages: [eciBookedPackage]).1

        // Assert
        XCTAssertEqual(availableExtraUpsells, false)
    }
    
    
    func testAvailableExtraUpsellsCallsAvailableEciLcoUpsellsWhenHotelHasEciAndLcoUpsellAndLcoBookedExtraPackages() {
        var eciDictionary = PIDictionary()
        eciDictionary["operaId"] = UpsellItemOperaId.earlyCheckIn.rawValue
        eciDictionary["legend"] = "Early Check In"
        eciDictionary["isExtraUpsell"] = true
        
        var lcoDictionary = PIDictionary()
        lcoDictionary["operaId"] = UpsellItemOperaId.lateCheckOut.rawValue
        lcoDictionary["legend"] = "Late Check Out"
        lcoDictionary["isExtraUpsell"] = true

        let eciPackage = try! UpsellItem(dictionary: eciDictionary)
        let lcoPackage = try! UpsellItem(dictionary: lcoDictionary)
        let lcoBookedPackage = try! UpsellItem(dictionary: lcoDictionary)

        // Act
        let availableExtraUpsells = CiolUpsellConfigurator.availableEciLcoUpsells(hotelPackages: [eciPackage, lcoPackage], bookedPackages: [lcoBookedPackage]).1

        // Assert
        XCTAssertEqual(availableExtraUpsells, false)
    }
    
    func testAvailableExtraUpsellsCallsAvailableEciLcoUpsellsWhenHotelHasEciAndLcoUpsellAndAllBookedExtraPackages() {
        var eciDictionary = PIDictionary()
        eciDictionary["operaId"] = UpsellItemOperaId.earlyCheckIn.rawValue
        eciDictionary["legend"] = "Early Check In"
        eciDictionary["isExtraUpsell"] = true
        
        var lcoDictionary = PIDictionary()
        lcoDictionary["operaId"] = UpsellItemOperaId.lateCheckOut.rawValue
        lcoDictionary["legend"] = "Late Check Out"
        lcoDictionary["isExtraUpsell"] = true

        let eciPackage = try! UpsellItem(dictionary: eciDictionary)
        let lcoPackage = try! UpsellItem(dictionary: lcoDictionary)
        
        let eciBookedPackage = try! UpsellItem(dictionary: eciDictionary)
        let lcoBookedPackage = try! UpsellItem(dictionary: lcoDictionary)

        // Act
        let availableExtraUpsells = CiolUpsellConfigurator.availableEciLcoUpsells(hotelPackages: [eciPackage, lcoPackage], bookedPackages: [eciBookedPackage, lcoBookedPackage]).1

        // Assert
        XCTAssertEqual(availableExtraUpsells, true)
    }
    
    func testAvailableExtraUpsellssCallsAvailableEciLcoUpsellsWhenHotelHasEciUpsellAndNoBookedExtraPackages() {
        var eciDictionary = PIDictionary()
        eciDictionary["operaId"] = UpsellItemOperaId.earlyCheckIn.rawValue
        eciDictionary["legend"] = "Early Check In"
        eciDictionary["isExtraUpsell"] = true

        let eciPackage = try! UpsellItem(dictionary: eciDictionary)

        // Act
        let availableExtraUpsells = CiolUpsellConfigurator.availableEciLcoUpsells(hotelPackages: [eciPackage], bookedPackages: []).0

        // Assert
        XCTAssertEqual(availableExtraUpsells.count, 1)
    }
    
    func testAvailableExtraUpsellsCallsAvailableEciLcoUpsellsWhenHotelHasEciUpsellAndEciBookedExtraPackages() {
        var eciDictionary = PIDictionary()
        eciDictionary["operaId"] = UpsellItemOperaId.earlyCheckIn.rawValue
        eciDictionary["legend"] = "Early Check In"
        eciDictionary["isExtraUpsell"] = true
        
        var lcoDictionary = PIDictionary()
        lcoDictionary["operaId"] = UpsellItemOperaId.lateCheckOut.rawValue
        lcoDictionary["legend"] = "Late Check Out"
        lcoDictionary["isExtraUpsell"] = true

        let eciPackage = try! UpsellItem(dictionary: eciDictionary)
        let eciBookedPackage = try! UpsellItem(dictionary: eciDictionary)

        // Act
        let availableExtraUpsells = CiolUpsellConfigurator.availableEciLcoUpsells(hotelPackages: [eciPackage], bookedPackages: [eciBookedPackage]).1

        // Assert
        XCTAssertEqual(availableExtraUpsells, true)
    }
    
    func testAvailableExtraUpsellsCallsAvailableEciLcoUpsellsWhenHotelHasLcoUpsellAndNoBookedExtraPackages() {
        var lcoDictionary = PIDictionary()
        lcoDictionary["operaId"] = UpsellItemOperaId.lateCheckOut.rawValue
        lcoDictionary["legend"] = "Late Check Out"
        lcoDictionary["isExtraUpsell"] = true

        let lcoPackage = try! UpsellItem(dictionary: lcoDictionary)

        // Act
        let availableExtraUpsells = CiolUpsellConfigurator.availableEciLcoUpsells(hotelPackages: [lcoPackage], bookedPackages: []).0

        // Assert
        XCTAssertEqual(availableExtraUpsells.count, 1)
    }
    
    func testAvailableExtraUpsellsCallsAvailableEciLcoUpsellsWhenHotelHasLcoUpsellAndLcoBookedExtraPackages() {
        var lcoDictionary = PIDictionary()
        lcoDictionary["operaId"] = UpsellItemOperaId.lateCheckOut.rawValue
        lcoDictionary["legend"] = "Late Check Out"
        lcoDictionary["isExtraUpsell"] = true

        let lcoPackage = try! UpsellItem(dictionary: lcoDictionary)
        let lcoBookedPackage = try! UpsellItem(dictionary: lcoDictionary)

        // Act
        let availableExtraUpsells = CiolUpsellConfigurator.availableEciLcoUpsells(hotelPackages: [lcoPackage], bookedPackages: [lcoBookedPackage]).1

        // Assert
        XCTAssertEqual(availableExtraUpsells, true)
    }
    
    func testIsAllGuestDataCompleteFalse() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(hotelBrand: .premierInn))
        XCTAssertFalse(interactor.isAllGuestDataComplete())
    }

    func testIsAllGuestDataCompleteRegCard() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(hotelBrand: .premierInnGermany))
        XCTAssertTrue(interactor.isAllGuestDataComplete())
    }

    func testIsAllGuestDataCompleteTrue() {
        let country = Country(code: "GB",
                              name: "United Kingdom",
                              isoCode: "GB",
                              dialingCode: "023",
                              flagImage: "🇬🇧",
                              passportRequired: false,
                              nationality: "British")
        let user = try? User(title: "Mr", firstName: "Second", lastName: "Guest")
        user?.country = country
        
        var params = setupPrestayParams(accompanyingGuest: user, hotelBrand: .premierInn)
        // Mark guests as confirmed (simulates user confirming guest details in UI)
        params.confirmedLeadGuestRows = [0, 1]
        params.confirmedSecondGuestRows = [0, 1]
        
        interactor = PreStayInteractor(preStayInputParams: params)
        XCTAssertTrue(interactor.isAllGuestDataComplete())
    }
    
    func testUpdateIsSpecialOccasionOnTrue() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(selectedPreference: HotelPreferenceViewModel(name: "",
                                                                                                                           code: "")))
        interactor.updateSpecialOccasion(true)

        XCTAssertTrue(interactor.viewModel.isSpecialOccasionOn)
    }
    
    func testUpdateIsSpecialOccasionOnFalse() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(selectedPreference: HotelPreferenceViewModel(name: "",
                                                                                                                           code: "")))
        interactor.updateSpecialOccasion(false)
        
        XCTAssertFalse(interactor.viewModel.isSpecialOccasionOn)
    }
    
    func testShouldShowUpsellsFeatureFlagTrueAndNotBusinessTripTrue() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(isBusinessTrip: false))
        setupRemoteConfig(featureCiolUpsells: true)
        
        XCTAssertTrue(interactor.shouldShowUpsells)
    }
    
    func testShouldShowUpsellsFeatureFlagTrueAndBusinessTripFalse() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(isBusinessTrip: true))
        setupRemoteConfig(featureCiolUpsells: true)
        
        XCTAssertFalse(interactor.shouldShowUpsells)
    }
    
    func testShouldShowUpsellsFeatureFlagFalseAndNotBusinessTripFalse() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(isBusinessTrip: false))
        setupRemoteConfig(featureCiolUpsells: false)
        
        XCTAssertFalse(interactor.shouldShowUpsells)
    }
    
    func testShouldShowUpsellsFeatureFlagFalseAndBusinessTripFalse() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(isBusinessTrip: true))
        setupRemoteConfig(featureCiolUpsells: false)
        
        XCTAssertFalse(interactor.shouldShowUpsells)
    }

    // MARK: - Non-direct / third party tests

    func testNonDirectThirdPartyBookingsShouldShowUpsellsFalse() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(isDirect: false))
        setupRemoteConfig(featureCiolUpsells: true)

        XCTAssertFalse(interactor.shouldShowUpsells)
    }

    func testDirectBookingsShouldShowUpsellsTrue() {
        interactor = PreStayInteractor(preStayInputParams: setupPrestayParams(isDirect: true))
        setupRemoteConfig(featureCiolUpsells: true)

        XCTAssertTrue(interactor.shouldShowUpsells)
    }

    func testIsAllGuestDataCompleteNonDirectMissingBookerDetailsReturnsFalse() {
        let validLead = makeGuest()
        let validSecond = makeGuest()

        let mockInput =  PreStayInputParams.createDetailedMock(
                leadGuest: validLead,
                accompanyingGuest: validSecond,
                hotelBrand: .premierInn,
                isDirect: false,
                email: nil,
                contactNumber: nil,
                address: nil
            )

        interactor = PreStayInteractor(preStayInputParams: mockInput)
        XCTAssertFalse(interactor.isAllGuestDataComplete())
    }

    func testIsAllGuestDataCompleteNonDirectValidBookerDetailsButMissingLeadGuestNationalityReturnsFalse() {
        let invalidLead = try! User(title: "Mr", firstName: "John", lastName: "Smith")
        invalidLead.country = nil

        let validSecond = makeGuest()
        let mockInput = PreStayInputParams.createDetailedMock(
            leadGuest: invalidLead,
            accompanyingGuest: validSecond,
            hotelBrand: .premierInn,
            isDirect: false,
            email: "test@example.com",
            contactNumber: "07123456789",
            address: mockAddress
        )

        interactor = PreStayInteractor(preStayInputParams: mockInput)
        XCTAssertFalse(interactor.isAllGuestDataComplete())
    }

    func testIsAllGuestDataCompleteDirectGermanBookingSkipsValidationAndReturnsTrue() {
        let invalidLead = try! User(title: "Mr", firstName: "John", lastName: "Smith")
        invalidLead.country = nil

        let invalidSecond = try! User(title: "Mrs", firstName: "Jane", lastName: "Smith")
        invalidSecond.country = nil

        let mockInput = PreStayInputParams.createDetailedMock(
            leadGuest: invalidLead,
            accompanyingGuest: invalidSecond,
            hotelBrand: .premierInnGermany,
            isDirect: true,
            email: nil,
            contactNumber: nil,
            address: nil
        )

        interactor = PreStayInteractor(preStayInputParams: mockInput)

        XCTAssertTrue(interactor.isAllGuestDataComplete())
    }

    func testIsAllGuestDataCompleteNonDirectGermanBookingDoesNotSkipValidationAndReturnsFalseWhenGuestInvalid() {
        let invalidLead = try! User(title: "Mr", firstName: "John", lastName: "Smith")
        invalidLead.country = nil

        let mockInput = PreStayInputParams.createDetailedMock(
            leadGuest: invalidLead,
            accompanyingGuest: nil,
            hotelBrand: .premierInnGermany,
            isDirect: false,
            email: "test@example.com",
            contactNumber: "07123456789",
            address: mockAddress
        )

        interactor = PreStayInteractor(preStayInputParams: mockInput)

        XCTAssertFalse(interactor.isAllGuestDataComplete())
    }

    func testIsAllGuestDataCompleteNonDirectGermanBookingReturnsFalseWhenThirdPartyDetailsMissing() {
        let validLead = makeGuest(passportNumber: "123456789")

        let mockInput = PreStayInputParams.createDetailedMock(
            leadGuest: validLead,
            accompanyingGuest: nil,
            hotelBrand: .premierInnGermany,
            isDirect: false,
            email: nil,
            contactNumber: "07123456789",
            address: mockAddress
        )

        interactor = PreStayInteractor(preStayInputParams: mockInput)
        XCTAssertFalse(interactor.isAllGuestDataComplete())
    }

    func testUpdateViewModelValidateGuestDetailsKeepsSurfaceErrorsTrueWhenAnotherGuestStillNeedsConfirmation() {
        let invalidLead = try! User(title: "Mr", firstName: "John", lastName: "Smith")
        invalidLead.country = nil

        let invalidSecond = try! User(title: "Mrs", firstName: "Jane", lastName: "Smith")
        invalidSecond.country = nil

        let mockInput = PreStayInputParams.createDetailedMock(
            leadGuest: invalidLead,
            accompanyingGuest: invalidSecond,
            hotelBrand: .premierInn,
            isDirect: false,
            email: "test@example.com",
            contactNumber: "07123456789",
            address: mockAddress
        )

        interactor = PreStayInteractor(preStayInputParams: mockInput)

        let editModel = EditDetailsModel(
            flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: "Mr",
            firstName: "John",
            lastName: "Smith",
            country: CountryItem(country: Self.makeCountry()),
            passportNumber: nil,
            hotelBrand: .premierInn
        )

        interactor.updateViewModel(with: editModel)

        XCTAssertTrue(interactor.viewModel.shouldSurfaceErrorMessages)
    }

    func testUpdateViewModelValidateGuestDetailsSetsSurfaceErrorsFalseWhenNoGuestsNeedConfirmation() {
        let invalidLead = try! User(title: "Mr", firstName: "John", lastName: "Smith")
        invalidLead.country = nil

        let invalidSecond = try! User(title: "Mrs", firstName: "Jane", lastName: "Smith")
        invalidSecond.country = nil

        let mockInput = PreStayInputParams.createDetailedMock(
            leadGuest: invalidLead,
            accompanyingGuest: invalidSecond,
            hotelBrand: .premierInn,
            isDirect: false,
            email: "test@example.com",
            contactNumber: "07123456789",
            address: mockAddress
        )

        interactor = PreStayInteractor(preStayInputParams: mockInput)

        let leadEditModel = EditDetailsModel(
            flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: "Mr",
            firstName: "John",
            lastName: "Smith",
            country: CountryItem(country: Self.makeCountry()),
            passportNumber: nil,
            hotelBrand: .premierInn
        )

        let secondEditModel = EditDetailsModel(
            flow: .secondGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: "Mrs",
            firstName: "Jane",
            lastName: "Smith",
            country: CountryItem(country: Self.makeCountry()),
            passportNumber: nil,
            hotelBrand: .premierInn
        )

        interactor.updateViewModel(with: leadEditModel)
        interactor.updateViewModel(with: secondEditModel)

        XCTAssertFalse(interactor.viewModel.shouldSurfaceErrorMessages)
    }

    func testUpdateViewModelValidateGuestDetailsSetsSurfaceErrorsTrueWhenLeadGuestNeedsConfirmation() {
        let invalidLead = try! User(title: "Mr", firstName: "John", lastName: "Smith")
        invalidLead.country = nil

        let validSecond = makeGuest()

        let mockInput = PreStayInputParams.createDetailedMock(
            leadGuest: invalidLead,
            accompanyingGuest: validSecond,
            hotelBrand: .premierInn,
            isDirect: false,
            email: "test@example.com",
            contactNumber: "07123456789",
            address: mockAddress
        )

        interactor = PreStayInteractor(preStayInputParams: mockInput)

        let secondEditModel = EditDetailsModel(
            flow: .secondGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: validSecond.title,
            firstName: validSecond.firstName,
            lastName: validSecond.lastName,
            country: CountryItem(country: Self.makeCountry()),
            passportNumber: validSecond.passport?.number,
            hotelBrand: .premierInn
        )

        interactor.updateViewModel(with: secondEditModel)

        XCTAssertTrue(interactor.viewModel.shouldSurfaceErrorMessages)
    }

    func testUpdateViewModelValidateGuestDetailsSetsSurfaceErrorsTrueWhenSecondGuestNeedsConfirmation() {
        let validLead = makeGuest()

        let invalidSecond = try! User(title: "Mrs", firstName: "Jane", lastName: "Smith")
        invalidSecond.country = nil

        let mockInput = PreStayInputParams.createDetailedMock(
            leadGuest: validLead,
            accompanyingGuest: invalidSecond,
            hotelBrand: .premierInn,
            isDirect: false,
            email: "test@example.com",
            contactNumber: "07123456789",
            address: mockAddress
        )

        interactor = PreStayInteractor(preStayInputParams: mockInput)

        let leadEditModel = EditDetailsModel(
            flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: validLead.title,
            firstName: validLead.firstName,
            lastName: validLead.lastName,
            country: CountryItem(country: Self.makeCountry()),
            passportNumber: validLead.passport?.number,
            hotelBrand: .premierInn
        )

        interactor.updateViewModel(with: leadEditModel)

        XCTAssertTrue(interactor.viewModel.shouldSurfaceErrorMessages)
    }

    func testPriceBreakdownViewModelWhenPIBACNPShowsPaidByCompanyLabel() {
        let pibaCnpStay = makeStay(paymentOption: "PIBA_CNP", totalCost: 100.0)
        interactor = PreStayInteractor(preStayInputParams: makePreStayParams(stay: pibaCnpStay))

        let viewModel = interactor.priceBreakdownViewModel

        XCTAssertEqual(viewModel.totalValue, "£0.00")
        XCTAssertEqual(viewModel.items.count, 1)
        XCTAssertEqual(viewModel.items.first?.name, PILocalizedString("ciolPriceBreakdownPaidByCompany"))
    }

    func testPriceBreakdownViewModelWhenPIBACPShowsPaidByYouLabel() {
        let pibaCpStay = makeStay(paymentOption: "PIBA_CP", balanceOutstanding: 341.0)
        let params = makePreStayParams(stay: pibaCpStay, outstandingBalance: Cost(amount: 341.0, currencyCode: "GBP"))
        interactor = PreStayInteractor(preStayInputParams: params)

        let viewModel = interactor.priceBreakdownViewModel

        XCTAssertEqual(viewModel.totalValue, "£341.00")
        XCTAssertEqual(viewModel.items.count, 1)
        XCTAssertEqual(viewModel.items.first?.name, PILocalizedString("ciolPriceBreakdownPaidByYou"))
    }

    func testPriceBreakdownViewModelWhenStandardPaymentShowsOutstandingBalanceLabel() {
        let standardStay = makeStay(paymentOption: "CC", balanceOutstanding: 70.0)
        let params = makePreStayParams(stay: standardStay, outstandingBalance: Cost(amount: 70.0, currencyCode: "GBP"))
        interactor = PreStayInteractor(preStayInputParams: params)

        let viewModel = interactor.priceBreakdownViewModel

        XCTAssertEqual(viewModel.totalValue, "£70.00")
        XCTAssertEqual(viewModel.items.count, 1)
        XCTAssertEqual(viewModel.items.first?.name, PILocalizedString("preStayOutstandingBalance"))
    }
}

extension PreStayInteractorTests {
    static func makeCountry(
        code: String = "GB",
        name: String = "United Kingdom",
        isoCode: String = "GB",
        dialingCode: String = "023",
        flagImage: String = "🇬🇧",
        passportRequired: Bool = false,
        nationality: String? = "British"
    ) -> Country {
        Country(
            code: code,
            name: name,
            isoCode: isoCode,
            dialingCode: dialingCode,
            flagImage: flagImage,
            passportRequired: passportRequired,
            nationality: nationality
        )
    }

    func makeGuest(
        title: String = "Mr",
        firstName: String = "John",
        lastName: String = "Smith",
        passportRequired: Bool = false,
        passportNumber: String? = nil
    ) -> User {
        let user = try! User(title: title, firstName: firstName, lastName: lastName)
        user.country = Self.makeCountry(passportRequired: passportRequired)

        if let passportNumber {
            user.passport = Passport(number: passportNumber, countryOfIssue: "GB")
        }

        return user
    }

    func makeStay(
        paymentOption: String,
        totalCost: Double? = nil,
        balanceOutstanding: Double? = nil
    ) -> Stay {
        var stayDict = PIDictionary()
        stayDict["hotelCode"] = "LONBLA"
        stayDict["hotelName"] = "Test Hotel"
        stayDict["hotelLatitude"] = 50.823071
        stayDict["hotelLongitude"] = -0.140976
        stayDict["lastName"] = "APPS"
        stayDict["identifier"] = "TEST123"
        stayDict["arrivalDate"] = "2024-06-10"
        stayDict["checkOutDate"] = "2024-06-11"
        stayDict["paymentOption"] = paymentOption

        if let totalCost = totalCost {
            stayDict["totalCost"] = ["amount": totalCost, "currency": "GBP"]
        }

        if let balanceOutstanding = balanceOutstanding {
            stayDict["balanceOutstanding"] = ["amount": balanceOutstanding, "currency": "GBP"]
        }

        return try! Stay(dictionary: stayDict)
    }

    func makeAddress(
        line1: String = ""
    ) -> Address {
        try! Address(
            dictionary: [
                "addressline1": "Royal Victoria Dock",
                "addressline2": "2 Festoon Way",
                "addressline3": "London",
                "countryCode": "GB",
                "postcode": "E16 1SJ"
            ]
        )
    }

    func makePreStayParams(
        stay: Stay,
        outstandingBalance: Cost? = nil,
        isDirect: Bool = true,
        address: Address? = nil
    ) -> PreStayInputParams {
        PreStayInputParams(
            flow: .myBookings,
            stay: stay,
            hotelCode: "LONBLA",
            hotelBrand: .premierInn,
            reservationId: "TEST123",
            bookingFlowId: "12345",
            arrivalDate: Date(),
            checkOutDate: Date(),
            adultsCountDescription: "2 adults",
            adultsCount: 2,
            childrenCountDescription: "",
            childrenCount: 0,
            nightsCountDescription: "1 night",
            roomsCountDescription: "1 room",
            rooms: [],
            address: address,
            outstandingBalance: outstandingBalance,
            selectedPreference: nil,
            isBusinessTrip: false,
            hasDERegCard: false,
            isDirect: isDirect
        )
    }
}
