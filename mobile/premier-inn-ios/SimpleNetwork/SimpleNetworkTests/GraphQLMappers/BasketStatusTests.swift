//
//  BasketStatusTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 12/05/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

enum MockBasketFile: String {
    case failed = "graphQLBasketStatus"
    case ciolFailed = "graphQLBasketStatusCiolFailed"
}

class BasketStatusTests: XCTestCase {

    func basket(fileName: MockBasketFile) -> Basket? {
        let fileURL = Bundle.module.url(forResource: fileName.rawValue, withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let basketStatusDict = dataDictionary["basketStatus"] as! PIDictionary

        do {
            let basketStatusData = try JSONSerialization.data(withJSONObject: basketStatusDict, options: .prettyPrinted)

            let decoder = JSONDecoder()
            return try decoder.decode(Basket.self, from: basketStatusData)
        } catch {
            XCTFail("error thrown when decoding Basket Information")
        }
        return nil
    }

    func testBasketStatusFailed() {
        let newBasketStatus = basket(fileName: .failed)
        XCTAssert(newBasketStatus!.basketStatus == .failed)
        XCTAssert(newBasketStatus!.basketError!.code == "Err01")
        XCTAssert(newBasketStatus!.basketError!.description == "Fraud check failed")
    }
    
    func testBasketStatusCiolFailed() {
        let newBasketStatus = basket(fileName: .ciolFailed)
        XCTAssert(newBasketStatus!.basketStatus == .ciolFailed)
    }

}
