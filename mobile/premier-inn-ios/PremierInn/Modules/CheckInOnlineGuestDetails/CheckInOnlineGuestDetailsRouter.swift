//
//  CheckInOnlineGuestDetailsRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 17/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

protocol CheckInOnlineGuestDetailsRouterProtocol {
    func cancelButtonTapped()
    func updated(leadGuest: LeadGuest, at index: Int)
    func showCountriesList()
    func showTitlesList()
}

protocol CheckInOnlineGuestDetailsRouterDelegate: AnyObject {
    func updated(leadGuest: LeadGuest, at index: Int)
}

class CheckInOnlineGuestDetailsRouter {
    private var viewController: UIViewController?
    private var presenter: CheckInOnlineGuestDetailsPresenter?
    private weak var delegate: CheckInOnlineGuestDetailsRouterDelegate?

    static func build(
        with leadGuest: LeadGuest?,
        index: Int,
        hasAdditionalGuest: Bool = false,
        bookerAddress: Address,
        and delegate: CheckInOnlineGuestDetailsRouterDelegate? = nil
    ) -> UIViewController? {
        let controller = CheckInOnlineGuestDetailsView(nibName: String(describing: FormekaViewController.self), bundle: nil)

        controller.eventHandler = {
            let presenter = CheckInOnlineGuestDetailsPresenter()
            presenter.interactor = CheckInOnlineGuestDetailsInteractor(
                with: leadGuest,
                roomIndex: index,
                hasAdditionalGuest: hasAdditionalGuest,
                bookerAddress: bookerAddress
            )

            let router = CheckInOnlineGuestDetailsRouter()
            router.viewController = controller
            router.presenter = presenter
            router.delegate = delegate

            presenter.router = router
            presenter.view = controller

            return presenter
        }()

        let navigationController = UINavigationController(rootViewController: controller)
        return navigationController
    }
}

extension CheckInOnlineGuestDetailsRouter: CheckInOnlineGuestDetailsRouterProtocol {
    func cancelButtonTapped() {
        viewController?.dismiss(animated: true)
    }

    func updated(leadGuest: LeadGuest, at index: Int) {
        delegate?.updated(leadGuest: leadGuest, at: index)
        cancelButtonTapped()
    }

    func showCountriesList() {
        let controller = ListViewController(
            viewModel: CountriesListViewModel(countries: Country.countriesList),
            invertedColours: true
        )
        controller.textFieldPlaceholder = PILocalizedString(
            "searchCountryPlaceholder",
            comment: "Search country input placeholder"
        )
        controller.shouldDelaySearchRequest = false
        controller.selectedObjectOutput = { [weak self] sender, object in
            sender.dismiss(animated: true)

            guard let country = object as? Country else { return }
            self?.presenter?.userSelected(country: country)
        }
        controller.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true)
        }

        viewController?.present(controller, animated: true)
    }

    func showTitlesList() {
        let viewModel = SalutationsListViewModel(salutations: Constants.CMS.salutations)

        let controller = ListViewController(viewModel: viewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString(
            "salutationListPlaceholder",
            comment: "Salutation list placeholder"
        )
        controller.textFieldValue = nil
        controller.shouldDelaySearchRequest = false
        controller.selectedObjectOutput = { [weak self] sender, salutation in
            sender.dismiss(animated: true)

            guard let salutation = salutation as? String else { return }
            self?.presenter?.userSelected(title: salutation)
        }
        controller.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true)
        }

        viewController?.present(controller, animated: true)
    }
}
