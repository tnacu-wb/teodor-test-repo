//
//  CiolInformationRouter.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 10.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

class CiolInformationRouter: CiolInformationRouterProtocol {
    weak var viewController: UIViewController?

    func close() {
        viewController?.dismiss(animated: true, completion: nil)
    }
}
