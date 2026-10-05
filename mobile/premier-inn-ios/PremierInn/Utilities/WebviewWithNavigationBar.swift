//
//  WebviewWithNavigationBar.swift
//  
//
//  Created by Santa Gurung on 19/02/2025.
//

import SwiftUI

struct WebviewWithNavigationBar: View {
    let url: URL?
    @Binding var isSheetPresented: Bool

    var body: some View {
        NavigationStack {
            WKWebViewWrapper(url: url)
                .ignoresSafeArea()
                .toolbar {
                    ToolbarItem(placement: .topBarLeading) {
                        Button(PILocalizedString("Done")) {
                            isSheetPresented = false
                        }
                    }
                }
        }
    }
}
