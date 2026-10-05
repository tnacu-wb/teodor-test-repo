//
//  FlexibleContentInformationCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 12/04/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class FlexibleContentInformationCell: SimpleSeparatorsCell {
    private enum Constants {
        static let defaultIconResource: String = "newCross"
        static let defaultBorderInset: CGFloat = 10
        static let defaultBorderWidth: CGFloat = 1
        static let yOffset: CGFloat = 0
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        contentView.layer.addSublayer(outerLeftBorderLayer)
        contentView.layer.addSublayer(outerRightBorderLayer)
        updateOuterBorderVisibility()
    }

    override func prepareForReuse() {
        super.prepareForReuse()

        showsOuterVerticalBorders = false
        outerBorderInset = Constants.defaultBorderInset
        outerBorderWidth = Constants.defaultBorderWidth
        outerBorderColor = .ColourLD3
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        guard showsOuterVerticalBorders else {
            return
        }

        setupBorderPositions()
    }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        containerView.layer.borderColor = UIColor.Tint2.cgColor
        outerLeftBorderLayer.backgroundColor = outerBorderColor.cgColor
        outerRightBorderLayer.backgroundColor = outerBorderColor.cgColor
    }

    // MARK: - Views

    var isDismissed: (() -> Void)?

    @IBOutlet weak var icon: UIImageView! {
        didSet {
            icon.tintColor = .BaseWhite
        }
    }
    @IBOutlet weak var content: UILabel! {
        didSet {
            content.font = .Body()
            content.textColor = .BaseWhite
        }
    }
    @IBOutlet weak var containerView: UIView! {
        didSet {
            containerView.backgroundColor = .Tint2
            containerView.layer.borderColor = UIColor.Tint2.cgColor
        }
    }

    @IBOutlet weak var crossIcon: UIButton! {
        didSet {
            crossIcon.imageView?.image = UIImage(named: Constants.defaultIconResource)
            crossIcon.isHidden = true
        }
    }

    // MARK: - Constraints

    @IBOutlet weak var topConstraint: NSLayoutConstraint!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var leadingConstraint: NSLayoutConstraint!
    @IBOutlet weak var trailingConstraint: NSLayoutConstraint!

    // MARK: - Optional border properties for reservations view card

    var showsOuterVerticalBorders: Bool = false {
        didSet {
            updateOuterBorderVisibility()
            setNeedsLayout()
        }
    }

    var outerBorderInset: CGFloat = Constants.defaultBorderInset {
        didSet {
            setNeedsLayout()
        }
    }

    var outerBorderWidth: CGFloat = Constants.defaultBorderWidth {
        didSet {
            setNeedsLayout()
        }
    }

    var outerBorderColor: UIColor = .ColourLD3 {
        didSet {
            outerLeftBorderLayer.backgroundColor = outerBorderColor.cgColor
            outerRightBorderLayer.backgroundColor = outerBorderColor.cgColor
        }
    }

    private let outerLeftBorderLayer: CALayer = {
        let layer = CALayer()
        layer.isHidden = true
        return layer
    }()

    private let outerRightBorderLayer: CALayer = {
        let layer = CALayer()
        layer.isHidden = true
        return layer
    }()

    // MARK: - Action

    @IBAction func crossIconClicked(_ sender: Any) {
        isDismissed?()
    }
}

private extension FlexibleContentInformationCell {
    func setupBorderPositions() {
        let height = contentView.bounds.height
        let width = contentView.bounds.width
        let inset = outerBorderInset
        let borderWidth = outerBorderWidth

        outerLeftBorderLayer.frame = CGRect(
            x: inset,
            y: Constants.yOffset,
            width: borderWidth,
            height: height
        )

        outerRightBorderLayer.frame = CGRect(
            x: width - inset - borderWidth,
            y: Constants.yOffset,
            width: borderWidth,
            height: height
        )
    }

    func updateOuterBorderVisibility() {
        outerLeftBorderLayer.isHidden = !showsOuterVerticalBorders
        outerRightBorderLayer.isHidden = !showsOuterVerticalBorders
        outerLeftBorderLayer.backgroundColor = outerBorderColor.cgColor
        outerRightBorderLayer.backgroundColor = outerBorderColor.cgColor
    }
}
