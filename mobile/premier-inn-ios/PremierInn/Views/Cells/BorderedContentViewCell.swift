//
//  BorderedContentViewCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 14/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Formeka
import UIKit

struct BorderMetrics {
    var width: CGFloat = 0.0
    var offset: CGFloat = 0.0
}

struct Borders {
    var top: BorderMetrics = BorderMetrics(width: 0, offset: 0)
    var bottom: BorderMetrics = BorderMetrics(width: 0, offset: 0)
    var left: BorderMetrics = BorderMetrics(width: 0, offset: 10)
    var right: BorderMetrics = BorderMetrics(width: 0, offset: 10)
    var width: CGFloat = 0.0 {
        didSet {
            top.width = width
            bottom.width = width
            left.width = width
            right.width = width
        }
    }
    var offset: CGFloat = 0.0 {
        didSet {
            top.offset = offset
            bottom.offset = offset
            left.offset = offset
            right.offset = offset
        }
    }
}

@IBDesignable class BorderedContentViewCell: SimpleSeparatorsCell {
    // MARK: - Views

    @IBInspectable var borderColor: UIColor = UIColor.ColourLD3

    var borders = Borders()
    lazy var topBorder: CALayer = {
        let border = CALayer()
        border.backgroundColor = borderColor.cgColor

        return border
    }()
    lazy var bottomBorder: CALayer = {
        let border = CALayer()
        border.backgroundColor = borderColor.cgColor

        return border
    }()
    lazy var leftBorder: CALayer = {
        let border = CALayer()
        border.backgroundColor = borderColor.cgColor

        return border
    }()
    lazy var rightBorder: CALayer = {
        let border = CALayer()
        border.backgroundColor = borderColor.cgColor

        return border
    }()

    // MARK: - Lifecycle

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        setup()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        setup()
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        setup()
    }

    private func setup() {
        textLabel?.backgroundColor = UIColor.clear
        textLabel?.textColor = .ColourDL5
        textLabel?.font = UIFont.Body()
        textLabel?.numberOfLines = 2
        backgroundColor = .ColourLD6
        contentView.backgroundColor = .ColourLD1
        selectionStyle = .none

        contentView.layer.addSublayer(topBorder)
        contentView.layer.addSublayer(bottomBorder)
        contentView.layer.addSublayer(leftBorder)
        contentView.layer.addSublayer(rightBorder)
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        alpha = selected ? 0.6 : 1.0
    }

    override func setHighlighted(_ highlighted: Bool, animated: Bool) {
        super.setHighlighted(highlighted, animated: animated)

        alpha = highlighted ? 0.6 : 1.0
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        let padding: CGFloat = 10
        let x = borders.left.offset + (imageView?.frame.maxX ?? 0)
        let width = bounds.width - (x + padding) - (borders.right.offset + padding)
        textLabel?.frame = CGRect(x: x, y: 0, width: width, height: bounds.height)

        contentView.frame = contentView.frame.inset(by: UIEdgeInsets(
            top: borders.top.offset,
            left: borders.left.offset,
            bottom: borders.bottom.offset,
            right: borders.right.offset
        ))

        topBorder.frame = CGRect(x: 0, y: 0, width: contentView.frame.width, height: borders.top.width)
        bottomBorder.frame = CGRect(
            x: 0,
            y: contentView.frame.height - borders.bottom.width,
            width: contentView.frame.width,
            height: borders.bottom.width
        )
        leftBorder.frame = CGRect(x: 0, y: 0, width: borders.left.width, height: contentView.frame.height)
        rightBorder.frame = CGRect(
            x: contentView.frame.width - borders.right.width,
            y: 0,
            width: borders.right.width,
            height: contentView.frame.height
        )
    }
}
