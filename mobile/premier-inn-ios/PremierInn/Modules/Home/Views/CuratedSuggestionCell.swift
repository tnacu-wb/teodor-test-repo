//
//  CuratedSuggestionCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 16/01/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class CuratedSuggestionCell: UICollectionViewCell {
    @IBOutlet weak var photo: UIImageView!
    @IBOutlet weak var suggestionName: UILabel! {
        didSet {
            suggestionName.font = .Body_Semibold()
        }
    }
}
