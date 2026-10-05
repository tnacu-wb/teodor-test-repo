//
//  LocationDetailsPasteboardWriting.swift
//  PremierInn
//
//  Created by Clint Mengolli on 27/04/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

protocol TextCopying {
    func copy(_ string: String)
}

struct PasteboardTextCopier: TextCopying {
    func copy(_ string: String) {
        UIPasteboard.general.string = string
    }
}
