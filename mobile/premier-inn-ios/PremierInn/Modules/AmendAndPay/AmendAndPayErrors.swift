//
//  AmendAndPayErrors.swift
//  PremierInn
//
//  Created by Santa Gurung on 18/12/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

enum AmendAndPayError: LocalizedError {
    case viewModelNil
    case missingTemporaryReference
    case missingSelectedPaymentMethod
    case confirmAmendLogicFail
}

enum AmendAndPayPaymentError: LocalizedError {
    case amendFailed
}
