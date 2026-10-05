//
//  PreStayRoomDetailsCell.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 06.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

final class PreStayRoomDetailsCell: SimpleSeparatorsCell {
    // MARK: - Core

    private var viewModel: RoomGuestsViewModelProtocol?

    var leadGuestCallBack: (() -> Void)?
    var secondGuestCallBack: (() -> Void)?

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        selectionStyle = .none
    }

    override func prepareForReuse() {
        super.prepareForReuse()

        viewModel = nil
        firstGuestButton.setTitle(nil, for: .normal)
        secondGuestButton.setTitle(nil, for: .normal)
    }

    func configure(viewModel: RoomGuestsViewModelProtocol?) {
        self.viewModel = viewModel
        setupViews()
    }

    // MARK: - Exposed Properties

    var showLeadGuestErrorMessage: Bool = true {
        didSet {
            leadGuestErrorStackView.isHidden = !showLeadGuestErrorMessage
        }
    }

    var showSecondGuestErrorMessage: Bool = false {
        didSet {
            secondGuestErrorStackView.isHidden = !showSecondGuestErrorMessage
            secondGuestButton.setTitleColor(showSecondGuestErrorMessage ? .Tint8 : .TintD1, for: .normal)
            secondGuestButton.titleLabel?.font = showSecondGuestErrorMessage ? .Body_Semibold() : .Body()
        }
    }

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Body_Medium()
            titleLabel.textColor = .BaseBlack
        }
    }

    @IBOutlet weak var firstGuestButton: UIButton! {
        didSet {
            firstGuestButton.setTitleColor(.TintD1, for: .normal)
            firstGuestButton.titleLabel?.font = .Body()
        }
    }

    @IBOutlet weak var secondGuestButton: UIButton! {
        didSet {
            secondGuestButton.titleLabel?.font = .Body_Semibold()
            secondGuestButton.setTitle(PILocalizedString("searchResultsCriteriaUpdateConfirm"), for: .normal)
        }
    }

    @IBOutlet weak var leadGuestErrorMessageLabel: UILabel! {
        didSet {
            leadGuestErrorMessageLabel.accessibilityIdentifier = AccessibilityIdentifiers.CIOL.errorMessageLabel
            leadGuestErrorMessageLabel.font = .BodySmall()
            leadGuestErrorMessageLabel.tintColor = .TintD1
            leadGuestErrorMessageLabel.text = PILocalizedString("leadGuestMissingIDErrorMessage")
        }
    }
    @IBOutlet weak var leadGuestErrorImageView: UIImageView!
    @IBOutlet weak var leadGuestErrorStackView: UIStackView!

    @IBOutlet weak var secondGuestErrorMessageLabel: UILabel! {
        didSet {
            secondGuestErrorMessageLabel.accessibilityIdentifier = AccessibilityIdentifiers.CIOL.errorMessageLabel
            secondGuestErrorMessageLabel.font = .BodySmall()
            secondGuestErrorMessageLabel.tintColor = .TintD1
            secondGuestErrorMessageLabel.text = PILocalizedString("secondGuestMissingErrorMessage")
        }
    }

    @IBOutlet weak var secondGuestErrorStackView: UIStackView!
    @IBOutlet weak var secondGuestView: UIStackView!

    @IBOutlet weak var childrenView: UIStackView!
    @IBOutlet weak var numberOfChildrenLabel: UILabel! {
        didSet {
            numberOfChildrenLabel.font = .Body()
            numberOfChildrenLabel.textColor = .BaseBlack
        }
    }

    @IBAction func firstGuestButtonTapped(_ sender: Any) {
        leadGuestCallBack?()
    }

    @IBAction func secondGuestButtonTapped(_ sender: Any) {
        secondGuestCallBack?()
    }
}

private extension PreStayRoomDetailsCell {
    func setupViews() {
        guard let viewModel else { return }

        firstGuestButton.setTitle(viewModel.leadGuestFullName, for: .normal)
        secondGuestView.isHidden = !viewModel.shouldShowSecondGuestView
        numberOfChildrenLabel.text = viewModel.formattedChildrenCount
        childrenView.isHidden = !viewModel.shouldShowChildrenCount

        if let secondGuestName = viewModel.secondGuestFullName {
            secondGuestButton.setTitle(secondGuestName, for: .normal)
            secondGuestButton.setTitleColor(.TintD1, for: .normal)
            secondGuestButton.titleLabel?.font = .Body()
        } else {
            secondGuestButton.setTitle(PILocalizedString("searchResultsCriteriaUpdateConfirm"), for: .normal)
            secondGuestButton.titleLabel?.font = .Body_Semibold()
        }
    }
}
