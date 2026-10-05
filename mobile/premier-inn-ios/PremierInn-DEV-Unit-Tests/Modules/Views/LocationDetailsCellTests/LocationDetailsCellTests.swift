//
//  LocationDetailsCellTests.swift
//  PremierInn
//
//  Created by Clint Mengolli on 27/04/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
import MapKit
@testable import PremierInn

struct MockLocationDetailsViewModel: LocationDetailsDisplayable {
    var title: String? = "Location"
    var address: String? = "123 Mock Address"
    var annotations: [MKAnnotation] = []
    var mapImage: UIImage?
}

final class LocationDetailsCellTests: XCTestCase {

    private var cell: LocationDetailsCell!
    private var textCopier: MockTextCopier!

    override func setUp() {
        super.setUp()
        cell = makeCell()
        textCopier = MockTextCopier()
    }

    override func tearDown() {
        textCopier = nil
        cell = nil
        super.tearDown()
    }

    func testCopyToClipboardCopiesAddress() {
        let viewModel = MockLocationDetailsViewModel(address: "123 Mock Address")

        cell.configure(with: viewModel, textCopier: textCopier)

        cell.didTapCopyToClipboard()

        XCTAssertEqual(textCopier.copiedText, "123 Mock Address")
    }

    func testCopyToClipboardShowsConfirmationLabel() {
        let viewModel = MockLocationDetailsViewModel(address: "123 Mock Address")

        cell.configure(with: viewModel, textCopier: textCopier)

        cell.didTapCopyToClipboard()

        XCTAssertEqual(cell.copyConfirmationAlpha, 1)
    }

    func testCopyToClipboardWithEmptyAddressDoesNothing() {
        let viewModel = MockLocationDetailsViewModel(address: "")

        cell.configure(with: viewModel, textCopier: textCopier)

        cell.didTapCopyToClipboard()

        XCTAssertNil(textCopier.copiedText)
        XCTAssertEqual(cell.copyConfirmationAlpha, 0)
    }

    func testPrepareForReuseResetsConfirmationLabel() {
        let viewModel = MockLocationDetailsViewModel(address: "123")

        cell.configure(with: viewModel, textCopier: textCopier)

        cell.didTapCopyToClipboard()
        XCTAssertEqual(cell.copyConfirmationAlpha, 1)

        cell.prepareForReuse()

        XCTAssertEqual(cell.copyConfirmationAlpha, 0)
    }
}

private extension LocationDetailsCellTests {
    private func makeCell() -> LocationDetailsCell {
        let bundle = Bundle(for: LocationDetailsCell.self)
        let nib = UINib(nibName: "LocationDetailsCell", bundle: bundle)

        let cell = nib.instantiate(withOwner: nil, options: nil)
            .compactMap { $0 as? LocationDetailsCell }
            .first!

        cell.awakeFromNib()
        return cell
    }
}
