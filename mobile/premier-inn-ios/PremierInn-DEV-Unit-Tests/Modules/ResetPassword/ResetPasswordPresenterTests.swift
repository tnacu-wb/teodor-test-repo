//
//  ResetPasswordPresenterTests.swift
//  PremierInn
//
//  Created by Nick Jones on 09/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn

private class MockView: ResetPasswordViewInput {

    var parentNavigationController: UINavigationController? {
        return nil
    }

    var tabBarIsHidden: Bool = false

    var updateScreenTitleDidCall = false
    var registerRowsDidCall = false
    var loadModelForViewDidCall = false
    var validateFormDidCall = false
    var scrollToAndFocusRowDidCall = false
    var makeRowFirstResponderDidCall = false
    var showAlertWithTitleAndErrorDidCall = false
    var showAlertWithTitleAndMessageDidCall = false
    var stopLoadingAnimationDidCall = false

    func updateScreenTitle(with screenTitle: String) {

        updateScreenTitleDidCall = true
    }

    func registerRows() {

        registerRowsDidCall = true
    }

    func loadModelForView() {

        loadModelForViewDidCall = true
    }

    func validateForm() {
        
        validateFormDidCall = true
    }

    func scrollToAndFocusRow(at indexPath: IndexPath) {

        scrollToAndFocusRowDidCall = true
    }

    func makeRowFirstResponder(at indexPath: IndexPath) {

        makeRowFirstResponderDidCall = true
    }

    func showAlert(with title: String, error: Error?) {

        showAlertWithTitleAndErrorDidCall = true
    }

    func showAlert(withTitle title: String, message: String) {

        showAlertWithTitleAndMessageDidCall = true
    }

    func stopLoadingAnimation() {

        stopLoadingAnimationDidCall = true
    }
}

class ResetPasswordPresenterTests: XCTestCase {

    private var view: MockView?
    var presenter: ResetPasswordPresenter?

    override func setUp() {
        super.setUp()

        view = MockView()

        presenter = ResetPasswordPresenter()
        presenter?.view = view
    }

    func testUpdateScreenTitleDidCall() {

        presenter?.viewIsReady()

        XCTAssertTrue(view?.updateScreenTitleDidCall ?? false)
    }

    func testRegisterRowsDidCall() {

        presenter?.viewIsReady()

        XCTAssertTrue(view?.registerRowsDidCall ?? false)
    }

    func testValidateFormDidCall() {

        presenter?.submitButtonTapped()

        XCTAssertTrue(view?.validateFormDidCall ?? false)
    }

    func testScrollToAndFocusRowDidCall() {

        presenter?.formSubmissionErrorOccured(at: IndexPath())

        XCTAssertTrue(view?.scrollToAndFocusRowDidCall ?? false)
    }

    func testMakeRowFirstResponseDidCall() {

        presenter?.userTappedEmailAddressRow(at: IndexPath())

        XCTAssertTrue(view?.makeRowFirstResponderDidCall ?? false)
    }

    func testShowAlertWithErrorDidShow() {

        let genericError = NSError(domain: "", code: 0, userInfo: nil)
        presenter?.genericFormSubmissionErrorOccured(with: genericError)

        XCTAssertTrue(view?.showAlertWithTitleAndErrorDidCall ?? false)
    }

    func testShowAlertWithMessageDidShow() {

        presenter?.emailAddressSuccesfullyUpdated()

        XCTAssertTrue(view?.showAlertWithTitleAndMessageDidCall ?? false)
    }

    func testStopLoadingAnimationDidCall() {

        presenter?.emailAddressSuccesfullyUpdated()

        XCTAssertTrue(view?.stopLoadingAnimationDidCall ?? false)
    }
}
