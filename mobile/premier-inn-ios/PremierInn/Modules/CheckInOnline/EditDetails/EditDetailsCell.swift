//
//  EditDetailsCell.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 17.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

struct EditDetailsCellModel {
    var title: String
    var textFieldText: String
    var errorMessage: String?
    var traits: FormekaTextFieldTraits?
    var rightTextFieldImage: UIImage?
    var rightTextFieldImageTint: UIColor = .TintD1
    var textFieldIsInteractionEnabled = true
    var isDisabled = false
    var showActionButton = false
}

final class EditDetailsCell: FormekaTextFieldCell {
    static let reuseIdentifier = "EditDetailsCell"

    override var textField: UITextField! {
        get {
            outlinedTextField.textField
        }
        set {
        }
    }

    override var errorLabel: UILabel? {
        get {
            errorMessageLabel
        }
        set {
        }
    }

    var didTapActionButton: (() -> Void)?

    lazy var outlinedTextField = OutLinedTextField()

    private let errorMessageLabel: UIPaddingLabel = {
        let label = UIPaddingLabel(frame: .zero, padding: .init(top: 0, left: 0, bottom: 12, right: 0))
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .Tint8
        label.font = .BodySmall()
        return label
    }()

    private let contentStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.axis = .vertical
        stackView.distribution = .fill
        stackView.alignment = .fill
        stackView.spacing = 12
        return  stackView
    }()

    private let actionItemsStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.axis = .horizontal
        stackView.distribution = .fillEqually
        stackView.alignment = .fill
        stackView.spacing = 16
        return  stackView
    }()

    private let actionButtonStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.axis = .vertical
        stackView.distribution = .fill
        stackView.alignment = .fill
        stackView.spacing = 0
        return  stackView
    }()

    private let actionButton: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false

        var title = AttributedString(PILocalizedString("ciolFindAddress"))
        title.font = .Heading3_Semibold()

        var buttonConfig = UIButton.Configuration.bordered()
        buttonConfig.attributedTitle = title
        buttonConfig.background.strokeColor = .purple
        buttonConfig.background.strokeWidth = 1
        buttonConfig.baseForegroundColor = .BasePurple
        buttonConfig.baseBackgroundColor = .clear

        button.configuration = buttonConfig
        button.layer.cornerRadius = 4
        button.heightAnchor.constraint(equalToConstant: 54).isActive = true
        return button
    }()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        outlinedTextField.textField.delegate = self
        actionButton.addTarget(self, action: #selector(actionButtonTapped), for: .touchUpInside)

        setupView()
        setupConstraints()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    @objc func actionButtonTapped() {
        didTapActionButton?()
    }

    override func prepareForReuse() {
        super.prepareForReuse()

        outlinedTextField.rightImageView.isHidden = true
        outlinedTextField.textField.text = ""
        errorMessageLabel.text = ""
    }

    func configureCell(editDetailsCellModel: EditDetailsCellModel) {
        outlinedTextField.titleLabel.text = editDetailsCellModel.title
        outlinedTextField.textField.text = editDetailsCellModel.textFieldText
        errorMessageLabel.text = editDetailsCellModel.errorMessage
        outlinedTextField.isUserInteractionEnabled = editDetailsCellModel.textFieldIsInteractionEnabled
        actionButtonStackView.isHidden = !editDetailsCellModel.showActionButton
        outlinedTextField.alpha = editDetailsCellModel.isDisabled ? 0.5 : 1

        if let traits = editDetailsCellModel.traits {
            outlinedTextField.textField.applyTraits(traits)
        }

        if let rightImage = editDetailsCellModel.rightTextFieldImage {
            outlinedTextField.rightImageView.isHidden = false
            outlinedTextField.rightImageView.image = rightImage
            outlinedTextField.rightImageView.tintColor = editDetailsCellModel.rightTextFieldImageTint
        }
    }

    // MARK: - Setup View
    private func setupView() {
        contentView.addSubview(contentStackView)

        contentStackView.addArrangedSubview(actionItemsStackView)
        contentStackView.addArrangedSubview(errorMessageLabel)

        actionItemsStackView.addArrangedSubview(outlinedTextField)
        actionItemsStackView.addArrangedSubview(actionButtonStackView)

        actionButtonStackView.addArrangedSubview(UIView())
        actionButtonStackView.addArrangedSubview(actionButton)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            contentStackView.topAnchor.constraint(equalTo: contentView.topAnchor),
            contentStackView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            contentStackView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            contentStackView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor)
        ])
    }
}
