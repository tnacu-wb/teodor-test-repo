//
//  URLExtensionsTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Emil Vaklinov on 06/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class URLExtensionsTests: XCTestCase {
    
    // MARK: - sizedImageURL Tests
    
    func testSizedImageURLHttpConvertedToHttps() {

        let httpURL = URL(string: "http://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/L/LONBLA/LONBLA%201.jpg")!
        let sizedURL = httpURL.sizedImageURL(withSize: .medium)
        
        XCTAssertNotNil(sizedURL)
        XCTAssertTrue(sizedURL!.absoluteString.hasPrefix("https://"), "Image URL should be converted to HTTPS")
        XCTAssertFalse(sizedURL!.absoluteString.contains("http://"), "Image URL should not contain http://")
    }
    
    func testSizedImageURLHttpsRemainsHttps() {

        let httpsURL = URL(string: "https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/L/LONBLA/LONBLA%201.jpg")!
        let sizedURL = httpsURL.sizedImageURL(withSize: .medium)
        
        XCTAssertNotNil(sizedURL)
        XCTAssertTrue(sizedURL!.absoluteString.hasPrefix("https://"), "Image URL should be HTTPS")
    }
    
    func testSizedImageURLContainsSizeParameters() {

        let imageURL = URL(string: "http://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/L/LONBLA/LONBLA%201.jpg")!
        let sizedURL = imageURL.sizedImageURL(withSize: .medium)
        
        XCTAssertNotNil(sizedURL)
        XCTAssertTrue(sizedURL!.absoluteString.contains(".piimage.thumbnail.480.320.jpg"), "URL should contain medium size parameters")
    }
    
    func testSizedImageURLTinySize() {

        let imageURL = URL(string: "https://www.premierinn.com/content/dam/test.jpg")!
        let sizedURL = imageURL.sizedImageURL(withSize: .tiny)
        
        XCTAssertNotNil(sizedURL)
        XCTAssertTrue(sizedURL!.absoluteString.contains(".piimage.thumbnail.80.80.jpg"))
    }
    
    func testSizedImageURLSmallSize() {

        let imageURL = URL(string: "https://www.premierinn.com/content/dam/test.jpg")!
        let sizedURL = imageURL.sizedImageURL(withSize: .small)
        
        XCTAssertNotNil(sizedURL)
        XCTAssertTrue(sizedURL!.absoluteString.contains(".piimage.thumbnail.120.120.jpg"))
    }
    
    func testSizedImageURLMediumSize() {

        let imageURL = URL(string: "https://www.premierinn.com/content/dam/test.jpg")!
        let sizedURL = imageURL.sizedImageURL(withSize: .medium)
        
        XCTAssertNotNil(sizedURL)
        XCTAssertTrue(sizedURL!.absoluteString.contains(".piimage.thumbnail.480.320.jpg"))
    }
    
    func testSizedImageURLLargeSize() {

        let imageURL = URL(string: "https://www.premierinn.com/content/dam/test.jpg")!
        let sizedURL = imageURL.sizedImageURL(withSize: .large)
        
        XCTAssertNotNil(sizedURL)
        XCTAssertTrue(sizedURL!.absoluteString.contains(".piimage.thumbnail.800.600.jpg"))
    }
    
    func testSizedImageURLMassiveSize() {

        let imageURL = URL(string: "https://www.premierinn.com/content/dam/test.jpg")!
        let sizedURL = imageURL.sizedImageURL(withSize: .massive)
        
        XCTAssertNotNil(sizedURL)
        XCTAssertTrue(sizedURL!.absoluteString.contains(".piimage.thumbnail.1920.1200.jpg"))
    }
    
    func testSizedImageURLPreservesPath() {

        let complexURL = URL(string: "http://www.premierinn.com/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-lounge2.jpg")!
        let sizedURL = complexURL.sizedImageURL(withSize: .medium)
        
        XCTAssertNotNil(sizedURL)
        XCTAssertTrue(sizedURL!.absoluteString.contains("https://www.premierinn.com/content/dam/hub/hotelimages/LONKIN/hub-kings-cross-lounge2.jpg"))
    }
    
    // MARK: - queryParams Tests
    
    func testQueryParamsEmptyQuery() {
        
        let url = URL(string: "https://www.premierinn.com/hotels")!
        let params = url.queryParams
        
        XCTAssertTrue(params.isEmpty)
    }
    
    func testQueryParamsSingleParameter() {

        let url = URL(string: "https://www.premierinn.com/search?term=london")!
        let params = url.queryParams
        
        XCTAssertEqual(params["term"], "london")
    }
    
    func testQueryParamsMultipleParameters() {
        
        let url = URL(string: "https://www.premierinn.com/search?term=london&nights=2&rooms=1")!
        let params = url.queryParams
        
        XCTAssertEqual(params["term"], "london")
        XCTAssertEqual(params["nights"], "2")
        XCTAssertEqual(params["rooms"], "1")
    }
}
