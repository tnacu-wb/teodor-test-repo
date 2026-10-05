//
//  NSNumber+Extensions.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 15/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

extension NSNumber {
    func britishFormattedString() -> String {
        let formatter = NumberFormatter.init()
        formatter.usesGroupingSeparator = true
        formatter.groupingSeparator = ","
        formatter.groupingSize = 3
        formatter.locale = Locale.current

        return formatter.string(from: self) ?? ""
    }
}
