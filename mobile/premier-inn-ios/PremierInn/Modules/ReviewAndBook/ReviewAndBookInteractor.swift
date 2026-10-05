//
//  ReviewAndBookInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

enum CCCPaymentError: LocalizedError {
    case storedCardNotImplemented
    case noBooker
    case noBillingAddress
    case noPaymentMethod
    case missingThreeCResponse
    case missingPaymentId
    case missingProviderUrl
    case missingHTML
    case missingResponse
    case unknown

    var errorDescription: String? {
        switch self {
        case .storedCardNotImplemented:
            return "Stored card 3C payment not implemented yet"
        case .missingThreeCResponse, .missingPaymentId, .missingProviderUrl, .missingHTML:
            return PILocalizedString("startingPaymentProcessError")
        default:
            return String(describing: self)
        }
    }
}

protocol ReviewAndBookInteractorProtocol {
    var businessCardQuestionsAndAnswers: [BusinessCardQuestionAndAnswer] { get }
    var bookingDetails: BookingDetails { get }
    var is3CHotel: Bool { get }
    var cccPaymentOptionTrackingParams: PIDictionary? { get }

    func appendValuesToBookingDetails(values: PIDictionary)

    func setCccCardType(_ cardType: String?)
    func saveStayToLocalStore(summary: Stay)
    func resetBookingDetails()
    func stay(with: BookingConfirmation) throws -> Stay
    func holdBookingWithGuests(completion: @escaping (_ success: Bool, _ error: Error?) -> Void)

    // 3CP
    func startCccPayment(
        with values: PIDictionary,
        paypalNonce: String?,
        paypalDeviceData: String?,
        completion: @escaping (Result<CCCPPaymentResponse>) -> Void
    ) throws
    func getPaymentMethods(completion: @escaping (Result<PaymentMethodsResponse>) -> Void)

    // Analytics
    func trackBookingConfirmation(confirmation: BookingConfirmation)
    func trackBookingFailure(error: Error)
    func trackBookingFailure_3CP(error: ErrorUI)
    func trackPaymentNotTaken()
    func trackConfirmationPollingBookingStatusFailed()
    func trackConfirmationPollingReachedMaxAttemptsFailed()

    // Opera
    func getTotalCostWithCityTax(completion: @escaping (Error?) -> Void)

    // PayPal
    func startPaypalVault(completion: @escaping (_ nonce: String?, _ paypalDeviceData: String?, _ error: Error?) -> Void)

    // Confirmation polling
    func checkBasketStatus(completion: @escaping (Result<BookingConfirmation>) -> Void)

    // Wallet analytics tracking
    func setWalletTypeSelected(_ walletType: PIAnalytics.WalletType?)
}

class ReviewAndBookInteractor {
    typealias Analytics = AnalyticsType & AnalyticsPromotionsTrackable

    private var processingPayment = false
    private var paymentResponse: PaymentResponse?
    // Card used in new card iPage tx - used for tracking
    private var cccCardType: String?
    private var walletSelected: PIAnalytics.WalletType?

    private let requestsManager = RequestsManager()

    var analytics: Analytics = AnalyticsManager.shared

    let bookingDetails: BookingDetails

    init(bookingDetails: BookingDetails) {
        // this sets the lead guest appropriately - used to happen in the UserDetails module, but now it's possible to skip it
        if bookingDetails.criteria.rooms.first?.leadGuest == nil {
            bookingDetails.criteria.rooms.first?.leadGuest = bookingDetails.booker
        }

        self.bookingDetails = bookingDetails
    }
}

extension ReviewAndBookInteractor: ReviewAndBookInteractorProtocol {
    var businessCardQuestionsAndAnswers: [BusinessCardQuestionAndAnswer] {
        bookingDetails.businessCardQuestionsAndAnswers
    }

    var is3CHotel: Bool {
        bookingDetails.hotel?.paymentProvider == .cccp
    }

    var cccPaymentOptionTrackingParams: PIDictionary? {
        guard is3CHotel == true else { return nil }
        var dictionary: PIDictionary = [
            PIAnalytics.Keys.bfUserType: bookingDetails.bookingMode == .leisure ? "Leisure" : "Business"
        ]

        let cardTypes: [String] = {
            guard let user = UserSessionManager.sharedInstance.currentUser else { return [] }
            var types = [String]()
            if let personalCardCode = user.paymentPreference?.card?.cardType.cardCode {
                types.append(personalCardCode)
            }
            if let businessCardCode = user.centrallyStoredBusinessCard?.cardType.cardCode {
                types.append(businessCardCode)
            }
            return types
        }()

        if !cardTypes.isEmpty {
            dictionary[PIAnalytics.Keys.bfPaymentCards] = cardTypes.count
            dictionary[PIAnalytics.Keys.bfPaymentCardTypes] = cardTypes.joined(separator: ", ")
        }
        dictionary[PIAnalytics.Keys.productString] = ";\(bookingDetails.hotel?.code ?? "")"
        return dictionary
    }

    func appendValuesToBookingDetails(values: PIDictionary) {
        bookingDetails.userSelectedPaymentOption = values[ReviewAndBookRow.paymentOption.rawValue] as? PaymentIntervalOption
        bookingDetails.businessAccount = getBusinessAccount(values: values)

        // We need to set CNP Auth to true if either 1) The stored business card requires it 2) User has added a new BAC and chosen CNP
        if bookingDetails.primaryPaymentMethod?.card?.cnpRequired == true || bookingDetails.businessAccount?
           .atosPassword != nil {
            bookingDetails.businessAccount?.cardNotPresentAuth = true
        }

        // Here we are adding allowances without letting the (BB) user specify
        if bookingDetails.primaryPaymentMethod?.card?.cnpRequired == true,
           let company = UserSessionManager.sharedInstance.currentUser?.company {
            bookingDetails.businessAccount?.carParkingAllowed = company.bookingAllowances?.allowCarParking
            bookingDetails.businessAccount?.wifiAccessAllowed = company.bookingAllowances?.upsellItemsAllowed?
                .contains(String(UpsellItemsCode.ultimateWifi.rawValue))
            bookingDetails.businessAccount?.otherChargesAllowed = company.bookingAllowances?.allowAdditionalCosts

            // No CNP dinner allowance required if going with a meal deal
            if bookingDetails.roomMealCombos?.first?.meal.id != UpsellItemOperaId.mealDeal.rawValue {
                let dinnerAllowanceAvailableAtHotel: Bool = {
                    (bookingDetails.primaryPaymentMethod?.card?.type.isBusiness == true) ||
                        (bookingDetails.primaryPaymentMethod?.paymentMethodType?.isNewBACCard == true)
                    ? bookingDetails.hotel?.cnpAuthorisation?.dinnerAvailable ?? true
                    : bookingDetails.hotel?.cnpAuthorisation?.dinnerAvailableNonBa ?? true
                }()

                bookingDetails.businessAccount?.alcoholAllowed = company.bookingAllowances?.allowAlcohol

                if dinnerAllowanceAvailableAtHotel == true, let allowance = company.allowance(for: bookingDetails.hotel) {
                    bookingDetails.businessAccount?.dinnerAllowance = allowance.amount.stringValue
                    // Doing this as BB set the default amount not a set amount (certain user levels can override the default amount e.g. travel manager)
                    // If we send a 0 amount with alcohol true BART will throw an error in booking call. This is handled in leisure via form validation where we don't allow a dinner allowance of 0. In the future we should probably allow BB users to set their allowances.
                    if allowance.amount == 0 {
                        bookingDetails.businessAccount?.alcoholAllowed = false
                    }
                }
            }
        }

        // Custom Q&A specified by the company
        bookingDetails.cbtAnswers = getCbtAnswers()
    }

    func setCccCardType(_ cardType: String?) {
        let actualCardType: String?
        if bookingDetails.primaryPaymentMethod?.paymentMethodType == .applePay {
            actualCardType = PaymentMethodType.applePay.rawValue
        } else {
            actualCardType = cardType
        }
        cccCardType = actualCardType
    }

    private func getBusinessAccount(values: PIDictionary) -> BusinessAccount? {
        var businessValues = values

        // update purchase order, customer ref
        if let customerReference = businessCardQuestionsAndAnswers.first(where: { $0.type == .customerReference })?.answer {
            businessValues["customerReference"] = customerReference
        }
        if let purchaseOrder = businessCardQuestionsAndAnswers.first(where: { $0.type == .purchaseOrder })?.answer {
            businessValues["purchaseOrder"] = purchaseOrder
        }

        return try? BusinessAccount(dictionary: businessValues)
    }

    private func getCbtAnswers() -> [PIDictionary]? {
        businessCardQuestionsAndAnswers.filter { $0.type == .custom }.compactMap { questionAnswer in
            guard let id = questionAnswer.question.questionId, let answer = questionAnswer.answer else { return nil }
            let bigBARTBrainAnswer: String = {
                // Check is answer is multiple choice. If not, send back user input string.

                guard questionAnswer.question.managementInformationAnswer?.answerType == .multiChoice else { return answer }
                guard let index = questionAnswer.question.managementInformationAnswer?.answers?.firstIndex(of: answer)
                    else { return answer }
                // Answer is multiple choice and BART thinks we should send the index (starting at 1 of course) of that answer as a string
                return "\(index + 1)"
            }()
            return ["questionId": id, "answer": bigBARTBrainAnswer]
        }
    }

    func trackBookingConfirmation(confirmation: BookingConfirmation) {
        trackBookingConfirmation_Adobe(with: bookingDetails, bookingConfirmation: confirmation)
        trackBookingConfirmation_Firebase(with: bookingDetails)
        trackBookingConfirmation_DT(with: bookingDetails)
        trackBookingConfirmation_AppsFlyer(with: bookingDetails)
        trackBookingConfirmation_ContentSquare(with: bookingDetails)
    }

    func trackBookingFailure(error: Error) {
        guard bookingDetails.hotel?.paymentProvider == .cccp else { return }
        guard let errorCode = error.piMSErrorCode else { return }
        let description = MSMappedError(rawValue: errorCode)?.errorMessage ?? "Unknown error code"

        var paymentTrackingParams = cccPaymentOptionTrackingParams ?? [:]
        paymentTrackingParams[PIAnalytics.Keys.errorCode] = errorCode
        paymentTrackingParams[PIAnalytics.Keys.errorMessage] = description
        paymentTrackingParams[PIAnalytics.Keys.productString] = bookingDetails.trackingProductString
        paymentTrackingParams[PIAnalytics.Keys.hasPaymentFailure] = true

        AnalyticsManager.shared.trackAction(PIAnalytics.Action.cccPaymentBookingFailed, userInfo: paymentTrackingParams)
    }

    func trackBookingFailure_3CP(error: ErrorUI) {
        var paymentTrackingParams = cccPaymentOptionTrackingParams ?? [:]
        paymentTrackingParams[PIAnalytics.Keys.paymentErrorCode] = error.code
        paymentTrackingParams[PIAnalytics.Keys.paymentFailureReasonMessage] = error.description
        paymentTrackingParams[PIAnalytics.Keys.hasPaymentFailureOpera] = true

        AnalyticsManager.shared.trackAction(PIAnalytics.Action.cccPaymentPaymentFailed, userInfo: paymentTrackingParams)
    }
    func trackPaymentNotTaken() {
        analytics.track(errorName: PIAnalytics.Error.paymentNotTakenBookingError)
    }

    func trackConfirmationPollingBookingStatusFailed() {
        analytics.track(errorName: PIAnalytics.Error.confirmationPollingStatusFailedError)
    }

    func trackConfirmationPollingReachedMaxAttemptsFailed() {
        analytics.track(errorName: PIAnalytics.Error.confirmationPollingReachedMaxAttemptsError)
    }

    func saveStayToLocalStore(summary: Stay) {
        guard UserSessionManager.sharedInstance.currentUser == nil else {
            requestsManager.refreshStays(
                for: UserSessionManager.sharedInstance.currentUser,
                shouldAttemptLogin: false
            ) { _ in }
            return
        }

        _ = LocalReservationManager.shared.update(with: [summary], sendUpdateNotification: false)

        ReservationsListViewController.shareDataWithTodayWidget()
    }

    private func roomsLettingsUsingExistingBooking(for newRate: Rate) -> [Room] {
        guard let existingLettings = BookingDetails.sharedInstance.roomLettings else { return [] }

        var roomsArray: [Room] = []
        for (index, room) in existingLettings.enumerated() {
            guard let roomCopy = room.copy() as? Room else { continue }
            guard newRate.rooms?.indices.contains(index) == true else { continue }

            guard let newRateRoom = newRate.rooms?[index] else { continue }

            roomCopy.options = []
            if let option = newRateRoom.options?.first(where: { $0.lettingType == room.options?.first?.lettingType }) {
                roomCopy.options = [option]
            }

            roomsArray.append(roomCopy)
        }

        return roomsArray
    }

    func resetBookingDetails() {
        bookingDetails.reset()
    }

    func stay(with confirmation: BookingConfirmation) throws -> Stay {
        let dictionary = Stay.dictionary(bookingDetails: bookingDetails, confirmation: confirmation)

        return try Stay(dictionary: dictionary)
    }

    func holdBookingWithGuests(completion: @escaping (_ success: Bool, _ error: Error?) -> Void) {
        requestsManager.performHoldBookingWithGuests(
            bookingDetails: bookingDetails,
            isCiolFlow: false,
            completion: completion
        )
    }

    func getPaymentMethods(completion: @escaping (Result<PaymentMethodsResponse>) -> Void) {
        guard let hotel = bookingDetails.hotel,
              let rate = bookingDetails.rate,
              hotel.paymentProvider == .cccp  else { return }

        requestsManager.paymentMethods(
            bookingDetails: bookingDetails,
            hotel: hotel,
            rate: rate,
            user: UserSessionManager.sharedInstance.currentUser,
            isCiol: false
        ) { paymentMethodsResponse, error in
            guard error == nil else {
                return completion(.failure(error: SelectRateError.genericPaymentMethod))
            }
            guard let paymentMethodsResponse = paymentMethodsResponse,
                  let paymentOptions = paymentMethodsResponse.paymentMethods, paymentOptions.isNotEmpty else {
                return completion(.failure(error: SelectRateError.noPaymentMethods))
            }

            completion(.success(result: paymentMethodsResponse))
        }
    }

    func getTotalCostWithCityTax(completion: @escaping (Error?) -> Void) {
        let bookingDetails = BookingDetails.sharedInstance

        requestsManager.getTotalCostWithCityTax(bookingDetails: bookingDetails) { totalCost, error in
            guard error == nil else { return completion(error) }

            bookingDetails.operaConfirmedTotalCost = totalCost
            return completion(nil)
        }
    }

    func startCccPayment(
        with values: PIDictionary,
        paypalNonce: String?,
        paypalDeviceData: String?,
        completion: @escaping (Result<CCCPPaymentResponse>) -> Void
    ) throws {
        // accepted cards only needed for isolating types - maybe use for BAC
        // guard let acceptedCards = bookingDetails.hotel?.acceptedCreditCards else { return }
        guard let booker = bookingDetails.booker else {
            completion(.failure(error: CCCPaymentError.noBooker))
            return
        }

        guard let billingAddress = billingAddress(using: values) else {
            completion(.failure(error: CCCPaymentError.noBillingAddress))
            return
        }

		guard let title = booker.title,
		      let firstName = booker.firstName,
		      let lastName = booker.lastName else {
			completion(.failure(error: CCCPaymentError.noBooker))
            return
		}

        let fullName: FullGuestName = (title, firstName, lastName)

        let billingDetails = BillingDetails(
            email: booker.emailAddress,
            telephone: booker.contactNumber,
            address: billingAddress,
            fullName: fullName
        )
        let card: Card? = {
            guard let paymentCard = bookingDetails.primaryPaymentMethod?.card,
                  paymentCard.token.isNotEmpty else { return nil }
            return paymentCard
        }()
        let isPIBAPaymentMethod = bookingDetails.primaryPaymentMethod?.paymentMethodType?
            .isNewBACCard == true || bookingDetails.primaryPaymentMethod?.card?.type.isBusiness == true
        let paymentParams = CCCPPaymentParams(
            acceptedCardCodes: nil,
            card: card,
            billingDetails: billingDetails,
            journey: .BOOKING,
            paymentInterval: bookingDetails.paymentOption,
            payingWithPIBA: isPIBAPaymentMethod,
            bbQuestionAndAnswers: bookingDetails.shouldShowEmployeeQuestionsForOpera ? bookingDetails
            .operaBusinessQuestionsAndAnswers : nil,
            paymentType: bookingDetails.primaryPaymentMethod?.paymentType,
            paypalNonce: paypalNonce,
            paypalDeviceData: paypalDeviceData,
            usePaypalInitiatePayment: SettingsManager.sharedInstance.featureUsePaypalInitiatePayment == true,
            donationPackage: BookingDetails.sharedInstance.selectedGoshPackage
        )

		let sensorData = AkamaiProtection.sensorData

        requestsManager.cccpPayment(
            with: paymentParams,
            and: bookingDetails,
            and: bookingDetails.basketReference,
            and: false,
            sensorData: sensorData
        ) { response, error in
            if let error = error {
                completion(.failure(error: error.serverErrorForCCCPayment))
            } else if let response = response {
                completion(.success(result: response))
            } else {
                completion(.failure(error: CCCPaymentError.unknown))
            }
        }
    }

    func checkBasketStatus(completion: @escaping (Result<BookingConfirmation>) -> Void) {
        requestsManager.checkBasketStatus(basketReference: bookingDetails.basketReference) { status, error in
            guard let status = status else {
                return completion(.failure(error: error ?? ReviewAndBookMakeBookingError.checkBasketGenericError))
            }

            completion(.success(result: status))
        }
    }

    private func billingAddress(using values: PIDictionary) -> Address? {
        if let storedbillingAddressOption = values[Step2Row.billingAddressSwitch.rawValue] as? Bool,
           storedbillingAddressOption == false, let address = AddressSectionDataProvider.address(for: values) {
            return address
        }
        // try returning appropriate address based on user type
        return bookingDetails.bookingMode == .business ? UserSessionManager.sharedInstance.currentUser?
            .address : bookingDetails.booker?.address
    }

    func setWalletTypeSelected(_ walletType: PIAnalytics.WalletType?) {
        walletSelected = walletType
    }
}

private extension ReviewAndBookInteractor {
    // swiftlint:disable:next function_body_length
    func trackBookingConfirmation_Adobe(with bookingDetails: BookingDetails, bookingConfirmation: BookingConfirmation) {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.prepay] = bookingDetails.userSelectedPaymentOption == .now ? "Prepay" : "Non-Prepay"

        let rooms = bookingDetails.criteria.rooms

        if let cardType = bookingDetails.primaryPaymentMethod?.card?.type {
            data[PIAnalytics.Keys.card] = "\(cardType.cardCode):\(cardType.cardFee == nil ? "NO CCHF" : "CCHF")"
        } else if let cardTypeCode = cccCardType {
            data[PIAnalytics.Keys.card] = "\(cardTypeCode): NO CCHF"
        }

        if let paymentType = bookingDetails.primaryPaymentMethod?.type {
            data[PIAnalytics.Keys.cccPaymentMethodType] = walletSelected?.rawValue ?? paymentType
        }

        if bookingDetails.isPaymentDown {
            data[PIAnalytics.Keys.paymentOutage] = true
        }
        data[PIAnalytics.Keys.bookingID] = bookingDetails.operaBookingReference

        let hubBiggerRoom = (bookingDetails.hotel?.brand == .hub && bookingDetails.rate?.isBiggerRoom ?? false)

        data[PIAnalytics.Keys.roomTypes] = rooms
            .map { hubBiggerRoom ? PILocalizedString("Hub Bigger", comment: "") : $0.type.localizedName }
            .joined(separator: ":")

        let roomOptions = bookingDetails.roomLettings?.compactMap { room in
            room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first
        }
        data[PIAnalytics.Keys.lettingTypes] = roomOptions?.map { $0.lettingType ?? "" }.joined(separator: ":")

        if let roomDescriptions = getRoomDescriptions(using: roomOptions) {
            data[PIAnalytics.Keys.bookingRoomDescription] = roomDescriptions
        }

        if let code = bookingDetails.rate?.classification {
            data[PIAnalytics.Keys.rateCode] = code
        }

        if let rateName = bookingDetails.rate?.name {
            data[PIAnalytics.Keys.rateName] = rateName
        }

        if let promoCode = bookingDetails.rate?.promotionCode {
            data[PIAnalytics.Keys.confPromoCode] = promoCode
        }

        if let uniqueMeals = (bookingDetails.roomMealCombos?.compactMap { $0.meal.legend }), !uniqueMeals.isEmpty {
            let mealBreakdown = Set(uniqueMeals).map { meal in
                let amount = bookingDetails.bookingMealAmount(for: meal)
                return "\(amount) x \(meal)"
            }.joined(separator: ", ")

            data[PIAnalytics.Keys.addedExtras] = mealBreakdown
        }

        if let extraUpsells = bookingDetails.roomExtraPackages,
           extraUpsells.isNotEmpty {
            let groupByUpsellName = Dictionary(grouping: extraUpsells, by: { $0.meal.legend })

            var extrasBreakdown = "\(data[PIAnalytics.Keys.addedExtras] as? String ?? ""), "
            groupByUpsellName.forEach { upsellName, combos in
                extrasBreakdown.append("\(combos.count) x \(upsellName), ")
            }
            data[PIAnalytics.Keys.addedExtras] = extrasBreakdown
        }
        data[PIAnalytics.Keys.upsellsRevenue] = bookingDetails.mealTotalCost?.amount.doubleValue ?? 0
        data[PIAnalytics.Keys.eci] = bookingDetails.earlyCheckInCost?.amount.doubleValue ?? 0
        data[PIAnalytics.Keys.lco] = bookingDetails.lateCheckOutCost?.amount.doubleValue ?? 0

        if let bookingFlowHoldTime = bookingDetails.bookingFlowHoldTime {
            let dateDifference = Date().timeIntervalSince(bookingFlowHoldTime)
            data[PIAnalytics.Keys.bookingFlowCompleteTime] = dateDifference.stringInMinutesSeconds
        }

        let criteria = bookingDetails.criteria
        let checkoutDate = criteria.checkOutDate ?? Date()
        let event20 = bookingDetails.roomLettings?.totalCost?.amount ?? 0
        let event36 = bookingDetails.extrasTotalCost?.amount ?? 0
        let event37 = bookingDetails.mealTotalCost?.amount ?? 0
        let event30 = bookingDetails.roomMealCombos != nil ? "1" : "0"
        let event31 = "\(rooms.count)"
        let event54 = "0" // GOSH
        let event82 = "\(rooms.count * bookingDetails.criteria.nights)" // Room nights

        let productString = bookingDetails.trackingProductString

        data[PIAnalytics.Keys.environment] = AnalyticsConstants.environment
        data[PIAnalytics.Keys.userLogin] = UserSessionManager.sharedInstance.currentUser != nil ? LoggedInAnalytic.loggedIn
            .rawValue : LoggedInAnalytic.notLoggedIn.rawValue
        data[PIAnalytics.Keys.timeZone] = TimeZone.current.description
        data[PIAnalytics.Keys.language] = Locale.current.language.languageCode?.identifier ?? "n/a"
        data[PIAnalytics.Keys.screenType] = PIAnalytics.StateTypes.bookingFlow
        data[PIAnalytics.Keys.time] = Date().analyticsTimeFormat
        data[PIAnalytics.Keys.leadDaysConf] = "\(criteria.leadDays)"
        data[PIAnalytics.Keys.checkInConf] = "\(criteria.arrivalDate.analyticsDateFormat)"
        data[PIAnalytics.Keys.checkOutConf] = "\(checkoutDate.analyticsDateFormat)"
        data[PIAnalytics.Keys.productString] = productString
        data[PIAnalytics.Keys.customerType] = (bookingDetails.purpose ?? .leisure).analyticsString
        data[PIAnalytics.Keys.hasPaymentFailure] = false
        data[PIAnalytics.Keys.confCccPaymentTakenNow] = bookingDetails.paymentOption == .now

        if let businessAccount = bookingDetails.businessAccount {
            data[PIAnalytics.Keys.bacDinner] = businessAccount.dinnerAllowance != nil
            data[PIAnalytics.Keys.bacDinnerBudget] = businessAccount.dinnerAllowance ?? ""
            data[PIAnalytics.Keys.bacAlcohol] = businessAccount.alcoholAllowed ?? false
            data[PIAnalytics.Keys.bacParking] = businessAccount.carParkingAllowed ?? false
            data[PIAnalytics.Keys.bacWifi] = businessAccount.wifiAccessAllowed ?? false
        }

        if let user = UserSessionManager.sharedInstance.currentUser, let companyId = user.companyId,
           let accessLevel = user.accessLevel?.rawValue {
            data[PIAnalytics.Keys.companyID] = companyId
            data[PIAnalytics.Keys.businessUserLevel] = accessLevel
        }

        data[PIAnalytics.Keys.event36] = event36 != .zero ? "1" : "0" // wifi revenue included as part of booking
        data[PIAnalytics.Keys.event37] = event37 != .zero ? "1" : "0" // food revenue included as part of booking
        data[PIAnalytics.Keys.event30] = event30 // added extras selected food/wifi...
        data[PIAnalytics.Keys.event20] = event20 != .zero ? "1" : "0" // all bookings (with a room)
        data[PIAnalytics.Keys.event31] = event31 // number of rooms booked
        data[PIAnalytics.Keys.event54] = event54 // set to gosh revenue donation amount if given
        data[PIAnalytics.Keys.eventsString] = bookingDetails.hotel?.eventString(existingEvents: [
            "event37=\(event37)",
            "event30=\(event30)",
            "event20=\(event20)",
            "event31=\(event31)",
            "event54=\(event54)",
            "event82=\(event82)",
            "event36=\(event36)"
        ]) ?? ""
        data[PIAnalytics.Keys.purchase] = "1"
        data[PIAnalytics.Keys.goshAmount] = bookingDetails.goshDonation?.amount.doubleValue.stringForAnalyticsCost

        data[PIAnalytics.Keys.confPromoBookingComplete] = bookingDetails.isPromotionalBooking
        data[PIAnalytics.Keys.promoBookingComplete] = bookingDetails.isPromotionalBooking

        if let promotionsAnalyticsData = analytics.getPromotionsAnalyticsDict(with: bookingDetails) {
            data.mergePreferNew(promotionsAnalyticsData)
        }

        self.analytics.trackState(PIAnalytics.StateNames.bookingConfirmation, data: data)
    }

    func trackBookingConfirmation_Firebase(with bookingDetails: BookingDetails) {
        var parameters: [String: NSObject] = [:]
        parameters[FirebaseAnalytics.Parameter.quantity] = 1 as NSObject
        parameters[FirebaseAnalytics.Parameter.userType] = (bookingDetails.purpose?.rawValue ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.paymentType] = (bookingDetails.primaryPaymentMethod?.card?.type
            .cardCode ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.currency] = (bookingDetails.totalCost.currencyCode) as NSObject
        parameters[FirebaseAnalytics.Parameter.totalBookingPrice] = (bookingDetails.totalCost.amount) as NSObject
        parameters[FirebaseAnalytics.Parameter.rateName] = (bookingDetails.rate?.name ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.startDate] = bookingDetails.criteria.arrivalDate.parameterString as NSObject
        parameters[FirebaseAnalytics.Parameter.endDate] = (bookingDetails.criteria.checkOutDate?
            .parameterString ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.numberOfNights] = bookingDetails.criteria.nights as NSObject
        parameters[FirebaseAnalytics.Parameter.numberOfRooms] = bookingDetails.criteria.rooms.count as NSObject
        parameters[FirebaseAnalytics.Parameter.numberOfPeople] = bookingDetails.criteria.guestsCount as NSObject
        parameters[FirebaseAnalytics.Parameter.hotelCode] = (bookingDetails.hotel?.code ?? "") as NSObject

        self.analytics.log(event: FirebaseAnalytics.Event.bookingConfirmation, parameters: parameters)
    }

    func trackBookingConfirmation_DT(with bookingDetails: BookingDetails) {
        AnalyticsManager.shared.trackDTMetric(
            double: bookingDetails.totalCost.amount.doubleValue,
            actionName: DynatraceActionName.totalRevenueAction,
            actionKey: DynatraceActionKey.totalRevenueKey
        )
    }

    func trackBookingConfirmation_AppsFlyer(with bookingDetails: BookingDetails) {
        var parameters: PIDictionary = [:]

        guard let reference = bookingDetails.operaBookingReference,
              let revenue = bookingDetails.totalCostOperaWithDonations?.amount.doubleValue,
              let currency = bookingDetails.rate?.totalCost.currencyCode,
              let hotelCode = bookingDetails.hotel?.code else { return }

        parameters[AppsFlyerAnalytics.Parameter.bookingReference] = reference
        parameters[AppsFlyerAnalytics.Parameter.revenue] = revenue
        parameters[AppsFlyerAnalytics.Parameter.currency] = currency
        parameters[AppsFlyerAnalytics.Parameter.hotelCode] = hotelCode

        AppsFlyerManager.sharedInstance.trackBookingConfirmation(parameters: parameters)
    }

    func trackBookingConfirmation_ContentSquare(with bookingDetails: BookingDetails) {
        guard let reference = bookingDetails.operaBookingReference,
              let revenue = bookingDetails.operaConfirmedTotalCost else {
            printDev("could not track booking transaction on CSQ - opera booking reference or total cost missing")
            return
        }

        AnalyticsManager.shared.trackCSQTransaction(bookingReference: reference, totalCost: revenue)
    }

    func getRoomDescriptions(using roomOptions: [RoomLettingOption]?) -> String? {
        guard let roomOptions else { return nil }
        let roomDescriptions = roomOptions
            .compactMap { $0.lettingType }
            .compactMap { SettingsManager.sharedInstance.roomLabelFor(lettingType: $0) }
            .joined(separator: ", ")

        return roomDescriptions.isEmpty ? nil : roomDescriptions
    }
}
