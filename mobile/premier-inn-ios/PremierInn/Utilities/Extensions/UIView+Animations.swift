//
//  UIButton+Extensions.swift
//  PremierInn
//
//  Created by Nick Jones on 02/08/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

// MARK: Fade To Bottom And Bounce In From Top 👻🕴
extension UIView {
    func fadeToBottomAndBounceInFromTopAnimation(withActionToPerformWhilstHidden actionWhilstHidden: @escaping () -> Void) {
        let originalYPosition = frame.origin.y

        UIView.animate(
            withDuration: .ocd,
            animations: { [weak self] in
                self?.alpha = 0
                self?.frame.origin.y = originalYPosition + 60
            },
            completion: { [weak self] _ in
            actionWhilstHidden()

            self?.frame.origin.y = originalYPosition - 60
                UIView.animate(
                    withDuration: .ocd,
                    delay: 0,
                    usingSpringWithDamping: 0.6,
                    initialSpringVelocity: 0.3,
                    options: [.curveEaseOut],
                    animations: { [weak self] in
                        self?.frame.origin.y = originalYPosition
                        self?.alpha = 1
                    }
                )
            }
        )
    }
}

// MARK: Pop 🎈

enum PopIntensity {
    case light
    case medium
    case heavy

    var popSize: CGFloat {
        switch self {
        case .light:
            return 1.2
        case .medium:
            return 1.4
        case .heavy:
            return 1.5
        }
    }
}

extension UIView {
    func pop(with intensity: PopIntensity, andCompletion completion: (() -> Void)? = nil) {
        let popSize = intensity.popSize

        UIView.animateKeyframes(
            withDuration: 0.1,
            delay: 0,
            options: .calculationModeLinear,
            animations: { [weak self] in
                UIView.addKeyframe(withRelativeStartTime: 0, relativeDuration: 0.5, animations: {
                    self?.transform = CGAffineTransform(scaleX: popSize, y: popSize)
                })

                UIView.addKeyframe(withRelativeStartTime: 0, relativeDuration: 0.5, animations: {
                    self?.transform = CGAffineTransform(scaleX: 1, y: 1)
                })
            },
            completion: { _ in
            completion?()
        }
        )
    }
}


// MARK: Confetti 🎉

extension UIView {
    enum ConfettiIntensity {
        case light
        case medium
        case heavy

        var popSize: CGFloat {
            switch self {
            case .light:
                return 1.2
            case .medium:
                return 1.4
            case .heavy:
                return 1.6
            }
        }
    }

    // MARK: Public Functions

    /**

     Builds a confetti backer view for your view

     For maximum performance make sure to call `preBuildBackerView` with your tag as soon as your view is available

     - parameter tag: Provide a unique tag for your view which this function will be used to identify the correct confetti holder for your view (You wouldn't want confetti all over the place would you)

     */
    func prebuildConfettiContainer(withTag tag: Int) {
        if superview?.viewWithTag(tag) != nil { return }

        let confettiContainer = UIView(frame:
            CGRect(
                x: self.frame.origin.x,
                y: self.frame.origin.y,
                width: self.frame.size.width,
                height: self.frame.size.height
            )
        )
        confettiContainer.tag = 1337
        confettiContainer.layer.cornerRadius = 4
        confettiContainer.isUserInteractionEnabled = false

        superview?.insert(confettiContainer, .below(self))
    }

    /**

     Pops some confetti (*Yay!*)

     For maximum performance make sure to call `preBuildBackerView` with your tag as soon as your view is available

     - parameter withTag: Provide a unique tag for your view which this function will be used to identify the correct confetti holder for your view (You wouldn't want confetti all over the place would you)
     - parameter and: The intensity of the confetti that you want

     */
    func confetti(withTag tag: Int, confettiIntensity: ConfettiIntensity, and popIntensity: PopIntensity) {
        guard let container = buildConfettiContainer(withTag: tag) else { return }

        let emitter: CAEmitterLayer = {
            if let existingEmitter = existingEmitter(on: container) {
                return existingEmitter
            }

            let emitter = CAEmitterLayer()
            container.layer.addSublayer(emitter)

            return emitter
        }()

        emitter.emitterPosition = CGPoint(
            x: frame.size.width / 2,
            y: frame.size.height / 2
        )
        emitter.emitterSize = CGSize(width: self.frame.size.width, height: self.frame.size.height)
        emitter.emitterCells = generateEmitterCells(for: confettiIntensity)
        emitter.birthRate = 1

        UIView.animate(
            withDuration: 0.05,
            animations: { [unowned self] in
                transform = CGAffineTransform(scaleX: popIntensity.popSize, y: popIntensity.popSize)
            },
            completion: { _ in
                UIView.animate(
                    withDuration: 0.05,
                    animations: {
                        self.transform = CGAffineTransform(scaleX: 1, y: 1)
                    },
                    completion: { _ in
                    emitter.birthRate = 0
            }
                )
        }
        )
    }

    // MARK: Convenience Methods
    private func buildConfettiContainer(withTag tag: Int) -> UIView? {
        if let existingView = superview?.viewWithTag(tag) {
            return existingView
        }

        let backerView = UIView(frame:
            CGRect(
                x: self.frame.origin.x,
                y: self.frame.origin.y,
                width: self.frame.size.width,
                height: self.frame.size.height
            )
        )

        backerView.tag = tag
        backerView.isUserInteractionEnabled = false

        superview?.insert(backerView, .below(self))

        return backerView
    }

    private func existingEmitter(on view: UIView) -> CAEmitterLayer? {
        view.layer.sublayers?.first(where: { $0 is CAEmitterLayer }) as? CAEmitterLayer
    }

    private func initialEmitterBirthRateFor(_ intensity: ConfettiIntensity) -> Float {
        switch intensity {
        case .light:
            return 80.0
        case .medium:
            return 250.0
        case .heavy:
            return 400.0
        }
    }

    private func generateEmitterCells(for intensity: ConfettiIntensity) -> [CAEmitterCell] {
        var cells = [CAEmitterCell]()

        for index in 0..<3 {
            let cell = CAEmitterCell()

            cell.birthRate = initialEmitterBirthRateFor(intensity)
            cell.lifetime = 1.2
            cell.alphaSpeed = -1 / (cell.lifetime / 2)
            cell.velocity = CGFloat(getRandomVelocity())
            cell.spinRange = 4
            cell.emissionRange = CGFloat(Double.pi * 2)
            cell.color = UIColor.sea.cgColor
            cell.contents = getNextImage(withIndex: index)
            cell.scaleRange = 0
            cell.scale = 0.2
            cells.append(cell)
        }

        return cells
    }

    private func getNextImage(withIndex index: Int) -> CGImage? {
        guard let box = UIImage(named: "Box.png") else { return nil }
        guard let circle = UIImage(named: "Circle.png") else { return nil }
        guard let swirl = UIImage(named: "Spiral.png") else { return nil }

        let images: [UIImage] = [box, circle, swirl, swirl]

        if index - 1 > images.count {
            let randomIndexWithinRange = Int.random(in: 0..<images.count - 1)

            return images[randomIndexWithinRange].cgImage
        }

        return images[index % 4].cgImage
    }

    private func getRandomVelocity() -> Int {
        Int.random(in: 0..<50) + 200
    }
}
