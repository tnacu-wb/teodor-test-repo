//
//  VenueCardCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 28/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class VenueCardCell: UICollectionViewCell {
    @IBOutlet weak var photoView: UIImageView! {
        didSet {
            photoView.backgroundColor = .TintD2
        }
    }
    @IBOutlet weak var photoFaderView: UIView!
    @IBOutlet weak var photoOverlayView: UIView!
    @IBOutlet weak var photoOverlayLabel: RoundedCornersLabel! {
        didSet {
            photoOverlayLabel.text = PILocalizedString("hotelSoldOut", comment: "Hotel: sold out title")
            photoOverlayLabel.font = UIFont.SubtextStrong()
            photoOverlayLabel.textColor = .TintD2
        }
    }
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = UIFont.Heading3_Semibold()
            titleLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var subtitleLabel: UILabel!
    @IBOutlet weak var parkingImageView: UIImageView! {
        didSet {
            parkingImageView.tintColor = .TintD1
        }
    }
    @IBOutlet weak var parkingLabel: UILabel! {
        didSet {
            parkingLabel.font = UIFont.SubtextSmall()
            parkingLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var lineView: UIView! {
        didSet {
            lineView.backgroundColor = .TintL4
        }
    }
    @IBOutlet weak var tripAdvisorImageView: UIImageView!
    @IBOutlet weak var pricesDescriptionLabel: UILabel! {
        didSet {
            let priceFromDescription = UIScreen.main.bounds.size.width <= 320 ?
                PILocalizedString("hotelDetailsFrom", comment: "Hotel details: prices from label") :
                PILocalizedString("hotelDetailsPricesFrom", comment: "Hotel details: prices from label")

            pricesDescriptionLabel.text = priceFromDescription
            pricesDescriptionLabel.font = UIFont.BodySmall()
            pricesDescriptionLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var pricesValueLabel: UILabel! {
        didSet {
            pricesValueLabel.font = UIFont.Heading3_Bold()
            pricesValueLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var arrowImageView: UIImageView! {
        didSet {
            arrowImageView.backgroundColor = .Tint1
            arrowImageView.tintColor = .BaseWhite
            arrowImageView.layer.cornerRadius = 12
        }
    }
    @IBOutlet weak var lastFewRoomsLabel: UILabel! {
        didSet {
            lastFewRoomsLabel.text = PILocalizedString("hotelDetailsLastFewRooms")
            lastFewRoomsLabel.font = .BodySmall_Bold()
            lastFewRoomsLabel.textColor = .Tint6
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)

        setup()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        setup()
    }

    private func setup() {
        layer.masksToBounds = false
        layer.shadowOffset = CGSize(width: 0.0, height: 2.0)
        layer.shadowColor = UIColor.black.cgColor
        layer.shadowOpacity = 0.5
        layer.shouldRasterize = true
        layer.rasterizationScale = UIScreen.main.scale
    }
}

extension VenueCardCell {
    func setup(with hotel: Hotel) {
        selectedBackgroundView?.backgroundColor = .Tint1
        selectedBackgroundView?.layer.cornerRadius = 4
        selectedBackgroundView?.layer.borderWidth = 2
        selectedBackgroundView?.layer.borderColor = UIColor.Tint1.cgColor

        // Hotel image
        photoFaderView.alpha = hotel.available ? 0 : 0.45
        photoView.image = nil
        photoOverlayView.isHidden = hotel.available

        if let imageURL = hotel.primaryImages.first?.sizedImageURL(withSize: .medium) {
            photoView.setImage(with: imageURL, transition: true)
        }

        // Hotel title
        titleLabel.text = hotel.name

        // Distance
        subtitleLabel.attributedText = getDistance(hotel: hotel)

        // Parking
        parkingImageView.image = hotel.parkings.first?.image
        parkingLabel.text = hotel.parkings.first?.title

        // Trip Advisor rating
        if let tripAdvisorDetails = hotel.tripAdvisorDetails {
            tripAdvisorImageView.isHidden = false
            tripAdvisorImageView.image = TripAdvisorDetails.image(with: tripAdvisorDetails.rating)
        } else {
            tripAdvisorImageView.isHidden = true
        }

        // Prices
        pricesDescriptionLabel.isHidden = hotel.available == false
        pricesValueLabel.isHidden = hotel.available == false
        pricesValueLabel.text = hotel.getLowestCost()?.localizedValue

        lastFewRoomsLabel.isHidden = hotel.limitedAvailability == false
    }

    private func getDistance(hotel: Hotel) -> NSAttributedString? {
        guard hotel.distance > 0 else { return nil }

        return LengthFormatter.distanceFormatter.attributedString(
            distance: hotel.distance,
            unit: hotel.distanceUnit,
            suffix: PILocalizedString(
                "hotelDetailsDistanceSuffix",
                comment: "Hotel details: search location distance suffix"
            ),
            boldFont: UIFont.Heading4_Semibold(),
            regularFont: UIFont.Subtext()
        )
    }
}
