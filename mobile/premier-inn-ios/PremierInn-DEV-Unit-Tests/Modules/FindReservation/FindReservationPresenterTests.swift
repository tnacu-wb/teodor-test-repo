//
//  FindReservationPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 06/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
import Formeka
@testable import PremierInn

private final class MockView: FindReservationViewProtocol {
    var loadViewModelDidCall = false
    var disableSubmitButtonDidCall = false
    var enableSubmitButtonDidCall = false
    var setCheckInDateDidCall = false
    var stopEditingDidCall = false
    var validateFormDidCall = false
    var showErrorDidCall = false
    var showRowErrorDidCall = false
    
    func loadViewModel(shouldShowError: Bool, arrivalDate: Date?, reservationNumber: String?, lastName: String?) {
        loadViewModelDidCall = true
    }
    
    func disableSubmitButton() {
        
        disableSubmitButtonDidCall = true
    }
    
    func enableSubmitButton() {
        
        enableSubmitButtonDidCall = true
    }
    
    func setCheckInDate(date: Date) {
        
        setCheckInDateDidCall = true
    }
    
    func stopEditing() {
        
        stopEditingDidCall = true
    }
    
    func validateForm() throws -> PIDictionary? {
        
        validateFormDidCall = true
        
        return ["good": "stuff"]
    }
    
    func showError(title: String, message: String?) {
        
        showErrorDidCall = true
    }
    
    func showRowError(_ error: RowValidatorError) {
        
        showRowErrorDidCall = true
    }
}

private final class MockRouter: FindReservationRouterProtocol {

    var showCalendarPickerDidCall = false
    var showBartErrorDidCall = false
    var navigateToBookingConfirmationDidCall = false
    
    func showCalendarPicker(withSelectedDate: Date, startDate: Date) {
        
        showCalendarPickerDidCall = true
    }
    
    func showBartError() {
        
        showBartErrorDidCall = true
    }
    
    func navigateToBookingConfirmation(stay: SimpleNetwork.Stay?) {
        navigateToBookingConfirmationDidCall = true
    }
}

private final class MockInteractor: FindReservationInteractorProtocol {
    var arrivalDate: Date?
    var reservationNumber: String?
    var lastName: String?
    var errorToThrow: Error?
    var shouldReturnBartError = false
    var shouldFailRequest = false
    var submitFormDataDidCall = false
    var trackErrorDidCall = false

    var searchForLocalStayUsingReservationDidCall = false
    var searchForLocalStayUsingReservationReturnValue: Stay? = .none

    func submitFormData(values: PIDictionary, completion: @escaping (Result<Reservation>) -> Void) throws {
        
        submitFormDataDidCall = true
        
        if let error = errorToThrow {
            throw error
        }
        
        if shouldReturnBartError {
            return completion(.failure(error: RequestsManagerError.maintenanceMode))
        }
        
        if shouldFailRequest {
            return completion(.failure(error: RequestsManagerError.unexpectedResponseError))
        }
        
        let reservation = try! Reservation(dictionary: MiscTests.reservationDictionary)
        completion(.success(result: reservation))
    }

    func searchForLocalStayUsingReservation(_ reservation: Reservation) -> Stay? {
        searchForLocalStayUsingReservationDidCall = true
        return searchForLocalStayUsingReservationReturnValue
    }

    func trackError(_ error: any Error) {
        trackErrorDidCall = true
    }
}

final class FindReservationPresenterTests: XCTestCase {
    
    private var presenter: FindReservationPresenter!
    private var view: MockView!
    private var router: MockRouter!
    private var interactor: MockInteractor!
    
    override func setUp() {
        
        view = MockView()
        router = MockRouter()
        interactor = MockInteractor()
        
        presenter = FindReservationPresenter()
        presenter.view = view
        presenter.router = router
        presenter.interactor = interactor
    }
    
    override func tearDown() {
        
        presenter = nil
        view = nil
        router = nil
        interactor = nil
        
        super.tearDown()
    }
    
    func testViewIsReady() {
        
        presenter.viewIsReady()
        XCTAssertTrue(view.loadViewModelDidCall)
    }
    
    func testCalendarButton() {
        
        presenter.calendarButtonDidTap(date: Date())
        XCTAssertTrue(router.showCalendarPickerDidCall)
    }
    
    func testCalendarSelection() {
        
        presenter.calendarDidSelect(date: Date())
        XCTAssertTrue(view.setCheckInDateDidCall)
    }
    
    func testSubmitButtonAction() {
        
        presenter.submitButtonDidTap()
        XCTAssertTrue(view.stopEditingDidCall)
        XCTAssertTrue(view.disableSubmitButtonDidCall)
        XCTAssertTrue(view.validateFormDidCall)
        XCTAssertTrue(interactor.submitFormDataDidCall)
        XCTAssertTrue(router.navigateToBookingConfirmationDidCall)
    }
    
    func testSubmitButtonAction_MissingReferenceNumber() {
        
        interactor.errorToThrow = ReservationDetailsError.missingReferenceNumber
        presenter.submitButtonDidTap()
        
        XCTAssertTrue(view.stopEditingDidCall)
        XCTAssertTrue(view.disableSubmitButtonDidCall)
        XCTAssertTrue(view.validateFormDidCall)
        XCTAssertTrue(interactor.submitFormDataDidCall)
        XCTAssertTrue(view.enableSubmitButtonDidCall)
        XCTAssertTrue(view.showErrorDidCall)
        XCTAssertFalse(view.showRowErrorDidCall)
    }
    
    func testSubmitButtonAction_RowValidatorError() {
        
        let row = FormekaModelRow(tag: "aRow", cellSetup: { _, _, _ in nil })
        
        interactor.errorToThrow = RowValidatorError(row: row, error: ReservationDetailsError.missingReferenceNumber)
        presenter.submitButtonDidTap()
        
        XCTAssertTrue(view.stopEditingDidCall)
        XCTAssertTrue(view.disableSubmitButtonDidCall)
        XCTAssertTrue(view.validateFormDidCall)
        XCTAssertTrue(interactor.submitFormDataDidCall)
        XCTAssertTrue(view.enableSubmitButtonDidCall)
        XCTAssertFalse(view.showErrorDidCall)
        XCTAssertTrue(view.showRowErrorDidCall)
    }
    
    func testSubmitButtonAction_BartError() {
        
        interactor.shouldReturnBartError = true
        presenter.submitButtonDidTap()
        
        XCTAssertTrue(view.stopEditingDidCall)
        XCTAssertTrue(view.disableSubmitButtonDidCall)
        XCTAssertTrue(view.validateFormDidCall)
        XCTAssertTrue(interactor.submitFormDataDidCall)
        XCTAssertTrue(view.enableSubmitButtonDidCall)
        XCTAssertFalse(view.showErrorDidCall)
        XCTAssertFalse(view.showRowErrorDidCall)
        XCTAssertTrue(router.showBartErrorDidCall)
    }
    
    func testSubmitButtonAction_FindReservationErrorWithBanner() {
        
        interactor.shouldFailRequest = true
        presenter.submitButtonDidTap()
        
        XCTAssertTrue(view.stopEditingDidCall)
        XCTAssertTrue(view.disableSubmitButtonDidCall)
        XCTAssertTrue(view.validateFormDidCall)
        XCTAssertTrue(interactor.submitFormDataDidCall)
        XCTAssertTrue(view.enableSubmitButtonDidCall)
        XCTAssertFalse(view.showErrorDidCall)
        XCTAssertFalse(view.showRowErrorDidCall)
        XCTAssertTrue(view.loadViewModelDidCall)
    }
}
