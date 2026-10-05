//
//  FormekaTextFieldCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 13/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

public protocol FormekaTextFieldCellDelegate: AnyObject {

    func textFieldDidUpdateContent(value: String, cell: FormekaTextFieldCell)
    func textFieldDidBeginEditing(cell: FormekaTextFieldCell)
    func textFieldDidEndEditing(value: String, cell: FormekaTextFieldCell)
}

open class FormekaTextFieldCell: SimpleSeparatorsCell, UITextFieldDelegate, FormekaErrorCell {

    public weak var delegate: FormekaTextFieldCellDelegate?
    public var valueChangedWithState: ((ValidationError?) -> Void)?
    public var valueEndChangeWithState: ((ValidationError?) -> Void)?
    public var valueEndChange: (() -> Void)?
    public var valueChanged: (() -> Void)?
    public var returnTapped: (() -> Bool)?
    public var maxNumberOfCharacters: Int?
    public var shouldTrimCurrencyPrefix: Bool = false

	open var errorMessage: String? {
		didSet {
			errorLabel?.text = errorMessage
			errorLabel?.alpha = errorMessage == nil ? 0 : 1
		}
	}

    override open var textLabel: UILabel? { return titleLabel }

    @IBOutlet public weak var titleLabel: UILabel! {
        didSet {
            titleLabel.text = nil
        }
    }
    @IBOutlet open weak var textField: UITextField! {
        didSet {
            textField.text = nil
        }
    }
	@IBOutlet open weak var errorBackground: UIView? {
		didSet {
			errorBackground?.backgroundColor = .clear
		}
	}
    @IBOutlet open weak var errorLabel: UILabel? {
        didSet {
            errorLabel?.text = nil
            errorLabel?.alpha = 0
        }
    }

	open override var isFirstResponder: Bool { return textField.isFirstResponder }

    override open func prepareForReuse() {
        super.prepareForReuse()

        errorLabel?.text = nil
        errorLabel?.alpha = 0
        accessoryView = nil
    }
    
    
    open func textFieldShouldClear(_ textField: UITextField) -> Bool {
        delegate?.textFieldDidUpdateContent(value: "", cell: self)
        return true
    }
    
    open func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {

        let text: NSString = textField.text as NSString? ?? ""
        let replacementText = text.replacingCharacters(in: range, with: string).trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)

        if let maxNumberOfCharacters = maxNumberOfCharacters {
            if replacementText.count > maxNumberOfCharacters {
                return false
            }
        }

        delegate?.textFieldDidUpdateContent(value: replacementText, cell: self)

        return true
    }

    public func textFieldDidBeginEditing(_ textField: UITextField) {

        delegate?.textFieldDidBeginEditing(cell: self)
    }

    public func textFieldDidEndEditing(_ textField: UITextField) {
        let trimmedText = trimCurrencyForValidation(text: textField.text)
        delegate?.textFieldDidEndEditing(value: trimmedText ?? "", cell: self)
        valueEndChange?()
    }

    private func trimCurrencyForValidation(text: String?) -> String? {
        guard let text,
              !text.isEmpty,
              shouldTrimCurrencyPrefix else {
            return text
        }
        var valueCopy = text
        valueCopy.remove(at: valueCopy.startIndex)
        return valueCopy
    }

    public func textFieldShouldReturn(_ textField: UITextField) -> Bool {

        return returnTapped?() ?? true
    }

    override open var canBecomeFirstResponder: Bool {

        return true
    }

    override open func becomeFirstResponder() -> Bool {

        return textField.becomeFirstResponder()
    }
}

// Custom views

public class DisabledPositionTextField: UITextField {

    override public func closestPosition(to point: CGPoint) -> UITextPosition? {

        let beginning = self.beginningOfDocument
        let textCount = text?.count ?? 0
        let end = self.position(from: beginning, offset: textCount)
        return end
    }
}


public protocol FormekaDatePickableCell: UITableViewCell, FormekaErrorCell {
    var valueChangedWithState: ((ValidationError?) -> Void)? { get set }
    var valueChanged: (() -> Void)? { get set }
}
