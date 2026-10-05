//
//  ForceUpdatePresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 10/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

class ForceUpdatePresenter {
    weak var view: ForceUpdateViewProtocol?
    var interactor: ForceUpdateInteractorProtocol?
    var router: ForceUpdateRouterProtocol?
}

extension ForceUpdatePresenter: ForceUpdatePresenterProtocol {
    func viewIsReady() {
        guard let interactor = interactor else { return }
        guard let viewModel = interactor.viewModel else { return }

        view?.update(with: viewModel)
    }

    func updateButtonDidTap() {
        guard let url = interactor?.appStoreUrl else { return }
        router?.openUrl(url: url)
    }
}
