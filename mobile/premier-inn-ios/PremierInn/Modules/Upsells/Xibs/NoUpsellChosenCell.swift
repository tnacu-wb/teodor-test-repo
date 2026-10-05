//
//  NoUpsellChosenCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 14/04/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class NoUpsellChosenCell: UITableViewCell {
    var addButtonTitle: String?

    @IBOutlet weak var noUpsellDescription: UILabel! {
        didSet {
            noUpsellDescription.text = PILocalizedString("noMealsChosen", comment: "")
            noUpsellDescription.textColor = .TintD1
            noUpsellDescription.font = .Heading4_Semibold()
        }
    }
    @IBOutlet weak var addUpsellButton: AddRoomButton! {
        didSet {
            addUpsellButton.configurationUpdateHandler = { button in
                var config = UIButton.Configuration.plain()
                config.title = self.addButtonTitle
                config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                    var title = attribute
                    title.font = UIFont.Heading3_Semibold()
                    title.foregroundColor = .BaseWhite
                    return title
                }
                config.contentInsets = NSDirectionalEdgeInsets(top: 10, leading: 20, bottom: 10, trailing: 20)
                button.configuration = config
            }

            addUpsellButton.setImage(nil, for: .normal)
            addUpsellButton.borderWidth = 0
            addUpsellButton.cornerRadius = 4
        }
    }

    var buttonAction: (() -> Void)?

    @IBAction func addUpsellButtonDidTap(_ sender: Any) {
        buttonAction?()
    }
}
