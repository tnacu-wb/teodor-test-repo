//
//  GuestDetailsErrorView.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

class EditDetailsErrorView: UIView {
    private lazy var stackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.spacing = 10
        stackView.axis = .horizontal
        stackView.alignment = .top
        stackView.distribution = .fillProportionally
        stackView.backgroundColor = .errorBackground
        stackView.layoutMargins = .init(top: 6, left: 12, bottom: 6, right: 6)
        stackView.isLayoutMarginsRelativeArrangement = true
        stackView.layer.cornerRadius = 4
        stackView.layer.borderWidth = 1.0
        stackView.clipsToBounds = true
        stackView.layer.borderColor = UIColor.clear.cgColor
        return stackView
    }()

    private lazy var errorImage: UIImageView = {
        let im = UIImageView(image: .init(named: "errorIconCiol")?.withRenderingMode(.alwaysOriginal))
        im.heightAnchor.constraint(equalToConstant: 16).isActive = true
        im.widthAnchor.constraint(equalToConstant: 16).isActive = true
        return im
    }()

    private lazy var infoLabel: UILabel = {
        let label = UILabel()
        label.font = .BodySmall()
        label.tintColor = .TintD1
        label.numberOfLines = 0
        label.text = PILocalizedString("guestDetailsFieldError")
        return label
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    private func setup() {
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(stackView)
        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: topAnchor),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor),
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor),
            stackView.trailingAnchor.constraint(equalTo: trailingAnchor)
        ])
        stackView.addArrangedSubview(errorImage)
        stackView.addArrangedSubview(infoLabel)
    }

    func configureError(with text: String) {
        infoLabel.text = text
    }
}
