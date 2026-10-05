//
//  AmendDatesPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 18/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

class AmendDatesPresenter {
    var router: AmendDatesRouterProtocol?
    var interactor: AmendDatesInteractorProtocol?
    weak var calendar: AmendDatesCalendarViewControllerProtocol?

    deinit {
        print("DEINIT: \(self)")
    }
}

extension AmendDatesPresenter: AlternateCalendarViewControllerDelegate {
    func calendarDidSelect(arrivalDate: Date, nights: Int) {
        guard let calendar = calendar else { return }
        guard let interactor = interactor else { return }

        calendar.showDoneButton(show: false, animated: true)
        calendar.toggle(is: true)

        interactor.changeDates(to: arrivalDate, nights: nights) { changeDatesResponse, error in
            calendar.toggle(is: false)

            if changeDatesResponse.available, let priceDifference = changeDatesResponse.priceDifference {
                let priceDifferenceString =
                    String("\(priceDifference.amount.doubleValue < 0 ? "" : "+")\(priceDifference.localizedValue)")

                let continueViewModel = AmendDatesConfirmViewModel(
                    localizedPriceDifference: priceDifferenceString,
                    numberOfNights: nights
                )

                AccessibilityManager.announce(String(
                    format: PILocalizedString("amendDatesAvailableAnnouncement"),
                    priceDifferenceString
                ))

                calendar.showContinueButton(with: continueViewModel)
            } else {
                AnalyticsManager.shared.track(
                    error: error ?? AmendDatesError.genericError,
                    name: PIAnalytics.Error.amendDatesChangeError
                )
                calendar.showError(with: error?.localizedDescription ?? PILocalizedString("somethingWentWrongMessage"))
            }
        }
    }

    func calendarDidChange(arrivalDate: Date, nights: Int) {
        guard let calendar = calendar else { return }
        guard let interactor = interactor else { return }

        calendar.hideContinueButton()
        calendar.hideError()

        if let priceDifference = interactor.currentPriceDifference, interactor.duplicateOfCurrentRange(
            date: arrivalDate,
            nights: nights
        ) {
            let priceDifferenceString =
                String("\(priceDifference.amount.doubleValue < 0 ? "" : "+")\(priceDifference.localizedValue)")

            let continueViewModel = AmendDatesConfirmViewModel(
                localizedPriceDifference: priceDifferenceString,
                numberOfNights: nights
            )

            calendar.showContinueButton(with: continueViewModel)
        } else if interactor.moveOnly && !interactor.duplicateOfExistingNights(nights: nights) {
            // if amend restrictions for nights and length is different then show error

            calendar.showError(with: String.localizedStringWithFormat(
                PILocalizedString("amendDatesOnlyMoveMessage"),
                interactor.existingNights
            ))
            calendar.showDoneButton(show: false, animated: true)
        } else if interactor.currentPriceDifference == nil && interactor.duplicateOfExisting(
            date: arrivalDate,
            nights: nights
        ) {
            // if it's the original dates and we haven't made a change

            calendar.showDoneButton(show: false, animated: true)
        } else {
            // here we end up when it's a different date range than the current
            calendar.showDoneButton(show: true, animated: true)
        }
    }

    func calendarDidInvalidate() {
        calendar?.hideContinueButton()
        calendar?.hideError()
        calendar?.showDoneButton(show: false, animated: true)
    }
}

extension AmendDatesPresenter: AlternateCalendarAmendDateConfirmViewDelegate {
    func continueButtonDidTap() {
        router?.finishedAmend()
    }
}
