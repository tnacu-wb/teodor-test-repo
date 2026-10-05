//
//  PreStayInteractor.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

enum CIOLError: Error {
    case performPrestayChecks
    case updateUserDetails
    case getHotelPreferences
    case updateHotelPreferences
    case regCardCheck
    case genericPayment
    case regCardFailed

    var title: String {
        switch self {
        case .performPrestayChecks,
                .updateUserDetails,
                .getHotelPreferences,
                .updateHotelPreferences,
                .regCardCheck,
                .genericPayment,
                .regCardFailed:
            return PILocalizedString("somethingWentWrongMessage")
        }
    }

    var body: String? {
        switch self {
        case .performPrestayChecks, .getHotelPreferences, .updateHotelPreferences:

            // Check if this is a PIBA CNP booking and use appropriate error message
             return BookingDetails.sharedInstance.isPIBACNP
                 ? PILocalizedString("ciolPibaCnpCheckInErrorMessage")
                 : PILocalizedString("ciolCheckErrorMessage")
        case .updateUserDetails:
            return PILocalizedString("ciolSaveDetailsErrorMessage")
        case .regCardCheck:
            return PILocalizedString("guestListUpdateError")
        case .genericPayment:
            return PILocalizedString("ciolPaymentErrorMessage")
        case .regCardFailed:
            return PILocalizedString("ciolRegCardErrorMessage")
        }
    }
}

protocol PreStayDataProvider: RegCardProvider, ReservationProvider {
    func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool,
        completion: @escaping (_ response: ([UpsellItem], [UpsellItem], CityTaxResponse, GoshPackage?)?, _ error: Error?)
        -> Void
    )
    func performHoldBookingWithGuests(
        bookingDetails: BookingDetails,
        isCiolFlow: Bool,
        isRegCard: Bool,
        completion: @escaping (_ success: Bool, _ error: Error?) -> Void
    )
    func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool,
        completion: @escaping (_ response: ConfirmPreCheckInOut?, _ error: Error?) -> Void
    )
    func getHotelPreferences(
        hotelCode: String,
        completion: @escaping (_ hotelPreference: [HotelPreference]?, _ error: Error?) -> Void
    )
    func updateReservationPreferences(
        hotelCode: String,
        reservationIds: [String],
        preferencesCollections: [PreferencesCollection],
        completion: @escaping (_ confirmation: PIDictionary?, _ error: Error?) -> Void
    )
    func ciolBackgroundCharge(
        basketReference: String,
        token: String,
        completion: @escaping (
            _ response: CiolBackgroundChargeResponse?,
            _ error: Error?
        ) -> Void
    )
}

extension RequestsManager: PreStayDataProvider { }

final class PreStayInteractor {
    weak var output: PreStayInteractorOutputProtocol?

    private let dataProvider: PreStayDataProvider

    private var preStayInputParams: PreStayInputParams
    private let settingsManager: SettingsManager
    private let bookingDetails: BookingDetails

    init(
        dataProvider: PreStayDataProvider = RequestsManager(),
        preStayInputParams: PreStayInputParams,
        settingsManager: SettingsManager = .sharedInstance,
        bookingDetails: BookingDetails = .sharedInstance
    ) {
        self.dataProvider = dataProvider
        self.preStayInputParams = preStayInputParams
        self.settingsManager = settingsManager
        self.bookingDetails = bookingDetails
    }
}

extension PreStayInteractor: PreStayInteractorProtocol {
    var customAnalyticsParameters: PIDictionary? { analyticsDefaultUserInfo() }

    struct ViewModel: PreStayViewModel {
        var email: String?
        var contactNumber: String?
        var formattedAddress: String
        var bookingSummaryViewModel: BookingSummaryCIOLViewModelProtocol
        var bookerViewModel: CIOLPersonViewModelProtocol
        var roomsViewModel: [RoomGuestsViewModelProtocol]
        var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol
        var hotelBrand: HotelBrand?
        var selectedPreference: HotelPreferenceViewModel?
        var hotelPreferences: [HotelPreferenceViewModel]?
        var isSpecialOccasionOn: Bool
        var showOccasionError: Bool

        let isDirect: Bool
        let shouldSurfaceErrorMessages: Bool
    }

    struct BookingSummaryCIOLViewModel: BookingSummaryCIOLViewModelProtocol {
        var image: URL?
        var hotelName: String?
        var duration: String?
        var summary: String?
    }

    struct CIOLPersonViewModel: CIOLPersonViewModelProtocol {
        var title: String?
        var firstName: String?
        var lastName: String?
        var country: CountryItem?
        var address: Address?
        var passportNumber: String?
        var warningMessageState: GuestWarningMessageState = .none
        var formattedBookerInfo: String? {
            var elementsToFormat = [String]()
            if let title = title { elementsToFormat.append(title) }
            if let firstName = firstName { elementsToFormat.append(firstName) }
            if let lastName = lastName { elementsToFormat.append(lastName) }
            return elementsToFormat.isEmpty ? nil : elementsToFormat.joined(separator: " ")
        }
    }

    struct RoomGuestsViewModel: RoomGuestsViewModelProtocol {
        var leadGuest: CIOLPersonViewModelProtocol
        var accompanyingGuest: CIOLPersonViewModelProtocol?
        var adultsCount: Int
        var childrenCount: Int
        var childrenCountDescription: String?
        var roomID: String
    }

    struct CIOLPriceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol {
        var ctaTitle: String
        var totalValue: String
        var displayTotalValue: Bool { !totalValue.isEmpty }
        var items: [CIOLPriceBreakdownItemViewModelProtocol]
    }

    struct CIOLPriceBreakdownItemViewModel: CIOLPriceBreakdownItemViewModelProtocol {
        var name: String
        var value: Cost
        var quantity: Int
        var formattedName: String { quantity > 1 ? "\(quantity)x \(name)" : name }
    }

    var bookingSummaryViewModel: BookingSummaryCIOLViewModel {
        BookingSummaryCIOLViewModel(
            image: preStayInputParams.hotelImage,
            hotelName: preStayInputParams.hotelName,
            duration: datesSummary,
            summary: staySummary
        )
    }

    var bookerViewModel: CIOLPersonViewModel {
        CIOLPersonViewModel(
            title: preStayInputParams.leadBookerTitle,
            firstName: preStayInputParams.leadBookerFirstName,
            lastName: preStayInputParams.leadBookerLastName,
            country: CountryItem(country: preStayInputParams.address?.country),
            address: preStayInputParams.address
        )
    }

    var roomsViewModels: [RoomGuestsViewModel] {
        preStayInputParams.rooms.enumerated().map { index, room in
            let shouldLeadGuestRequireConfirmation = leadGuestNeedsConfirmation(at: index)
            let leadGuestViewModel = createGuestViewModel(
                room.leadGuest,
                warningMessageState: shouldLeadGuestRequireConfirmation
            )

            let shouldAccompanyingGuestRequireConfirmation = secondGuestNeedsConfirmation(at: index)
            let accompanyingGuestViewModel = createGuestViewModel(
                room.accompanyingGuest,
                warningMessageState: shouldAccompanyingGuestRequireConfirmation
            )

            return RoomGuestsViewModel(
                leadGuest: leadGuestViewModel,
                accompanyingGuest: accompanyingGuestViewModel,
                adultsCount: room.adults,
                childrenCount: room.children,
                childrenCountDescription: room.childrenCountDescription,
                roomID: room.roomId ?? ""
            )
        }
    }

    private func createGuestViewModel(_ guest: User?, warningMessageState: GuestWarningMessageState) -> CIOLPersonViewModel {
        let leadGuestCountry = CountryItem(country: guest?.country)
        return CIOLPersonViewModel(
            title: guest?.title,
            firstName: guest?.firstName,
            lastName: guest?.lastName,
            country: leadGuestCountry,
            address: guest?.address,
            passportNumber: guest?.passport?.number,
            warningMessageState: warningMessageState
        )
    }

    /// Builds the CIOL price breakdown view model.
    ///
    /// **Third Party PrePaid (no outstanding balance) City Tax:**
    /// - Business requires charging a city tax for pre-paid third party bookings
    /// - Header displays the city tax which is to be paid
    /// - Breakdown shows "City tax fees"
    ///
    /// ** Third Party Outstanding balance:**
    /// - Outstanding balance for all the third party bookings
    /// - This is received from the `CiolPaymentActionsResponse`
    ///
    /// **PIBA CNP (Card Not Present):**
    /// - Header displays £0.00 (customer owes nothing during CIOL)
    /// - Breakdown shows "Paid by company" with full booking total (company is charged)
    ///
    /// **PIBA CP (Card Present):**
    /// - Header displays the amount due now (typically the full amount)
    /// - Breakdown shows "Paid by you" with the amount due now
    ///
    /// **Standard payments:**
    /// - Header displays outstanding balance
    /// - Breakdown shows outstanding balance item
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModel {
        if let thirdPartyCityTax {
            return getThirdPartyCityTaxPriceBreakdown(cityTax: thirdPartyCityTax)
        }

        if let outstandingCost = preStayInputParams.ciolPaymentActions?.outstandingBalance {
            return getThirdPartyOutstandingBalancePriceBreakdown(cost: outstandingCost)
        }

        let isPIBACNP = preStayInputParams.stay.paymentOption == .pibaCardNotPresent
        let isPIBACP = preStayInputParams.stay.paymentOption == .pibaCardPresent

        if isPIBACNP, let totalCost = preStayInputParams.stay.totalCost {
            let zeroOutstandingValue = Cost(amount: 0.0, currencyCode: totalCost.currencyCode).localizedValue
            let items: [CIOLPriceBreakdownItemViewModelProtocol] = [
                CIOLPriceBreakdownItemViewModel(
                    name: PILocalizedString("ciolPriceBreakdownPaidByCompany"),
                    value: totalCost,
                    quantity: 1
                )
            ]

            return CIOLPriceBreakdownViewModel(
                ctaTitle: PILocalizedString("Continue"),
                totalValue: zeroOutstandingValue,
                items: items
            )
        }

        guard let outstandingCost = preStayInputParams.outstandingBalance else {
            return CIOLPriceBreakdownViewModel(ctaTitle: PILocalizedString("Continue"), totalValue: "", items: [])
        }

        let labelItem = isPIBACP
            ? PILocalizedString("ciolPriceBreakdownPaidByYou")
            : PILocalizedString("preStayOutstandingBalance")
        let outstandingItem = CIOLPriceBreakdownItemViewModel(
            name: labelItem,
            value: outstandingCost,
            quantity: 1
        )

        return CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: outstandingCost.localizedValue,
            items: [outstandingItem]
        )
    }

    private func getThirdPartyCityTaxPriceBreakdown(cityTax: Cost) -> CIOLPriceBreakdownViewModel {
        CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: cityTax.localizedValue,
            items: [
                CIOLPriceBreakdownItemViewModel(
                    name: PILocalizedString("ciolCityTaxDisclaimerTitle"),
                    value: cityTax,
                    quantity: 1
                )
            ]
        )
    }

    private func getThirdPartyOutstandingBalancePriceBreakdown(cost: Cost) -> CIOLPriceBreakdownViewModel {
        CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: cost.localizedValue,
            items: [
                CIOLPriceBreakdownItemViewModel(
                    name: PILocalizedString("preStayOutstandingBalance"),
                    value: cost,
                    quantity: 1
                )
            ]
        )
    }

    private var formattedAddress: String {
        let line1 = preStayInputParams.address?.line1 ?? ""
        let line2 = preStayInputParams.address?.line2 ?? ""
        let line3 = preStayInputParams.address?.line3 ?? ""
        let line4 = preStayInputParams.address?.line4 ?? ""
        let postalCode = preStayInputParams.address?.postcode ?? ""
        return [line4, line3, line2, line1, postalCode].filter { !$0.isEmpty }.joined(separator: ", ")
    }

    private var datesSummary: String {
        guard let arrivalDate = preStayInputParams.arrivalDate,
              let checkOutDate = preStayInputParams.checkOutDate else { return "" }

        return arrivalDate.localizedVeryShortStringFormat + " - " + checkOutDate.localizedVeryShortStringFormat
    }

    private var staySummary: String {
        let adultsCountDescription = preStayInputParams.adultsCountDescription

        let childrenCountDescription = preStayInputParams.childrenCount > 0 ?
        preStayInputParams.childrenCountDescription :
        ""

        let roomsCountDescription = preStayInputParams.rooms.count > 1 ? preStayInputParams.roomsCountDescription : ""
        let nightsCountDescription = preStayInputParams.nightsCountDescription

        return [adultsCountDescription, childrenCountDescription, roomsCountDescription, nightsCountDescription]
            .filter { !$0.isEmpty }.joined(separator: ", ")
    }

    var viewModel: PreStayViewModel {
        ViewModel(
            email: preStayInputParams.email,
            contactNumber: preStayInputParams.contactNumber,
            formattedAddress: formattedAddress,
            bookingSummaryViewModel: bookingSummaryViewModel,
            bookerViewModel: bookerViewModel,
            roomsViewModel: roomsViewModels,
            priceBreakdownViewModel: priceBreakdownViewModel,
            hotelBrand: preStayInputParams.hotelBrand,
            selectedPreference: selectedHotelPreferenceViewModel,
            hotelPreferences: preStayInputParams.hotelPreferences,
            isSpecialOccasionOn: preStayInputParams.isSpecialOccasionOn,
            showOccasionError: preStayInputParams.showSpecialOccasionError,
            isDirect: preStayInputParams.isDirect,
            shouldSurfaceErrorMessages: preStayInputParams.shouldSurfaceErrorMessages
        )
    }

    var shouldShowDeRegCard: Bool {
        preStayInputParams.hasDERegCard == false && preStayInputParams.hotelBrand == .premierInnGermany
    }

    var shouldShowUpsells: Bool {
        SettingsManager.sharedInstance.featureCIOLUpsells &&
        !preStayInputParams.isBusinessTrip &&
        preStayInputParams.isDirect
    }

    var thirdPartyCityTax: Cost? {
        preStayInputParams.ciolPaymentActions?.cityTax
    }

    private var selectedHotelPreferenceViewModel: HotelPreferenceViewModel? {
        preStayInputParams.hotelPreferences?.first(where: { selectedPreference in
            selectedPreference.code == preStayInputParams.selectedPreference?.code
        })
    }

    func viewOccasions() {
        output?.viewOccasions()
    }

    func updateSelectedPreference(with occasion: String) {
        let selectedPreference = viewModel.hotelPreferences?.first(where: { selectedPreference in
            selectedPreference.name == occasion
        })
        preStayInputParams.showSpecialOccasionError = false
        preStayInputParams.selectedPreference = selectedPreference
    }

    func updateSpecialOccasion(_ isOn: Bool) {
        preStayInputParams.isSpecialOccasionOn = isOn
    }

    func performCheckIn(completion: @escaping (_ success: Bool, _ error: Error?) -> Void) {
        if preStayInputParams.isSpecialOccasionOn && preStayInputParams.selectedPreference == nil {
            preStayInputParams.showSpecialOccasionError = true
            output?.reloadData(with: viewModel)
            completion(false, nil)
            return
        }

        updateUserDetails { [weak self] success in
            guard success else {
                return completion(false, CIOLError.updateUserDetails)
            }

            self?.updateReservationsPreference { success in
                guard success else {
                    return completion(false, CIOLError.updateHotelPreferences)
                }

                self?.performPreStayChecks { success in
                    completion(success, success ? nil : CIOLError.performPrestayChecks)
                }
            }
        }
    }

    func performPreStayChecks(completion: @escaping (Bool) -> Void) {
        if !preStayInputParams.isDirect &&
           settingsManager.featureThirdPartyPrepaid {
            handleThridPartyBookingFlow(completion: completion)
            return
        }

        guard let reservationId = preStayInputParams.reservationId,
              let bookingFlowId = preStayInputParams.bookingFlowId,
              let arrivalDate = preStayInputParams.arrivalDate,
              let checkOutDate = preStayInputParams.checkOutDate else {
            completion(false)
            return
        }

        let bookingDetails = BookingDetails()
        var criteria = Criteria()
        criteria.arrivalDate = arrivalDate
        criteria.nights = arrivalDate.numberOfNights(to: checkOutDate)
        bookingDetails.criteria = criteria

        guard shouldShowUpsells else {
            redirectUserToNextStep(
                reservationId: reservationId,
                hotelBrand: preStayInputParams.hotelBrand,
                upsellInput: nil,
                completion: completion
            )
            return
        }

        dataProvider.getPackages(
            reservationId: reservationId,
            bookingDetails: bookingDetails,
            hotelCode: preStayInputParams.hotelCode,
            bookingFlowId: bookingFlowId,
            showMealInclusiveRate: false
        ) { [weak self] response, error in
            guard let self = self, error == nil else {
                return completion(false)
            }

            let ciolUpsellInputParams = getCiolUpsellInputParams(
                hotelPackages: response?.0,
                bookedPackages: response?.1,
                prestayInputParams: preStayInputParams
            )

            if preStayInputParams.hotelBrand == .premierInnGermany {
                redirectUserToNextStep(
                    reservationId: reservationId,
                    hotelBrand: preStayInputParams.hotelBrand,
                    upsellInput: ciolUpsellInputParams,
                    completion: completion
                )
            } else if let ciolUpsellInputParams {
                completion(true)
                output?.preStayChecksCompleted(ciolUpsellInputParams: ciolUpsellInputParams)
            } else {
                redirectUserToNextStep(
                    reservationId: reservationId,
                    hotelBrand: preStayInputParams.hotelBrand,
                    upsellInput: nil,
                    completion: completion
                )
            }
        }
    }

    func resolvePreCheckinFlow(completion: @escaping (Bool) -> Void) {
        if preStayInputParams.ciolPaymentActions?.displayPaymentPage == true {
            handlePaymentFlow(completion: completion)
            return
        }

        if preStayInputParams.ciolPaymentActions?.shouldPerformBackgroundCharge == true {
            handleBackgroundCharge(completion: completion)
            return
        }

        handleCheckInFlow(completion: completion)
    }

    private func handleThridPartyBookingFlow(completion: @escaping (Bool) -> Void) {
        if shouldShowDeRegCard {
            handleDERegCardFlow(upsellInput: nil, completion: completion)
            return
        }

        if thirdPartyCityTax != nil {
            output?.showCityTaxDisclaimer()
            completion(true)
            return
        }

        resolvePreCheckinFlow(completion: completion)
    }

    private func handleBackgroundCharge(completion: @escaping (Bool) -> Void) {
        guard let basketReference = bookingDetails.basketReference,
              let token = bookingDetails.token else {
            completion(false)
            return
        }

        dataProvider.ciolBackgroundCharge(
            basketReference: basketReference,
            token: token
        ) { [weak self] response, error in
            guard response != nil, error == nil else {
                completion(false)
                return
            }

            self?.handleCheckInFlow(completion: completion)
        }
    }

    private func redirectUserToNextStep(
        reservationId _: String,
        hotelBrand: HotelBrand?,
        upsellInput: CiolUpsellInputParams?,
        completion: @escaping (Bool) -> Void
    ) {
        let isDE = hotelBrand == .premierInnGermany
        let needsRegCard = isDE && !preStayInputParams.hasDERegCard
        let hasUpsells = (upsellInput != nil) && shouldShowUpsells
        let needsPayment = shouldGoToPayment()

        // Flow priority: DE RegCard → DE Upsells → Payment → Check-in
        switch (isDE, needsRegCard, hasUpsells, needsPayment) {
        case (true, true, _, _):
            handleDERegCardFlow(upsellInput: upsellInput, completion: completion)
        case (true, _, true, _):
            handleDEUpsellFlow(upsellInput: upsellInput, completion: completion)
        case (_, _, _, true):
            handlePaymentFlow(completion: completion)
        default:
            handleCheckInFlow(completion: completion)
        }
    }

    // MARK: - Flow Handlers

    private func handleDERegCardFlow(upsellInput: CiolUpsellInputParams?, completion: @escaping (Bool) -> Void) {
        guard let room = preStayInputParams.rooms.first?.copy() as? Room else { return }
        let users = room.guestList?.compactMap { $0.copy() as? User }
        room.guestList = users?.sorted(by: { $0.isAccompanyingGuest == false && $1.isAccompanyingGuest == true })

        output?.goToGuestDetailsDERegCard(input: GuestDetailsInput(
            upsellInput: upsellInput,
            room: room,
            prestayInputParams: preStayInputParams,
            defaultAnalytics: analyticsDefaultUserInfo()
        ))
        completion(true)
    }

    private func handleDEUpsellFlow(upsellInput: CiolUpsellInputParams?, completion: @escaping (Bool) -> Void) {
        completion(true)
        guard let upsellInput else { return }
        output?.preStayChecksCompleted(ciolUpsellInputParams: upsellInput)
    }

    private func handlePaymentFlow(completion: @escaping (Bool) -> Void) {
        completion(true)
        let priceBreakdownViewModel = CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("ciolContinueToPay"),
            totalValue: self.priceBreakdownViewModel.totalValue,
            items: self.priceBreakdownViewModel.items
        )

        let paymentInputParams = CiolReviewAndPayInputParams(
            ciolFlow: preStayInputParams.flow,
            bookerFirstName: preStayInputParams.leadBookerFirstName ?? "",
            bookingReference: preStayInputParams.bookingReference ?? "",
            hotelBrand: preStayInputParams.hotelBrand ?? .premierInn,
            bookingSummaryViewModel: bookingSummaryViewModel,
            priceBreakdownViewModel: priceBreakdownViewModel,
            address: preStayInputParams.address,
            defaultAnalyticsParams: analyticsDefaultUserInfo(),
            additionalAnalyticsParams: [:],
            stay: preStayInputParams.stay,
            showBannerMessage: false,
            ciolPaymentActions: preStayInputParams.ciolPaymentActions
        )
        output?.goToPayment(with: paymentInputParams)
    }

    private func handleCheckInFlow(completion: @escaping (Bool) -> Void) {
        let confirmationDetails = CiolConfirmationDetails(
            bookerFirstName: preStayInputParams.leadBookerFirstName ?? "",
            hotelBrand: preStayInputParams.hotelBrand ?? .premierInn,
            ciolStartFlow: preStayInputParams.flow,
            hotelImage: preStayInputParams.hotelImage,
            analyticsInfo: analyticsDefaultUserInfo(),
            stay: preStayInputParams.stay
        )

        // PIBA CNP bookings show confirmation popup before check-in
        let isPIBACNP = preStayInputParams.stay.paymentOption == .pibaCardNotPresent

        if isPIBACNP {
            // PIBA CNP bookings: Show pop-up, then call confirmPreCheckIn after user confirms
            completion(true)
            output?.goToCompletion(ciolConfirmationDetails: confirmationDetails)
        } else {
            // All other bookings: Call confirmPreCheckIn immediately, skip pop-up
            dataProvider.confirmPreCheckInOut(
                basketReference: confirmationDetails.stay.reservationIdentifier ?? "",
                type: .checkIn,
                isCiol: false
            ) { [weak self] _, error in
                guard let self else { return }
                completion(error == nil)
                if error == nil {
                    output?.goDirectlyToCompletion(ciolConfirmationDetails: confirmationDetails)
                }
            }
        }
    }

    // MARK: - Helper Methods

    private func shouldGoToPayment() -> Bool {
        let hasRemainingBalance = preStayInputParams.outstandingBalance?.amount != 0

        // PIBA CNP bookings skip payment (paid at front desk)
        let isPIBACNP = preStayInputParams.stay.paymentOption == .pibaCardNotPresent
        let shouldPay = hasRemainingBalance && !isPIBACNP
        return shouldPay
    }

    private func updateUserDetails(completion: @escaping (_ success: Bool) -> Void) {
        guard preStayInputParams.hotelBrand != .premierInnGermany else {
            completion(true)
            return
        }

        bookingDetails.updateBooking(with: preStayInputParams)

        dataProvider.performHoldBookingWithGuests(
            bookingDetails: bookingDetails,
            isCiolFlow: true,
            isRegCard: false
        ) { success, error in
            completion(success && error == nil)
        }
    }

    func isAllGuestDataComplete() -> Bool {
        let isValid: Bool

        if preStayInputParams.hotelBrand == .premierInnGermany {
            isValid = preStayInputParams.isDirect ?
            true :
            isThirdPartyRequiredDataPresent
        } else if preStayInputParams.isDirect {
            isValid = isRequiredDataPresent
        } else {
            isValid = isThirdPartyDetailsValid
        }

        if !isValid {
            preStayInputParams.shouldSurfaceErrorMessages = true
        }

        return isValid
    }

    func confirmPreCheckIn(basketReference: String, completion: @escaping (Bool) -> Void) {
        let isPIBACNP = preStayInputParams.stay.paymentOption == .pibaCardNotPresent
        dataProvider
            .confirmPreCheckInOut(
                basketReference: basketReference,
                type: .checkIn,
                isCiol: isPIBACNP
            ) { [weak self] _, error in
            guard error == nil else {
                completion(false)
                return
            }
            self?.trackCiolConfirmation()
            completion(true)
        }
    }

    func getCiolUpsellInputParams(
        hotelPackages: [UpsellItem]?,
        bookedPackages: [UpsellItem]?,
        prestayInputParams: PreStayInputParams
    ) -> CiolUpsellInputParams? {
        var ciolUpsellPackages: [UpsellItem] = []
        var upsellRoomModels: [UpsellRoom] = []
        let stayHasChildren = prestayInputParams.childrenCount > 0
        let stayHasMultipleRooms = prestayInputParams.rooms.count > 1
        var guests: [Guest] = []
        if prestayInputParams.hotelBrand == .premierInnGermany {
            guard let room = prestayInputParams.rooms.first,
                  let roomID = room.roomId else { return nil }

            let adults: [String] = room.guestList?.compactMap { $0.firstName } ?? []
            guard adults.isNotEmpty else { return nil }
            let ciolRoom = CiolUpsellRoom(
                hasChildren: room.children > 0,
                numberOfChildren: room.children,
                id: roomID,
                adults: adults,
                title: ""
            )
            upsellRoomModels.append(ciolRoom)

            guests = GuestDetailsInteractor.guestModels(room: room)
        } else {
            for (index, room) in prestayInputParams.rooms.enumerated() {
                guard let roomID = room.roomId else { continue }
                let adults = [room.leadGuest?.firstName, room.accompanyingGuest?.firstName].compactMap { $0 }
                guard adults.isNotEmpty else { continue }
                let ciolRoom = CiolUpsellRoom(
                    hasChildren: room.children > 0,
                    numberOfChildren: room.children,
                    id: roomID,
                    adults: adults,
                    title: "\(PILocalizedString("Room")) \(index + 1)"
                )
                upsellRoomModels.append(ciolRoom)
            }
        }

        guard let hotelPackages = hotelPackages, !hotelPackages.isEmpty else {
            return nil
        }

        let bookedPackagesArray = bookedPackages ?? []

        // Backend controls whether upsells can be added via upsellsAddOnEnabled flag
        // If false (e.g., PIBA bookings), only show page if customer has booked packages to review
        // If true (e.g., credit card bookings), show upsell screen to allow purchasing during CIOL
        if !prestayInputParams.stay.upsellsAddOnEnabled {
            // Cannot add new upsells, but show page if customer has booked packages to review
            guard !bookedPackagesArray.isEmpty else {
                return nil
            }
        }

        let foodUpsellConfig = CiolUpsellConfigurator.availableFoodUpsells(
            hotelPackages: hotelPackages,
            bookedPackages: bookedPackagesArray,
            numberOfAdults: prestayInputParams.adultsCount
        )
        let eciLcoConfig = CiolUpsellConfigurator.availableEciLcoUpsells(
            hotelPackages: hotelPackages,
            bookedPackages: bookedPackagesArray
        )
        let wifiConfig = CiolUpsellConfigurator.availableWifiUpsells(
            hotelPackages: hotelPackages,
            bookedPackages: bookedPackagesArray,
            roomCount: prestayInputParams.rooms.count
        )

        // Only include available upsells if the flag allows adding new ones
        if prestayInputParams.stay.upsellsAddOnEnabled {
            ciolUpsellPackages.append(contentsOf: foodUpsellConfig.0)
            ciolUpsellPackages.append(contentsOf: eciLcoConfig.0)
            ciolUpsellPackages.append(contentsOf: wifiConfig.0)
        }

        return CiolUpsellInputParams(
            hotel: prestayInputParams.hotel,
            availableUpsells: ciolUpsellPackages,
            bookedUpsells: bookedPackages,
            rooms: upsellRoomModels,
            hasChildren: stayHasChildren,
            isMultiRoom: stayHasMultipleRooms,
            nights: prestayInputParams.nightsCount,
            priceBreakdownViewModel: viewModel.priceBreakdownViewModel,
            bookingSummaryViewModel: viewModel.bookingSummaryViewModel,
            flow: prestayInputParams.flow,
            leadBookerFirstName: prestayInputParams.leadBookerFirstName,
            hotelBrand: prestayInputParams.hotelBrand ?? .premierInn,
            address: prestayInputParams.address,
            bookingReference: prestayInputParams.bookingReference,
            stay: prestayInputParams.stay,
            reservationID: prestayInputParams.reservationId,
            hotelID: prestayInputParams.hotelCode,
            arrivalDate: prestayInputParams.arrivalDate,
            departureDate: prestayInputParams.checkOutDate,
            outstandingBalance: prestayInputParams.outstandingBalance,
            selectedPreference: prestayInputParams.selectedPreference,
            hotelImage: prestayInputParams.hotelImage,
            analyticsParams: analyticsDefaultUserInfo(),
            bookingFlowId: prestayInputParams.bookingFlowId,
            hotelName: prestayInputParams.hotelName,
            hotelAddress: prestayInputParams.hotelAddress,
            guests: guests
        )
    }


    func getSpecialOccasions(completion: @escaping (_ success: Bool, _ error: Error?) -> Void) {
        dataProvider.getHotelPreferences(hotelCode: preStayInputParams.hotelCode) { [weak self] preferences, error in
            guard error == nil else {
                completion(false, CIOLError.getHotelPreferences)
                return
            }
            guard let preferences else {
                completion(false, nil)
                return
            }
            let hotelPreferenceViewModel = preferences.map { preference in
                HotelPreferenceViewModel(
                    name: preference.label ?? "",
                    code: preference.code ?? ""
                )
            }
            self?.preStayInputParams.hotelPreferences = hotelPreferenceViewModel
            completion(true, nil)
        }
    }

    private func updateReservationsPreference(completion: @escaping (_ success: Bool) -> Void) {
        guard preStayInputParams.hotelBrand != .premierInnGermany,
              preStayInputParams.isSpecialOccasionOn == true else {
            completion(true)
            return
        }

        let preferenceCollection = PreferencesCollection(
            preferenceType: "EVENTS",
            preferences: [preStayInputParams.selectedPreference?.code ?? ""]
        )
        var bookingCollections = preStayInputParams.bookingPreferences?
            .map { PreferencesCollection(preferenceType: $0.type ?? "", preferences: [$0.code]) }
            .filter { $0.preferenceType != "EVENTS" } ?? []
        bookingCollections.append(preferenceCollection)
        let reservationIDs = preStayInputParams.rooms.compactMap { $0.reservationId }

        dataProvider.updateReservationPreferences(
            hotelCode: preStayInputParams.hotelCode,
            reservationIds: reservationIDs,
            preferencesCollections: bookingCollections
        ) { _, error in
            completion(error == nil)
        }
    }

    func goToEditDetails(flow: EditDetailsFlow) {
        var guest: CIOLPersonViewModelProtocol?

        switch flow {
        case .leadGuestTitleNameInfo(let index):
            guest = viewModel.roomsViewModel[index.row].leadGuest

        case .secondGuestTitleNameInfo(indexPath: let indexPath):
            guest = viewModel.roomsViewModel[indexPath.row].accompanyingGuest

        case .address, .emailAddress, .phoneNumber:
            createAndPresentEditDetailsModule(
                flow: flow,
                guest: guest,
                viewModel: viewModel
            )

        default:
            break
        }

        if let guest {
            createAndPresentEditDetailsModule(
                flow: flow,
                guest: guest,
                viewModel: viewModel
            )
        }
    }

    private func createAndPresentEditDetailsModule(
        flow: EditDetailsFlow,
        guest: CIOLPersonViewModelProtocol?,
        viewModel: PreStayViewModel
    ) {
        let editDetailsModel = EditDetailsModel(
            flow: flow,
            title: guest?.title,
            firstName: guest?.firstName,
            lastName: guest?.lastName,
            country: guest?.country,
            passportNumber: guest?.passportNumber,
            hotelBrand: viewModel.hotelBrand,
            bookingReference: preStayInputParams.bookingReference,
            emailAddress: viewModel.email,
            phoneNumber: viewModel.contactNumber,
            address: StoredAddressModel(with: preStayInputParams.address)
        )

        output?.editBookingDetails(
            inputParams: EditDetailsInputParams(
                editDetailsViewModel: editDetailsModel,
                analyticsParams: analyticsDefaultUserInfo()
            )
        )
    }

    func updateViewModel(with editDetailsModel: EditDetailsModel) {
        switch editDetailsModel.flow {
        case .leadGuestTitleNameInfo(let index):
            guard let updatedUser = updatedUser(
                index: index,
                from: editDetailsModel,
                isSecondGuest: false
            ) else {
                return
            }

            preStayInputParams.rooms[safe: index.row]?.leadGuest = updatedUser
            preStayInputParams.confirmedLeadGuestRows.insert(index.row)

        case .secondGuestTitleNameInfo(indexPath: let index):
            guard let updatedUser = updatedUser(
                index: index,
                from: editDetailsModel,
                isSecondGuest: true
            ) else {
                return
            }

            preStayInputParams.rooms[safe: index.row]?.accompanyingGuest = updatedUser
            preStayInputParams.confirmedSecondGuestRows.insert(index.row)

        case .address:
            preStayInputParams.address = editDetailsModel.address?.address
        case .emailAddress:
            preStayInputParams.email = editDetailsModel.emailAddress
        case .phoneNumber:
            preStayInputParams.contactNumber = editDetailsModel.phoneNumber
        default:
            break
        }

        validateGuestDetails()
        output?.reloadData(with: viewModel)
    }

    private func updatedUser(index: IndexPath, from editDetailsModel: EditDetailsModel, isSecondGuest: Bool) -> User? {
        guard let room = preStayInputParams.rooms[safe: index.row] else {
            return nil
        }

        let existingUser = isSecondGuest ? room.accompanyingGuest : room.leadGuest

        guard let user = (existingUser?.copy() as? User)
              ?? (try? User(
                  title: editDetailsModel.title,
                  firstName: editDetailsModel.firstName,
                  lastName: editDetailsModel.lastName
              )) else {
            return nil
        }

        user.title = editDetailsModel.title
        user.firstName = editDetailsModel.firstName
        user.lastName = editDetailsModel.lastName
        user.country = editDetailsModel.country?.country

        if let passportNumber = editDetailsModel.passportNumber,
           let countryCode = editDetailsModel.country?.country.code {
            user.passport = Passport(number: passportNumber, countryOfIssue: countryCode)
        } else {
            user.passport = nil
        }

        return user
    }

    private func trackCiolConfirmation() {
        var info = analyticsDefaultUserInfo()
        info[PIAnalytics.Keys.checkInOnlineBookingID] = preStayInputParams.bookingReference
        info[PIAnalytics.Keys.checkInOnlinePrepaid] = preStayInputParams.outstandingBalance == nil || preStayInputParams
            .outstandingBalance?.amount == 0
        info[PIAnalytics.Keys.checkInOnlineRoomTypeChange] = false
        info[PIAnalytics.Keys.checkInOnlineRevenue] = (preStayInputParams.outstandingBalance?.amount.doubleValue ?? 0.0)
            .stringForAnalyticsCost
        info[PIAnalytics.Keys.checkInOnlineSpecialOccasion] = preStayInputParams.selectedPreference?.name

        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.checkInOnlineConfirmation, data: info)

        guard let reference = preStayInputParams.bookingReference else { return }

        AppsFlyerManager.sharedInstance.trackCiolComplete(reference: reference, hotelCode: preStayInputParams.hotelCode)
    }

    private func analyticsDefaultUserInfo() -> PIDictionary {
        var info = PIDictionary()
        info[PIAnalytics.Keys.checkInOnline] = true
        info[PIAnalytics.Keys.checkInOnlineBookingID] = preStayInputParams.bookingReference
        info[PIAnalytics.Keys.checkInOnlineHotelCode] = preStayInputParams.hotelCode
        info[PIAnalytics.Keys.checkInOnlineAdults] = preStayInputParams.adultsCount
        info[PIAnalytics.Keys.checkInOnlineChildren] = preStayInputParams.childrenCount
        info[PIAnalytics.Keys.productString] = ";\(preStayInputParams.hotelCode)"
        info[PIAnalytics.Keys.checkInOnlineNights] = preStayInputParams.nightsCount
        info[PIAnalytics.Keys.checkInOnlineRooms] = preStayInputParams.rooms.count
        info[PIAnalytics.Keys.checkInOnlineCheckInDate] = preStayInputParams.arrivalDate?.analyticsDateFormat
        info[PIAnalytics.Keys.checkInOnlineCheckOutDate] = preStayInputParams.checkOutDate?.analyticsDateFormat
        info[PIAnalytics.Keys.checkInOnlineCheckInDay] = preStayInputParams.arrivalDate?.analyticsDayFormat
        info[PIAnalytics.Keys.checkInOnlineCheckOutDay] = preStayInputParams.checkOutDate?.analyticsDayFormat
        info[PIAnalytics.Keys.checkInOnlineCheckInOutDay] = "\(preStayInputParams.arrivalDate?.analyticsDayFormat ?? "")-\(preStayInputParams.checkOutDate?.analyticsDayFormat ?? "")"
        info[PIAnalytics.Keys.checkInOnlineRateDescription] = preStayInputParams.rateDescription
        info[PIAnalytics.Keys.checkInOnlineRateCode] = preStayInputParams.rateCode

        info[PIAnalytics.Keys.time] = Date().analyticsTimeFormat
        info[PIAnalytics.Keys.environment] = environment
        info[PIAnalytics.Keys.userLogin] = loggedIn
        info[PIAnalytics.Keys.timeZone] = timeZone
        info[PIAnalytics.Keys.language] = language
        info[PIAnalytics.Keys.screenType] = PIAnalytics.StateTypes.ciolFlow
        info[PIAnalytics.Keys.userID] = AnalyticsManager.shared.userID

        if preStayInputParams.hotelBrand == .premierInnGermany {
            info[PIAnalytics.Keys.checkInOnlineAction] = PIAnalytics.Action.ciolDeRegCard
        }

        return info
    }

    func trackSubmissionIfThirdPartyBooking() {
        var data = PIDictionary()
        if viewModel.isThirdParty {
            data[PIAnalytics.Keys.thirdPartyBookingExported] = true
            AnalyticsManager.shared.trackState(PIAnalytics.StateNames.checkInOnline, data: data)
        }
    }
}

extension PreStayInteractor: Trackable {
    var screenName: String { PIAnalytics.StateNames.ciolPreStay }
    var trackScreen: Bool { false }
    var environment: String { AnalyticsConstants.environment }
    var loggedIn: LoggedInAnalytic { UserSessionManager.sharedInstance.currentUser != nil ? .loggedIn : .notLoggedIn }
    var timeZone: String { TimeZone.current.description }
    var language: String { Locale.current.language.languageCode?.identifier ?? "n/a" }
    var screenType: String { PIAnalytics.StateTypes.ciolFlow }
    var customParameters: [String: Any]? { nil }

    func applicationDidTakeScreenshot() { }
}

enum CiolUpsellConfigurator {
    static func availableFoodUpsells(
        hotelPackages: [UpsellItem],
        bookedPackages: [UpsellItem],
        numberOfAdults: Int
    ) -> ([UpsellItem], allBooked: Bool) {
        let hotelFoodUpsells = hotelPackages.filter({ $0.foodUpsell})

        guard !hotelFoodUpsells.isEmpty else { return ([], true) }

        let bookedFoodUpsells = bookedPackages.filter({ $0.foodUpsell })

        guard !bookedFoodUpsells.isEmpty else { return (hotelFoodUpsells, false) }

        let numberOfAdultsWithFoodUpsells = bookedFoodUpsells.reduce(0) { $0 + ($1.quantity ?? 0) }

        let allBooked = numberOfAdultsWithFoodUpsells == numberOfAdults
        return allBooked ? (hotelFoodUpsells, true) : (hotelFoodUpsells, false)
    }

    static func availableEciLcoUpsells(
        hotelPackages: [UpsellItem],
        bookedPackages: [UpsellItem]
    ) -> ([UpsellItem], booked: Bool) {
        let hotelUpsells = hotelPackages.filter { $0.isExtraUpsell }

        guard !hotelUpsells.isEmpty else { return ([], true) }

        var extraUpsells: [UpsellItem] = []
        var booked = true
        var eciBooked = true
        var lcoBooked = true
        if let eciUpsell = hotelUpsells.first(where: { $0.id == UpsellItemOperaId.earlyCheckIn.rawValue }) {
            eciBooked = false
            if bookedPackages.contains(where: { $0.id == UpsellItemOperaId.earlyCheckIn.rawValue }) {
                eciBooked = true
            }
            extraUpsells.append(eciUpsell)
        }

        if let lcoUpsell = hotelUpsells.first(where: { $0.id == UpsellItemOperaId.lateCheckOut.rawValue }) {
            lcoBooked = false
            if bookedPackages.contains(where: { $0.id == UpsellItemOperaId.lateCheckOut.rawValue }) {
                lcoBooked = true
            }
            extraUpsells.append(lcoUpsell)
        }
        booked = eciBooked && lcoBooked
        return (extraUpsells, booked)
    }

    static func availableWifiUpsells(
        hotelPackages: [UpsellItem],
        bookedPackages: [UpsellItem],
        roomCount: Int
    ) -> ([UpsellItem], booked: Bool) {
        guard let wifiPackage = hotelPackages
              .first(where: { $0.id == UpsellItemOperaId.ultimateWifi24Hours.rawValue && $0.wifiUpsell }) else { return (
                  [],
                  true
              ) }

        let roomsWithBookedWifiPackage = bookedPackages.filter { $0.id == UpsellItemOperaId.ultimateWifi24Hours.rawValue }

        let booked = roomsWithBookedWifiPackage.count == roomCount
        return ([wifiPackage], booked)
    }
}

extension PreStayInteractor: PrestayDelegate {
    func didUpdateBalance(with newBalance: Cost) {
        let isPIBACNP = preStayInputParams.stay.paymentOption == .pibaCardNotPresent
        if !isPIBACNP {
            preStayInputParams.updateOutstandingBalance(with: newBalance)
        }
        output?.reloadPriceBreakdown(priceModel: priceBreakdownViewModel)
    }
}

// MARK: - Data validation

private extension PreStayInteractor {
    enum GuestRole {
        case lead
        case accompanying
    }

    func leadGuestNeedsConfirmation(at row: Int) -> GuestWarningMessageState {
        guard let guest = preStayInputParams.rooms[safe: row]?.leadGuest else {
            return .none
        }

        let shouldRequireNationalityDetails = shouldNationalityBeProvided(guest: guest)
        let isConfirmed = isConfirmed(row: row, role: .lead)

        if !isConfirmed && shouldRequireNationalityDetails {
            return .nationalityAndPassportNumberRequired
        }

        if !isConfirmed && !shouldRequireNationalityDetails {
            return .nationalityRequiresConfirmation
        }

        return .none
    }

    func secondGuestNeedsConfirmation(at row: Int) -> GuestWarningMessageState {
        guard let room = preStayInputParams.rooms[safe: row], room.adults > 1 else {
            return .none
        }

        let shouldRequireNationalityDetails = shouldNationalityBeProvided(guest: room.accompanyingGuest)
        let isConfirmed = isConfirmed(row: row, role: .accompanying)

        if !isConfirmed && shouldRequireNationalityDetails {
            return .nationalityAndPassportNumberRequired
        }

        if !isConfirmed && !shouldRequireNationalityDetails {
            return .nationalityRequiresConfirmation
        }

        return .none
    }

    func validateGuestDetails() {
        preStayInputParams.shouldSurfaceErrorMessages =
            preStayInputParams.rooms.indices.contains { row in
                getGuestWarningMessageState(for: row, role: .lead) != .none ||
                getGuestWarningMessageState(for: row, role: .accompanying) != .none
            }
    }

    func getGuestWarningMessageState(for row: Int, role: GuestRole) -> GuestWarningMessageState {
        guard let room = preStayInputParams.rooms[safe: row] else {
            return .none
        }

        guard shouldValidateGuest(in: room, role: role) else {
            return .none
        }

        let guest = guest(in: room, role: role)
        let isConfirmed = isConfirmed(row: row, role: role)

        if shouldNationalityBeProvided(guest: guest) {
            return .nationalityAndPassportNumberRequired
        }

        if !isConfirmed {
            return .nationalityRequiresConfirmation
        }

        return .none
    }

    func guest(in room: Room, role: GuestRole) -> User? {
        switch role {
        case .lead:
            return room.leadGuest
        case .accompanying:
            return room.accompanyingGuest
        }
    }

    func shouldValidateGuest(in room: Room, role: GuestRole) -> Bool {
        switch role {
        case .lead:
            return true
        case .accompanying:
            return room.adults > 1
        }
    }

    func isConfirmed(row: Int, role: GuestRole) -> Bool {
        guard preStayInputParams.hotelBrand != .premierInnGermany else {
            return true
        }

        let explicitlyConfirmed: Bool = {
            switch role {
            case .lead:
                return preStayInputParams.confirmedLeadGuestRows.contains(row)
            case .accompanying:
                return preStayInputParams.confirmedSecondGuestRows.contains(row)
            }
        }()

        return explicitlyConfirmed
    }

    func shouldNationalityBeProvided(guest: User?) -> Bool {
        guard let guest else {
            return true
        }

        switch preStayInputParams.hotelBrand {
        case .premierInn, .zip, .hub:
            return shouldRequireNationalityDetails(guest: guest)
        case .premierInnGermany:
            return false
        default:
            return false
        }
    }

    func shouldRequireNationalityDetails(guest: User?) -> Bool {
        guard let guest else {
            return true
        }
        return guest.isMissingNationality ?
        true :
        (CountryItem(country: guest.country)?.isPassportRequired ?? false) && guest.isMissingPassportNumber
    }

    var isLeadGuestsIDInfoComplete: Bool {
        !preStayInputParams.rooms.indices.contains { row in
            getGuestWarningMessageState(for: row, role: .lead) != .none
        }
    }

    var isAccompanyingGuestInfoComplete: Bool {
        !preStayInputParams.rooms.indices.contains { row in
            getGuestWarningMessageState(for: row, role: .accompanying) != .none
        }
    }

    var isRequiredDataPresent: Bool {
        preStayInputParams.isDirect &&
        isLeadGuestsIDInfoComplete &&
        isAccompanyingGuestInfoComplete
    }

    var isThirdPartyDetailsValid: Bool {
        !preStayInputParams.isDirect &&
        isThirdPartyRequiredDataPresent &&
        isLeadGuestsIDInfoComplete &&
        isAccompanyingGuestInfoComplete
    }

    var isThirdPartyRequiredDataPresent: Bool {
        guard let email = viewModel.email,
              let contactNumber = viewModel.contactNumber,
              let address = preStayInputParams.address,
              let country = bookerViewModel.country,
              let postcode = address.postcode,
              let addressLine1 = address.line1 else {
            return false
        }

        return viewModel.isThirdParty &&
        !email.isEmpty &&
        !contactNumber.isEmpty &&
        !country.title.isEmpty &&
        !postcode.isEmpty &&
        !addressLine1.isEmpty &&
        viewModel.isAddressPostcodeValid
    }
}
