//
//  HotelDetailsViewController.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import MapKit
import MessageUI

final class HotelDetailsViewController: BaseViewController {
    // MARK: - Views

    // 🏠 Base View Controller Properties
    override var screenName: String { presenter.screenName }
    override var screenType: String { presenter.screenType }
    override var trackScreen: Bool { presenter.shouldTrackScreen }

    override var prefersStatusBarHidden: Bool { !shouldShowStatusBar }
    override var reachabilityViewPositionY: CGFloat { fakeNavigationBar.frame.height }
    override var reachabilityViewContainer: UIView { fakeNavigationBar }
    override var hidesBottomBarWhenPushed: Bool {
        get {
            true
        }
        set { }
    }
    override var preferredStatusBarUpdateAnimation: UIStatusBarAnimation { .fade }

    // 🛍 Outlets
    @IBOutlet weak var continueView: UIView!
    @IBOutlet weak var continueViewTopConstraint: NSLayoutConstraint! {
        didSet {
            continueViewTopConstraint.constant = 0
        }
    }
    @IBOutlet weak var continueViewHeightConstraint: NSLayoutConstraint! {
        didSet {
            let safeAreaBottom: CGFloat = UIApplication.shared.currentWindow()?.safeAreaInsets.bottom ?? 0.0
            continueViewHeightConstraint.constant = (continueViewHeightConstraint.constant + safeAreaBottom)
        }
    }

    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var backButton: RoundedCornersButtonBiggerTapArea! {
        didSet {
            // This asset could move into the view model
            backButton.setImage(Style.HotelDetails.backButtonImage, for: .normal)
        }
    }
    @IBOutlet weak var fakeBackground: UIView!
    @IBOutlet weak var fakeNavigationBar: UIView!
    @IBOutlet weak var backButtonLabel: UIButton!
    @IBOutlet weak var fakeNavigationBarTitle: UILabel!
    @IBOutlet weak var backButtonTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var backButtonLeftConstraint: NSLayoutConstraint!
    @IBOutlet weak var fakeNavigationBarTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var bookNowButton: FadeOnHighlightButton! {
        didSet {
            bookNowButton.backgroundColor = .Tint1
            bookNowButton.setTitleColor(.BaseWhite, for: .normal)
        }
    }
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    private let statusBarUnderlay = UIView()

    // MARK: - Properties

    var presenter: HotelDetailsPresenterProtocol
    var eventHandler: HotelDetailsEventHandler
    var viewModel: HotelDetailsViewModel?

    private var defaultAccessoryButtonTopConstraint: CGFloat = 0

    var titleView: UIView = UIView()

    var currentRoomSectionIndex = 0 {
        didSet {
            let imageTag: ImageTag = {
                switch currentRoomSectionIndex {
                case 1:
                    return .hubBiggerRoom
                case 2:
                    return .hubAccessibleRoom
                default:
                    return .hubStandardRoom
                }
            }()

            eventHandler.roomSectionIndexDidChange(to: imageTag)
        }
    }
    var currentFoodSectionIndex = 0
    var searchIndex: Int?
    var cachedMapImage: UIImage?
    var hotelCode: String?
    var shouldShowStatusBar = true {
        didSet {
            UIView.animate(withDuration: .ocd) {
                self.setNeedsStatusBarAppearanceUpdate()
            }
        }
    }
    var navBarAnimationHelper = AnimationHelper()
    var currentImageIndex = 0
    var tableViewModel: FormekaViewModel?

    lazy var cachedRgbaForPIPurple = UIColor.BasePurple.rgba

    // MARK: - Lifecycle

    init(presenter: HotelDetailsPresenter, and eventHandler: HotelDetailsEventHandler) {
        self.presenter = presenter
        self.eventHandler = eventHandler

        super.init(nibName: String(describing: HotelDetailsViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = nil

        setupUI()

        tableView.accessibilityIdentifier = "hotelDetailPageTableAcc"

        view.clipsToBounds = true

        navigationItem.backBarButtonItem = UIBarButtonItem(
            title: PILocalizedString("hotelDetailsBackButton", comment: "Hotel details: back button title"),
            style: .plain,
            target: nil,
            action: nil
        )

        eventHandler.viewDidFinishLoading()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        if UIDevice.current.userInterfaceIdiom != .pad {
            backButton.isHidden = true
            navigationController?.setNavigationBarHidden(false, animated: animated)
        } else {
            backButtonLeftConstraint?.constant = view.frame.size.width - 20 - backButton.frame.size.width
        }

        updateHotelImageCarouselActiveImage()
        eventHandler.viewIsAppearing()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        updateStatusBarUI()
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()

        statusBarUnderlay.backgroundColor = .BasePurple

        let statusBarFrame = view?.window?.windowScene?.statusBarManager?.statusBarFrame ?? CGRect()
        statusBarUnderlay.frame = CGRect(origin: CGPoint(
            x: 0,
            y: -statusBarFrame.height
        ), size: statusBarFrame.size)
        navigationController?.navigationBar.addSubview(statusBarUnderlay)
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        if viewModel?.shouldShowRatesSection ?? false {
            let bottomInset = continueView.frame.height
            tableView.contentInset.bottom = bottomInset
            tableView.verticalScrollIndicatorInsets.bottom = bottomInset
        }

        if defaultAccessoryButtonTopConstraint <= 0 {
            let rect = tableView.rectForRow(at: IndexPath(row: 0, section: 0))

            defaultAccessoryButtonTopConstraint = rect.maxY

            navBarAnimationHelper.pointsBeforeTriggeringAnimation = fakeNavigationBar.frame.height + view.safeAreaInsets.top
            navBarAnimationHelper.animationLength = rect.height - (fakeNavigationBar.frame.height + view.safeAreaInsets.top)
                .doubled
            navBarAnimationHelper.pointsBeforeTriggeringButtonsAnimation = rect
                .height - (fakeNavigationBar.frame.height + view.safeAreaInsets.top)
        }
    }

    // MARK: - Actions

    @IBAction private func backButtonDidTap(_ sender: UIButton) {
        eventHandler.backButtonDidTap()
    }

    @IBAction func seePricesButtonDidTap(_ sender: AnyObject) {
        guard let rateIndex: IndexPath = tableViewModel?.indexPath(forRowNamed: HotelDetailRow.rateCell.rawValue)
            else { return }
        tableView.scrollToRow(at: rateIndex, at: .middle, animated: true)
    }

    private func hotelDidLoad() {
    }

    private func updateHotelImageCarouselActiveImage() {
        guard let row = tableViewModel?.row(named: HotelDetailRow.hotelDetailCarouselCell.rawValue) else { return }
        guard let indexPath = tableViewModel?.indexPath(for: row) else { return }
        guard let cell = tableView.cellForRow(at: indexPath) as? CarouselCell else { return }

        let imageIndexPath = IndexPath(item: currentImageIndex, section: 0)

        guard currentImageIndex < cell.collectionView.numberOfItems(inSection: 0) else { return }

        cell.collectionView.scrollToItem(at: imageIndexPath, at: .centeredHorizontally, animated: false)
    }

    private func showEmailPopup() {
        guard MFMailComposeViewController.canSendMail() else {
            let controller = UIAlertController(
                title: PILocalizedString("sendFeedbackErrorAlertTitle", comment: "Send feedback error alert title"),
                message: PILocalizedString("sendFeedbackErrorAlertMessage", comment: "Send feedback error alert message"),
                preferredStyle: .alert
            )
            controller.addAction(UIAlertAction(
                title: PILocalizedString("sendFeedbackErrorAlertAction", comment: "Send feedback error alert action"),
                style: .cancel,
                handler: nil
            ))

            present(controller, animated: true)
            return
        }

        let controller = MFMailComposeViewController()
        controller.mailComposeDelegate = self
        controller.navigationBar.tintColor = .BaseWhite
        controller.setToRecipients([Constants.accessibleContactCenterEmailAddress])

        present(controller, animated: true)
    }
}

extension HotelDetailsViewController: HotelDetailsViewProtocol {
    func showNotAllowedToBookPrompt() {}

    func showAlert(
        with title: String,
        message: String,
        confirmTitle: String,
        cancelTitle: String,
        confirmAction: @escaping (() -> Void)
    ) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: confirmTitle, style: .default, handler: { _ in
            confirmAction()
        }))
        alertController.addAction(UIAlertAction(title: cancelTitle, style: .cancel, handler: nil))

        present(alertController)
    }

    func updateViewModelTo(_ viewModel: HotelDetailsViewModel?) {
        guard let newViewModel = viewModel else { return }
        self.viewModel = newViewModel
        self.updateHotelAndAvailability(toAvailable: self.viewModel?.titleAndSummaryViewModel.isHotelNotAvailable == false)

        (titleView.subviews.first(where: { $0 is UILabel }) as? UILabel)?.text = newViewModel.titleAndSummaryViewModel
            .hotelName
    }

    func updateRoomImage(withDesiredRoomIndex desiredRoomIndex: Int) {
        guard let photosIndexPath = tableViewModel?.indexPath(forRowNamed: HotelDetailRow.hotelRoomPhotosCell.rawValue)
            else { return }
        guard let cell: CarouselCell = tableView.dequeueCell(for: photosIndexPath) else { return }
        guard desiredRoomIndex < cell.collectionView.numberOfItems(inSection: 0) else { return }

        cell.collectionView.scrollToItem(at: IndexPath(item: desiredRoomIndex, section: 0), at: .left, animated: false)
    }

    func updateHotelAndAvailability(toAvailable available: Bool) {
        if tableView != nil {
            // This bit is everything....
            tableViewModel = tableViewModelScructure
            tableViewModel?.delegate = self

            tableView.delegate = tableViewModel
            tableView.dataSource = tableViewModel

            tableView.reloadData()
        }

        fakeNavigationBarTitle.text = title
        bookNowButton.setTitle(
            PILocalizedString("hotelDetailsBookButtonTitle", comment: "Hotel details: book button title"),
            for: .normal
        )
        bookNowButton.isEnabled = available

        bookNowButton.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.hotelDetailsSelectRateButton

        setNeedsStatusBarAppearanceUpdate()

        if available == false {
            trackState(withName: PIAnalytics.StateNames.hotelDetailsUnavailable)
        }
    }

    func startDisplayingLoadingElements() {
        activityIndicator.startAnimating()
        tableView.isUserInteractionEnabled = false
        tableView.alpha = 0.5
        bookNowButton.isEnabled = false
    }

    func stopDisplayingLoadingElements() {
        activityIndicator.stopAnimating()
        tableView.isUserInteractionEnabled = true
        tableView.alpha = 1
        bookNowButton.isEnabled = true
    }

    func hotelUpdateFailed(with error: Error) {
        stopDisplayingLoadingElements()
        showErrorAlertWith(
            title: PILocalizedString("somethingWentWrongMessage"),
            message: PILocalizedString("ciolCheckErrorMessage"),
            error: nil
        )
        BARTDowntimeHandler.handle(error: error, withParentNavigationController: self.navigationController)
    }

    func present(_ viewController: UIViewController) {
        present(viewController, animated: true)
    }

    func push(_ viewController: UIViewController) {
        navigationController?.pushViewController(viewController, animated: true)
    }

    func showDirectionsScreen(with viewModel: DirectionsViewModel) {
        showMapsDirectionsOptions(with: viewModel)
    }

    func openPrivacyPolicy() {
        openPrivacyPolicyExternalLink()
    }

    func callHotel(withPhoneNumber phoneNumber: String) {
        showCallHotelAlert(number: phoneNumber)
    }

    func emailCustomerService() {
        self.showEmailPopup()
    }

    func dismissCurrentOverlay() {
        dismiss(animated: true)
    }

    func scrollTo(rateSection: RateSection) {
        currentRoomSectionIndex = rateSection.rawValue

        guard let indexPath = tableViewModel?.indexPath(forRowNamed: HotelDetailRow.roomSegmentsCell.rawValue)
            else { return }

        tableView.scrollToRow(at: indexPath, at: .middle, animated: true)
    }

    func provideHapticFeedback() {
        NotificationFeedbackManager.shared.provideLightTapFeedback()
    }

    func scrollTo(roomTypesSection: Int) {
        guard let indexPath = tableViewModel?.indexPath(forRowNamed: HotelDetailRow.roomContentCell.rawValue) else { return }

        tableView.scrollToRow(at: indexPath, at: .middle, animated: true)
    }

    func scrollToTripAdvisorSection() {
        guard let indexPath = tableViewModel?.indexPath(forRowNamed: HotelDetailRow.tripAdvisorDetails.rawValue)
            else { return }

        tableView.scrollToRow(at: indexPath, at: .middle, animated: true)
    }
}

extension HotelDetailsViewController {
    private func setupUI() {
        continueView.layer.shadowOpacity = 0.5
        continueView.layer.shouldRasterize = true
        continueView.layer.rasterizationScale = UIScreen.main.scale
        continueView.layer.shadowOffset = .zero
        continueView.layer.shadowColor = UIColor.BaseBlack.cgColor
        continueView.layer.shadowRadius = 2

        tableView.contentInsetAdjustmentBehavior = .never
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 150
        tableView.tableHeaderView = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: CGFloat.leastNormalMagnitude))
        tableView.tableFooterView = nil
        tableView.backgroundColor = .TintL5

        registerCellsFor(tableView)

        fakeNavigationBarTopConstraint.constant = -fakeNavigationBar.frame.height
        bookNowButton.backgroundColor = UIColor.Tint1
        backButtonLabel.alpha = 0
        fakeBackground.backgroundColor = UIColor.BasePurple
        backButton.minimumHitArea = CGSize(width: 60, height: 60)

        guard UIDevice.current.userInterfaceIdiom != .pad else { return }

        titleView.frame = CGRect(x: 0, y: 0, width: 220, height: 40)
        titleView.clipsToBounds = true

        let label = UILabel(frame: titleView.bounds)
        label.frame.origin.y = 100
        label.textAlignment = .center
        label.font = UIFont.Heading4_Semibold()
        label.textColor = currentNavigationTheme.foregroundColor

        titleView.addSubview(label)

        navigationItem.titleView = titleView
    }

    private func updateStatusBarUI() {
        shouldShowStatusBar = true
    }

    func updateUI() {
        hotelDidLoad()
    }

    func updateHotelAndAvailability() {
        hotelDidLoad()

        eventHandler.viewIsAppearing()
    }

    func logOutButtonClicked() {
        let controller = UIAlertController(
            title: PILocalizedString("hotelDetailsBBLogoutPopupTitle"),
            message: PILocalizedString("hotelDetailsBBLogoutPopupMessage"),
            preferredStyle: .alert
        )
        controller.addAction(UIAlertAction(title: PILocalizedString("Cancel"), style: .cancel, handler: nil))
        controller.addAction(UIAlertAction(
            title: PILocalizedString("Confirm"),
            style: .default,
            handler: { [unowned self] (_) in
            eventHandler.logoutButtonDidTap()
        }
        ))

        present(controller, animated: true)
    }
}


extension HotelDetailsViewController: HotelInformationSegmentsCellDelegate {
    func hotelInformationSegmentsCellSegmentDidChange(cell: HotelInformationSegmentsCell) {
        guard let segmentControl = cell.segmentControl else { return }
        guard let indexPath = tableView.indexPath(for: cell) else { return }
        guard let row = tableViewModel?.row(at: indexPath) else { return }

        switch HotelDetailRow(rawValue: row.tag) {
        case .roomSegmentsCell?:
            currentRoomSectionIndex = segmentControl.selectedSegmentIndex

            if let indexPath = tableViewModel?.indexPath(forRowNamed: HotelDetailRow.roomContentCell.rawValue) {
                tableView.reloadRows(at: [indexPath], with: .automatic)
            }

        case .foodOptionsAndRestaurantSegmentsCell?:
            currentFoodSectionIndex = segmentControl.selectedSegmentIndex

            if let indexPath = tableViewModel?.indexPath(forRowNamed: HotelDetailRow.foodContentCell.rawValue) {
                tableView.reloadRows(at: [indexPath], with: .automatic)
            }

        default:
            return
        }
    }
}

extension HotelDetailsViewController: AlertMessageCellInputDelegate {
    func dismissButtonTapped(withSenderView senderView: AlertMessageCell?) {
        eventHandler.dismissCoronavirusInformationBannerTapped()
    }
}
