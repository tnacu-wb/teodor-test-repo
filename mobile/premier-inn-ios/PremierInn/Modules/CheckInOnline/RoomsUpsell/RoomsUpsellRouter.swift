//
//  RoomsUpsellRouter.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 14.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class RoomsUpsellRouter: RoomsUpsellRouterProtocol {
    weak var viewController: UIViewController?

    func popController() {
        guard let viewController = self.viewController else { return }

        viewController.navigationController?.popViewController(animated: true)
    }

    func showUpsellDetails(input: CiolUpsellDetailsInputParams, outputDelegate: CiolUpsellDetailsOutputDelegate) {
        let controller = CiolUpsellDetailsModule.build(
            inputParams: input,
            outputDelegate: outputDelegate
        )
        viewController?.present(controller, animated: true)
    }
}
