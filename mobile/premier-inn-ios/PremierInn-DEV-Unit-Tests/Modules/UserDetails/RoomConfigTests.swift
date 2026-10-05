//
//  RoomConfigTests.swift
//  PremierInn
//
//  Created by Clint Mengolli on 21/04/2026.
//

import XCTest
@testable import PremierInn
import SimpleNetwork

final class RoomConfigTests: XCTestCase {

    func testInitStoresPassedValues() {
        let user = try! User(
            title: "Mr",
            firstName: "Test",
            lastName: "Tester"
        )

        let sut = RoomConfig(
            user: user,
            roomIndex: 1,
            totalRooms: 3,
            isBookerStaying: false,
            shouldShowRoomInfo: true
        )

        XCTAssertTrue(sut.user === user)
        XCTAssertEqual(sut.roomIndex, 1)
        XCTAssertEqual(sut.isBookerStaying, false)
        XCTAssertEqual(sut.shouldShowRoomInfo, true)
    }

    func testRoomTitleForFirstRoomIsRoomOne() {
        let sut = makeSUT(roomIndex: 0, totalRooms: 3)

        XCTAssertEqual(
            sut.roomTitle,
            PILocalizedString("genericRoomTitle") + " 1"
        )
    }

    func testRoomTitleForSecondRoomIsRoomTwo() {
        let sut = makeSUT(roomIndex: 1, totalRooms: 3)

        XCTAssertEqual(
            sut.roomTitle,
            PILocalizedString("genericRoomTitle") + " 2"
        )
    }

    func testFooterHeightForNonLastRoomIsStandardHeight() {
        let sut = makeSUT(roomIndex: 0, totalRooms: 3)

        XCTAssertEqual(sut.footerHeight, 10)
    }

    func testFooterHeightForLastRoomIsLeastNormalMagnitude() {
        let sut = makeSUT(roomIndex: 2, totalRooms: 3)

        XCTAssertEqual(sut.footerHeight, CGFloat.leastNormalMagnitude)
    }

    func testFooterHeightForSingleRoomIsLeastNormalMagnitude() {
        let sut = makeSUT(roomIndex: 0, totalRooms: 1)

        XCTAssertEqual(sut.footerHeight, CGFloat.leastNormalMagnitude)
    }

    func testShouldSummariseIsTrueWhenBookerIsStayingAndFirstRoom() {
        let sut = makeSUT(roomIndex: 0, totalRooms: 2, isBookerStaying: true)

        XCTAssertTrue(sut.shouldSummarise)
    }

    func testShouldSummariseIsFalseWhenBookerIsNotStayingAndFirstRoom() {
        let sut = makeSUT(roomIndex: 0, totalRooms: 2, isBookerStaying: false)

        XCTAssertFalse(sut.shouldSummarise)
    }

    func testShouldSummariseIsFalseWhenBookerIsStayingAndNotFirstRoom() {
        let sut = makeSUT(roomIndex: 1, totalRooms: 2, isBookerStaying: true)

        XCTAssertFalse(sut.shouldSummarise)
    }

    func testIsEmailRequiredIsTrueWhenBookerIsStayingAndFirstRoom() {
        let sut = makeSUT(roomIndex: 0, totalRooms: 2, isBookerStaying: true)

        XCTAssertTrue(sut.isEmailRequired)
    }

    func testIsEmailRequiredIsFalseWhenBookerIsNotStayingAndFirstRoom() {
        let sut = makeSUT(roomIndex: 0, totalRooms: 2, isBookerStaying: false)

        XCTAssertFalse(sut.isEmailRequired)
    }

    func testIsEmailRequiredIsFalseWhenBookerIsStayingAndNotFirstRoom() {
        let sut = makeSUT(roomIndex: 1, totalRooms: 2, isBookerStaying: true)

        XCTAssertFalse(sut.isEmailRequired)
    }
}

// MARK: - Helpers

private extension RoomConfigTests {
    func makeSUT(
        user: User? = nil,
        roomIndex: Int = 0,
        totalRooms: Int = 1,
        isBookerStaying: Bool = true,
        shouldShowRoomInfo: Bool = true
    ) -> RoomConfig {
        RoomConfig(
            user: user,
            roomIndex: roomIndex,
            totalRooms: totalRooms,
            isBookerStaying: isBookerStaying,
            shouldShowRoomInfo: shouldShowRoomInfo
        )
    }
}
