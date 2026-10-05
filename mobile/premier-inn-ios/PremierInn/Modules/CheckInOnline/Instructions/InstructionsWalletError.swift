//
//  InstructionsWalletError.swift
//  PremierInn
//
//  Created by Badea, Bogdan (Cognizant) on 28.05.2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

enum InstructionsWalletPassError: LocalizedError {
    case fetchFailed
    case invalidData

    var errorDescription: String? {
        switch self {
        case .fetchFailed, .invalidData:
            return PILocalizedString("somethingWentWrongTryAgainMessage",
                                     comment: "Alert title general error message")
        }
    }
}
