//
//  Font+SwiftUI.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 22/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

extension Text {
    func font(_ uiFont: UIFont) -> Text {
        font(uiFont.swiftUI)
    }
}

extension View {
    func font(_ uiFont: UIFont) -> some View {
        font(uiFont.swiftUI)
    }
}

private extension UIFont {
    var swiftUI: Font {
        Font(self)
    }
}
