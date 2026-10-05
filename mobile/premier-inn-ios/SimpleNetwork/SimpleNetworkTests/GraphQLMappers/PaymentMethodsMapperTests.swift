//
//  PaymentMethodsMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 21/09/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
final class PaymentMethodsMapperTests: XCTestCase {

    private func getDataFor(fileName: String) -> Data {
        let fileURL = Bundle.module.url(forResource: fileName, withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let paymentMethods = dataDictionary["paymentMethods"] as! [PIDictionary]
        let paymentMethodsMapped = PaymentMethodsMapper.map(from: paymentMethods)

        return try! JSONSerialization.data(withJSONObject: paymentMethodsMapped)
    }

    func testReserveWithoutCard() {
        let paymentMethodsData = getDataFor(fileName: "graphQLPaymentMethods")
        let response: PaymentMethodsResponse = try! JSONDecoder().decode(PaymentMethodsResponse.self, from: paymentMethodsData)

        let rwcMethod = response.paymentMethods!.first(where: { $0.name == "RESERVE_WITHOUT_CARD" })!
        XCTAssertEqual(rwcMethod.name, "RESERVE_WITHOUT_CARD")
        XCTAssertEqual(rwcMethod.enabled, true)
        XCTAssertEqual(rwcMethod.type, "RESERVE_WITHOUT_CARD")
        XCTAssertEqual(rwcMethod.order, 4)
        XCTAssertEqual(rwcMethod.cnpOptionAvailable, false)
    }

    func testUpdateEnabledStatusForPaymentMethods() {
        let paymentMethodsData = getDataFor(fileName: "paymentMethodsForRWCOnly")
        let response: PaymentMethodsResponse = try! JSONDecoder().decode(PaymentMethodsResponse.self, from: paymentMethodsData)

        XCTAssertEqual(response.paymentMethods!.count, 4)
        // SAVED CARD
        XCTAssertEqual(response.paymentMethods![0].name, "CARD")
        XCTAssertEqual(response.paymentMethods![0].enabled, false)

        // NEW CARD
        XCTAssertEqual(response.paymentMethods![1].name, "CARD")
        XCTAssertEqual(response.paymentMethods![1].enabled, false)

        // PIBA CARD
        XCTAssertEqual(response.paymentMethods![2].name, "PIBA")
        XCTAssertEqual(response.paymentMethods![2].enabled, false)

        // RWC
        XCTAssertEqual(response.paymentMethods![3].name, "RESERVE_WITHOUT_CARD")
        XCTAssertEqual(response.paymentMethods![3].enabled, true)
    }
}
