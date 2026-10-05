//
//  EditDetailsModule.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

enum EditDetailsModule {
    static func build(inputParams: EditDetailsInputParams) -> UIViewController {
        let viewController = EditDetailsViewController(nibName: String(describing: FormekaViewController.self), bundle: nil)

        viewController.eventHandler = {
            let presenter = EditDetailsPresenter()
            let uniqueSalutations: [String] = (Array(NSOrderedSet(array: Constants.CMS.salutations)) as? [String]) ?? []
            let interactor = EditDetailsInteractor(salutationItems: uniqueSalutations, editDetailsInputParams: inputParams)
            let router = EditDetailsRouter()

            viewController.eventHandler = presenter

            presenter.view = viewController
            presenter.interactor = interactor
            presenter.router = router

            router.viewController = viewController
            return presenter
        }()

        return viewController
    }
}

struct EditDetailsInputParams {
    let editDetailsViewModel: EditDetailsModel
    let analyticsParams: PIDictionary
}

protocol EditDetailsEventHandler {
    var flow: EditDetailsFlow? { get }
    var address: StoredAddressModel? { get }
    var isLeadGuest: Bool { get }

    func viewIsReady()
    func handleValidationSuccess()
    func showSalutationView(indexPath: IndexPath)
    func showCountriesView(indexPath: IndexPath, source: ListViewSource)
    func salutationUpdated(with title: String, indexPath: IndexPath)
    func nationalityUpdated(with country: CountryItem, indexPath: IndexPath)
    func updateEditDetailsModel(with viewModel: EditDetailsModel)
    func didUpdateNationality(with country: CountryItem)
    func didUpdateCountry(with country: CountryItem)

    func didTapPostcodeSearch(with postcode: String?)
    func didDismissPostcodeSearch()
    func didSelectAddress(_ address: Any?)
}

protocol EditDetailsRouterProtocol {
    func showSalutationView(listItems: [String], indexPath: IndexPath)
    func showCountriesView(indexPath: IndexPath, source: ListViewSource)
    func goBackToPreStayView()
}

protocol EditDetailsInteractorProtocol {
    var salutationItems: [String] { get set }
    var editDetailsViewModel: EditDetailsModel { get set }
    var customAnalyticsParameters: PIDictionary? { get }

    func updateModel(with editDetailsModel: EditDetailsModel)
    func updateAddress(_ address: Any?)
}

protocol EditDetailsViewProtocol: AnyObject {
    var editDetailsViewDelegate: EditDetailsViewDelegate? { get set }
    var customAnalyticsParameters: PIDictionary? { get set }

    func loadViewModel(with editDetailsModel: EditDetailsModel)
    func onTitleFieldSelection(salutation: String, indexPath: IndexPath)
    func onNationalityFieldSelection(country: CountryItem, indexPath: IndexPath)

    func showPostcodePicker(with postcode: String?)
    func dismissPostcodePicker()
}

enum ListViewSource {
    case countries, nationalities
}
