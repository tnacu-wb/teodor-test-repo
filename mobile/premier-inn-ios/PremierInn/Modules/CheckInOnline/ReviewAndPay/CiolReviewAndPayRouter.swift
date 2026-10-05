//
//  CiolReviewAndPayRouter.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

final class CiolReviewAndPayRouter: CiolReviewAndPayRouterProtocol {
    weak var viewController: UIViewController?
    private let ciolFlow: CIOLStartFlow
    init(ciolFlow: CIOLStartFlow) {
        self.ciolFlow = ciolFlow
    }

    func navigateToConfirmationScreen(ciolConfirmationDetails: CiolConfirmationDetails) {
        if ciolConfirmationDetails.stay.isDigitalKeyEnabled {
            let keyViewController = KeyModule.createModule(stay: ciolConfirmationDetails.stay, disableBackButton: true)
            viewController?.navigationController?.pushViewController(keyViewController, animated: true)
            return
        }
        let confirmationViewController = CiolConfirmationModule.build(ciolConfirmationDetails: ciolConfirmationDetails)
        viewController?.navigationController?.pushViewController(confirmationViewController, animated: true)
    }

    func processPayment(
        with cccpiPageParams: ThreeCiPageParams,
        using threeCiPageDelegate: ThreeCiPageDelegate,
        webDelegate: WebViewControllerDelegate,
        and webviewLayout: WebViewControllerLayout
    ) {
        // TODO: Update with proper values upon receiving them from business
        let parameters: ThreeCWebPageInitVariables = (
            cccpiPageParams.html,
            PIAnalytics.StateNames.pay3CiPage,
            PIAnalytics.StateTypes.ciolFlow,
            [],
            cccpiPageParams.trackingParams ?? [:],
            webviewLayout
        )
        let controller = ThreeCiPageViewController(parameters: parameters)
        controller.delegate = webDelegate
        controller.threeCiPageDelegate = threeCiPageDelegate
        let navigationController = UINavigationController(rootViewController: controller)
        navigationController.modalPresentationStyle = .fullScreen

        viewController?.present(navigationController, animated: true)
    }

    func showCountriesView(indexPath: IndexPath, completion: ((CountryItem) -> Void)?) {
        let listViewController = ListViewController(
            viewModel: CountriesListViewModel(countries: Country.countriesList),
            invertedColours: true
        )
        listViewController.textFieldPlaceholder = PILocalizedString(
            "searchCountryPlaceholder",
            comment: "Search country input placeholder"
        )
        listViewController.shouldDelaySearchRequest = false
        listViewController.selectedObjectOutput = { sender, object in
            sender.dismiss(animated: true)

            guard let country = object as? Country else { return }
            guard let countryItem = CountryItem(country: country) else { return }
            completion?(countryItem)
        }
        listViewController.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true)
        }
        viewController?.present(listViewController, animated: true)
    }

    func showPostcodePicker(
        with postCode: String?,
        addressType: AddressType,
        completion: ((StoredAddressModel) -> Void)?
    ) {
        let postCodeViewModel = PostCodeLookupViewModel()
        postCodeViewModel.addressType = addressType

        let controller = ListViewController(viewModel: postCodeViewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString(
            "postCodeSearchInputPlaceholder",
            comment: "Post code search input placeholder"
        )
        controller.textFieldValue = postCode
        controller.minimumSearchRequestCharacters = Constants.minimumCharactersForPostCodeLookup
        controller.shouldUppercaseTextFieldInput = true
        controller.shouldShowKeyboardOnLoad = true
        controller.cancelButtonDidTap = { _ in
            controller.dismiss(animated: true)
        }
        controller.selectedObjectOutput = { _, address in
            guard let address = address as? Address else { return }
            let storedAddressViewModel = StoredAddressModel(with: address)
            completion?(storedAddressViewModel)
            controller.dismiss(animated: true)
        }
        viewController?.present(controller, animated: true)
    }

    func navigateToStart() {
        viewController?.navigationController?.setNavigationBarHidden(false, animated: false)

        switch ciolFlow {
        case .bookingConfirmation:
            if let controller = viewController?.navigationController?.viewControllers
               .first(where: { $0 is BookingConfirmationViewController }) {
                viewController?.navigationController?.popToViewController(controller, animated: true)
            } else {
                viewController?.navigationController?.popToRootViewController(animated: true)
            }
        case .myBookings:
            if let controller = viewController?.navigationController?.viewControllers
               .first(where: { $0 is ReservationsListViewController }) {
                viewController?.navigationController?.popToViewController(controller, animated: true)
            } else {
                viewController?.navigationController?.popToRootViewController(animated: true)
            }
        }
    }
}
