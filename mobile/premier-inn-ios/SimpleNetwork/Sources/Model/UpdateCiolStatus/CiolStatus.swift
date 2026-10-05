//
//  CiolStatus.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 11/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

public enum CiolStatus: String, Decodable {
    case ciolStarted = "CIOL_STARTED"
    case walletPass = "WALLET_PASS"
}
