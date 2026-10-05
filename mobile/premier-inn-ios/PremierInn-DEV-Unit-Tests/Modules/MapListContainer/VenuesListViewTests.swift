//
//  VenuesListViewTests.swift
//  PremierInnTests
//
//  Created by Nick Jones on 12/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

fileprivate enum HotelState {
    case Available
    case Unavailable
    case Nil
}

private class MockPresenter: ListPresenterProtocol {

	var hotelState: HotelState = .Available
	var viewIsReadyDidCall = false
	var numberOfVenuesDidCall = false
	var hotelForIndexPathDidCall = false
	var didSelectItemDidCall = false
	var didScrollDidCall = false
	var didScrollToPositionDidCall = false
	var didTapMapAreaDidCall = false
    var venueCellDidTapEditButtonDidCall = false

    var currentStyle = VenuesListStyle.regular
	var listStyle: VenuesListStyle { return currentStyle }
    var shouldShowCoronavirusInformationBanner: Bool = false

	func listViewIsReady() {

		viewIsReadyDidCall = true
	}

    func didStartScrolling() {

    }

	func numberOfVenues() -> Int {

		numberOfVenuesDidCall = true

		return 1
	}

	func hotel(for index: IndexPath) -> Hotel? {

		hotelForIndexPathDidCall = true

		var hotel: Hotel! = try! Hotel(dictionary: [
			"name": "Hotel Name",
			"code": "LONLEI",
			"prepaymentAllowed": true,
			"address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
			"images": [["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]]
			])

		switch hotelState {
		case .Available:
			hotel.update(available: true)
		case .Unavailable:
			hotel.update(available: false)
		case .Nil:
			hotel = nil
		}

		return hotel
	}

	func didSelectItem(at indexPath: IndexPath, mapVisible: Bool) {

		didSelectItemDidCall = true
	}

	func listDidScroll(to indexPath: IndexPath) {

		didScrollDidCall = true
	}

	func listDidScroll(to yPosition: CGFloat) {

		didScrollToPositionDidCall = true
	}

	func didTapMapArea() {

		didTapMapAreaDidCall = true
	}

    func venueCellDidTapEditButton() {

        venueCellDidTapEditButtonDidCall = true
    }

    func didTapDismissCoronavirusInformationBanner() {}
}

class VenuesListTests: XCTestCase {

    fileprivate var presenter: MockPresenter!
    var controller: VenuesListViewController!

    override func setUp() {
		super.setUp()

		presenter = MockPresenter()

        controller = VenuesListViewController()
        controller.presenter = presenter
    }

    override func tearDown() {

		presenter = nil
        controller = nil

        super.tearDown()
    }

    func testVenueViewIsReady() {

        controller.viewDidLoad()

		XCTAssertTrue(presenter.viewIsReadyDidCall)
    }

	func testCollectionViewMethods() {

		let collectionView = UICollectionView(frame: .zero, collectionViewLayout: UICollectionViewLayout())

		_ = controller.collectionView(collectionView, numberOfItemsInSection: 0)
		controller.collectionView(collectionView, didSelectItemAt: IndexPath(item: 0, section: 0))
		controller.collectionView(collectionView, willDisplay: UICollectionViewCell(), forItemAt: IndexPath(item: 0, section: 0))

		XCTAssertTrue(presenter.numberOfVenuesDidCall)
        if UIDevice.current.userInterfaceIdiom != .pad {
            XCTAssertTrue(presenter.didSelectItemDidCall)
        }
	}

	func testMapAreaTap() {

		controller.mapAreaButtonDidTap()

		XCTAssertTrue(presenter.didTapMapAreaDidCall)
	}

	func testScroll() {

		let collectionView = UICollectionView(frame: .zero, collectionViewLayout: UICollectionViewLayout())

		controller.scrollViewDidScroll(collectionView)

		XCTAssertTrue(presenter.didScrollToPositionDidCall)
	}
}
