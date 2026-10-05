//
//  CreateReservationGuestTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 01/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class CreateReservationGuestTests: XCTestCase {
    
    var bookingDetails: BookingDetails!
    
    override func setUp() {
        bookingDetails = BookingDetails()
    }
    
    override func tearDown() {
        bookingDetails = nil
    }
    
    func testMandatoryCreateReservationGuestVariablesAreValid() {
        
        bookingDetails.hotel = getHotel()
        bookingDetails.basketReference = "MOCK_SESSION_ID"
        bookingDetails.purpose = .leisure
        bookingDetails.booker = getBooker()
        bookingDetails.criteria = getCriteria()
        
        let variables = try! GraphQL.getCreateReservationGuestVariables(bookingDetails: bookingDetails, isCiolFlow: true)
        XCTAssertNotNil(variables["createReservationGuestCriteria"])
        
        let createReservationGuestCriteria = variables["createReservationGuestCriteria"] as! PIDictionary
        XCTAssertEqual(createReservationGuestCriteria["basketReference"] as! String, "MOCK_SESSION_ID")
        XCTAssertEqual(createReservationGuestCriteria["hotelId"] as! String, "MANOLD")
        XCTAssertEqual(createReservationGuestCriteria["reasonForStay"] as! String, "LEI")
        
        let booker = createReservationGuestCriteria["booker"] as! PIDictionary
        XCTAssertEqual(booker["title"] as! String, "Mr")
        XCTAssertEqual(booker["firstName"] as! String, "Test")
        XCTAssertEqual(booker["lastName"] as! String, "Booker")
        XCTAssertEqual(booker["emailAddress"] as! String, "test@booker.com")
        
        let bookerAddress = booker["address"] as! PIDictionary
        XCTAssertEqual(bookerAddress["addressType"] as! String, "HOME")
        XCTAssertEqual(bookerAddress["postalCode"] as! String, "EC1 2DF")
        XCTAssertEqual(bookerAddress["addressLine1"] as! String, "line1")
        
        let stayingGuests = createReservationGuestCriteria["stayingGuests"] as! [PIDictionary]
        XCTAssertEqual(stayingGuests.first!["sameAsBooker"] as! Bool, true)
        XCTAssertEqual((stayingGuests.first!["stayingGuestDetails"] as! PIDictionary)["title"] as! String, "Mr")
        XCTAssertEqual((stayingGuests.first!["stayingGuestDetails"] as! PIDictionary)["firstName"] as! String, "Test")
        XCTAssertEqual((stayingGuests.first!["stayingGuestDetails"] as! PIDictionary)["lastName"] as! String, "Booker")

        
        XCTAssertEqual((stayingGuests.first!["accompanyingGuestDetails"] as! PIDictionary)["title"] as! String, "Mr")
        XCTAssertEqual((stayingGuests.first!["accompanyingGuestDetails"] as! PIDictionary)["firstName"] as! String, "Second")
        XCTAssertEqual((stayingGuests.first!["accompanyingGuestDetails"] as! PIDictionary)["lastName"] as! String, "Guest")
        
        let additionalDetails = (stayingGuests.first?["stayingGuestDetails"] as! PIDictionary)["additionalDetails"] as! PIDictionary
        XCTAssertEqual(additionalDetails["passportNumber"] as! String, "1234")
        XCTAssertEqual(additionalDetails["nationality"] as! String, "GB")
        
        let additionalDetailsAcompanyingGuest = (stayingGuests.first?["accompanyingGuestDetails"] as! PIDictionary)["additionalDetails"] as! PIDictionary
        XCTAssertEqual(additionalDetailsAcompanyingGuest["passportNumber"] as! String, "123456")
        XCTAssertEqual(additionalDetailsAcompanyingGuest["nationality"] as! String, "DE")
    }
    
    func getHotel() -> Hotel {
        return try! Hotel(dictionary: [
            "hotelCode": "MANOLD",
            "hotelInfo": [
                "name": "PremierInn Manchester Old Trafford",
                "address": ["postcode": "BN1 1RE", "addressline1": "144 North Street", "addressline2": "Brighton", "addressline3": "East Sussex", "country": "United Kingdom (the)"]
            ]
        ])
    }
    
    func getCriteria() -> Criteria {
        var criteria = Criteria()
        criteria.rooms = {
            let room = Room()
            room.adults = 2
            room.children = 0
            room.cotRequired = false
            room.type = RoomType.double
            room.leadGuest = getBooker()
            room.accompanyingGuest = getSecondGuest()
            return [room]
        }()
        
        criteria.arrivalDate = {
            var dateComponents = DateComponents()
            dateComponents.year = 2050
            dateComponents.month = 9
            dateComponents.day = 12
            dateComponents.hour = 12
            dateComponents.timeZone = TimeZone(identifier: "Europe/London")

            return Calendar.current.date(from: dateComponents)!
        }()
        criteria.nights = 2
        return criteria
    }
    
    func getBooker() -> User {
        let user = try! User(
            title: "Mr",
            firstName: "Test",
            lastName: "Booker",
            email: "test@booker.com")

        user.passport =  Passport(number: "1234", countryOfIssue: "GB")
        user.country = Country(code: "GB",
                               name: "United Kingdom",
                               isoCode: "GB",
                               dialingCode: nil,
                               flagImage: nil,
                               passportRequired: nil,
                               nationality: nil)
        user.address = try! Address(dictionary: [
            "addressLine1": "line1",
            "addressLine2": "line2",
            "addressLine3": "line3",
            "postalCode": "EC1 2DF",
            "countryCode": "GB",
            "addressType": "HOME"
        ])
        return user
    }
    
    func getSecondGuest() -> User {
        let user = try! User(
            title: "Mr",
            firstName: "Second",
            lastName: "Guest",
            email: "second@guest.com"
        )
        user.passport =  Passport(number: "123456", countryOfIssue: "DE")
        user.country = Country(code: "DE",
                               name: "Germany",
                               isoCode: "DE",
                               dialingCode: nil,
                               flagImage: nil,
                               passportRequired: nil,
                               nationality: nil)
        user.address = try! Address(dictionary: [
            "addressLine1": "line1",
            "addressLine2": "line2",
            "addressLine3": "line3",
            "postalCode": "EC1 2DF",
            "countryCode": "GB",
            "addressType": "HOME"
        ])
        return user
    }
    
}
