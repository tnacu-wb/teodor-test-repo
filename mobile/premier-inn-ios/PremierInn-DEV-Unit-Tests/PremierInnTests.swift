//
//  PremierInnTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 12/05/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

import Formeka

class PremierInnTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testCustomButtons() {

        let button = RoundedCornersButton(frame: CGRect.zero)
        button.prepareForInterfaceBuilder()

        do {
            let data = try NSKeyedArchiver.archivedData(withRootObject: button, requiringSecureCoding: false)
            let cd = try NSKeyedUnarchiver(forReadingFrom: data)
            let button2 = RoundedCornersButton(coder: cd)
            button2?.awakeFromNib()
        } catch {
            XCTFail()
        }
    }

    func testCountryValidator() {

        let validator = CountryValidator()

		let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = Country(code: "GB", name: "United Kingdom", isoCode: "GB", dialingCode: nil, flagImage: nil, passportRequired: true, nationality: "British")
        XCTAssertNoThrow(try row1.validate())

		let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row2.value = Country(code: nil, name: "United Kingdom", isoCode: "GB", dialingCode: nil, flagImage: nil, passportRequired: true, nationality: "British")
		XCTAssertThrowsError(try row2.validate())

		let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
		row3.value = ""
		XCTAssertThrowsError(try row3.validate())

		let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
		row4.value = Date()
		XCTAssertThrowsError(try row4.validate())
    }

    func testCardExpiredDateValidator() {

        let validator = CardExpiredDateValidator()

		let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
		row1.value = "12/33"
        XCTAssertNoThrow(try row1.validate())

		let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
		XCTAssertThrowsError(try row2.validate())

		let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
		row3.value = "1233"
		XCTAssertNoThrow(try row3.validate())

		let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
		row4.value = "06/17"
		XCTAssertThrowsError(try row4.validate())

        let row5 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row3.value = "1213"
        XCTAssertThrowsError(try row5.validate())
    }

	func testForm1_Output() {

		let bookinDetails = BookingDetails()
		bookinDetails.criteria.rooms = [Room(), Room()]

		let booker = try! User(title: "Mr", firstName: "Paolino", lastName: "Paperino")
		let guest = try! User(title: "Mrs", firstName: "Paolina", lastName: "Paperina")
		let guests = [booker, guest]

		let output: FormStep1Output = (booker: booker, guests: guests, purpose: TripPurpose.business)
        UserDetailsRouter().userDetailsDidFinish(with: bookinDetails, output: output, sender: UIViewController())

		// Make sure the booker is correct
		XCTAssertEqual(bookinDetails.booker, booker)

		// Make sure the room setup is correct
		XCTAssertEqual(bookinDetails.criteria.rooms.count, 2)

		XCTAssertEqual(bookinDetails.criteria.rooms.first?.adults, 1)
		XCTAssertEqual(bookinDetails.criteria.rooms.first?.children, 0)
		XCTAssertEqual(bookinDetails.criteria.rooms.first?.cotRequired, false)

		XCTAssertEqual(bookinDetails.criteria.rooms.last?.adults, 1)
		XCTAssertEqual(bookinDetails.criteria.rooms.last?.children, 0)
		XCTAssertEqual(bookinDetails.criteria.rooms.last?.cotRequired, false)

		// Make sure guests are correct
		XCTAssertEqual(bookinDetails.criteria.rooms.first?.leadGuest, booker)
		XCTAssertEqual(bookinDetails.criteria.rooms.last?.leadGuest, guest)

		// Check the trip purpose
		XCTAssertEqual(bookinDetails.purpose, .business)

		// Check if user is also staying
		//        XCTAssertEqual(bookinDetails.bookerIsAlsoFirstRoomGuest, true)
	}

	func testForm1_Output_BookerIsGuest() {

		let bookinDetails = BookingDetails()
		bookinDetails.criteria.rooms = [Room(), Room()]

		let booker = try! User(title: "Mr", firstName: "Paolino", lastName: "Paperino")
		let guest1 = try! User(title: "Mrs", firstName: "Paolina", lastName: "Paperina")
		let guest2 = try! User(title: "Master", firstName: "Of", lastName: "The Universe")
		let guests = [guest1, guest2]

		let output: FormStep1Output = (booker: booker, guests: guests, purpose: TripPurpose.business)
		UserDetailsRouter().userDetailsDidFinish(with: bookinDetails, output: output, sender: UIViewController())

		// Make sure the booker is correct
		XCTAssertEqual(bookinDetails.booker, booker)

		// Make sure the room setup is correct
		XCTAssertEqual(bookinDetails.criteria.rooms.count, 2)

		XCTAssertEqual(bookinDetails.criteria.rooms.first?.adults, 1)
		XCTAssertEqual(bookinDetails.criteria.rooms.first?.children, 0)
		XCTAssertEqual(bookinDetails.criteria.rooms.first?.cotRequired, false)

		XCTAssertEqual(bookinDetails.criteria.rooms.last?.adults, 1)
		XCTAssertEqual(bookinDetails.criteria.rooms.last?.children, 0)
		XCTAssertEqual(bookinDetails.criteria.rooms.last?.cotRequired, false)

		// Make sure guests are correct
		XCTAssertEqual(bookinDetails.criteria.rooms.first?.leadGuest, guest1)
		XCTAssertEqual(bookinDetails.criteria.rooms.last?.leadGuest, guest2)

		// Check the trip purpose
		XCTAssertEqual(bookinDetails.purpose, .business)

		// Check if user is also staying
		//        XCTAssertEqual(bookinDetails.bookerIsAlsoFirstRoomGuest, false)
	}
}
