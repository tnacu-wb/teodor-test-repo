//
//  RemoteImageUIView.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 02/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

final class RemoteImageUIView: UIView {
    private let imageView = UIImageView()
    private let loadingBackgroundView = UIView()

    override init(frame: CGRect) {
        super.init(frame: frame)

        loadingBackgroundView.isHidden = false
        loadingBackgroundView.backgroundColor = UIColor.TintD2

        imageView.contentMode = .scaleAspectFill
        imageView.clipsToBounds = true

        addSubview(loadingBackgroundView)
        addSubview(imageView)

        loadingBackgroundView.translatesAutoresizingMaskIntoConstraints = false
        imageView.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            loadingBackgroundView.topAnchor.constraint(equalTo: topAnchor),
            loadingBackgroundView.leadingAnchor.constraint(equalTo: leadingAnchor),
            loadingBackgroundView.trailingAnchor.constraint(equalTo: trailingAnchor),
            loadingBackgroundView.bottomAnchor.constraint(equalTo: bottomAnchor),

            imageView.topAnchor.constraint(equalTo: topAnchor),
            imageView.leadingAnchor.constraint(equalTo: leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func prepareForReuse() {
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
