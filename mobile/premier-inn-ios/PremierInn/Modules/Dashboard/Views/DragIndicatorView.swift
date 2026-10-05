//
//  DragIndicatorView.swift
//  PremierInn
//
//  Created by Santa Gurung on 06/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct DragIndicatorView: View {
    var body: some View {
        RoundedRectangle(cornerRadius: 10)
            .fill(Color(.TintL3))
            .frame(width: 40, height: 6)
            .padding(.top, 8)
            .padding(.bottom, 4)
    }
}
