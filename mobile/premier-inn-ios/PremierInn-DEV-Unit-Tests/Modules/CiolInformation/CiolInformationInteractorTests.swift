//
//  CiolInformationInteractorTests.swift
//  PremierInnTests
//
//  Created by Muresan, Andreea (Cognizant) on 11.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class CiolInformationInteractorTests: XCTestCase {
    
    var interactor: CiolInformationInteractor!
    
    override func setUp() {
        super.setUp()

        let viewModel = CiolInformationModel(image: nil,
                                             title: "Title",
                                             subtitle: "This is a subtitle",
                                             showSubtitle: true,
                                             description: .init(type: .string("Description")),
                                             showCTA: true,
                                             ctaTitle: "Understood",
                                             delegate: MockCiolInformationDelegate())
        
        interactor = CiolInformationInteractor(viewModel: viewModel)
    }
    
    override func tearDown() {
        interactor = nil
        super.tearDown()
    }
    
    func testViewModel() {
        XCTAssertNil(interactor.viewModel.image)
        XCTAssertEqual(interactor.viewModel.title, "Title")
        XCTAssertEqual(interactor.viewModel.subtitle, "This is a subtitle")
        XCTAssertTrue(interactor.viewModel.showSubtitle)
        XCTAssertEqual(interactor.viewModel.description.type, .string("Description"))
        XCTAssertTrue(interactor.viewModel.showCTA)
        XCTAssertEqual(interactor.viewModel.ctaTitle, "Understood")
        XCTAssertNotNil(interactor.viewModel.delegate)
    }

}

// Mock Delegate
class MockCiolInformationDelegate: CiolInformationDelegate {
    func ctaAction(ciolInformationType: CiolInformationType) {
        
    }
}
