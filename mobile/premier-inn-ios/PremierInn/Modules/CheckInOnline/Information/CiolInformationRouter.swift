//
//  CiolInformationRouter.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 10.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CiolInformationRouter: CiolInformationRouterProtocol {
    weak var viewController: UIViewController?

    var completion: (() -> Void)?

    init(completion: (() -> Void)?) {
        self.completion = completion
    }

    func close() {
        viewController?.dismiss(animated: true, completion: nil)
    }
}
