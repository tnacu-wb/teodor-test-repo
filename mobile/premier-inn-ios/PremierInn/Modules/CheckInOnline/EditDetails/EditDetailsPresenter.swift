//
//  EditDetailsPresenter.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

final class EditDetailsPresenter {
    weak var view: EditDetailsViewProtocol?
    var interactor: EditDetailsInteractorProtocol?
    var router: EditDetailsRouterProtocol?
}

extension EditDetailsPresenter: EditDetailsEventHandler {
    var isLeadGuest: Bool {
        interactor?.editDetailsViewModel.isLeadGuest ?? false
    }

    func updateEditDetailsModel(with viewModel: EditDetailsModel) {
        interactor?.updateModel(with: viewModel)
    }

    var flow: EditDetailsFlow? {
        interactor?.editDetailsViewModel.flow
    }

    var address: StoredAddressModel? {
        interactor?.editDetailsViewModel.address
    }

    func handleValidationSuccess() {
        guard let editDetailsViewModel = interactor?.editDetailsViewModel else { return }

        view?.editDetailsViewDelegate?.didUpdateUserDetails(with: editDetailsViewModel)
        router?.goBackToPreStayView()
    }

    func salutationUpdated(with title: String, indexPath: IndexPath) {
        interactor?.editDetailsViewModel.title = title
        view?.onTitleFieldSelection(salutation: title, indexPath: indexPath)
    }

    func nationalityUpdated(with country: CountryItem, indexPath: IndexPath) {
        view?.onNationalityFieldSelection(country: country, indexPath: indexPath)
    }

    func viewIsReady() {
        guard let editDetailsViewModel = interactor?.editDetailsViewModel else { return }
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters
        view?.loadViewModel(with: editDetailsViewModel)
    }

    func showSalutationView(indexPath: IndexPath) {
        router?.showSalutationView(
            listItems: interactor?.salutationItems ?? [],
            indexPath: indexPath
        )
    }

    func showCountriesView(indexPath: IndexPath, source: ListViewSource) {
        router?.showCountriesView(indexPath: indexPath, source: source)
    }

    func didUpdateNationality(with country: CountryItem) {
        interactor?.editDetailsViewModel.country = country
        interactor?.editDetailsViewModel.passportNumber = nil
    }

    func didUpdateCountry(with country: CountryItem) {
        var address = interactor?.editDetailsViewModel.address ?? StoredAddressModel()
        address.country = country.country
        interactor?.editDetailsViewModel.address = address
    }

    func didTapPostcodeSearch(with postcode: String?) {
        view?.showPostcodePicker(with: postcode)
    }

    func didDismissPostcodeSearch() {
        view?.dismissPostcodePicker()
    }

    func didSelectAddress(_ address: Any?) {
        interactor?.updateAddress(address)
        view?.dismissPostcodePicker()
        self.viewIsReady()
    }
}
