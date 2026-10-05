//
//  BookingConfirmationViewController.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import EventKitUI
import MapKit

protocol BookingConfirmationViewControllerOutput: ExternaLinkable {
    func bookingFlowDidCancel(sender: UIViewController)
    func selectedHotelInfo(hotel: Hotel, sender: UIViewController)
    func selectedHotelInfo(
        hotelCode: String,
        sender: UIViewController,
        bookingAllowed: Bool,
        shouldShowCheckAvailabilityButton: Bool,
        criteria: Criteria?
    )
    func showPriceBreakdown(sender: UIViewController, reservation: Reservation, hotel: Hotel)
    func showDirections(sender: UIViewController, hotel: Hotel)
    func amendBooking(sender: UIViewController, summary: ReservationSummary)
    func checkInOnlineButtonDidTap(with reservation: Reservation, sender: UIViewController)
}

class BookingConfirmationViewController: BaseViewController {
    override var screenName: String { PIAnalytics.StateNames.bookingConfirmation }
    override var screenType: String { PIAnalytics.StateTypes.bookingFlow }
    override var trackScreen: Bool { false }

    @IBOutlet weak var table: UITableView! {
        didSet {
            table.estimatedRowHeight = 125
            table.rowHeight = UITableViewAutomaticDimension
            table.backgroundColor = .whiteTwo
            table.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: CGFloat.leastNormalMagnitude
            ))

            table.registerCellNib(with: BookingConfirmationInfoCell.self)
            table.registerCellNib(with: BookingConfirmationHotelInfoCell.self)
            table.registerCellNib(with: BookingConfirmationTripSummaryCell.self)
            table.registerCellNib(with: BookingConfirmationTotalPriceCell.self)
            table.registerCellNib(with: PartialSeparatorCell.self)
            table.registerCellNib(with: BookingConfirmationPriceBreakdownCell.self)
            table.registerCellNib(with: ManagingBookingActionCell.self)
            table.registerCellNib(with: BookingConfirmationLocationCell.self)
            table.registerCellNib(with: FAQActionCell.self)
            table.registerCellNib(with: TitleCell.self)
            table.registerCellNib(with: CallHotelCell.self)
            table.registerCellNib(with: BookingConfirmationEventCell.self)
            table.registerCellNib(with: CheckInOutCell.self)
            table.registerCellNib(with: CheckInOnlineActionCell.self)
            table.registerHeaderFooterNib(with: CutoutSectionFooterView.self)
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!

    weak var controllerOutput: BookingConfirmationViewControllerOutput?

    private var reservationSummary: ReservationSummary
    private let isBookingFlowEnd: Bool
    private var reservation: Reservation?
    private var viewModel: BookingConfirmationViewModel?

    private var requestsManager = RequestsManager()
    private var actionsSectionRows: [BookingConfirmationRow] {
        var rows: [BookingConfirmationRow] = []
        rows.append(BookingConfirmationRow(
            rowType: .tripTitle,
            title: PILocalizedString("bookingConfirmationTripTitle", comment: "Booking confirmation trip section title"),
            subtitle: ""
        ))

        if let reservation = reservation {
            if reservation.amendable || reservation.cancelable {
                rows.append(BookingConfirmationRow(
                    rowType: .amend,
                    title: PILocalizedString(
                        "bookingConfirmationManageBookingTitle",
                        comment: "Booking confirmation manage booking cell title"
                    ),
                    subtitle: PILocalizedString(
                        "bookingConfirmationManageBookingSubtitle",
                        comment: "Booking confirmation manage booking cell subtitle"
                    )
                ))
            }
        }

        rows.append(BookingConfirmationRow(
            rowType: .faq,
            title: PILocalizedString("bookingConfirmationFaqTitle", comment: "Booking confirmation FAQ cell title"),
            subtitle: nil
        ))

        return rows
    }

    deinit {
        requestsManager.cancelConnections()
    }

    init(summary: ReservationSummary, isBookingFlowEnd: Bool) {
        self.reservationSummary = summary
        self.isBookingFlowEnd = isBookingFlowEnd

        super.init(nibName: String(describing: BookingConfirmationViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        title = PILocalizedString("bookingConfirmationScreenTitle", comment: "Booking confirmation screen title")

        setup(with: reservationSummary)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: animated)
    }


    // MARK: -


    @objc func cancelButtonDidTap() {
        controllerOutput?.bookingFlowDidCancel(sender: self)
    }

    private func setup(with summary: ReservationSummary?) {
        if isBookingFlowEnd {
            navigationItem.backBarButtonItem?.isEnabled = false
            navigationItem.leftBarButtonItem = UIBarButtonItem(
                title: PILocalizedString("closeBarButtonTitle", comment: "Close bar button title"),
                style: .plain,
                target: self,
                action: #selector(cancelButtonDidTap)
            )
            navigationItem.backBarButtonItem = UIBarButtonItem(
                title: PILocalizedString(
                    "bookingConfirmationBackButtonTitle",
                    comment: "Booking confirmation back button title"
                ),
                style: .plain,
                target: nil,
                action: nil
            )
        }

        guard let summary = summary else { showErrorAndDie(
            title: PILocalizedString(
                "bookingConfirmationLoadingErrorTitle",
                comment: "Booking confirmation loading error title"
            ),
            message: PILocalizedString(
                "bookingConfirmationLoadingErrorMessage",
                comment: "Booking confirmation loading error message"
            )
        )
return }

        activityIndicator.startAnimating()

        requestsManager.loadHotel(with: summary.hotelCode) { hotel, _ in
            self.activityIndicator.stopAnimating()

            guard let hotel = hotel else { return  self.showErrorAndDie(
                title: PILocalizedString(
                    "bookingConfirmationLoadingErrorTitle",
                    comment: "Booking confirmation loading error title"
                ),
                message: PILocalizedString(
                    "bookingConfirmationHotelDetailsLoadingErrorMessage",
                    comment: "Booking confirmation hotel details loading error message"
                )
            ) }
            guard let arrivalDate = summary.arrivalDate else { return self.showErrorAndDie(
                title: PILocalizedString(
                    "bookingConfirmationLoadingErrorTitle",
                    comment: "Booking confirmation loading error title"
                ),
                message: PILocalizedString(
                    "bookingConfirmationArrivalDateNotAvailableMessage",
                    comment: "Booking confirmation arrival date not available message"
                )
            ) }

            self.activityIndicator.startAnimating()

            self.requestsManager.reservation(
                surname: summary.lastName,
                arrival: arrivalDate,
                reservationId: summary.identifier
            ) { reservation, _ in
                self.activityIndicator.stopAnimating()

                guard let reservation = reservation else { return self.showErrorAndDie(
                    title: PILocalizedString(
                        "bookingConfirmationLoadingErrorTitle",
                        comment: "Booking confirmation loading error title"
                    ),
                    message: PILocalizedString(
                        "bookingConfirmationReservationNotAvailableMessage",
                        comment: "Booking confirmation reservation not available error message"
                    )
                ) }

                self.loadViewModel(hotel: hotel, reservation: reservation, summary: summary)
            }
        }
    }

    private func loadViewModel(hotel: Hotel, reservation: Reservation, summary: ReservationSummary) {
        self.reservation = reservation

        var rows = [BookingConfirmationRow]()

        if isBookingFlowEnd || reservation.cancelled {
            rows.append(BookingConfirmationRow(rowType: .info))
        }

        if let canCheckIn = reservation.checkInOnline, canCheckIn == true {
            rows.append(BookingConfirmationRow(
                rowType: .checkInOnline,
                title: PILocalizedString("Check-in online", comment: ""),
                subtitle: PILocalizedString("Save time by checking in now", comment: "")
            ))
        }

        rows.append(BookingConfirmationRow(rowType: .hotel))
        rows.append(BookingConfirmationRow(rowType: .checkInOut))
        rows.append(BookingConfirmationRow(rowType: .event))
        rows.append(BookingConfirmationRow(rowType: .tripSummary))
        rows.append(BookingConfirmationRow(rowType: .location))
        rows.append(BookingConfirmationRow(rowType: .priceTotal))
        rows.append(BookingConfirmationRow(rowType: .priceBreakdown))

        viewModel = BookingConfirmationViewModel(
            reservation: reservation,
            summary: summary,
            hotel: hotel,
            sections: [
                BookingConfirmationSection(title: "", header: nil, footer: "CutoutSectionFooterView", rows: rows),
                BookingConfirmationSection(title: "", header: nil, footer: nil, rows: actionsSectionRows),
                BookingConfirmationSection(
                    title: "",
                    header: nil,
                    footer: nil,
                    rows: [BookingConfirmationRow(rowType: .callHotel)]
                )
            ]
        )

        table.delegate = viewModel
        table.dataSource = viewModel

        viewModel?.delegate = self

        table.reloadData()
    }

    private func showErrorAndDie(title: String?, message: String) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(title: PILocalizedString("OK", comment: "OK button title"), style: .cancel) { _ in
            self.navigationController?.popViewController(animated: true)
        })

        present(controller, animated: true, completion: nil)
    }
}

extension BookingConfirmationViewController: BookingConfirmationViewModelDelegate {
    func selectedHotelInfo(hotel: Hotel) {
        controllerOutput?.selectedHotelInfo(hotel: hotel, sender: self)
    }

    func priceBreakdown(hotel: Hotel) {
        guard let reservation = reservation else { return }

        controllerOutput?.showPriceBreakdown(sender: self, reservation: reservation, hotel: hotel)
    }

    func amendBooking() {
        controllerOutput?.amendBooking(sender: self, summary: reservationSummary)
    }

    func selectedFaqs() {
        controllerOutput?.openFAQExternalLink(sender: self)
    }

    func checkInOnline() {
        guard let reservation = reservation else { return }

        controllerOutput?.checkInOnlineButtonDidTap(with: reservation, sender: self)
    }

    func selectedAddToCalendar(hotel: Hotel, confirmationNumber: String, arrivalDate: Date, checkoutDate: Date) {
        SettingsManager.sharedInstance.emptyEventForBooking { store, event, _ in
            guard let event = event else {
                let controller = UIAlertController(
                    title: PILocalizedString(
                        "calendarEventCreationFailureTitle",
                        comment: "Calendar event creation failure title"
                    ),
                    message: PILocalizedString(
                        "calendarEventCreationFailureMessage",
                        comment: "Calendar event creation failure message"
                    ),
                    preferredStyle: .alert
                )
                controller.addAction(UIAlertAction(
                    title: PILocalizedString("OK", comment: "OK button title"),
                    style: .cancel,
                    handler: nil
                ))

                return self.present(controller, animated: true, completion: nil)
            }

            event.title = hotel.name + "(" + confirmationNumber + ")"

            let mapItem: MKMapItem = {
                guard let placemark = hotel.placeMark else {
                    let address = hotel.address?.postalAddressDictionary
                    let placemark = MKPlacemark(coordinate: hotel.coordinate, addressDictionary: address)

                    return MKMapItem(placemark: placemark)
                }

                return MKMapItem(placemark: placemark)
            }()
            event.structuredLocation = EKStructuredLocation(mapItem: mapItem)
            event.startDate = arrivalDate
            event.endDate = checkoutDate

            let controller = EKEventEditViewController()
            controller.event = event
            controller.eventStore = store
            controller.editViewDelegate = self

            self.present(controller, animated: true, completion: nil)
        }
    }

    func showDirections(for hotel: Hotel) {
        controllerOutput?.showDirections(sender: self, hotel: hotel)
    }

    func callHotelButtonDidTap(hotel: Hotel) {
        // Post booking we don't charge for extra calls. Read the phonenumber from the Hotel object
        guard let number = hotel.nationalPhoneNumber else { return }

        FlowController.sharedInstance.showCallHotelAlert(from: self, number: number)
    }
}

extension BookingConfirmationViewController: EKEventEditViewDelegate {
    func eventEditViewController(_ controller: EKEventEditViewController, didCompleteWith action: EKEventEditViewAction) {
        controller.dismiss(animated: true, completion: nil)

        if action != .canceled {
            self.table.reloadData()
        }
    }
}
