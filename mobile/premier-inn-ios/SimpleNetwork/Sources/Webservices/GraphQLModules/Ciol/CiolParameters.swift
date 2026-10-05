//
//  CIOLParameters.swift
//  SimpleNetwork
//
//  Created by Muresan, Andreea (Cognizant) on 16.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func confirmPreCheckInOutVariables(
        basketReference: String,
        isCiol: Bool = false,
        includeIsCiol: Bool
    ) -> PIDictionary {
        var params = PIDictionary()

        params["basketReference"] = basketReference

        // Only include isCiol parameter for check-in
        if includeIsCiol {
            params["isCiol"] = isCiol
        }

        return params
    }
}
