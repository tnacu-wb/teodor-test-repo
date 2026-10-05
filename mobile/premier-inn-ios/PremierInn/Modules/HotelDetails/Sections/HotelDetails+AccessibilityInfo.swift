//
//  HotelDetails+AccessibilityInfo.swift
//  PremierInn
//
//  Created by Freddie Parks on 30/05/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import Foundation
import UIKit

extension HotelDetailsViewController {
    func accessibilityInfoSection(with viewModel: AccessibilityInfoViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: HotelAccessibilityInfoCell = table.dequeueCell(for: indexPath) else { return nil }

            let mutableParagraphStyle = NSMutableParagraphStyle()
            mutableParagraphStyle.lineSpacing = 6

            let bulletedInfo: NSAttributedString = NSAttributedString(
                string: viewModel.hotelInfo.reduce("", { $0 + ("• " + $1 + "\n") }),
                attributes: [.paragraphStyle: mutableParagraphStyle]
            ).attributedStringByColoringBullets(with: .turquoise)
// What color
            cell.hotelInfo.attributedText = bulletedInfo
            cell.contactInfo.attributedText = NSAttributedString(
                string: viewModel.callInfo,
                attributes: [.paragraphStyle: mutableParagraphStyle]
            )
            cell.callButton.setTitle(viewModel.callButtonTitle, for: .normal)
            cell.emailButton.setTitle(viewModel.emailButtonTitle, for: .normal)

            cell.delegate = self

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: accessFooter)
    }

    private var accessFooter: FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 10, viewSetup: { _, table in
            let view: SimpleFooter? = table.headerFooterView()
            view?.contentView.backgroundColor = .TintL5
            view?.lineView.backgroundColor = .TintL2

            return view
        })
    }
}

extension HotelDetailsViewController: HotelAccessibilityInfoCellDelegate {
    func callButtonDidTap() {
        eventHandler.callHotelDidTap()
    }

    func accessibleRoomsButtonDidTap() {
        eventHandler.showOurRoomsTapped(lettingType: "ABS")
    }

    func disabledAccessButtonDidTap() {
        eventHandler.selectedShowDisabledAccess()
    }

    func emailButtonDidTap() {
        eventHandler.emailButtonDidTap()
    }
}
