//
//  RemoteImageCarouselCell.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 24/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

final class RemoteImageCarouselCell: UICollectionViewCell {
    static let reuseIdentifier = String(describing: RemoteImageCarouselCell.self)

    private let imageView = UIImageView()
    private let loadingBackgroundView = UIView()

    override init(frame: CGRect) {
        super.init(frame: frame)

        loadingBackgroundView.isHidden = false
        loadingBackgroundView.backgroundColor = UIColor.TintD2

        imageView.contentMode = .scaleAspectFill
        imageView.clipsToBounds = true

        contentView.addSubview(loadingBackgroundView)
        contentView.addSubview(imageView)

        loadingBackgroundView.translatesAutoresizingMaskIntoConstraints = false
        imageView.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            loadingBackgroundView.topAnchor.constraint(equalTo: contentView.topAnchor),
            loadingBackgroundView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            loadingBackgroundView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            loadingBackgroundView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),

            imageView.topAnchor.constraint(equalTo: contentView.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func prepareForReuse() {
        super.prepareForReuse()

        imageView.cancelImageRequest()
        imageView.image = nil
        loadingBackgroundView.isHidden = false
    }

    func configure(
        with url: URL?,
        urlImageSize: URLImageSize = .medium
    ) {
        imageView.cancelImageRequest()
        imageView.image = nil
        loadingBackgroundView.isHidden = false

        guard let url,
              let imageURL = url.sizedImageURL(withSize: urlImageSize) else {
            return
        }

        imageView.setImage(with: imageURL, transition: true)
    }
}
