//
//  CiolUpsellDetailsInteractorTests.swift
//  PremierInnTests
//
//  Created by Oltean Vasile Bogdan on 24.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
@testable import SimpleNetwork

class CiolUpsellDetailsInteractorTests: XCTestCase {
    struct MockCiolUpsellItemViewModel: CiolUpsellItemViewModelProtocol {
        var legendTitle: String = ""
        var imageURL: URL? = nil
        var title: String = ""
        var costSummary: String = ""
        var itemDescription: String? = ""
        var isFoodUpsell: Bool = false
        var isBooked: Bool = false
        var menuUrls: [RestaurantMenuItem] = [RestaurantMenuItem(name: "Test", description: nil, disclaimer: nil, path: "")]
        var allergensUrls: [AllergenInformation] = [("Test", "")]
        var subtitle: String = ""
        var hasChildren: Bool = false
        var isMultiRoom: Bool = false
        var subitems: [PremierInn.CiolUpsellSubitemViewModel] = []
        var room: (any PremierInn.UpsellRoom)?
        var id: String = ""
        var enabled: Bool = false
        var bookingReference: String?
        var selected: Bool = false
        var isPrebooked: Bool = false
        var isWifi: Bool = false
        var nights: Int = 0
    }

    var interactor: CiolUpsellDetailsInteractor!

    override func setUp() {
        super.setUp()
        
        interactor = CiolUpsellDetailsInteractor(inputParams: CiolUpsellDetailsInputParams(upsell: MockCiolUpsellItemViewModel(), nights: 0, addedFoodItems: 0, addedKidsItems: 0, prebookedItems: nil, bookingReference: "", analyticsParams: PIDictionary()))
    }

    override func tearDown() {
        interactor = nil
        super.tearDown()
    }
}
