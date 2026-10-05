//
//  InlineMessageView.swift
//  PremierInn
//
//  Created by Clint Mengolli on 13/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

final class InlineMessageView: UIView {
    private enum Constants {
        static let imagePadding: CGFloat = 12
        static let contentPadding: CGFloat = 12
        static let labelLeadingPadding: CGFloat = 8
    }

    private let shapeLayer = CAShapeLayer()

    private let tailHost: TailHost
    private let tailDimensions: CGSize
    private let tailAlignment: TailAlignment

    private let text: String

    private lazy var label: UILabel = {
        let label = UILabel()
        label.text = text
        label.font = styling.textFont
        label.lineBreakMode = .byWordWrapping
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()

    private lazy var iconImage: UIImageView = {
        let uiImage = UIImage(named: styling.iconResourceName)

        if let iconTint = styling.iconTint {
            uiImage?.withTintColor(iconTint, renderingMode: .alwaysTemplate)
        }

        let imageView = UIImageView(image: uiImage)
        imageView.translatesAutoresizingMaskIntoConstraints = false

        return imageView
    }()

    var styling: InlineMessageStylingType

    init(
        text: String,
        style: InlineMessageStylingPreset,
        arrowPlacement: TailHost,
        tailDimensions: CGSize
    ) {
        self.text = text
        self.styling = style.config
        self.tailHost = arrowPlacement
        self.tailAlignment = tailHost.alignment
        self.tailDimensions = tailDimensions
        super.init(frame: .zero)

        setupDrawProperties()
        setupView()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func setText(_ text: String) {
        label.text = text
        setNeedsLayout()
        setNeedsDisplay()
    }

    override var intrinsicContentSize: CGSize {
        let labelSize = label.intrinsicContentSize

        let viewHeight = max(labelSize.height, styling.iconSize)
        + Constants.contentPadding
        + tailDimensions.height

        let viewWidth = labelSize.width
        + styling.iconSize
        + Constants.imagePadding
        + Constants.contentPadding

        return CGSize(width: viewWidth, height: viewHeight)
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        let path = CustomDrawBuilder(
            rect: bounds,
            cornerRadius: styling.cornerRadius,
            tailHost: tailHost,
            tailDimensions: tailDimensions,
            tailAlignment: tailAlignment
        ).build()

        shapeLayer.frame = bounds
        shapeLayer.path = path.cgPath
        shapeLayer.fillColor = styling.backgroundColor.cgColor
        shapeLayer.strokeColor = styling.borderColor.cgColor
        shapeLayer.lineWidth = styling.borderWidth
    }

    private func setupDrawProperties() {
        isOpaque = false
        backgroundColor = .clear

        layer.insertSublayer(shapeLayer, at: 0)
        shapeLayer.contentsScale = UIScreen.main.scale
        shapeLayer.lineJoin = .round
        shapeLayer.lineCap = .round
    }

    private func setupView() {
        addSubview(iconImage)
        addSubview(label)

        iconImage.translatesAutoresizingMaskIntoConstraints = false
        label.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            iconImage.leadingAnchor.constraint(equalTo: leadingAnchor, constant: Constants.imagePadding),
            iconImage.topAnchor.constraint(equalTo: topAnchor, constant: topAnchorOffset),
            iconImage.bottomAnchor.constraint(equalTo: bottomAnchor, constant: bottomAnchorOffset),
            iconImage.widthAnchor.constraint(equalToConstant: styling.iconSize),
            iconImage.heightAnchor.constraint(equalToConstant: styling.iconSize),

            label.leadingAnchor.constraint(equalTo: iconImage.trailingAnchor, constant: Constants.labelLeadingPadding),
            label.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -Constants.contentPadding),
            label.centerYAnchor.constraint(equalTo: iconImage.centerYAnchor)
        ])
    }

    private var topAnchorOffset: CGFloat {
        switch tailHost {
        case .topBorder:
            return Constants.contentPadding + tailDimensions.height
        case .bottomBorder:
            return Constants.contentPadding
        case .none:
            return Constants.contentPadding
        }
    }

    private var bottomAnchorOffset: CGFloat {
        switch tailHost {
        case .topBorder:
            return -tailDimensions.height
        case .bottomBorder:
            return -tailDimensions.height - Constants.imagePadding
        case .none:
            return -Constants.contentPadding
        }
    }
}
