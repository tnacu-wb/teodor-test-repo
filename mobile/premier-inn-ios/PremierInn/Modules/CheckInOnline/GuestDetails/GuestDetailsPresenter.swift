//
//  GuestDetailsPresenterBlueprint.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

class GuestDetailsPresenter: GuestDetailsPresenterBlueprint {
    weak var view: GuestDetailsViewBlueprint?
    var interactor: GuestDetailsInteractorBlueprint?
    var router: GuestDetailsRouterBlueprint?

    var customAnalyticsParameters: PIDictionary? {
        interactor?.defaultAnalytics
    }

    func handleContinueButtonTap() {
        interactor?.handleContinueButtonTap()
    }

    func viewIsReady() {
        update()
    }

    func edit(with index: Int) {
        guard let interactor,
              let editModel = interactor.getEditModel(for: index) else { return }
        router?.showEdit(with: editModel, delegate: interactor)
    }

    func update() {
        guard let guests = interactor?.guests, let priceVM = interactor?.priceVM else { return }
        view?.update(with: guests, priceVM: priceVM)
    }
}
