//
//  UpsellItemTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class UpsellItemTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testUpsellItem() {

		XCTAssertThrowsError(try UpsellItem(dictionary: ["rub": "up"]))

		do {
			let upsellItem = try UpsellItem(dictionary: [
                "operaId": "MDP",
				"code": "17",
				"legend": "Meal Deal",
				"price": ["amount": "26.49",
						  "currency": "GBP"],
				"description": "<p>Enjoy a tempting Premier Inn Breakfast, including delicious cooked and continental dishes, plus 2-course dinner and a selected drink.&nbsp;</p>\n",
				"imagePath": "/content/dam/global/restaurants/Global/meal-deal-booking.png",
				"additionalInfo": "<p>Certain dishes will incur a supplementary charge.</p>\n",
				"availableForChildren": false,
				"freeBreakfastTrigger": true,
				"freeBreakfastCode": "15",
				"foodUpsell": true,
				"allergyInfoSrc": "/content/dam/global/restaurants/THY/Main menu CD.pdf"
                ]
            )
			XCTAssert(upsellItem.kidsHaveToPay == false)
			XCTAssert(upsellItem.upsellOperaId == UpsellItemOperaId.mealDeal)
		} catch {
			print(error)
			XCTFail()
		}
	}

	func testModel() {

		var upsellDic: PIDictionary = [
            "operaId": "BFADBF",
			"code": "11",
			"legend": "Premier Inn Breakfast",
			"price": [
				"amount": "10.50",
				"currency": "GBP"
			],
			"foodUpsell": true,
			"description": "Some description",
			"imagePath": "/content/dam/global/restaurants/Global/meal-deal-booking.png"
		]
		do {
			let breakfastUpsell = try UpsellItem(dictionary: upsellDic)
			XCTAssertNotNil(breakfastUpsell.itemDescription)
		}
		catch {
			XCTFail("Valid upsell dictionary rejected")
		}

		upsellDic.removeValue(forKey: "legend")
		XCTAssertThrowsError(try UpsellItem(dictionary: upsellDic))
		upsellDic.removeValue(forKey: "code")
		XCTAssertThrowsError(try UpsellItem(dictionary: upsellDic))
	}

	func testMiddlewareOldModel() {

		var upsellDic: PIDictionary = [
            "operaId": "BFADBF",
			"code": 11,
			"legend": "Premier Inn Breakfast",
			"price": [
				"amount": "10.50",
				"currency": "GBP"
			],
			"description": "Some description",
			"additionalInfo": "Some additional info"
		]
		do {
			let breakfastUpsell = try UpsellItem(dictionary: upsellDic)
			XCTAssertNotNil(breakfastUpsell.itemDescription)
		}
		catch {
			XCTFail("Valid upsell dictionary rejected")
		}

		upsellDic.removeValue(forKey: "legend")
		XCTAssertThrowsError(try UpsellItem(dictionary: upsellDic))
		upsellDic.removeValue(forKey: "code")
		XCTAssertThrowsError(try UpsellItem(dictionary: upsellDic))
	}

    func testQuantityPerMealUpsell_IsEqualsTo_NumberOfAdults() {
        let mealUpsell = try! UpsellItem(dictionary: [
            "operaId": "MDP",
            "code": "17",
            "legend": "Meal Deal",
            "foodUpsell": true
        ])
        let room = Room(dictionary: [
            "adults" : 2,
            "children" : 2]
        )

        let upsellQuantity = UpsellQuantity(upsellItem: mealUpsell, room: room)
        XCTAssertEqual(upsellQuantity.quantityPerUpsell, 2)
    }

    func testQuantityPerWifiUpsell_IsEqualsTo_One() {
        let wifiUpsell = try! UpsellItem(dictionary: [
            "operaId": "FI24HR",
            "code": "135",
            "legend": "Upgrade to Ultimate Wi-Fi (24h)"
        ])

        let upsellQuantity = UpsellQuantity(upsellItem: wifiUpsell)
        XCTAssertEqual(upsellQuantity.quantityPerUpsell, 1)
    }

    func testQuantityPerCheckinCheckoutUpsell_IsEqualsTo_One() {
        let checkinCheckoutUpsell = try! UpsellItem(dictionary: [
            "operaId": "HSCKIN",
            "legend": "Early check-in",
            "isExtraUpsell": true
        ])

        let upsellQuantity = UpsellQuantity(upsellItem: checkinCheckoutUpsell)
        XCTAssertEqual(upsellQuantity.quantityPerUpsell, 1)
    }
}
