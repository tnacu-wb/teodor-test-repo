//
//  FormekaCardDateCell.swift
//  Formeka
//
//  Created by Marcello Mascia on 14/04/2018.
//  Copyright © 2018 Marcello Mascia. All rights reserved.
//

import UIKit

public class FormekaCardDateCell: FormekaTextFieldCell {

	override public func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {

		let text: NSString = textField.text as NSString? ?? ""
		let replacementText = text.replacingCharacters(in: range, with: string).trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)

		guard !(string == "/" && replacementText.count != 3) && replacementText.count < 6 else { return false }

		let newValue: String = {

			if range.length == 1 && replacementText.count == 2 {
				return replacementText.substringToIndex(1)
			} else if range.length == 0 && replacementText.count == 2 && string != "/" {
				var newValue = replacementText
				newValue.insert("/", at: newValue.endIndex)
				return newValue
			}

			return replacementText
		}()

		textField.text = newValue
		delegate?.textFieldDidUpdateContent(value: newValue, cell: self)
		return false
	}
}

private extension String {

	func substringToIndex(_ index: Int) -> String {

		let index = clamp(value: index, lower: 0, upper: self.count)
		let idx = self.index(startIndex, offsetBy: index)

		return String(self[..<idx])
	}

}

private func clamp<T: Comparable>(value: T, lower: T, upper: T) -> T {

	return min(max(value, lower), upper)
}
