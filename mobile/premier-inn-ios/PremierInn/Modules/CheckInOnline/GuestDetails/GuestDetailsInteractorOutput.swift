//
//  File.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

extension GuestDetailsPresenter: GuestDetailsInteractorOutput {
    func stopLoadingUI(error: (any Error)?) {
        guard let error = error as? CIOLError else { return }
        didFinish(error: error)
    }

    @MainActor
    func startLoading() {
        didStart()
    }

    func setupThreeCIpage(for response: CCCPPaymentResponse) {
        guard let interactor else { return }
        do {
            let threeCiPageParams = try threeCIpageParams(for: response)
            router?.processPayment(
                with: threeCiPageParams,
                using: self,
                and: self,
                authorizationDelegate: interactor,
                webviewLayout: interactor.paymentViewLayout
            )
        } catch {
            stopLoadingUI(error: error)
        }
    }

    // move this to interactor
    private func threeCIpageParams(for response: CCCPPaymentResponse) throws -> ThreeCiPageParams {
        guard let htmlString = response.paymentRequiredDetails?.htmlString else { throw CCCPaymentError.missingProviderUrl }
        var trackingParams = PIDictionary()
        trackingParams[PIAnalytics.Keys.checkInOnline] = true
        trackingParams[PIAnalytics.Keys.checkInOnlineBookingID] = BookingDetails.sharedInstance.operaBookingReference

        return ThreeCiPageParams(
            html: htmlString,
            trackingParams: trackingParams,
            allowedEvents: nil
        )
    }

    func goToCompletion(error: (any Error)?, ciolConfirmationDetails: CiolConfirmationDetails) {
        if let error = error as? CIOLError {
            didFinish(error: error)
        } else {
            goToCompletion(ciolConfirmationDetails: ciolConfirmationDetails)
        }
    }

    func didFinish(error: CIOLError?) {
        view?.hideLoadingIndicator()
        if let error {
            view?.showError(title: error.title, message: error.body, shouldDie: false)
        }
    }

    func didStart() {
        view?.showLoadingIndicator()
    }

    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails) {
        router?.goToCompletion(ciolConfirmationDetails: ciolConfirmationDetails)
    }

    func goToPayment(with inputParams: CiolReviewAndPayInputParams) {
        router?.goToPayment(inputParams: inputParams)
    }

    func goToUpsells(upsellInput: CiolUpsellInputParams) {
        guard let prestayDelegate = interactor?.prestayDelegate else { return }
        router?.goToUpsell(ciolUpsellInputParams: upsellInput, prestayDelegate: prestayDelegate)
    }

    func reload() {
        update()
    }

    func reloadBalance() {
        guard let priceVM = interactor?.priceVM else { return }
        view?.updateBalance(priceVM: priceVM)
    }

    func showCityTaxDisclaimer() {
        let informationImage = CiolInformationImage(
            type: .named(UIImage(named: "QRCodePILogo"))
        )

        let viewModel = CiolInformationModel(
            image: informationImage,
            title: PILocalizedString("ciolCityTaxDisclaimerTitle"),
            subtitle: nil,
            showSubtitle: true,
            description: .init(type: .string(PILocalizedString("ciolCityTaxDisclaimerDescription"))),
            showCTA: true,
            ctaTitle: PILocalizedString("ciolCityTaxDisclaimerActionText"),
            delegate: self
        )

        displayCIOLInformation(
            model: viewModel,
            ciolInformationType: .cityTaxDisclaimer,
            ciolBottomSheetAnalyticsInfo: .init(screenNameForViewUnderneath: nil),
            view: view as? UIViewController,
            completion: nil
        )
    }
}

extension GuestDetailsPresenter: ThreeCiPageDelegate {
    func startPolling(sender: ThreeCiPageViewController?, transactionID: String) {
        sender?.dismiss()
    }

    func finishedAuth(cardType: String?) {}
}

extension GuestDetailsPresenter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true) {
            Task { @MainActor in
                self.stopLoadingUI(error: nil)
            }
        }
    }
}

extension GuestDetailsPresenter: CanDisplayCIOLInformation { }

extension GuestDetailsPresenter: CiolInformationDelegate {
    func ctaAction(ciolInformationType: CiolInformationType) {
        switch ciolInformationType {
        case .cityTaxDisclaimer:
            interactor?.validate()
        default:
            return
        }
    }
}
