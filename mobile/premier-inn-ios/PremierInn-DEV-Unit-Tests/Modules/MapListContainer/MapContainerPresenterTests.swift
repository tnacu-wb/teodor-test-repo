//
//  MapContainerPresenterTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 02/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import MapKit
@testable import PremierInn
import SimpleNetwork

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
    var showLoadingBoxDidCall = false
    var hideLoadingBoxDidCall = false
    var refreshCriteriaElementsDidCall = false

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

        refreshCriteriaElementsDidCall = true
    }

    func errorDidOccur() {

    }

    func showLoadingBox() {

        showLoadingBoxDidCall = true
    }

    func hideLoadingBox() {

        hideLoadingBoxDidCall = true
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

	func showErrorMessage(message: String?, bottomConstraint: CGFloat) {

		showErrorMessageDidCall = true
	}

	func removeErrorMessage() {

		removeErrorMessageDidCall = true
	}

    func selectedListCell() -> VenueCell? {

        return nil
    }

    func showError() {
    }
}

private class MockCardsView: ListViewProtocol {

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

	func showErrorMessage(message: String?, bottomConstraint: CGFloat) {

		showErrorMessageDidCall = true
	}

	func removeErrorMessage() {

		removeErrorMessageDidCall = true
	}

    func selectedListCell() -> VenueCell? {

        return nil
    }
    func showError(){}
}

private class MockInteractor: VenueInteractorProtocol {

    var criteria: Criteria {
        return BookingDetails.sharedInstance.criteria
    }

    static let mockHotel: Hotel! = try! Hotel(dictionary: [
        "name": "Hotel Name",
        "code": "LONLEI",
        "prepaymentAllowed": true,
        "address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
        "images": [["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]]
        ])

    var fetchHotelsDidCall = false
    var venuesDidCall = false
    var suggestionAndAllVenuesDidCall = false
    var suggestionAndNearbyVenuesDidCall = false
    var screenTitleDidCall = false
    var screenSubtitleDidCall = false
    var suggestionDidCall = false
	var trackAvailabilityDidCall = false
	var logToFirebaseDidCall = false
	var errorDidCall = false
    var shouldShowCoronavirusInformationBanner: Bool = false

    var venues: [Venue] {

        venuesDidCall = true

        return [MockInteractor.mockHotel]
    }
    var suggestion: Suggestion? {
        suggestionDidCall = true
        return PISuggestion(coordinate: CLLocationCoordinate2D(latitude: 12, longitude: 12))
    }
    var lastSuccessfulSuggestion: Suggestion? {
        return PISuggestion(coordinate: CLLocationCoordinate2D(latitude: 50, longitude: 0))
    }
    var screenTitle: String? {
        screenTitleDidCall = true
        return nil
    }
    var screenSubtitle: String? {
        screenSubtitleDidCall = true
        return nil
    }
    var suggestionAndAllVenues: [Venue] {
        suggestionAndAllVenuesDidCall = true
        return venues
    }
    var suggestionAndVenuesInReasonableDistance: [Venue] {
        suggestionAndNearbyVenuesDidCall = true
        return venues
    }
	var error: LocalizedError? {
		errorDidCall = true
		return nil
	}

    var fetchHotelsWithHotelDetailViewControllerDidCall = false
    var updateSuggestionDidCall = false

    func fetchHotels(hotelDetailsRouterDelegate: HotelDetailsRouterDelegate?, completion: @escaping (Bool, UIViewController?) -> Void) {

        fetchHotelsWithHotelDetailViewControllerDidCall = true

        completion(true, nil)
    }

    func updateSuggestion(to suggestion: Suggestion) {

        updateSuggestionDidCall = true
    }

    func fetchHotels(with sorting: AvailabilitiesSorting, completion: @escaping (Bool) -> Void) {

        fetchHotelsDidCall = true

        completion(true)
    }

    func fetchHotelsNearby(with sorting: AvailabilitiesSorting, completion: @escaping (Bool) -> Void) {

        completion(true)
    }

	func trackAvailability(style: VenuesListStyle) {

		trackAvailabilityDidCall = true
	}

	func logToFirebase() {

		logToFirebaseDidCall = true
	}

    func indexOfHotel(with code: String) -> Int? { return nil }

    func didTapDismissCoronavirusInformationBanner() {}

    func shouldShowFallBack() -> Bool {
        return false
    }
}

private class MockRouter: MapListContainerRouterProtocol {

    var showMapModuleDidCall = false
    var showListModuleDidCall = false
    var showCardsModuleDidCall = false
    var navigateToHotelDidCall = false
    var selectedLocationDidCall = false
    var selectedNightsDidCall = false
    var selectedGuestsDidCall = false
    var presentCriteriaDidCall = false

    func selectedLocation() {

        selectedLocationDidCall = true
    }

    func selectedNights() {

        selectedNightsDidCall = true
    }

    func selectedGuests() {

        selectedGuestsDidCall = true
    }

    func showHotelDetailsPage(_ hotelController: HotelDetailsViewController) {

    }

    func showMapModule() {

        showMapModuleDidCall = true
    }

    func showListModule() {

        showListModuleDidCall = true
    }

	func showCardsModule() {

		showCardsModuleDidCall = true
	}

    func navigateTo(hotel: Hotel, at indexPath: IndexPath, suggestion: Suggestion?, isMapVisible: Bool) {

        navigateToHotelDidCall = true
    }

    func presentCriteriaViewController(with suggestion: Suggestion) {

        presentCriteriaDidCall = true
    }

    func updateHotelDetailsIfVisible() {
        
    }

    func hideHotelDetailsIfVisible() {
        
    }
}

class MapContainerPresenterTests: XCTestCase {

    private var presenter: MapListContainerPresenter!
    private var containerView: MockContainerView!
    private var listView: MockListView!
	private var cardsView: MockCardsView!
    private var mapView: MockMapView!
    private var interactor: MockInteractor!
    private var router: MockRouter!

    override func setUp() {
        super.setUp()

        containerView = MockContainerView()
        listView = MockListView()
		cardsView = MockCardsView()
        mapView = MockMapView()
        interactor = MockInteractor()
        router = MockRouter()

        presenter = MapListContainerPresenter()
        presenter.containerView = containerView
        presenter.listView = listView
		presenter.cardsView = cardsView
        presenter.mapView = mapView
        presenter.interactor = interactor
        presenter.router = router

        BookingDetails.sharedInstance.criteria = Criteria()
    }

    override func tearDown() {

        listView = nil
		cardsView = nil
        interactor = nil
        presenter = nil

        super.tearDown()
    }

    func testViewIsReady() {

        presenter.viewIsReady()

        XCTAssertTrue(router.showMapModuleDidCall)
        XCTAssertTrue(router.showListModuleDidCall)
		XCTAssertTrue(router.showCardsModuleDidCall)
		XCTAssertTrue(interactor.trackAvailabilityDidCall)
		XCTAssertTrue(interactor.logToFirebaseDidCall)
    }

    func testMapButton() {

        presenter.mapButtonDidTap()

        XCTAssertTrue(containerView.makeMapFullDidCall)
    }

    func testBackButton_CardStyle() {

        presenter.currentListStyle = .card
        presenter.backButtonDidTap()

        XCTAssertTrue(containerView.makeMapSmallDidCall)
        XCTAssertFalse(containerView.popViewControllerDidCall)
    }

    func testBackButton_RegularStyle() {

        presenter.currentListStyle = .regular
        presenter.backButtonDidTap()

        XCTAssertFalse(containerView.makeMapSmallDidCall)
        XCTAssertTrue(containerView.popViewControllerDidCall)
    }

    func testSorting() {

        presenter.sortDidChange(with: .distance)

        XCTAssertTrue(interactor.fetchHotelsDidCall)
        XCTAssertTrue(mapView.reloadDidCall)
        XCTAssertTrue(mapView.fitAnnotationsDidCall)
        XCTAssertTrue(listView.reloadDidCall)
		// Cards should always be sorted by distance
		XCTAssertFalse(cardsView.reloadDidCall)
		XCTAssertTrue(interactor.trackAvailabilityDidCall)
		XCTAssertTrue(interactor.logToFirebaseDidCall)
    }

    func testMapWillGoFullScreen() {

        presenter.mapWillGoFullScreen()

        XCTAssertTrue(presenter.currentListStyle == .card)
        XCTAssertTrue(listView.mapWillGoFullScreenDidCall)
		XCTAssertTrue(cardsView.mapWillGoFullScreenDidCall)
        XCTAssertTrue(mapView.mapWillGoFullDidCall)
    }

    func testMapDidGoFullScreen() {

        presenter.mapDidGoFullScreen()

        XCTAssertTrue(listView.mapDidGoFullScreenDidCall)
		XCTAssertTrue(cardsView.mapDidGoFullScreenDidCall)

        XCTAssertTrue(mapView.fitAnnotationsDidCall)
		XCTAssertTrue(interactor.trackAvailabilityDidCall)
		XCTAssertTrue(interactor.logToFirebaseDidCall)

        if UIDevice.current.userInterfaceIdiom != .pad {
            XCTAssertTrue(mapView.showListButtonDidCall)
        }
    }

    func testMapWillGoSmall() {

        presenter.mapWillGoSmall()

        XCTAssertTrue(presenter.currentListStyle == .regular)
        XCTAssertTrue(listView.willReturnToListViewDidCall)
		XCTAssertTrue(cardsView.willReturnToListViewDidCall)
        XCTAssertTrue(mapView.mapWillGoSmallDidCall)
    }

    func testMapDidGoSmall() {

        presenter.mapDidGoSmall()

        XCTAssertTrue(mapView.hideListButtonDidCall)
        XCTAssertTrue(mapView.hideResetButtonDidCall)
        XCTAssertTrue(mapView.hideSearchHereButtonDidCall)
        XCTAssertTrue(mapView.fitAnnotationsDidCall)
		XCTAssertTrue(interactor.trackAvailabilityDidCall)
		XCTAssertTrue(interactor.logToFirebaseDidCall)
    }

    func testUpdatedSuggestion() {

        presenter.updatedSuggestion(to: PISuggestion(title: ""))

        XCTAssertTrue(interactor.updateSuggestionDidCall)
        XCTAssertTrue(containerView.showLoadingBoxDidCall)
        XCTAssertTrue(interactor.fetchHotelsWithHotelDetailViewControllerDidCall)
        XCTAssertTrue(containerView.hideLoadingBoxDidCall)
        XCTAssertTrue(interactor.trackAvailabilityDidCall)
        XCTAssertTrue(interactor.logToFirebaseDidCall)
        XCTAssertTrue(containerView.refreshCriteriaElementsDidCall)
        XCTAssertTrue(listView.reloadDidCall)
        XCTAssertTrue(mapView.reloadDidCall)
        XCTAssertTrue(mapView.fitAnnotationsDidCall)
    }

    func testCriteriaUpdated() {

        presenter.criteriaUpdated(to: Criteria())

        XCTAssertTrue(containerView.showLoadingBoxDidCall)
        XCTAssertTrue(interactor.fetchHotelsWithHotelDetailViewControllerDidCall)
        XCTAssertTrue(containerView.hideLoadingBoxDidCall)
        XCTAssertTrue(interactor.trackAvailabilityDidCall)
        XCTAssertTrue(interactor.logToFirebaseDidCall)
        XCTAssertTrue(containerView.refreshCriteriaElementsDidCall)
        XCTAssertTrue(listView.reloadDidCall)
        XCTAssertTrue(mapView.reloadDidCall)
        XCTAssertTrue(mapView.fitAnnotationsDidCall)
    }

    func testSelectedLocation() {

        presenter.selectedLocation()

        XCTAssertTrue(router.selectedLocationDidCall)
    }

    func testSelectedNights() {

        presenter.selectedNights()

        XCTAssertTrue(router.selectedNightsDidCall)
    }

    func testSelectedGuests() {

        presenter.selectedGuests()

        XCTAssertTrue(router.selectedGuestsDidCall)
    }

    func testDateTextForCurrentCriteriaSummary() {

        BookingDetails.sharedInstance.criteria.arrivalDate = Date().dateByAddingUnit(unitType: .day, number: 2)!
        let arrivalDateString = BookingDetails.sharedInstance.criteria.arrivalDate.localizedShortDayMonthStringFormat
        let checkOutDateString = BookingDetails.sharedInstance.criteria.checkOutDate?.localizedShortDayMonthStringFormat ?? ""
        let datesText = presenter.dateTextForCurrentCriteriaSummary

        XCTAssertEqual(datesText, "\(arrivalDateString) - \(checkOutDateString)")
    }

    func testCondensedCriteriaTitle() {

        let checkOutDateString : String = {

            guard let checkOutDate = BookingDetails.sharedInstance.criteria.checkOutDate else { return "" }

            return checkOutDate.localizedShortDayMonthStringFormat
        }()
        
        let checkInDateString: String = {

            let checkInDate = BookingDetails.sharedInstance.criteria.arrivalDate

            return checkInDate.localizedShortDayMonthStringFormat
        }()
        let title = presenter.condensedCriteriaTitle

        XCTAssertEqual(title.string, "\(checkInDateString) - \(checkOutDateString)  1 guest, 1 room")
    }
}
