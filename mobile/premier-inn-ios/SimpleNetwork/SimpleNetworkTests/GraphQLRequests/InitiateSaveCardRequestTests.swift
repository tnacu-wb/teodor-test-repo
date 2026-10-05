//
//  InitiateSaveCardRequestTests.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 03/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

final class InitiateSaveCardRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    var address: Address? {
        do {
             let address = try Address(dictionary: [
                "line1": "line1",
                "line2": "line2",
                "line3": "line3",
                "postCode": "postCode",
                "country": "GB"
            ])
            return address
        } catch {
            XCTFail("\(error)")
            return nil
        }
    }

    func testInitiateSaveCard() {

        let initiateSaveCardDetails = InitiateSaveCardDetails(cardType: .CARD,
                                                              cnpRequired: false,
                                                              memorableWord: nil)
        let initiateSaveCardParameters = InitiateSaveCardParameters(billingAddress: address!, cardDetails: initiateSaveCardDetails)

        // Valid resource
        do {
            let resource = try webservice.initiateSaveCard(initiateSaveCardParameters: initiateSaveCardParameters)
            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.initiateSaveCardMutation)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let initiateSaveCardRequest = variables?["initiateSaveCardRequest"] as? PIDictionary
            XCTAssertNotNil(initiateSaveCardRequest)

            let billingAddress = initiateSaveCardRequest?["billingAddress"] as? PIDictionary
            XCTAssertNotNil(billingAddress?["line1"])
            XCTAssertNotNil(billingAddress?["line2"])
            XCTAssertNotNil(billingAddress?["line3"])
            XCTAssertNotNil(billingAddress?["line4"])
            XCTAssertNotNil(billingAddress?["postCode"])
            XCTAssertNotNil(billingAddress?["countryCode"])
            XCTAssertNotNil(billingAddress?["type"])
            XCTAssertNotNil(billingAddress?["companyName"])

            let cardDetails = initiateSaveCardRequest?["cardDetails"] as? PIDictionary
            XCTAssertNotNil(cardDetails?["cardType"])
            XCTAssertNotNil(cardDetails?["cnpRequired"])
            XCTAssertNotNil(initiateSaveCardRequest?["country"])
            XCTAssertNil(cardDetails?["memorableWord"])
        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

    func testInitiateSaveCardResponse() {

        let fileURL = Bundle.module.url(forResource: "graphQLInitiateSaveCard", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let initiateSaveCardDict = dataDictionary["saveCard"] as! PIDictionary

        XCTAssertNotNil(initiateSaveCardDict["paymentRedirect"] as! String)

        guard let iframeData = try? JSONSerialization.data(withJSONObject: initiateSaveCardDict, options: .prettyPrinted),
              let paymentRequiredDetails = try? JSONDecoder().decode(CCCPPaymentProviderResponse.self, from: iframeData) else { return XCTFail() }

        XCTAssertNotNil(paymentRequiredDetails.htmlString)
        XCTAssertTrue(paymentRequiredDetails.htmlString?.contains("html") == true)
    }
}
