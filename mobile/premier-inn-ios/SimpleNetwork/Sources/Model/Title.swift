//
//  Title.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 19/01/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public enum TitleEnumError: Error {
    case unknownTitle
}

public enum Title: String, Codable {
    case mr = "Mr"
    case mrs = "Mrs"
    case ms = "Ms"
    case miss = "Miss"

    public var localised: String {
        switch self {
        case .mr:
            return NSLocalizedString("Mr", comment: "")
        case .mrs:
            return NSLocalizedString("Mrs", comment: "")
        case .ms:
            return NSLocalizedString("Ms", comment: "")
        case .miss:
            return NSLocalizedString("Miss", comment: "")
        }
    }
}

public extension Title {
    init(title: String) throws {
        switch title {
        case "Mr", "Herr":
            self = .mr
        case "Mrs", "Frau":
            self = .mrs
        case "Ms":
            self = .ms
        case "Miss":
            self = .miss
        default:
            throw TitleEnumError.unknownTitle
        }
    }
}
