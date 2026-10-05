//
//  AdditionalGuestFormView.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

private enum AdditionalGuestFormError: LocalizedError {
    case missingValues

	var errorDescription: String? { String(describing: self)	}
}

protocol AdditionalGuestFormViewProtocol: AnyObject {
    func set(title: String)
    func loadViewModel(user: AdditionalGuest?, viewContent: AdditionalGuestFormContent)
    func validatedUser() throws -> AdditionalGuest
    func showErrorMessage(title: String, error: Error)
    func showErrorFor(row: FormekaModelRow)
    func selected(salutation: String)
    func selected(nationality: Country)
    func updateViewBusy(busy: Bool)
}

protocol AdditionalGuestFormViewEventHandler {
    var additionalGuestFormTracking: AdditionalGuestFormTracking { get }

    func viewIsReady()
    func save(user: AdditionalGuest?)
    func cancel()
    func submitForm()
    func salutationRowDidTap()
    func countryRowDidTap()
}

class AdditionalGuestFormView: FormekaViewController {
    var eventHandler: AdditionalGuestFormViewEventHandler?
    var activityIndicator: UIActivityIndicatorView?

    override var screenName: String {
        eventHandler?.additionalGuestFormTracking.screenName ?? super.screenName
    }
    override var screenType: String {
        eventHandler?.additionalGuestFormTracking.screenType ?? super.screenType
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        activityIndicator = UIActivityIndicatorView(style: .medium)
        activityIndicator?.hidesWhenStopped = true

        if let activityIndicator = activityIndicator {
            view.addSubview(activityIndicator)
        }

        if table != nil {
            activityIndicator?.center = table.center

            table.backgroundColor = .whiteTwo
            table.separatorColor = .ColourLD3

            registerTableElements()
        }

        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(cancelButtonDidTap)
        )

        eventHandler?.viewIsReady()
    }

    private func registerTableElements() {
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
    }

    @objc func cancelButtonDidTap() {
        eventHandler?.cancel()
    }
}

extension AdditionalGuestFormView: AdditionalGuestFormViewProtocol {
    func set(title: String) {
        navigationItem.title = title
    }

    func loadViewModel(user: AdditionalGuest?, viewContent: AdditionalGuestFormContent) {
        viewModel = FormekaViewModel(sections: viewModelSections(guest: user, content: viewContent))
        viewModel?.delegate = self

        table?.delegate = viewModel
        table?.dataSource = viewModel
        table?.reloadData()
    }

    func validatedUser() throws -> AdditionalGuest {
        try viewModel?.validate()
        guard let values = viewModel?.values else { throw AdditionalGuestFormError.missingValues }

        return try AdditionalGuest(dictionary: values)
    }

    func showErrorMessage(title: String, error: Error) {
        showErrorAlertWith(title: title, error: error)
    }

    func showErrorFor(row: FormekaModelRow) {
        scrollAndFocus(at: viewModel?.indexPath(for: row))
    }

    func selected(salutation: String) {
        viewModel?.row(named: AdditionalGuestFormRow.title.rawValue)?.value = salutation
        table?.reloadData()
    }

    func selected(nationality: Country) {
        viewModel?.row(named: AdditionalGuestFormRow.nationality.rawValue)?.value = nationality
        table?.reloadData()
    }

    func updateViewBusy(busy: Bool) {
        guard let activityIndicator = activityIndicator else { return }
        activityIndicator.center = table.center
        activityIndicator.move(to: .front)

        if busy {
            activityIndicator.startAnimating()
        } else {
            activityIndicator.stopAnimating()
        }
        view.isUserInteractionEnabled = !busy
    }
}

extension AdditionalGuestFormView: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        eventHandler?.submitForm()
    }
}
