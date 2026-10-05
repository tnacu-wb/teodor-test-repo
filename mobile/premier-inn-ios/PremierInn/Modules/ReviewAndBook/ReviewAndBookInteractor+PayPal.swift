//
//  ReviewAndBookInteractor+PayPal.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 20/10/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public enum PayPalError: LocalizedError {
    case missingClientToken
    case apiClientFailed
    case genericError
    case failedToRetrieveDeviceData

    // Temporary state while PayPal is migrated off Braintree onto the Datatrans SDK
    public var errorDescription: String? {
        PILocalizedString("bookingGenericError")
    }
}

extension ReviewAndBookInteractor {
    func startPaypalVault(completion: @escaping (_ nonce: String?, _ paypalDeviceData: String?, _ error: Error?) -> Void) {
        guard let clientToken = bookingDetails.primaryPaymentMethod?.clientToken else {
            completion(nil, nil, PayPalError.missingClientToken)
            return
        }
    }
}
