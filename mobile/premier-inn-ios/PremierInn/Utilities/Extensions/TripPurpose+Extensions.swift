//
//  TripPurpose+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Formeka

extension TripPurpose {
	var localizedString: String {
		switch self {
		case .leisure:
			return PILocalizedString("tripPurposeLeisure", comment: "Trip purpose: leisure")
		case .business:
			return PILocalizedString("tripPurposeBusiness", comment: "Trip purpose: business")
		}
	}
}

extension TripPurpose: @retroactive FormekaValue {
	public var displayName: String { localizedString }
}
