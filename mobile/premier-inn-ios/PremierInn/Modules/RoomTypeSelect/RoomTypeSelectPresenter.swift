//
//  RoomTypeSelectPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

class RoomTypeSelectPresenter {
    var router: RoomTypeSelectRouterProtocol?
    var interactor: RoomTypeSelectInteractorProtocol?
    var view: RoomTypeSelectViewProtocol?
}

extension RoomTypeSelectPresenter: RoomTypeSelectViewEventHandler {
    func viewIsReady() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.update(with: viewModel)
    }

    func select(roomType: RoomTypeSelectOptionViewModel) {
        interactor?.select(roomType: roomType)

        guard let roomType = interactor?.selectedRoomType else { return }
        router?.selected(roomType: roomType)
    }
}
