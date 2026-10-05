//
//  BookedUpsellViewModel.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 04.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation
import UIKit

struct BookedUpsellViewModel {
    var title: String
    var summary: String
    var image: URL?
    var isAvailable: Bool
    var unavailableSummary: NSAttributedString {
        let formattedSummary = NSMutableAttributedString(
            string: PILocalizedString("ciolContactReceptionForRefund"),
            attributes: [
                NSAttributedString.Key.foregroundColor: UIColor.BaseBlack,
                NSAttributedString.Key.font: UIFont.Subtext()
            ]
        )
        formattedSummary.setAttributes(
            [NSAttributedString.Key.font: UIFont.Subtext_Italic()],
            range: NSRange(location: 0, length: PILocalizedString("ciolUnavailable").count)
        )
        return formattedSummary
    }
}

extension BookedUpsellViewModel {
    init(upsell: UpsellItem, isAvailable: Bool, nights: Int) {
        title = upsell.legend
        if upsell.foodUpsell {
            var adultsAndChildren = [String.localizedStringWithFormat(
                PILocalizedString("%d adult(s)", comment: "Message shown for number of adults"),
                upsell.adults ?? 0
            )]
            if upsell.children ?? 0 > 0 {
                adultsAndChildren.append(String.localizedStringWithFormat(
                    PILocalizedString("%d child(children)", comment: "Message shown for number of children"),
                    upsell.children ?? 0
                ))
            }
            let adultsAndChildrenFormatted = adultsAndChildren.joined(separator: " & ")
            summary = [
                adultsAndChildrenFormatted,
                String.localizedStringWithFormat(
                    PILocalizedString("%d night(s)", comment: "Message shown for number of nights"),
                    nights
                )
            ]
                .joined(separator: ", ")
        } else if upsell.wifiUpsell {
            let roomsCount = (upsell.roomId ?? "").components(separatedBy: "/").count
            let roomsFormatted = String.localizedStringWithFormat(
                PILocalizedString("%d room(s)", comment: "Message shown for number of rooms"),
                roomsCount
            )
            summary = [roomsFormatted, String(24 * nights) + PILocalizedString("ciolHours")].joined(separator: ", ")
        } else {
            summary = PILocalizedString("addedLabel")
        }
        image = upsell.image
        self.isAvailable = isAvailable
    }
}
