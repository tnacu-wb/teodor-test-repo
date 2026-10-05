//
//  Misc.swift
//  PremierInn
//
//  Created by Marcello Mascia on 11/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

public protocol FormekaErrorCell {

	var errorMessage: String? { get set }
}

public class FormekaHeader: UITableViewHeaderFooterView {

    @IBOutlet public weak var titleLabel: UILabel!
}

public class FormekaIconFooter: UITableViewHeaderFooterView {

    @IBOutlet public weak var imageView: UIImageView!
    @IBOutlet public weak var titleLabel: UILabel!
    @IBOutlet public weak var grayAreaView: UIView!
    @IBOutlet public weak var lineView: UIView!
}

extension String: FormekaValue {

	public var displayName: String { return self }
}

extension Date: FormekaValue {

	public var displayName: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd/MM/yyyy"
        return dateFormatter.string(from: self)
    }
}

extension FormekaViewModel {

	public var values: JsonDictionary {

		var dict = JsonDictionary()

		sections.forEach { section in

			section.rows.forEach { row in
				dict[row.tag] = row.value
			}
		}

		return dict
	}

	public func validate() throws {

        var validationError: Error?

        for section in sections.reversed() {
            for row in section.rows.reversed() {

				do {
                    try row.validate()
                } catch {
                    validationError = error
                }
            }
        }

        if let error = validationError {
            throw error
        }
    }
}

extension UIViewController {

	public func showAlertWith(title: String, message: String?, handler: ((UIAlertAction) -> Void)? = nil) {

		let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let action = UIAlertAction(
            title: NSLocalizedString("OK", comment: "OK button title"),
            style: .cancel,
            handler: handler
        )
		controller.addAction(action)

		present(controller, animated: true, completion: nil)
	}

    public func showErrorAlertWith(title: String, message: String? = nil, error: Error?, handler: ((UIAlertAction) -> Void)? = nil) {

		let message = message ?? error?.localizedDescription ?? NSLocalizedString("Please try again", comment: "")

		showAlertWith(title: title, message: message, handler: handler)
	}
}

public protocol FormekaCellMargins {

    var margins: UIEdgeInsets { get }
}
