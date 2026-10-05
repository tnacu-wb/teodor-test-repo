//
//  Hotel+CloseOut.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 12/02/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

public struct AncillaryCloseOutItem: Codable {
    public let serviceCode: String?
    public let upsellCodes: String?
    public var startDateString: String?
    public var endDateString: String?

    enum CodingKeys: String, CodingKey {
        case serviceCode
        case startDateString = "startDate"
        case endDateString = "endDate"
        case upsellCodes
    }

    public var startDate: Date? {
        guard let startDateString = startDateString else { return nil }
        guard let date = DateFormatter.analyticsDateFormatter.date(from: startDateString) else { return nil }
        return date.dateNormalised
    }

    public var endDate: Date? {
        guard let endDateString = endDateString else { return nil }
        guard let date = DateFormatter.analyticsDateFormatter.date(from: endDateString) else { return nil }
        return date.dateNormalised
    }

    public init(serviceCode: String?, upsellCodes: String?, startDateString: String?, endDateString: String?) {
        self.serviceCode = serviceCode
        self.upsellCodes = upsellCodes
        self.startDateString = startDateString
        self.endDateString = endDateString
    }
}

extension Hotel {
    class func ancillaryCloseOutItems(dictionary: PIDictionary?) -> [AncillaryCloseOutItem]? {
        guard let dict = dictionary, let items = dict["items"] as? [PIDictionary] else { return nil }

        guard let data = try? JSONSerialization.data(withJSONObject: items, options: .prettyPrinted) else { return nil }
        let decoder = JSONDecoder()
        return try? decoder.decode([AncillaryCloseOutItem].self, from: data)
    }
}
