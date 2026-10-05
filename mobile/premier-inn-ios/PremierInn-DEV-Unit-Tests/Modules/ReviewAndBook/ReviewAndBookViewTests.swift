//
//  ReviewAndBookViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 10/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: ReviewAndBookPresenterProtocol {
    
    var viewIsReadyDidCall = false
    var confirmButtonDidTapDidCall = false

    func viewIsReady() {
        
        viewIsReadyDidCall = true
    }
    
    func editUpsellsButtonDidTap() {
        
    }
    
    func editGuestButtonDidTap() {
        
    }
    
    func summaryButtonDidTap() {
        
    }
    
    func editAdditionalInformationButtonDidTap() {
        
    }
    
    func confirmButtonDidTap() {
        
        confirmButtonDidTapDidCall = true
    }
    
    func continueWithNewRateSelected(rate: Rate) {
        
    }
    
    func noMoreAvailabilityButtonDidTap() {
        
    }
    
    func cancelRateUpdateButtonDidTap() {
        
    }
    
    func paymentFailureButtonDidTap() {
        
    }

    func createAccountChanged(to selected: Bool, withCard: Bool) {

    }
}

class ReviewAndBookViewTests: XCTestCase {
    
    private var view: ReviewAndBookViewController!
    private var presenter: MockPresenter!

    private var donationsWithPackages: [String: Any] = [
        "description": "",
        "imageSrc": "/content/dam/pi/websites/desktop/why/great-ormond-street/gosh-donations.jpg",
        "name": "Can you help beat childhood cancer?",
        "donationPackages": [
            [
                "code": "ZCHRY1",
                "currency": "GBP",
                "unitPrice": 3.0
            ],
            [
                "code": "ZCHRY2",
                "currency": "GBP",
                "unitPrice": 0.3
            ],
            [
                "code": "ZCHRY7",
                "currency": "GBP",
                "unitPrice": 1.0
            ]
        ]
    ]

    private var donationsWithoutPackages: [String: Any] = [
        "description": "",
        "imageSrc": "/content/dam/pi/websites/desktop/why/great-ormond-street/gosh-donations.jpg",
        "name": "Can you help beat childhood cancer?"
    ]

    override func setUp() {
        
        presenter = MockPresenter()
        
        view = ReviewAndBookViewController()
        view.presenter = presenter
    }
    
    override func tearDown() {
        
        presenter = nil
        view = nil
        
        super.tearDown()
    }
    
    func testViewDidLoad() {
        
        view.viewDidLoad()
        XCTAssertTrue(presenter.viewIsReadyDidCall)
    }
    
    func testConfirmButton() {
        
        view.confirmButtonDidTap()
        XCTAssertTrue(presenter.confirmButtonDidTapDidCall)
    }

    func testDonationsSectionFeatureFlagDoesShow() {

        let jsonData = try! JSONSerialization.data(withJSONObject: donationsWithPackages, options: .prettyPrinted)
        let goshPackage = try! JSONDecoder().decode(GoshPackage.self, from: jsonData)
      
        UserSessionManager.sharedInstance.currentUser?.company = nil
        let remoteConfig = MockRemoteConfig(featureDonations: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let bookingDetails = BookingDetails.sharedInstance
        bookingDetails.goshOptions = goshPackage

        let goshSection = view.goshSection(bookingDetails: bookingDetails)
        XCTAssertNotNil(goshSection)
    }

    func testDonationsSectionFeatureFlagDoesNotShow() {

        let jsonData = try! JSONSerialization.data(withJSONObject: donationsWithPackages, options: .prettyPrinted)
        let goshPackage = try! JSONDecoder().decode(GoshPackage.self, from: jsonData)
        UserSessionManager.sharedInstance.currentUser?.company = nil

        let remoteConfig = MockRemoteConfig(featureDonations: false)

        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        UserSessionManager.sharedInstance.currentUser?.company = nil

        let bookingDetails = BookingDetails.sharedInstance
        bookingDetails.goshOptions = goshPackage

        let goshSection = view.goshSection(bookingDetails: bookingDetails)
        XCTAssertNil(goshSection)
    }

}
