//
//  PaymentMethodsTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 09/06/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class PaymentMethodsTests: XCTestCase {

    func testGraphQLPaymentMethodsMapper() {
        let fileURL = Bundle.module.url(forResource: "graphQLPaymentMethods", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let paymentMethodsDictionary = dataDictionary["paymentMethods"] as! [PIDictionary]
        let paymentMethodsMapped = PaymentMethodsMapper.map(from: paymentMethodsDictionary)

        let paymentMethodsData = try! JSONSerialization.data(withJSONObject: paymentMethodsMapped)

        do {
            let paymentMethods = try JSONDecoder().decode(PaymentMethodsResponse.self, from: paymentMethodsData)
            let savedCard = paymentMethods.paymentMethods!.first!
            XCTAssertEqual(savedCard.name, "CARD")
            XCTAssertEqual(savedCard.type, "SAVED_CARD")
            XCTAssertEqual(savedCard.order, 1)
            XCTAssertEqual(savedCard.enabled, true)
            XCTAssertEqual(savedCard.card?.token, "5479321898918651100")
            XCTAssertEqual(savedCard.card?.type, CardType(cardCode: "MD", cardName: nil))
            XCTAssertEqual(savedCard.card?.cardType, "BUSINESS_PERSONAL_STORED_CARD")
            XCTAssertEqual(savedCard.card?.cardholderName, "test")
            XCTAssertEqual(savedCard.card?.logoUrl, "/content/dam/global/booking/MD.jpg")

            let reserveWithoutCard = paymentMethods.paymentMethods!.first(where: { $0.type == "RESERVE_WITHOUT_CARD" })!
            XCTAssertEqual(reserveWithoutCard.name, "RESERVE_WITHOUT_CARD")
            XCTAssertEqual(reserveWithoutCard.enabled, true)

        } catch {
            XCTFail("Decoding failed: \(error)")
        }
    }

    func testRestPaymentMethodsDecoding() {
        let fileURL = Bundle.module.url(forResource: "restPaymentMethods", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let paymentMethodsDictionary = jsonDictionary["paymentMethods"] as! [PIDictionary]
        let paymentMethodsData = try! JSONSerialization.data(withJSONObject: paymentMethodsDictionary)

        do {
            let paymentMethods = try JSONDecoder().decode([PaymentOption].self, from: paymentMethodsData)
            let savedCard = paymentMethods.first!
            XCTAssertEqual(savedCard.name, "CARD")
            XCTAssertEqual(savedCard.type, "SAVED_CARD")
            XCTAssertEqual(savedCard.order, 1)
            XCTAssertEqual(savedCard.enabled, true)
            XCTAssertEqual(savedCard.card?.token, "5479321898918651111")
            XCTAssertEqual(savedCard.card?.type, CardType(cardCode: "MD", cardName: nil))
            XCTAssertEqual(savedCard.card?.cardType, "LEISURE_STORED_CARD")
            XCTAssertEqual(savedCard.card?.cardholderName, "Santa")
            XCTAssertEqual(savedCard.card?.logoUrl, "/content/dam/global/booking/MD.jpgREST")
        } catch {
            XCTFail("Decoding failed: \(error)")
        }
    }

    
}
