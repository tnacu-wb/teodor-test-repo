//
//  AddNewCardView.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 03/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

enum AddNewCardViewRow: String {
    case savedCardSyncInfo
    case paymentMethod
    case cnpRow
    case memorableWord
    case memorableWordInfo
    case submitButton
}

enum AddNewCardError: LocalizedError {
    case missingFormValues

    var errorDescription: String? { String(describing: self)    }
}

class AddNewCardView: FormekaViewController {
    var eventHandler: AddNewCardViewEventHandler?

    override var screenName: String {
        PIAnalytics.StateNames.manageCardsAdd
    }
    override var screenType: String {
        PIAnalytics.StateTypes.myPI
    }

    private(set) var addressSectionView: AddressSectionView?
    private(set) var persistedCompanyName: String?

    private var activityIndicator = UIActivityIndicatorView()
    private let viewForActivityIndicator = UIView()

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .plain, target: nil, action: nil)

        if table != nil {
            table.backgroundColor = .whiteTwo
            table.separatorColor = .ColourLD3
            table.separatorStyle = .none
            table.accessibilityIdentifier = "tableView"

            registerTableElements()
        }

        eventHandler?.viewIsReady()
    }

    private func registerTableElements() {
        table.registerHeaderFooterNib(with: TitleAndOptionalImageHeader.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: DynamicListCell.self)
        table.registerCellNib(with: FullsizeTextFieldCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)

        // address view
        table.registerCellNib(with: SwitchCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: FormekaSegmentedControlCell.self)
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerCellNib(with: FormekaPostCodeCell.self)
        table.registerHeaderFooterNib(with: FormekaHeader.self)
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)
    }

    private func setupAddressSectionView(addressRequirements: AddressSectionRequirements) {
        addressSectionView = {
            let view = AddressSectionRouter.buildSection(with: addressRequirements)
            view.parentFormekaViewController = self
            view.segmentedControlDelegate = self
            view.companyNameDelegate = self

            return view
        }()
    }

    private func setupConfirmationActivityIndicatorConstraints() {
        let viewForActivityIndicatorHorizontalConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .centerX,
            relatedBy: .equal,
            toItem: self.view,
            attribute: .centerX,
            multiplier: 1,
            constant: 0
        )
        let viewForActivityIndicatorVerticalConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .centerY,
            relatedBy: .equal,
            toItem: self.view,
            attribute: .centerY,
            multiplier: 1,
            constant: 0
        )
        let viewForActivityIndicatorWidthConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .width,
            relatedBy: .equal,
            toItem: self.view,
            attribute: .width,
            multiplier: 1,
            constant: 0
        )
        let viewForActivityIndicatorHeightConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .height,
            relatedBy: .equal,
            toItem: self.view,
            attribute: .height,
            multiplier: 1,
            constant: 0
        )
        let activityIndicatorHorizontalConstraint = NSLayoutConstraint(
            item: activityIndicator as Any,
            attribute: .centerX,
            relatedBy: .equal,
            toItem: viewForActivityIndicator,
            attribute: .centerX,
            multiplier: 1,
            constant: 0
        )
        let activityIndicatorVerticalConstraint = NSLayoutConstraint(
            item: activityIndicator as Any,
            attribute: .centerY,
            relatedBy: .equal,
            toItem: viewForActivityIndicator,
            attribute: .centerY,
            multiplier: 1,
            constant: 0
        )
        NSLayoutConstraint.activate(
            [
                viewForActivityIndicatorHorizontalConstraint,
                viewForActivityIndicatorVerticalConstraint,
                viewForActivityIndicatorWidthConstraint,
                viewForActivityIndicatorHeightConstraint,
                activityIndicatorHorizontalConstraint,
                activityIndicatorVerticalConstraint
            ]
        )
    }

    private func startDisplayingLoadingElements() {
        viewForActivityIndicator.frame = CGRect(x: 0.0, y: 0.0, width: view.frame.size.width, height: view.frame.size.height)
        viewForActivityIndicator.backgroundColor = .BaseBlack.withAlphaComponent(0.8)
        viewForActivityIndicator.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(viewForActivityIndicator)

        activityIndicator = UIActivityIndicatorView(style: .large)
        activityIndicator.color = .lightGray
        activityIndicator.hidesWhenStopped = true
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false
        activityIndicator.startAnimating()
        view.addSubview(activityIndicator)

        table.isUserInteractionEnabled = false
        table.alpha = 0.5

        // Auto layout
        setupConfirmationActivityIndicatorConstraints()
    }

    private func stopDisplayingLoadingElements() {
        activityIndicator.stopAnimating()
        activityIndicator.removeFromSuperview()
        viewForActivityIndicator.removeFromSuperview()
        table.isUserInteractionEnabled = true
        table.alpha = 1

        if let row = viewModel?.row(named: AddNewCardViewRow.submitButton.rawValue),
           let indexPath = viewModel?.indexPath(for: row),
           let submitButtonCell = table.cellForRow(at: indexPath) as? FormekaSubmitButtonCell {
            submitButtonCell.button.isEnabled = true
        }
    }

    private var memorableWord: String? {
        let row = viewModel?.row(named: AddNewCardViewRow.memorableWord.rawValue)

        return row?.value as? String
    }

    private func validateForm() throws -> PIDictionary? {
        try viewModel?.validate()

        return viewModel?.values
    }
}

extension AddNewCardView: AddNewCardViewProtocol {
    func loadViewModel(addNewCardViewModel: AddNewCardViewModel) {
        setupAddressSectionView(addressRequirements: addNewCardViewModel.billingAddressViewModel.addressRequirements)

        let sections = viewModelSections(addNewCardViewModel: addNewCardViewModel)
        viewModel = FormekaViewModel(sections: sections)
        viewModel?.delegate = self

        if table != nil {
            table.delegate = viewModel
            table.dataSource = viewModel
        }
    }

    func updatePaymentMethods(with viewModel: AddNewCardPaymentMethodsViewModel) {
        guard let indexPath = self.viewModel?.indexPath(forRowNamed: AddNewCardViewRow.paymentMethod.rawValue),
              self.viewModel?.remove(sectionAtIndex: indexPath.section) != nil else { return }

        let updatedPaymentMethodsSection = paymentMethodsSection(with: viewModel)

        self.viewModel?.add(section: updatedPaymentMethodsSection, index: indexPath.section)
        table.reloadSections(IndexSet(integer: indexPath.section), with: .automatic)
    }

    func finishedLoading() {
        stopDisplayingLoadingElements()
    }

    func showError(title: String, message: String) {
        let controller = TimeoutErrorAlertController(title: nil, message: nil, preferredStyle: .alert)
        controller.headingText = title
        controller.messageText = message
        controller.messageButton = PILocalizedString("Close")
        controller.controllerOutput = self

        present(controller, animated: true)
    }
}

extension AddNewCardView: TimeoutErrorAlertControllerOutput {
    func tryAgainButtonDidTap(sender: UIViewController) {
        sender.dismiss(animated: true)
    }
}

extension AddNewCardView: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        do {
            guard let values = try validateForm() else { throw AddNewCardError.missingFormValues }

            if let button = cell.button {
                button.isEnabled = false
            }
            startDisplayingLoadingElements()
            eventHandler?.addCardDidTap(values: values, memorableWord: memorableWord)
        } catch let error as RowValidatorError {
            scrollAndFocus(at: viewModel?.indexPath(for: error.row))
        } catch {
            self.showError(
                title: PILocalizedString("Something went wrong"),
                message: PILocalizedString("initiateSaveCardGenericError")
            )
        }
    }
}

extension AddNewCardView: FormekaSegmentedControlCellDelegate, CompanyNameDelegate {
    func formekaSegmentedControlValueDidChange(cell: FormekaSegmentedControlCell) {
        guard let indexPath = table.indexPath(for: cell) else { return }
        guard let row = viewModel?.row(at: indexPath) else { return }

        switch row.tag {
        case GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix:

            switch cell.segmentedControl.selectedSegmentIndex {
            case 0: // home
                persistedCompanyName = viewModel?.row(named: GuestDetailsRow.companyName.rawValue)?.value as? String
                if let removedIndexPath = viewModel?.remove(rowNamed: GuestDetailsRow.companyName.rawValue) {
                    table.deleteRows(at: [removedIndexPath], with: .automatic)
                }

            default: // work
                let newIndexPath = IndexPath(item: indexPath.row + 1, section: indexPath.section)
                let textFieldRow = textFieldRow(
                    name: GuestDetailsRow.companyName.rawValue,
                    title: PILocalizedString("userDetailsCompanyName", comment: "User details form: company name label"),
                    value: persistedCompanyName,
                    inlineValidators: [.companyName],
                    onBlurValidators: [.required, StringLengthValidator(range: 1...40)]
                )
                viewModel?.add(row: textFieldRow, at: newIndexPath)
                table.insertRows(at: [newIndexPath], with: .automatic)
            }

        default:
            break
        }
    }

    func removePersistedCompanyName() {
        persistedCompanyName = nil
    }
}
