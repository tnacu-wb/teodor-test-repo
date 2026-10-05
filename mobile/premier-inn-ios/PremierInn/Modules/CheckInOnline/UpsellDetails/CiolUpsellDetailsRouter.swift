//
//  CiolUpsellDetailsRouter.swift
//  PremierInn
//
//  Created by Oltean Vasile Bogdan on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CiolUpsellDetailsRouter: CiolUpsellDetailsRouterProtocol {
    weak var viewController: UIViewController?

    func showMenuOrAllergyInfo(url: String) {
        guard let url = URL(string: url) else { return }

        viewController?.openURLInSafari(url: url)
    }
}
