//
//  TripAdvisorView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 16/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class TripAdvisorView: UIView {
    @IBOutlet weak var owlLogo: UIImageView!
	@IBOutlet weak var imageView: UIImageView!
	@IBOutlet weak var numberOfReviewsLabel: UILabel? {
		didSet {
			numberOfReviewsLabel?.font = UIFont.SubtextSmall()
			numberOfReviewsLabel?.textColor = UIColor.lightGray
		}
	}

	func load(viewModel: TripAdvisorViewModel) {
        owlLogo.image = #imageLiteral(resourceName: "owl")
        imageView.image = viewModel.image
        let string = "\(viewModel.numberOfReviews) " + PILocalizedString(
        	"tripAdvisorRatings",
        	comment: "Trip Advisor ratings"
        )
        numberOfReviewsLabel?.attributedText = NSAttributedString(
        	string: string,
        	attributes: [
        		.font: UIFont.SubtextSmall(),
        		.foregroundColor: UIColor.TintD1,
        		.underlineStyle: NSUnderlineStyle.single.rawValue
        	]
        )
	}
}
