//
//  AdditionalGuestsPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

class AdditionalGuestsPresenter {
    weak var view: AdditionalGuestsViewProtocol?
    var router: AdditionalGuestsRouterProtocol?
    var interactor: AdditionalGuestsInteractorProtocol?
}

extension AdditionalGuestsPresenter: AdditionalGuestsViewEventHandler {
    var additionalGuestsTracking: AdditionalGuestsTracking {
        interactor?.additionalGuestsTracking ?? ("", "")
    }

    func viewIsReady() {
        view?.setTitle(title: interactor?.viewTitle ?? "")
        view?.listViewModels = interactor?.guestViewModels
    }

    func selectedAddGuest() {
        router?.addAdditionalGuest()
    }

    func selectedEditGuest(atIndex index: Int) {
        guard let guest = interactor?.additionalGuests?[index] else { return }
        router?.edit(additionalGuest: guest, atIndex: index)
    }

    func selectedDeleteGuest(atIndex index: Int) {
        guard let deleteAlertContent = interactor?.deleteAlertContent else { return }

        view?.showOptionAlert(
            withTitle: deleteAlertContent.title,
            message: deleteAlertContent.message,
            cancelTitle: deleteAlertContent.cancel,
            confirmTitle: deleteAlertContent.confirm
        ) { [unowned self] in
            view?.updateViewBusy(busy: true)
            interactor?.deleteGuest(atIndex: index)
        }
    }
}

extension AdditionalGuestsPresenter: AdditionalGuestsInteractorDelegate {
    func guestDeleted(withMessage message: String) {
        view?.updateViewBusy(busy: false)
        viewIsReady()
    }

    func guestDeleteFailed(withError error: Error) {
        view?.updateViewBusy(busy: false)
        view?.showErrorMessage(title: PILocalizedString("Something went wrong"), error: error)
    }
}
