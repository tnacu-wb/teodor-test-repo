//
//  LoginTypeCell.swift
//  PremierInn
//
//  Created by Nick Jones on 30/09/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class LoginTypeCell: SimpleSeparatorsCell {
    var personalAccountSelected: Bool = true {
        didSet {
            updateAllUI()
        }
    }
    var eventHandler: LoginTypeEventHandler?

// MARK: - Outlets

    @IBOutlet weak var personalAccountButton: UIButton! {
        didSet {
            personalAccountButton.accessibilityIdentifier = AccessibilityIdentifiers.Login.personalAccountTab
            updatePersonalAccountButton()
            personalAccountButton.isAccessibilityElement = true
        }
    }

    @IBOutlet weak var businessBookerButton: UIButton! {
        didSet {
            businessBookerButton.accessibilityIdentifier = AccessibilityIdentifiers.Login.businessBookerTab
            updateBusinessBookerButton()
            businessBookerButton.isAccessibilityElement = true
        }
    }

// MARK: - Actions
    @IBAction func tappedPersonalAccount() {
        NotificationFeedbackManager.shared.provideLightTapFeedback()

        personalAccountSelected = true

        UIView.animate(withDuration: 0.28) {
            self.updateAllUI()
        }

        eventHandler?.selectedPersonalAccount()
    }

    @IBAction func tappedBusinessBooker() {
        NotificationFeedbackManager.shared.provideLightTapFeedback()

        personalAccountSelected = false

        UIView.animate(withDuration: 0.28) {
            self.updateAllUI()
        }

        eventHandler?.selectedBusinessBooker()
    }
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=



// MARK: - Private Functions

    private func updateAllUI() {
        updatePersonalAccountButton()
        updateBusinessBookerButton()
    }

    private func updatePersonalAccountButton() {
        self.personalAccountButton.backgroundColor = personalAccountSelected ? .white : .whiteTwo
        self.personalAccountButton.setTitleColor(.TintD2, for: .normal)
        self.personalAccountButton.titleLabel?.font = personalAccountSelected ? UIFont.Body_Semibold() : UIFont.Body()
        self.personalAccountButton.layer.borderColor = personalAccountSelected ? UIColor.white.cgColor : UIColor.TintL2
            .cgColor
        self.personalAccountButton.layer.borderWidth = personalAccountSelected ? 0 : 1
        self.personalAccountButton.setTitle(PILocalizedString("loginAccountTypePersonal"), for: .normal)
    }

    private func updateBusinessBookerButton() {
        self.businessBookerButton.backgroundColor = personalAccountSelected ? .whiteTwo : .white
        self.businessBookerButton.setTitleColor(.TintD2, for: .normal)
        self.businessBookerButton.titleLabel?.font = personalAccountSelected ? UIFont.Body() : UIFont.Body_Semibold()
        self.businessBookerButton.layer.borderColor = personalAccountSelected ? UIColor.TintL2.cgColor : UIColor.white
            .cgColor
        self.businessBookerButton.layer.borderWidth = personalAccountSelected ? 1 : 0
        self.businessBookerButton.setTitle(PILocalizedString("loginAccountTypeBusiness"), for: .normal)
    }
// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

}

protocol LoginTypeEventHandler {
    func selectedPersonalAccount()
    func selectedBusinessBooker()
}
