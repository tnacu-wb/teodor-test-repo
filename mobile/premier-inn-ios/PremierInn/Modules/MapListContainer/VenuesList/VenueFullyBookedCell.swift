//
//  VenueFullyBookedCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 09/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol VenueFullyBookedCellDelegate: AnyObject {
    func venueCellDidTapEditButton()
}

class VenueFullyBookedCell: UICollectionViewCell {
    weak var delegate: VenueFullyBookedCellDelegate?

    // MARK: - Views

    @IBOutlet weak var fullyBookedLabel: RoundedCornersLabel! {
        didSet {
            fullyBookedLabel.text = PILocalizedString("hotelSoldOut", comment: "Hotel: sold out title")
            fullyBookedLabel.textColor = .TintD1
            fullyBookedLabel.font = UIFont.Heading4_Semibold()
        }
    }
    @IBOutlet weak var hotelImage: UIImageView!
    @IBOutlet weak var hotelName: UILabel! {
        didSet {
            hotelName.textColor = .TintD1
            hotelName.font = UIFont.Heading3_Semibold()
        }
    }
    @IBOutlet weak var editDatesButton: RoundedCornersButton! {
        didSet {
            editDatesButton.titleLabel?.textColor = .BasePurple
            editDatesButton.backgroundColor = .BaseWhite
            editDatesButton.titleLabel?.font = UIFont.Heading3_Semibold()
            editDatesButton.layer.borderWidth = 1
            editDatesButton.layer.borderColor = UIColor.BasePurple.cgColor
            editDatesButton.setTitle(
                PILocalizedString("hotelDetailsFullyBookedEditDates", comment: "Hotel details: edit dates button title"),
                for: .normal
            )
            editDatesButton.accessibilityIdentifier = AccessibilityIdentifiers.SearchResults.editDatesButton
        }
    }

    // MARK: - Lifecycle

	override func preferredLayoutAttributesFitting(_ layoutAttributes: UICollectionViewLayoutAttributes)
	    -> UICollectionViewLayoutAttributes {
		let size = contentView.systemLayoutSizeFitting(
		    CGSize(width: UIScreen.main.bounds.size.width, height: UIView.layoutFittingCompressedSize.height),
		    withHorizontalFittingPriority: .required,
		    verticalFittingPriority: .defaultLow
		)

		layoutAttributes.frame.size = size

		return layoutAttributes
	}

    // MARK: - Actions

	@IBAction func editButtonDidTap(_ sender: UIButton) {
        delegate?.venueCellDidTapEditButton()
    }
}

extension VenueFullyBookedCell {
    func setup(with hotel: Hotel) {
        hotelName.text = hotel.name
        hotelName.accessibilityIdentifier = AccessibilityIdentifiers.SearchResults.noAvailabilityHotelName

        hotelImage.image = nil
        hotelImage.accessibilityIdentifier = AccessibilityIdentifiers.SearchResults.noAvailabilityHotelImage

        if let imageURL = hotel.primaryImages.first?.sizedImageURL(withSize: .medium) {
            hotelImage.setImage(with: imageURL, transition: true)
        }
    }
}
