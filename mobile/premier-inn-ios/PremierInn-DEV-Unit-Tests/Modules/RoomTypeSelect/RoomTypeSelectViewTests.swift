//
//  RoomTypeSelectViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 25/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation
import XCTest

@testable import PremierInn

private class MockEventHandler: RoomTypeSelectViewEventHandler {

    var viewIsReadyDidCall = false
    var selectedRoomTypeDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func select(roomType: RoomTypeSelectOptionViewModel) {

        selectedRoomTypeDidCall = true
    }
}

class RoomTypeSelectViewTests: XCTestCase {

    fileprivate var mockEventHandler: MockEventHandler?

    fileprivate let table: UITableView = UITableView()

    var view: RoomTypeSelectViewController?

    override func setUp() {
        super.setUp()

        mockEventHandler = MockEventHandler()

        view = RoomTypeSelectViewController()

        table.registerCellNib(with: DynamicListCell.self)
        table.delegate = view
        table.dataSource = view

        view?.table = table
        view?.eventHandler = mockEventHandler
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewIsReady() {

        view?.viewDidLoad()
        view?.viewWillAppear(true)

        XCTAssert(mockEventHandler?.viewIsReadyDidCall == true)
    }

    func _testViewModel() {

        struct ViewModel: RoomTypeSelectViewModel {
            let roomTypeOptions: [RoomTypeSelectOptionViewModel]
        }

        struct OptionViewModel: RoomTypeSelectOptionViewModel {
            let name: String
            let description: String
            let iconName: String
            let enabled: Bool
            let selected: Bool
        }

        let viewModel = ViewModel(
            roomTypeOptions: [
                OptionViewModel(
                    name: "Double",
                    description: "Top tier accommodation",
                    iconName: "jewel",
                    enabled: true,
                    selected: true
                )
            ]
        )

        view?.update(with: viewModel)

        let cell = self.view?.table.cellForRow(at: IndexPath(row: 0, section: 0)) as? RoomTypeCell

        XCTAssertNotNil(cell)

        XCTAssert(cell?.typeTitle.text == "Double")
        XCTAssert(cell?.typeDescription.text == "Top tier accommodation")
    }
}
