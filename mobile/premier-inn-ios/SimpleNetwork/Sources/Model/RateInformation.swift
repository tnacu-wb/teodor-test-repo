//
//  RateInformation.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 26/01/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public struct RateInformation: Codable {
    public let rateName: String
    public let rateClassification: String
    let rateOrder: String?
    public let rateDescription: String?
    public let rateNotes: String?
    public let rateTags: [String?]?
}
