//
//  Allocation.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import Foundation

public struct DigitalKeyCheckInResponse: Decodable {
    public let roomNumber: String
    public let checkInStatus: CheckInStatus?

    public enum CheckInStatus: String, Decodable {
        case success = "Success"
        case failed = "Failed"
        case clean = "Clean"
    }
}
