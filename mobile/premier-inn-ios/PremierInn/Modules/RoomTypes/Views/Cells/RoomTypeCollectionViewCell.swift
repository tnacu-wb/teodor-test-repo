//
//  RoomTypeCollectionViewCell.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 08/12/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

class RoomTypeCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var photo: UIImageView!
    @IBOutlet weak var roomTitle: UILabel!
    @IBOutlet weak var roomDescription: UILabel!
    @IBOutlet weak var roomCharacteristics: UILabel!
    @IBOutlet weak var containerView: UIView!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        containerView.layer.borderWidth = 1.0
        containerView.layer.borderColor = UIColor.TintL3.cgColor
        containerView.layer.cornerRadius = 4
        photo.layer.cornerRadius = 4
    }

    func configure(with viewModel: RoomTypesViewModel?, currentRoomTypeSectionIndex: Int, indexPath: IndexPath) {
        let currentRoomTypeSection = viewModel?.roomTypeSegmentData.informationSegments[currentRoomTypeSectionIndex].title
            .lowercased()

        let selectedViewModel = viewModel?.roomCategoryViewModels
            .first(where: { $0.title?.lowercased() == currentRoomTypeSection })?.roomTypeViewModels[indexPath.row]

        if let url = selectedViewModel?.url {
            photo.af.setImage(withURL: url)
        }

        let roomTitleText = selectedViewModel?.roomTitle

        roomTitle.text = roomTitleText
        roomTitle.textColor = UIColor.TintD1
        roomTitle.font = UIFont.Body_Bold()

        let roomDescriptionText = selectedViewModel?.description

        roomDescription.text = roomDescriptionText
        roomDescription.textColor = UIColor.TintD2
        roomDescription.font = UIFont.BodySmall()

        if let characteristics = selectedViewModel?.characteristics {
            let characteristicsText = characteristics.reduce("", { result, characteristic in
                let descriptionText = characteristic.description.isEmpty ? "" : (characteristic.description + "\n")
                return result + "• " + characteristic.title + "\n" + descriptionText
            })

            var ranges: [NSRange] = []
            characteristics.forEach { (title: String, _: String) in
                let range = characteristicsText.ranges(of: "• " + title)
                ranges.append(contentsOf: range)
            }

            let attributedText = characteristicsText.attributedString(
                with: ranges,
                attributes: [.font: UIFont.BodySmall_Bold()]
            )
            // cell.characteristicTickImageView.tintColor = UIColor.BasePurple
            roomCharacteristics.textColor = UIColor.TintD2
            roomCharacteristics.font = UIFont.BodySmall_Bold()
            roomCharacteristics.attributedText = attributedText
        }
    }

    override func preferredLayoutAttributesFitting(_ layoutAttributes: UICollectionViewLayoutAttributes)
        -> UICollectionViewLayoutAttributes {
        layoutAttributes.bounds.size.width = systemLayoutSizeFitting(UIView.layoutFittingCompressedSize).width
        layoutAttributes.bounds.size.height = systemLayoutSizeFitting(UIView.layoutFittingCompressedSize).height

        return layoutAttributes
    }
}
