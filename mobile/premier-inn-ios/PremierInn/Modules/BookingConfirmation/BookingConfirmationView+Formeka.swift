//
//  BookingConfirmationView+Formeka.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 11/07/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import Formeka

extension BookingConfirmationViewController {
    func bookingConfirmationViewModel(with viewModel: BookingConfirmationViewModel) -> FormekaViewModel {
        var sections = [FormekaModelSection]()

        sections.append(firstSection(bookingConfirmationViewModel: viewModel))
        if let availableUpsells = viewModel.upsellsViewModel?.availableUpsells, !availableUpsells.isEmpty {
            sections.append(extrasSection(upsells: availableUpsells, isAvailableUpsellsSection: true))
        }
        if let unavailableUpsells = viewModel.upsellsViewModel?.unavailableUpsells, !unavailableUpsells.isEmpty {
            sections.append(extrasSection(upsells: unavailableUpsells, isAvailableUpsellsSection: false))
        }
        sections.append(secondSection(bookingConfirmationViewModel: viewModel))
        sections.append(thirdSection(bookingConfirmationViewModel: viewModel))

        return FormekaViewModel(sections: sections)
    }

    // swiftlint:disable:next cyclomatic_complexity function_body_length
    private func firstSection(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        if bookingConfirmationViewModel.shouldShowOutOfDateBanner {
            rows.append(iconInfoRow(
                tag: BookingConfirmationRowType.error.rawValue,
                text: bookingConfirmationViewModel.errorBannerViewModel?.message,
                attributedString: nil,
                highlightedText: nil,
                topPadding: ViewConstants.LegacyPadding.small,
                bottomPadding: ViewConstants.LegacyPadding.small,
                style: .alert
            ))
        }

        if bookingConfirmationViewModel.shouldShowBannerInfo {
            rows.append(iconInfoRow(
                tag: BookingConfirmationRowType.info.rawValue,
                text: nil,
                attributedString: bookingConfirmationViewModel.infoViewModel.message,
                highlightedText: nil,
                topPadding: ViewConstants.LegacyPadding.small,
                bottomPadding: ViewConstants.LegacyPadding.small,
                style: bookingConfirmationViewModel.infoViewModel.notificationStyle
            ))
        }
        if bookingConfirmationViewModel.shouldShowInfo {
            rows.append(bookingInfoRow(bookingConfirmationViewModel: bookingConfirmationViewModel))
        }

        if let bookingStatus = bookingConfirmationViewModel.bookingCheckStatus {
            rows.append(bookingCheckedStatusRow(bookingCheckStatus: bookingStatus))
        }

        if bookingConfirmationViewModel.shouldShowHotelInfoAlert {
            rows.append(iconInfoRow(
                tag: bookingConfirmationViewModel.hotelInformationAlertViewModel.tag,
                text: bookingConfirmationViewModel.hotelInformationAlertViewModel.text,
                attributedString: bookingConfirmationViewModel.hotelInformationAlertViewModel.attributedString,
                highlightedText: bookingConfirmationViewModel.hotelInformationAlertViewModel.highlightedText,
                topPadding: bookingConfirmationViewModel.hotelInformationAlertViewModel.topPadding,
                bottomPadding: bookingConfirmationViewModel.hotelInformationAlertViewModel.bottomPadding,
                style: bookingConfirmationViewModel.hotelInformationAlertViewModel.style,
                icon: bookingConfirmationViewModel.hotelInformationAlertViewModel.icon,
                actionable: true
            ))
        }

        rows.append(bookingReferenceRow(bookingConfirmationViewModel: bookingConfirmationViewModel))

        if let resendViewModel = bookingConfirmationViewModel.resendInvoiceInfoViewModel {
            rows.append(iconInfoRow(
                tag: BookingConfirmationRowType.resendBanner.rawValue,
                text: nil,
                attributedString: resendViewModel.message,
                highlightedText: nil,
                topPadding: ViewConstants.LegacyPadding.small,
                bottomPadding: ViewConstants.LegacyPadding.small,
                style: resendViewModel.notificationStyle
            ))
        }

        rows.append(hotelRow(bookingConfirmationViewModel: bookingConfirmationViewModel))

        rows.append(checkInOutRow(bookingConfirmationViewModel: bookingConfirmationViewModel))

        if let checkInOutExtrasInfoViewModel = bookingConfirmationViewModel.checkInOutExtrasInfoViewModel {
            rows.append(iconInfoRow(
                tag: checkInOutExtrasInfoViewModel.tag,
                text: checkInOutExtrasInfoViewModel.message,
                attributedString: nil,
                highlightedText: nil,
                topPadding: checkInOutExtrasInfoViewModel.topPadding,
                bottomPadding: checkInOutExtrasInfoViewModel.bottomPadding,
                style: checkInOutExtrasInfoViewModel.style,
                icon: nil
            ))
        }

        if bookingConfirmationViewModel.isCheckInOnlineAvailable {
            rows.append(checkInButtonRow())
        }

        if bookingConfirmationViewModel.isCheckOutOnlineAvailable {
            rows.append(checkOutButtonRow())
        }

        if let keyRows = digitalKeyRows(bookingConfirmationViewModel: bookingConfirmationViewModel) {
            rows.append(contentsOf: keyRows)
        }

        if bookingConfirmationViewModel.shouldShowAddToCalendar {
            rows.append(addToCalendarRow(bookingConfirmationViewModel: bookingConfirmationViewModel))
        }

        if bookingConfirmationViewModel.shouldShowAppleWallet {
            rows.append(addToWalletRow())
        } else if bookingConfirmationViewModel.shouldShowPassExistsButton {
            rows.append(openExistingPassRow())
        }

        if bookingConfirmationViewModel.shouldShowEmployeeOfferBanner {
            rows.append(iconInfoRow(
                tag: bookingConfirmationViewModel.employeeOfferWarningViewModel.tag,
                text: bookingConfirmationViewModel.employeeOfferWarningViewModel.message,
                attributedString: nil,
                highlightedText: nil,
                topPadding: bookingConfirmationViewModel.employeeOfferWarningViewModel.topPadding,
                bottomPadding: bookingConfirmationViewModel.employeeOfferWarningViewModel.bottomPadding,
                style: bookingConfirmationViewModel.employeeOfferWarningViewModel.style,
                icon: nil
            ))
        }

        if bookingConfirmationViewModel.shouldShowKeyLimitMessage {
            rows.append(iconInfoRow(
                tag: BookingConfirmationRowType.keyLimitMessage.rawValue,
                text: PILocalizedString("digitalKeys2KeysPerBookingLimit"),
                attributedString: nil,
                highlightedText: nil,
                topPadding: ViewConstants.LegacyPadding.small,
                bottomPadding: ViewConstants.LegacyPadding.small,
                style: .info,
                icon: nil
            ))
        }

        rows.append(locationRow(bookingConfirmationViewModel: bookingConfirmationViewModel))

        if let accessibilityRowViewModel = bookingConfirmationViewModel.accessibilityViewModel {
            rows.append(accessibilityRow(with: accessibilityRowViewModel))
        }

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func digitalKeyRows(bookingConfirmationViewModel: BookingConfirmationViewModel) -> [FormekaModelRow]? {
        guard SettingsManager.sharedInstance.featureDigitalKeys else { return nil }

        var rows: [FormekaModelRow] = []

        if bookingConfirmationViewModel.digitalKeyInWallet {
            rows.append(openKeyInPassRow())
            rows.append(howYourKeyWorksRow())
        }
        if bookingConfirmationViewModel.shouldShowDigitalKey {
            rows.append(addKeyToWalletRow())
        }
        return rows
    }

    func extrasSection(upsells: [BookedUpsellViewModel], isAvailableUpsellsSection: Bool) -> FormekaModelSection {
        let rows = upsells.map { upsellRow(with: $0)}

        let header = FormekaModelHeaderFooter(height: 40, viewSetup: { _, _ in
            let headerView = UITableViewHeaderFooterView()
            let backgroundView = UIView()
            backgroundView.backgroundColor = .white
            backgroundView.translatesAutoresizingMaskIntoConstraints = false
            let label = UILabel()
            label
                .text = isAvailableUpsellsSection ? PILocalizedString("ciolYourExtras") :
                PILocalizedString("ciolUnavailableExtras")
            label.font = .Body_Bold()
            label.textColor = .BaseBlack
            label.translatesAutoresizingMaskIntoConstraints = false

            headerView.contentView.addSubview(backgroundView)
            headerView.contentView.addSubview(label)
            NSLayoutConstraint.activate([
                backgroundView.leadingAnchor.constraint(equalTo: headerView.contentView.leadingAnchor),
                backgroundView.trailingAnchor.constraint(equalTo: headerView.contentView.trailingAnchor),
                backgroundView.topAnchor.constraint(equalTo: headerView.contentView.topAnchor),
                backgroundView.bottomAnchor.constraint(equalTo: headerView.contentView.bottomAnchor),
                label.leadingAnchor.constraint(equalTo: headerView.contentView.leadingAnchor, constant: 20),
                label.trailingAnchor.constraint(equalTo: headerView.contentView.trailingAnchor, constant: -16),
                label.centerYAnchor.constraint(equalTo: headerView.contentView.centerYAnchor)
            ])
            return headerView
        })
        return FormekaModelSection(header: header, rows: rows, footer: nil)
    }

    private func secondSection(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(priceTotalRow(bookingConfirmationViewModel: bookingConfirmationViewModel))

        rows.append(priceBreakdownRow(bookingConfirmationViewModel: bookingConfirmationViewModel))

        if bookingConfirmationViewModel.shouldShowAmend {
            rows.append(amendBookingRow(bookingConfirmationViewModel: bookingConfirmationViewModel))
        }

        rows.append(faqRow(
            url: bookingConfirmationViewModel.faqUrl,
            title: PILocalizedString("bookingConfirmationFaqTitle")
        ))
        if bookingConfirmationViewModel.shouldShowParkingInformation {
            rows.append(parkingInformationRow())
        }
        if let bookingCheckStatus = bookingConfirmationViewModel.bookingCheckStatus, bookingCheckStatus == .checkIn {
            rows.append(instructionsRow())
        }

        if bookingConfirmationViewModel.shouldShowDigitalKeyFAQ {
            rows.append(faqRow(
                url: bookingConfirmationViewModel.digitalKeyFaqUrl,
                title: PILocalizedString("bookingConfirmationDigitalKeyFaq")
            ))
        }

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func thirdSection(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(callHotelRow(bookingConfirmationViewModel: bookingConfirmationViewModel))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func errorBanner(errorBannerViewModel: ErrorBannerViewModel?) -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.error.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationErrorCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.contentView.backgroundColor = .redBanner
            cell.message.text = errorBannerViewModel?.message

            return cell
        })
    }

    private func bookingCheckedStatusRow(bookingCheckStatus: BookingCheckStatus) -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.status.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationStatusCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.configureCell(with: bookingCheckStatus)
            return cell
        })
    }

    private func bookingInfoRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.success.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationInfoCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.heading.text = bookingConfirmationViewModel.bookingSuccessInfoModel?.title
            cell.heading.accessibilityTraits.insert(.header)
            cell.message.attributedText = bookingConfirmationViewModel.bookingSuccessInfoModel?.message
            ContentsquareConfig.mask(view: cell)
            return cell
        })
    }

    func iconInfoRow(
        tag: String,
        text: String?,
        attributedString: NSAttributedString?,
        highlightedText: String?,
        topPadding: CGFloat,
        bottomPadding: CGFloat,
        style: NotificationStyle,
        icon: String? = nil,
        actionable: Bool? = false
    ) -> FormekaModelRow {
        // this row doesn't have a value so the notNil validator will be triggered
        let validators = style == .error ? [Validator.notNil] : []

        return FormekaModelRow(
            tag: tag,
            onBlurValidators: validators,
            cellSetup: { indexPath, _, table in
                guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.hiddenSeparatorLocations = [.top, .bottom]

                let mutableAttributedString: NSMutableAttributedString = {
                    if let attributedString = attributedString {
                        return NSMutableAttributedString(attributedString: attributedString)
                    } else {
                        return NSMutableAttributedString(string: text ?? "")
                    }
                }()

                let paragraphStyle = NSMutableParagraphStyle()
                paragraphStyle.lineSpacing = 4

                mutableAttributedString.addAttribute(
                    .paragraphStyle,
                    value: paragraphStyle,
                    range: NSRange(location: 0, length: mutableAttributedString.string.count)
                )

                cell.bottomConstraint.constant = bottomPadding
                cell.topConstraint.constant = topPadding

                if let highlightedText = highlightedText,
                   let range = mutableAttributedString.string.ranges(of: highlightedText).first {
                    mutableAttributedString.addAttribute(
                        NSAttributedString.Key.foregroundColor,
                        value: UIColor.BasePurple,
                        range: range
                    )
                }

                cell.content.textColor = .ColourDL1
                cell.content.attributedText = NSAttributedString(attributedString: mutableAttributedString)
                cell.content.font = .BodySmall()
                cell.content.backgroundColor = .clear

                cell.containerView.backgroundColor = style.background
                cell.containerView.layer.borderColor = style.tint.withAlphaComponent(0.4).cgColor
                cell.containerView.layer.cornerRadius = 4

                cell.icon.image = {
                    if let icon = icon {
                        return UIImage(named: icon)
                    }
                    return style.icon
                }()
                cell.icon.tintColor = style.tint

                cell.backgroundColor = .white
                ContentsquareConfig.mask(view: cell)
                return cell
            },
            didSelect: { [unowned self] _, _ in
            guard actionable == true else { return }

            guard let url = Constants.premierInnBaseURL else { return }
            openURL(url: url)
        }
        )
    }

    private func bookingReferenceRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: TitleSubtitleWithButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.selectionStyle = .none
            cell.contentLabel.text = bookingConfirmationViewModel.hotelAndReferenceViewModel.reference
            cell.button.isHidden = bookingConfirmationViewModel.hotelAndReferenceViewModel.shouldShowResendInvoice == false
            cell.button.removeTarget(self, action: nil, for: .touchUpInside)
            cell.button.addTarget(self, action: #selector(self.resendInvoiceButtonTapped), for: .touchUpInside)

            return cell
        })
    }

    private func hotelRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        FormekaModelRow(
            tag: BookingConfirmationRowType.hotel.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: BookingConfirmationHotelInfoCell = table.dequeueCell(for: indexPath) else { return nil }

                if let hotelImage = bookingConfirmationViewModel.hotelAndReferenceViewModel.hotelImage {
                    cell.hotelImageView.setImage(with: hotelImage)
                }
                cell.hotelNameLabel.text = bookingConfirmationViewModel.hotelAndReferenceViewModel.hotelName
                cell.hiddenSeparatorLocations = [.bottom]

                return cell
            },
            didSelect: { [unowned self] _, _ in
            presenter?.hotelInfoDidTap()
        }
        )
    }

    private func checkInOutRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.checkInOut.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: CheckInOutCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.tripTitleLabel.text = bookingConfirmationViewModel.tripSummaryViewModel.title
            cell.tripValueLabel.text = bookingConfirmationViewModel.tripSummaryViewModel.guestsRoomsSummary
            cell.checkInLabel.text = PILocalizedString("arrivingCellLabel", comment: "Arriving cell label")
            cell.checkOutLabel.text = PILocalizedString("leavingCellLabel", comment: "Leaving cell label")
            cell.checkInDateLabel.text = bookingConfirmationViewModel.bookingDatesViewModel.arrivalDateString
            cell.checkInValueLabel.text = bookingConfirmationViewModel.bookingDatesViewModel.checkInValue
            cell.checkOutDateLabel.text = bookingConfirmationViewModel.bookingDatesViewModel.checkOutDateString
            cell.checkOutValueLabel.text = bookingConfirmationViewModel.bookingDatesViewModel.checkOutValue
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        })
    }

    private func checkInButtonRow() -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.checkInButton.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingCheckInButtonCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.buttonTitle = PILocalizedString("ciolCheckInOnlineTitleButton")
            cell.checkInButton.removeTarget(self, action: nil, for: .touchUpInside)
            cell.checkInButton.addTarget(self, action: #selector(self.checkinButtonTapped), for: .touchUpInside)
            cell.hiddenSeparatorLocations = [.bottom]

            return cell
        })
    }

    private func addToCalendarRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.event.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationEventCell = table.dequeueCell(for: indexPath) else { return nil }

            let isAlreadyStored = bookingConfirmationViewModel.eventExistsInCalendar

            cell.ctaButton.tintColor = isAlreadyStored ? .BaseWhite : .BasePurple
            cell.ctaButton.backgroundColor = isAlreadyStored ? .BasePurple : .BaseWhite
            cell.ctaButton.isEnabled = !isAlreadyStored
            cell.ctaButton.addTarget(self, action: #selector(self.addToCalendar), for: .touchUpInside)
            cell.ctaButton.layoutIfNeeded()
            cell.hiddenSeparatorLocations = [.bottom]

            return cell
        })
    }

    private func addKeyToWalletRow() -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.addKeyToWallet.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: AddToWalletCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.addToWalletButton.addTarget(self, action: #selector(self.addKeyToWalletTapped), for: .touchUpInside)
            cell.addToWalletLabel.text = PILocalizedString("digitalKeyAppleWalletTitle")
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        })
    }

    private func addToWalletRow() -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.wallet.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: AddToWalletCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.addToWalletButton.addTarget(self, action: #selector(self.addToWallet), for: .touchUpInside)
            cell.addToWalletLabel.text = nil
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        })
    }

    private func checkOutButtonRow() -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.checkOutButton.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingCheckInButtonCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.buttonTitle = PILocalizedString("ciolCheckOutButtonTitle")
            cell.checkInButton.removeTarget(self, action: nil, for: .touchUpInside)
            cell.checkInButton.addTarget(self, action: #selector(self.checkOutButtonTapped), for: .touchUpInside)
            cell.hiddenSeparatorLocations = [.bottom]

            return cell
        })
    }

    private func openExistingPassRow() -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.passExists.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationViewPassCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.viewPassButton.contentHorizontalAlignment = .center
            cell.viewPassButton.tintColor = .Tint4
            cell.viewPassButton.accessibilityIdentifier = "viewInAppleWalletButtonAcc"
            cell.viewPassButton.backgroundColor = .Tint5
            cell.viewPassButton.setTitleColor(.Tint4, for: .normal)
            cell.viewPassButton.setAttributedStringForAppleWallet(font: .Button1())
            cell.viewPassButton.isEnabled = true
            cell.viewPassButton.addTarget(self, action: #selector(self.openExistingWallet), for: .touchUpInside)
            cell.viewPassButton.layoutIfNeeded()
            cell.hiddenSeparatorLocations = [.bottom]

            return cell
        })
    }

    private func openKeyInPassRow() -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.keyExists.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationViewPassCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.viewPassButton.contentHorizontalAlignment = .center
            cell.viewPassButton.tintColor = .BaseWhite
            cell.viewPassButton.accessibilityIdentifier = "viewInAppleWalletButtonAcc"
            cell.viewPassButton.backgroundColor = .Tint1
            cell.viewPassButton.setTitleColor(.BaseWhite, for: .normal)
            cell.viewPassButton.setTitle(PILocalizedString("bookingConfirmationShowKeyInAppleWallet"), for: .normal)
            cell.viewPassButton.isEnabled = true
            cell.viewPassButton.addTarget(self, action: #selector(self.viewKeyInWallet), for: .touchUpInside)
            cell.viewPassButton.layoutIfNeeded()
            cell.hiddenSeparatorLocations = [.bottom]

            return cell
        })
    }

    private func howYourKeyWorksRow() -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.howYourKeyWorks.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationHowToUseKeyCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.ctaButton.contentHorizontalAlignment = .center
            cell.ctaButton.addTarget(self, action: #selector(self.howYourKeyWorksDidTap), for: .touchUpInside)
            cell.ctaButton.layoutIfNeeded()

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        })
    }

    private func locationRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        let tag = BookingConfirmationRowType.location.rawValue

        return FormekaModelRow(tag: tag, cellSetup: { [weak self] indexPath, _, table in
            guard let cell: LocationDetailsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.didTapDirections = { [weak self] sender in
                self?.tappedMapForDirections(sender: sender)
            }

            cell.mapImageView.loadMapImage(with: [bookingConfirmationViewModel.locationViewModel.hotelAnnotation]) { _ in }
            cell.configure(with: bookingConfirmationViewModel.locationViewModel)

            return cell
        })
    }

    private func accessibilityRow(with accessibilityViewModel: AccessibilityViewModel) -> FormekaModelRow {
        FormekaModelRow(
            tag: BookingConfirmationRowType.accessibilityInformation.rawValue,
            cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationAccessibilityCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.accessibilityButton.setTitle(accessibilityViewModel.callButtonTitle, for: .normal)
            cell.accessibilityDetails.text = accessibilityViewModel.accessibilityDetails

            cell.delegate = self

            return cell
        }
        )
    }

    private func upsellRow(with upsell: BookedUpsellViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: "", cellSetup: { indexPath, _, table in
            guard let cell: BookedUpsellCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.configure(with: upsell)
            cell.hiddenSeparatorLocations = [.top, .bottom]
            return cell
        })
    }

    private func priceTotalRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.priceTotal.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationTotalPriceCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.outstandingAmount.isHidden = true
            cell.outstandingAmountLabel.isHidden = true
            if let amount = bookingConfirmationViewModel.priceTotalViewModel.outstandingAmount {
                cell.outstandingAmount.text = amount
                cell.outstandingAmount.isHidden = false
                cell.outstandingAmountLabel.isHidden = false
            }
            cell.totalPrice.text = bookingConfirmationViewModel.priceTotalViewModel.totalPrice
            cell.rate.text = bookingConfirmationViewModel.priceTotalViewModel.rate

            return cell
        })
    }

    private func priceBreakdownRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        FormekaModelRow(
            tag: BookingConfirmationRowType.priceBreakdown.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: BookingConfirmationPriceBreakdownCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.titleLabel.text = bookingConfirmationViewModel.priceBreakdownViewModel.title
                cell.hiddenSeparatorLocations = [.top, .bottom]
                return cell
            },
            didSelect: { [unowned self] _, _ in
            presenter?.priceBreakdownDidTap()
        }
        )
    }

    private func parkingInformationRow() -> FormekaModelRow {
        FormekaModelRow(
            tag: BookingConfirmationRowType.parkingInformation.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: BookingConfirmationParkingCell = table.dequeueCell(for: indexPath) else { return nil }
                cell.hiddenSeparatorLocations = [.top, .bottom]
                return cell
            },
            didSelect: { [unowned self] _, _ in
            presenter?.parkingInfoDidTap()
        }
        )
    }

    private func instructionsRow() -> FormekaModelRow {
        FormekaModelRow(
            tag: BookingConfirmationRowType.instructions.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: BookingConfirmationInstructionsCell = table.dequeueCell(for: indexPath) else { return nil }
                cell.hiddenSeparatorLocations = [.top, .bottom]
                return cell
            },
            didSelect: { [unowned self] _, _ in
            presenter?.instructionsDidTap()
        }
        )
    }

    private func amendBookingRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        FormekaModelRow(
            tag: BookingConfirmationRowType.amend.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: ManagingBookingActionCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.textLabel?.text = bookingConfirmationViewModel.amendViewModel.title
                cell.detailTextLabel?.text = bookingConfirmationViewModel.amendViewModel.subtitle
                cell.textLabel?.font = .Action3()
                cell.hiddenSeparatorLocations = [.top, .bottom]
                return cell
            },
            didSelect: { [unowned self] _, _ in
            presenter?.amendDidTap()
        }
        )
    }

    private func faqRow(url: URL?, title: String) -> FormekaModelRow {
        FormekaModelRow(
            tag: BookingConfirmationRowType.faq.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: FAQActionCell = table.dequeueCell(for: indexPath) else { return nil }
                cell.hiddenSeparatorLocations = [.top, .bottom]
                cell.actionFAQ.text = title
                return cell
            },
            didSelect: { [unowned self] _, _ in
            presenter?.faqDidTap(url: url)
        }
        )
    }

    private func callHotelRow(bookingConfirmationViewModel: BookingConfirmationViewModel) -> FormekaModelRow {
        FormekaModelRow(
            tag: BookingConfirmationRowType.callHotel.rawValue,
            cellSetup: { indexPath, _, table in
            guard let cell: CallHotelCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.callChargeInformationLabel.text = bookingConfirmationViewModel.callHotelViewModel.callChargeInformation
            cell.callHotelButton.setTitle(bookingConfirmationViewModel.callHotelViewModel.callDescription, for: .normal)
            cell.callHotelButton.addTarget(self, action: #selector(self.callHotelButtonDidTap), for: .touchUpInside)

            return cell
        }
        )
    }

    // Button actions
    @objc private func resendInvoiceButtonTapped() {
        presenter?.resendInvoiceDidTap()
    }

    @objc private func checkinButtonTapped() {
        presenter?.tapOnCheckIn()
    }

    @objc func checkOutButtonTapped() {
        presenter?.tapOnCheckOut()
    }

    @objc private func addKeyToWalletTapped() {
        presenter?.addKeyToWalletTap()
    }

    @objc private func addToCalendar() {
        self.presenter?.addToCalendar()
    }

    @objc private func addToWallet() {
        self.presenter?.addToWallet()
    }

    @objc private func openExistingWallet() {
        self.presenter?.openExistingWalletDidTap()
    }

    @objc private func viewKeyInWallet() {
        self.presenter?.viewKeyInWallet()
    }

    @objc private func howYourKeyWorksDidTap() {
        self.presenter?.howYourKeyWorksDidTap()
    }

    @objc private func tappedMapForDirections(sender: UIView) {
        self.presenter?.showDirections(withSender: sender)
    }

    @objc private func tappedButtonForDirections(sender: UIView) {
        self.presenter?.showDirections(withSender: sender)
    }

    @objc private func callHotelButtonDidTap() {
        self.presenter?.callHotelButtonDidTap()
    }
}
