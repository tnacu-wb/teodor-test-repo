//
//  FirebaseAppConfigurator.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Firebase

protocol FirebaseAppConfigurable {
    static func configure(options: FirebaseOptions?)
}

enum FirebaseAppConfig: FirebaseAppConfigurable {
    static func configure(options: FirebaseOptions?) {
        if let options {
            FirebaseApp.configure(options: options)
        } else {
            FirebaseApp.configure()
        }
    }
}
