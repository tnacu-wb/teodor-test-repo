//
//  AmendBreakfastTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Nick Jones on 03/04/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import XCTest
@testable import PremierInn

class AmendBreakfastTests: XCTestCase {

    var fullRoomReservationWithPaidBreakfastSelected: Reservation?
    var fullRoomReservationWithFreeBreakfastSelectedForChildren: Reservation?
    var partialRoomReservationWithPaidBreakfastSelected: Reservation?

    override func setUp() {
        super.setUp()


        //A standard reservation for 2 adults and 2 children and paid breakfasts
        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        let fileURLForFullRoomReservationWithPaidBreakfast = Bundle(for: type(of: self)).url(forResource: "fullRoomReservationWithPaidBreakfast", withExtension: "json")!
        let dataForFullRoomReservationWithPaidBreakfastSelected = try! Data(contentsOf: fileURLForFullRoomReservationWithPaidBreakfast)
        let jsonDictionaryForFullRoomReservationWithPaidBreakfastSelected = try! JSONSerialization.jsonObject(with: dataForFullRoomReservationWithPaidBreakfastSelected, options: .allowFragments) as! PIDictionary

        fullRoomReservationWithPaidBreakfastSelected = try? Reservation(dictionary: jsonDictionaryForFullRoomReservationWithPaidBreakfastSelected)
        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*


        //A standard reservation for 2 adults and 2 children and free breakfasts for children
        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        let fileURLForFullRoomReservationWithFreeBreakfast = Bundle(for: type(of: self)).url(forResource: "fullRoomReservationWithFreeBreakfast", withExtension: "json")!
        let dataForFullRoomReservationWithFreeBreakfastSelected = try! Data(contentsOf: fileURLForFullRoomReservationWithFreeBreakfast)
        let jsonDictionaryForFullRoomReservationWithFreeBreakfastSelected = try! JSONSerialization.jsonObject(with: dataForFullRoomReservationWithFreeBreakfastSelected, options: .allowFragments) as! PIDictionary

        fullRoomReservationWithFreeBreakfastSelectedForChildren = try? Reservation(dictionary: jsonDictionaryForFullRoomReservationWithFreeBreakfastSelected)
        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*


        //A standard reservation for 1 adult and 1 child and paid breakfasts
        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        let fileURLForPartialRoomReservationWithPaidBreakfastSelected = Bundle(for: type(of: self)).url(forResource: "partialRoomReservation", withExtension: "json")!
        let dataForPartialRoomReservationWithPaidBreakfastSelected = try! Data(contentsOf: fileURLForPartialRoomReservationWithPaidBreakfastSelected)
        let jsonDictionaryForPartialRoomReservationWithPaidBreakfastSelected = try! JSONSerialization.jsonObject(with: dataForPartialRoomReservationWithPaidBreakfastSelected, options: .allowFragments) as! PIDictionary

        partialRoomReservationWithPaidBreakfastSelected = try? Reservation(dictionary: jsonDictionaryForPartialRoomReservationWithPaidBreakfastSelected)
        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    }

// All kids breakfasts are free, if they can get them as of 25/01/2023
//    func test_Reservation_Amend_correctBreakfasts_Removes_Upsells_For_Adults_But_Does_Not_Change_Child_Upsells_When_The_Number_Of_Adults_Decreases_And_Selected_Breakfasts_Are_Not_Free() {
//
//        guard var reservation = fullRoomReservationWithPaidBreakfastSelected else { return XCTFail("Could not parse a reservation from mock data") }
//
//        guard let firstRoom = reservation.rooms.first else { return XCTFail("Unable to parse mock data") }
//        guard reservation.breakfasts.first != nil else { return XCTFail("No breakfasts found on mock data") }
//        guard firstRoom.adults == 2 else { return XCTFail("Number of adults in mocked data first room should be 2") }
//        guard firstRoom.children == 2 else { return XCTFail("Number of adults in mocked data first room should be 2") }
//
//        if reservation.breakfasts.first(where: { $0.roomId == firstRoom.roomId })?.kidsHaveToPay == false { return XCTFail("Mocked data contains a free breakfast where it should be paid for") }
//
//        firstRoom.adults = 1
//
//        reservation.correctBreakfasts()
//
//        XCTAssert(reservation.breakfasts.first?.adults == 1, "Number of adults with breakfast is \(String(describing: reservation.breakfasts.first?.adults!)), expected 1")
//        XCTAssert(reservation.breakfasts.first?.children == 2, "Number of chidlren with breakfast has changed to \(String(describing: reservation.breakfasts.first?.children!)), expected the original value of 2")
//
//    }

//    func test_Reservation_Amend_correctBreakfasts_Removes_Upsells_For_Children_When_The_Number_Of_Children_Decreases_And_Selected_Breakfasts_Are_Not_Free() {
//
//        guard var reservation = fullRoomReservationWithPaidBreakfastSelected else { return XCTFail("Could not parse a reservation from mock data") }
//
//        guard let firstRoom = reservation.rooms.first else { return XCTFail("Unable to parse mock data") }
//        guard let breakfast = reservation.breakfasts.first else { return XCTFail("No breakfasts found on mock data") }
//        guard firstRoom.children == 2 else { return XCTFail("Number of children in mocked data first room should be 2") }
//        guard breakfast.children == 2 else { return XCTFail("Number of children in mocked data breakfast should be 2") }
//
//        if reservation.breakfasts.first(where: { $0.roomId == firstRoom.roomId })?.kidsHaveToPay == false { return XCTFail("Mocked data contains a free breakfast where it should be paid for") }
//
//        firstRoom.children = 1
//
//        reservation.correctBreakfasts()
//
//        XCTAssert(reservation.breakfasts.first?.children == 1, "Number of children with breakfast is \(String(describing: reservation.breakfasts.first?.children!)), expected 1")
//    }

    func test_Reservation_Amend_correctBreakfasts_Removes_Upsells_For_Adults_When_The_Number_Of_Adults_Decreases_And_Selected_Breakfasts_Are_Free_But_Does_Not_Change_Children() {

        guard var reservation = fullRoomReservationWithFreeBreakfastSelectedForChildren else { return XCTFail("Could not parse a reservation from mock data") }

        guard let firstRoom = reservation.rooms.first else { return XCTFail("Unable to parse mock data") }
        guard let breakfast = reservation.breakfasts.first else { return XCTFail("No breakfasts found on mock data") }
        guard firstRoom.adults == 2 else { return XCTFail("Number of adults in mocked data first room should be 2") }
        guard firstRoom.children == 2 else { return XCTFail("Number of children in mocked data first room should be 2") }
        guard breakfast.adults == 2 else { return XCTFail("Number of adults in mocked data breakfast should be 2") }
        guard breakfast.children == 0 else { return XCTFail("Number of children in mocked data breakfast should be 0 for free breakfasts") }

        if reservation.breakfasts.first(where: { $0.roomId == firstRoom.roomId })?.kidsHaveToPay == true { return XCTFail("Mocked data contains a paid breakfast where it should be free") }

        firstRoom.adults = 1

        reservation.correctBreakfasts()

        XCTAssert(reservation.breakfasts.first?.children == 0, "Number of children with breakfast is \(String(describing: reservation.breakfasts.first?.children!)), expected 0")
        XCTAssert(reservation.breakfasts.first?.adults == 1, "Number of adults with breakfast is \(String(describing: reservation.breakfasts.first?.adults!)), expected 0")
    }

    func test_Reservation_Amend_correctBreakfasts_Does_Not_Add_Upsell_When_Adults_Increases() {

        guard var reservation = partialRoomReservationWithPaidBreakfastSelected else { return XCTFail("Could not parse a reservation from mock data") }

        guard let firstRoom = reservation.rooms.first else { return XCTFail("Unable to parse mock data") }
        guard let breakfast = reservation.breakfasts.first else { return XCTFail("No breakfasts found on mock data") }
        guard firstRoom.adults == 1 else { return XCTFail("Number of adults in mocked data first room should be 1") }
        guard firstRoom.children == 1 else { return XCTFail("Number of children in mocked data first room should be 1") }
        guard breakfast.adults == 1 else { return XCTFail("Number of adults in mocked data breakfast should be 1") }
        guard breakfast.children == 1 else { return XCTFail("Number of children in mocked data breakfast should be 1") }

        firstRoom.adults = 2

        reservation.correctBreakfasts()

        XCTAssert(reservation.breakfasts.first?.adults == 1, "Number of adults with breakfast is \(String(describing: reservation.breakfasts.first?.adults!)), expected 1")
    }


//    func test_Reservation_Amend_correctBreakfasts_Does_Not_Add_Upsell_For_Children_When_Children_Increases_For_Paid_Breakfast() {
//
//        guard var reservation = partialRoomReservationWithPaidBreakfastSelected else { return XCTFail("Could not parse a reservation from mock data") }
//
//        guard let firstRoom = reservation.rooms.first else { return XCTFail("Unable to parse mock data") }
//        guard let breakfast = reservation.breakfasts.first else { return XCTFail("No breakfasts found on mock data") }
//        guard firstRoom.adults == 1 else { return XCTFail("Number of adults in mocked data first room should be 1") }
//        guard firstRoom.children == 1 else { return XCTFail("Number of children in mocked data first room should be 1") }
//        guard breakfast.adults == 1 else { return XCTFail("Number of adults in mocked data breakfast should be 1") }
//        guard breakfast.children == 1 else { return XCTFail("Number of children in mocked data breakfast should be 1") }
//
//        if reservation.breakfasts.first(where: { $0.roomId == firstRoom.roomId })?.kidsHaveToPay == false { return XCTFail("Mocked data contains a free breakfast where it should be paid") }
//
//        firstRoom.children = 2
//
//        reservation.correctBreakfasts()
//
//        XCTAssert(reservation.breakfasts.first?.children == 1, "Number of children with breakfast is \(String(describing: reservation.breakfasts.first?.children!)), expected 1")
//    }
}

