//
//  BookingSummaryView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 04/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol BookingSummaryViewProtocol: AnyObject {
    func setup(withTitle: String)
    func showViewModel(with: BookingSummaryViewModel)
    func showError(title: String, message: String?)
    func toggleActivity(isOn: Bool)
}

class BookingSummaryViewController: BaseViewController {
    @IBOutlet weak var table: UITableView! {
        didSet {
            table.backgroundColor = .whiteTwo
            table.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: CGFloat.leastNormalMagnitude
            ))
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 40
            table.sectionFooterHeight = 0
            table.tableFooterView = UIView()

            registerNibs()
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!

    override var screenName: String { screen }
    override var screenType: String { PIAnalytics.StateTypes.bookingFlow }
    override var customParameters: [String: Any]? {
        let bookingDetails = BookingDetails.sharedInstance
        let criteria = BookingDetails.sharedInstance.criteria
        let checkoutDate = criteria.checkOutDate ?? Date()
        let startDay = criteria.arrivalDate.analyticsDayFormat
        let endDay = checkoutDate.analyticsDayFormat
        let rateCode = bookingDetails.rate?.rateCode()

        return [
            PIAnalytics.Keys.productString: ";\(bookingDetails.hotel?.code ?? "")",
            PIAnalytics.Keys.bfRateCode: rateCode ?? "",
            PIAnalytics.Keys.bfRateDescription: bookingDetails.rate?.description ?? "",
            PIAnalytics.Keys.bfCheckInDate: criteria.arrivalDate.analyticsDateFormat,
            PIAnalytics.Keys.bfCheckOutDate: checkoutDate.analyticsDateFormat,
            PIAnalytics.Keys.bfNights: "\(criteria.nights)",
            PIAnalytics.Keys.bfRooms: "\(criteria.rooms.count)",
            PIAnalytics.Keys.bfAdults: "\(criteria.adultsCount)",
            PIAnalytics.Keys.bfChildren: "\(criteria.childrenCount)",
            PIAnalytics.Keys.bfCheckInDay: startDay,
            PIAnalytics.Keys.bfCheckOutDay: endDay,
            PIAnalytics.Keys.bfCheckInOutDay: [startDay, endDay].joined(separator: "-"),
            PIAnalytics.Keys.bfExtrasSelectedDescriptions: bookingDetails.selectedUpsellsForAnalytics
        ]
    }

    private var tableViewModel: FormekaViewModel?
    private let screen: String

    var presenter: BookingSummaryPresenterProtocol?

    init(screenName: String) {
        self.screen = screenName

        super.init(nibName: String(describing: BookingSummaryViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        presenter?.viewIsReady()

        navigationItem.titleView?.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary.summaryOverlayPageTitle
    }

    private func registerNibs() {
        guard table != nil else { return }

        table.registerCellClass(with: UITableViewCell.self)
        table.registerCellNib(with: SummaryHotelCell.self)
        table.registerCellNib(with: SummaryDatesCell.self)
        table.registerCellNib(with: SummaryRoomCell.self)
        table.registerCellNib(with: SummaryTotalCell.self)
        table.registerCellNib(with: BookingReviewAdditionsCell.self)
        table.registerHeaderFooterNib(with: BookingSummaryHeader.self)
    }
}

extension BookingSummaryViewController: BookingSummaryViewProtocol {
    func setup(withTitle viewTitle: String) {
        title = viewTitle
    }

    func showViewModel(with viewModel: BookingSummaryViewModel) {
        tableViewModel = FormekaViewModel(sections: sectionsWith(viewModel: viewModel))

        table.delegate = tableViewModel
        table.dataSource = tableViewModel
        table.reloadData()
    }

    func showError(title: String, message: String?) {
        showAlertWith(title: title, message: message)
    }

    func toggleActivity(isOn: Bool) {
        if isOn {
            activityIndicator.startAnimating()
        } else {
            activityIndicator.stopAnimating()
        }
    }
}

private extension BookingSummaryViewController {
    func sectionsWith(viewModel: BookingSummaryViewModel) -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        sections.append(hotelSection(hotel: viewModel.hotel))

        sections.append(datesSection(
            arrival: viewModel.arrivalDateText,
            checkOut: viewModel.checkOutDateText,
            summary: viewModel.summaryText
        ))

        sections.append(roomsSection(rooms: viewModel.rooms))

        if let section = upsellsSection(viewModel: viewModel) {
            sections.append(section)
        }

        sections.append(totalSection(
            totalCostDescription: viewModel.totalCostText,
            rateDescription: viewModel.rateText,
            shouldShowCityTaxInfo: viewModel.shouldTotalShowCityTaxInfo
        ))

        return sections
    }

    func hotelSection(hotel: Hotel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SummaryHotelCell = table.dequeueCell(for: indexPath) else { return nil }

            if let imageUrl = hotel.primaryImages.first?.sizedImageURL(withSize: .medium) {
                cell.thumbView.setImage(with: imageUrl, transition: true)
            }

            cell.nameLabel.text = hotel.name
            cell.addressLabel.text = hotel.address?.description

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func datesSection(arrival: String?, checkOut: String?, summary: String?) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SummaryDatesCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.titleLabel.text = PILocalizedString("bookingSummaryArriveLabel", comment: "Booking summary arrive label")
            cell.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary.summaryPageArrivalTitle
            cell.contentLabel.text = arrival
            cell.contentLabel.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary
                .summaryPageArrivalDateCheckinTime

            return cell
        }))

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SummaryDatesCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.titleLabel.text = PILocalizedString("bookingSummaryLeaveLabel", comment: "Booking summary leave label")
            cell.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary.summaryPageLeaveTitle
            cell.contentLabel.text = checkOut
            cell.contentLabel.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary
                .summaryPageLeavingDateCheckOutTime

            return cell
        }))

        return FormekaModelSection(
            header: FormekaModelHeaderFooter(height: 50, viewSetup: { _, table in
                let view: BookingSummaryHeader? = table.headerFooterView()
                view?.titleLabel.text = summary
                view?.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary.summaryPageGuestAndDays
                view?.titleLabel.accessibilityTraits.insert(.header)
                return view
            }),
            rows: rows,
            footer: nil
        )
    }

    func roomsSection(rooms: [Room]?) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        if let rooms = rooms {
            for (index, room) in rooms.enumerated() {
                rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                    guard let cell: SummaryRoomCell = table.dequeueCell(for: indexPath) else { return nil }
                    cell.accessibilityIdentifier = "\(AccessibilityIdentifiers.BookingSummary.roomIndex)\(index)"
                    cell.configure(with: room, roomIndex: index)

                    cell.cityTaxLabel.text = nil

                    return cell
                }))
            }
        }

        return FormekaModelSection(
            header: FormekaModelHeaderFooter(height: 50, viewSetup: { _, table in
                let view: BookingSummaryHeader? = table.headerFooterView()
                view?.titleLabel.text = PILocalizedString(
                    "bookingSummaryRoomDetailsSectionTitle",
                    comment: "Booking summary room details section title"
                )
                view?.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary
                    .summaryPageRoomDetailsTitle

                return view
            }),
            rows: rows,
            footer: nil
        )
    }

    func upsellsSection(viewModel: BookingSummaryViewModel) -> FormekaModelSection? {
        guard let roomsCountDescription = viewModel.roomsCountDescription else { return nil }
        guard let nightsCountDescription = viewModel.nightsCountDescription else { return nil }

        var rows = [FormekaModelRow]()

        rows.append(breakDownRow(
            title: PILocalizedString("bookingSummaryHotelStayLabel", comment: "Booking summary hotel stay label") + "\n(" +
                nightsCountDescription + ", " + roomsCountDescription + ")",
            value: viewModel.roomsCostText
        ))

        viewModel.mealModels.forEach { mealModel in
            rows.append(contentsOf: mealRows(mealModel: mealModel, nightsCountDescription: nightsCountDescription))
        }

        viewModel.extrasModels.forEach { extrasModel in
            rows.append(contentsOf: extrasRow(extrasModel: extrasModel))
        }

        viewModel.wifiModels.forEach { wifiModel in
            rows.append(contentsOf: wifiRow(wifiModel: wifiModel, nightsCountDescription: nightsCountDescription))
        }

        if let donation = viewModel.donationsAmount {
            rows.append(donationRow(goshDonation: donation))
        }

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func wifiRow(wifiModel: BookingSummaryWifiViewModel, nightsCountDescription: String) -> [FormekaModelRow] {
        guard let wifi = wifiModel.wifi else { return [] }
        let numberOfRooms = wifiModel.numberOfRooms

        let upsellTitle = "\(wifi.legend)\n(\(nightsCountDescription), \(String.localizedStringWithFormat(PILocalizedString("%d room(s)", comment: "Message shown for number of rooms"), numberOfRooms)))"

        return [breakDownRow(title: upsellTitle, value: wifiModel.totalCostText)]
    }

    func extrasRow(extrasModel: BookingSummaryExtrasViewModel) -> [FormekaModelRow] {
        guard let extra = extrasModel.extraItem else { return [] }
        let numberOfRooms = extrasModel.numberOfRooms

        let upsellTitle = numberOfRooms > 1 ?
            "\(extra.legend)\n(\(String.localizedStringWithFormat(PILocalizedString("%d room(s)", comment: "Message shown for number of rooms"), numberOfRooms)))" :
            extra.legend

        return [breakDownRow(title: upsellTitle, value: extrasModel.totalCostText)]
    }

    func mealRows(mealModel: BookingSummaryMealViewModel, nightsCountDescription: String) -> [FormekaModelRow] {
        guard let meal = mealModel.meal else { return [] }
        guard let guestsCountDescription = mealModel.guestsCountDescription else { return [] }

        // Translate if free child breakfast as we hardcode the value in SimpleNetwork
        let mealTitle = meal.id == UpsellItemOperaId.freeChildBreakfast
            .rawValue ? PILocalizedString("userPreferenceFreeChildBreakfast") : meal.legend

        return [breakDownRow(
            title: mealTitle + "\n(" + guestsCountDescription + ", " + nightsCountDescription + ")",
            value: mealModel.totalCostText
        )]
    }

    func breakDownRow(title: String?, value: String?) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingReviewAdditionsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.titleLabel.text = title
            cell.valueLabel.text = value
            cell.contentView.backgroundColor = .white
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        })
    }

    func donationRow(goshDonation: Cost) -> FormekaModelRow {
           FormekaModelRow(tag: ReviewAndBookRow.donationSummary.rawValue, cellSetup: { indexPath, _, table in
               guard let cell: BookingReviewAdditionsCell = table.dequeueCell(for: indexPath) else { return nil }
               cell.hiddenSeparatorLocations = [.top, .bottom]
               cell.titleLabel.text = PILocalizedString("bookingSummaryGoshLabel", comment: "Booking summary GOSH label")
               cell.valueLabel.text = goshDonation.localizedValue
               cell.backgroundColor = .white

               return cell
           })
       }

    func totalSection(
        totalCostDescription: String?,
        rateDescription: String?,
        shouldShowCityTaxInfo: Bool
    ) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SummaryTotalCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.contentView.backgroundColor = .whiteTwo
            cell.descriptionLabel.text = PILocalizedString(
                "bookingSummaryTotalPriceTitle",
                comment: "Booking summary total price title"
            )
            cell.descriptionLabel.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary.summaryPageTotalBooking
            cell.priceLabel.text = totalCostDescription
            cell.priceLabel.accessibilityIdentifier = "summaryPageTotalAcc"
            cell.priceLabel.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary.summaryPageTotalBookingPrice

            cell.rateLabel.text = rateDescription
            cell.rateLabel.accessibilityIdentifier = AccessibilityIdentifiers.BookingSummary.summaryPageBookingType

            cell.cityTaxLabel.text = shouldShowCityTaxInfo ? PILocalizedString(
                "bookingReviewCityTaxTitle",
                comment: "Booking review city tax title"
            ) : nil

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }
}
