//
//  VenueFullyBookedCellViewModel.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 03/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
@testable import PremierInn

struct VenueFullyBookedCellViewModelTests {

    @Test
    func givenProviderWhenAccessingNameThenReturnsProviderName() {
        // GIVEN
        let expectedName = "London Euston"
        let provider = MockVenueFullyBookedCellViewModelDataProvider(
            name: expectedName
        )
        let sut = VenueFullyBookedCellViewModel(provider: provider)

        // WHEN
        let result = sut.name

        // THEN
        #expect(result == expectedName)
    }

    @Test
    func givenProviderWithImagesWhenAccessingImageUrlThenReturnsFirstImage() {
        // GIVEN
        let provider = MockVenueFullyBookedCellViewModelDataProvider()
        let expectedUrl = provider.primaryImages.first
        let sut = VenueFullyBookedCellViewModel(provider: provider)

        // WHEN
        let result = sut.imageUrl

        // THEN
        #expect(result == expectedUrl)
    }

    @Test
    func givenProviderWithoutImagesWhenAccessingImageUrlThenReturnsNil() {
        // GIVEN
        let provider = MockVenueFullyBookedCellViewModelDataProvider(
            primaryImages: []
        )
        let sut = VenueFullyBookedCellViewModel(provider: provider)

        // WHEN
        let result = sut.imageUrl

        // THEN
        #expect(result == nil)
    }
}
