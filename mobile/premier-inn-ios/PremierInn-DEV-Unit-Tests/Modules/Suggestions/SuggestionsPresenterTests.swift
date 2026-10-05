//
//  SuggestionsPresenterTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 11/12/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation
import SimpleNetwork
@testable import PremierInn

private class MockSuggestion: NSObject, Suggestion {
    
    var type: SuggestionType { return .location }
    var title: String? { return "A place" }
	var subtitle: String? { return nil }
	var iconName: String? { return nil }
	var rangeOfSearchTerm: NSRange? { return nil }
	var coordinate: CLLocationCoordinate2D {
        get {
            return CLLocationCoordinate2D(latitude: 12, longitude: 2)
        }
        set {

        }
    }
	var isHotel: Bool { return false }
	var identifier: String? { return "FAKE-ID" }
    var brand: HotelBrand? { return nil }
}

private class MockView: SuggestionsViewProtocol {

    var parentNavigationController: UINavigationController? {
        return nil
    }

	private let table = UITableView()
	var setSuggestionTitleDidCall = false
	var showSuggestionsDidCall = false
	var hideSuggestionsDidCall = false
	var showErrorDidCall = false

	func showError(message: String) {

		showErrorDidCall = true
	}

	func showSuggestionsTable() {

		showSuggestionsDidCall = true
	}

	func deselectSelectedCellIfAny() {

	}

	func hideSuggestionsTable() {

		hideSuggestionsDidCall = true
	}

	func dequeueCell<T>(for indexPath: IndexPath) -> T? where T : UITableViewCell {

		return table.dequeueCell(for: indexPath)
	}

	func dequeueReusableHeaderFooterView(withIdentifier identifier: String) -> UITableViewHeaderFooterView? {

		return nil
	}

	func headerFooterView<T>() -> T? where T : UITableViewHeaderFooterView {

		return table.headerFooterView()
	}

	func reloadSection(at indexSet: IndexSet) {

	}

	func showLoadingIndicator() {

	}

	func hideLoadingIndicator() {

	}

	func trackAnalytics(searchTerm: String) {

	}

	func toggleNoResultsMessage(visible: Bool) {

	}

	func reloadSuggestionsTable() {

	}

	func suggestionDidSelect(withText text: String?) {

	}

	func presentAlertController(_ controller: UIAlertController) {

	}

	func setSuggestionTitle(_ title: String?) {

		setSuggestionTitleDidCall = true
	}
}

private class MockInteractor: SuggestionsInteractorProtocol {

	var cancelConnectionsDidCall = false
	var loadLocalSuggestionsDidCall = false
	var loadRemoteSuggestionsDidCall = false
	var saveSuggestionDidCall = false
	var clearRecententSearchesDidCall = false

	func clearRecenteSearches() {

		clearRecententSearchesDidCall = true
	}

	func cancelConnections() {

		cancelConnectionsDidCall = true
	}

	func loadLocalSuggestions(completion: @escaping ([Suggestion]?, SuggestionsSource) -> Void) {

		loadLocalSuggestionsDidCall = true

		completion(nil, .history)
	}

	func loadRemoteSuggestions(searchTerm: String, completion: @escaping ([PISuggestion]?, Error?) -> Void) {

		loadRemoteSuggestionsDidCall = true

		completion(nil, nil)
	}

	func saveSuggestionToRecentSearches(_ suggestion: Suggestion) {

		saveSuggestionDidCall = true
	}

    func updateCoordinates(for googlePlacesSuggestion: Suggestion, completion: @escaping (_ suggestion: Suggestion?, _ success: Bool) -> Void) {
        
    }
}

class SuggestionsPresenterTests: XCTestCase {

	private var view: MockView!
	private var interactor: MockInteractor!
	private var presenter: SuggestionsPresenter!

    override func setUp() {
        super.setUp()

		view = MockView()
		interactor = MockInteractor()
		presenter = SuggestionsPresenter(suggestion: nil)
		presenter.view = view
		presenter.interactor = interactor
    }
    
    override func tearDown() {

		view = nil
		interactor = nil
		presenter = nil

        super.tearDown()
    }

	func testViewIsReady_NoSuggestion() {

		presenter.viewIsReady()

		XCTAssertTrue(view.setSuggestionTitleDidCall)
		XCTAssertTrue(view.showSuggestionsDidCall)
		XCTAssertFalse(view.hideSuggestionsDidCall)
		XCTAssertTrue(interactor.cancelConnectionsDidCall)
		XCTAssertTrue(interactor.loadLocalSuggestionsDidCall)
		XCTAssertFalse(interactor.loadRemoteSuggestionsDidCall)
	}

	func testViewIsReady_ExistingSuggestion() {

		let suggestion = PISuggestion(coordinate: CLLocationCoordinate2D(latitude: 1, longitude: 1))

		view = MockView()
		presenter = SuggestionsPresenter(suggestion: suggestion)
		presenter.view = view

		presenter.viewIsReady()
		XCTAssertTrue(view.setSuggestionTitleDidCall)
		XCTAssertFalse(view.showSuggestionsDidCall)
		XCTAssertTrue(view.hideSuggestionsDidCall)
	}

	func testViewIsReady_ExistingSuggestionTriggerSearch() {

		let suggestion = PISuggestion(title: "SW19 3SH")

		view = MockView()
		interactor = MockInteractor()

		presenter = SuggestionsPresenter(suggestion: suggestion)
		presenter.view = view
		presenter.interactor = interactor

		presenter.viewIsReady()

		wait(for: SuggestionsPresenter.searchDelay + 1, description: #function)

		XCTAssertTrue(view.setSuggestionTitleDidCall)
		XCTAssertFalse(interactor.loadLocalSuggestionsDidCall)
		//XCTAssertTrue(interactor.loadRemoteSuggestionsDidCall)
		XCTAssertTrue(view.showSuggestionsDidCall)
		XCTAssertFalse(view.hideSuggestionsDidCall)
	}

	func testTextChanges_FewChars() {

		presenter.searchTextDidChange("")

		XCTAssertTrue(interactor.cancelConnectionsDidCall)
		XCTAssertTrue(interactor.loadLocalSuggestionsDidCall)
		XCTAssertFalse(interactor.loadRemoteSuggestionsDidCall)
		XCTAssertTrue(view.showSuggestionsDidCall)
		XCTAssertFalse(view.hideSuggestionsDidCall)
	}

	func testTextChanges_EnoughChars() {

		presenter.searchTextDidChange("lon")

        // This is called immediately
        XCTAssertTrue(view.showSuggestionsDidCall)
        
        // searchTextDidChange schedule a timer of .searchDelay seconds
        // before calling the other functions
        wait(for: SuggestionsPresenter.searchDelay, description: #function)
        XCTAssertTrue(interactor.cancelConnectionsDidCall)
        XCTAssertFalse(interactor.loadLocalSuggestionsDidCall)
        XCTAssertTrue(interactor.loadRemoteSuggestionsDidCall)
        
        XCTAssertFalse(view.hideSuggestionsDidCall)
	}

	func testSuggestionSelection_MyLocation() {

		let suggestion = PISuggestion(coordinate: CLLocationCoordinate2D(latitude: 1, longitude: 1))

		presenter.didSelect(suggestion: suggestion)

		XCTAssertFalse(view.showSuggestionsDidCall)
		XCTAssertTrue(view.hideSuggestionsDidCall)
		XCTAssertFalse(interactor.saveSuggestionDidCall)
	}

	func testSuggestionSelection_RegularSuggestion() {

		let suggestion = MockSuggestion()

		presenter.didSelect(suggestion: suggestion)

		XCTAssertFalse(view.showSuggestionsDidCall)
		XCTAssertTrue(view.hideSuggestionsDidCall)
		XCTAssertTrue(interactor.saveSuggestionDidCall)
	}

	func testClearRecentSearches() {

		presenter.actionLabelDidTap(header: SimpleHeaderWithActionLabel())

		XCTAssertTrue(interactor.clearRecententSearchesDidCall)
		XCTAssertTrue(interactor.loadLocalSuggestionsDidCall)
		XCTAssertFalse(interactor.loadRemoteSuggestionsDidCall)
	}

}
