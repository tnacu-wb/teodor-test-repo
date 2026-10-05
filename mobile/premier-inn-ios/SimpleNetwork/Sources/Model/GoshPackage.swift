//
//  GoshPackage.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 24/01/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

public struct GoshPackage: Decodable {
    public let name: String?
    public let description: String?
    public let imageSrc: String?
    private let donationPackages: [DonationPackages]?

    public var packagesOrdered: [DonationPackages]? {
        donationPackages?.sorted(by: { $0.cost < $1.cost })
    }
}

public struct DonationPackages: Decodable {
    public let code: String
    private let currency: String
    private let unitPrice: Float

    public var cost: Cost {
        Cost(amount: Double(unitPrice), currencyCode: currency)
    }
}
