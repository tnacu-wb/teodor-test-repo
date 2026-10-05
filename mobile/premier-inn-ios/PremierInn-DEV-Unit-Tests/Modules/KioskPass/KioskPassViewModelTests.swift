//
//  KioskPassViewModelTests.swift
//  PremierInnTests
//
//  Created by Louis Faria-Softly on 27/08/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockDataProvider: KioskDataProvider {
    var qrCodeShown: Bool = false
    var walletPassCalled: Bool = false

    func loadWalletPass(with reservationDetails: SimpleNetwork.ReservationDetails, isQRCodeEnabled: Bool, completion: @escaping (Data?, (any Error)?) -> Void) {
        qrCodeShown = isQRCodeEnabled
        walletPassCalled = true
    }
}

private class MockKioskViewModel: KioskViewModel {

    var appleWalletExistsInPass: Bool = false

    override func getExistingPass() {
        if appleWalletExistsInPass {
            self.appleWalletState = .passSaved
        } else {
            self.appleWalletState = .passCanBeAdded
        }
    }
}


final class KioskPassViewModelTests: XCTestCase {

    fileprivate var dataProvider = MockDataProvider()

    var summary: Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-09"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"

        return try! Stay(dictionary: dictionary)
    }

    func testKioskPassViewModel() {

        let viewModel = KioskViewModel(stay: summary)
        viewModel.kioskDataProvider = dataProvider
        XCTAssertEqual(viewModel.stay.identifier, "BBER264250")
        XCTAssertEqual(viewModel.summary, "Speed up check-in")
        XCTAssertEqual(viewModel.text, "Scan the QR code below at our kiosk upon arrival. Hold it up to the scanner, collect your room key and enjoy your stay!")
        XCTAssertEqual(viewModel.title, "My QR Code")
        XCTAssertNotNil(viewModel.qrImage)
    }

    func testQRCodeForWalletShown() {
        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"LONBLA"]], appleWalletEnabled: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        let viewModel = KioskViewModel(stay: summary)
        viewModel.kioskDataProvider = dataProvider

        viewModel.fetchWalletPass()
        XCTAssertEqual(dataProvider.qrCodeShown, true)
        XCTAssertEqual(dataProvider.walletPassCalled, true)

    }

    func testQRCodeForWalletHidden() {

        let remoteConfig = MockRemoteConfig(kioskHotels: nil, appleWalletEnabled: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let viewModel = KioskViewModel(stay: summary)
        viewModel.kioskDataProvider = dataProvider
        viewModel.fetchWalletPass()

        XCTAssertEqual(dataProvider.qrCodeShown, false)
        XCTAssertEqual(dataProvider.walletPassCalled, true)
    }

    func testKioskPassWalletButtonShown() {
        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"LONBLA"]], appleWalletEnabled: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        let viewModel = KioskViewModel(stay: summary)

        XCTAssertEqual(viewModel.appleWalletState, AppleWalletState.passCanBeAdded)
    }

    func testKioskPassWalletButtonHiden() {
        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"LONBLA"]], appleWalletEnabled: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        let viewModel = KioskViewModel(stay: summary)

        XCTAssertEqual(viewModel.appleWalletState, AppleWalletState.passHidden)
    }

    func testPKPassSerialNumber() {
        let viewModel = KioskViewModel(stay: summary)
        XCTAssert(viewModel.stay.pkPassSerialNumber.contains(viewModel.stay.identifier))
    }

    func testPKPassExists() {
        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"LONBLA"]], appleWalletEnabled: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        let viewModel = MockKioskViewModel(stay: summary)
        viewModel.appleWalletExistsInPass = true
        viewModel.getExistingPass()
        XCTAssertEqual(viewModel.appleWalletState, AppleWalletState.passSaved)
    }

    func testPKPassDoesNotExist() {
        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"LONBLA"]], appleWalletEnabled: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        let viewModel = MockKioskViewModel(stay: summary)
        viewModel.appleWalletExistsInPass = false
        viewModel.getExistingPass()

        XCTAssertEqual(viewModel.appleWalletState, AppleWalletState.passCanBeAdded)
    }
}
