//
//  TitleSubtitleWithButtonCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 27/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class TitleSubtitleWithButtonCell: UITableViewCell {
    private enum Constants {
        static let confirmationDisplayTime: TimeInterval = 3
        static let alphaHidden: CGFloat = 0
        static let alphaVisible: CGFloat = 1
        static let copyToClipboardButtonAccessibilityID = "copyToClipboardButtonAccessibilityID"
        static let copyToClipboardTextAccessibilityID = "copyToClipboardTextAcc"
    }

    private var hideCopyConfirmationWorkItem: DispatchWorkItem?
    private var textCopier: TextCopying = PasteboardTextCopier()

    var copyConfirmationAlpha: CGFloat {
        copyToClipboardLabel.alpha
    }

    func configure(
        bookingReference: String?,
        textCopier: TextCopying = PasteboardTextCopier()
    ) {
        self.textCopier = textCopier
        contentLabel.text = bookingReference
    }

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.textColor = .TintD2
            titleLabel.text = PILocalizedString("bookingConfirmationReferenceLabelTitle")
            titleLabel.font = .Body_Semibold()
        }
    }
    @IBOutlet weak var contentLabel: UILabel! {
        didSet {
            contentLabel.textColor = .TintD1
            contentLabel.text = nil
            contentLabel.font = .Heading1_Bold()
        }
    }
    @IBOutlet weak var button: UIButton! {
        didSet {
            button.tintColor = .BasePurple
            button.configurationUpdateHandler = { button in
                var config = UIButton.Configuration.plain()
                config.title = PILocalizedString("bookingConfirmationResendInvoiceButtonTitle")
                config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                    var title = attribute
                    title.font = .Body_Semibold()
                    return title
                }
                button.configuration = config
            }
        }
    }

    @IBOutlet weak var copyButton: UIButton! {
        didSet {
            copyButton.accessibilityIdentifier = Constants.copyToClipboardButtonAccessibilityID
            copyButton.addTarget(self, action: #selector(didTapCopyToClipboard), for: .touchUpInside)
        }
    }

    @IBOutlet weak var copyToClipboardLabel: UILabel! {
        didSet {
            copyToClipboardLabel.accessibilityIdentifier = Constants.copyToClipboardTextAccessibilityID
            copyToClipboardLabel.font = .BodySmall()
            copyToClipboardLabel.textColor = .TintD1
            copyToClipboardLabel.text = PILocalizedString("copiedToClipboardConfirmation")
            copyToClipboardLabel.alpha = Constants.alphaHidden
        }
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        hideCopyConfirmationWorkItem?.cancel()
        hideCopyConfirmationWorkItem = nil
        copyToClipboardLabel.alpha = Constants.alphaHidden
    }

    @objc private func didTapCopyToClipboard() {
        guard let bookingReference = contentLabel.text, !bookingReference.isEmpty else { return }

        textCopier.copy(bookingReference)
        copyToClipboardLabel.alpha = Constants.alphaVisible
        scheduleCopyConfirmationHide()
    }

    // MARK: - Clipboard Confirmation Logic

    private func scheduleCopyConfirmationHide() {
        hideCopyConfirmationWorkItem?.cancel()

        let currentWorkItem = makeHideCopyConfirmationWorkItem()
        hideCopyConfirmationWorkItem = currentWorkItem

        let timeToDisplayText: DispatchTime = .now() + Constants.confirmationDisplayTime
        DispatchQueue.main.asyncAfter(
            deadline: timeToDisplayText,
            execute: currentWorkItem
        )
    }

    private func makeHideCopyConfirmationWorkItem() -> DispatchWorkItem {
        DispatchWorkItem { [weak self] in
            self?.copyToClipboardLabel.alpha = Constants.alphaHidden
        }
    }
}
