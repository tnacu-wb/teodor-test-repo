//
//  LoginInteractorTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import Formeka
import SimpleNetwork
@testable import PremierInn

private class MockRequestsManager: LoginInteractorOutput {

    var company: Company? {

        var requestCompanyDictionary: PIDictionary = [
            "companyDetails": [
                "companyName": "Pogg Inc"
            ]
        ]
        requestCompanyDictionary["allowCentralCreditCard"] = true

        guard let companyData = try? JSONSerialization.data(withJSONObject: requestCompanyDictionary, options: .prettyPrinted) else { return nil }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(Company.self, from: companyData)
        } catch {
            return nil
        }
    }


    var loginDidCall = false
    var getUserDidCall = false
    var getCompanyFail = false
    var refreshStaysDidCall = false

    func login(withUsername username: String, password: String, isBusiness: Bool, completion: @escaping (Result<Bool>) -> Void) {

        loginDidCall = true

        if username.isEmpty && password.isEmpty {
			return completion(.failure(error: LoginInteractorError.viewModelNotFound))
        }

		completion(.success(result: true))
    }

    func getUser(userId: String, isBusiness: Bool, completion: @escaping (Result<User>) -> Void) {
        let user = try! User(dictionary: [
            "contactDetail" : ["title" : "Mr", "firstName": "Pippo", "lastName": "Paperino"],
            "additionalGuests": [
                ["title": "Mr", "firstName": "Marcello", "lastName": "Mascia", "nationality": "Sardinian", "email": "godTierMccree@owl.com"]
            ], "companyId": "test-company",
            "business": ["employeeId": "test", "centralCard":"test", "awaitingApproval": 1]
            ], sessionId: "hello")
        getUserDidCall = true

		completion(.success(result: user))
    }
    
    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void) {
        refreshStaysDidCall = true
    }

	func getCompany(companyId: String, sensorData: String, completion: @escaping (Company?, Error?) -> Void) {
        if getCompanyFail == true {
            completion(nil, nil)
        } else {
            completion(company, nil)
        }
    }
}

class LoginInteractorTests: XCTestCase {

    var interactor: LoginInteractor!
    private var output: MockRequestsManager!

    func mockViewModelWith(email: String?, password: String?) -> FormekaViewModel {

        var emailRow: FormekaModelRow {
            let row = FormekaModelRow(tag: LoginRow.email.rawValue, cellSetup: { _, _, _ in nil })
			row.value = email

			return row
        }

        var passwordRow: FormekaModelRow {
            let row = FormekaModelRow(tag: LoginRow.password.rawValue, cellSetup: { _, _, _ in nil })
			row.value = password

			return row
        }

        return FormekaViewModel(sections: [FormekaModelSection(header: nil, rows: [emailRow, passwordRow], footer: nil)])
    }

    override func setUp() {
        super.setUp()

        output = MockRequestsManager()
        interactor = LoginInteractor(asBusiness: false, fromSplashScreen: false, andFromBookingFlow: false)
        interactor.dataManager = output
    }
    
    override func tearDown() {

        interactor = nil
        output = nil

        UserDefaults.standard.removeObject(forKey: "storedUsername")

        super.tearDown()
    }
    
//    func testUsername() {
//
//		interactor.login(with: ("myUsername", "myPassword", false)) { _ in
//
//		}
//
//        // this will now be stored only after a successful login
//        XCTAssertEqual(interactor.userName, "myUsername")
//    }

    func testViewModelValidation() {

        XCTAssertThrowsError(try interactor.validate(viewModel: nil))
        XCTAssertThrowsError(try interactor.validate(viewModel: mockViewModelWith(email: nil, password: nil)))
        XCTAssertThrowsError(try interactor.validate(viewModel: mockViewModelWith(email: "Ippo", password: nil)))
        XCTAssertThrowsError(try interactor.validate(viewModel: mockViewModelWith(email: "Ippo", password: "")))
        XCTAssertThrowsError(try interactor.validate(viewModel: mockViewModelWith(email: "", password: nil)))

        let credential = try? interactor.validate(viewModel: mockViewModelWith(email: "Ippo", password: "Potamo"))

        XCTAssertEqual(credential?.username, "Ippo")
        XCTAssertEqual(credential?.password, "Potamo")
    }

    func testLogin() {

        interactor.login(with: (username: "u", password: "p", false)) { _ in }
        wait(for: .ocd, description: #function)

        XCTAssertTrue(output.getUserDidCall)
    }

    func testLogin_Error() {

        interactor.login(with: (username: "", password: "", false)) { _ in }
        wait(for: .ocd, description: #function)

        XCTAssertFalse(output.getUserDidCall)
    }

    func testGetCompanyLogin_Error() {
        output.getCompanyFail = true
        interactor.asBusiness = true
        interactor.login(with: (username: "u", password: "p", true)) { _ in }
        wait(for: .ocd, description: #function)

        XCTAssertFalse(output.refreshStaysDidCall)
    }

    func testGetCompanyLogin() {
        output.getCompanyFail = false
        interactor.asBusiness = true
        interactor.login(with: (username: "u", password: "p", true)) { _ in }
        wait(for: .ocd, description: #function)

        XCTAssertTrue(output.refreshStaysDidCall)
    }
}
