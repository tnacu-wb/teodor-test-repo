//
//  MockMessaging.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import FirebaseMessaging
@testable import PremierInn

final class MockMessaging: MessagingType {

    weak var delegate: MessagingDelegate?
}
