//
//  BookingConfirmationViewModel.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol BookingConfirmationCellProtocol {
    func configure(with reservation: Reservation, summary: ReservationSummary, hotel: Hotel, sender: Any?)
}

enum BookingConfirmationRowType: String {
    case info = "BookingConfirmationInfoCell"
    case hotel = "BookingConfirmationHotelInfoCell"
    case event = "BookingConfirmationEventCell"
    case tripSummary = "BookingConfirmationTripSummaryCell"
    case priceTotal = "BookingConfirmationTotalPriceCell"
    case partialSeparator = "PartialSeparatorCell"
    case priceBreakdown = "BookingConfirmationPriceBreakdownCell"
    case amend = "ManagingBookingActionCell"
    case location = "BookingConfirmationLocationCell"
    case faq = "FAQActionCell"
    case checkInOnline = "CheckInOnlineActionCell"
    case tripTitle = "TitleCell"
    case callHotel = "CallHotelCell"
    case checkInOut = "CheckInOutCell"
}

struct BookingConfirmationRow {
    var rowType: BookingConfirmationRowType
    var title: String?
    var subtitle: String?
}

extension BookingConfirmationRow {
    init(rowType: BookingConfirmationRowType) {
        self.rowType = rowType
    }
}

struct BookingConfirmationSection {
    var title: String?
    var header: String?
    var footer: String?
    var rows: [BookingConfirmationRow]
}

protocol BookingConfirmationViewModelDelegate: class {
    func selectedHotelInfo(hotel: Hotel)
    func priceBreakdown(hotel: Hotel)
    func amendBooking()
    func selectedFaqs()
    func checkInOnline()
    func selectedAddToCalendar(hotel: Hotel, confirmationNumber: String, arrivalDate: Date, checkoutDate: Date)
    func showDirections(for hotel: Hotel)
    func callHotelButtonDidTap(hotel: Hotel)
}

class BookingConfirmationViewModel: NSObject {
    private var sections = [BookingConfirmationSection]()
    private var cellHeights = [IndexPath: CGFloat]()
    private var reservation: Reservation?
    private var reservationSummary: ReservationSummary?
    private let hotel: Hotel

    weak var delegate: BookingConfirmationViewModelDelegate?

    init(reservation: Reservation, summary: ReservationSummary, hotel: Hotel, sections: [BookingConfirmationSection]) {
        self.reservation = reservation
        self.reservationSummary = summary
        self.sections = sections
        self.hotel = hotel
    }

    func amendAction() {
        delegate?.amendBooking()
    }

    func calendarAction() {
        guard let reservation = reservation else { return }
        guard let arrivalDate = reservation.arrivalDate else { return }
        guard let checkoutDate = reservation.checkOutDate else { return }

        delegate?.selectedAddToCalendar(
            hotel: hotel,
            confirmationNumber: reservation.confirmationNumber,
            arrivalDate: arrivalDate,
            checkoutDate: checkoutDate
        )
    }
}

extension BookingConfirmationViewModel: UITableViewDelegate, UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        sections.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let aSection = sections[section]

        return aSection.rows.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let rowModel = sections[indexPath.section].rows[indexPath.row]

        let cell = tableView.dequeueReusableCell(
            withIdentifier: String(describing: rowModel.rowType.rawValue),
            for: indexPath
        )
        cell.textLabel?.text = rowModel.title
        cell.detailTextLabel?.text = rowModel.subtitle

        if let reservation = reservation, let summary = reservationSummary {
            (cell as? BookingConfirmationCellProtocol)?.configure(
                with: reservation,
                summary: summary,
                hotel: hotel,
                sender: self
            )
        }

        if let cell = cell as? CallHotelCell {
            cell.delegate = self
            cell.callChargeInformationLabel.text = PILocalizedString("telephoneNoCost", comment: "Call cost message")
        }

        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let rowModel = sections[indexPath.section].rows[indexPath.row]

        switch rowModel.rowType {
        case .hotel:
            delegate?.selectedHotelInfo(hotel: hotel)

        case .priceBreakdown:
            delegate?.priceBreakdown(hotel: hotel)

        case .amend:
            amendAction()

        case .faq:
            delegate?.selectedFaqs()

        case .checkInOnline:
            delegate?.checkInOnline()

        default:
            break
        }
    }

    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let sectionFooter = sections[section].footer

        guard let view = tableView.dequeueReusableHeaderFooterView(withIdentifier: String(describing: sectionFooter)) else {
            return nil
        }

        return view
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        guard section == 0 else { return 0 }

        return 54
    }

    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        if cellHeights.keys.contains(indexPath) {
            if let cellHeight = cellHeights[indexPath] {
                guard cellHeight > 1.0  else { return UITableViewAutomaticDimension }

                return cellHeight
            }
        }

        return UITableViewAutomaticDimension
    }

    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        guard cell.frame.size.height > 1.0 else { return }

        cellHeights[indexPath] = cell.frame.size.height
    }
}

extension BookingConfirmationViewModel: BookingConfirmationLocationCellDelegate {
    func directionsCellDidTap(cell: BookingConfirmationLocationCell) {
        delegate?.showDirections(for: hotel)
    }
}

extension BookingConfirmationViewModel: CallHotelCellDelegate {
    func callHotelButtonDidTap(sender: CallHotelCell) {
        delegate?.callHotelButtonDidTap(hotel: hotel)
    }
}

extension BookingConfirmationViewModel: BookingConfirmationEventDelegate {
    func selectedAddToCalendar() {
        calendarAction()
    }

    var eventExistsInCalendar: Bool {
        guard let reservation = reservation else { return false }
        guard let arrivalDate = reservation.arrivalDate else { return false }
        guard let checkoutDate = reservation.checkOutDate else { return false }

        return SettingsManager.sharedInstance.calendar(
            containsEventWithBookingRefererence: reservation.confirmationNumber,
            startDate: arrivalDate,
            endDate: checkoutDate
        )
    }
}
