//
//  BottomBorderedCell.swift
//  PremierInn
//
//  Created by Santa Gurung on 25/02/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class BottomBorderCell: FormekaTableViewCell {
    enum PaddingStyle {
        case noPadding
        case paddedGap(left: CGFloat, right: CGFloat)

        var constraints: (left: CGFloat, right: CGFloat) {
            switch self {
            case .noPadding:
                return (0, 0)
            case let .paddedGap(left, right):
                return (left, right)
            }
        }
    }

    var paddingStyle: PaddingStyle = .paddedGap(left: 10, right: -10) {
        didSet {
            updateViewConstraints()
        }
    }

    var height: CGFloat = 1 {
        didSet {
            heightConstraint?.constant = height
        }
    }

    var topGap: CGFloat = 16 {
        didSet {
            topConstraint?.constant = topGap
        }
    }

    private var leftConstraint: NSLayoutConstraint?
    private var rightConstraint: NSLayoutConstraint?
    private var topConstraint: NSLayoutConstraint?
    private var heightConstraint: NSLayoutConstraint?

    private let containerView: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.ColourLD3
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    override public init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupView()
    }

    public required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupView() {
        selectionStyle = .none
        backgroundColor = .BaseWhite
        contentView.addSubview(containerView)

        let left = containerView.leadingAnchor.constraint(
            equalTo: contentView.leadingAnchor,
            constant: paddingStyle.constraints.left
        )
        let right = containerView.trailingAnchor.constraint(
            equalTo: contentView.trailingAnchor,
            constant: paddingStyle.constraints.right
        )
        let height = containerView.heightAnchor.constraint(equalToConstant: height)
        let top = containerView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: topGap)

        NSLayoutConstraint.activate([
            left,
            top,
            right,
            containerView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
            height
        ])

        leftConstraint = left
        rightConstraint = right
        heightConstraint = height
        topConstraint = top
    }

    private func updateViewConstraints() {
        guard let leftConstraint = leftConstraint,
              let rightConstraint = rightConstraint else { return }

        leftConstraint.constant = paddingStyle.constraints.left
        rightConstraint.constant = paddingStyle.constraints.right
        layoutIfNeeded()
    }
}
