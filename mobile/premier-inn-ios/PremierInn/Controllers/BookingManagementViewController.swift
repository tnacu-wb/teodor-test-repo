//
//  BookingManagementViewController.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 23/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

private enum BookingManagementError: LocalizedError {
    case missingArrivalDate

	var errorDescription: String? { String(describing: self)	}
}

class BookingManagementViewController: BaseViewController {
    override var screenName: String { PIAnalytics.StateNames.manageBooking }
    override var screenType: String { PIAnalytics.StateTypes.myBookings }

    @IBOutlet weak var table: UITableView! {
        didSet {
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 44

            table.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: CGFloat.leastNormalMagnitude
            ))
            table.backgroundColor = UIColor.whiteTwo
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!

    private var viewModel: FormekaViewModel?
    private var stay: Stay
    private var requestsManager = RequestsManager()
    private var reservation: Reservation?
    private var hotel: Hotel?

    init(summary: Stay) {
        self.stay = summary

        super.init(nibName: String(describing: BookingManagementViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        registerTableElements()

        title = PILocalizedString("bookingManagementScreenTitle", comment: "Booking management screen title")

        setup(with: stay)
    }

    private func registerTableElements() {
        table.registerCellNib(with: ContentCell.self)
        table.registerCellNib(with: PartialSeparatorCell.self)
        table.registerCellNib(with: CallHotelCell.self)
        table.registerCellNib(with: CancelButtonCell.self)
    }

    private func setup(with summary: Stay) {
        guard let arrivalDate = summary.arrivalDate else { return }

        activityIndicator.startAnimating()

        requestsManager
            .reservation(
                surname: summary.lastName,
                arrival: arrivalDate,
                reservationId: summary.identifier
            ) { reservation, _ in
            self.activityIndicator.stopAnimating()

            self.reservation = reservation

            guard let reservation = self.reservation else { return }

            self.requestsManager.loadHotel(with: reservation.hotelCode, completion: { (hotel, _) in
                self.hotel = hotel

                self.viewModel = FormekaViewModel(sections: {
                    var sections: [FormekaModelSection] = []

                    sections.append(section1())

                    if let section = section2(reservation: reservation) {
                        sections.append(section)
                    }

                    return sections
                }())

                self.table.delegate = self.viewModel
                self.table.dataSource = self.viewModel
                self.table.reloadData()
            })
        }
    }

    private func section1() -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: ContentCell = table.dequeueCell(for: indexPath) else { return nil }

            if let attributedText = NSAttributedString.attributedStringWith(
                text: PILocalizedString("amendBookingText", comment: "Message displayed to explain how to amend a booking"),
                lineSpacing: 7,
                font: UIFont.premierInn(ofSize: 15),
                textColor: .greyishBrown,
                textAlignment: .left
            ) {
                let mutableString = NSMutableAttributedString(attributedString: attributedText)
                let stringsToHighlight = [PILocalizedString(
                    "amendBookingBoldText",
                    comment: "Text in amendBookingText that should be bold"
                )]
                cell.contentLabel.attributedText = mutableString.attributedStringByHighlightingCharacters(
                    highlights: stringsToHighlight,
                    font: UIFont.premierInnBold(ofSize: 18)
                )
            } else {
                cell.contentLabel.attributedText = nil
            }

            return cell
        }))

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: PartialSeparatorCell = table.dequeueCell(for: indexPath) else { return nil }

            return cell
        }))

        rows.append(FormekaModelRow(cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: CallHotelCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.contentView.backgroundColor = .white
            cell.callChargeInformationLabel.text = PILocalizedString("telephoneNoCost", comment: "Call cost message")
            cell.delegate = self

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func section2(reservation: Reservation) -> FormekaModelSection? {
        guard reservation.cancelable else { return nil }

        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(tag: "CancelButton", cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: CancelButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func toggleCancelButtonProcessing(active: Bool) {
        guard let cell: CancelButtonCell = self.viewModel?.cell(forRowNamed: "CancelButton", table: self.table)
            else { return }
        cell.cancelButton.setTitle(
            active ? nil :
                PILocalizedString("bookingManagementCancelButtonTitle", comment: "Booking management cancel button title"),
            for: .normal
        )
        cell.cancelButton.isEnabled = !active
        if active {
            cell.activityIndicator.startAnimating()
        } else {
            cell.activityIndicator.stopAnimating()
        }
    }
}

extension BookingManagementViewController: CancelButtonCellDelegate {
    func cancelButtonDidTap(cell: CancelButtonCell) throws {
        guard let arrivalDate = stay.arrivalDate  else { throw BookingManagementError.missingArrivalDate }

        let controller = UIAlertController(
            title: PILocalizedString("bookingManagementCancelAlertTitle", comment: "Booking management cancel alert title"),
            message: PILocalizedString(
                "bookingManagementCancelAlertMessage",
                comment: "Booking management cancel alert message"
            ),
            preferredStyle: .alert
        )
        controller.addAction(UIAlertAction(
            title: PILocalizedString("Cancel", comment: "Title for alert cancel button"),
            style: .cancel,
            handler: nil
        ))
        controller.addAction(UIAlertAction(
            title: PILocalizedString("Confirm", comment: "Confirm button title"),
            style: .destructive
        ) { [weak self] _ in
            self?.toggleCancelButtonProcessing(active: true)

            guard let identifier = self?.stay.identifier, let lastName = self?.stay.lastName else { return }
            self?.cancelBooking(identifier: identifier, surname: lastName, arrivalDate: arrivalDate)
        })

        self.present(controller, animated: true)
    }

    private func cancelBooking(identifier: String, surname: String, arrivalDate: Date) {
        requestsManager
            .cancelReservation(
                identifier: identifier,
                surname: surname,
                arrivalDate: arrivalDate
            ) { [weak self] dictionary, error in
            self?.toggleCancelButtonProcessing(active: false)

            if dictionary?["cancellationId"] is String {
                self?.track(action: PIAnalytics.Action.cancelledBooking, additionalData: [
                    PIAnalytics.Keys.didCancel: true,
                    PIAnalytics.Keys.cancelBookingId: identifier
                ])

                let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

                if let stay = reservationsManager.items.first(where: {$0.identifier == identifier }) {
                    stay.cancelled = true
                    _ = reservationsManager.update(with: [stay])
                }

                let controller = UIAlertController(
                    title: PILocalizedString("bookingCancelledAlertTitle", comment: ""),
                    message: PILocalizedString("bookingCancelledAlertMessage", comment: ""),
                    preferredStyle: .alert
                )
                controller.addAction(UIAlertAction(
                    title: PILocalizedString("bookingCancelledCancelButtonTitle", comment: ""),
                    style: .cancel,
                    handler: { _ in
                    _ = self?.navigationController?.popViewController(animated: false)
                    self?.requestsManager.refreshStays(
                        for: UserSessionManager.sharedInstance.currentUser,
                        shouldAttemptLogin: false
                    )
                }
                ))

                self?.present(controller, animated: true)
            } else {
                switch error {
                case RequestsManagerError.serverError(let dict)?:
                    print("ERROR DICTIONARY: \(String(describing: dict))")
                default:
                    break
                }

                let controller = UIAlertController(
                    title: PILocalizedString(
                        "bookingManagementCancelAlertTitle",
                        comment: "Booking management cancel alert title"
                    ),
                    message: PILocalizedString(
                        "bookingManagementCancelErrorMessage",
                        comment: "Booking management cancel error message"
                    ),
                    preferredStyle: .alert
                )
                controller.addAction(UIAlertAction(
                    title: PILocalizedString("OK", comment: "OK button title"),
                    style: .cancel,
                    handler: nil
                ))

                self?.present(controller, animated: true)
            }
        }
    }
}

extension BookingManagementViewController: CallHotelCellDelegate {
    func callHotelButtonDidTap(sender: CallHotelCell) {
        // Post booking we don't charge for extra calls. Read the phonenumber from the Hotel object
        guard let number = hotel?.nationalPhoneNumber else { return }

        showCallHotelAlert(number: number)
    }
}
