//
//  PassManager.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 01/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import Foundation
import PassKit

public protocol PassManagerProtocol {
    func isKeyInUsersWallet(passId: String) -> Bool

    func fetchPassFromLibrary(passId: String) -> PKPass?

    func makePass(from data: Data) throws -> PKPass

    func contains(_ pass: PKPass) -> Bool

    func getPass(
        withPassTypeIdentifier: String,
        serialNumber: String
    ) -> PKPass?

    func replace(with pass: PKPass)
}

public class PassManager: PassManagerProtocol {
    private let library = PKPassLibrary()

    public init() { }

    public func isKeyInUsersWallet(passId: String) -> Bool {
        fetchPassFromLibrary(passId: passId) != nil
    }

    public func fetchPassFromLibrary(passId: String) -> PKPass? {
        library.passes(of: .secureElement)
		    .first { $0.secureElementPass?.primaryAccountIdentifier.contains(passId) ?? false }
    }

    public func makePass(from data: Data) throws -> PKPass {
        try PKPass(data: data)
    }

    public func contains(_ pass: PKPass) -> Bool {
        library.containsPass(pass)
    }

    public func getPass(
        withPassTypeIdentifier: String,
        serialNumber: String
    ) -> PKPass? {
        library.pass(
            withPassTypeIdentifier: withPassTypeIdentifier,
            serialNumber: serialNumber
        )
    }

    public func replace(with pass: PKPass) {
        library.replacePass(with: pass)
    }
}
