//
//  Channel.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 19/12/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public enum Channel: String, Decodable, CaseIterable {
    case PI
    case BB
    case EMPLOYEE

    // Array used for business restrictions
    static var channelsWithRestrictions: [Channel] {
        Channel.allCases
    }
}
