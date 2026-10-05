//
//  CustomButtons.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

extension UIButton {
    func addIcon(icon: UIImage, with tint: UIColor) {
        if viewWithTag(999) == nil {
            let imageView = UIImageView(image: icon)
            imageView.frame = bounds.insetBy(dx: 20, dy: 0)
            imageView.contentMode = .right
            imageView.tintColor = tint
            imageView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
            imageView.tag = 999
            addSubview(imageView)
        }
    }

    func removeIcon() {
        if let icon = viewWithTag(999) {
            icon.removeFromSuperview()
        }
    }
}

class ButtonWithBlocks: UIButton {
    private var actionBlock: (() -> Void)?

    func actionTouchUp(block: @escaping () -> Void) {
        actionBlock = block
        addTarget(self, action: #selector(actionTouchUpPressed), for: [.touchUpInside])
    }

    @objc func actionTouchUpPressed() {
        actionBlock?()
    }
}

@IBDesignable class RoundedCornersButton: ButtonWithBlocks {
	@IBInspectable var cornerRadius: CGFloat = 0.0 {
		didSet {
			setup()
		}
	}
    @IBInspectable var borderWidth: CGFloat = 0.0
    @IBInspectable var adjustsFontSizeToFitWidth: Bool = false
    @IBInspectable var shadowOffsetWidth: CGFloat = 0
    @IBInspectable var shadowOffsetHeight: CGFloat = 0
    @IBInspectable var shadowOpacity: Float = 0.0

    override init(frame: CGRect) {
        super.init(frame: frame)

        setup()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        setup()
    }

    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()

        setup()
    }

    func setup() {
        layer.cornerRadius = cornerRadius
        layer.borderColor = tintColor.cgColor
        layer.borderWidth = borderWidth

        layer.masksToBounds = shadowOpacity > 0 ? false : true
        layer.shadowOffset = CGSize(width: shadowOffsetWidth, height: shadowOffsetHeight)
        layer.shadowColor = UIColor.black.cgColor
        layer.shadowOpacity = shadowOpacity

        titleLabel?.adjustsFontSizeToFitWidth = adjustsFontSizeToFitWidth
        imageView?.contentMode = .scaleAspectFit
    }
}

@IBDesignable class RoundedCornersButtonBiggerTapArea: RoundedCornersButton {
    var minimumHitArea: CGSize?
    var debugTag = 0
}

extension RoundedCornersButtonBiggerTapArea {
    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        if let minimumHitArea = minimumHitArea {
            if isHidden || !isUserInteractionEnabled || alpha == 0 {
                return nil
            }

            let buttonSize = bounds.size
            let widthToAdd = max(minimumHitArea.width - buttonSize.width, 0)
            let heightToAdd = max(minimumHitArea.height - buttonSize.height, 0)
            let largerFrame = bounds.insetBy(dx: -widthToAdd * 0.5, dy: -heightToAdd * 0.5)

            if debugTag > 0 {
                let debugViewTag = debugTag + 1000

                if let prevTemp = superview?.viewWithTag(debugViewTag) {
                    prevTemp.removeFromSuperview()
                }

                let debugView = UIView(frame: frame.insetBy(dx: -widthToAdd * 0.5, dy: -heightToAdd * 0.5))
                debugView.tag = debugViewTag
                debugView.backgroundColor = UIColor.red.withAlphaComponent(0.5)
                debugView.isUserInteractionEnabled = false
                superview?.insert(debugView, .below(self))
            }

            return largerFrame.contains(point) ? self : nil
        }

        return super.hitTest(point, with: event)
    }
}

class RoundedCornersTintButton: RoundedCornersButton {
    override var tintColor: UIColor! {
        didSet {
            layer.borderColor = tintColor.cgColor
        }
    }
}

class SeparatedStepperButton: RoundedCornersButton {
    @IBInspectable var activeTintColor: UIColor = .BasePurple

    override var isEnabled: Bool {
        didSet {
            backgroundColor = backgroundColorForState(state)
            tintColor = tintColorForState(state)
            layer.borderColor = tintColorForState(state)?.cgColor
        }
    }

    override var isHighlighted: Bool {
        didSet {
            backgroundColor = backgroundColorForState(state)
            tintColor = tintColorForState(state)
            layer.borderColor = tintColorForState(state)?.cgColor
        }
    }

    var fakeDisabled: Bool = false {
        didSet {
            backgroundColor = backgroundColorForState(state)
            tintColor = tintColorForState(state)
            layer.borderColor = tintColorForState(state)?.cgColor
        }
    }

    func backgroundColorForState(_ state: UIControl.State) -> UIColor? {
        switch state {
        case UIControl.State():
            return fakeDisabled ? .clear : .white

        case UIControl.State.disabled:
            return .clear

        case UIControl.State.highlighted:
            return fakeDisabled ? .clear : .gray

        default:
            return .clear
        }
    }

    func tintColorForState(_ state: UIControl.State) -> UIColor? {
        switch state {
        case UIControl.State(), UIControl.State.highlighted:
            return fakeDisabled ? .TintL2 : .Tint1

        case UIControl.State.disabled:
            return .TintL2

        default:
            return .clear
        }
    }
}

class FadeOnHighlightButton: RoundedCornersButton {
    @IBInspectable var highlightBackgroundAlpha: CGFloat = 0.65
    @IBInspectable var highlightTitleAlpha: CGFloat = 0.75

    override var isHighlighted: Bool {
        didSet {
            let alphaComponent = isHighlighted ? highlightBackgroundAlpha : 1.0
            backgroundColor = backgroundColor?.withAlphaComponent(alphaComponent)
            titleLabel?.alpha = isHighlighted ? highlightTitleAlpha : 1.0
        }
    }
	override var isEnabled: Bool {
		didSet {
			let alphaComponent = isEnabled ? 1 : highlightBackgroundAlpha
			backgroundColor = backgroundColor?.withAlphaComponent(alphaComponent)
			titleLabel?.alpha = isEnabled ? 1 : highlightTitleAlpha
		}
	}
}

class AddRoomButton: RoundedCornersButton {
    override func setup() {
        super.setup()

        layer.borderColor = UIColor.TintL2.cgColor
        backgroundColor = .TintL1
    }
}

@IBDesignable class UnderlinedTitleButton: ButtonWithBlocks {
    @IBInspectable var titleColor: UIColor? = .black {
        didSet {
            setup()
        }
    }
    var titleFont: UIFont? = UIFont.Body() {
        didSet {
            setup()
        }
    }
    var titleAlignment: NSTextAlignment? = .center
    var underlineStyle: NSUnderlineStyle = NSUnderlineStyle.single

    override init(frame: CGRect) {
        super.init(frame: frame)

        setup()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        setup()
    }

    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()

        setup()
    }

    override func setTitle(_ title: String?, for state: UIControl.State) {
        super.setTitle(title, for: state)

        setup()
    }

    func setup() {
        let attributedString = NSAttributedString(
            string: title(for: .normal) ?? "",
            attributes: [
                NSAttributedString.Key.font: titleFont ?? UIFont(),
                NSAttributedString.Key.foregroundColor: titleColor ?? .BasePurple,
                NSAttributedString.Key.underlineStyle: underlineStyle.rawValue
            ]
        )
        setAttributedTitle(attributedString, for: .normal)
    }
}

@IBDesignable class SimpleTextButton: UnderlinedTitleButton {
    override func setup() {
        underlineStyle = []

        super.setup()
    }
}

@IBDesignable class PasswordVisibilityButton: SimpleTextButton {
    var textfield: UITextField?

    override func setup() {
        super.setup()

        addTarget(self, action: #selector(toggle), for: .touchUpInside)
    }

    @objc private func toggle() {
        guard let textfield = textfield else { return }
        textfield.isSecureTextEntry.toggle()
        textfield.adjustsFontSizeToFitWidth = textfield.isSecureTextEntry ? false : true
        setTitle(
            textfield
                .isSecureTextEntry ?
                PILocalizedString("passwordVisibilityShowTitle", comment: "Password visibility button: show") :
                PILocalizedString(
                    "passwordVisibilityHideTitle",
                    comment: "Password visibility button: hide"
                ),
            for: .normal
        )
    }
}

class SimpleUnderlineButton: UIButton {
	override func setTitle(_ title: String?, for state: UIControl.State) {
		let attributedString = NSAttributedString(string: title ?? "", attributes: [
		    .font: titleLabel?.font ?? UIFont.Body(),
		    .foregroundColor: titleColor(for: .normal) ?? .white,
		    .underlineStyle: NSUnderlineStyle.single.rawValue
		])

		setAttributedTitle(attributedString, for: state)
	}
}

extension FadeOnHighlightButton {
    func setAttributedStringForPayPal(style: ButtonStyling) {
        let leadingString = NSMutableAttributedString(
            string: PILocalizedString("reviewSubmitButtonPayPalLeadingText"),
            attributes: [.font: style.font, .foregroundColor: style.titleColour]
        )

        let paypalImage = NSTextAttachment()
        paypalImage.image = UIImage(named: "PayPalLogo")

        var imageSize = CGSize(width: 100, height: 25)
        if UIDevice.current.userInterfaceIdiom == .pad {
            imageSize = paypalImage.image?.size ?? imageSize
        }
        paypalImage.bounds = CGRect(
            x: 0,
            y: (style.font.capHeight - imageSize.height) / 2,
            width: imageSize.width,
            height: imageSize.height
        )
        let paypalImageString = NSAttributedString(attachment: paypalImage)

        leadingString.append(paypalImageString)

        // If the locale is german then text needs to be added to the end of the image for english this will be an empty string
        leadingString.append(NSAttributedString(
            string: PILocalizedString("reviewSubmitButtonPayPalTrailingText"),
            attributes: [.font: style.font, .foregroundColor: style.titleColour]
        ))

        setAttributedTitle(leadingString, for: .normal)
    }
}

extension RoundedCornersTintButton {
    func setAttributedStringForAppleWallet(font: UIFont) {
        let leadingString = NSMutableAttributedString()

        let checkMarkImage = NSTextAttachment()
        checkMarkImage.image = UIImage(systemName: "checkmark.circle")
        checkMarkImage.image = checkMarkImage.image?.withTintColor(.Tint4)

        checkMarkImage.bounds = CGRect(x: CGFloat(0), y: (font.capHeight - 20) / 2, width: 20, height: 20)
        let checkMarkImageString = NSAttributedString(attachment: checkMarkImage)

        leadingString.append(checkMarkImageString)
        leadingString.append(NSAttributedString(string: PILocalizedString("bookingConfirmationViewInAppleWallet")))

        setAttributedTitle(leadingString, for: .normal)
    }
}
