//
//  UIStackView+Extensions.swift
//  PremierInn
//
//  Created by Filippo Minelle on 04/12/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import UIKit

extension UIStackView {
    /// Set brackground color and radius for the StackView
    /// - Parameters:
    ///   - color: background color (default `clear`)
    ///   - radius: corner radius (default `0.0`)
    func setBackgroundColor(
        _ backgroundColor: UIColor = .clear,
        cornerRadius: CGFloat = 0.0,
        borderWidth: CGFloat = 0.0,
        borderColor: UIColor = .clear
    ) {
        let subView = UIView(frame: bounds)
        subView.backgroundColor = backgroundColor
        subView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        insertSubview(subView, at: 0)

        // Border
        subView.layer.borderWidth = borderWidth
        subView.layer.borderColor = borderColor.cgColor

        // Corner Radius
        subView.layer.cornerRadius = cornerRadius

        subView.layer.masksToBounds = true
        subView.clipsToBounds = true
    }

    func addSeparator(color: UIColor = .TintL3, height: CGFloat = 1.0) {
        let separator = UIView()
        separator.backgroundColor = color
        separator.translatesAutoresizingMaskIntoConstraints = false
        addArrangedSubview(separator)

        NSLayoutConstraint.activate([
            separator.heightAnchor.constraint(equalToConstant: height),
            separator.leadingAnchor.constraint(equalTo: leadingAnchor),
            separator.trailingAnchor.constraint(equalTo: trailingAnchor)
        ])
    }


    func addArrangedSubViewWithSeparator(_ view: UIView, shouldAddSeparator: Bool = true) {
        addArrangedSubview(view)
        if shouldAddSeparator, arrangedSubviews.isNotEmpty {
            addSeparator()
        }
    }
}
