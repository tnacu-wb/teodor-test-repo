//
//  CiolInformationViewController.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 10.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

enum CiolInformationType {
    case checkIn
    case checkOut
    case general
    case paymentConfirmation
    case cityTaxDisclaimer
}

protocol CiolInformationDelegate: AnyObject {
    func ctaAction(ciolInformationType: CiolInformationType)
}

struct CiolBottomSheetAnalyticsInfo {
    /// ScreenName of the view that is underneath when CiolInformation is displayed on screen.
    let screenNameForViewUnderneath: String?

    /// CiolInformationViewController is used in multiple screens.
    /// But we want to track this view only when presented on top of CIOL screens.
    var shouldTrackScreenState: Bool {
        screenNameForViewUnderneath != nil
    }
}

class CiolInformationViewController: BaseViewController {
    @IBOutlet weak var imageView: UIImageView!

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .BodySmall_Semibold()
            titleLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var subtitleLabel: UILabel! {
        didSet {
            subtitleLabel.font = .Subtext()
            subtitleLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var closeButton: UIButton!

    @IBOutlet weak var descriptionTextView: UITextView! {
        didSet {
            descriptionTextView.font = .BodySmall()
            descriptionTextView.linkTextAttributes = [.foregroundColor: UIColor.Tint1]
            descriptionTextView.dataDetectorTypes = .link
        }
    }

    @IBOutlet weak var actionButton: UIButton! {
        didSet {
            actionButton.titleLabel?.font = .Button1()
            actionButton.layer.cornerRadius = 4
            actionButton.clipsToBounds = true
        }
    }

    @IBAction func closeButtonDidTap(_ sender: Any) {
        eventHandler?.close()
    }

    @IBAction func actionButtonDidTap(_ sender: Any) {
        if trackScreen {
            eventHandler?.logActionAnalytics()
        }
        eventHandler?.close()
        delegate?.ctaAction(ciolInformationType: ciolInformationType)
    }

    @IBOutlet weak var contentView: UIView! {
        didSet {
            contentView.layer.masksToBounds = true
            contentView.layer.cornerRadius = 10
            contentView.layer.maskedCorners = [.layerMaxXMinYCorner, .layerMinXMinYCorner]
        }
    }
    weak var delegate: CiolInformationDelegate?

    override var screenName: String { ciolBottomSheetAnalyticsInfo.screenNameForViewUnderneath ?? "" }
    override var screenType: String { PIAnalytics.StateTypes.ciolFlow }
    override var trackScreen: Bool { ciolBottomSheetAnalyticsInfo.shouldTrackScreenState }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var eventHandler: CiolInformationViewEventHandler?

    var ciolInformationType: CiolInformationType
    var ciolBottomSheetAnalyticsInfo: CiolBottomSheetAnalyticsInfo
    var customAnalyticsParameters: PIDictionary?

    init(ciolInformationType: CiolInformationType = .general,
         ciolBottomSheetAnalyticsInfo: CiolBottomSheetAnalyticsInfo) {
        self.ciolInformationType = ciolInformationType
        self.ciolBottomSheetAnalyticsInfo = ciolBottomSheetAnalyticsInfo
        super.init(nibName: String(describing: CiolInformationViewController.self), bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        eventHandler?.viewIsReady()
    }

    private func configureImage(image: CiolInformationImage?) {
        guard let image else { return }
        switch image.type {
        case .named(let image):
            if let image {
                imageView.image = image
            }
        case .url(let url):
            if let url {
                imageView.contentMode = .scaleAspectFill
                imageView.layer.cornerRadius = 8
                imageView.setImage(with: url)
            }
        }
    }

    private func configureDecription(text: CiolInformationText) {
        switch text.type {
        case .string(let description):
            descriptionTextView.text = description
        case .html(let description):
            descriptionTextView.attributedText = description
        }
    }
}

extension CiolInformationViewController: CiolInformationViewProtocol {
    func reloadData(with viewModel: CiolInformationModel) {
        delegate = viewModel.delegate
        configureImage(image: viewModel.image)
        titleLabel.text = viewModel.title
        subtitleLabel.isHidden = !viewModel.showSubtitle
        subtitleLabel.text = viewModel.subtitle
        configureDecription(text: viewModel.description)
        actionButton.setTitle(viewModel.ctaTitle, for: .normal)
        actionButton.isHidden = !viewModel.showCTA
    }
}
