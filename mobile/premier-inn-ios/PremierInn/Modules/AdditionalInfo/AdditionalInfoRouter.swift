//
//  AdditionalInfoRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class AdditionalInfoRouter {
    weak var viewController: UIViewController?
}

extension AdditionalInfoRouter: AdditionalInfoRouterProtocol {
    func closeButtonDidTap() {
        viewController?.dismiss(animated: true)
    }
}
