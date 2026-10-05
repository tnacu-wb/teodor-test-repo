//
//  UpsellsView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 05/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol UpsellsPresenterProtocol {
    func viewIsReady()
    func summaryButtonDidTap()
    func selected(meal: UpsellItem?)
    func allergyInformationDidTap()
    func continueButtonDidTap()
}

class UpsellsViewController: BaseViewController {
    private var setup: Bool = false

    override var screenName: String { PIAnalytics.StateNames.upsells }
    override var screenType: String { eventHandler?.analyticsScope ?? PIAnalytics.StateTypes.bookingFlow }
    override var customParameters: [String: Any]? {
        if let customParams = eventHandler?.analyticsCustomParams {
            return customParams
        }

        let bookingDetails = BookingDetails.sharedInstance
        let criteria = BookingDetails.sharedInstance.criteria
        let checkoutDate = criteria.checkOutDate ?? Date()
        let startDay = criteria.arrivalDate.analyticsDayFormat
        let endDay = checkoutDate.analyticsDayFormat
        let rateCode = bookingDetails.rate?.rateCode()
        // TODO: AB Test to remove when completed - Sticky Extras CTA - Get variant from Adobe Target
        let stickyExtrasCTAVariant = MVTManager.sharedInstance.value(
            forTestWithIdentifier: Constants.stickyExtrasCTAIdentifier
        ) as? String ?? "control"

        let pushToken: String? = (screenType == PIAnalytics.StateTypes.bookingFlow)
        ? AdobeCampaignManager.shared.apnsTokenString
        : ""

        var dict: PIDictionary = [
            PIAnalytics.Keys.productString: ";\(bookingDetails.hotel?.code ?? "")",
            PIAnalytics.Keys.eventsString: "scOpen",
            PIAnalytics.Keys.bfPrepay: bookingDetails.paymentOption == .now ? "Prepay" : "Non-Prepay",
            PIAnalytics.Keys.bfRateCode: rateCode ?? "",
            PIAnalytics.Keys.bfRateDescription: bookingDetails.rate?.name ?? "",
            PIAnalytics.Keys.bfExtrasDescriptions: bookingDetails.getShownUpsellsNamesForAnalytics(),
            PIAnalytics.Keys.bfExtrasShownCodes: bookingDetails.getShownUpsellsIdsForAnalytics(),
            PIAnalytics.Keys.bfCheckInDate: criteria.arrivalDate.analyticsDateFormat,
            PIAnalytics.Keys.bfCheckOutDate: checkoutDate.analyticsDateFormat,
            PIAnalytics.Keys.bfNights: "\(criteria.nights)",
            PIAnalytics.Keys.bfRooms: "\(criteria.rooms.count)",
            PIAnalytics.Keys.bfAdults: "\(criteria.adultsCount)",
            PIAnalytics.Keys.bfChildren: "\(criteria.childrenCount)",
            PIAnalytics.Keys.bfCheckInDay: startDay,
            PIAnalytics.Keys.bfCheckOutDay: endDay,
            PIAnalytics.Keys.bfCheckInOutDay: [startDay, endDay].joined(separator: "-"),
            PIAnalytics.Keys.pushToken: pushToken ?? "",
            PIAnalytics.Keys.bfStickyExtrasCTA: stickyExtrasCTAVariant
        ]

        if let promotionsAnalytics = eventHandler?.promotionsAnalytics {
            dict.mergePreferNew(promotionsAnalytics)
        }

        return dict
    }

    // MARK: - Views

    @IBOutlet weak var loadingSpinner: UIActivityIndicatorView! {
        didSet {
            loadingSpinner.isHidden = true
        }
    }

    @IBOutlet weak var summaryContainer: UIView! {
        didSet {
            summaryContainer.set(hasHeaderFooterShadow: true)
        }
    }
    @IBOutlet weak var summary: UILabel! {
        didSet {
            summary.font = .Body()
        }
    }
    @IBOutlet weak var table: UITableView! {
        didSet {
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 150
            table.backgroundColor = .BaseWhite
            table.tableHeaderView = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: CGFloat.leastNormalMagnitude))
            table.separatorStyle = .none

            registerNibs()
        }
    }
    @IBOutlet weak var totalContainer: UIView! {
        didSet {
            totalContainer.set(hasHeaderFooterShadow: true)
        }
    }
    @IBOutlet weak var total: UILabel! {
        didSet {
            total.font = UIFont.Heading2_Semibold()
        }
    }
    @IBOutlet weak var totalSubButton: UIButton! {
        didSet {
            totalSubButton.contentHorizontalAlignment = .left
            totalSubButton.titleLabel?.font = UIFont.BodySmall()
            totalSubButton.setTitle(PILocalizedString("FooterViewBreakdown"), for: .normal)
        }
    }
    @IBOutlet weak var continueButton: RoundedCornersButton! {
        didSet {
            continueButton.titleLabel?.font = UIFont.Button1()
            continueButton.setTitleColor(.BaseWhite, for: .normal)
            continueButton.backgroundColor = .Tint1
            continueButton.accessibilityLabel = PILocalizedString("upsellsContinueToNextSteps")
        }
    }
    @IBOutlet weak var doneButton: RoundedCornersButton! {
        didSet {
            doneButton.titleLabel?.font = UIFont.Button1()
            doneButton.backgroundColor = .Tint1
            doneButton.setTitleColor(.BaseWhite, for: .normal)
            doneButton.setTitle(PILocalizedString("bookingReviewContinueButtonTitle"), for: .normal)
        }
    }

    // MARK: - Properties

    var eventHandler: UpsellsViewEventHandler?

    private var viewModel: FormekaViewModel?

    var shouldShowStickyCTA: Bool = true

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.accessibilityIdentifier = "tableView"
        }

        title = PILocalizedString("upsellsScreenTitle", comment: "Upsells: screen title")
        configureStickyExtrasCTAVariant()
    }
    // TODO: AB Test to remove when completed - Sticky Extras CTA - Configure UI based on Adobe Target variant
    func configureStickyExtrasCTAVariant() {
        guard table != nil, totalContainer != nil else { return }
        let variant = MVTManager.sharedInstance.value(
            forTestWithIdentifier: Constants.stickyExtrasCTAIdentifier) as? String ?? "control"
        shouldShowStickyCTA = (variant == "control")
        if !shouldShowStickyCTA {
            // TODO: AB Test - Sticky Extras CTA - For variant: hide sticky footer and extend table
            // For variant: hide sticky totalContainer and extend table to bottom
            totalContainer.isHidden = true
            let bottomConstraint = table.constraints.first { constraint in
                constraint.firstItem as? UITableView == table &&
                constraint.firstAttribute == .bottom &&
                constraint.secondItem as? UIView == totalContainer
            }
            bottomConstraint?.isActive = false
            if let parentBottomConstraint = view.constraints.first(where: { constraint in
                (constraint.firstItem as? UITableView == table && constraint.secondItem as? UIView == totalContainer) ||
                (constraint.secondItem as? UITableView == table && constraint.firstItem as? UIView == totalContainer)
            }) {
                parentBottomConstraint.isActive = false
            }
            NSLayoutConstraint.activate([
                table.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor)
            ])
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        eventHandler?.viewIsReady()
        navigationController?.setNavigationBarHidden(false, animated: animated)
    }

    private func registerNibs() {
        table.registerCellNib(with: BookingSummaryCell.self)
        table.registerCellNib(with: NoMealsCell.self)
        table.registerCellNib(with: MealOptionCell.self)
        table.registerCellNib(with: ExtraUpsellCell.self)
        table.registerCellNib(with: MealsInformationCell.self)
        table.registerCellNib(with: BookingReviewAdditionsCell.self)
        table.registerCellNib(with: CarouselCell.self)
        table.registerCellNib(with: SimpleSubtitleCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: FormekaFreeTextCell.self)
        table.registerCellNib(with: NoUpsellChosenCell.self)
        table.registerCellNib(with: NoRestaurantCell.self)
        table.registerCellNib(with: UpsellChosenSummaryCell.self)
        table.registerCellNib(with: CreditCardsListCell.self)
        table.registerCellNib(with: RestaurantLinksCell.self)
        table.registerHeaderFooterNib(with: TitleAndOptionalImageHeader.self)
        table.registerHeaderFooterNib(with: SimpleFooter.self)
        table.registerCellClass(with: BottomBorderCell.self)
    }

    // MARK: - Actions

    @IBAction func breakdownButtonDidTap(_ sender: Any) {
        eventHandler?.summaryButtonDidTap()
    }

    @IBAction func continueButtonDidTap(_ sender: Any) {
        eventHandler?.continueButtonDidTap()
    }

    @objc private func cancelButtonDidTap() {
        eventHandler?.cancelButtonDidTap()
        dismiss(animated: true)
    }

    private var processingView: ProcessingView?

    func lockFullScreen(shouldLock: Bool) {
        if shouldLock {
            guard let window = UIApplication.shared.connectedScenes.compactMap({ ($0 as? UIWindowScene)?.keyWindow }).last
                else { return }

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

extension UpsellsViewController: UpsellsViewProtocol {
    func update(with viewModel: UpsellsViewModel) {
        continueButton.setTitle(viewModel.continueButtonTitle, for: .normal)

        // Done Button or Summary View
        if let totalViewModel = viewModel.totalViewModel {
            doneButton.isHidden = !totalViewModel.showDoneButton
            totalSubButton.isHidden = totalViewModel.showDoneButton
            total.isHidden = totalViewModel.showDoneButton
            summary.isHidden = totalViewModel.showDoneButton
        }

        totalSubButton.setTitle(viewModel.totalViewModel?.summaryBreakdownLabelSetup.text, for: .normal)
        totalSubButton.setTitleColor(viewModel.totalViewModel?.summaryBreakdownLabelSetup.colour, for: .normal)

        total.text = viewModel.totalViewModel?.totalCost?.localizedValue
        total.accessibilityIdentifier = "totalBookingPrice"

        self.viewModel = tableViewModel(with: viewModel)

        navigationItem.title = viewModel.screenName

        table.delegate = self.viewModel
        table.dataSource = self.viewModel

        table.reloadData()

        summaryContainer.isHidden = viewModel.summaryViewModel == nil
        summary.text = viewModel.summaryViewModel?.stayDetails
        summary.accessibilityIdentifier = "customiseYourStayBookingInfo"

        table.contentInset.top = viewModel.summaryViewModel == nil ? 0 : summaryContainer.frame.height

        if !setup {
            setup.toggle()
            table.contentOffset.y = viewModel.summaryViewModel == nil ? 0 : -summaryContainer.frame.height
        }
    }

    func showCancelButton() {
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(cancelButtonDidTap)
        )
    }

    func showSpinnerAndLockScreen() {
        table.isUserInteractionEnabled = false
        table.alpha = 0.5

        // TODO: AB Test - Sticky Extras CTA - For variant, use full screen lock since totalContainer is hidden
        if !shouldShowStickyCTA {
            lockFullScreen(shouldLock: true)
        } else {
            loadingSpinner.isHidden = false
            continueButton.setTitle(PILocalizedString(""), for: .normal)
            loadingSpinner.startAnimating()
        }
    }

    func hideSpinnerAndUnLockScreen() {
        table.isUserInteractionEnabled = true
        table.alpha = 1

        // TODO: AB Test - Sticky Extras CTA - For variant, unlock full screen
        if !shouldShowStickyCTA {
            lockFullScreen(shouldLock: false)
        } else {
            loadingSpinner.isHidden = true
            continueButton.setTitle(PILocalizedString("Continue"), for: .normal)
            loadingSpinner.stopAnimating()
        }
    }

    func showErrorMessage(title: String, message: String, error: Error?, handler: ((UIAlertAction) -> Void)? = nil) {
        showErrorAlertWith(title: title, message: message, error: error, handler: handler)
    }

    func stepperValueDidChange(value: Int, roomIndex: Int, mealId: Int) {
        eventHandler?.numberOfAdultsDidChange(value: value, at: roomIndex, mealCode: mealId)
    }
}

extension UpsellsViewController: CustomStepperCellDelegate {
    func customStepperCellDidChangeValue(cell: CustomStepperCell, value: Int) {}

    func customStepperCellDidFailChangingValue(cell: CustomStepperCell, error: NSError) {}
}
