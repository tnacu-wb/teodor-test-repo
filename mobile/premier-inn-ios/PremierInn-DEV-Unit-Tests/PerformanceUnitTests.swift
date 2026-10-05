//
//  PerformanceUnitTests.swift
//  PremierInn
//
//  Created by rambabu vallapuri on 03/07/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation

@testable import PremierInn_DEV

class PerformanceUnitTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }
    
    func testPerformance_login() {
        
        for i in 1...10 {
            
            let urlExpectation = expectation(description: "GET")
            
            let requestManager = RequestsManager()
            
            let sTime = CFAbsoluteTimeGetCurrent()
            requestManager.login(withUsername: "whitbreadapps@gmail.com", password: "Wh1tbr34d") { user, error in
                
                if error == nil {
                    urlExpectation.fulfill()
                }
            }
            
            waitForExpectations(timeout: 5) { error in
                
                if let error = error {
                    
                    print("Error: \(error.localizedDescription)")
                }
                else {
                    let tElapsed = CFAbsoluteTimeGetCurrent() - sTime
                    print("Time elapsed for\(i): \(tElapsed) s.")
                    
                }
                
            }
            
        }
    }
    
    
    func testPerformanceSearch() {
        
        let bookingDetails = BookingDetails()
        
        var criteria: Criteria {
            let criteria = Criteria()
            criteria.rooms = {
                let room = Room()
                room.adults = 1
                room.children = 0
                room.type = RoomType.accessible
                
                return [room]
            }()
            criteria.arrivalDate = Date()
            criteria.nights = 2
            
            return criteria
        }
        
        bookingDetails.criteria = criteria
        
        let suggestion = PISuggestion(coordinate: CLLocationCoordinate2D(latitude: 51.5074, longitude: 0.1278))
        
        let requestManager = RequestsManager()
        
        let count = 10
        var sum = 0.0
        
        for index in 1...count {
            
            let suggestionsExpectation = expectation(description: "Test search availabilities")
            
            let timeNow = Date()
            
            requestManager.searchHotelsAvailabilities(bookingDetails: bookingDetails, suggestion: suggestion, page: 0) { response, error in
                
                if error == nil {
                    suggestionsExpectation.fulfill()
                }
            }
            
            waitForExpectations(timeout: 5) { error in
                
                if error != nil {
                    print("Failed to complete in 5 seconds")
                } else {
                    let timeTaken: Double = Date().timeIntervalSince(timeNow)
                    sum += timeTaken
                    print("\(index) Finished in \(timeTaken)")
                }
            }
        }
        print("Average time \((sum / Double(count)))")
    }
    
    func testPerformanceAvailability() {
        
        let bookingDetails = BookingDetails()
        
        var criteria: Criteria {
            let criteria = Criteria()
            criteria.rooms = {
                let room = Room()
                room.adults = 1
                room.children = 0
                room.type = RoomType.accessible
                
                return [room]
            }()
            criteria.arrivalDate = Date()
            criteria.nights = 2
            
            return criteria
        }
        
        bookingDetails.criteria = criteria
        
        let requestManager = RequestsManager()
        
        let count = 10
        var sum = 0.0
        
        for index in 1...count {
            
            let suggestionsExpectation = expectation(description: "Test search availability")
            
            let timeNow = Date()
            
            requestManager.hotelAvailability(hotelCode: "BRIPTI", bookingDetails: bookingDetails) { response, error in
                
                if error == nil {
                    suggestionsExpectation.fulfill()
                }
            }
            
            waitForExpectations(timeout: 5) { error in
                
                if error != nil {
                    print("Failed to complete in 5 seconds")
                } else {
                    let timeTaken: Double = Date().timeIntervalSince(timeNow)
                    sum += timeTaken
                    print("\(index) Finished in \(timeTaken)")
                }
            }
        }
        print("Average time \((sum / Double(count)))")
    }
    
}
