//
//  UIPaddingLabel.swift
//  PremierInn
//
//  Created by Marcello Mascia on 20/09/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

@IBDesignable class UIPaddingLabel: UILabel {
    @IBInspectable private var top: CGFloat = 2
    @IBInspectable private var left: CGFloat = 2
    @IBInspectable private var bottom: CGFloat = 2
    @IBInspectable private var right: CGFloat = 2

    private var padding: UIEdgeInsets {
        UIEdgeInsets(top: top, left: left, bottom: bottom, right: right)
    }

    init(frame: CGRect, padding: UIEdgeInsets) {
        self.top = padding.top
        self.left = padding.left
        self.bottom = padding.bottom
        self.right = padding.right

        super.init(frame: frame)
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override func drawText(in rect: CGRect) {
        super.drawText(in: rect.inset(by: padding))
    }

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        var size = super.sizeThatFits(size)
        size.width += padding.left + padding.right
        size.height += padding.top + padding.bottom

        return size
    }

    override var intrinsicContentSize: CGSize {
        var size = super.intrinsicContentSize
        size.width += padding.left + padding.right
        size.height += padding.top + padding.bottom

        return size
    }

    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()

        self.top = 10
        self.left = 20
        self.bottom = 2
        self.right = 7

        backgroundColor = .green
    }
}
