//
//  PlanYourTripInfoPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class PlanYourTripInfoPresenter {
    weak var view: PlanYourTripInfoViewProtocol?

    var interactor: PlanYourTripInfoInteractorProtocol?
    var router: PlanYourTripInfoRouterProtocol?

    private func reloadView() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.update(with: viewModel)
    }
}

extension PlanYourTripInfoPresenter: PlanYourTripInfoViewEventHandler {
    func viewIsReady() {
        reloadView()
    }

    func openDirections(withSender sender: UIView) {
        router?.openDirections(withSender: sender)
    }
}
