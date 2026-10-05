//
//  MockBootstrapTask.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class MockBootstrapTask: BootstrapTask {

    private(set) var runCallCount = 0
    private(set) var executionIndex: Int?

    private let onRun: (() -> Void)?

    init(onRun: (() -> Void)? = nil) {
        self.onRun = onRun
    }

    func run() {
        runCallCount += 1
        onRun?()
    }
}
