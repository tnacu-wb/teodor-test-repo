//
//  FormekaButtonCell.swift
//  Formeka
//
//  Created by Marcello Mascia on 22/07/2017.
//  Copyright © 2017 Marcello Mascia. All rights reserved.
//

import UIKit

public class FormekaButtonCell: FormekaTableViewCell, FormekaErrorCell {

    override public var textLabel: UILabel? { return titleLabel }
	open var errorMessage: String? {
		didSet {
			errorLabel?.text = errorMessage
			errorLabel?.alpha = errorMessage == nil ? 0 : 1
		}
	}

    @IBOutlet public weak var titleLabel: UILabel!
    @IBOutlet public weak var valueLabel: UILabel!
    @IBOutlet public weak var errorLabel: UILabel? {
        didSet {
            errorLabel?.text = nil
            errorLabel?.alpha = 0
        }
    }

    override public func prepareForReuse() {
        super.prepareForReuse()

        errorLabel?.text = nil
        errorLabel?.alpha = 0
    }
}
