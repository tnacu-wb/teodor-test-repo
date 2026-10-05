//
//  CustomStepperCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 13/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol CustomStepperCellDelegate: AnyObject {
	func customStepperCellDidChangeValue(cell: CustomStepperCell, value: Int)
	func customStepperCellDidFailChangingValue(cell: CustomStepperCell, error: NSError)
}

class CustomStepperCell: TableCellWithError {
    @IBOutlet weak var label: UILabel! {
        didSet {
            label.font = UIFont.Heading1_Semibold()
        }
    }
    @IBOutlet weak var minusButton: SeparatedStepperButton!
    @IBOutlet weak var plusButton: SeparatedStepperButton!

	var rangeManager: RangeManager?
	weak var delegate: CustomStepperCellDelegate?

    override func awakeFromNib() {
        super.awakeFromNib()

		self.plusButton.isEnabled = true
        self.minusButton.isEnabled = false
    }

    @IBAction func plusButtonDidTap(_ sender: UIButton) {
		do {
			_ = try rangeManager?.increase()

			updateUI()

			if let rangeManager = rangeManager {
				delegate?.customStepperCellDidChangeValue(cell: self, value: rangeManager.currentStep)
			}
		} catch let error as NSError {
			delegate?.customStepperCellDidFailChangingValue(cell: self, error: error)
		}
    }

    @IBAction func minusButtonDidTap(_ sender: UIButton) {
		do {
			_ = try rangeManager?.decrease()

			updateUI()

			if let rangeManager = rangeManager {
				delegate?.customStepperCellDidChangeValue(cell: self, value: rangeManager.currentStep)
			}
		} catch {
        }
    }

    func updateUI() {
	}
}
