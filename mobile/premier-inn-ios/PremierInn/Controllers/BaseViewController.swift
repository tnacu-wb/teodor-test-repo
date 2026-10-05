//
//  BaseViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 18/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class BaseViewController: UIViewController, Trackable {
    // MARK: - Properties

    private lazy var reachabilityManager: ReachabilityManager? = {
        guard shouldShowReachability else { return nil }
        guard NSClassFromString("XCTestCase") == nil else { return nil }

        let manager = ReachabilityManager()
        manager.delegate = self

        return manager
    }()
    private var reachabilityView: ReachabilityView?
    private var screenshotObserver: NSObjectProtocol?

    var shouldShowReachability: Bool { true }
    var reachabilityViewContainer: UIView? { view }
    var reachabilityViewPositionY: CGFloat { view.safeAreaInsets.top }
    var reachabilityAccessibilityIdentifier: String {
        AccessibilityIdentifiers.Shared.noInternetBanner
    }
    var processingActivityIndicator: UIActivityIndicatorView?
    var viewFader: UIView?
    var screenName: String { "\(type(of: self))" }
    var trackScreen: Bool { true }
    var environment: String { AnalyticsConstants.environment }
    var loggedIn: LoggedInAnalytic { UserSessionManager.sharedInstance.currentUser != nil ? .loggedIn : .notLoggedIn }
    var timeZone: String { TimeZone.current.description }
    var language: String { Locale.current.language.languageCode?.identifier ?? "n/a" }
    var screenType: String { "PI_DEV" }
    var userID: String { UserSessionManager.sharedInstance.currentUser?.customerAccountId ?? "" }
    var companyID: String { UserSessionManager.sharedInstance.currentUser?.companyId ?? "" }
    var businessUserLevel: String { UserSessionManager.sharedInstance.currentUser?.accessLevel?.rawValue ?? "" }
    var time: String { Date().analyticsTimeFormat }
    var customParameters: [String: Any]? { nil }
    var detectedScreenAsOverlayView: Bool = false
    var analytics: AnalyticsType = AnalyticsManager.shared

    override var shouldAutorotate: Bool { true }
    override var preferredStatusBarStyle: UIStatusBarStyle { .default }
    override var title: String? {
        get {
            super.title
        }
        set {
            var value = newValue

            if let string = value, string.count > Constants.navigationMaxTitleLength {
                value = string.truncated(withLength: Constants.navigationMaxTitleLength)
            }

            super.title = value
        }
    }

    // MARK: - Lifecycle

    deinit {
        print("DEINIT: \(self)")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        enableScrollHack()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        SettingsManager.sharedInstance.supportedInterfaceOrientations = {
            guard UIDevice.current.userInterfaceIdiom != .pad else { return .all }

            return .portrait
        }()

        reachabilityManager?.startObserving()

        addViewFader()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        if trackScreen {
            trackState(withName: screenName, type: screenType, additionalData: customParameters)
        }

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(applicationDidTakeScreenshot),
            name: UIApplication.userDidTakeScreenshotNotification,
            object: nil
        )
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        reachabilityManager?.stopObserving()
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)

        NotificationCenter.default.removeObserver(self, name: UIApplication.userDidTakeScreenshotNotification, object: nil)
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()

        if let reachabilityView = reachabilityView {
            reachabilityView.frame.origin.y = reachabilityViewPositionY
        }
    }

    // MARK: - Analytics

    private func analyticsDic(type: String?) -> PIDictionary {
        var dict = [
            PIAnalytics.Keys.environment: environment,
            PIAnalytics.Keys.userLogin: loggedIn.rawValue,
            PIAnalytics.Keys.timeZone: timeZone,
            PIAnalytics.Keys.language: language,
            PIAnalytics.Keys.screenType: type ?? screenType,
            PIAnalytics.Keys.userID: userID,
            PIAnalytics.Keys.time: time
        ]

        if let user = UserSessionManager.sharedInstance.currentUser, let companyId = user.companyId,
           let accessLevel = user.accessLevel?.rawValue {
            dict[PIAnalytics.Keys.companyID] = companyId
            dict[PIAnalytics.Keys.businessUserLevel] = accessLevel
        }

        // Deep link tracking campaign properties
        if let campaign = analytics.campaignAttribution.remove() {
            dict[PIAnalytics.Keys.sCampaign] = campaign.cid
            dict[PIAnalytics.Keys.mckv] = campaign.mckv
            dict[PIAnalytics.Keys.etRid] = campaign.etRid
            dict[PIAnalytics.Keys.sFullURL] = campaign.fullURLString
            dict[PIAnalytics.Keys.sReferrer] = campaign.referrerURLString
        }

        return dict
    }

    func trackState(withName name: String, type: String? = nil, additionalData: PIDictionary? = nil) {
        let data = analyticsDic(type: type).merging(additionalData ?? [:], uniquingKeysWith: { (_, last) in last })

        analytics.trackState(name, data: data)
    }

    func track(action: String, additionalData: PIDictionary?) {
        let data = analyticsDic(type: nil).merging(additionalData ?? [:], uniquingKeysWith: { (_, last) in last })

        analytics.trackAction(action, userInfo: data)
    }

    private func addViewFader() {
        let viewFader = UIView(frame: CGRect(
            x: 0,
            y: 0,
            width: view.frame.size.width,
            height: view.frame.size.height
        ))

        viewFader.alpha = 0
        viewFader.backgroundColor = .white

        if let processingActivityIndicator = processingActivityIndicator {
            view.insert(viewFader, .below(processingActivityIndicator))
        } else {
            view.addSubview(viewFader)
        }

        self.viewFader = viewFader
    }

    @objc func applicationDidTakeScreenshot() {
        analytics.trackAction(
            PIAnalytics.Action.screenshot + self.screenName,
            userInfo: [PIAnalytics.Keys.screenshots: true]
        )
    }

    // MARK: - View Activities

    func toggleProcessing(isProcessing processing: Bool, with parentView: UIView? = nil) {
        let activityIndicator: UIActivityIndicatorView = {
            if let 🚨 = processingActivityIndicator { return 🚨 }

            let indicator = UIActivityIndicatorView(style: .medium)
            indicator.color = .BasePurple

            let 🌄: UIView = parentView ?? view
            indicator.center = 🌄.center
            indicator.autoresizingMask = [
                .flexibleTopMargin,
                .flexibleBottomMargin,
                .flexibleLeftMargin,
                .flexibleRightMargin
            ]
            indicator.hidesWhenStopped = true

            view.addSubview(indicator)

            processingActivityIndicator = indicator
            return indicator
        }()

        view.isUserInteractionEnabled = !processing
        activityIndicator.move(to: .front)
        if processing {
            activityIndicator.startAnimating()
        } else {
            activityIndicator.stopAnimating()
        }
    }

    func toggleViewFader(toBeVisible visible: Bool) {
        guard let lordViewFader = viewFader else { return }

        UIView.animate(withDuration: .ocd) {
            lordViewFader.alpha = visible ? 0.75 : 0
        }
    }
}

extension BaseViewController: ReachabilityManagerDelegate {
    func networkBecameReachable() {
        guard let reachabilityView = reachabilityView else { return }
        reachabilityView.frame.origin.y = reachabilityViewPositionY
        reachabilityView.update(message: ReachabilityMessage.reachable.rawValue)
    }

    func networkNotReachable() {
        trackState(withName: PIAnalytics.StateNames.noConnection, type: screenName)

        if let reachabilityView = reachabilityView {
            reachabilityView.frame.origin.y = reachabilityViewPositionY
            reachabilityView.update(message: ReachabilityMessage.unreachable.rawValue)
        } else {
            reachabilityView = ReachabilityView(
                frame: CGRect(x: 0, y: reachabilityViewPositionY, width: view.frame.size.width, height: 35),
                message: ReachabilityMessage.unreachable.rawValue
            )
            if let reachabilityView = reachabilityView {
                reachabilityViewContainer?.addSubview(reachabilityView)
            }
        }

        reachabilityView?.accessibilityIdentifier = reachabilityAccessibilityIdentifier
    }
}
