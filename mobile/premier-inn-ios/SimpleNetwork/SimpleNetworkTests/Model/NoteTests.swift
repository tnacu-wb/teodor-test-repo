//
//  NoteTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class NoteTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testNote() {

		let note = Note(dictionary: ["date": "12-12-12", "text": "test", "priority": 1])
		XCTAssertEqual(note?.dateString, "12-12-12")
		XCTAssertEqual(note?.text, "test")
		XCTAssertEqual(note?.priority, 1)
	}
    
}
