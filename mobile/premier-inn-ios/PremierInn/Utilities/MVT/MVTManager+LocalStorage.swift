//
//  MVTManager+LocalStorage.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/08/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

enum MVTDefaultError: Error {
    case missingIdentifier
    case missingDefault
}

class MVTDefault: DictionaryInitialisable {
    let identifier: String
    let defaultValue: Any

    var dictionary: PIDictionary {
        [
            "identifier": identifier,
            "defaultValue": defaultValue
        ]
    }

    public required init(dictionary: PIDictionary) throws {
        guard let identifier = dictionary["identifier"] as? String else { throw MVTDefaultError.missingIdentifier }
        guard let defaultValue = dictionary["defaultValue"] else { throw MVTDefaultError.missingDefault }

        self.identifier = identifier
        self.defaultValue = defaultValue
    }

    public static func == (lhs: MVTDefault, rhs: MVTDefault) -> Bool {
        lhs.identifier == rhs.identifier
    }
}

extension MVTest {
    static let storage = SimpleStorageManager<MVTDefault>(dataSource: UserDefaults.standard)

    func updateLocalDefault() {
        guard let defaultValue = value else { return }
        guard let mvtDefault = try? MVTDefault(dictionary: ["identifier": tag, "defaultValue": defaultValue]) else { return }

        _ = MVTest.storage.update(with: mvtDefault)
    }

    func defValue() -> Any {
        MVTest.storage.items.first { $0.identifier == tag }?.defaultValue ?? defaultValue
    }
}

extension SimpleStorageManager where T: MVTDefault {
    func update(with test: T) -> Bool {
        let removed = remove(test)

        try? add(test)

        return removed
    }
}
