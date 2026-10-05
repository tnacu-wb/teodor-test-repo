//
//  ReviewAndBookView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

protocol ReviewAndBookViewProtocol: AnyObject {
    func loadViewModel(with bookingDetails: BookingDetails)
    func trackPaymentOptionsState(with params: PIDictionary)
    func stopEditing()
    func validateForm() throws -> PIDictionary?
    func showError(title: String, message: String?)
    func showErrorMessage(title: String, message: String, error: Error?, handler: ((UIAlertAction) -> Void)?)
    func showTimeoutErrorAlert(title: String, message: String, customButton: String?, shouldShowFailureButton: Bool)
    func showGenericErrorAlert(title: String, message: String, shouldShowFailureButton: Bool)
    func showStorageFailureError(title: String, message: String, customButton: String, shouldShowFailureButton: Bool)
    func showPollingFinishedError(
        title: String,
        message: String,
        shouldShowFailureButton: Bool,
        accessibilityButtonLabel: String?
    )
    func showRowError(_ error: RowValidatorError)
    func showErrorUI(_ ui: ErrorUI)
    func showPayOnArrivalMessage(title: String, message: String, completion: @escaping () -> Void)
    func scrollToAtosPasswordRow() -> Bool
    func showNoMoreAvailabilityMessage(title: String, message: String)
    func provideSuccessFeedback()
    func startDisplayingLoadingElements()
    func stopDisplayingLoadingElements()

    // confirmation polling
    func startConfirmationPolling()
    func stopConfirmationPolling()
}

enum ReviewAndBookRow: String {
    case paymentOption
    case legal
    case cvv
    case cvvInfo
    case confirm
    case paymentCard
    case paymentAuthInfo
    case paymentTimeMessage
    case paymentTimeMessage3C
    case cancellationMessage
    case ccChargeMessage
    case expiryError
    case showPaymentMessagesDottedSeparator
    case dottedSeparatorCell
    case cnpToggle
    case memorableWord = "atosPassword"
    case memorableWordInfo
    case dinnerAllowanceDottedSeparator
    case dinnerAllowanceToggle
    case dinnerAllowanceDescription
    case dinnerAllowanceBudget = "dinnerAllowance"
    case dinnerAllowanceIncludeAlcohol = "alcoholAllowed"
    case parkingIncluded = "carParkingAllowed"
    case wifiIncluded = "wifiAccessAllowed"
    case donation
    case donationSummary
    case totalCost
    case cityTax
    case parkingAllowanceInfo
    case dinnerAllowanceInfo
    case dinnerAndParkingAllowanceInfo
    case invalidOptionsMessage3C
    case addressTypeBooker
    case thirdPartyInfoMessage
}

extension PaymentIntervalOption: FormekaValue {
    public var displayName: String { String(describing: self) }
}

class ReviewAndBookViewController: FormekaViewController {
    var activityIndicator: UIActivityIndicatorView!

    private var confirmationActivityIndicator: UIActivityIndicatorView!
    private let viewForActivityIndicator = UIView()
    private let loadingTextLabel = UILabel()

    private var shownConfirmationPollingMessageIndex = 0
    private let confirmationPollingMessagesConfig: ConfirmationPollingMessagesConfig? = SettingsManager.sharedInstance
        .confirmationPollingMessagesConfig

    override var screenName: String { PIAnalytics.StateNames.review }
    override var screenType: String { PIAnalytics.StateTypes.bookingFlow }
    override var customParameters: [String: Any]? {
        let bookingDetails = BookingDetails.sharedInstance
        let analyticsManager = AnalyticsManager.shared
        let criteria = BookingDetails.sharedInstance.criteria
        let checkoutDate = criteria.checkOutDate ?? Date()
        let startDay = criteria.arrivalDate.analyticsDayFormat
        let endDay = checkoutDate.analyticsDayFormat
        let rateCode = bookingDetails.rate?.rateCode()

        var params: PIDictionary = [
            PIAnalytics.Keys.productString: ";\(bookingDetails.hotel?.code ?? "")",
            PIAnalytics.Keys.bfRateCode: rateCode ?? "",
            PIAnalytics.Keys.bfRateDescription: bookingDetails.rate?.description ?? "",
            PIAnalytics.Keys.bfCheckInDate: criteria.arrivalDate.analyticsDateFormat,
            PIAnalytics.Keys.bfCheckOutDate: checkoutDate.analyticsDateFormat,
            PIAnalytics.Keys.bfNights: "\(criteria.nights)",
            PIAnalytics.Keys.bfRooms: "\(criteria.rooms.count)",
            PIAnalytics.Keys.bfAdults: "\(criteria.adultsCount)",
            PIAnalytics.Keys.bfChildren: "\(criteria.childrenCount)",
            PIAnalytics.Keys.bfCheckInDay: startDay,
            PIAnalytics.Keys.bfCheckOutDay: endDay,
            PIAnalytics.Keys.bfCheckInOutDay: [startDay, endDay].joined(separator: "-"),
            PIAnalytics.Keys.bfExtrasSelectedDescriptions: bookingDetails.selectedUpsellsForAnalytics
        ]

        if let promotionsAnalytics = analyticsManager.getPromotionsAnalyticsDict(with: bookingDetails) {
            params.mergePreferNew(promotionsAnalytics)
        }

        return params
    }

    var presenter: ReviewAndBookPresenterProtocol?
    var termsAndConditionEnabled = false
    var persistedCompanyName: String?

    private(set) var addressSectionView: AddressSectionView?

    var paymentMethodsSectionFooter: FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 10, viewSetup: { _, table in
            let view: SimpleFooter? = table.headerFooterView()
            view?.contentView.backgroundColor = .BaseWhite
            view?.lineView.backgroundColor = .TintL2

            return view
        })
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.backgroundColor = .BaseWhite
            table.separatorColor = .ColourLD3
            table.separatorStyle = .none
            table.tableHeaderView = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: CGFloat.leastNormalMagnitude))
            table.tableFooterView = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: CGFloat.leastNormalMagnitude))
            table.accessibilityIdentifier = "tableView"

            registerTableElements()
        }

        title = PILocalizedString("reviewScreenTitle", comment: "Review and Book: screen title")

        setupActivityIndicator()

        presenter?.viewIsReady()
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

    private func registerTableElements() {
        table.registerCellNib(with: ReviewAndBookSummaryCell.self)
        table.registerCellNib(with: SimpleSubtitleCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: PaymentCardCell.self)
        table.registerCellNib(with: FormekaSegmentedControlCell.self)
        table.registerCellNib(with: FormekaFreeTextCell.self)
        table.registerCellNib(with: BookingReviewAdditionsCell.self)
        table.registerCellNib(with: DottedSeparatorCell.self)
        table.registerCellNib(with: HotelNotesCell.self)
        table.registerCellNib(with: CVVInputCell.self)
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: SwitchCell.self)
        table.registerCellNib(with: FullsizeTextFieldCell.self)
        table.registerCellNib(with: CostInputCell.self)
        table.registerCellNib(with: ErrorCell.self)
        table.registerCellNib(with: TermsAndConditionsCell.self)
        table.registerCellNib(with: GoshCell.self)
        table.registerHeaderFooterNib(with: ActionableHeader.self)
        table.registerHeaderFooterNib(with: FormekaHeader.self)
        table.registerHeaderFooterNib(with: SimpleFooter.self)
        table.registerCellClass(with: SimpleSeparatorsCell.self)
        table.registerCellClass(with: BottomBorderCell.self)
        table.registerCellNib(with: DynamicListCell.self)
        table.registerCellNib(with: FormekaPostCodeCell.self)
        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerCellNib(with: CostWithPayButtonCell.self)
    }
}

extension ReviewAndBookViewController: ReviewAndBookViewProtocol {
    func loadViewModel(with bookingDetails: BookingDetails) {
        viewModel = viewModel(with: bookingDetails)
        viewModel?.delegate = self

        table.delegate = viewModel
        table.dataSource = viewModel

        table.reloadData()
    }

    func trackPaymentOptionsState(with params: PIDictionary) {
        var mutableParams = params
        mutableParams[PIAnalytics.Keys.eventsString] = "scCheckout"
        mutableParams[PIAnalytics.Keys.pushToken] = AdobeCampaignManager.shared.apnsTokenString
        trackState(
            withName: PIAnalytics.StateNames.cardDetails,
            type: PIAnalytics.StateTypes.bookingFlow,
            additionalData: mutableParams
        )
    }

    func stopEditing() {
        view.endEditing(true)
    }

    func toggleSubmitButton(enabled: Bool) {
        guard let row = viewModel?.row(named: ReviewAndBookRow.confirm.rawValue) else { return }
        guard let indexPath = viewModel?.indexPath(for: row) else { return }
        guard let cell = table.cellForRow(at: indexPath) as? BookingReviewSubmitCell else { return }

        if enabled {
            cell.activityIndicator.stopAnimating()
        } else {
            cell.activityIndicator.startAnimating()
        }

        navigationItem.hidesBackButton = !enabled
        cell.continueButton.isEnabled = enabled
    }

    func validateForm() throws -> PIDictionary? {
        try viewModel?.validate()

        return viewModel?.values
    }

    func showError(title: String, message: String?) {
        showAlertWith(title: title, message: message)
    }

    func showRowError(_ error: RowValidatorError) {
        scrollAndFocus(at: viewModel?.indexPath(for: error.row))
    }

    func showErrorUI(_ ui: ErrorUI) {
        guard ui.presentationStyle != .customAlert else {
            showGenericErrorAlert(
                title: ui.title ?? PILocalizedString("timeoutErrorTitle", comment: "Timeout error title"),
                message: ui.description ?? "",
                shouldShowFailureButton: false
            )
            return
        }

        let controller = UIAlertController(title: ui.title, message: ui.description, preferredStyle: .alert)

        if let actions = ui.actions {
            actions.forEach { controller.addAction($0) }
        } else {
            controller.addAction(UIAlertAction(
                title: PILocalizedString("OK", comment: "Generic ok string"),
                style: .cancel,
                handler: nil
            ))
        }

        present(controller, animated: true)
        return
    }

    func showTimeoutErrorAlert(title: String, message: String, customButton: String?, shouldShowFailureButton: Bool) {
        let controller = TimeoutErrorAlertController(title: nil, message: nil, preferredStyle: .alert)
        controller.headingText = title
        controller.messageText = message
        controller.controllerOutput = self
        if let button = customButton {
            controller.messageButton = button
        }

        if shouldShowFailureButton {
            controller.controllerSetup = { viewController in
                viewController.optionalCTAButton?.setTitle(
                    PILocalizedString("paymentFailureClickToAction", comment: "Payment failure: click to action"),
                    for: .normal
                )
                viewController.optionalCTAButton?.backgroundColor = .Tint1
                viewController.optionalCTAButton?.setTitleColor(.BaseWhite, for: .normal)
                viewController.optionalCTAButton?.isHidden = false
                viewController.optionalCompletion = { [weak self] controller in
                    controller.dismiss(animated: true) {
                        self?.presenter?.paymentFailureButtonDidTap()
                    }
                }
            }
        }

        present(controller, animated: true)
    }

    func showStorageFailureError(title: String, message: String, customButton: String, shouldShowFailureButton: Bool) {
        showTimeoutErrorAlert(
            title: title,
            message: message,
            customButton: customButton,
            shouldShowFailureButton: shouldShowFailureButton
        )
    }

    func showPollingFinishedError(
        title: String,
        message: String,
        shouldShowFailureButton: Bool,
        accessibilityButtonLabel: String?
    ) {
        let controller = TimeoutErrorAlertController(title: nil, message: nil, preferredStyle: .alert)
        controller.headingText = title
        controller.messageText = message
        controller.messageButton = PILocalizedString("Close")
        controller.accessibilityButtonLabel = accessibilityButtonLabel
        controller.controllerOutput = self
        controller.controllerSetup = { viewController in
            viewController.overrideCtaCompletion = { [weak self] controller in
                controller.dismiss(animated: true) {
                    self?.presenter?.cancelRateUpdateButtonDidTap()
                }
            }
        }

        present(controller, animated: true)
    }

    func showGenericErrorAlert(title: String, message: String, shouldShowFailureButton: Bool) {
        showTimeoutErrorAlert(
            title: title,
            message: message,
            customButton: nil,
            shouldShowFailureButton: shouldShowFailureButton
        )
    }

    func showErrorMessage(title: String, message: String, error: Error?, handler: ((UIAlertAction) -> Void)?) {
        showErrorAlertWith(title: title, message: message, error: error, handler: handler)
    }

    func showPayOnArrivalMessage(title: String, message: String, completion: @escaping () -> Void) {
        let controller = PaymentAlertController(title: nil, message: nil, preferredStyle: .alert)
        controller.headingText = title
        controller.messageText = message
        controller.controllerSetup = { contentController in
            contentController.ctaButton.setTitle(
                PILocalizedString("paymentAlertClickToAction", comment: "Payment alert: click to action"),
                for: .normal
            )
            contentController.ctaButton.setTitleColor(.white, for: .normal)
            contentController.ctaButton.backgroundColor = .BasePurple
        }
        controller.completion = {
            controller.dismiss(animated: true) {
                completion()
            }
        }

        present(controller, animated: true)
    }

    func showNoMoreAvailabilityMessage(title: String, message: String) {
        let controller = PaymentAlertController(title: nil, message: nil, preferredStyle: .alert)
        controller.headingText = title
        controller.messageText = message
        controller.controllerSetup = { contentController in
            contentController.ctaButton.setTitle(
                PILocalizedString(
                    "noMoreAvailabilityAlertClickToAction",
                    comment: "No more availability alert click to action"
                ),
                for: .normal
            )
            contentController.ctaButton.setTitleColor(.white, for: .normal)
            contentController.ctaButton.backgroundColor = .BasePurple
        }
        controller.completion = {
            controller.dismiss(animated: true) {
                self.presenter?.noMoreAvailabilityButtonDidTap()
            }
        }

        present(controller, animated: true)
    }

    func scrollToAtosPasswordRow() -> Bool {
        guard let row = viewModel?.row(named: ReviewAndBookRow.memorableWord.rawValue) else { return false }
        guard let indexPath = viewModel?.indexPath(for: row) else { return false }

        row.error = RowValidatorError(
            row: row,
            error: ValidationError.customMessage(PILocalizedString(
                "We do not recognise your password or memorable word",
                comment: ""
            ))
        )

        table.scrollToRow(at: indexPath, at: .top, animated: true)

        return true
    }

    func provideSuccessFeedback() {
        NotificationFeedbackManager.shared.provideFeedback(for: .success)
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

    private func advanceToNextConfirmationPollingMessage() {
        guard shownConfirmationPollingMessageIndex + 1 < confirmationPollingMessagesConfig?.messages.count ?? 0
            else { return }

        shownConfirmationPollingMessageIndex += 1

        guard let nextMessage = confirmationPollingMessagesConfig?.messages[safe: shownConfirmationPollingMessageIndex]?
              .message else { return }

        loadingTextLabel.text = nextMessage

        scheduleNextConfirmationPollingMessage()
    }

    private func scheduleNextConfirmationPollingMessage() {
        guard let messageDuration = confirmationPollingMessagesConfig?.messages[safe: shownConfirmationPollingMessageIndex]?
              .seconds else { return }

        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(messageDuration)) { [self] in
            advanceToNextConfirmationPollingMessage()
        }
    }
}

private extension ReviewAndBookViewController {
    func viewModel(with bookingDetails: BookingDetails) -> FormekaViewModel {
        let sections = cccpPaymentSections(with: bookingDetails)

        return FormekaViewModel(sections: sections)
    }

    private func cccpPaymentSections(with bookingDetails: BookingDetails) -> [FormekaModelSection] {
        addressSectionView = {
            let addressRequirements = AddressSectionRequirements(
                address: storedQuickAddress,
                storedAddress: storedQuickAddress,
                shouldShowAddressSwitch: true,
                useStoredAddressSwitchDescription: quickAddressDecription.string,
                addressSwitchInitialState: true,
                shouldShowAddressForm: false,
                shouldShowHeader: true,
                shouldShowFooter: false,
                shouldShowAddressSummary: false
            )

            let view = AddressSectionRouter.buildSection(with: addressRequirements)
            view.parentFormekaViewController = self
            view.addressSectionSwitchDelegate = self
            view.segmentedControlDelegate = self
            view.companyNameDelegate = self
            return view
        }()

        var sections = [FormekaModelSection]()

        sections.append(summarySection(with: bookingDetails))
        sections.append(guestsSection(bookingDetails: bookingDetails))
        if let section = businessCardQuestionsAndAnswersSection(bookingDetails: bookingDetails) {
            sections.append(section)
        }
        sections.append(contentsOf: cccpPaymentMethodsSections(
            bookingDetails: bookingDetails,
            and: bookingDetails.paymentMethodsViewModel
        ))
        if let goshSection = goshSection(bookingDetails: bookingDetails) {
            sections.append(goshSection)
        }
        sections.append(totalSection(bookingDetails: bookingDetails))

        return sections
    }

    private var quickAddressDecription: NSAttributedString {
        let quickAddressParams: (prefix: String, postcode: String) = {
            switch BookingDetails.sharedInstance.bookingMode {
            case .business:
                return (PILocalizedString("userDetailsSameAsCompanyAddress"), storedQuickAddress?.postcode ?? "")
            case .leisure:
                return (PILocalizedString("userDetailsSameAsBookersAddress"), storedQuickAddress?.line1 ?? "")
            }
        }()

        let quickAddress = "\(quickAddressParams.prefix) (\(quickAddressParams.postcode))"
        let mutableQuickAddress = NSMutableAttributedString(string: quickAddress)

        if let range = quickAddress.ranges(of: quickAddressParams.postcode).first {
            mutableQuickAddress.addAttribute(.font, value: UIFont.Body_Semibold(), range: range)
        }
        return NSAttributedString(attributedString: mutableQuickAddress)
    }

    private var storedQuickAddress: Address? {
        switch BookingDetails.sharedInstance.bookingMode {
        case .business:
            return UserSessionManager.sharedInstance.currentUser?.company?.companyDetails?.companyAddress
        case .leisure:
            return BookingDetails.sharedInstance.booker?.address
        }
    }
}

extension ReviewAndBookViewController: FormekaSegmentedControlCellDelegate, CompanyNameDelegate {
    func formekaSegmentedControlValueDidChange(cell: FormekaSegmentedControlCell) {
        guard let indexPath = table.indexPath(for: cell) else { return }
        guard let row = viewModel?.row(at: indexPath) else { return }

        switch ReviewAndBookRow(rawValue: row.tag) {
        case .paymentOption?:
            let paymentOption: PaymentIntervalOption = cell.segmentedControl.selectedSegmentIndex == 0 ? .later : .now

            row.value = paymentOption
            updatePaymentTimeMessage(for: paymentOption)

        case .addressTypeBooker?:

            let addressType: AddressType = (cell.segmentedControl.selectedSegmentIndex == 0) ? .home : .commercial

            row.value = addressType

            // Add addition
            switch addressType {
            case .commercial:
                let newIndexPath = IndexPath(item: indexPath.row + 1, section: indexPath.section)
                let textFieldRow = textFieldRow(
                    name: GuestDetailsRow.companyName.rawValue,
                    title: PILocalizedString("userDetailsCompanyName", comment: "User details form: company name label"),
                    value: persistedCompanyName,
                    inlineValidators: [.companyName],
                    onBlurValidators: [.required, StringLengthValidator(range: 1...40)]
                )
                viewModel?.add(row: textFieldRow, at: newIndexPath)
                table.insertRows(at: [newIndexPath], with: .automatic)

            default:
                persistedCompanyName = viewModel?.row(named: GuestDetailsRow.companyName.rawValue)?.value as? String
                if let removedIndexPath = viewModel?.remove(rowNamed: GuestDetailsRow.companyName.rawValue) {
                    table.deleteRows(at: [removedIndexPath], with: .automatic)
                }
            }

        default:
            return
        }
    }

    func removePersistedCompanyName() {
        persistedCompanyName = nil
    }
}

extension ReviewAndBookViewController: TimeoutErrorAlertControllerOutput {
    func tryAgainButtonDidTap(sender: UIViewController) {
        sender.dismiss(animated: true)
    }
}

extension ReviewAndBookViewController: AddressSectionSwitchDelegate {
    func useBookerAddressSwitchValueDidChange(to value: Bool) {
        // presenter?.useBookerAddressSwitchDidChange(value: value)
    }
}

// MARK: Confirmation spinner

extension ReviewAndBookViewController {
    private func setupConfirmationActivityIndicatorConstraints() {
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

extension ReviewAndBookViewController: GoshCellDelegate {
     func goshDonationButtonDidSelect(cell: GoshCell, selectedIndex: Int?) {
         if let selectedIndex = selectedIndex {
             BookingDetails.sharedInstance.goshDonation = BookingDetails.sharedInstance.goshOptions?
                 .packagesOrdered?[safe: selectedIndex]?.cost
             BookingDetails.sharedInstance.selectedGoshPackage = BookingDetails.sharedInstance.goshOptions?
                 .packagesOrdered?[safe: selectedIndex]?.code
         } else {
             BookingDetails.sharedInstance.goshDonation = nil
             BookingDetails.sharedInstance.selectedGoshPackage = nil
         }

         guard let viewModel = viewModel else { return }

         if viewModel.row(named: ReviewAndBookRow.donationSummary.rawValue) == nil {
             guard let row = donationRow(bookingDetails: BookingDetails.sharedInstance) else { return }
             guard let indexPath = viewModel.indexPath(forRowNamed: ReviewAndBookRow.totalCost.rawValue) else { return }

             let newIndexPath = IndexPath(row: indexPath.row - 1, section: indexPath.section)
             viewModel.add(row: row, at: newIndexPath)

             table.insertRows(at: [newIndexPath], with: .none)
         }

         guard let totalCostCell: BookingReviewTotalPriceCell = viewModel.cell(
             forRowNamed: ReviewAndBookRow.totalCost.rawValue,
             table: table
         ) else {
             table.reloadData()
             return
         }

         totalCostCell.totalPriceLabel.pop(with: .heavy) { [unowned self] in
             table.reloadData()
         }
     }
 }
