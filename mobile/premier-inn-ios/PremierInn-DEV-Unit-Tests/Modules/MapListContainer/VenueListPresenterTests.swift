//
//  VenueListPresenterTests.swift
//  PremierInnTests
//
//  Created by Nick Jones on 13/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import MapKit
import SimpleNetwork
@testable import PremierInn

private class MockMapView: MapViewProtocol {

    var centerCoordinate: CLLocationCoordinate2D { return CLLocationCoordinate2D.zero }

	var reloadDidCall = false
	var mapWillGoFullDidCall = false
	var mapWillGoSmallDidCall = false
	var deselectAnnotationDidCall = false
	var selectAnnotationDidCall = false
	var centerAnnotationDidCall = false
	var zoomToAnnotationDidCall = false
	var fitAnnotationsDidCall = false
	var showListButtonDidCall = false
	var hideListButtonDidCall = false
	var showResetButtonDidCall = false
	var hideResetButtonDidCall = false
    var showSearchHereButtonDidCall = false
    var hideSearchHereButtonDidCall = false

	func reload() {

		reloadDidCall = true
	}

	func mapWillGoFullScreen() {

		mapWillGoFullDidCall = true
	}

	func mapWillGoSmall() {

		mapWillGoSmallDidCall = true
	}

	func deselectAnnotation(_ annotation: MKAnnotation) {

		deselectAnnotationDidCall = true
	}

	func selectAnnotation(_ annotation: MKAnnotation) {

		selectAnnotationDidCall = true
	}

	func centerAnnotation(_ annotation: MKAnnotation) {

		centerAnnotationDidCall = true
	}

	func zoomToAnnotation(_ annotation: MKAnnotation) {

		zoomToAnnotationDidCall = true
	}

	func fitAnnotationsInView(style: VenuesListStyle, animated: Bool) {

		fitAnnotationsDidCall = true
	}

	func showListButton() {

		showListButtonDidCall = true
	}

	func hideListButton() {

		hideListButtonDidCall = true
	}

	func showResetButton() {

		showResetButtonDidCall = true
	}

	func hideResetButton() {

		hideResetButtonDidCall = true
	}

    func showSearchHereButton() {

        showSearchHereButtonDidCall = true
    }

    func hideSearchHereButton() {

        hideSearchHereButtonDidCall = true
    }
}

private class MockContainerView: MapListContainerViewProtocol {

    var makeMapFullDidCall = false
    var makeMapSmallDidCall = false
    var popViewControllerDidCall = false
    var updateConstraintsDidCall = false
    var hideCardsDidCall = false
    var showCardsDidCall = false
    var revertSegmentDidCall = false
    var provideHapticDidCall = false

    func getCurrentOptionsViewTopHandleOffset() -> CGFloat {

        return 0
    }

    func userDidScroll() {

    }

    func updateCriteria(to criteria: Criteria) {

    }

    func updatedSuggestion(to suggestion: Suggestion) {

    }

    func refreshCriteriaElements() {

    }

    func errorDidOccur() {

    }

    func showLoadingBox() {

    }

    func hideLoadingBox() {
        
    }

	func makeMapFullScreen() {

		makeMapFullDidCall = true
	}

	func makeMapSmall() {

		makeMapSmallDidCall = true
	}

	func popViewController() {

		popViewControllerDidCall = true
	}

	func updateMapConstraints(optionsViewTop: CGFloat) {

		updateConstraintsDidCall = true
	}

    func toggleOptionsView(visible: Bool) {

    }

    func slideOptionsViewIn() {

    }
    
    func slideOptionsViewInWithoutAnimation() {
        
    }
    
    func slideOptionsViewOut() {

    }

    func hideSortBar() {

    }

    func showSortBar() {

    }

	func hideCards() {

		hideCardsDidCall = true
	}

	func showCards() {

		showCardsDidCall = true
	}

	func revertSegmentedControlToPreviousSetting() {

		revertSegmentDidCall = true
	}

	func provideHapticFeedback() {

		provideHapticDidCall = true
	}

    func hotelsWereReloaded() {

    }

    func showError(alertController: UIAlertController) {

    }

    func showFallBack() {

    }
}

private class MockListView: ListViewProtocol {

	var reloadDidCall = false
	var mapWillGoFullScreenDidCall = false
	var mapDidGoFullScreenDidCall = false
	var scrooToItemDidCall = false
	var willReturnToListViewDidCall = false
	var selectCellDidCall = false
	var showErrorMessageDidCall = false
	var removeErrorMessageDidCall = false

	func reload() {

		reloadDidCall = true
	}

	func mapWillGoFullScreen() {

		mapWillGoFullScreenDidCall = true
	}

	func mapDidGoFullScreen() {

		mapDidGoFullScreenDidCall = true
	}

	func scrollToItemAt(indexPath: IndexPath, andSelect selectCell: Bool?) {

		scrooToItemDidCall = true
	}

	func willReturnToListView() {

		willReturnToListViewDidCall = true
	}

	func selectCellAt(indexPath: IndexPath) {

		selectCellDidCall = true
	}

    func selectedListCell() -> VenueCell? {

        return nil
    }

	func showErrorMessage(message: String?, bottomConstraint: CGFloat) {

		showErrorMessageDidCall = true
	}

	func removeErrorMessage() {

		removeErrorMessageDidCall = true
	}
}

private class MockInteractor: VenueInteractorProtocol {

    var criteria: Criteria {
        return Criteria()
    }

	var fetchHotelsDidCall = false
	var venuesDidCall = false
	var errorDidCall = false

	var venues: [Venue] {

		venuesDidCall = true

		let hotel: Hotel = try! Hotel(dictionary: [
			"name": "Hotel Name",
			"code": "LONLEI",
			"prepaymentAllowed": true,
			"address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
			"images": [["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]]
			])

		return [hotel]
	}
	var suggestion: Suggestion? { return nil }
    var lastSuccessfulSuggestion: Suggestion? { return nil }
	var screenTitle: String? { return nil }
	var screenSubtitle: String? { return nil }
	var suggestionAndAllVenues: [Venue] { return [] }
	var suggestionAndVenuesInReasonableDistance: [Venue] { return [] }
	var error: LocalizedError? {
		errorDidCall = true
		return nil
	}
    var shouldShowCoronavirusInformationBanner: Bool { return false }

    func fetchHotels(hotelDetailsRouterDelegate: HotelDetailsRouterDelegate?, completion: @escaping (Bool, UIViewController?) -> Void) {

    }

    func updateSuggestion(to suggestion: Suggestion) {
        
    }

	func fetchHotels(with sorting: AvailabilitiesSorting, completion: @escaping (Bool) -> Void) {

		fetchHotelsDidCall = true

		completion(true)
	}

    func fetchHotelsNearby(with sorting: AvailabilitiesSorting, completion: @escaping (Bool) -> Void) {

        completion(true)
    }

	func trackAvailability(style: VenuesListStyle) {

	}

	func logToFirebase() {

	}

    func indexOfHotel(with code: String) -> Int? { return nil }

    func didTapDismissCoronavirusInformationBanner() {}

    func shouldShowFallBack() -> Bool {
        return false
    }
}

private class MockRouter: MapListContainerRouterProtocol {

    var navigateToHotelDidCall = false

    func selectedLocation() {

    }

    func selectedNights() {

    }

    func selectedGuests() {

    }

    func showHotelDetailsPage(_ hotelController: HotelDetailsViewController) {

    }

	func showMapModule() {

	}

	func showListModule() {

	}

	func showCardsModule() {

	}

	func navigateTo(hotel: Hotel, at indexPath: IndexPath, suggestion: Suggestion?, isMapVisible: Bool) {

		navigateToHotelDidCall = true
	}

	func presentCriteriaViewController(with suggestion: Suggestion) {

	}

    func updateHotelDetailsIfVisible() {
        
    }

    func hideHotelDetailsIfVisible() {
        
    }
}

class VenueListPresenterTests: XCTestCase {

    private var presenter: MapListContainerPresenter!
	private var containerView: MockContainerView!
    private var listView: MockListView!
	private var mapView: MockMapView!
    private var interactor: MockInteractor!
	private var router: MockRouter!

    override func setUp() {
        super.setUp()

		containerView = MockContainerView()
        listView = MockListView()
		mapView = MockMapView()
        interactor = MockInteractor()
		router = MockRouter()

        presenter = MapListContainerPresenter()
		presenter.containerView = containerView
        presenter.listView = listView
		presenter.mapView = mapView
        presenter.interactor = interactor
		presenter.router = router
    }

    override func tearDown() {

		listView = nil
        interactor = nil
        presenter = nil

        super.tearDown()
    }

    func testViewIsReady() {

        presenter.listViewIsReady()

		XCTAssertTrue(listView.reloadDidCall)
    }

	func testNumberOfVenues() {

		_ = presenter.numberOfVenues()

		XCTAssertTrue(interactor.venuesDidCall)
	}

	func testHotelForIndexPath() {

		_ = presenter.hotel(for: IndexPath(item: 0, section: 0))

		XCTAssertTrue(interactor.venuesDidCall)
	}

	func testDidSelectItem() {

		presenter.didSelectItem(at: IndexPath(item: 0, section: 0), mapVisible: true)

		XCTAssertTrue(interactor.venuesDidCall)
		XCTAssertTrue(router.navigateToHotelDidCall)
	}

	func testListDidScroll() {

		presenter.listDidScroll(to: -10)
		XCTAssertFalse(containerView.makeMapFullDidCall)
		XCTAssertFalse(containerView.provideHapticDidCall)
		XCTAssertTrue(containerView.updateConstraintsDidCall)
	}

	func testListDidScroll_PullToOpen() {

		presenter.listDidScroll(to: Constants.searchResultsMapListDragToPopAmount)
		XCTAssertTrue(containerView.makeMapFullDidCall)
		XCTAssertTrue(containerView.provideHapticDidCall)
		XCTAssertFalse(containerView.updateConstraintsDidCall)
	}

	func testListDidScrollToIndexPath() {

		presenter.listDidScroll(to: IndexPath(item: 0, section: 0))
		XCTAssertTrue(interactor.venuesDidCall)
		XCTAssertTrue(mapView.centerAnnotationDidCall)
		XCTAssertTrue(listView.selectCellDidCall)
	}

	func testSmallMapTap() {

		presenter.didTapMapArea()
		XCTAssertTrue(containerView.makeMapFullDidCall)
	}
}
