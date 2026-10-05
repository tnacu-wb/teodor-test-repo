//
//  CiolUpsellPresenter.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

protocol CiolUpsellInteractorOutputProtocol: AnyObject, RegCardOutput {
    func reloadData(model: CiolUpsellViewModelProtocol)
    func goToPayment(inputParams: CiolReviewAndPayInputParams)
    func goToCompletion(error: Error?, ciolConfirmationDetails: CiolConfirmationDetails)
    func goDirectlyToCompletion(error: Error?, ciolConfirmationDetails: CiolConfirmationDetails)
    func startLoadingUI()
    @MainActor
    func stopLoadingUI(error: Error?)
    @MainActor
    func setupThreeCIpage(for response: CCCPPaymentResponse)
}

class CiolUpsellPresenter: CiolUpsellViewEventHandler {
    weak var view: CiolUpsellViewProtocol?
    var interactor: CiolUpsellInteractorProtocol?
    var router: CiolUpsellRouterProtocol?
    private var pendingConfirmationDetails: CiolConfirmationDetails?

    func getCellSetup(id: String, isSelected: Bool) -> CiolUpsellCellSetup? {
        interactor?.getCellSetup(id: id, isSelected: isSelected)
    }

    func handleContinueButtonTap() {
        self.view?.showLoadingIndicator()
        guard let interactor else {
            self.view?.hideLoadingIndicator()
            return
        }
        interactor.goToNextStep()
    }

    func handleUpsellsRowTapped(_ upsell: CiolUpsellItemViewModelProtocol) {
        guard let interactor else { return }
        if upsell.isMultiRoom, !upsell.addToAllRooms {
            router?.showUpsellRooms(
                RoomsUpsellInputParams(
                    upselltem: upsell,
                    rooms: interactor.upsellOutput.rooms,
                    priceBreakdownViewModel: interactor.ciolUpsellViewModel
                                                           .priceBreakdownViewModel,
                    nights: interactor.ciolUpsellViewModel.nights ?? 0,
                    analyticsParams: interactor
                                                           .customAnalyticsParameters ?? PIDictionary()
                ),
                roomsOutputDelegate: interactor
            )
        } else {
            let viewModel = interactor.ciolUpsellViewModel
            guard let room = viewModel.rooms.first else { return }

            var upsellToDisplay: CiolUpsellItemViewModelProtocol
            if let existingUpsell = interactor.upsell(from: room.id, upsellID: upsell.id) {
                upsellToDisplay = existingUpsell
            } else {
                upsellToDisplay = upsell
                upsellToDisplay.room = room
            }

            let detailsInput = CiolUpsellDetailsInputParams(
                upsell: upsellToDisplay,
                nights: viewModel.nights,
                addedFoodItems: interactor.addedFoodItems(
                    for: room.id,
                    withoutUpsellID: upsell.id
                ) ?? 0,
                addedKidsItems: interactor.addedKidsItems(
                    for: room.id,
                    withoutUpsellID: upsell.id
                ) ?? 0,
                prebookedItems: interactor.prebookedItems(
                    for: upsellToDisplay.id,
                    roomID: room.id
                ),
                bookingReference: interactor.bookingReference,
                analyticsParams: interactor
                                                            .customAnalyticsParameters ?? PIDictionary()
            )
            router?.showUpsellDetails(input: detailsInput, outputDelegate: interactor)
        }
    }

    func viewIsReady() {
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters
        reloadViewModel()
    }

    func reloadViewModel() {
        guard let viewModel = interactor?.ciolUpsellViewModel else { return }
        view?.reloadData(viewModel: viewModel)
    }

    func trackPriceBreakdownTapAnalytics() {
        interactor?.trackPriceBreakdownTapAnalytics()
    }
}

extension CiolUpsellPresenter: CiolUpsellInteractorOutputProtocol {
    func goToPayment(inputParams: CiolReviewAndPayInputParams) {
        view?.hideLoadingIndicator()
        guard let interactor else { return }
        router?.goToPayment(inputParams: inputParams, failedPaymentDelegate: interactor)
    }

    func goToCompletion(error: (any Error)?, ciolConfirmationDetails: CiolConfirmationDetails) {
        self.view?.hideLoadingIndicator()
        guard let error = error as? CIOLError else {
            handleSuccessfulCompletion(ciolConfirmationDetails: ciolConfirmationDetails)
            return
        }
        self.view?.showError(title: error.title, message: error.body, shouldDie: false)
    }

    private func handleSuccessfulCompletion(ciolConfirmationDetails: CiolConfirmationDetails) {
        // PIBA CNP bookings need the confirmation pop-up
        // All other flows (including RegCard) skip it
        let isPIBACNP = ciolConfirmationDetails.stay.paymentOption == .pibaCardNotPresent

        if isPIBACNP {
            showPaymentConfirmationPopup(ciolConfirmationDetails: ciolConfirmationDetails)
        } else {
            // For RegCard and other flows, skip the pop-up
            router?.goToCompletion(ciolConfirmationDetails: ciolConfirmationDetails)
        }
    }

    private func showPaymentConfirmationPopup(ciolConfirmationDetails: CiolConfirmationDetails) {
        guard let viewController = view as? UIViewController else {
            return
        }

        pendingConfirmationDetails = ciolConfirmationDetails

        let informationImage = CiolInformationImage(type: .named(UIImage(named: "QRCodePILogo")))
        let model = CiolInformationModel(
            image: informationImage,
            title: PILocalizedString("ciolPaymentConfirmationTitle"),
            subtitle: PILocalizedString("ciolPaymentConfirmationSubtitle"),
            showSubtitle: true,
            description: .init(type: .string(PILocalizedString("ciolPaymentConfirmationMessage"))),
            showCTA: true,
            ctaTitle: PILocalizedString("ciolConfirmCheckInButtonTitle"),
            delegate: self
        )

        let ciolBottomSheetAnalyticsInfo = CiolBottomSheetAnalyticsInfo(
            screenNameForViewUnderneath: PIAnalytics.StateNames.ciolUpsells
        )
        displayCIOLInformation(
            model: model,
            ciolInformationType: .paymentConfirmation,
            ciolBottomSheetAnalyticsInfo: ciolBottomSheetAnalyticsInfo,
            view: viewController,
            completion: nil
        )
    }

    func goDirectlyToCompletion(error: (any Error)?, ciolConfirmationDetails: CiolConfirmationDetails) {
        self.view?.hideLoadingIndicator()
        guard let error = error as? CIOLError else {
            // Skip pop-up and go directly to completion screen
            router?.goToCompletion(ciolConfirmationDetails: ciolConfirmationDetails)
            return
        }
        self.view?.showError(title: error.title, message: error.body, shouldDie: false)
    }

    func reloadData(model: any CiolUpsellViewModelProtocol) {
        view?.reloadData(viewModel: model)
    }

    func startLoading() {
        startLoadingUI()
    }

    func startLoadingUI() {
        self.view?.showLoadingIndicator()
    }

    @MainActor
    func stopLoadingUI(error: Error?) {
        view?.hideLoadingIndicator()
        guard let error = error as? CIOLError else { return }
        self.view?.showError(title: error.title, message: error.body, shouldDie: false)
    }

    @MainActor
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
}

extension CiolUpsellPresenter: ThreeCiPageDelegate {
    func startPolling(sender: ThreeCiPageViewController?, transactionID: String) {
        sender?.dismiss()
    }

    func finishedAuth(cardType: String?) {}
}

extension CiolUpsellPresenter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true) {
            Task { @MainActor in
                self.stopLoadingUI(error: nil)
            }
        }
    }
}

extension CiolUpsellPresenter: CanDisplayCIOLInformation {}

extension CiolUpsellPresenter: CiolInformationDelegate {
    func ctaAction(ciolInformationType: CiolInformationType) {
        switch ciolInformationType {
        case .paymentConfirmation:
            guard let confirmationDetails = pendingConfirmationDetails else { return }

            view?.showLoadingIndicator()

            // Call confirmPreCheckIn API to update basketStatus to PRE_CHECKED_IN
            interactor?
                .confirmPreCheckIn(basketReference: confirmationDetails.stay
                .reservationIdentifier ?? "") { [weak self] error in
                guard let self else { return }

                view?.hideLoadingIndicator()

                if let error = error as? CIOLError {
                    view?.showError(title: error.title, message: error.body, shouldDie: false)
                    return
                }

                router?.goToCompletion(ciolConfirmationDetails: confirmationDetails)
                pendingConfirmationDetails = nil
            }
        default:
            break
        }
    }
}
