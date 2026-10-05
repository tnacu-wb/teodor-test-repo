//
//  CiolConfirmationPresenter.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

class CiolConfirmationPresenter: CiolConfirmationPresenterProtocol {
    weak var view: CiolConfirmationViewProtocol?
    var interactor: CiolConfirmationInteractorProtocol?
    var router: CiolConfirmationRouterProtocol?

    func viewIsReady() {
        guard let interactor else { return }
        view?.customAnalyticsParameters = interactor.customAnalyticsParameters
        view?.displayConfirmation(details: interactor.ciolConfirmationDetails)
		interactor.callBookingConfirmation()
    }

    func navigateToMyBookings() {
        router?.navigateToMyBookings()
    }

    func showKeyInstructions() {
        guard let model = interactor?.roomKeyInstructionsModel else {
            view?.showError(
                title: PILocalizedString("Something went wrong"),
                message: PILocalizedString("kioskPassAppleWalletFailed")
            )
            return
        }
        router?.showRoomKeyInstructions(model: model)
    }

    func refreshStays() {
        NotificationCenter.default.post(name: .staysWillChange, object: nil)
        interactor?.refreshStays(completion: { _ in })
    }

    func trackCiolComplete() {
        guard let reference = interactor?.customAnalyticsParameters?[PIAnalytics.Keys.checkInOnlineBookingID] as? String,
              let hotelCode = interactor?.customAnalyticsParameters?[PIAnalytics.Keys.checkInOnlineHotelCode] as? String
        else { return }

        AppsFlyerManager.sharedInstance.trackCiolComplete(reference: reference, hotelCode: hotelCode)
    }
}
