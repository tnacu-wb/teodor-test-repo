//
//  PassView.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 23/08/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI
import PassKit

struct PassView: UIViewControllerRepresentable {
    let pkPass: PKPass?
    let bookingReference: String

    func makeUIViewController(context: Context) -> UIViewController {
        guard let pass = pkPass else { return UIViewController() }
        guard let addPassVC = PKAddPassesViewController(pass: pass) else { return UIViewController() }

        AnalyticsManager.shared.trackState(
            PIAnalytics.StateNames.appleWalletQRCode,
            data: [PIAnalytics.Keys.appleWallet: true, PIAnalytics.Keys.bookingID: bookingReference]
        )

        return addPassVC
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // No updates needed
    }
}
