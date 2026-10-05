//
//  ObserverManagable.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

protocol ObserverManageable: AnyObject {
    @discardableResult
    func observe(
        name: Notification.Name,
        object: Any?,
        queue: OperationQueue?,
        using block: @escaping (Notification) -> Void
    ) -> NSObjectProtocol

    func removeAll()
}

extension ObserverManageable {
    @discardableResult
    func observe(
        name: Notification.Name,
        using block: @escaping (Notification) -> Void
    ) -> NSObjectProtocol {
        observe(name: name, object: nil, queue: .main, using: block)
    }
}
