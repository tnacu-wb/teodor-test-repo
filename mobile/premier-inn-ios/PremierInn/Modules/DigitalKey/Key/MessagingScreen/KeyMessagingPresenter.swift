//
//  MessagingPresenter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 22/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

class KeyMessagingPresenter: KeyMessagingPresenterProtocol {
    weak var view: KeyMessagingViewProtocol?
    var interactor: KeyMessagingInteractorInputProtocol?
    var router: KeyMessagingRouterProtocol?

    func viewDidLoad() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.updateView(with: viewModel)
        interactor?.trackStateAnalytics()
    }

    func mapButtonDidClick() {
        guard let stay = interactor?.stay else { return }
        view?.showMapDirections(directionsViewModel: DirectionsViewModel.create(from: stay))
    }
}

extension KeyMessagingPresenter: KeyMessagingInteractorOutputProtocol {
}
