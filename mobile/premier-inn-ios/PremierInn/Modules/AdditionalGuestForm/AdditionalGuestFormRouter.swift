//
//  AdditionalGuestFormRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol AdditionalGuestFormRouterProtocol {
    func selectedSalutation(completion: @escaping (_ salutation: String?) -> Void)
    func selectedCountry(completion: @escaping (_ country: Country?) -> Void)
    func closeView()
}

class AdditionalGuestFormRouter {
    private weak var viewController: AdditionalGuestFormView?

    static func buildController(user: AdditionalGuest?, index: Int?) -> UIViewController {
        let controller = AdditionalGuestFormView(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.eventHandler = {
            let presenter = AdditionalGuestFormPresenter()
            presenter.view = controller

            let interactor = AdditionalGuestFormInteractor(guest: user, index: index)
            interactor.delegate = presenter
            presenter.interactor = interactor

            let router = AdditionalGuestFormRouter()
            router.viewController = controller

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

extension AdditionalGuestFormRouter: AdditionalGuestFormRouterProtocol {
    func selectedSalutation(completion: @escaping (String?) -> Void) {
        guard let viewController = viewController else { return }

        let viewModel = SalutationsListViewModel(salutations: Constants.CMS.salutations)

        let controller = ListViewController(viewModel: viewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString(
            "salutationListPlaceholder",
            comment: "Salutation list placeholder"
        )
        controller.textFieldValue = nil
        controller.shouldDelaySearchRequest = false
        controller.selectedObjectOutput = { sender, salutation in
            sender.dismiss(animated: true)
            completion(salutation as? String)
        }
        controller.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true)
            completion(nil)
        }

        viewController.present(controller, animated: true)
    }

    func selectedCountry(completion: @escaping (Country?) -> Void) {
        guard let viewController = viewController else { return }

        let viewModel = CountriesListViewModel(countries: Country.countriesList)

        let controller = ListViewController(viewModel: viewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString(
            "searchCountryPlaceholder",
            comment: "Search country input placeholder"
        )
        controller.textFieldValue = nil
        controller.shouldDelaySearchRequest = false
        controller.selectedObjectOutput = { sender, country in
            sender.dismiss(animated: true)
            completion(country as? Country)
        }
        controller.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true)
            completion(nil)
        }

        viewController.present(controller, animated: true)
    }

    func closeView() {
        viewController?.dismiss(animated: true)
    }
}
