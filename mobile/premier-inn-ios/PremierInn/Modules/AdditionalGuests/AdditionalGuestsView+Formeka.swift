//
//  AdditionalGuestsView+Formeka.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension AdditionalGuestsView {
    func viewModelSections(guests: [AdditionalGuestListViewModel]?) -> [FormekaModelSection] {
        var sections = [FormekaModelSection]()

        if guests == nil || guests?.isEmpty == true {
            sections.append(noGuestsInfoSection())
        }

        sections.append(addGuestSection())

        if let guests = guests {
            for (index, viewModel) in guests.enumerated() {
                sections.append(guestSection(guestViewModel: viewModel, index: index))
            }
        }

        return sections
    }

    private func noGuestsInfoSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(tag: "", cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            cell.content.font = UIFont.Heading3_Semibold()
            cell.content.textColor = .TintD1
            cell.content.textAlignment = .center
            cell.content.text = PILocalizedString("additionalGuestsNoGuestsTitle", comment: "")

            cell.messageTopConstraint.constant = 66

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }))

        rows.append(FormekaModelRow(tag: "", cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            cell.content.font = UIFont.Body()
            cell.content.textColor = .TintD1
            cell.content.textAlignment = .center
            cell.content.text = PILocalizedString("additionalGuestsNoGuestsDetail", comment: "")

            cell.messageTopConstraint.constant = 4

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func addGuestSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(tag: "", cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            cell.button.backgroundColor = .whiteTwo
            cell.button.setTitleColor(.BasePurple, for: .normal)
            cell.button.setTitle(PILocalizedString("addRegularGuestButtonTitle", comment: ""), for: .normal)

            cell.button.titleLabel?.font = .Button1()

            cell.button.layer.cornerRadius = 5
            cell.button.layer.borderColor = UIColor.BasePurple.cgColor
            cell.button.layer.borderWidth = 1

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func guestSection(guestViewModel: AdditionalGuestListViewModel, index: Int) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(guestDetailsRow(forTable: table, guestViewModel: guestViewModel))

        rows.append(simpleRow(forTable: table, title: PILocalizedString(
            "regularGuestEditButtonTitle",
            comment: ""
        )) { [unowned self] _, _ in
            eventHandler?.selectedEditGuest(atIndex: index)
        })

        rows.append(simpleRow(
            forTable: table,
            textColour: .strongRed,
            title: PILocalizedString("regularGuestDeleteButtonTitle", comment: "")
        ) { [unowned self] _, _ in
            eventHandler?.selectedDeleteGuest(atIndex: index)
        })

        return FormekaModelSection(header: nil, rows: rows, footer: spacerHeaderFooter(height: 8))
    }

    private func guestDetailsRow(
        forTable table: UITableView?,
        guestViewModel: AdditionalGuestListViewModel
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: AccountLogoutCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.username.text = guestViewModel.fullName
            cell.email.text = guestViewModel.email

            return cell
        }, didSelect: nil)
    }

    private func simpleRow(
        forTable table: UITableView?,
        textColour: UIColor = .BasePurple,
        title: String,
        onSelect: TableRowCellSelection?
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SimpleActionCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.textLabel?.textColor = textColour
            cell.textLabel?.text = title

            return cell
        }, didSelect: onSelect)
    }
}

extension AdditionalGuestsView: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        eventHandler?.selectedAddGuest()
    }
}
