//
//  KeyMessagingPresenterTests.swift
//  PremierInnTests
//
//  Created by Louis Faria-Softly on 26/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn
class MockKeyMessagingView: KeyMessagingViewProtocol {
    
    var presenter: (any KeyMessagingPresenterProtocol)?

    var title: String?
    var description: String?
    var shouldShowDirections: Bool?
    var directionsViewModel: DirectionsViewModel?
    func updateView(with viewModel: KeyMessagingViewModel) {
        self.title = viewModel.title
        self.description = viewModel.messaging
        self.shouldShowDirections = viewModel.shouldShowDirectionsButton
    }
    
    func showMapDirections(directionsViewModel: DirectionsViewModel) {
        self.directionsViewModel = directionsViewModel
    }
    

}

final class KeyMessagingPresenterTests: XCTestCase {

    var mockKeyView: MockKeyMessagingView!
    var interactor: KeyMessagingInteractor!

    var presenter: KeyMessagingPresenter!

    func stay(hasECI: Bool) -> Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-11"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"

        let stay = try! Stay(dictionary: dictionary)

        if hasECI {
            let packages: [PIDictionary] = [
                ["packageCode": "HSCKIN",
                 "description": "Early Check-In",
                 "unitPrice": 10.00,
                 "totalQuantity": 1,
                 "computedPrice": 10.00]
            ]

            let data = try! JSONSerialization.data(withJSONObject: packages, options: .prettyPrinted)
            let decodedPackages: ReservationPackageList? = try? JSONDecoder().decode(ReservationPackageList.self, from: data)
            stay.reservationPackageList = decodedPackages
        }
        return stay
    }

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    override func setUp() {

        presenter = KeyMessagingPresenter()
        mockKeyView = MockKeyMessagingView()
        interactor = KeyMessagingInteractor(messagingFlow: .userIsTooEarly, stay: stay(hasECI: true))
        presenter.view = mockKeyView
        mockKeyView.presenter = presenter
        presenter.interactor = interactor
        interactor.presenter = presenter
    }

    func testUserIsTooEarlyWithECI() throws {
        presenter.interactor = KeyMessagingInteractor(messagingFlow: .userIsTooEarly, stay: stay(hasECI: true))
        presenter.viewDidLoad()
        XCTAssert(mockKeyView.title == "Hi, Early Bird!")
        XCTAssert(mockKeyView.description == "We're preparing your room so come back at 11am")
        XCTAssert(mockKeyView.shouldShowDirections == false)
    }

    func testUserIsTooEarlyWithoutECI() throws {
        presenter.interactor = KeyMessagingInteractor(messagingFlow: .userIsTooEarly, stay: stay(hasECI: false))
        presenter.viewDidLoad()
        XCTAssert(mockKeyView.title == "Hi, Early Bird!")
        XCTAssert(mockKeyView.description == "We're preparing your room so come back at 3pm")
        XCTAssert(mockKeyView.shouldShowDirections == false)
    }

    func testUserIsTooFarAway() throws {
        presenter.interactor = KeyMessagingInteractor(messagingFlow: .userIsNotInRange, stay: stay(hasECI: true))
        presenter.viewDidLoad()
        XCTAssert(mockKeyView.title == "Are you near the hotel?")
        XCTAssert(mockKeyView.description == "Come a little closer so we can find you!")
        XCTAssert(mockKeyView.shouldShowDirections == true)
    }

    func testDirectionsButtonClicked() throws {
        presenter.interactor = KeyMessagingInteractor(messagingFlow: .userIsNotInRange, stay: stay(hasECI: false))
        presenter.viewDidLoad()
        presenter.mapButtonDidClick()
        XCTAssert(mockKeyView.directionsViewModel?.coordinates.latitude == 50.823071)
        XCTAssert(mockKeyView.directionsViewModel?.coordinates.longitude == -0.140976)
    }

}
