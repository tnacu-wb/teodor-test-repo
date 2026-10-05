//
//  FormekaSubmitButtonCell.swift
//  Formeka
//
//  Created by Marcello Mascia on 22/07/2017.
//  Copyright © 2017 Marcello Mascia. All rights reserved.
//

import UIKit

public protocol FormekaSubmitButtonCellDelegate: AnyObject {

    func submitButtonDidTap(cell: FormekaSubmitButtonCell)
}

public class FormekaSubmitButtonCell: SimpleSeparatorsCell {

    public weak var delegate: FormekaSubmitButtonCellDelegate?

    @IBOutlet public weak var button: UIButton!
    @IBOutlet public weak var topConstraint: NSLayoutConstraint!
    @IBOutlet public weak var bottomConstraint: NSLayoutConstraint!
    @IBOutlet public weak var activityIndicator: UIActivityIndicatorView!

    @IBAction func submitButtonDidTap(_ sender: UIButton) {

        delegate?.submitButtonDidTap(cell: self)
    }
}
