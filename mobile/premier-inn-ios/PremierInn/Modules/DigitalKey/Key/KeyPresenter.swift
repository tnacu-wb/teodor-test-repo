//
//  KeyPresenter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import Foundation
import CoreLocation

class KeyPresenter: KeyPresenterProtocol {
    weak var view: KeyViewProtocol?
    var interactor: KeyInteractorInputProtocol?
    var router: KeyRouterProtocol?
	private let analytics: AnalyticsType
    private var isLoading: Bool = false

	init(analytics: AnalyticsType = AnalyticsManager.shared) {
		self.analytics = analytics
	}

    func viewIsReady() {
        guard let interactor else {
            self.goBackAlert()
			analytics.track(errorName: PIAnalytics.Error.dkInteractorMissing)
            return
        }

        // Need to move this into the function to update the view model so it changes when we refresh the view
        if interactor.disableBackButton == true {
            view?.disableBackNavigation()
        }

        // Trigger the notification permission check
        interactor.loadNotificationPermissionStatus()

        if isLoading == false {
            isLoading = true

            self.view?.showLoadingIndicator()
            interactor.setup { response in
                guard response == true else {
                    self.goBackAlert()
                    self.isLoading = false
                    return
                }
                self.view?.hideLoadingIndicator()
                self.updateViewModel(with: interactor.viewModel)
                self.isLoading = false
            }
        }
    }

    func addToAppleWalletDidTap() {
        interactor?.trackActionAnalytics(action: PIAnalytics.Action.addDigitalKeyDidTap)
		guard let stay = interactor?.stay,
		      let roomId = interactor?.roomId else {
			analytics.track(errorName: PIAnalytics.Error.dkAddToWalletButtonTapFailed)
			return
		}
        router?.openMFAScreen(stay: stay, roomId: roomId, keyAddedDelegate: self)
    }

    func viewInAppleWallet() {
		guard let walletIdentifier = interactor?.stay.digitalKeyIdentifier else {
			analytics.track(errorName: PIAnalytics.Error.dkAddToWalletButtonTapFailed)
			return
		}
        interactor?.trackActionAnalytics(action: PIAnalytics.Action.showKeyInWallet)
        router?.openPassInWallet(id: walletIdentifier)
        interactor?.updateCiolStatus()
    }

    func startRoomAllocation(completion: @escaping () -> Void) {
        // Check if arrival time has passed
		guard let interactor else {
			analytics.track(errorName: PIAnalytics.Error.dkInteractorMissing)
			completion()
            return
		}

        guard interactor.stay.checkInTimeHasPassed == true else {
            // we just want to refresh the view here
            updateViewModel(with: interactor.viewModel)

            completion()
            return
        }

        allocateAndCheckIn(completion: completion)
    }

    func instructionsDidTap(model: InstructionsViewModel) {
        router?.instructionsDidTap(model: model)
    }

    private func allocateAndCheckIn(completion: @escaping () -> Void) {
		guard let interactor else {
			analytics.track(errorName: PIAnalytics.Error.dkInteractorMissing)
			completion()
            return
		}

        interactor.allocateAndCheckInBooking { _ in
            self.updateViewModel(with: interactor.viewModel)
            return completion()
        }
    }

    func updateViewModel(with viewModel: KeyDetailsViewModel) {
        // Change the state when we update the view model
        interactor?.trackStateAnalytics()
        view?.updateView(with: viewModel)

        if viewModel.shouldGoToUsingYourKeyInstructions,
           let instructionsModel = interactor?.instructionsModel(for: .usingDigitalKey) {
            router?.instructionsDidTap(model: instructionsModel)
        }
    }

    func closeButtonClicked() {
        guard let stay = interactor?.stay else {
			analytics.track(errorName: PIAnalytics.Error.dkInteractorMissing)
			return
		}
        router?.closeButtonClicked(digitalKeyIdentifier: stay.digitalKeyIdentifier)
    }

    private func goBackAlert() {
        view?.showAlert(with: PILocalizedString("Something went wrong"), and: PILocalizedString("Please try again")) {
            self.router?.closeButtonClicked(digitalKeyIdentifier: nil)
        }
    }
}

extension KeyPresenter: KeyInteractorOutputProtocol {
}

extension KeyPresenter: KeyAddedProtocol {
    func didFinishAddingKey() {
        // automate the allocation process
        isLoading = true
        view?.showLoadingIndicator()

        startRoomAllocation {
            self.view?.hideLoadingIndicator()
            self.isLoading = false
        }
    }
}
