//
//  ViewTests.swift
//  PremierInn
//
//  Created by Freddie Parks on 07/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import XCTest
import Formeka
import SimpleNetwork
@testable import PremierInn

class ViewTests: XCTestCase {

    private static let staysDictionary: PIDictionary = {
        let fileURL = Bundle(for: ViewTests.self).url(forResource: "stay", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let stays = jsonDictionary["stays"] as! [PIDictionary]

        return stays.first!
    }()

    private static let hotelDic: PIDictionary = {

        let fileURL = Bundle(for: ViewTests.self).url(forResource: "hotelInfo", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

        return jsonDictionary
    }()

    override func setUp() {
        super.setUp()
    }

    override func tearDown() {
        super.tearDown()
    }
}
