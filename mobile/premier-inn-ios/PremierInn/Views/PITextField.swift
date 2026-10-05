//
//  PITextField.swift
//  PremierInn
//
//  Created by Marcello Mascia on 13/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

@IBDesignable class PITextField: UITextField {
	@IBInspectable var originX: CGFloat = 15.0

	override func textRect(forBounds bounds: CGRect) -> CGRect {
		CGRect(x: originX, y: 0.0, width: bounds.width - originX, height: bounds.height)
	}

	override func editingRect(forBounds bounds: CGRect) -> CGRect {
		CGRect(x: originX, y: 0.0, width: bounds.width - originX, height: bounds.height)
	}

	override func placeholderRect(forBounds bounds: CGRect) -> CGRect {
		CGRect(x: originX, y: 0.0, width: bounds.width - originX, height: bounds.height)
	}
}
