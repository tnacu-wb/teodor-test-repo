//
//  BBUserConfig.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 12/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

public struct BBUserConfig: Codable {
    public let centralCard: String?
    public let awaitingApproval: Int?
    public let employeeId: String?
}
