//
//  MealOptionCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class MealOptionCell: CustomStepperCell {
    @IBOutlet weak var mealTitle: UILabel! {
        didSet {
            mealTitle.font = .Heading2_Bold()
        }
    }
    @IBOutlet weak var priceDescription: UILabel! {
        didSet {
            priceDescription.font = .BodySmall()
        }
    }
    @IBOutlet weak var mealDescription: UILabel! {
        didSet {
            mealDescription.font = .BodySmall()
        }
    }
    @IBOutlet weak var note: UIPaddingLabel! {
        didSet {
            note.clipsToBounds = true
            note.layer.cornerRadius = 2
            note.font = .SubtextSmall()
        }
    }
    @IBOutlet weak var adultsLabel: UILabel! {
        didSet {
            adultsLabel.font = .Body_Semibold()
            adultsLabel.text = PILocalizedString("adultTitleRoomStepper")
        }
    }

    @IBOutlet weak var mealDescriptionBottomConstraint: NSLayoutConstraint!

    func update(with noteContent: NSAttributedString?) {
        note.attributedText = noteContent
        note.isHidden = noteContent == nil
        note.backgroundColor = UIColor.Tint3

        mealDescriptionBottomConstraint.constant = noteContent == nil ? 16 : 48
    }

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

    var stepperChanged: ((Int) -> Void)?

    @IBAction override func plusButtonDidTap(_ sender: UIButton) {
        do {
            _ = try rangeManager?.increase()

            if let rangeManager = rangeManager {
                stepperChanged?(rangeManager.currentStep)
                UIAccessibility.post(notification: .announcement, argument: "\(rangeManager.currentStep) adults selected")
            }
        } catch _ as NSError {
            // TODO: Error handling
        }
    }

    @IBAction override func minusButtonDidTap(_ sender: UIButton) {
        do {
            _ = try rangeManager?.decrease()

            if let rangeManager = rangeManager {
                stepperChanged?(rangeManager.currentStep)
            }
        } catch _ as NSError {
            // TODO: Error handling
        }
    }
}
