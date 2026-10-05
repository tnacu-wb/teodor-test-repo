//
//  UIView+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 18/09/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

enum UIViewPosition {
    case front
    case back
}

enum UIViewSubPosition {
    case at(index: Int)
    case above(UIView)
    case below(UIView)
}

extension UIView {
    private var loadingView: LoadingView? {
        if let loadingView = subviews.first(where: { $0 is LoadingView }) {
            return loadingView as? LoadingView
        }

        let loadingView = LoadingView(frame: CGRect(
            x: (frame.size.width / 2) - 50,
            y: (frame.size.height / 2) - 50,
            width: 80,
            height: 80
        ))
        loadingView.autoresizingMask = [.flexibleLeftMargin, .flexibleRightMargin]
        loadingView.alpha = 0

        return loadingView
    }

    func showLoadingView() {
        isUserInteractionEnabled = false

        guard let loadingView = loadingView else { return }

        addSubview(loadingView)

        UIView.animate(
            withDuration: .ocd,
            animations: {
                loadingView.alpha = 1
            },
            completion: { _ in
            loadingView.animate()
        }
        )
    }

    func hideLoadingView() {
        guard let loadingView = loadingView else { return }

        UIView.animate(
            withDuration: .ocd,
            animations: {
                loadingView.alpha = 0
            },
            completion: { [weak self] _ in
            self?.isUserInteractionEnabled = true
            loadingView.stopAnimating()
            loadingView.removeFromSuperview()
        }
        )
    }

    class func fromNib<T: UIView>(nibName: String) -> T? {
        UINib(nibName: nibName, bundle: nil).instantiate(withOwner: nil, options: nil).first as? T
    }

    class func fromNib<T: UIView>() -> T? {
        fromNib(nibName: String(describing: T.self))
    }

    func pressAnimation(pressed: Bool) {
        UIView.animate(withDuration: .ocd) {
            self.transform = pressed ? CGAffineTransform(scaleX: 0.95, y: 0.95) : .identity
        }
    }

    /**
     Shakes the view horizontally by 10 pixels using spring dampening

     - parameter maximumDistance: Provide this to increase or decrease the shake intensity as desired

     */
    func shake(withMaximumDistance maximumDistance: CGFloat = 10) {
        let originalCenter = center.x

        center.x += maximumDistance

        UIView.animate(
            withDuration: 0.5,
            delay: 0,
            usingSpringWithDamping: 0.2,
            initialSpringVelocity: 0.5,
            options: [.curveEaseOut],
            animations: {
            self.center.x = originalCenter
        }
        )
    }

    /**
     Bounces the view vertically by 10 pixels using spring dampening

     - parameter maximumDistance: Provide this to increase or decrease the bounce intensity as desired

     */
    func bounce(withMaximumDistance maximumDistance: CGFloat = 10) {
        let originalCenter = center.y

        center.y += maximumDistance

        UIView.animate(
            withDuration: 0.5,
            delay: 0,
            usingSpringWithDamping: 0.2,
            initialSpringVelocity: 0.5,
            options: [.curveEaseOut],
            animations: {
            self.center.y = originalCenter
        }
        )
    }

    func roundCorners(withDropShadowOffset dropShadowOffset: CGSize) {
        layer.cornerRadius = 3

        layer.masksToBounds = false
        layer.shadowOffset = dropShadowOffset
        layer.shadowColor = UIColor.black.cgColor
        layer.shadowOpacity = 0.5
        layer.shouldRasterize = true
        layer.rasterizationScale = UIScreen.main.scale
    }

    func set(hasHeaderFooterShadow: Bool) {
        layer.shadowOpacity = hasHeaderFooterShadow ? 0.5 : 0.0
        layer.shouldRasterize = true
        layer.rasterizationScale = UIScreen.main.scale
        layer.shadowOffset = .zero
        layer.shadowColor = hasHeaderFooterShadow ? UIColor.black.cgColor : UIColor.clear.cgColor
        layer.shadowRadius = hasHeaderFooterShadow ? 2 : 0
    }

    func move(to position: UIViewPosition) {
        switch position {
        case .front:
            superview?.bringSubviewToFront(self)
        case .back:
            superview?.sendSubviewToBack(self)
        }
    }

    func insert(_ subView: UIView, _ position: UIViewSubPosition) {
        switch position {
        case .at(let index):
            insertSubview(subView, at: index)
        case .above(let view):
            insertSubview(subView, aboveSubview: view)
        case .below(let view):
            insertSubview(subView, belowSubview: view)
        }
    }

    func debugBorders(color: UIColor = .red) {
        layer.borderColor = color.cgColor
        layer.borderWidth = 1
    }

    func findFirstResponder() -> UIView? {
        if isFirstResponder {
            return self
        }

        for subview in subviews {
            if let firstResponder = subview.findFirstResponder() {
                return firstResponder
            }
        }

        return nil
    }
}
