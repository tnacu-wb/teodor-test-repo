//
//  UserPreferencesView.swift
//  PremierInn
//
//  Created by Nick Jones on 30/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import Formeka

protocol UserPreferencesViewInput: AnyObject {
    func viewIsReady()
    func refreshRows()
    func set(title: String?)
}

class UserPreferencesView: BaseViewController {
    var showUpdateMessageOnDisplay = false

    var presenter: UserPreferencesPresenterInput?

    override var screenName: String {
        presenter?.userPreferencesTracking.screenName ?? super.screenName
    }
    override var screenType: String {
        presenter?.userPreferencesTracking.screenType ?? super.screenType
    }

    @IBOutlet weak var table: UITableView! {
        didSet {
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 100
            table.sectionHeaderHeight = .leastNormalMagnitude
            table.sectionFooterHeight = .leastNormalMagnitude
            table.backgroundColor = .BaseWhite
            table.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: .leastNormalMagnitude
            ))
            table.tableFooterView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: .leastNormalMagnitude
            ))

            table.registerCellNib(with: StayPreferencesCell.self)
            table.registerCellNib(with: GenericPreferencesCell.self)
            table.registerCellNib(with: FlexibleContentInformationCell.self)
            table.registerCellNib(with: FlexibleTextContentCell.self)
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .ColourLD6
        presenter?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        presenter?.reloadRows()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        if let messageToShow = presenter?.userPreferenceChangedMessage() {
            SuccessBanner(withMessage: messageToShow, on: view).show()
            NotificationFeedbackManager.shared.provideFeedback(for: .success)
        }
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)

        presenter?.viewDidDisappear()
    }

    private func errorSection(with indexPath: IndexPath) -> UITableViewCell {
        guard let table = self.table else { return UITableViewCell() }
        guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return UITableViewCell() }

        cell.hiddenSeparatorLocations = [.top, .bottom]
        cell.bottomConstraint.constant = 10
        cell.topConstraint.constant = 10

        cell.content.text = PILocalizedString(
            "userPrerenceScScreenGenericUpdateError",
            comment: "User Preferences Generic Update Error"
        )
        cell.content.textColor = .ColourDL1
        cell.content.font = .BodySmall()
        cell.content.backgroundColor = .clear

        cell.containerView.backgroundColor = .Tint9
        cell.containerView.layer.borderColor = UIColor.Tint8.withAlphaComponent(0.4).cgColor
        cell.containerView.layer.cornerRadius = 4

        cell.icon.image = #imageLiteral(resourceName: "UNKNOWN")
        cell.icon.tintColor = .Tint8

        cell.backgroundColor = .BaseWhite
        cell.accessibilityIdentifier = "bannerCellAcc"
        return cell
    }
}

extension UserPreferencesView: UserPreferencesViewInput {
    func viewIsReady() {
        presenter?.viewIsReady()
    }

    func refreshRows() {
        table.reloadData()
    }

    func set(title: String?) {
        navigationItem.title = title
    }
}

extension UserPreferencesView: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        1
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        presenter?.numberOfRows ?? 0
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        presenter?.selectedRow(atIndex: indexPath)
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (presenter?.shouldShowUserUpdateFailedError() ?? false) && indexPath.section == 0 {
            return errorSection(with: indexPath)
        }

        guard let userPreferenceRow = presenter?.userPreferenceRow(forPath: indexPath) else { return UITableViewCell() }

        let cell = tableView.dequeueReusableCell(withIdentifier: userPreferenceRow.cellIdentifier.rawValue, for: indexPath)

        if let titleCell = cell as? FlexibleTextContentCell {
            titleCell.content.text = userPreferenceRow.title
            titleCell.hiddenSeparatorLocations = [.top, .bottom]

            return titleCell
        }

        if let stayCell = cell as? StayPreferencesCell,
           let stayRequirements = userPreferenceRow as? RoomRequirementsRow {
            stayCell.hiddenSeparatorLocations = [.top, .bottom]
            stayCell.configure(withRoomRequirementsRow: stayRequirements)
            return stayCell
        }

        if let genericCell = cell as? GenericPreferencesCell {
            genericCell.configure(withUserPreferenceRow: userPreferenceRow)
            genericCell.hiddenSeparatorLocations = [.top]
            return genericCell
        }

        return UITableViewCell()
    }
}
