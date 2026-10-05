//
//  HomeView.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

typealias HomeViewModel = (
    homeMessage: String,
    location: String,
    nights: String,
    guests: String,
    shouldShowCoronavirusInformationBanner: Bool,
    companyName: String?
)

protocol HomeViewInput: AnyObject {
    var viewModel: HomeViewModel? { get set }
    var eventHandler: HomeViewEventHandler? { get set }

    func setProcessing(is busy: Bool)
}

protocol HomeViewEventHandler: AnyObject {
    var hasAcceptedGDPRChanges: Bool { get }
    var currentCriteria: Criteria? { get }
    var adobeTrackingCode: String? { get set }
    var adobeTrackingCodeUpdated: (() -> Void)? { get }

    func viewIsReady()
    func selectedLocation(sender: UIView)
    func selectedNights(sender: UIView)
    func selectedGuests(sender: UIView)
    func selectedSearch()
    func showGDPRInfo()
    func update(with criteria: Criteria)
    func selectedRecentSearch(at index: Int)
    func showBooking(with identifier: String)
    func showCheckInOnline(with identifier: String)
    func showAmendBooking(with identifier: String)
    func dismissCoronavirusInformationBannerTapped()
    func showHotelCalendar(with dashboardHotelDetails: DashboardHotelDetails)
}

private enum SearchComponentMetrics {
    static let idealRangeFromTop: ClosedRange<CGFloat> = (10...165)
    static let smallestIdealGap: CGFloat = 40
}

class HomeView: BaseViewController {
    enum ViewConstants {
        static let containerCornerRadius: CGFloat = 6
        static let destinationFallbackImage = "manchester"
        static let leisureImageName = "piLeisureLogo"
        static let businessImageName = "piBusinessLogo"
    }

    // MARK: - Properties

    override var screenName: String { PIAnalytics.StateNames.home }
    override var screenType: String { PIAnalytics.StateTypes.home }

    override var customParameters: [String: Any]? {
        var recentSearchFormatted = [String]()

        let recentSearchManager = RecentSearchesManager(dataSource: UserDefaults.standard)
        let results = recentSearchManager.selectedResults

        results.forEach { recentSearch in
            recentSearchFormatted
                .append((recentSearch.identifier ?? "") + ":" + (recentSearch.criteria?.arrivalDate ?? Date())
                .analyticsDateFormat)
        }

        guard recentSearchFormatted.isNotEmpty else { return nil }

        var detailsJoinedArray = [String]()
        detailsJoinedArray.append(recentSearchFormatted.joined(separator: ","))

        var analyticsDict = [PIAnalytics.Keys.homeContent: detailsJoinedArray.joined(separator: ",")]

        if let code = eventHandler?.adobeTrackingCode,
           code.isNotEmpty {
            analyticsDict[PIAnalytics.Keys.sCampaign] = code
        }

        return analyticsDict
    }

    var viewModel: HomeViewModel? {
        didSet {
            guard location != nil else { return }
            guard nights != nil else { return }
            guard guests != nil else { return }

            updateLogo(isBusiness: viewModel?.companyName != nil)
            location.text = viewModel?.location
            nights.text = viewModel?.nights
            guests.text = viewModel?.guests

            updateCoronavirusBanner()
        }
    }
    var eventHandler: HomeViewEventHandler?
    var updatedCriteria: Criteria?
    var coronavirusInformationBanner: UIView?

    private func updateLogo(isBusiness: Bool) {
        let imageResourceName = isBusiness ? ViewConstants.businessImageName : ViewConstants.leisureImageName
        logoImageView.image = UIImage(named: imageResourceName)
    }

    private var versionText: String {
        guard let infoDictionary = Bundle.main.infoDictionary,
              let versionText = infoDictionary["CFBundleShortVersionString"] as? String,
              let buildVersion = infoDictionary["CFBundleVersion"] as? String,
              let appName = infoDictionary["CFBundleName"] as? String else {
                return ""
        }

        return "\(appName) \(versionText) (build \(buildVersion))"
    }

    // MARK: - Views

    @IBOutlet weak var horizontalDivider: UIView! {
        didSet {
            horizontalDivider.backgroundColor = UIColor.TintL3
        }
    }

    @IBOutlet weak var verticalDivider: UIView! {
        didSet {
            verticalDivider.backgroundColor = UIColor.TintL3
        }
    }

    // Main
    @IBOutlet weak var bookingInfoContainer: UIStackView! {
        didSet {
            bookingInfoContainer.layer.cornerRadius = ViewConstants.containerCornerRadius
            bookingInfoContainer.layer.backgroundColor = UIColor.BaseWhite.cgColor
        }
    }

    @IBOutlet weak var bookingInfoContainerTopConstraint: NSLayoutConstraint!

    @IBOutlet weak var whereToLabel: UILabel! {
        didSet {
            whereToLabel.font = UIFont.BodySmall()
            whereToLabel.textColor = UIColor.ColourDL2
            whereToLabel.text = PILocalizedString("locationHeaderTitle")
        }
    }

    @IBOutlet weak var location: UILabel! {
        didSet {
            location.accessibilityIdentifier = AccessibilityIdentifiers.Home.searchSuggestionTitle
            location.accessibilityTraits = .button
            location.font = UIFont.BodySmall_Semibold()
            location.textColor = UIColor.TintD1
        }
    }

    @IBOutlet weak var dateLabel: UILabel! {
        didSet {
            dateLabel.font = UIFont.BodySmall()
            dateLabel.textColor = UIColor.ColourDL2 // Dark Grey 2
            dateLabel.text = PILocalizedString("dateHeaderCriteria")
        }
    }

    @IBOutlet weak var nights: UILabel! {
        didSet {
            nights.accessibilityIdentifier = AccessibilityIdentifiers.Home.datesTitle
            nights.font = UIFont.BodySmall()
            nights.textColor = UIColor.TintD1
        }
    }

    @IBOutlet weak var guestsRoomsLabel: UILabel! {
        didSet {
            guestsRoomsLabel.font = UIFont.BodySmall()
            guestsRoomsLabel.textColor = UIColor.ColourDL2
            guestsRoomsLabel.text = PILocalizedString("roomHeaderCriteria")
        }
    }

    @IBOutlet weak var guests: UILabel! {
        didSet {
            guests.accessibilityIdentifier = AccessibilityIdentifiers.Home.roomsTitle
            guests.font = UIFont.BodySmall()
            guests.textColor = UIColor.TintD1
        }
    }

    @IBOutlet weak var ctaButton: RoundedCornersButton! {
        didSet {
            ctaButton.accessibilityIdentifier = AccessibilityIdentifiers.Home.searchButton
            ctaButton.backgroundColor = .Tint1
            ctaButton.titleLabel?.font = UIFont.Heading3_Semibold()
            ctaButton.setTitle(PILocalizedString("homeSearchButtonTitle"), for: .normal)
            ctaButton.setTitleColor(.ColourLD1, for: .normal)
        }
    }

    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!

    @IBOutlet weak var logoImageView: UIImageView!

    // Dashboard
    lazy var dashboardViewController: UIViewController? = {
        guard UIDevice.current.userInterfaceIdiom != .pad else { return nil }

        let simpleStorageManager = SimpleStorageManager<Stay>(dataSource: .standard)
        return DashboardModule.build(
            with: simpleStorageManager.items.upcomingStays.first,
            recentSearchesViewDelegate: self,
            dashboardRouterDelegate: self
        )
    }()

    // Recent Searches
    @IBOutlet weak var curatedSuggestions: UICollectionView! {
        didSet {
            curatedSuggestions.delegate = self
            curatedSuggestions.dataSource = self
            curatedSuggestions.registerCellForNib(with: CuratedSuggestionCell.self)
            curatedSuggestions.backgroundColor = .whiteTwo

            curatedSuggestions.reloadData()
        }
    }
    @IBOutlet weak var recentSearchesHeightConstraint: NSLayoutConstraint?

    // Debug
    @IBOutlet weak var versionButton: UIButton! {
        didSet {
            versionButton.titleLabel?.font = UIFont.Subtext()
            versionButton.setTitle(versionText, for: .normal)
            versionButton.isHidden = !SettingsManager.sharedInstance.showVersionNumber
            versionButton.isEnabled = SettingsManager.sharedInstance.showDebugMenu
            versionButton.accessibilityIdentifier = "versionNumberAcc"
        }
    }

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        edgesForExtendedLayout = []
        view.backgroundColor = .ColourLD4

        if UIDevice.current.userInterfaceIdiom == .pad {
            HomeRouter.build(with: self)
        }

        if let dashboardViewController = dashboardViewController {
            addBottomSheetDashboardView(dashboardViewController)
        }

        guard let window = UIApplication.shared.currentWindow() else { return }
        guard let debugView = window.viewWithTag(666) else { return }
        window.bringSubviewToFront(debugView)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(true, animated: animated)

        if SettingsManager.sharedInstance.piRemoteConfig.bartDown == true {
            if UserDefaults.standard.bool(forKey: Constants.shownBartIsDownKey) != true || !SettingsManager.sharedInstance
               .isBartBannerDismissible {
                UserDefaults.standard.set(true, forKey: Constants.shownBartIsDownKey)
                BARTDowntimeHandler.bartIsDown(parentNavigationController: navigationController)
            }
        } else {
            UserDefaults.standard.set(false, forKey: Constants.shownBartIsDownKey)

            if eventHandler?.hasAcceptedGDPRChanges == false {
                eventHandler?.showGDPRInfo()
            }
        }

        eventHandler?.viewIsReady()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        guard updatedCriteria != nil else { return }
        showUpdateAlert(sender: self)
    }

    // Updates

    private func updateCoronavirusBanner() {
        if viewModel?.shouldShowCoronavirusInformationBanner ?? false {
            if coronavirusInformationBanner != nil {
                coronavirusInformationBanner?.isHidden = false
            } else {
                let view: AlertMessageCell? = AlertMessageCell.fromNib()

                view?.frame.origin.y = view?.window?.windowScene?.statusBarManager?.statusBarFrame.maxY ?? 0
                view?.update(
                    with: SettingsManager.sharedInstance.coronavirusBannerMessage,
                    using: self.view.frame.width,
                    and: .info
                )
                view?.alertMessageCellInputDelegate = self

                if let coronavirusView = view {
                    coronavirusInformationBanner = coronavirusView
                    self.view.addSubview(coronavirusView)
                }
            }
        } else {
            coronavirusInformationBanner?.isHidden = true
        }
    }

    func showAlertMessage(title: String, message: String) {
        showAlertWith(title: title, message: message)
    }

    // MARK: - Actions

    @IBAction func locationDidTap(_ sender: UITapGestureRecognizer) {
        guard let view = sender.view else { return }

        eventHandler?.selectedLocation(sender: view)
    }

    @IBAction func nightsDidTap(_ sender: UITapGestureRecognizer) {
        guard let view = sender.view else { return }

        eventHandler?.selectedNights(sender: view)
    }

    @IBAction func guestsDidTap(_ sender: UITapGestureRecognizer) {
        guard let view = sender.view else { return }

        eventHandler?.selectedGuests(sender: view)
    }

    @IBAction func searchButtonDidTap(_ sender: Any) {
        // Haptic Feedback
        UINotificationFeedbackGenerator().notificationOccurred(.success)

        eventHandler?.selectedSearch()
    }

    @IBAction func versionButtonDidTap(_ sender: UIButton) {
        let controller = DebugViewController()

        let navController = UINavigationController(rootViewController: controller)
        navController.modalPresentationStyle = .popover
        navController.popoverPresentationController?.sourceView = sender
        navController.popoverPresentationController?.sourceRect = sender.bounds

        present(navController, animated: true)
    }

    func showEmployeeRatesConfirmationAlert() {
        let alertController = UIAlertController.init(
            title: nil,
            message: PILocalizedString("employeeRatesEnableQuestions"),
            preferredStyle: .alert
        )
        alertController.addAction(UIAlertAction(title: PILocalizedString("Cancel"), style: .cancel, handler: nil))
        alertController.addAction(UIAlertAction(title: PILocalizedString("Enable"), style: .default) { _ in
            SettingsManager.sharedInstance.enableEmployeeRates = true

            // to refresh My Account
            NotificationCenter.default.post(name: .userDidChange, object: nil)
        })

        present(alertController, animated: true, completion: nil)
    }

    // MARK: - Dashboard

    func addBottomSheetDashboardView(_ dashboardView: UIViewController) {
        addChild(dashboardView)
        dashboardView.view.frame = CGRect(
            x: 0,
            y: view.frame.maxY + SearchComponentMetrics.smallestIdealGap,
            width: view.frame.width,
            height: view.frame.height - SearchComponentMetrics.smallestIdealGap
        )
        view.addSubview(dashboardView.view)
        dashboardView.didMove(toParent: self)
    }

    private func moveInfoContainer(to yPosition: CGFloat) {
        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.7,
            initialSpringVelocity: 0.9,
            options: [.curveEaseOut],
            animations: { [weak self] in
            guard let self = self else { return }

            bookingInfoContainerTopConstraint.constant = yPosition
        }
        )
    }

    private func visibleBottomComponentChanged(
        with topY: CGFloat,
        using idealRangeFromTop: ClosedRange<CGFloat> = SearchComponentMetrics.idealRangeFromTop,
        and smallestIdealGap: CGFloat = SearchComponentMetrics.smallestIdealGap
    ) {
        let sizeOfUnitToMove = bookingInfoContainer.frame.maxY - bookingInfoContainer.frame.minY
        let positionAboveBottomComponent = topY - sizeOfUnitToMove - smallestIdealGap
        let newInfoTopPosition = idealRangeFromTop.clamp(positionAboveBottomComponent)

        moveInfoContainer(to: newInfoTopPosition)
    }

    private func dashboardDidHide() {
        moveInfoContainer(to: SearchComponentMetrics.idealRangeFromTop.upperBound)
    }
}

extension HomeView: HomeViewInput {
    func setProcessing(is busy: Bool) {
        view.isUserInteractionEnabled = !busy
        ctaButton.isEnabled = !busy
        ctaButton.setTitle(busy ? "" : PILocalizedString("homeSearchButtonTitle"), for: .normal)

        if busy {
            activityIndicator.startAnimating()
        } else {
            activityIndicator.stopAnimating()
        }
    }
}

extension HomeView: AlertMessageCellInputDelegate {
    func dismissButtonTapped(withSenderView senderView: AlertMessageCell?) {
        guard let senderView = senderView else { return }

        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.7,
            initialSpringVelocity: 0.9,
            options: [.curveEaseOut],
            animations: {
                senderView.frame.origin.y = -(senderView.frame.size.height)
            },
            completion: { _ in
            senderView.removeFromSuperview()
        }
        )

        eventHandler?.dismissCoronavirusInformationBannerTapped()
    }
}

extension HomeView: CriteriaModifiable {
    var currentCriteria: Criteria? { eventHandler?.currentCriteria }

    func update(with criteria: Criteria) {
        updatedCriteria = nil
        eventHandler?.update(with: criteria)
    }
}

extension HomeView: RecentSearchesViewDelegate {
    func selectedRecentSearch(at index: Int) {
        eventHandler?.selectedRecentSearch(at: index)
    }
}

extension HomeView: DashboardRouterDelegate {
    func showHotelCalendar(with dashboardHotelDetails: DashboardHotelDetails) {
        eventHandler?.showHotelCalendar(with: dashboardHotelDetails)
    }

    func showBooking(with identifier: String) {
        eventHandler?.showBooking(with: identifier)
    }

    func showCheckInOnline(with identifier: String) {
        eventHandler?.showCheckInOnline(with: identifier)
    }

    func showAmendBooking(with identifier: String) {
        eventHandler?.showAmendBooking(with: identifier)
    }

    func navigateToSRP(with suggestion: Suggestion) {
        (eventHandler as? HomePresenter)?.navigateToSRP(with: suggestion)
    }
}
