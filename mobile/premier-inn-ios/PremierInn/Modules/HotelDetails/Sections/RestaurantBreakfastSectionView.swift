//
//  RestaurantBreakfastSectionView.swift
//  PremierInn
//
//  Created by andrei.cojocaru on 7/26/24.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class RestaurantBreakfastSectionView: UIView {
    private let restaurantImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.layer.masksToBounds = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        return imageView
    }()

    private let titleLabel = UILabel.label(
        text: "Placeholder",
        font: .Heading3_Bold(),
        lineHeightMultiple: 0.99,
        textColor: .TintD1,
        numberOfLines: 1,
        accessibilityIdentifier: "restaurantTitleLabel"
    )

    private let descriptionLabel = UILabel.label(
        text: "Placeholder",
        font: .Heading4_Regular(),
        lineHeightMultiple: 1.23,
        textColor: .TintD1,
        accessibilityIdentifier: "restaurantDescriptionLabel"
    )

    private let contentStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.alignment = .leading
        stackView.distribution = .fill
        stackView.axis = .vertical
        return stackView
    }()

    private let stackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.alignment = .fill
        stackView.distribution = .fill
        stackView.axis = .vertical
        stackView.spacing = 8
        return stackView
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        translatesAutoresizingMaskIntoConstraints = false

        setupSubviews()
        setupConstraints()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func configure(with foodContentViewModel: FoodContentViewModel) {
        titleLabel.text = foodContentViewModel.title
        descriptionLabel.text = foodContentViewModel.description

        guard let imageURL = foodContentViewModel.restaurantImageURL else {
            restaurantImageView.isHidden = true
            return
        }
        restaurantImageView.isHidden = false
        restaurantImageView.setImage(with: imageURL)
    }

    private func setupSubviews() {
        addSubview(contentStackView)

        contentStackView.addArrangedSubview(restaurantImageView)
        contentStackView.addArrangedSubview(stackView)

        stackView.addArrangedSubview(titleLabel)
        stackView.addArrangedSubview(descriptionLabel)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            restaurantImageView.heightAnchor.constraint(equalToConstant: 48),
            restaurantImageView.widthAnchor.constraint(equalToConstant: 108),

            contentStackView.topAnchor.constraint(equalTo: topAnchor),
            contentStackView.leadingAnchor.constraint(equalTo: leadingAnchor),
            contentStackView.trailingAnchor.constraint(equalTo: trailingAnchor),
            contentStackView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }
}
