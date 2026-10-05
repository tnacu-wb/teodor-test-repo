//
//  CiolReviewAndPayModule.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

enum CiolReviewAndPayModule {
    static func build(
        inputParams: CiolReviewAndPayInputParams,
        failedPaymentDelegate: CiolUpsellPayDelegate? = nil
    ) -> UIViewController {
        let viewController = CiolReviewAndPayViewController()
        viewController.eventHandler = {
            let presenter = CiolReviewAndPayPresenter()
            let interactor = CiolReviewAndPayInteractor(inputParams: inputParams)
            interactor.failedPaymentDelegate = failedPaymentDelegate
            let router = CiolReviewAndPayRouter(ciolFlow: inputParams.ciolFlow)

            viewController.eventHandler = presenter

            presenter.view = viewController
            presenter.interactor = interactor
            presenter.router = router
            interactor.output = presenter
            router.viewController = viewController
            return presenter
        }()
        return viewController
    }
}

protocol CiolReviewAndPayViewModel {
    var bookingSummaryViewModel: BookingSummaryCIOLViewModelProtocol { get }
    var paymentMethods: [PaymentMethodViewModelType] { get }
    var selectedPaymentMethod: PaymentOption? { get }
    var currentUserAccessLevel: AccessLevel? { get }
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol { get }
    var formattedBillingAddress: String { get }
    var billingAddress: StoredAddressModel { get }
    var isBillingFieldOn: Bool { get }
    var confirmationDetails: CiolConfirmationDetails { get }
    var isGermanHotel: Bool { get }
    var deRegCardPaymentInformationMessage: NSAttributedString { get }
    var paymentMethodPIBAUnavailable: PIBAUnavailableViewModelProtocol { get }
}

protocol PIBAUnavailableViewModelProtocol {
    var message: String { get }
    var shouldShow: Bool { get }
}

protocol CiolReviewAndPayRouterProtocol {
    func processPayment(
        with cccpiPageParams: ThreeCiPageParams,
        using threeCiPageDelegate: ThreeCiPageDelegate,
        webDelegate: WebViewControllerDelegate,
        and  webviewLayout: WebViewControllerLayout
    )
    func navigateToConfirmationScreen(ciolConfirmationDetails: CiolConfirmationDetails)
    func showCountriesView(indexPath: IndexPath, completion: ((CountryItem) -> Void)?)
    func showPostcodePicker(with postCode: String?, addressType: AddressType, completion: ((StoredAddressModel) -> Void)?)
    func navigateToStart()
}

protocol CiolReviewAndPayInputParamsProtocol {
    var ciolFlow: CIOLStartFlow { get }
    var bookerFirstName: String { get }
    var bookingReference: String { get }
    var hotelBrand: HotelBrand { get }
    var bookingSummaryViewModel: BookingSummaryCIOLViewModelProtocol { get }
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol { get }
    var address: Address? { get }
    var defaultAnalyticsParams: PIDictionary { get }
    var additionalAnalyticsParams: PIDictionary { get }
    var upsellAmendInfo: CiolAmendInfo? { get }
    var regCardFlow: RegCardFlow { get set }
    var regCardInput: RegCardInput? { get set }
    var showBannerMessage: Bool { get }
}

struct CiolReviewAndPayInputParams: CiolReviewAndPayInputParamsProtocol {
    var ciolFlow: CIOLStartFlow
    var bookerFirstName: String
    var bookingReference: String
    var hotelBrand: HotelBrand
    var bookingSummaryViewModel: BookingSummaryCIOLViewModelProtocol
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol
    var address: Address?
    /// Analytics params to be used on track screen
    var defaultAnalyticsParams: PIDictionary
    /// Analytics params with upsells and price details to be used on confirmation
    var additionalAnalyticsParams: PIDictionary
    var stay: Stay
    var upsellAmendInfo: CiolAmendInfo?
    var regCardFlow: RegCardFlow = .general
    var regCardInput: RegCardInput?
    var showBannerMessage: Bool
    var ciolPaymentActions: CiolPaymentActionsResponse?
}

protocol CiolReviewAndPayInteractorProtocol: AnyObject {
    var viewModel: CiolReviewAndPayViewModel { get }
    var bookingDetails: BookingDetails { get }
    var showBillingAddressFields: Bool { get set }
    var customAnalyticsParameters: PIDictionary? { get }
    var storedAddressModel: StoredAddressModel { get set }
    var paymentViewLayout: WebViewControllerLayout { get }

    func updateSelectedPaymentMethod(paymentViewModel: PaymentMethodViewModelType)
    func getPaymentMethods(completion: @escaping (Result<PaymentMethodsResponse>) -> Void)
    func startCccPayment(
        with paypalNonce: String?,
        paypalDeviceData: String?,
        completion: @escaping (Result<CCCPPaymentResponse>) -> Void
    ) throws
    func startPaypalVault(completion: @escaping (_ nonce: String?, _ paypalDeviceData: String?, _ error: Error?) -> Void)
    func checkBasketStatus(transactionID: String, completion: @escaping (Result<BookingConfirmation>) -> Void)
    func updateStoredAddress(with addressLine: AddressLineType)
    func didPop()
    func failedPayment()
    func trackPriceBreakdownTapAnalytics()
    func trackContinueButtonAnalytics()
    func setCccCardType(_ cardType: String?)
    func checkPIBAPaymentMethodExists(paymentMethods: [PaymentOption]?)
    func handleBackgroundChargeIfRequired(completion: @escaping (Bool) -> Void)
}

protocol ReviewAndPayViewEventHandler {
    func viewIsReady()
    func updateSelectedPaymentMethod(paymentViewModel: PaymentMethodViewModelType)
    func handlePayButtonTap()
    func setupThreeCIpage(for response: CCCPPaymentResponse)
    func showCountriesView(indexPath: IndexPath)
    func showPostcodePicker(with postCode: String?)
    func updateAddress(with addressLine: AddressLineType)
    func showBillingAddressField(_ show: Bool)
    func didPop()
    func trackPriceBreakdownTapAnalytics()
}

protocol CiolReviewAndPayViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }

    func showLoadingIndicator()
    func hideLoadingIndicator()
    func loadPriceBreakdown(with priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol)
    func reloadData(with viewModel: CiolReviewAndPayViewModel)
    func showError(title: String, message: String?, shouldDie: Bool)
    func showError(title: String, message: String?, action: @escaping (() -> Void))
    func startDisplayingActionLoadingElements()
    func stopDisplayingActionLoadingElements()
}

struct StoredAddressModel {
    var line1: String?
    var line2: String?
    var line3: String?
    var line4: String?
    var line5: String?
    var country: Country?
    var postcode: String?
    var companyName: String?
    var city: String?
    var type: AddressType = .home

    init(with address: Address? = nil) {
        self.line1 = address?.line1
        self.line2 = address?.line2
        self.line3 = address?.line3
        self.line4 = address?.line4
        self.line5 = address?.line5
        self.country = address?.country
        self.city = address?.cityName ?? address?.line4
        self.postcode = address?.postcode
        self.companyName = address?.companyName
    }

    var address: Address? {
        var dictionary = PIDictionary()
        dictionary["line1"] = line1
        dictionary["line2"] = line2
        dictionary["line3"] = line3
        dictionary["line4"] = line4
        dictionary["line5"] = line5
        dictionary["country"] = country
        dictionary["cityName"] = city
        dictionary["postcode"] = postcode
        dictionary["companyName"] = companyName
        return try? Address(dictionary: dictionary)
    }
}
