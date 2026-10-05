//
//  FontsTests.swift
//  PremierInn UnitTests
//
//  Created by Filippo Minelle on 27/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation
import SimpleNetwork

@testable import PremierInn

class FontsTests: XCTestCase {
    
    // MARK: - Properties
    
    var label: UILabel?
    
    // MARK: - Lifecycle
    
    override func setUp() {
        super.setUp()
        
        label = UILabel()
    }
    
    override func tearDown() {
        
        label = nil
        
        super.tearDown()
    }
    
    // MARK: - Tests

    // Headings

    // H1

    func test_TestConfiguration_Heading1_Regular() {

        label?.font = UIFont.Heading1_Regular()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 23.0)
    }

    func test_TestConfiguration_Heading1_Medium() {

        label?.font = UIFont.Heading1_Medium()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Medium")
        XCTAssertEqual(label?.font.pointSize, 23.0)
    }

    func test_TestConfiguration_Heading1_Semibold() {

        label?.font = UIFont.Heading1_Semibold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 23.0)
    }

    func test_TestConfiguration_Heading1_Bold() {

        label?.font = UIFont.Heading1_Bold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Bold")
        XCTAssertEqual(label?.font.pointSize, 23.0)
    }

    // H2

    func test_TestConfiguration_Heading2_Regular() {

        label?.font = UIFont.Heading2_Regular()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 20.0)
    }

    func test_TestConfiguration_Heading2_Medium() {

        label?.font = UIFont.Heading2_Medium()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Medium")
        XCTAssertEqual(label?.font.pointSize, 20.0)
    }

    func test_TestConfiguration_Heading2_Semibold() {

        label?.font = UIFont.Heading2_Semibold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 20.0)
    }

    func test_TestConfiguration_Heading2_Bold() {

        label?.font = UIFont.Heading2_Bold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Bold")
        XCTAssertEqual(label?.font.pointSize, 20.0)
    }
    
    func test_TestConfiguration_Heading2_ExtraBold() {

        label?.font = UIFont.Heading2_ExtraBold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Extrabld")
        XCTAssertEqual(label?.font.pointSize, 20.0)
    }

    // H3

    func test_TestConfiguration_Heading3_Regular() {

        label?.font = UIFont.Heading3_Regular()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 18.0)
    }

    func test_TestConfiguration_Heading3_Medium() {

        label?.font = UIFont.Heading3_Medium()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Medium")
        XCTAssertEqual(label?.font.pointSize, 18.0)
    }

    func test_TestConfiguration_Heading3_Semibold() {

        label?.font = UIFont.Heading3_Semibold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 18.0)
    }

    func test_TestConfiguration_Heading3_Bold() {

        label?.font = UIFont.Heading3_Bold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Bold")
        XCTAssertEqual(label?.font.pointSize, 18.0)
    }

    // H4

    func test_TestConfiguration_Heading4_Regular() {

        label?.font = UIFont.Heading4_Regular()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    func test_TestConfiguration_Heading4_Medium() {

        label?.font = UIFont.Heading4_Medium()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Medium")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    func test_TestConfiguration_Heading4_Semibold() {

        label?.font = UIFont.Heading4_Semibold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    func test_TestConfiguration_Heading4_Bold() {

        label?.font = UIFont.Heading4_Bold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Bold")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    // Body

    func test_TestConfiguration_Body_Regular() {

        label?.font = UIFont.Body()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    func test_TestConfiguration_Body_Medium() {

        label?.font = UIFont.Body_Medium()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Medium")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    func test_TestConfiguration_Body_Semibold() {

        label?.font = UIFont.Body_Semibold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    func test_TestConfiguration_Body_Bold() {

        label?.font = UIFont.Body_Bold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Bold")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    // Body Small

    func test_TestConfiguration_BodySmall_Regular() {

        label?.font = UIFont.BodySmall()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 14.0)
    }

    func test_TestConfiguration_BodySmall_Medium() {

        label?.font = UIFont.BodySmall_Medium()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Medium")
        XCTAssertEqual(label?.font.pointSize, 14.0)
    }

    func test_TestConfiguration_BodySmall_Semibold() {

        label?.font = UIFont.BodySmall_Semibold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 14.0)
    }

    func test_TestConfiguration_BodySmall_Bold() {

        label?.font = UIFont.BodySmall_Bold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Bold")
        XCTAssertEqual(label?.font.pointSize, 14.0)
    }

    // Subtext

    func test_TestConfiguration_Subtext_Regular() {
        
        label?.font = UIFont.Subtext()
        
        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 13.0)
    }

    func test_TestConfiguration_Subtext_Medium() {

        label?.font = UIFont.Subtext_Medium()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Medium")
        XCTAssertEqual(label?.font.pointSize, 13.0)
    }

    func test_TestConfiguration_Subtext_Semibold() {

        label?.font = UIFont.SubtextStrong()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 13.0)
    }

    func test_TestConfiguration_Subtext_Bold() {

        label?.font = UIFont.Subtext_Bold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Bold")
        XCTAssertEqual(label?.font.pointSize, 13.0)
    }

    // Subtext Small

    func test_TestConfiguration_SubtextSmall() {

        label?.font = UIFont.SubtextSmall()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 12.0)
    }

    func test_TestConfiguration_SubtextSmall_Medium() {

        label?.font = UIFont.SubtextSmall_Medium()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Medium")
        XCTAssertEqual(label?.font.pointSize, 12.0)
    }

    func test_TestConfiguration_SubtextSmall_Semibold() {

        label?.font = UIFont.SubtextSmallStrong()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 12.0)
    }

    func test_TestConfiguration_SubtextSmall_Bold() {

        label?.font = UIFont.SubtextSmall_Bold()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Bold")
        XCTAssertEqual(label?.font.pointSize, 12.0)
    }

    // Link

    func test_TestConfiguration_Link() {

        label?.font = UIFont.Link()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 14.0)
    }

    // Marketing

    func test_TestConfiguration_XXLargeTitle() {

        label?.font = UIFont.XXLargeTitle()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 50.0)
    }

    func test_TestConfiguration_LargeTitle() {

        label?.font = UIFont.LargeTitle()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 26.0)
    }

    // Buttons
    
    func test_TestConfiguration_Button1() {
        
        label?.font = UIFont.Button1()
        
        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 18.0)
    }

    func test_TestConfiguration_Button2() {

        label?.font = UIFont.Button2()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 17.0)
    }

    // Actions

    func test_TestConfiguration_Action1() {

        label?.font = UIFont.Action1()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    func test_TestConfiguration_Action2() {

        label?.font = UIFont.Action2()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Regular")
        XCTAssertEqual(label?.font.pointSize, 14.0)
    }

    // NavBar

    func test_TestConfiguration_NavTitle1() {

        label?.font = UIFont.NavTitle1()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 16.0)
    }

    func test_TestConfiguration_NavActive() {
        
        label?.font = UIFont.NavActive()
        
        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 12.0)
    }
    
    func test_TestConfiguration_NavInactive() {
        
        label?.font = UIFont.NavInactive()
        
        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 12.0)
    }

    // Custom

    func test_TestConfiguration_MapPin() {

        label?.font = UIFont.MapPin()

        XCTAssertEqual(label?.font.fontName, "ProximaNova-Semibold")
        XCTAssertEqual(label?.font.pointSize, 10.0)
    }
}
