//
//  RequestsManagerDashboardDataProviderMockTests.swift
//  PremierInn
//
//  Created by Stanciu, Valentin (Cognizant) on 16/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
import SimpleNetwork
@testable import PremierInn
import Foundation

@Suite("Dashboard Data Provider Mock Tests")
struct RequestsManagerDashboardDataProviderMockTests {

    private func makeStay() throws -> Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 51.5074
        dictionary["hotelLongitude"] = -0.1278
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2026-06-17"
        dictionary["checkOutDate"] = "2026-06-18"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"

        return try Stay(dictionary: dictionary)
    }

    @Test func testDashboardWithMockUpcomingBooking() async throws {
        let mockProvider = MockDashboardRequestsProvider()
        let stay = try makeStay()
        let dataProvider = RequestsManagerDashboardDataProvider(
            with: stay,
            and: nil,
            provider: mockProvider
        )

        // The mock data should be loaded
        #expect(dataProvider.dashboardUpcomingBooking != nil)
    }

    @Test func testDashboardWithMockCheckedInBooking() async throws {
        let mockProvider = MockDashboardCheckedInRequestsProvider()
        let stay = try makeStay()
        let dataProvider = RequestsManagerDashboardDataProvider(
            with: stay,
            and: nil,
            provider: mockProvider
        )

        // Verify checked-in state
        #expect(dataProvider.dashboardUpcomingBooking?.isCheckedIn == true)
        #expect(dataProvider.dashboardUpcomingBooking?.hotel == "Lincoln (Canwick)")
    }

    @Test func testDashboardWithMockEmpty() async throws {
        let mockProvider = MockDashboardEmptyRequestsProvider()
        let stay = try makeStay()
        let dataProvider = RequestsManagerDashboardDataProvider(
            with: stay,
            and: nil,
            provider: mockProvider
        )

        // No upcoming booking should be set
        #expect(dataProvider.dashboardUpcomingBooking == nil)
    }

    @Test func testDashboardWithMockError() async throws {
        let mockProvider = MockDashboardRequestsProvider()
        mockProvider.shouldFail = true
        mockProvider.mockError = NSError(domain: "TestError", code: 500, userInfo: [NSLocalizedDescriptionKey: "Server error"])

        let dataProvider = RequestsManagerDashboardDataProvider(
            with: nil,
            and: nil,
            provider: mockProvider
        )

        // Should handle error gracefully
        #expect(dataProvider.dashboardUpcomingBooking == nil)
    }
}
