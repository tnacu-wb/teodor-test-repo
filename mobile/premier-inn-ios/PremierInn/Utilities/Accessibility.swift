//
//  Accessibility.swift
//  PremierInn
//
//  Created by Freddie Parks on 15/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

enum AccessibilityIdentifiers {
    // SHARED
    enum Shared {
        static let noInternetBanner = "noInternetConnectionBanner"
        static let navigateUp = "navigateUp"
        static let privacyDataPolicyButton = "privacyDataPolicyButton"
    }

    // BB INTRO
    enum BBIntro {
        static let businessBookerSpashScreenTitle = "businessBookerSpashScreenTitle"
        static let businessBookerSpashScreenContent = "businessBookerSpashScreenContent"
        static let signInBusinessBookerButton = "signInBusinessBookerButton"
        static let normalSignInButton = "normalSignInButton"
        static let skipButton = "skipButton"
    }

    // HOME
    enum Home {
        static let homePageTitle = "homePageTitle"
        static let searchSuggestionTitle = "searchSuggestionTitle"
        static let datesTitle = "datesTitle"
        static let roomsTitle = "roomsTitle"
        static let searchButton = "searchButton"
        static let searchNearMeButton = "searchNearMeButton"
        static let searchTabBarItem = "searchTabBarItem"
        static let bookingsTabBarItem = "bookingsTabBarItem"
        static let accountTabBarItem = "accountTabBarItem"
        static let bbCardExpiredEmailTravelManagerAction = "emailTravelManagerLink"
        static let bbCardExpiredContinueAction = "continueButton"
        static let redCircleDebugButton = "redCircleDebugButton"
    }

    enum Debug {
        static let buildVersionLabel = "buildVersionLabel"
    }

    // LOGIN
    enum Login {
        static let loginPageTitle = "loginPageTitle"
        static let personalAccountTab = "personalAccountTab"
        static let businessBookerTab = "businessBookerTab"
        static let touchIdLink = "touchIdLink"
        static let loginButton = "loginButton"
        static let secondaryActionButton = "secondaryActionButton"
        static let forgotPasswordButtonAcc = "forgotPasswordButtonAcc"
        static let bannerCellAcc = "bannerCellAcc"
        static let submitButton = "submitButton"
        static let moreAboutBusinessBookerButton = "moreAboutBusinessBookerButton"
    }

    enum Account {
        static let loginButton = "loginButton"
        static let logoutButton = "logoutButton"
        static let tableView = "myAccountTableView"
    }

    // SEARCH SUGGESTIONS
    enum Suggestions {
        static let searchTextField = "searchTextField"
        static let searchHotelsNearMeButton = "searchHotelsNearMeButton"
        static let searchCancelButton = "searchCancelButton"
        static let recentSearchesHeader = "recentSearchesHeader"
        static let clearSearchButton = "clearSearchButton"
        static let searchResult = "searchResult"
        static let suggestionHeader = "suggestionHeader"
        static let hotelsHeader = "hotelsHeader"
        static let placesHeader = "placesHeader"
        static let placeLabel = "searchPlaceName"
        static let hotelLabel = "PIHotelName"
        static let PIHotelIcon = "PIHotelIcon"
        static let noResultText = "noResultText"
        static let noInternetBanner = "searchNoInternetConnectionBanner"
    }

    // HOTEL DETAILS
    enum HotelDetails {
        static let restaurantAndBarHeader = "restaurantAndBarHeader"
        static let restaurantDisclaimer = "restaurantDisclaimer"
        static let parkingDetails = "parkingDetails"
        static let noPremierInnBreakfastDetails = "NoPremierInnBreakfastDetails"
        static let roomSubstitutionHeader = "roomSubstitutionHeader"
        static let roomSubstitutionDetails = "roomSubstitutionDetails"
        static let multipleRoomSubstitutionHeader = "multipleRoomSubstitutionHeader"
        static let hotelDetailsPageHotelImage = "hotelDetailsPageHotelImage"
        static let hotelDetailsNewHotelBanner = "hotelDetailsNewHotelBanner"
        static let hotelDetailsPageHotelAddress = "hotelDetailsPageHotelAddress"
        static let hotelReviews = "hotelReviews"
        static let navigateToCalendarButton = "navigateToCalendarButton"
        static let navigateToGuestAndRooms = "navigateToGuestAndRooms"
        static let showMeNearByHotelsButton = "showMeNearByHotelsButton"
        static let hotelDetailsSelectRateButton = "hotelDetailsSelectRateButton"
        static let importantHotelInfoButton = "importantHotelInfoButton"
        static let hotelDetailsReadMoreButton = "hotelDetailsReadMoreButton"
        static let hotelDetailsDirectionButton = "hotelDetailsDirectionButton"
        static let roomSegmentedControl = "roomSegmentedControl"
        static let foodSegmentedControler = "foodSegementedControl"
        static let tripAdvisorDetails = "tripAdvisorDetails"
        static let bbLogoutButton = "logoutOfBusinessBookerButton"


        static let callUsButton = "callUsButton"

        static let checkInTimeHeader = "checkInTimeHeader"
        static let checkOutTimeHeader = "checkOutTimeHeader"
        static let checkInTime = "checkInTime"
        static let checkOutTime = "checkOutTime"

        static let bookingTypeHeader = "bookingType%@Header"
        static let bookingTypeDetails = "bookingType%@Details"
        static let BookingDays = "BookingDays"
        static let BookingPrice = "BookingPrice"
        static let BookingButton = "BookButton"

        static let bbCardExpiredEmailTravelManagerAction = "emailTravelManagerLink"
        static let bbCardExpiredCloseAction = "closeButton"
    }

    // ADDITIONAL INFO
    enum AdditionalInfo {
        static let hotelInfoName = "aboutTheAreaHeader"
        static let hotelInfoAreaDescription = "aboutTheAreaPageDetails"
        static let hotelInfoDirectionsHeader = "aboutTheAreaDirectionHeader"
        static let hotelInfoDirectionsDescription = "aboutTheAreaDirectionDetails"
        static let hotelFacilitiesHeader = "hotelFacilitiesHeader"
        static let hotelFacilitiesTitle = "hotelFacilitiesTitle"
        static let aboutThisHotel = "aboutThisHotelTitle"
        static let roomFeaturesHeader = "roomFeaturesHeader"
        static let closeButton = "closeModalButton"
        static let screenTitle = "additionalInfoScreenTitle"
    }

    // DISCOUNT CODE
    enum DiscountCode {
        static let closeButton = "discountCodeCloseButton"
        static let applyButton = "discountCodeApplyButton"
    }

    // PARKING INFO
    enum ParkingInfo {
        static let parkingInfoHeader = "ParkingAtThisHotelHeader"
        static let parkingInfoDescription = "parkingAtThisHotelDescription"
    }

    // ROOM TYPES
    enum RoomTypes {
        static let roomTypesHeader = "roomTypesHeader"
        static let closeButton = "NavigateUp"
        static let roomTypesSegmentedControl = "roomTypesSegmentedControl"
    }

    // MAP DETAIL
    enum MapDetail {
        static let navigationItemTitle = "aboutTheHotelMap"
        static let closeButton = "NavigateUp"
        static let distanceFromSearch = "distanceFromCurrentLocation"
    }

    // SEARCH RESULTS
    enum SearchResults {
        static let backButton = "NavigateUp"
        static let sortDistanceSegment = "searchResultSortByDistanceButton"
        static let sortPriceSegment = "searchResultSortByPriceButton"
        static let mapButton = "searchResultMapViewButton"
        static let mapView = "searchResultPageMapView"
        static let venuesList = "venuesList"
        static let imageFormat = "searchResultIndex%dHotelImage"
        static let hotelNameFormat = "searchResultIndex%dHotelName"
        static let fromRateLabelFormat = "searchResultIndex%dHotelRateBooking"
        static let fromRatePriceFormat = "searchResultIndex%dHotelRatePrice"
        static let parkingDetailFormat = "searchResultIndex%dHotelParkingDetails"
        static let newHotelBannerFormat = "searchResultIndex%dNewHotelBanner"
        static let discountAppliedBannerFormat = "searchResultIndex%dDiscountAppliedBanner"

        static let noAvailabilityOpeningDate = "searchResultNewHotelOpeningDetails"
        static let noAvailabilityNewHotelName = "searchResultNewHotelName"

        static let noAvailabilityHotelImage = "searchResultFullyBookedHotelImage"
        static let noAvailabilitySoldOutLabel = "searchResultFullyBookedSoldOutLabel"
        static let noAvailabilityHotelName = "searchResultFullyBookedHotelName"

        static let noAvailabilityForHotelSearchMessage = "searchResultFullyBookedHotelNotification"
        static let noAvailabilitiesForHotelSearchMessage = "searchResultsNoHotelsNearByNotification"

        static let hubFamilyRoomNotAvailableMessage = "hubFamilyRoomNotAvailableNotification"

        static let editDatesButton = "searchResultEditDatesButton"
        static let dataPolicyLink = "searchResultDataPrivacyLink"
    }

    enum ReviewAndBook {
        static let chooseWhenToPaySegment = "chooseWhenToPaySegment"
        static let cvv = "reviewPageCVVNumberAcc"
    }

    enum BookingSummary {
        static let summaryOverlayPageTitle = "summaryOverleyPageTitle"
        static let summaryPageGuestAndDays = "summaryPageGuestAndDays"
        static let summaryPageArrivalTitle = "summaryPageArrivalTitle"
        static let summaryPageArrivalDateCheckinTime = "summaryPageArrivalDateCheckinTime"
        static let summaryPageLeaveTitle = "summaryPageLeaveTitle"
        static let summaryPageLeavingDateCheckOutTime = "summaryPageLeavingDateCheckOutTime"
        static let summaryPageRoomDetailsTitle = "summaryPageRoomDetailsTitle"
        static let roomIndex = "roomIndex"
        static let TypeTitle = "TypeTitle"
        static let GuestTitle = "GuestTitle"
        static let GuestCount = "GuestCount"
        static let Price = "Price"
        static let PriceByNightDateIndex = "PriceByNightDateIndex0"
        static let summaryPageTotalBooking = "summaryPageTotalBooking"
        static let summaryPageTotalBookingPrice = "summaryPageTotalBookingPrice"
        static let summaryPageBookingType = "summaryPageBookingType"
    }

    enum MyBookings {
        static let myBookingsPageHeader = "myBookingsPageHeader"
        static let guestMyBookingPage = "guestMyBookingPage"
        static let guestMyBookingPageLoginButton = "guestMyBookingPageLoginButton"
        static let guestMyBookingForgottenPassword = "guestMyBookingForgottenPassword"
        static let guestMyBookingFindABooking = "guestMyBookingFindABooking"
        static let myBookingsPageSeachBookingButton = "myBookingsPageSeachBookingButton"
        static let registeredUserNoPastBookings = "registeredUserNoPastBookings"
        static let registeredUserSeachforAHotelButton = "registeredUserSeachforAHotelButton"
    }

    enum FindBooking {
        static let findABookingPageTitle = "findABookingPageTitle"
        static let findABookingLoginButton = "findABookingLoginButton"
        static let findBookingReferenceNumberTitle = "findBookingReferenceNumberTitle"
        static let findBookingReferenceNumberTextField = "findBookingReferenceNumberTextField"
        static let findBookingLastName = "findBookingLastName"
        static let findBookingLastNameTextField = "findBookingLastNameTextField"
        static let findBookingArrivalDateTitle = "findBookingArrivalDateTitle"
        static let findBookingArrivalDateTextField = "findBookingArrivalDateTextField"
        static let findBookingFindBookingButton = "findBookingFindBookingButton"
        static let noBookingExistsNotification = "noBookingExistsNotification"
    }

    enum UserDetails {
        static let userDetailsPageTitle = "yourDetailsPageHeader"

        static let bookerHeader = "yourDetailsTitle"
        static let bookerTitleLabel = "yourDetailsPageTitle"
        static let bookerTitleInput = "chooseTitle"
        static let bookerFirstNameLabel = "yourDetailsPageFirstName"
        static let bookerFirstNameInput = "yourDetailsFirstNameTextField"
        static let bookerLastNameLabel = "yourDetailsPageLastName"
        static let bookerLastNameInput = "yourDetailsLastNameTextField"
        static let bookerNumberLabel = "yourDetailsPageContactNumber"
        static let bookerNumberInput = "yourDetailsContactTextField"
        static let bookerEmailLabel = "yourDetailsPageEmailAddress"
        static let bookerEmailInput = "yourDetailsEmailTextField"
        static let bookerEmailUsageMessage = "detailsWillBeUsedNotification"

        static let bookerAddressHeader = "yourDetailsAddressTitle"
        static let bookerAddressTypeHome = "yourDetailsAddressHomeOption"
        static let bookerAddressTypeWork = "yourDetailsAddressWorkOption"
        static let bookerAddressCountryLabel = "yourDetailsCountry"
        static let bookerAddressPostcodeLabel = "yourDetailsPostcode"
        static let bookerAddressLine1Label = "yourDetailsAddressLine1"
        static let bookerAddressLine1Input = "yourDetailsAddressLine1TextField"
        static let bookerAddressLine2Label = "yourDetailsAddressLine2"
        static let bookerAddressLine2Input = "yourDetailsAddressLine2TextField"
        static let bookerAddressLine3Label = "yourDetailsAddressLine3"
        static let bookerAddressLine3Input = "yourDetailsAddressLine3TextField"
        static let bookerAddressLine4Label = "yourDetailsAddressLine4"
        static let bookerAddressLine4Input = "yourDetailsAddressLine4TextField"

        static let leadDetailSectionHeader = "yourDetailsLeadGuestTitle"
        static let bookerStayerLabel = "bookingNotStayingTitle"
        static let bookerStayerSwitch = "bookingNotStayingRadioButton"
        static let leadGDPRMessage = "guestDetailsShareNotification"
        static let leadRoomHeaderFormat = "yourDetailsPageRoomIndex%d"
        static let leadTitleLabelFormat = "RoomIndex%dTitle"
        static let leadTitleInput = "chooseTitle"
        static let leadFirstNameLabelFormat = "RoomIndex%dFirstName"
        static let leadFirstNameInput = "yourDetailsFirstNameTextField"
        static let leadLastNameLabelFormat = "RoomIndex%dLastName"
        static let leadLastNameInput = "yourDetailsLastNameTextField"
        static let leadEmailLabelFormat = "RoomIndex%dEmailAddress"
        static let leadEmailInput = "yourDetailsEmailAddressTextField"

        static let tripPurposeMessage = "purposeOfTheTripTitle"
        static let tripPurposeSelectorCell = "purposeOfTripSelectorCell"
        static let tripPurposeSelector = "purposeOfTripSelector"
        static let tripPurposeLeisure = "purposeOfTripLeisureButton"
        static let tripPurposeBusiness = "purposeOfTripBusinessButton"
        static let tripPurposeErrorMessage = "purposeOfTripErrorMessage"
        static let continueButton = "yourDetailsPageContinueToPaymentButton"
        static let cardsAcceptedTitle = "yourDetailsPagecardsAccepted"
        static let privacyPolicyLink = "yourDetailsPrivacyPolicyLink"

        static let marketingSwitch = "yourDetailsMarketingSwitch"
    }

    enum TitleList {
        static let titleListPrefixFormat = "chooseTitle%@"
    }

    enum ButtonRowList {
        static let cancelButton = "CancelButton"
        static let pageHeader = "PageHeader"
    }

    enum Upsells {
        static let businessAllowancesTitle = "businessAllowanceTitle"
        static let businessAllowanceDinnerTitle = "dinnerAllowance"
        static let businessAllowanceParkingTitle = "parkingAllowance"
        static let businessAllowanceMealsMessage = "approvedAllowanceInfo"
    }

    enum ImportantAnnouncements {
        static let ctaButton = "ctaButton"
    }

    enum BusinessCardQuestions {
        static let ctaButton = "continueButton"
    }

    enum CheckInOnline {
        static let checkInButton = "checkInButton"
        static let paymentButton = "paymentButton"
    }

    enum CIOL {
        static let ciolCheckIntButton = "ciolCheckIntButton"
        static let errorMessageLabel = "errorMessageLabel"
    }

    enum ResetPassword {
        static let emailTextField = "emailTextField"
        static let emailCell = "emailCellAcc"
    }
}

enum AccessibilityManager {
    // This announces a message when in voice over mode, care is needed when announcing if other elements are being read out as they are not queued but will get cut off.
    static func announce(_ message: String) {
        if #available(iOS 17, *) {
            var announcement = AttributedString(message)
            announcement.accessibilitySpeechAnnouncementPriority = .high
            AccessibilityNotification.Announcement(announcement).post()
        } else {
            UIAccessibility.post(
                notification: .announcement,
                argument: message
            )
        }
    }
}
