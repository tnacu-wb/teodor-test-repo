//
//  FormInputKeyboardToolbar.swift
//  PremierInn
//
//  Created by Freddie Parks on 04/08/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class FormInputKeyboardToolbar: UIToolbar {
    internal var doneCompletion: (() -> Void)?

    @IBAction func doneButtonDidTap(_ sender: Any) {
        doneCompletion?()
    }
}
