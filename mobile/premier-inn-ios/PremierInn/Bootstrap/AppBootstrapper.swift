//
//  AppBootstrapper.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

struct AppBootstrapper {
    private let tasks: [BootstrapTask]

    init(tasks: [BootstrapTask]) {
        self.tasks = tasks
    }

    func start() {
        tasks.forEach { $0.run() }
    }
}
