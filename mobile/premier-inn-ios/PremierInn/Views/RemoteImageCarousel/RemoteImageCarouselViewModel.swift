//
//  RemoteImageCarouselViewModel.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 24/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

final class RemoteImageCarouselViewModel {
    private let maxPageControlDots: Int

    private(set) var urls: [URL] = []
    private(set) var currentIndex = 0

    init(maxPageControlDots: Int = 4) {
        self.maxPageControlDots = maxPageControlDots
    }

    var pageControlNumberOfPages: Int {
        min(urls.count, maxPageControlDots)
    }

    var isPageControlHidden: Bool {
        urls.count <= 1
    }

    /// Returns the active page control dot for the currently visible image.
    ///
    /// When the number of images is greater than the number of available dots,
    /// multiple images are mapped to the same dot.
    ///
    /// Example:
    /// - Example (10 images, 4 dots):
    /// - Images 0-1 → Dot 0
    /// - Images 2-4 → Dot 1
    /// - Images 5-7 → Dot 2
    /// - Images 8-9 → Dot 3
    ///
    /// If the number of images is less than or equal to the number of dots,
    /// the current image index is used directly.
    var activePageControlIndex: Int {
        let totalImages = urls.count
        let totalDots = min(totalImages, maxPageControlDots)

        guard totalImages > totalDots else {
            return currentIndex
        }

        let lastImageIndex = totalImages - 1
        let lastDotIndex = totalDots - 1

        let progress = Double(currentIndex) / Double(lastImageIndex)
        return Int(round(progress * Double(lastDotIndex)))
    }

    func setImages(_ urls: [URL]) {
        self.urls = urls
        currentIndex = 0
    }

    /// Updates the currently visible image index based on the collection view's scroll position.
    ///
    /// The page width is the width of the collection view, so this calculation
    /// automatically works across different device sizes.
    ///
    /// Example:
    /// - iPhone: width = 320pt, offset = 640pt → image index = 2
    /// - iPad: width = 834pt, offset = 1668pt → image index = 2
    ///
    /// The calculated index is clamped to ensure it remains within the valid range
    /// of available images.
    func updateCurrentIndex(contentOffsetX: CGFloat, pageWidth: CGFloat) {
        guard pageWidth > 0, !urls.isEmpty else {
            return
        }

        let visiblePage = contentOffsetX / pageWidth
        let newIndex = Int(round(visiblePage))
        let maxIndex = urls.count - 1

        currentIndex = newIndex.clamped(to: 0...maxIndex)
    }
}
