//
//  VenueCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol VenueCellDelegate: AnyObject {
    func photoPriceCellDidTap(cell: VenueCell)
}

class VenueCell: UICollectionViewCell {
    @IBOutlet weak var photoContainer: UIView! {
        didSet {
            photoContainer.backgroundColor = .TintD2
            photoContainer.clipsToBounds = false
            photoContainer.layer.shouldRasterize = true
            photoContainer.layer.rasterizationScale = UIScreen.main.scale
            photoContainer.layer.cornerRadius = 5
        }
    }
    @IBOutlet weak var photo: UIImageView! {
        didSet {
            photo.backgroundColor = .TintD2
            photo.clipsToBounds = true
            photo.layer.cornerRadius = 5
        }
    }
	@IBOutlet weak var fullyBookedLabel: UILabel! {
		didSet {
			fullyBookedLabel.text = PILocalizedString("hotelSoldOut", comment: "Hotel: sold out title")
			fullyBookedLabel.font = UIFont.Heading4_Semibold()
			fullyBookedLabel.textColor = UIColor.TintD1
		}
	}

    @IBOutlet weak var fromPriceView: UIView! {
        didSet {
            fromPriceView.layer.cornerRadius = 3
        }
    }
    @IBOutlet weak var fromPriceTitle: UILabel! {
        didSet {
             fromPriceTitle.font = UIFont.Body()
        }
    }
    @IBOutlet weak var fromPriceLabel: UILabel! {
        didSet {
            fromPriceLabel.font = UIFont.Heading1_Semibold()
        }
    }
    @IBOutlet weak var hotelNameLabel: UILabel!
    @IBOutlet weak var distanceLabel: UILabel! {
        didSet {
            distanceLabel.font = .Body()
            distanceLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var tripAdvisorView: TripAdvisorView!
    @IBOutlet weak var tripAdvisorWidthConstraint: NSLayoutConstraint!
    @IBOutlet weak var tripAdvisorDistanceToParkingViewConstraint: NSLayoutConstraint!

    @IBOutlet weak var parkingImageView: UIImageView! {
        didSet {
            parkingImageView.tintColor = .TintD1
        }
    }
    @IBOutlet weak var parkingLabel: UILabel! {
        didSet {
            parkingLabel.font = UIFont.BodySmall()
            parkingLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var messagingFlagView: UIView! {
        didSet {
            messagingFlagView.backgroundColor = .red
            messagingFlagView.layer.cornerRadius = 1
            messagingFlagView.layer.masksToBounds = true
        }
    }
    @IBOutlet weak var messagingFlagLabel: UILabel! {
        didSet {
            messagingFlagLabel.font = UIFont.Heading4_Semibold()
        }
    }
    @IBOutlet weak var discountAppliedView: UIView! {
        didSet {
            discountAppliedView.backgroundColor = .Tint1
            discountAppliedView.layer.cornerRadius = 15
            discountAppliedView.layer.masksToBounds = true
        }
    }
    @IBOutlet weak var discountAppliedLabel: UILabel! {
        didSet {
            discountAppliedLabel.font = .Heading4_Semibold()
            discountAppliedLabel.textColor = .BaseWhite
            discountAppliedLabel.text = PILocalizedString("discountAppliedBanner")
        }
    }

    weak var delegate: VenueCellDelegate?

     override var isHighlighted: Bool {
        didSet {
            UIView.animate(withDuration: .ocd) {
                self.photoContainer.transform = self.isHighlighted ? CGAffineTransform(scaleX: 0.95, y: 0.95) : .identity
            }
        }
    }
    @IBOutlet weak var bannerRubber: UIView!
    @IBOutlet weak var bannerRubberHeight: NSLayoutConstraint!
    @IBOutlet weak var bannerRubberImage: UIImageView!

	override func preferredLayoutAttributesFitting(_ layoutAttributes: UICollectionViewLayoutAttributes)
	    -> UICollectionViewLayoutAttributes {
        let width = UIDevice.current.userInterfaceIdiom == .pad ? 374 : UIScreen.main.bounds.size.width

		let size = contentView.systemLayoutSizeFitting(
		    CGSize(width: width, height: UIView.layoutFittingCompressedSize.height),
		    withHorizontalFittingPriority: .required,
		    verticalFittingPriority: .defaultLow
		)

        layoutAttributes.frame.size = size

        return layoutAttributes
    }

    private func attributedTitleWithText(text: String, fewRooms: Bool) -> NSAttributedString? {
        var textForLabel = text

        if textForLabel.count > Constants.hotelDetailMaxTitleLength {
            textForLabel = textForLabel.truncated(withLength: Constants.hotelDetailMaxTitleLength)
        }

        let attributes = [
            NSAttributedString.Key.font: UIFont.Heading2_Semibold(),
            NSAttributedString.Key.foregroundColor: UIColor.TintD1
        ]

        let attributedString = NSMutableAttributedString(string: textForLabel, attributes: attributes)

        if fewRooms {
            let attributes = [
                NSAttributedString.Key.font: UIFont.BodySmall(),
                NSAttributedString.Key.foregroundColor: UIColor.deepOrange  // FIXME: no deepOrange colour in new style
            ]

            let attributedFewRoomsString = NSMutableAttributedString(
                string: "  Last\u{a0}few\u{a0}rooms",
                attributes: attributes
            )

            attributedString.insert(attributedFewRoomsString, at: attributedString.string.count)
        }

        return attributedString
    }
}

extension VenueCell {
    func setup(with hotel: Hotel, at index: Int = 0) {
        hotelNameLabel.attributedText = attributedTitleWithText(text: hotel.name, fewRooms: hotel.limitedAvailability)
        hotelNameLabel.accessibilityIdentifier = String(
            format: AccessibilityIdentifiers.SearchResults.hotelNameFormat,
            index
        )

        distanceLabel.attributedText = getDistance(hotel: hotel)

        parkingImageView.image = hotel.parkings.first?.image

        parkingLabel.text = hotel.parkings.first?.title
        parkingLabel.accessibilityIdentifier = String(
            format: AccessibilityIdentifiers.SearchResults.parkingDetailFormat,
            index
        )

        if let tripAdvisorDetails = hotel.tripAdvisorDetails,
           let viewModel = TripAdvisorViewModel.create(from: tripAdvisorDetails) {
            tripAdvisorWidthConstraint.constant = 96
            tripAdvisorDistanceToParkingViewConstraint.constant = 20
            tripAdvisorView.load(viewModel: viewModel)
        } else {
            tripAdvisorWidthConstraint.constant = 0
            tripAdvisorDistanceToParkingViewConstraint.constant = 0
        }
		fullyBookedLabel.isHidden = hotel.available ? true : false

        setupPhoto(hotel: hotel, at: index)

        setupMessagingFlag(hotel: hotel)
        setupDiscountAppliedMessaging(hotel: hotel)
        setupLowestRate(hotel: hotel, at: index)

        setUpBannerRubber(hotel: hotel)
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
            regularFont: UIFont.Body()
        )
    }

    private func setupPhoto(hotel: Hotel, at index: Int = 0) {
		photo.alpha = hotel.available ? 1 : 0.5
		photo.image = nil
        photo.accessibilityIdentifier = String(format: AccessibilityIdentifiers.SearchResults.imageFormat, index)

        guard let imageURL = hotel.primaryImages.first?.sizedImageURL(withSize: .medium) else {
            photo.image = nil
            return
        }

        photo.setImage(with: imageURL, transition: true)
    }

    private func setupMessagingFlag(hotel: Hotel, at index: Int = 0) {
        guard hotel.available else {
            messagingFlagView.isHidden = true
            return
        }

        guard let flag = hotel.messagingFlag else {
            messagingFlagView.isHidden = true
            return
        }

        if flag.text.uppercased().contains(PILocalizedString("NEW", comment: "")) {
            messagingFlagView.accessibilityIdentifier = String(
                format: AccessibilityIdentifiers.SearchResults.newHotelBannerFormat,
                index
            )
        }

        messagingFlagView.isHidden = false
        messagingFlagView.backgroundColor = flag.color
        messagingFlagLabel.textColor = flag.textColor
        messagingFlagLabel.text = flag.text
    }

    private func setupDiscountAppliedMessaging(hotel: Hotel, at index: Int = 0) {
        guard hotel.available else {
            discountAppliedView.isHidden = true
            return
        }

        guard let cellCode = hotel.cheapestRate?.classification,
              cellCode == SimpleNetwork.Constants.EmployeeOffer.rateCode else {
            discountAppliedView.isHidden = true
            return
        }

        discountAppliedView.accessibilityIdentifier = String(
            format: AccessibilityIdentifiers.SearchResults.discountAppliedBannerFormat,
            index
        )

        discountAppliedView.isHidden = false
    }

    private func setupLowestRate(hotel: Hotel, at index: Int = 0) {
        guard hotel.available else {
            fromPriceView.isHidden = true
            return
        }

        guard let cost = hotel.getLowestCost() else {
            fromPriceView.isHidden = true
            return
        }

        fromPriceView.backgroundColor = .Tint7
        fromPriceView.layer.borderColor = UIColor.wheat.cgColor // FIXME: no wheat colour in style
        fromPriceView.isHidden = false
        fromPriceTitle.text = PILocalizedString("hotelDetailsPricesFrom", comment: "Hotel details: prices from label")
        fromPriceLabel.attributedText = fromPriceLabel.fancyMantissaString(
            cost: cost,
            mantissaFontSize: UIFont.Heading3_Semibold().pointSize
        )

        fromPriceTitle.accessibilityIdentifier = String(
            format: AccessibilityIdentifiers.SearchResults.fromRateLabelFormat,
            index
        )
        fromPriceLabel.accessibilityIdentifier = String(
            format: AccessibilityIdentifiers.SearchResults.fromRatePriceFormat,
            index
        )
    }

    private func setUpBannerRubber(hotel: Hotel) {
        bannerRubberHeight.constant = 0

        bannerRubberImage.alpha = 0

        if hotel.brand == .premierInn || hotel.brand == .premierInnGermany { return }

        bannerRubberImage.alpha = 1
        bannerRubberHeight.constant = 41
        bannerRubber.backgroundColor = hotel.brand == .hub ? .HubPrimary : .ZipPrimary
        bannerRubberImage.image = hotel.brand == .hub ? UIImage(named: "hubLogoBanner") : UIImage(named: "zipLogoBanner")
        bannerRubber.layer.cornerRadius = 5
        bannerRubber.layer.maskedCorners = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]
    }
}
