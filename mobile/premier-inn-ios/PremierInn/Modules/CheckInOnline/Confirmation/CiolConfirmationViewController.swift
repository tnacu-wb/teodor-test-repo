//
//  CiolConfirmationViewController.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

protocol CiolConfirmationViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }
    func displayConfirmation(details: CiolConfirmationDetails)
    func showError(title: String, message: String?)
}

class CiolConfirmationViewController: BaseViewController {
    var presenter: CiolConfirmationPresenterProtocol?

    override var screenName: String { PIAnalytics.StateNames.checkInOnlineConfirmation }
    override var screenType: String { PIAnalytics.StateTypes.ciolFlow }

    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    private let backgroundImageView: UIImageView = {
       let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.clipsToBounds = true
        imageView.contentMode = .scaleAspectFill
        imageView.image = .ciolPiBG
        return imageView
    }()

    private let filterView: UIView = {
        let imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.backgroundColor = .BasePurple
        imageView.alpha = 0.7
        return imageView
    }()

    private let doneTextImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.heightAnchor.constraint(equalToConstant: 170).isActive = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.clipsToBounds = true
        imageView.contentMode = .scaleAspectFit
        imageView.image = UIImage(named: PILocalizedString("ciolDone"))
        return imageView
    }()

    private let checkInSuccessLabel: UILabel = {
       let label = UILabel()
        label.text = PILocalizedString("ciolYouAreCheckedInMessage")
        label.translatesAutoresizingMaskIntoConstraints = false
        label.setContentHuggingPriority(.required, for: .vertical)
        label.setContentCompressionResistancePriority(.required, for: .vertical)
        label.font = .Heading1_ExtraBold(29.0)
        label.accessibilityTraits.insert(.header)
        label.numberOfLines = 0
        label.textAlignment = .center
        label.textColor = .white
        return label
    }()

    private let gotItButton: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setTitle(
            PILocalizedString("ciolGotIt"),
            for: .normal
        )
        button.setTitleColor(.BaseWhite, for: .normal)
        button.titleLabel?.font = .Heading4_Semibold()
        button.heightAnchor.constraint(equalToConstant: 30).isActive = true
        return button
    }()

    private let getKeyInfoButton: UIButton = {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false

        button.setTitle(PILocalizedString("ciolGetRoomKey"), for: .normal)
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
        stackView.isLayoutMarginsRelativeArrangement = true
        stackView.directionalLayoutMargins = .init(top: 0, leading: 20, bottom: 0, trailing: 20)
        return stackView
    }()

    override func viewDidLoad() {
        super.viewDidLoad()

        gotItButton.addTarget(self, action: #selector(gotItAction), for: .touchUpInside)
        getKeyInfoButton.addTarget(self, action: #selector(showKeyInstructions), for: .touchUpInside)

        setupView()
        setupConstraints()
        presenter?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: false)

        presenter?.trackCiolComplete()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        showKeyInstructions()
    }

    @objc func gotItAction() {
        presenter?.refreshStays()
        presenter?.navigateToMyBookings()
    }

    @objc func showKeyInstructions() {
        presenter?.showKeyInstructions()
    }

    // MARK: - Setup View
    private func setupView() {
        view.addSubview(backgroundImageView)
        view.addSubview(filterView)
        view.addSubview(stackView)

        stackView.addArrangedSubview(doneTextImageView)
        stackView.addArrangedSubview(checkInSuccessLabel)
        stackView.setCustomSpacing(130, after: checkInSuccessLabel)
        stackView.addArrangedSubview(getKeyInfoButton)
        stackView.setCustomSpacing(16, after: getKeyInfoButton)
        stackView.addArrangedSubview(gotItButton)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            backgroundImageView.topAnchor.constraint(equalTo: view.topAnchor),
            backgroundImageView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            backgroundImageView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            backgroundImageView.bottomAnchor.constraint(equalTo: view.bottomAnchor),

            filterView.topAnchor.constraint(equalTo: view.topAnchor),
            filterView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            filterView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            filterView.bottomAnchor.constraint(equalTo: view.bottomAnchor),

            stackView.topAnchor.constraint(equalTo: view.centerYAnchor, constant: -120),
            stackView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            stackView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            stackView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -40)
        ])
    }
}

extension CiolConfirmationViewController: CiolConfirmationViewProtocol {
    func displayConfirmation(details: CiolConfirmationDetails) {
        switch details.hotelBrand {
        case .premierInn, .premierInnGermany, .zip:
            backgroundImageView.image = .ciolPiBG
        case .hub:
            backgroundImageView.image = .ciolHubBG
        }
        checkInSuccessLabel.text = details.confirmationMessage
    }

    func showError(title: String, message: String?) {
        DispatchQueue.main.async {
            let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
            controller.addAction(UIAlertAction(
                title: PILocalizedString("OK", comment: "OK button title"),
                style: .cancel,
                handler: nil
            ))

            self.present(controller, animated: true)
        }
    }
}
