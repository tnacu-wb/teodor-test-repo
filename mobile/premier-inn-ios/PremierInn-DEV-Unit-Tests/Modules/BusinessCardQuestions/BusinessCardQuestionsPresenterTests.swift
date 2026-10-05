//
//  BusinessCardQuestionsPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 04/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockView: BusinessCardQuestionViewProtocol {

    var customAnalyticsParameters: PIDictionary?

    var setupDidCall = false
    var loadViewModelDidCall = false
    var showCancelButtonDidCall = false
    
    func setup(withTitle viewTitle: String) {

    }

    func loadViewModel(with businessCardQuestionsViewModel: BusinessCardQuestionsAnswersViewModel) {

        setupDidCall = true

        loadViewModelDidCall = true
    }
    
    func showCancelButton() {
        
        showCancelButtonDidCall = true
    }
}

private class MockRouter: BusinessCardQuestionsRouterProtocol {
    
    var dismissControllerDidCall = false
    var submitBusinessCardQuestionsDidCall = false
    var shouldShowCancelButtonDidCall = false
    
    var shouldShowCancelButton: Bool {
        
        shouldShowCancelButtonDidCall = true
        return true
    }
    
    func dismissController() {
        
        dismissControllerDidCall = true
    }
    
    func submitBusinessCardQuestions(questions: [BusinessCardQuestionAndAnswer]) {
        
        submitBusinessCardQuestionsDidCall = true
    }
}

private class MockInteractor: BusinessCardQuestionsInteractorProtocol {
    
    var getScreenTitleDidCall = false
    var getQuestionsDidCall = false
    var customAnalyticsParameters: PIDictionary?
    var viewModel: BusinessCardQuestionsAnswersViewModel {

        getScreenTitleDidCall = true
        getQuestionsDidCall = true

        let dic = ["label": "Hi?"]
        let jsonData = try! JSONSerialization.data(withJSONObject: dic, options: .prettyPrinted)

        let companyQuestion = try! JSONDecoder().decode(CompanyManagementQuestion.self, from: jsonData)

        return BusinessCardQuestionsAnswersViewModel(screenTitle: "", questions: [BusinessCardQuestionAndAnswer(type: .custom, question: companyQuestion, answer: "hi")], ctaSetup: BusinessQuestionsCTASetup(title: "", style: .purple), willShowPayment: false)
    }

    var screenTitle: String {
        
        getScreenTitleDidCall = true
        
        return ""
    }
}

class BusinessCardQuestionsPresenterTests: XCTestCase {
    
    private var view: MockView!
    private var router: MockRouter!
    private var interactor: MockInteractor?
    private var presenter: BusinessCardQuestionPresenter!
    
    override func setUp() {
        
        view = MockView()
        router = MockRouter()
        interactor = MockInteractor()
        
        presenter = BusinessCardQuestionPresenter()
        presenter.view = view
        presenter.router = router
        presenter.interactor = interactor
    }
    
    override func tearDown() {
        
        presenter = nil
        view = nil
        router = nil
        
        super.tearDown()
    }
    
    func testViewIsReady() {
        
        presenter.viewIsReady()
        XCTAssertTrue(view.setupDidCall)
        XCTAssertTrue(view.loadViewModelDidCall)
        XCTAssertTrue(router.shouldShowCancelButtonDidCall)
        XCTAssertTrue(view.showCancelButtonDidCall)
        
        XCTAssert(interactor?.getScreenTitleDidCall == true)
        XCTAssert(interactor?.getQuestionsDidCall == true)
    }
    
    func testCancelButtonTap() {
        
        presenter.cancelButtonDidTap()
        XCTAssertTrue(router.dismissControllerDidCall)
    }
    
    func testSubmitButtonTap() {
        
        presenter.submitButtonDidTap(values: [:])
        XCTAssertTrue(router.submitBusinessCardQuestionsDidCall)
    }
}
