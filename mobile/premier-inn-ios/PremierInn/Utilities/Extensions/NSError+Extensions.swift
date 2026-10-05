//
//  NSError+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 20/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

extension NSError {
    var localizedExceptionMessage: String {
        var message = ""

        if let text = localizedFailureReason {
            message = text
        } else {
            message = PILocalizedString("Unknown error", comment: "Unknown error message")
        }

        message += "\n\n"
        message += PILocalizedString("Error code:", comment: "Error code") + " \(code)"

        return message
    }
}

// Maybe this can be put somewhere better for reuse
private extension String {
    static let errorCodeKey = "code"
    static let alternativeErrorCodeKey = "errorCode"
}

extension Error {
    /// Checks if error is an MS server error and if so returns the MS error code from the response
    var piMSErrorCode: String? {
        guard let requestManagerError = self as? RequestsManagerError else { return nil }
        switch requestManagerError {
        case RequestsManagerError.serverError(let dict):
            guard let code = dict?[.errorCodeKey] as? String else { return localizedDescription }
            return code
        default:
            return nil
        }
    }

    /// Checks if error error contains an MS error code and if so returns a message using MS error code mappings
    var piLocalizedDescription: String {
        guard let code = piMSErrorCode else { return localizedDescription }
        guard let msError = MSMappedError(rawValue: code) else { return localizedDescription }
        return msError.errorMessage
    }
}
