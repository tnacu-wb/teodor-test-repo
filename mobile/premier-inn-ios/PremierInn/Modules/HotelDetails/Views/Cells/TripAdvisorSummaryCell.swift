//
//  TripAdvisorSummaryCell.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 13/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class TripAdvisorSummaryCell: UITableViewCell {
    @IBOutlet weak var rating: UILabel! {
        didSet {
            rating.font = UIFont.XXLargeTitle()
            rating.textColor = .tripAdvisorShamrockGreen
            // what color
        }
    }
    @IBOutlet weak var ratingDescription: UILabel! {
        didSet {
            ratingDescription.font = UIFont.Heading3_Semibold()
            ratingDescription.textColor = .tripAdvisorShamrockGreen
            // what color
        }
    }
    @IBOutlet weak var ratingImage: UIImageView!
    @IBOutlet weak var numberOfReviews: UILabel! {
        didSet {
            numberOfReviews.font = UIFont.Body()
            numberOfReviews.textColor = .TintD1
        }
    }
}
