//
//  MockDashboardRequestsProvider.swift
//  PremierInn
//
//  Created by Stanciu, Valentin (Cognizant) on 16/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation
import CoreLocation

class MockDashboardRequestsProvider: DashboardRequestsProvider {
    var shouldFail = false
    var mockError: Error?
        
    func getHomepageContent(
        channel: Channel,
        subchannel: String,
        language: String,
        country: String,
        completion: @escaping (HomepageAppsContent?, Error?) -> Void
    ) {
        if shouldFail {
            completion(nil, mockError ?? NSError(domain: "MockError", code: -1))
            return
        }
        
        // Return empty/minimal content for testing
        completion(nil, nil)
    }
    
    func getDashboardComponents(
        stay: Stay?,
        recentSearchesFlag: Bool,
        completion: @escaping ([DashboardComponent]?, Error?) -> Void
    ) {
        if shouldFail {
            completion(nil, mockError ?? NSError(domain: "MockError", code: -1))
            return
        }

        let sourceStay = stay

        let arrivalString = sourceStay?.arrivalDateString ?? "2026-06-20"
        let checkoutString = sourceStay?.checkOutDateString ?? "2026-06-22"
        let hotelName = sourceStay?.hotelName ?? "Premier Inn Manchester"
        let confirmation = sourceStay?.identifier ?? "AKN4171250"
        let checkedIn = sourceStay?.checkedIn ?? false
        let latitude = sourceStay?.hotelLatitude ?? 53.4830
        let longitude = sourceStay?.hotelLongitude ?? -2.2319
        let upcoming = UpcomingBooking(
            hotelImage: "https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/L/LINMIL/LINMIL%202.jpg",
            hotelName: hotelName,
            confirmationNumber: confirmation,
            arrivalDateString: arrivalString,
            departureDateString: checkoutString,
            guests: 2,
            map: CLLocationCoordinate2D(latitude: latitude, longitude: longitude),
            rooms: [UpcomingBookingRoom(type: "Double")],
            actions: [
                UpcomingBookingAction(type: "BOOKING_DETAILS", title: "Booking Details"),
                UpcomingBookingAction(type: "CIOL", title: "Check In Online"),
                UpcomingBookingAction(type: "DIRECTIONS", title: "Get Directions"),
                UpcomingBookingAction(type: "UPSELLS", title: "Add Meals")
            ],
            address: nil,
            checkedIn: checkedIn,
            arrivalDate: DateFormatter.parameterFormatter.date(from: arrivalString),
            departureDate: DateFormatter.parameterFormatter.date(from: checkoutString)
        )
                
        completion([.upcomingBooking(upcoming)], nil)
    }
}

// MARK: - Test Data Variants

class MockDashboardCheckedInRequestsProvider: MockDashboardRequestsProvider {
    override func getDashboardComponents(
        stay: Stay?,
        recentSearchesFlag: Bool,
        completion: @escaping (_ dashboardComponents: [DashboardComponent]?, _ error: Error?) -> Void
    ) {
        let testUpcomingBooking = UpcomingBooking(
            hotelImage: "https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/L/LINMIL/LINMIL%202.jpg",
            hotelName: "Lincoln (Canwick)",
            confirmationNumber: "AKN4171250",
            arrivalDateString: "2026-06-17",
            departureDateString: "2026-06-25",
            guests: 1,
            map: CLLocationCoordinate2D(latitude: 51.5074, longitude: -0.1278),
            rooms: [
                UpcomingBookingRoom(type: "Single")
            ],
            actions: [
                UpcomingBookingAction(type: "BOOKING_DETAILS", title: "Booking Details"),
                UpcomingBookingAction(type: "DIRECTIONS", title: "Get Directions")
            ],
            address: nil,
            checkedIn: true,
            arrivalDate: DateFormatter.parameterFormatter.date(from: "2026-06-17"),
            departureDate: DateFormatter.parameterFormatter.date(from: "2026-06-18")
        )

        let components: [DashboardComponent] = [
            .upcomingBooking(testUpcomingBooking)
        ]

        completion(components, nil)
    }
}

class MockDashboardEmptyRequestsProvider: MockDashboardRequestsProvider {
    override func getDashboardComponents(
        stay: Stay?,
        recentSearchesFlag: Bool,
        completion: @escaping (_ dashboardComponents: [DashboardComponent]?, _ error: Error?) -> Void
    ) {
        completion([], nil)
    }
}

