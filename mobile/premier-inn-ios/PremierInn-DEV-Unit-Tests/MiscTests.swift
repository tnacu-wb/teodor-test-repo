//
//  MiscTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 21/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class MiscTests: XCTestCase {

    static var reservationDictionary: PIDictionary {
        let fileURL = Bundle(for: MiscTests.self).url(forResource: "reservation", withExtension: "json")!
        let    data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

        return jsonDictionary
    }

    static var paymentCardDictionary: PIDictionary {
        let fileURL = Bundle(for: MiscTests.self).url(forResource: "paymentCard", withExtension: "json")!
        let    data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

        return jsonDictionary
    }

    static var dashboardComponentsDictionary: PIDictionary {

        let fileURL = Bundle(for: MiscTests.self).url(forResource: "dashboardComponent", withExtension: "json")!
        let    data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

        return jsonDictionary
    }

    override func setUp() {
        super.setUp()
        UserSessionManager.sharedInstance.userLoggedOut()
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        //        SettingsManager.sharedInstance.cmsStrings = [:]
        super.tearDown()
    }

    func testTopDestinations() {

        XCTAssertGreaterThan(SettingsManager.sharedInstance.topDestinations.count, 0)
    }

    func testRecentSearches() {

        let ud = UserDefaults(suiteName: "test")!

        let manager = RecentSuggestionsManager(dataSource: ud, maximumSize: 3)
        manager.reset(userDefaults: ud)

        XCTAssertEqual(manager.results.count, 0)

        let search1 = RecentSearch(dictionary: ["title" : "Search1", "subtitle" : "search1 subtitle"], criteria: nil)!
        let search2 = RecentSearch(dictionary: ["title" : "Search2"], criteria: nil)!
        let search3 = RecentSearch(dictionary: ["title" : "Search3", "latitude" : 12.0, "longitude" : 13.0], criteria: nil)!

        manager.add(search1)
        manager.add(search2)
        manager.add(search3)

        XCTAssertEqual(manager.results.count, 3)
        XCTAssertEqual(manager.results.first?.title, "Search3")
        XCTAssertEqual(manager.results.first?.coordinate.latitude, 12.0)
        XCTAssertEqual(manager.results.first?.coordinate.longitude, 13.0)
        XCTAssertEqual(manager.results.last?.title, "Search1")
        XCTAssertEqual(manager.results.last?.subtitle, "search1 subtitle")

        // Test bounds
        let search4 = RecentSearch(dictionary: ["title" : "Search4"], criteria: nil)!
        manager.add(search4)

        XCTAssertEqual(manager.results.count, 3)
        XCTAssertEqual(manager.results.first?.title, "Search4")
        XCTAssertEqual(manager.results.last?.title, "Search2")

        // Test duplicates
        let search5 = RecentSearch(dictionary: ["title" : "Search4"], criteria: nil)!
        manager.add(search5)

        XCTAssertEqual(manager.results.count, 3)
        XCTAssertEqual(manager.results.first?.title, "Search4")
        XCTAssertEqual(manager.results.last?.title, "Search2")

        XCTAssertEqual(manager.recentSearchesShortcutItems.count, 3)
    }

    func testRangeManager() {

        let manager = RangeManager(range: 1...3, currentStep: 1)

        XCTAssertEqual(manager.currentStep, 1)
        XCTAssertEqual(try? manager.increase(), 2)
        XCTAssertEqual(try? manager.decrease(), 1)

        do {
            _ = try manager.decrease()
        } catch let error as NSError {
            XCTAssertEqual(error.code, PIError.Code.minRangeReached)
            XCTAssertEqual(manager.currentStep, 1)
        }

        XCTAssertEqual(try? manager.increase(), 2)
        XCTAssertEqual(try? manager.increase(), 3)

        do {
            _ = try manager.increase()
        } catch let error as NSError {
            XCTAssertEqual(error.code, PIError.Code.maxRangeReached)
            XCTAssertEqual(manager.currentStep, 3)
        }
    }

    func testBookingDetailsSummary() {

        let bookingDetails = BookingDetails.sharedInstance

        var criteria = Criteria()
        criteria.nights = 2
        bookingDetails.criteria = criteria

        var room = bookingDetails.criteria.rooms.first
        room?.adults = 2
        room?.children = 1

        let nightsString = String.localizedStringWithFormat(PILocalizedString("%d night(s)", comment: "Message shown for number of nights"), bookingDetails.criteria.nights)
        let adultsString = String.localizedStringWithFormat(PILocalizedString("%d adult(s)", comment: "Message shown for number of adults"), room!.adults)
        let childrenString = String.localizedStringWithFormat(PILocalizedString("%d child(children)", comment: "Message shown for number of children"), room!.children)

        let expectedString = "\(Date().localizedShortStringFormat) - \(nightsString) - \(adultsString) - \(childrenString)"
        XCTAssert(bookingDetails.criteria.summary == expectedString)

        UserSessionManager.sharedInstance.piUserLoggedOut()
        bookingDetails.reset()

        XCTAssertNil(bookingDetails.purpose)

        var criteria2 = Criteria()
        criteria2.nights = 1
        bookingDetails.criteria = criteria2

        room = bookingDetails.criteria.rooms.first
        room?.adults = 1
        room?.children = 0

        let nightsString2 = String.localizedStringWithFormat(PILocalizedString("%d night(s)", comment: "Message shown for number of nights"), bookingDetails.criteria.nights)
        let adultsString2 = String.localizedStringWithFormat(PILocalizedString("%d adult(s)", comment: "Message shown for number of adults"), room!.adults)

        let expectedString2 = "\(Date().localizedShortStringFormat) - \(nightsString2) - \(adultsString2)"
        XCTAssertEqual(bookingDetails.criteria.summary, expectedString2)

        XCTAssertEqual(bookingDetails.criteria.guestsAndNightsSummary, "\(String.localizedStringWithFormat(PILocalizedString("%d guest(s)", comment: ""), 1))\(PILocalizedString(", ", comment: ""))\(String.localizedStringWithFormat(PILocalizedString("%d night(s)", comment: ""), 1))")

        XCTAssertEqual(bookingDetails.criteria.rateGuestsSummary, "\(String.localizedStringWithFormat(PILocalizedString("%d night(s)", comment: "Message shown for number of nights"), 1)), \(String.localizedStringWithFormat(PILocalizedString("%d guest(s)", comment: "Message shown for number of guests"), 1))")
        XCTAssertEqual(bookingDetails.criteria.reviewRoomsSummary, "\(String.localizedStringWithFormat(PILocalizedString("%d guest(s)", comment: "Message shown for number of guests"), 1)) - \(String.localizedStringWithFormat(PILocalizedString("%d room(s)", comment: "Message shown for number of rooms"), 1))")
    }

    func testWebserviceConfiguration() {

        SettingsManager.sharedInstance.currentRouterType = .demoGraphQL
        XCTAssertEqual(Router.current, .demoGraphQL)

        SettingsManager.sharedInstance.currentRouterType = .developmentGraphQL
        XCTAssertTrue(Router.current is DevelopmentGraphQLRouter)
    }

    func testStayDictionaryWithReservation() {

        do {
            let reservation = try Reservation(dictionary: MiscTests.reservationDictionary)
            let hotel = try Hotel(dictionary: ["name": "Fake Hotel", "code": "CODE"])
            let dictionary = Stay.dictionary(reservation: reservation, hotel: hotel, importType: .account)

            XCTAssertEqual(dictionary["hotelCode"] as? String, "SOUANC")
            XCTAssertEqual(dictionary["hotelName"] as? String, "Fake Hotel")
            XCTAssertEqual(dictionary["hotelLongitude"] as? Double, 0)
            XCTAssertEqual(dictionary["hotelLatitude"] as? Double, 0)
            XCTAssertEqual(dictionary["lastName"] as? String, "Apps")
            XCTAssertEqual(dictionary["identifier"] as? String, "ATCR153968")
            XCTAssertEqual(dictionary["arrivalDate"] as? String, "2017-10-11")
            XCTAssertEqual(dictionary["checkOutDate"] as? String, "2017-10-12")
            XCTAssertEqual(dictionary["importType"] as? String, "account")
            XCTAssertEqual(dictionary["cancelled"] as? Bool, false)
            XCTAssertEqual(dictionary["checkInOnline"] as? Bool, false)
            XCTAssertEqual(dictionary["amendable"] as? Bool, true)
            XCTAssertEqual(dictionary["leadGuest"] as? String, "MR WHITBREAD APPS")
            XCTAssertEqual(dictionary["rateClass"] as? String, "A")
            XCTAssertEqual(dictionary["noOfRooms"] as? Int, 1)
            XCTAssertEqual(dictionary["business"] as? Bool, false)

            let totalCost = dictionary["totalCost"] as? PIDictionary
            XCTAssertEqual(totalCost?["amount"] as? Double, 209.5)
            XCTAssertEqual(totalCost?["currency"] as? String, "GBP")

            let cityTax = dictionary["cityTax"] as? PIDictionary
            XCTAssertEqual(cityTax?["amount"] as? Double, 2)
            XCTAssertEqual(cityTax?["currency"] as? String, "GBP")

            let prePaidAmount = dictionary["prePaidAmount"] as? PIDictionary
            XCTAssertEqual(prePaidAmount?["amount"] as? Double, 209.5)
            XCTAssertEqual(prePaidAmount?["currency"] as? String, "GBP")

        } catch {
            print(error)
            XCTFail()
        }
    }

    func testStayDictionaryWithBookingDetailsAndConfirmation() {

        let bookingDetails = BookingDetails()
        bookingDetails.hotel = try? Hotel(dictionary: ["name": "Fake Hotel", "code": "FAKECODE"])
        bookingDetails.booker = try? User(title: "Mr", firstName: "Test", lastName: "McTester")
        bookingDetails.criteria = {
            var criteria = Criteria()
            criteria.arrivalDate = {
                var dateComponents = DateComponents()
                dateComponents.year = 2050
                dateComponents.month = 9
                dateComponents.day = 12
                dateComponents.hour = 12
                dateComponents.timeZone = TimeZone(identifier: "Europe/London")

                return Calendar.current.date(from: dateComponents)!
            }()
            criteria.nights = 1
            criteria.rooms = {
                let dailyRates = [["date": "", "price": ["amount": 12.3, "currency": "GBP"]]]
                let room1 = Room(dictionary: ["adults": 2, "children": 2, "cotRequired": true, "lettingType": "ASDASD", "totalCost": ["amount": 12.0, "currency": "GBP"], "dailyRates": dailyRates])
                room1.leadGuest = try? User(title: "Mr", firstName: "Test", lastName: "McTester")
                room1.adults = 2
                room1.children = 2

                let room2 = Room()
                room2.adults = 1
                room2.children = 2

                return [room1, room2]
            }()

            return criteria
        }()
        bookingDetails.rate = Rate(dictionary: ["cardFeeApplies": true, "classification": "F", "totalCost": ["amount": "191.50", "currency": "GBP"]])
        bookingDetails.operaBookingReference = "MOCK666999"

        let bookingConfirmation = BookingConfirmation.mock!

        let dictionary = Stay.dictionary(bookingDetails: bookingDetails, confirmation: bookingConfirmation)

        XCTAssertEqual(dictionary["hotelCode"] as? String, "FAKECODE")
        XCTAssertEqual(dictionary["hotelName"] as? String, "Fake Hotel")
        XCTAssertEqual(dictionary["hotelLongitude"] as? Double, 0)
        XCTAssertEqual(dictionary["hotelLatitude"] as? Double, 0)
        XCTAssertEqual(dictionary["lastName"] as? String, "McTester")
        XCTAssertEqual(dictionary["identifier"] as? String, "MOCK666999")
        XCTAssertEqual(dictionary["arrivalDate"] as? String, "2050-09-12")
        XCTAssertEqual(dictionary["checkOutDate"] as? String, "2050-09-13")
        XCTAssertEqual(dictionary["importType"] as? String, "imported")
        XCTAssertEqual(dictionary["cancelled"] as? Bool, false)
        XCTAssertEqual(dictionary["leadGuest"] as? String, "Mr Test McTester")
        XCTAssertEqual(dictionary["rateClass"] as? String, "F")
        XCTAssertEqual(dictionary["noOfRooms"] as? Int, 2)
        XCTAssertEqual(dictionary["business"] as? Bool, false)
    }

    func testPaymentCards() {

        do {
            let paymentCard = try PaymentCard(dictionary: MiscTests.paymentCardDictionary)
            XCTAssert(paymentCard.cardNumber == "************1111")
            XCTAssert(paymentCard.cardNameDescription == "Visa Credit")

        } catch {
            print(error)
        }
    }

    func testDashboardComponent() {

        let dic1: PIDictionary = [
            "type": "UPCOMING_BOOKING",
            "content": [
                "hotelImage": "https://www.beta.premierinn.digital/content/dam/pi/websites/hotelimages/gb/en/L/LKEBAR/LKEBAR 3.jpg",
                "hotelName": "London Victoria",
                "map": [
                    "latitude": 51.49306081188715,
                    "longitude": -0.14288663864134826
                ],
                "confirmationNumber": "AYRR237204",
                "arrivalDate": "2021-01-11",
                "departureDate": "2021-01-12",
                "rooms": [[
                    "type": "Double"
                ]],
                "guests": 1,
                "actions": [[
                    "type": "DIRECTIONS",
                    "title": "Show hotel directions"
                ]]
            ]
        ]

        do {
            let jsonData1 = try JSONSerialization.data(withJSONObject: dic1["content"] as Any, options: .prettyPrinted)
            let dashboardComponent = try JSONDecoder().decode(DashboardComponent.self, from: jsonData1)

            XCTAssertNotNil(dashboardComponent)

            switch dashboardComponent {
            case .upcomingBooking(let upcomingBooking):
                XCTAssert(upcomingBooking.confirmationNumber == "AYRR237204")
            case .frequentlyBooked(_):
                break
            default:
                return
            }
        } catch {
            print(error)
        }
    }

    func testCloseoutUpsells_filtersThemOutFromTotalUpsells() {
        // GIVEN
        // Hotel with closeouts
        let hotel = try! Hotel(dictionary: ["hotelCode": "BRIPTI"])
        hotel.ancillaryCloseout = [
            AncillaryCloseOutItem(
                serviceCode: "Meal Deal",
                upsellCodes: "MDP",
                startDateString: "25/06/2026",
                endDateString: "25/07/2026"
            )
        ]

        // all meals coming from BE
        let mealDeal = try! UpsellItem(dictionary: [
            "operaId": "MDP",
            "legend": "Meal Deal"
        ])
        let continental = try! UpsellItem(dictionary: [
            "operaId": "BFADCT",
            "legend": "Continental Breakfast"
        ])
        let premierInn = try! UpsellItem(dictionary: [
            "operaId": "BFADBF",
            "legend": "Unlimited Premier Inn Breakfast"
        ])
        let upsells = [mealDeal, continental, premierInn]

        // customer's arrival and departure date
        let arrivalDate = {
            var dateComponents = DateComponents()
            dateComponents.year = 2026
            dateComponents.month = 6
            dateComponents.day = 12
            return Calendar.current.date(from: dateComponents)!
        }()
        let departureDate = {
            var dateComponents = DateComponents()
            dateComponents.year = 2026
            dateComponents.month = 9
            dateComponents.day = 14
            return Calendar.current.date(from: dateComponents)!
        }()

        // WHEN
        let result = hotel.closeoutUpsells(
            arrivalDate: arrivalDate,
            departureDate: departureDate,
            upsells: upsells
        )

        // THEN
        XCTAssertEqual(result.count, 1)
        XCTAssertEqual(result.first?.id, "MDP")
    }
}
