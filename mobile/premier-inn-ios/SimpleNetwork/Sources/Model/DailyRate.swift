//
//  DailyRate.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

public struct DailyRate: Codable {
    enum CodingKeys: String, CodingKey {
        case dateString = "date"
        case price
        case cityTax
    }

	private let dateString: String

	public let price: Cost
    public let cityTax: Cost?

	public var date: Date? { DateFormatter.parameterFormatter.date(from: dateString) }

	init?(dictionary: PIDictionary) {
		guard let dateString = dictionary["date"] as? String else { return nil }
		guard let priceDictionary = dictionary["price"] as? PIDictionary else { return nil }
		guard let cost = try? Cost(dictionary: priceDictionary) else { return nil }

		self.dateString = dateString
		self.price = cost
        self.cityTax = try? Cost(dictionary: dictionary["cityTax"] as? PIDictionary)
	}

    func priceDescription(withCityTax shouldIncludeCityTax: Bool) -> String? {
        if shouldIncludeCityTax { return price.localizedValue }

        let tax = cityTax ?? Cost(amount: 0, currencyCode: price.currencyCode)

        return (price - tax)?.localizedValue
    }
}
