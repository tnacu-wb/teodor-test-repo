//
//  EditDetailsViewController.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

protocol EditDetailsViewDelegate: AnyObject {
    func didUpdateUserDetails(with editDetailsModel: EditDetailsModel)
}

class EditDetailsViewController: FormekaViewController {
    var eventHandler: EditDetailsEventHandler?
    weak var editDetailsViewDelegate: EditDetailsViewDelegate?

    override var screenName: String { PIAnalytics.StateNames.checkInOnlineGuestDetails }
    override var screenType: String { PIAnalytics.StateTypes.ciolFlow }
    override var customParameters: [String: Any]? { customAnalyticsParameters }
    var customAnalyticsParameters: PIDictionary?

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.contentInset = .init(top: 16, left: 0, bottom: 0, right: 0)
            table.backgroundColor = .white
            table.separatorStyle = .none
            table.accessibilityIdentifier = "tableView"

            registerTableElements()
        }
        setupNavigationBarElements()
        eventHandler?.viewIsReady()
    }

    private func registerTableElements() {
        table.register(EditDetailsCell.self, forCellReuseIdentifier: EditDetailsCell.reuseIdentifier)
        table.register(GuestEditDateCell.self, forCellReuseIdentifier: GuestEditDateCell.reuseIdentifier)
    }

    private func setupNavigationBarElements() {
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            title: PILocalizedString("ciolSaveButton"),
            style: .plain,
            target: self,
            action: #selector(saveButtonAction)
        )
        navigationItem.rightBarButtonItem?.tintColor = .Tint1
        navigationItem.rightBarButtonItem?.setTitleTextAttributes(
            [.font: UIFont.Body_Semibold()],
            for: .normal
        )

        guard case .regCard = eventHandler?.flow else {
            title = PILocalizedString("regularGuestEditButtonTitle")
            return
        }
        title = PILocalizedString(eventHandler?
            .isLeadGuest ?? false ? "ciolRegCardEditLeadTitle" : "ciolRegCardEditAdditionalTitle")
    }

    override func textFieldDidUpdateContent(value: String, cell: FormekaTextFieldCell) {
        guard let indexPath = table.indexPath(for: cell) else { return }
        guard let row = viewModel?.row(at: indexPath) else { return }

        row.value = value
        cell.valueChanged?()
    }

    @objc private func saveButtonAction() {
        validateFields()
    }

    private func validateFields() {
        do {
            try viewModel?.validate()
        } catch let error as RowValidatorError {
            if let indexPath = viewModel?.indexPath(for: error.row),
               var cell = table.cellForRow(at: indexPath) as? FormekaErrorCell {
                cell.errorMessage = error.localizedDescription
                table.beginUpdates()
                table.endUpdates()
                return
            }
        } catch {
            return
        }
        eventHandler?.handleValidationSuccess()
    }

    override func keyboardWillShow(notification: Notification) {
        switch eventHandler?.flow {
        case .regCard:
            guard let keyboardFrame = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect else {
                return
            }
            let keyboardHeight = keyboardFrame.height
            UIView.animate(withDuration: .ocd) {
                self.table.contentInset.bottom = keyboardHeight
                self.table.verticalScrollIndicatorInsets.bottom = keyboardHeight
                if let activeField = self.view.findFirstResponder() {
                    let rect = self.table.convert(activeField.bounds, from: activeField)
                    self.table.scrollRectToVisible(rect.insetBy(dx: 0, dy: -10), animated: true)
                }
            }
        default:
            super.keyboardWillShow(notification: notification)
        }
    }

    override func keyboardWillHide(notification: Notification) {
        switch eventHandler?.flow {
        case .regCard:
            UIView.animate(withDuration: .ocd) {
                self.table.contentInset.bottom = 0
                self.table.verticalScrollIndicatorInsets.bottom = 0
            }
        default:
            super.keyboardWillHide(notification: notification)
        }
    }
}

extension EditDetailsViewController: EditDetailsViewProtocol {
    func onNationalityFieldSelection(country: CountryItem, indexPath: IndexPath) {
        guard let row = viewModel?.row(at: indexPath) else { return }
        var value: String?
        switch row.tag {
        case Step1Row.nationality.rawValue:
            eventHandler?.didUpdateNationality(with: country)
            value = country.title
        case Step1Row.country.rawValue:
            eventHandler?.didUpdateCountry(with: country)
            value = country.country.displayName
        default: value = nil
        }
        row.value = value

        if let cell = table.cellForRow(at: indexPath) as? EditDetailsCell {
            cell.outlinedTextField.textField.text = country.title
        }
        eventHandler?.viewIsReady()
        table.reloadData()
    }

    func onTitleFieldSelection(salutation: String, indexPath: IndexPath) {
        guard let row = viewModel?.row(at: indexPath) else { return }
        row.value = salutation

        try? row.validate()

        if let cell = table.cellForRow(at: indexPath) as? EditDetailsCell {
            cell.outlinedTextField.textField.text = salutation
        }

        if var cell = table.cellForRow(at: indexPath) as? FormekaErrorCell {
            cell.errorMessage = row.error?.localizedDescription
        }
    }

    func loadViewModel(with editDetailsModel: EditDetailsModel) {
        viewModel = tableViewModel(with: editDetailsModel)
        viewModel?.delegate = self

        if table != nil {
            table.delegate = viewModel
            table.dataSource = viewModel
            table.reloadData()
        }
    }

    // MARK: - Postcode 

    func showPostcodePicker(with postCode: String?) {
        view.endEditing(true)

        let postCodeViewModel = PostCodeLookupViewModel()
        postCodeViewModel.addressType = .home

        let controller = ListViewController(viewModel: postCodeViewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString("postCodeSearchInputPlaceholder")
        controller.textFieldValue = postCode
        controller.minimumSearchRequestCharacters = Constants.minimumCharactersForPostCodeLookup
        controller.shouldUppercaseTextFieldInput = true
        controller.shouldShowKeyboardOnLoad = true

        controller.cancelButtonDidTap = { [weak self] _ in
            self?.eventHandler?.didDismissPostcodeSearch()
        }

        controller.selectedObjectOutput = { [weak self] sender, object in
            sender.dismiss(animated: true)
            self?.eventHandler?.didSelectAddress(object)
        }

        ContentsquareConfig.mask(view: controller.view)
        navigationController?.present(controller, animated: true)
    }

    func dismissPostcodePicker() {
        guard let picker = navigationController?.presentedViewController as? ListViewController else { return }
        picker.dismiss(animated: true)
    }
}
