//
//  MockPassManager.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 21/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import PassKit
import SimpleNetwork

final class MockPassManager: PassManagerProtocol {
    private(set) var isInPassManager: Bool
    private(set) var makePassCalled = false
    private(set) var containsPassCalled = false
    private(set) var getPassCalled = false
    private(set) var replacePassCalled = false

    var shouldThrowErrorForMakePass = false
    var makePassResult = PKPass()
    var containsPassResult: Bool = false
    var getPassResult: PKPass?

    init(isInPassManager: Bool = true) {
        self.isInPassManager = isInPassManager
    }

    func isKeyInUsersWallet(passId: String) -> Bool {
        isInPassManager
    }

    func fetchPassFromLibrary(passId: String) -> PKPass? {
        nil
    }

    func makePass(from data: Data) throws -> PKPass {
        makePassCalled = true
        if shouldThrowErrorForMakePass {
            throw NSError(domain: "SomeError", code: 202)
        }
        return makePassResult
    }
    
    func contains(_ pass: PKPass) -> Bool {
        containsPassCalled = true
        return containsPassResult
    }
    
    func getPass(withPassTypeIdentifier: String, serialNumber: String) -> PKPass? {
        getPassCalled = true
        return getPassResult
    }
    
    func replace(with pass: PKPass) {
        replacePassCalled = true
    }
}
