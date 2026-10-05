//
//  KeyView.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import UIKit
import PassKit
import Formeka

class KeyView: UIViewController, KeyViewProtocol {
    private enum Constants {
        static let addToWalletAnalyticsKey = "DigitalKey: Add to Apple Wallet"
        static let confirmationButtonText = PILocalizedString("OK")
        static let allocationButtonText = PILocalizedString("imHereButtonLabel")
        static let showKeyButtonText = PILocalizedString("showKeyInWallet")
        static let allocateRoomText = PILocalizedString("allocateRoomWaitMessage")

        static let buttonHeight: CGFloat = 50
        static let buttonWidth: CGFloat = 320
        static let buttonCornerRadius: CGFloat = 4

        static let tableViewRowHeight: CGFloat = 88
        static let tableViewHeaderPadding: CGFloat = 0
        static let tableViewCornerRadius: CGFloat = 10

        static let buttonVerticalSpacing: CGFloat = 24
    }

    var presenter: KeyPresenterProtocol?
    private var tableViewModel: FormekaViewModel?

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Heading1_ExtraBold(29)
            titleLabel.textColor = .BaseWhite
            titleLabel.accessibilityTraits.insert(.header)
        }
    }

    @IBOutlet weak var tableViewHeightConstraint: NSLayoutConstraint!

    @IBOutlet weak var tableView: UITableView! {
        didSet {
            tableView.estimatedRowHeight = Constants.tableViewRowHeight
            tableView.rowHeight = Constants.tableViewRowHeight
            tableView.backgroundColor = .ColourLD5
            tableView.sectionHeaderTopPadding = Constants.tableViewHeaderPadding
            tableView.layer.cornerRadius = Constants.tableViewCornerRadius
            tableView.layer.masksToBounds = true
            tableView.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]

            tableView.registerCellNib(with: SimpleIndicatorCell.self)
            tableView.isHidden = true
        }
    }

    @IBOutlet weak var messageLabel: UILabel! {
        didSet {
            messageLabel.font = .Heading3_Medium()
            messageLabel.textColor = .BaseWhite
        }
    }

    @IBOutlet weak var appleWalletButton: PKAddPassButton! {
        didSet {
            appleWalletButton.dtxCustomControlName(Constants.addToWalletAnalyticsKey)
        }
    }

    @IBOutlet weak var activityIndicator: UIActivityIndicatorView! {
        didSet {
            activityIndicator.isHidden = true
        }
    }

    @IBOutlet weak var keyBadgeIcon: UIImageView!

    @IBOutlet weak var allocationCheckInButton: UIButton! {
        didSet {
            allocationCheckInButton.setTitle(Constants.allocationButtonText, for: .normal)

            allocationCheckInButton.setTitleColor(.TintD1, for: .normal)
            allocationCheckInButton.titleLabel?.font = .Heading4_Semibold()
            allocationCheckInButton.tintColor = .TintD1
            allocationCheckInButton.backgroundColor = .white
            allocationCheckInButton.layer.cornerRadius = Constants.buttonCornerRadius

            allocationCheckInButton.heightAnchor.constraint(equalToConstant: Constants.buttonHeight).isActive = true
            allocationCheckInButton.widthAnchor.constraint(equalToConstant: Constants.buttonWidth).isActive = true

            allocationCheckInButton.titleLabel?.textAlignment = .center
            allocationCheckInButton.isHidden = true
        }
    }

    @IBOutlet weak var viewInAppleWalletButton: UIButton! {
        didSet {
            viewInAppleWalletButton.setTitle(Constants.showKeyButtonText, for: .normal)

            viewInAppleWalletButton.setTitleColor(.TintD1, for: .normal)
            viewInAppleWalletButton.titleLabel?.font = .Heading4_Semibold()
            viewInAppleWalletButton.tintColor = .TintD1
            viewInAppleWalletButton.backgroundColor = .white
            viewInAppleWalletButton.layer.cornerRadius = Constants.buttonCornerRadius

            viewInAppleWalletButton.heightAnchor.constraint(equalToConstant: Constants.buttonHeight).isActive = true
            viewInAppleWalletButton.widthAnchor.constraint(equalToConstant: Constants.buttonWidth).isActive = true

            viewInAppleWalletButton.titleLabel?.textAlignment = .center
        }
    }

    @IBOutlet weak var guidanceLabel: UILabel! {
        didSet {
            guidanceLabel.textColor = .BaseWhite
            guidanceLabel.font = .Body_Bold()
        }
    }

    init() {
        super.init(nibName: String(describing: KeyView.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(appDidBecomeActive),
            name: UIApplication.didBecomeActiveNotification,
            object: nil
        )

        presenter?.viewIsReady()
    }

    @objc private func appDidBecomeActive() {
        presenter?.viewIsReady()
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)

        NotificationCenter.default.removeObserver(
            self,
            name: UIApplication.didBecomeActiveNotification,
            object: nil
        )
    }

    func showLoadingIndicator() {
        DispatchQueue.main.async {
            self.view.isUserInteractionEnabled = false
            self.view.alpha = 0.5
            self.activityIndicator.isHidden = false
            self.activityIndicator.startAnimating()
        }
    }

    func hideLoadingIndicator() {
        DispatchQueue.main.async {
            self.view.isUserInteractionEnabled = true
            self.view.alpha = 1
            self.activityIndicator.isHidden = true
            self.activityIndicator.stopAnimating()
        }
    }

    @IBAction func appleWalletButtonClicked(_ sender: Any) {
        presenter?.addToAppleWalletDidTap()
    }

    @IBAction func imHereClicked(_ sender: Any) {
    }

    @IBAction func viewInAppleWalletClicked(_ sender: Any) {
        presenter?.viewInAppleWallet()
    }

    public func updateView(with viewModel: KeyDetailsViewModel) {
        setupTableView(using: viewModel)
        setupLabels(using: viewModel)
        handleViewState(using: viewModel.keyState)
    }

    func showAlert(with title: String, and message: String, completion: @escaping () -> Void) {
        let alertController = UIAlertController(
            title: title,
            message: message,
            preferredStyle: .alert
        )

        alertController.addAction(
            UIAlertAction(title: Constants.confirmationButtonText, style: .cancel, handler: { _ in
                completion()
            })
        )

        present(alertController, animated: true, completion: nil)
    }

    func disableBackNavigation() {
        navigationItem.backBarButtonItem?.isEnabled = false
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            title: PILocalizedString("closeBarButtonTitle", comment: "Close bar button title"),
            style: .plain,
            target: self,
            action: #selector(cancelButtonDidTap)
        )
    }

    @objc func cancelButtonDidTap() {
        presenter?.closeButtonClicked()
    }
}

// MARK: - ViewModel Preparation

private extension KeyView {
    func setupLabels(using viewModel: KeyDetailsTextDisplayable) {
        titleLabel.isHidden = false
        messageLabel.isHidden = false
        titleLabel.text = viewModel.title
        messageLabel.text = viewModel.description
    }

    func setupTableView(using viewModel: KeyDetailsInfoRowsDisplayable) {
        tableViewModel = tableViewModel(viewModel: viewModel)
        tableView.delegate = tableViewModel
        tableView.dataSource = tableViewModel

        tableView.reloadData()
        tableView.layoutIfNeeded()

        tableViewHeightConstraint.constant = tableView.contentSize.height
        tableView.isHidden = false
    }
}

// MARK: - View State Management

private extension KeyView {
    func handleViewState(using keyState: KeyState) {
        switch keyState {
        case .digitalKeyReady:
            setupForDigitalKeyReadyState()
        case .beforeCheckInTime, .waitingForAllocation:
            setupForBeforeCheckInTimeAndAllocationState()
        case .checkedIn:
            setupForCheckedInState()
        }
    }

    func setupForDigitalKeyReadyState() {
        appleWalletButton.isHidden = false
        viewInAppleWalletButton.isHidden = true
    }

    func setupForBeforeCheckInTimeAndAllocationState() {
        appleWalletButton.isHidden = true
        viewInAppleWalletButton.isHidden = false
    }

    func setupForCheckedInState() {
        appleWalletButton.isHidden = true
        viewInAppleWalletButton.isHidden = false

        titleLabel.font = .Heading1_Bold_custom(25)
        messageLabel.font = .Heading1_Bold_custom(130)

        keyBadgeIcon.isHidden = false
        guidanceLabel.isHidden = false
        guidanceLabel.text = Constants.allocateRoomText

        viewInAppleWalletButton.topAnchor.constraint(
            greaterThanOrEqualTo: keyBadgeIcon.bottomAnchor,
            constant: Constants.buttonVerticalSpacing
        ).isActive = true
    }
}
