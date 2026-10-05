//
//  DatatransPaymentSession.swift
//  SimpleNetwork
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

public struct DatatransPaymentSessionResponse: Decodable {
    public let transactionId: String

    public init(transactionId: String) {
        self.transactionId = transactionId
    }
}
