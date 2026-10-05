//
//  MapViewTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 01/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import MapKit
import SimpleNetwork
@testable import PremierInn

private class MockAnnotation: NSObject, MKAnnotation {
	var coordinate: CLLocationCoordinate2D { return CLLocationCoordinate2D(latitude: 12, longitude: 12) }
}

private class MockPresenter: MapPresenterProtocol {

	var mapIsReadDidCall = false
	var listButtonTappedDidCall = false
	var resetButtonTappedDidCall = false
    var seachHereButtonTappedDidCall = false
	var singleTapDidCall = false
	var annotationsDidCall = false
	var nearbyAnnotationsDidCall = false
	var mapDidDeselectAnnotationDidCall = false
    var performLastSuccessfulSearchDidCall = false

	var annotations: [MKAnnotation] {

		annotationsDidCall = true
		return []
	}
	var selectedHotel: Hotel? { return nil }
	var nearbyAnnotations: [MKAnnotation] {

		nearbyAnnotationsDidCall = true
		return []
	}
    var lastSuccessfulSuggestion: Suggestion? { return nil }

	func mapViewIsReady() {

		mapIsReadDidCall = true
	}

	func mapDidPanByUser() {

	}

	func didTapAnnotation(_ annotation: MKAnnotation) {

		singleTapDidCall = true
	}

	func listButtonDidTap() {

		listButtonTappedDidCall = true
	}

	func resetButtonDidTap() {

		resetButtonTappedDidCall = true
	}

	func mapViewDidDeselectAnnotation() {

		mapDidDeselectAnnotationDidCall = true
	}

    func searchHereButtonDidTap() {

        seachHereButtonTappedDidCall = true
    }

    func performLastSuccessfulSearch() {

        performLastSuccessfulSearchDidCall = true
    }
}

class MapViewTests: XCTestCase {
    
	private var presenter: MockPresenter!
	private var controller: Map2ViewController!
	private var mapView = MKMapView(frame: CGRect(x: 0, y: 0, width: 300, height: 300))

	override func setUp() {
		super.setUp()

		presenter = MockPresenter()

		controller = Map2ViewController()
		controller.mapView = mapView
		controller.presenter = presenter
	}

	override func tearDown() {

		presenter = nil
		controller = nil

		super.tearDown()
	}

    func testViewIsReady() {

        controller.loadView()
		controller.viewDidLoad()
		XCTAssertFalse(presenter.mapIsReadDidCall)

		controller.viewDidAppear(false)
		XCTAssertTrue(presenter.mapIsReadDidCall)
	}

	func testListButton() {

		controller.listButtonDidTap(UIButton())

		XCTAssertTrue(presenter.listButtonTappedDidCall)
	}

	func testResetButton() {

		controller.resetButtonDidTap(UIButton())

		XCTAssertTrue(presenter.resetButtonTappedDidCall)
	}

    func testSearchHereButton() {

        controller.searchHereButtonDidTap(UIButton())

        XCTAssertTrue(presenter.seachHereButtonTappedDidCall)
    }

	func testReload() {

        controller.loadView()
		controller.reload()

		XCTAssertTrue(presenter.annotationsDidCall)
	}

	func testFitAnnotations() {

        controller.fitAnnotationsInView(style: .regular, animated: false)
		XCTAssertTrue(presenter.nearbyAnnotationsDidCall)
	}
}
