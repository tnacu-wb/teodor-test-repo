//
//  FormekaDateCell.swift
//  Formeka
//
//  Created by Raiu, George Marius (Cognizant) on 05.03.2025.
//  Copyright © 2025 Marcello Mascia. All rights reserved.
//

import UIKit

open class FormekaDateCell: SimpleSeparatorsCell, FormekaErrorCell {
    open var errorMessage: String?
    public var valueChangedWithState: ((ValidationError?) -> Void)?
    public var valueChanged: ((Date) -> Void)?
    public weak var delegate: FormekaDateCellDelegate?
}
