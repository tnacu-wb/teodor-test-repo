//
//  UserDetailsInteractorTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 12/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn
import SimpleNetwork

final class UserDetailsMockDataProvider: UserDetailsDataProvider {

    enum MarketingPreferenceResult {
        case optedIn
        case optedOut
    }

    let optedInPreference = MarketingPreferences(
        contactChannelId: "mock contact channel id",
        permissions: [
            BrandPermission(
                brandCode: "PINN",
                optIn: true,
                suppressMarketingCheckbox: false
            )
        ]
    )
    let optedOutPreference = MarketingPreferences(
        contactChannelId: "mock contact channel id",
        permissions: [
            BrandPermission(
                brandCode: "PINN",
                optIn: false,
                suppressMarketingCheckbox: false
            )
        ]
    )

    let result: MarketingPreferenceResult

    init(result: MarketingPreferenceResult = .optedIn) {
        self.result = result
    }

    func getMarketingPreferences(
        for emailAddress: String,
        and brands: MarketingBrandCode,
        isBusiness: Bool,
        completion: @escaping (MarketingPreferences?, (any Error)?) -> Void
    ) {
        var marketingPreferences: MarketingPreferences?

        switch result {
        case .optedIn:
            marketingPreferences = optedInPreference
        case .optedOut:
            marketingPreferences = optedOutPreference
        }
        return completion(marketingPreferences, nil)
    }
    
    func updateMarketingPreferences(for brands: [SimpleNetwork.MarketingBrandCode], and emailAddress: String, optin: Bool, isoCountryCode: String, completion: @escaping (Bool, (any Error)?) -> Void) {
        return completion(true, nil)
    }
    
    func anonymousNewsletterPreferences(for email: String, countryOfResidence: String, completion: @escaping (SimpleNetwork.AnonymousNewsletterPreferences?, (any Error)?) -> Void) {
        return completion(nil, nil)
    }
    
    func performHoldBookingWithGuests(bookingDetails: SimpleNetwork.BookingDetails, isCiolFlow: Bool, isRegCard: Bool, completion: @escaping (Bool, (any Error)?) -> Void) {
        return completion(true, nil)
    }
    
	func updateUserDetails(for user: SimpleNetwork.User, sensorData: String, completion: @escaping (Bool, (any Error)?) -> Void) {
        return completion(true, nil)
    }
}

final class UserDetailsInteractorTests: XCTestCase {

	private var view: UserDetailsViewController!
	private var presenter: UserDetailsPresenter!
	private var interactor: UserDetailsInteractor!

	override func setUp() {
        super.setUp()

		let bookingDetails = BookingDetails()
		bookingDetails.criteria.rooms = [Room(), Room()]

		view = UserDetailsViewController()
		view.presenter = {
			let presenter = UserDetailsPresenter()
			presenter.view = view

            interactor = UserDetailsInteractor(user: user,
                                               bookingDetails: bookingDetails,
                                               scope: .bookingFlow)

			presenter.interactor = interactor

//			let router = UserDetailsRouter()
//			router.viewController = controller
//			router.delegate = delegate
//
//			presenter.router = router

			return presenter
		}()


    }
    
    override func tearDown() {

		interactor = nil

		super.tearDown()
    }

	func testBookerGuestAndPurpose() {

        guard let user,
              let address = try? Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"]) else { return }
        user.address = address

        interactor.bookingDetails?.purpose = .leisure
        interactor.bookingDetails?.booker = user
        interactor.bookerIsStayingDidChange(isStaying: true)

        let roomConfigs: [RoomConfig] = [
            RoomConfig(
                user: user,
                roomIndex: 0,
                totalRooms: 2,
                isBookerStaying: true,
                shouldShowRoomInfo: true
            ),
            RoomConfig(
                user: user,
                roomIndex: 1,
                totalRooms: 2,
                isBookerStaying: true,
                shouldShowRoomInfo: true
            )
        ]

        view.loadViewModel(
            conf: UserDetailsConfiguration(
                scope: .bookingFlow,
                user: user,
                roomConfigurations: roomConfigs,
                marketingModel: nil,
                tripPurposeModel: nil
            )
        )

		view.viewModel?.row(named: Step1Row.salutation.rawValue + Constants.bookerSuffix)?.value = "Mr"
		view.viewModel?.row(named: Step1Row.firstName.rawValue + Constants.bookerSuffix)?.value = "Marcello"
		view.viewModel?.row(named: Step1Row.lastName.rawValue + Constants.bookerSuffix)?.value = "Mascia"
		view.viewModel?.row(named: Step1Row.contactNumber.rawValue + Constants.bookerSuffix)?.value = "1234567890"
		view.viewModel?.row(named: Step1Row.emailAddress.rawValue + Constants.bookerSuffix)?.value = "marcello@me.com"
		view.viewModel?.row(named: CountryActionableRow.postCode.rawValue)?.value = "SW19 3SH"
		view.viewModel?.row(named: CountryActionableRow.addressLine1.rawValue)?.value = "Blabla1"
		view.viewModel?.row(named: CountryActionableRow.addressLine2.rawValue)?.value = "Blabla2"
		view.viewModel?.row(named: CountryActionableRow.addressLine3.rawValue)?.value = "Blabla3"
		view.viewModel?.row(named: Step1Row.salutation.rawValue + "1")?.value = "Mrs"
		view.viewModel?.row(named: Step1Row.firstName.rawValue + "1")?.value = "Marcella"
		view.viewModel?.row(named: Step1Row.lastName.rawValue + "1")?.value = "Maria"
		view.viewModel?.row(named: Step1Row.bookerIsGuest.rawValue)?.value = true
        view.viewModel?.row(named: GuestDetailsRow.purpose.rawValue)?.value = TripPurposeSelections.leisure

		do {
			let values = view.getViewModelValues()
			let result = try interactor.getBookerGuestsAndPurpose(from: values)

			XCTAssertEqual(result.booker.title, "Mr")
			XCTAssertEqual(result.booker.firstName, "Marcello")
			XCTAssertEqual(result.booker.lastName, "Mascia")
			XCTAssertEqual(result.booker.contactNumber, "1234567890")
			XCTAssertEqual(result.booker.emailAddress, "marcello@me.com")
			XCTAssertEqual(result.booker.address?.postcode, "SW19 3SH")
			XCTAssertEqual(result.booker.address?.line1, "Blabla1")
			XCTAssertEqual(result.booker.address?.line2, "Blabla2")
			XCTAssertEqual(result.booker.address?.line3, "Blabla3")

			XCTAssertEqual(result.guests.count, 2)
			XCTAssertEqual(result.guests.last?.title, "Mrs")
			XCTAssertEqual(result.guests.last?.firstName, "Marcella")
			XCTAssertEqual(result.guests.last?.lastName, "Maria")

			XCTAssertEqual(result.purpose, .leisure)

		} catch let error as RowValidatorError {
			XCTFail("Missing value: " + error.row.tag)
		} catch {
			XCTFail(error.localizedDescription)
		}
	}

    private var hotel: Hotel? {

        do {
            let hotel = try Hotel(dictionary: [
                "hotelInfo": [
                    "name": "Fake Hotel",
                    "code": "CODE"]
            ])

            return hotel
        } catch {
            print(error)
        }

        return nil
    }

    var roomLettings: [Room]? {

        let roomDictionary: PIDictionary = [
            "roomId": "lol",
            "options": [
                [
                    "lettingType": "DBS",
                    "totalCost": [
                        "amount": 99.99,
                        "currency": "USD"
                    ],
                    "cityTax": [
                        "amount": 2.00,
                        "currency": "USD"
                    ]
                ]
            ]
        ]

        let room = Room(dictionary: roomDictionary)

        return [room]
    }

    func testUpdateMarketingPreferenceOptInDE() {

        interactor.userDetailsDataProvider = UserDetailsMockDataProvider()

        interactor.marketingOptInPreference = false
        let usersMarketingPreference = true
        let expectation = expectation(description: "marketing preference should update to true")

        interactor.updateMarketingPreference(optin: usersMarketingPreference, emailAddress: "email@foo.com", doubleOptIn: true, isoCountryCode: "DE") {
            expectation.fulfill()
        }
        waitForExpectations(timeout: 1.0)
        XCTAssertEqual(interactor.marketingOptInPreference, true)
    }

    func testUpdateMarketingPreferenceOptOutGB() {

        interactor.userDetailsDataProvider = UserDetailsMockDataProvider()

        interactor.marketingOptInPreference = true
        let usersMarketingPreference = false
        let expectation = expectation(description: "marketing preference should update to false")

        interactor.updateMarketingPreference(optin: usersMarketingPreference, emailAddress: "email@foo.com", doubleOptIn: false, isoCountryCode: "GB") {
            expectation.fulfill()
        }
        waitForExpectations(timeout: 1.0)
        XCTAssertEqual(interactor.marketingOptInPreference, false)
    }

    func testGetMarketingPreferencesWhenOptedIn() {

        // GIVEN
        interactor.userDetailsDataProvider = UserDetailsMockDataProvider(result: .optedIn)
        let expectation = expectation(description: "should get marketing preferences")

        // WHEN
        interactor.getMarketingPreferences(
            for: "email@foo.com",
            and: .premierInn,
            isBusiness: false
        ) { _ in
            expectation.fulfill()
        }
        waitForExpectations(timeout: .ocd)

        // THEN
        XCTAssertEqual(interactor.marketingOptInPreference, true)
    }

    func testGetMarketingPreferencesWhenOptedOut() {

        // GIVEN
        interactor.userDetailsDataProvider = UserDetailsMockDataProvider(result: .optedOut)
        let expectation = expectation(description: "should get marketing preferences")

        // WHEN
        interactor.getMarketingPreferences(
            for: "email@foo.com",
            and: .premierInn,
            isBusiness: false
        ) { _ in
            expectation.fulfill()
        }
        waitForExpectations(timeout: .ocd)

        // THEN
        XCTAssertEqual(interactor.marketingOptInPreference, false)
    }

    // MARK: - Helpers

    private var user: User? {
        try? User(dictionary: [
            "contactDetail" : ["title" : "Mr",
                               "firstName": "Pippo",
                               "lastName": "Paperino"],
            "additionalGuests": [
                ["title": "Mr",
                 "firstName": "Marcello",
                 "lastName": "Mascia",
                 "nationality": "Sardinian",
                 "email": "godTierMccree@owl.com"]
            ]
        ],
                  sessionId: "hello")
    }
}
