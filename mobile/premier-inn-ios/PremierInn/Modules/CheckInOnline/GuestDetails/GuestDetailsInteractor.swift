//
//  GuestDetailsInteractor.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

class GuestDetailsInteractor: GuestDetailsInteractorBlueprint {
    var input: GuestDetailsInputBlueprint
    weak var output: GuestDetailsInteractorOutput?
    weak var prestayDelegate: PrestayDelegate?
    var dataProvider: PreStayDataProvider
    private let settingsManager: SettingsManager
    private let bookingDetails: BookingDetails
    var room: Room
    var guests: [Guest]

    var defaultAnalytics: PIDictionary {
        input.defaultAnalytics
    }

    private var paymentActions: CiolPaymentActionsResponse? {
        input.prestayInputParams.ciolPaymentActions
    }

    init(
        input: GuestDetailsInputBlueprint,
        provider: PreStayDataProvider = RequestsManager(),
        settingsManager: SettingsManager = .sharedInstance,
        bookingDetails: BookingDetails = .sharedInstance
    ) {
        self.dataProvider = provider
        self.settingsManager = settingsManager
        self.bookingDetails = bookingDetails
        self.input = input
        self.room = input.room

        let totalGuests = room.adults + room.children
        guard let guestList = room.guestList else {
            self.guests = []
            return
        }
        if guestList.count < totalGuests {
            let guestList = guestList.count
            let emptyGuests = room.adults + room.children - guestList
            for _ in 1...emptyGuests {
                let user = User.emptyGuest()
                user.isAccompanyingGuest = true
                self.room.guestList?.append(user)
            }
        }
        self.guests = GuestDetailsInteractor.guestModels(room: input.room)
    }

    func handleContinueButtonTap() {
        if isThirdPartyBookingWithCityTax {
            output?.showCityTaxDisclaimer()
            return
        }

        validate()
    }

    func validate() {
        guard let guestList = room.guestList else { return }
        var hasError: Bool = false

        for (index, guest) in guestList.enumerated() {
            var guestType: GuestType = .additional
            if let isAccompayingGuest = guest.isAccompanyingGuest {
                guestType = isAccompayingGuest ? .additional : .lead
            }
            if let guestStatus = guestStatus(user: guest, guestType: guestType) {
                guests[index].status = guestStatus
                if guestStatus == .error {
                    hasError = true
                }
            }
        }
        output?.reload()
        if hasError == false {
            output?.didStart()
            updateUserDetails { [weak self] succes in
                guard succes else {
                    self?.output?.didFinish(error: CIOLError.regCardCheck)
                    return
                }
                self?.output?.didFinish(error: nil)
                self?.goToNextStep()
            }
        }
    }

    func guestStatus(user: User, guestType: GuestType) -> GuestStatus? {
        switch guestType {
        case .lead:
            var hasErrors: [Bool] = [
                user.isMissingFirstname,
                user.isMissingLastName,
                user.isMissingAddress,
                user.isMissingPostcode,
                user.isMissingCountry,
                user.isMissingDOB,
                user.isMissingNationality
            ]
            if user.country != .germany {
                hasErrors.append(user.isMissingPassportNumber)
            }
            return hasErrors.contains(true) ? .error : nil
        case .additional:
            var hasErrors: [Bool] = [
                user.isMissingFirstname,
                user.isMissingLastName,
                user.isMissingDOB,
                user.isMissingNationality
            ]
            if user.country != .germany {
                hasErrors.append(user.isMissingPassportNumber)
            }
            return hasErrors.contains(true) ? .error : nil
        }
    }

    class func guestModels(room: Room) -> [Guest] {
        room.guestList?.compactMap { Guest(with: $0) } ?? []
    }

    private lazy var guestDateFormatter = DateFormatter.slashDayMonthYearStringFormatter

    func getEditModel(for index: Int) -> EditDetailsInputParams? {
        var editModel: EditDetailsModel

        guard let guest = room.guestList?[safe: index] else { return nil }

        let dateOfBirth: Date?
        if let date = guestDateFormatter.date(from: guest.dob ?? "") {
            dateOfBirth = date
        } else {
            dateOfBirth = DateFormatter.regCardGuestOutputFormatter.date(from: guest.dob ?? "")
        }
        editModel = EditDetailsModel(
            flow: .regCard(index: index),
            title: guest.title,
            firstName: guest.firstName,
            lastName: guest.lastName,
            country: .init(country: guest.country),
            passportNumber: guest.passport?.number,
            hotelBrand: input.prestayInputParams.hotelBrand,
            bookingReference: input.prestayInputParams.bookingReference,
            address: .init(with: guest.address),
            dateOfBirth: dateOfBirth,
            isLeadGuest: guest.isAccompanyingGuest == false,
            isLastNameDisabled: !(guest.isAccompanyingGuest ?? true)
        )

        trackEditAnalytics(isLeadGuest: editModel.isLeadGuest)

        return .init(editDetailsViewModel: editModel, analyticsParams: input.defaultAnalytics)
    }

    // Save button
    func updateInput(with editDetailsModel: EditDetailsModel) {
        switch editDetailsModel.flow {
        case .regCard(index: let index):
            guard room.guestList?[safe: index] != nil else { return }

            room.guestList?[safe: index]?.title = editDetailsModel.title ?? ""
            room.guestList?[safe: index]?.firstName = editDetailsModel.firstName ?? ""
            room.guestList?[safe: index]?.lastName = editDetailsModel.lastName ?? ""
            room.guestList?[safe: index]?.country = editDetailsModel.country?.country
            if let passportNumber = editDetailsModel.passportNumber,
               let countryCode = editDetailsModel.country?.country.code {
                room.guestList?[safe: index]?.passport = Passport(
                    number: passportNumber,
                    countryOfIssue: countryCode
                )
            }
            if let dateOfBirth = editDetailsModel.dateOfBirth {
                let dobString = guestDateFormatter.string(from: dateOfBirth)
                room.guestList?[safe: index]?.dob = dobString
            }

            if let address = editDetailsModel.address {
                room.guestList?[safe: index]?.address = address.address
            }
            trackSaveCTAAnalytics(editDetailsModel: editDetailsModel)
        default:
            break
        }
        self.guests = GuestDetailsInteractor.guestModels(room: input.room)

        output?.reload()
    }

    private func trackEditAnalytics(isLeadGuest: Bool) {
        var dictionary = input.defaultAnalytics
        if isLeadGuest {
            dictionary[PIAnalytics.Keys.checkInOnlineLeadEdit] = true
        } else {
            dictionary[PIAnalytics.Keys.checkInOnlineAdditionalEdit] = true
        }
        let stateName = isLeadGuest ? PIAnalytics.StateNames.ciolLeadGuestDetails : PIAnalytics.StateNames
            .ciolAdditionalGuestDetails
        AnalyticsManager.shared.trackState(stateName, data: dictionary)
    }

    private func trackSaveCTAAnalytics(editDetailsModel: EditDetailsModel) {
        var dictionary = input.defaultAnalytics
        dictionary[PIAnalytics.Keys.checkInOnlineBtnSave] = true
        dictionary[PIAnalytics.Keys.checkInOnlineFirstName] = editDetailsModel.firstName
        dictionary[PIAnalytics.Keys.checkInOnlineDobEdit] = editDetailsModel.dateOfBirth
        dictionary[PIAnalytics.Keys.checkInOnlineNationalityEdit] = editDetailsModel.country?.nationality

        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.checkInOnlineGuestDetailsPage, data: dictionary)
    }

    private func trackContinueCTAAnalytics() {
        var dictionary = input.defaultAnalytics
        dictionary[PIAnalytics.Keys.checkInOnlineBtnContinue] = true

        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.checkInOnlineGuestDetailsPage, data: dictionary)
    }

    var priceVM: GuestDetailsPriceBreakdownViewModel {
        let isDirectBooking = input.prestayInputParams.isDirect

        let outstandingCost: Cost? = isDirectBooking
            ? input.prestayInputParams.outstandingBalance
            : input.prestayInputParams.ciolPaymentActions?.outstandingBalance

        if let outstandingCost, outstandingCost.amount != 0 || !isDirectBooking {
            let outstandingItem = PreStayInteractor.CIOLPriceBreakdownItemViewModel(
                name: PILocalizedString("preStayOutstandingBalance"),
                value: outstandingCost,
                quantity: 1
            )
            return GuestDetailsPriceBreakdownViewModel(
                ctaTitle: PILocalizedString("Continue"),
                totalValue: outstandingCost.localizedValue,
                items: [outstandingItem]
            )
        }
        return GuestDetailsPriceBreakdownViewModel(ctaTitle: PILocalizedString("Continue"), totalValue: "", items: [])
    }

    private var isThirdPartyBookingWithCityTax: Bool {
        paymentActions?.cityTax != nil &&
        settingsManager.featureThirdPartyPrepaid
    }

    func goToNextStep() {
        if var upsellInput = input.upsellInput, shouldShowUpsells {
            guard let roomID = room.roomId else { return }
            let childrenNumber = room.children

            var adults: [String] = room.guestList?.compactMap { $0.firstName } ?? []
            adults.removeLast(childrenNumber)
            guard adults.isNotEmpty else { return }
            let ciolRoom = CiolUpsellRoom(
                hasChildren: room.children > 0,
                numberOfChildren: room.children,
                id: roomID,
                adults: adults,
                title: ""
            )
            upsellInput.rooms = [ciolRoom]
            upsellInput.regCardFlow = .regCard
            upsellInput.guests = self.guests
            self.output?.goToUpsells(upsellInput: upsellInput)
        } else if !input.prestayInputParams.isDirect && settingsManager.featureThirdPartyPrepaid {
            handleFlowForThirdPartyBooking()
        } else if let outstandingBalance = input.prestayInputParams.outstandingBalance, outstandingBalance.amount != 0 {
            goToPayment()
        } else {
            handleAuthorizeOrCheckin()
        }
        trackContinueCTAAnalytics()
    }

    private func handleFlowForThirdPartyBooking() {
        if paymentActions?.displayPaymentPage == true {
            goToPayment()
            return
        }

        if paymentActions?.shouldPerformBackgroundCharge == true {
            handleBackgroundCharge()
            return
        }

        handleAuthorizeOrCheckin()
    }

    private func handleBackgroundCharge() {
        output?.didStart()

        guard let basketReference = bookingDetails.basketReference,
              let token = bookingDetails.token else {
            output?.didFinish(error: .performPrestayChecks)
            return
        }

        dataProvider.ciolBackgroundCharge(
            basketReference: basketReference,
            token: token
        ) { [weak self] response, error in
            guard response != nil, error == nil else {
                self?.output?.didFinish(error: .genericPayment)
                return
            }

            self?.output?.didFinish(error: nil)
            self?.handleAuthorizeOrCheckin()
        }
    }

    private func goToPayment() {
        let priceBreakdownViewModel = GuestDetailsPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("ciolContinueToPay"),
            totalValue: priceVM.totalValue,
            items: priceVM.items
        )

        let paymentInputParams = CiolReviewAndPayInputParams(
            ciolFlow: input.prestayInputParams.flow,
            bookerFirstName: input.prestayInputParams.leadBookerFirstName ?? "",
            bookingReference: input.prestayInputParams.bookingReference ?? "",
            hotelBrand: input.prestayInputParams.hotelBrand ?? .premierInn,
            bookingSummaryViewModel: bookingSummaryViewModel,
            priceBreakdownViewModel: priceBreakdownViewModel,
            address: input.prestayInputParams.address,
            defaultAnalyticsParams: input.defaultAnalytics,
            additionalAnalyticsParams: [:],
            stay: input.prestayInputParams.stay,
            regCardFlow: .regCard,
            regCardInput: regCardInput(transactionID: ""),
            showBannerMessage: leadGuestIsNotGerman,
            ciolPaymentActions: paymentActions
        )

        output?.goToPayment(with: paymentInputParams)
    }

    private func handleAuthorizeOrCheckin() {
        guard let leadGuest = guests.first(where: { $0.type == .lead }) else {
            return
        }
        if leadGuest.isNotGerman,
           let output = output {
            showAuthorize(provider: dataProvider, output: output)
        } else {
            Task { @MainActor in
                let updatePreCheckinInput = UpdatePrecheckInParams(
                    hotelId: input.prestayInputParams.hotelCode,
                    reservationId: room.roomId ?? "",
                    arrivalTime: input.prestayInputParams.arrivalDate?
                                                                   .parameterString ?? ""
                )
                do {
                    let regCardCheckinStatus = try await updatePreCheckInStatus(
                        provider: dataProvider,
                        params: updatePreCheckinInput
                    )
                    guard let output, let regCardCheckinStatus,
                          regCardCheckinStatus == .success else { throw CIOLError.performPrestayChecks }
                    let confirmInput = CiolConfirmationDetails(
                        bookerFirstName: input.prestayInputParams.leadBookerFirstName ?? "",
                        hotelBrand: input.prestayInputParams.hotelBrand ?? .premierInnGermany,
                        ciolStartFlow: .bookingConfirmation,
                        hotelImage: input.prestayInputParams.hotelImage,
                        analyticsInfo: input.defaultAnalytics,
                        stay: input.prestayInputParams.stay
                    )
                    try? await checkIn(
                        provider: dataProvider,
                        confirmInput: confirmInput,
                        reservationID: input.prestayInputParams.reservationId ?? "",
                        output: output
                    )
                } catch {
                    output?.didFinish(error: error as? CIOLError)
                }
            }
        }
    }

    var bookingSummaryViewModel: PreStayInteractor.BookingSummaryCIOLViewModel {
        PreStayInteractor.BookingSummaryCIOLViewModel(
            image: input.prestayInputParams.hotelImage,
            hotelName: input.prestayInputParams.hotelName,
            duration: datesSummary,
            summary: staySummary
        )
    }

    var shouldShowUpsells: Bool {
        SettingsManager.sharedInstance.featureCIOLUpsells &&
        !input.prestayInputParams.isBusinessTrip &&
        input.prestayInputParams.isDirect
    }

    private var datesSummary: String {
        guard let arrivalDate = input.prestayInputParams.arrivalDate,
              let checkOutDate = input.prestayInputParams.checkOutDate else { return "" }

        return arrivalDate.localizedVeryShortStringFormat + " - " + checkOutDate.localizedVeryShortStringFormat
    }

    private var staySummary: String {
        let adultsCountDescription = input.prestayInputParams.adultsCountDescription
        let childrenCountDescription = input.prestayInputParams.childrenCount > 0 ? input.prestayInputParams
            .childrenCountDescription : ""
        let roomsCountDescription = input.prestayInputParams.rooms.count > 1 ? input.prestayInputParams
            .roomsCountDescription : ""
        let nightsCountDescription = input.prestayInputParams.nightsCountDescription

        return [adultsCountDescription, childrenCountDescription, roomsCountDescription, nightsCountDescription]
            .filter { !$0.isEmpty }.joined(separator: ", ")
    }

    struct GuestDetailsPriceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol {
        var ctaTitle: String
        var totalValue: String
        var displayTotalValue: Bool { false }
        var items: [CIOLPriceBreakdownItemViewModelProtocol]
    }

    private func updateUserDetails(completion: @escaping (_ success: Bool) -> Void) {
        bookingDetails.updateBooking(with: input.prestayInputParams, rooms: [self.room])

        dataProvider.performHoldBookingWithGuests(
            bookingDetails: bookingDetails,
            isCiolFlow: true,
            isRegCard: true
        ) { success, error in
            guard error == nil else {
                completion(false)
                return
            }
            completion(success)
        }
    }

    func regCardInput(transactionID: String) -> RegCardInput? {
        guard let leadGuest = guests.first(where: { $0.type == .lead }) else { return nil }
        let pdfInput = PDFBookingDetails(
            transactionID: transactionID,
            reservationID: input.prestayInputParams.reservationId ?? "",
            profileID: leadGuest.profileId ?? "",
            hotelName: input.prestayInputParams.hotelName ?? "",
            hotelAddress: input.prestayInputParams.hotelAddress ?? "",
            arrivalDate: input.prestayInputParams.arrivalDate?.parameterString ?? "",
            departureDate: input.prestayInputParams.checkOutDate?.parameterString ?? "",
            guestList: guests
        )

        let fileAttachmentInput = AuthorizationFileAttachmentParams(
            fileName: "",
            reservationId: room.roomId ?? "",
            hotelId: input.prestayInputParams.hotelCode,
            fileAttachment: ""
        )

        let updatePreCheckinInput = UpdatePrecheckInParams(
            hotelId: input.prestayInputParams.hotelCode,
            reservationId: room.roomId ?? "",
            arrivalTime: input.prestayInputParams.arrivalDate?
                                                           .parameterString ?? ""
        )

        let confirmInput = CiolConfirmationDetails(
            bookerFirstName: input.prestayInputParams.leadBookerFirstName ?? "",
            hotelBrand: input.prestayInputParams.hotelBrand ?? .premierInnGermany,
            ciolStartFlow: .bookingConfirmation,
            hotelImage: input.prestayInputParams.hotelImage,
            analyticsInfo: input.defaultAnalytics,
            stay: input.prestayInputParams.stay
        )
        return RegCardInput(
            pdfInput: pdfInput,
            fileAttachmentInput: fileAttachmentInput,
            updatePrecheckinInput: updatePreCheckinInput,
            confirmInput: confirmInput,
            shouldAttachPDF: leadGuest.isNotGerman
        )
    }

    private var leadGuestIsNotGerman: Bool {
        guard let lead = guests.first(where: { $0.type == .lead }) else { return false }
        return lead.isNotGerman
    }

    var paymentViewLayout: WebViewControllerLayout {
        leadGuestIsNotGerman ? .withBanner(authorizationBannerMessage) : .general
    }
}

extension GuestDetailsInteractor: CanGetReservation {}
