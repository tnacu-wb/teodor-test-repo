//
//  WebserviceProtocol.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 05/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

public protocol WebserviceProtocol {
    func searchAvailabilities(
        bookingDetails: BookingDetails,
        suggestion: Suggestion,
        page: Int?,
        sorting: AvailabilitiesSorting,
        allowEmployeeOffer: Bool
    ) throws -> Resource<AvailabilitiesResponse>
    func hotelAvailability(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        bookingDetails: BookingDetails,
        allowEmployeeOffer: Bool
    ) throws -> Resource<HotelAvailabilityResponse>
    func hotelAvailabilityForAmendBooking(
        withHotelCode hotelCode: String,
        bookingDetails: BookingDetails,
        brand: HotelBrand?,
        allowEmployeeOffer: Bool
    ) throws -> Resource<HotelAvailabilityResponse>
    func holdBooking(bookingDetails: BookingDetails, sensorData: String) throws -> Resource<String>
    func getTotalCostWithCityTax(bookingDetails: BookingDetails) throws -> Resource<Cost>
    func getRestrictions() throws -> Resource<[Restrictions]>
    func holdBookingWithGuests(bookingDetails: BookingDetails, isCiolFlow: Bool, isRegCard: Bool) throws -> Resource<Bool>
    func releaseBooking(basketReference: String, hotelId: String?) throws -> Resource<Bool>
    func checkBasketStatus(basketReference: String?) throws -> Resource<BookingConfirmation>
    func reservation(reservationDetails: ReservationDetails, hotelCode: String?, bookingDetails: BookingDetails?) throws
        -> Resource<Reservation>
    func cancelReservation(reservationDetails: ReservationDetails, hotelCode: String?) throws -> Resource<PIDictionary>
    func updateReservationPreferences(
        hotelCode: String,
        reservationIds: [String],
        preferencesCollections: [PreferencesCollection]
    ) throws -> Resource<PIDictionary>
    func suggestions(searchTerm: String) throws -> Resource<[PISuggestion]>
    func login(username: String, password: String, isBusiness: Bool, completion: @escaping (Result<Bool>) -> Void)
    func addressLookup(postCode: String) throws -> Resource<[AddressSummary]>
    func addressLookup(postCode: String, id: String) throws -> Resource<Address>
    func logout() throws
    func forgotPassword(emailAddress: String, isBusiness: Bool) throws -> Resource<Bool>
    func completePayment(with sessionId: String, and paRes: String) throws -> Resource<Bool>
    func getHotel(with hotelCode: String) throws -> Resource<Hotel>
    func getUserCredentials(completion: @escaping (Result<Bool>) -> Void)
    func getUser(userId: String, isBusiness: Bool, completion: @escaping (Result<Resource<User>>) -> Void)
	func getCompany(companyId: String, sensorData: String) throws -> Resource<Company>
    func getStays() throws -> Resource<[Stay]>
	func savePaymentCard(for user: User, sensorData: String) throws -> Resource<Bool>
    func initiateSaveCard(initiateSaveCardParameters: InitiateSaveCardParameters) throws
        -> Resource<CCCPPaymentProviderResponse>
    func anonymousNewsletterPreferences(email: String, countryOfResidence: String) throws
        -> Resource<AnonymousNewsletterPreferences>
	func updateUserAdditionalGuests(user: User, sensorData: String) throws -> Resource<Bool>
	func updateUserDetails(user: User, sensorData: String) throws -> Resource<Bool>
    func deleteUser() throws -> Resource<Bool>
	func deletePaymentCard(for user: User, sensorData: String) throws -> Resource<Bool>
	func register(registerParameters: RegisterParameters, sensorData: String) throws -> Resource<Bool>
	func changePassword(user: User, existingPassword: String, newPassword: String, sensorData: String) throws
	    -> Resource<Bool>
	func updateFoodPreference(user: User, sensorData: String) throws -> Resource<Bool>
	func updateRoomPreference(user: User, sensorData: String) throws -> Resource<Bool>
    func startCheckInOnlineSession(with params: StartCheckInRequestParameters) throws
        -> Resource<CheckInOnlineSessionResponse>
    func addCheckInOnlineGuestDetails(with params: AddCheckInOnlineGuestDetailsParameters) throws -> Resource<Bool>
    func addCheckInOnlineUpsells(
        withSessionID sessionID: String,
        confirmationNumber: String,
        andUpsells upsells: [UpsellItem]
    ) throws -> Resource<CheckInOnlineAddUpsellsResponse>
    func closeCheckInOnlineSession(withSessionID sessionID: String) throws -> Resource<Bool>
    func checkInOnlinePayment(with sessionId: String, confirmationNumber: String, paymentDetails: PaymentDetails) throws
        -> Resource<CheckInPaymentResponse>
    func getWalletPass(with reservationDetails: ReservationDetails, excludeBarcode: Bool) throws -> Resource<Data>
    func getMarketingPreferences(for emailAddress: String, and brandCodes: MarketingBrandCode, isBusiness: Bool) throws
        -> Resource<MarketingPreferences>
    func updateMarketingPreferences(
        brands: [MarketingBrandCode],
        emailAddress: String,
        optIn: Bool,
        isoCountryCode: String
    ) throws -> Resource<Bool>
    func getDashboardComponents(
        surname: String?,
        arrival: Date?,
        reservationId: String?,
        isBusiness: Bool,
        recentSearchesFlag: Bool
    ) throws -> Resource<[DashboardComponent]>
    func cccpPayment(
        with paymentParams: CCCPPaymentParams,
        and stayDetails: CCCPStayDetails,
        and sessionId: String?,
        and isCiol: Bool,
        sensorData: String
    ) throws -> Resource<CCCPPaymentResponse>
    func paymentMethods(for bookingDetails: BookingDetails, hotel: Hotel, rate: Rate, user: User?, isCiol: Bool) throws
        -> Resource<PaymentMethodsResponse>
    func getDoorKey(reservationDetails: ReservationDetails, deviceId: String, authenticationCode: String?) throws
        -> Resource<String>
    func getCountries() throws -> Resource<[Country]>
    func headerInformation() throws -> Resource<HeaderInformation>
    func saveUpsellsToBooking(bookingDetails: BookingDetails) throws -> Resource<Bool>
    func findBookingSource(findBookingDetails: FindBookingDetails) throws -> Resource<FindBookingSource>
    func ratesInformation(ratePlans: [String], hotelCode: String, hotelBrand: HotelBrand?) throws
        -> Resource<[RateInformation]>
    func copyBooking(reservationDetails: ReservationDetails) throws -> Resource<String>
    func addNewRoom(roomCriteria: AmendRoomCriteria) throws -> Resource<String>
    func removeRoom(roomCriteria: AmendRoomCriteria) throws -> Resource<String>
    func amendPackages(packagesDetails: AmendPackagesDetail) throws -> Resource<Bool>
    func confirmAmendLogic(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        selectedPaymentOption: String
    ) throws -> Resource<CCCPPaymentResponse>
    func amendBookingDates(temporaryReference: String, arrivalDate: Date, departureDate: Date, token: String) throws
        -> Resource<String>
    func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool
    ) throws -> Resource<([UpsellItem], [UpsellItem], CityTaxResponse, GoshPackage?)>
    func amendEditRoom(amendEditDetails: AmendRoomCriteria) throws -> Resource<String>
    func reservationForAmend(reservationDetails: ReservationDetails, hotelCode: String?) throws -> Resource<Reservation>
    func amendSummary(reservationDetails: ReservationDetails, temporaryReference: String) throws -> Resource<AmendSummary>
    func amendConfirmationPrices(tempBookingRef: String, originalBookingRef: String, token: String) throws
        -> Resource<AmendConfirmationPrices>
    func bookingConfirmationWithAmendSummary(reservationDetails: ReservationDetails, originalBookingRef: String) throws
        -> Resource<(
            Reservation,
            AmendSummary
        )>
    func bookingInformation(basketReference: String, isBusiness: Bool) throws -> Resource<BookingInformation>
    func getHotelBySlug(slug: String) throws -> Resource<Hotel>
    func confirmPreCheckInOut(basketReference: String, type: CiolRequestType, isCiol: Bool) throws
        -> Resource<ConfirmPreCheckInOut>
    func getCategoryLabels(labelType: LabelsConfig) throws -> Resource<CategoryLabels>
    func getHotelPreferences(hotelCode: String) throws -> Resource<[HotelPreference]>
    func getRoomClassConfig(hotelBrand: HotelBrand?, channel: Channel) throws -> Resource<RoomClassConfig>
    func amendCiolPackages(amendInfo: CiolAmendInfo) throws -> Resource<Bool>
    func homepageAppsContent(channel: Channel, subchannel: String, language: String, country: String) throws
        -> Resource<HomepageAppsContent>
    func resendInvoiceEmail(reservation: Reservation) throws -> Resource<Bool>
    func authorizePayment() throws -> Resource<CCCPPaymentProviderResponse>
    func attachFileToReservation(params: AuthorizationFileAttachmentParams) throws -> Resource<StatusResult>
    func updatePreCheckInStatus(params: UpdatePrecheckInParams) throws -> Resource<StatusResult>
    func generateOTP(email: String) throws -> Resource<Bool>
    func verifyOTP(bookingReference: String, otpCode: String) throws -> Resource<Bool>
    func digitalKeyProvision(bookingReference: String, otpCode: String, email: String, reservationId: String) throws
        -> Resource<DigitalKeyProvisionResponse>
    func digitalKeyCheckIn(reservationId: String, hotelCode: String) throws -> Resource<DigitalKeyCheckInResponse>
    func getPromotionsInformation(criteria: PromotionsInformationCriteria) throws -> Resource<PromotionsInformation>
    func validateDiscountCode(criteria: PromotionsInformationCriteria) throws -> Resource<ValidateDiscountCodeResult>
    func updateCiolStatus(payload: UpdateCiolStatusPayload) throws -> Resource<UpdateCiolStatusResponse>
    func ciolPaymentActions(basketReference: String) throws -> Resource<CiolPaymentActionsResponse>
    func ciolBackgroundCharge(
        basketReference: String,
        token: String
    ) throws -> Resource<CiolBackgroundChargeResponse>
    func initMobileSDKPayment(basketId: String) throws -> Resource<DatatransPaymentSessionResponse>
}

extension WebserviceProtocol {
    func searchAvailabilities(
        bookingDetails: BookingDetails,
        suggestion: Suggestion,
        page: Int?,
        sorting: AvailabilitiesSorting,
        allowEmployeeOffer: Bool
    ) throws -> Resource<AvailabilitiesResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    public func hotelAvailability(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        bookingDetails: BookingDetails,
        allowEmployeeOffer: Bool
    ) throws -> Resource<HotelAvailabilityResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    public func hotelAvailabilityForAmendBooking(
        withHotelCode hotelCode: String,
        bookingDetails: BookingDetails,
        brand: HotelBrand?,
        allowEmployeeOffer: Bool
    ) throws -> Resource<HotelAvailabilityResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    public func holdBooking(bookingDetails: BookingDetails, sensorData: String) throws -> Resource<String> {
        throw WebserviceError.notImplemented(#function)
    }

    public func getTotalCostWithCityTax(bookingDetails: BookingDetails) throws -> Resource<Cost> {
        throw WebserviceError.notImplemented(#function)
    }

    public func holdBookingWithGuests(
        bookingDetails: BookingDetails,
        isCiolFlow: Bool,
        isRegCard: Bool
    ) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    public func releaseBooking(basketReference: String, hotelId: String?) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    public func checkBasketStatus(basketReference: String?) throws -> Resource<BookingConfirmation> {
        throw WebserviceError.notImplemented(#function)
    }

    public func getRestrictions() throws -> Resource<[Restrictions]> {
        throw WebserviceError.notImplemented(#function)
    }

    public func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?
    ) throws -> Resource<Reservation> {
        throw WebserviceError.notImplemented(#function)
    }

    public func cancelReservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?
    ) throws -> Resource<PIDictionary> {
        throw WebserviceError.notImplemented(#function)
    }

    public func updateReservationPreferences(
        hotelCode: String,
        reservationIds: [String],
        preferencesCollections: [PreferencesCollection]
    ) throws -> Resource<PIDictionary> {
        throw WebserviceError.notImplemented(#function)
    }

    public func suggestions(searchTerm: String) throws -> Resource<[PISuggestion]> {
        throw WebserviceError.notImplemented(#function)
    }

    public func addressLookup(postCode: String) throws -> Resource<[AddressSummary]> {
        throw WebserviceError.notImplemented(#function)
    }

    public func addressLookup(postCode: String, id: String) throws -> Resource<Address> {
        throw WebserviceError.notImplemented(#function)
    }

    public func login(
        username: String,
        password: String,
        isBusiness: Bool = false,
        completion: @escaping (Result<Bool>) -> Void
    ) {
        completion(.failure(error: WebserviceError.notImplemented(#function)))
    }

	public func logout() throws {
		throw WebserviceError.notImplemented(#function)
	}

    public func forgotPassword(emailAddress: String, isBusiness: Bool) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    public func completePayment(with sessionId: String, and paRes: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    public func getHotel(with hotelCode: String) throws -> Resource<Hotel> {
        throw WebserviceError.notImplemented(#function)
    }

    public func getStays() throws -> Resource<[Stay]> {
        throw WebserviceError.notImplemented(#function)
    }

    public func getUserCredentials(completion: @escaping (Result<Bool>) -> Void) {
        completion(.failure(error: WebserviceError.notImplemented(#function)))
    }

    public func getUser(userId: String, isBusiness: Bool, completion: @escaping (Result<Resource<User>>) -> Void) {
        completion(.failure(error: WebserviceError.notImplemented(#function)))
    }

	public func getCompany(companyId: String, sensorData: String) throws -> Resource<Company> {
        throw WebserviceError.notImplemented(#function)
    }

	public func savePaymentCard(for user: User, sensorData: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    public func initiateSaveCard(initiateSaveCardParameters: InitiateSaveCardParameters) throws
        -> Resource<CCCPPaymentProviderResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    func anonymousNewsletterPreferences(
        email: String,
        countryOfResidence: String
    ) throws -> Resource<AnonymousNewsletterPreferences> {
        throw WebserviceError.notImplemented(#function)
    }
	public func updateUserAdditionalGuests(user: User, sensorData: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

	public func updateUserDetails(user: User, sensorData: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    public func deleteUser() throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

	public func deletePaymentCard(for user: User, sensorData: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

	public func register(registerParameters: RegisterParameters, sensorData: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

	public func changePassword(
	    user: User,
	    existingPassword: String,
	    newPassword: String,
	    sensorData: String
	) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

	public func updateFoodPreference(user: User, sensorData: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

	public func updateRoomPreference(user: User, sensorData: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    public func startCheckInOnlineSession(with params: StartCheckInRequestParameters) throws
        -> Resource<CheckInOnlineSessionResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    public func addCheckInOnlineGuestDetails(with params: AddCheckInOnlineGuestDetailsParameters) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    func addCheckInOnlineUpsells(
        withSessionID sessionID: String,
        confirmationNumber: String,
        andUpsells upsells: [UpsellItem]
    ) throws -> Resource<CheckInOnlineAddUpsellsResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    public func closeCheckInOnlineSession(withSessionID sessionID: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    public func checkInOnlinePayment(
        with sessionId: String,
        confirmationNumber: String,
        paymentDetails: PaymentDetails
    ) throws -> Resource<CheckInPaymentResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    public func getWalletPass(with reservationDetails: ReservationDetails, excludeBarcode: Bool) throws -> Resource<Data> {
        throw WebserviceError.notImplemented(#function)
    }

    func getMarketingPreferences(
        for emailAddress: String,
        and brandCodes: MarketingBrandCode,
        isBusiness: Bool
    ) throws -> Resource<MarketingPreferences> {
        throw WebserviceError.notImplemented(#function)
    }

    func updateMarketingPreferences(
        brands: [MarketingBrandCode],
        emailAddress: String,
        optIn: Bool,
        isoCountryCode: String
    ) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    func getDashboardComponents(
        surname: String?,
        arrival: Date?,
        reservationId: String?,
        isBusiness: Bool,
        recentSearchesFlag: Bool
    ) throws -> Resource<[DashboardComponent]> {
        throw WebserviceError.notImplemented(#function)
    }

    public func getCountries() throws -> Resource<[Country]> {
        throw WebserviceError.notImplemented(#function)
    }

    public func cccpPayment(
        with paymentParams: CCCPPaymentParams,
        and stayDetails: CCCPStayDetails,
        and sessionId: String?,
        and isCiol: Bool,
        sensorData: String
    ) throws -> Resource<CCCPPaymentResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    public func paymentMethods(
        for bookingDetails: BookingDetails,
        hotel: Hotel,
        rate: Rate,
        user: User?
    ) throws -> Resource<PaymentMethodsResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    func getDoorKey(
        reservationDetails: ReservationDetails,
        deviceId: String,
        authenticationCode: String?
    ) throws -> Resource<String> {
        throw WebserviceError.notImplemented(#function)
    }

    public func headerInformation() throws -> Resource<HeaderInformation> {
        throw WebserviceError.notImplemented(#function)
    }

    public func saveUpsellsToBooking(bookingDetails: BookingDetails) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    func findBookingSource(findBookingDetails: FindBookingDetails) throws -> Resource<FindBookingSource> {
        throw WebserviceError.notImplemented(#function)
    }

    public func ratesInformation(
        ratePlans: [String],
        hotelCode: String,
        hotelBrand: HotelBrand?
    ) throws -> Resource<[RateInformation]> {
        throw WebserviceError.notImplemented(#function)
    }

    func copyBooking(reservationDetails: ReservationDetails) throws -> Resource<String> {
        throw WebserviceError.notImplemented(#function)
    }

    func amendPackages(packagesDetails: AmendPackagesDetail) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    func amendCiolPackages(amendInfo: CiolAmendInfo) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    func confirmAmendLogic(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        selectedPaymentOption: String
    ) throws -> Resource<CCCPPaymentResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    public func amendBookingDates(
        temporaryReference: String,
        arrivalDate: Date,
        departureDate: Date,
        token: String
    ) throws -> Resource<String> {
        throw WebserviceError.notImplemented(#function)
    }

    func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool
    ) throws -> Resource<([UpsellItem], [UpsellItem], CityTaxResponse, GoshPackage?)> {
        throw WebserviceError.notImplemented(#function)
    }

    public func addNewRoom(roomCriteria: AmendRoomCriteria) throws -> Resource<String> {
        throw WebserviceError.notImplemented(#function)
    }

    public func removeRoom(roomCriteria: AmendRoomCriteria) throws -> Resource<String> {
        throw WebserviceError.notImplemented(#function)
    }

    public func amendEditRoom(amendEditDetails: AmendRoomCriteria) throws -> Resource<String> {
        throw WebserviceError.notImplemented(#function)
    }

    public func reservationForAmend(
        reservationDetails: ReservationDetails,
        hotelCode: String?
    ) throws -> Resource<Reservation> {
        throw WebserviceError.notImplemented(#function)
    }

    public func amendSummary(
        reservationDetails: ReservationDetails,
        temporaryReference: String
    ) throws -> Resource<AmendSummary> {
        throw WebserviceError.notImplemented(#function)
    }

    public func amendConfirmationPrices(
        tempBookingRef: String,
        originalBookingRef: String,
        token: String
    ) throws -> Resource<AmendConfirmationPrices> {
        throw WebserviceError.notImplemented(#function)
    }

    public func bookingConfirmationWithAmendSummary(
        reservationDetails: ReservationDetails,
        originalBookingRef: String
    ) throws -> Resource<(
        Reservation,
        AmendSummary
    )> {
        throw WebserviceError.notImplemented(#function)
    }

    public func bookingInformation(basketReference: String, isBusiness: Bool) throws -> Resource<BookingInformation> {
        throw WebserviceError.notImplemented(#function)
    }

    public func getHotelBySlug(slug: String) throws -> Resource<Hotel> {
        throw WebserviceError.notImplemented(#function)
    }

    public func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool
    ) throws -> Resource<ConfirmPreCheckInOut> {
        throw WebserviceError.notImplemented(#function)
    }

    public func getCategoryLabels(labelType: LabelsConfig) throws -> Resource<CategoryLabels> {
        throw WebserviceError.notImplemented(#function)
    }

    func getHotelPreferences(hotelCode: String) throws -> Resource<[HotelPreference]> {
        throw WebserviceError.notImplemented(#function)
    }

    func getRoomClassConfig(hotelBrand: HotelBrand?, channel: Channel) throws -> Resource<RoomClassConfig> {
        throw WebserviceError.notImplemented(#function)
    }

    public func resendInvoiceEmail(reservation: Reservation) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    public func homepageAppsContent(
        channel: Channel,
        subchannel: String,
        language: String,
        country: String
    ) throws -> Resource<HomepageAppsContent> {
        throw WebserviceError.notImplemented(#function)
    }

    func authorizePayment() throws -> Resource<CCCPPaymentProviderResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    func attachFileToReservation(params: AuthorizationFileAttachmentParams) throws -> Resource<StatusResult> {
        throw WebserviceError.notImplemented(#function)
    }

    func updatePreCheckInStatus(params: UpdatePrecheckInParams) throws -> Resource<StatusResult> {
        throw WebserviceError.notImplemented(#function)
    }

    func generateOTP(email: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    func verifyOTP(bookingReference: String, otpCode: String) throws -> Resource<Bool> {
        throw WebserviceError.notImplemented(#function)
    }

    func digitalKeyProvision(
        bookingReference: String,
        otpCode: String,
        email: String,
        reservationId: String
    ) throws -> Resource<DigitalKeyProvisionResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    func digitalKeyCheckIn(reservationId: String, hotelCode: String) throws -> Resource<DigitalKeyCheckInResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    func getPromotionsInformation(criteria: PromotionsInformationCriteria) throws -> Resource<PromotionsInformation> {
        throw WebserviceError.notImplemented(#function)
    }

    func validateDiscountCode(criteria: PromotionsInformationCriteria) throws -> Resource<ValidateDiscountCodeResult> {
        throw WebserviceError.notImplemented(#function)
    }

    func updateCiolStatus(payload: UpdateCiolStatusPayload) throws -> Resource<UpdateCiolStatusResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    func paymentMethods(
        for bookingDetails: BookingDetails,
        hotel: Hotel,
        rate: Rate,
        user: User?,
        isCiol: Bool
    ) throws -> Resource<PaymentMethodsResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    func ciolPaymentActions(
        basketReference: String
    ) throws -> Resource<CiolPaymentActionsResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    func ciolBackgroundCharge(
        basketReference: String,
        token: String
    ) throws -> Resource<CiolBackgroundChargeResponse> {
        throw WebserviceError.notImplemented(#function)
    }

    func initMobileSDKPayment(basketId: String) throws -> Resource<DatatransPaymentSessionResponse> {
        throw WebserviceError.notImplemented(#function)
    }
}
