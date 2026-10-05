//
//  AdditionalInfoPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

class AdditionalInfoPresenter {
    weak var view: AdditionalInfoViewProtocol?

    var interactor: AdditionalInfoInteractorProtocol?
    var router: AdditionalInfoRouterProtocol?
}

extension AdditionalInfoPresenter: AdditionalInfoViewEventHandler {
    func viewIsReady() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.update(with: viewModel)
    }

    func closeButtonDidTap() {
        router?.closeButtonDidTap()
    }
}
