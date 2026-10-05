//
//  HotelResultsTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 17/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn_DEV

class HotelResultsTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testHotelResponse_EmptyData() {

        class MockRequestsManager: RequestsManager {

            override func load<T>(resource: Resource<T>, timeout: NSTimeInterval, completion: ((result: T?, error: NSError?) -> Void)?) {

                completion?(result: nil, error: nil)
            }
        }

        let hotels: [Hotel] = {

            let fileURL = NSBundle(forClass: self.dynamicType).URLForResource("hotelInfo", withExtension: "json")!
            let	data = NSData(contentsOfURL: fileURL)!
            let jsonDictionary = try! NSJSONSerialization.JSONObjectWithData(data, options: .AllowFragments) as! PIDictionary

            let hotel = Hotel(dictionary:jsonDictionary)

            return [hotel]
        }()

        let viewModel = HotelSearchResultViewModel(hotels: hotels, numberOfHotelsExpected: 2)
        viewModel.requestsManager = MockRequestsManager()

        XCTAssertEqual(viewModel.numberOfSections, 2, "Expecting one room section + load more section")

        try! viewModel.loadMoreHotelsIfNeeded()

        XCTAssertEqual(viewModel.numberOfSections, 1, "Expecting only one room section because mocked response is empty so we will not allow any more attempts")
    }

    func testHotelResponse() {

        class MockRequestsManager: RequestsManager {

            override func load<T>(resource: Resource<T>, timeout: NSTimeInterval, completion: ((result: T?, error: NSError?) -> Void)?) {

                let fileURL = NSBundle(forClass: self.dynamicType).URLForResource("hotelResults", withExtension: "json")!
                let	data = NSData(contentsOfURL: fileURL)!
                let jsonDictionary = try! NSJSONSerialization.JSONObjectWithData(data, options: .AllowFragments) as! PIDictionary

                let result = try! resource.parse(data: jsonDictionary)

                completion?(result: result, error: nil)
            }
        }

        let hotels: [Hotel] = {

            let fileURL = NSBundle(forClass: self.dynamicType).URLForResource("hotelInfo", withExtension: "json")!
            let	data = NSData(contentsOfURL: fileURL)!
            let jsonDictionary = try! NSJSONSerialization.JSONObjectWithData(data, options: .AllowFragments) as! PIDictionary

            let hotel = Hotel(dictionary:jsonDictionary)

            return [hotel]
        }()

        let viewModel = HotelSearchResultViewModel(hotels: hotels, numberOfHotelsExpected: 2)
        viewModel.requestsManager = MockRequestsManager()

        XCTAssertEqual(viewModel.numberOfSections, 2, "Expecting one room section + load more section")

        try! viewModel.loadMoreHotelsIfNeeded()

        XCTAssertEqual(viewModel.numberOfSections, 3, "Expecting 3 room sections because mocked contains two additional hotel to display")
        XCTAssertEqual(viewModel.numberOfHotelsExpected, viewModel.hotels.count)
    }

}
