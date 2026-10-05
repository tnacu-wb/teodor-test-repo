//
//  Utilities.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 02/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest

extension XCTestCase {
    
    func wait(for duration: TimeInterval, description: String = "Expectation") {
        
        let expectation = self.expectation(description: description)
        DispatchQueue.main.asyncAfter(deadline: .now() + duration) {
            expectation.fulfill()
        }
        
        // Wait for a reasonable amount of time before giving up
        waitForExpectations(timeout: 10) { error in

            if let error = error {
                print(description + " - error: " + error.localizedDescription)
            }
        }
    }

    func predicateExpectation(
        description: String,
        _ condition: @autoclosure @escaping () -> Bool,
    ) -> XCTestExpectation {
        let predicate = NSPredicate { _, _ in
            condition()
        }

        return XCTNSPredicateExpectation(
            predicate: predicate,
            object: nil
        )
    }
}

extension String {

    /// Generate random alphanumeric string.
    /// - Parameter length: string length (default 20)
    /// - Returns: random string
    func random(_ length: Int = 20) -> String {

        let letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return String((0..<length).map{ _ in letters.randomElement() ?? "a" })
    }
}
