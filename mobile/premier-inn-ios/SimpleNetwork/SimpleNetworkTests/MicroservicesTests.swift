//
//  MicroservicesTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 17/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation
import Alamofire
@testable import SimpleNetwork

class MicroservicesTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    let webservice = Webservice.liveMicroservices
    
    func testPaymentMethodsDecoding() {
        let fileURL = Bundle.module.url(forResource: "paymentMethods", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        
        do {
            let decoded = try JSONDecoder().decode(PaymentMethodsResponse.self, from: data)
            XCTAssertNotNil(decoded, "Decoded should not be nil")
        } catch {
            XCTFail("Decoding fail with error: \(error)")
        }
    }

    func testUpdateUser() {

        do {
            // No email address
            let dict1: PIDictionary = ["contactDetail": ["title": "Mr", "firstName": "Pippo", "lastName": "Paperino"]]
            let user1 = try User(dictionary: dict1, sessionId: nil)
            XCTAssertThrowsError(try webservice.updateUserDetails(user: user1, sensorData: ""))

            // Session-id is nil
            let dict2: PIDictionary = ["contactDetail": ["title": "Mr", "firstName": "Pippo", "lastName": "Paperino", "email": "adas@dasd.com"]]
            let user2 = try User(dictionary: dict2, sessionId: nil)
            XCTAssertThrowsError(try webservice.updateUserDetails(user: user2, sensorData: ""))

            // Session-id is empty
            let user3 = try User(dictionary: dict2, sessionId: "")
            XCTAssertThrowsError(try webservice.updateUserDetails(user: user3, sensorData: ""))

            // User is ok now
            let contactDictionary: PIDictionary = [
                "title": "Mr",
                "firstName": "Pippo",
                "lastName": "Paperino",
                "email": "macello@text.com",
                "address": ["line1": "Herbert Road"]
                ]
            let user = try User(dictionary: ["contactDetail": contactDictionary], sessionId: "fake-session")

            let resource = try webservice.updateUserDetails(user: user, sensorData: "")

            // Wrong response type
            XCTAssertThrowsError(try resource.parse(""))

            // Success
            XCTAssertNoThrow(try resource.parse(["success": true]))

        } catch {
            XCTFail("Unexpected error: \(error)")
        }
    }

    func testForgotPassword() {

        let resource = try? webservice.forgotPassword(emailAddress: "email", isBusiness: false)
        XCTAssertNotNil(resource)

        XCTAssertThrowsError(try resource?.parse(""))
        XCTAssertThrowsError(try resource?.parse(["addresses": "data"]))
        XCTAssertNotNil(try resource?.parse(["success": true]))
    }

    func testDateParameterFormatting() {

        var dateComponents = DateComponents()
        dateComponents.year = 2016
        dateComponents.month = 9
        dateComponents.day = 12

        let date = Calendar.current.date(from: dateComponents)!

        XCTAssertEqual(date.parameterString, "2016-09-12")
        XCTAssertEqual(date.creditCardDateFormat, "09/16")
        XCTAssertEqual(date.expiryDateFormat, "2016-09-12 00:00:00")
        XCTAssertEqual(date.analyticsDateFormat, "12/09/2016")
    }

	func testMethods() {

		let webservice = Microservices(scheme: "http", host: "www.pippo.com", port: 44)
		let address = try! Address(dictionary: [:])
		let user: User = {
			let user = try! User(dictionary: ["contactDetail" : ["title" : "Mr", "firstName": "Pippo", "lastName": "Paperino", "email": "pippo@gmail.com", "mobile": "2131321"]], sessionId: nil)
			user.address = address
			user.bookingPreference = BookingPreference(
				foodPreference: MealOption.none,
				wantSmsConfirmations: false,
				preselectWifi: false,
				roomRequirements: RoomRequirements(hotelBrand: "PI", lettingType: nil, adults: 1, children: 0, cotRequired: false, type: .accessible)
			)

			return user
		}()
		let bookingDetails: BookingDetails = {
			let details = BookingDetails()
			details.basketReference = "asdas"
			details.hotel = HotelTests.hotel

			details.rate = Rate(
                uniqueID: UUID(),
                isBiggerRoom: false,
				code: "C",
				totalCost: Cost(amount: 12, currencyCode: "GBP"),
				description: nil,
                name: "Flex",
				text: nil,
                classification: "A",
				rooms: {
					let room = Room()
					room.leadGuest = user

					return [room]
				}(),
				upsellItems:nil,
                cellCode: .none,
                promotionCode: nil,
                lettingTypes: []
			)
			details.booker = user
            let paymentCard = Card(token: "11112222333344445555", expiryMonth: "01", expiryYear: "30", type: CardType(cardCode: "AM", cardName: "Visa"), logoUrl: "https://test.com", cardholderName: "John Doe", cardType: "AM", cnpRequired: false)
    //        paymentCard.address = try? Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"])

            details.primaryPaymentMethod = PaymentOption(name: "Visa", type: "NEW_CARD", logoSrc: nil, order: 1, acceptedCardTypes: [], enabled: true, cnpOptionAvailable: true, card: paymentCard, paymentOptions: [PaymentSettlementOption(type: PaymentIntervalOption.later.rawValue, enabled: true), PaymentSettlementOption(type: PaymentIntervalOption.now.rawValue, enabled: true)], reasons: [], clientToken: nil, subType: nil)

			details.criteria = {
				var criteria = Criteria()
				criteria.rooms = {
					let room = Room()
					room.leadGuest = user

					return [room]
				}()

				return criteria
			}()
            details.roomLettings = {
                let room = Room()
                room.leadGuest = user

                return [room]
            }()

			return details
		}()

        UserSessionManager.sharedInstance.idToken = "XXX"

        let reservationDetails = ReservationDetails(reservationId: "XXX", surname: "XXX", arrivalDate: Date(), business: false, token: nil)

        XCTAssertThrowsError(try webservice.hotelAvailability(hotelCode: "XXX", hotelBrand: HotelBrand.premierInn, bookingDetails: bookingDetails, allowEmployeeOffer: true))
        XCTAssertThrowsError(try webservice.holdBooking(bookingDetails: bookingDetails, sensorData: ""))
		XCTAssertThrowsError(try webservice.releaseBooking(basketReference: "XXX", hotelId: nil))
        XCTAssertThrowsError(try webservice.reservation(reservationDetails: reservationDetails, hotelCode: nil, bookingDetails: nil))
		XCTAssertNoThrow(try webservice.suggestions(searchTerm: "XXX"))
        XCTAssertNoThrow(try webservice.forgotPassword(emailAddress: "XXX", isBusiness: false))

        // non-3CP payment fails
//		XCTAssertNoThrow(try webservice.payment(bookingDetails: bookingDetails, cv2: nil, payingOnArrival: false))
//		XCTAssertNoThrow(try webservice.getHotel(with: "XXX"))
		XCTAssertNoThrow(try webservice.savePaymentCard(for: user, sensorData: ""))
		XCTAssertNoThrow(try webservice.updateUserAdditionalGuests(user: user, sensorData: ""))
		XCTAssertNoThrow(try webservice.updateUserDetails(user: user, sensorData: ""))
		XCTAssertNoThrow(try webservice.deletePaymentCard(for: user, sensorData: ""))
        XCTAssertNoThrow(try webservice.changePassword(user: user, existingPassword: "password", newPassword: "XXX", sensorData: ""))
		XCTAssertNoThrow(try webservice.updateFoodPreference(user: user, sensorData: ""))
		XCTAssertNoThrow(try webservice.updateRoomPreference(user: user, sensorData: ""))
	}

    // TODO: these are failing due to race condition (?)
    func testDonation() {

//        let donation1Expectation = self.expectation(description: "donation1")
//        let donation2Expectation = self.expectation(description: "donation2")
//        let donation3Expectation = self.expectation(description: "donation3")
//
//
//        wait(for: [donation1Expectation], timeout: 2)

        let params = Cost(amount: 0.3, currencyCode: "GBP").toDictionary
        XCTAssertEqual(params["amount"] as? String, "0.3")
        XCTAssertEqual(params["currency"] as? String, "GBP")
        let string = try! jsonString(with: params)
        XCTAssertTrue(string.contains("0.3"))
        XCTAssertTrue(string.contains("GBP"))

//        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1, execute: { donation1Expectation.fulfill() })

        let params2 = Cost(amount: 3, currencyCode: "GBP").toDictionary
        XCTAssertEqual(params2["amount"] as? String, "3.0")
        XCTAssertEqual(params2["currency"] as? String, "GBP")
        let string2 = try! jsonString(with: params2)
        XCTAssertTrue(string2.contains("3.0"))
        XCTAssertTrue(string2.contains("GBP"))

//        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1, execute: { donation2Expectation.fulfill() })
//
//        wait(for: [donation2Expectation], timeout: 2)

        let params3 = Cost(amount: 0, currencyCode: "GBP").toDictionary
        XCTAssertEqual(params3["amount"] as? String, "0.0")
        XCTAssertEqual(params3["currency"] as? String, "GBP")
        let string3 = try! jsonString(with: params3)
        XCTAssertTrue(string3.contains("0.0"))
        XCTAssertTrue(string3.contains("GBP"))

//        wait(for: [donation3Expectation], timeout: 2)
    }

    func testGetCompany() {

        let webservice = Webservice.uatMicroservicesAlpha2

        UserSessionManager.sharedInstance.idToken = "XXX"

        XCTAssertNoThrow(try webservice.getCompany(companyId: "1", sensorData: ""))
        XCTAssertNoThrow(try webservice.getCompany(companyId: "1", sensorData: ""))

        UserSessionManager.sharedInstance.idToken = nil
        XCTAssertThrowsError(try webservice.getCompany(companyId: "1", sensorData: ""))
    }
}

private enum TestError: Error {
    case missingBody
    case missingJsonString
}

private extension MicroservicesTests {

    func jsonString(with params: [String: Any]) throws -> String {

        let expectation = self.expectation(description: "jsonString signedRequest")

        let resource: Resource<String> = Resource(url: URL(string: "https://www.google.com")!, parameters: params, method: .post, encoding: JSONEncoding.default, headers: nil) { _ in return "" }

        let requestManager = RequestsManager()
        let signedRequest: DataRequest = try requestManager.signedRequest(with: resource)

        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1, execute: { expectation.fulfill() })

        wait(for: [expectation], timeout: 2)

        guard let body = signedRequest.request?.httpBody else { throw TestError.missingBody }
        guard let jsonString = String(data: body, encoding: .utf8) else { throw TestError.missingJsonString }

        return jsonString
    }
}
