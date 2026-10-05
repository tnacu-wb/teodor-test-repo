//
//  LocationDetailsCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 22/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Formeka
import UIKit
import MapKit

final class LocationDetailsCell: SimpleSeparatorsCell {
    private enum Constants {
        static let titleLabelAccessibilityID = "titleLabelAcc"
        static let addressLabelAccessibilityID = "addressLabelAcc"
        static let directionsButtonAccessibilityID = "directionsButtonAcc"
        static let copyToClipboardTextAccessibilityID = "copyToClipboardTextAcc"

        static let copyToClipboardImageName = "copyToClipboard"
        static let confirmationDisplayTime: TimeInterval = 3
        static let alphaHidden: CGFloat = 0
        static let alphaVisible: CGFloat = 1
    }

    private var hideCopyConfirmationWorkItem: DispatchWorkItem?

    private var textCopier: TextCopying = PasteboardTextCopier()

    var didTapDirections: ((UIView) -> Void)?

    var copyConfirmationAlpha: CGFloat {
        copyToClipboardLabel.alpha
    }

    func configure(
        with viewModel: LocationDetailsDisplayable,
        textCopier: TextCopying = PasteboardTextCopier()
    ) {
        self.textCopier = textCopier
        titleLabel.text = viewModel.title
        addressLabel.text = viewModel.address
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        let copyToClipboardGesture = UITapGestureRecognizer(target: self, action: #selector(didTapCopyToClipboard))
        let tapMapGesture = UITapGestureRecognizer(target: self, action: #selector(didTapMap))

        addressContainerView.isUserInteractionEnabled = true
        mapImageView.isUserInteractionEnabled = true

        addressContainerView.addGestureRecognizer(copyToClipboardGesture)
        mapImageView.addGestureRecognizer(tapMapGesture)

        directionsButton.addTarget(self, action: #selector(didTapDirectionsButton(_:)), for: .touchUpInside)
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        hideCopyConfirmationWorkItem?.cancel()
        hideCopyConfirmationWorkItem = nil
        didTapDirections = nil
        mapImageView.image = nil
        copyToClipboardLabel.alpha = Constants.alphaHidden
        mapImageView.alpha = Constants.alphaVisible
    }

    // Exposed for test coverage
    @objc func didTapCopyToClipboard() {
        guard let address = addressLabel.text, !address.isEmpty else {
            return
        }

        textCopier.copy(address)
        copyToClipboardLabel.alpha = Constants.alphaVisible
        scheduleCopyConfirmationHide()
    }

    // MARK: - IBOutlets

    @IBOutlet private weak var addressContainerView: UIView!

    @IBOutlet private weak var titleLabel: UILabel! {
        didSet {
            titleLabel.accessibilityIdentifier = Constants.titleLabelAccessibilityID
            titleLabel.accessibilityTraits.insert(.header)
            titleLabel.font = .Heading2_Bold()
            titleLabel.textColor = .BasePurple
            titleLabel.text = PILocalizedString("mapScreenTitlePlan")
        }
    }

    @IBOutlet private weak var addressLabel: UILabel! {
        didSet {
            addressLabel.accessibilityIdentifier = Constants.addressLabelAccessibilityID
            addressLabel.font = .Body()
            addressLabel.textColor = .TintD1
        }
    }

    @IBOutlet private weak var directionsButton: RoundedCornersTintButton! {
        didSet {
            directionsButton.accessibilityIdentifier = Constants.directionsButtonAccessibilityID
            let title = PILocalizedString("bookingConfDirectionsButtonTitle")
            directionsButton.setTitle(title, for: .normal)
            directionsButton.setTitleColor(.BasePurple, for: .normal)
            directionsButton.titleLabel?.font = .Action1()
        }
    }

    @IBOutlet weak var mapImageView: UIImageView!

    @IBOutlet private weak var copyToClipboardView: UIImageView! {
        didSet {
            copyToClipboardView.image = UIImage(named: Constants.copyToClipboardImageName)
        }
    }

    @IBOutlet private weak var copyToClipboardLabel: UILabel! {
        didSet {
            copyToClipboardLabel.accessibilityIdentifier = Constants.copyToClipboardTextAccessibilityID
            copyToClipboardLabel.font = .BodySmall()
            copyToClipboardLabel.textColor = .TintD1
            copyToClipboardLabel.text = PILocalizedString("copiedToClipboardConfirmation")
            copyToClipboardLabel.alpha = Constants.alphaHidden
        }
    }
}

// MARK: - Internal logic

private extension LocationDetailsCell {
    // MARK: - Clipboard Confirmation Logic

    func scheduleCopyConfirmationHide() {
        hideCopyConfirmationWorkItem?.cancel()

        let currentWorkItem = makeHideCopyConfirmationWorkItem()
        hideCopyConfirmationWorkItem = currentWorkItem

        let timeToDisplayText: DispatchTime = .now() + Constants.confirmationDisplayTime
        DispatchQueue.main.asyncAfter(
            deadline: timeToDisplayText,
            execute: currentWorkItem
        )
    }

    func makeHideCopyConfirmationWorkItem() -> DispatchWorkItem {
        DispatchWorkItem { [weak self] in
            self?.copyToClipboardLabel.alpha = Constants.alphaHidden
        }
    }
}

// MARK: - Behaviour / Actions

private extension LocationDetailsCell {
    @objc func didTapMap(_ gesture: UITapGestureRecognizer) {
        guard let view = gesture.view else { return }
        didTapDirections?(view)
    }

    @objc func didTapDirectionsButton(_ sender: UIView) {
        didTapDirections?(sender)
    }
}
