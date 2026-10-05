//
//  UpsellsPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 05/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

class UpsellsPresenter {
    // MARK: - Properties

    weak var view: UpsellsViewProtocol?

    var interactor: UpsellsInteractorProtocol?
    var router: UpsellsRouterProtocol?
    var reviewBookDelegate: UpsellsRouterDelegate?
    private let scope: UserDetailScope

    // MARK: - Init

    init(scope: UserDetailScope) {
        self.scope = scope
    }

    private func reloadViewModel() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.update(with: viewModel)
    }
}

extension UpsellsPresenter: UpsellsViewEventHandler {
    var promotionsAnalytics: PIDictionary? {
        interactor?.promotionsAnalytics
    }

    var analyticsScope: String {
        scope == .amendFlow ? PIAnalytics.StateTypes.myBookings : PIAnalytics.StateTypes.bookingFlow
    }

    var analyticsCustomParams: [String: Any]? {
        guard scope == .amendFlow else { return nil }
        guard let amendAnalytics = interactor?.amendAnalytics else { return nil }

        var dict: PIDictionary = [:]
        dict[PIAnalytics.Keys.amendExtrasShownDescriptions] = amendAnalytics.upsellsLegends
        dict[PIAnalytics.Keys.amendExtrasShownCodes] = amendAnalytics.upsellsCodes

        if let promotionsAnalytics {
            dict.mergePreferNew(promotionsAnalytics)
        }

        return dict
    }

    func viewIsReady() {
        if scope == .bookingFlowEditing || scope == .amendFlow {
            view?.showCancelButton()
        }

        reloadViewModel()
    }

    func toggledExtraUpsell(toggle: Bool, upsellIndex: Int, in roomIndex: Int) {
        interactor?.toggledExtraUpsell(toggle: toggle, upsellIndex: upsellIndex, in: roomIndex)
        reloadViewModel()
    }

    func selected(change roomIndex: Int) {
        interactor?.selected(change: roomIndex)
        reloadViewModel()
    }

    func selectedExtraUpsell(change roomIndex: Int) {
        interactor?.selectedExtraUpsell(change: roomIndex)
        reloadViewModel()
    }

    func cancelButtonDidTap() {
        interactor?.resetUsersMealSelection()
    }

    func numberOfAdultsDidChange(value: Int, at roomIndex: Int, mealCode: Int) {
        interactor?.selected(upsellAt: mealCode, in: roomIndex, numberOfSelection: value)
        reloadViewModel()
    }

    func continueButtonDidTap() {
        // Amend flow
        if scope == .amendFlow {
            view?.lockFullScreen(shouldLock: true)

            interactor?.amendPackages(completion: { response, error in
                self.view?.lockFullScreen(shouldLock: false)

                if let error = error {
                    self.view?.showErrorMessage(
                        title: PILocalizedString("somethingWentWrongAlertTitle"),
                        message: PILocalizedString("saveUpsellsErrorMessage"),
                        error: error,
                        handler: nil
                    )
                    return
                }

                if let success = response, success == true {
                    self.router?.continueToNextAmendStep()
                }
            })
        } else {
            DispatchQueue.main.async {
                self.view?.showSpinnerAndLockScreen()
            }
            DispatchGroupManager.sharedInstance.holdBookingDispatchGroup.notify(queue: .main) {
                self.interactor?.saveAncillaries { success, error in
                    if error != nil || success == false {
                        DispatchQueue.main.async {
                            if let reviewBookDelegate = self.reviewBookDelegate {
                                reviewBookDelegate.showErrorMessage(
                                    title: PILocalizedString("somethingWentWrongAlertTitle"),
                                    message: PILocalizedString("saveUpsellsErrorMessage"),
                                    handler: { _ in
                                        self.reloadViewModel()
                                        reviewBookDelegate.goBackToUpsellsScreen()
                                    }
                                )
                            } else {
                                self.view?.showErrorMessage(
                                    title: PILocalizedString("somethingWentWrongAlertTitle"),
                                    message: PILocalizedString("saveUpsellsErrorMessage"),
                                    error: error,
                                    handler: { _ in
                                        self.reloadViewModel()
                                        self.router?.goBackToUpsellsScreen()
                                    }
                                )
                            }
                        }
                        return
                    }
                }
                DispatchQueue.main.async {
                    self.view?.hideSpinnerAndUnLockScreen()
                }
                self.router?.continueToNextStep(with: self.interactor?.bookingDetails ?? BookingDetails.sharedInstance)
            }
        }
    }

    func summaryButtonDidTap() {
        router?.showSummary(with: self.interactor?.bookingDetails ?? BookingDetails.sharedInstance)
    }
}
