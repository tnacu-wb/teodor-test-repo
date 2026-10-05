//
//  GuestAddCell.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

class GuestAddCell: UITableViewCell {
    static let reuseIdentifier = String(describing: GuestAddCell.self)

    private lazy var stackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.spacing = 8
        stackView.axis = .vertical
        stackView.alignment = .fill
        return stackView
    }()

    private lazy var addStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.spacing = 8
        stackView.axis = .horizontal
        stackView.alignment = .center
        return stackView
    }()


    private lazy var stateLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .Tint2
        label.font = .Body_Medium()
        return label
    }()

    private lazy var chevronImage: UIImageView = {
        let im = UIImage(named: "accessChevron")?.withRenderingMode(.alwaysTemplate)
        let image = UIImageView(image: im)
        image.tintColor = .TintL2
        image.translatesAutoresizingMaskIntoConstraints = false
        image.heightAnchor.constraint(equalToConstant: 22).isActive = true
        image.widthAnchor.constraint(equalToConstant: 22).isActive = true
        return image
    }()

    private lazy var errorView = EditDetailsErrorView()


    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none

        setupViews()
        setupConstraints()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupViews()
        setupConstraints()
    }

    func configure(status: GuestStatus) {
        stateLabel.text = PILocalizedString(status.displayValue)
        stateLabel.textColor = status.displayColor

        switch status {
        case .edited, .empty:
            errorView.isHidden = true
        case .error:
            errorView.isHidden = false
        }
    }

    private func setupViews() {
        let spacer = UIView()
        spacer.setContentHuggingPriority(.defaultLow, for: .horizontal)
        addSubview(stackView)
        addStackView.addArrangedSubview(spacer)
        addStackView.addArrangedSubview(stateLabel)
        addStackView.addArrangedSubview(chevronImage)
        stackView.addArrangedSubview(addStackView)
        stackView.addArrangedSubview(errorView)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: topAnchor, constant: 8),
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16),
            stackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -16),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -8)
        ])
    }
}
