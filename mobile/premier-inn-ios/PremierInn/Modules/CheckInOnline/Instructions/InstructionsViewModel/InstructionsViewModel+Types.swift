//
//  InstructionsViewModel+Types.swift
//  PremierInn
//
//  Created by Badea, Bogdan (Cognizant) on 20.05.2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import PassKit

enum InstructionsType: Equatable {
    case roomKeyWithQRCode
    case roomKeyWithoutQRCode
    case usingDigitalKey
    case gettingDigitalKey(isKioskAvailable: Bool, isDESite: Bool)
    case roomWithQRCodeAndDigitalKey(isKeyDownloaded: Bool)
    case roomWithoutQRCodeButWithDigitalKey(isKeyDownloaded: Bool)
}

enum InstructionsSheetSize {
    case fullSize
    case custom(height: CGFloat)
}
