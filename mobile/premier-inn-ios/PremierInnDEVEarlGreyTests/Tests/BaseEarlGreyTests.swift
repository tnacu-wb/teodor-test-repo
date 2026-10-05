//
//  BaseEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 17/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte
import SimpleNetwork

@testable import PremierInn

class BaseEarlGreyTests: XCTestCase {

    // MARK: - App Pages

    let homePage = HomePage()
    let loginPage = LoginPage()
    let myAccountPage = MyAccountPage()
    let searchResultsPage = SearchResultsPage()
    let locationSuggestionsPage = LocationSuggestionsPage()
    let hotelDetailsPage = HotelDetailsPage()
    let upsellsPage = UpsellsPage()
    let personalDetailsPage = PersonalDetailsPage()
    let cardDetailsPage = CardDetailsPage()
    let reviewAndBookPage = ReviewAndBookPage()
    let addressLookupPage = AddressLookupPage()
    let bookingConfirmationPage = BookingConfirmationPage()
    let businessCardQuestionsPage = BusinessCardQuestionsPage()
    let checkInOnlinePage = CheckInOnlinePage()
    let resetPasswordPage = ResetPasswordPage()
    let paymentDetailsPage = PaymentDetailsPage()
    let manageBookingPage = AmendBookingPage()
    let paymentMethodsPage = PaymentMethodsPage()

    // MARK: - Boilerplate

    override func setUp() {

        super.setUp()

        Biometrics.unenrolled()
        
        setupMockAPI()

        let appDelegate = UIApplication.shared.delegate as? AppDelegate
        appDelegate?.resetApplicationForTesting()

        GREYCondition(name: "Wait for main root view controller") {
            return true
        }.wait(withTimeout: 3)

        dismissBanners()
    }

    override func tearDown() {

        stopMockAPI()

        super.tearDown()
    }

    func setupMockAPI() {

        // subclasses may override if needed
    }

    func stopMockAPI() {

        if Hippolyte.shared.isStarted {
            Hippolyte.shared.stop()
        }
    }

    func loadMock(with config: MockConfig) {

        Hippolyte.shared.pause()

        if let config = config as? LoginMock, config.username != "selfwhitbread@mailinator.com" {
            NetworkMockManager.stubLoginRequest(method: config.method, urlString: config.url, jsonName: config.jsonFileName, username: config.username, password: config.password)
        } else {
            if config.urlType == .pattern {
                NetworkMockManager.addStubRequest(method: config.method, urlStringPattern: config.url, jsonName: config.jsonFileName)
            } else {
                NetworkMockManager.addStubRequest(method: config.method, urlString: config.url, jsonName: config.jsonFileName, statusCode: config.statusCode)
            }
        }

        Hippolyte.shared.resume()
    }

    // MARK: - Predefined User Paths

    func dismissBanners() {

        homePage.hideBanner()
        homePage.select(environment: .alpha2)
    }

    func givenIHaveLoggedInAndViewMyBookings() {

        loginPage.performLogin(username: "ntwisp@me.com", password: "Password1")
        MyAccountPage.goToMyBookings()
    }

    func givenIHaveSelectedAFlexRateAndPremierInnBreakfastAtLiverpoolMoorfield(using cardNumber: String = "4444333322221111") {

        homePage.goToLocationSelection()
        locationSuggestionsPage.searchLocation(text: "Liverpool")
        locationSuggestionsPage.selectSuggestion(text: "Liverpool, UK") {

            homePage.tapSearch()

            // check SRP elements
            searchResultsPage.checkPageElementsExist()
            searchResultsPage.tapHotelInList(hotelName: "Liverpool City Centre (Moorfields)")

            hotelDetailsPage.tapOnFlexRate()

            UpsellsPage.addBreakfast()
            UpsellsPage.tapContinue()

            personalDetailsPage.enterTestPersonalDetails()
            personalDetailsPage.tapContinue()

            cardDetailsPage.enterTestData(for: cardNumber)
            cardDetailsPage.tapContinue()

            reviewAndBookPage.waitToLoad()
        }
    }

    func givenIHaveSearchedForHotelsInLiverpoolAndSelectedLiverpoolMoorfields() {

        homePage.goToLocationSelection()
        locationSuggestionsPage.searchLocation(text: "Liverpool")
        locationSuggestionsPage.selectSuggestion(text: "Liverpool, UK") {

            homePage.tapSearch()

            // check SRP elements
            searchResultsPage.checkPageElementsExist()
            searchResultsPage.tapHotelInList(hotelName: "Liverpool City Centre (Moorfields)")
        }
    }

    // TODO: extend to be able to select room criteria
    func givenIHaveSelectedAccessibleRoomCriteria() {

        homePage.goToRoomCriteria()
        RoomCriteriaPage.changeDoubleRoomToAccessible()
        RoomCriteriaPage.tapDone()
    }

    func givenIHaveSelectedASemiFlexRateAtHubKX(using cardNumber: String = "4444333322221111") {

        homePage.goToLocationSelection()
        locationSuggestionsPage.searchLocation(text: "hub kin")
        locationSuggestionsPage.selectSuggestion(text: "hub London Kings Cross") {

            homePage.tapSearch()

            // check SRP elements

            hotelDetailsPage.tapOnSemiFlexRate()

            // UpsellsPage.addBreakfast()
            UpsellsPage.tapContinue()

            personalDetailsPage.enterTestPersonalDetails()
            personalDetailsPage.tapContinue()

            cardDetailsPage.enterTestData(for: cardNumber)
            cardDetailsPage.tapContinue()

            reviewAndBookPage.waitToLoad()
        }
    }

    func givenIhaveLoggedInSelectedAFlexRateWifiAndBookingForSomeoneElse() {

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performLogin(username: "ntwisp@me.com", password: "Password1")
        MyAccountPage.goToHome()

        homePage.goToLocationSelection()
        locationSuggestionsPage.searchLocation(text: "Liverpool")
        locationSuggestionsPage.selectSuggestion(text: "Liverpool, UK") {

            homePage.tapSearch()

            // check SRP elements
            searchResultsPage.checkPageElementsExist()
            searchResultsPage.tapHotelInList(hotelName: "Liverpool City Centre (Moorfields)")

            hotelDetailsPage.tapOnFlexRate()

            UpsellsPage.addWifi()
            UpsellsPage.tapContinue()

            personalDetailsPage.toggleBookerNotStayer()
            personalDetailsPage.enterTestGuestDetails()
            personalDetailsPage.tapContinue()

            cardDetailsPage.enterTestData()
            cardDetailsPage.tapContinue()

            reviewAndBookPage.waitToLoad()
        }
    }

    func givenIhaveSelectedAFlexRateWifiAndBookingForSomeoneElse() {

        homePage.goToLocationSelection()
        locationSuggestionsPage.searchLocation(text: "Liverpool")
        locationSuggestionsPage.selectSuggestion(text: "Liverpool, UK") {

            homePage.tapSearch()

            // check SRP elements
            searchResultsPage.checkPageElementsExist()
            searchResultsPage.tapHotelInList(hotelName: "Liverpool City Centre (Moorfields)")

            hotelDetailsPage.tapOnFlexRate()

            UpsellsPage.addWifi()
            UpsellsPage.tapContinue()

            personalDetailsPage.enterTestPersonalDetails()
            personalDetailsPage.toggleBookerNotStayer()
            personalDetailsPage.enterTestGuestDetails()
            personalDetailsPage.tapContinue()

            cardDetailsPage.enterTestData()
            cardDetailsPage.tapContinue()

            reviewAndBookPage.waitToLoad()
        }
    }

    func givenIHaveLoggedInToBACAccountSelectedFlex() {

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.performLogin(username: "ddollars@me.com", password: "Password1")
        MyAccountPage.goToHome()

        homePage.goToLocationSelection()
        locationSuggestionsPage.searchLocation(text: "Liverpool")
        locationSuggestionsPage.selectSuggestion(text: "Liverpool, UK") {

            homePage.tapSearch()

            // check SRP elements
            searchResultsPage.checkPageElementsExist()
            searchResultsPage.tapHotelInList(hotelName: "Liverpool City Centre (Moorfields)")

            hotelDetailsPage.tapOnFlexRate()

            UpsellsPage.tapContinue()

            personalDetailsPage.tapContinue()

            businessCardQuestionsPage.inputTestDetails()
            businessCardQuestionsPage.tapContinue()

            reviewAndBookPage.waitToLoad()
        }
    }

    // MARK: - Check Methods

    func checkScrolling(down: Int = 50, _ tableIdentifier: String, forLabel title: String) {

        GREYCondition(name: "scroll until element is visible") {

            var error: NSError?

            EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel(title), grey_interactable()]))
                .usingSearch(grey_scrollInDirection(.down, CGFloat(down)), onElementWith: grey_accessibilityID(tableIdentifier))
                .assert(grey_sufficientlyVisible(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    func checkScrolling(down: Int = 50, _ tableIdentifier: String, forElement element: GREYInteraction) {

        GREYCondition(name: "scroll until element is visible") {

            var error: NSError?

            element.usingSearch(grey_scrollInDirection(.down, CGFloat(down)), onElementWith: grey_accessibilityID(tableIdentifier))
                .assert(grey_sufficientlyVisible(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }
}

// MARK: - Mock Configs

enum MockConfigUrlType {
    case absolute
    case pattern
}

protocol MockConfig {
    var url: String { get }
    var urlType: MockConfigUrlType { get }
    var jsonFileName: String { get }
    var method: HTTPMethod { get }
    var statusCode: Int { get }
}

extension MockConfig {
    var urlType: MockConfigUrlType {
        return .pattern
    }
    var method: HTTPMethod {
        return .GET
    }
    var statusCode: Int {
        return 200
    }
}

enum ResetPasswordMock: MockConfig {
    case successTrue
    case successFalse

    var url: String {
        return "https://api-uat.whitbread.co.uk/auth/hotels/forgot-password"
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .successTrue:
            return "resetPasswordSuccessTrue"
        case .successFalse:
            return "resetPasswordSuccessFalse"
        }
    }
    var method: HTTPMethod {
        return .POST
    }
}

enum LoginMock: MockConfig {
    case nickJones
    case dickDollars
    case selfWhitbreadBB

    var url: String {
        switch self {
        case .selfWhitbreadBB:
            return "https://api-uat.whitbread.co.uk/auth/hotels/login?business=true"
        default:
            return "https://wbodetest.eu.auth0.com/oauth/token"
        }
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .selfWhitbreadBB:
            return "successfulLoginResponseNotAuth0"
        default:
            return "successfulLoginResponse"
        }
    }
    var method: HTTPMethod {
        return .POST
    }
    var username: String {
        switch self {
        case .nickJones:
            return "ntwisp@me.com"
        case .dickDollars:
            return "ddollars@me.com"
        case .selfWhitbreadBB:
            return "selfwhitbread@mailinator.com"
        }
    }
    var password: String {
        switch self {
        case .selfWhitbreadBB:
            return "Hello123"
        default:
            return "Password1"
        }
    }
}

enum GetUserMock: MockConfig {
    case nickJones
    case userWithStoredBAC
    case selfWhitbreadBB
    case stayerBB
    case managerBB
    case managerBBNoPersonalCard

    var url: String {
        switch self {
        case .nickJones:
            return "https://api-uat.whitbread.co.uk/customers/hotels/ntwisp@me.com?business=false"
        case .userWithStoredBAC:
            return "https://api-uat.whitbread.co.uk/customers/hotels/ddollars@me.com?business=false"
        case .selfWhitbreadBB, .stayerBB, .managerBB, .managerBBNoPersonalCard:
            return "https://api-uat.whitbread.co.uk/customers/hotels/selfwhitbread@mailinator.com?business=true"
        }
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .nickJones:
            return "getUserNickJonesResponse"
        case .userWithStoredBAC:
            return "getUserDickDollars"
        case .selfWhitbreadBB:
            return "getUserSelfBookerBB"
        case .stayerBB:
            return "getUserStayerBB"
        case .managerBB:
            return "getUserManagerBB"
        case .managerBBNoPersonalCard:
            return "getUserManagerBBNoPersonalCard"
        }
    }
    var method: HTTPMethod {
        return .GET
    }
}

enum GetCompanyMock: MockConfig {
    case company2891
    case company2891NoCards
    case company2891NoCentralBACs
    case company2891NoBACInCSCAllowPersonal
    case company2891NoUpsellsAllowed

    var url: String {
        return "https://api-uat.whitbread.co.uk/company/2891"
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .company2891:
            return "company2891"
        case .company2891NoCards:
            return "company2891NoCards"
        case .company2891NoCentralBACs:
            return "company2891NoBACInCSC"
        case .company2891NoBACInCSCAllowPersonal:
            return "company2891NoBACInCSCAllowPersonal"
        case .company2891NoUpsellsAllowed:
            return "company2891NoUpsellAllowances"
        }
    }
    var method: HTTPMethod {
        return .GET
    }
}

enum MarketingPreferencesMock: MockConfig {
    case piOptIn
    case piOptOut

    var url: String {
        return "https://api-uat.whitbread.co.uk/marketing/hotels/newsletter/get"
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .piOptIn:
            return "marketingOptInTrue"
        case .piOptOut:
            return "marketingOptInFalse"
        }
    }
    var method: HTTPMethod {
        return .POST
    }
}

enum SuggestionMock: MockConfig {
    case liverpool
    case hubKingsCross

    var url: String {
        return "https://api-uat\\.whitbread\\.co\\.uk/autocomplete(.*)"
    }
    var jsonFileName: String {
        switch self {
        case .liverpool:
            return "liverpoolSuggestionsResponse"
        case .hubKingsCross:
            return "hubKXSuggestionsResponse"
        }
    }
}

enum PlacesLookupMock: MockConfig {
    case liverpool
    case hubKingsCross

    var url: String {
        return "https://maps\\.googleapis\\.com/maps/api/place/details/json(.*)"
    }
    var jsonFileName: String {
        switch self {
        case .liverpool, .hubKingsCross:
            return "liverpoolGooglePlacesResponse"
        }
    }
}

enum AvailabilitiesMock: MockConfig {
    case liverpool

    var url: String {
        return "https://api-uat\\.whitbread\\.co\\.uk/snowdrop/search/hotels/availabilities(.*)"
    }
    var jsonFileName: String {
        switch self {
        case .liverpool:
            return "liverpoolAvailabilitiesResponse"
        }
    }
}

enum AvailabilityMock: MockConfig {
    case liverpool
    case liverpoolExpensive
    case hubKingsCross
    case standardRooms
    case premierPlusRooms
    case businessRooms
    case unknownRooms
    case mixedRooms
    case accessibleRoom1
    case accessibleRooms2
    case accessibleRoomsMixed
    case accessibleRoomsMixedLimited
    case accessibleRoomsMixedExact
    case accessibleRoomsUnknown
    case liverpoolBB
    case liverpoolBBPrepaymentRequired
    case liverpoolBBPoAOnlyPremierInnBreak

    var url: String {
        return "https://api-uat\\.whitbread\\.co\\.uk/booking/hotels/(.*)/availability(.*)"
    }
    var jsonFileName: String {
        switch self {
        case .liverpool:
            return "availabilityResponse"
        case .liverpoolExpensive:
            return "availabilityResponsePriceChange"
        case .hubKingsCross:
            return "availabilityResponseHubKX"
        case .standardRooms:
            return "availabilityV2_rooms2_standard"
        case .premierPlusRooms:
            return "availabilityV2_rooms2_premierplus"
        case .businessRooms:
            return "availabilityV2_rooms2_business"
        case .unknownRooms:
            return "availabilityV2_rooms2_unknown"
        case .mixedRooms:
            return "availabilityV2_rooms3_mixed"
        case .accessibleRoom1:
            return "availabilityV2_accessible_room1"
        case .accessibleRooms2:
            return "availabilityV2_accessible_rooms2"
        case .accessibleRoomsMixed:
            return "availabilityV2_accessible_rooms3_mixed"
        case .accessibleRoomsMixedLimited:
            return "availabilityV2_accessible_rooms3_mixed_limited"
        case .accessibleRoomsMixedExact:
            return "availabilityV2_accessible_rooms3_mixed_exact"
        case .accessibleRoomsUnknown:
            return "availabilityV2_accessible_rooms3_unknown"
        case .liverpoolBB:
            return "availabilityResponseBBPrepaymentNotRequired"
        case .liverpoolBBPrepaymentRequired:
            return "availabilityResponseBBPrepaymentRequired"
        case .liverpoolBBPoAOnlyPremierInnBreak:
            return "availabilityResponseBBPrepaymentNotRequiredOnlyPIBreakfast"
        }
    }
}

enum HotelInfoMock: MockConfig {
    case liverpool
    case hubKingsCross
    case victoria
    case hotelsWithCardAuthenticationRequired

    var url: String {
        switch self {
        case .liverpool:
            return "https://api-uat.whitbread.co.uk/hotels/LIVCIT"
        case .hubKingsCross:
            return "https://api-uat.whitbread.co.uk/hotels/LONKIN"
        case .victoria:
            return "https://api-uat.whitbread.co.uk/hotels/LKEBAR"
        case .hotelsWithCardAuthenticationRequired:
            return "https://api-uat.whitbread.co.uk/hotels/LKEBAR"
        }
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .liverpool:
            return "liverpoolHotelInfo"
        case .hubKingsCross:
            return "hubKXHotelInfo"
        case .victoria:
            return "londonVictoria"
        case .hotelsWithCardAuthenticationRequired:
            return "hotelsWithCardAuthenticationRequired"
        }
    }
}

enum HotelReviewsMock: MockConfig {
    case liverpool
    case hubKingsCross

    var url: String {
        return "https://api-uat.whitbread.co.uk/reviews/*"
    }
    var jsonFileName: String {
        return "reviewResponse"
    }
}

enum HoldBookingMock: MockConfig {
    case success

    var url: String {
        return "https://api-uat.whitbread.co.uk/booking/hotels"
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        return "holdBookingResponse"
    }
    var method: HTTPMethod {
        return .POST
    }
}

enum CardValidationMock: MockConfig {
    case visaCredit
    case visaDebit

    var url: String {
        switch self {
        case .visaCredit:
            return "https://api-uat\\.whitbread\\.co\\.uk/payment/validations/444433(.*)"
        case .visaDebit:
            return "https://api-uat\\.whitbread\\.co\\.uk/payment/validations/458262(.*)"
        }
    }
    var jsonFileName: String {
        switch self {
        case .visaCredit:
            return "visaCreditResponse"
        case .visaDebit:
            return "visaDebitResponse"
        }
    }
}

enum BookingMock: MockConfig {
    case payOnArrival
    case payNow
    case timeout

    var url: String {
        return "https://api-uat.whitbread.co.uk/booking/hotels/*"
    }

    var jsonFileName: String {
        switch self {
        case .payOnArrival:
            return "makeBookingResponse"
        case .payNow:
            return "makeBookingResponsePaid"
        case .timeout:
            return "makeBookingResponseTimeout"
        }
    }
    var method: HTTPMethod {
        return .POST
    }
}

enum PaymentMock: MockConfig {
    case successNo3DS
    case success3DS
    case fail
    case poaFallback
    case timeout

    var url: String {
        return "https://api-uat.whitbread.co.uk/payment/hotels"
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .successNo3DS:
            return "makePaymentResponse"
        case .success3DS:
            /* This works by using the success url as the opening url, meaning the success page is loaded straight away and we don't try and do any UI tests on the 3DS web view 😬 */
            return "makePaymentResponse3DS"
        case .fail:
            return "makePaymentResponseFail"
        case .poaFallback:
            return "makePaymentResponse"
        case .timeout:
            return "makePaymentResponseFailTimeout"
        }
    }
    var method: HTTPMethod {
        return .POST
    }
    var statusCode: Int {
        switch self {
        case .timeout:
            return 500
        default:
            return 200
        }
    }
}

enum ReservationsMock: MockConfig {
    case checkInOnlineDisabled
    case checkInOnlineEnabled
    case checkInOnlineEnabledButCheckedIn
    case nickJones
    case nickJonesOnlyVictoria
    case staysCiolBooking
    case staysWithCardAuthenticationRequired

    var url: String {
        return "https://api-uat\\.whitbread\\.co\\.uk/customers/hotels/(.*)/stays"
    }
    var jsonFileName: String {
        switch self {
        case .checkInOnlineDisabled:
            return "staysCIOLDisabledResponse"
        case .checkInOnlineEnabled:
            return "staysCIOLEnabledResponse"
        case .checkInOnlineEnabledButCheckedIn:
            return "staysCIOLEnabledButCheckedInResponse"
        case .nickJones:
            return "getStaysNickJones"
        case .nickJonesOnlyVictoria:
            return "getStaysNickJonesOnlyVictoria"
        case .staysCiolBooking:
            return "staysCiolBookingPaymentNotRequired"
        case .staysWithCardAuthenticationRequired:
            return "staysWithCardAuthenticationRequired"
        }

    }
}

enum ReservationMock: MockConfig {
    case ciolBooking
    case jonesVictoria
    case jonesVictoriaCheckedIn
    case jonesVictoriaNotAmendable
    case jonesVictoriaAmendNotRestricted
    case jonesVictoriaAmendRestricted
    case reservationsWithCardAuthenticationRequired
    case reservationsWithCardAuthenticationRequiredWithBAC

    var url: String {
        switch self {
        case .jonesVictoria, .jonesVictoriaNotAmendable, .jonesVictoriaAmendNotRestricted, .jonesVictoriaAmendRestricted, .reservationsWithCardAuthenticationRequired, .reservationsWithCardAuthenticationRequiredWithBAC:
            return "https://api-uat.whitbread.co.uk/reservation/hotels/AYRR237176?arrival=2025-11-10&surname=TWISPALITE"
        case .ciolBooking, .jonesVictoriaCheckedIn:
            return "https://api-uat.whitbread.co.uk/reservation/hotels/AYRR237177?arrival=2025-11-10&surname=TWISPALITE"
        }
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .jonesVictoria, .ciolBooking:
            return "londonVictoriaPayOnArrival"
        case .jonesVictoriaCheckedIn:
            return "londonVictoriaCheckedIn"
        case .jonesVictoriaNotAmendable:
            return "londonVictoriaPayOnArrivalNotAmendable"
        case .jonesVictoriaAmendNotRestricted:
            return "londonVictoriaPayOnArrivalAmends_NotRestricted"
        case .jonesVictoriaAmendRestricted:
            return "londonVictoriaPayOnArrivalAmends_Restricted"
        case .reservationsWithCardAuthenticationRequired:
            return "reservationsWithCardAuthenticationRequired"
        case .reservationsWithCardAuthenticationRequiredWithBAC:
            return "reservationsWithCardAuthenticationRequiredWithBAC"
        }

    }
}

enum StartCIOLMock: MockConfig {
    case paymentAllowedPaymentRequired
    case paymentAllowedPaymentNotRequired

    var url: String {
        return "https://api-uat.whitbread.co.uk/checkin/hotels"
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .paymentAllowedPaymentRequired:
            return "startCheckinPaymentAllowedPaymentRequired"
        case .paymentAllowedPaymentNotRequired:
            return "startCheckinPaymentAllowedPaymentNotRequired"
        }
    }
    var method: HTTPMethod {
        return .POST
    }
}

enum CleanCIOLSessionMock: MockConfig {
    case cleanSession

    var url: String {
        return "https://api-uat\\.whitbread\\.co\\.uk/checkin/hotels/(.*)"
    }
    var urlType: MockConfigUrlType {
        return .pattern
    }
    var jsonFileName: String {
        switch self {
        case .cleanSession:
            return "" // unused as it's a 204 No Content response
        }

    }
    var method: HTTPMethod {
        return .DELETE
    }
    var statusCode: Int {
        return 204
    }
}

enum CIOLGuestsMock: MockConfig {
    case guests

    var url: String {
        return "https://api-uat\\.whitbread\\.co\\.uk/checkin/hotels/(.*)/guests"
    }
    var urlType: MockConfigUrlType {
        return .pattern
    }
    var jsonFileName: String {
        switch self {
        case .guests:
            return "" // unused as it's a 204 No Content response
        }

    }
    var method: HTTPMethod {
        return .PUT
    }
    var statusCode: Int {
        return 204
    }
}

enum CIOLUpsellsMock: MockConfig {
    case upsells
    case upsellsPaymentRequired

    var url: String {
        return "https://api-uat\\.whitbread\\.co\\.uk/checkin/hotels/(.*)/upsells"
    }
    var urlType: MockConfigUrlType {
        return .pattern
    }
    var jsonFileName: String {
        switch self {
        case .upsells:
            return "ciolUpsellsResponse"
        case .upsellsPaymentRequired:
            return "ciolAddUpsellsSuccess"
        }

    }
    var method: HTTPMethod {
        return .PUT
    }
}

enum CIOLPaymentMock: MockConfig {
    case successNo3DS

    var url: String {
        return "https://api-uat.whitbread.co.uk/payment/checkin"
    }
    var urlType: MockConfigUrlType {
        return .absolute
    }
    var jsonFileName: String {
        switch self {
        case .successNo3DS:
            return "ciolPaymentSuccessNo3DS"
        }
    }
    var method: HTTPMethod {
        return .POST
    }
}
