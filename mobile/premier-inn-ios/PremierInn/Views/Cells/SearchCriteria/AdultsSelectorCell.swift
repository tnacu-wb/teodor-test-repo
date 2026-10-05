//
//  AdultsSelectorCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 19/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class AdultsSelectorCell: CustomStepperCell {
	override var minusButton: SeparatedStepperButton! {
		didSet {
			minusButton.accessibilityIdentifier = "decrementAdultsAcc"
		}
	}
	override var plusButton: SeparatedStepperButton! {
		didSet {
			plusButton.accessibilityIdentifier = "incrementAdultsAcc"
		}
	}

	@IBAction override func plusButtonDidTap(_ sender: UIButton) {
        do {
            _ = try rangeManager?.increase()

            if let rangeManager = rangeManager {
                delegate?.customStepperCellDidChangeValue(cell: self, value: rangeManager.currentStep)
            }
        } catch let error as NSError {
            var errorMessage: String
            var errorCode: Int
            var theError = error

            if rangeManager?.currentStep == Constants.Config.maxNumberOfAdults {
                errorMessage = PILocalizedString(
                	"maximumAdultsNumberPerRoom",
                	comment: "Maximum number of adults per room error message"
                )
                errorCode = PIError.Code.maxNumberOfAdultsRangeReached

                let userInfo = [NSLocalizedDescriptionKey: errorMessage]

                theError = NSError(domain: PIError.domain, code: errorCode, userInfo: userInfo)
            }

            delegate?.customStepperCellDidFailChangingValue(cell: self, error: theError)
        }
    }

    override func updateUI() {
    }
}
