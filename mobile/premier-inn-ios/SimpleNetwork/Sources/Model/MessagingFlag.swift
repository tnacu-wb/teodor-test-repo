//
//  MessagingFlag.swift
//  PremierInn
//
//  Created by Marcello Mascia on 20/04/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

public struct MessagingFlag {
    public var text: String
    public var hexColor: String

    public init?(dictionary: PIDictionary?) {
        guard let dictionary = dictionary else { return nil }
        guard let text = dictionary["flagText"] as? String else { return nil }
        guard text.isEmpty == false else { return nil }
        guard let hexColor = dictionary["flagColor"] as? String else { return nil }

        self.text = text
        self.hexColor = hexColor
    }
}
