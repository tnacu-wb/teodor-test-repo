//
//  NightsSelectorCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 24/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleCalendar

class NightsSelectorCell: CustomStepperCell {
    @IBOutlet weak var checkOutLabel: UILabel!

	@IBAction override func plusButtonDidTap(_ sender: UIButton) {
		do {
			_ = try rangeManager?.increase()

			if let rangeManager = rangeManager {
				delegate?.customStepperCellDidChangeValue(cell: self, value: rangeManager.currentStep)
			}
		} catch {
			var errorMessage: String
			var errorCode: Int

			if rangeManager?.currentStep == SettingsManager.sharedInstance.activeRules.maxNights {
				errorMessage = String.localizedStringWithFormat(
					PILocalizedString("criteriaMaxNightsErrorMessage", comment: "Criteria screen: maximum number of nights error message"),
					SettingsManager.sharedInstance.activeRules.maxNights
				) + "\n" + PILocalizedString("telephoneMessage", comment: "Message displayed to ask the user to call us for more info")
				errorCode = PIError.Code.maxNightsRangeReached
			} else {
				errorMessage = PILocalizedString(
					"criteriaOneYearInAdvanceErrorMessage",
					comment: "Criteria screen: one year in advance error message"
				)
				errorCode = PIError.Code.moreThanOneYear
			}

			let userInfo = [NSLocalizedDescriptionKey: errorMessage]
			let error = NSError(domain: PIError.domain, code: errorCode, userInfo: userInfo)

			delegate?.customStepperCellDidFailChangingValue(cell: self, error: error)
		}
	}
}
