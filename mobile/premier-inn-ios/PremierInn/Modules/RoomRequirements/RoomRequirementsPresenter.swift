//
//  RoomRequirementsPresenter.swift
//  PremierInn
//
//  Created by Nick Jones on 01/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

protocol RoomRequirementsPresenterInput {
    var roomRequirementsTracking: RoomRequirementsTracking { get }

    func viewIsReady()
    func saveChanges()
    func roomTypeDidSelect()
    func cotRequirementChanged(toRequired: Bool)
    func adultRequirementsChanged(to: Int)
    func childrenRequirementsChanged(to: Int)
}

class RoomRequirementsPresenter {
    weak var view: RoomRequirementsViewInput?
    var interactor: RoomRequirementsInteractorInput?
    var router: RoomRequirementsRouterInput?

    private func reloadView() {
        guard let model = interactor?.model else { return }

        view?.reload(with: model)
    }
}

extension RoomRequirementsPresenter: RoomRequirementsPresenterInput {
    var roomRequirementsTracking: RoomRequirementsTracking {
        interactor?.roomRequirementsTracking ?? ("", "")
    }

    func viewIsReady() {
        reloadView()
    }

    func saveChanges() {
        view?.toggleLoading(isLoading: true)
        interactor?.saveChanges(shouldAttemptLogin: true) { [weak self] result in
            switch result {
            case .success:
                let data: PIDictionary? = {
                    guard let interactor = self?.interactor else { return nil }

                    return [
                        PIAnalytics.Keys.adultPreference: interactor.currentNumberOfAdults,
                        PIAnalytics.Keys.children: interactor.currentNumberOfChildren,
                        PIAnalytics.Keys.cotPreference: interactor.cotCurrentlyRequired,
                        PIAnalytics.Keys.roomType: interactor.currentRoomType.rawValue
                    ]
                }()

                AnalyticsManager.shared.trackAction(PIAnalytics.Action.bookingPreferencesChanged, userInfo: data)

                self?.router?.removeModule()

            case .failure(let error):
                BARTDowntimeHandler.handle(
                    error: error,
                    withParentNavigationController: self?.view?.parentNavigationController
                )
                self?.router?.errorOccuredWhenUpdatingPreferences()
            }
        }
    }

    func roomTypeDidSelect() {
        guard let interactor = interactor else { return }

        router?.showRoomTypePicker(
            selectedType: interactor.currentRoomType,
            availableTypes: interactor.availableTypes,
            delegate: self
        )
    }

    func cotRequirementChanged(toRequired: Bool) {
        interactor?.cotCurrentlyRequired = toRequired

        reloadView()
    }

    func adultRequirementsChanged(to value: Int) {
        view?.provideSmallHapticFeedback()

        interactor?.currentNumberOfAdults = value

        reloadView()
    }

    func childrenRequirementsChanged(to value: Int) {
        view?.provideSmallHapticFeedback()

        interactor?.currentNumberOfChildren = value

        reloadView()
    }
}

extension RoomRequirementsPresenter: RoomTypeSelectRouterDelegate {
    func roomTypeSelectViewControllerDidUpdate(roomType: SelectableRoomType) {
        guard let roomType = roomType as? RoomType else { return }
        interactor?.currentRoomType = roomType

        reloadView()
    }
}
