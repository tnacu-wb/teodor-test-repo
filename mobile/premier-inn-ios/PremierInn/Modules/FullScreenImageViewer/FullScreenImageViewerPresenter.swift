//
//  FullScreenImageViewerPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 12/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

class FullScreenImageViewerPresenter {
    weak var view: FullScreenImageViewerViewProtocol?

    var interactor: FullScreenImageViewerInteractorProtocol?
    var router: FullScreenImageViewerRouterProtocol?
}

extension FullScreenImageViewerPresenter: FullScreenImageViewerViewEventHandler {
    func viewIsReady() {
        interactor?.trackState()

        guard let viewModel = interactor?.viewModel else { return }
        view?.update(with: viewModel)
    }

    func closeButtonDidTap() {
        router?.fullscreenImageSetCloseButtonDidTap()
    }

    func imageSetDidChangePicture(atIndex index: Int) {
        router?.fullscreenImageSetDidChangePicture(atIndex: index)
    }
}
