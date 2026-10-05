//
//  ReservationsView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol ReservationsViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }

    func loadViewModel(with: ActivePastStays)
    func showErrorMessage(title: String, message: String)
    func showRefreshButton()
    func hideRefreshButton()
    func presentAlertController(_ controller: UIAlertController)
    func toggleLoadingIndicator(isLoading: Bool, hideTableAsWell: Bool)
}

class ReservationsListViewController: BaseViewController {
    // MARK: - Views

    @IBOutlet weak var table: UITableView! {
        didSet {
            table.tableFooterView = UIView()
            table.estimatedRowHeight = 315
            table.rowHeight = UITableView.automaticDimension
            table.backgroundColor = .BaseWhite
            table.tableHeaderView = table.style == .grouped ? UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: .leastNormalMagnitude
            )) : nil
            table.separatorStyle = .none

            registerNibs()
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!

    // MARK: - Properties

    override var screenName: String { PIAnalytics.StateNames.myBookings }
    override var screenType: String { PIAnalytics.StateTypes.myBookings }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    private var tableViewModel: FormekaViewModel?

    var presenter: ReservationsPresenterProtocol?
    var checkedIn: Bool = false {
        didSet {
            guard checkedIn else { return }
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1) { [weak self] in
                guard let view = self?.view else { return }
                let text = self?
                    .justPaid == true ? PILocalizedString("You have checked-in and your payment has been successful") :
                    PILocalizedString("You have checked-in")
                SuccessBanner.init(withMessage: text, on: view, with: "tick").show()
            }
        }
    }
    var justPaid: Bool = false

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        title = PILocalizedString("reservationsScreenTitle", comment: "Reservations list: screen title")
		NotificationCenter.default.addObserver(
		    self,
		    selector: #selector(refreshData),
		    name: .refreshReservationsList,
		    object: nil
		)
        let button = UIButton(type: .system)
        button.setTitle(PILocalizedString("reservationsFindBookingButtonTitle"), for: .normal)
        button.titleLabel?.font = UIFont.Body()
        button.addTarget(self, action: #selector(findBookingButtonDidTap), for: .touchUpInside)
        navigationItem.rightBarButtonItem = UIBarButtonItem(customView: button)
        navigationItem.rightBarButtonItem?.tintColor = .ColourDL6
        navigationItem.rightBarButtonItem?.accessibilityIdentifier = AccessibilityIdentifiers.MyBookings
            .myBookingsPageSeachBookingButton
        navigationItem.titleView?.accessibilityIdentifier = AccessibilityIdentifiers.MyBookings.myBookingsPageHeader

        view.backgroundColor = .ColourLD6

        presenter?.viewIsReady()
    }

	@objc func refreshData() {
		presenter?.viewIsReady()
	}

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        justPaid = false
        checkedIn = false
    }

    @objc func findBookingButtonDidTap() {
        presenter?.findBookingButtonDidTap()
    }

    @objc func refreshButtonDidTap() {
        presenter?.refreshButtonDidTap()
    }

    private func registerNibs() {
        table.registerCellNib(with: SimpleSubtitleCell.self)
        table.registerCellNib(with: ReservationStaySummaryCell.self)
        table.registerCellNib(with: ActionIconCell.self)
        table.registerCellNib(with: ReservationListActionCell.self)
        table.registerCellNib(with: TitleTextCell.self)
        table.registerCellNib(with: HeaderCell.self)
        table.registerCellNib(with: FooterCell.self)
        table.registerCellNib(with: BookingConfirmationInfoCell.self)
        table.registerCellNib(with: MyBookingsErrorsViewCell.self)
        table.registerCellNib(with: GDPRBannerHeaderRow.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: BookingCheckInButtonListCell.self)
        table.registerCellClass(with: BottomBorderCell.self)
    }

    func presentAlertController(_ controller: UIAlertController) {
        present(controller, animated: true)
    }
}

extension ReservationsListViewController: ReservationsViewProtocol {
    func loadViewModel(with response: ActivePastStays) {
        tableViewModel = tableViewModel(with: response)

        table.delegate = tableViewModel
        table.dataSource = tableViewModel

        table.reloadData()
    }

    func showErrorMessage(title: String, message: String) {
        DispatchQueue.main.async {
            self.showAlertWith(title: title, message: message)
        }
    }

    func showRefreshButton() {
        navigationItem.setLeftBarButton(
            UIBarButtonItem(image: #imageLiteral(resourceName: "refreshRotate"), style: .plain, target: self, action: #selector(refreshButtonDidTap)),
            animated: true
        )
        navigationItem.leftBarButtonItem?.tintColor = currentNavigationTheme.iconTintColor
    }

    func hideRefreshButton() {
        navigationItem.setLeftBarButton(nil, animated: true)
    }

    func toggleLoadingIndicator(isLoading: Bool, hideTableAsWell: Bool) {
        DispatchQueue.main.async {
            if isLoading {
                self.activityIndicator.startAnimating()
            } else {
                self.activityIndicator.stopAnimating()
            }
            self.table.isHidden = hideTableAsWell ? isLoading : false
        }
    }
}

extension ReservationsListViewController {
    func tableViewModel(with response: ActivePastStays) -> FormekaViewModel {
        guard (response.activeStays + response.pastStays).isNotEmpty
            else { return FormekaViewModel(sections: [errorsSection()]) }

        var sections: [FormekaModelSection] = []

        if response.importAction {
            let message = PILocalizedString(
                "reservationsSuccessfulImport",
                comment: "Reservations list: import successful message"
            )
            sections.append(updatedReservationsBannerSection(with: message))
        }

        if let headerMessage = response.headerMessage {
            sections.append(infoHeaderSection(infoMessage: headerMessage))
        }

        let migrationBanner = PILocalizedString("reservationsMigrationBanner")
        if migrationBanner.isNotEmpty {
            sections.append(infoHeaderSection(infoMessage: migrationBanner))
        }

        if response.activeStays.isNotEmpty {
            sections.append(titleSection(title: PILocalizedString("reservationsFutureSectionTitle")))
            sections.append(contentsOf: descriptionSections(forReservations: response.activeStays, isPast: false))
        }

        if response.pastStays.isNotEmpty {
            sections.append(titleSection(title: PILocalizedString("reservationsPastSectionTitle")))
            sections.append(contentsOf: descriptionSections(forReservations: response.pastStays, isPast: true))
        }

        sections.append(gdprShieldSection(for: table, backgroundColor: .clear))

        return FormekaViewModel(sections: sections)
    }

    func errorsSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        let migrationBanner = PILocalizedString("reservationsMigrationBanner")
        if migrationBanner.isNotEmpty {
            rows.append(contentsOf: infoHeaderSection(infoMessage: migrationBanner).rows)
        }

        rows.append(FormekaModelRow(cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: MyBookingsErrorsViewCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.titleLabel.text = PILocalizedString("reservationsEmptyListTitle")

            cell.findBookingButton.setTitle(PILocalizedString("reservationsFindBookingButtonTitle"), for: .normal)
            cell.findBookingButton.accessibilityIdentifier = AccessibilityIdentifiers.MyBookings.guestMyBookingFindABooking
            cell.orLabel.text = PILocalizedString("reservationsOrLabelTitle")

            if UserSessionManager.sharedInstance.currentUser != nil {
                cell.actionButton.setTitle(PILocalizedString("reservationsSearchButtonTitle"), for: .normal)
                cell.actionButton.accessibilityIdentifier = AccessibilityIdentifiers.MyBookings
                    .registeredUserSeachforAHotelButton

                cell.subtitleLabel.text = PILocalizedString("reservationsSearchExplanation")
                cell.subtitleLabel.accessibilityIdentifier = AccessibilityIdentifiers.MyBookings.registeredUserNoPastBookings
            } else {
                cell.actionButton.setTitle(PILocalizedString("reservationsLoginButtonTitle"), for: .normal)
                cell.actionButton.accessibilityIdentifier = AccessibilityIdentifiers.MyBookings.guestMyBookingPageLoginButton

                cell.subtitleLabel.text = PILocalizedString("reservationsLoginExplanation")
                cell.subtitleLabel.accessibilityIdentifier = AccessibilityIdentifiers.MyBookings.guestMyBookingPage
            }

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func infoHeaderSection(infoMessage: String) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.text = infoMessage

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: footer)
    }

    private var footer: FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 12, viewSetup: { _, _ in
            let footerView = UITableViewHeaderFooterView(frame: CGRect.zero)
            footerView.contentView.backgroundColor = .ColourLD6

            let lineView = UIView(frame: CGRect(x: 0, y: 0, width: footerView.frame.width, height: 2))
            lineView.autoresizingMask = [.flexibleWidth]
            lineView.backgroundColor = .ColourLD6

            footerView.addSubview(lineView)

            return footerView
        })
    }

    func titleSection(title: String) -> FormekaModelSection {
        FormekaModelSection(
            header: nil,
            rows: [
                FormekaModelRow(cellSetup: { indexPath, _, table in
                    guard let cell: SimpleSubtitleCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.backgroundColor = .clear
                    cell.bottomConstraint.constant = 25

                    cell.title.text = title
                    cell.title.textColor = .BasePurple
                    cell.title.font = .Heading1_Bold()
                    cell.title.accessibilityTraits.insert(.header)

                    cell.subtitle.text = nil
                    cell.subtitle.textColor = .ColourDL1
                    cell.subtitle.font = .Heading4_Semibold()

                    cell.solidSeparator.backgroundColor = .clear

                    return cell
                })
            ],
            footer: nil
        )
    }

    func descriptionSections(
        forReservations reservations: [Stay],
        isPast: Bool,
        isCiolEnabled: Bool = SettingsManager.sharedInstance.featureCIOL
    ) -> [FormekaModelSection] {
        reservations.compactMap { stay -> FormekaModelSection in
            var rows = [FormekaModelRow]()

            rows.append(titleRow(summary: stay, isPastBooking: isPast))

            let reservationsHotelSpecificMigrationBanner = PILocalizedString("reservationsHotelSpecificMigrationBanner")
            if reservationsHotelSpecificMigrationBanner.isNotEmpty {
                rows.append(infoRow(with: reservationsHotelSpecificMigrationBanner))
            } else {
                rows.append(actionCell(
                    icon: #imageLiteral(resourceName: "details"),
                    title: PILocalizedString(
                        "reservationsBookingDetailsShortcut",
                        comment: "Reservations list: booking details shortcut"
                    )
                ) { [weak self] _  in
                    self?.presenter?.bookingDetailsButtonDidTap(stay: stay)
                })

				if stay.showAddDigitalKeyButton {
					rows.append(actionCell(
					    icon: UIImage(named: "digitalKeyDoor"),
					    title: PILocalizedString("addDigitalKey"),
					    action: { [weak self] _ in
						self?.presenter?.addDigitalKeyButtonDidTap(stay: stay)
					}
					))
				}

				if stay.userHasPassInWallet {
					rows.append(actionCell(
					    icon: UIImage(named: "digitalKeyDoor"),
					    title: PILocalizedString("showDigitalKey"),
					    action: { [weak self] _ in
						self?.presenter?.showDigitalKeyButtonDidTap(stay: stay)
					}
					))
				}

                if stay.qrCodeEnabledAndWithin48Hours {
                    rows.append(actionCell(
                        icon: UIImage(systemName: "qrcode"),
                        title: PILocalizedString("kioskPassPageTitle")
                    ) { [weak self] _  in
                        self?.presenter?.qrCodeButtonDidTap(stay: stay)
                    })
                }

                if stay.showHotelWifiOption {
                    rows.append(actionCell(
                        icon: UIImage(systemName: "wifi"),
                        title: PILocalizedString("reservationsWifiButtonTitle")
                    ) { [weak self] _  in
                        self?.presenter?.connectToWifiDidTap(freeSSID: stay.hotelFreeSSID, paidSSID: stay.hotelPaidSSID)
                    })
                }
            }

            if isPast || stay.cancelled {
                // Rebook maybe?
            } else {
                rows.append(actionCell(
                    icon: #imageLiteral(resourceName: "map"),
                    title: PILocalizedString(
                        "reservationsPlanShortcut",
                        comment: "Reservations list: plan your trip shortcut"
                    )
                ) { [weak self] _ in
                    self?.presenter?.planTripButtonDidTap(hotelCode: stay.hotelCode)
                })
            }

            if SettingsManager.sharedInstance.getKey {
                rows.append(actionCell(
                    icon: #imageLiteral(resourceName: "HAC"),
                    title: PILocalizedString("zaploxKeyShortcut", comment: "Get key for RFID Locker")
                ) { [weak self] _  in
                    self?.presenter?.getKeyButtonDidTap(stay: stay)
                })
            }

            let isCheckInAvailable = isCiolEnabled && (stay.isCheckInOnlineAvailable ?? false)
            if isCheckInAvailable {
                rows.append(checkInButtonRow(
                    title: PILocalizedString("ciolCheckInOnlineTitleButton"),
                    action: { [weak self] _ in
                    self?.presenter?.checkInOnlineDidTap(stay: stay)
                }
                ))
            }

            if stay.isPreCheckedIn {
                rows.append(actionCell(icon: #imageLiteral(resourceName: "keyInstructions"), title: PILocalizedString("ciolGetRoomKey")) { [weak self] _ in
                    self?.presenter?.instructionsDidTap(stay: stay)
                })
            }

            if !stay.isDirect {
                let messageKey = (stay.cancelled || stay.checkedIn || stay.isPast) ?
                "thirdPartyReservationInfoMessageHelpAndSupport" :
                "thirdPartyReservationInfoMessageActive"

                let thirdPartyMessageRow = thirdPartyMessageRow(message: PILocalizedString(messageKey))

                rows.append(thirdPartyMessageRow)
            }

            rows.append(bottomLineRow())

            return FormekaModelSection(header: nil, rows: rows, footer: footer(height: 30))
        }
    }

    private func bottomLineRow() -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BottomBorderCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.topGap = 0
            return cell
        })
    }

    private func infoRow(with message: String) -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.paymentAuthInfo.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 4

            cell.bottomConstraint.constant = 10
            cell.topConstraint.constant = 10

            cell.content.text = message
            cell.content.font = .Body()
            cell.icon.image = #imageLiteral(resourceName: "importantInfo")

            cell.backgroundColor = .white

            return cell
        })
    }

    private func thirdPartyMessageRow(message: String) -> FormekaModelRow {
        let attributedString = NSMutableAttributedString()
        let components = message.split(separator: "\n", maxSplits: 1, omittingEmptySubsequences: false)

        if let first = components.first {
            attributedString.append(
                NSAttributedString(
                    string: String(first),
                    attributes: [.font: UIFont.BodySmall_Bold()]
                )
            )
        }

        if components.count == 2 {
            attributedString.append(NSAttributedString(string: "\n"))
            attributedString.append(
                NSAttributedString(
                    string: String(components[1]),
                    attributes: [.font: UIFont.BodySmall()]
                )
            )
        }

        let row = iconInfoRow(
            tag: ReviewAndBookRow.thirdPartyInfoMessage.rawValue,
            attributedString: attributedString,
            style: .infoAlt
        )

        return row
    }

    func updatedReservationsBannerSection(with message: String = PILocalizedString(
        "reservationsSuccessfulImport",
        comment: "Reservations list: import successful message"
    )) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.text = message
            cell.content.font = .BodySmall()
            cell.content.textColor = .ColourDL1
            cell.icon.image = #imageLiteral(resourceName: "notificationSuccess")
            cell.icon.tintColor = .Tint4
            cell.containerView.backgroundColor = .Tint5
            cell.containerView.layer.cornerRadius = 2
            cell.containerView.layer.borderColor = UIColor.Tint4.withAlphaComponent(0.4).cgColor

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func titleRow(summary: Stay, isPastBooking: Bool) -> FormekaModelRow {
        var title = summary.hotelName
        let subtitle = summary.attributedDatesString
        let guestName = summary.leadGuestName ?? summary.importName ?? summary.lastName

        let bookingStatus: BookingStatus =
        if summary.cancelled {
            .cancelled
        } else if summary.isBusinessTrip {
            .isBusiness
        } else if summary.isPast || summary.basketStatus == .preCheckedOut {
            .isPast
        } else if summary.isPreCheckedIn {
            .isPreCheckIn
        } else if summary.isUpcoming {
            .isUpcoming
        } else {
            .unowned
        }

        for (index, char) in title.enumerated() where char == "(" {
            let indexToInsert = title.index(title.startIndex, offsetBy: index)
            title.insert("\n", at: indexToInsert)
        }

        return FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: ReservationStaySummaryCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.contentView.layoutMargins = UIEdgeInsets(top: 0, left: 90, bottom: 0, right: 90)
            cell.backgroundColor = .clear

            cell.hotelName.attributedText = NSAttributedString(string: title)
            cell.hotelName.textColor = .BasePurple
            cell.stayDates.attributedText = subtitle
            cell.stayDates.textColor = .ColourDL1
            cell.guestName.text = guestName
            cell.guestName.textColor = .ColourDL1
            cell.status.attributedText = bookingStatus.statusText
            cell.status.textColor = bookingStatus.statusTextColor
            cell.status.backgroundColor = bookingStatus.statusBackgroundColor
            cell.status.layer.borderColor = bookingStatus.borderColor.cgColor
            cell.borders.right.width = 1
            cell.borders.left.width = 1
            cell.borders.top.width = 1
            ContentsquareConfig.mask(view: cell.guestName)
            return cell
        })
    }

    func actionCell(icon: UIImage?, title: String?, action: ((IndexPath) -> Void)?) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: ReservationListActionCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .clear
            cell.contentView.layoutMargins = UIEdgeInsets(top: 0, left: 90, bottom: 0, right: 90)
            cell.icon.image = icon?.imageWithColor(.ColourDL5)
            cell.textLabel?.text = title
            cell.textLabel?.textColor = .ColourDL5
            cell.title.font = .Body_Medium()
            cell.borders.right.width = 1
            cell.borders.left.width = 1
            cell.topBorderView.isHidden = true
            cell.bottomBorderView.isHidden = true

            return cell
        }, didSelect: { [unowned self] indexPath, _ in
            table.deselectRow(at: indexPath, animated: true)
            action?(indexPath)
        })
    }

    private func checkInButtonRow(title: String, action: ((IndexPath) -> Void)?) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingCheckInButtonListCell = table.dequeueCell(for: indexPath) else { return nil }
            // Disabling the user interaction will cause the button to have the same action as the entire cell
            // No need to set additional action for the button
            cell.backgroundColor = .clear
            cell.checkInButton.setTitle(
                title,
                for: .normal
            )
            cell.checkInButton.isUserInteractionEnabled = false
            cell.borders.right.width = 1
            cell.borders.left.width = 1
            return cell
        }, didSelect: { [unowned self] indexPath, _ in
            table.deselectRow(at: indexPath, animated: true)
            action?(indexPath)
        })
    }

    func footer(height: CGFloat) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, _ in
            let view = UITableViewHeaderFooterView(frame: .zero)
            view.contentView.backgroundColor = .clear

            return view
        })
    }
}

extension ReservationsListViewController: MyBookingsErrorsViewCellDelegate {
    func actionButtonDidTap(cell: MyBookingsErrorsViewCell) {
        if UserSessionManager.sharedInstance.currentUser != nil {
            presenter?.searchHotelButtonDidTap()
        } else {
            presenter?.loginButtonDidTap()
        }
    }

    func findBookingDidTap(cell: MyBookingsErrorsViewCell) {
        presenter?.findBookingButtonDidTap()
    }
}

private extension ReservationsListViewController {
    func iconInfoRow(
        tag: String,
        attributedString: NSAttributedString,
        style: NotificationStyle
    ) -> FormekaModelRow {
        FormekaModelRow(
            tag: tag,
            cellSetup: { indexPath, _, table in
                guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else {
                    return nil
                }

                let verticalPadding: CGFloat = 16
                let horizontalPadding: CGFloat = 32

                let mutableAttributedString = NSMutableAttributedString(attributedString: attributedString)
                let paragraphStyle = NSMutableParagraphStyle()
                let stringRange = NSRange(location: 0, length: mutableAttributedString.string.count)

                paragraphStyle.lineSpacing = 4
                mutableAttributedString.addAttribute(.paragraphStyle, value: paragraphStyle, range: stringRange)

                cell.bottomConstraint.constant = verticalPadding
                cell.topConstraint.constant = verticalPadding
                cell.leadingConstraint.constant = horizontalPadding
                cell.trailingConstraint.constant = horizontalPadding

                cell.content.attributedText = NSAttributedString(attributedString: mutableAttributedString)
                cell.content.textColor = .ColourDL1
                cell.content.backgroundColor = style.background

                cell.backgroundColor = style.background
                cell.containerView.backgroundColor = style.background
                cell.containerView.layer.borderColor = style.tint.cgColor
                cell.containerView.layer.cornerRadius = 4

                cell.icon.image = style.icon

                // added this to continue the vertical borders for the reservations card view
                cell.showsOuterVerticalBorders = true

                return cell
            },
            didSelect: nil
        )
    }
}
