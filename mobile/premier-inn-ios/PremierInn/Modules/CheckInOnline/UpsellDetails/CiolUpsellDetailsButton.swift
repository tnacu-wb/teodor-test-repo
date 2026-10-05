//
//  CiolUpsellDetailsButton.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 18.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CiolUpsellDetailsButton: RoundedCornersButton {
    struct Config {
        let titleColor: UIColor
        let backgroundColor: UIColor
        let text: String
        let border: CGFloat
        let enabled: Bool
    }

    enum Title {
        case add
        case addToAll
        case addPeriod(String, Int)
    }
    enum ButtonState {
        case enabled(Title)
        case disabled
        case booked(Bool)
        case hidden

        var config: Config? {
            let titleColor: UIColor
            let bgColor: UIColor
            let title: String
            let border: CGFloat
            let enabled: Bool
            switch self {
            case .enabled(let enabledConfig):
                titleColor = Constants.availableTitleColor
                bgColor = Constants.availableBackgroundColor
                switch enabledConfig {
                case .add: title = Constants.addTitle
                case .addToAll: title = Constants.addToAllTitle
                case .addPeriod(let cost, let nights):
                    let nightsFormatted = (String.localizedStringWithFormat(PILocalizedString("%d night(s)"), nights))
                    title = String.localizedStringWithFormat(
                        PILocalizedString("ciolAddUpsellButtonTitle"),
                        cost,
                        nightsFormatted
                    )
                }
                border = 0.0
                enabled = true
            case .booked(let isMultiRoom):
                titleColor = Constants.bookedTitleColor
                bgColor = Constants.bookedBackgroundColor
                title = isMultiRoom ? Constants.bookedMultiRoomTitle : Constants.bookedTitle
                border = 1.0
                enabled = true
            case .disabled:
                titleColor = Constants.disabledTitleColor
                bgColor = Constants.disabledBackgroundColor
                title = Constants.addTitle
                border = 0.0
                enabled = false
            case .hidden: return nil
            }
            return .init(titleColor: titleColor, backgroundColor: bgColor, text: title, border: border, enabled: enabled)
        }
    }

    enum Constants {
        static let bookedTitleColor: UIColor = .BasePurple
        static let bookedBackgroundColor: UIColor = .BaseWhite
        static let bookedTitle = PILocalizedString("removeTitle")
        static let bookedMultiRoomTitle = PILocalizedString("ciolUpsellRemoveAllTitle")
        static let availableTitleColor: UIColor = .BaseWhite
        static let availableBackgroundColor: UIColor = .Tint1
        static let addTitle = PILocalizedString("Add")
        static let addToAllTitle = PILocalizedString("Add")
        static let disabledTitleColor: UIColor = .TintL1
        static let disabledBackgroundColor: UIColor = .TintL3
    }
    private var buttonState: ButtonState = .disabled {
        didSet {
            configure(state: buttonState)
        }
    }

    private var selectedFont: UIFont = .Body()

    func updateState(buttonState: ButtonState, font: UIFont = .Body()) {
        self.selectedFont = font
        self.buttonState = buttonState
    }

    private func configure(state: ButtonState) {
        guard let config = state.config else { return }
        isEnabled = config.enabled
        backgroundColor = config.backgroundColor
        setAttributedTitle(
            NSAttributedString(
                string: config.text,
                attributes: [
                    .font: selectedFont,
                    .foregroundColor: config.titleColor
                ]
            ),
            for: .normal
        )
        layer.borderWidth = config.border
    }
}
