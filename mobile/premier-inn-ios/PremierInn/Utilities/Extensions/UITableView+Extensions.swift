//
//  UITableView+Extensions.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 21/04/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

extension UITableView {
    func scrollToLastRow(animated: Bool = true) {
        let lastSection = numberOfSections - 1
        guard lastSection >= 0 else { return }

        let lastRow = numberOfRows(inSection: lastSection) - 1
        guard lastRow >= 0 else { return }

        scrollToRow(
            at: IndexPath(row: lastRow, section: lastSection),
            at: .bottom,
            animated: animated
        )
    }
}
