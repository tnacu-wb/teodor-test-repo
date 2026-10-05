//
//  HotelTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 24/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class HotelTests: XCTestCase {

	static let hotel: Hotel = {

        let fileURL = Bundle.module.url(forResource: "hotelInfo", withExtension: "json")!
		let	data = try! Data(contentsOf: fileURL)
		let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

		return try! Hotel(dictionary: jsonDictionary)
	}()

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

	func testHotel() {

        let fileURL = Bundle.module.url(forResource: "hotelInfo", withExtension: "json")!
		let	data = try! Data(contentsOf: fileURL)
		let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

        do {
            let hotel = try Hotel(dictionary: jsonDictionary)

            XCTAssertEqual(hotel.code, "BRIPTI")
            XCTAssertEqual(hotel.name, "Brighton City Centre")
            XCTAssertEqual(hotel.title, "Brighton City Centre")
            XCTAssertEqual(hotel.distance, 0.44)
            XCTAssertEqual(hotel.rates.count, 2)
            XCTAssertEqual(hotel.limitedAvailability, false)

            XCTAssertEqual(hotel.parkings.count, 1)
            XCTAssertEqual(hotel.parkings.first?.image, UIImage(named: "COP")?.withRenderingMode(.alwaysTemplate))
            
            XCTAssertEqual(hotel.cheapestRate?.classification, "F")
            XCTAssertEqual(hotel.rate(for: "F")?.code, "SV743")
            XCTAssertEqual(hotel.acceptedCreditCards?.count, 8)

            let isFavourite = hotel.isFavourite
            XCTAssertTrue(isFavourite == hotel.isFavourite)
            _ = hotel.toggleFavourite()
            XCTAssertFalse(isFavourite == hotel.isFavourite)

            hotel.update(with: ["hotelDescription": "<arse><a />A lovely joint</arse>",
                                "hotelDirections": "<htmlisgreat>Go straight ahead</htmlisgreat>",
                                "parkingDescription": "Pick a space"])
            XCTAssert(hotel.hotelDescription == "A lovely joint")
            XCTAssert(hotel.directions == "Go straight ahead")
            XCTAssert(hotel.parkingDescription == "Pick a space")
        } catch {
            XCTFail("\(error)")
        }
	}

    func testHotelRoomConfiguration() {

        let fileURL = Bundle.module.url(forResource: "hotelInfo", withExtension: "json")!
        guard let data = try? Data(contentsOf: fileURL) else { return }
        guard let jsonDictionary = try? JSONSerialization.jsonObject(with: data, options: .allowFragments) as? PIDictionary else { return }

        guard let hotelInfoDic = jsonDictionary["hotelInfo"] as? PIDictionary else { return }
        guard let hotelRoomConfigDic = hotelInfoDic["hotelRoomConfiguration"] as? PIDictionary else { return }

        let hotelRoomConfig = Hotel.hotelRoomConfiguration(with: hotelRoomConfigDic)

        print("hello")

        XCTAssertNotNil(hotelRoomConfig)
    }

    func testHotelWithHotelInfo() {

        let dictionary: PIDictionary = ["hotelInfo": ["name": "Hotel name", "code": "FAKECODE", "brand": "PI", "address": ["postcode": "POST CODE", "line1": ""]]]
        XCTAssertNoThrow(try Hotel(dictionary: dictionary))

        let dictionary2: PIDictionary = ["hotelCode": "FAKECODE", "hotelBrand": "PI", "hotelInfo": ["name": "Hotel name", "code": "FAKECODE", "address": ["postcode": "POST CODE", "line1": ""]]]
        XCTAssertNoThrow(try Hotel(dictionary: dictionary2))

        let dictionary3: PIDictionary = ["code": "FAKECODE", "brand": "PI", "address": ["postcode": "POST CODE", "line1": ""]]
        XCTAssertNoThrow(try Hotel(dictionary: dictionary3))

        let dictionary4: PIDictionary = ["brand": "PI", "address": ["postcode": "POST CODE", "line1": ""]]
        XCTAssertThrowsError(try Hotel(dictionary: dictionary4))
    }

	func testHotelLocation() {

		let coordinate = Hotel.coordinate(dictionary: ["latitude": 51.50725, "longitude": 0.03571])
		XCTAssertEqual(coordinate.latitude, 51.50725)
		XCTAssertEqual(coordinate.longitude, 0.03571)

		let coordinate2 = Hotel.coordinate(dictionary: ["longitude": 0.03571])
		XCTAssertEqual(coordinate2.latitude, 0)
		XCTAssertEqual(coordinate2.longitude, 0)
	}

	func testHotelBrand() {

		XCTAssertEqual(Hotel.brand(rawString: "PI"), .premierInn)
		XCTAssertEqual(Hotel.brand(rawString: "HUB"), .hub)
		XCTAssertEqual(Hotel.brand(rawString: nil), .premierInn)
	}

	func testHotelImages() {

		let dictionaries: [PIDictionary] = [
			["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/L/LONDOC/LONDOC 1.jpg"],
			["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/L/LONDOC/LONDOC 2.jpg"],
			["blablabla": "/content/dam/pi/websites/hotelimages/gb/en/L/LONDOC/LONDOC 2.jpg"],
			]
		let images = Hotel.images(dictionaries: dictionaries)

		XCTAssertEqual(images.count, 2)
		XCTAssertEqual(Hotel.images(dictionaries: nil).count, 0)
	}

	func testHotelMessagingFlag() {

		XCTAssertNotNil(MessagingFlag(dictionary: ["flagColor": "aa53b7", "flagText": "New hotel"]))
		XCTAssertNil(MessagingFlag(dictionary: ["flagText": "New hotel"]))
		XCTAssertNil(MessagingFlag(dictionary: ["flagColor": "aa53b7", "flagText": ""]))
	}

	func testHotelFacilities() {

		let dictionaries: [PIDictionary] = [
			["code": "DIS", "legend": "Universally accessible rooms"],
			["code": "LFT", "legend": "Lift"]
		]
		let facilities = Hotel.facilities(dictionaries: dictionaries)

		XCTAssertNotNil(facilities)
		XCTAssertEqual(facilities?.count, 2)
		XCTAssertNil(Hotel.facilities(dictionaries: nil))
	}

	func testHotelRates() {

        XCTAssertEqual(Hotel.rates(dictionaries: nil).count, 0)

        let dictionaries: [PIDictionary] = [
            [
                "code": "SV865",
                "description": "Premier Saver",
                "text": "Pay Now, Save More",
                "classification": "F",
                "sleepParkFly": false,
                "packageLength": 0,
                "rooms": [["type": "DB", "substitutedType": false]],
                "totalCost": ["amount": "42.00", "currency": "GBP"]
            ],
            [
                "code": "SV865",
                "description": "Slee park fly",
                "text": "Fake text",
                "classification": "D",
                "sleepParkFly": false,
                "packageLength": 0,
                "rooms": [["type": "DB", "substitutedType": false]],
                "totalCost": ["amount": "42.00", "currency": "GBP"]
            ]
        ]

        let rates = Hotel.rates(dictionaries: dictionaries)
		XCTAssertNotNil(rates)
		XCTAssertEqual(rates.count, 2)
	}

	func testCheapestRate() {

		let hotel = HotelTests.hotel

		XCTAssertEqual(hotel.cheapestRate?.totalCost.amount, 76.0)
	}

	func testHotelNotes() {

		let dictionaries = [
			["something" : "somecontent"],
			["something" : "somecontent2"]
		]
		let notes = Hotel.notes(dictionaries: dictionaries)
		
		XCTAssertNil(Hotel.notes(dictionaries: nil))
		XCTAssertNotNil(notes)
		XCTAssertEqual(notes?.count, 2)
	}

    func testHotelAcceptedCreditCards() {

        let dictionaries = [
            ["code": "AC",
             "feeAmount": "2.00",
             "feeCurrency": "£",
             "paymentOnly": false,
             "listOrder": "3",
             "name": "Mastercard Credit",
             "schemeLogo": "/content/dam/global/booking/Mastercard.jpg"],
            ["code": "AM",
             "feeAmount": "2.00",
             "feeCurrency": "£",
             "paymentOnly": false,
             "listOrder": "6",
             "name": "American Express",
             "schemeLogo": "/content/dam/global/booking/AX.jpg"]
        ]
        let acceptedCreditCards = Hotel.acceptedCreditCards(dictionaries: dictionaries)

        XCTAssertNil(Hotel.notes(dictionaries: nil))
        XCTAssertNotNil(acceptedCreditCards)
        XCTAssertEqual(acceptedCreditCards?.count, 2)
        XCTAssertEqual(acceptedCreditCards?.first?.listOrder, 3)
    }

    func testHotelRoomImageIndex() {

        let hubStandardRoomImageIndex = HotelTests.hotel.indexOfRoomImage(with: "hub-standard-room")

        XCTAssertEqual(hubStandardRoomImageIndex, 1)
    }

    func testGetDistanceStringWhenDistanceIsNotZero() {
        let sut = HotelTests.hotel

        let result = sut.getDistanceString(formatter: LengthFormatter())

        XCTAssertEqual(result, "0.44 mi")
    }

    func testGetDistanceStringWhenDistanceIsZero() {
        let dictionary: PIDictionary = ["hotelInfo": [
            "name": "Hotel name",
            "code": "FAKECODE",
            "brand": "PI",
            "address": ["postcode": "POST CODE", "line1": ""]
        ]]

        let sut = try! Hotel(dictionary: dictionary)

        let result = sut.getDistanceString(formatter: LengthFormatter())

        XCTAssertNil(result)
    }
}
