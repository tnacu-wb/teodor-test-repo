//
//  RoomTypeSelectModule.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

enum RoomTypeSelectModule {
    static func build(
        with availableRoomTypes: [SelectableRoomType],
        selectedRoomType: SelectableRoomType,
        and delegate: RoomTypeSelectRouterDelegate
    ) -> UIViewController {
        let controller = RoomTypeSelectViewController()
        controller.eventHandler = {
            let router = RoomTypeSelectRouter(with: controller, and: delegate, andShouldBePopped: false)
            let interactor = RoomTypeSelectInteractor(with: availableRoomTypes, selectedRoomType: selectedRoomType)
            let presenter = RoomTypeSelectPresenter()

            presenter.router = router
            presenter.interactor = interactor
            presenter.view = controller

            return presenter
        }()

        return controller
    }

    static func buildAccessibleSelection(
        with availableRoomTypes: [SelectableRoomType],
        selectedRoomType: SelectableRoomType,
        and delegate: RoomTypeSelectRouterDelegate
    ) -> UIViewController {
        let controller = RoomTypeSelectViewController()
        controller.eventHandler = {
            let router = RoomTypeSelectRouter(with: controller, and: delegate, andShouldBePopped: false)
            let interactor = AccessibleRoomTypeSelectInteractor(with: availableRoomTypes, selectedRoomType: selectedRoomType)
            let presenter = RoomTypeSelectPresenter()

            presenter.router = router
            presenter.interactor = interactor
            presenter.view = controller

            return presenter
        }()

        return controller
    }
}

protocol RoomTypeSelectViewModel {
    var roomTypeOptions: [RoomTypeSelectOptionViewModel] { get }
}

protocol RoomTypeSelectOptionViewModel {
    var name: String { get }
    var description: String { get }
    var iconName: String { get }
    var enabled: Bool { get }
    var selected: Bool { get }
}

protocol RoomTypeSelectViewProtocol {
    func update(with roomTypeSelectViewModel: RoomTypeSelectViewModel)
}

protocol RoomTypeSelectViewEventHandler {
    func viewIsReady()
    func select(roomType: RoomTypeSelectOptionViewModel)
}

protocol RoomTypeSelectInteractorProtocol {
    var viewModel: RoomTypeSelectViewModel { get }
    var selectedRoomType: SelectableRoomType? { get }

    func select(roomType: RoomTypeSelectOptionViewModel)
}

protocol RoomTypeSelectPresenterProtocol {
}

protocol RoomTypeSelectRouterProtocol {
    func selected(roomType: SelectableRoomType)
}

protocol RoomTypeSelectRouterDelegate: AnyObject {
    func roomTypeSelectViewControllerDidUpdate(roomType: SelectableRoomType)
}
