//
//  AmendDatesPresenterTests.swift
//  PremierInn
//
//  Created by Freddie Parks on 20/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockAmendDatesRouter: AmendDatesRouterProtocol {

    var finishedAmendDidCall = false

    func finishedAmend() {

        finishedAmendDidCall = true
    }
}

private class MockAmendDatesInteractor: AmendDatesInteractorProtocol {

    private let rate = Rate(dictionary: ["cardFeeApplies": true, "classification": "F", "totalCost": ["amount": "191.50", "currency": "GBP"]])

    var duplicateNightsDidCall = false
    var duplicatesDidCall = false
    var changeDatesDidCall = false
    
    var changeDatesShouldFail = false

    var bookingDatesRange: BookingDateRange? {
        return nil
    }

    var existingNights: Int = 1

    var moveOnly: Bool {
        return false
    }
    
    var currentPriceDifference: Cost? {
        return nil
    }

    func duplicateOfExistingNights(nights: Int) -> Bool {

        duplicateNightsDidCall = true
        return false
    }

    func duplicateOfExisting(date: Date, nights: Int) -> Bool {

        duplicatesDidCall = true
        return false
    }

    func duplicateOfCurrentRange(date: Date, nights: Int) -> Bool {
        return false
    }

    func changeDates(to arrivalDate: Date, nights: Int, completion: @escaping (PremierInn.ChangeDatesResponse, Error?) -> Void) {

        changeDatesDidCall = true
        if changeDatesShouldFail {
            completion((false, nil), nil)
        } else {
            completion((true, nil), nil)
        }
    }
}

class MockAlternateCalendarView: AmendDatesCalendarViewControllerProtocol {

    var showErrorDidCall = false
    var hideErrorDidCall = false
    var toggleProcessingDidCall = false
    var showDoneButtonDidCall = false

    var view: UIView! {
        return UIView()
    }

    func showError(with message: String) {

        showErrorDidCall = true
    }

    func hideError() {

        hideErrorDidCall = true
    }

    func toggle(is processing: Bool) {

        toggleProcessingDidCall = true
    }

    func showDoneButton(show: Bool, animated: Bool) {

        showDoneButtonDidCall = true
    }

    func showContinueButton(with viewModel: PremierInn.AmendDatesConfirmViewModel) {

    }

    func hideContinueButton() {
        
    }
}

class AmendDatesPresenterTests: XCTestCase {

    private var presenter: AmendDatesPresenter?

    private var calendar: MockAlternateCalendarView?
    private var interactor: MockAmendDatesInteractor?
    private var router: MockAmendDatesRouter?

    override func setUp() {

        presenter = AmendDatesPresenter()

        interactor = MockAmendDatesInteractor()
        presenter?.interactor = interactor

        calendar = MockAlternateCalendarView()
        presenter?.calendar = calendar

        router = MockAmendDatesRouter()
        presenter?.router = router
    }

    func testCalendarInvalidated() {

        presenter?.calendarDidInvalidate()

        XCTAssert(calendar?.hideErrorDidCall == true)
        XCTAssert(calendar?.showDoneButtonDidCall == true)
    }

    func testCalendarDidChange() {

        presenter?.calendarDidChange(arrivalDate: Date(), nights: 5)

        XCTAssert(calendar?.hideErrorDidCall == true)
        XCTAssert(interactor?.duplicatesDidCall == true)
        XCTAssert(calendar?.showDoneButtonDidCall == true)
    }

    func testCalendarDidSelect() {

        presenter?.calendarDidSelect(arrivalDate: Date(), nights: 5)

        XCTAssert(calendar?.showDoneButtonDidCall == true)
        XCTAssert(calendar?.toggleProcessingDidCall == true)
        XCTAssert(interactor?.changeDatesDidCall == true)
        XCTAssert(calendar?.showErrorDidCall == true)

        interactor?.changeDatesShouldFail = false
        presenter?.calendarDidSelect(arrivalDate: Date(), nights: 5)

        XCTAssert(calendar?.showDoneButtonDidCall == true)
    }

    func testContinueButtonDidTap() {

        presenter?.continueButtonDidTap()

        interactor?.changeDatesShouldFail = false
        presenter?.continueButtonDidTap()

        XCTAssert(router?.finishedAmendDidCall == true)
    }
}
