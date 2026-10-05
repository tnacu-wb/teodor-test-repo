//
//  PushBootstrap.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit
import FirebaseMessaging

struct PushBootstrap: BootstrapTask {
    private let appDelegate: AppDelegate
    private let firebaseMessaging: MessagingType

    init(
        appDelegate: AppDelegate,
        firebaseMessaging: MessagingType = Messaging.messaging()
    ) {
        self.appDelegate = appDelegate
        self.firebaseMessaging = firebaseMessaging
    }

    func run() {
        firebaseMessaging.delegate = appDelegate
    }
}
