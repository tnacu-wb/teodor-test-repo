//
//  RemoteImageViewWrapper.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 02/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI
import UIKit

struct RemoteImageView: UIViewRepresentable {
    private let url: URL?
    private let urlImageSize: URLImageSize

    init(
        url: URL?,
        urlImageSize: URLImageSize = .medium
    ) {
        self.url = url
        self.urlImageSize = urlImageSize
    }

    func makeUIView(context: Context) -> RemoteImageUIView {
        RemoteImageUIView()
    }

    func updateUIView(_ uiView: RemoteImageUIView, context: Context) {
        uiView.configure(
            with: url,
            urlImageSize: urlImageSize
        )
    }

    static func dismantleUIView(_ uiView: RemoteImageUIView, coordinator: ()) {
        uiView.prepareForReuse()
    }
}
