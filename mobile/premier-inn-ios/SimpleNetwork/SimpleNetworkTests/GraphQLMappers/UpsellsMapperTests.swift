//
//  UpsellsMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Georgios Aikaterinakis on 16/06/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import XCTest

@testable import SimpleNetwork

class UpsellsMapperTests: XCTestCase {

    var sut: [PIDictionary] = []

    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "singleHotelAvailability_getPackages_Response", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let getPackagesDictionary = dataDictionary["packages"] as! PIDictionary

        sut = AvailableUpsellsMapper.map(from: getPackagesDictionary)!
    }

    func testPIBreakfast() {
        let anUpsell = sut.first(where: { ($0["legend"] as! String) == "Premier Inn Breakfast" })!
        XCTAssertNotNil(anUpsell)

        // test the mapping of the graphQL dictionary to keys that we would expect from BART

        // code?
        XCTAssertEqual(anUpsell["legend"] as! String, "Premier Inn Breakfast")
        XCTAssertEqual(anUpsell["imagePath"] as! String, "/content/dam/global/restaurants/Global/full-breakfast-booking.png")
        XCTAssertEqual(anUpsell["description"] as! String, "<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n")
        XCTAssert(anUpsell["foodUpsell"] as! Bool)

        let priceDict = anUpsell["unitCost"] as! PIDictionary
        XCTAssertNotNil(priceDict)
        XCTAssertEqual(priceDict["amount"] as? Double, 9.5)
        XCTAssertEqual(priceDict["currency"] as! String, "GBP")
//        XCTAssert(anUpsell["freeBreakfastTrigger"] as! Bool)

        // create the object with the mapped dictionary and test the expected values

        let upsellObject = try? UpsellItem(dictionary: anUpsell)

        XCTAssertEqual(upsellObject?.legend, "Premier Inn Breakfast")
        XCTAssertEqual(upsellObject?.image?.absoluteString, "https://www.premierinn.com/content/dam/global/restaurants/Global/full-breakfast-booking.png")
        XCTAssertEqual(upsellObject?.itemDescription, "Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.")
        XCTAssert(upsellObject?.foodUpsell == true)
        XCTAssertEqual(upsellObject?.price, Cost(amount: 9.5, currencyCode: "GBP"))
        XCTAssertEqual(upsellObject?.allergenInformation?.path as? String, "/content/dam/global/restaurants/Global/breakfast-allergy.pdf")
        XCTAssertEqual(upsellObject?.allergenInformation?.name as? String, "Premier Inn Breakfast")
        XCTAssertEqual(upsellObject?.menu?.path as? String, "/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf")
        XCTAssertEqual(upsellObject?.menu?.name as? String, "Breakfast menu")
    }

    func testKidsMeal() {
        let anUpsell = sut.first(where: { ($0["legend"] as! String) == "Free breakfast for kids" })!
        XCTAssertNotNil(anUpsell)

        // test the mapping of the graphQL dictionary to keys that we would expect from BART

        XCTAssertEqual(anUpsell["legend"] as! String, "Free breakfast for kids")
        XCTAssertEqual(anUpsell["imagePath"] as! String, "/content/dam/global/restaurants/Global/child-breakfast.jpg")
        XCTAssertEqual(anUpsell["description"] as! String, "<p>Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.</p>\r\n")
        XCTAssertFalse(anUpsell["foodUpsell"] as! Bool)

        let priceDict = anUpsell["unitCost"] as! PIDictionary
        XCTAssertNotNil(priceDict)
        XCTAssertEqual(priceDict["amount"] as? Float ?? 0, 0)
        XCTAssertEqual(priceDict["currency"] as? String ?? "GBP", "GBP")

        // create the object with the mapped dictionary and test the expected values

        let upsellObject = try? UpsellItem(dictionary: anUpsell)

        XCTAssertEqual(upsellObject?.upsellOperaId, UpsellItemOperaId.freeChildBreakfast)
        XCTAssertEqual(upsellObject?.legend, "Free breakfast for kids")
        XCTAssertEqual(upsellObject?.image?.absoluteString, "https://www.premierinn.com/content/dam/global/restaurants/Global/child-breakfast.jpg")
        XCTAssertEqual(upsellObject?.itemDescription, "Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.")
        XCTAssert(upsellObject?.foodUpsell == false)
        XCTAssertEqual(upsellObject?.price, Cost(amount: 0, currencyCode: "GBP"))
        XCTAssertEqual(upsellObject?.menu?.path as? String, "/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf")
        XCTAssertEqual(upsellObject?.menu?.name as? String, "Breakfast menu")
    }

    func testExtraItemsMapping() {
        let extraItems = sut.filter { $0["isExtraUpsell"] != nil && $0["isExtraUpsell"] as! Bool == true }
        XCTAssertEqual(extraItems.count, 2)
    }

    func testEarlyCheckinMapping() {
        let earlyCheckinItem = sut.first(where: { $0["operaId"] as! String == "HSCKIN" })!
        XCTAssertEqual(earlyCheckinItem["legend"] as! String, "Early check-in")
        XCTAssertEqual(earlyCheckinItem["description"] as! String, "Check in any time from 11am (normal check-in time is 3pm).")
    }

    func testUpsellItemsInit() {
        let upsells = sut.map { try! UpsellItem(dictionary: $0) }
        XCTAssertEqual(upsells.count, 6)
    }

    func testLateCheckout() {
        let lateCheckoutDict = sut.first(where: { $0["operaId"] as! String == "HSCOU2" })!
        let lateCheckout = try! UpsellItem(dictionary: lateCheckoutDict)
        XCTAssertEqual(lateCheckout.id, "HSCOU2")
        XCTAssertEqual(lateCheckout.legend, "Late check-out")
        XCTAssertEqual(lateCheckout.availableCount, 3)
        XCTAssertNil(lateCheckout.menu)
    }
}
