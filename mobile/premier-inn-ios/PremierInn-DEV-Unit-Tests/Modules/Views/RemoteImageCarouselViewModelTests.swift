//
//  RemoteImageCarouselViewModelTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 25/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
import Foundation
import CoreGraphics

@testable import PremierInn

@Suite("RemoteImageCarouselViewModel")
struct RemoteImageCarouselViewModelTests {

    @Test("setImages stores URLs and resets current index")
    func setImagesStoresURLsAndResetsCurrentIndex() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel()
        let urls = makeURLs(count: 3)

        sut.setImages(makeURLs(count: 2))
        sut.updateCurrentIndex(contentOffsetX: 300, pageWidth: 300)

        // WHEN
        sut.setImages(urls)

        // THEN
        #expect(sut.urls == urls)
        #expect(sut.currentIndex == 0)
    }

    @Test("page control number of pages is capped by max dots")
    func pageControlNumberOfPagesIsCappedByMaxDots() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel(maxPageControlDots: 4)

        // WHEN
        sut.setImages(makeURLs(count: 10))

        // THEN
        #expect(sut.pageControlNumberOfPages == 4)
    }

    @Test("page control number of pages matches image count when below max dots")
    func pageControlNumberOfPagesMatchesImageCountWhenBelowMaxDots() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel(maxPageControlDots: 4)

        // WHEN
        sut.setImages(makeURLs(count: 3))

        // THEN
        #expect(sut.pageControlNumberOfPages == 3)
    }

    @Test("page control is hidden when there is one or no image")
    func pageControlIsHiddenWhenThereIsOneOrNoImage() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel()

        // WHEN / THEN
        sut.setImages([])
        #expect(sut.isPageControlHidden)

        sut.setImages(makeURLs(count: 1))
        #expect(sut.isPageControlHidden)
    }

    @Test("page control is visible when there are multiple images")
    func pageControlIsVisibleWhenThereAreMultipleImages() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel()

        // WHEN
        sut.setImages(makeURLs(count: 2))

        // THEN
        #expect(!sut.isPageControlHidden)
    }

    @Test("current index updates from scroll offset")
    func currentIndexUpdatesFromScrollOffset() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel()
        sut.setImages(makeURLs(count: 5))

        // WHEN
        sut.updateCurrentIndex(contentOffsetX: 640, pageWidth: 320)

        // THEN
        #expect(sut.currentIndex == 2)
    }

    @Test("current index clamps to first image")
    func currentIndexClampsToFirstImage() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel()
        sut.setImages(makeURLs(count: 5))

        // WHEN
        sut.updateCurrentIndex(contentOffsetX: -100, pageWidth: 320)

        // THEN
        #expect(sut.currentIndex == 0)
    }

    @Test("current index clamps to last image")
    func currentIndexClampsToLastImage() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel()
        sut.setImages(makeURLs(count: 5))

        // WHEN
        sut.updateCurrentIndex(contentOffsetX: 9999, pageWidth: 320)

        // THEN
        #expect(sut.currentIndex == 4)
    }

    @Test("current index does not update when page width is invalid")
    func currentIndexDoesNotUpdateWhenPageWidthIsInvalid() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel()
        sut.setImages(makeURLs(count: 5))
        sut.updateCurrentIndex(contentOffsetX: 320, pageWidth: 320)

        // WHEN
        sut.updateCurrentIndex(contentOffsetX: 640, pageWidth: 0)

        // THEN
        #expect(sut.currentIndex == 1)
    }

    @Test("active page control index uses current index when image count is within max dots")
    func activePageControlIndexUsesCurrentIndexWhenImageCountIsWithinMaxDots() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel(maxPageControlDots: 4)
        sut.setImages(makeURLs(count: 4))

        // WHEN
        sut.updateCurrentIndex(contentOffsetX: 600, pageWidth: 300)

        // THEN
        #expect(sut.currentIndex == 2)
        #expect(sut.activePageControlIndex == 2)
    }

    @Test("active page control index maps many images into limited dots")
    func activePageControlIndexMapsManyImagesIntoLimitedDots() {
        // GIVEN
        let sut = RemoteImageCarouselViewModel(maxPageControlDots: 4)
        sut.setImages(makeURLs(count: 10))

        // WHEN / THEN
        sut.updateCurrentIndex(contentOffsetX: 0, pageWidth: 100)
        #expect(sut.activePageControlIndex == 0)

        sut.updateCurrentIndex(contentOffsetX: 300, pageWidth: 100)
        #expect(sut.activePageControlIndex == 1)

        sut.updateCurrentIndex(contentOffsetX: 600, pageWidth: 100)
        #expect(sut.activePageControlIndex == 2)

        sut.updateCurrentIndex(contentOffsetX: 900, pageWidth: 100)
        #expect(sut.activePageControlIndex == 3)
    }
}

private extension RemoteImageCarouselViewModelTests {
    func makeURLs(count: Int) -> [URL] {
        (0..<count).compactMap {
            URL(string: "https://example.com/image-\($0).jpg")
        }
    }
}
