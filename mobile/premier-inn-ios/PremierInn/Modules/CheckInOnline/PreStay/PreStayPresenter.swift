//
//  PreStayPresenter.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import UIKit

final class PreStayPresenter {
    weak var view: PreStayViewProtocol?
    var interactor: PreStayInteractorProtocol?
    var router: PreStayRouterProtocol?
    private var pendingConfirmationDetails: CiolConfirmationDetails?
}

extension PreStayPresenter: PreStayViewEventHandler {
    func updateViewModel(with: EditDetailsModel) {
        interactor?.updateViewModel(with: with)
    }

    func viewIsReady() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters

        view?.showLoadingIndicator()
        interactor?.getSpecialOccasions { [weak self] _, error in
            self?.view?.hideLoadingIndicator()
            if let error = error as? CIOLError {
                self?.view?.showError(title: error.title, message: error.body, shouldDie: false)
            }
            self?.view?.reloadData(with: viewModel)
        }
    }

    func handleSelectOccasion() {
        interactor?.viewOccasions()
    }

    func isSpecialOccasionOn(_ isOn: Bool) {
        interactor?.updateSpecialOccasion(isOn)
    }

    func handleContinueButtonTap() {
        guard let interactor else { return }

        guard interactor.isAllGuestDataComplete() else {
            view?.reloadData(with: interactor.viewModel)
            return
        }

        view?.showLoadingIndicator()
        interactor.performCheckIn { [weak self] success, error in
            guard let self else { return }

            view?.hideLoadingIndicator()
            view?.reloadData(with: interactor.viewModel)

            if let error = error as? CIOLError, !success {
                view?.showError(
                    title: error.title,
                    message: error.body,
                    shouldDie: false
                )
            }

            if success {
                interactor.trackSubmissionIfThirdPartyBooking()
            }
        }
    }

    func showEditDetails(flow: EditDetailsFlow) {
        interactor?.goToEditDetails(flow: flow)
    }
}

extension PreStayPresenter: PreStayInteractorOutputProtocol {
    func preStayChecksCompleted(ciolUpsellInputParams: CiolUpsellInputParams) {
        guard let interactor else { return }
        router?.goToUpsell(ciolUpsellInputParams: ciolUpsellInputParams, prestayDelegate: interactor)
    }

    func editBookingDetails(inputParams: EditDetailsInputParams) {
        router?.goToEditDetails(inputParams: inputParams)
    }

    func reloadData(with viewModel: PreStayViewModel) {
        view?.reloadData(with: viewModel)
    }

    func goToPayment(with inputParams: CiolReviewAndPayInputParams) {
        router?.goToPayment(inputParams: inputParams)
    }

    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails) {
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
            screenNameForViewUnderneath: PIAnalytics.StateNames.ciolPreStay
        )
        displayCIOLInformation(
            model: model,
            ciolInformationType: .paymentConfirmation,
            ciolBottomSheetAnalyticsInfo: ciolBottomSheetAnalyticsInfo,
            view: viewController,
            completion: nil
        )
    }

    func goDirectlyToCompletion(ciolConfirmationDetails: CiolConfirmationDetails) {
        // Skip pop-up and go directly to completion screen
        router?.goToCompletion(ciolConfirmationDetails: ciolConfirmationDetails)
    }

    func goToGuestDetailsDERegCard(input: GuestDetailsInputBlueprint) {
        guard let interactor else { return }
        router?.goToRegCard(regCardInput: input, prestayDelegate: interactor)
    }

    func viewOccasions() {
        guard let viewModel = interactor?.viewModel else { return }
        guard let hotelPreferences = viewModel.hotelPreferences else { return }
        let specialOccasionsNames = hotelPreferences.compactMap { $0.name }

        router?.viewOccasions(hotelPreferences: specialOccasionsNames, completion: { [weak self] selectedOccasion in
            guard let self else { return }

            interactor?.updateSelectedPreference(with: selectedOccasion)
            guard let viewModel = interactor?.viewModel else { return }
            view?.reloadData(with: viewModel)
        })
    }

    func reloadPriceBreakdown(priceModel: CIOLPriceBreakdownViewModelProtocol) {
        view?.reloadPriceBreakdown(priceModel: priceModel)
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

extension PreStayPresenter: CanDisplayCIOLInformation {}

extension PreStayPresenter: CiolInformationDelegate {
    func ctaAction(ciolInformationType: CiolInformationType) {
        switch ciolInformationType {
        case .paymentConfirmation:
            guard let confirmationDetails = pendingConfirmationDetails else { return }

            view?.showLoadingIndicator()

            // Call confirmPreCheckIn API to update basketStatus to PRE_CHECKED_IN
            interactor?
                .confirmPreCheckIn(basketReference: confirmationDetails.stay
                .reservationIdentifier ?? "") { [weak self] success in
                guard let self else { return }

                view?.hideLoadingIndicator()

                guard success else {
                    view?.showError(
                        title: CIOLError.performPrestayChecks.title,
                        message: CIOLError.performPrestayChecks.body,
                        shouldDie: false
                    )
                    return
                }

                router?.goToCompletion(ciolConfirmationDetails: confirmationDetails)
                pendingConfirmationDetails = nil
            }
        case .cityTaxDisclaimer:
            view?.showLoadingIndicator()

            interactor?.resolvePreCheckinFlow { [weak self] success in
                self?.view?.hideLoadingIndicator()

                if !success {
                    self?.view?.showError(
                        title: CIOLError.performPrestayChecks.title,
                        message: CIOLError.performPrestayChecks.body,
                        shouldDie: false
                    )
                }
            }
        default:
            break
        }
    }
 }
