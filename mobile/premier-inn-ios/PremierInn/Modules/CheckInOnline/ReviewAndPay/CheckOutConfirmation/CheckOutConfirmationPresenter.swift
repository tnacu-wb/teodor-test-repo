//
//  CiolConfirmationPresenter.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei (Cognizant) on 21.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

class CheckOutConfirmationPresenter: CheckOutConfirmationPresenterProtocol {
    var checkOutDetails: CheckOutDetails?
    var view: CheckOutConfirmationViewProtocol?
    var interactor: CheckOutConfirmationInteractorProtocol?
    var router: CheckOutConfirmationRouterProtocol?

    func viewIsReady() {
        guard let interactor else { return }
        view?.displayConfirmation(details: interactor.checkOutDetails)
    }

    func dismissView() {
        router?.dismissView()
    }
}
