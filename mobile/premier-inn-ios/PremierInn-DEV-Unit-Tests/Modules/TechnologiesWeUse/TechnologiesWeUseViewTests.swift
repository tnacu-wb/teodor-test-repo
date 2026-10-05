////
////  TechnologiesWeUseViewTests.swift
////  PremierInnDEVUnitTests
////
////  Created by Nick Jones on 26/02/2019.
////  Copyright © 2019 Whitbread. All rights reserved.
////
//
//import XCTest
//@testable import PremierInn
//
//private class MockEventHandler: TechnologiesWeUseViewEventHandler {
//    var userHasAccepted: Bool
//
//    func viewHasLoaded() {
//
//    }
//
//    func viewHasFinishedLayout() {
//
//    }
//
//    func viewIsAppearing(withAnimation animated: Bool) {
//
//    }
//
//    func userDidAccept() {
//
//    }
//
//
//    var viewIsReadyDidCall = false
//
//    func viewIsReady() {
//
//        viewIsReadyDidCall = true
//    }
//}
//
//class TechnologiesWeUseViewTests: XCTestCase {
//
//    private var view: TechnologiesWeUseViewController!
//    private var eventHandler: MockEventHandler!
//
//    override func setUp() {
//
////        eventHandler = MockPresenter()
////
////        let technologiesWeUseViewModel = TechnologiesWeUseViewModel(title: "Test View Header", html: "https://www.google.com", screenName: "Tech We Use", screenType: "Test")
////
////        view = TechnologiesWeUseViewController(with: technologiesWeUseViewModel)
////        view.eventHandler = eventHandler
//    }
//
//override func tearDown() {
//
//    eventHandler = nil
//    view = nil
//
//    super.tearDown()
//}
//
//    func testViewDidLoad() {
//
//        view?.viewDidLoad()
//        XCTAssertTrue(eventHandler.viewIsReadyDidCall)
//    }
//
//}
