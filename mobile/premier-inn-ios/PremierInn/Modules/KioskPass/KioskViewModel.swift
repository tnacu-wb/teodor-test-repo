//
//  KioskViewModel.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 26/01/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import SimpleNetwork
import PassKit


protocol KioskDataProvider {
    func loadWalletPass(
        with reservationDetails: ReservationDetails,
        isQRCodeEnabled: Bool,
        completion: @escaping (Data?, Error?) -> Void
    )
}

extension RequestsManager: KioskDataProvider {}

class KioskViewModel: ObservableObject {
    let navigationTheme: UINavigationController.NavigationBarColours
    @Published var qrImage: UIImage
    @Published var showPassView = false
    @Published var pass: PKPass?
    @Published var appleWalletState: AppleWalletState = .passHidden
    @Published var appleWalletError: Bool = false

    var stay: Stay
    var kioskDataProvider: KioskDataProvider = RequestsManager()
    private var passLibrary = PKPassLibrary()

    var title: String {
        PILocalizedString("kioskPassPageTitle")
    }

    var summary: String {
        PILocalizedString("kioskPassPageSummary")
    }

    var text: String {
        PILocalizedString("kioskPassPageText")
    }

    var appleWalletExistsButtonTitle: String {
        PILocalizedString("kioskPassAppleWalletButton")
    }

    init(stay: Stay, navigationTheme: UINavigationController.NavigationBarColours = .premierInn) {
        qrImage = generateQRCode(from: stay.identifier)
        self.stay = stay
        self.appleWalletState = SettingsManager.sharedInstance.featureAppleWalletPass ? .passCanBeAdded : .passHidden
        self.navigationTheme = navigationTheme
    }

    func getExistingPass() {
        guard let pass = self.passLibrary.pass(
            withPassTypeIdentifier: Constants.pkPassTypeIdentifier,
            serialNumber: stay.pkPassSerialNumber
        ) else { return }
        self.pass = pass
        appleWalletState = .passSaved
    }

    func fetchWalletPass() {
        AnalyticsManager.shared.trackAction(PIAnalytics.Action.addToWalletTapQRCode, userInfo: nil)

        guard let arrivalDate = stay.arrivalDate else { return }

        let reservationDetails = ReservationDetails(
            reservationId: stay.identifier,
            surname: stay.lastName,
            arrivalDate: arrivalDate,
            business: stay.isBusinessTrip,
            token: nil
        )

        self.kioskDataProvider
            .loadWalletPass(with: reservationDetails, isQRCodeEnabled: self.stay.qrCodeIsEnabled) { data, error in
                guard error == nil, let data = data else {
                    DispatchQueue.main.async {
                        self.appleWalletError = true
                    }
                    return
                }
                DispatchQueue.main.async {
                    if let pass: PKPass = try? PKPass.init(data: data) {
                        self.pass = pass
                        self.showPassView = true
                    }
                }
        }
    }
}
