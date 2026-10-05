//
//  AmendBookingInteractorTests.swift
//  PremierInn UnitTests
//
//  Created by Filippo Minelle on 20/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

class MockAmendBookingPresenter: AmendBookingInteractorDelegate {
    
    // MARK: - AmendBookingInteractorDelegate
    
    func interactorIsBusy() { }
    
    func failed(with errorMessage: String, goBack: Bool) { }

    func finishedLoadingRequireData(isNewRoomAdded: Bool) { }
    
}

class AmendBookingInteractorTests: XCTestCase {
    
    // MARK: - Properties

    func mealDealUpsell(quantity: Int) -> UpsellItem {
        return try! UpsellItem(dictionary: [
        "operaId": "MDP",
        "code": "17",
        "legend": "Meal Deal",
        "foodUpsell": true,
        "freeBreakfastTrigger": true,
        "quantity": quantity,
        "roomId": "2272970",
        "freeBreakfastCode": "BFCHDF",
        "freeBreakfastMaxPerMeal": 1,
        "price": ["amount": "26.49",
                  "currency": "GBP"],
        "unitCost": ["amount": "26.49",
                     "currency": "GBP"]
    ])
    }

    private var stay: Stay {
        
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
        
        return try! Stay(dictionary: dictionary)
    }

    private var hotel: Hotel? {
        guard let hotelFileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json") else { return nil }
        guard let hotelData = try? Data(contentsOf: hotelFileURL) else { return nil }
        guard let hotelJSON = try? JSONSerialization.jsonObject(with: hotelData, options: .allowFragments) as? PIDictionary else { return nil }
        guard let hotel = try? Hotel(dictionary: hotelJSON) else { return nil }

        return hotel
    }

    private var presenter: MockAmendBookingPresenter!
    private var analytics: MockAnalyticsManager!
    private var interactor: AmendBookingInteractor!
    private var existingReservation: Reservation!
    private var updatedReservationModel: AmendBookingInteractor.AmendReservationModel!

    // MARK: - Lifecycle
    
    override func setUp() {
        super.setUp()
        
        presenter = MockAmendBookingPresenter()
        
        interactor = AmendBookingInteractor(stay: stay, hotel: nil, delegate: presenter)

        analytics = MockAnalyticsManager()
        interactor.analytics = analytics
    }
    
    private func getBookingConfirmationDict(isECIAndNoKids: Bool) -> PIDictionary {
        var url: URL
        if isECIAndNoKids {
            url = Bundle(for: type(of: self)).url(forResource: "graphQLBookingConfirmationWithECILCO", withExtension: "json")!
        } else {
            url = Bundle(for: type(of: self)).url(forResource: "graphQLBookingConfirmationWithKids", withExtension: "json")!
        }
        let data = try! Data(contentsOf: url)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        return dataDictionary["bookingConfirmation"] as! PIDictionary
    }

    private func getPackagesDict() -> PIDictionary {

        let url = Bundle(for: type(of: self)).url(forResource: "graphQLPackagesWithECILCO", withExtension: "json")!
        let data = try! Data(contentsOf: url)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        return dataDictionary["packages"] as! PIDictionary
    }

    override func tearDown() {
        
        analytics = nil
        interactor = nil
        presenter = nil
        
        super.tearDown()
    }

    private var criteria: Criteria {
        var criteria = Criteria()
        criteria.rooms = existingReservation.rooms.copy()
        return criteria
    }
    
    func interactorSetup(isECIAndNoKids: Bool) {

        let mappedReservationDict = ReservationMapper.map(
            from: getBookingConfirmationDict(isECIAndNoKids: isECIAndNoKids),
            basketReference: "AQN-b2638abb-d034-4e93-b2ea-0778c30139e9",
            manageBooking: nil,
            packagesDict: getPackagesDict(),
            isBusiness: false
        )
        existingReservation = try! Reservation(dictionary: mappedReservationDict)
        interactor.existingReservation = existingReservation
        interactor.hotel = self.hotel

        updatedReservationModel = AmendBookingInteractor.AmendReservationModel(with: existingReservation, criteria: criteria, and: Rate(dictionary: ["cardFeeApplies": true, "classification": "F", "totalCost": ["amount": "191.50", "currency": "GBP"]]))
        interactor.amendReservationModel = updatedReservationModel

        let itemsExcludingECILCO = updatedReservationModel.reservation.upsellItems.filter {
            $0.upsellOperaId != .earlyCheckIn && $0.upsellOperaId != .lateCheckOut
        }
        updatedReservationModel.reservation.upsellItems = itemsExcludingECILCO
        
    }

    // MARK: - Tests
    
    func testInteractor_analyticsProperties() {
        
        let properties = interactor.analyticsProperties.trackingDictionary
        
        XCTAssertEqual(properties?["analyticsData.amend.conf.bookingID"], "BBER264250")
    }
    
    func testInteractor_whenTrackAvailabilityUpdate_invokesTrackState() {
        UserSessionManager.sharedInstance.piUserLoggedOut()
        
        interactor.trackAvailabilityUpdate(available: false)
        
        let state = analytics.states.first
        let dictionary = analytics.dictionaries.first

        XCTAssertEqual(state, "iOS:PI:UK: Amend a Booking: search criteria")
        XCTAssertEqual(dictionary?["analyticsData.amend.availability"], "false")
    }
    
    func testInteractor_whenTrackState_invokesTrackState() {
        UserSessionManager.sharedInstance.piUserLoggedOut()
        
        interactor.trackState(of: "view-custom")
        
        let state = analytics.states.first
        let dictionary = analytics.dictionaries.first

        XCTAssertEqual(state, "iOS:PI:UK: Amend a Booking: view-custom")
        XCTAssertEqual(dictionary?["analyticsData.amend.conf.bookingID"], "BBER264250")
    }

    func testECILCOAmendmentsCount() {
        interactorSetup(isECIAndNoKids: true)
        let amendments = interactor.extraUpsellsChangesFor(
            roomId: "2272970",
            existingReservation: existingReservation,
            updatedReservationModel: updatedReservationModel
        )
        XCTAssertEqual(amendments?.count, 2)
    }

    func testECILCORemovalMessages_whenRemovedAutomatically() {
        interactorSetup(isECIAndNoKids: true)
        let amendments = interactor.extraUpsellsChangesFor(
            roomId: "2272970",
            existingReservation: existingReservation,
            updatedReservationModel: updatedReservationModel
        )

        XCTAssertEqual(amendments?.first?.title.string, "Extras changes (Mr Santa Gurung)\nRemoved Early check-in\n")
        XCTAssertEqual(amendments?.last?.title.string, "Extras changes (Mr Santa Gurung)\nRemoved Late check-out\n")
    }

    func testECILCORemovalBannerShown_whenRemovedAutomatically() {
        interactorSetup(isECIAndNoKids: true)
        let result = interactor.shouldShowECILCORemovalMessage(
            existingReservation: existingReservation,
            updatedReservation: updatedReservationModel
        )
        XCTAssertTrue(result)
    }

    func testUpsellDescriptionStringNoKids() {
        interactorSetup(isECIAndNoKids: true)

        let mealDescription = interactor.amendViewModel!.rooms.first!.mealDescription
        XCTAssertEqual(mealDescription, "Premier Inn Breakfast x 1")
    }

    func testUpsellDescriptionStringWithKids() {
        interactorSetup(isECIAndNoKids: false)
        
        let mealDescription = interactor.amendViewModel!.rooms.first!.mealDescription
        XCTAssertEqual(mealDescription, "Premier Inn Breakfast x 1\nKids breakfast x 1")
    }

    func testUpsellChanges() {
        interactorSetup(isECIAndNoKids: false)
        // Change the bookings upsells and make sure the meal changes text is correct

        // Change from PI Breakfast to Meal Deal
        interactor.amendReservationModel?.reservation.upsellItems = [self.mealDealUpsell(quantity: 1)]
        let amendments = interactor.amendDifferencesModel
        let mealChange = amendments?.updateModel.updates[2]
        XCTAssertEqual(mealChange!.title.string, "Meal changes (Mr Santa Gurung)\n")
        XCTAssertEqual(mealChange!.description, "+£14.50")

        // Change from PI Breakfast to No Meals
        interactor.amendReservationModel?.reservation.upsellItems = [self.mealDealUpsell(quantity: 0)]
        let amendmentsTwo = interactor.amendDifferencesModel
        let mealChangeTwo = amendmentsTwo?.updateModel.updates[2]
        XCTAssertEqual(mealChangeTwo!.title.string, "Meal changes (Mr Santa Gurung)\n")
        XCTAssertEqual(mealChangeTwo!.description, "-£11.99")

    }
}
