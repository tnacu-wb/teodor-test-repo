//
//  BookingConfirmationView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import EventKitUI
import SimpleNetwork
import MessageUI
import Formeka

class BookingConfirmationViewController: BaseViewController {
    // MARK: - Views

    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var table: UITableView! {
        didSet {
            table.rowHeight = UITableView.automaticDimension
			table.estimatedRowHeight = 100
            table.sectionHeaderHeight = .leastNormalMagnitude
            table.sectionFooterHeight = 10
            table.backgroundColor = .BaseWhite
            table.separatorStyle = .none
            table.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: .leastNormalMagnitude
            ))
            table.registerCellNib(with: BookingConfirmationErrorCell.self)
            table.registerCellNib(with: BookingConfirmationInfoCell.self)
            table.registerCellNib(with: BookingConfirmationHotelInfoCell.self)
            table.registerCellNib(with: TitleSubtitleWithButtonCell.self)
            table.registerCellNib(with: BookingCheckInButtonCell.self)
            table.registerCellNib(with: BookingConfirmationTripSummaryCell.self)
            table.registerCellNib(with: BookingConfirmationTotalPriceCell.self)
            table.registerCellNib(with: BookingConfirmationPriceBreakdownCell.self)
            table.registerCellNib(with: ManagingBookingActionCell.self)
            table.registerCellNib(with: LocationDetailsCell.self)
            table.registerCellNib(with: FAQActionCell.self)
            table.registerCellNib(with: CallHotelCell.self)
            table.registerCellNib(with: BookingConfirmationEventCell.self)
            table.registerCellNib(with: CheckInOutCell.self)
            table.registerCellNib(with: BookingConfirmationAccessibilityCell.self)
            table.registerCellNib(with: AddToWalletCell.self)
            table.registerCellNib(with: FlexibleContentInformationCell.self)
            table.registerCellNib(with: BookingConfirmationViewPassCell.self)
            table.registerCellNib(with: BookingConfirmationHowToUseKeyCell.self)
            table.registerCellNib(with: BookingConfirmationParkingCell.self)
            table.registerCellNib(with: BookingConfirmationStatusCell.self)
            table.registerCellNib(with: BookingConfirmationInstructionsCell.self)
            table.registerCellNib(with: BookedUpsellCell.self)
        }
    }

    // MARK: - Properties

    override var screenName: String { PIAnalytics.StateNames.bookingDetails }
    override var screenType: String { PIAnalytics.StateTypes.myBookings }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    private var formekaViewModel: FormekaViewModel?

    var presenter: BookingConfirmationPresenterInput?

    var justPaid: Bool = false

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.accessibilityIdentifier = "tableView"
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        presenter?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: animated)

        deselectRow(animated: animated)
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        navigationController?.setSwipeGesturePopsViewController(true)
        justPaid = false
    }

    @objc func cancelButtonDidTap() {
        presenter?.cancelNavigationButtonDidTap()
    }

    func deselectRow(animated: Bool = false) {
        guard let indexPath = table.indexPathForSelectedRow else { return }
        table.deselectRow(at: indexPath, animated: animated)
    }
}

extension BookingConfirmationViewController: BookingConfirmationViewInput {
    func setScreenTitle(title: String) {
        self.title = title
    }

    func showLoadingIndicator() {
        table.isUserInteractionEnabled = false
        table.alpha = 0.5
        activityIndicator.startAnimating()
    }

    func hideLoadingIndicator() {
        table.isUserInteractionEnabled = true
        table.alpha = 1
        activityIndicator.stopAnimating()
    }

    func showError(title: String, message: String?, shouldDie: Bool) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(title: PILocalizedString("OK", comment: "OK button title"), style: .cancel) { _ in
            if shouldDie {
                self.navigationController?.popViewController(animated: true)
            }
        })

        present(controller, animated: true)
    }

    func promptUserToEnableCalendarAccess() {
        let alertController = UIAlertController(
            title: PILocalizedString("eventAuthErrorTitle", comment: "Event permission: error alert title"),
            message: PILocalizedString("eventAuthErrorMessage", comment: "Event permission: error alert message"),
            preferredStyle: .alert
        )
        alertController.addAction(UIAlertAction(
            title: PILocalizedString(
                "eventAuthErrorCancelAction",
                comment: "Calendar Permission: error alert cancel action button title"
            ),
            style: .cancel
        ) { _ in
            self.analytics.trackAction(PIAnalytics.Action.locationAuthSettingsCancelButtonTap, userInfo: nil)
        })
        alertController.addAction(UIAlertAction(
            title: PILocalizedString(
                "eventAuthErrorSettingsAction",
                comment: "Event permission: error alert settings action button title"
            ),
            style: .default
        ) { _ in
            self.analytics.trackAction(PIAnalytics.Action.locationAuthSettingsGoButtonTap, userInfo: nil)

            guard let url = URL(string: UIApplication.openSettingsURLString) else { return }
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        })

        present(alertController, animated: true, completion: nil)
    }

    func disableBackNavigation(shouldEnableGestureSwipe: Bool) {
        navigationItem.backBarButtonItem?.isEnabled = false
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            title: PILocalizedString("closeBarButtonTitle"),
            style: .plain,
            target: self,
            action: #selector(cancelButtonDidTap)
        )
        navigationItem.backBarButtonItem = UIBarButtonItem(
            title: PILocalizedString("bookingConfirmationBackButtonTitle"),
            style: .plain,
            target: nil,
            action: nil
        )
        navigationController?.setSwipeGesturePopsViewController(shouldEnableGestureSwipe)
    }

    func update(with viewModel: BookingConfirmationViewModel) {
        formekaViewModel = bookingConfirmationViewModel(with: viewModel)

        table.delegate = formekaViewModel
        table.dataSource = formekaViewModel

        table.reloadData()
    }

    func scrollToTop() {
        guard self.table != nil else { return }
        guard self.table.cellForRow(at: IndexPath(row: 0, section: 1)) != nil else { return }

        self.table.scrollToRow(at: IndexPath(row: 0, section: 0), at: .top, animated: true)
    }

    func showCalendarPrompt(
        fullAccessAction: @escaping () -> Void,
        writeOnlyAction: @escaping () -> Void,
        cancelAction: @escaping () -> Void
    ) {
        showInitialCalendarPrompt(
            fullAccessAction: fullAccessAction,
            writeOnlyAction: writeOnlyAction,
            cancelAction: cancelAction
        )
    }

    func presentAlert(_ alert: UIAlertController) {
        navigationController?.present(alert, animated: true)
    }
}

extension BookingConfirmationViewController: EKEventEditViewDelegate {
	func eventEditViewController(_ controller: EKEventEditViewController, didCompleteWith action: EKEventEditViewAction) {
		controller.dismiss(animated: true)

		if action != .canceled {
            presenter?.reloadViewModel()
		}
	}
}

extension BookingConfirmationViewController: BookingConfirmationAccessibilityCellCallProtocol {
    func didTapCallHotel() {
        self.presenter?.callHotelButtonDidTap()
    }

    func didTapEmailUs() {
        guard MFMailComposeViewController.canSendMail() else {
            let controller = UIAlertController(
                title: PILocalizedString("sendFeedbackErrorAlertTitle"),
                message: PILocalizedString("sendFeedbackErrorAlertMessage"),
                preferredStyle: .alert
            )

            controller.addAction(UIAlertAction(
                title: PILocalizedString("sendFeedbackErrorAlertAction"),
                style: .cancel,
                handler: nil
            ))
            present(controller, animated: true)
            return
        }

        let controller = MFMailComposeViewController()
        controller.mailComposeDelegate = self
        controller.navigationBar.tintColor = .white
        controller.setToRecipients([Constants.accessibleContactCenterEmailAddress])

        present(controller, animated: true)
    }
}
