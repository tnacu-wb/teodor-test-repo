//
//  ReachabilityView.swift
//  PremierInn
//
//  Created by Freddie Parks on 06/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

enum ReachabilityMessage: String {
    case unreachable = "No internet connection"
    case reachable = "Back online"

    var localizedValue: String {
        switch self {
        case .reachable:
            return PILocalizedString("reachabilityReachable", comment: "Reachability: reachable")
        case .unreachable:
            return PILocalizedString("reachabilityUnreachable", comment: "Reachability: unreachable")
        }
    }
}

class ReachabilityView: UIView {
    private var messageLabel: UILabel?
    private var messageLabelHeight: CGFloat = 35

    init(frame: CGRect, message: String) {
        messageLabelHeight = frame.height

        super.init(frame: CGRect(x: frame.origin.x, y: frame.origin.y, width: frame.size.width, height: 0))

        clipsToBounds = true
        autoresizingMask = .flexibleWidth

        messageLabel = UILabel(frame: CGRect(x: 0, y: 0, width: frame.size.width, height: messageLabelHeight))
        messageLabel?.backgroundColor = UIColor.paleRed
        messageLabel?.font = UIFont.BodySmall_Semibold()
        messageLabel?.textAlignment = .center
        messageLabel?.textColor = .white
        messageLabel?.autoresizingMask = .flexibleWidth

        guard let messageLabel = messageLabel else { return }
        addSubview(messageLabel)

        update(message: message)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func update(message: String) {
        guard let messageLabel = messageLabel,
              let message = ReachabilityMessage.init(rawValue: message)?.localizedValue else { return }

        if message != messageLabel.text {
            messageLabel.text = message

            switch message {
            case ReachabilityMessage.reachable.rawValue:
                messageLabel.backgroundColor = UIColor.paleTeal
                layer.removeAllAnimations()
                hideMessageLabel()

            case ReachabilityMessage.unreachable.rawValue:
                messageLabel.backgroundColor = UIColor.paleRed
                showMessageLabel()

            default:
                break
            }
        }
    }

    private func showMessageLabel() {
        guard frame.size.height == 0 else { return }

        UIView.animate(withDuration: .ocd) {
            self.frame.size.height = self.messageLabelHeight
        }
    }

    private func hideMessageLabel() {
        UIView.animate(
            withDuration: .ocd,
            delay: 2.0,
            options: [.curveLinear],
            animations: {
                self.frame.size.height = 0
            },
            completion: nil
        )
    }
}
