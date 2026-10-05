//
//  DestinationCardViewModel.swift
//  PremierInn
//
//  Created by Santa Gurung on 26/11/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import CoreLocation
import SimpleNetwork

struct DestinationCardViewModel: Identifiable {
    let id = UUID()
    let imageName: String
    let title: String
    let description: String?
    let tag: String?
    let url: URL?
    let openLinkInApp: Bool?
    let analyticsKey: String?
    let order: Int
    let latitude: Double?
    let longitude: Double?

    var imageUrl: URL? {
        Constants.imageBaseUrl?.appendingPathComponent(imageName)
    }

    func trackDestinationTapped() {
        guard let analyticsKey else { return }
        AnalyticsManager.shared.trackAction(analyticsKey, userInfo: [
            PIAnalytics.Keys.dashboardCardClick: true,
            PIAnalytics.Keys.dashboardCardAction: analyticsKey
        ])
    }

    // MARK: - Navigation Logic

    private enum CoordinateValidation {
        static let maxLatitude: Double = 90
        static let maxLongitude: Double = 180
    }

    /// Validates if coordinates are valid for SRP navigation
    var hasValidCoordinates: Bool {
        guard let latitude = latitude,
              let longitude = longitude,
              latitude != 0,
              longitude != 0,
              abs(latitude) <= CoordinateValidation.maxLatitude,
              abs(longitude) <= CoordinateValidation.maxLongitude else {
            return false
        }
        return true
    }

    /// Creates a PISuggestion for SRP navigation with coordinate validation and workarounds
    func createSearchSuggestion() -> PISuggestion? {
        guard hasValidCoordinates,
              var longitude = longitude,
              let latitude = latitude else {
            return nil
        }

        // WORKAROUND: Fix Isle of Wight longitude sign error from backend
        // UK locations should have negative longitude (west of Greenwich)
        // Remove this once backend fixes the data
        if title.contains("Isle of Wight") && longitude > 0 {
            longitude = -longitude
        }

        return PISuggestion(dictionary: [
            "name": title,
            "lat": latitude,
            "long": longitude
        ])
    }
}
