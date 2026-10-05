//
//  PlanYourTripInfoView.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 16/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import MapKit
import Formeka

class PlanYourTripInfoView: BaseViewController {
    private var viewModel: PlanYourTripInfoViewModel? {
        didSet {
            hotelDidLoad()
        }
    }

    var eventHandler: PlanYourTripInfoViewEventHandler?
    var tableViewModel: FormekaViewModel?

    override var screenName: String { PIAnalytics.StateNames.planTrip }
    override var screenType: String { PIAnalytics.StateTypes.myBookings }

    @IBOutlet var tableView: UITableView! {
        didSet {
            tableView.estimatedRowHeight = 125
            tableView.rowHeight = UITableView.automaticDimension
            tableView.backgroundColor = .whiteTwo
            tableView.tableHeaderView = tableView.style == .grouped ? UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: tableView.frame.size.width,
                height: CGFloat.leastNormalMagnitude
            )) : nil

            tableView.registerCellNib(with: PlanYourTripAddressCell.self)
            tableView.registerCellNib(with: PlanYourTripGetDirectionsCell.self)
            tableView.registerCellNib(with: PlanYourTripDirectionsCell.self)
            tableView.registerCellNib(with: PlanYourTripParkingCell.self)
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!

    init() {
        super.init(nibName: "PlanYourTripInfoViewController", bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        eventHandler?.viewIsReady()

        if parent != nil {
            tableView.contentInset.bottom = topLayoutPadding
            tableView.verticalScrollIndicatorInsets.bottom = topLayoutPadding
        }
    }

    private func hotelDidLoad() {
        let section: FormekaModelSection = {
            let rows = [
                FormekaModelRow(cellSetup: { [unowned self] indexPath, _, table in
					guard let cell: PlanYourTripAddressCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.distanceLabel.text = viewModel?.title
                    cell.addressLabel.text = viewModel?.hotelAddress

                    return cell
                }),
                FormekaModelRow(cellSetup: { [unowned self] indexPath, _, table in
					guard let cell: PlanYourTripGetDirectionsCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.directionsButton.addTarget(
                        self,
                        action: #selector(directionsCellDidTap(button:)),
                        for: .touchUpInside
                    )

                    return cell
                }),
                FormekaModelRow(cellSetup: { [unowned self] indexPath, _, table in
					guard let cell: PlanYourTripDirectionsCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.directions.text = viewModel?.directions

                    return cell
                }),
                FormekaModelRow(cellSetup: { [unowned self] indexPath, _, table in
					guard let cell: PlanYourTripParkingCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.parkingInfo.text = viewModel?.hotelParking

                    return cell
                })
            ]

            return FormekaModelSection(header: nil, rows: rows, footer: nil)
        }()

        tableViewModel = FormekaViewModel(sections: [section])

        tableView.delegate = tableViewModel
        tableView.dataSource = tableViewModel

        tableView.reloadData()
    }
}

extension PlanYourTripInfoView: PlanYourTripInfoViewProtocol {
    func update(with viewModel: PlanYourTripInfoViewModel) {
        self.viewModel = viewModel
    }
}

extension PlanYourTripInfoView: OverlayProtocol {
    var controller: UIViewController { self }
    var scrollView: UIScrollView? { tableView }
    var topLayoutPadding: CGFloat { 20 }
    var collapsedHeight: CGFloat { 100 }
}

extension PlanYourTripInfoView {
    @objc func directionsCellDidTap(button: UIButton) {
        eventHandler?.openDirections(withSender: button)
    }
}
