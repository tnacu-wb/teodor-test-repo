//
//  HotelDetailsView+TitleAndSummary.swift
//  PremierInn
//
//  Created by Nick Jones on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

extension HotelDetailsViewController {
    func titleAndSummarySection(with viewModel: TitleAndSummaryViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(hotelNameAndAddressRow(name: viewModel.hotelName))

        if let distanceDescription = viewModel.distanceDescription {
            rows.append(hotelDistanceRow(distanceDescription: distanceDescription))
        }

        if viewModel.hasPremierPlus != false || viewModel.messagingFlag != nil {
            rows.append(hotelBadgesRow(
                hasPremierPlus: viewModel.hasPremierPlus ?? false,
                messagingFlag: viewModel.messagingFlag
            ))
        }

        if let tripAdvisorRatingRow = tripAdvisorRatingRow(tripAdvisorViewModel: viewModel.tripAdvisorViewModel) {
            rows.append(tripAdvisorRatingRow)
        }

        if let limitedOrNoAvailabilityInfo = viewModel.limitedOrNoAvailabilityInfo {
            rows.append(limitedOrNoAvailabilityRow(limitedOrNoAvailabilityInfo: limitedOrNoAvailabilityInfo))
        }

        if viewModel.shouldShowCriteriaSummaryRow {
            rows.append(newCriteriaSummaryRow(
                withCriteriaDatesButtonTitle: viewModel.criteriaDatesButtonTitle,
                andGuestsAndRoomsButtonTitle: viewModel.guestsAndRoomsButtonTitle
            ))
        } else if viewModel.shouldShowCheckAvailabilityRow {
            rows.append(availabilityCheckRow())
        }

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }
}

extension HotelDetailsViewController {
    func simpleFooterView(
        backgroundColor: UIColor,
        showLine: Bool = true,
        withCustomHeight height: CGFloat = 20
    ) -> FormekaModelHeaderFooter? {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, table in
            let view: SimpleFooter? = table.headerFooterView()
            view?.contentView.backgroundColor = backgroundColor
            view?.lineView.backgroundColor = showLine ? UIColor.TintL2 : nil
            return view
        })
    }

    private func hotelNameAndAddressRow(name: String) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.hotelDetailNameAddressCell.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.text = name
            cell.content.font = .Heading1_ExtraBold(29.0)
            cell.content.textColor = .BasePurple
            cell.content.accessibilityTraits.insert(.header)

            cell.messageTopConstraint.constant = 32
            cell.messageLeadingConstraint.constant = 16
            cell.messageTrailingConstraint.constant = 16
            cell.messageBottomConstraint.constant = 4

            return cell
        })
    }

    private func hotelDistanceRow(distanceDescription: NSAttributedString) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.hotelDistanceCell.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.attributedText = distanceDescription

            cell.messageTopConstraint.constant = 4
            cell.messageLeadingConstraint.constant = 16
            cell.messageTrailingConstraint.constant = 16
            cell.messageBottomConstraint.constant = 4

            return cell
        })
    }

    private func hotelBadgesRow(hasPremierPlus: Bool, messagingFlag: MessagingFlag?) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.hotelDistanceCell.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: HotelBadgesCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.stackView.arrangedSubviews.forEach { $0.removeFromSuperview() }
            if hasPremierPlus {
                let premierPlusBadge = UIPaddingLabel(
                    frame: .zero,
                    padding: .init(top: 2, left: 8, bottom: 4, right: 8)
                )
                premierPlusBadge.setupLabel(
                    text: PILocalizedString("premierPlus"),
                    font: .Subtext_Bold(),
                    lineHeightMultiple: 1.23,
                    textAlignment: .center,
                    textColor: .Tint1,
                    numberOfLines: 1,
                    accessibilityIdentifier: "premierPlusBadge"
                )
                premierPlusBadge.layer.borderWidth = 1
                premierPlusBadge.layer.borderColor = UIColor.Tint1.cgColor
                premierPlusBadge.layer.cornerRadius = 12
                cell.stackView.addArrangedSubview(premierPlusBadge)
            }
            if let messagingFlag {
                let messagingFlagBadge = UIPaddingLabel(
                    frame: .zero,
                    padding: .init(top: 2, left: 8, bottom: 4, right: 8)
                )
                messagingFlagBadge.setupLabel(
                    text: messagingFlag.text,
                    font: .Subtext_Bold(),
                    lineHeightMultiple: 1.23,
                    textAlignment: .center,
                    textColor: messagingFlag.color ?? .BasePurple,
                    numberOfLines: 1,
                    accessibilityIdentifier: "messagingFlagBadge"
                )
                messagingFlagBadge.layer.borderWidth = 1
                messagingFlagBadge.layer.borderColor = UIColor.BasePurple.cgColor
                messagingFlagBadge.layer.cornerRadius = 12
                cell.stackView.addArrangedSubview(messagingFlagBadge)
            }
            cell.stackView.addArrangedSubview(UIView())
            return cell
        })
    }

    private func tripAdvisorRatingRow(tripAdvisorViewModel: TripAdvisorViewModel?) -> FormekaModelRow? {
        guard let tripAdvisorViewModel = tripAdvisorViewModel else { return nil }

        return FormekaModelRow(
            tag: HotelDetailRow.tripAdvisorCell.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: TripAdvisorCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.tripAdvisorView.load(viewModel: tripAdvisorViewModel)

                cell.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.hotelReviews

                return cell
            },
            didSelect: { [unowned self] _, _ in
            presenter.tripAdvisorRatingTapped()
        }
        )
    }

    private func limitedOrNoAvailabilityRow(limitedOrNoAvailabilityInfo: (text: String, color: UIColor)) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.hotelFewRoomsCell.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: HotelFewRoomsCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.configureWithPlainText(message: limitedOrNoAvailabilityInfo.text, color: limitedOrNoAvailabilityInfo.color)
            return cell
        })
    }

    private func newCriteriaSummaryRow(
        withCriteriaDatesButtonTitle criteriaDatesButtonTitle: String,
        andGuestsAndRoomsButtonTitle guestsAndRoomsButtonTitle: String
    ) -> FormekaModelRow {
        FormekaModelRow(
            tag: HotelDetailRow.hotelDetailsCriteriaSummaryCell.rawValue,
            cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: HotelDetailsCriteriaSummaryCell = table.dequeueCell(for: indexPath) else { return nil }
            let dateCell = HDPCustomButton(
                iconImage: .calendarHDP,
                title: PILocalizedString("hotelDetailsDateButtonTitle"),
                infoText: criteriaDatesButtonTitle,
                accessibilityIdentifier: AccessibilityIdentifiers.HotelDetails
                                           .navigateToCalendarButton
            ) { [weak self] in
                self?.eventHandler.showCalendarDidTap()
            }
            let guestsCell = HDPCustomButton(
                iconImage: .accountHDP,
                title: PILocalizedString("hotelDetailsGuestsButtonTitle"),
                infoText: guestsAndRoomsButtonTitle,
                accessibilityIdentifier: AccessibilityIdentifiers.HotelDetails
                                             .navigateToGuestAndRooms
            ) { [weak self] in
                self?.eventHandler.showGuestsAndRoomsDidTap()
            }
            cell.stackView.arrangedSubviews.forEach { $0.removeFromSuperview() }
            cell.stackView.addArrangedSubview(dateCell)
            cell.stackView.addArrangedSubview(guestsCell)
            return cell
        }
        )
    }

    private func availabilityCheckRow() -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.availabilityUnchecked.rawValue, cellSetup: { [weak self] indexPath, _, table in
            guard let cell: HotelCheckAvailabilityCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.checkAvailability.actionTouchUp {
                self?.eventHandler.checkAvailabilityButtonTapped()

                self?.datesButtonTapped()
            }

            return cell
        })
    }
}
