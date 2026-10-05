//
//  PreStayRouter.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class PreStayRouter: PreStayRouterProtocol {
    weak var viewController: UIViewController?

    func goToPayment(inputParams: CiolReviewAndPayInputParams) {
        let ciolReviewAndPayViewController = CiolReviewAndPayModule.build(inputParams: inputParams)
        viewController?.navigationController?.pushViewController(ciolReviewAndPayViewController, animated: true)
    }

    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails) {
        if ciolConfirmationDetails.stay.isDigitalKeyEnabled {
            let keyViewController = KeyModule.createModule(stay: ciolConfirmationDetails.stay, disableBackButton: true)
            viewController?.navigationController?.pushViewController(keyViewController, animated: true)
            return
        }
        let ciolConfirmationViewController = CiolConfirmationModule.build(ciolConfirmationDetails: ciolConfirmationDetails)
        viewController?.navigationController?.pushViewController(ciolConfirmationViewController, animated: true)
    }

    func goToUpsell(ciolUpsellInputParams: CiolUpsellInputParams, prestayDelegate: PrestayDelegate) {
        let ciolUpsellViewController = CiolUpsellModule.build(
            inputParams: ciolUpsellInputParams,
            prestayDelegate: prestayDelegate
        )
        viewController?.navigationController?.pushViewController(ciolUpsellViewController, animated: true)
    }

    func goToRegCard(regCardInput: GuestDetailsInputBlueprint, prestayDelegate: PrestayDelegate) {
        let vc = GuestDetailsModule.build(input: regCardInput, prestayDelegate: prestayDelegate)
        viewController?.navigationController?.pushViewController(vc, animated: true)
    }

    func goToEditDetails(inputParams: EditDetailsInputParams) {
        guard let editDetailsViewController = EditDetailsModule
              .build(inputParams: inputParams) as? EditDetailsViewController else { return }
        editDetailsViewController.editDetailsViewDelegate = viewController as? EditDetailsViewDelegate

        viewController?.navigationController?.pushViewController(editDetailsViewController, animated: true)
    }

    func viewOccasions(hotelPreferences: [String], completion: ((String) -> Void)?) {
        let simpleListViewController = SimpleListViewController(listItems: hotelPreferences)
        simpleListViewController.modalPresentationStyle = .pageSheet

        if let sheet = simpleListViewController.sheetPresentationController {
            let customHeight = UISheetPresentationController.Detent.Identifier("customHeight")
            sheet.detents = [.custom(identifier: customHeight) { _ in
                350
            }]
            sheet.prefersGrabberVisible = true
        }
        viewController?.present(simpleListViewController, animated: true)
        simpleListViewController.itemSelected = { selectedOccasion in
            completion?(selectedOccasion)
            simpleListViewController.dismiss(animated: true)
        }
    }
}
