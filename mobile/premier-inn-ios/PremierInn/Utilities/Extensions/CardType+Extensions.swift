//
//  CardType+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

extension CardType {
	var imageURL: URL? {
		Constants.CardTypeConfigMapper(rawValue: cardCode)?.completeUrl
	}

    var cardName: String {
        Constants.CardTypeConfigMapper(rawValue: cardCode)?.cardName ?? ""
    }
}
