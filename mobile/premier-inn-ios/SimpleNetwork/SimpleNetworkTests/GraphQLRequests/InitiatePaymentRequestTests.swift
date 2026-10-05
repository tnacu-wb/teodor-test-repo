//
//  InitiatePaymentRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 14/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

final class InitiatePaymentRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    var user: User? {

        do {
            let user = try User(title: "mr", firstName: "justin", lastName: "pogg")
            return user
        } catch {
            XCTFail("\(error)")
            return nil
        }
    }

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

    var address: Address? {
        do {
             let address = try Address(dictionary: [
                "line1": "line1",
                "line2": "line2",
                "line3": "line3",
                "postCode": "postCode",
                "country": "GB"
            ])
            return address
        } catch {
            XCTFail("\(error)")
            return nil
        }
    }

    var hotel: Hotel? {
        do {
            let hotel = try Hotel(dictionary: [
                "hotelCode": "BRIPTI",
                "hotelInfo": [
                    "name": "PremierInn Brighton",
                    "address": ["postcode": "BN1 1RE", "addressline1": "144 North Street", "addressline2": "Brighton", "addressline3": "East Sussex", "country": "United Kingdom (the)"]
                ]])
            return hotel
        } catch {
            XCTFail("\(error)")
            return nil
        }
    }

    var criteria: Criteria {
        var criteria = Criteria()
        criteria.rooms = {
            let room1 = Room()
            room1.adults = 2
            room1.children = 2
            room1.cotRequired = true
            room1.type = RoomType.family

            let room2 = Room()
            room2.adults = 1
            room2.children = 0
            room2.type = RoomType.accessible

            return [room1, room2]
        }()
        criteria.arrivalDate = {
            var dateComponents = DateComponents()
            dateComponents.year = 2050
            dateComponents.month = 9
            dateComponents.day = 12
            dateComponents.hour = 12
            dateComponents.timeZone = TimeZone(identifier: "Europe/London")

            return Calendar.current.date(from: dateComponents)!
        }()
        criteria.nights = 2

        return criteria
    }

    func testInitiatePaymentCore() {

        let paymentParams = CCCPPaymentParams(acceptedCardCodes: nil, card: nil, billingDetails: BillingDetails(email: "email@test.com", telephone: "07512121212", address: address!, fullName: (title: "", firstName: "", lastName: "")), journey: .BOOKING, paymentInterval: .now, payingWithPIBA: false, bbQuestionAndAnswers: nil, paymentType: .PIBA, paypalNonce: nil, paypalDeviceData: nil, usePaypalInitiatePayment: true, donationPackage: "GOSH")

        let bookingDetails = BookingDetails()

        bookingDetails.hotel = hotel!

        bookingDetails.criteria = criteria
        bookingDetails.booker = try! User(title: "Mr", firstName: "Booker", lastName: "McBooky")
        // sessionID missing
        XCTAssertThrowsError(try webservice.cccpPayment(with: paymentParams, and: bookingDetails, and: nil, and: false, sensorData: ""))

        // Valid resource
        do {
            let resource = try webservice.cccpPayment(with: paymentParams, and: bookingDetails, and: "MOCK_SESSION_ID", and: false, sensorData: "")
            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.initiatePaymentMutation)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let basketReference = variables?["basketReference"] as? String
            XCTAssertEqual(basketReference, "MOCK_SESSION_ID")
            let createPaymentCriteria = variables?["createPaymentCriteria"] as? PIDictionary
            XCTAssertNotNil(createPaymentCriteria)

            XCTAssertNotNil(createPaymentCriteria?["isCiol"])
            XCTAssertNotNil(createPaymentCriteria?["booking"])
            XCTAssertNotNil(createPaymentCriteria?["payment"])
            XCTAssertNotNil(createPaymentCriteria?["requestId"])

            // Test Donations
            XCTAssertNotNil(createPaymentCriteria?["charityPackageCode"])
            XCTAssertNotNil(createPaymentCriteria?["hotelId"])

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

    // MARK: Testing business questions for PIBA and Business Booker
    func testLeisurePIBAParametersInitiatePayment() {

        let paymentParams = CCCPPaymentParams(acceptedCardCodes: nil, card: nil, billingDetails: BillingDetails(email: "email@test.com", telephone: "07512121212", address: address!, fullName: (title: "", firstName: "", lastName: "")), journey: .BOOKING, paymentInterval: .now, payingWithPIBA: true, bbQuestionAndAnswers: nil, paymentType: .PIBA, paypalNonce: nil, paypalDeviceData: nil, usePaypalInitiatePayment: true, donationPackage: nil)

        let bookingDetails = BookingDetails()

        bookingDetails.hotel = hotel!
        UserSessionManager.sharedInstance.currentUser?.company = nil

        bookingDetails.criteria = criteria
        bookingDetails.booker = try! User(title: "Mr", firstName: "Booker", lastName: "McBooky")
        // sessionID missing
        XCTAssertThrowsError(try webservice.cccpPayment(with: paymentParams, and: bookingDetails, and: nil, and: false, sensorData: ""))

        // Valid resource
        do {
            let resource = try webservice.cccpPayment(with: paymentParams, and: bookingDetails, and: "MOCK_SESSION_ID", and: false, sensorData: "")
            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.initiatePaymentMutation)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)

            let createPaymentCriteria = variables?["createPaymentCriteria"] as? PIDictionary
            XCTAssertNotNil(createPaymentCriteria)

            XCTAssertNotNil(createPaymentCriteria?["booking"])
            XCTAssertNotNil(createPaymentCriteria?["payment"])
            XCTAssertNotNil(createPaymentCriteria?["requestId"])
            XCTAssertNotNil(createPaymentCriteria?["isCiol"])

            XCTAssertNotNil(createPaymentCriteria?["payment"])
            let payment = createPaymentCriteria?["payment"] as? PIDictionary
            XCTAssertNotNil(payment)
            let businessItems = payment?["businessItems"] as? PIDictionary
            XCTAssertNotNil(businessItems?["customReferenceNumber"] as? String)
            XCTAssertNotNil(businessItems?["purchaseOrderNumber"] as? String)
            XCTAssertNotNil(businessItems?["businessAllowances"] as? [PIDictionary])

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

    func testBusinessPIBAParametersInitiatePayment() {

        let paymentParams = CCCPPaymentParams(acceptedCardCodes: nil, card: nil, billingDetails: BillingDetails(email: "email@test.com", telephone: "07512121212", address: address!, fullName: (title: "", firstName: "", lastName: "")), journey: .BOOKING, paymentInterval: .now, payingWithPIBA: true, bbQuestionAndAnswers: nil, paymentType: .PIBA, paypalNonce: nil, paypalDeviceData: nil, usePaypalInitiatePayment: true, donationPackage: nil)

        let bookingDetails = BookingDetails()

        bookingDetails.hotel = hotel!
        if let user = user {
            user.company = company
            UserSessionManager.sharedInstance.loggedIn(with: user)
        }

        bookingDetails.criteria = criteria
        bookingDetails.booker = try! User(title: "Mr", firstName: "Booker", lastName: "McBooky")
        // sessionID missing
        XCTAssertThrowsError(try webservice.cccpPayment(with: paymentParams, and: bookingDetails, and: nil, and: false, sensorData: ""))

        // Valid resource
        do {
            let resource = try webservice.cccpPayment(with: paymentParams, and: bookingDetails, and: "MOCK_SESSION_ID", and: false, sensorData: "")
            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.initiatePaymentMutation)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let createPaymentCriteria = variables?["createPaymentCriteria"] as? PIDictionary
            XCTAssertNotNil(createPaymentCriteria)

            let payment = createPaymentCriteria?["payment"] as? PIDictionary
            XCTAssertNotNil(payment)
            let isCiol = createPaymentCriteria?["isCiol"] as? Bool
            XCTAssertNotNil(isCiol)
            let businessItems = payment?["businessItems"] as? PIDictionary
            XCTAssertNotNil(businessItems?["businessAllowances"] as? [PIDictionary])

            XCTAssertNil(businessItems?["customerReference"] as? String)
            XCTAssertNil(businessItems?["purchaseOrder"] as? String)
        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

    func testBusinessCompanyQuestionsParametersInitiatePayment() {



        let bookingDetails = BookingDetails()

        bookingDetails.hotel = hotel!

        if let user = user {
            user.company = company
            UserSessionManager.sharedInstance.loggedIn(with: user)
        }

        bookingDetails.criteria = criteria
        bookingDetails.booker = try! User(title: "Mr", firstName: "Booker", lastName: "McBooky")
        // sessionID missing

        // Mock business questions and answers
        let businessCardQuestionsAndAnswers = [BusinessCardQuestionAndAnswer(type: .custom, question: CompanyManagementQuestion(questionId: nil, label: "q1", mandatory: false, active: true, managementInformationAnswer: nil, location: nil), answer: "a1"), BusinessCardQuestionAndAnswer(type: .customerReference, question: CompanyManagementQuestion(questionId: nil, label: "q2", mandatory: false, active: true, managementInformationAnswer: nil, location: nil), answer: "a2"), BusinessCardQuestionAndAnswer(type: .purchaseOrder, question: CompanyManagementQuestion(questionId: nil, label: "q3", mandatory: false, active: true, managementInformationAnswer: nil, location: nil), answer: "a3")]
        bookingDetails.businessCardQuestionsAndAnswers = businessCardQuestionsAndAnswers

        let paymentParams = CCCPPaymentParams(acceptedCardCodes: nil, card: nil, billingDetails: BillingDetails(email: "email@test.com", telephone: "07512121212", address: address!, fullName: (title: "", firstName: "", lastName: "")), journey: .BOOKING, paymentInterval: .now, payingWithPIBA: false, bbQuestionAndAnswers: bookingDetails.operaBusinessQuestionsAndAnswers, paymentType: .PIBA, paypalNonce: nil, paypalDeviceData: nil, usePaypalInitiatePayment: true, donationPackage: nil)

        XCTAssertThrowsError(try webservice.cccpPayment(with: paymentParams, and: bookingDetails, and: nil, and: false, sensorData: ""))

        // Valid resource
        do {
            let resource = try webservice.cccpPayment(with: paymentParams, and: bookingDetails, and: "MOCK_SESSION_ID", and: false, sensorData: "")
            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.initiatePaymentMutation)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let createPaymentCriteria = variables?["createPaymentCriteria"] as? PIDictionary
            XCTAssertNotNil(createPaymentCriteria)

            let payment = createPaymentCriteria?["payment"] as? PIDictionary
            XCTAssertNotNil(payment)
            let isCiol = createPaymentCriteria?["isCiol"] as? Bool
            XCTAssertNotNil(isCiol)
            let businessItems = payment?["businessItems"] as? PIDictionary
            XCTAssertNotNil(businessItems?["businessAllowances"] as? [PIDictionary])

            XCTAssertNil(businessItems?["customerReference"] as? String)
            XCTAssertNil(businessItems?["purchaseOrder"] as? String)

            let companyQuestionAndAnswerDetails = createPaymentCriteria?["companyQuestionAndAnswerDetails"] as? PIDictionary

            let customerReferenceQuestionAndAnswer = companyQuestionAndAnswerDetails?["customerReferenceQuestionAndAnswer"] as? PIDictionary
            XCTAssertEqual(customerReferenceQuestionAndAnswer!["question"] as? String, "q2")
            XCTAssertEqual(customerReferenceQuestionAndAnswer!["answer"] as? String, "a2")

            let purchaseOrderQuestionAndAnswer = companyQuestionAndAnswerDetails?["purchaseOrderQuestionAndAnswer"] as? PIDictionary
            XCTAssertEqual(purchaseOrderQuestionAndAnswer!["question"] as? String, "q3")
            XCTAssertEqual(purchaseOrderQuestionAndAnswer!["answer"] as? String, "a3")

            let userDefinedQuestionAndAnswers = companyQuestionAndAnswerDetails?["userDefinedQuestionAndAnswers"] as? [PIDictionary]
            let userDefinedQuestionAndAnswersFirst = userDefinedQuestionAndAnswers?.first
            XCTAssertEqual(userDefinedQuestionAndAnswersFirst!["question"] as? String, "q1")
            XCTAssertEqual(userDefinedQuestionAndAnswersFirst!["answer"] as? String, "a1")

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }
}
