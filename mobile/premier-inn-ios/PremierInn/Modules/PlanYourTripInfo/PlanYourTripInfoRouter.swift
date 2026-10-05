//
//  PlanYourTripInfoRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class PlanYourTripInfoRouter {
    weak var delegate: PlanYourTripInfoRouterDelegate?
}

extension PlanYourTripInfoRouter: PlanYourTripInfoRouterProtocol {
    func openDirections(withSender sender: UIView) {
        delegate?.openDirections(withSender: sender)
    }
}
