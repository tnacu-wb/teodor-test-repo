//
//  BookingReviewViewModel.swift
//  PremierInn
//
//  Created by Freddie Parks on 11/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

enum BookingReviewSection {
    case extras
    case total
}

enum BookingReviewRow {
    case breakfast
    case paymentInterval
    case total
    var cellClass: AnyClass {
        switch self {
        case .breakfast:
            return BreakfastPlaceholderCell.self
        case .paymentInterval:
            return PayNowLaterCell.self
        case .total:
            return BookingReviewTotalCell.self
        }
    }
}

struct BookingReviewStructure {
    var sectionIndex: [BookingReviewSection]
    var sections: [BookingReviewSection: [BookingReviewRow]]
}

protocol BookingReviewCellProtocol {
    func configure(withBookingDetails bookingDetails: BookingDetails, withIndexPath indexPath: IndexPath, delegate: Any)
}

enum PaymentIntervalOption {
    case arrival
    case now
}

protocol BookingReviewViewModelDelegate: class {
    func selectedPayPoint(paypoint: PaymentIntervalOption)
    func continueButtonDidTap()
    func manageCreditCardButtonDidTap()
}

class BookingReviewViewModel: NSObject {
    private var bookingDetails: BookingDetails?
    var structure: BookingReviewStructure?
    weak var delegate: BookingReviewViewModelDelegate?

    init(bookingDetails: BookingDetails) {
        self.bookingDetails = bookingDetails
        structure = BookingReviewViewModel.simpleStructure(withBookingDetails: bookingDetails)
    }

    class func simpleStructure(withBookingDetails bookingDetails: BookingDetails) -> BookingReviewStructure {
        var sections = [
            BookingReviewSection.extras: [BookingReviewRow.breakfast],
            BookingReviewSection.total: [BookingReviewRow.total]
        ]
        if bookingDetails.prepaymentRequired == false {
            sections[.total]?.insert(BookingReviewRow.paymentInterval, at: 0)
        }
        return BookingReviewStructure(sectionIndex: [.extras, .total], sections: sections)
    }
}

extension BookingReviewViewModel: UITableViewDelegate, UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        structure?.sections.count ?? 0
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard let structure = structure else { return 0 }
        return structure.sections[structure.sectionIndex[section]]?.count ?? 0
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        func structureCell() -> UITableViewCell {
            if let structure = structure, let section = structure.sections[structure.sectionIndex[indexPath.section]] {
                let row: BookingReviewRow = section[indexPath.row]
                let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: row.cellClass), for: indexPath)
                return cell
            }
            return UITableViewCell()
        }

        let cell = structureCell()

        if let bookingDetails = bookingDetails {
            (cell as? BookingReviewCellProtocol)?.configure(
                withBookingDetails: bookingDetails,
                withIndexPath: indexPath,
                delegate: self
            )
        }

        return cell
    }
}

extension BookingReviewViewModel: PayNowLaterCellDelegate {
    func payNowLaterToggleDidChange(cell: PayNowLaterCell, selectedIndex: Int) {
        delegate?.selectedPayPoint(paypoint: selectedIndex == 0 ? .arrival : .now)
    }
}

extension BookingReviewViewModel: BookingReviewTotalCellDelegate {
    func continueButtonDidTap(bookingReviewTotalCell: BookingReviewTotalCell) {
        delegate?.continueButtonDidTap()
    }

    func manageCreditCardButtonDidTap(bookingReviewTotalCell: BookingReviewTotalCell) {
        delegate?.manageCreditCardButtonDidTap()
    }
}
