//
//  AccountView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 19/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol AccountViewEventHandler {
    func viewIsReady()
    func viewDidAppear()
    func myDetailsButtonDidTap()
    func paymentMethodsButtonDidTap()
    func changePasswordButtonDidTap()
    func bookingPreferencesButtonDidTap()
    func newsletterUpdatesButtonDidTap()
    func loginButtonDidTap()
    func registerButtonDidTap()
    func logoutButtonDidTap()
}

class AccountViewController: BaseViewController {
    // MARK: - Views

    @IBOutlet var tableView: UITableView! {
        didSet {
            tableView.estimatedRowHeight = 125
            tableView.rowHeight = UITableView.automaticDimension
            tableView.backgroundColor = .BaseWhite
            tableView.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: CGFloat.leastNormalMagnitude,
                height: CGFloat.leastNormalMagnitude
            ))
            tableView.tableFooterView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: CGFloat.leastNormalMagnitude,
                height: CGFloat.leastNormalMagnitude
            ))
            tableView.accessibilityIdentifier = AccessibilityIdentifiers.Account.tableView
            tableView.separatorStyle = .none

            tableView.registerCellNib(with: AccountLogoutCell.self)
            tableView.registerCellNib(with: SimpleActionCell.self)
            tableView.registerCellNib(with: SwitchCell.self)
            tableView.registerCellNib(with: ContentCell.self)
            tableView.registerCellNib(with: SimpleButtonCell.self)
            tableView.registerCellNib(with: ImageWithTextCell.self)
            tableView.registerCellClass(with: BottomBorderCell.self)
            tableView.register(UITableViewCell.self, forCellReuseIdentifier: String(describing: UITableViewCell.self))
        }
    }

    // MARK: - Properties

    override var screenName: String { PIAnalytics.StateNames.myAccount }
    override var screenType: String { PIAnalytics.StateTypes.myPI }

    var eventHandler: AccountViewEventHandler?

    private var viewModel: FormekaViewModel?

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        title = PILocalizedString("myAccountScreenTitle", comment: "My account screen title")

        eventHandler?.viewIsReady()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        DispatchQueue.main.async {
            self.eventHandler?.viewDidAppear()
        }
    }
}

extension AccountViewController: AccountViewProtocol {
    func reloadData(with accountViewModel: AccountViewModel) {
        viewModel = tableViewModel(with: accountViewModel)

        guard tableView != nil else { return }

        tableView.delegate = viewModel
        tableView.dataSource = viewModel
        tableView.reloadData()
    }

    func scrollToTop() {
        guard tableView != nil else { return }
        guard tableView.cellForRow(at: IndexPath(row: 0, section: 0)) != nil else { return }

        tableView.scrollToRow(at: IndexPath(row: 0, section: 0), at: .top, animated: true)
    }

    func showSuccessConfirmation(withMessage message: String) {
        SuccessBanner(withMessage: message, on: view).show()
        NotificationFeedbackManager.shared.provideFeedback(for: .success)
    }
}
