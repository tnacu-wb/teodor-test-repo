//
//  CheckInOnlineGuestDetailsView.swift
//  PremierInn
//
//  Created by Freddie Parks on 17/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

protocol CheckInOnlineGuestDetailsViewFormRowModel {
    var title: String { get }
    var value: Any? { get }
    var placeholder: String { get }
}

protocol CheckInOnlineGuestDetailsViewModel {
    var titleRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }
    var firstNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }
    var lastNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }
    var emailRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }
    var contactNumberRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }
    var nationalityRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }
    var passportNumberRowModel: CheckInOnlineGuestDetailsViewFormRowModel? { get }
    var nextDestinationRowModel: CheckInOnlineGuestDetailsViewFormRowModel? { get }
    var additionalGuestTitleRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }
    var additionalGuestFirstNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }
    var additionalGuestLastNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }

    var useBookerAddress: Bool { get }
    var bookerAddressSummary: String { get }
    var address: Address? { get }

    var carRegistrationRowModel: CheckInOnlineGuestDetailsViewFormRowModel { get }

    var shouldShowAdditionalGuestRows: Bool { get }

    var shouldShowPassportAndNextDestinationRows: Bool { get }
    var carRegistrationSectionTitle: String { get }
    var carRegistrationSectionDescription: String { get }
}

struct CheckInOnlineGuestDetailsViewOutput {
    let title: String
    let firstName: String
    let lastName: String
    let email: String
    let contactNumber: String
    let nationality: Any
    let passportNumber: String?
    let nextDestination: String?
    let carRegistration: String?

    let useBookerAddress: Bool
    let addressLine1: String?
    let addressLine2: String?
    let addressLine3: String?
    let country: Country?
    let postcode: String?

    let additionalGuestTitle: String?
    let additionalGuestFirstName: String?
    let additionalGuestLastName: String?
}

protocol CheckInOnlineGuestDetailsViewProtocol {
    func update(with title: String)
    func update(with formViewModel: CheckInOnlineGuestDetailsViewModel)
    func update(nationalityWith formViewModel: CheckInOnlineGuestDetailsViewModel)
    func update(title: String)
}

protocol CheckInOnlineGuestDetailsViewEventHandler: AnyObject {
    func viewIsReady()
    func cancelDidTap()
    func guestDetailsDidComplete(with output: CheckInOnlineGuestDetailsViewOutput)
    func nationalityRowDidTap()
    func titleRowDidTap()
}

class CheckInOnlineGuestDetailsView: FormekaViewController {
    override var trackScreen: Bool { false }

    var eventHandler: CheckInOnlineGuestDetailsViewEventHandler?
    var addressSectionView: AddressSectionView?

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.backgroundColor = .whiteTwo
            registerTableElements()
        }

        eventHandler?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: animated)

        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(cancelButtonDidTap)
        )
    }

    private func registerTableElements() {
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaPostCodeCell.self)
        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerCellNib(with: SimpleSubtitleCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: SwitchCell.self)
    }

    @objc private func cancelButtonDidTap() {
        eventHandler?.cancelDidTap()
    }
}

extension CheckInOnlineGuestDetailsView: CheckInOnlineGuestDetailsViewProtocol {
    func update(with title: String) {
        navigationItem.title = title
    }

    func update(with formViewModel: CheckInOnlineGuestDetailsViewModel) {
        viewModel = FormekaViewModel(sections: viewModelSection(with: formViewModel))

        table.delegate = viewModel
        table.dataSource = viewModel
        table.reloadData()
    }

    func update(nationalityWith formViewModel: CheckInOnlineGuestDetailsViewModel) {
        viewModel?.row(named: CheckInOnlineGuestRows.nationality.rawValue)?
            .value = (formViewModel.nationalityRowModel.value as? FormekaValue)
        if formViewModel.shouldShowPassportAndNextDestinationRows {
            addAdditionalImmigrationRowsIfAbsent(with: formViewModel)
        } else {
            removeAdditionalImmigrationRowsIfPresent()
        }

        table.reloadData()
    }

    func update(title: String) {
        viewModel?.row(named: CheckInOnlineGuestRows.title.rawValue)?.value = title

        table.reloadData()
    }
}
