//
//  FullScreenImageViewerRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 12/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class FullScreenImageViewerRouter {
    private(set) weak var delegate: FullScreenImageViewerRouterDelegate?

    weak var viewController: UIViewController?

    init(with delegate: FullScreenImageViewerRouterDelegate?) {
        self.delegate = delegate
    }
}

extension FullScreenImageViewerRouter: FullScreenImageViewerRouterProtocol {
    func fullscreenImageSetCloseButtonDidTap() {
        guard let viewController = viewController else { return }
        delegate?.fullscreenImageSetCloseButtonDidTap(viewController: viewController)
    }

    func fullscreenImageSetDidChangePicture(atIndex index: Int) {
        delegate?.fullscreenImageSetDidChangePicture(atIndex: index)
    }
}
