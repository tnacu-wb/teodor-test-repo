//
//  SuggestionExpandCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 11/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

@IBDesignable class SuggestionExpandCell: BorderedContentViewCell {
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.text = PILocalizedString(
                "suggestionsMoreHotelTitle",
                comment: "Suggestions: more hotels button title"
            )
            titleLabel.font = .BodySmall_Semibold()
        }
    }
}
