//
//  HotelDetailsDiscountCodeModule.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 22/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

enum HotelDetailsDiscountCodeModule {
    static func build(viewModel: HotelDetailsDiscountCodeViewModel) -> UIViewController {
        let view = HotelDetailsDiscountCodeView(viewModel: viewModel)
        let viewController = view.embedInHostingController()
        return viewController
    }
}
