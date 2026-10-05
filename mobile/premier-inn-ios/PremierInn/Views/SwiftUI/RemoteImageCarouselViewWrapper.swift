//
//  RemoteImageCarouselViewWrapper.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 24/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

struct RemoteImageCarouselViewWrapper: UIViewRepresentable {
    private let urls: [URL]
    private let urlImageSize: URLImageSize
    private let onTap: () -> Void

    init(
        urls: [URL],
        urlImageSize: URLImageSize = .medium,
        onTap: @escaping () -> Void
    ) {
        self.urls = urls
        self.urlImageSize = urlImageSize
        self.onTap = onTap
    }

    func makeUIView(context: Context) -> RemoteImageCarouselView {
        let view = RemoteImageCarouselView()
        view.onTap = onTap
        view.urlImageSize = urlImageSize
        view.setImages(urls)
        return view
    }

    func updateUIView(_ uiView: RemoteImageCarouselView, context: Context) {
        uiView.onTap = onTap
        uiView.setImages(urls)
    }
}
