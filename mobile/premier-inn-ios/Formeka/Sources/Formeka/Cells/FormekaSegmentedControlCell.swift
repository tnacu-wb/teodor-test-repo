//
//  FormekaSegmentedControlCell.swift
//  Formeka
//
//  Created by Marcello Mascia on 22/07/2017.
//  Copyright © 2017 Marcello Mascia. All rights reserved.
//

import UIKit

public protocol FormekaSegmentedControlCellDelegate: AnyObject {

    func formekaSegmentedControlValueDidChange(cell: FormekaSegmentedControlCell)
    func removePersistedCompanyName()
}

public protocol CompanyNameDelegate: AnyObject {

    func removePersistedCompanyName()
}

public class FormekaSegmentedControlCell: SimpleSeparatorsCell, FormekaErrorCell {

    public weak var delegate: FormekaSegmentedControlCellDelegate?
	open var errorMessage: String? {
		didSet {
			errorLabel?.text = errorMessage
			errorLabel?.alpha = errorMessage == nil ? 0 : 1
		}
	}

    @IBOutlet public weak var segmentedControl: UISegmentedControl!
    @IBOutlet public weak var errorLabel: UILabel! {
        didSet {
            errorLabel?.text = nil
            errorLabel?.alpha = 0
        }
    }

    @IBAction func segmentedControlValueDidChange(_ sender: UISegmentedControl) {

        delegate?.formekaSegmentedControlValueDidChange(cell: self)
    }
}
