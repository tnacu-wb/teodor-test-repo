//
//  MapViewController+Overlay.swift
//  PremierInn
//
//  Created by Marcello Mascia on 15/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

protocol OverlayProtocol {
    var controller: UIViewController { get }
    var scrollView: UIScrollView? { get }
    var topLayoutPadding: CGFloat { get }
    var collapsedHeight: CGFloat { get }
}

enum OverlayError: LocalizedError {
    case onlyOneChildControllerAllowed
    case controllerDoesNotConformToOverlayProtocol
}

enum OverlayAnimationDirection {
    case up
    case down
}

extension MapViewController {
    var overlay: OverlayProtocol? {
        guard let overlay = children.first(where: { $0 is OverlayProtocol }) as? OverlayProtocol else { return nil }

        return overlay
    }

    func displayOverlayController(controller: UIViewController) throws {
        guard children.isEmpty else { throw OverlayError.onlyOneChildControllerAllowed }
        guard controller is OverlayProtocol else { throw OverlayError.controllerDoesNotConformToOverlayProtocol }

        addChild(controller)
        controller.view.frame = view.bounds
        controller.view.frame.origin.y = view.frame.height
        controller.view.layer.shadowOpacity = 0.5
        controller.view.layer.shouldRasterize = true
        controller.view.layer.rasterizationScale = UIScreen.main.scale
        controller.view.layer.shadowOffset = CGSize.zero
        controller.view.layer.shadowColor = UIColor.black.cgColor
        controller.view.layer.shadowRadius = 2
        view.addSubview(controller.view)
        controller.didMove(toParent: self)

        let gestureRecognizer = UIPanGestureRecognizer(target: self, action: #selector(handlePan))
        gestureRecognizer.delegate = self

        controller.view.addGestureRecognizer(gestureRecognizer)

        animate(childView: controller.view, to: view.frame.midY, duration: .ocd, direction: .up)
    }

    @objc func handlePan(_ gestureRecognizer: UIPanGestureRecognizer) {
        guard let overlay = overlay else { return }

        var movingView: UIView?
        movingView = gestureRecognizer.view

        guard let childView = movingView else { return }

        let minimumY: CGFloat = overlay.topLayoutPadding
        let minimumHeight: CGFloat = overlay.collapsedHeight
        let maximumY = view.frame.height - view.safeAreaInsets.bottom - minimumHeight
        let midY = view.frame.midY

        let currentY = childView.frame.origin.y
        let velocity = gestureRecognizer.velocity(in: view).y * 0.6 // Slow it down a little bit...
        let direction: OverlayAnimationDirection = velocity > 0 ? .down : .up

        switch gestureRecognizer.state {
        case .began, .changed:
            overlay.scrollView?.isScrollEnabled = shouldEnableInnerScroll(
                currentY: currentY,
                minimumY: minimumY,
                direction: direction
            )

            guard overlay.scrollView?.isScrollEnabled == false else { return }

            let translation = gestureRecognizer.translation(in: childView)
            childView.frame.origin.y = clamp(value: currentY + translation.y, lower: minimumY, upper: maximumY)
            gestureRecognizer.setTranslation(CGPoint.zero, in: childView)

        case .ended:
            overlay.scrollView?.isScrollEnabled = shouldEnableInnerScroll(
                currentY: currentY,
                minimumY: minimumY,
                direction: direction
            )

            guard velocity != 0 else { return }
            guard overlay.scrollView?.isScrollEnabled == false else { return }

            var distanceY: CGFloat = 0
            var destinationY: CGFloat = 0

            if direction == .up {
                destinationY = currentY <= midY ? minimumY : midY
                distanceY = currentY - destinationY
            } else {
                destinationY = currentY >= midY ? maximumY : midY
                distanceY = destinationY - currentY
            }

            let duration = linearInterpolation(value: TimeInterval(distanceY / abs(velocity)), lower: 0.2, upper: 0.6)

            animate(childView: childView, to: destinationY, duration: duration, direction: direction)

        default:
            break
        }
    }

    func shouldEnableInnerScroll(currentY: CGFloat, minimumY: CGFloat, direction: OverlayAnimationDirection) -> Bool {
        guard let scrollView = overlay?.scrollView else { return false }

        if direction == .down && scrollView.contentOffset.y <= 0 { return false }

        return (currentY == minimumY) ? true : false
    }

    func animate(childView: UIView, to destinationY: CGFloat, duration: TimeInterval, direction: OverlayAnimationDirection) {
        UIView.animate(
            withDuration: duration,
            delay: 0,
            usingSpringWithDamping: 0.7,
            initialSpringVelocity: 0.8,
            options: [.beginFromCurrentState, .allowUserInteraction],
            animations: {
                childView.frame.origin.y = destinationY
        }, completion: { _ in
            if let overlay = self.overlay {
                overlay.scrollView?.isScrollEnabled = self.shouldEnableInnerScroll(
                    currentY: childView.frame.origin.y,
                    minimumY: overlay.topLayoutPadding,
                    direction: direction
                )
            }
        }
        )
    }
}
