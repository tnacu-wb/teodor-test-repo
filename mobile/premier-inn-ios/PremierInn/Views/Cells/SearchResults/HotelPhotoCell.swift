//
//  HotelPhotoCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class HotelPhotoCell: UICollectionViewCell {
    @IBOutlet private weak var photo: UIImageView!
    @IBOutlet private weak var activityIndicator: UIActivityIndicatorView!

	override func prepareForReuse() {
		super.prepareForReuse()

        photo.cancelImageRequest()
        photo.layer.removeAllAnimations()
        photo.image = nil
	}

    func configure(_ url: URL) {
        activityIndicator.startAnimating()

		photo.setImage(with: url, transition: true) { [weak self] _ in
			self?.activityIndicator.stopAnimating()
        }
    }
}
