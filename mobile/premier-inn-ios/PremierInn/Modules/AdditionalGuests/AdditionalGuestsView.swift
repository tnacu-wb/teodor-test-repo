//
//  AdditionalGuestsView.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka

struct AdditionalGuestListViewModel {
    let fullName: String
    let email: String
}

protocol AdditionalGuestsViewProtocol: AnyObject {
    var listViewModels: [AdditionalGuestListViewModel]? { get set }

    func setTitle(title: String)
    func loadViewModel()
    func showOptionAlert(
        withTitle: String?,
        message: String?,
        cancelTitle: String,
        confirmTitle: String,
        completion: @escaping () -> Void
    )
    func showErrorMessage(title: String, error: Error)
    func updateViewBusy(busy: Bool)
}

protocol AdditionalGuestsViewEventHandler {
    var additionalGuestsTracking: AdditionalGuestsTracking { get }

    func viewIsReady()
    func selectedAddGuest()
    func selectedEditGuest(atIndex index: Int)
    func selectedDeleteGuest(atIndex index: Int)
}

class AdditionalGuestsView: FormekaViewController {
    var listViewModels: [AdditionalGuestListViewModel]? {
        didSet {
            loadViewModel()
        }
    }
    var eventHandler: AdditionalGuestsViewEventHandler?
    var activityIndicator: UIActivityIndicatorView?

    override var screenName: String {
        eventHandler?.additionalGuestsTracking.screenName ?? super.screenName
    }
    override var screenType: String {
        eventHandler?.additionalGuestsTracking.screenType ?? super.screenType
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        activityIndicator = UIActivityIndicatorView(style: .medium)
        activityIndicator?.hidesWhenStopped = true

        if table != nil {
            table.backgroundColor = .whiteTwo
            table.separatorColor = .ColourLD3

            registerTableElements()

            activityIndicator?.center = table.center
        }

        if let activityIndicator = activityIndicator {
            view.addSubview(activityIndicator)
        }

        eventHandler?.viewIsReady()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        eventHandler?.viewIsReady()
    }

    private func registerTableElements() {
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: AccountLogoutCell.self)
        table.registerCellNib(with: SimpleActionCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
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

extension AdditionalGuestsView: AdditionalGuestsViewProtocol {
    func setTitle(title: String) {
        navigationItem.title = title
    }

    func loadViewModel() {
        viewModel = FormekaViewModel(sections: viewModelSections(guests: listViewModels))
        viewModel?.delegate = self

        table?.delegate = viewModel
        table?.dataSource = viewModel
        table?.reloadData()
    }

    func showOptionAlert(
        withTitle: String?,
        message: String?,
        cancelTitle: String,
        confirmTitle: String,
        completion: @escaping () -> Void
    ) {
        let alertController = UIAlertController(title: withTitle, message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: cancelTitle, style: .cancel, handler: nil))
        alertController.addAction(UIAlertAction(title: confirmTitle, style: .default, handler: { _ in
            completion()
        }))

        present(alertController, animated: true)
    }

    func showErrorMessage(title: String, error: Error) {
        showErrorAlertWith(title: title, error: error)
    }
}
