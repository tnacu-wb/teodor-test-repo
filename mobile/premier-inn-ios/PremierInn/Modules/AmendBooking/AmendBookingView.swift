//
//  AmendBookingView.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/10/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka

typealias AmendmentsModel = (containsAmendments: Bool, priceDifferenceText: String?)

protocol AmendUpsellsModel {
    var title: String { get }
    var description: String? { get }
    var actionTitle: String? { get }
}

protocol AmendBookingViewModel {
    var hotelImageUrl: URL? { get }
    var hotelName: String { get }
    var bannerMessage: String? { get }
    var datesSummary: String { get }
    var amendable: Bool { get }
	var shouldShowUpsellsRemovedMessage: Bool { get }
    var shouldShowChangeDatesButton: Bool { get }
    var shouldShowChangeUpsellsButton: Bool { get }
    var shouldShowEditRoomButton: Bool { get }
    var shouldShowAddRoomButton: Bool { get }
    var shouldShowCancelBookingButton: Bool { get }
    var amendUpsellsModel: AmendUpsellsModel? { get }
    var rooms: [AmendBookingRoomViewModel] { get }
    var amendmentsModel: AmendmentsModel { get }
}

protocol AmendBookingRoomViewModel {
    var existingRoom: Bool { get }
    var roomNumber: Int { get }
    var roomDescription: String { get }
    var leadGuest: String { get }
    var mealDescription: String? { get }
    var extraDescription: String? { get }
}

protocol AmendBookingViewEventHandler {
    func viewIsReady()
    func editDatesButtonDidTap()
    func editUpsellsDidTap()
    func addRoomButtonDidTap()
    func editButtonDidTap(forRoomNumber roomNumber: Int)
    func cancelButtonDidTap()
    func continueButtonDidTap()
    func backButtonDidTap()
}

protocol AmendBookingViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }

    func updateViewModel(
        with amendViewModel: AmendBookingViewModel,
        isNewRoomAdded: Bool
    )
    func set(cancelling: Bool)
    func set(processing: Bool)
    func toggleLock(is processing: Bool)

    func showAlert(with title: String, and message: String, completion: @escaping () -> Void)
    func showOptionAlert(
        with title: String,
        and message: String,
        confirmTitle: String?,
        cancelTitle: String?,
        completion: @escaping () -> Void
    )
}

class AmendBookingView: BaseViewController {
    override var screenName: String { PIAnalytics.StateNames.manageBooking }
    override var screenType: String { PIAnalytics.StateTypes.myBookings }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    @IBOutlet weak var table: UITableView! {
        didSet {
            table.accessibilityIdentifier = "amendTableView"
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 44

            table.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: CGFloat.leastNormalMagnitude
            ))
            table.backgroundColor = .BaseWhite

            registerTableElements()
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var priceDifference: UILabel! {
        didSet {
            priceDifference.accessibilityIdentifier = "priceDifferenceAcc"
        }
    }
    @IBOutlet weak var continueContainerBottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var continueContainer: UIView!
    @IBOutlet weak var continueContainerHeight: NSLayoutConstraint!
    @IBOutlet weak var continueButton: RoundedCornersButton! {
        didSet {
            continueButton.accessibilityIdentifier = "reviewAmendsAcc"
            continueButton.titleLabel?.font = .Button1()
            continueButton.setTitle(PILocalizedString("reviewAmendsButtonTitle"), for: .normal)
            continueButton.backgroundColor = .Tint1
        }
    }

    private var viewModel: FormekaViewModel?
    private var canAmend: Bool = false

    var eventHandler: AmendBookingViewEventHandler?

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .whiteTwo

        continueContainer.layer.shadowOffset = CGSize(width: 0.0, height: -1.0)
        continueContainer.layer.shadowColor = UIColor.black.cgColor
        continueContainer.layer.shadowOpacity = 0.5
        continueContainer.layer.shouldRasterize = true
        continueContainer.layer.rasterizationScale = UIScreen.main.scale

        eventHandler?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationItem.backBarButtonItem?.isEnabled = false
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            image: UIImage(named: "back"),
            style: .plain,
            target: self,
            action: #selector(backButtonDidTap)
        )
        navigationItem.leftBarButtonItem?.accessibilityIdentifier = "amendBackButton"
    }

    @objc private func backButtonDidTap() {
        eventHandler?.backButtonDidTap()
    }

    private func registerTableElements() {
        table.registerCellNib(with: CancelButtonCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: BookingConfirmationHotelInfoCell.self)
        table.registerCellNib(with: SimpleSubtitleCell.self)
        table.registerCellNib(with: SimpleActionCell.self)
        table.registerCellNib(with: AddRoomCell.self)
        table.registerCellClass(with: UITableViewCell.self)
        table.registerCellClass(with: BottomBorderCell.self)
    }

    private func toggleCancelButtonProcessing(active: Bool) {
        guard let cell: CancelButtonCell = self.viewModel?.cell(
            forRowNamed: AmendBookingRow.cancelButton.rawValue,
            table: self.table
        ) else { return }

        cell.cancelButton.setTitle(
            active ? nil :
                PILocalizedString("bookingManagementCancelButtonTitle", comment: "Booking management cancel button title"),
            for: .normal
        )
        cell.cancelButton.isEnabled = !active

        if active {
            cell.activityIndicator.startAnimating()
        } else {
            cell.activityIndicator.stopAnimating()
        }
    }

    @IBAction func continueButtonDidTap(_ sender: Any) {
        eventHandler?.continueButtonDidTap()
    }

    private func attributedStringForConfirmButton(with priceDifferenceString: String?) -> NSAttributedString? {
        guard let priceDifferenceString = priceDifferenceString else { return nil }

        let prefixString = PILocalizedString("priceDifferenceLabel", comment: "")
        let suffixString = priceDifferenceString
        let fullString = String(prefixString + "\n" + suffixString)

        let mutableString = NSMutableAttributedString(string: fullString, attributes: [.foregroundColor: UIColor.TintD1])

        let prefixRange = NSRange(location: 0, length: prefixString.count)
        mutableString.addAttributes([.font: UIFont.Subtext()], range: prefixRange)

        guard let suffixRange = fullString.ranges(of: suffixString).first
            else { return NSAttributedString(attributedString: mutableString) }
        mutableString.addAttributes([.font: UIFont.Heading2_Semibold()], range: suffixRange)

        return NSAttributedString(attributedString: mutableString)
    }

    private func updateContinueSection(with amendmentsModel: AmendmentsModel) {
        let bottomAmountConstraintWhenAmendmentsMade = view.safeAreaInsets.bottom

        continueContainerHeight.constant = 64 + bottomAmountConstraintWhenAmendmentsMade

        if canAmend {
            continueContainerBottomConstraint.constant = amendmentsModel.containsAmendments ? 0 : -100
            priceDifference.numberOfLines = 0
            priceDifference.attributedText = attributedStringForConfirmButton(with: amendmentsModel.priceDifferenceText)

            UIView.animate(withDuration: .ocd, animations: {
                self.view.layoutIfNeeded()
            })
        }
    }

    func editRoomButtonDidTap(forRoomNumber roomNumber: Int) {
        eventHandler?.editButtonDidTap(forRoomNumber: roomNumber)
    }

    private var processingView: ProcessingView?

     func toggle(is processing: Bool) {
         if processing {
             guard let window = UIApplication.shared.currentWindow() else { return }

             processingView = ProcessingView(frame: window.bounds)
             guard let processingView = processingView else { return }
             window.addSubview(processingView)
         } else {
             UIView.animate(
                 withDuration: .ocd,
                 animations: {
                    self.processingView?.alpha = 0
                },
                 completion: { _ in
                 self.processingView?.removeFromSuperview()
             }
             )
         }
     }
}

extension AmendBookingView: AmendBookingViewProtocol {
    func toggleLock(is processing: Bool) {
        toggle(is: processing)
    }

    func updateViewModel(
        with amendViewModel: AmendBookingViewModel,
        isNewRoomAdded: Bool
    ) {
        canAmend = amendViewModel.amendable
        viewModel = FormekaViewModel(sections: viewModelSections(with: amendViewModel))

        updateContinueSection(with: amendViewModel.amendmentsModel)

        table?.delegate = viewModel
        table?.dataSource = viewModel
        table.reloadData()

        if isNewRoomAdded {
            DispatchQueue.main.async { [weak self] in
                self?.table.scrollToLastRow()
            }
        }
    }

    func set(cancelling: Bool) {
        toggleCancelButtonProcessing(active: cancelling)
    }

    func set(processing: Bool) {
        if processing {
            activityIndicator?.startAnimating()
        } else {
            activityIndicator?.stopAnimating()
        }
    }

    func showAlert(with title: String, and message: String, completion: @escaping () -> Void) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(
            title: PILocalizedString("OK", comment: "OK button title"),
            style: .cancel,
            handler: { _ in
            completion()
        }
        ))

        present(alertController, animated: true, completion: nil)
    }

    func showOptionAlert(
        with title: String,
        and message: String,
        confirmTitle: String?,
        cancelTitle: String?,
        completion: @escaping () -> Void
    ) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let noAction = UIAlertAction(title: cancelTitle ?? PILocalizedString("no"), style: .cancel, handler: nil)
        let yesAction = UIAlertAction(title: confirmTitle ?? PILocalizedString("yes"), style: .destructive, handler: { _ in
            completion()
        })
        alertController.addAction(noAction)
        alertController.addAction(yesAction)
        present(alertController, animated: true, completion: nil)
    }
}

extension AmendBookingView: CancelButtonCellDelegate {
    func cancelButtonDidTap(cell: CancelButtonCell) throws {
        eventHandler?.cancelButtonDidTap()
    }
}

extension AmendBookingView: AddRoomCellDelegate {
    func addRoomCellDidTap(cell: AddRoomCell) {
        eventHandler?.addRoomButtonDidTap()
    }
}
