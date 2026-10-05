//
//  BookingInformationMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 26/07/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import XCTest

@testable import SimpleNetwork

class BookingConfirmationMapperTests: XCTestCase {

    var sut = PIDictionary()

    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "graphQLBookingConfirmation", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let bookingInformationDictionary = dataDictionary["bookingConfirmation"] as! PIDictionary
        let manageBookingDict = dataDictionary["manageBooking"] as! PIDictionary
        let packagesDict = dataDictionary["packages"] as! PIDictionary

        let reservationDetails = ReservationDetails(reservationId: "", surname: "", arrivalDate: Date(), business: false, token: "testToken")
        sut = ReservationMapper.map(from: bookingInformationDictionary, basketReference: reservationDetails.reservationId, manageBooking: manageBookingDict, packagesDict: packagesDict, isBusiness: reservationDetails.business)
    }

    func testHotelDetailsMapping() {
        let reservationDetails = sut["reservationDetails"] as! PIDictionary
        XCTAssertEqual(reservationDetails["hotelCode"] as! String, "MANOLD")
        XCTAssertEqual(reservationDetails["arrivalDate"] as! String, "2022-07-25")
        XCTAssertEqual(reservationDetails["departureDate"] as! String, "2022-07-26")
    }
    
    func testPackagesMapping() {
        let reservationDetails = sut["reservationDetails"] as! PIDictionary
        let upsellBreakdown = reservationDetails["upsellBreakdown"] as! PIDictionary
        let upsellItems = upsellBreakdown["upsellItems"] as! [PIDictionary]

        let piBreakfast = upsellItems[1]
        XCTAssertEqual(piBreakfast["legend"] as! String, "Premier Inn Breakfast")
        XCTAssertEqual(piBreakfast["quantity"] as! Int, 2)
        let unitCost = piBreakfast["unitCost"] as! PIDictionary
        XCTAssertEqual(unitCost["amount"] as! Double, 10.5)
    }

    func testRoomsMapping() {

        let reservationDetails = sut["reservationDetails"] as! PIDictionary
        let rooms = reservationDetails["rooms"] as! [PIDictionary]
        for room in rooms {
            XCTAssertEqual(room["reservationId"] as! String, "480150")
            XCTAssertEqual(room["adults"] as! Int, 1)
            XCTAssertEqual(room["children"] as! Int, 0)
            XCTAssertEqual(room["cot"] as! Bool, false)
            XCTAssertEqual(room["roomType"] as! String, "TWINRM")
            XCTAssertEqual(room["roomName"] as! String, "Twin Room")
            XCTAssertTrue(room["preCheckInStatus"] as! Bool)
            XCTAssertTrue(room["deRegCardCompleted"] as! Bool)

            let guest = room["guest"] as! PIDictionary
            XCTAssertEqual(guest["firstName"] as! String, "Tester")
            XCTAssertEqual(guest["lastName"] as! String, "Testerson")
            XCTAssertEqual(guest["profileId"] as! String, "124212")
            XCTAssertTrue(guest["isAccompanyingGuest"] as! Bool)
            
            let additionalDetails = guest["additionalDetails"] as! PIDictionary
            XCTAssertEqual(additionalDetails["passportNumber"] as! String, "123")
            XCTAssertEqual(additionalDetails["nationality"] as! String, "GB")
            
        }
    }

    func testTotalCostMapping() {

        let reservationDetails = sut["reservationDetails"] as! PIDictionary
        let totalCost = reservationDetails["totalCost"] as! PIDictionary
        do {
            let cost = try Cost(dictionary: totalCost)

            XCTAssertEqual(cost.amount, 832.5)
            XCTAssertEqual(cost.currencyCode, "GBP")
        }
        catch {
            XCTFail("Cost conversion Fail")
        }
    }

    func testRateMapping() {

        let reservationDetails = sut["reservationDetails"] as! PIDictionary
        XCTAssertEqual(reservationDetails["rateText"] as! String, "Flex")
        XCTAssertEqual(reservationDetails["rateDescription"] as! String, "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival")
        XCTAssertEqual(reservationDetails["ratePlan"] as! String, "FLEXRATE")
    }


    func testBookerMapping() {

        let reservationDetails = sut["reservationDetails"] as! PIDictionary
        let booker = reservationDetails["booker"] as! PIDictionary
        XCTAssertEqual(booker["firstName"] as! String, "Tester")
        XCTAssertEqual(booker["lastName"] as! String, "Testerson")
        XCTAssertEqual(booker["telephone"] as! String, "+447911123456")
        
        let address = booker["address"] as! PIDictionary
        XCTAssertEqual(address["line1"] as! String, "line1")
        XCTAssertEqual(address["countryCode"] as! String, "GB")
    }

    func testCancelBookingFlag() {
        do {
            let reservation = try Reservation(dictionary: sut)
            XCTAssertEqual(reservation.cancelable, true)
            XCTAssertEqual(reservation.amendable, true)
            XCTAssertEqual(reservation.isCheckInOnlineAvailable, true)
            XCTAssertEqual(reservation.isCheckOutOnlineAvailable, true)
            
        } catch {
            XCTFail()
        }
    }

    func testToken() {
        do {
            let reservation = try Reservation(dictionary: sut, manageBookingOperaToken: "testToken")
            XCTAssertEqual(reservation.token, "testToken")
        } catch {
            XCTFail()
        }
    }

    func testPrePaidAmount() {
        do {
            let reservation = try Reservation(dictionary: sut)
            XCTAssertEqual(reservation.prepaidAmount?.amount, 0.0)
        } catch {
            XCTFail()
        }
    }

    func testBasketInfo() {
        do {
            let reservation = try Reservation(dictionary: sut)
            XCTAssertEqual(reservation.cancelled, true)
        } catch {
            XCTFail()
        }
    }

    func testBalanceOutstanding() {
        do {
            let reservation = try Reservation(dictionary: sut)
            XCTAssertEqual(reservation.balanceOutstanding?.amount, 832.5)
        } catch {
            XCTFail()
        }
    }

    func testReservationPackageList() {
        do {
            let reservation = try Reservation(dictionary: sut)
            XCTAssertNotNil(reservation.reservationPackageList)
            XCTAssertNotNil(reservation.reservationPackageList?.reservationPackages)
            XCTAssertEqual(reservation.reservationPackageList?.reservationPackages.count, 2)
            XCTAssertNotNil(reservation.reservationPackageList?.reservationPackages.first(where: { $0.packageCode == .earlyCheckIn }))
            XCTAssertNil(reservation.reservationPackageList?.reservationPackages.first(where: { $0.packageCode == .lateCheckOut }))
        } catch {
            XCTFail()
        }
    }
    
    func testPreferencesSpecialsList() {
        do {
            let reservation = try Reservation(dictionary: sut)
            let specials = reservation.preferences?.filter { $0.preferenceType == "SPECIALS" }
            XCTAssertNotNil(reservation.preferences)
            XCTAssertEqual(specials?.count, 1)
            XCTAssertEqual(specials?.first?.code, "SING")
            XCTAssertEqual(specials?.first?.preferenceType, "SPECIALS")
        } catch {
            XCTFail()
        }
    }
    
    func testPreferencesEventsList() {
        do {
            let reservation = try Reservation(dictionary: sut)
            let events = reservation.preferences?.filter { $0.preferenceType == "EVENTS" }
            XCTAssertNotNil(reservation.preferences)
            XCTAssertEqual(events?.count, 1)
            XCTAssertEqual(events?.first?.code, "DIETARY")
            XCTAssertEqual(events?.first?.preferenceType, "EVENTS")
        } catch {
            XCTFail()
        }
    }

    func testBasketStatus() {
        do {
            let reservation = try Reservation(dictionary: sut)
            let basketStatus = reservation.basketStatus
            XCTAssertEqual(basketStatus?.rawValue, "PRE_CHECKED_IN")
        } catch {
            XCTFail()
        }
    }
}
