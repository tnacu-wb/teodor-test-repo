//
//  NotificationsObserverManager.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

final class NotificationObserverManager: ObserverManageable {
    private let notificationCenter: NotificationCenter
    private var observers: [NSObjectProtocol] = []

    init(notificationCenter: NotificationCenter = .default) {
        self.notificationCenter = notificationCenter
    }

    @discardableResult
    func observe(
        name: Notification.Name,
        object: Any? = nil,
        queue: OperationQueue? = .main,
        using block: @escaping (Notification) -> Void
    ) -> NSObjectProtocol {
        let observer = notificationCenter.addObserver(
            forName: name,
            object: object,
            queue: queue,
            using: block
        )

        observers.append(observer)
        return observer
    }

    func removeAll() {
        observers.forEach { notificationCenter.removeObserver($0) }
        observers.removeAll()
    }

    deinit {
        removeAll()
    }
}
