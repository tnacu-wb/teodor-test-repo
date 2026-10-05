//
//  HotelInformationMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 01/06/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import XCTest

@testable import SimpleNetwork

class HotelInformationMapperTests: XCTestCase {
    
    var sut = PIDictionary()
    
    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "graphQLHotelInformation", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let hotelInformationDictionary = dataDictionary["hotelInformation"] as! PIDictionary
        sut = HotelInformationMapper.map(from: hotelInformationDictionary, disclaimer: nil)
    }
    
    func testNameMapping() {
        XCTAssertEqual(sut["name"] as! String, "London Holborn")
    }

    func testAdditionalInfoMapping() {
        XCTAssertEqual(sut["hotelDirections"] as! String, "Exit M60 at Junction 7, follow A56 Chester Road and signs for Manchester United Football Ground. After approx 1 mile you will pass a Ford garage on your left, go through 2 sets of traffic lights keeping in the left hand lane and following signs for Salford Quays. At the third set, take the second left onto Trafford Wharf Road, go through the lights and the hotel is on your right. Tram is located nearby, bus number 250 to centre.")

        XCTAssertEqual(sut["hotelDescription"] as! String, "Get closer to the drama at our very own 'Theatre of Dreams' -Premier Inn Manchester Old Trafford. Under a mile from the world-famous stadium, you're also in the perfect spot for shopping, shows, cricket or just chilling out. \n\nIndulge in some retail therapy at the Trafford Centre and Salford Quays. Catch the action at Lancashire Cricket Club. Or soak up the atmosphere, visit the museum and cheer on your team at the legendary Old Trafford. Win, lose or draw, you can rely on extra-comfy beds, spacious rooms and a great night's sleep.")
    }
    
    func testImagesMapping() {
        let images = (sut["images"] as! [PIDictionary]).first
        XCTAssertEqual(images!["fileReference"] as! String, "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room1.jpg")
        XCTAssertEqual((images!["tags"] as! [String]).first(where: { $0 == "standard-room"} ) != nil, true)
    }
    
    func testRoomConfigImageMapping() {

        XCTAssertEqual(((((sut["hotelRoomConfiguration"] as! PIDictionary)["tabItems"] as! [Any]).first as! PIDictionary)["renditions"] as! [Any]).first as! String, "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room1.jpg")
    }

    func testRoomTypeMapping() {

        let hotelRoomConfiguration = sut["hotelRoomConfiguration"] as! PIDictionary
        let tabItems = hotelRoomConfiguration["tabItems"] as! [PIDictionary]
        let aTabItem = tabItems.first(where: { ($0["room"] as! String) == "double" })!
        XCTAssertEqual(aTabItem["roomDescriptionText"] as! String, "A super-comfy Hypnos bed, a power shower and free Wi-Fi, our double rooms have everything you'll need for a great night's sleep.")
    }

    func testRoomTypeMappingFacilities() {

        let hotelRoomConfiguration = sut["hotelRoomConfiguration"] as! PIDictionary
        let tabItems = hotelRoomConfiguration["tabItems"] as! [PIDictionary]
        let aTabItem = tabItems.first(where: { ($0["room"] as! String) == "double" })!
        XCTAssertEqual(aTabItem["roomDescriptionText"] as! String, "A super-comfy Hypnos bed, a power shower and free Wi-Fi, our double rooms have everything you'll need for a great night's sleep.")

        let newFacilities = aTabItem["newFacilities"] as! [PIDictionary]
        let facility = newFacilities.first(where: { ($0["facilitiesTitle"] as! String) == "Improved refreshments" })!
        XCTAssertEqual(facility["facilitiesTitle"] as! String, "Improved refreshments")

        let navOptions = facility["navOptions"] as! [PIDictionary]
        let navOption = navOptions.first!
        XCTAssertEqual(navOption["facility"] as! String, "Including a variety of Pure Leaf tea bags and a sweet treat")
    }

    func testNotesMapping() {
        let hotelNotes = sut["notes"] as! [PIDictionary]
        let hotelNote = hotelNotes.first!
        XCTAssertEqual(hotelNote["text"] as! String, "Here at hub by Premier Inn our terms and conditions are a little different – so please make sure you check them out before booking.")
        XCTAssertEqual(hotelNote["endDate"] as! String, "20/06/2043")
        XCTAssertEqual(hotelNote["startDate"] as! String, "20/06/2023")

    }

    func testTripAdvisorMapping() {
        let tripAdvisorDetails = sut["tripAdvisorDetails"] as! PIDictionary
        XCTAssertEqual(tripAdvisorDetails["numberOfReviews"] as! Int, 1143)
        XCTAssertEqual(tripAdvisorDetails["rating"] as! Double, 4.5)
        XCTAssertEqual((tripAdvisorDetails["awards"] as! [PIDictionary]).first!["awardType"] as! String, "Travelers Choice")
    }
    
    func testMessagingFlagMapping() {
        let messagingFlag = sut["messagingFlag"] as! PIDictionary
        XCTAssertEqual(messagingFlag["flagColor"] as! String, "BCD01B")
        XCTAssertEqual(messagingFlag["flagText"] as! String, "pi")
    }
}
