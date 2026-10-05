//
//  FormekaLoginRequestCell.swift
//  Formeka
//
//  Created by Marcello Mascia on 22/07/2017.
//  Copyright © 2017 Marcello Mascia. All rights reserved.
//

import UIKit

public protocol FormekaLoginRequestCellDelegate: AnyObject {

    func loginButtonDidTap()
}

public class FormekaLoginRequestCell: FormekaTableViewCell {

    public weak var delegate: FormekaLoginRequestCellDelegate?

    @IBOutlet public weak var label: UILabel!
    @IBOutlet public weak var loginButton: UIButton!

    @IBAction func loginButtonDidTap(_ sender: UIButton) {

        delegate?.loginButtonDidTap()
    }
}
