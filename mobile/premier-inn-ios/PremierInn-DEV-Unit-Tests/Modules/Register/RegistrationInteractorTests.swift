//
//  RegistrationInteractorTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 26/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockInteractorOutput: RegisterInteractorOutput, LoginInteractorOutput {

    enum MockFailure {
        case none
        case registration
        case login
        case getUser
    }

    var loginDidCall = false
    var getUserDidCall = false
    var registerDidCall = false
    var failure: MockFailure = .none

	func register(withRegisterParameters registerParameters: RegisterParameters, sensorData: String, completion: @escaping (Bool?, Error?) -> Void) {

        registerDidCall = true

        if failure == .registration {
            let serverErrorMessage = String(format: PILocalizedString("registerRequestAlreadyRegisteredMessage", comment: ""), RegistrationInteractorTests.mockUser.emailAddress!)

            return completion(false, RequestsManagerError.serverError(["details": [serverErrorMessage]]))
        }

        completion(true, nil)
    }

    func logout() {

    }

    func login(withUsername username: String, password: String, isBusiness: Bool = false, completion: @escaping (Result<Bool>) -> Void) {

        loginDidCall = true

        if failure == .login {
            return completion(.failure(error: RegisterInteractorError.genericNetworkError))
        }

        completion(.success(result: true))
    }

    func getUser(userId: String, isBusiness: Bool, completion: @escaping (Result<User>) -> Void) {

        getUserDidCall = true

        if failure == .getUser {
            return completion(.failure(error: RegisterInteractorError.genericNetworkError))
        }

        completion(.success(result: RegistrationInteractorTests.mockUser))
    }
    
    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void) {

    }

	func getCompany(companyId: String, sensorData: String, completion: @escaping (Company?, Error?) -> Void) {}

    func updateMarketingPreferences(for brands: [MarketingBrandCode], and emailAddress: String, optin: Bool, isoCountryCode: String, completion: @escaping (Bool, Error?) -> Void) {

        // kakaka
        completion(true, nil)
    }
}

class RegistrationInteractorTests: XCTestCase {

    static let mockUser: User = {
        let address = try! Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"])
        let user = try! User(dictionary: [
            "contactDetail" : ["title" : "Mr", "firstName": "Pippo", "lastName": "Paperino", "emailAddress": "ntwisp@me.com"],
            "additionalGuests": [
                ["title": "Mr", "firstName": "Marcello", "lastName": "Mascia", "nationality": "Sardinian", "email": "godTierMccree@owl.com"]
            ]
            ], sessionId: "hello")
        user.address = address
        return user
    }()
    static let mockParameters: RegisterParameters = {

        let password = "Password1"

        return RegisterParameters(
            user: mockUser,
            password: password,
            marketingOptIn: false,
            doubleOptIn: false,
            isoCountryCode: "GB",
            brandCodes: [.premierInn]
        )
    }()

    private var mockInteractorInOutManager: MockInteractorOutput!
    private var interactor: RegisterInteractor!

    override func setUp() {
        super.setUp()

        mockInteractorInOutManager = MockInteractorOutput()

        interactor = RegisterInteractor()
		interactor.registerDataManager = mockInteractorInOutManager
    }

    override func tearDown() {

        interactor = nil
        mockInteractorInOutManager = nil

        super.tearDown()
    }

    func testRegister_HappyPath() {

        mockInteractorInOutManager.failure = .none

        let expectation = self.expectation(description: "Registration")
        var expectedError: Error? = nil

        interactor.performRegistration(with: RegistrationInteractorTests.mockParameters) { error in

            expectedError = error

            expectation.fulfill()
        }

        waitForExpectations(timeout: 5, handler: nil)

        XCTAssertNil(expectedError)
        if let error = expectedError {
            XCTFail(error.localizedDescription)
        }
        XCTAssertTrue(self.mockInteractorInOutManager.registerDidCall)
        XCTAssertTrue(self.mockInteractorInOutManager.loginDidCall)
        XCTAssertTrue(self.mockInteractorInOutManager.getUserDidCall)
    }

    func testRegister_RegistrationFailure() {

        mockInteractorInOutManager.failure = .registration

        let expectation = self.expectation(description: "Registration")
        var expectedError: Error? = nil

        interactor.performRegistration(with: RegistrationInteractorTests.mockParameters) { error in

            expectedError = error

            expectation.fulfill()
        }

        waitForExpectations(timeout: 5, handler: nil)

        XCTAssertNotNil(expectedError)
        XCTAssertEqual(expectedError?.localizedDescription, RegisterInteractorError.emailAlreadyRegistered.localizedDescription)
        XCTAssertTrue(self.mockInteractorInOutManager.registerDidCall)
        XCTAssertFalse(self.mockInteractorInOutManager.loginDidCall)
        XCTAssertFalse(self.mockInteractorInOutManager.getUserDidCall)
    }

    func testRegister_LoginFailure() {

        mockInteractorInOutManager.failure = .login

        let expectation = self.expectation(description: "Registration")
        var expectedError: Error? = nil

        interactor.performRegistration(with: RegistrationInteractorTests.mockParameters) { error in

            expectedError = error

            expectation.fulfill()
        }

        waitForExpectations(timeout: 5, handler: nil)

        XCTAssertNotNil(expectedError)
        XCTAssertEqual(expectedError?.localizedDescription, "Account successfully created but automatic login failed. Please try to login through Account screen")
        XCTAssertTrue(mockInteractorInOutManager.registerDidCall)
        XCTAssertTrue(mockInteractorInOutManager.loginDidCall)
        XCTAssertFalse(mockInteractorInOutManager.getUserDidCall)
    }

    func testRegister_GetUserFailure() {

        mockInteractorInOutManager.failure = .getUser

        let expectation = self.expectation(description: "Registration")
        var expectedError: Error? = nil

        interactor.performRegistration(with: RegistrationInteractorTests.mockParameters) { error in

            expectedError = error

            expectation.fulfill()
        }

        waitForExpectations(timeout: 5, handler: nil)

        XCTAssertNotNil(expectedError)
        XCTAssertEqual(expectedError?.localizedDescription, RegisterInteractorError.genericNetworkError.localizedDescription)
        XCTAssertTrue(mockInteractorInOutManager.registerDidCall)
        XCTAssertTrue(mockInteractorInOutManager.loginDidCall)
        XCTAssertTrue(mockInteractorInOutManager.getUserDidCall)
    }

    func testRegisterParameters() {

        let regValues: PIDictionary = [
            "title": "Mr",
            "firstName": "Freddie",
            "lastName": "Baggins",
            "contactNumber": "0784531233",
            "fjdk": "jkfjdk"
        ]

        XCTAssertThrowsError(try RegisterInteractor.registerParameters(values: regValues))

        let regValues2: PIDictionary = [
            "title": "Mr",
            "firstName": "Freddie",
            "lastName": "Baggins",
            "postcode": "SG7 5QN",
            "line1": "42 Dixies Close",
            "nationality": "GB",
            "type": "RESIDENTIAL",
            "emailAddress": "fred@me.com",
            "invoiceDelivery": "email",
            "mobileNumber": "07841345555",
            "password": "Sausages123",
            "marketingOptIn": false
        ]

        XCTAssertNoThrow(try RegisterInteractor.registerParameters(values: regValues2))
    }
}
