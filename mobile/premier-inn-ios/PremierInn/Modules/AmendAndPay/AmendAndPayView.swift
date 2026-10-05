//
//  AmendAndPayView.swift
//  PremierInn
//
//  Created by Santa Gurung on 16/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import UIKit
import Formeka

protocol AmendAndPayViewProtocol: AnyObject {
    func updateWith(amendAndPayViewModel: AmendAndPayViewModel)
    func stopDisplayingLoadingElements()
    func showAlertError(title: String, message: String?, error: Error, handler: @escaping ((UIAlertAction) -> Void))
    func toggleLock(processing: Bool)
    func startConfirmationPolling()
    func stopConfirmationPolling()
}

class AmendAndPayView: FormekaViewController {
    override var screenName: String { PIAnalytics.StateNames.amendAndPay }
    override var screenType: String { PIAnalytics.StateTypes.amend }

    var presenter: AmendAndPayPresenterProtocol?

    private var processingView: ProcessingView?
    var activityIndicator: UIActivityIndicatorView!
    private var confirmationActivityIndicator: UIActivityIndicatorView!
    private let viewForActivityIndicator = UIView()
    private let loadingTextLabel = UILabel()
    private var shownConfirmationPollingMessageIndex = 0
    private let confirmationPollingMessagesConfig: ConfirmationPollingMessagesConfig? = SettingsManager.sharedInstance
        .confirmationPollingMessagesConfig

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.title = PILocalizedString("amendAndPayTitle")

        if table != nil {
            table.separatorStyle = .none
            table.accessibilityIdentifier = "amendAndPayTableView"
            registerTableElements()
        }

        setupActivityIndicator()
        presenter?.viewIsReady()
    }

    private func registerTableElements() {
        table.registerCellNib(with: DynamicListCell.self)
        table.registerHeaderFooterNib(with: ActionableHeader.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: BookingReviewTotalPriceCell.self)
        table.registerCellNib(with: BookingReviewSubmitCell.self)
        table.registerCellClass(with: SimpleSeparatorsCell.self)
        table.registerCellNib(with: SwitchCell.self)
    }

    private func setupActivityIndicator() {
        activityIndicator = UIActivityIndicatorView(style: .large)
        activityIndicator.color = .lightGray
        activityIndicator.hidesWhenStopped = true
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(activityIndicator)

        // Auto layout
        let horizontalConstraint = NSLayoutConstraint(
            item: activityIndicator as Any,
            attribute: .centerX,
            relatedBy: .equal,
            toItem: self.view,
            attribute: .centerX,
            multiplier: 1,
            constant: 0
        )
        let verticalConstraint = NSLayoutConstraint(
            item: activityIndicator as Any,
            attribute: .centerY,
            relatedBy: .equal,
            toItem: self.view,
            attribute: .centerY,
            multiplier: 1,
            constant: 0
        )
        NSLayoutConstraint.activate([horizontalConstraint, verticalConstraint])
    }

     func toggleLock(processing: Bool) {
         if processing {
             guard let window = UIApplication.shared.currentWindow() else { return }
             processingView = ProcessingView(frame: window.bounds)
             guard let processingView = processingView else { return }
             window.addSubview(processingView)
         } else {
             UIView.animate(withDuration: .ocd) {
                 self.processingView?.alpha = 0
             } completion: { _ in
                 self.processingView?.removeFromSuperview()
             }
         }
     }
}

extension AmendAndPayView: AmendAndPayViewProtocol {
    func startConfirmationPolling() {
        viewForActivityIndicator.frame = CGRect(x: 0.0, y: 0.0, width: view.frame.size.width, height: view.frame.size.height)
        viewForActivityIndicator.backgroundColor = .BaseBlack.withAlphaComponent(0.8)
        viewForActivityIndicator.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(viewForActivityIndicator)

        confirmationActivityIndicator = UIActivityIndicatorView(style: .large)
        confirmationActivityIndicator.color = .lightGray
        confirmationActivityIndicator.hidesWhenStopped = true
        confirmationActivityIndicator.translatesAutoresizingMaskIntoConstraints = false
        confirmationActivityIndicator.startAnimating()
        view.addSubview(confirmationActivityIndicator)

        let message = confirmationPollingMessagesConfig?.messages[safe: shownConfirmationPollingMessageIndex]?.message
        loadingTextLabel.textColor = .BaseWhite
        loadingTextLabel.text = message
        loadingTextLabel.font = .Body()
        loadingTextLabel.translatesAutoresizingMaskIntoConstraints = false
        loadingTextLabel.sizeToFit()
        viewForActivityIndicator.addSubview(loadingTextLabel)

        // Auto layout
        setupConfirmationActivityIndicatorConstraints()

        scheduleNextConfirmationPollingMessage()
    }

    func stopConfirmationPolling() {
        viewForActivityIndicator.removeFromSuperview()
        confirmationActivityIndicator.stopAnimating()
        confirmationActivityIndicator.removeFromSuperview()

        shownConfirmationPollingMessageIndex = 0
    }

    func stopDisplayingLoadingElements() {
        activityIndicator.stopAnimating()
        table.isUserInteractionEnabled = true
        table.alpha = 1
    }

    func updateWith(amendAndPayViewModel: AmendAndPayViewModel) {
        viewModel = FormekaViewModel(sections: viewModelSections(with: amendAndPayViewModel))
        table.delegate = viewModel
        table.dataSource = viewModel
        table.reloadData()
    }

    func showAlertError(title: String, message: String?, error: Error, handler: @escaping ((UIAlertAction) -> Void)) {
        showErrorAlertWith(title: title, message: message, error: error, handler: handler)
    }

    private func scheduleNextConfirmationPollingMessage() {
        guard let messageDuration = confirmationPollingMessagesConfig?.messages[safe: shownConfirmationPollingMessageIndex]?
              .seconds else { return }

        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(messageDuration)) { [self] in
            advanceToNextConfirmationPollingMessage()
        }
    }

    private func advanceToNextConfirmationPollingMessage() {
        guard shownConfirmationPollingMessageIndex + 1 < confirmationPollingMessagesConfig?.messages.count ?? 0
            else { return }

        shownConfirmationPollingMessageIndex += 1

        guard let nextMessage = confirmationPollingMessagesConfig?.messages[safe: shownConfirmationPollingMessageIndex]?
              .message else { return }

        loadingTextLabel.text = nextMessage

        scheduleNextConfirmationPollingMessage()
    }

    func setupConfirmationActivityIndicatorConstraints() {
        let viewForActivityIndicatorHorizontalConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .centerX,
            relatedBy: .equal,
            toItem: view,
            attribute: .centerX,
            multiplier: 1,
            constant: 0
        )
        let viewForActivityIndicatorVerticalConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .centerY,
            relatedBy: .equal,
            toItem: view,
            attribute: .centerY,
            multiplier: 1,
            constant: 0
        )
        let viewForActivityIndicatorWidthConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .width,
            relatedBy: .equal,
            toItem: view,
            attribute: .width,
            multiplier: 1,
            constant: 0
        )
        let viewForActivityIndicatorHeightConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .height,
            relatedBy: .equal,
            toItem: view,
            attribute: .height,
            multiplier: 1,
            constant: 0
        )
        let confirmationActivityIndicatorHorizontalConstraint = NSLayoutConstraint(
            item: confirmationActivityIndicator as Any,
            attribute: .centerX,
            relatedBy: .equal,
            toItem: viewForActivityIndicator,
            attribute: .centerX,
            multiplier: 1,
            constant: 0
        )
        let confirmationActivityIndicatorVerticalConstraint = NSLayoutConstraint(
            item: confirmationActivityIndicator as Any,
            attribute: .centerY,
            relatedBy: .equal,
            toItem: viewForActivityIndicator,
            attribute: .centerY,
            multiplier: 1,
            constant: 0
        )
        let loadingTextLabelVerticalConstraint = NSLayoutConstraint(
            item: loadingTextLabel as Any,
            attribute: .centerY,
            relatedBy: .equal,
            toItem: viewForActivityIndicator,
            attribute: .centerY,
            multiplier: 1,
            constant: 50
        )
        let loadingTextLabelHorizontalConstraint = NSLayoutConstraint(
            item: loadingTextLabel as Any,
            attribute: .centerX,
            relatedBy: .equal,
            toItem: viewForActivityIndicator,
            attribute: .centerX,
            multiplier: 1,
            constant: 0
        )
        NSLayoutConstraint.activate(
            [
                viewForActivityIndicatorHorizontalConstraint,
                viewForActivityIndicatorVerticalConstraint,
                viewForActivityIndicatorWidthConstraint,
                viewForActivityIndicatorHeightConstraint,
                confirmationActivityIndicatorHorizontalConstraint,
                confirmationActivityIndicatorVerticalConstraint,
                loadingTextLabelVerticalConstraint,
                loadingTextLabelHorizontalConstraint
            ]
        )
    }
}

extension AmendAndPayView: TimeoutErrorAlertControllerOutput {
    func tryAgainButtonDidTap(sender: UIViewController) {
        sender.dismiss(animated: true)
    }
}
