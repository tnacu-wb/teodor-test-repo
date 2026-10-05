//
//  GenericPreferencesCell.swift
//  PremierInn
//
//  Created by Nick Jones on 30/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import Formeka

class GenericPreferencesCell: SimpleSeparatorsCell {
    // MARK: - Views

    @IBOutlet weak var title: UILabel! {
        didSet {
            title.font = .Heading3_Semibold()
            title.textColor = .ColourDL1
        }
    }
    @IBOutlet weak var subTitle: UILabel! {
        didSet {
            subTitle.font = .Body()
            subTitle.textColor = .ColourDL1
        }
    }
    @IBOutlet weak var actionButton: UIButton! {
        didSet {
            actionButton.titleLabel?.font = .Action1()
            actionButton.setTitleColor(.ColourDL5, for: .normal)
        }
    }

    // MARK: - Lifecycle

    required init?(coder: NSCoder) {
        super.init(coder: coder)

        self.contentView.backgroundColor = .ColourLD1
    }

    func configure(withUserPreferenceRow userPreferenceRow: UserPreferenceRow) {
        title.text = userPreferenceRow.title
        subTitle.text = userPreferenceRow.subTitle
        actionButton.setTitle(userPreferenceRow.buttonTitle, for: .normal)
    }
}
