//
//  AddressSummary.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 23/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

public enum AddressSummaryError: Error {
    case missingAddressSummaryDictionary
    case missingId
}

public struct AddressSummary: Codable {
    public let id: String
    let label: String?
    public let searchedPostCode: String

    public init(dictionary: PIDictionary?, postCode: String) throws {
        guard let dictionary = dictionary else { throw AddressSummaryError.missingAddressSummaryDictionary }

        guard let id = dictionary["id"] as? String else { throw AddressSummaryError.missingId }

        self.id = id
        self.label = dictionary["addressText"] as? String
        self.searchedPostCode = postCode
    }

    public var description: String { label ?? "" }
}
