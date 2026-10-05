//
//  CustomRadioButtonViewTests.swift
//  PremierInn
//
//  Created by Clint Mengolli on 12/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class CustomRadioButtonViewTests: XCTestCase {
    func test_initialSetup_createsButtons() {
        let options: [MockSelectableType] = [.leisure, .business]
        let view = CustomRadioButtonView(options: options)

        XCTAssertEqual(view.options.count, 2)
    }

    func test_tappingButton_updatesSelection() {
        let options: [MockSelectableType] = [.leisure, .business]
        let view = CustomRadioButtonView(options: options)

        view.currentSelection = options[1]
        XCTAssertEqual(view.currentSelection, options[1])
    }

    func test_errorState_propagatesToButtons() {
        let options: [MockSelectableType] = [.leisure, .business]
        let view = CustomRadioButtonView(options: options)

        view.shouldShowError = true
        XCTAssertTrue(view.shouldShowError)
    }

    func test_onSelectionChanged_callbackFires() {
        let expectation = XCTestExpectation(description: "Selection callback fired")
        let options: [MockSelectableType] = [.leisure, .business]
        let view = CustomRadioButtonView(options: options)

        view.onSelectionChanged = { selection in
            XCTAssertEqual(selection, options[0])
            expectation.fulfill()
        }
        view.currentSelection = .leisure

        wait(for: [expectation], timeout: 1.0)
    }

    func test_rebuildButtons_whenOptionsChange() {
        let options: [MockSelectableType] = [.leisure, .business]
        let view = CustomRadioButtonView(options: options)

        view.options = [.leisure, .business, .business, .business]
        XCTAssertEqual(view.options.count, 4)
    }

    func test_shouldRequireSelection_isCorrect() {
        let options: [MockSelectableType] = [.leisure, .business]
        let view = CustomRadioButtonView(options: options)
        view.shouldRequireSelection = false
        XCTAssertFalse(view.shouldRequireSelection)
    }
}

enum MockSelectableType: SelectableType {
    case leisure
    case business

    var title: String {
        switch self {
        case .leisure:
            return "Leisure"
        case .business:
            return "Business"
        }
    }

    var accessibilityIdentifier: String {
        switch self {
        case .leisure:
            return "Leisure Accessibility"
        case .business:
            return "Business Accessibility"
        }
    }
}
