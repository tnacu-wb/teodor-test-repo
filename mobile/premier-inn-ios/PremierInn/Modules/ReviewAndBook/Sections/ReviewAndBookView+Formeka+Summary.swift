//
//  ReviewAndBookView+Formeka+Summary.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Formeka
import UIKit

extension ReviewAndBookViewController {
    private func showUpsellsSection(bookingDetails: BookingDetails?) -> Bool {
        guard let bookingDetails = bookingDetails else { return false }
        guard bookingDetails.rate?.hasUserPurchasableUpsells == true else { return false }
        if let company = UserSessionManager.sharedInstance.currentUser?.company {
            let allowedCompanyUpsellsCodes = (company.bookingAllowances?.upsellItemsAllowed)?.compactMap { Int($0) } ?? []
            let hasECILCO = bookingDetails.rate?.extraUpsells?
                .contains(where: { $0.upsellOperaId == .earlyCheckIn || $0.upsellOperaId == .lateCheckOut }) ?? false

            guard bookingDetails.rate?.upsellsAvailable(for: allowedCompanyUpsellsCodes) == true || hasECILCO
                else { return false }
        }
        return true
    }

    func summarySection(with bookingDetails: BookingDetails?) -> FormekaModelSection {
        guard let bookingDetails = bookingDetails else { return FormekaModelSection(header: nil, rows: [], footer: nil) }

        var rows: [FormekaModelRow] = []

        rows.append(bookingDetailsRow(with: bookingDetails))

        if let allowancesRow = allowancesMessageRow(for: bookingDetails) {
            rows.append(allowancesRow)
        }

        rows.append(iconInfoRow(
            tag: ReviewAndBookRow.cancellationMessage.rawValue,
            text: bookingDetails.rate?
            .bookingTermsMessage(with: bookingDetails.hotel?.brand) ?? PILocalizedString("reviewCancellationNotAllowed"),
            style: .info
        ))

        rows.append(separatorLineRow(style: .paddedGap(left: 16, right: -16)))

        let sectionHeader = actionHeader(
            withHeading: PILocalizedString("reviewAndBookSummaryTitle"),
            isActionButtonHidden: true,
            action: { }
        )
        return FormekaModelSection(
            header: sectionHeader,
            rows: rows,
            footer: nil
        )
    }

    private func allowancesMessageRow(for bookingDetails: BookingDetails) -> FormekaModelRow? {
        guard let primaryPaymentMethod = bookingDetails.primaryPaymentMethod?.card else { return nil }

        let dinnerAllowanceAvailableAtHotel: Bool = {
            primaryPaymentMethod.type.isBusiness ? bookingDetails.hotel?.cnpAuthorisation?
                .dinnerAvailable ?? true : bookingDetails.hotel?.cnpAuthorisation?.dinnerAvailableNonBa ?? true
        }()

        let companyAllowancesIncluded: Bool = {
            guard bookingDetails.bookingMode == .business else { return false }
            guard let user = UserSessionManager.sharedInstance.currentUser else { return false }

            if primaryPaymentMethod.type == user.centrallyStoredBusinessCard?.cardType {
                return primaryPaymentMethod.cnpRequired == true
            }

            guard primaryPaymentMethod.type == user.paymentPreference?.card?.cardType else { return false }
            return primaryPaymentMethod.cnpRequired == true
        }()

        let parkingAllowance = UserSessionManager.sharedInstance.currentUser?.company?.bookingAllowances?
            .allowCarParking ?? false && companyAllowancesIncluded
        let mealAllowance = UserSessionManager.sharedInstance.currentUser?.company?.allowance(for: bookingDetails.hotel)?
            .amount != 0 && companyAllowancesIncluded && dinnerAllowanceAvailableAtHotel

        switch (parkingAllowance, mealAllowance) {
        case (true, true):
            return infoTextRow(
                withName: ReviewAndBookRow.dinnerAndParkingAllowanceInfo.rawValue,
                andText: PILocalizedString("bookingReviewParkingAndDinnerAllowance")
            )
        case (true, false):
            return infoTextRow(
                withName: ReviewAndBookRow.parkingAllowanceInfo.rawValue,
                andText: PILocalizedString("bookingReviewParkingAllowance")
            )
        case (false, true):
            return infoTextRow(
                withName: ReviewAndBookRow.dinnerAllowanceInfo.rawValue,
                andText: PILocalizedString("bookingReviewDinnerAllowance")
            )
        default:
            return nil
        }
    }
}

private extension ReviewAndBookViewController {
    private func createDescriptionLabel(text: String?, textColor: UIColor, font: UIFont) -> UILabel {
        let label = UILabel()
        label.text = text
        label.textColor = textColor
        label.font = font
        label.numberOfLines = 0
        return label
    }

    private func bookingDetailsRow(with bookingDetails: BookingDetails) -> FormekaModelRow {
        FormekaModelRow { indexPath, _, table in
            guard let cell: ReviewAndBookSummaryCell = table.dequeueCell(for: indexPath) else { return nil }
            guard let hotel = bookingDetails.hotel else { return nil }

            // Hotel Image and name section
            if let imageUrl = hotel.primaryImages.first?.sizedImageURL(withSize: .small) {
                cell.hotelImageView.setImage(with: imageUrl, transition: true)
            }
            cell.hotelNameLabel.text = hotel.name
            cell.dateLabel.text = "\(bookingDetails.criteria.reviewDatesSummaryShort) • \(bookingDetails.criteria.reviewRoomsSummary)"

            // Upsells breakdown section

            cell.breakdownStackView.arrangedSubviews.forEach { $0.removeFromSuperview() }

            if let donation = bookingDetails.goshDonation {
                let descriptionLabel = self.createDescriptionLabel(
                    text: PILocalizedString("bookingSummaryGoshLabel"),
                    textColor: .TintD1,
                    font: UIFont.Body()
                )
                let priceLabel = self.createDescriptionLabel(
                    text: donation.localizedValue,
                    textColor: .TintD1,
                    font: UIFont.Heading3_Semibold()
                )
                priceLabel.textAlignment = .right

                let stackView = UIStackView(arrangedSubviews: [descriptionLabel, priceLabel])
                stackView.alignment = .top
                cell.breakdownStackView.addArrangedSubview(stackView)
            }

            if self.showUpsellsSection(bookingDetails: bookingDetails) {
                cell.extrasHeaderView.isHidden = false
                cell.breakdownSeparatorView.isHidden = false
                cell.editButton.addTarget(self, action: #selector(self.editUpsellsButtonDidTap), for: .touchUpInside)

                for contents in self.upsellRows(with: bookingDetails) {
                    let descriptionLabel = self.createDescriptionLabel(
                        text: contents.0,
                        textColor: .TintD1,
                        font: UIFont.Body()
                    )
                    let priceLabel = self.createDescriptionLabel(
                        text: contents.1,
                        textColor: .TintD1,
                        font: UIFont.Heading3_Semibold()
                    )
                    priceLabel.textAlignment = .right

                    let stackView = UIStackView(arrangedSubviews: [descriptionLabel, priceLabel])
                    stackView.alignment = .top
                    cell.breakdownStackView.addArrangedSubview(stackView)
                }
            }

            // promotion
            let promotionTagString: NSAttributedString? = {
                guard let tag = bookingDetails.rate?.promotionTag(for: bookingDetails.hotel?.brand) else { return nil }

                return NSAttributedString.attributedStringAndImageForDiscount(label: tag)
            }()

            if let promotionTagString {
                cell.promotionLabel.attributedText = promotionTagString
                cell.promotionLabelHeight.constant = 26
                cell.promotionLabel.isHidden = false
            } else {
                cell.roomTotalLabel.text = nil
                cell.promotionLabelHeight.constant = 0
                cell.promotionLabel.isHidden = true
            }

            let roomsLettingTypes: [RoomLettingOption]? = bookingDetails.roomLettings?.compactMap { $0.options }
                .flatMap { $0 }
            let originalCost = roomsLettingTypes?.nonDiscountedRateCost
            let originalCostString = originalCost?.fancyMantissaString(
                baseAttributes: [
                    .strikethroughStyle: NSUnderlineStyle.single.rawValue,
                    .strikethroughColor: UIColor.ColourDL2
                ],
                mantissaAttributes: [:]
            )
            cell.showOriginalRoomCost(originalCostString: originalCostString)

            // Total summary section
            cell.roomLabel.text = bookingDetails.rateDescriptionAndBreakDownText
            cell.roomTotalLabel.text = bookingDetails.roomCost?.localizedValue
            cell.totalCostLabel.text = PILocalizedString("bookingSummaryTotalLabel", comment: "Booking summary extras title")

            let totalText: String? = {
                // override for Opera
                if let operaConfirmedTotalCost = bookingDetails.totalCostOperaWithDonations {
                    return operaConfirmedTotalCost.localizedValue
                }

                let totalCost = (bookingDetails.cityTaxRequired ? bookingDetails
                    .totalCostWithCityTaxAndExtras : bookingDetails.totalCostWithoutCityTax) ?? bookingDetails.totalCost

                return totalCost.localizedValue
            }()
            cell.totalAmount.text = totalText
            cell.fullBreakdownButton.addTarget(self, action: #selector(self.fullBreakdownDidTap), for: .touchUpInside)

            return cell
        }
    }

    @objc func editUpsellsButtonDidTap() {
        presenter?.editUpsellsButtonDidTap()
    }

    @objc func fullBreakdownDidTap() {
        presenter?.summaryButtonDidTap()
    }

    /**
     Returns an array of rows for any meals chosen as part of the booking

     - parameter bookingDetails: The booking details object from which to build the array of meal rows

     - returns: An array of FormekaModelRow objects
     */
    func upsellRows(with bookingDetails: BookingDetails) -> [(String, String?)] {
        var upsellRows = [(String, String?)]()

        let nightsDescription = Criteria.nightCountDescription(for: bookingDetails.criteria.nights)

        if bookingDetails.paidBreakfasts.isNotEmpty {
            upsellRows.append(contentsOf: bookingDetails.paidBreakfasts.map {
                let upsellTitle = "\($0.upsell.legend)\n(\(String.localizedStringWithFormat(PILocalizedString("%d guest(s)", comment: "Message shown for number of guests"), $0.count)), \(nightsDescription))"
                let upsellCostAmount = ($0.upsell.price.amount.doubleValue * Double($0.count)) *
                    Double(bookingDetails.criteria.nights)
                let upsellTotalCost = Cost(amount: upsellCostAmount, currencyCode: $0.upsell.price.currencyCode)

                return (upsellTitle, upsellTotalCost.localizedValue)
            })
        }

        if bookingDetails.freeBreakfastCount > 0 {
            let freeBreakfastTitle = "\(PILocalizedString("bookingSummaryKidsBreakfastLabel", comment: "Booking summary kids breakfast label"))\n(\(String.localizedStringWithFormat(PILocalizedString("%d child(children)", comment: "Message shown for number of children"), bookingDetails.freeBreakfastCount)), \(nightsDescription))"

            upsellRows.append((
                freeBreakfastTitle,
                PILocalizedString("bookingSummaryKidsBreakfastValue", comment: "Booking summary kids breakfast value")
            ))
        }

        if upsellRows.isEmpty,
           bookingDetails.rate?.foodUpsells(for: UserSessionManager.sharedInstance.currentUser?.company).count ?? 0 > 0 {
            upsellRows.append((PILocalizedString("noMeals"), nil))
        }

        // use wifiUpsells(for company...) when bartId is added for WiFi
        if bookingDetails.extraItems.isNotEmpty {
            upsellRows.append(contentsOf: bookingDetails.extraItems.map {
                let upsellTitle = bookingDetails.criteria.rooms.count == 1 ? $0.upsell.legend : "\($0.upsell.legend)\n(\(String.localizedStringWithFormat(PILocalizedString("%d room(s)", comment: "Message shown for number of rooms"), $0.count)))"
                let upsellCostAmount = $0.upsell.price.amount.doubleValue * Double($0.count)
                let upsellTotalCost = Cost(amount: upsellCostAmount, currencyCode: $0.upsell.price.currencyCode)

                return (upsellTitle, upsellTotalCost.localizedValue)
            })
        }

        return upsellRows
    }
}

private extension BookingDetails {
    var rateDescriptionAndBreakDownText: String {
        let rateDescription: NSMutableString = {
            let bookingSummaryRateTypeLabelTitle = NSMutableString(string: PILocalizedString(
                "bookingSummaryHotelStayLabel",
                comment: "Booking summary hotel stay label"
            ))
            if let description = rate?.name(with: hotel?.brand) {
                bookingSummaryRateTypeLabelTitle.append(": ")
                bookingSummaryRateTypeLabelTitle.append("\(description)")
            }

            return bookingSummaryRateTypeLabelTitle
        }()

        let roomBreakdown: String = {
            if let rooms = roomLettings, checkIfAnyNonSilentRoomsPresent(rooms: rooms) {
                // If there are any rooms with silentSubstitution = false, then we ignore existing implementation for getting room titles and follow this
                return getRoomLettingsLabels(rooms: rooms)
            }

            if let categories = rate?.lettingTypes?.compactMap({ $0.categoryName }),
               categories.filter({ $0 != "Standard" }).isNotEmpty {
                let categoryDictionary = categories.reduce(into: [:]) { $0[$1] = ($0[$1] ?? 0) + 1 }

                var roomBreakdownComponents: [String] = []
                for (category, count) in categoryDictionary {
                    roomBreakdownComponents.append(String.init(
                        format: "%d %@ %@",
                        count,
                        category,
                        String.localizedStringWithFormat(
                            PILocalizedString("room(s)", comment: "Message shown for number of rooms"),
                            count
                        )
                    ))
                }

                return roomBreakdownComponents.joined(separator: ", ")
            } else {
                return criteria.roomBreakdown()
            }
        }()

        rateDescription.append("\n")
        rateDescription.append("(\(criteria.nightsCountDescription), \(roomBreakdown))")

        return rateDescription as String
    }


    func checkIfAnyNonSilentRoomsPresent(rooms: [Room]?) -> Bool {
        guard let rooms = rooms else { return false }

        let roomOptions = rooms.compactMap { room in
            room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first
        }

        let nonSilentRoomOptions = roomOptions.filter { $0.silentSubstitution == false }

        return nonSilentRoomOptions.isNotEmpty
    }

    func getRoomLettingsLabels(rooms: [Room]) -> String {
        // Non silent
        let roomOptions = rooms.compactMap { room in
            room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first
        }

        let nonSilentRoomOptions = roomOptions.filter { $0.silentSubstitution == false }

        let nonSilentRoomLabels = nonSilentRoomOptions
            .map { SettingsManager.sharedInstance.roomLabelFor(lettingType: $0.lettingType ?? "") }

        let dictionary = nonSilentRoomLabels.reduce(into: [String: Int]()) { result, roomLabel in
            let count = result[roomLabel] ?? 0
            result[roomLabel] = count + 1
        }

        var roomBreakdownComponents: [String] = []
        for (roomLabel, count) in dictionary {
            roomBreakdownComponents.append(String(
                format: "%d %@%@",
                count,
                roomLabel,
                String.localizedStringWithFormat(PILocalizedString("(s)"), count)
            ))
        }

        // Silent
        let silentRooms = rooms.filter { room in
            let roomOptions = room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first
            return roomOptions?.silentSubstitution ?? true
        }
        let silentRoomsDescription = [RoomType]([.single, .double, .twin, .family, .accessible])
            .compactMap { $0.localizedDescription(for: silentRooms) }.joined(separator: ", ")

        if silentRoomsDescription.isEmpty {
            return roomBreakdownComponents.joined(separator: ", ")
        } else {
            return roomBreakdownComponents.joined(separator: ", ") + ", " + silentRoomsDescription
        }
    }
}
