//
//  CoreSettingsBootstrap.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SimpleNetwork

struct CoreSettingsBootstrap: BootstrapTask {
    private let bookingDetails: BookingDetails
    private let settingsManager: SettingsManager
    private let requestsManager: RequestsManager.Type
    private let googleAPIDataProvider: GoogleAPIDataProvider

    init(
        bookingDetails: BookingDetails = .sharedInstance,
        settingsManager: SettingsManager = .sharedInstance,
        requestsManager: RequestsManager.Type = RequestsManager.self,
        googleAPIDataProvider: GoogleAPIDataProvider = .sharedInstance
    ) {
        self.bookingDetails = bookingDetails
        self.settingsManager = settingsManager
        self.requestsManager = requestsManager
        self.googleAPIDataProvider = googleAPIDataProvider
    }

    func run() {
        bookingDetails.employeeRatesEnabled = settingsManager.enableEmployeeRates
        requestsManager.setupImageCache()
        googleAPIDataProvider.setup()
    }
}
