//
//  MessagingFlagTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class MessagingFlagTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testMessagingFlag() {

		let messagingFlag = MessagingFlag(dictionary: ["flagText": "Hello", "flagColor": "#fff"])
		XCTAssertNotNil(messagingFlag)
	}
    
}
