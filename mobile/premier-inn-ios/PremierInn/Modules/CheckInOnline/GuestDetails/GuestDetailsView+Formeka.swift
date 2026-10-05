//
//  GuestDetailsVC+Formeka.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension GuestDetailsVC {
   func tableViewModel(with guests: [Guest]) -> FormekaViewModel {
       let sections = guests.map { guestSection(with: $0) }
       return FormekaViewModel(sections: sections)
   }

   func guestSection(with guest: Guest) -> FormekaModelSection {
       let rows = guest.displayedFields.map { field in
           FormekaModelRow(cellSetup: { indexPath, _, table in
               if field == .add {
                   guard let cell: GuestAddCell = table.dequeueCell(for: indexPath) else { return nil }
                   cell.isUserInteractionEnabled = true
                   cell.selectionStyle = .none
                   cell.configure(status: guest.status)
                   return cell
               } else {
                   guard let cell: GuestCell = table.dequeueCell(for: indexPath),
                         let value = guest.value(for: field) else { return nil }

                   cell.configure(field: field.rawValue, value: value)
                   cell.isUserInteractionEnabled = false
                   return cell
               }
           }, didSelect: { [weak self] index, _ in
               self?.edit(index: index.section)
           })
       }

       let header = FormekaModelHeaderFooter(height: 40, viewSetup: { _, _ in
           let headerView = UITableViewHeaderFooterView()
           let label = UILabel()
           label.text = PILocalizedString(guest.type.rawValue)
           label.font = UIFont.Heading2_ExtraBold()
           label.textColor = .BaseBlack
           label.translatesAutoresizingMaskIntoConstraints = false

           headerView.contentView.addSubview(label)
           NSLayoutConstraint.activate([
               label.leadingAnchor.constraint(equalTo: headerView.contentView.leadingAnchor, constant: 16),
               label.trailingAnchor.constraint(equalTo: headerView.contentView.trailingAnchor, constant: -16),
               label.centerYAnchor.constraint(equalTo: headerView.contentView.centerYAnchor)
           ])

           return headerView
       })
       return FormekaModelSection(header: header, rows: rows, footer: footer(height: 20))
   }

   private func footer(height: CGFloat) -> FormekaModelHeaderFooter {
       FormekaModelHeaderFooter(height: height, viewSetup: { _, _ in
           let view = UITableViewHeaderFooterView(frame: .zero)
           view.contentView.backgroundColor = .white

           return view
       })
   }
}
