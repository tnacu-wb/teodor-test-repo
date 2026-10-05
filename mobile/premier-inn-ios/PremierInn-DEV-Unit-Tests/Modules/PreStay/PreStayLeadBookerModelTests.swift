//
//  PreStayLeadBookerModelTests.swift
//  PremierInn
//
//  Created by Clint Mengolli on 19/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
@testable import SimpleNetwork

final class PreStayLeadBookerModelTests: XCTestCase {

    override func setUp() {
        super.setUp()

        Country.countriesList = [
            Country(
                code: "GB",
                name: "United Kingdom",
                isoCode: "GB",
                dialingCode: "+44",
                flagImage: nil,
                passportRequired: nil,
                nationality: nil
            )
        ]
    }

    func testDirectBookingUsesBookerDetails() throws {
        let expectedTitle = "Mr"
        let expectedFirstName = "Direct"
        let expectedLastName = "Booker"
        let expectedEmail = "direct.booker@example.com"
        let expectedPhoneNumber = "07123456789"
        let expectedAddressLine1 = "1 Booker Street"
        let expectedPostcode = "SW1A 1AA"

        let bookerAddress = makeAddress(
            line1: expectedAddressLine1,
            postcode: expectedPostcode
        )

        let booker = makeBooker(
            title: expectedTitle,
            firstName: expectedFirstName,
            lastName: expectedLastName,
            email: expectedEmail,
            phone: expectedPhoneNumber,
            address: bookerAddress
        )

        let leadGuestAddress = makeAddress(
            line1: "2 Guest Road",
            postcode: "M1 1AA"
        )

        let firstRoomLeadGuest = makeGuest(
            title: "Mrs",
            firstName: "Lead",
            lastName: "Guest",
            isAccompanyingGuest: false,
            address: leadGuestAddress
        )

        let firstRoomGuests = [firstRoomLeadGuest]

        let reservation = try makeReservation(
            isDirect: true,
            booker: booker,
            firstRoomGuests: firstRoomGuests
        )

        let model = PreStayLeadBookerModel(reservation: reservation)

        XCTAssertEqual(model.title, expectedTitle)
        XCTAssertEqual(model.firstName, expectedFirstName)
        XCTAssertEqual(model.lastName, expectedLastName)
        XCTAssertEqual(model.address?.line1, expectedAddressLine1)
        XCTAssertEqual(model.address?.postcode, expectedPostcode)
        XCTAssertEqual(model.email, expectedEmail)
        XCTAssertEqual(model.phoneNumber, expectedPhoneNumber)
    }

    func testThirdPartyBookingWithoutBookerUsesFirstRoomLeadGuest() throws {
        let expectedTitle = "Ms"
        let expectedFirstName = "Lead"
        let expectedLastName = "Guest"
        let expectedAddressLine1 = "3 Guest Lane"
        let expectedPostcode = "LS1 2AB"

        let leadGuestAddress = makeAddress(
            line1: expectedAddressLine1,
            postcode: expectedPostcode
        )

        let leadGuest = makeGuest(
            title: expectedTitle,
            firstName: expectedFirstName,
            lastName: expectedLastName,
            isAccompanyingGuest: false,
            address: leadGuestAddress
        )

        let accompanyingGuestAddress = makeAddress(
            line1: "4 Other Street",
            postcode: "LS1 2CD"
        )

        let accompanyingGuest = makeGuest(
            title: "Mr",
            firstName: "Accompanying",
            lastName: "Guest",
            isAccompanyingGuest: true,
            address: accompanyingGuestAddress
        )

        let firstRoomGuests = [leadGuest, accompanyingGuest]

        let reservation = try makeReservation(
            isDirect: false,
            booker: nil,
            firstRoomGuests: firstRoomGuests
        )

        let model = PreStayLeadBookerModel(reservation: reservation)

        XCTAssertEqual(model.title, expectedTitle)
        XCTAssertEqual(model.firstName, expectedFirstName)
        XCTAssertEqual(model.lastName, expectedLastName)
        XCTAssertEqual(model.address?.line1, expectedAddressLine1)
        XCTAssertEqual(model.address?.postcode, expectedPostcode)
        XCTAssertNil(model.email)
        XCTAssertNil(model.phoneNumber)
    }

    func testThirdPartyBookingUsesLeadGuestFromFirstRoomOnly() throws {
        let expectedTitle = "Mrs"
        let expectedFirstName = "FirstRoom"
        let expectedLastName = "Lead"
        let expectedAddressLine1 = "5 First Room Street"
        let expectedPostcode = "B1 1AA"

        let firstRoomLeadGuestAddress = makeAddress(
            line1: expectedAddressLine1,
            postcode: expectedPostcode
        )

        let firstRoomLeadGuest = makeGuest(
            title: expectedTitle,
            firstName: expectedFirstName,
            lastName: expectedLastName,
            isAccompanyingGuest: false,
            address: firstRoomLeadGuestAddress
        )

        let firstRoomGuests = [firstRoomLeadGuest]

        let secondRoomLeadGuestAddress = makeAddress(
            line1: "6 Second Room Street",
            postcode: "B2 2BB"
        )

        let secondRoomLeadGuest = makeGuest(
            title: "Dr",
            firstName: "SecondRoom",
            lastName: "Lead",
            isAccompanyingGuest: false,
            address: secondRoomLeadGuestAddress
        )

        let secondRoomGuests = [secondRoomLeadGuest]

        let secondRoom = makeRoom(
            roomId: "2",
            guests: secondRoomGuests
        )

        let additionalRooms = [secondRoom]

        let reservation = try makeReservation(
            isDirect: false,
            booker: nil,
            firstRoomGuests: firstRoomGuests,
            additionalRooms: additionalRooms
        )

        let model = PreStayLeadBookerModel(reservation: reservation)

        XCTAssertEqual(model.title, expectedTitle)
        XCTAssertEqual(model.firstName, expectedFirstName)
        XCTAssertEqual(model.lastName, expectedLastName)
        XCTAssertEqual(model.address?.line1, expectedAddressLine1)
        XCTAssertEqual(model.address?.postcode, expectedPostcode)
        XCTAssertNil(model.email)
        XCTAssertNil(model.phoneNumber)
    }

    func testThirdPartyBookingWithNoLeadGuestInFirstRoomReturnsNilValues() throws {
        let accompanyingGuestAddress = makeAddress(
            line1: "123 Line",
            postcode: "N1 1NN"
        )

        let accompanyingGuestOnly = makeGuest(
            title: "Mr",
            firstName: "Only",
            lastName: "Accompanying",
            isAccompanyingGuest: true,
            address: accompanyingGuestAddress
        )

        let firstRoomGuests = [accompanyingGuestOnly]

        let reservation = try makeReservation(
            isDirect: false,
            booker: nil,
            firstRoomGuests: firstRoomGuests
        )

        let model = PreStayLeadBookerModel(reservation: reservation)

        XCTAssertNil(model.title)
        XCTAssertNil(model.firstName)
        XCTAssertNil(model.lastName)
        XCTAssertNil(model.address)
        XCTAssertNil(model.email)
        XCTAssertNil(model.phoneNumber)
    }

    func testThirdPartyBookingWithBillingDetailsPresentUsesBooker() throws {
        let expectedTitle = "Sir"
        let expectedFirstName = "ThirdParty"
        let expectedLastName = "Booker"
        let expectedEmail = "thirdparty.booker@example.com"
        let expectedPhoneNumber = "07000000000"
        let expectedAddressLine1 = "8 Booker Avenue"
        let expectedPostcode = "EC1A 1BB"

        let bookerAddress = makeAddress(
            line1: expectedAddressLine1,
            postcode: expectedPostcode
        )

        let booker = makeBooker(
            title: expectedTitle,
            firstName: expectedFirstName,
            lastName: expectedLastName,
            email: expectedEmail,
            phone: expectedPhoneNumber,
            address: bookerAddress
        )

        let leadGuestAddress = makeAddress(
            line1: "9 Guest Crescent",
            postcode: "EC2A 2CC"
        )

        let firstRoomLeadGuest = makeGuest(
            title: "Mrs",
            firstName: "Lead",
            lastName: "Guest",
            isAccompanyingGuest: false,
            address: leadGuestAddress
        )

        let firstRoomGuests = [firstRoomLeadGuest]

        let reservation = try makeReservation(
            isDirect: false,
            booker: booker,
            firstRoomGuests: firstRoomGuests
        )

        let model = PreStayLeadBookerModel(reservation: reservation)

        XCTAssertEqual(model.title, expectedTitle)
        XCTAssertEqual(model.firstName, expectedFirstName)
        XCTAssertEqual(model.lastName, expectedLastName)
        XCTAssertEqual(model.address?.line1, expectedAddressLine1)
        XCTAssertEqual(model.address?.postcode, expectedPostcode)
        XCTAssertEqual(model.email, expectedEmail)
        XCTAssertEqual(model.phoneNumber, expectedPhoneNumber)
    }
}

private extension PreStayLeadBookerModelTests {
    func makeReservation(
        isDirect: Bool,
        booker: [String: Any]?,
        firstRoomGuests: [[String: Any]],
        additionalRooms: [[String: Any]] = []
    ) throws -> Reservation {
        let firstRoom = makeRoom(
            roomId: "1",
            guests: firstRoomGuests
        )

        let rooms = [firstRoom] + additionalRooms

        var reservationDetails: [String: Any] = [
            "confirmationNumber": "ABC123456",
            "hotelCode": "LON01",
            "arrivalDate": "2026-03-20",
            "departureDate": "2026-03-21",
            "isThirdPartyBooking": !isDirect,
            "rooms": rooms
        ]

        if let booker {
            reservationDetails["booker"] = booker
        }

        let dictionary: [String: Any] = [
            "reservationDetails": reservationDetails
        ]

        return try Reservation(dictionary: dictionary)
    }

    func makeRoom(roomId: String, guests: [[String: Any]]) -> [String: Any] {
        [
            "roomId": roomId,
            "adults": 2,
            "children": 0,
            "guestList": guests
        ]
    }

    func makeBooker(
        title: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        address: [String: Any]
    ) -> [String: Any] {
        [
            "title": title,
            "firstName": firstName,
            "lastName": lastName,
            "emailAddress": email,
            "mobileNumber": phone,
            "address": address
        ]
    }

    func makeGuest(
        title: String,
        firstName: String,
        lastName: String,
        isAccompanyingGuest: Bool,
        address: [String: Any]
    ) -> [String: Any] {
        [
            "title": title,
            "firstName": firstName,
            "lastName": lastName,
            "isAccompanyingGuest": isAccompanyingGuest,
            "address": address
        ]
    }

    func makeAddress(
        line1: String,
        postcode: String
    ) -> [String: Any] {
        [
            "line1": line1,
            "postcode": postcode,
            "countryCode": "GB"
        ]
    }
}
