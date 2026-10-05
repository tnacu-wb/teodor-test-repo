//
//  OutLinedTextField.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

final class OutLinedTextField: UIView {
    let titleLabel: UILabel = {
        let label = UIPaddingLabel(
            frame: .zero,
            padding: .init(top: 0, left: 4, bottom: 0, right: 4)
        )
        label.font = .BodySmall()
        label.backgroundColor = .white
        label.textColor = .TintD1
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()

    let leftImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.isHidden = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.widthAnchor.constraint(equalToConstant: 24).isActive = true
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()

    let rightImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.isHidden = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.widthAnchor.constraint(equalToConstant: 24).isActive = true
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()

    let textField: UITextField = {
        let textField = UITextField()
        textField.borderStyle = .none
        textField.translatesAutoresizingMaskIntoConstraints = false
        textField.heightAnchor.constraint(equalToConstant: 54).isActive = true
        return textField
    }()

    private let borderView: UIView = {
        let view = UIView()
        view.layer.borderColor = UIColor.TintL1.cgColor
        view.layer.borderWidth = 1.0
        view.layer.cornerRadius = 4.0
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    private let contentStackView: UIStackView = {
       let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.axis = .horizontal
        stackView.alignment = .fill
        stackView.distribution = .fill
        stackView.spacing = 16
        return stackView
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        translatesAutoresizingMaskIntoConstraints = false

        setupViews()
        setupConstraints()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }

    private func setupViews() {
        addSubview(borderView)
        addSubview(titleLabel)
        borderView.addSubview(contentStackView)

        contentStackView.addArrangedSubview(leftImageView)
        contentStackView.addArrangedSubview(textField)
        contentStackView.addArrangedSubview(rightImageView)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            titleLabel.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16),
            titleLabel.topAnchor.constraint(equalTo: topAnchor),

            borderView.leadingAnchor.constraint(equalTo: leadingAnchor),
            borderView.trailingAnchor.constraint(equalTo: trailingAnchor),
            borderView.topAnchor.constraint(equalTo: titleLabel.centerYAnchor),
            borderView.bottomAnchor.constraint(equalTo: bottomAnchor),

            contentStackView.leadingAnchor.constraint(equalTo: borderView.leadingAnchor, constant: 16),
            contentStackView.trailingAnchor.constraint(equalTo: borderView.trailingAnchor, constant: -16),
            contentStackView.topAnchor.constraint(equalTo: borderView.topAnchor),
            contentStackView.bottomAnchor.constraint(equalTo: borderView.bottomAnchor)
        ])
    }
}
