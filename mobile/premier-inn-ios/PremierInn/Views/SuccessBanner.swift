//
//  SuccessBanner.swift
//  PremierInn
//
//  Created by Nick Jones on 12/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class ErrorBanner: SuccessBanner {
    override init(withMessage message: String, on view: UIView, with optionalIcon: String? = nil) {
        super.init(withMessage: message, on: view)

        self.backgroundColor = .Tint8
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

class SuccessBanner: UIView {
    enum State {
        case showing
        case hidden
    }

    let originalViewYPosition: CGFloat
    let mainView: UIView

    var showing: State = .hidden
    var messageLabel: UILabel?

    var toggleTimer: Timer?

    init(withMessage message: String, on view: UIView, with iconNamed: String? = nil) {
        mainView = view
        originalViewYPosition = view.frame.origin.y

        let 🖌 = UIFont.Body_Semibold()
        let 🚚 = view.frame.size.width - 40
        let expectedHeight = message.height(withConstrainedWidth: 🚚, font: 🖌)

        messageLabel = UILabel(frame:
            CGRect(
                x: 20,
                y: 15,
                width: 🚚,
                height: expectedHeight
            ))

        messageLabel?.numberOfLines = 0
        messageLabel?.textColor = .BaseWhite
        messageLabel?.font = 🖌

        if let iconName = iconNamed {
            let imageAttachment = NSTextAttachment()
            imageAttachment.image = UIImage(named: iconName)?.withRenderingMode(.alwaysOriginal)

            let mutableAttributedString =
                NSMutableAttributedString(attributedString: NSAttributedString(attachment: imageAttachment))
            var string = "  "
            string.append(message)

            mutableAttributedString.append(NSAttributedString(string: string))

            messageLabel?.attributedText = NSAttributedString(attributedString: mutableAttributedString)
        } else {
            messageLabel?.text = message
        }

        let 📏 = expectedHeight + 30

        super.init(frame:
            CGRect(
                x: 0,
                y: -📏,
                width: view.frame.size.width,
                height: 📏
            ))

        backgroundColor = UIColor.Tint4

        guard let fullyFormedMessageLabel = messageLabel else { return }
        addSubview(fullyFormedMessageLabel)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func show() {
        guard let message = messageLabel?.text else { return }

        toggle(to: .showing)

        toggleTimer = Timer.scheduledTimer(
            timeInterval: message.timeToRead(),
            target: self,
            selector: #selector(hide),
            userInfo: nil,
            repeats: false
        )

        mainView.addSubview(self)
    }

    @objc func hide() {
        toggle(to: .hidden)
    }

    func toggle(to desiredState: State) {
        let amountToSlide = desiredState == .showing ? frame.size.height : 0

        DispatchQueue.main.async {
            UIView.animate(
                withDuration: .ocd,
                animations: {
                    self.mainView.frame.origin.y = self.originalViewYPosition + amountToSlide
                },
                completion: { _ in
                if desiredState == .hidden {
                    self.removeFromSuperview()
                }
            }
            )
        }
    }
}
