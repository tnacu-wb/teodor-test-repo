//
//  CiolUpsellItemCell.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 09.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CiolUpsellItemCell: UITableViewCell {
    // MARK: - UI Elements

    private let containerView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.borderWidth = 1
        view.layer.borderColor = UIColor.lightGray.cgColor
        view.layer.cornerRadius = 3
        return view
    }()

    private let itemImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.contentMode = .scaleAspectFill
        imageView.clipsToBounds = true
        imageView.layer.cornerRadius = 4
        return imageView
    }()

    private let titleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.Heading2_Bold()
        label.textColor = .TintD1
        label.numberOfLines = 1
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()

    private let descriptionLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.BodySmall()
        label.textColor = .TintD2
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()

    private let paragraphStyle: NSParagraphStyle = {
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineHeightMultiple = 1.0
        paragraphStyle.lineBreakMode = .byWordWrapping
        return paragraphStyle
    }()

    private lazy var messageView = CiolUpsellMessageView()

    private let stackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.alignment = .leading
        stackView.spacing = 4
        stackView.translatesAutoresizingMaskIntoConstraints = false
        return stackView
    }()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupView()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // MARK: - Setup View
    private func setupView() {
        contentView.addSubview(itemImageView)
        contentView.addSubview(stackView)
        contentView.addSubview(containerView)

        stackView.addArrangedSubview(titleLabel)
        stackView.addArrangedSubview(descriptionLabel)
        stackView.addArrangedSubview(messageView)

        setupConstraints()
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            // Container view constraints
            containerView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 0),
            containerView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: 0),
            containerView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 2),
            containerView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -2),

            // Image constraints
            itemImageView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            itemImageView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            itemImageView.widthAnchor.constraint(equalToConstant: 56),
            itemImageView.heightAnchor.constraint(equalToConstant: 56),

            // stack constraints
            stackView.leadingAnchor.constraint(equalTo: itemImageView.trailingAnchor, constant: 16),
            stackView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 16),
            stackView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            stackView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -16)
        ])
    }

    func configureAvailable(
        with viewModel: CiolUpsellItemViewModelProtocol,
        setup: CiolUpsellCellSetup
    ) {
        titleLabel.text = viewModel.title
        titleLabel.textColor = viewModel.enabled ? .TintD1 : .TintD1.withAlphaComponent(0.4)
        itemImageView.alpha = viewModel.enabled ? 1 : 0.4
        descriptionLabel.isHidden = !viewModel.enabled && !setup.isMultiRoom
        messageView.isHidden = viewModel.enabled
        if setup.isMultiRoom {
            descriptionLabel.text = viewModel.costSummary
            descriptionLabel.textColor = viewModel.enabled ? .TintD2 : .TintD2.withAlphaComponent(0.4)
            itemImageView.alpha = viewModel.enabled ? 1 : 0.4
            messageView.isHidden = true
        } else {
            if viewModel.enabled {
                descriptionLabel.text = viewModel.costSummary
                descriptionLabel.textColor = viewModel.enabled ? .TintD2 : .TintD2.withAlphaComponent(0.4)
                itemImageView.alpha = viewModel.enabled ? 1 : 0.4
            } else {
                if let text = setup.text {
                    messageView.configure(config: .init(hasBackground: .info, text: text, isMultiRoom: false))
                    messageView.isHidden = false
                }
                descriptionLabel.isHidden = true
            }
        }
        itemImageView.image = nil
        if let imageURL = viewModel.imageURL {
            itemImageView.setImage(with: imageURL)
        }
    }

    func configureSelected(with viewModel: CiolUpsellItemViewModelProtocol, setup: CiolUpsellCellSetup) {
        titleLabel.text = viewModel.title
        titleLabel.textColor = .TintD1
        descriptionLabel.isHidden = !viewModel.enabled
        descriptionLabel.text = viewModel.costSummary
        descriptionLabel.textColor = .TintD2
        messageView.isHidden = viewModel.enabled
        messageView.configure(config: setup)
        if setup.isMultiRoom {
            if let items = setup.items {
                descriptionLabel.isHidden = false
                let description = items.joined(separator: "\n")
                let upsellsText = NSAttributedString(
                    string: description,
                    attributes: [.paragraphStyle: paragraphStyle]
                )
                descriptionLabel.attributedText = upsellsText
            }
            messageView.isHidden = false
        } else {
            if viewModel.isFoodUpsell, let items = setup.items {
                let description = items.joined(separator: "\n")
                let upsellsText = NSAttributedString(
                    string: description,
                    attributes: [.paragraphStyle: paragraphStyle]
                )
                descriptionLabel.attributedText = upsellsText
                descriptionLabel.isHidden = false
                messageView.isHidden = false
            } else {
                descriptionLabel.text = viewModel.costSummary
            }
        }

        itemImageView.image = nil
        if let imageURL = viewModel.imageURL {
            itemImageView.setImage(with: imageURL)
        }
    }
}

struct CiolUpsellCellSetup {
    let hasBackground: MessageType?
    let text: String?
    let isMultiRoom: Bool
    let items: [String]?
    let enabled: Bool?

    init(
        hasBackground: MessageType? = nil,
        text: String? = nil,
        isMultiRoom: Bool,
        items: [String]? = nil,
        enabled: Bool = false
    ) {
        self.hasBackground = hasBackground
        self.text = text
        self.isMultiRoom = isMultiRoom
        self.items = items
        self.enabled = enabled
    }

    enum MessageType {
        case info
        case warning

        var messageColor: UIColor {
            switch self {
            case .warning: return .Tint7
            case .info: return .TintL3.withAlphaComponent(0.2)
            }
        }
    }
}

class CiolUpsellMessageView: UIView {
    func configure(config: CiolUpsellCellSetup) {
        if let backgroundType = config.hasBackground {
            backgroundColor = backgroundType.messageColor
            layer.cornerRadius = 8
            clipsToBounds = true
        } else {
            backgroundColor = .clear
        }
        messageLabel.text = config.text
        if config.text != nil {
            isHidden = false
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    private lazy var messageLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = description
        label.font = .Subtext()
        label.numberOfLines = 0
        return label
    }()

    private func setup() {
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(messageLabel)

        NSLayoutConstraint.activate([
            messageLabel.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 8.0),
            messageLabel.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -8.0),
            messageLabel.topAnchor.constraint(equalTo: topAnchor, constant: 4.0),
            messageLabel.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -4.0)
        ])
    }
}
