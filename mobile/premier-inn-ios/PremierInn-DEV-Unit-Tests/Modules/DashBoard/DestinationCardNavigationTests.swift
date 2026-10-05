//
//  DestinationCardNavigationTests.swift
//  PremierInn-DEV-Unit-Tests
//
//  Created by Emil Vaklinov 21/04/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation
@testable import PremierInn
@testable import SimpleNetwork

final class DestinationCardNavigationTests: XCTestCase {
    
    // MARK: - Coordinate Validation Tests
    
    func testValidCoordinatesPassValidation() {
        let viewModel = createViewModel(title: "Isle of Wight", latitude: 50.6938, longitude: -1.3047)
        
        XCTAssertTrue(viewModel.hasValidCoordinates)
    }
    
    func testZeroLatitudeFailsValidation() {
        let viewModel = createViewModel(title: "Test", latitude: 0.0, longitude: -1.3047)
        
        XCTAssertFalse(viewModel.hasValidCoordinates)
    }
    
    func testZeroLongitudeFailsValidation() {
        let viewModel = createViewModel(title: "Test", latitude: 50.6938, longitude: 0.0)
        
        XCTAssertFalse(viewModel.hasValidCoordinates)
    }
    
    func testNilCoordinatesFailValidation() {
        let viewModel = createViewModel(title: "Test", latitude: nil, longitude: nil)
        
        XCTAssertFalse(viewModel.hasValidCoordinates)
    }
    
    func testLatitudeOutOfRangeFailsValidation() {
        let invalidLatitudes = [91.0, -91.0, 100.0, -100.0]
        
        for latitude in invalidLatitudes {
            let viewModel = createViewModel(title: "Test", latitude: latitude, longitude: 0.5)
            XCTAssertFalse(viewModel.hasValidCoordinates, "Latitude \(latitude) should fail validation")
        }
    }
    
    func testLongitudeOutOfRangeFailsValidation() {
        let invalidLongitudes = [181.0, -181.0, 200.0, -200.0]
        
        for longitude in invalidLongitudes {
            let viewModel = createViewModel(title: "Test", latitude: 50.0, longitude: longitude)
            XCTAssertFalse(viewModel.hasValidCoordinates, "Longitude \(longitude) should fail validation")
        }
    }
    
    func testBoundaryCoordinatesPassValidation() {
        let validCases = [
            (lat: 90.0, long: 180.0),
            (lat: -90.0, long: -180.0),
            (lat: 89.9999, long: 179.9999),
            (lat: -89.9999, long: -179.9999)
        ]
        
        for testCase in validCases {
            let viewModel = createViewModel(title: "Test", latitude: testCase.lat, longitude: testCase.long)
            XCTAssertTrue(viewModel.hasValidCoordinates, "Coordinates (\(testCase.lat), \(testCase.long)) should pass validation")
        }
    }
    
    // MARK: - PISuggestion Creation Tests
    
    func testCreateSearchSuggestionWithValidCoordinates() {
        let viewModel = createViewModel(title: "Bristol", latitude: 51.4545, longitude: -2.5879)
        
        guard let suggestion = viewModel.createSearchSuggestion() else {
            XCTFail("Suggestion should not be nil")
            return
        }
        
        XCTAssertEqual(suggestion.title, "Bristol")
        XCTAssertEqual(suggestion.coordinate.latitude, 51.4545, accuracy: 0.0001)
        XCTAssertEqual(suggestion.coordinate.longitude, -2.5879, accuracy: 0.0001)
        XCTAssertFalse(suggestion.isHotel)
    }
    
    func testCreateSearchSuggestionWithInvalidCoordinatesReturnsNil() {
        let viewModel = createViewModel(title: "Test", latitude: 0.0, longitude: 0.0)
        
        let suggestion = viewModel.createSearchSuggestion()
        
        XCTAssertNil(suggestion)
    }
    
    func testCreateSearchSuggestionWithNilCoordinatesReturnsNil() {
        let viewModel = createViewModel(title: "Test", latitude: nil, longitude: nil)
        
        let suggestion = viewModel.createSearchSuggestion()
        
        XCTAssertNil(suggestion)
    }
    
    // MARK: - Isle of Wight Longitude Correction Tests
    
    func testIsleOfWightLongitudeCorrectionApplied() {
        let viewModel = createViewModel(title: "Isle of Wight", latitude: 50.6938, longitude: 1.3047)
        
        guard let suggestion = viewModel.createSearchSuggestion() else {
            XCTFail("Suggestion should not be nil")
            return
        }
        
        XCTAssertEqual(suggestion.coordinate.longitude, -1.3047, accuracy: 0.0001, "Isle of Wight longitude should be corrected to negative")
    }
    
    func testIsleOfWightLongitudeCorrectionNotAppliedWhenAlreadyNegative() {
        let viewModel = createViewModel(title: "Isle of Wight", latitude: 50.6938, longitude: -1.3047)
        
        guard let suggestion = viewModel.createSearchSuggestion() else {
            XCTFail("Suggestion should not be nil")
            return
        }
        
        XCTAssertEqual(suggestion.coordinate.longitude, -1.3047, accuracy: 0.0001)
    }
    
    func testIsleOfWightLongitudeCorrectionNotAppliedToOtherLocations() {
        let viewModel = createViewModel(title: "Bristol", latitude: 51.4545, longitude: 1.5)
        
        guard let suggestion = viewModel.createSearchSuggestion() else {
            XCTFail("Suggestion should not be nil")
            return
        }
        
        XCTAssertEqual(suggestion.coordinate.longitude, 1.5, accuracy: 0.0001, "Other locations should not have longitude corrected")
    }
    
    // MARK: - Helper Methods
    
    private func createViewModel(
        title: String,
        latitude: Double?,
        longitude: Double?
    ) -> DestinationCardViewModel {
        return DestinationCardViewModel(
            imageName: "/test/image.jpg",
            title: title,
            description: "Test description",
            tag: nil,
            url: URL(string: "https://example.com"),
            openLinkInApp: true,
            analyticsKey: "test-key",
            order: 1,
            latitude: latitude,
            longitude: longitude
        )
    }
}
