//
//  EditDetailsRouter.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

final class EditDetailsRouter: EditDetailsRouterProtocol {
    weak var viewController: UIViewController?

    func showSalutationView(listItems: [String], indexPath: IndexPath) {
        let salutationViewController = SimpleListViewController(listItems: listItems)
        salutationViewController.modalPresentationStyle = .pageSheet
        if let sheet = salutationViewController.sheetPresentationController {
            sheet.detents = [.medium()]
            sheet.prefersGrabberVisible = true
        }
        viewController?.present(salutationViewController, animated: true)
        salutationViewController.itemSelected = { [weak self] item in
            guard let controller = self?.viewController as? EditDetailsViewController else { return }
            controller.eventHandler?.salutationUpdated(
                with: item,
                indexPath: indexPath
            )
            salutationViewController.dismiss(animated: true)
        }
    }

    func showCountriesView(indexPath: IndexPath, source: ListViewSource) {
        let listViewController = ListViewController(viewModel: CountriesListViewModel(
            countries: Country.countriesList,
            source: source
        ), invertedColours: true)
        listViewController.textFieldPlaceholder = PILocalizedString("searchCountryPlaceholder")
        listViewController.shouldDelaySearchRequest = false
        listViewController.selectedObjectOutput = { [weak self] sender, object in
            sender.dismiss(animated: true)

            guard let country = object as? Country else { return }
            guard let controller = self?.viewController as? EditDetailsViewController else { return }
            guard let countryItem = CountryItem(country: country) else { return }

            controller.eventHandler?.nationalityUpdated(with: countryItem, indexPath: indexPath)
        }

        listViewController.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true)
        }
        viewController?.present(listViewController, animated: true)
    }

    func goBackToPreStayView() {
        guard let viewController = self.viewController else { return }

        viewController.navigationController?.popViewController(animated: true)
    }
}
