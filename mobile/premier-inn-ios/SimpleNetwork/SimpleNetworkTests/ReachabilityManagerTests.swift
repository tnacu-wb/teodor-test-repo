//
//  ReachabilityManagerTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 29/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

private class ReachabilityDelegate: ReachabilityManagerDelegate {

	var networkBecameReachableDidCall = false
	var networkNotReachableDidCall = false

	func networkBecameReachable() {

		networkBecameReachableDidCall = true
	}

	func networkNotReachable() {

		networkNotReachableDidCall = true
	}
}

class ReachabilityManagerTests: XCTestCase {

	private var reachabilityDelegate: ReachabilityDelegate!

    override func setUp() {
        super.setUp()

		reachabilityDelegate = ReachabilityDelegate()
    }
    
    override func tearDown() {

		reachabilityDelegate = nil

		super.tearDown()
    }
    
	func testReachabilityManager() {

		let manager = ReachabilityManager()
		manager.delegate = reachabilityDelegate
		manager.startObserving()

		let delegateMethodsDidCall = reachabilityDelegate.networkNotReachableDidCall || reachabilityDelegate.networkBecameReachableDidCall

		XCTAssertTrue(delegateMethodsDidCall)
	}
    
}
