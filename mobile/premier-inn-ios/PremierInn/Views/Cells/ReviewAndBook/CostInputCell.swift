//
//  CostInputCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 30/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class CostInputCell: FullsizeTextFieldCell {
    override func textField(
        _ textField: UITextField,
        shouldChangeCharactersIn range: NSRange,
        replacementString string: String
    ) -> Bool {
        guard let text = textField.text else { return false }

        let newValue = NSString(string: text).replacingCharacters(in: range, with: string)
            .trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).replacingOccurrences(
                of: "£",
                with: ""
            )
        let replacementText = "£" + newValue

        textField.text = replacementText
        delegate?.textFieldDidUpdateContent(value: newValue, cell: self)

        return false
    }
}
