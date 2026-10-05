//
//  ColorsTests.swift
//  PremierInn UnitTests
//
//  Created by Filippo Minelle on 27/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation
import SimpleNetwork

@testable import PremierInn

class ColorsTests: XCTestCase {

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        NetworkErrorManager.sharedInstance.errors = [:]
    }

    override func tearDown() {

        super.tearDown()
    }

    // MARK: - Tests

    func testHexColor() {

        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0

        UIColor(hex: "ff9900").getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        XCTAssertEqual(red, 1)
        XCTAssertEqual(green, 0.6)
        XCTAssertEqual(blue, 0)
        XCTAssertEqual(alpha, 1)

        UIColor(hex: "#ff9900").getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        XCTAssertEqual(red, 1)
        XCTAssertEqual(green, 153 / 255)
        XCTAssertEqual(blue, 0)
        XCTAssertEqual(alpha, 1)

        UIColor(hex: "ff9").getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        XCTAssertEqual(red, 1)
        XCTAssertEqual(green, 1)
        XCTAssertEqual(blue, 153 / 255)
        XCTAssertEqual(alpha, 1)

        UIColor(hex: "ff93").getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        XCTAssertEqual(red, 0)
        XCTAssertEqual(green, 0)
        XCTAssertEqual(blue, 0)
        XCTAssertEqual(alpha, 1)
    }

    func testRGBAColour() {

        let components = UIColor.black.rgba
        XCTAssertEqual(components.red, 0)
        XCTAssertEqual(components.green, 0)
        XCTAssertEqual(components.blue, 0)
        XCTAssertEqual(components.alpha, 1)
    }

}
