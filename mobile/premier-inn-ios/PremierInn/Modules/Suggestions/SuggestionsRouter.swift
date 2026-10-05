//
//  SuggestionsRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 27/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

enum SuggestionsRouter {
    static func build() -> SuggestionsViewController {
        let controller = SuggestionsViewController()
        controller.suggestionsPresenter = {
            let presenter = SuggestionsPresenter(suggestion: nil)
            presenter.view = controller
            presenter.interactor = SuggestionsInteractor()

            return presenter
        }()

        return controller
    }
}
