//
//  BookingConfirmationEventCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 16/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class BookingConfirmationEventCell: SimpleSeparatorsCell {
    @IBOutlet weak var ctaButton: RoundedCornersTintButton! {
        didSet {
            ctaButton.accessibilityIdentifier = "addToCalendarButtonAcc"

            ctaButton.configurationUpdateHandler = { button in
                var config = UIButton.Configuration.plain()

                if button.state == .normal {
                    config.title = PILocalizedString("addToCalendarButtonTitle", comment: "Add to calendar button title")
                    config.image = UIImage(named: "calendarHDP")
                    config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                        var newAttr = attribute
                        newAttr.font = .Button1()
                        newAttr.foregroundColor = .BasePurple
                        return newAttr
                    }
                } else if button.state == .disabled {
                    config.title = PILocalizedString("addedToCalendarButtonTitle", comment: "Added to calendar button title")
                    config.image = UIImage(named: "tick")
                    config.imageColorTransformer = UIConfigurationColorTransformer { _ in
                        UIColor.BaseWhite
                    }
                    config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                        var newAttr = attribute
                        newAttr.font = .Button1()
                        newAttr.foregroundColor = UIColor.BaseWhite
                        return newAttr
                    }
                }

                var edgeInsets = NSDirectionalEdgeInsets()
                edgeInsets.leading = 12
                edgeInsets.trailing = 12
                config.contentInsets = edgeInsets
                config.titlePadding = 8
                config.imagePadding = 8
                button.configuration = config
            }
        }
    }
}
