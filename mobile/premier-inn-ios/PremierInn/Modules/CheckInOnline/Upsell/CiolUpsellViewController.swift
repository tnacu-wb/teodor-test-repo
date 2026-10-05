//
//  CiolUpsellViewController.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class CiolUpsellViewController: BaseViewController, CiolUpsellViewProtocol {
    override var screenName: String { PIAnalytics.StateNames.ciolUpsells }
    override var screenType: String { PIAnalytics.StateTypes.ciolFlow }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            tableView.delegate = viewModel
            tableView.dataSource = viewModel
            tableView.registerCellClass(with: CiolUpsellItemCell.self)
            tableView.registerCellNib(with: FlexibleContentInformationCell.self)
            tableView.separatorStyle = .none
        }
    }

    private(set) var priceBreakdownView: PriceBreakdownView?

    var eventHandler: CiolUpsellViewEventHandler?

    private var viewModel: FormekaViewModel?

    override func viewDidLoad() {
        super.viewDidLoad()
        setupPriceBreakdown()
        eventHandler?.viewIsReady()
        self.title = PILocalizedString("ciolUpsellsTitle")
        hideLoadingIndicator()
    }

    func reloadData(viewModel: CiolUpsellViewModelProtocol) {
        guard let priceModel = viewModel.displayedPriceBreakdownModel else { return }
        priceBreakdownView?.customise(with: priceModel)
        self.viewModel = tableViewModel(with: viewModel)
        tableView.delegate = self.viewModel
        tableView.dataSource = self.viewModel
        tableView.reloadData()
    }

    private  func setupPriceBreakdown() {
        guard let view: PriceBreakdownView = .fromNib() else { return }

        priceBreakdownView = view

        guard let priceBreakdownView = priceBreakdownView else { return }

        priceBreakdownView.delegate = self

        self.view.addSubview(priceBreakdownView)
        priceBreakdownView.translatesAutoresizingMaskIntoConstraints = false
        priceBreakdownView.topAnchor.constraint(equalTo: tableView.bottomAnchor).isActive = true
        priceBreakdownView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor).isActive = true
        priceBreakdownView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor).isActive = true
        priceBreakdownView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor).isActive = true
    }

    func showLoadingIndicator() {
        tableView.isUserInteractionEnabled = false
        tableView.alpha = 0.5
        priceBreakdownView?.alpha = 0.5
        priceBreakdownView?.isUserInteractionEnabled = false
        activityIndicator.startAnimating()
    }

    func hideLoadingIndicator() {
        tableView.isUserInteractionEnabled = true
        tableView.alpha = 1
        priceBreakdownView?.alpha = 1
        priceBreakdownView?.isUserInteractionEnabled = true
        activityIndicator.stopAnimating()
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
}

extension CiolUpsellViewController: PriceBreakdownViewDelegate {
    func toggleButtonDidTap() {
        eventHandler?.trackPriceBreakdownTapAnalytics()
    }

    func buttonDidTap() {
        eventHandler?.handleContinueButtonTap()
    }
}
