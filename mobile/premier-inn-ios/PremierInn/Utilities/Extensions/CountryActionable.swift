//
//  CountryActionable.swift
//  PremierInn
//
//  Created by Marcello Mascia on 11/07/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

enum CountryActionableRow: String {
    case country
    case postCode
    case addressLine1
    case addressLine2
    case addressLine3
}

protocol CountryActionable: FormekaPostCodeCellDelegate {
}

class CountryValidator: Validator {
	override func validate(row: FormekaModelRow) throws {
        guard let country = row.value as? Country else { throw RowValidatorError(
            row: row,
            error: ValidationError.valueRequired(PILocalizedString("country", comment: ""))
        ) }
        guard country.code != nil else { throw RowValidatorError(
            row: row,
            error: ValidationError.valueRequired(PILocalizedString("country", comment: ""))
        ) }
    }
}
