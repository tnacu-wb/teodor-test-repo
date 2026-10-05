//
//  MockObserverManager.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import PremierInn

final class MockObserverManager: ObserverManageable {

    private(set) var observedNames: [Notification.Name] = []
    private(set) var handlers: [Notification.Name: (Notification) -> Void] = [:]

    @discardableResult
    func observe(
        name: Notification.Name,
        object: Any?,
        queue: OperationQueue?,
        using block: @escaping (Notification) -> Void
    ) -> NSObjectProtocol {

        observedNames.append(name)
        handlers[name] = block
        return NSObject()
    }

    func removeAll() {}
}
