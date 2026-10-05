//
//  UserDetailsPresenterTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 09/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
import SimpleNetwork
@testable import PremierInn

private final class MockView: UserDetailsViewProtocol {
    var parentNavigationController: UINavigationController? {
        return nil
    }

    var viewModelValues: JsonDictionary?

	var updateBookerStayerDidCall = false
	var showCancelButtonDidCall = false
	var setTitleDidCall = false
	var loadViewModelDidCall = false
	var setSalutationDidCall = false
	var setSalutationForFirstGuestDidCall = false
	var reloadDidCall = false
	var endEditingDidCall = false
	var showErrorMessageDidCall = false
	var showErrorForDidCall = false
	var validateViewModelDidCall = false
	var getViewModelValuesDidCall = false
	var getErrorForRowTaggedDidCall = false
    var userDidChangeDidCall = false
    var formLockStateIsLocked = false

    var reloadFirstRoomSectionDidCall = false
    var reloadFirstRoomSectionShouldShow: Bool?

	var modelValidationError: Error?

	func toggleFormLock(locked: Bool, submitButtonTitle: String?) {

        formLockStateIsLocked = locked
	}

	func updateBookerStayer(isStaying: Bool) {

		updateBookerStayerDidCall = true
	}

	func showCancelButton() {

		showCancelButtonDidCall = true
	}

    func addAddressManually(){

    }
    
    func reloadFirstRoomSection(shouldShow: Bool) {
        reloadFirstRoomSectionDidCall = true
        reloadFirstRoomSectionShouldShow = shouldShow
    }

	func setTitle(_ title: String?) {

		setTitleDidCall = true
	}

    func loadMarketingModel(model: UserMarketingModel) {}
    func updateMarketingSection(model: UserMarketingModel?) {}
	func loadViewModel(conf: UserDetailsConfiguration) {

		loadViewModelDidCall = true
	}

	func setSalutation(_ salutation: String?, indexPath: IndexPath) {

		setSalutationDidCall = true
	}

	func setSalutationForFirstGuest(_ salutation: String?) {

		setSalutationForFirstGuestDidCall = true
	}

	func reload() {

		reloadDidCall = true
	}

	func endEditing() {

		endEditingDidCall = true
	}

	func showErrorMessage(title: String, error: Error) {

		showErrorMessageDidCall = true
	}

	func showErrorFor(row: FormekaModelRow) {

		showErrorForDidCall = true
	}

	func validateViewModel() throws {

		validateViewModelDidCall = true

		if let error = modelValidationError {
			throw error
		}
	}

    func getViewModelValues() -> JsonDictionary? {
        getViewModelValuesDidCall = true
        return viewModelValues
    }

	func getErrorForRowTagged(_ tag: String) -> Error? {

		getErrorForRowTaggedDidCall = true

		return nil
	}

    func userDidChange() {

        userDidChangeDidCall = true
    }

    func showErrorMessage(title: String, message: String, error: Error?, handler: ((UIAlertAction) -> Void)?) {

        showErrorMessageDidCall = true
    }

}

private final class MockInteractor: UserDetailsInteractorProtocol {

    var suppressEmailSection: Bool = false
    var isMarketingSwitchEnabled: Bool = false
    var mockScope: UserDetailScope
	var bookerIsStayingDidCall = false
	var roomsDidCall = false
	var purposeGetterDidCall = false
	var purposeSetterDidCall = false
	var reloadRoomsDidCall = false
	var getBookerGuestsAndPurposeDidCall = false
	var bookerIsStayingDidChangeDidCall = false
	var updateUserDidCall = false
    var localizedCost: String?
    var localizedCostWithCityTax: String?
    var cityTaxRequired: Bool = false
    private let loggedUser: User?
    var bookingDetails: BookingDetails?

    var wasUserDetailsComplete: Bool = false
    var updateUserDetailsCompletionStateDidCall = false
    var updateUserDetailsCompletionStateReceivedValue: Bool?
    var stubbedDidCompletionStateChange = false

    var scope: UserDetailScope {
        return mockScope
    }
	var user: User? {
        loggedUser ?? bookingDetails?.booker
	}
	var bookerIsStaying: Bool {
		bookerIsStayingDidCall = true
		return true
	}
	var rooms: [Room] {
		roomsDidCall = true

		return []
	}
	var purpose: TripPurpose? {
		get {
			purposeGetterDidCall = true
            
            return bookingDetails?.purpose
		}
		set {
			purposeSetterDidCall = true
            bookingDetails?.purpose = newValue
		}
	}
    var marketingModel: UserMarketingModel? {
        return nil
    }
    var marketingOptInPreference: Bool? = false
    var tripPurposeModel: TripPurposeMessagesModel? {
        return nil
    }
    var shouldShowCreateAccountSection: Bool = false

	func reloadRooms() {

		reloadRoomsDidCall = true
	}

	func updateUser(with values: PIDictionary?, completion: @escaping (Error?) -> Void) throws {

		updateUserDidCall = true
	}

	func getBookerGuestsAndPurpose(from values: PIDictionary?) throws -> FormStep1Output {

		getBookerGuestsAndPurposeDidCall = true

        let user = mockUser!

        guard let purpose = purpose else { throw Step1Error.missingPurpose }

        return (booker: user, guests: [], purpose: purpose)
	}

	func bookerIsStayingDidChange(isStaying: Bool) {

		bookerIsStayingDidChangeDidCall = true
	}

    func updateMarketingPreference(optin: Bool, emailAddress: String, doubleOptIn: Bool, isoCountryCode: String, completion: (() -> Void)?) { }
    func updateAnonymousNewsLetter(email: String, country: String, completion: @escaping () -> Void) {}
    func holdBookingWithGuests(output: FormStep1Output, isCiolFlow: Bool, completion: @escaping (_ success: Bool, _ error: Error?) -> Void) {}
    func updateCountry(code: String, email: String, completion: @escaping () -> Void) {}

    init(user: User? = nil, bookingDetails: BookingDetails? = nil, scope: UserDetailScope) {
        self.loggedUser = user
        self.bookingDetails = bookingDetails
        self.mockScope = scope
    }

    func updateMarketingValues(optIn: Bool?, suppress: Bool) {
        
    }

    func getMarketingPreferences(
        for emailAddress: String,
        and brands: MarketingBrandCode,
        isBusiness: Bool,
        completion: @escaping (Bool) -> Void
    ) {

    }

    func updateUserDetailsCompletionState(_ isComplete: Bool) -> Bool {
        updateUserDetailsCompletionStateDidCall = true
        updateUserDetailsCompletionStateReceivedValue = isComplete
        wasUserDetailsComplete = isComplete
        return stubbedDidCompletionStateChange
    }
}

private final class MockRouter: UserDetailsRouterProtocol {

	var presentLoginDidCall = false
	var presentSalutationPickerDidCall = false
	var continueToNextScreenDidCall = false
    var showAddressPickerScreenDidCall = false
	var goBackDidCall = false
    var goToDeleteAccountDidCall = false
    var goBackToMyAccountDidCall = false
	func presentLogin() {

		presentLoginDidCall = true
	}

	func presentSalutationPicker(for indexPath: IndexPath) {

		presentSalutationPickerDidCall = true
	}

    func continueToNextScreen(output: FormStep1Output) {

		continueToNextScreenDidCall = true
	}

    func showAddressPickerScreen(with searchTerm: SearchTerm, completion: @escaping (Address?) -> Void) {

        showAddressPickerScreenDidCall = true
    }

	func goBack() {

		goBackDidCall = true
	}

    func goBackToMyAccount() {

        goBackToMyAccountDidCall = true
    }

    func goToDeleteAccount() {

        goToDeleteAccountDidCall = true
    }
}

final class UserDetailsPresenterTests: XCTestCase {

	private var view: MockView!
	private var interactor: MockInteractor!
	private var router: MockRouter!
	var presenter: UserDetailsPresenter!

	override func setUp() {
		super.setUp()

		view = MockView()
        
        BookingDetails.sharedInstance.purpose = .leisure

        interactor = MockInteractor(user: mockUser,
                                    bookingDetails: BookingDetails.sharedInstance,
                                    scope: .bookingFlow)

		presenter = UserDetailsPresenter()
		presenter.view = view
		presenter.interactor = interactor

		router = MockRouter()

		presenter.router = router
	}

	override func tearDown() {

		view = nil
		interactor = nil
		presenter = nil
		router = nil

		super.tearDown()
	}
    
    func testViewIsReady() {

		presenter.viewIsReady()

		XCTAssertFalse(view.showCancelButtonDidCall)
		XCTAssertTrue(view.setTitleDidCall)
		XCTAssertTrue(view.loadViewModelDidCall)
	}

	func testViewIsReady_Modal() {

		view = MockView()
        interactor = MockInteractor(scope: .bookingFlowEditing)
		presenter = UserDetailsPresenter()
		presenter.view = view
		presenter.interactor = interactor

		presenter.viewIsReady()

		XCTAssertTrue(view.showCancelButtonDidCall)
		XCTAssertTrue(view.setTitleDidCall)
		XCTAssertTrue(view.loadViewModelDidCall)
	}

	func testBookerStayerDidChange() {

		presenter.bookerIsStayingDidChange(isStaying: true)

		XCTAssertTrue(interactor.bookerIsStayingDidChangeDidCall)
		XCTAssertTrue(view.updateBookerStayerDidCall)
	}

	func testSalutationDidTap() {

		presenter.salutationRowDidTap(indexPath: IndexPath(row: 0, section: 0))

		XCTAssertTrue(router.presentSalutationPickerDidCall)
	}

	func testSubmitButtonDidTap() {

		presenter.submitButtonDidTap()

        let expectation = self.expectation(description: "continueToNextScreen on main thread")

		XCTAssertTrue(view.endEditingDidCall)
		XCTAssertTrue(view.validateViewModelDidCall)
		XCTAssertTrue(interactor.getBookerGuestsAndPurposeDidCall)

        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1) {
            expectation.fulfill()
        }
        waitForExpectations(timeout: 5, handler: nil)

		XCTAssertTrue(router.continueToNextScreenDidCall)
        XCTAssertFalse(view.formLockStateIsLocked)
	}

	func testSubmitButtonDidTap_GenericError() {

        let expectation = self.expectation(description: "continueToNextScreen on main thread")

		view.modelValidationError = Step1Error.missingBookerIsStayer
		presenter.submitButtonDidTap()

		XCTAssertTrue(view.endEditingDidCall)
		XCTAssertTrue(view.validateViewModelDidCall)
		XCTAssertFalse(interactor.getBookerGuestsAndPurposeDidCall)

        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1) {
            expectation.fulfill()
        }
        waitForExpectations(timeout: 5, handler: nil)

		XCTAssertFalse(router.continueToNextScreenDidCall)
		XCTAssertTrue(view.showErrorMessageDidCall)
        XCTAssertFalse(view.formLockStateIsLocked)
	}

	func testSubmitButtonDidTap_ValidationError() {

		let row = FormekaModelRow(tag: "aRow", cellSetup: { _, _, _ in nil })
		view.modelValidationError = RowValidatorError(row: row, error: Step1Error.missingBookerIsStayer)
		presenter.submitButtonDidTap()

		XCTAssertTrue(view.endEditingDidCall)
		XCTAssertTrue(view.validateViewModelDidCall)
		XCTAssertFalse(interactor.getBookerGuestsAndPurposeDidCall)
		XCTAssertFalse(router.continueToNextScreenDidCall)
		XCTAssertTrue(view.showErrorForDidCall)
        XCTAssertFalse(view.formLockStateIsLocked)
	}
    
    func testSubmitButtonDidTap_DoesNotRoute_WhenBookingDetailsIsNil() {
        // Arrange
        interactor = MockInteractor(user: mockUser,
                                    bookingDetails: nil,
                                    scope: .bookingFlow)
        
        presenter.interactor = interactor
        
        // Act
        presenter.submitButtonDidTap()
        
        // Assert
        XCTAssertTrue(view.endEditingDidCall)
        XCTAssertTrue(view.validateViewModelDidCall)
        XCTAssertTrue(interactor.getBookerGuestsAndPurposeDidCall)
        XCTAssertFalse(router.continueToNextScreenDidCall)
        XCTAssertFalse(view.formLockStateIsLocked)
    }

	func testLoginButtonDidTap() {

		presenter.loginButtonDidTap()

		XCTAssertTrue(router.presentLoginDidCall)
	}

	func testSalutationSetter() {

		presenter.setSalutation("Mr", at: IndexPath(row: 0, section: 0))

		XCTAssertTrue(view.setSalutationDidCall)
		XCTAssertTrue(interactor.bookerIsStayingDidCall)
		XCTAssertTrue(view.setSalutationForFirstGuestDidCall)
		XCTAssertTrue(view.reloadDidCall)
	}

	func testStuff() {

		_ = presenter.rooms
		XCTAssertTrue(interactor.roomsDidCall)

		XCTAssertTrue(presenter.bookerIsStaying)

		_ = presenter.purpose
		XCTAssertTrue(interactor.purposeGetterDidCall)

		presenter.purpose = nil
		XCTAssertTrue(interactor.purposeSetterDidCall)
	}

	func testSubmitButton_BookingFlow() {

		view = MockView()
        interactor = MockInteractor(scope: .bookingFlow)
		presenter = UserDetailsPresenter()
		presenter.view = view
		presenter.interactor = interactor

		XCTAssertEqual(presenter.configuration.submitButtonBackgroundColor, .Tint1)
		XCTAssertEqual(presenter.configuration.submitButtonForegroundColor, .BaseWhite)
		XCTAssertEqual(presenter.configuration.submitButtonTitle, PILocalizedString("Continue"))
	}

	func testSubmitButton_Editing() {

		view = MockView()
        interactor = MockInteractor(scope: .bookingFlowEditing)
		presenter = UserDetailsPresenter()
		presenter.view = view
		presenter.interactor = interactor

		XCTAssertEqual(presenter.configuration.submitButtonBackgroundColor, .BasePurple)
		XCTAssertEqual(presenter.configuration.submitButtonForegroundColor, .BaseWhite)
		XCTAssertEqual(presenter.configuration.submitButtonTitle, PILocalizedString("userDetailsSubmitButtonUpdate", comment: ""))
	}

    // MARK: - Room 1 Hide/Show Logic

    func testUserDetailsDidChangeReloadsFirstRoomSectionWhenCompletionStateChangesToComplete() {
        UserSessionManager.sharedInstance.userLoggedOut()

        view.viewModelValues = [
            Step1Row.salutation.rawValue + Constants.bookerSuffix: "Mr",
            Step1Row.firstName.rawValue + Constants.bookerSuffix: "Marcello",
            Step1Row.lastName.rawValue + Constants.bookerSuffix: "Mascia",
            Step1Row.emailAddress.rawValue + Constants.bookerSuffix: "marcello@test.com",
            Step1Row.contactNumber.rawValue + Constants.bookerSuffix: "123456789"
        ]
        interactor.stubbedDidCompletionStateChange = true

        presenter.userDetailsDidChange()

        XCTAssertTrue(view.getViewModelValuesDidCall)
        XCTAssertTrue(interactor.updateUserDetailsCompletionStateDidCall)
        XCTAssertEqual(interactor.updateUserDetailsCompletionStateReceivedValue, true)
        XCTAssertTrue(view.reloadFirstRoomSectionDidCall)
        XCTAssertEqual(view.reloadFirstRoomSectionShouldShow, true)
    }

    func testUserDetailsDidChangeReloadsFirstRoomSectionWhenCompletionStateChangesToIncomplete() {
        UserSessionManager.sharedInstance.userLoggedOut()

        view.viewModelValues = [
            Step1Row.salutation.rawValue + Constants.bookerSuffix: "Mr",
            Step1Row.firstName.rawValue + Constants.bookerSuffix: "Marcello",
            Step1Row.lastName.rawValue + Constants.bookerSuffix: "",
            Step1Row.emailAddress.rawValue + Constants.bookerSuffix: "marcello@test.com",
            Step1Row.contactNumber.rawValue + Constants.bookerSuffix: "123456789"
        ]
        interactor.stubbedDidCompletionStateChange = true

        presenter.userDetailsDidChange()

        XCTAssertTrue(view.getViewModelValuesDidCall)
        XCTAssertTrue(interactor.updateUserDetailsCompletionStateDidCall)
        XCTAssertEqual(interactor.updateUserDetailsCompletionStateReceivedValue, false)
        XCTAssertTrue(view.reloadFirstRoomSectionDidCall)
        XCTAssertEqual(view.reloadFirstRoomSectionShouldShow, false)
    }

    func testUserDetailsDidChangeDoesNotReloadFirstRoomSectionWhenCompletionStateDoesNotChange() {
        UserSessionManager.sharedInstance.userLoggedOut()

        view.viewModelValues = [
            Step1Row.salutation.rawValue + Constants.bookerSuffix: "Mr",
            Step1Row.firstName.rawValue + Constants.bookerSuffix: "Marcello",
            Step1Row.lastName.rawValue + Constants.bookerSuffix: "Mascia",
            Step1Row.emailAddress.rawValue + Constants.bookerSuffix: "marcello@test.com",
            Step1Row.contactNumber.rawValue + Constants.bookerSuffix: "123456789"
        ]
        interactor.stubbedDidCompletionStateChange = false

        presenter.userDetailsDidChange()

        XCTAssertTrue(view.getViewModelValuesDidCall)
        XCTAssertTrue(interactor.updateUserDetailsCompletionStateDidCall)
        XCTAssertEqual(interactor.updateUserDetailsCompletionStateReceivedValue, true)
        XCTAssertFalse(view.reloadFirstRoomSectionDidCall)
    }
}

// MARK: - Helpers

fileprivate var mockUser: User? {
    try? User(title: "Mr", firstName: "Santo", lastName: "Pallino")
}
