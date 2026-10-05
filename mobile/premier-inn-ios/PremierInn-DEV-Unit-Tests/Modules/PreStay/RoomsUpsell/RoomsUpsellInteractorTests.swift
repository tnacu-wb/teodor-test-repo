//
//  RoomsUpsellInteractorTests.swift
//  PremierInnTests
//
//  Created by Raiu, George Marius (Cognizant) on 10.12.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Testing
@testable import PremierInn

class RoomsUpsellInteractorTests {
    struct MockRoomsUpsellInputParams: RoomsUpsellInput {
        var bookingReference: String? = ""
        
        var analyticsParams: PremierInn.PIDictionary = PIDictionary()
        
        var upselltem: CiolUpsellItemViewModelProtocol = MockCiolUpsellItem()
        var rooms: [UpsellRoom] = []
        var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol?
        var nights: Int = 0
    }
    var sut: RoomsUpsellInteractor!
    var mockViewModel: MockRoomsUpsellViewProtocol!

    @Test func testUpdatePrice() async throws {
        sut = RoomsUpsellInteractor(roomsUpsellInputParams: MockRoomsUpsellInputParams())
        mockViewModel = MockRoomsUpsellViewProtocol()
        sut.viewModel = mockViewModel
        sut.didUpdateUpsell(for: MockCiolUpsellItem(), action: .add)
        #expect(mockViewModel.didCallUpdateRooms)        
    }

}
