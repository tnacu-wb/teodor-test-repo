//
//  RoomsUpsellView+Formeka.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 14.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension RoomsUpsellViewController {
    func tableViewModel(with roomsUpsellViewModel: RoomsUpsellViewModel) -> FormekaViewModel {
        var sections = [FormekaModelSection]()
        sections.append(rooms(with: roomsUpsellViewModel))

        return FormekaViewModel(sections: sections)
    }

    private func rooms(with roomsViewModel: RoomsUpsellViewModel) -> FormekaModelSection {
        let rows = roomsViewModel.rooms.map { room in
            FormekaModelRow(cellSetup: { index, _, table in
                guard let cell = table
                      .dequeueReusableCell(withIdentifier: RoomUpsellCell.reuseIdentifier) as? RoomUpsellCell,
                      let setup = self.getRoomCellConfig(roomID: room.id) else {
                    return UITableViewCell()
                }
                let children = String.localizedStringWithFormat(
                    PILocalizedString(
                        "%d child(children)",
                        comment: "Message shown for number of children"
                    ),
                    room.numberOfChildren
                )
                var guests = room.adults
                if room.hasChildren { guests.append(children) }
                cell.configure(
                    roomNumber: index.row,
                    guests: guests,
                    addedDescriptions: room.addedDescriptions,
                    config: setup
                )
                return cell
            }, didSelect: { [weak self] _, _ in
                let config = self?.getRoomCellConfig(roomID: room.id)
                guard config?.enabled == true else { return }
                self?.eventHandler?.showUpsellDetails(room: room)
            })
        }

        let header = FormekaModelHeaderFooter(height: 40, viewSetup: { _, _ in
            let headerView = UITableViewHeaderFooterView()
            let label = UILabel()
            label.text = PILocalizedString("ciolSelectRoomHeader")
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
        return FormekaModelSection(header: header, rows: rows, footer: nil)
    }
}
