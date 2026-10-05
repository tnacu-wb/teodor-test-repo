//
//  AdditionalInfoViewModels.swift
//  PremierInn
//
//  Created by Marcello Mascia on 20/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol AdditionalInfoViewProtocol: UITableViewDataSource {
    func registerCellsForTable(_ table: UITableView)
}

final class AdditionalInfoViewModel: NSObject, AdditionalInfoViewProtocol {
    private var hotel: Hotel
    var table: UITableView?

    init(hotel: Hotel) {
        self.hotel = hotel
    }

    func registerCellsForTable(_ table: UITableView) {
        table.registerCellNib(with: HotelTitleCell.self)
        table.registerCellNib(with: HotelNotesCell.self)
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let count = hotel.notes?.count ?? 0

        return count + 1
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.row {
        case 0:
            if let cell: HotelTitleCell = tableView.dequeueCell(for: indexPath) {
                cell.hotelName.text = PILocalizedString(
                    "importantHotelInfoCellTitle",
                    comment: "Important hotel information title"
                )

                return cell
            }

        default:
            if let cell: HotelNotesCell = tableView.dequeueCell(for: indexPath) {
                let index = indexPath.row - 1

                if let notes = hotel.notes, index < notes.count {
                    cell.noteLabel.text = hotel.notes?[index].text
                }

                return cell
            }
        }

        return UITableViewCell()
    }
}

final class AdditionalLocationInfoViewModel: NSObject, AdditionalInfoViewProtocol {
    private var hotel: Hotel

    init(hotel: Hotel) {
        self.hotel = hotel
    }

    func registerCellsForTable(_ table: UITableView) {
        table.registerCellNib(with: HotelTitleCell.self)
        table.registerCellNib(with: HotelDescriptionCell.self)
        table.registerCellNib(with: HotelDetailItemCell.self)
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        3
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.row {
        case 0:
            if let cell: HotelTitleCell = tableView.dequeueCell(for: indexPath) {
                cell.hotelName.text = hotel.name
                return cell
            }

        case 1:
            if let cell: HotelDescriptionCell = tableView.dequeueCell(for: indexPath) {
                cell.hotelDescription.attributedText = NSAttributedString.attributedStringWith(
                    text: hotel.hotelDescription,
                    lineSpacing: 7,
                    font: UIFont.premierInn(ofSize: 15),
                    textColor: .slate,
                    textAlignment: .justified
                )

                return cell
            }

        case 2:
            if let cell: HotelDetailItemCell = tableView.dequeueCell(for: indexPath) {
                cell.hotelDetailTitle.text = PILocalizedString("directionCellTitle", comment: "Directions cell title")
                cell.hotelDetailDescription.attributedText = NSAttributedString.attributedStringWith(
                    text: hotel.directions,
                    lineSpacing: 7,
                    font: UIFont.premierInn(ofSize: 15),
                    textColor: .slate
                )

                return cell
            }

        default:
            break
        }

        return UITableViewCell()
    }
}
