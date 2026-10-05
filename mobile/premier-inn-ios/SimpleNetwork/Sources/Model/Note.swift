//
//  Note.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 19/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

public struct Note {
    let dateString: String
    public let text: String
    let priority: Int
    public var startDate: Date?
    public var endDate: Date?

    init?(dictionary: PIDictionary?) {
        guard let dictionary = dictionary else { return nil }

        self.dateString = dictionary["date"] as? String ?? ""
        self.text = dictionary["text"] as? String ?? ""
        self.priority = dictionary["priority"] as? Int ?? 0

        if let startDateString = dictionary["startDate"] as? String {
            self.startDate = DateFormatter.analyticsDateFormatter.date(from: startDateString)
        }

        if let endDateString = dictionary["endDate"] as? String {
            self.endDate = DateFormatter.analyticsDateFormatter.date(from: endDateString)
        }
    }
}
