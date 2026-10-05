//
//  CiolReviewAndPayInteractor.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import SimpleNetwork
//import BraintreeCore
//import BraintreePayPal
//import BraintreeDataCollector

enum CiolReviewAndPayPaymentError: LocalizedError {
    case ciolFailed
}

enum AddressLineType {
    case postCode(String)
    case addressLine1(String)
    case addressLine2(String)
    case addressLine3(String)
}

protocol CiolReviewAndPayDataProvider: RegCardProvider {
    func paymentMethods(
        bookingDetails: BookingDetails,
        hotel: Hotel,
        rate: Rate,
        user: User?,
        isCiol: Bool,
        completion: @escaping (PaymentMethodsResponse?, Error?) -> Void
    )
    func cccpPayment(
        with paymentParams: CCCPPaymentParams,
        and stayDetails: CCCPStayDetails,
        and sessionId: String?,
        and isCiol: Bool,
        sensorData: String,
        completion: @escaping (_ response: CCCPPaymentResponse?, _ error: Error?) -> Void
    )
    func checkBasketStatus(
        basketReference: String?,
        completion: @escaping (_ status: BookingConfirmation?, _ error: Error?) -> Void
    )
    func amendCiolPackages(amendInfo: CiolAmendInfo, completion: @escaping (_ response: Bool?, _ error: Error?) -> Void)
    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void)
    func ciolBackgroundCharge(
        basketReference: String,
        token: String,
        completion: @escaping (
            _ response: CiolBackgroundChargeResponse?,
            _ error: Error?
        ) -> Void
    )
}

extension RequestsManager: CiolReviewAndPayDataProvider { }

final class CiolReviewAndPayInteractor: CiolReviewAndPayInteractorProtocol {
    private enum Constants {
        static let pibaSubTypeIdentifier = "PIBAGB"
        static let fallbackBookerTitle = ""
    }

    private let dataProvider: CiolReviewAndPayDataProvider
    private let inputParams: CiolReviewAndPayInputParams
    private let settingsManager: SettingsManager

    private(set) var cardType: String?

    var bookingDetails = BookingDetails.sharedInstance
    var showBillingAddressFields = false
    var paymentDidFail = false
    var doesUserHavePIBACard: Bool = false

    weak var output: CiolReviewAndPayOutput?
    weak var failedPaymentDelegate: CiolUpsellPayDelegate?

    var isGermanHotel: Bool {
        inputParams.hotelBrand == .premierInnGermany &&
        inputParams.showBannerMessage
    }

    var paymentViewLayout: WebViewControllerLayout {
        inputParams.showBannerMessage ?
        .withBanner(deRegCardPaymentInformationMessage) :
        .general
    }

    var deRegCardPaymentInformationMessage: NSAttributedString {
        let muttableAttributedString = NSMutableAttributedString()

        muttableAttributedString
            .append(NSAttributedString(string: PILocalizedString("deRegCardPaymentDisclaimerMessagePart1")))
        muttableAttributedString.append(NSAttributedString(
            string: PILocalizedString("deRegCardPaymentDisclaimerMessagePart2"),
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        ))
        muttableAttributedString
            .append(NSAttributedString(string: PILocalizedString("deRegCardPaymentDisclaimerMessagePart3")))
        muttableAttributedString.append(NSAttributedString(
            string: PILocalizedString("deRegCardPaymentDisclaimerMessagePart4"),
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        ))
        muttableAttributedString
            .append(NSAttributedString(string: PILocalizedString("deRegCardPaymentDisclaimerMessagePart5")))
        muttableAttributedString.append(NSAttributedString(
            string: PILocalizedString("deRegCardPaymentDisclaimerMessagePart6"),
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        ))
        muttableAttributedString
            .append(NSAttributedString(string: "\n\n" + PILocalizedString("deRegCardPaymentDisclaimerMessage")))
        return muttableAttributedString
    }

    var storedAddressModel: StoredAddressModel = {
        var storedAddressModel = StoredAddressModel()
        let defaultCountry = LanguageManager.supportedLanguage == .german ? Country.germany : Country.greatBritain
        storedAddressModel.country = defaultCountry
        return storedAddressModel
    }()

    init(
        dataProvider: CiolReviewAndPayDataProvider = RequestsManager(),
        inputParams: CiolReviewAndPayInputParams,
        settingsManager: SettingsManager = .sharedInstance
    ) {
        self.dataProvider = dataProvider
        self.inputParams = inputParams
        self.settingsManager = settingsManager
    }

    var customAnalyticsParameters: PIDictionary? { inputParams.defaultAnalyticsParams }

    private var isPIBAPaymentMethod: Bool {
        bookingDetails.primaryPaymentMethod?.paymentMethodType?.isNewBACCard == true ||
        bookingDetails.primaryPaymentMethod?.card?.type.isBusiness == true
    }

    private var paymentCard: Card? {
        guard let paymentCard = self.bookingDetails.primaryPaymentMethod?.card,
              paymentCard.token.isNotEmpty else {
            return nil
        }
        return paymentCard
    }

    private var shouldPerformBackgroundCharge: Bool {
        settingsManager.featureThirdPartyPrepaid &&
        !bookingDetails.isCiolBackgroundChargePerformedSuccessfully &&
        inputParams.ciolPaymentActions?.shouldPerformBackgroundCharge == true
    }

    func updateSelectedPaymentMethod(paymentViewModel: PaymentMethodViewModelType) {
        BookingDetails.sharedInstance.primaryPaymentMethod = BookingDetails.sharedInstance.paymentMethods?
            .first(where: { $0.paymentMethodType == paymentViewModel.type })
    }

    func handleBackgroundChargeIfRequired(completion: @escaping (Bool) -> Void) {
        guard shouldPerformBackgroundCharge else {
            completion(true)
            return
        }

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

            self?.bookingDetails.isCiolBackgroundChargePerformedSuccessfully = true
            completion(true)
        }
    }

    func getPaymentMethods(completion: @escaping (Result<PaymentMethodsResponse>) -> Void) {
        guard let hotel = bookingDetails.hotel,
              let rate = bookingDetails.rate else {
            completion(.failure(error: SelectRateError.userRoleNotAllowedToMakeBookings))
            return
        }

        dataProvider.paymentMethods(
            bookingDetails: bookingDetails,
            hotel: hotel,
            rate: rate,
            user: UserSessionManager.sharedInstance.currentUser,
            isCiol: true
        ) { [weak self] paymentMethodsResponse, error in
            guard let self else {
                return completion(.failure(error: SelectRateError.userRoleNotAllowedToMakeBookings))
            }

            guard error == nil else {
                return completion(.failure(error: SelectRateError.genericPaymentMethod))
            }

            guard let paymentMethodsResponse = paymentMethodsResponse,
                  let paymentOptions = paymentMethodsResponse.paymentMethods,
                  paymentOptions.isNotEmpty else {
                return completion(.failure(error: SelectRateError.noPaymentMethods))
            }

            // For PIBA CP bookings, allow NEW_PIBA payment method but filter stored business cards
            // For other CIOL flows, filter all business cards
            let isPIBACP = inputParams.stay.paymentOption == .pibaCardPresent
            let isPIBACPFeatureEnabled = SettingsManager.sharedInstance.featurePIBACPEnabled

            bookingDetails.paymentMethods = paymentOptions
                .sorted(by: { $0.order < $1.order })
                .filter { paymentOption in
                    if paymentOption.card?.type.isBusiness == true {
                        return false
                    }
                    // For PIBA CP bookings with feature flag enabled, allow PIBA payment methods (NEW_PIBA / NEW_PIBA_EURO)
                    // For PIBA CP bookings with feature flag disabled, filter out PIBA payment methods (only non-PIBA cards allowed)
                    guard isPIBACP == false else {
                        return isPIBACPFeatureEnabled ? true : (paymentOption.subType != Constants.pibaSubTypeIdentifier
                            && paymentOption.subType != SimpleNetwork.Constants.PIBAEuro.subType)
                    }
                    // For other CIOL flows, exclude PIBA payment methods
                    return paymentOption.subType != Constants.pibaSubTypeIdentifier
                        && paymentOption.subType != SimpleNetwork.Constants.PIBAEuro.subType
                }

            bookingDetails.primaryPaymentMethod = bookingDetails
                .paymentMethods?
                .first(where: { $0.enabled })

            completion(.success(result: paymentMethodsResponse))
        }
    }

    /// Checks if the user has a PIBA (Premier Inn Business Account) card
    /// For PIBA CP bookings with feature flag enabled: PIBA cards CAN be used, so don't show warning
    /// For PIBA CP bookings with feature flag disabled: PIBA cards cannot be used, show warning
    /// For other CIOL flows: PIBA cards cannot be used, show warning
    /// - Parameter paymentMethods: Available payment methods from the API
    func checkPIBAPaymentMethodExists(paymentMethods: [PaymentOption]?) {
        let isPIBACP = inputParams.stay.paymentOption == .pibaCardPresent
        let isPIBACPFeatureEnabled = SettingsManager.sharedInstance.featurePIBACPEnabled
        if isPIBACP && isPIBACPFeatureEnabled {
            doesUserHavePIBACard = false
            return
        }

        // Check for both UK PIBA (PIBAGB) and Euro PIBA (PIBADE)
        let hasPIBAInPaymentMethods = paymentMethods?.contains {
            $0.subType == Constants.pibaSubTypeIdentifier
            || $0.subType == SimpleNetwork.Constants.PIBAEuro.subType
        } ?? false

        doesUserHavePIBACard = hasPIBAInPaymentMethods || isPIBAPaymentMethod
    }

    func updateUpsells(upsellAmendInfo: CiolAmendInfo) async throws {
        try await withCheckedThrowingContinuation { [weak self] continuation in
            self?.dataProvider.amendCiolPackages(amendInfo: upsellAmendInfo) { _, error in
                error != nil ? continuation.resume(throwing: CIOLError.updateUserDetails) : continuation.resume()
            }
        }
    }

    func startCccPayment(
        with paypalNonce: String?,
        paypalDeviceData: String?,
        completion: @escaping (Result<CCCPPaymentResponse>) -> Void
    ) throws {
        let payment = { [weak self] in
            guard let self else {
                return
            }

            guard let booker = bookingDetails.booker else {
                return completion(.failure(error: CCCPaymentError.noBooker))
            }

            guard let billingAddress = showBillingAddressFields
                  ? storedAddressModel.address
                  : inputParams.address else {
                return completion(.failure(error: CCCPaymentError.noBillingAddress))
            }

            let title = booker.title ?? Constants.fallbackBookerTitle
			guard let firstName = booker.firstName,
			      let lastName = booker.lastName else {
				return completion(.failure(error: CCCPaymentError.noBooker))
			}

            let shouldShowPaypalOption = SettingsManager.sharedInstance.featureUsePaypalInitiatePayment
            let fullName: FullGuestName = (title, firstName, lastName)

            let billingDetails = BillingDetails(
                email: booker.emailAddress,
                telephone: booker.contactNumber,
                address: billingAddress,
                fullName: fullName
            )
            // PIBA payments require PAY_ON_ARRIVAL (.later) as PAY_NOW is not supported
            // Other payment methods use PAY_NOW (.now)
            let paymentInterval: PaymentIntervalOption = isPIBAPaymentMethod ? .later : .now
            let paymentParams = CCCPPaymentParams(
                acceptedCardCodes: nil,
                card: paymentCard,
                billingDetails: billingDetails,
                journey: .CIOL,
                paymentInterval: paymentInterval,
                payingWithPIBA: isPIBAPaymentMethod,
                bbQuestionAndAnswers: nil,
                paymentType: bookingDetails.primaryPaymentMethod?.paymentType,
                paypalNonce: paypalNonce,
                paypalDeviceData: paypalDeviceData,
                usePaypalInitiatePayment: shouldShowPaypalOption,
                donationPackage: nil
            )

			let sensorData = AkamaiProtection.sensorData

            dataProvider.cccpPayment(
                with: paymentParams,
                and: bookingDetails,
                and: bookingDetails.basketReference,
                and: true,
                sensorData: sensorData
            ) { [weak self] response, error in
                if let error = error {
                    self?.paymentDidFail = true
                    completion(.failure(error: error.serverErrorForCCCPayment))
                } else if let response = response {
                    completion(.success(result: response))
                }
            }
        }
        self.paymentDidFail = false
        guard let amendInfo = inputParams.upsellAmendInfo,
              amendInfo.roomsSelections.flatMap({ $0.packagesSelection }).isNotEmpty else {
            payment()
            return
        }
        Task { [weak self] in
            guard let self else { return }
            do {
                try await updateUpsells(upsellAmendInfo: amendInfo)
                payment()
            } catch {
                Task { @MainActor in
                    completion(.failure(error: error))
                }
            }
        }
    }

    func didPop() {
        guard paymentDidFail else { return }
        failedPaymentDelegate?.refreshForFailedPayment()
    }

    func failedPayment() {
        paymentDidFail = true
    }

    func checkBasketStatus(transactionID: String, completion: @escaping (Result<BookingConfirmation>) -> Void) {
        dataProvider.checkBasketStatus(basketReference: bookingDetails.basketReference) { [weak self] status, error in
            guard let self,
                  let status = status else {
                return completion(.failure(error: error ?? ReviewAndBookMakeBookingError.checkBasketGenericError))
            }

            completionFlow(
                with: inputParams.regCardFlow,
                bookingConfirmation: status,
                transactionID: transactionID,
                completion: completion
            )
        }
    }

    private func completionFlow(
        with flow: RegCardFlow,
        bookingConfirmation: BookingConfirmation,
        transactionID: String,
        completion: @escaping (Result<BookingConfirmation>) -> Void
    ) {
        switch flow {
        case .general:
            completion(.success(result: bookingConfirmation))
        case .regCard:
            switch bookingConfirmation.bookingConfirmationOperaStatus?.basketStatus {
            case .complete, .preCheckedIn:

                guard var regCardInput = inputParams.regCardInput else {
                    completion(.success(result: bookingConfirmation))
                    return
                }
                var pdfInput = regCardInput.pdfInput
                pdfInput.transactionID = transactionID
                regCardInput.pdfInput = pdfInput
                Task { @MainActor in
                    do {
                        try await finishRegCardWithOutstanding(
                            regcardInput: regCardInput,
                            provider: dataProvider,
                            bookingConfirmation: bookingConfirmation
                        )
                        completion(.success(result: bookingConfirmation))
                    } catch {
                        output?.didFailRegCard(error: CIOLError.regCardFailed)
                        NotificationCenter.default.post(name: .staysWillChange, object: nil)
                        dataProvider.refreshStays(
                            for: UserSessionManager.sharedInstance.currentUser,
                            shouldAttemptLogin: true,
                            completion: { _ in
                            NotificationCenter.default.post(name: .reservationSummariesDidChange, object: nil)
                        }
                        )
                    }
                }
            default:
                completion(.success(result: bookingConfirmation))
            }
        }
    }

    func startPaypalVault(completion: @escaping (_ nonce: String?, _ paypalDeviceData: String?, _ error: Error?) -> Void) {
        guard let clientToken = bookingDetails.primaryPaymentMethod?.clientToken else {
            completion(nil, nil, PayPalError.missingClientToken)
            return
        }

//        guard let braintreeClient = BTAPIClient(authorization: clientToken) else {
//            completion(nil, nil, PayPalError.apiClientFailed)
//            return
//        }
//
//        let payPalClient = BTPayPalClient(apiClient: braintreeClient)
//
//        let request = BTPayPalVaultRequest()
//
//        payPalClient.tokenize(request) { (tokenizedPayPalAccount, error) in
//            if let tokenizedPayPalAccount = tokenizedPayPalAccount {
//                let dataCollector = BTDataCollector(apiClient: braintreeClient)
//                dataCollector.collectDeviceData { deviceData, _ in
//                    completion(tokenizedPayPalAccount.nonce, deviceData, nil)
//                }
//            } else {
//                return completion(nil, nil, error)
//            }
//        }
    }

    func updateStoredAddress(with addressLine: AddressLineType) {
        switch addressLine {
        case .postCode(let value):
            storedAddressModel.postcode = value
        case .addressLine1(let value):
            storedAddressModel.line1 = value
        case .addressLine2(let value):
            storedAddressModel.line2 = value
        case .addressLine3(let value):
            storedAddressModel.line3 = value
        }
    }

    private func completionAnalytics() -> PIDictionary {
        var info = inputParams.defaultAnalyticsParams
        info = info.mergeByKeepingAllValues(with: inputParams.additionalAnalyticsParams)
        info[PIAnalytics.Keys.checkInOnlineCardType] = cardType ?? bookingDetails.primaryPaymentMethod?.card?.cardType
        info[PIAnalytics.Keys.checkInOnlineBillingAddress] = showBillingAddressFields
        return info
    }

    func setCccCardType(_ cardType: String?) {
        let actualCardType: String?
        if bookingDetails.primaryPaymentMethod?.paymentMethodType == .applePay {
            actualCardType = PaymentMethodType.applePay.rawValue
        } else {
            actualCardType = cardType
        }
        self.cardType = actualCardType
    }

    func trackPriceBreakdownTapAnalytics() {
        var dictionary = inputParams.defaultAnalyticsParams
        dictionary[PIAnalytics.Keys.checkInOnlineBtnExpand] = bookingDetails.totalCost.localizedValue

        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.ciolPayment, data: dictionary)
    }

    func trackContinueButtonAnalytics() {
        var dictionary = inputParams.defaultAnalyticsParams
        dictionary[PIAnalytics.Keys.checkInOnlineBtnContinue] = true

        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.ciolPayment, data: dictionary)
    }
}

extension CiolReviewAndPayInteractor {
    struct ViewModel: CiolReviewAndPayViewModel {
        var bookingSummaryViewModel: BookingSummaryCIOLViewModelProtocol
        var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol
        var formattedBillingAddress: String
        var billingAddress: StoredAddressModel
        var confirmationDetails: CiolConfirmationDetails
        var isBillingFieldOn: Bool
        var isGermanHotel: Bool
        var deRegCardPaymentInformationMessage: NSAttributedString
        var paymentMethodPIBAUnavailable: PIBAUnavailableViewModelProtocol

        var paymentMethods: [PaymentMethodViewModelType] {
            BookingDetails.sharedInstance.paymentMethodsViewModel.paymentMethods
        }

        var selectedPaymentMethod: PaymentOption? {
            BookingDetails.sharedInstance.primaryPaymentMethod
        }

        var currentUserAccessLevel: AccessLevel? {
            UserSessionManager.sharedInstance.currentUser?.accessLevel
        }
    }

    struct CIOLPIBAUnavailableViewModel: PIBAUnavailableViewModelProtocol {
        var message: String
        var shouldShow: Bool
    }

    private var confirmationDetails: CiolConfirmationDetails {
        CiolConfirmationDetails(
            bookerFirstName: inputParams.bookerFirstName,
            hotelBrand: inputParams.hotelBrand,
            ciolStartFlow: inputParams.ciolFlow,
            hotelImage: inputParams.bookingSummaryViewModel.image,
            analyticsInfo: completionAnalytics(),
            stay: inputParams.stay
        )
    }

    var viewModel: CiolReviewAndPayViewModel {
        ViewModel(
            bookingSummaryViewModel: inputParams.bookingSummaryViewModel,
            priceBreakdownViewModel: priceBreakdownViewModel,
            formattedBillingAddress: formattedBillingAddress,
            billingAddress: storedAddressModel,
            confirmationDetails: confirmationDetails,
            isBillingFieldOn: showBillingAddressFields,
            isGermanHotel: isGermanHotel,
            deRegCardPaymentInformationMessage: deRegCardPaymentInformationMessage,
            paymentMethodPIBAUnavailable: paymentMethodPIBAUnavailable
        )
    }

    private var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol {
        if let thirdPartyCityTax = inputParams.ciolPaymentActions?.cityTax {
            return getPriceBreakdownForThirdPartyCityTax(cityTax: thirdPartyCityTax)
        }

        if let thirdPartyOutstandingbalance = inputParams.ciolPaymentActions?.outstandingBalance {
            return getPriceBreakdownForThirdPartyOutstandingBalance(cost: thirdPartyOutstandingbalance)
        }

        return inputParams.priceBreakdownViewModel
    }

    private func getPriceBreakdownForThirdPartyCityTax(cityTax: Cost) -> CIOLPriceBreakdownViewModelProtocol {
        PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("ciolContinueToPay"),
            totalValue: cityTax.localizedValue,
            items: [
                PreStayInteractor.CIOLPriceBreakdownItemViewModel(
                    name: PILocalizedString("ciolCityTaxDisclaimerTitle"),
                    value: cityTax,
                    quantity: 1
                )
            ]
        )
    }

    private func getPriceBreakdownForThirdPartyOutstandingBalance(cost: Cost) -> CIOLPriceBreakdownViewModelProtocol {
        PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: cost.localizedValue,
            items: [
                PreStayInteractor.CIOLPriceBreakdownItemViewModel(
                    name: PILocalizedString("preStayOutstandingBalance"),
                    value: cost,
                    quantity: 1
                )
            ]
        )
    }

    private var thirdPartyCityTax: Cost? {
        inputParams.ciolPaymentActions?.cityTax
    }

    private var paymentMethodPIBAUnavailable: PIBAUnavailableViewModelProtocol {
        CIOLPIBAUnavailableViewModel(
            message: PILocalizedString("ciolPibaCardUnavailableMessage"),
            shouldShow: doesUserHavePIBACard
        )
    }

    private var formattedBillingAddress: String {
        let prefix = PILocalizedString("ciolSameAsBookersAddress")
        let postcode = inputParams.address?.postcode ?? ""

        return [prefix, postcode]
            .filter { !$0.isEmpty }
            .joined(separator: "\n")
    }
}

extension CiolReviewAndPayInteractor: CanCompleteRegCard {}
