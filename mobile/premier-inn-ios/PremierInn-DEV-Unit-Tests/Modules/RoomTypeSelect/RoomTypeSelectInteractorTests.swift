//
//  RoomTypeSelectInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 25/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import SimpleNetwork

@testable import PremierInn

class RoomTypeSelectInteractorTests: XCTestCase {

    var interactor: RoomTypeSelectInteractor?

    private let allRooms = [RoomType.single, .double, .twin, .family, .accessible]
    private let someRooms = [RoomType.double, .twin, .accessible]

    override func setUp() {
        super.setUp()

        interactor = RoomTypeSelectInteractor(with: allRooms, selectedRoomType: RoomType.double)
    }

    override func tearDown() {
        super.tearDown()
    }

    func testAllAvailable() {

        guard let selectedRoomType = interactor?.selectedRoomType as? RoomType else { return }
        XCTAssert(selectedRoomType == RoomType.double)

        let twinRoom = OptionViewModel(name: "Twin room", description: "", iconName: "", enabled: true, selected: true)
        interactor?.select(roomType: twinRoom)

        guard let selectedRoomType2 = interactor?.selectedRoomType as? RoomType else { return }
        XCTAssert(selectedRoomType2 == RoomType.twin)
    }

    func testSomeAvailable() {

        interactor = RoomTypeSelectInteractor(with: someRooms, selectedRoomType: RoomType.double)

        guard let selectedRoomType = interactor?.selectedRoomType as? RoomType else { return }
        XCTAssert(selectedRoomType == RoomType.double)

        let accessibleRoom = OptionViewModel(name: "Accessible room", description: "", iconName: "", enabled: true, selected: true)
        interactor?.select(roomType: accessibleRoom)

        guard let selectedRoomType2 = interactor?.selectedRoomType as? RoomType else { return }
        XCTAssert(selectedRoomType2 == RoomType.accessible)

        let twinRoom = OptionViewModel(name: "Twin room", description: "", iconName: "", enabled: true, selected: true)
        interactor?.select(roomType: twinRoom)

        guard let selectedRoomType3 = interactor?.selectedRoomType as? RoomType else { return }
        XCTAssert(selectedRoomType3 == RoomType.twin)
    }

    func testViewModel() {

        interactor = RoomTypeSelectInteractor(with: someRooms, selectedRoomType: RoomType.twin)

        if let viewModel = interactor?.viewModel {

            XCTAssert(viewModel.roomTypeOptions.count == 3)

            XCTAssert(viewModel.roomTypeOptions[0].selected == false)
            XCTAssert(viewModel.roomTypeOptions[1].selected == true)

            XCTAssert(viewModel.roomTypeOptions[1].enabled == true)
        }
    }
}
