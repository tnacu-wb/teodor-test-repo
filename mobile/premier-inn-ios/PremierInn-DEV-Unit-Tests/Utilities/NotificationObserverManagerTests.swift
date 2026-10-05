//
//  NotificationObserverManagerTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class NotificationObserverManagerTests: XCTestCase {
    
    // MARK: - Tests
    
    func testObserveRegistersObserverAndReceivesNotification() {
        // GIVEN: A notification manager and expectation
        let notificationCenter = NotificationCenter()
        let sut = NotificationObserverManager(notificationCenter: notificationCenter)
        
        let expectation = expectation(description: "Observer should receive notification")
        
        // WHEN: Observing a notification
        sut.observe(name: .testNotification) { _ in
            expectation.fulfill()
        }
        
        notificationCenter.post(name: .testNotification, object: nil)
        
        // THEN: The observer block is executed
        wait(for: [expectation], timeout: 1)
    }
    
    func testRemoveAllStopsObservingNotifications() {
        // GIVEN: A manager with registered observer
        let notificationCenter = NotificationCenter()
        let sut = NotificationObserverManager(notificationCenter: notificationCenter)
        
        var callCount = 0
        
        sut.observe(name: .testNotification) { _ in
            callCount += 1
        }
        
        // WHEN: Removing all observers
        sut.removeAll()
        
        notificationCenter.post(name: .testNotification, object: nil)
        
        // THEN: Callback is NOT executed
        XCTAssertEqual(callCount, 0)
    }
    
    func testObserveReturnsObserverToken() {
        // GIVEN: A manager
        let notificationCenter = NotificationCenter()
        let sut = NotificationObserverManager(notificationCenter: notificationCenter)
        
        // WHEN: Registering an observer
        let token = sut.observe(name: .testNotification) { _ in }
        
        // THEN: A token is returned
        XCTAssertNotNil(token)
    }
    
    func testRemoveAllClearsInternalObservers() {
        // GIVEN: A manager with observers
        let notificationCenter = NotificationCenter()
        let sut = NotificationObserverManager(notificationCenter: notificationCenter)
        
        sut.observe(name: .testNotification) { _ in }
        sut.observe(name: .testNotification) { _ in }
        
        // WHEN: Removing observers
        sut.removeAll()
        
        // THEN: Calling again should not crash and still do nothing
        XCTAssertNoThrow(sut.removeAll())
    }
    
    func testDeinitRemovesObservers() {
        // GIVEN: A retained notificationCenter
        let notificationCenter = NotificationCenter()
        
        var callCount = 0
        
        var sut: NotificationObserverManager? =
            NotificationObserverManager(notificationCenter: notificationCenter)
        
        sut?.observe(name: .testNotification) { _ in
            callCount += 1
        }
        
        // WHEN: Deallocating manager
        sut = nil
        
        notificationCenter.post(name: .testNotification, object: nil)
        
        // THEN: No callbacks are executed after deallocation
        XCTAssertEqual(callCount, 0)
    }
}

private extension Notification.Name {
    static let testNotification = Notification.Name("testNotification")
}
