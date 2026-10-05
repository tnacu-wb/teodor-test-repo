//
//  GuestDetailsViewBlueprint.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class GuestDetailsVC: UIViewController, GuestDetailsViewBlueprint {
    private var screenName: String { PIAnalytics.StateNames.checkInOnlineGuestDetailsPage }
    private var screenType: String { PIAnalytics.StateTypes.ciolFlow }
    private var customParameters: [String: Any]? { presenter?.customAnalyticsParameters }
    var customAnalyticsParameters: PIDictionary?

    var presenter: (any GuestDetailsPresenterBlueprint)?
    private var viewModel: FormekaViewModel?

    private(set) var priceBreakdownView: PriceBreakdownView?

    private lazy var tableView: UITableView = {
        let tableView = UITableView(frame: .zero, style: .grouped)
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.separatorStyle = .none
        tableView.showsVerticalScrollIndicator = false
        tableView.rowHeight = UITableView.automaticDimension
        tableView.backgroundColor = .white
        tableView.register(GuestCell.self, forCellReuseIdentifier: GuestCell.reuseIdentifier)
        tableView.register(GuestAddCell.self, forCellReuseIdentifier: GuestAddCell.reuseIdentifier)
        return tableView
    }()

    private lazy var activityIndicator: UIActivityIndicatorView = {
        let ai = UIActivityIndicatorView(style: .large)
        ai.hidesWhenStopped = true
        ai.stopAnimating()
        ai.translatesAutoresizingMaskIntoConstraints = false
        return ai
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        title = PILocalizedString("guestDetailsVCTitle")
        setupViews()
        setupPriceBreakdown()
        presenter?.viewIsReady()
        trackScreenAnalytics()
    }

    func update(with: [Guest], priceVM: CIOLPriceBreakdownViewModelProtocol?) {
        viewModel = tableViewModel(with: with)
        if let priceVM {
            priceBreakdownView?.customise(with: priceVM)
        }

        tableView.delegate = viewModel
        tableView.dataSource = viewModel
        tableView.reloadData()
    }

    private func setupViews() {
        view.backgroundColor = .white
        view.addSubview(tableView)
        view.addSubview(activityIndicator)
        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.topAnchor, constant: 24),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 8),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -8),

            activityIndicator.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            activityIndicator.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ])
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

    private func setupPriceBreakdown() {
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

    func edit(index: Int) {
        presenter?.edit(with: index)
    }

    func updateBalance(priceVM: CIOLPriceBreakdownViewModelProtocol) {
        priceBreakdownView?.customise(with: priceVM)
    }

    private func trackScreenAnalytics() {
        guard var analyticsData = customParameters else { return }
        analyticsData[PIAnalytics.Keys.screenType] = screenType

        AnalyticsManager.shared.trackState(screenName, data: analyticsData)
    }
}

extension GuestDetailsVC: PriceBreakdownViewDelegate {
    func buttonDidTap() {
        presenter?.handleContinueButtonTap()
    }
}
