//
//  FormekaPostCodeCell.swift
//  Formeka
//
//  Created by Marcello Mascia on 14/04/2018.
//  Copyright © 2018 Marcello Mascia. All rights reserved.
//

import UIKit

public protocol FormekaPostCodeCellDelegate: FormekaTextFieldCellDelegate {

	func searchButtonDidTap(cell: FormekaPostCodeCell)
}

public class FormekaPostCodeCell: FormekaTextFieldCell {

	public weak var postCodeDelegate: FormekaPostCodeCellDelegate?

	@IBOutlet public weak var searchButton: UIButton!

	@IBAction func searchButtonDidTap(_ sender: UIButton) {

		postCodeDelegate?.searchButtonDidTap(cell: self)
	}
}
