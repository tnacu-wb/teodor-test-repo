//
//  AddressSectionPresenter.swift
//  PremierInn
//
//  Created by Nick Jones on 20/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

protocol AddressSectionPresenterInput {
	func billingAddressSwitchDidChange(isSameAsYourAddress: Bool)
    func addAddressManuallyButtonDidTap()
    func userPicked(_ country: Country?)
    func userPicked(_ address: Address?)
    func userDidTapCountryRow()
    func userCancelledCountryPicker()
    func postcodeSearchButtonDidTap(with initialValue: String?)
    func userCancelledPostcodePicker()
}

class AddressSectionPresenter {
	weak var view: AddressSectionViewInput?

	deinit {
		print("DEINIT: \(self)")
	}
}

extension AddressSectionPresenter: AddressSectionPresenterInput {
    func postcodeSearchButtonDidTap(with value: String?) {
        view?.showPostcodePicker(with: value)
    }

    func userCancelledCountryPicker() {
        view?.dismissCountryPicker()
    }

    func userCancelledPostcodePicker() {
        view?.dismissPostcodePicker()
    }

    func userDidTapCountryRow() {
        view?.showCountryPicker()
    }

    func userPicked(_ country: Country?) {
        view?.dismissCountryPicker()

        guard let country = country else { return }

        view?.updateCountryRow(with: country)
        view?.configurePostcodeRow(for: country)
        view?.showAllAddressRows(with: nil, completion: nil)
        view?.removeAddressButton()
    }

    func userPicked(_ address: Address?) {
        view?.dismissPostcodePicker()

        guard let address = address else { return }

        view?.toggleCompanyRow(with: address)
        view?.updateCompanyRow(with: address)
        view?.showAllAddressRows(with: address, completion: nil)
        view?.updatePostcodeRow(with: address.postcode)
        view?.removeAddressButton()
    }

	func billingAddressSwitchDidChange(isSameAsYourAddress: Bool) {
        if isSameAsYourAddress {
            view?.removeAddressButton()
            view?.removeAddressSection()
            if view?.shouldShowAddressSummary == true {
                view?.addAddressSummaryRow()
            }
        } else {
            view?.removeAddressSummaryRow()
            view?.showAddressSection()
            view?.addAddressButton()
        }
    }

    func addAddressManuallyButtonDidTap() {
		view?.showAllAddressRows(with: nil) { [unowned self] in
			view?.removeAddressButton()
		}
    }
}
