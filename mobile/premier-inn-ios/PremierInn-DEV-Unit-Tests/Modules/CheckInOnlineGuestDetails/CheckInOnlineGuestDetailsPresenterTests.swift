//
//  CheckInOnlineGuestDetailsPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 15/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

private class MockView: CheckInOnlineGuestDetailsViewProtocol {

    var updateScreenTitleDidCall = false
    var updateViewModelDidCall = false
    var updateNationalityDidCall = false
    var updateGuestTitleDidCall = false

    func update(with title: String) {

        updateScreenTitleDidCall = true
    }

    func update(with formViewModel: CheckInOnlineGuestDetailsViewModel) {

        updateViewModelDidCall = true
    }

    func update(nationalityWith formViewModel: CheckInOnlineGuestDetailsViewModel) {

        updateNationalityDidCall = true
    }

    func update(title: String) {

        updateGuestTitleDidCall = true
    }
}

private class MockInteractor: CheckInOnlineGuestDetailsInteractorProtocol {

    private struct ViewModel: CheckInOnlineGuestDetailsViewModel {
        let titleRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let firstNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let lastNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let emailRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let contactNumberRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let nationalityRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let passportNumberRowModel: CheckInOnlineGuestDetailsViewFormRowModel?
        let nextDestinationRowModel: CheckInOnlineGuestDetailsViewFormRowModel?
        let carRegistrationRowModel: CheckInOnlineGuestDetailsViewFormRowModel

        let useBookerAddress: Bool
        let bookerAddressSummary: String
        let address: Address?

        let shouldShowAdditionalGuestRows: Bool
        let additionalGuestTitleRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let additionalGuestFirstNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let additionalGuestLastNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel

        let shouldShowPassportAndNextDestinationRows: Bool
        let carRegistrationSectionTitle: String
        let carRegistrationSectionDescription: String
    }

    private struct RowViewModel: CheckInOnlineGuestDetailsViewFormRowModel {
        let title: String
        let placeholder: String
        let value: Any?
    }

    var getViewModelDidCall = false
    var getScreenTitleDidCall = false
    var getLeadGuestAndRoomIndexDidCall = false
    var updateLeadGuestDidCall = false
    var updateLeadGuestCountryDidCall = false
    var updateLeadGuestTitleDidCall = false

    var viewModel: CheckInOnlineGuestDetailsViewModel {

        getViewModelDidCall = true

        return ViewModel(
            titleRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            firstNameRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            lastNameRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            emailRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            contactNumberRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            nationalityRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            passportNumberRowModel: nil, nextDestinationRowModel: nil,
            carRegistrationRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            useBookerAddress: true,
            bookerAddressSummary: "",
            address: nil,
            shouldShowAdditionalGuestRows: false,
            additionalGuestTitleRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            additionalGuestFirstNameRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            additionalGuestLastNameRowModel: RowViewModel(title: "", placeholder: "", value: nil),
            shouldShowPassportAndNextDestinationRows: false,
            carRegistrationSectionTitle: "",
            carRegistrationSectionDescription: ""
        )
    }

    var screenTitle: String {

        getScreenTitleDidCall = true

        return ""
    }

    var leadGuestAndRoomIndex: LeadGuestAndRoomIndex? {

        getLeadGuestAndRoomIndexDidCall = true

        return (
            LeadGuest(user: nil, nextDestination: nil),
            1
        )
    }

    func update(leadGuestWith formOutput: CheckInOnlineGuestDetailsViewOutput) {

        updateLeadGuestDidCall = true
    }

    func update(leadGuest country: Country) {

        updateLeadGuestCountryDidCall = true
    }

    func update(leadGuest title: String) {

        updateLeadGuestTitleDidCall = true
    }
}

private class MockRouter: CheckInOnlineGuestDetailsRouterProtocol {

    var cancelButtonTappedDidCall = false
    var updatedLeadGuestDidCall = false
    var showCountriesListDidCall = false
    var showTitlesListDidCall = false

    func cancelButtonTapped() {

        cancelButtonTappedDidCall = true
    }

    func updated(leadGuest: PremierInn.LeadGuest, at index: Int) {

        updatedLeadGuestDidCall = true
    }

    func showCountriesList() {

        showCountriesListDidCall = true
    }

    func showTitlesList() {

        showTitlesListDidCall = true
    }
}

class CheckInOnlineGuestDetailsPresenterTests: XCTestCase {

    fileprivate var mockInteractor: MockInteractor?
    fileprivate var mockView: MockView?
    fileprivate var mockRouter: MockRouter?
    fileprivate var presenter: CheckInOnlineGuestDetailsPresenter?

    override func setUp() {
        super.setUp()

        mockInteractor = MockInteractor()
        mockRouter = MockRouter()
        mockView = MockView()

        presenter = CheckInOnlineGuestDetailsPresenter()
        presenter?.view = mockView
        presenter?.interactor = mockInteractor
        presenter?.router = mockRouter
    }

    override func tearDown() {
        super.tearDown()
    }

    func testUserSelectedCountry() {

        presenter?.userSelected(country: Country(code: "", name: "", isoCode: "", dialingCode: nil, flagImage: nil, passportRequired: nil, nationality: nil))

        XCTAssert(mockInteractor?.updateLeadGuestCountryDidCall == true)
        XCTAssert(mockInteractor?.getViewModelDidCall == true)
        XCTAssert(mockView?.updateNationalityDidCall == true)
    }

    func testUserSelectedTitle() {

        presenter?.userSelected(title: "Emperor")

        XCTAssert(mockInteractor?.updateLeadGuestTitleDidCall == true)
        XCTAssert(mockView?.updateGuestTitleDidCall == true)
    }

    func testViewIsReady() {

        presenter?.viewIsReady()

        XCTAssert(mockInteractor?.getScreenTitleDidCall == true)
        XCTAssert(mockView?.updateScreenTitleDidCall == true)
        XCTAssert(mockInteractor?.getViewModelDidCall == true)
        XCTAssert(mockView?.updateViewModelDidCall == true)
    }

    func testCancelButtonTapped() {

        presenter?.cancelDidTap()

        XCTAssert(mockRouter?.cancelButtonTappedDidCall == true)
    }

    func testGuestDetailsDidComplete() {

        let guestDetailsOutput = CheckInOnlineGuestDetailsViewOutput(
            title: "",
            firstName: "",
            lastName: "",
            email: "",
            contactNumber: "",
            nationality: "",
            passportNumber: nil,
            nextDestination: nil,
            carRegistration: nil,
            useBookerAddress: true,
            addressLine1: nil,
            addressLine2: nil,
            addressLine3: nil,
            country: nil,
            postcode: nil,
            additionalGuestTitle: nil,
            additionalGuestFirstName: nil,
            additionalGuestLastName: nil
        )

        presenter?.guestDetailsDidComplete(with: guestDetailsOutput)

        XCTAssert(mockInteractor?.updateLeadGuestDidCall == true)
        XCTAssert(mockInteractor?.getLeadGuestAndRoomIndexDidCall == true)
        XCTAssert(mockRouter?.updatedLeadGuestDidCall == true)
    }

    func testNationalityTapped() {

        presenter?.nationalityRowDidTap()

        XCTAssert(mockRouter?.showCountriesListDidCall == true)
    }

    func testTitleTapped() {

        presenter?.titleRowDidTap()

        XCTAssert(mockRouter?.showTitlesListDidCall == true)
    }
}
