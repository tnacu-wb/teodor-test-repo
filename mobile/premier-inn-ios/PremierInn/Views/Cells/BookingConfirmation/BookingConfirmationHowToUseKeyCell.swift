//
//  BookingConfirmationHowToUseKeyCell.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 14/11/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class BookingConfirmationHowToUseKeyCell: SimpleSeparatorsCell {
    @IBOutlet weak var ctaButton: RoundedCornersTintButton! {
        didSet {
            ctaButton.accessibilityIdentifier = "howToUseYourKeyButtonAcc"

            ctaButton.configurationUpdateHandler = { button in
                var config = UIButton.Configuration.plain()

                if button.state == .normal {
                    config.title = PILocalizedString("howYourKeyWorksButtonTitle")
                    config.image = UIImage(named: "importantInfo")
                    config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                        var newAttr = attribute
                        newAttr.font = .Button1()
                        newAttr.foregroundColor = .BasePurple
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

            ctaButton.imageView?.tintColor = .BasePurple
            ctaButton.tintColor = .BasePurple
            ctaButton.borderWidth = 1
            ctaButton.cornerRadius = 4
        }
    }
}
