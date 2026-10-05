//
//  CiolReviewAndPayViewController.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

final class CiolReviewAndPayViewController: FormekaViewController {
    private let confirmationPollingMessagesConfig: ConfirmationPollingMessagesConfig? = SettingsManager.sharedInstance
        .confirmationPollingMessagesConfig

    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!

    private(set) var priceBreakdownView: PriceBreakdownView?
    private var loadingView: LoadingView?

    var eventHandler: ReviewAndPayViewEventHandler?

    override var screenName: String { PIAnalytics.StateNames.ciolPayment }
    override var screenType: String { PIAnalytics.StateTypes.ciolFlow }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    override func viewDidLoad() {
        super.viewDidLoad()

        title = PILocalizedString("ciolPayAndCheck-in")
        setupPriceBreakdown()
        eventHandler?.viewIsReady()

        if table != nil {
            table.estimatedRowHeight = 70
            table.rowHeight = UITableView.automaticDimension
            table.backgroundColor = .ColourLD5
            table.sectionHeaderTopPadding = 0

            table.registerCellNib(with: BookingSummaryCIOLCell.self)
            table.registerCellNib(with: DynamicListCell.self)
            table.registerCellNib(with: CiolReviewAndPayBillingAddressCell.self)
            table.registerCellNib(with: FlexibleContentInformationCell.self)
            table.register(EditDetailsCell.self, forCellReuseIdentifier: EditDetailsCell.reuseIdentifier)
        }
    }

    override func willMove(toParent parent: UIViewController?) {
        super.willMove(toParent: parent)
        if parent == nil {
            eventHandler?.didPop()
        }
    }

    override func textFieldDidUpdateContent(value: String, cell: FormekaTextFieldCell) {
        guard let indexPath = table.indexPath(for: cell) else { return }
        guard let row = viewModel?.row(at: indexPath) else { return }

        row.value = value
        cell.valueChanged?()
    }

    private func setupPriceBreakdown() {
        guard let view: PriceBreakdownView = .fromNib() else { return }

        priceBreakdownView = view

        guard let priceBreakdownView = priceBreakdownView else { return }

        priceBreakdownView.delegate = self

        self.view.addSubview(priceBreakdownView)
        priceBreakdownView.translatesAutoresizingMaskIntoConstraints = false
        priceBreakdownView.topAnchor.constraint(equalTo: table.bottomAnchor).isActive = true
        priceBreakdownView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor).isActive = true
        priceBreakdownView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor).isActive = true
        priceBreakdownView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor).isActive = true
    }
}

extension CiolReviewAndPayViewController: PriceBreakdownViewDelegate {
    func buttonDidTap() {
        do {
            try viewModel?.validate()
            eventHandler?.handlePayButtonTap()
        } catch {
            if let error = error as? RowValidatorError,
               let indexPath = viewModel?.indexPath(for: error.row),
               var cell = table.cellForRow(at: indexPath) as? FormekaErrorCell {
                cell.errorMessage = error.localizedDescription
                table.beginUpdates()
                table.endUpdates()
                return
            }
        }
    }

    func toggleButtonDidTap() {
        eventHandler?.trackPriceBreakdownTapAnalytics()
    }
}

extension CiolReviewAndPayViewController: CiolReviewAndPayViewProtocol {
    func showLoadingIndicator() {
        table.isUserInteractionEnabled = false
        table.alpha = 0
        activityIndicator.startAnimating()
    }

    func hideLoadingIndicator() {
        table.isUserInteractionEnabled = true
        table.alpha = 1
        activityIndicator.stopAnimating()
    }

    func loadPriceBreakdown(with priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol) {
        priceBreakdownView?.customise(with: priceBreakdownViewModel)
    }

    func reloadData(with ciolPayAndReviewViewModel: CiolReviewAndPayViewModel) {
        viewModel = tableViewModel(with: ciolPayAndReviewViewModel)

        guard table != nil else { return }

        table.delegate = viewModel
        table.dataSource = viewModel
        table.reloadData()
    }

    func showError(title: String, message: String?, shouldDie: Bool) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller
            .addAction(UIAlertAction(
                title: PILocalizedString("OK", comment: "OK button title"),
                style: .cancel
            ) { [weak self] _ in
            if shouldDie {
                self?.navigationController?.popViewController(animated: true)
            }
        })

        present(controller, animated: true)
    }

    func showError(title: String, message: String?, action: @escaping (() -> Void)) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(title: PILocalizedString("OK", comment: "OK button title"), style: .cancel) { _ in
            action()
        })

        present(controller, animated: true)
    }

    func startDisplayingActionLoadingElements() {
        priceBreakdownView?.activityIndicator.startAnimating()
        priceBreakdownView?.continueButton.isUserInteractionEnabled = false
        table.isUserInteractionEnabled = false
        table.alpha = 0.5
    }

    func stopDisplayingActionLoadingElements() {
        priceBreakdownView?.activityIndicator.stopAnimating()
        priceBreakdownView?.continueButton.isUserInteractionEnabled = true
        table.isUserInteractionEnabled = true
        table.alpha = 1.0
    }
}
