//
//  PaymentDetailsView.swift
//  PremierInn
//
//
//  Created Freddie Parks on 07/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//
//

import UIKit
import Formeka

protocol PaymentDetailsViewModel {
    var infoMessage: String? { get }
    var cardViewModel: PaymentCardSummaryViewModel { get }
    var confirmationViewModel: PaymentDetailsConfirmationViewModel { get }
}

protocol PaymentCardCVVModel {
    var cvvLength: Int { get }
    var cvvInputHelperDescription: String { get }
}

protocol PaymentCardSummaryViewModel {
    var sectionTitle: String? { get }
    var canChangeCard: Bool { get }
    var cardTitle: String { get }
    var cardType: String { get }
    var cardIconUrl: URL? { get }
    var cardNumberDescription: String { get }
    var cvvModel: PaymentCardCVVModel? { get }
    var showCardAuthenticationMessage: Bool { get }
}

protocol PaymentDetailsConfirmationViewModel {
    var termsMessage: NSAttributedString? { get }
    var bookingManagementTermsMessage: NSAttributedString? { get }
    var totalLabel: String { get }
    var total: String { get }
    var ctaTitle: String { get }
    var ctaIcon: UIImage? { get }
}

class PaymentDetailsView: FormekaViewController {
    var eventHandler: PaymentDetailsViewEventHandler?

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.title = PILocalizedString("Payment details", comment: "")

        if table != nil {
            table.separatorStyle = .none

            registerTableElements()
        }

        eventHandler?.viewIsReady()
    }

    private func registerTableElements() {
        table.registerCellNib(with: PaymentCardCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: BookingReviewSubmitCell.self)
        table.registerCellNib(with: BookingReviewTotalPriceCell.self)
        table.registerCellNib(with: CVVInputCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerHeaderFooterNib(with: ActionableHeader.self)
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)
    }

    @objc func ctaDidTap() {
        eventHandler?.payAndCheckInDidTap(with: viewModel?.row(named: ReviewAndBookRow.cvv.rawValue)?.value as? String)
    }
}

extension PaymentDetailsView: PaymentDetailsViewProtocol {
    func update(with viewModel: PaymentDetailsViewModel) {
        self.viewModel = self.formekaViewModel(with: viewModel)

        table.delegate = self.viewModel
        table.dataSource = self.viewModel
        table.reloadData()
    }

    func toggleLoadingIndicator(should show: Bool) {
        if show {
            view.showLoadingView()
        } else {
            view.hideLoadingView()
        }
    }

    func showError(title: String, message: String?) {
        showAlertWith(title: title, message: message)
    }
}
