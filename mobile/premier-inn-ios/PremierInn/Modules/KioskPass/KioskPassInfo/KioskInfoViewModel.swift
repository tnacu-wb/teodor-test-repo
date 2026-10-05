//
//  KioskInfoViewModel.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 16/05/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct KioskInfoViewModel {
    var title: String = ""
    var about: String = ""
    var kioskImage: Image
    var instructions: String = ""
    var doneButton: String = ""

    init() {
        title = PILocalizedString("kioskInfoPageTitle")
        kioskImage = Image("SOCIAL_KIOSK_FOREGROUND")
        about = PILocalizedString("kioskInfoPageAbout")
        instructions = PILocalizedString("kioskInfoPageInstructions")
        doneButton = PILocalizedString("Done")
    }
}
