//
//  RoomPreferencesCell.swift
//  PremierInn
//
//  Created by Nick Jones on 31/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import SimpleNetwork
import Formeka

class StayPreferencesCell: SimpleSeparatorsCell {
    // MARK: - Views

    @IBOutlet weak var title: UILabel! {
        didSet {
            title.font = .Heading3_Semibold()
            title.textColor = .ColourDL1
        }
    }
    @IBOutlet weak var adultsChildrenLabel: UILabel! {
        didSet {
            adultsChildrenLabel.font = .Body()
            adultsChildrenLabel.textColor = .ColourDL1
        }
    }
    @IBOutlet weak var cotLabel: UILabel! {
        didSet {
            cotLabel.font = .Body()
            cotLabel.textColor = .ColourDL1
        }
    }
    @IBOutlet weak var roomTypeLabel: UILabel! {
        didSet {
            roomTypeLabel.font = .Body()
            roomTypeLabel.textColor = .ColourDL1
        }
    }
    @IBOutlet weak var action: UILabel! {
        didSet {
            action.font = .Action1()
            action.textColor = .ColourDL5
        }
    }

    // MARK: - Lifecycle

    required init?(coder: NSCoder) {
        super.init(coder: coder)

        self.contentView.backgroundColor = .ColourLD1
    }

    func configure(withRoomRequirementsRow roomRequirementsRow: RoomRequirementsRow) {
        title.text = roomRequirementsRow.title
        action.text = roomRequirementsRow.buttonTitle
        configureAdultsChildrenLabel(
            numberOfAdults: roomRequirementsRow.adults,
            numberOfChildren: roomRequirementsRow.children
        )

        configureRoomTypeLabel(with: roomRequirementsRow.room)
    }

    private func configureAdultsChildrenLabel(numberOfAdults: Int, numberOfChildren: Int) {
        let adults: String? = numberOfAdults > 0
        ? String.localizedStringWithFormat(
            PILocalizedString("%d Adult(s)", comment: "Message shown for number of adults"),
            numberOfAdults
        )
        : nil

        let children: String? = numberOfChildren > 0
        ? String.localizedStringWithFormat(
            PILocalizedString("%d Child(Children)", comment: "Message shown for number of children"),
            numberOfChildren
        )
        : nil

        adultsChildrenLabel.text = [adults, children].compactMap { $0 }.joined(separator: ", ")
    }

    private func configureRoomTypeLabel(with room: Room?) {
        var roomTypeTitle = room?.type.localizedName.capitalized
        roomTypeTitle?.append(" \(PILocalizedString("genericRoomTitle", comment: "room").lowercased())")

        if room?.cotRequired ?? false {
            cotLabel.text = PILocalizedString("criteriaIncludeCotSwitchLabel")
        } else {
            cotLabel.text = nil
        }

        roomTypeLabel.text = roomTypeTitle
    }
}
