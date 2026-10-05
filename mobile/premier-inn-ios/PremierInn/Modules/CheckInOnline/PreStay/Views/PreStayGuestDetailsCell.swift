//
//  PreStayGuestDetailsCell.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 06.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class PreStayGuestDetailsCell: SimpleSeparatorsCell {
    private enum Constants {
        static let horizontalPadding: CGFloat = 16
    }

    var didTapCell: (() -> Void)? {
        didSet {
            handleChevronLayout()
        }
    }

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Body_Medium()
            titleLabel.textColor = .BaseBlack
            titleLabel.textAlignment = .left
        }
    }

    @IBOutlet weak var valueLabel: UILabel! {
        didSet {
            valueLabel.font = .Body()
            valueLabel.textColor = .TintD1
            valueLabel.textAlignment = .right
        }
    }

    var shouldShowErrorMessage: Bool = false {
        didSet {
            errorMessageStackView.isHidden = !shouldShowErrorMessage
        }
    }

    @IBOutlet weak var errorMessageLabel: UILabel! {
        didSet {
            errorMessageLabel.font = .Body()
        }
    }

    @IBOutlet weak var errorMessageStackView: UIStackView!

    @IBOutlet weak var viewTrailingConstraint: NSLayoutConstraint!
    @IBOutlet weak var valueLabelTrailingConstraint: NSLayoutConstraint!
    @IBOutlet weak var accessChevron: UIImageView!

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setup()
    }

    override func prepareForReuse() {
        super.prepareForReuse()

        shouldShowErrorMessage = false
    }

    private func setup() {
        selectionStyle = .none
        handleChevronLayout()

        let tap = UITapGestureRecognizer(target: self, action: #selector(handleDidTapCell))
        self.addGestureRecognizer(tap)
    }

    @objc private func handleDidTapCell() {
        didTapCell?()
    }

    private var shouldShowChevron: Bool {
        didTapCell != nil
    }

    private func handleChevronLayout() {
        accessChevron?.isHidden = !shouldShowChevron
        if shouldShowChevron {
            valueLabelTrailingConstraint?.constant = viewTrailingConstraint.constant
        }
    }
}
