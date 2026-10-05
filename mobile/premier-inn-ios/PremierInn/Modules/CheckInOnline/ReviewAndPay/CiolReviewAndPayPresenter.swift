//
//  CiolReviewAndPayPresenter.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol CiolReviewAndPayOutput: AnyObject {
    func didFailRegCard(error: CIOLError)
}

final class CiolReviewAndPayPresenter {
    weak var view: CiolReviewAndPayViewProtocol?
    var pollingController: PollingController?
    var interactor: CiolReviewAndPayInteractorProtocol?
    var router: CiolReviewAndPayRouterProtocol?
}

extension CiolReviewAndPayPresenter: ReviewAndPayViewEventHandler {
    func viewIsReady() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters
        view?.loadPriceBreakdown(with: viewModel.priceBreakdownViewModel)

        view?.showLoadingIndicator()
        loadPaymentMethods()
    }

    func didPop() {
        interactor?.didPop()
    }

    func updateSelectedPaymentMethod(paymentViewModel: PaymentMethodViewModelType) {
        interactor?.updateSelectedPaymentMethod(paymentViewModel: paymentViewModel)
        guard let viewModel = interactor?.viewModel else { return }

        view?.reloadData(with: viewModel)
    }

    func handlePayButtonTap() {
        view?.startDisplayingActionLoadingElements()
        interactor?.trackContinueButtonAnalytics()
        interactor?.handleBackgroundChargeIfRequired { [weak self] isSuccess in
            guard isSuccess else {
                self?.view?.showError(
                    title: PILocalizedString("ciolPayAndCheck-in"),
                    message: PILocalizedString("bookingGenericError"),
                    shouldDie: false
                )
                self?.view?.stopDisplayingActionLoadingElements()
                return
            }

            let paymentMethod = self?.interactor?.bookingDetails.primaryPaymentMethod?.paymentMethodType
            switch paymentMethod {
            case .paypal:
                self?.paypalVaultFlow()
            default:
                self?.cccPaymentFlow()
            }
        }
    }

    private func loadPaymentMethods() {
       interactor?.getPaymentMethods { [weak self] result in
            switch result {
            case .success(let response):
                self?.interactor?.checkPIBAPaymentMethodExists(
                    paymentMethods: response.paymentMethods
                )
            case .failure(let error):
                self?.view?.showError(
                    title: PILocalizedString("Something went wrong"),
                    message: error.localizedDescription,
                    shouldDie: true
                )
            }

           self?.refreshView()
        }
    }

   func cccPaymentFlow(with paypalNonce: String? = nil, paypalDeviceData: String? = nil) {
        do {
            try self.interactor?
                .startCccPayment(with: paypalNonce, paypalDeviceData: paypalDeviceData) { [weak self] result in
                guard let self = self else { return }

                switch result {
                case .success(let paymentResponse):
                    pollingController = PollingController(delegate: self)
                    // for flows that skip the iframe e.g PayPal
                    if paymentResponse.status == .notRequired {
                        startPolling(sender: nil, transactionID: "")
                        return
                    }
                    setupThreeCIpage(for: paymentResponse)
                case .failure(let error):
                    handlePaymentFailure(error: error)
                }
            }
        } catch {
            view?.stopDisplayingActionLoadingElements()
            view?.showError(
                title: PILocalizedString("paymentProcessingError"),
                message: error.localizedDescription,
                shouldDie: false
            )
        }
    }

    private func handlePaymentFailure(error: Error) {
        view?.stopDisplayingActionLoadingElements()
        handleCCCPaymentFailure(with: error)
    }

    func paypalVaultFlow() {
        interactor?.startPaypalVault { [weak self] nonce, paypalDeviceData, error in
            if let paypalNonce = nonce {
                self?.cccPaymentFlow(with: paypalNonce, paypalDeviceData: paypalDeviceData)
            } else {
                self?.view?.stopDisplayingActionLoadingElements()
                // If the user has cancelled the paypal journey themselves, then do not show an error pop up
                guard (error as? NSError)?.code != Constants.paypalUserCancelledErrorCode else { return }
                // In any instance of a paypal error we want to just show the generic text
                self?.view?.showError(
                    title: PILocalizedString("ciolPayAndCheck-in"),
                    message: PayPalError.genericError.localizedDescription,
                    shouldDie: false
                )
            }
        }
    }

    func setupThreeCIpage(for response: CCCPPaymentResponse) {
        do {
            let threeCiPageParams = try threeCIpageParams(for: response)
            router?.processPayment(
                with: threeCiPageParams,
                using: self,
                webDelegate: self,
                and: interactor?.paymentViewLayout ?? .general
            )
        } catch {
            view?.stopDisplayingActionLoadingElements()
            view?.showError(
                title: PILocalizedString("paymentProcessingError"),
                message: error.localizedDescription,
                shouldDie: false
            )
        }
    }

    private func threeCIpageParams(for response: CCCPPaymentResponse) throws -> ThreeCiPageParams {
        guard let htmlString = response.paymentRequiredDetails?.htmlString else { throw CCCPaymentError.missingProviderUrl }
        guard BookingDetails.sharedInstance.primaryPaymentMethod != nil else { throw CCCPaymentError.noPaymentMethod }

        var trackingParams = PIDictionary()
        trackingParams[PIAnalytics.Keys.checkInOnline] = true
        trackingParams[PIAnalytics.Keys.checkInOnlineBookingID] = interactor?.bookingDetails.operaBookingReference

        return ThreeCiPageParams(
            html: htmlString,
            trackingParams: trackingParams,
            allowedEvents: nil
        )
    }

    func updateAddress(with addressLine: AddressLineType) {
        interactor?.updateStoredAddress(with: addressLine)
    }

    func trackPriceBreakdownTapAnalytics() {
        interactor?.trackPriceBreakdownTapAnalytics()
    }

    private func refreshView() {
        DispatchQueue.main.async { [weak self] in
            guard let viewModel = self?.interactor?.viewModel else {
                return
            }
            self?.view?.reloadData(with: viewModel)
            self?.view?.hideLoadingIndicator()
        }
    }
}

extension CiolReviewAndPayPresenter: ThreeCiPageDelegate {
    func startPolling(sender: ThreeCiPageViewController?, transactionID: String) {
        sender?.dismiss()
        pollingController?.startPollingForBasketComplete(transactionID: transactionID)
    }

    func finishedAuth(cardType: String?) {
        interactor?.setCccCardType(cardType)
    }
}

extension CiolReviewAndPayPresenter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        interactor?.failedPayment()
        sender.dismiss(animated: true) {
            self.view?.stopDisplayingActionLoadingElements()
        }
    }
}

extension CiolReviewAndPayPresenter: PollingDelegate {
    func startConfirmationPolling() {
        view?.startDisplayingActionLoadingElements()
    }

    func stopConfirmationPolling() {
        view?.stopDisplayingActionLoadingElements()
    }

    func checkBasketStatus(
        transactionID: String,
        completion: @escaping (SimpleNetwork.Result<SimpleNetwork.BookingConfirmation>) -> Void
    ) {
        interactor?.checkBasketStatus(transactionID: transactionID, completion: completion)
    }

    func handleCCCPaymentFailure(with error: Error) {
        view?.stopDisplayingActionLoadingElements()
        interactor?.failedPayment()
        // Check if this is a PIBA CNP (Card Not Present) booking
        // PIBA CNP: Guest pays at front desk, no payment during check-in
        // Use specific error message for PIBA CNP, generic error for all other payment methods
         let errorMessage = interactor?.bookingDetails.isPIBACNP == true
             ? PILocalizedString("ciolPibaCnpCheckInErrorMessage")
             : PILocalizedString("ciolCheckErrorMessage")
        view?.showError(
            title: PILocalizedString("somethingWentWrongMessage", comment: "Payment failure: card error title"),
            message: errorMessage,
            shouldDie: false
        )
    }

    func handleBookingResult(confirmation: BookingConfirmation, confirmationPollingFinished: Bool) {
        guard let confirmationDetails = interactor?.viewModel.confirmationDetails else { return }
        router?.navigateToConfirmationScreen(ciolConfirmationDetails: confirmationDetails)
    }

    func showCountriesView(indexPath: IndexPath) {
        router?.showCountriesView(indexPath: indexPath, completion: { [weak self] countryItem in
            guard let self else { return }

            interactor?.storedAddressModel.country = countryItem.country
            interactor?.storedAddressModel.postcode = nil
            interactor?.storedAddressModel.line1 = nil
            interactor?.storedAddressModel.line2 = nil
            interactor?.storedAddressModel.line3 = nil
            interactor?.storedAddressModel.line4 = nil

            guard let viewModel = interactor?.viewModel else { return }
            view?.reloadData(with: viewModel)
        })
    }

    func showPostcodePicker(with postCode: String?) {
        let isBusinessBooker = UserSessionManager.sharedInstance.currentUser?.isBusiness ?? false
        let addressType: AddressType = isBusinessBooker ? .commercial : .home
        router?.showPostcodePicker(with: postCode, addressType: addressType, completion: { [weak self] address in
            self?.interactor?.storedAddressModel = address

            guard let viewModel = self?.interactor?.viewModel else { return }
            self?.view?.reloadData(with: viewModel)
        })
    }

    func showBillingAddressField(_ show: Bool) {
        interactor?.showBillingAddressFields = show

        guard let viewModel = self.interactor?.viewModel else { return }
        view?.reloadData(with: viewModel)
    }
}

extension CiolReviewAndPayPresenter: CiolReviewAndPayOutput {
    func didFailRegCard(error: CIOLError) {
        view?.showError(title: error.title, message: error.body, action: { [weak self] in
            self?.router?.navigateToStart()
        })
    }
}
