//
//  InfantsStepperCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 10/05/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class InfantsStepperCell: CustomStepperCell {
    @IBOutlet weak var primaryText: UILabel! {
        didSet {
            let string = PILocalizedString("infantRoomStepper", comment: "")
            let mutableString = NSMutableAttributedString(
                string: string,
                attributes: [.foregroundColor: UIColor.TintD1, .font: UIFont.Heading4_Semibold()]
            )
            let yearsOld = PILocalizedString("infantYearsRange")
            let mutableYearsOld = NSMutableAttributedString(
                string: yearsOld,
                attributes: [.foregroundColor: UIColor.TintD1, .font: UIFont.Body()]
            )

            mutableString.append(mutableYearsOld)
            primaryText.attributedText = NSAttributedString(attributedString: mutableString)
        }
    }

    override var minusButton: SeparatedStepperButton! {
        didSet {
            minusButton.accessibilityIdentifier = "decrementInfantsAcc"
        }
    }
    override var plusButton: SeparatedStepperButton! {
        didSet {
            plusButton.accessibilityIdentifier = "incrementInfantsAcc"
        }
    }

    @IBAction override func plusButtonDidTap(_ sender: UIButton) {
        do {
            _ = try rangeManager?.increase()

            if let rangeManager = rangeManager {
                delegate?.customStepperCellDidChangeValue(cell: self, value: rangeManager.currentStep)
            }
        } catch let error as NSError {
            print(error)
            delegate?.customStepperCellDidFailChangingValue(cell: self, error: error)
        }
    }

    override func updateUI() {
    }
}
