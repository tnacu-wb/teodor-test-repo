//
//  CiolConfirmationRouter.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei (Cognizant) on 21.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SwiftUI

class CheckOutConfirmationRouter: CheckOutConfirmationRouterProtocol {
    weak var viewController: UIViewController?

    func dismissView() {
        viewController?.navigationController?.popViewController(animated: true)
    }
}
