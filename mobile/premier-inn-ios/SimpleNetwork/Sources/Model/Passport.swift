//
//  Passport.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 24/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

public struct Passport: Equatable {
	public let number: String
	public let countryOfIssue: String

    public init(number: String, countryOfIssue: String) {
        self.number = number
        self.countryOfIssue = countryOfIssue
    }

	var parameters: PIDictionary {
		var dict = PIDictionary()
		dict["number"] = number
		dict["countryOfIssue"] = countryOfIssue

		return dict
	}
}
