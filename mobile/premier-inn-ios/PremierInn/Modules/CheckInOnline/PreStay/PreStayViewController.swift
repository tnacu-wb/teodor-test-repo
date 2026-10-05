//
//  PreStayViewController.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class PreStayViewController: BaseViewController {
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            tableView.estimatedRowHeight = 70
            tableView.rowHeight = UITableView.automaticDimension
            tableView.backgroundColor = .ColourLD5
            tableView.sectionHeaderTopPadding = 0

            tableView.registerCellNib(with: BookingSummaryCIOLCell.self)
            tableView.registerCellNib(with: PreStayGuestDetailsCell.self)
            tableView.registerCellNib(with: SimpleActionCell.self)
            tableView.registerCellNib(with: PreStayAddressCell.self)
            tableView.registerCellNib(with: PreStayRoomDetailsCell.self)
            tableView.registerCellNib(with: SpecialOccasionCell.self)
        }
    }

    private(set) var priceBreakdownView: PriceBreakdownView?

    var eventHandler: PreStayViewEventHandler?
    private var viewModel: FormekaViewModel?

    override var screenName: String { PIAnalytics.StateNames.ciolPreStay }
    override var screenType: String { PIAnalytics.StateTypes.ciolFlow }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    override func viewDidLoad() {
        super.viewDidLoad()

        title = PILocalizedString("confirmDetailsPreStay")
        setupPriceBreakdown()
        eventHandler?.viewIsReady()
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
}

extension PreStayViewController: PreStayViewProtocol {
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

    func reloadData(with preStayViewModel: PreStayViewModel) {
        viewModel = tableViewModel(with: preStayViewModel)
        priceBreakdownView?.customise(with: preStayViewModel.priceBreakdownViewModel)

        guard tableView != nil else { return }

        tableView.delegate = viewModel
        tableView.dataSource = viewModel
        tableView.reloadData()
    }

    func reloadPriceBreakdown(priceModel: CIOLPriceBreakdownViewModelProtocol) {
        priceBreakdownView?.customise(with: priceModel)
    }

    func showError(title: String, message: String?, shouldDie: Bool) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(title: PILocalizedString("OK"), style: .cancel) { [weak self] _ in
            if shouldDie {
                self?.navigationController?.popViewController(animated: true)
            }
        })

        present(controller, animated: true)
    }
}

extension PreStayViewController: EditDetailsViewDelegate {
    func didUpdateUserDetails(with editDetailsModel: EditDetailsModel) {
        eventHandler?.updateViewModel(with: editDetailsModel)
    }
}

extension PreStayViewController: PriceBreakdownViewDelegate {
    func buttonDidTap() {
        eventHandler?.handleContinueButtonTap()
    }
}
