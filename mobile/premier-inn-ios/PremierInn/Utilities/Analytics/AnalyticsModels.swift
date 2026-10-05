//
//  AnalyticsModels.swift
//  PremierInn
//
//  Created by Filippo Minelle on 07/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import FirebaseAnalytics

enum LoggedInAnalytic: String {
    case loggedIn = "Logged In"
    case notLoggedIn = "Logged Out"
}

enum CampaignId: String {
    case qrCode = "QRCode_KioskCheckIn"
}

protocol Trackable {
    var screenName: String { get }
    var trackScreen: Bool { get }
    var environment: String { get }
    var loggedIn: LoggedInAnalytic { get }
    var timeZone: String { get }
    var language: String { get }
    var screenType: String { get }
    var customParameters: [String: Any]? { get }

    func applicationDidTakeScreenshot()
}

struct CampaignAttribution {
    let fullURLString: String?
    let referrerURLString: String?
    let cid: String?
    let mckv: String?
    let etRid: String?
}

enum PIAnalytics {
    enum CustomUserInfoParameters {
        static let errorName = "PIAnalyticsErrorName"
    }

    enum HotelSource: String {
        case opera = "Opera"
        case bart = "Bart"
    }

    enum WalletType: String {
        case applePay = "AP"
        case googlePay = "GP"
    }

    enum PromoNames {
        static let appIncentive = "APP_INCENTIVE"
        static let freeBreakfast = "FREE_BREAKFAST"
    }

    enum Action {
        static let screenViewed = "analyticsDate.all.screenViewed"
        static let landingSearchBoxTap = "landingSearchBoxTap"
        static let landingNearMeButtonTap = "landingNearMeButtonTap"
        static let searchLocationFinderButtonTap = "searchLocationFinderButtonTap"
        static let searchBackButtonTap = "searchBackButtonTap"
        static let locationAuthorizationConfirmed = "locationAuthorizationConfirmed"
        static let locationAuthorizationDenied = "locationAuthorizationDenied"
        static let locationAuthSettingsGoButtonTap = "locationAuthorizationSettingsGoButtonTap"
        static let locationAuthSettingsCancelButtonTap = "locationAuthorizationSettingsCancelButtonTap"
        static let remoteSuggestionsInvalidSearch = "iOS: Invalid Search"
        static let screenshot = "iOS: Screenshot: "
        static let requestDidRetry = "requestDidRetry"
        static let error = "analyticsData.error.message"
        static let callUs = "Call us"
        static let resetPasswordSuccess = "iOS: Password Reset"
        static let passwordChanged = "iOS: Password Changed"
        static let accountCreated = "iOS: My Account Created"
        static let accountAmended = "iOS: My Account Amended"
        static let addCard = "iOS: Card Added"
        static let replaceCard = "iOS: Card Replaced"
        static let removeCard = "iOS: Card Removed"
        static let bookingPreferencesChanged = "iOS: Booking Preferences Changed"
        static let cancelledBooking = "iOS: Booking Cancelled"
        static let amendChange = "amend.change"
        static let updateUserDetails = "iOS: Personal details updated"
        static let cccPaymentBookingFailed = "iOS: Card Details: 3CP Payment Portal Validation"
        static let cccPaymentPaymentFailed = "iOS: Payment Decline"
        static let pushIdSet = "iOS: Push ID Set"
        static let amendComplete = "iOS: Amend Complete"
        static let qrCodeShown = "iOS: QR Code Shown"
        static let addToWalletTap = "iOS: Add to Wallet Button Tapped"
        static let addToWalletTapQRCode = "iOS: Add QR Code to Wallet Button Tapped"
        static let wifiConnected = "iOS: Wifi Connection Successful"
        static let wifiConnectionFailed = "iOS: Wifi Connection Failed"
        static var checkOut: String { "Leave easy: Confirmation".analyticsComponentsPrefixed }
        static var ciolDeRegCard: String { "CIOL: Reg Cards".analyticsComponentsPrefixed }
        static let ciolUpdateUdfc20Status = "CIOL: UDFC20 Status Update".analyticsComponentsPrefixed
        static var marketingSuppressionLoggedIn: String {
            "Marketing Consent GDP Suppression".analyticsComponentsPrefixed }
        static var marketingSuppressionAnonymous: String {
            "Marketing Consent Anonymous Suppression".analyticsComponentsPrefixed }
        static var addDigitalKeyDidTap: String {
            "Add Digital Key".analyticsComponentsPrefixed }
        static var showKeyInWallet: String { "Show Key in Apple Wallet" }
        static let hotelDetailsDiscountCodeBox = "Discount Code Box".analyticsComponentsPrefixed
        static let digitalKeyTraySelected: String = "Digital Key Tray Selected"
        static let promoBoxExpand = "analyticsData.search.promoBoxExpand"
        static let ciolBottomSheetActionName = "CIOL Bottom Sheet Tapped"
    }

    enum Keys {
        // Push Notifications
        static let pushType = "analyticsData.pushNotification.type"
        static let deeplinkUrl = "analyticsData.pushNotification.url"
        static let hotelCode = "analyticsData.pushNotification.hotelCode"
        static let hotelBrand = "analyticsData.pushNotification.hotelBrand"
        static let arrivalDate = "analyticsData.pushNotification.arrivalDate"
        static let nightsCount = "analyticsData.pushNotification.nights"
        static let locationTitle = "analyticsData.pushNotification.locationTitle"
        static let latitude = "analyticsData.pushNotification.lat"
        static let longitude = "analyticsData.pushNotification.long"

        // Generic
        static let productString = "&&products"
        static let eventsString = "&&events"
        static let screenshots = "analyticsData.screenshot"
        static let pushId = "analyticsData.pushID"
        static let sCampaign = "s.campaign"
        static let sFullURL = "s.g"
        static let sReferrer = "s.r"
        // All
        static let environment = "analyticsData.all.environment"
        static let userLogin = "analyticsData.all.userLogin"
        static let loginSuccess = "analyticsData.all.loginSuccess"
        static let timeZone = "analyticsData.all.timeZone"
        static let language = "analyticsData.all.language"
        static let screenType = "analyticsData.all.screenType"
        static let userID = "analyticsData.all.userID"
        static let companyID = "analyticsData.all.business.companyID"
        static let businessUserLevel = "analyticsData.all.business.userLevel"
        static let time = "analyticsData.all.time"
        static let popoverPromoCode = "analyticsData.all.promoCode"
        // Search
        static let searchLocation = "analyticsData.search.searchLocation"
        static let searchType = "analyticsData.search.searchType"
        static let sortType = "analyticsData.search.sort"
        static let nights = "analyticsData.search.nights"
        static let rooms = "analyticsData.search.rooms"
        static let checkIn = "analyticsData.search.check-in"
        static let checkOut = "analyticsData.search.check-out"
        static let adults = "analyticsData.search.adults"
        static let children = "analyticsData.search.children"
        static let guests = "analyticsData.search.guests"
        static let leadDays = "analyticsData.search.leadDays"
        static let roomType = "analyticsData.search.roomType"
        static let roomTypeNames = "analyticsData.search.roomTypeNames"
        static let numResults = "analyticsData.search.numResults"
        static let startEndDay = "analyticsData.search.startEndDay"
        static let startDay = "analyticsData.search.startDay"
        static let endDay = "analyticsData.search.endDay"
        static let hotelLabel = "analyticsData.search.hotelLabel"
        static let prodView = "analyticsData.search.event.prodView"
        static let fromPrice = "analyticsData.search.fromPrice"
        static let mapView = "analyticsData.search.mapView"
        static let listView = "analyticsData.search.listView"
        static let event1 = "analyticsData.search.event.event1"
        static let event2 = "analyticsData.search.event.event2"
        static let noAvailability = "analyticsData.search.event.noAvail"

        // All > Promotion analytics tracking keys
        static let promoCode = "analyticsData.all.promoCode"
        static let promoName = "analyticsData.all.promoName"
        static let promoRateTags = "analyticsData.all.rateTags"
        static let promoCodeApplied = "analyticsData.all.discountCodeApplied"
        static let promoBookingComplete = "analyticsData.all.promoBookingComplete"
        static let promoBoxVisible = "analyticsData.search.promoBoxVisible"
        static let promoBoxExpand = "analyticsData.search.promoBoxExpand"
        static let promoInputType = "analyticsData.search.promoInputType"

        static func getBookingSystem(index: Int) -> String {
            "analyticsData.analyticsDataSearchResult.searchResultsDisplayed[\(index)].bookingSystem"
        }

        // BookingFlow
        static let bfPrepay = "analyticsData.bf.prepay"
        static let bfNights = "analyticsData.bf.nights"
        static let bfRooms = "analyticsData.bf.rooms"
        static let bfCheckInDate = "analyticsData.bf.checkInDate"
        static let bfCheckOutDate = "analyticsData.bf.checkOutDate"
        static let bfCheckInDay = "analyticsData.bf.checkInDay"
        static let bfCheckOutDay = "analyticsData.bf.checkOutDay"
        static let bfCheckInOutDay = "analyticsData.bf.CheckinoutDay"
        static let bfAdults = "analyticsData.bf.adults"
        static let bfChildren = "analyticsData.bf.children"
        static let bfRateCode = "analyticsData.bf.rateCode"
        static let bfLettingType = "analyticsData.bf.lettingType"
        static let bfRateDescription = "analyticsData.bf.rateDescription"
        static let bfExtrasDescriptions = "analyticsData.bf.extrasShownDescription"
        static let bfExtrasShownCodes = "analyticsData.bf.extrasShownCode"
        static let bfExtrasSelectedDescriptions = "analyticsData.bf.extrasSelectedDescription"
        static let bfStickyExtrasCTA = "analyticsData.bf.stickyExtrasCTA"
        static let bfCardType = "analyticsData.bf.cardType"
        static let bfUserType = "analyticsData.bf.userType"
        static let bfUserDefinedQuestions = "analyticsData.bf.userDefinedQuestions"
        static let bfBusinessAccQuestions = "analyticsData.bf.businessAccQuestions"
        static let bfSavedCards = "analyticsData.bf.savedCards"
        static let bfPaymentCards = "analyticsData.bf.paymentCards"
        static let bfCardTypes = "analyticsData.bf.cardTypes"
        static let bfPaymentCardTypes = "analyticsData.bf.paymentCardTypes"
        static let bfExpiredCards = "analyticsData.bf.expiredCards"
        static let bfSuppressMarketingBox = "analyticsData.bf.marketingOptOutSuppressed"
        static let bfOptedInToMarketing = "analyticsData.bf.optInCustomer"
        // Payment
        static let errorMessage = "analyticsData.error.message"
        static let errorCode = "analyticsData.error.code"
        static let errorDetails = "analyticsData.error.details"
        static let hasPaymentFailure = "analyticsData.paymentFailure"

        // Opera Payment Error Tracking
        static let hasPaymentFailureOpera = "analyticsData.bf.paymentFailure"
        static let paymentErrorCode = "analyticsData.bf.paymentFailureReasonCode"
        static let paymentFailureReasonMessage = "analyticsData.bf.paymentFailureReasonMessage"

        // 3C iPage Launch
        static let cccTemplateId = "analyticsData.bf.paymentTemplateID"
        static let cccSessionId = "analyticsData.bf.paymentSessionID"
        static let cccCardSelected = "analyticsData.bf.paymentCardSelected"
        static let iPageloadTime = "analyticsData.bf.paymentLoadTime"
        static let cccPaymentTakenNow = "analyticsData.bf.paymentTakenNow"
        static let cccPaymentMethodType = "analyticsData.bf.paymentMethod"
        static let paymentOutage = "analyticsData.bf.paymentOutage"
        // Confirmation
        static let prepay = "analyticsData.conf.prepay"
        static let card = "analyticsData.conf.card"
        static let bookingID = "analyticsData.conf.bookingReference"
        static let bookingRoomDescription = "analyticsData.conf.bookingRoomDescription"
        static let bookingFlowCompleteTime = "analyticsData.conf.bookingFlowCompleteTime"
        static let roomTypes = "analyticsData.conf.roomTypes"
        static let lettingTypes = "analyticsData.conf.lettingTypes"
        static let rateCode = "analyticsData.conf.rateCode"
        static let rateName = "analyticsData.conf.rateName"
        static let customerType = "analyticsData.conf.customerType"
        static let addedExtras = "analyticsData.conf.addedExtras"
        static let upsellsRevenue = "analyticsData.conf.upsellsRevenue"
        static let eci = "analyticsData.conf.earlyCheckIn"
        static let lco = "analyticsData.conf.lateCheckOut"
        static let purchase = "analyticsData.conf.purchase"
        static let checkInConf = "analyticsData.conf.check-in"
        static let checkOutConf = "analyticsData.conf.check-out"
        static let leadDaysConf = "analyticsData.conf.leadDays"
        static let event37 = "analyticsData.conf.events.event37"
        static let event30 = "analyticsData.conf.events.event30"
        static let event20 = "analyticsData.conf.events.event20"
        static let event31 = "analyticsData.conf.events.event31"
        static let event54 = "analyticsData.conf.events.event54"
        static let event36 = "analyticsData.conf.events.event36"
        static let bacDinner = "analyticsData.conf.BAC.dinner"
        static let bacAlcohol = "analyticsData.conf.BAC.alcohol"
        static let bacDinnerBudget = "analyticsData.conf.BAC.dinnerBudget"
        static let bacParking = "analyticsData.conf.BAC.parking"
        static let bacWifi = "analyticsData.conf.BAC.ultimateWifi"
        static let goshAmount = "analyticsData.conf.goshAmount"
        static let bfacCountCreated = "analyticsData.conf.events.BFAccountCreated"
        static let newSavedCard = "analyticsData.conf.events.newSavedCard"
        static let appleWallet = "analyticsData.conf.appleWallet"
        static let confPromoCode = "analyticsData.conf.promoCode"
        static let confCccPaymentTakenNow = "analyticsData.conf.paymentTakenNow"
        static let confNights = "analyticsData.conf.nights"
        static let confRooms = "analyticsData.conf.rooms"
        static let confAdults = "analyticsData.conf.adults"
        static let confChildren = "analyticsData.conf.children"
        static let confGuests = "analyticsData.conf.guests"
        static let confHotelCode = "analyticsData.conf.hotelCode"
        static let roomNumber = "analyticsData.conf.roomNumber"
        static let confPromoBookingComplete = "analyticsData.conf.promoBookingComplete"

        // Booking Details
        static let bookingReference = "analyticsData.bd.bookingReference"

        // Register
        static let emailOptIn = "analyticsData.emailOptIn"
        // UserPreferences
        static let accountChanged = "analyticsData.accountChanged"
        static let adultPreference = "analyticsData.preferenceAdults"
        static let childPreference = "analyticsData.preferenceChildren"
        static let roomTypePreference = "analyticsData.preferenceRoomType"
        static let cotPreference = "analyticsData.preferenceCot"
        static let mealPreference = "analyticsData.preferenceMeals"
        // PaymentPreferences
        static let cardType = "analyticsData.cardType"
        // CID Campaigns
        static let mckv = "analyticsData.MCKV"
        static let etRid = "analyticsData.ET_RID"
        // Cancelled booking
        static let didCancel = "analyticsData.cancel"
        static let cancelBookingId = "analyticsData.cancel.bookingID"
        static let cancelNights = "analyticsData.cancel.nights"
        static let cancelRooms = "analyticsData.cancel.rooms"

        // CIOL
        static let checkInOnline = "analyticsData.ciol"
        static let checkInOnlineAction = "analyticsData.ciol.action"
        static let checkInOnlineBookingID = "analyticsData.ciol.bookingid"
        static let checkInOnlineHotelCode = "analyticsData.ciol.hotelCode"
        static let checkInOnlinePrepaid = "analyticsData.ciol.prepay"
        static let checkInOnlineCardType = "analyticsData.ciol.cardType"
        static let checkInOnlineNightsChange = "analyticsData.ciol.nightsChange"
        static let checkInOnlineRoomsChange = "analyticsData.ciol.roomsChange"
        static let checkInOnlineRoomTypeChange = "analyticsData.ciol.roomTypeChange"
        static let checkInOnlineExtrasBooked = "analyticsData.ciol.extrasBooked"
        static let checkInOnlineChange = "analyticsData.ciol.change"
        static let checkInOnlineFoodRevenueChange = "analyticsData.ciol.foodRevenueChange"
        static let checkInOnlineRoomRevenueChange = "analyticsData.ciol.roomRevenueChange"
        static let checkInOnlineWifiRevenueChange = "analyticsData.ciol.wifiRevenueChange"
        static let checkInOnlineEciRevenueChange = "analyticsData.ciol.eciRevenueChange"
        static let checkInOnlineLcoRevenueChange = "analyticsData.ciol.lcoRevenueChange"
        static let checkInOnlineTotalRevenueChange = "analyticsData.ciol.totalRevenueChange"
        static let checkInOnlineRevenue = "analyticsData.ciol.revenue"
        static let checkInOnlineSpecialOccasion = "analyticsData.ciol.specialOccasion"
        static let checkInOnlineExtrasShownDescription = "analyticsData.ciol.extrasShownDescription"
        static let checkInOnlineAddedExtras = "analyticsData.ciol.addedExtras"
        static let checkInOnlineNights = "analyticsData.ciol.nights"
        static let checkInOnlineRooms = "analyticsData.ciol.rooms"
        static let checkInOnlineCheckInDate = "analyticsData.ciol.checkInDate"
        static let checkInOnlineCheckOutDate = "analyticsData.ciol.checkOutDate"
        static let checkInOnlineCheckInDay = "analyticsData.ciol.checkInDay"
        static let checkInOnlineCheckOutDay = "analyticsData.ciol.checkOutDay"
        static let checkInOnlineCheckInOutDay = "analyticsData.ciol.CheckinoutDay"
        static let checkInOnlineAdults = "analyticsData.ciol.adults"
        static let checkInOnlineChildren = "analyticsData.ciol.children"
        static let checkInOnlineRateDescription = "analyticsData.ciol.rateDescription"
        static let checkInOnlineRateCode = "analyticsData.ciol.rateCode"
        static let checkInOnlineLeadEdit = "analyticsData.ciol.leadDetailsEdit"
        static let checkInOnlineAdditionalEdit = "analyticsData.ciol.additionalDetailsEdit"
        static let checkInOnlineBtnContinue = "analyticsData.ciol.btnContinue"
        static let checkInOnlineBtnSave = "analyticsData.ciol.btnSave"
        static let checkInOnlineFirstName = "analyticsData.ciol.fieldEdit"
        static let checkInOnlineDobEdit = "analyticsData.ciol.dobEdit"
        static let checkInOnlineNationalityEdit = "analyticsData.ciol.nationalityEdit"
        static let checkInOnlineBtnExpand = "analyticsData.ciol.btnExpand"
        static let checkInOnlinePaymentComplete = "analyticsData.ciol.paymentComplete"
        static let checkInOnlineBillingAddress = "analyticsData.ciol.billingAddress"
        static let checkInOnlineBottomSheet = "analyticsData.ciol.bottomSheet"
        static let checkInOnlineBottomSheetClick = "analyticsData.ciol.bottomSheetButtonClick"
        // CIOL update status
        static let checkInOnlineUpdateStatusReservationIds = "analyticsData.updateCiolStatus.request.reservationIds"
        static let checkInOnlineUpdateStatusHotelId = "analyticsData.updateCiolStatus.request.hotelId"
        static let ciolStatusUpdateRequestEvent  = "analyticsData.updateCiolStatus.request.updateUdfc20Status"
        static let ciolStatusUpdateResponseEvent = "analyticsData.updateCiolStatus.response.updateUdfc20Status"

        // Amend
        static let amendFoodRevenueChange = "analyticsData.amend.foodRevChange"
        static let amendRoomTypesDidChange = "analyticsData.amend.roomTypeChange"
        static let amendRoomRevenueChange = "analyticsData.amend.roomRevenueChange"
        static let amendExtrasRevenueChange = "analyticsData.amend.extrasRevenueChange"
        static let amendTotalRevenue = "analyticsData.amend.revenue"
        static let amendRoomsAmountChange = "analyticsData.amend.roomsChange"
        static let amendNightsChange = "analyticsData.amend.nightsChange"
        static let amendTotalRevenueChange = "analyticsData.amend.totalRevenueChange"
        static let amendChanges = "analyticsData.amend.change"
        static let amendTotalRevenueOriginal = "analyticsData.amend.totalRevenueOriginal"
        static let amendAvailability = "analyticsData.amend.availability"
        static let amendExtrasBookingId = "analyticsData.amend.bookingID"
        static let amendExtrasShownDescriptions = "analyticsData.amend.extrasShownDescription"
        static let amendExtrasShownCodes = "analyticsData.amend.extrasShownCode"
        static let amendPayNow = "analyticsData.amend.payNow"

        // Amend booking - Conf
        static let amendBookingId = "analyticsData.amend.conf.bookingID"
        static let amendConfFoodRevenueChange = "analyticsData.amend.conf.foodRevChange"
        static let amendConfRoomTypesDidChange = "analyticsData.amend.conf.roomTypeChange"
        static let amendConfRoomRevenueChange = "analyticsData.amend.conf.roomRevenueChange"
        static let amendConfExtrasRevenueChange = "analyticsData.amend.conf.extrasRevenueChange"
        static let amendConfRoomsAmountChange = "analyticsData.amend.conf.roomsChange"
        static let amendConfNightsChange = "analyticsData.amend.conf.nightsChange"
        static let amendConfTotalRevenueChange = "analyticsData.amend.conf.totalRevenueChange"
        static let amendConfChanges = "analyticsData.amend.conf.change"
        static let amendConfTotalRevenueOriginal = "analyticsData.amend.conf.totalRevenueOriginal"
        static let amendConfTotalRevenue = "analyticsData.amend.conf.revenue"

        // User details
        static let userDetailsMarketingOptIn = "analyticsData.emailOptIn"
        static let userDetailsCar = "analyticsData.carDetails"

        // Dashboard
        static let homeContent = "analyticsData.home.content"
        static let dashboardCancelledBookings = "analyticsData.dashboard.cancelledBookings"
        static let dashboardCheckedinBookings = "analyticsData.dashboard.checkedInBookings"
        static let dashboardFutureBookings = "analyticsData.dashboard.futureBookings"
        static let dashboardMoreThanFourRooms = "analyticsData.dashboard.moreThanFourRooms"
        static let dashboardMoreThanNineNights = "analyticsData.dashboard.moreThanNineNights"
        static let dashboardStayedBookings = "analyticsData.dashboard.stayedBookings"
        static let dashboardTotalBookings = "analyticsData.dashboard.totalBookings"
        static let dashboardCardTrackingId = "analyticsData.homepage.cardTrackingId"
        static let dashboardCardClick = "analyticsData.homepage.cardClick"
        static let dashboardCardAction = "analyticsData.homepage.action"

        // QR Code
        static let qrCodeCid = "analyticsData.qrCode.cid"

        // Homepage
        static let dashboardEcommerceOne = "analyticsData.homepage.ecommerce1"
        static let dashboardEcommerceTwo = "analyticsData.homepage.ecommerce2"

        // In App Wifi
        static let inAppWifiInitiated = "analyticsData.bf.inAppWifiInitiated"
        static let inAppWifiConnected = "analyticsData.bf.inAppWifiConnected"

        static let pushToken = "analyticsData.pushToken"

        static let digitalKeyEmail = "analyticsData.digitalKey.email"
        static let digitalKeyRoomAllocationMessaging = "analyticsData.digitalKey.allocationError"
        static let digitalKeyTrayNotification = "analyticsData.digitalKey.notification"
        static let digitalKeyTrayUseKey = "analyticsData.digitalKey.useKey"
        static let digitalKeyTrayGettingKey = "analyticsData.digitalKey.keyCard"

        // Add booking
        static let addBookingErrorMessage = "analyticsData.error.message"

        // Third-party bookings
        static let isThirdPartyBooking = "analyticsData.all.TPbooking"
        static let thirdPartyBookingID = "analyticsData.all.TPbookingid"
        static let thirdPartyAncillariesBooked = "analyticsData.all.ancillariesBooked"
        static let thirdPartyBookingExported = "analyticsData.all.TPbookingExported"
    }

    enum StateNames {
        static var splash: String { "Splash".analyticsComponentsPrefixed }
        static var home: String { "Home".analyticsComponentsPrefixed }
        static var appIncentivePopover: String { "First Time Offer Overlay".analyticsComponentsPrefixed }
        // Dashboard
        static var dashboard: String { "Home Dashboard".analyticsComponentsPrefixed }
        // 👀 To Book
        static var suggestions: String { "Search Criteria - Location only".analyticsComponentsPrefixed }
        static var noResults: String { "Search Results: no results returned".analyticsComponentsPrefixed }
        static var noConnection: String { "Error no internet connection".analyticsComponentsPrefixed }
        static var criteria: String { "Search Criteria".analyticsComponentsPrefixed }
        static var datePicker: String { "Search Criteria - Date Picker".analyticsComponentsPrefixed }
        static var roomType: String { "Search Criteria - room type".analyticsComponentsPrefixed }
        static var searchResultsMap: String { "Search Results Map".analyticsComponentsPrefixed }
        static var searchResults: String { "Search Results List".analyticsComponentsPrefixed }
        static var searchResultsUnavailable: String { "Search Results List - no availability".analyticsComponentsPrefixed }
        static var hotelImages: String { "hotel Image Gallery".analyticsComponentsPrefixed }
        static var hotelDetails: String { "Hotel Details".analyticsComponentsPrefixed }
        static var hotelDetailsImportantInfo: String { "Hotel Details - Important information".analyticsComponentsPrefixed }
        static var hotelDetailsMoreInfo: String { "Hotel Details - More information".analyticsComponentsPrefixed }
        static var hotelDetailsUnavailable: String { "Hotel Details - no availability".analyticsComponentsPrefixed }
        static var hotelDetailsUncheckedAvailability: String {
            "Hotel Details - Availability Unchecked".analyticsComponentsPrefixed }
        static var map: String { "Map".analyticsComponentsPrefixed }
        static var chooseRates: String { "Choose rates".analyticsComponentsPrefixed }
        static var chooseRooms: String { "Choose Your Rooms".analyticsComponentsPrefixed }
        // Booking Flow
        static var upsells: String { "Add Extras".analyticsComponentsPrefixed }
        static var summaryBreakdown: String { "Add Extras - View Full Summary".analyticsComponentsPrefixed }
        static var summaryBreakdownRB: String { "Review And Book - View Full Summary".analyticsComponentsPrefixed }
        static var login: String { "Log In".analyticsComponentsPrefixed }
        static var resetPassword: String { "Reset Password".analyticsComponentsPrefixed }
        static var bookerDetails: String { "Your Details".analyticsComponentsPrefixed }
        static var cardDetails: String { "Card Details".analyticsComponentsPrefixed }
        static var manageCardsUpdate: String { "Manage Cards - Update".analyticsComponentsPrefixed }
        static var manageCardsAdd: String { "Manage Cards - Add".analyticsComponentsPrefixed }
        static var review: String { "Add Extras - Review".analyticsComponentsPrefixed }
        static var myBookingsReservationBreakdown: String { "Card Details - Stay Summary".analyticsComponentsPrefixed }
        static var businessQuestions: String { "BAC - Additional Information".analyticsComponentsPrefixed }
        static var pay3CiPage: String { "Payment Details - 3C iPage".analyticsComponentsPrefixed }
        static var pay3CiPageLaunched: String { "Card Details: 3CP Payment Portal Launched".analyticsComponentsPrefixed }
        static var bookingConfirmation: String { "Booking Confirmation".analyticsComponentsPrefixed }
        // My Bookings
        static var myBookingsReservationDetails: String { "My Bookings - Booking Detail".analyticsComponentsPrefixed }
        static var myBookings: String { "My Bookings".analyticsComponentsPrefixed }
        static var myNoBookings: String { "My Bookings - No Bookings".analyticsComponentsPrefixed }
        static var addBooking: String { "Add Booking".analyticsComponentsPrefixed }
        static var datePickerB: String { "Find a Booking - Date Picker".analyticsComponentsPrefixed }
        static var planTrip: String { "Plan Your Trip".analyticsComponentsPrefixed }
        // Booking Details
        static var bookingDetails: String { "Booking Details".analyticsComponentsPrefixed }
        // Amend (1)
        static var manageBooking: String { "Amend a Booking".analyticsComponentsPrefixed }
        static var amendReview: String { "Amend Review".analyticsComponentsPrefixed }
        static var amendAndPay: String { "Amend and Pay".analyticsComponentsPrefixed }
        static var amendConfirmation: String { "Amend a Booking Confirmation".analyticsComponentsPrefixed }
        // >> Amend (2) sub pages
        static let amendRoom = "Edit Room"
        static let amendAddRoom = "Add Room"
        static let amendCalendar = "Date Picker"
        static let amendAvailability = "search criteria"
        // My Account
        static var myAccount: String { "My Account".analyticsComponentsPrefixed }
        static var register: String { "My Account: Create Account".analyticsComponentsPrefixed }
        static var manageCards: String { "My Account: Payment Methods: Manage Saved Cards".analyticsComponentsPrefixed }
        static var about: String { "My Account - About".analyticsComponentsPrefixed }
        static var myPiLogin: String { "My Account: Log In".analyticsComponentsPrefixed }
        static var changePassword: String { "My Account: Manage Password".analyticsComponentsPrefixed }
        static var changeAddress: String { "My Account: Manage Address".analyticsComponentsPrefixed }
        static var myPiResetPassword: String { "My Account: Log In: Reset Password".analyticsComponentsPrefixed }
        static var accountDetails: String { "My Account: Manage My Details".analyticsComponentsPrefixed }
        static var appSettings: String { "My Account: App Settings".analyticsComponentsPrefixed }
        static var regularGuests: String { "My Account: Manage Saved Guests".analyticsComponentsPrefixed }
        static var addRegularGuest: String { "My Account: Manage Saved Guests: Add Guest".analyticsComponentsPrefixed }
        static var bookingPrefs: String { "My Account: Manage Booking Preferences".analyticsComponentsPrefixed }
        static var roomPrefs: String { "My Account: Manage Booking Preferences: Room".analyticsComponentsPrefixed }
        static var mealPrefs: String { "My Account: Manage Booking Preferences: Meal".analyticsComponentsPrefixed }
        static var faq: String { "FAQ".analyticsComponentsPrefixed }
        static var faqStay: String { "FAQ - Your Stay".analyticsComponentsPrefixed }
        // GDPR
        static var gdprHowWeUseData: String { "Data Policy".analyticsComponentsPrefixed }
        static var gdprInterstitial: String { "Privacy Policy Acceptance Offer".analyticsComponentsPrefixed }
        static var onboardingView: String { "Technologies Accepted".analyticsComponentsPrefixed }
        // CIOL
        static var checkInOnline: String { "Check-in online".analyticsComponentsPrefixed }
        static var startCiol: String { "Check-in online: Start Check In".analyticsComponentsPrefixed }
        static var checkInOnlineGuestDetails: String { "Check-in online: Room Guest Details".analyticsComponentsPrefixed }
        static var checkInOnlineGuestDetailsPage: String { "Check-in online: Guest Details Page".analyticsComponentsPrefixed
            }
        static var ciolPreStay: String { "Check-in online: Confirm details".analyticsComponentsPrefixed }
        static var ciolUpsells: String { "Check-in online: Upsells".analyticsComponentsPrefixed }
        static var ciolUpsellDetails: String { "Check-in online: Upsell Details".analyticsComponentsPrefixed }
        static var ciolSelectRoom: String { "Check-in online: Select a room".analyticsComponentsPrefixed }
        static var ciolMenuSelected: String { "Check-in online: Menu Selected".analyticsComponentsPrefixed }
        static var ciolAllergensSelected: String { "Check-in online: Allergens Selected".analyticsComponentsPrefixed }
        static var ciolPayment: String { "Check-in online: Payment Page".analyticsComponentsPrefixed }
        static var checkInOnlineConfirmation: String { "Check-in online: Confirmation".analyticsComponentsPrefixed }
        static var ciolLeadGuestDetails: String { "Check-in online: Lead guest details".analyticsComponentsPrefixed }
        static var ciolAdditionalGuestDetails: String {
            "Check-in online: Additional guest details".analyticsComponentsPrefixed }

        // Apple Wallet
        static var appleWalletBookingConf: String {
            "Booking Confirmation: Apple Wallet Selected".analyticsComponentsPrefixed }

        static var appleWalletQRCode: String { "QR Code: Apple Wallet Selected".analyticsComponentsPrefixed }

        // In App Wifi
        static var inAppWifi: String { "In App Wifi: Wifi Option Selected".analyticsComponentsPrefixed }

        // Digital Keys
        static var digitalKeyAtHotel: String { "Digital Key Step 1 - Request Digital Key".analyticsComponentsPrefixed }
        static var otpEmailCapture: String { "Digital Key Step 2 - Enter Email".analyticsComponentsPrefixed }
        static var otpEmailVerify: String { "Digital Key Step 3 - Enter OTP".analyticsComponentsPrefixed }
        static var digitalKeyAddPass: String { "Digital Key Step 4 - Add Key To Wallet".analyticsComponentsPrefixed }
        static var digitalKeyCompletion: String { "Digital Key Step 5 - Completion".analyticsComponentsPrefixed }
        static var digitalKeyRoomBeingPrepared: String { "Digital Key - Room Being Prepared".analyticsComponentsPrefixed }
    }

    enum StateTypes {
        static let home = "iOS: Home"
        static let lookToBook = "iOS: Look to Book"
        static let bookingFlow = "iOS: Booking Flow"
        static let ciolFlow = "iOS: Check-in online flow"
        static let myPI = "iOS: Account"
        static let myBookings = "iOS: My Bookings"
        static let GDPR = "iOS: GDPR"
        static let amend = "iOS: Amend"
        static let gdprHowWeUseData = "iOS: Data Policy"
        static let gdprInterstitial = "iOS: Privacy Policy Acceptance Offer"
    }

    enum Error {
        static let remoteSuggestionsLoadError = "iOS: Remote Suggestions Load Error"
        static let remoteAvailabilitiesLoadError = "iOS: Remote Availabilities Load Error"
        static let remoteAvailabilityLoadError = "iOS: Remote Availability Load Error"
        static let remotePaymentError = "iOS: Remote Payment Error"
        static let remoteBookingError = "iOS: Remote Booking Error"
        static let remoteLoginError = "iOS: Remote Login Error"
        static let autoLoginError = "iOS: Auto Login Error"
        static let paymentNotTakenBookingError = "iOS: Forced Pay On Arrival"
        static let rateChangedError = "iOS: Session Timeout - Rate Changed"
        static let timeoutNoAvailError = "iOS: Session Timeout - No Availability"
        static let confirmationPollingStatusFailedError = "iOS: Confirmation Polling Status Failed Error"
        static let confirmationPollingReachedMaxAttemptsError = "iOS: Confirmation Polling Reached Max Attempts Error"
        static let paymentFailed = "Payment Failed"
        static let paypalVaultFailed = "iOS: PayPal Vault failed"
        static let akamaiSDKNoUrl = "iOS: Akamai SDK No BaseURL"
        static let akamaiSDKInitFail = "iOS: Akamai SDK Init Fail"
        static let ciolConfirmationMissingData = "iOS: Missing required data for booking confirmation"
        static let ciolConfirmationFindBookingFailed = "iOS: Failed findBooking in CIOL"
        static let ciolConfirmationHotelInfoFailed = "iOS: Failed hotelInfo in CIOL"
        static let ciolConfirmationBookingConfFailed = "iOS: Failed bookingConfirmation in CIOL"
        static let ciolUpdateUdfc20StatusFailed = "iOS: Failed updateUdfc20Status in CIOL"
        static let dkInteractorMissing = "iOS: DK Interactor Missing"
        static let dkAddToWalletButtonTapFailed = "iOS: DK addToWalletButtonTap failed"
        static let dkViewInWalletButtonTapFailed = "iOS: DK viewInWalletButtonTap keyIdentifier Missing"
        static let dkFindBookingSourceFailed = "iOS: DK findBooking failed"
        static let dkBookingConfirmationFailed = "iOS: DK bookingConfirmation failed"
        static let dkCheckInFailed = "iOS: DK checkIn failed"
        static let dkKeyNotFoundInWallet = "iOS: DK Key Not Found in Wallet"
        static let dkGenerateOTPFailed = "iOS: DK Generate OTP Failed"
        static let dkResendOTPFailed = "iOS: DK Resend OTP Failed"
        static let dkProvisionFailed = "iOS: DK Provision Failed"

        // Amend
        static let amendAmendSummaryError = "iOS: Amend Summary Error"
        static let amendSetupError = "iOS: Amend Setup Error"
        static let amendDatesAvailabilityError = "iOS: Amend Dates Availability Error"
        static let amendDatesChangeError = "iOS: Amend Dates Change Error"
        static let amendAddRoomAvailabilityError = "iOS: Amend Add Room Availability Error"
        static let amendAddRoomChangeError = "iOS: Amend Add Room Change Error"
        static let amendAddRoomGetReservationIdError = "iOS: Amend Add Room Get ReservationId Error"
        static let amendEditRoomAvailabilityError = "iOS: Amend Edit Room Availability Error"
        static let amendEditRoomChangeError = "iOS: Amend Edit Room Change Error"
        static let amendCancelRoomError = "iOS: Amend Cancel Room Error"
        static let amendSaveUpsellsError = "iOS: Amend Save Upsells Error"
        static let amendConfirmError = "iOS: Amend Confirm Error"
        static let amendPollingFailedError = "iOS: Amend Polling Status Failed Error"
        static let amendPollingOpenError = "iOS: Amend Polling Status Open Error"
        static let amendPollingReachedMaxAttemptsError = "iOS: Amend Polling Reached Max Attempts Error"
    }

    enum ErrorCode {
        static let customErrorCode = -999
    }
}

enum FirebaseAnalytics {
    enum Event {
        static let login = AnalyticsEventLogin
        static let searchResults = AnalyticsEventViewSearchResults
        static let hotelDetail = AnalyticsEventViewItem
        static let beginCheckout = AnalyticsEventBeginCheckout
        static let addPaymentInfo = AnalyticsEventAddPaymentInfo
        static let bookingConfirmation = AnalyticsEventPurchase
        static let legacyAvailabilities = "legacy_availabilities_call"
        static let graphQLAvailabilities = "graphQL_availabilities_call"
        static let failedToGetBookingFlowId = "getBookingFlowIdFailed"
        static let failedToGetPackages = "getPackagesFailed"
        static let wifiConnectionCTA = "wifiConnectionCTAClicked"
        static let wifiConnectionInitiated = "wifiConnectionInitiated"
        static let wifiConnected = "wifiConnectionSuccessful"
        static let wifiConnectionFailed = "wifiConnectionFailed"

        // Accessibility Events
        static let isVoiceOverEnabled = "isVoiceOverEnabledAtLaunch"
        static let isBoldTextEnabled = "isBoldTextEnabledAtLaunch"
        static let isReduceMotionEnabled = "isReduceMotionEnabledAtLaunch"
    }

    enum Parameter {
        static let hotelCode = AnalyticsParameterLocation // in our case the Hotel Code
        static let rateName = AnalyticsParameterTravelClass // rate type: flex, saver...
        static let currency = AnalyticsParameterCurrency // USD, GBP ...
        static let totalBookingPrice = AnalyticsParameterValue // double NSNumber
        static let searchTerm = AnalyticsParameterSearchTerm
        static let startDate = AnalyticsParameterStartDate // YYYY-MM-DD
        static let endDate = AnalyticsParameterEndDate // YYYY-MM-DD
        static let numberOfNights = AnalyticsParameterNumberOfNights
        static let numberOfRooms = AnalyticsParameterNumberOfRooms
        static let numberOfPeople = AnalyticsParameterNumberOfPassengers
        static let quantity = AnalyticsParameterQuantity
        static let userType = "user_type"
        static let paymentType = "payment_type"
    }
}

enum AppsFlyerAnalytics {
    enum Parameter {
        static let bookingReference = "bookingReference"
        static let revenue = "revenue"
        static let currency = "currency"
        static let hotelCode = "hotelCode"
        static let hotel = "hotel"
    }
}

enum BookingMetrics {
    static let totalRevenue = "total.revenue"
    static let totalRevenueCount = "total.revenue.count"
    static let availabilityLoadCount = "availability.loaded.count"
}

enum AppDynamicBreadCrumbs {
    static let totalRevenueCount = "total.revenue.count"
    static let availabilityLoadCount = "availability.loaded.count"
}

enum DynatraceActionName {
    static let totalRevenueAction = "action.revenue"
    static let availabilityLoaded = "action.availability"
}

enum DynatraceActionKey {
    static let totalRevenueKey = "key.revenue"
    static let availabilityKey = "key.availability"
}

enum ContentSquareKey: String, CaseIterable {
    case userLogin = "analyticsData.all.userLogin"
    case screenType = "analyticsData.all.screenType"

    case nights = "analyticsData.search.nights"
    case guests = "analyticsData.search.guests"
    case startEndDay = "analyticsData.search.startEndDay"
    case roomTypeNames = "analyticsData.search.roomTypeNames"

    case bfRateCode = "analyticsData.bf.rateCode"

    case rateCode = "analyticsData.conf.rateCode"
    case roomTypes = "analyticsData.conf.roomTypes"
    case customerType = "analyticsData.conf.customerType"
    case bookingID = "analyticsData.conf.bookingReference"

    // we need to use the same index for each variable every time
    var index: Int {
        switch self {
        case .userLogin:
            return 1
        case .screenType:
            return 2
        case .nights:
            return 3
        case .guests:
            return 4
        case .startEndDay:
            return 5
        case .roomTypeNames:
            return 6
        case .bfRateCode:
            return 7
        case .rateCode:
            return 8
        case .roomTypes:
            return 9
        case .customerType:
            return 10
        case .bookingID:
            return 11
        }
    }
}

enum AnalyticsConstants {
    #if DEV
    static let environment = "debug"
    static let appId = "launch-ENcff3a5708b18474f91f196e6d2bd7742-staging"
    #else
    static let environment = "production"
    static let appId = "launch-EN4bd5615a389d488abe5f55dfc5d4791b"
    #endif
}
