//
//  CiolUpsellDetailsPresenter.swift
//  PremierInn
//
//  Created by Oltean Vasile Bogdan on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CiolUpsellDetailsPresenter: CiolUpsellDetailsViewEventHandler {
    weak var view: CiolUpsellDetailsViewProtocol?
    var interactor: CiolUpsellDetailsInteractorProtocol?
    var router: CiolUpsellDetailsRouterProtocol?

    func viewIsReady() {
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters
        reloadViewModel()
    }

    func reloadViewModel() {
        guard let viewModel = interactor?.ciolUpsellDetailsViewModel else { return }
        view?.displayUpsellData(viewModel: viewModel)
    }

    func showAllergyInfo() {
        guard let allergens = interactor?.ciolUpsellDetailsViewModel.upsell.allergensUrls else {
            return
        }

        if allergens.count > 1 {
            let optionMenu = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
            for allergen in allergens {
                let openAction = UIAlertAction(title: allergen.name, style: .default, handler: { [weak self] _ in
                    self?.router?.showMenuOrAllergyInfo(url: "https://premierinn.com" + allergen.path)
                    self?.interactor?.trackAllergensScreenAnalytics()
                })
                optionMenu.addAction(openAction)
            }
            let cancelAction = UIAlertAction(title: PILocalizedString("Cancel"), style: .cancel, handler: { _ in
            })
            optionMenu.addAction(cancelAction)
            (view as? UIViewController)?.present(optionMenu, animated: true, completion: nil)
        } else {
            guard let url = allergens.first?.path else { return }

            router?.showMenuOrAllergyInfo(url: "https://premierinn.com" + url)
            interactor?.trackAllergensScreenAnalytics()
        }
    }

    func showMenu() {
        guard let menus = interactor?.ciolUpsellDetailsViewModel.upsell.menuUrls else {
            return
        }

        if menus.count > 1 {
            let optionMenu = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
            for menu in menus {
                let openAction = UIAlertAction(title: menu.title, style: .default, handler: { [weak self] _ in
                    self?.router?.showMenuOrAllergyInfo(url: "https://premierinn.com" + (menu.path ?? ""))
                    self?.interactor?.trackMenuScreenAnalytics()
                })
                optionMenu.addAction(openAction)
            }
            let cancelAction = UIAlertAction(title: PILocalizedString("Cancel"), style: .cancel, handler: { _ in
            })
            optionMenu.addAction(cancelAction)
            (view as? UIViewController)?.present(optionMenu, animated: true, completion: nil)
        } else {
            guard let url = interactor?.ciolUpsellDetailsViewModel.upsell.menuUrls.first?.path else { return }

            router?.showMenuOrAllergyInfo(url: "https://premierinn.com" + url)
            interactor?.trackMenuScreenAnalytics()
        }
    }

    func didUpdateUpsell(with id: String, action: CIOLStepperAction, completion: @escaping () -> Void) {
        guard let interactor else { return }
        interactor.didUpdateUpsell(with: id, action: action) { [weak self] in
            guard let self else { return }
            completion()
            view?.updateButton(state: interactor.addButtonConfig)
            view?.updateEnablement(models: interactor.output.upsells)
        }
    }

    func sendUpsellOutput(action: CiolUpsellDetailsOutputAction) {
        interactor?.sendUpsellOutput(action: action)
    }
}
