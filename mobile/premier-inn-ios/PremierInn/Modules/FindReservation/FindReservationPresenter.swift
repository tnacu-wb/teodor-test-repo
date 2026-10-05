//
//  FindReservationPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import Foundation

protocol FindReservationPresenterProtocol: AnyObject {
    func viewIsReady()
    func calendarButtonDidTap(date: Date)
    func submitButtonDidTap()
    func calendarDidSelect(date: Date)
}

final class FindReservationPresenter {
    weak var view: FindReservationViewProtocol?
    var router: FindReservationRouterProtocol?
    var interactor: FindReservationInteractorProtocol?
}

extension FindReservationPresenter: FindReservationPresenterProtocol {
    func viewIsReady() {
        view?.loadViewModel(
            shouldShowError: false,
            arrivalDate: interactor?.arrivalDate,
            reservationNumber: interactor?.reservationNumber,
            lastName: interactor?.lastName
        )
    }

    func calendarButtonDidTap(date: Date) {
        guard let startDate = Date().dateByAddingUnit(unitType: .month, number: -6) else { return }

        router?.showCalendarPicker(withSelectedDate: date, startDate: startDate)
    }

    func calendarDidSelect(date: Date) {
        view?.setCheckInDate(date: date)
    }

    func submitButtonDidTap() {
        view?.stopEditing()
        view?.disableSubmitButton()

        do {
            guard let values = try view?.validateForm() else { throw ReservationDetailsError.missingFormValues }

            try interactor?.submitFormData(values: values) { [weak self] result in
                guard let self else { return }

                switch result {
                case .success(let reservation):

                    let stay = interactor?.searchForLocalStayUsingReservation(reservation)
                    router?.navigateToBookingConfirmation(stay: stay)

                case .failure(let error):

                    view?.enableSubmitButton()
                    interactor?.trackError(error)

                    if BARTDowntimeHandler.canHandle(error: error) {
                        router?.showBartError()
                    } else {
                        view?.loadViewModel(shouldShowError: true, arrivalDate: nil, reservationNumber: nil, lastName: nil)
                    }
                }
            }
        } catch let error as RowValidatorError {
            view?.enableSubmitButton()
            view?.showRowError(error)
        } catch {
            view?.enableSubmitButton()
            view?.showError(
                title: PILocalizedString(
                    "findReservationLoadingErrorTitle",
                    comment: "Find reservation loading error title"
                ),
                message: error.localizedDescription
            )
        }
    }
}
