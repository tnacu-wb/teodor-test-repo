//
//  AdditionalGuestFormPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork

class AdditionalGuestFormPresenter {
    weak var view: AdditionalGuestFormViewProtocol?

    var interactor: AdditionalGuestFormInteractorProtocol?
    var router: AdditionalGuestFormRouterProtocol?
}

extension AdditionalGuestFormPresenter: AdditionalGuestFormInteractorDelegate {
    func savedGuest(withMessage message: String) {
        router?.closeView()
    }

    func savedGuestFailed(withError error: Error) {
        view?.updateViewBusy(busy: false)
        view?.showErrorMessage(title: PILocalizedString("Something went wrong"), error: error)
    }
}

extension AdditionalGuestFormPresenter: AdditionalGuestFormViewEventHandler {
    var additionalGuestFormTracking: AdditionalGuestFormTracking {
        interactor?.additionalGuestFormTracking ?? ("", "")
    }

    func viewIsReady() {
        guard let interactor = interactor else { return }

        view?.set(title: interactor.viewContent.title)
        view?.loadViewModel(user: interactor.guest, viewContent: interactor.viewContent)
    }

    func save(user: AdditionalGuest?) {
        guard let user = user else { return }

        view?.updateViewBusy(busy: true)
        interactor?.save(guest: user)
    }

    func cancel() {
        router?.closeView()
    }

    func submitForm() {
        do {
            let additionalGuest = try view?.validatedUser()
            save(user: additionalGuest)
        } catch let error as RowValidatorError {
            view?.showErrorFor(row: error.row)
        } catch {
            view?.showErrorMessage(
                title: PILocalizedString(
                    "userDetailsGenericErrorTitle",
                    comment: "User details form, generic submission error title"
                ),
                error: error
            )
        }
    }

    func salutationRowDidTap() {
        router?.selectedSalutation { [weak self] salutation in
            guard let salutation = salutation else { return }
            self?.view?.selected(salutation: salutation)
        }
    }

    func countryRowDidTap() {
        router?.selectedCountry { [weak self] country in
            guard let country = country else { return }
            self?.view?.selected(nationality: country)
        }
    }
}
