//
//  Messaging+Extensions.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import FirebaseMessaging

protocol MessagingType: AnyObject {
    var delegate: MessagingDelegate? { get set }
}

extension Messaging: MessagingType { }
