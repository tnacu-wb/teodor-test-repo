//
//  InlineMessageViewTests.swift
//  PremierInn
//
//  Created by Clint Mengolli on 14/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class InlineMessageViewTests: XCTestCase {

    func test_initialisation_setsUpSubviews() {
        let view = createView()

        XCTAssertTrue(view.subviews.contains(where: { $0 is UIImageView }))
        XCTAssertTrue(view.subviews.contains(where: { $0 is UILabel }))
    }

    func test_setText_updatesLabelText() {
        let view = createView()
        view.setText("Hello world")

        XCTAssertEqual(view.subviews.compactMap { $0 as? UILabel }.first?.text, "Hello world")
    }

    func test_view_usesCorrectStylingForError() {
        let styling = InlineMessageStylingPreset.error.config
        let view = createView()
        view.styling = styling

        XCTAssertEqual(view.styling.textColor.cgColor, UIColor.BaseBlack.cgColor)
        XCTAssertEqual(view.styling.textFont, .BodySmall())
        XCTAssertEqual(view.styling.borderColor, .clear)
        XCTAssertEqual(view.styling.borderWidth, 0)
        XCTAssertEqual(view.styling.cornerRadius, 8)
        XCTAssertEqual(view.styling.backgroundColor.cgColor, UIColor.errorBackground.cgColor)
        XCTAssertEqual(view.styling.iconSize, 16)
        XCTAssertNil(view.styling.iconTint)
    }
}

private extension InlineMessageViewTests {
    func createView(
        text: String = "test view",
        style: InlineMessageStylingPreset = .error,
        tailHost: TailHost = .none,
        tailDimensions: CGSize = .init(width: 10, height: 10)
    ) -> InlineMessageView {
        InlineMessageView(
            text: text,
            style: style,
            arrowPlacement: tailHost,
            tailDimensions: tailDimensions
        )
    }
}
