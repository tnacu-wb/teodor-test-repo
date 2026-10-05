//
//  RoomTypeSelectRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

// MARK: Inaccessible Router

class RoomTypeSelectRouter {
    private var viewController: UIViewController?

    private weak var delegate: RoomTypeSelectRouterDelegate?

    private let shouldBePopped: Bool

    init(
        with viewController: UIViewController,
        and delegate: RoomTypeSelectRouterDelegate?,
        andShouldBePopped shouldBePopped: Bool
    ) {
        self.viewController = viewController
        self.delegate = delegate
        self.shouldBePopped = shouldBePopped
    }
}

extension RoomTypeSelectRouter: RoomTypeSelectRouterProtocol {
    func selected(roomType: SelectableRoomType) {
        if shouldBePopped {
            viewController?.navigationController?.popViewController(animated: true)
        }

        delegate?.roomTypeSelectViewControllerDidUpdate(roomType: roomType)
    }
}
