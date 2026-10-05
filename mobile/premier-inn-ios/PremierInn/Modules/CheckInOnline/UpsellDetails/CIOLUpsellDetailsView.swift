//
//  CIOLUpsellDetailsView.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 15.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CIOLUpsellDetailsView: UIStackView, CIOLStepperDelegate {
    weak var updateDelegate: CIOLUpsellDetailsDelegate?
    var model: CiolUpsellSubitemViewModel?

    private lazy var listStackView: UIStackView = {
        let stackview = UIStackView()
        stackview.axis = .vertical
        stackview.alignment = .leading
        stackview.spacing = 8.0
        return stackview
    }()

    private func kidsView(with description: String) -> UIView {
        let kidsView = UIView()
        kidsView.backgroundColor = .Tint1.withAlphaComponent(0.1)
        kidsView.layer.cornerRadius = 4
        kidsView.clipsToBounds = true
        kidsView.translatesAutoresizingMaskIntoConstraints = false

        let kidsLabel = UILabel()
        kidsLabel.translatesAutoresizingMaskIntoConstraints = false
        kidsLabel.text = description
        kidsLabel.font = .Subtext()
        kidsLabel.numberOfLines = 0

        kidsView.addSubview(kidsLabel)
        NSLayoutConstraint.activate([
            kidsLabel.leadingAnchor.constraint(equalTo: kidsView.leadingAnchor, constant: 8.0),
            kidsLabel.trailingAnchor.constraint(equalTo: kidsView.trailingAnchor, constant: -8.0),
            kidsLabel.topAnchor.constraint(equalTo: kidsView.topAnchor, constant: 4.0),
            kidsLabel.bottomAnchor.constraint(equalTo: kidsView.bottomAnchor, constant: -4.0)
        ])
        return kidsView
    }

    private func descriptionLabel(text: String, enabled: Bool = true) -> UILabel {
        let label = UILabel()
        label.font = .BodySmall()
        label.textColor = enabled ? .TintD1 : .TintL1
        label.numberOfLines = 0
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = text
        return label
    }

    private lazy var actionsView = CIOLStepperView()

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    private func setup() {
        axis = .horizontal
        distribution = .fill
        alignment = .center
    }

    func configure(viewModel: CiolUpsellSubitemViewModel) {
        self.model = viewModel
        viewModel.descriptions.forEach({ upsellDetails in
            switch upsellDetails {
            case .generic(let description):
                listStackView.addArrangedSubview(descriptionLabel(text: description, enabled: viewModel.enabled))
            case .title(let description):
                let headerView = CiolUpsellHeaderView()
                headerView.configure(header: description, enabled: viewModel.enabled)
                listStackView.addArrangedSubview(headerView)
            case .kidsLabel(let kidsMealDescription):
                let kidsLabel = kidsView(with: kidsMealDescription)
                listStackView.addArrangedSubview(kidsLabel)
            }
        })
        addArrangedSubview(listStackView)
        if viewModel.canUpdateQuantity {
            let updateValuesView = actionsView
            updateValuesView.updateEnablement(enabled: viewModel.enabled)
            updateValuesView.value = viewModel.quantity
            updateValuesView.updateDelegate = self
            NSLayoutConstraint.activate([
                updateValuesView.widthAnchor.constraint(equalToConstant: 87.6),
                updateValuesView.heightAnchor.constraint(equalToConstant: 27)
            ])
            addArrangedSubview(updateValuesView)
        }
    }

    func reconfigure(viewModel: CiolUpsellSubitemViewModel) {
        self.model = viewModel
        actionsView.updateEnablement(enabled: viewModel.enabled)
        listStackView.subviews.forEach({ view in
            if let headerView = view as? CiolUpsellHeaderView {
                headerView.configureEnablement(enabled: viewModel.enabled)
            } else if let genericLabel = view as? UILabel {
                genericLabel.textColor = viewModel.enabled ? .TintD1 : .TintL1
            }
        })
    }

    func didUpdate(action: CIOLStepperAction, completion: @escaping () -> Void) {
        guard let model else { return }
        updateDelegate?.didUpdateUpsell(with: model.id, action: action, completion: completion)
    }
}

class CiolUpsellHeaderView: UIStackView {
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = .BodySmall_Bold()
        titleLabel.numberOfLines = 1
        return titleLabel
    }()

    lazy var subtitleLabel: UILabel = {
        let subtitleLabel = UILabel()
        subtitleLabel.font = .BodySmall()
        subtitleLabel.numberOfLines = 1
        return subtitleLabel
    }()

    private func setup() {
        translatesAutoresizingMaskIntoConstraints = false
        axis = .vertical
        alignment = .leading
        addArrangedSubview(titleLabel)
        addArrangedSubview(subtitleLabel)
    }

    func configure(header: CiolUpsellDetailsTitleDescription, enabled: Bool) {
        titleLabel.textColor = enabled ? .TintD1 : .TintL1
        titleLabel.text = header.title
        subtitleLabel.textColor = enabled ? .TintD1 : .TintL1
        subtitleLabel.text = header.subtitle
    }

    func configureEnablement(enabled: Bool) {
        titleLabel.textColor = enabled ? .TintD1 : .TintL1
        subtitleLabel.textColor = enabled ? .TintD1 : .TintL1
    }
}
