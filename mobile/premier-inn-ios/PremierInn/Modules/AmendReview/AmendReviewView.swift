//
//  AmendReviewView.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import Foundation
import UIKit

enum AmendReviewTag: String {
    case balanceOutstanding
    case warning
}

protocol AmendReviewViewViewModel {
    var hotelImageUrl: URL? { get }
    var hotelName: String? { get }
    var previousTotal: String { get }
    var amendments: [AmendmentDetailsViewModel] { get }
    var outstandingBalance: String { get }
    var outstandingBalanceActionRequiredText: String { get }
    var amendPaymentViewModel: AmendPaymentIntervalViewModel? { get }
    var showECILCORemovalMessage: Bool { get }
    var tripSummary: AmendReviewTripSummary { get }
}

protocol AmendmentDetailsViewModel {
    var title: NSAttributedString { get }
    var description: String? { get }
}

protocol AmendReviewViewEventHandler: AnyObject {
    func viewIsReady()
    func confirmChangesButtonDidTap()
    func updatePaymentOptionSelection(paymentOption: PaymentIntervalOption) -> AmendReviewViewViewModel?
}

protocol AmendReviewViewProtocol: AnyObject {
    func update(with reviewViewModel: AmendReviewViewViewModel)
    func set(processing: Bool)
    func showError(with title: String, and message: String, completion: @escaping (UIAlertAction) -> Void)
    func amendFailed()
    func validateForm() throws -> PIDictionary?
    func startConfirmationPolling()
    func stopConfirmationPolling()
    func showCustomUIError(title: String, message: String?, dismissButton: String?)
    func showAlertError(title: String, message: String?, error: Error, handler: @escaping ((UIAlertAction) -> Void))
}

class AmendReviewView: FormekaViewController {
    @IBOutlet weak var confirmContainer: UIView! {
        didSet {
            confirmContainer.set(hasHeaderFooterShadow: true)
        }
    }

    @IBOutlet weak var termsConditionsLabel: UILabel! {
        didSet {
            termsConditionsLabel.attributedText = {
                let text = PILocalizedString("amendReviewTermsAndConditionsStatement")
                let highlightText = PILocalizedString("amendReviewTermsAndConditionsHighlighted")

                let attributedString = NSMutableAttributedString(
                    string: text,
                    attributes: [
                        .font: UIFont.BodySmall(),
                        .foregroundColor: UIColor.TintD1,
                        NSAttributedString.Key.paragraphStyle: {
                                     let style = NSMutableParagraphStyle()
                                     style.lineSpacing = 3
                                     style.alignment = .left

                                     return style
                                 }()
                    ]
                )
                text.ranges(of: highlightText).forEach { range in
                    attributedString.addAttributes([
                        .font: UIFont.BodySmall_Semibold(),
                        .foregroundColor: UIColor.BasePurple
                    ], range: range)
                }
                return attributedString
            }()
        }
    }

    @IBOutlet weak var confirmButton: UIButton! {
        didSet {
            confirmButton.backgroundColor = .Tint1
            confirmButton.setTitleColor(.BaseWhite, for: .normal)
            confirmButton.setTitle(PILocalizedString("amendReviewConfirmChangesButton", comment: ""), for: .normal)
            confirmButton.accessibilityIdentifier = "confirmChangesButton"
            confirmButton.titleLabel?.font = .Button1()
            confirmButton.layer.cornerRadius = 3
        }
    }

    @IBAction func confirmButtonTapped(_ sender: Any) {
        eventHandler?.confirmChangesButtonDidTap()
    }

    override var screenName: String { PIAnalytics.StateNames.amendReview }
    override var screenType: String { PIAnalytics.StateTypes.amend }

    private var confirmationActivityIndicator: UIActivityIndicatorView!
    private let viewForActivityIndicator = UIView()
    private let loadingTextLabel = UILabel()
    private var shownConfirmationPollingMessageIndex = 0
    private let confirmationPollingMessagesConfig: ConfirmationPollingMessagesConfig? = SettingsManager.sharedInstance
        .confirmationPollingMessagesConfig

    var eventHandler: AmendReviewViewEventHandler?

    var outstandingBalanceActionRequired: String?

    private var loadingView: LoadingView?

    override func loadView() {
        let nib = UINib(nibName: String(describing: AmendReviewView.self), bundle: nil)
        view = nib.instantiate(withOwner: self, options: nil).first as? UIView
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.title = PILocalizedString("amendReviewAmendmentsTitle", comment: "")

        if table != nil {
            table.separatorStyle = .none
            table.accessibilityIdentifier = "amendReviewTableView"

            registerTableElements()
        }
        setupTermsConditionsGesture()
        eventHandler?.viewIsReady()
    }

    private func setupTermsConditionsGesture() {
        let tap = UITapGestureRecognizer(target: self, action: #selector(termsTapped))
        termsConditionsLabel.isUserInteractionEnabled = true
        termsConditionsLabel.addGestureRecognizer(tap)
    }

    @objc func termsTapped() {
        guard let url = Constants.termsAndConditionsUrl else { return }
        openURLInSafari(url: url)
    }

    private func registerTableElements() {
        table.registerCellNib(with: BookingReviewAdditionsCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: FormekaSegmentedControlCell.self)
        table.registerCellNib(with: HotelDetailSeparatorCell.self)
        table.registerCellNib(with: PaymentCardCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: CVVInputCell.self)
        table.registerCellNib(with: UITableViewCell.self)
        table.registerCellNib(with: BookingConfirmationHotelInfoCell.self)
        table.registerCellNib(with: CheckInOutCell.self)
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)
        table.registerHeaderFooterNib(with: ActionableHeader.self)
        table.registerCellClass(with: BottomBorderCell.self)
    }
}

extension AmendReviewView: AmendReviewViewProtocol {
    func update(with reviewViewModel: AmendReviewViewViewModel) {
        self.outstandingBalanceActionRequired = reviewViewModel.outstandingBalanceActionRequiredText

        viewModel = FormekaViewModel(sections: viewModelSections(with: reviewViewModel))

        table?.delegate = viewModel
        table?.dataSource = viewModel
        table.backgroundColor = .BaseWhite
        table.reloadData()
    }

    func set(processing: Bool) {
        if processing {
            showLoadingView()
        } else {
            hideLoadingView()
        }
    }

    private func showLoadingView() {
        view.isUserInteractionEnabled = false

        loadingView = LoadingView(frame: CGRect(
            x: (view.frame.size.width / 2) - 50,
            y: (view.frame.size.height / 2) - 50,
            width: 80,
            height: 80
        ))
        loadingView?.alpha = 0

        guard let loadingView = loadingView else { return }

        view.addSubview(loadingView)

        UIView.animate(
            withDuration: .ocd,
            animations: {
                loadingView.alpha = 1
            },
            completion: { _ in
            loadingView.animate()
        }
        )
    }

    private func hideLoadingView() {
        guard let loadingView = loadingView else { return }

        UIView.animate(
            withDuration: .ocd,
            animations: {
                loadingView.alpha = 0
            },
            completion: { [weak self] _ in
            self?.view.isUserInteractionEnabled = true
            loadingView.removeFromSuperview()
            self?.loadingView?.stopAnimating()
        }
        )
    }

    func amendFailed() {
        let alertController = UIAlertController(
            title: PILocalizedString("amendBookingFailedAlertTitle", comment: "Amend Booking failed alert title"),
            message: PILocalizedString("amendBookingFailedAlertMessage", comment: "Amend Booking failed alert message"),
            preferredStyle: .alert
        )
        let okAction = UIAlertAction(
            title: PILocalizedString("OK", comment: "OK button title"),
            style: .cancel
        )
        alertController.addAction(okAction)

        present(alertController, animated: true, completion: nil)
    }

    func showError(with title: String, and message: String, completion: @escaping (UIAlertAction) -> Void) {
        showAlertWith(title: title, message: message, handler: completion)
    }

    func validateForm() throws -> PIDictionary? {
        try viewModel?.validate()

        return viewModel?.values
    }

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

    func showCustomUIError(title: String, message: String?, dismissButton: String?) {
        let controller = TimeoutErrorAlertController(title: nil, message: nil, preferredStyle: .alert)
        controller.headingText = title
        controller.messageText = message
        controller.controllerOutput = self
        controller.messageButton = dismissButton

        present(controller, animated: true)
    }

    func setupConfirmationActivityIndicatorConstraints() {
        let viewForActivityIndicatorHorizontalConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .centerX,
            relatedBy: .equal,
            toItem: self.view,
            attribute: .centerX,
            multiplier: 1,
            constant: 0
        )
        let viewForActivityIndicatorVerticalConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .centerY,
            relatedBy: .equal,
            toItem: self.view,
            attribute: .centerY,
            multiplier: 1,
            constant: 0
        )
        let viewForActivityIndicatorWidthConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .width,
            relatedBy: .equal,
            toItem: self.view,
            attribute: .width,
            multiplier: 1,
            constant: 0
        )
        let viewForActivityIndicatorHeightConstraint = NSLayoutConstraint(
            item: viewForActivityIndicator as Any,
            attribute: .height,
            relatedBy: .equal,
            toItem: self.view,
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

extension AmendReviewView: FormekaSegmentedControlCellDelegate {
    func formekaSegmentedControlValueDidChange(cell: FormekaSegmentedControlCell) {
        guard let indexPath = table.indexPath(for: cell) else { return }
        guard let row = viewModel?.row(at: indexPath) else { return }

        let paymentOption: PaymentIntervalOption = cell.segmentedControl.selectedSegmentIndex == 0 ? .later : .now
        row.value = paymentOption
        let reviewModel = eventHandler?.updatePaymentOptionSelection(paymentOption: paymentOption)
        updateBalanceOutstandingText(reviewModel: reviewModel)
        updatePaymentInfoMessage(for: paymentOption)
    }

    private func updateBalanceOutstandingText(reviewModel: AmendReviewViewViewModel?) {
        guard let indexPath = viewModel?.remove(rowNamed: AmendReviewTag.balanceOutstanding.rawValue) else { return }

        let updatedTextRow = totalSubtextRow(text: reviewModel?.outstandingBalanceActionRequiredText)
        viewModel?.add(row: updatedTextRow, at: indexPath)
        table.reloadRows(at: [indexPath], with: .automatic)
    }

    private func updatePaymentInfoMessage(for option: PaymentIntervalOption) {
        table.beginUpdates()
        if option == .now {
            guard let indexPathForSegmentedControl = viewModel?
                  .indexPath(forRowNamed: ReviewAndBookRow.paymentOption.rawValue) else { return }

            let infoIndexPath = IndexPath(
                row: indexPathForSegmentedControl.row + 1,
                section: indexPathForSegmentedControl.section
            )
            let infoRow = iconInfoRow(
                tag: ReviewAndBookRow.paymentTimeMessage3C.rawValue,
                text: PILocalizedString("amendPayNow3CMessage"),
                topPadding: 4,
                bottomPadding: 8,
                style: .info
            )
            viewModel?.add(row: infoRow, at: infoIndexPath)
            table.insertRows(at: [infoIndexPath], with: .automatic)
        } else {
            guard let indexPath = viewModel?.remove(rowNamed: ReviewAndBookRow.paymentTimeMessage3C.rawValue) else { return }

            table.deleteRows(at: [indexPath], with: .automatic)
        }
        table.endUpdates()
    }

    func removePersistedCompanyName() { }
}

extension AmendReviewView: TimeoutErrorAlertControllerOutput {
    func tryAgainButtonDidTap(sender: UIViewController) {
        sender.dismiss(animated: true)
    }
}
