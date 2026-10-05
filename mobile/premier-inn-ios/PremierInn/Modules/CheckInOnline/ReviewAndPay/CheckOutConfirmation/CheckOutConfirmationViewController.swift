//
//  CiolConfirmationViewController.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei (Cognizant) on 21.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

class CheckOutConfirmationViewController: BaseViewController {
    var presenter: CheckOutConfirmationPresenterProtocol?

    private let checkOutSuccess: UILabel = {
       let label = UILabel()
        label.text = PILocalizedString("ciolCheckOutTitleMessage")
        label.translatesAutoresizingMaskIntoConstraints = false
        label.setContentHuggingPriority(.required, for: .vertical)
        label.setContentCompressionResistancePriority(.required, for: .vertical)
        label.font = .Title1_ExtraBold()
        label.numberOfLines = 0
        label.textAlignment = .center
        label.textColor = .white
        return label
    }()

    private let subtitleLabel: UILabel = {
       let label = UILabel()
        label.text = PILocalizedString("ciolCheckOutSubtitleMessage")
        label.translatesAutoresizingMaskIntoConstraints = false
        label.setContentHuggingPriority(.required, for: .vertical)
        label.setContentCompressionResistancePriority(.required, for: .vertical)
        label.font = .Heading3_Bold()
        label.numberOfLines = 0
        label.textAlignment = .center
        label.textColor = .white
        return label
    }()

    private let doneButton: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setTitle(
            PILocalizedString("ciolDone"),
            for: .normal
        )
        button.setTitleColor(.TintD1, for: .normal)
        button.titleLabel?.font = .Heading4_Semibold()
        button.tintColor = .TintD1
        button.backgroundColor = .white
        button.layer.cornerRadius = 4
        button.heightAnchor.constraint(equalToConstant: 50).isActive = true
        button.widthAnchor.constraint(equalToConstant: 320).isActive = true
        return button
    }()

    private let stackView: UIStackView = {
       let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.alignment = .center
        stackView.distribution = .fill
        stackView.axis = .vertical
        stackView.spacing = 16
        stackView.isLayoutMarginsRelativeArrangement = true
        stackView.directionalLayoutMargins = .init(top: 0, leading: 20, bottom: 0, trailing: 20)
        return stackView
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .BasePurple
        doneButton.addTarget(self, action: #selector(doneAction), for: .touchUpInside)

        setupView()
        setupConstraints()
        presenter?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: false)
    }

    @objc func doneAction() {
        presenter?.dismissView()
    }

    // MARK: - Setup View
    private func setupView() {
        view.addSubview(stackView)
        view.addSubview(doneButton)

        stackView.addArrangedSubview(checkOutSuccess)
        stackView.addArrangedSubview(subtitleLabel)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            stackView.centerYAnchor.constraint(equalTo: view.centerYAnchor, constant: -60),
            stackView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            stackView.trailingAnchor.constraint(equalTo: view.trailingAnchor),

            doneButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -100),
            doneButton.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            doneButton.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),
            doneButton.heightAnchor.constraint(equalToConstant: 50)
        ])
    }
}

extension CheckOutConfirmationViewController: CheckOutConfirmationViewProtocol {
    func displayConfirmation(details: CheckOutDetails) {
        checkOutSuccess.text = details.confirmationMessage
    }
}
