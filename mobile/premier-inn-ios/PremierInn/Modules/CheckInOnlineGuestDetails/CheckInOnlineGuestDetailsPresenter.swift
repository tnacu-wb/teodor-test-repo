//
//  CheckInOnlineGuestDetailsPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 18/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

class CheckInOnlineGuestDetailsPresenter {
    var view: CheckInOnlineGuestDetailsViewProtocol?
    var interactor: CheckInOnlineGuestDetailsInteractorProtocol?
    var router: CheckInOnlineGuestDetailsRouterProtocol?

    private func updateViewModel() {
        guard let interactor = interactor else { return }

        view?.update(with: interactor.viewModel)
    }

    func userSelected(country: Country) {
        guard let interactor = interactor else { return }

        interactor.update(leadGuest: country)
        view?.update(nationalityWith: interactor.viewModel)
    }

    func userSelected(title: String) {
        guard let interactor = interactor else { return }

        interactor.update(leadGuest: title)
        view?.update(title: title)
    }
}

extension CheckInOnlineGuestDetailsPresenter: CheckInOnlineGuestDetailsViewEventHandler {
    func viewIsReady() {
        guard let interactor = interactor else { return }

        view?.update(with: interactor.screenTitle)
        updateViewModel()
    }

    func cancelDidTap() {
        router?.cancelButtonTapped()
    }

    func guestDetailsDidComplete(with output: CheckInOnlineGuestDetailsViewOutput) {
        guard let interactor = interactor else { return }

        interactor.update(leadGuestWith: output)

        guard let leadGuestAndIndex = interactor.leadGuestAndRoomIndex else { return }

        router?.updated(leadGuest: leadGuestAndIndex.leadGuest, at: leadGuestAndIndex.roomIndex)
    }

    func nationalityRowDidTap() {
        router?.showCountriesList()
    }

    func titleRowDidTap() {
        router?.showTitlesList()
    }
}
