//
//  BusinessCardQuestionsInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 21/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

class BusinessCardQuestionsInteractorTests: XCTestCase {

    private var interactor: BusinessCardQuestionsInteractorProtocol?

    override func setUp() {
        super.setUp()

        let dic1 = ["label": "Hi?"]
        let jsonData1 = try! JSONSerialization.data(withJSONObject: dic1, options: .prettyPrinted)

        let companyQuestion1 = try! JSONDecoder().decode(CompanyManagementQuestion.self, from: jsonData1)

        let dic2 = ["label": "hungry?"]
        let jsonData2 = try! JSONSerialization.data(withJSONObject: dic2, options: .prettyPrinted)

        let companyQuestion2 = try! JSONDecoder().decode(CompanyManagementQuestion.self, from: jsonData2)

        let question1 = BusinessCardQuestionAndAnswer(type: .purchaseOrder, question: companyQuestion1, answer: "hey")
        let question2 = BusinessCardQuestionAndAnswer(type: .purchaseOrder, question: companyQuestion2, answer: "yeah")

        let questions = [
            question1,
            question2
        ]

        interactor = BusinessCardQuestionsInteractor(businessCardQuestions: questions)
    }

    override func tearDown() {

        interactor = nil

        super.tearDown()
    }

    func testQuestions() {

        let interactorQuestions = interactor?.viewModel.questions

        XCTAssertNotNil(interactorQuestions)
        XCTAssert(interactorQuestions?.count == 2)
        XCTAssertNotNil(interactorQuestions?.first(where: { $0.answer == "hey" }))
        XCTAssertNotNil(interactorQuestions?.first(where: { $0.question.label == "hungry?" }))
    }

    func testTitle() {

        XCTAssertNotNil(interactor)
        XCTAssert(interactor?.viewModel.screenTitle.isNotEmpty == true)
    }
}
