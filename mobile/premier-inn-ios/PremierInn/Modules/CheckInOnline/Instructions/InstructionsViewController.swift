//
//  RoomKeyInstructionsViewController.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 25.06.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit
import PassKit

class InstructionsViewController: UIViewController {
    @IBOutlet weak var logoImageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Heading2_ExtraBold()
            titleLabel.textColor = .BaseBlack
            titleLabel.accessibilityTraits.insert(.header)
        }
    }

    @IBOutlet weak var closeButton: UIButton!

    @IBAction func closeButtonDidTap(_ sender: Any) {
        eventHandler?.close()
    }

    @IBOutlet weak var loadingOverlay: UIView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var contentView: UIView! {
        didSet {
            contentView.layer.masksToBounds = true
            contentView.layer.cornerRadius = 10
            contentView.layer.maskedCorners = [.layerMaxXMinYCorner, .layerMinXMinYCorner]
        }
    }

    @IBOutlet weak var topDescriptionLabel: UILabel! {
        didSet {
            topDescriptionLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var bookingReferenceLabel: UILabel! {
        didSet {
            bookingReferenceLabel.font = .BodySmall()
            bookingReferenceLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var qrCodeImage: UIImageView!

    @IBAction func qrCodeImageTapped(_ sender: Any) {
        NotificationFeedbackManager.shared.provideLightTapFeedback()
        eventHandler?.showUsingYourDigitalKeyAnimation()
    }

    @IBOutlet weak var viewInWalletButton: RoundedCornersTintButton! {
        didSet {
            viewInWalletButton.contentHorizontalAlignment = .center
            viewInWalletButton.tintColor = .Tint4
            viewInWalletButton.backgroundColor = .Tint5
            viewInWalletButton.setTitleColor(.Tint4, for: .normal)
            viewInWalletButton.setAttributedStringForAppleWallet(font: .Button1())
            viewInWalletButton.isEnabled = true
            viewInWalletButton.titleLabel?.font = .Button1()
        }
    }
    @IBOutlet weak var addToWalletButton: PKAddPassButton!

    @IBAction func addToWalletButtonDidTap(_ sender: Any) {
        eventHandler?.addToWallet()
    }

    @IBAction func viewInWalletTapped(_ sender: Any) {
        eventHandler?.viewExistingWalletPass()
    }

    @IBOutlet weak var disclaimerLabel: UILabel! {
        didSet {
            disclaimerLabel.font = .BodySmall()
            disclaimerLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var bottomDescriptionLabel: UILabel! {
        didSet {
            bottomDescriptionLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var stackView: UIStackView!

    var eventHandler: InstructionsViewEventHandler?

    override func viewDidLoad() {
        super.viewDidLoad()

        stackView.setCustomSpacing(27, after: topDescriptionLabel)
        stackView.setCustomSpacing(24, after: qrCodeImage)
        stackView.setCustomSpacing(16, after: addToWalletButton)
        stackView.setCustomSpacing(32, after: disclaimerLabel)
        eventHandler?.viewIsReady()
    }
}

extension InstructionsViewController: InstructionsViewProtocol {
    func reloadData(with viewModel: InstructionsViewModel) {
        logoImageView.image = viewModel.titleLogo
        logoImageView.isHidden = viewModel.titleLogo == nil
        titleLabel.text = viewModel.title
        topDescriptionLabel.attributedText = viewModel.topDescription
        topDescriptionLabel.isHidden = viewModel.topDescription == nil
        bookingReferenceLabel.text = viewModel.formattedBookingReference
        bookingReferenceLabel.textAlignment = viewModel.type == .roomKeyWithQRCode ? .center : .left
        bookingReferenceLabel.isHidden = viewModel.formattedBookingReference == nil
        addToWalletButton.isHidden = viewModel.isAddToAppleWalletHidden
        viewInWalletButton.isHidden = viewModel.isViewInAppleWalletHidden
        qrCodeImage.image = viewModel.qrImage
        qrCodeImage.contentMode = viewModel.imageContentMode
        qrCodeImage.layer.magnificationFilter = .nearest
        qrCodeImage.isHidden = viewModel.qrImage == nil

        if viewModel.type == .usingDigitalKey {
            qrCodeImage.isUserInteractionEnabled = true
            qrCodeImage.layer.cornerRadius = 16
            qrCodeImage.clipsToBounds = true
        }

        disclaimerLabel.text = viewModel.disclaimer
        disclaimerLabel.isHidden = viewModel.disclaimer == nil
        bottomDescriptionLabel.attributedText = viewModel.bottomDescription
        bottomDescriptionLabel.isHidden = viewModel.bottomDescription == nil
    }

    func toggleLoadingIndicator(isLoading: Bool) {
        navigationController?.isModalInPresentation = isLoading
        isModalInPresentation = isLoading

        showLoader(isLoading)
    }

    func showFailedWalletFetch(with error: Error) {
        let alertController = UIAlertController(title: error.localizedDescription,
                                                message: nil,
                                                preferredStyle: .alert)

        let cancel = PILocalizedString("Cancel", comment: "Title for alert cancel button")
        let tryAgain = PILocalizedString("tryAgain", comment: "Title for alert try again button")

        let cancelAction = UIAlertAction(title: cancel,
                                                style: .cancel,
                                                handler: nil)
        let tryAgainAction = UIAlertAction(title: tryAgain,
                                                style: .default,
                                                handler: { [weak self] _ in
            self?.eventHandler?.addToWallet()
        })

        alertController.addAction(cancelAction)
        alertController.addAction(tryAgainAction)

        alertController.preferredAction = tryAgainAction

        present(alertController, animated: true, completion: nil)
    }
}
