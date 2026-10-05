//
//  Facility.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

public struct Facility {
    public var code: String
    var description: String
    public var legend: String

    init(dictionary: PIDictionary) {
        self.code = dictionary["code"] as? String ?? ""
        self.description = dictionary["description"] as? String ?? ""
        self.legend = dictionary["legend"] as? String ?? ""
    }
}
