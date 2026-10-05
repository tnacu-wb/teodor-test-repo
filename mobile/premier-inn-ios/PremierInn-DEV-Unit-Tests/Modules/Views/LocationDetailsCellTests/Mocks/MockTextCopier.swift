//
//  SpyTextCopier.swift
//  PremierInn
//
//  Created by Clint Mengolli on 27/04/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

@testable import PremierInn
import MapKit

final class MockTextCopier: TextCopying {
    private(set) var copiedText: String?

    func copy(_ string: String) {
        copiedText = string
    }
}

