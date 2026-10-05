//
//  Compass.swift
//  PremierInn
//
//  Created by Marcello Mascia on 05/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class Compass: UIView {
    @IBOutlet weak var button: UIButton!
    @IBOutlet weak var backgroundImageView: UIImageView!

    private let animationSpeed = 0.48

    var show: Bool = false {
        didSet {
            if show != oldValue {
                layer.removeAllAnimations()

                UIView.animate(
                    withDuration: animationSpeed,
                    delay: 0,
                    usingSpringWithDamping: 0.6,
                    initialSpringVelocity: 0.4,
                    options: [],
                    animations: {
                        self.transform = self.show ? CGAffineTransform.identity : CGAffineTransform(scaleX: 0.01, y: 0.01)
                        self.alpha = self.show ? 1 : 0
                    }, completion: nil
                )
            }
        }
    }

    var rotation: CGFloat = 90 {
        didSet {
            backgroundImageView.transform = CGAffineTransform(
                rotationAngle: rotation.degreesToRadians + .pi / 2
            )
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        backgroundImageView.layer.anchorPoint.y = button.bounds.height * 0.5 / backgroundImageView.bounds.height
    }
}
