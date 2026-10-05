//
//  CreditCardNumberCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 21/07/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class CreditCardNumberCell: FormekaTextFieldCell {
    override func textField(
        _ textField: UITextField,
        shouldChangeCharactersIn range: NSRange,
        replacementString string: String
    ) -> Bool {
        guard let text = textField.text else { return false }

        let replacementText = NSString(string: text).replacingCharacters(in: range, with: string)
            .trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).replacingOccurrences(
                of: " ",
                with: ""
            )
        if range.length == 1 && text.last == " " { return true }
        if let maxNumberOfCharacters = maxNumberOfCharacters, replacementText.count > maxNumberOfCharacters { return false }

        textField.text = replacementText.creditCardDisplayValue()

        delegate?.textFieldDidUpdateContent(value: replacementText, cell: self)

        return false
    }
}
