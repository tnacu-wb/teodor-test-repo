//
//  LoadingView.swift
//  PremierInn
//
//  Created by Nick Jones on 09/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class LoadingView: UIView {
    override func willMove(toWindow newWindow: UIWindow?) {
        guard newWindow == nil else { return }

        NotificationFeedbackManager.shared.stopExperimentalFeedback()
        canAnimate = false
    }

    override func willMove(toSuperview newSuperview: UIView?) {
        guard newSuperview == nil else { return }

        NotificationFeedbackManager.shared.stopExperimentalFeedback()
        canAnimate = false
    }

    let firstDot: UIView
    let secondDot: UIView
    let thirdDot: UIView

    private var shouldRepeatAnimation = true
    private var dots: [UIView] = []
    private var canAnimate: Bool

    override init(frame: CGRect) {
        self.canAnimate = true

        let dotSize = (frame.size.width / 4) - 8
        let middleOfBox = (frame.size.width / 2) - (dotSize / 2)
        let spacing = dotSize / 3

        secondDot = UIView(frame: CGRect(
            x: middleOfBox,
            y: middleOfBox,
            width: dotSize,
            height: dotSize
        )
        )

        firstDot = UIView(frame: CGRect(
            x: (middleOfBox - dotSize) - spacing,
            y: middleOfBox,
            width: dotSize,
            height: dotSize
        )
        )

        thirdDot = UIView(frame: CGRect(
            x: (middleOfBox + dotSize) + spacing,
            y: middleOfBox,
            width: dotSize,
            height: dotSize
        )
        )

        super.init(frame: frame)

        dots = [firstDot, secondDot, thirdDot]

        dots.forEach { ($0.backgroundColor, $0.clipsToBounds, $0.layer.cornerRadius) = (
            .sea,
            true,
            dotSize.halved
        ); addSubview($0) }

        backgroundColor = .white

        layer.cornerRadius = 5

        layer.shadowRadius = 2
        layer.shadowOpacity = 0.8
        layer.shadowOffset = CGSize(width: 2.0, height: 2.0)
        layer.shadowColor = UIColor.lightGray.cgColor
    }

    func stopAnimating() {
        shouldRepeatAnimation = false
        layer.removeAllAnimations()
    }

    func animate() {
        NotificationFeedbackManager.shared.runExperimentalFeedback(ofType: .tripleTake)

        // Animation bounce distances
        let upBounceAmount: CGFloat = 15
        let downBounceAmount: CGFloat = 20
        let resetAmount: CGFloat = downBounceAmount - upBounceAmount

        // Durations
        let duration: Double = 1
        let animationSplit: Double = 1 / 4

        // First dot timings
        let firstDot_Up_AnimationRelativeStartTime: Double = 0
        let firstDot_Down_AnimationRelativeStartTime: Double = firstDot_Up_AnimationRelativeStartTime + animationSplit
        let firstDot_Original_AnimationRelativeStartTime: Double = firstDot_Down_AnimationRelativeStartTime + animationSplit

        // Second dot timings
        let secondDot_Up_AnimationRelativeStartTime: Double = animationSplit / 2
        let secondDot_Down_AnimationRelativeStartTime: Double = secondDot_Up_AnimationRelativeStartTime + animationSplit
        let secondDot_Original_AnimationRelativeStartTime: Double = secondDot_Down_AnimationRelativeStartTime +
            animationSplit

        // Third dot timings
        let thirdDot_Up_AnimationRelativeStartTime: Double = animationSplit
        let thirdDot_Down_AnimationRelativeStartTime: Double = thirdDot_Up_AnimationRelativeStartTime + animationSplit
        let thirdDot_Original_AnimationRelativeStartTime: Double = thirdDot_Down_AnimationRelativeStartTime + animationSplit

        UIView.animateKeyframes(
            withDuration: duration,
            delay: 0,
            options: .calculationModeLinear,
            animations: {
                // •=•=•=•=•=• First Dot Keyframes •=•=•=•=•=•
                UIView.addKeyframe(
                    withRelativeStartTime: firstDot_Up_AnimationRelativeStartTime,
                    relativeDuration: animationSplit,
                    animations: {
                    self.firstDot.frame.origin.y -= upBounceAmount
                }
                )
                UIView.addKeyframe(
                    withRelativeStartTime: firstDot_Down_AnimationRelativeStartTime,
                    relativeDuration: animationSplit,
                    animations: {
                    self.firstDot.frame.origin.y += downBounceAmount
                }
                )
                UIView.addKeyframe(
                    withRelativeStartTime: firstDot_Original_AnimationRelativeStartTime,
                    relativeDuration: animationSplit,
                    animations: {
                    self.firstDot.frame.origin.y -= resetAmount
                }
                )
                // •=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•


                // •=•=•=•=•=• Second Dot Keyframes •=•=•=•=•=•
                UIView.addKeyframe(
                    withRelativeStartTime: secondDot_Up_AnimationRelativeStartTime,
                    relativeDuration: animationSplit,
                    animations: {
                    self.secondDot.frame.origin.y -= upBounceAmount
                }
                )
                UIView.addKeyframe(
                    withRelativeStartTime: secondDot_Down_AnimationRelativeStartTime,
                    relativeDuration: animationSplit,
                    animations: {
                    self.secondDot.frame.origin.y += downBounceAmount
                }
                )
                UIView.addKeyframe(
                    withRelativeStartTime: secondDot_Original_AnimationRelativeStartTime,
                    relativeDuration: animationSplit,
                    animations: {
                    self.secondDot.frame.origin.y -= resetAmount
                }
                )
                // •=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•


                // •=•=•=•=•=• Third Dot Keyframes •=•=•=•=•=•
                UIView.addKeyframe(
                    withRelativeStartTime: thirdDot_Up_AnimationRelativeStartTime,
                    relativeDuration: animationSplit,
                    animations: {
                    self.thirdDot.frame.origin.y -= upBounceAmount
                }
                )
                UIView.addKeyframe(
                    withRelativeStartTime: thirdDot_Down_AnimationRelativeStartTime,
                    relativeDuration: animationSplit,
                    animations: {
                    self.thirdDot.frame.origin.y += downBounceAmount
                }
                )
                UIView.addKeyframe(
                    withRelativeStartTime: thirdDot_Original_AnimationRelativeStartTime,
                    relativeDuration: animationSplit,
                    animations: {
                    self.thirdDot.frame.origin.y -= resetAmount
                }
                )
                // •=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•=•
            },
            completion: { success in
            if success && self.shouldRepeatAnimation { self.animate() }
        }
        )
    }

    required init?(coder aDecoder: NSCoder) {
        self.canAnimate = true

        self.firstDot = UIView()
        self.secondDot = UIView()
        self.thirdDot = UIView()

        super.init(coder: aDecoder)
    }
}
