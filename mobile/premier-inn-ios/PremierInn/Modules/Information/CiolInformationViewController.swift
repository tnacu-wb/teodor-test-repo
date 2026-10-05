//
//  CiolInformationViewController.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 08.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

protocol CiolInformationDelegate: AnyObject {
    func close()
    func ctaAction()
}

class CiolInformationViewController: UIViewController {
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

    @IBOutlet weak var descriptionLabel: UILabel! {
        didSet {
            descriptionLabel.font = .BodySmall()
        }
    }

    @IBOutlet weak var actionButton: UIButton! {
        didSet {
            actionButton.titleLabel?.font = .Button1()
        }
    }

    @IBAction func closeButtonDidTap(_ sender: Any) {
        delegate?.close()
    }

    @IBAction func actionButtonDidTap(_ sender: Any) {
        delegate?.ctaAction()
    }

    weak var delegate: CiolInformationDelegate?

    func customise(with viewModel: CiolInformationModel) {
        imageView.image = viewModel.image
        titleLabel.text = viewModel.title
        subtitleLabel.isHidden = !viewModel.showSubtitle
        subtitleLabel.text = viewModel.subtitle
        descriptionLabel.text = viewModel.description
        actionButton.setTitle(viewModel.ctaTitle, for: .normal)
        actionButton.isHidden = !viewModel.showCTA
    }
}
