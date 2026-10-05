//
//  PriceBreakdownItemView.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 24.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

final class PriceBreakdownItemView: UIView {
    @IBOutlet weak var nameLabel: UILabel! {
        didSet {
            nameLabel.font = .Action1()
            nameLabel.textColor = .ColourDL1
            nameLabel.numberOfLines = 0
        }
    }

    @IBOutlet weak var valueLabel: UILabel! {
        didSet {
            valueLabel.font = .Body_Bold()
            valueLabel.textColor = .ColourDL1
            valueLabel.numberOfLines = 0
            valueLabel.textAlignment = .right
        }
    }

    func customise(with viewModel: CIOLPriceBreakdownItemViewModelProtocol) {
        nameLabel.text = viewModel.formattedName
        valueLabel.text = viewModel.value.localizedValue
    }
}
