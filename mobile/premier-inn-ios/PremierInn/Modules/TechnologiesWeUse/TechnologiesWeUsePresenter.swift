//
//  TechnologiesWeUsePresenter.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

class TechnologiesWeUsePresenter {
    weak var view: TechnologiesWeUseViewProtocol?
    var interactor: TechnologiesWeUseInteractorProtocol?
    var router: TechnologiesWeUseRouterProtocol?
}

extension TechnologiesWeUsePresenter: TechnologiesWeUseViewEventHandler {
    var userHasAccepted: Bool {
        interactor?.userHasAccepted ?? false
    }

    func viewHasLoaded() {
        view?.configureLeftNavigationBar(withTitle: interactor?.navigationBarTitle ?? "")
    }

    func viewHasFinishedLayout() {
        guard (interactor?.userHasAccepted ?? false) == false else { return }

        view?.performAdditionalLayoutSetup()
        view?.addFooter(withButtonTitle: interactor?.footerButtonTitle ?? "")
    }

    func viewIsAppearing(withAnimation animated: Bool) {
        view?.showNavigationBar(withAnimation: animated)
    }

    func userDidAccept() {
        router?.userAcceptedTermsAndConditions()
    }
}
