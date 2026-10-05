//
//  FindReservationView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

enum ReservationFormRows: String {
    case referenceNumber
    case bookerSurname
    case checkInDate
}

protocol FindReservationViewProtocol: AnyObject {
    func loadViewModel(shouldShowError: Bool, arrivalDate: Date?, reservationNumber: String?, lastName: String?)
    func disableSubmitButton()
    func enableSubmitButton()
    func setCheckInDate(date: Date)
    func stopEditing()
    func validateForm() throws -> PIDictionary?
    func showError(title: String, message: String?)
    func showRowError(_ error: RowValidatorError)
}

class FindReservationViewController: FormekaViewController {
    override var screenName: String { PIAnalytics.StateNames.addBooking }
    override var screenType: String { PIAnalytics.StateTypes.myBookings }

    var presenter: FindReservationPresenterProtocol?

    init() {
        super.init(nibName: String(describing: FormekaViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        title = PILocalizedString("findReservationScreenTitle", comment: "Find reservation screen title")
        navigationItem.titleView?.accessibilityIdentifier = AccessibilityIdentifiers.FindBooking.findABookingPageTitle

        if table != nil {
            table.backgroundColor = .whiteTwo
            table.separatorColor = .ColourLD3
        }

        registerTableElements()

        presenter?.viewIsReady()
    }

    private func registerTableElements() {
        guard table != nil else { return }

        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: FormekaErrorBannerCell.self)
        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerHeaderFooterNib(with: FormekaHeader.self)
    }
}

extension FindReservationViewController: FindReservationViewProtocol {
    func loadViewModel(shouldShowError: Bool, arrivalDate: Date?, reservationNumber: String?, lastName: String?) {
        viewModel = FormekaViewModel(sections: viewModelSections(shouldShowError: shouldShowError))
        viewModel?.delegate = self

        let reservationNumberRow = viewModel?.row(named: ReservationFormRows.referenceNumber.rawValue)
        reservationNumberRow?.value = reservationNumber

        let lastNameRow = viewModel?.row(named: ReservationFormRows.bookerSurname.rawValue)
        lastNameRow?.value = lastName

        let arrivalDateRow = viewModel?.row(named: ReservationFormRows.checkInDate.rawValue)
        arrivalDateRow?.value = arrivalDate

        table.delegate = viewModel
        table.dataSource = viewModel

        table.reloadData()

        if !shouldShowError, let cell: FormekaTextFieldCell = viewModel?.cell(
            forRowNamed: ReservationFormRows.referenceNumber.rawValue,
            table: table
        ) {
            cell.textField.becomeFirstResponder()
        }
    }

    func disableSubmitButton() {
        guard let cell: FormekaSubmitButtonCell = viewModel?.cell(forRowNamed: "SubmitButton", table: table) else { return }

        cell.button.setTitle(nil, for: .normal)
        cell.button.isEnabled = false
        cell.activityIndicator.color = .white
        cell.activityIndicator.startAnimating()
    }

    func enableSubmitButton() {
        guard let cell: FormekaSubmitButtonCell = viewModel?.cell(forRowNamed: "SubmitButton", table: table) else { return }

        cell.button.setTitle(
            PILocalizedString("findReservationSubmitButtonTitle", comment: "Find reservation submit button title"),
            for: .normal
        )
        cell.button.isEnabled = true
        cell.activityIndicator.stopAnimating()
    }

    func setCheckInDate(date: Date) {
        guard let row = viewModel?.row(named: ReservationFormRows.checkInDate.rawValue) else { return }
        guard let indexPath = viewModel?.indexPath(for: row) else { return }

        row.value = date as FormekaValue
        table.reloadRows(at: [indexPath], with: .automatic)

        if var cell = table.cellForRow(at: indexPath) as? FormekaErrorCell {
            cell.errorMessage = nil
        }
    }

    func stopEditing() {
        view.endEditing(true)
    }

    func validateForm() throws -> PIDictionary? {
        try viewModel?.validate()

        return viewModel?.values
    }

    func showError(title: String, message: String?) {
        showAlertWith(title: title, message: message)
    }

    func showRowError(_ error: RowValidatorError) {
        scrollAndFocus(at: viewModel?.indexPath(for: error.row))
    }
}

private extension FindReservationViewController {
    func viewModelSections(shouldShowError: Bool) -> [FormekaModelSection] {
        var sections = [FormekaModelSection]()

        // Error section if required
        if shouldShowError {
            sections.append(errorSection())
        }

        // Form section
        sections.append(reservationDetailsSection())

        // Submit
        sections.append(submitSection())

        return sections
    }

    func errorSection() -> FormekaModelSection {
        FormekaModelSection(
            header: nil,
            rows: [
                FormekaModelRow(tag: LoginRow.banner.rawValue, cellSetup: { indexPath, _, table in
                    guard let cell: FormekaErrorBannerCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.backgroundColor = .whiteTwo

                    cell.message.text = PILocalizedString(
                        "findReservationErrorNotAvailable",
                        comment: "Find reservation error message: reservation not available"
                    )
                    cell.message.textColor = .paleRed
                    cell.message.font = .Body()

                    cell.accessibilityIdentifier = AccessibilityIdentifiers.FindBooking.noBookingExistsNotification
                    return cell
                })
            ],
            footer: nil
        )
    }

    func reservationDetailsSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(textFieldRow(
            name: ReservationFormRows.referenceNumber.rawValue,
            title: PILocalizedString(
                "findReservationReferenceNumberLabel",
                comment: "Find reservation reference number label"
            ),
            value: viewModel?.row(named: ReservationFormRows.referenceNumber.rawValue)?.value as? String,
            traits: {
                var traits = FormekaTextFieldTraits()
                traits.autocapitalizationType = .allCharacters

                return traits
            }(),
            onBlurValidators: [.required],
            titleLabelAccessibilityIdentifier: AccessibilityIdentifiers.FindBooking.findBookingReferenceNumberTitle,
            textFieldAccessibilityIdentifier: AccessibilityIdentifiers.FindBooking.findBookingReferenceNumberTextField,
            maskField: true
        )
        )

        rows.append(textFieldRow(
            name: ReservationFormRows.bookerSurname.rawValue,
            title: PILocalizedString("findReservationLastNameLabel", comment: "Find reservation last name label"),
            value: viewModel?.row(named: ReservationFormRows.bookerSurname.rawValue)?.value as? String,
            traits: {
                var traits = FormekaTextFieldTraits()
                traits.autocapitalizationType = .words

                return traits
            }(),
            onBlurValidators: [.required],
            titleLabelAccessibilityIdentifier: AccessibilityIdentifiers.FindBooking.findBookingLastName,
            textFieldAccessibilityIdentifier: AccessibilityIdentifiers.FindBooking.findBookingLastNameTextField,
            maskField: true
        )
        )

        rows.append(buttonRow(
            tag: ReservationFormRows.checkInDate.rawValue,
            title: PILocalizedString("findReservationArrivalDateLabel", comment: "Find reservation arrival date label"),
            validators: [.notNil],
            value: viewModel?.row(named: ReservationFormRows.checkInDate.rawValue)?.value as? Date,
            titleLabelAccessibilityIdentifier: AccessibilityIdentifiers.FindBooking.findBookingArrivalDateTitle,
            valueLabelAccessibilityIdentifier: AccessibilityIdentifiers.FindBooking.findBookingArrivalDateTextField,
            action: { [weak self] _, row in
                let selectedDate = row.value as? Date ?? Date()

                self?.presenter?.calendarButtonDidTap(date: selectedDate)
            }
        )
        )

        return FormekaModelSection(
            header: header(
                title: PILocalizedString("findReservationHeaderTitle", comment: "Find reservation header title"),
                height: 78
            ),
            rows: rows,
            footer: nil
        )
    }

    func submitSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(tag: "SubmitButton", cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.button.setTitle(
                PILocalizedString("findReservationSubmitButtonTitle", comment: "Find reservation submit button title"),
                for: .normal
            )
            cell.button.setTitleColor(.white, for: .normal)
            cell.button.backgroundColor = .Tint1
            cell.button.titleLabel?.font = .Button1()
            cell.activityIndicator.color = .BasePurple

            cell.button.accessibilityIdentifier = AccessibilityIdentifiers.FindBooking.findBookingFindBookingButton

            cell.topConstraint.constant = 60
            cell.bottomConstraint.constant = 60
            cell.delegate = self
            cell.contentView.backgroundColor = .white

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }
}

extension FindReservationViewController: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        presenter?.submitButtonDidTap()
    }
}
