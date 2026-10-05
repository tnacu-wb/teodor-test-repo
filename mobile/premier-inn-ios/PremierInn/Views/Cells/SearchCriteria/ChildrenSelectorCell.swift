//
//  ChildrenSelectorCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 21/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class ChildrenSelectorCell: CustomStepperCell {
	override var minusButton: SeparatedStepperButton! {
		didSet {
			minusButton.accessibilityIdentifier = "decrementChildrenAcc"
		}
	}
	override var plusButton: SeparatedStepperButton! {
		didSet {
			plusButton.accessibilityIdentifier = "incrementChildrenAcc"
		}
	}

    @IBOutlet weak var subtitlelabel: UILabel!

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
                    "childrenSelectorMaxChildrenPerRoom",
                    comment: "Children selector cell: maximum number of children per room error message"
                )
                errorCode = PIError.Code.maxNumberOfAdultsRangeReached

                let userInfo = [NSLocalizedDescriptionKey: errorMessage]

                theError = NSError(domain: PIError.domain, code: errorCode, userInfo: userInfo)
            }

            delegate?.customStepperCellDidFailChangingValue(cell: self, error: theError)
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        subtitlelabel?.text = PILocalizedString("childrenSelectorAgeRange", comment: "Children selector cell: age range")
    }
}
