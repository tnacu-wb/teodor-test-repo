//
//  Hotel+Announcement.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 20/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation

public struct Announcement: Codable {
    public var showAnnouncement: Bool
    public var text: String?                // HTML
    public var bbText: String?
    public var startDateString: String?
    public var endDateString: String?

    public var startDate: Date? {
        guard let startDateString = startDateString else { return nil }

        return DateFormatter.analyticsDateFormatter.date(from: startDateString)
    }
    public var endDate: Date? {
        guard let endDateString = endDateString else { return nil }

        return DateFormatter.analyticsDateFormatter.date(from: endDateString)
    }

    enum CodingKeys: String, CodingKey {
        case showAnnouncement
        case startDateString = "startDate"
        case endDateString = "endDate"
        case text
        case bbText
    }

    public init(showAnnouncement: Bool, text: String?, startDateString: String?, endDateString: String?) {
        self.showAnnouncement = showAnnouncement
        self.text = text
        self.startDateString = startDateString
        self.endDateString = endDateString
    }
}

public extension Hotel {
    class func announcement(with dictionary: PIDictionary?) -> Announcement? {
        guard let dictionary = dictionary else { return nil }
        guard let data = try? JSONSerialization.data(withJSONObject: dictionary, options: .prettyPrinted) else { return nil }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(Announcement.self, from: data)
        } catch {
            print(error)
            return nil
        }
    }
}
