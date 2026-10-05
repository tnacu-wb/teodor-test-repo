//
//  SimpleStorageManager.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

enum SimpleStorageError: LocalizedError {
    case duplicatedItem
}

public class SimpleStorageManager<T: DictionaryInitialisable> {
    private let dataSource: UserDefaults
    private let keyName = String(describing: T.self) + "Store"

    public init(dataSource: UserDefaults) {
        self.dataSource = dataSource
    }

    public var items: [T] {
        guard let dictionaries = dataSource.array(forKey: keyName) as? [PIDictionary] else { return [] }

        return dictionaries.compactMap { try? T(dictionary: $0) }
    }

    func toggle(_ item: T) throws {
        if items.contains(item) {
            _ = remove(item)
        } else {
            try add(item)
        }
    }

    public func add(_ item: T) throws {
        if items.contains(item) { throw SimpleStorageError.duplicatedItem }

        guard var dictionaries = dataSource.array(forKey: keyName) as? [PIDictionary] else {
            dataSource.set([item.dictionary], forKey: keyName)
            return
        }

        dictionaries.append(item.dictionary)

        dataSource.set(dictionaries, forKey: keyName)
    }

    public func remove(_ item: T) -> Bool {
        guard var dictionaries = dataSource.array(forKey: keyName) as? [PIDictionary] else { return false }

        guard let index = dictionaries.firstIndex(where: { dict -> Bool in
            if let innerItem = try? T(dictionary: dict), innerItem == item {
                return true
            }

            return false
        }) else { return false }

        dictionaries.remove(at: index)

        dataSource.set(dictionaries, forKey: keyName)

        return true
    }

    func reset() {
        dataSource.removeObject(forKey: keyName)
        dataSource.synchronize()
    }
}

extension SimpleStorageManager where T: Stay {
    private func update(with summary: T) -> Bool {
        let removed = remove(summary)

        try? add(summary)

        return removed
    }

    public func update(with summaries: [T]) -> Bool {
        var removed = false

        for summary in summaries where update(with: summary) {
            removed = true
        }

        NotificationCenter.default.post(
            name: .reservationSummariesDidChange,
            object: nil,
            userInfo: [Constants.NotificationKeys.reservationSummariesWereImported: !removed]
        )

        return removed
    }
}
