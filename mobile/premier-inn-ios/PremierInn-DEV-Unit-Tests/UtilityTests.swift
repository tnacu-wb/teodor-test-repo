//
//  UtilityTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import CoreLocation
import XCTest
import SimpleNetwork
@testable import PremierInn
import Formeka

class MockRemoteConfig: PIRemoteConfig {

    static var sharedInstance: PIRemoteConfig = MockRemoteConfig()

    var topDestinations: [PIDictionary]?
    var cmsStrings: [String : String] = [:]
    var rateContent: [PIDictionary] = [
        ["": ""]
    ]
    var forceUpdateTitle: String = ""
    var forceUpdateDescription: String = ""
    var appNeedsToBeUpdated: Bool = false
    var forceTestPushValue: String?
    var notificationsMessage: NotificationsMessage?
    var versioningDict: PIDictionary?
    var notificationsMessageFeature: Bool? = false
    var myAccountLinks: [PIDictionary] = []
    var coronavirusMessagingHomepage: Bool? = false
    var coronavirusMessagingSRPAndHDP: Bool? = false
    var coronavirusBannerMessage: String = ""
    var passwordRegexs: [String: String] = [:]
    var twinRoomInfo: [PIDictionary] = []
    var bartDown: Bool? = false
    var skipHotelMigrationServiceCheck: Bool? = false
    var shouldOperaRedirectToWeb: Bool? = false
    var shouldShowAmendBanner: Bool? = false
    var orderAndPay: Bool? = false
    var getKey: Bool? = false
    var auth0RealmLeisureUAT: String?
    var auth0RealmBBUAT: String?
    var auth0RealmLeisureProd: String?
    var auth0RealmBBProd: String?
    var operaHotels: [PIDictionary] = []
    var pollingDelay3cp: Int = 3
    var pollingRetries3cp: Int = 20
    var pollingInterval3cp: Int = 3
    var confirmationPollingMessagesConfig: PIDictionary?
    var confirmationPollingDelay: Int = 1
    var confirmationPollingInterval: Int = 1
    var shouldOperaShowFallBackForBB: Bool? = true
    var employeeQuestionsOperaFeature: Bool?
    var featureUsePIProdAuth0LeisureDatabase: Bool?
    var featureUseBBProdAuth0BBDatabase: Bool?
    var featureUseOperaRestProdEndpoint: Bool?
    var featureUseOperaRestLowerEnvironmentEndpoint: Bool?
    var featureUsePILowerAuth0LeisureDatabase: Bool?
    var featureUseBBLowerAuth0BBDatabase: Bool?
    var featureUseSnowdropOperaLowerEnvEndpoint: Bool?
    var featureUseSnowdropOperaProdEndpoint: Bool?
    var featureContentsquareUnmask: Bool?
    var featureThirdPartyPrepaid: Bool?

    var allowEmployeeOfferFeature: Bool? = true

    // PayPal
    var featurePayPal: Bool? = false
    var featureUsePaypalInitiatePayment: Bool? = false

    // ApplePay
    var featureApplePay: Bool? = false
    var kioskHotels: [PIDictionary]?
    var snpWifiHotels: [PIDictionary]?

    // Deeplinking
    var featureDeeplinkSRP: Bool? = false
    var featureDeeplinkHDP: Bool? = false
    var featureDeeplinkHomepage: Bool? = false
    var featureDeeplinkCIOL: Bool? = false
    
    // CIOL
    var featureCIOL: Bool? = false
    var featureCIOLUpsells: Bool? = false

    // ECI, LCO
    var featureAllowEciLco: Bool? = false

    var featureAppleWalletPass: Bool? = false
    var featureAddNewCard: Bool? = false
    var featureDonations: Bool? = false
    var featureAppIncentive: Bool? = false

    var featureDigitalKeys: Bool? = false

    var featureHDPDiscountCode: Bool? = false
    var hdpDiscountCodeAllowedBrands: [String]  = []

    var featurePIBACPEnabled: Bool? = false

    // Dashboard
    var featureShowDashboard: Bool? = true
    var featureShouldShowDashboardEcommerceContent: Bool? = false

    func fetchRemoteConfig(completion: @escaping (Bool) -> Void) {
        completion(true)
    }

    init(rateContent: [PIDictionary]? = [["": ""]],
         confirmationPollingMessagesConfig: PIDictionary? = nil,
         featureDeeplinkHDP: Bool? = false,
         featureDeeplinkSRP: Bool? = false,
         featureDeeplinkHomepage: Bool? = false,
         featureDeeplinkCIOL: Bool? = false,
         shouldShowAmendBanner: Bool? = false,
         shouldOperaShowFallBackForBB: Bool? = false,
         kioskHotels: [PIDictionary]? = nil,
         shouldOperaRedirectToWeb: Bool? = false,
         skipHotelMigrationServiceCheck: Bool? = false,
         operaHotels: [PIDictionary]? = [],
         featureCIOL: Bool? = false,
         featureCIOLUpsells: Bool? = false,
         appleWalletEnabled: Bool? = false,
         employeeQuestionsOperaFeature: Bool? = true,
         featureAddNewCard: Bool? = false,
         wifiEnabledHotel: [PIDictionary]? = nil,
         featureDigitalKeys: Bool? = false,
         featureHDPDiscountCode: Bool? = false,
         featureDonations: Bool? = false) {

        self.rateContent = rateContent ?? [["": ""]]
        self.confirmationPollingMessagesConfig = confirmationPollingMessagesConfig
        self.featureDeeplinkHDP = featureDeeplinkHDP
        self.featureDeeplinkSRP = featureDeeplinkSRP
        self.featureDeeplinkHomepage = featureDeeplinkHomepage
        self.featureDeeplinkCIOL = featureDeeplinkCIOL
        self.shouldShowAmendBanner = shouldShowAmendBanner
        self.shouldOperaShowFallBackForBB = shouldOperaShowFallBackForBB
        self.kioskHotels = kioskHotels
        self.shouldOperaRedirectToWeb = shouldOperaRedirectToWeb
        self.skipHotelMigrationServiceCheck = skipHotelMigrationServiceCheck
        self.operaHotels = operaHotels ?? []
        self.featureCIOL = featureCIOL
        self.featureCIOLUpsells = featureCIOLUpsells
        self.featureAppleWalletPass = appleWalletEnabled
        self.employeeQuestionsOperaFeature = employeeQuestionsOperaFeature
        self.featureAddNewCard = featureAddNewCard
        self.snpWifiHotels = wifiEnabledHotel
        self.featureDonations = featureDonations
        self.featureDigitalKeys = featureDigitalKeys
        self.featureHDPDiscountCode = featureHDPDiscountCode
    }
}

class UtilityTests: XCTestCase {

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        NetworkErrorManager.sharedInstance.errors = [:]
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        //        NetworkErrorManager.sharedInstance.errors = [:]

        super.tearDown()
    }

    // MARK: - Tests

    func testWalletKeyDateFormatSameMonthSameYear() {
        // GIVEN
        let arrivalDate = DateFormatter.parameterFormatter.date(from: "2026-04-04")
        let departureDate = DateFormatter.parameterFormatter.date(from: "2026-04-06")

        // WHEN
        let formatted = Date.walletKeyFormat(arrival: arrivalDate, departure: departureDate)

        // THEN
        XCTAssertEqual(formatted, "04 - 06 Apr 2026")
    }

    func testWalletKeyDateFormatDifferentMonthSameYear() {
        // GIVEN
        let arrivalDate = DateFormatter.parameterFormatter.date(from: "2026-04-30")
        let departureDate = DateFormatter.parameterFormatter.date(from: "2026-05-01")

        // WHEN
        let formatted = Date.walletKeyFormat(arrival: arrivalDate, departure: departureDate)

        // THEN
        XCTAssertEqual(formatted, "30 Apr - 01 May 2026")
    }

    func testWalletKeyDateFormatDifferentYear() {
        // GIVEN
        let arrivalDate = DateFormatter.parameterFormatter.date(from: "2026-12-31")
        let departureDate = DateFormatter.parameterFormatter.date(from: "2027-01-01")

        // WHEN
        let formatted = Date.walletKeyFormat(arrival: arrivalDate, departure: departureDate)

        // THEN
        XCTAssertEqual(formatted, "31 Dec 2026 - 01 Jan 2027")
    }

    func testTimeSinceMidnight() {

        var calendar = Calendar(identifier: Calendar.Identifier.gregorian)

        for name in TimeZone.knownTimeZoneIdentifiers {

            calendar.timeZone = TimeZone(identifier: name)!

            var components = DateComponents()
            components.year = 2016
            components.month = 9
            components.day = 10
            components.hour = 0
            components.minute = 0
            components.second = 36

            let date = calendar.date(from: components)

            let seconds = date!.secondsSinceMidnight(timeZone: calendar.timeZone)

            XCTAssertEqual(seconds, 36)
        }
    }

    func testCMSAndLocalizationStrings() {

        //        let fileURL = Bundle(for: type(of: self)).url(forResource: "config", withExtension: "json")!
        //        let    data = try! Data(contentsOf: fileURL)
        //        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        //
        //        let sm = SettingsManager.sharedInstance
        //
        //        let testKey = "landingMainMessage"
        //        let notLocalizedString = PILocalizedString(testKey, comment: "")
        //
        //        XCTAssertEqual(notLocalizedString, "Where are you going?", "Failed PILocalizedString check with unset string")

        //        sm.updateCMSStrings(with: jsonDictionary)
        //
        //        let localizedStringFromJsonFile = "Where are you going test?"
        //        let localizedString = PILocalizedString(testKey, comment: "")
        //
        //        XCTAssertEqual(localizedString, localizedStringFromJsonFile)
    }

    func testCMSErrors() {

        let fileURL = Bundle(for: type(of: self)).url(forResource: "errors", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

        let nem = NetworkErrorManager.sharedInstance
        let serverError = RequestsManagerError.serverError(["details": ["CARD_NOT_VALID_ON_DAY_OF_DEPARTURE", "So many reasons"]])

        XCTAssertNil(NetworkErrorManager.errorUI(forError: serverError))

        nem.updateErrors(with: jsonDictionary)

        let errorUI = NetworkErrorManager.errorUI(forError: serverError)
        XCTAssertNotNil(errorUI)
        XCTAssertEqual(errorUI?.title, PILocalizedString("CARD_NOT_VALID_ON_DAY_OF_DEPARTURE_TITLE", comment: ""))
        XCTAssertEqual(errorUI?.description, PILocalizedString("CARD_NOT_VALID_ON_DAY_OF_DEPARTURE_DESCRIPTION", comment: ""))
        XCTAssertNil(errorUI?.actions)
    }

    func testFraudCheckError() {

        let fileURL = Bundle(for: type(of: self)).url(forResource: "errors", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

        let nem = NetworkErrorManager.sharedInstance
        let serverError = RequestsManagerError.serverError(["details": ["FRAUD_CHECK_FAILED", "So many reasons"]])

        XCTAssertNil(NetworkErrorManager.errorUI(forError: serverError))

        nem.updateErrors(with: jsonDictionary)

        let errorUI = NetworkErrorManager.errorUI(forError: serverError)
        XCTAssertNotNil(errorUI)
        XCTAssertEqual(errorUI?.title, PILocalizedString("FRAUD_CHECK_FAILED_TITLE", comment: ""))
        XCTAssertEqual(errorUI?.description, PILocalizedString("FRAUD_CHECK_FAILED_DESCRIPTION", comment: ""))
        XCTAssertEqual(errorUI?.actions?.count, 2)
    }

    func testPrepaymentFailedError() {

        let fileURL = Bundle(for: type(of: self)).url(forResource: "errors", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

        let nem = NetworkErrorManager.sharedInstance
        let serverError = RequestsManagerError.serverError(["details": ["PREPAYMENT_FAILED", "So many reasons"]])

        XCTAssertNil(NetworkErrorManager.errorUI(forError: serverError))

        nem.updateErrors(with: jsonDictionary)

        let errorUI = NetworkErrorManager.errorUI(forError: serverError)
        XCTAssertNotNil(errorUI)
        XCTAssertEqual(errorUI?.title, PILocalizedString("PREPAYMENT_FAILED_TITLE", comment: ""))
        XCTAssertEqual(errorUI?.description, PILocalizedString("PREPAYMENT_FAILED_DESCRIPTION", comment: ""))
        XCTAssertEqual(errorUI?.actions, nil)
    }

    func testLocationManager() {

        let locationManager = LocationManager()

        let alertController = locationManager.authDeniedSettingsAlertController()
        XCTAssertNotNil(alertController.title)
        XCTAssertNotNil(alertController.description)

        locationManager.findUserLocation()
        locationManager.locationManager(CLLocationManager(), didChangeAuthorization: .authorizedWhenInUse)
        locationManager.locationManager(CLLocationManager(), didChangeAuthorization: .denied)
        locationManager.locationManager(CLLocationManager(), didUpdateLocations: [CLLocation(latitude: 2.0, longitude: 2.0)])
    }

    func testLocalizedDateString() {

        let testString1 = Date.localizedMediumDateStringForToday

        XCTAssertNotNil(testString1, "Localized today date string was nil")

        let testString2 = Date().localizedShortStringFormat

        XCTAssertNotNil(testString2, "Localized specified date string was nil")

        let testString3 = Date().localizedVeryShortStringFormat
        XCTAssertNotNil(testString3, "Localized specified date string was nil")

        let testString4 = Date().localizedShortDayMonthStringFormat
        XCTAssertNotNil(testString4, "Localized specified date string was nil")
    }

    func testSecondsSinceMidnight() {

        let date = Date()
        let dateComponents = Calendar.current.dateComponents([.day, .month, .year, .hour, .minute, .second], from: date)

        let secondsSinceMidnightDate = Date().dateUsing(existingDate: date, secondsSinceMidnight: 100)!
        let secondsSinceMidnightDateComponents = Calendar.current.dateComponents([.day, .month, .year, .hour, .minute, .second], from: secondsSinceMidnightDate)

        XCTAssertEqual(dateComponents.year, secondsSinceMidnightDateComponents.year)
        XCTAssertEqual(dateComponents.month, secondsSinceMidnightDateComponents.month)
        XCTAssertEqual(dateComponents.day, secondsSinceMidnightDateComponents.day)

        XCTAssertEqual(secondsSinceMidnightDateComponents.second, 40)
        XCTAssertEqual(secondsSinceMidnightDateComponents.minute, 2)
        XCTAssertEqual(secondsSinceMidnightDateComponents.hour, 0)
    }

    func testSubstringFunctions() {

        let testString = "Hello, Elephants"

        XCTAssertEqual(testString.substringToIndex(3), "Hel", "Failed substring to index check")
        XCTAssertEqual(testString.substringToIndex(30), "Hello, Elephants", "Failed substring to index check")
        XCTAssertEqual(testString.substringToIndex(-30), "", "Failed substring to index check")

        XCTAssertEqual(testString.substringFromIndex(3), "lo, Elephants", "Failed substring from index check")
        XCTAssertEqual(testString.substringFromIndex(30), "")
        XCTAssertEqual(testString.substringFromIndex(-30), "Hello, Elephants", "Failed substring from index check")

        XCTAssertEqual(testString.truncated(withLength: 15), "Hello, Elephant...", "Failed truncate with length check")
        XCTAssertEqual(testString.truncated(withLength: 30), "Hello, Elephants", "Failed truncate with length check")
        XCTAssertEqual(testString.truncated(withLength: -30), "", "Failed truncate with length check")
    }

    func testRangesForStringInString() {

        let ranges = "abc a bc abc".ranges(of: "e")
        XCTAssertNotNil(ranges)
        XCTAssert(ranges.isEmpty)
    }

    func testLengthFormatterAttributedString() {

        let formatter = LengthFormatter()
        formatter.unitStyle = .long

        let attributedString = formatter.attributedString(distance: 14, unit: .mile, suffix: " from you")

        XCTAssertEqual(attributedString?.string, "14 miles from you")
    }

    func testPriceMantissaMatching() {

        let regularFont = UIFont.systemFont(ofSize: 12)
        let mantissaFont = UIFont.boldSystemFont(ofSize: 14)
        let regularColor = UIColor.red
        let mantissaColor = UIColor.green

        let baseAttributes: [NSAttributedString.Key: Any] = [.font: regularFont, .foregroundColor: regularColor]
        let mantissaAttributes: [NSAttributedString.Key: Any] = [.font: mantissaFont, .foregroundColor: mantissaColor]

        XCTAssertEqual(Cost(amount: 0, currencyCode: "GBP").fancyMantissaString(baseAttributes: baseAttributes, mantissaAttributes: mantissaAttributes).string, "£0.00")
        XCTAssertEqual(Cost(amount: 0.0, currencyCode: "EUR").fancyMantissaString(baseAttributes: baseAttributes, mantissaAttributes: mantissaAttributes).string, "€0.00")
        XCTAssertEqual(Cost(amount: 0.1, currencyCode: "GBP").fancyMantissaString(baseAttributes: baseAttributes, mantissaAttributes: mantissaAttributes).string, "£0.10")

        let cost = Cost(amount: 12.34, currencyCode: "GBP")
        let attributedString = cost.fancyMantissaString(baseAttributes: baseAttributes, mantissaAttributes: mantissaAttributes)
        XCTAssertEqual(attributedString.string, "£12.34")

        /*£*/XCTAssertEqual(attributedString.attributes(at: 0, effectiveRange: nil)[.font] as? UIFont, regularFont)
        /*£*/XCTAssertEqual(attributedString.attributes(at: 0, effectiveRange: nil)[.foregroundColor] as? UIColor, regularColor)
        /*1*/XCTAssertEqual(attributedString.attributes(at: 1, effectiveRange: nil)[.font] as? UIFont, regularFont)
        /*1*/XCTAssertEqual(attributedString.attributes(at: 1, effectiveRange: nil)[.foregroundColor] as? UIColor, regularColor)
        /*2*/XCTAssertEqual(attributedString.attributes(at: 2, effectiveRange: nil)[.font] as? UIFont, regularFont)
        /*2*/XCTAssertEqual(attributedString.attributes(at: 2, effectiveRange: nil)[.foregroundColor] as? UIColor, regularColor)

        /*.*/XCTAssertEqual(attributedString.attributes(at: 3, effectiveRange: nil)[.font] as? UIFont, mantissaFont)
        /*.*/XCTAssertEqual(attributedString.attributes(at: 3, effectiveRange: nil)[.foregroundColor] as? UIColor, mantissaColor)
        /*3*/XCTAssertEqual(attributedString.attributes(at: 4, effectiveRange: nil)[.font] as? UIFont, mantissaFont)
        /*3*/XCTAssertEqual(attributedString.attributes(at: 4, effectiveRange: nil)[.foregroundColor] as? UIColor, mantissaColor)
        /*4*/XCTAssertEqual(attributedString.attributes(at: 5, effectiveRange: nil)[.font] as? UIFont, mantissaFont)
        /*4*/XCTAssertEqual(attributedString.attributes(at: 5, effectiveRange: nil)[.foregroundColor] as? UIColor, mantissaColor)
    }

    func testSimplePriceRegEx() {

        let text = "The price $12 should be similar to £ 13 and also, to be sure, to £13.23. Also accepted are 45,3€, 56.4€ and 4.01 €."

        let ranges = text.ranges(ofRegex: Constants.Regex.simplePrice)

        XCTAssertEqual(ranges.count, 6)
        XCTAssertEqual((text as NSString).substring(with: ranges[0]), "$12")
        XCTAssertEqual((text as NSString).substring(with: ranges[1]), "£ 13")
        XCTAssertEqual((text as NSString).substring(with: ranges[2]), "£13.23")
        XCTAssertEqual((text as NSString).substring(with: ranges[3]), "45,3€")
        XCTAssertEqual((text as NSString).substring(with: ranges[4]), "56.4€")
        XCTAssertEqual((text as NSString).substring(with: ranges[5]), "4.01 €")
    }

    func testTriangleView() {

        let triangleView = TriangleView(frame: CGRect(x: 0, y: 0, width: 100, height: 30))
        XCTAssertNil(triangleView.layer.sublayers)
        triangleView.awakeFromNib()
        XCTAssertNotNil(triangleView.layer.sublayers)
    }

    func testStringCreditCardValue() {

        let testCard1 = "44443333222211"
        XCTAssert(testCard1.creditCardDisplayValue() == "4444 3333 2222 11")
    }

    func testVersions() {

        let version1 = Version(string: "3.1.2")
        let version2 = Version(string: "3.1.3")
        let version3 = Version(string: "3")
        let version4 = Version(string: "3.1")
        let version5 = Version(string: "3.0.0")

        XCTAssert(version1 < version2)
        XCTAssert(version3 < version1)
        XCTAssert(version3 < version2)
        XCTAssert(version4 < version2)
        XCTAssert(version3 < version4)

        XCTAssertFalse(version2 < version4)
        XCTAssertFalse(version3 < version3)
        XCTAssertFalse(version3 < version5)
        XCTAssertFalse(version5 < version3)
    }

    func testKeychainUserCredentials() {

        let items: [AuthCredentials] = [
            (username: "pippo", password: "pluto", business: false),
            (username: "john", password: "doe", business: false),
            (username: "frizz", password: "lazz", business: false),
            (username: "rick", password: "morty", business: false),
            (username: "crick", password: "crock", business: false),
            (username: "pippo", password: "pluto", business: true),
        ]
        let credentials = items.randomElement()!
        User.saveCredentials(credentials)

        let storedCredentialsFor = User.storedCredentials(for: credentials.username, business: credentials.business)
        XCTAssertNotNil(storedCredentialsFor)
        XCTAssertEqual(storedCredentialsFor?.username, credentials.username)
        XCTAssertEqual(storedCredentialsFor?.password, credentials.password)
        XCTAssertEqual(storedCredentialsFor?.business, credentials.business)

        User.removedStoredCredentials()
        let storedCredentialsCheck = User.storedCredentials(for: credentials.username, business: credentials.business)
        XCTAssertNil(storedCredentialsCheck)
    }

    func testLessThanTwentyFourHoursUntilCheckIn() {

        var dateComponents = DateComponents()

        dateComponents.hour = 13
        dateComponents.day = 11
        dateComponents.month = 1
        dateComponents.year = 2000

        guard let testArrivalDate = Calendar.current.date(from: dateComponents) else { return }

        dateComponents.hour = 15
        dateComponents.day = 10

        guard let testDateOne = Calendar.current.date(from: dateComponents) else { return }

        XCTAssertTrue(testDateOne.isLessThanTwentyFourHoursUntilCheckin(on: testArrivalDate))

        dateComponents.hour = 10

        guard let testDateTwo = Calendar.current.date(from: dateComponents) else { return }

        XCTAssertFalse(testDateTwo.isLessThanTwentyFourHoursUntilCheckin(on: testArrivalDate))

        dateComponents.hour = 12
        dateComponents.day = 11

        guard let testDateThree = Calendar.current.date(from: dateComponents) else { return }

        XCTAssertTrue(testDateThree.isLessThanTwentyFourHoursUntilCheckin(on: testArrivalDate))
    }

    func do_testTripAdvisorRatingsAndImages(with rating: Double, expectedDescription: String, expectedImageLiteral: String) {

        let tripAdvisorDetails = TripAdvisorDetails.init(dictionary: [
            "rating": rating,
            "numberOfReviews": 120120
        ])

        XCTAssertEqual(expectedDescription, tripAdvisorDetails.ratingDescription, String.init(format: "on ratingDescription with rating %f", rating))
        XCTAssertEqual(UIImage(imageLiteralResourceName: expectedImageLiteral), TripAdvisorDetails.image(with: tripAdvisorDetails.rating), String.init(format: "on imageLiteral with rating %f", rating))
    }

    func testTripAdvisorRatingsAndImages() {

        do_testTripAdvisorRatingsAndImages(with: 0.0, expectedDescription: "Terrible", expectedImageLiteral: "tripScore0")
        do_testTripAdvisorRatingsAndImages(with: 0.1, expectedDescription: "Terrible", expectedImageLiteral: "tripScore0-5")
        do_testTripAdvisorRatingsAndImages(with: 0.5, expectedDescription: "Terrible", expectedImageLiteral: "tripScore0-5")
        do_testTripAdvisorRatingsAndImages(with: 1.0, expectedDescription: "Poor", expectedImageLiteral: "tripScore1")
        do_testTripAdvisorRatingsAndImages(with: 1.5, expectedDescription: "Poor", expectedImageLiteral: "tripScore1-5")
        do_testTripAdvisorRatingsAndImages(with: 2.0, expectedDescription: "Average", expectedImageLiteral: "tripScore2")
        do_testTripAdvisorRatingsAndImages(with: 2.5, expectedDescription: "Average", expectedImageLiteral: "tripScore2-5")
        do_testTripAdvisorRatingsAndImages(with: 3.0, expectedDescription: "Very good", expectedImageLiteral: "tripScore3")
        do_testTripAdvisorRatingsAndImages(with: 3.5, expectedDescription: "Very good", expectedImageLiteral: "tripScore3-5")
        do_testTripAdvisorRatingsAndImages(with: 4.0, expectedDescription: "Excellent", expectedImageLiteral: "tripScore4")
        do_testTripAdvisorRatingsAndImages(with: 4.5, expectedDescription: "Excellent", expectedImageLiteral: "tripScore4-5")
        do_testTripAdvisorRatingsAndImages(with: 5.0, expectedDescription: "Excellent", expectedImageLiteral: "tripScore5")
    }

    func testDateIgnoringTime() {

        let date = Date()

        XCTAssertFalse(date == date.ignoringTime)

        let date2 = Date()
        var dateComponents = Calendar.current.dateComponents([.year, .month, .day, .hour], from: date2)
        dateComponents.hour = 3

        XCTAssertTrue(date.ignoringTime == Calendar.current.date(from: dateComponents)?.ignoringTime)
    }
}

class FirebaseContentTests: XCTestCase {

    var ratesContent: [RateInformation]? {
        
        let ratesContentDictionary: [PIDictionary] = [
            [
                "rateClassification": "A",
                "rateOrder": "5",
                "rateName": "Flex",
                "rateDescription": "Zahlen Sie bei Ankunft. Änderbar oder stornierbar bis 18 Uhr am Anreisetag.",
                "rateNotes": "<p>test</p>\n",
                "brand": "PID"
            ],
            [
                "rateClassification": "FLEXRATE",
                "rateOrder": "5",
                "rateName": "Flex",
                "rateDescription": "Zahlen Sie bei Ankunft. Änderbar oder stornierbar bis 18 Uhr am Anreisetag.",
                "rateNotes": "<p>Kostenlose Stornierung und Änderung bis 18 Uhr am Anreisetag.</p>\n",
                "brand": "PID"
            ]
        ]

        guard let ratesContentData = try? JSONSerialization.data(withJSONObject: ratesContentDictionary, options: .prettyPrinted) else { return nil }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode([RateInformation].self, from: ratesContentData)
        } catch {
            return nil
        }
    }

    override class func setUp() {
        super.setUp()
    }

    override class func tearDown() {
        super.tearDown()
    }

    func doTestRateName(with remoteConfigRatesDictionary: [PIDictionary], rate: Rate, for brand: HotelBrand, expecting nameWithBrand: String) {

        let remoteConfig = MockRemoteConfig(rateContent: remoteConfigRatesDictionary)

        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssert(rate.name(with: brand) == nameWithBrand)
    }

    func testRateNames() {

        let remoteConfigOverrideFNameRatesDictionary = [
            [
                "classification": "F",
                "name": "Saver"
            ]
        ]
        let fRate = Rate(dictionary: ["name": "Advance", "classification": "F"])
        let aRate = Rate(dictionary: ["name": "Flex", "classification": "A"])

        doTestRateName(with: remoteConfigOverrideFNameRatesDictionary, rate: fRate, for: .premierInn, expecting: "Saver")
        doTestRateName(with: remoteConfigOverrideFNameRatesDictionary, rate: aRate, for: .premierInn, expecting: "Flex")
        doTestRateName(with: remoteConfigOverrideFNameRatesDictionary, rate: fRate, for: .hub, expecting: "Saver")
        doTestRateName(with: remoteConfigOverrideFNameRatesDictionary, rate: aRate, for: .hub, expecting: "Flex")
        doTestRateName(with: remoteConfigOverrideFNameRatesDictionary, rate: fRate, for: .zip, expecting: "Saver")
        doTestRateName(with: remoteConfigOverrideFNameRatesDictionary, rate: aRate, for: .zip, expecting: "Flex")

        let remoteConfigNoOverrideRatesDictionary: [PIDictionary] = []

        doTestRateName(with: remoteConfigNoOverrideRatesDictionary, rate: fRate, for: .premierInn, expecting: "Advance")
        doTestRateName(with: remoteConfigNoOverrideRatesDictionary, rate: aRate, for: .premierInn, expecting: "Flex")
        doTestRateName(with: remoteConfigNoOverrideRatesDictionary, rate: fRate, for: .hub, expecting: "Advance")
        doTestRateName(with: remoteConfigNoOverrideRatesDictionary, rate: aRate, for: .hub, expecting: "Flex")
        doTestRateName(with: remoteConfigNoOverrideRatesDictionary, rate: fRate, for: .zip, expecting: "Advance")
        doTestRateName(with: remoteConfigNoOverrideRatesDictionary, rate: aRate, for: .zip, expecting: "Flex")
    }

    func doTestRateTermsMessage(with remoteConfigRatesDictionary: [PIDictionary], rate: Rate, expecting message: String) {

        let remoteConfig = MockRemoteConfig(rateContent: remoteConfigRatesDictionary)

        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertEqual(rate.bookingTermsMessage(with: BookingDetails.sharedInstance.hotel?.brand), message)
    }

    func doTestRateDescription(with remoteConfigRatesDictionary: [PIDictionary], and ratesContent: [RateInformation]? = [], rate: Rate, for brand: HotelBrand, expecting description: String) {

        let remoteConfig = MockRemoteConfig(rateContent: remoteConfigRatesDictionary)

        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        SettingsManager.sharedInstance.ratesContent = ratesContent

        XCTAssert(rate.longDescription(with: brand) == description)
    }

    func testRateDescriptions() {

        let remoteConfigOverrideFDescriptionRatesDictionary = [
            [
                "classification": "F",
                "description": "The good old saver rate"
            ]
        ]
        let fRate = Rate(dictionary: ["name": "Advance", "classification": "F", "description": "Cancel 28 days before"])
        let aRate = Rate(dictionary: ["name": "Flex", "classification": "A", "description": "cancel and amend up to 1pm on arrival day"])

        doTestRateDescription(with: remoteConfigOverrideFDescriptionRatesDictionary, rate: fRate, for: .premierInn, expecting: "The good old saver rate")
        doTestRateDescription(with: remoteConfigOverrideFDescriptionRatesDictionary, rate: aRate, for: .premierInn, expecting: "cancel and amend up to 1pm on arrival day")

        let remoteConfigNoOverrideRatesDictionary: [PIDictionary] = []

        doTestRateDescription(with: remoteConfigNoOverrideRatesDictionary, rate: fRate, for: .premierInn, expecting: "Cancel 28 days before")
        doTestRateDescription(with: remoteConfigNoOverrideRatesDictionary, rate: aRate, for: .premierInn, expecting: "cancel and amend up to 1pm on arrival day")

        let mockRatesContent = ratesContent

        doTestRateDescription(with: remoteConfigNoOverrideRatesDictionary, and: mockRatesContent, rate: aRate, for: .premierInnGermany, expecting: "Zahlen Sie bei Ankunft. Änderbar oder stornierbar bis 18 Uhr am Anreisetag.")
    }

    func testLocalSessionStorage() {

        let storageManager = SimpleStorageManager<CheckInSession>.init(dataSource: UserDefaults.standard)

        func deleteAllSessions() {
            storageManager.items.forEach {
                _ = storageManager.remove($0)
            }
        }

        deleteAllSessions()

        let session1 = CheckInSession(confirmationNumber: "1234343", sessionId: "session", date: Date())
        try? storageManager.add(session1)

        LocalReservationManager.shared.cleanupLocalCheckInSessions()

        XCTAssert(storageManager.items.count == 1)

        if let pastDate = Date().dateByAddingUnit(unitType: .day, number: -2) {
            let session2 = CheckInSession(confirmationNumber: "7487483", sessionId: "sesh", date: pastDate)
            try? storageManager.add(session2)
        }

        XCTAssert(storageManager.items.count == 2)

        LocalReservationManager.shared.cleanupLocalCheckInSessions()

        XCTAssert(storageManager.items.count == 1)
    }

    // MARK: Confirmation/basket polling messages config

    func testConfirmationPollingMessagesConfig() {

        var confirmationPollingMessagesConfig = PIDictionary()
        confirmationPollingMessagesConfig = [
            "messages": [
                [
                    "order": 2,
                    "seconds": 30,
                    "message": "last"
                ],
                [
                    "order": 0,
                    "seconds": 10,
                    "message": "first"
                ],
                [
                    "order": 1,
                    "seconds": 20,
                    "message": "second"
                ]
            ]
        ]
        let remoteConfig = MockRemoteConfig(confirmationPollingMessagesConfig: confirmationPollingMessagesConfig)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertEqual(SettingsManager.sharedInstance.confirmationPollingMessagesConfig.maxDurationOfPolling, 60, "combined duration of messages")
        XCTAssertEqual(SettingsManager.sharedInstance.confirmationPollingMessagesConfig.messages.count, 3, "number of messages")
        XCTAssertEqual(SettingsManager.sharedInstance.confirmationPollingMessagesConfig.messages.first?.message, "first", "first message")
        XCTAssertEqual(SettingsManager.sharedInstance.confirmationPollingMessagesConfig.messages[safe: 1]?.message, "second", "second message")
        XCTAssertEqual(SettingsManager.sharedInstance.confirmationPollingMessagesConfig.messages.last?.message, "last", "last message")
    }

    func testNoConfirmationPollingMessagesConfig() {

        // test empty
        let remoteConfig = MockRemoteConfig(confirmationPollingMessagesConfig: nil)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertEqual(SettingsManager.sharedInstance.confirmationPollingMessagesConfig.maxDurationOfPolling, 0, "combined duration of messages")
        XCTAssertEqual(SettingsManager.sharedInstance.confirmationPollingMessagesConfig.messages.count, 0, "number of messages")
        XCTAssertEqual(SettingsManager.sharedInstance.confirmationPollingMessagesConfig.messages.first?.message, nil, "no messages")
    }
}



class AnalyticsTimeTest: XCTestCase {
    var analytics: AnalyticsManager!

    override func setUp() {
        super.setUp()
        analytics = AnalyticsManager()
    }


    func testTimeBeingSentProperties() {
        let state = analytics.analyticsProperties(stateType: "mock-state")

        let time = state["analyticsData.all.time"] as? String
        let date = DateFormatter.analyticsTimeFormatter.date(from: time!)
        XCTAssertNotNil(date)
    }
}
