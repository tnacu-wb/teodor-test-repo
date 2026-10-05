//
//  RoomTypeSelectPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 25/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import XCTest

@testable import PremierInn

private class MockView: RoomTypeSelectViewProtocol {

    var updateDidCall = false

    func update(with roomTypeSelectViewModel: RoomTypeSelectViewModel) {

        updateDidCall = true
    }
}

private class MockInteractor: RoomTypeSelectInteractorProtocol {

    var getViewModelDidCall = false
    var getSelectedRoomDidCall = false
    var updateSelectedRoomDidCall = false

    private struct ViewModel: RoomTypeSelectViewModel {
        let roomTypeOptions: [RoomTypeSelectOptionViewModel]
    }

    private struct OptionViewModel: RoomTypeSelectOptionViewModel {
        let name: String
        let description: String
        let iconName: String
        let enabled: Bool
        let selected: Bool
    }

    var viewModel: RoomTypeSelectViewModel {

        getViewModelDidCall = true

        return ViewModel(
            roomTypeOptions: [
                OptionViewModel(name: "", description: "", iconName: "", enabled: true, selected: true)
            ]
        )
    }

    var selectedRoomType: SelectableRoomType? {

        getSelectedRoomDidCall = true

        return RoomType.double
    }

    func select(roomType: RoomTypeSelectOptionViewModel) {

        updateSelectedRoomDidCall = true
    }
}

private class MockRouter: RoomTypeSelectRouterProtocol {

    var selectedRoomDidCall = false

    func selected(roomType: SelectableRoomType) {

        selectedRoomDidCall = true
    }
}

class RoomTypeSelectPresenterTests: XCTestCase {

    fileprivate var mockView: MockView?
    fileprivate var mockInteractor: MockInteractor?
    fileprivate var mockRouter: MockRouter?
    
    var presenter: RoomTypeSelectPresenter?

    override func setUp() {
        super.setUp()

        mockView = MockView()
        mockInteractor = MockInteractor()
        mockRouter = MockRouter()

        presenter = RoomTypeSelectPresenter()
        presenter?.view = mockView
        presenter?.interactor = mockInteractor
        presenter?.router = mockRouter
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewReady() {

        presenter?.viewIsReady()

        XCTAssert(mockInteractor?.getViewModelDidCall == true)
        XCTAssert(mockView?.updateDidCall == true)
    }

    func testSelectedRoomType() {

        let roomTypeModel = OptionViewModel(name: "Double room", description: "", iconName: "", enabled: true, selected: true)
        presenter?.select(roomType: roomTypeModel)

        XCTAssert(mockInteractor?.updateSelectedRoomDidCall == true)
        XCTAssert(mockRouter?.selectedRoomDidCall == true)
    }
}
