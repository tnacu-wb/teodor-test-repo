//
//  RegisterView.swift
//  PremierInn
//
//  Created by Freddie Parks on 02/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import UIKit

enum RegisterRow: String {
    case error
    case title
    case firstName
    case lastName
    case postcode
    case addressLine1
    case addressLine2
    case addressLine3
    case addressLine4
    case nationality
    case addressType = "type"
    case companyName
    case emailAddress
    case invoiceDelivery
    case mobileNumber
    case password
    case termsAndConditions
    case marketingOptIn
    case submit
}

class RegisterViewController: FormekaViewController {
    var presenter: RegisterPresenterInput?
    var termsAndConditionEnabled = false
    var isOptIn: Bool?
    var persistedCompanyName: String?

    private(set) var addressSectionView: AddressSectionView?

    override var screenName: String {
        presenter?.registerTracking.screenName ?? super.screenName
    }

    override var screenType: String {
        presenter?.registerTracking.screenType ?? super.screenType
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        registerTableElements()
        ContentsquareConfig.mask(view: self.view)
        presenter?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: animated)
    }

    private func registerTableElements() {
        guard table != nil else { return }

        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaTextFieldPasswordCell.self)
        table.registerCellNib(with: FormekaSegmentedControlCell.self)
        table.registerCellNib(with: FormekaPostCodeCell.self)
        table.registerCellNib(with: FormekaFreeTextCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: SwitchCell.self)
        table.registerCellNib(with: ErrorCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: TermsAndConditionsCell.self)
        table.registerCellClass(with: SimpleSeparatorsCell.self)
        table.registerHeaderFooterNib(with: FormekaHeader.self)
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)
    }

    @objc func cancelButtonDidTap() {
        presenter?.dismissRegisterDidTap()
    }
}

extension RegisterViewController: RegisterViewInput {
    func loadMarketingOptIn(with viewModel: MarketingPreferenceViewModelType?) {
        let isOptIn = viewModel?.isOptIn ?? false
        self.isOptIn = isOptIn
    }

    func setup(withTitle title: String) {
        self.title = title

        table.backgroundColor = .whiteTwo
        table.separatorColor = .ColourLD3

        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(cancelButtonDidTap)
        )
    }

    func stopEditing() {
        view.endEditing(true)
    }

    func focusFormRow(at indexPath: IndexPath) {
        scrollAndFocus(at: indexPath)
    }

    func presentAlertWith(title: String, error: Error?) {
        showErrorAlertWith(title: title, error: error)
    }

    func lock() {
        isLocked = true
    }

    func unlock() {
        isLocked = false
    }

    func askForBiometricActivation(completion: @escaping () -> Void) {
        let alert = UIAlertController(
            title: BiometricAuthenticationManager.biometryTypeAvailable.enableTitle,
            message: BiometricAuthenticationManager.biometryTypeAvailable.enableMessage,
            preferredStyle: .alert
        )

        alert.addAction(UIAlertAction(
            title: BiometricAuthenticationManager.biometryTypeAvailable.enableAction,
            style: .default
        ) { _ in
            self.presenter?.biometricActivationCompleted(activated: true)
        })

        alert.addAction(UIAlertAction(
            title: BiometricAuthenticationManager.biometryTypeAvailable.cancelAction,
            style: .cancel
        ) { _ in
            self.presenter?.biometricActivationCompleted(activated: false)
        })

        present(alert, animated: true, completion: nil)
    }

    func updateSubmitButton(isLoading: Bool) {
        guard let cell: FormekaSubmitButtonCell = viewModel?.cell(forRowNamed: LoginRow.submit.rawValue, table: table)
            else { return }

        let title = isLoading ? nil : PILocalizedString(
            "registerButtonTitle",
            comment: "Register form: register button title"
        )

        cell.button.setTitle(title, for: .normal)
        cell.button.isEnabled = !isLoading

        if isLoading {
            cell.activityIndicator.startAnimating()
        } else {
            cell.activityIndicator.stopAnimating()
        }
    }

    func reloadViewModel() {
        addressSectionView = {
            let addressRequirements = AddressSectionRequirements(
                address: nil,
                storedAddress: nil,
                shouldShowAddressSwitch: false,
                useStoredAddressSwitchDescription: nil,
                addressSwitchInitialState: false,
                shouldShowAddressForm: true,
                shouldShowHeader: true,
                shouldShowFooter: true,
                shouldShowAddressSummary: false
            )

            let view = AddressSectionRouter.buildSection(with: addressRequirements)
            view.parentFormekaViewController = self
            view.segmentedControlDelegate = self
            view.companyNameDelegate = self
            return view
        }()

        viewModel = FormekaViewModel(sections: viewModelSections)
        viewModel?.delegate = self

        if table != nil {
            table.delegate = viewModel
            table.dataSource = viewModel

            table.reloadData()
        }
    }

    func selected(salutation: String) {
		guard let row = viewModel?.row(named: RegisterRow.title.rawValue) else { return }

		row.value = salutation
		row.error = nil

		table?.reloadData()
    }
}
