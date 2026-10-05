//
//  FormekaModelRow.swift
//  PremierInn
//
//  Created by Marcello Mascia on 19/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

public protocol FormekaValue {
    var displayName: String { get }
}

public class FormekaModelRow {

	public let tag: String
	public let cellSetup: TableRowCellSetup
	public let cellWillDisplay: TableRowWillDisplay?
	public let didSelect: TableRowCellSelection?
    public let inlineValidators: [Validator]
    public let onBlurValidators: [Validator]

    public var title: String?
	public var editable: Bool = false
	public var deleteAction: TableRowCellDeletion?
	public var value: FormekaValue?
	public var error: Error?

    public init(tag: String = "", title: String? = nil, inlineValidators: [Validator] = [], onBlurValidators: [Validator] = [], cellSetup: @escaping TableRowCellSetup, cellWillDisplay: TableRowWillDisplay? = nil, didSelect: TableRowCellSelection? = nil) {

		self.tag = tag
        self.title = title
		self.cellSetup = cellSetup
		self.cellWillDisplay = cellWillDisplay
		self.didSelect = didSelect
        self.inlineValidators = inlineValidators
        self.onBlurValidators = onBlurValidators
    }

    private func validate(validators: [Validator]) throws {

        do {
            for validator in validators {
                try validator.validate(row: self)
            }

            self.error = nil
        } catch {
            self.error = error

            throw error
        }
    }

    public func validate() throws {

        try validate(validators: inlineValidators + onBlurValidators)
    }

    func validateInline() throws {

        try validate(validators: inlineValidators)
    }

    func validateOnBlur() throws {

        try validate(validators: inlineValidators + onBlurValidators)
    }
}

extension FormekaModelRow: Equatable {

	public static func == (lhs: FormekaModelRow, rhs: FormekaModelRow) -> Bool {

		return lhs.tag == rhs.tag
	}
}
