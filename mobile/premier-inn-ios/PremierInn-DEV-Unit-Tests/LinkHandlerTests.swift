//
//  LinkHandlerTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 12/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class LinkHandlerTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        let remoteConfig = MockRemoteConfig(featureDeeplinkHDP: true, featureDeeplinkSRP: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testLinkHandler() {

        guard let url = URL(string: "http://www.premierinn.com/gb/en/hotels/england/greater-london/london/london-blackfriars-fleet-street.html?cid=GLBC_LONBLA") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertNotNil(components.hotelCode)
        XCTAssertNil(components.criteria)
        XCTAssertNil(components.searchTerm)
    }

    func testLinkHandler2() {

        guard let url = URL(string: "http://www.premierinn.com/gb/en/hotels/england/greater-london/london/london-blackfriars-fleet-street.html?INNID=LONBLA&ARRdd=22&ARRmm=02&ARRyyyy=2017&NIGHTS=3&ROOMS=1&ADULT1=2&CHILD1=0&COT1=0&INTTYP1=DB&SID=4&ISH=true&RAND=PI&CID=GHF_GB_localuniversal_desktop_LONBLA") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertNotNil(components.hotelCode)

        let criteria = components.criteria

        XCTAssertNotNil(criteria)
        XCTAssert(criteria?.nights == 3)
        XCTAssert(criteria?.rooms.first?.adults == 2)
        XCTAssert(criteria?.rooms.first?.children == 0)
        XCTAssert(criteria?.rooms.first?.type == .double)
        XCTAssert(criteria?.rooms.first?.cotRequired == false)

        XCTAssertNil(components.searchTerm)
    }

    func testLinkHandler3() {

        guard let url = URL(string: "http://uk.premierinn.com/en/homeQuickSearch!execute.action?INNID=ABEMAR&ARRdd=22&ARRmm=02&ARRyyyy=2017&NIGHTS=2&ROOMS=1&ADULT1=2&CHILD1=1&COT1=1&INTTYP1=FAM&SID=4&ISH=true&RAND=PI&CID=GHF_GB_localuniversal_mobile_ABEMAR") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertNotNil(components.hotelCode)

        let criteria = components.criteria
        XCTAssertNotNil(criteria)
        XCTAssert(criteria?.nights == 2)
        XCTAssert(criteria?.rooms.first?.adults == 2)
        XCTAssert(criteria?.rooms.first?.children == 1)
        XCTAssert(criteria?.rooms.first?.type == .family)
        XCTAssert(criteria?.rooms.first?.cotRequired == true)

        XCTAssertNil(components.searchTerm)
    }

    func testLinkHandler4() {

        guard let url = URL(string: "http://www.premierinn.com/gb/en/hotels/scotland/lothian/edinburgh/edinburgh-central-lauriston-place.html?CID=TRA_UK_BL_SpecialOfferLink_cmp002_HDP_EDIPTI") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertNotNil(components.hotelCode)
        XCTAssertNil(components.criteria)
        XCTAssertNil(components.searchTerm)
    }

    func testLinkHandler5() {

        guard let url = URL(string: "http://www.premierinn.com/gb/en/search.html?searchModel.searchTerm=EH3%209DG&ARRdd=26&ARRmm=03&ARRyyyy=2017&NIGHTS=1&ROOMS=1&CHILD1=0&COT1=1&ADULT1=1&INTTYP1=SB&SMO1=NON&CID=TRA_UK_META-DESKTOP_EDIPTI&refid=WIIIqwokK2AAAmkH1isAAAA8") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertNotNil(components.hotelCode)

        let criteria = components.criteria
        XCTAssertNotNil(criteria)
        XCTAssert(criteria?.nights == 1)
        XCTAssert(criteria?.rooms.first?.adults == 1)
        XCTAssert(criteria?.rooms.first?.children == 0)
        XCTAssert(criteria?.rooms.first?.type == .single)
        XCTAssert(criteria?.rooms.first?.cotRequired == true)

        XCTAssertNotNil(components.searchTerm)
    }

    func testLinkHandler6() {

        guard let url = URL(string: "http://m.premierinn.com/gb/en/searchresults.html?searchModel.searchTerm=EH3%209DG&ARRdd=26&ARRmm=03&ARRyyyy=2017&NIGHTS=1&ROOMS=1&CHILD1=0&COT1=0&ADULT1=2&INTTYP1=DB&SMO1=NON&CID=TRA_UK_META-MOBILE_EDIPTI&refid=WIIKbQokKVcAASqMNpYAAAAr#/search/") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertNotNil(components.hotelCode)

        let criteria = components.criteria
        XCTAssertNotNil(criteria)
        XCTAssert(criteria?.nights == 1)
        XCTAssert(criteria?.rooms.first?.adults == 2)
        XCTAssert(criteria?.rooms.first?.children == 0)
        XCTAssert(criteria?.rooms.first?.type == .double)
        XCTAssert(criteria?.rooms.first?.cotRequired == false)

        XCTAssertNotNil(components.searchTerm)
    }

    func testLinkHandler7() {

        guard let url = URL(string: "http://www.premierinn.com/gb/en/search.html?searchModel.searchTerm=NG1%205LT&ARRdd=22&ARRmm=10&ARRyyyy=2017&NIGHTS=1&ROOMS=1&CHILD1=0&COT1=0&ADULT1=2&INTTYP1=DB&SMO1=NON&CID=SS_UK_Meta_Desktop_hotelWebsiteLink_NOTMTI_35d54716-df16-11e6-9d88-35af60f3e030&skyscanner_redirectid=NdVHFt8WEeadiDWvYPPgMA") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertNotNil(components.hotelCode)

        let criteria = components.criteria
        XCTAssertNotNil(criteria)
        XCTAssert(criteria?.nights == 1)
        XCTAssert(criteria?.rooms.first?.adults == 2)
        XCTAssert(criteria?.rooms.first?.children == 0)
        XCTAssert(criteria?.rooms.first?.type == .double)
        XCTAssert(criteria?.rooms.first?.cotRequired == false)

        XCTAssertNotNil(components.searchTerm)
    }

    func testCampaignIdentifierCatcher() {

        guard let url = URL(string: "https://www.premierinn.com/gb/en/hotels/england/greater-london/london.html?cid=KNC_Brn%7C_G_UK_UK_Eng_Enc_Brand%20Destinations_LO_London_EX&mckv=s2kVw9eAK_dc%7Cpcrid%7C212526141590%7Ckword%7Clondon%20premier%20inn%7Cmatch%7Ce%7Cplid%7C%7Cpgrid%7C45371048139%7Cptaid%7Ckwd-302755472773%7C&gclid=EAIaIQobChMI2N_mlfyC3AIV3oeyCh3NJQgqEAAYASAAEgJSR_D_BwE") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertEqual(components.campaignIdentifier?.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed), "KNC_Brn%7C_G_UK_UK_Eng_Enc_Brand%20Destinations_LO_London_EX")
    }

    func testMCKVCatcher() {

        guard let url = URL(string: "https://www.premierinn.com/gb/en/hotels/england/greater-london/london.html?cid=KNC_Brn%7C_G_UK_UK_Eng_Enc_Brand%20Destinations_LO_London_EX&mckv=s2kVw9eAK_dc%7Cpcrid%7C212526141590%7Ckword%7Clondon%20premier%20inn%7Cmatch%7Ce%7Cplid%7C%7Cpgrid%7C45371048139%7Cptaid%7Ckwd-302755472773%7C&gclid=EAIaIQobChMI2N_mlfyC3AIV3oeyCh3NJQgqEAAYASAAEgJSR_D_BwE") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertEqual(components.mckv?.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed), "s2kVw9eAK_dc%7Cpcrid%7C212526141590%7Ckword%7Clondon%20premier%20inn%7Cmatch%7Ce%7Cplid%7C%7Cpgrid%7C45371048139%7Cptaid%7Ckwd-302755472773%7C")
    }

    func testETRIDCatcher() {

        guard let url = URL(string: "https://www.premierinn.com/gb/en/home.html?CID=EMC_Alert_londonandcities_B_280618_0to4_12804366&ET_RID=303685521") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        XCTAssertEqual(components.campaignIdentifier?.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed), "EMC_Alert_londonandcities_B_280618_0to4_12804366")
        XCTAssertEqual(components.exactTargetRecipientIdentifier?.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed), "303685521")
    }

	func testAppShortcut_Search() {

		guard let url = URL(string: "http://www.premierinn.com.prelive.nativ-systems.com/gb/en/search.html?searchModel.searchTerm=EH3%209DG&ARRdd=26&ARRmm=03&ARRyyyy=2097&NIGHTS=1&ROOMS=1&CHILD1=0&COT1=0&ADULT1=2&INTTYP1=DB&SMO1=NON&CID=TRA_UK_META-DESKTOP_EDIPTI&refid=WIIIqwokK2AAAmkH1isAAAA8") else { return XCTFail("No url") }
		guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

		XCTAssertEqual(components.campaignIdentifier, "TRA_UK_META-DESKTOP_EDIPTI")

		switch components.shortcutAction {
		case AppShortcut.search(let criteria, let searchTerm)?:
			XCTAssertEqual(criteria?.arrivalDate.analyticsDateFormat, "26/03/2097")
			XCTAssertEqual(criteria?.nights, 1)
			XCTAssertEqual(criteria?.rooms.count, 1)
			XCTAssertEqual(criteria?.rooms.first?.children, 0)
			XCTAssertEqual(criteria?.rooms.first?.adults, 2)
			XCTAssertEqual(criteria?.rooms.first?.cotRequired, false)
			XCTAssertEqual(criteria?.rooms.first?.type, RoomType.double)
			XCTAssertEqual(searchTerm, "EH3 9DG")
		default:
			XCTFail("Expected a shortcut action")
		}
	}

    func testAppShortcut_HotelDetails() {
        
        guard let url = URL(string: "http://uk.premierinn.com/en/homeQuickSearch!execute.action?INNID=ABEMAR&ARRdd=22&ARRmm=02&ARRyyyy=2097&NIGHTS=1&ROOMS=1&ADULT1=2&CHILD1=0&COT1=0&INTTYP1=DB&SID=4&ISH=true&RAND=PI&CID=GHF_GB_localuniversal_mobile_ABEMAR") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }
        
        XCTAssertEqual(components.campaignIdentifier, "GHF_GB_localuniversal_mobile_ABEMAR")
        
        switch components.shortcutAction {
        case AppShortcut.hotelDetails(let hotelCode, _, let criteria)?:
            XCTAssertEqual(criteria?.arrivalDate.analyticsDateFormat, "22/02/2097")
            XCTAssertEqual(criteria?.nights, 1)
            XCTAssertEqual(criteria?.rooms.count, 1)
            XCTAssertEqual(criteria?.rooms.first?.children, 0)
            XCTAssertEqual(criteria?.rooms.first?.adults, 2)
            XCTAssertEqual(criteria?.rooms.first?.cotRequired, false)
            XCTAssertEqual(criteria?.rooms.first?.type, RoomType.double)
            XCTAssertEqual(hotelCode, "ABEMAR")
        default:
            XCTFail("Expected a shortcut action")
        }
    }
    
    func testAppShortcut_HotelDetailsWithSlug() {
        
        guard let url = URL(string: "https://www.premierinn.com/gb/en/hotels/england/greater-london/london/london-waterloo-westminster-bridge.html?ARRdd=03&ARRmm=09&ARRyyyy=2024&NIGHTS=1&ROOMS=1&ADULT1=2&CHILD1=2&COT1=0&INTTYP1=FAM&BRAND=PI") else { return XCTFail("No url") }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return XCTFail("No components") }

        
        switch components.shortcutAction {
        case AppShortcut.hotelDetailsBySlug(let slug, let criteria)?:
            XCTAssert(criteria?.nights == 1)
            XCTAssert(criteria?.rooms.first?.adults == 2)
            XCTAssert(criteria?.rooms.first?.children == 2)
            XCTAssert(criteria?.rooms.first?.type == .family)
            XCTAssert(criteria?.rooms.first?.cotRequired == false)
            XCTAssertEqual(slug, "/hotels/england/greater-london/london/london-waterloo-westminster-bridge.html")
        default:
            XCTFail("Expected a shortcut action")
        }
    }

    func testGenericPushNotifications() {

        // banner missing type
        var dictionary = [
            "title": "title",
            "message": "some text",
            "button": "Continue"
        ]
        var notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .landingScreen)

        // banner with typo in the type
        dictionary = [
            "type": "bannerr",
            "title": "title",
            "message": "some text",
            "button": "Continue"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .landingScreen)
    }

    func testBannerPushNotifications() {

        // banner missing title, message and button
        var dictionary = [
            "type": "banner",
            "CID": "test_cid",
        ]
        var notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .landingScreen)

        // banner
        dictionary = [
            "type": "banner",
            "CID": "test_cid",
            "title": "a title",
            "message": "Some text",
            "button": "Continue"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)

        var message = NotificationsMessage(title: "a title", message: "Some text", ctaTitle: "Continue", id: nil)
        XCTAssertEqual(notification, .banner(message: message))

        var dictionaryToTest = LinkHandler.pushNotificationAnalytics(for: dictionary)
        XCTAssertEqual(dictionaryToTest.count, 2)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.type"] as? String, "banner")
        XCTAssertEqual(dictionaryToTest["s.campaign"] as? String, "test_cid")

        // banner with message parts
        dictionary = [
            "type": "banner",
            "CID": "test_cid",
            "title": "a title",
            "message": "Some text",
            "message1": "Some text", // expect this to be ignored
            "message2": "Some text",
            "message3": "Some text",
            "button": "Continue"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)

        message = NotificationsMessage(title: "a title", message: "Some text\n\nSome text\n\nSome text", ctaTitle: "Continue", id: nil)
        XCTAssertEqual(notification, .banner(message: message))

        dictionaryToTest = LinkHandler.pushNotificationAnalytics(for: dictionary)
        XCTAssertEqual(dictionaryToTest.count, 2)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.type"] as? String, "banner")
        XCTAssertEqual(dictionaryToTest["s.campaign"] as? String, "test_cid")
    }

    func testHDPPushNotifications() {

        // missing hotelCode and hotelBrand
        var dictionary = [
            "type": "hotel_details",
            "CID": "test_cid",
        ]
        var notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .landingScreen)

        // missing hotelBrand
        dictionary = [
            "type": "hotel_details",
            "CID": "test_cid",
            "hotelCode": "LONKIN"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .landingScreen)

        // missing hotelCode
        dictionary = [
            "type": "hotel_details",
            "CID": "test_cid",
            "hotelBrand": "PI"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .landingScreen)

        // HDP without criteria
        dictionary = [
            "type": "hotel_details",
            "CID": "test_cid",
            "hotelCode": "LONKIN",
            "hotelBrand": "PI"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .hotelDetails(hotelCode: "LONKIN", hotelBrand: .premierInn, criteria: nil))

        var dictionaryToTest = LinkHandler.pushNotificationAnalytics(for: dictionary)
        XCTAssertEqual(dictionaryToTest.count, 4)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.type"] as? String, "hotel_details")
        XCTAssertEqual(dictionaryToTest["s.campaign"] as? String, "test_cid")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.hotelCode"] as? String, "LONKIN")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.hotelBrand"] as? String, "PI")

        // HDP missing arrivalDate for criteria
        dictionary = [
            "type": "hotel_details",
            "CID": "test_cid",
            "hotelCode": "LONKIN",
            "hotelBrand": "PI",
            "nights": "2"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .hotelDetails(hotelCode: "LONKIN", hotelBrand: .premierInn, criteria: nil))

        // HDP missing nights for criteria
        dictionary = [
            "type": "hotel_details",
            "CID": "test_cid",
            "hotelCode": "LONKIN",
            "hotelBrand": "PI",
            "arrivalDate": "2024/08/15"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .hotelDetails(hotelCode: "LONKIN", hotelBrand: .premierInn, criteria: nil))

        // HDP with criteria
        dictionary = [
            "type": "hotel_details",
            "CID": "test_cid",
            "hotelCode": "LONKIN",
            "hotelBrand": "PI",
            "arrivalDate": "2024/05/15",
            "nights": "2"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)

        var criteria = Criteria()
        let mockArrivalDateString = "2024/05/15"
        if let arrivalDate = mockArrivalDateString.dateValue {
            criteria.arrivalDate = arrivalDate
            criteria.nights = 2
        }
        XCTAssertEqual(notification, .hotelDetails(hotelCode: "LONKIN", hotelBrand: .premierInn, criteria: criteria))

        dictionaryToTest = LinkHandler.pushNotificationAnalytics(for: dictionary)
        XCTAssertEqual(dictionaryToTest.count, 6)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.type"] as? String, "hotel_details")
        XCTAssertEqual(dictionaryToTest["s.campaign"] as? String, "test_cid")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.hotelCode"] as? String, "LONKIN")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.hotelBrand"] as? String, "PI")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.arrivalDate"] as? String, "2024/05/15")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.nights"] as? String, "2")
    }

    func testSRPPushNotifications() {

        // test location search
        var dictionary = [
            "type": "location_search",
            "CID": "test_cid",
            "location_title": "London",
            "lat": "51.53182",
            "long": "-0.11195"
        ]
        var notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .hotelsNearLocation(suggestion: PISuggestion(dictionary: ["name": "London", "lat": "51.53182", "long": "-0.11195"]), criteria: nil))

        var dictionaryToTest = LinkHandler.pushNotificationAnalytics(for: dictionary)
        XCTAssertEqual(dictionaryToTest.count, 5)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.type"] as? String, "location_search")
        XCTAssertEqual(dictionaryToTest["s.campaign"] as? String, "test_cid")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.locationTitle"] as? String, "London")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.lat"] as? String, "51.53182")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.long"] as? String, "-0.11195")

        // test top destination
        dictionary = [
            "type": "location_search",
            "CID": "test_cid",
            "location_title": "London"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        // this is the London topDestination
        XCTAssertEqual(notification, .hotelsNearLocation(suggestion: PISuggestion(dictionary: ["name": "London", "lat": 51.512238, "long": -0.1059152]), criteria: nil))

        dictionaryToTest = LinkHandler.pushNotificationAnalytics(for: dictionary)
        XCTAssertEqual(dictionaryToTest.count, 3)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.type"] as? String, "location_search")
        XCTAssertEqual(dictionaryToTest["s.campaign"] as? String, "test_cid")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.locationTitle"] as? String, "London")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.lat"] as? String, nil)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.long"] as? String, nil)

        // test missing destination
        dictionary = [
            "type": "location_search",
            "CID": "test_cid",
            "location_title": "something",
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)
        XCTAssertEqual(notification, .landingScreen)

        dictionaryToTest = LinkHandler.pushNotificationAnalytics(for: dictionary)
        XCTAssertEqual(dictionaryToTest.count, 3)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.type"] as? String, "location_search")
        XCTAssertEqual(dictionaryToTest["s.campaign"] as? String, "test_cid")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.locationTitle"] as? String, "something")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.lat"] as? String, nil)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.long"] as? String, nil)

        // test location search with criteria
        dictionary = [
            "type": "location_search",
            "CID": "test_cid",
            "location_title": "London",
            "lat": "51.53182",
            "long": "-0.11195",
            "arrivalDate": "2024/05/15",
            "nights": "2"
        ]
        notification = LinkHandler.handleCustomPushPayload(userInfo: dictionary)

        var criteria = Criteria()
        let mockArrivalDateString = "2024/05/15"
        if let arrivalDate = mockArrivalDateString.dateValue {
            criteria.arrivalDate = arrivalDate
            criteria.nights = 2
        }
        XCTAssertEqual(notification, .hotelsNearLocation(suggestion: PISuggestion(dictionary: ["name": "London", "lat": "51.53182", "long": "-0.11195"]), criteria: criteria))

        dictionaryToTest = LinkHandler.pushNotificationAnalytics(for: dictionary)
        XCTAssertEqual(dictionaryToTest.count, 7)
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.type"] as? String, "location_search")
        XCTAssertEqual(dictionaryToTest["s.campaign"] as? String, "test_cid")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.locationTitle"] as? String, "London")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.lat"] as? String, "51.53182")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.long"] as? String, "-0.11195")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.arrivalDate"] as? String, "2024/05/15")
        XCTAssertEqual(dictionaryToTest["analyticsData.pushNotification.nights"] as? String, "2")
    }
    
    func testAppShortcut_HomepageGbMapsToLandingScreen() {
        SettingsManager.sharedInstance.piRemoteConfig = MockRemoteConfig(featureDeeplinkHomepage: true)

        guard let url = URL(string: "https://www.premierinn.com/gb/en/home.html") else {
            return XCTFail("No url")
        }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
            return XCTFail("No components")
        }

        XCTAssertEqual(components.shortcutAction, .landingScreen)
    }

    func testAppShortcut_HomepageGbDoesNotMapWhenFeatureDisabled() {
        SettingsManager.sharedInstance.piRemoteConfig = MockRemoteConfig(featureDeeplinkHomepage: false)

        guard let url = URL(string: "https://www.premierinn.com/gb/en/home.html") else {
            return XCTFail("No url")
        }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
            return XCTFail("No components")
        }

        XCTAssertNil(components.shortcutAction)
    }
    
    func testAppShortcut_HomepageDeMapsToLandingScreen() {
        SettingsManager.sharedInstance.piRemoteConfig = MockRemoteConfig(featureDeeplinkHomepage: true)

        guard let url = URL(string: "https://www.premierinn.com/de/de/home.html") else {
            return XCTFail("No url")
        }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
            return XCTFail("No components")
        }

        XCTAssertEqual(components.shortcutAction, .landingScreen)
    }

    func testAppShortcut_HomepageDeDoesNotMapWhenFeatureDisabled() {
        SettingsManager.sharedInstance.piRemoteConfig = MockRemoteConfig(featureDeeplinkHomepage: false)

        guard let url = URL(string: "https://www.premierinn.com/de/de/home.html") else {
            return XCTFail("No url")
        }
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
            return XCTFail("No components")
        }

        XCTAssertNil(components.shortcutAction)
    }

}
