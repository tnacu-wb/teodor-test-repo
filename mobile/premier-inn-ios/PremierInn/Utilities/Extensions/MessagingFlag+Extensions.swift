//
//  MessagingFlag+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

extension MessagingFlag {
	var color: UIColor? {
		UIColor(hex: hexColor)
	}
	var textColor: UIColor? { .BaseWhite }
	var localizedText: String {
		PILocalizedString(text, comment: "")
	}
}
