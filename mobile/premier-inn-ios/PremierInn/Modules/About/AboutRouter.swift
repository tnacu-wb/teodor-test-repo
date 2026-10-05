//
//  AboutRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SwiftUI

enum AboutRouter {
    static func build() -> UIViewController {
        let viewModel = AboutViewModel()
        let view = AboutView(viewModel: viewModel)
        let controller = UIHostingController(rootView: view)
        controller.title = viewModel.title

        return controller
    }
}
