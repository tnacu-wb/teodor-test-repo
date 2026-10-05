//
//  MVTManager.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

class MVTest {
    let tag: String
    let identifier: String
    let completionIdentifier: String?
    var defaultValue: Any
    var value: Any?

    init(tag: String, identifier: String, completionIdentifier: String?, defaultValue: Any) {
        self.tag = tag
        self.identifier = identifier
        self.completionIdentifier = completionIdentifier
        self.defaultValue = defaultValue
    }
}

extension MVTest: Equatable {
    static func == (lhs: MVTest, rhs: MVTest) -> Bool {
        lhs.tag == rhs.tag && lhs.identifier == rhs.identifier && lhs.completionIdentifier == rhs.completionIdentifier
    }
}

protocol MVTManagerProvider: AnyObject {
    func load(test: MVTest, completion: @escaping (Any?) -> Void)
    func complete(test: MVTest)
}

class MVTManager {
    static let sharedInstance = MVTManager()

    var provider: MVTManagerProvider?

    private let dispatchGroup = DispatchGroup()
    private(set) var tests: [MVTest]?

    func load(tests: [MVTest], completion: @escaping () -> Void) {
        self.tests = tests

        self.tests?.forEach { test in
            test.defaultValue = test.defValue()

            guard let provider = self.provider else { return }

            DispatchQueue.global(qos: .userInitiated).async {
                self.dispatchGroup.enter()

                provider.load(test: test) { response in
                    test.value = response
                    test.updateLocalDefault()

                    let debug = response ?? "Error loading test: No resource for identifier: \(test.identifier)"
                    print("Multivariant Test Response: \(debug)")

                    self.dispatchGroup.leave()
                }
            }
        }

        dispatchGroup.notify(queue: .main) {
            completion()
        }
    }

    func value(forTestWithIdentifier identifier: String) -> Any? {
        guard let test = tests?.first(where: { $0.identifier == identifier }) else { return nil }

        return test.value ?? test.defaultValue
    }

    func complete(testWithIdentifier identifier: String) {
        guard let test = tests?.first(where: { $0.identifier == identifier }) else { return }

        provider?.complete(test: test)
    }
}

// A simple example of a locally stored option
class SimpleMVTResourceProvider: MVTManagerProvider {
    func load(test: MVTest, completion: @escaping (Any?) -> Void) {
        completion(String(test.identifier.reversed()))
    }

    func complete(test: MVTest) {
        print("Huzzah!")
    }
}
