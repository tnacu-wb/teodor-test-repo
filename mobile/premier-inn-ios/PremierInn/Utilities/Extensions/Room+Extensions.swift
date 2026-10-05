//
//  Room+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

struct RoomCouple {
    let newRoom: Room
    let oldRoom: Room

    var isOnlyRoom: Bool

    var delimeter: String = "\n"
}

extension RoomCouple {
    private func headerString(with string: String, andSubTitle subtitle: NSAttributedString) -> NSAttributedString {
        let header = NSAttributedString.mutableAttributedStringWith(
            text: string + delimeter,
            lineSpacing: 7.5,
            maxLines: 0,
            font: UIFont.Body(),
            textColor: UIColor.TintD1
        )
            ??
            NSMutableAttributedString(string: string, attributes: [NSAttributedString.Key.font: UIFont.Body()])

        header.append(subtitle)
        return header
    }

    private func subtitleString(with string: String) -> NSAttributedString {
        NSAttributedString.attributedStringWith(
            text: string,
            lineSpacing: 7.5,
            maxLines: 0,
            font: UIFont.Body(),
            textColor: UIColor.TintD1
        )
            ??
            NSAttributedString(string: string, attributes: [NSAttributedString.Key.font: UIFont.Body()])
    }

    // swiftlint:disable:next cyclomatic_complexity
    func differences(withNamesRemoved namesRemoved: Bool, andRoomNumber roomNumber: Int) -> NSAttributedString? {
        var attributedAmendmentStrings = [NSAttributedString]()

        if newRoom.leadGuest?.displayName != oldRoom.leadGuest?.displayName {
            if namesRemoved {
                attributedAmendmentStrings
                    .append(
                        subtitleString(
                            with: "\(String.localizedStringWithFormat(PILocalizedString("Room %d lead guest name changed"), roomNumber))"
                        )
                    )
            } else {
                if let newDisplayName = newRoom.leadGuest?.displayName, oldRoom.leadGuest?.displayName != nil {
                    attributedAmendmentStrings
                        .append(
                            subtitleString(
                                with: "\(String.localizedStringWithFormat(PILocalizedString("Lead guest name changed to %@"), newDisplayName))"
                            )
                        )
                } else {
                    attributedAmendmentStrings
                        .append(subtitleString(with: PILocalizedString("amendRoomLeadGuestNameChanged")))
                }
            }
        }

        if newRoom.type != oldRoom.type {
            let vowelSoundRequired = newRoom.type.localizedName.shouldBePrecededByAn ? "an" : "a"
            attributedAmendmentStrings
                .append(
                    subtitleString(
                        with: "\(String.localizedStringWithFormat(PILocalizedString("Room type changed to \(vowelSoundRequired) %@"), newRoom.type.localizedName))"
                    )
                )
        } else if newRoom.groupId != oldRoom.groupId {
            attributedAmendmentStrings
                .append(
                    subtitleString(
                        with: "\(String.localizedStringWithFormat(PILocalizedString("Room type changed to %@"), newRoom.roomName ?? ""))"
                    )
                )
        }

        if newRoom.adults > oldRoom.adults {
            let numberOfAdults = newRoom.adults - oldRoom.adults

            attributedAmendmentStrings
                .append(
                    subtitleString(
                        with: "\(String.localizedStringWithFormat(PILocalizedString("%d adult(s)", comment: "Message shown for number of adults"), numberOfAdults)) added"
                    )
                )
        }

        if oldRoom.adults > newRoom.adults {
            let numberOfAdults = oldRoom.adults - newRoom.adults

            attributedAmendmentStrings
                .append(
                    subtitleString(
                        with: "\(String.localizedStringWithFormat(PILocalizedString("%d adult(s)", comment: "Message shown for number of adults"), numberOfAdults)) removed"
                    )
                )
        }

        if newRoom.children > oldRoom.children {
            let numberOfChildren = newRoom.children - oldRoom.children


            attributedAmendmentStrings
                .append(
                    subtitleString(
                        with: "\(String.localizedStringWithFormat(PILocalizedString("%d child(children)", comment: "Message shown for number of children"), numberOfChildren)) added"
                    )
                )
        }

        if oldRoom.children > 0 && (newRoom.children < oldRoom.children) {
            let numberOfChildren = oldRoom.children - newRoom.children
            attributedAmendmentStrings
                .append(
                    subtitleString(
                        with: "\(String.localizedStringWithFormat(PILocalizedString("%d child(children)", comment: "Message shown for number of children"), numberOfChildren)) removed"
                    )
                )
        }

        if newRoom.cotRequired && oldRoom.cotRequired == false {
            attributedAmendmentStrings.append(subtitleString(with: PILocalizedString("cotAdded")))
        }

        if newRoom.cotRequired == false && oldRoom.cotRequired {
            attributedAmendmentStrings.append(subtitleString(with: PILocalizedString("cotRemoved")))
        }

        if attributedAmendmentStrings.isEmpty { return nil }

        let finalAttributedString = NSMutableAttributedString()
        for (index, amendmentString) in attributedAmendmentStrings.enumerated() {
            let delimiter = index > 0 ? delimeter : ""

            finalAttributedString.append(NSAttributedString(
                string: delimiter + amendmentString.string,
                attributes: amendmentString.attributes(at: 0, effectiveRange: nil)
            ))
        }

        guard let newRoomLeadGuestName = newRoom.leadGuest?.displayName else { return finalAttributedString }

        let roomNumberOfNoRoomNumber = namesRemoved ? " \(roomNumber)" : ""

        let title = (isOnlyRoom || namesRemoved) ? String.localizedStringWithFormat(
            PILocalizedString("Room%@ changed"),
            roomNumberOfNoRoomNumber
        ) : String.localizedStringWithFormat(PILocalizedString("Room changed (%@)"), newRoomLeadGuestName)
        return headerString(with: title, andSubTitle: finalAttributedString)
    }
}

extension Room {
	var adultsCountDescription: String {
		String.localizedStringWithFormat(
		    PILocalizedString("%d adult(s)", comment: "Message shown for number of adults"),
		    adults
		)
	}

	var childrenCountDescription: String {
		String.localizedStringWithFormat(
		    PILocalizedString("%d child(children)", comment: "Message shown for number of children"),
		    children
		)
	}

	func availableRoomTypes() -> [RoomType] {
		type.availableTypes(adults: adults, children: children, cot: cotRequired)
	}

	var guestsSummary: String {
		var summaryItems: [String] = []

		if adults > 0 {
			summaryItems.append(adultsCountDescription)
		}

		if children > 0 {
			summaryItems.append(childrenCountDescription)
		}

		return summaryItems.joined(separator: ", ")
	}

	var typeSummary: String {
        if let roomName = roomName {
            return roomName
        }

        var result = LettingType(rawValue: lettingType ?? "")?.categoryName ?? "Standard"

        if result == "Standard" {
            result = type.name
        } else {
            result += PILocalizedString(" room")
        }

		if cotRequired {
			result += " " + PILocalizedString(
			    "roomTypeCotDescription",
			    comment: "Additional description when cot is selected"
			)
		}

		return result
	}

    var bookingSummaryRoomDescription: String {
        guestsSummary + ", " + typeSummary
    }

    var isAccessibleRoom: Bool {
        guard type == .accessible else { return false }

        if let specialRequests = options?.compactMap({ $0.specialRequests }).flatMap({ $0 }), specialRequests.isNotEmpty {
            let foundSpecialRequests = specialRequests
                .filter { bathroomSpecialRequests.contains(SpecialRequest(rawValue: $0) ?? .unknown) }

            return foundSpecialRequests.isNotEmpty
        }

        return false
    }

    var isNewTwinRoom: Bool {
        guard type == .twin else { return false }

        if let specialRequests = options?.compactMap({ $0.specialRequests }).flatMap({ $0 }), specialRequests.isNotEmpty {
            let foundSpecialRequests = specialRequests
                .filter { twinRoomSpecialRequests.contains(SpecialRequest(rawValue: $0) ?? .unknown) }

            return foundSpecialRequests.isNotEmpty
        }

        return false
    }
}

extension Sequence where Iterator.Element == Room {
    func squashedById() -> [Room] {
        var newSquashedArray: [Room] = []

        for room in self {
            guard let roomId = room.roomId else { continue }
            if newSquashedArray.contains(where: { $0.roomId == roomId }) == false {
                newSquashedArray.append(room)
            }
        }

        return newSquashedArray
    }
}
