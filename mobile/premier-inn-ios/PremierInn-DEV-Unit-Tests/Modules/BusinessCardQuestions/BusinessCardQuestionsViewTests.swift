//
//  BusinessCardQuestionsViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 04/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: BusinessCardQuestionPresenterProtocol {
    
    var viewIsReadyDidCall = false
    var cancelButtonDidTapDidCall = false
    var submitButtonDidTapDidCall = false
    
    func viewIsReady() {
        
        viewIsReadyDidCall = true
    }
    
    func cancelButtonDidTap() {
        
        cancelButtonDidTapDidCall = true
    }
    
    func submitButtonDidTap(values: PIDictionary) {
        
        submitButtonDidTapDidCall = true
    }
}

class BusinessCardQuestionsViewTests: XCTestCase {
    
    private var viewController: FormBusinessCardQuestionsController!
    private var presenter: MockPresenter!
    private var analytics: MockAnalyticsManager!
    
    // MARK: - Lifecycle
    
    override func setUp() {
        
        presenter = MockPresenter()
        
        viewController = FormBusinessCardQuestionsController()
        viewController.presenter = presenter
    }
    
    override func tearDown() {
        
        presenter = nil
        viewController = nil
        
        super.tearDown()
    }
    
    // MARK: - Tests
    
    func testViewIsReady() {
        
        viewController?.viewDidLoad()
        XCTAssertTrue(presenter.viewIsReadyDidCall)
    }
    
    func testCancelButton() {
        
        viewController.cancelButtonDidTap(UIBarButtonItem())
        XCTAssertTrue(presenter.cancelButtonDidTapDidCall)
    }
    
    func testSubmitButton() {

        let dic = ["label": "Hi?"]
        let jsonData = try! JSONSerialization.data(withJSONObject: dic, options: .prettyPrinted)

        let companyQuestion = try! JSONDecoder().decode(CompanyManagementQuestion.self, from: jsonData)

        let questions = [
            BusinessCardQuestionAndAnswer(type: .purchaseOrder, question: companyQuestion, answer: "hey")
        ]

        let businessViewModel: BusinessCardQuestionsAnswersViewModel = BusinessCardQuestionsAnswersViewModel(
            screenTitle: "",
            questions: questions,
            ctaSetup: BusinessQuestionsCTASetup(title: "", style: .purple),
            willShowPayment: false
        )

        viewController.viewModel = FormekaViewModel(sections: viewController.viewModelSections(with: businessViewModel))
        viewController.submitButtonDidTap(cell: FormekaSubmitButtonCell())

        XCTAssertTrue(presenter.submitButtonDidTapDidCall)
    }
}
