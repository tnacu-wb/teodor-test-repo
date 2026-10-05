//
//  RoomsUpsellViewController.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 14.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class RoomsUpsellViewController: BaseViewController {
    private var tableView: UITableView = {
        let tableView = UITableView(frame: .zero, style: .grouped)
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.separatorStyle = .none
        tableView.backgroundColor = .white
        tableView.register(RoomUpsellCell.self, forCellReuseIdentifier: RoomUpsellCell.reuseIdentifier)
        return tableView
    }()

    var eventHandler: RoomsUpsellModuleEventHandler?

    private var viewModel: FormekaViewModel?

    private(set) var priceBreakdownView: PriceBreakdownView?

    override var screenName: String { PIAnalytics.StateNames.ciolSelectRoom }
    override var screenType: String { PIAnalytics.StateTypes.ciolFlow }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    override func viewDidLoad() {
        super.viewDidLoad()
        title = PILocalizedString("ciolUpsellsTitle")

        setupViews()
        setupConstraints()

        setupPriceBreakdown()
        eventHandler?.viewIsReady()
    }

    override func willMove(toParent parent: UIViewController?) {
        super.willMove(toParent: parent)
        if parent == nil {
            eventHandler?.updateOutput()
        }
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

    private func setupViews() {
        view.addSubview(tableView)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.topAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
    }

    func getRoomCellConfig(roomID: String) -> CiolUpsellCellSetup? {
        eventHandler?.getRoomCellConfig(roomID: roomID)
    }
}

extension RoomsUpsellViewController: RoomsUpsellViewProtocol {
    func loadData(with roomsUpsellViewModel: RoomsUpsellViewModel) {
        viewModel = tableViewModel(with: roomsUpsellViewModel)
        if let priceBreakdownViewModel = roomsUpsellViewModel.displayedPriceBreakdownModel {
            priceBreakdownView?.customise(with: priceBreakdownViewModel)
        }

        tableView.delegate = viewModel
        tableView.dataSource = viewModel
        tableView.reloadData()
    }
}

extension RoomsUpsellViewController: PriceBreakdownViewDelegate {
    func buttonDidTap() {
        eventHandler?.handleContinueButtonTap()
    }
}
