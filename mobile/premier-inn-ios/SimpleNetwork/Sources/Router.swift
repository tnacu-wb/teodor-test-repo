//
//  Router.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/05/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

public enum WebserviceAction: String {
    case availabilities
	case availability
    case availabilityForAmend
    case totalCostWithCityTax
	case hold
    case holdWithGuests
	case release
    case checkBasketStatus
	case reservation
	case cancelReservation
    case updateReservationPreferences
    case startCheckInOnlineSession
    case addCheckInOnlineGuestDetails
    case anonNewsletter
    case addCheckInOnlineUpsells
    case closeCheckInOnlineSession
	case getStays
	case suggestions
	case addressLookup
    case migratedLogin
    case migratedBBLogin
	case logout
	case forgotPassword
    case completePayment
	case hotelDetails
	case savePaymentCard
    case initiateSaveCard
	case deletePaymentCard
    case migratedGetUserCredentials
    case migratedBBGetUserCredentials
    case getUser
    case getCompany
    case checkInOnlinePayment
	case changePassword
	case registerUser
	case updateUserDetails
    case deleteUser
	case additionalGuests
	case updateFoodPref
	case updateRoomPref
    case wallet
    case marketingPreferences
    case updateMarketingPreferences
    case dashboardComponents
    case cccpPayment
    case paymentMethods
    case doorKey
    case countries
    case headerInformation
    case getRestrictions
    case saveUpsells
    case findBookingSource
    case ratesInformation
    case copyBooking
    case amendBookingDates
    case addNewRoom
    case amendEditRoom
    case amendRemoveRoom
    case amendPackages
    case confirmAmend
    case getPackages
    case reservationForAmend
    case amendSummary
    case amendConfirmationPrices
    case getReservationWithAmendSummary
    case bookingInformation
    case getHotelBySlug
    case confirmPreCheckInOut
    case getCategoryLabels
    case getHotelPreferences
    case roomClassConfig
    case amendCiolPackages
    case resendInvoiceEmail
    case homepageContent
    case ciolPDFGenerator
    case authorizePayment
    case attachFileToReservation
    case updatePreCheckInStatus
    case generateOTP
    case verifyOTP
    case provisionKey
    case keyCheckIn
    case promotionsInformation
    case validateDiscountCode
    case updateCiolStatus
    case ciolPaymentActions
    case ciolBackgroundCharge
    case initMobileSDKPayment
}

public class Router {
    public static let production = ProductionRouter()
    public static let developmentGraphQL = DevelopmentGraphQLRouter()
    public static let uatGraphQL = UatGraphQLRouter()
    public static let qaGraphQLDit = DitGraphQLRouter()
    public static let qaGraphQLSit = SitGraphQLRouter()
    public static let demoGraphQL = DemoGraphQLRouter()
    public static let preprodGraphQL = PreprodGraphQLRouter()
    public static let perfGraphQL = PerfGraphQLRouter()
    public static let hulkGraphQL = HulkGraphQLRouter()
    public static let wandaGraphQL = WandaGraphQLRouter()

    public static var current: Router = .production {
        didSet {
            let graphQLRouter = current as? LowerEnvGraphQLRouter
            originHost = graphQLRouter?.getOriginURL
        }
    }
    public static var versioning: Versioning?
    public static var originHost: String?
    public static var isRestMigrated: Bool = false
    public static var isAutocompleteMigrated: Bool = false

    public static var baseURLString: String? {
        switch current {
        case .production: production.graphQLService.baseURL?.absoluteString
        case .developmentGraphQL: developmentGraphQL.graphQLService.baseURL?.absoluteString
        case .uatGraphQL: uatGraphQL.graphQLService.baseURL?.absoluteString
        case .qaGraphQLDit: qaGraphQLDit.graphQLService.baseURL?.absoluteString
        case .qaGraphQLSit: qaGraphQLSit.graphQLService.baseURL?.absoluteString
        case .demoGraphQL: demoGraphQL.graphQLService.baseURL?.absoluteString
        case .preprodGraphQL: preprodGraphQL.graphQLService.baseURL?.absoluteString
        case .perfGraphQL: perfGraphQL.graphQLService.baseURL?.absoluteString
        case .hulkGraphQL: hulkGraphQL.graphQLService.baseURL?.absoluteString
        case .wandaGraphQL: wandaGraphQL.graphQLService.baseURL?.absoluteString
        default: nil
        }
    }

    func service(for action: WebserviceAction) throws -> WebserviceProtocol {
        throw WebserviceError.notImplemented(action.rawValue)
    }
}

extension Router: Equatable {
	public static func == (lhs: Router, rhs: Router) -> Bool {
		lhs === rhs
	}
}

extension LowerEnvGraphQLRouter {
    var getOriginURL: String? {
        let newUrl = self.graphQLService.baseURL?.absoluteString.replacingOccurrences(of: "api", with: "www")
        return newUrl
    }
}

extension Router: WebserviceProtocol {
    public func searchAvailabilities(
        bookingDetails: BookingDetails,
        suggestion: Suggestion,
        page: Int?,
        sorting: AvailabilitiesSorting,
        allowEmployeeOffer: Bool
    ) throws -> Resource<AvailabilitiesResponse> {
        try service(for: .availabilities).searchAvailabilities(
            bookingDetails: bookingDetails,
            suggestion: suggestion,
            page: page,
            sorting: sorting,
            allowEmployeeOffer: allowEmployeeOffer
        )
    }

    public func hotelAvailability(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        bookingDetails: BookingDetails,
        allowEmployeeOffer: Bool
    ) throws -> Resource<HotelAvailabilityResponse> {
		try service(for: .availability).hotelAvailability(
		    hotelCode: hotelCode,
		    hotelBrand: hotelBrand,
		    bookingDetails: bookingDetails,
		    allowEmployeeOffer: allowEmployeeOffer
		)
	}

    public func hotelAvailabilityForAmendBooking(
        withHotelCode hotelCode: String,
        bookingDetails: BookingDetails,
        brand: HotelBrand?,
        allowEmployeeOffer: Bool
    ) throws -> Resource<HotelAvailabilityResponse> {
        try service(for: .availabilityForAmend).hotelAvailabilityForAmendBooking(
            withHotelCode: hotelCode,
            bookingDetails: bookingDetails,
            brand: brand,
            allowEmployeeOffer: allowEmployeeOffer
        )
    }

    public func holdBooking(bookingDetails: BookingDetails, sensorData: String) throws -> Resource<String> {
		try service(for: .hold).holdBooking(bookingDetails: bookingDetails, sensorData: sensorData)
	}

    public func getTotalCostWithCityTax(bookingDetails: BookingDetails) throws -> Resource<Cost> {
        try service(for: .totalCostWithCityTax).getTotalCostWithCityTax(bookingDetails: bookingDetails)
    }

    public func holdBookingWithGuests(
        bookingDetails: BookingDetails,
        isCiolFlow: Bool,
        isRegCard: Bool
    ) throws -> Resource<Bool> {
        try service(for: .holdWithGuests).holdBookingWithGuests(
            bookingDetails: bookingDetails,
            isCiolFlow: isCiolFlow,
            isRegCard: isRegCard
        )
    }

    public func releaseBooking(basketReference: String, hotelId: String?) throws -> Resource<Bool> {
		try service(for: .release).releaseBooking(basketReference: basketReference, hotelId: hotelId)
	}

    public func checkBasketStatus(basketReference: String?) throws -> Resource<BookingConfirmation> {
        try service(for: .checkBasketStatus).checkBasketStatus(basketReference: basketReference)
    }

    public func getRestrictions() throws -> Resource<[Restrictions]> {
        try service(for: .getRestrictions).getRestrictions()
    }

    public func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?
    ) throws -> Resource<Reservation> {
        try service(for: .reservation).reservation(
            reservationDetails: reservationDetails,
            hotelCode: hotelCode,
            bookingDetails: bookingDetails
        )
    }

    public func cancelReservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?
    ) throws -> Resource<PIDictionary> {
		try service(for: .cancelReservation).cancelReservation(reservationDetails: reservationDetails, hotelCode: hotelCode)
	}

    public func updateReservationPreferences(
        hotelCode: String,
        reservationIds: [String],
        preferencesCollections: [PreferencesCollection]
    ) throws -> Resource<PIDictionary> {
        try service(for: .updateReservationPreferences).updateReservationPreferences(
            hotelCode: hotelCode,
            reservationIds: reservationIds,
            preferencesCollections: preferencesCollections
        )
    }

	public func suggestions(searchTerm: String) throws -> Resource<[PISuggestion]> {
		try service(for: .suggestions).suggestions(searchTerm: searchTerm)
	}

    public func addressLookup(postCode: String) throws -> Resource<[AddressSummary]> {
        try service(for: .addressLookup).addressLookup(postCode: postCode)
    }

    public func addressLookup(postCode: String, id: String) throws -> Resource<Address> {
        try service(for: .addressLookup).addressLookup(postCode: postCode, id: id)
	}

    public func login(
        username: String,
        password: String,
        isBusiness: Bool = false,
        completion: @escaping (Result<Bool>) -> Void
    ) {
        do {
            let serviceAction: WebserviceAction = {
                isBusiness ? .migratedBBLogin : .migratedLogin
            }()

            try service(for: serviceAction).login(
                username: username,
                password: password,
                isBusiness: isBusiness,
                completion: completion
            )
        } catch {
            completion(.failure(error: error))
        }
    }

	public func logout() throws {
		try service(for: .logout).logout()
	}

    public func forgotPassword(emailAddress: String, isBusiness: Bool) throws -> Resource<Bool> {
		try service(for: .forgotPassword).forgotPassword(emailAddress: emailAddress, isBusiness: isBusiness)
	}

    public func completePayment(with sessionId: String, and paRes: String) throws -> Resource<Bool> {
        try service(for: .completePayment).completePayment(with: sessionId, and: paRes)
    }

    public func getHotel(with hotelCode: String) throws -> Resource<Hotel> {
		try service(for: .hotelDetails).getHotel(with: hotelCode)
	}

    public func getStays() throws -> Resource<[Stay]> {
		try service(for: .getStays).getStays()
	}

    public func getUserCredentials(isBusiness: Bool = false, completion: @escaping (Result<Bool>) -> Void) {
        do {
            let serviceAction: WebserviceAction = {
                isBusiness ? .migratedBBGetUserCredentials : .migratedGetUserCredentials
            }()

            try service(for: serviceAction).getUserCredentials(completion: completion)
        } catch {
            completion(.failure(error: error))
        }
    }

    public func getUser(userId: String, isBusiness: Bool = false, completion: @escaping (Result<Resource<User>>) -> Void) {
        do {
            try service(for: .getUser).getUser(userId: userId, isBusiness: isBusiness, completion: completion)
        } catch {
            completion(.failure(error: error))
        }
    }

	public func getCompany(companyId: String, sensorData: String) throws -> Resource<Company> {
        try service(for: .getCompany).getCompany(companyId: companyId, sensorData: sensorData)
    }

	public func savePaymentCard(for user: User, sensorData: String) throws -> Resource<Bool> {
		try service(for: .savePaymentCard).savePaymentCard(for: user, sensorData: sensorData)
	}

    public func initiateSaveCard(initiateSaveCardParameters: InitiateSaveCardParameters) throws
        -> Resource<CCCPPaymentProviderResponse> {
        try service(for: .initiateSaveCard).initiateSaveCard(initiateSaveCardParameters: initiateSaveCardParameters)
    }

    public func anonymousNewsletterPreferences(
        email: String,
        countryOfResidence: String
    ) throws -> Resource<AnonymousNewsletterPreferences> {
        try service(for: .anonNewsletter).anonymousNewsletterPreferences(
            email: email,
            countryOfResidence: countryOfResidence
        )
    }

	public func updateUserAdditionalGuests(user: User, sensorData: String) throws -> Resource<Bool> {
		try service(for: .additionalGuests).updateUserAdditionalGuests(user: user, sensorData: sensorData)
	}

	public func updateUserDetails(user: User, sensorData: String) throws -> Resource<Bool> {
		try service(for: .updateUserDetails).updateUserDetails(user: user, sensorData: sensorData)
	}

    public func deleteUser() throws -> Resource<Bool> {
        try service(for: .deleteUser).deleteUser()
    }

	public func deletePaymentCard(for user: User, sensorData: String) throws -> Resource<Bool> {
		try service(for: .deletePaymentCard).deletePaymentCard(for: user, sensorData: sensorData)
	}

	public func register(registerParameters: RegisterParameters, sensorData: String) throws -> Resource<Bool> {
		try service(for: .registerUser).register(registerParameters: registerParameters, sensorData: sensorData)
	}

	public func changePassword(
	    user: User,
	    existingPassword: String,
	    newPassword: String,
	    sensorData: String
	) throws -> Resource<Bool> {
		try service(for: .changePassword).changePassword(
		    user: user,
		    existingPassword: existingPassword,
		    newPassword: newPassword,
		    sensorData: sensorData
		)
	}

	public func updateFoodPreference(user: User, sensorData: String) throws -> Resource<Bool> {
		try service(for: .updateFoodPref).updateFoodPreference(user: user, sensorData: sensorData)
	}

	public func updateRoomPreference(user: User, sensorData: String) throws -> Resource<Bool> {
		try service(for: .updateRoomPref).updateRoomPreference(user: user, sensorData: sensorData)
	}

    public func startCheckInOnlineSession(with params: StartCheckInRequestParameters) throws
        -> Resource<CheckInOnlineSessionResponse> {
        try service(for: .startCheckInOnlineSession).startCheckInOnlineSession(with: params)
    }

    public func addCheckInOnlineGuestDetails(with params: AddCheckInOnlineGuestDetailsParameters) throws -> Resource<Bool> {
        try service(for: .addCheckInOnlineGuestDetails).addCheckInOnlineGuestDetails(with: params)
    }

    public func addCheckInOnlineUpsells(
        withSessionID sessionID: String,
        confirmationNumber: String,
        andUpsells upsells: [UpsellItem]
    ) throws -> Resource<CheckInOnlineAddUpsellsResponse> {
        try service(for: .addCheckInOnlineUpsells).addCheckInOnlineUpsells(
            withSessionID: sessionID,
            confirmationNumber: confirmationNumber,
            andUpsells: upsells
        )
    }

    public func closeCheckInOnlineSession(withSessionID sessionID: String) throws -> Resource<Bool> {
        try service(for: .closeCheckInOnlineSession).closeCheckInOnlineSession(withSessionID: sessionID)
    }

    public func checkInOnlinePayment(
        with sessionId: String,
        confirmationNumber: String,
        paymentDetails: PaymentDetails
    ) throws -> Resource<CheckInPaymentResponse> {
        try service(for: .checkInOnlinePayment).checkInOnlinePayment(
            with: sessionId,
            confirmationNumber: confirmationNumber,
            paymentDetails: paymentDetails
        )
    }

    public func getWalletPass(with reservationDetails: ReservationDetails, excludeBarcode: Bool) throws -> Resource<Data> {
        try service(for: .wallet).getWalletPass(with: reservationDetails, excludeBarcode: excludeBarcode)
    }

    public func getMarketingPreferences(
        for emailAddress: String,
        and brandCodes: MarketingBrandCode,
        isBusiness: Bool
    ) throws -> Resource<MarketingPreferences> {
        try service(for: .marketingPreferences).getMarketingPreferences(
            for: emailAddress,
            and: brandCodes,
            isBusiness: isBusiness
        )
    }

    public func updateMarketingPreferences(
        brands: [MarketingBrandCode],
        emailAddress: String,
        optIn: Bool,
        isoCountryCode: String
    ) throws -> Resource<Bool> {
        try service(for: .updateMarketingPreferences).updateMarketingPreferences(
            brands: brands,
            emailAddress: emailAddress,
            optIn: optIn,
            isoCountryCode: isoCountryCode
        )
    }

    public func getDashboardComponents(
        surname: String?,
        arrival: Date?,
        reservationId: String?,
        isBusiness: Bool,
        recentSearchesFlag: Bool
    ) throws -> Resource<[DashboardComponent]> {
        try service(for: .dashboardComponents).getDashboardComponents(
            surname: surname,
            arrival: arrival,
            reservationId: reservationId,
            isBusiness: isBusiness,
            recentSearchesFlag: recentSearchesFlag
        )
    }

    public func cccpPayment(
        with paymentParams: CCCPPaymentParams,
        and stayDetails: CCCPStayDetails,
        and sessionId: String?,
        and isCiol: Bool,
        sensorData: String
    ) throws -> Resource<CCCPPaymentResponse> {
        try service(for: .cccpPayment).cccpPayment(
            with: paymentParams,
            and: stayDetails,
            and: sessionId,
            and: isCiol,
            sensorData: sensorData
        )
    }

    public func paymentMethods(
        for bookingDetails: BookingDetails,
        hotel: Hotel,
        rate: Rate,
        user: User?,
        isCiol: Bool
    ) throws -> Resource<PaymentMethodsResponse> {
        try service(for: .paymentMethods).paymentMethods(
            for: bookingDetails,
            hotel: hotel,
            rate: rate,
            user: user,
            isCiol: isCiol
        )
    }

    public func getDoorKey(
        reservationDetails: ReservationDetails,
        deviceId: String,
        authenticationCode: String?
    ) throws -> Resource<String> {
        try service(for: .doorKey).getDoorKey(
            reservationDetails: reservationDetails,
            deviceId: deviceId,
            authenticationCode: authenticationCode
        )
    }

    public func getCountries() throws -> Resource<[Country]> {
        try service(for: .countries).getCountries()
    }

    public func headerInformation() throws -> Resource<HeaderInformation> {
        try service(for: .headerInformation).headerInformation()
    }

    public func saveUpsellsToBooking(bookingDetails: BookingDetails) throws -> Resource<Bool> {
        try service(for: .saveUpsells).saveUpsellsToBooking(bookingDetails: bookingDetails)
    }

    public func findBookingSource(findBookingDetails: FindBookingDetails) throws -> Resource<FindBookingSource> {
        try service(for: .findBookingSource).findBookingSource(findBookingDetails: findBookingDetails)
    }

    public func ratesInformation(
        ratePlans: [String],
        hotelCode: String,
        hotelBrand: HotelBrand?
    ) throws -> Resource<[RateInformation]> {
        try service(for: .ratesInformation).ratesInformation(
            ratePlans: ratePlans,
            hotelCode: hotelCode,
            hotelBrand: hotelBrand
        )
    }

    public func copyBooking(reservationDetails: ReservationDetails) throws -> Resource<String> {
        try service(for: .copyBooking).copyBooking(reservationDetails: reservationDetails)
    }

    public func amendPackages(packagesDetails: AmendPackagesDetail) throws -> Resource<Bool> {
        try service(for: .amendPackages).amendPackages(packagesDetails: packagesDetails)
    }

    public func amendCiolPackages(amendInfo: CiolAmendInfo) throws -> Resource<Bool> {
        try service(for: .amendCiolPackages).amendCiolPackages(amendInfo: amendInfo)
    }

    public func confirmAmendLogic(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        selectedPaymentOption: String
    ) throws -> Resource<CCCPPaymentResponse> {
        try service(for: .confirmAmend).confirmAmendLogic(
            reservationDetails: reservationDetails,
            tempBookingReference: tempBookingReference,
            selectedPaymentOption: selectedPaymentOption
        )
    }

    public func amendBookingDates(
        temporaryReference: String,
        arrivalDate: Date,
        departureDate: Date,
        token: String
    ) throws -> Resource<String> {
        try service(for: .amendBookingDates).amendBookingDates(
            temporaryReference: temporaryReference,
            arrivalDate: arrivalDate,
            departureDate: departureDate,
            token: token
        )
    }

    public func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool
    ) throws -> Resource<([UpsellItem], [UpsellItem], CityTaxResponse, GoshPackage?)> {
        try service(for: .getPackages).getPackages(
            reservationId: reservationId,
            bookingDetails: bookingDetails,
            hotelCode: hotelCode,
            bookingFlowId: bookingFlowId,
            showMealInclusiveRate: showMealInclusiveRate
        )
    }

    public func addNewRoom(roomCriteria: AmendRoomCriteria) throws -> Resource<String> {
        try service(for: .addNewRoom).addNewRoom(roomCriteria: roomCriteria)
    }

    public func amendEditRoom(amendEditDetails: AmendRoomCriteria) throws -> Resource<String> {
        try service(for: .amendEditRoom).amendEditRoom(amendEditDetails: amendEditDetails)
    }

    public func removeRoom(roomCriteria: AmendRoomCriteria) throws -> Resource<String> {
        try service(for: .amendRemoveRoom).removeRoom(roomCriteria: roomCriteria)
    }

    public func reservationForAmend(
        reservationDetails: ReservationDetails,
        hotelCode: String?
    ) throws -> Resource<Reservation> {
        try service(for: .reservationForAmend).reservationForAmend(
            reservationDetails: reservationDetails,
            hotelCode: hotelCode
        )
    }

    public func amendSummary(
        reservationDetails: ReservationDetails,
        temporaryReference: String
    ) throws -> Resource<AmendSummary> {
        try service(for: .amendSummary).amendSummary(
            reservationDetails: reservationDetails,
            temporaryReference: temporaryReference
        )
    }

    public func amendConfirmationPrices(
        tempBookingRef: String,
        originalBookingRef: String,
        token: String
    ) throws -> Resource<AmendConfirmationPrices> {
        try service(for: .amendConfirmationPrices).amendConfirmationPrices(
            tempBookingRef: tempBookingRef,
            originalBookingRef: originalBookingRef,
            token: token
        )
    }

    public func bookingConfirmationWithAmendSummary(
        reservationDetails: ReservationDetails,
        originalBookingRef: String
    ) throws -> Resource<(
        Reservation,
        AmendSummary
    )> {
        try service(for: .getReservationWithAmendSummary).bookingConfirmationWithAmendSummary(
            reservationDetails: reservationDetails,
            originalBookingRef: originalBookingRef
        )
    }

    public func bookingInformation(basketReference: String, isBusiness: Bool) throws -> Resource<BookingInformation> {
        try service(for: .bookingInformation).bookingInformation(basketReference: basketReference, isBusiness: isBusiness)
    }

    public func getHotelBySlug(slug: String) throws -> Resource<Hotel> {
        try service(for: .getHotelBySlug).getHotelBySlug(slug: slug)
    }

    public func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool = false
    ) throws -> Resource<ConfirmPreCheckInOut> {
        try service(for: .confirmPreCheckInOut).confirmPreCheckInOut(
            basketReference: basketReference,
            type: type,
            isCiol: isCiol
        )
    }

    public func getCategoryLabels(labelType: LabelsConfig) throws -> Resource<CategoryLabels> {
        try service(for: .getCategoryLabels).getCategoryLabels(labelType: labelType)
    }

    public func getHotelPreferences(hotelCode: String) throws -> Resource<[HotelPreference]> {
        try service(for: .getHotelPreferences).getHotelPreferences(hotelCode: hotelCode)
    }

    public func getRoomClassConfig(hotelBrand: HotelBrand?, channel: Channel) throws -> Resource<RoomClassConfig> {
        try service(for: .roomClassConfig).getRoomClassConfig(hotelBrand: hotelBrand, channel: channel)
    }

    public func resendInvoiceEmail(reservation: Reservation) throws -> Resource<Bool> {
        try service(for: .resendInvoiceEmail).resendInvoiceEmail(reservation: reservation)
    }

    public func homepageAppsContent(
        channel: Channel,
        subchannel: String,
        language: String,
        country: String
    ) throws -> Resource<HomepageAppsContent> {
        try service(for: .homepageContent).homepageAppsContent(
            channel: channel,
            subchannel: subchannel,
            language: language,
            country: country
        )
    }

    public func authorizePayment() throws -> Resource<CCCPPaymentProviderResponse> {
        try service(for: .authorizePayment).authorizePayment()
    }

    public func attachFileToReservation(params: AuthorizationFileAttachmentParams) throws -> Resource<StatusResult> {
        try service(for: .attachFileToReservation).attachFileToReservation(params: params)
    }

    public func updatePreCheckInStatus(params: UpdatePrecheckInParams) throws -> Resource<StatusResult> {
        try service(for: .updatePreCheckInStatus).updatePreCheckInStatus(params: params)
    }

    public func generateOTP(email: String) throws -> Resource<Bool> {
        try service(for: .generateOTP).generateOTP(email: email)
    }

    public func verifyOTP(bookingReference: String, otpCode: String) throws -> Resource<Bool> {
        try service(for: .verifyOTP).verifyOTP(bookingReference: bookingReference, otpCode: otpCode)
    }

    public func digitalKeyProvision(
        bookingReference: String,
        otpCode: String,
        email: String,
        reservationId: String
    ) throws -> Resource<DigitalKeyProvisionResponse> {
        try service(for: .provisionKey).digitalKeyProvision(
            bookingReference: bookingReference,
            otpCode: otpCode,
            email: email,
            reservationId: reservationId
        )
    }

    public func digitalKeyCheckIn(reservationId: String, hotelCode: String) throws -> Resource<DigitalKeyCheckInResponse> {
        try service(for: .keyCheckIn).digitalKeyCheckIn(reservationId: reservationId, hotelCode: hotelCode)
    }

    public func getPromotionsInformation(criteria: PromotionsInformationCriteria) throws -> Resource<PromotionsInformation> {
        try service(for: .promotionsInformation).getPromotionsInformation(criteria: criteria)
    }

    public func validateDiscountCode(criteria: PromotionsInformationCriteria) throws
        -> Resource<ValidateDiscountCodeResult> {
        try service(for: .validateDiscountCode).validateDiscountCode(criteria: criteria)
    }

    public func updateCiolStatus(payload: UpdateCiolStatusPayload) throws -> Resource<UpdateCiolStatusResponse> {
        try service(for: .updateCiolStatus).updateCiolStatus(payload: payload)
    }

    public func ciolPaymentActions(
        basketReference: String
    ) throws -> Resource<CiolPaymentActionsResponse> {
        try service(for: .ciolPaymentActions)
            .ciolPaymentActions(basketReference: basketReference)
    }

    public func ciolBackgroundCharge(
        basketReference: String,
        token: String
    ) throws -> Resource<CiolBackgroundChargeResponse> {
        try service(for: .ciolBackgroundCharge)
            .ciolBackgroundCharge(
                basketReference: basketReference,
                token: token
            )
    }

    public func initMobileSDKPayment(basketId: String) throws -> Resource<DatatransPaymentSessionResponse> {
        try service(for: .initMobileSDKPayment)
            .initMobileSDKPayment(basketId: basketId)
    }
}
