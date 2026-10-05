//
//  FormekaTests.swift
//  FormekaTests
//
//  Created by Marcello Mascia on 21/07/2017.
//  Copyright © 2017 Marcello Mascia. All rights reserved.
//

import XCTest

import Formeka

class FormekaTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testRequiredValidator() {

        let validator = RequiredValidator()

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "Hello world"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row3.value = Date()
        XCTAssertThrowsError(try row3.validate())

        let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row4.value = Date()
        XCTAssertThrowsError(try row4.validate())
    }

    func testStringLengthValidator() {

        let validator = StringLengthValidator(range: 2...3)

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "Hey"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row2.value = "He"
        XCTAssertNoThrow(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row3.value = "Hello"
        XCTAssertThrowsError(try row3.validate())

        let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row4.validate())

        let row5 = FormekaModelRow(tag: "A", inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row5.value = ""
        XCTAssertThrowsError(try row5.validate())

        let row6 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row6.value = Date()
        XCTAssertThrowsError(try row6.validate())
    }

    func testNotNilValidator() {

        let validator = NotNilValidator()

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "Hello world"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row2.validate())
    }

    func testNameValidator() {

        let validator = NameValidator()

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "Hello world"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row2.value = "Bernabè"
        XCTAssertNoThrow(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row3.value = "Bin-Taleb"
        XCTAssertNoThrow(try row3.validate())

        let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row4.value = "O'Riordan"
        XCTAssertNoThrow(try row4.validate())

        let row5 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row5.value = "Hell0 w0rld"
        XCTAssertThrowsError(try row5.validate())

        let row6 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row6.value = "$3 Asd"
        XCTAssertThrowsError(try row6.validate())

        let row7 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row7.value = Date()
        XCTAssertThrowsError(try row7.validate())
    }

    func testEmailValidator() {

        let validator = EmailValidator()

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "marcello@mac.com"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row2.value = "marcellà@mac.com"
        XCTAssertNoThrow(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row3.validate())

        let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row4.value = "aasdas"
        XCTAssertThrowsError(try row4.validate())
    }

    func testPhoneNumberValidator() {

        let validator = PhoneNumberValidator()

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "+"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row2.value = "1231231"
        XCTAssertNoThrow(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row3.value = "+1231231"
        XCTAssertNoThrow(try row3.validate())

        let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row4.value = ""
        XCTAssertNoThrow(try row4.validate())

        let row5 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row5.value = "12321+"
        XCTAssertThrowsError(try row5.validate())

        let row6 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row6.validate())

        let row7 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row7.value = "aasdas"
        XCTAssertThrowsError(try row7.validate())
    }

    func testNumericValidator() {

        let validator = NumericValidator()

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "01231231"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row2.value = "12321+"
        XCTAssertThrowsError(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row3.validate())

        let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row4.value = "aasdas"
        XCTAssertThrowsError(try row4.validate())
    }

    func testPostCodeValidator() {

        let validator = UKPostCodeValidator()

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "SW19 3SH"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row3.value = "aasdas"
        XCTAssertThrowsError(try row3.validate())
    }

    func testCVVLengthValidator() {

        let validator1 = CVVLengthValidator(length: 3)

        let row1 = FormekaModelRow(inlineValidators: [validator1], cellSetup: { _, _, _ in nil })
        row1.value = "123"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator1], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator1], cellSetup: { _, _, _ in nil })
        row3.value = "12323"
        XCTAssertThrowsError(try row3.validate())

        let validator2 = CVVLengthValidator(length: 4)

        let row4 = FormekaModelRow(inlineValidators: [validator2], cellSetup: { _, _, _ in nil })
        row4.value = "1233"
        XCTAssertNoThrow(try row4.validate())

        let row5 = FormekaModelRow(inlineValidators: [validator2], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row5.validate())

        let row6 = FormekaModelRow(inlineValidators: [validator2], cellSetup: { _, _, _ in nil })
        row6.value = "12323"
        XCTAssertThrowsError(try row6.validate())
    }

    func testCardShortDateValidator() {

        let validator = CardShortDateValidator()

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "12/33"
        XCTAssertNoThrow(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row2.value = "1233"
        XCTAssertThrowsError(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        XCTAssertThrowsError(try row3.validate())

        let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row4.value = "12345678901234567"
        XCTAssertThrowsError(try row4.validate())
    }

    func testPasswordValidator() {

        let validator = PasswordValidator(regexsDict: [".{8,}" : "8 char", "(?s)[^A-Z]*[A-Z].*" : "Upper case char"], customSuccessValue: "Success")

        let row1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row1.value = "hello"
        XCTAssertThrowsError(try row1.validate())

        let row2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row2.value = "Hello"
        XCTAssertThrowsError(try row2.validate())

        let row3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row2.value = "hellothere"
        XCTAssertThrowsError(try row3.validate())

        let row4 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        row4.value = "Qwertyui"
        XCTAssertNoThrow(try row4.validate())
    }

    func testCompanyNameValidator() {

        let validator = CompanyNameValidator()

        let valid1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid1.value = " New Company Ltd. "
        XCTAssertNoThrow(try valid1.validate())

        let valid2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid2.value = "New %@!*[] Company Pvt - Ltd."
        XCTAssertNoThrow(try valid2.validate())

        let valid3 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid3.value = "X Æ A-12 and Sons"
        XCTAssertNoThrow(try valid3.validate())

        let invalid1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        invalid1.value = "Brand New ¢∞ Ltd."
        XCTAssertThrowsError(try invalid1.validate())

        let invalid2 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        invalid2.value = "Brand New Ltd. @{+sons}/"
        XCTAssertThrowsError(try invalid2.validate())
    }

    func testLeadGuestAgeValidator() {
        let validator = GuestAgeValidator(isLeadGuest: true)
        let df = DateFormatter()
        df.dateFormat = "dd/MM/yyyy"
        let date = df.date(from: "01/01/2020")
        let valid1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid1.value = date
        XCTAssertThrowsError(try valid1.validate())
    }

    func testAdditionalGuestAgeValidator() {
        let validator = GuestAgeValidator(isLeadGuest: false)
        let df = DateFormatter()
        df.dateFormat = "dd/MM/yyyy"
        let date = df.date(from: "01/01/2020")
        let valid1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid1.value = date
        XCTAssertNoThrow(try valid1.validate())
    }

    func testEmptyDateValidator() {
        let validator = NotEmptyDateValidator()
        let valid1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid1.value = nil
        XCTAssertThrowsError(try valid1.validate())
    }

    func testInvalidLeadGuestAddressValidator() {
        let validator = GuestAddressValidator(customErrorValue: "")
        let address = "street *"
        let invalid1 = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        invalid1.value = address
        XCTAssertThrowsError(try invalid1.validate())
    }

    func testValidLeadGuestAddressValidator() {
        let validator = GuestAddressValidator(customErrorValue: "")
        let address = "street "
        let valid = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid.value = address
        XCTAssertNoThrow(try valid.validate())
    }

    func testValidGuestPostcodeValidator() {
        let validator = GuestPostcodeValidator(customErrorValue: "")
        let code = "01001"
        let valid = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid.value = code
        XCTAssertNoThrow(try valid.validate())
    }

    func testInvalid2GermanGuestPostcodeValidator() {
        let validator = GuestPostcodeValidator(customErrorValue: "")
        let code = "01001%"
        let valid = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid.value = code
        XCTAssertThrowsError(try valid.validate())
    }

    func testValidGuestPassportValidator() {
        let validator = GuestPassportValidator(customErrorValue: "")
        let code = "01001sd"
        let valid = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid.value = code
        XCTAssertNoThrow(try valid.validate())
    }

    func testInvalidGuestPassportValidator() {
        let validator = GuestPassportValidator(customErrorValue: "")
        let code = "870dsfjds*"
        let valid = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid.value = code
        XCTAssertThrowsError(try valid.validate())
    }

    func testCityValidator() {
        let validator = GuestCityValidator(customErrorValue: "Validator should support whitespace in city name")
        let cityWithSpace = "Frankfurt am Main"
        let valid = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        valid.value = cityWithSpace
        XCTAssertNoThrow(try valid.validate())

        let cityWithUnsupportedCharacter = "Frankfurt@Main"
        let invalidRow = FormekaModelRow(inlineValidators: [validator], cellSetup: { _, _, _ in nil })
        invalidRow.value = cityWithUnsupportedCharacter
        XCTAssertThrowsError(try invalidRow.validate())
    }
}
