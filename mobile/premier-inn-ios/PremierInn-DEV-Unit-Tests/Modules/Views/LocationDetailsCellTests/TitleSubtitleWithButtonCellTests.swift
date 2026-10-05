//
//  TitleSubtitleWithButtonCellTests.swift
//  PremierInn
//
//  Created by Valentin Stanciu (Cognizant) on 25/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class TitleSubtitleWithButtonCellTests: XCTestCase {

    private var sut: TitleSubtitleWithButtonCell!
    private var textCopier: MockTextCopier!

    override func setUp() {
        super.setUp()
        sut = makeCell()
        textCopier = MockTextCopier()
    }

    override func tearDown() {
        textCopier = nil
        sut = nil
        super.tearDown()
    }

    func testCopyButtonTapCopiesBookingReference() {
        sut.configure(bookingReference: "ABC123", textCopier: textCopier)

        sut.copyButton.sendActions(for: .touchUpInside)

        XCTAssertEqual(textCopier.copiedText, "ABC123")
    }

    func testCopyButtonTapShowsConfirmationLabel() {
        sut.configure(bookingReference: "ABC123", textCopier: textCopier)

        sut.copyButton.sendActions(for: .touchUpInside)

        XCTAssertEqual(sut.copyConfirmationAlpha, 1)
    }

    func testCopyButtonTapWithEmptyReferenceDoesNotShowConfirmationLabel() {
        sut.configure(bookingReference: "", textCopier: textCopier)

        sut.copyButton.sendActions(for: .touchUpInside)

        XCTAssertNil(textCopier.copiedText)
        XCTAssertEqual(sut.copyConfirmationAlpha, 0)
    }

    func testPrepareForReuseResetsConfirmationLabel() {
        sut.configure(bookingReference: "ABC123", textCopier: textCopier)

        sut.copyButton.sendActions(for: .touchUpInside)
        XCTAssertEqual(sut.copyConfirmationAlpha, 1)

        sut.prepareForReuse()

        XCTAssertEqual(sut.copyConfirmationAlpha, 0)
    }

    func testCopyButtonTapHidesConfirmationLabelAfterDelay() {
        sut.configure(bookingReference: "ABC123", textCopier: textCopier)

        sut.copyButton.sendActions(for: .touchUpInside)
        XCTAssertEqual(sut.copyConfirmationAlpha, 1)

        let hideExpectation = predicateExpectation(
            description: "Copy confirmation should hide after delay",
            self.sut.copyConfirmationAlpha == 0
        )
        wait(for: [hideExpectation], timeout: 4)
        XCTAssertEqual(sut.copyConfirmationAlpha, 0)
    }
}

private extension TitleSubtitleWithButtonCellTests {
    func makeCell() -> TitleSubtitleWithButtonCell {
        let bundle = Bundle(for: TitleSubtitleWithButtonCell.self)
        let nib = UINib(nibName: "TitleSubtitleWithButtonCell", bundle: bundle)

        let cell = nib.instantiate(withOwner: nil, options: nil)
            .compactMap { $0 as? TitleSubtitleWithButtonCell }
            .first!

        cell.awakeFromNib()
        return cell
    }
}
