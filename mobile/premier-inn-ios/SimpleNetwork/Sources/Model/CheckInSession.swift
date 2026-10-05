//
//  CheckInSession.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 18/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation

public struct CheckInSession {
    let confirmationNumber: String
    let sessionId: String

    public let timestamp: String

    public static let dateFormat = "yyyy/MM/dd hh:mm:ss"

    public init(confirmationNumber: String, sessionId: String, date: Date = Date()) {
        self.confirmationNumber = confirmationNumber
        self.sessionId = sessionId

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = CheckInSession.dateFormat
        self.timestamp = dateFormatter.string(from: date)
    }
}

private enum CheckInSessionError: LocalizedError {
    case missingConfirmationNumber
    case missingSessionId
    case missingTimestamp

    var errorDescription: String? {
        switch self {
        case .missingConfirmationNumber:
            return "Missing confirmation number"
        case .missingSessionId:
            return "Missing session ID"
        case .missingTimestamp:
            return "Missing timestamp"
        }
    }
}

extension CheckInSession: DictionaryInitialisable {
    public var identifier: String {
        confirmationNumber
    }

    public var dictionary: PIDictionary {
        [
            "confirmationNumber": confirmationNumber,
            "sessionId": sessionId,
            "timestamp": timestamp
        ]
    }

    public init(dictionary: PIDictionary) throws {
        guard let confirmationNumber = dictionary["confirmationNumber"] as? String
            else { throw CheckInSessionError.missingConfirmationNumber }
        guard let sessionId = dictionary["sessionId"] as? String else { throw CheckInSessionError.missingSessionId }
        guard let timestamp = dictionary["timestamp"] as? String else { throw CheckInSessionError.missingTimestamp }

        self.confirmationNumber = confirmationNumber
        self.sessionId = sessionId
        self.timestamp = timestamp
    }
}
