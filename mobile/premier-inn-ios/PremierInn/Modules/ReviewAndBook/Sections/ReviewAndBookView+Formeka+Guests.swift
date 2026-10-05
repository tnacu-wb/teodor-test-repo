//
//  ReviewAndBookView+Formeka+Guests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork

extension ReviewAndBookViewController {
    func guestsSection(bookingDetails: BookingDetails) -> FormekaModelSection {
        var guestsRows: [FormekaModelRow] = []
        var roomCount = 1

        func guestRowForRoom(guest: User?) -> FormekaModelRow {
            roomCount += 1
            return guestRow(withGuest: guest)
        }

        let bookerIsFirstRoomGuest = bookingDetails.booker == bookingDetails.criteria.rooms.first?.leadGuest

        if bookerIsFirstRoomGuest == false {
            guestsRows.append(guestRow(withGuest: bookingDetails.booker))

            guestsRows.append(FormekaModelRow(tag: "guestHeaderCell", cellSetup: { indexPath, _, table in
                guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.content.text = PILocalizedString(
                    "reviewLeadGuestDetailsTitle",
                    comment: "Review and Book: lead guest details title"
                )
                cell.content.textColor = .BasePurple
                cell.content.font = .Heading2_Bold()
                cell.content.isUserInteractionEnabled = false
                cell.contentView.backgroundColor = .clear
                cell.messageLeadingConstraint.constant = 16
                cell.messageTopConstraint.constant = 10
                cell.messageBottomConstraint.constant = 10
                cell.hiddenSeparatorLocations = [.top, .bottom]

                return cell
            }))
        }

        guestsRows.append(contentsOf: bookingDetails.criteria.rooms.flatMap { room -> [FormekaModelRow] in
            var roomGuestRows: [FormekaModelRow] = []

            if bookingDetails.criteria.rooms
               .count > 1 || (bookingDetails.criteria.rooms.count == 1 && bookerIsFirstRoomGuest == false) {
                roomGuestRows.append(roomRow(withRoomNumber: roomCount))
            }
            roomGuestRows.append(guestRowForRoom(guest: room.leadGuest))

            return roomGuestRows
        })

        let leadGuestTitle = bookingDetails.booker?.isBusiness == true ? PILocalizedString(
            "reviewBBLeadGuestDetailsTitle",
            comment: "Review and Book: BB lead guest details title"
        ) : PILocalizedString("reviewLeadGuestDetailsTitle", comment: "Review and Book: lead guest details title")
        let sectionTitle = bookerIsFirstRoomGuest ? leadGuestTitle : PILocalizedString(
            "reviewYourDetailsSectionTitle",
            comment: "Review and Book: your details section title"
        )

        guestsRows.append(separatorLineRow(style: .paddedGap(left: 16, right: -16)))

        return FormekaModelSection(
            header: actionHeader(
                withHeading: sectionTitle,
                isActionButtonHidden: bookingDetails.booker?.isBusiness == true,
                action: { [weak self] in
                self?.presenter?.editGuestButtonDidTap()
            }
            ),
            rows: guestsRows,
            footer: nil
        )
    }
}

private extension ReviewAndBookViewController {
    func guestRow(withGuest guest: User?) -> FormekaModelRow {
        FormekaModelRow(tag: "guestDetails\(String(describing: guest?.description))", cellSetup: { indexPath, _, table in
            guard let cell: SimpleSubtitleCell = table.dequeueCell(for: indexPath), let guest = guest else { return nil }

            cell.title.text = "\(guest.title ?? "") \(guest.firstName ?? "") \(guest.lastName ?? "")"
            cell.subtitle.text = guest.emailAddress
            cell.hiddenSeparatorLocations = [.top, .bottom]
            cell.titleLeadingConstraint.constant = 16
            cell.subtitleLeadingConstraint.constant = 16
            cell.bottomConstraint.constant = 16
            ContentsquareConfig.mask(view: cell)
            return cell
        })
    }

    func roomRow(withRoomNumber roomNumber: Int) -> FormekaModelRow {
        FormekaModelRow(tag: "room\(roomNumber)", cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content
                .text = PILocalizedString("genericRoomTitle", comment: "Room information title") + " " +
                String(describing: roomNumber)
            cell.content.textColor = .BaseBlack
            cell.content.font = .Heading4_Semibold()
            cell.content.isUserInteractionEnabled = false
            cell.contentView.backgroundColor = .clear
            cell.messageLeadingConstraint.constant = 16
            cell.messageTopConstraint.constant = 6
            cell.messageBottomConstraint.constant = 3
            cell.hiddenSeparatorLocations = [.bottom]

            return cell
        })
    }
}
