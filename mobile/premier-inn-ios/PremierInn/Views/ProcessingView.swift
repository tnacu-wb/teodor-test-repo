//
//  ProcessingView.swift
//  PremierInn
//
//  Created by Freddie Parks on 15/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class ProcessingView: UIView {
    override init(frame: CGRect) {
        super.init(frame: frame)

        backgroundColor = UIColor(red: 43 / 255, green: 43 / 255, blue: 43 / 255, alpha: 0.6)

        let activityIndicator = UIActivityIndicatorView(style: .large)
        activityIndicator.center = CGPoint(x: bounds.width.halved, y: bounds.height.halved)
        activityIndicator.autoresizingMask = [
            .flexibleLeftMargin,
            .flexibleRightMargin,
            .flexibleTopMargin,
            .flexibleBottomMargin
        ]

        addSubview(activityIndicator)

        activityIndicator.startAnimating()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
