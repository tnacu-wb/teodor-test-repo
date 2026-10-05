//
//  File.swift
//  Formeka
//
//  Created by Raiu, George Marius (Cognizant) on 05.03.2025.
//  Copyright © 2025 Marcello Mascia. All rights reserved.
//
import UIKit

public protocol FormekaDateCellDelegate: AnyObject {
    func didPickDate(date: Date, cell: FormekaDateCell)
}

extension FormekaViewController: FormekaDateCellDelegate {
    public func didPickDate(date: Date, cell: FormekaDateCell) {
        guard let indexPath = table.indexPath(for: cell) else { return }
        guard let row = viewModel?.row(at: indexPath) else { return }

        row.value = date
        try? row.validateInline()
        cell.errorMessage = row.error?.localizedDescription
        cell.valueChanged?(date)
        table.beginUpdates()
        table.endUpdates()
    }
}
