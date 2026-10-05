//
//  KeyView+Formeka.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 05/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import Formeka

enum KeyRowType: String {
    case instructions
}

extension KeyView {
    func tableViewModel(viewModel: KeyDetailsInfoRowsDisplayable) -> FormekaViewModel {
        var sections = [FormekaModelSection]()
        if let infoRows = viewModel.infoRows {
            sections.append(keyRowSections(infoRows: infoRows))
        }

        return FormekaViewModel(sections: sections)
    }

    private func keyRowSections(infoRows: [KeyInfoCellViewModel]) -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        for row in infoRows {
            rows.append(keyInfoRow(viewModel: row))
        }
        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func keyInfoRow(viewModel: KeyInfoCellViewModel) -> FormekaModelRow {
        FormekaModelRow(
            tag: KeyRowType.instructions.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: SimpleIndicatorCell = table.dequeueCell(for: indexPath) else { return nil }
                cell.titleLabel.text = viewModel.title
                cell.messageLabel.text = viewModel.description
                cell.titleLeadingConstraint.constant = 24
                cell.messageLeadingConstraint.constant = 24
                return cell
            },
            didSelect: { [unowned self] _, _ in
            guard let instructionsModel = viewModel.instructionsModel else {
                viewModel.tapAction?()
                return
            }
            presenter?.instructionsDidTap(model: instructionsModel)
        }
        )
    }
}
