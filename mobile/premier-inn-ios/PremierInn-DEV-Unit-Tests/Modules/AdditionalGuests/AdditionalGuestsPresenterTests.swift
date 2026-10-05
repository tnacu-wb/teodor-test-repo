//
//  AdditionalGuestsPresenterTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 23/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockInteractor: AdditionalGuestsInteractorProtocol {

    var additionalGuestsTracking: AdditionalGuestsTracking { return ("", "") }
    var deleteGuestDidCall = false

    var viewTitle: String {
        return PILocalizedString("additionalGuestsScreenTitle", comment: "")
    }
    var deleteAlertContent: DeleteAlertContent {
        return (
            PILocalizedString("deleteGuestAlertTitle", comment: ""),
            PILocalizedString("deleteGuestAlertMessage", comment: ""),
            PILocalizedString("deleteGuestAlertCancel", comment: ""),
            PILocalizedString("deleteGuestAlertConfirm", comment: "")
        )
    }
    var additionalGuests: [AdditionalGuest]? {
        return [AdditionalGuest(title: "Mr", firstName: "Marcello", lastName: "Mascia", nationality: "Sardinian", email: "mm@mm.com")]
    }
    var guestViewModels: [AdditionalGuestListViewModel]? {

        guard let additionalGuests = additionalGuests else { return nil }
        return additionalGuests.map {
            AdditionalGuestListViewModel(fullName: "\($0.firstName) \($0.lastName)", email: $0.email ?? "")
        }
    }

    func deleteGuest(atIndex index: Int) {

        deleteGuestDidCall = true
    }
}

private class MockRouter: AdditionalGuestsRouterProtocol {

    var addGuestDidCall = false
    var editGuestDidCall = false

    func addAdditionalGuest() {

        addGuestDidCall = true
    }

    func edit(additionalGuest guest: AdditionalGuest, atIndex index: Int) {

        editGuestDidCall = true
    }
}

private class MockView: AdditionalGuestsViewProtocol {

    var setTitleDidCall = false
    var loadViewModelDidCall = false
    var showOptionAlertDidCall = false
    var showErrorMessageDidCall = false
    var updateBusyDidCall = false

    var listViewModels: [AdditionalGuestListViewModel]? {
        get { return nil }
        set {}
    }

    func setTitle(title: String) {

        setTitleDidCall = true
    }

    func loadViewModel() {

        loadViewModelDidCall = true
    }

    func showOptionAlert(withTitle: String?, message: String?, cancelTitle: String, confirmTitle: String, completion: @escaping () -> Void) {

        showOptionAlertDidCall = true
    }

    func showErrorMessage(title: String, error: Error) {

        showErrorMessageDidCall = true
    }

    func updateViewBusy(busy: Bool) {

        updateBusyDidCall = true
    }
}

class AdditionalGuestsPresenterTests: XCTestCase {

    fileprivate var interactor: MockInteractor?
    fileprivate var router: MockRouter?
    fileprivate var view: MockView?

    var presenter: AdditionalGuestsPresenter?

    override func setUp() {
        super.setUp()

        interactor = MockInteractor()
        router = MockRouter()
        view = MockView()

        presenter = AdditionalGuestsPresenter()
        presenter?.interactor = interactor
        presenter?.router = router
        presenter?.view = view
    }

    override func tearDown() {
        super.tearDown()
    }

    func testGuestDeleted() {

        presenter?.guestDeleted(withMessage: "")

        XCTAssert(view?.updateBusyDidCall == true)
    }

    func testGuestDeleteFailed() {

        let error = NSError(domain: "", code: 1, userInfo: nil)
        presenter?.guestDeleteFailed(withError: error)

        XCTAssert(view?.updateBusyDidCall == true)
        XCTAssert(view?.showErrorMessageDidCall == true)
    }

    func testAddGuest() {

        presenter?.selectedAddGuest()

        XCTAssert(router?.addGuestDidCall == true)
    }

    func testEditGuest() {

        presenter?.selectedEditGuest(atIndex: 0)

        XCTAssert(router?.editGuestDidCall == true)
    }

    func testDeleteGuest() {

        presenter?.selectedDeleteGuest(atIndex: 0)

        XCTAssert(view?.showOptionAlertDidCall == true)
    }

    func testViewIsReady() {

        presenter?.viewIsReady()

        XCTAssert(view?.setTitleDidCall == true)
    }
}
