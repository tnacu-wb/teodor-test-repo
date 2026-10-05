//
//  StatusResult.swift
//  SimpleNetwork
//
//  Created by Raiu, George Marius (Cognizant) on 04.04.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

public struct StatusResult: Codable {
    public let status: Status
    let message: String

    public enum Status: String, Codable {
        case error = "Error"
        case success = "Success"
    }
}
