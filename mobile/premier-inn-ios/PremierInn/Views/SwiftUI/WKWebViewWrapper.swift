//
//  WKWebViewWrapper.swift
//  PremierInn
//
//  Created by Santa Gurung on 17/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI
import WebKit

struct WKWebViewWrapper: UIViewRepresentable {
    let url: URL?

    func makeUIView(context: Context) -> WKWebView {
        WKWebView()
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {
        guard let url = url else { return }

        let request = URLRequest(url: url)
        uiView.load(request)
    }
}
