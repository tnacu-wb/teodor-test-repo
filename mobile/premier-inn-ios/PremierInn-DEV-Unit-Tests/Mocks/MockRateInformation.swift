//
//  MockRateInformation.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import SimpleNetwork

extension RateInformation {
    static func mock(
        name: String = "PROBRKST",
        classification: String,
        order: String? = "0",
        description: String? = "Some description",
        notes: String? = "Some notes",
        tags: [String?]? = ["Free breakfast"]
    ) -> Self {
        .init(
            rateName: name,
            rateClassification: classification,
            rateOrder: order,
            rateDescription: description,
            rateNotes: notes,
            rateTags: tags
        )
    }
}
