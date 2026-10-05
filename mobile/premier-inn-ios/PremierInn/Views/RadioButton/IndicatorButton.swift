//
//  IndicatorButton.swift
//  PremierInn
//
//  Created by Clint Mengolli on 12/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

final class IndicatorButton: UIControl {
    enum ButtonState {
        case selected
        case unselected
        case error
    }

    override var isHighlighted: Bool {
        didSet {
            updateAppearance()
        }
    }

    var style = IndicatorStyling() {
        didSet {
            applyStyle()
        }
    }

    var maskedCorners: CACornerMask = [] {
        didSet {
            layer.maskedCorners = maskedCorners
        }
    }

    var buttonState: ButtonState = .unselected {
        didSet {
            updateAppearance()
        }
    }

    private let outerCircle = UIView()
    private let innerCircle = UIView()
    private let label = UILabel()

    init(title: String) {
        super.init(frame: .zero)
        label.text = title
        setup()
        applyStyle()
        updateAppearance()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setup() {
        isAccessibilityElement = true
        accessibilityTraits = [.button]

        outerCircle.translatesAutoresizingMaskIntoConstraints = false
        innerCircle.translatesAutoresizingMaskIntoConstraints = false

        outerCircle.isUserInteractionEnabled = false
        innerCircle.isUserInteractionEnabled = false

        label.translatesAutoresizingMaskIntoConstraints = false

        label.font = .preferredFont(forTextStyle: .body)
        label.adjustsFontForContentSizeCategory = true

        addSubview(outerCircle)
        addSubview(innerCircle)
        addSubview(label)

        NSLayoutConstraint.activate([
            outerCircle.widthAnchor.constraint(equalToConstant: style.indicatorSize),
            outerCircle.heightAnchor.constraint(equalTo: outerCircle.widthAnchor),
            outerCircle.leadingAnchor.constraint(equalTo: leadingAnchor, constant: style.contentInsets.leading),
            outerCircle.centerYAnchor.constraint(equalTo: centerYAnchor),

            innerCircle.widthAnchor.constraint(equalToConstant: style.innerDotSize),
            innerCircle.heightAnchor.constraint(equalTo: innerCircle.widthAnchor),
            innerCircle.centerXAnchor.constraint(equalTo: outerCircle.centerXAnchor),
            innerCircle.centerYAnchor.constraint(equalTo: outerCircle.centerYAnchor),

            label.leadingAnchor.constraint(equalTo: outerCircle.trailingAnchor, constant: 12),
            label.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -style.contentInsets.trailing),
            label.centerYAnchor.constraint(equalTo: outerCircle.centerYAnchor),

            heightAnchor.constraint(greaterThanOrEqualToConstant: 56)
        ])
    }

    private func applyStyle() {
        layer.cornerRadius = style.cornerRadius
        layer.maskedCorners = maskedCorners

        outerCircle.layer.cornerRadius = style.indicatorSize / 2
        outerCircle.layer.borderWidth = style.indicatorBorderWidth

        innerCircle.layer.cornerRadius = style.innerDotSize / 2

        label.textColor = style.textColor

        updateAppearance()
    }

    private func updateAppearance() {
        let baseColor: UIColor
        let baseBorderWidth: CGFloat

        let isSelected = buttonState == .selected

        if buttonState == .error {
            baseColor = style.borderUnselectedColor
            baseBorderWidth = style.unselectedBorderWidth
        } else {
            baseColor = isSelected ? style.borderSelectedColor : style.borderUnselectedColor
            baseBorderWidth = isSelected ? style.selectedBorderWidth : style.unselectedBorderWidth
        }

        layer.borderColor = baseColor.cgColor
        layer.borderWidth = baseBorderWidth

        outerCircle.layer.borderColor = isSelected ? style.circleSelectedColor.cgColor : style.circleUnselectedColor.cgColor
        innerCircle.backgroundColor = style.circleSelectedColor
        innerCircle.isHidden = !isSelected

        backgroundColor = isSelected ? style.backgroundColorSelected : style.backgroundColorUnselected
    }
}

// MARK: - Styling

struct IndicatorStyling {
    var contentInsets = NSDirectionalEdgeInsets(
        top: 14,
        leading: 16,
        bottom: 14,
        trailing: 16
    )

    var backgroundColorSelected: UIColor = .TintL5
    var backgroundColorUnselected: UIColor = .BaseWhite

    var indicatorSize: CGFloat = 24
    var innerDotSize: CGFloat = 12
    var cornerRadius: CGFloat = 4
    var indicatorBorderWidth: CGFloat = 2

    var errorBorderWidth: CGFloat = 2

    var selectedBorderWidth: CGFloat = 2
    var unselectedBorderWidth: CGFloat = 1

    var borderSelectedColor: UIColor = .Tint1
    var borderUnselectedColor: UIColor = .TintL5

    var circleSelectedColor: UIColor = .Tint1
    var circleUnselectedColor: UIColor = .TintL1

    var borderErrorColor: UIColor = .Tint8

    var textColor: UIColor = .label
}
