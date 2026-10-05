//
//  HDPCustomButton.swift
//  PremierInn
//
//  Created by andrei.cojocaru on 8/14/24.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

final class HDPCustomButton: UIButton {
    private let customButtonView = CustomButtonView()

    var didTapButton: (() -> Void)?

    init(
        iconImage: UIImage,
        title: String,
        infoText: String,
        accessibilityIdentifier: String,
        didTapButton: (() -> Void)?
    ) {
        self.didTapButton = didTapButton
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        self.accessibilityIdentifier = accessibilityIdentifier

        addTarget(self, action: #selector(tapButton), for: .touchUpInside)

        customButtonView.iconImageView.image = iconImage
        customButtonView.titleLabel.text = title
        customButtonView.subtitleLabel.text = infoText

        setupButtonLayer()
        setupSubviews()
        setupConstraints()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    @objc func tapButton() {
        didTapButton?()
    }

    func configureInfoText(with text: String) {
        customButtonView.subtitleLabel.text = text
    }

    private func setupButtonLayer() {
        layer.cornerRadius = 4
        layer.borderColor = UIColor.TintL4.cgColor
        layer.borderWidth = 1
    }

    private func setupSubviews() {
        addSubview(customButtonView)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            customButtonView.topAnchor.constraint(equalTo: topAnchor),
            customButtonView.leadingAnchor.constraint(equalTo: leadingAnchor),
            customButtonView.trailingAnchor.constraint(equalTo: trailingAnchor),
            customButtonView.bottomAnchor.constraint(equalTo: bottomAnchor),
            customButtonView.heightAnchor.constraint(equalToConstant: 40)
        ])
    }
}

private class CustomButtonView: UIView {
    let iconImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.contentMode = .scaleAspectFit
        imageView.tintColor = .BasePurple
        return imageView
    }()

    let titleLabel = UILabel.label(
        text: "Placeholder",
        font: .BodySmall_Semibold(),
        lineHeightMultiple: 1.15,
        textColor: .TintD2,
        accessibilityIdentifier: "hdpCustomButtonTitleLabel"
    )
    let subtitleLabel = UILabel.label(
        text: "Placeholder",
        font: .BodySmall(),
        lineHeightMultiple: 1.15,
        textColor: .TintD1,
        accessibilityIdentifier: "hdpCustomButtonSubtitleLabel"
    )

    private let stackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.axis = .horizontal
        stackView.alignment = .fill
        stackView.distribution = .fill
        stackView.spacing = 12
        return stackView
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        isUserInteractionEnabled = false
        translatesAutoresizingMaskIntoConstraints = false

        setupSubviews()
        setupConstraints()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupSubviews() {
        addSubview(stackView)

        stackView.addArrangedSubview(iconImageView)
        stackView.addArrangedSubview(titleLabel)
        stackView.addArrangedSubview(subtitleLabel)
    }

    private func setupConstraints() {
        iconImageView.setContentHuggingPriority(.required, for: .horizontal)
        titleLabel.setContentHuggingPriority(.required, for: .horizontal)
        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: topAnchor, constant: 8),
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 8),
            stackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -8),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -8),

            titleLabel.widthAnchor.constraint(greaterThanOrEqualToConstant: 50)
        ])
    }
}
