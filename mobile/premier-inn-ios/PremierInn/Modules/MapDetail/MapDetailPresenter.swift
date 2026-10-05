//
//  MapPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class MapDetailPresenter {
    weak var view: MapDetailViewProtocol?

    var interactor: MapDetailInteractorProtocol?
    var router: MapDetailRouterProtocol?
}

extension MapDetailPresenter: MapDetailViewEventHandler {
    func viewIsReady() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.update(with: viewModel)
    }
}

extension MapDetailPresenter: MapDetailInteractorDelegate {
    func finishedLoadingHotel() {
        guard interactor?.layout == .journeyPlanner else { return }
        guard let hotel = interactor?.hotel else { return }
        guard let viewModel = interactor?.viewModel else { return }

        view?.update(with: viewModel)
        router?.showOverlayController(with: hotel)
    }
}

extension MapDetailPresenter: MapDetailPresenterProtocol {
    func openDirections(withSender sender: UIView) {
        guard let hotel = interactor?.hotel else { return }
        router?.openDirections(with: hotel, withSender: sender)
    }
}
