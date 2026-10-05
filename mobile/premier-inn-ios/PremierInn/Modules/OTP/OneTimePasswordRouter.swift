//
//  OneTimePasswordRouter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 08/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class OneTimePasswordRouter {
    weak var viewController: UIViewController?
}

extension OneTimePasswordRouter: OneTimePasswordRouterInput {
    func goBackToKeyScreen() {
        guard let keyView = viewController?.navigationController?.viewControllers
              .first(where: { $0 is KeyView }) as? KeyView else {
            viewController?.navigationController?.popToRootViewController(animated: true)
            return
        }
        viewController?.navigationController?.popToViewController(keyView, animated: true)
    }

    func presentGetKeyView(_ controller: UIViewController) {
        viewController?.present(controller, animated: true)
    }
}
