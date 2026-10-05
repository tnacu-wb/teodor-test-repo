//
//  UpdateCiolStatusResponse.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 20/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

public struct UpdateCiolStatusResponse: Decodable {
    /// Named `ciolStatus` to provide clearer context, since the mobile client
    /// does not have meaningful insight into what `updateUdfc20` refers to
    /// in the Opera backend.
    public let ciolStatus: CiolStatus?

    enum CodingKeys: String, CodingKey {
        case ciolStatus = "updateUdfc20"
    }
}
