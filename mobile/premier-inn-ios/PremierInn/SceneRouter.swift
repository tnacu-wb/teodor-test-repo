//
//  SceneRouter.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import AEPAssurance
import _LocationEssentials
import AppsFlyerLib

protocol SceneRouting {
    func start(with options: UIScene.ConnectionOptions)

    func sceneDidBecomeActive()

    func sceneWillResignActive()

    func sceneWillEnterForeground()

    func sceneDidEnterBackground()

    @discardableResult
    func handle(shortcutItem: UIApplicationShortcutItem) -> Bool

    func handle(_ url: URL)

    func handle(_ userActivity: NSUserActivity)
}

final class SceneRouter: SceneRouting {
    // MARK: - Properties

    private weak var window: UIWindow?
    private let debugButtonRouter: DebugButtonRouter

    private let observerManager: ObserverManageable

    private var launchedShortcutItem: UIApplicationShortcutItem?

    // MARK: - Init

    init(
        window: UIWindow?,
        debugButtonRouter: DebugButtonRouter = DebugButtonRouter(),
        observerManager: ObserverManageable = NotificationObserverManager()
    ) {
        self.window = window
        self.debugButtonRouter = debugButtonRouter
        self.observerManager = observerManager
    }

    // MARK: - Start

    func start(with options: UIScene.ConnectionOptions) {
        prepareShortcutsFlow()
        handleLaunchOptions(options)
    }

    // MARK: - Lifecycle Hooks

    func sceneDidBecomeActive() {
        AppsFlyerManager.sharedInstance.start()

        if let shortcut = launchedShortcutItem {
            _ = handle(shortcutItem: shortcut)
            launchedShortcutItem = nil
        } else {
            handleDefaultActivation()
        }

        ReservationsListViewController.shareDataWithTodayWidget()
    }

    func sceneWillResignActive() {
        updateShortcutItems()
    }

    func sceneWillEnterForeground() {
        AnalyticsManager.shared.start()
        observeEnvironmentChanges()
    }

    func sceneDidEnterBackground() {
        AnalyticsManager.shared.pause()
        observerManager.removeAll()
    }

    // MARK: - Launch Handling

    private func handleLaunchOptions(_ options: UIScene.ConnectionOptions) {
        if let shortcut = options.shortcutItem {
            launchedShortcutItem = shortcut
            return
        }

        if let notificationResponse = options.notificationResponse {
            handleNotificationResponse(notificationResponse)
            return
        }

        if let userActivity = options.userActivities.first {
            handle(userActivity)
        }

        if let urlContext = options.urlContexts.first {
            handle(urlContext.url)
        }
    }


    private func prepareShortcutsFlow() {
        if !ProcessInfo.isRunningUnitTests {
            DispatchGroupManager.sharedInstance
                .appShortcutsDispatchGroup
                .enter()
        }
    }

    private func handleNotificationResponse(_ response: UNNotificationResponse) {
        let userInfo = response.notification.request.content.userInfo
        let filtered = PushPayloadParser.extract(from: userInfo)
        let notification = LinkHandler.handleCustomPushPayload(userInfo: filtered)

        LinkHandler.sharedInstance.activeAppShortcut = notification
    }

    // MARK: - Shortcut Handling

    @discardableResult
    func handle(shortcutItem: UIApplicationShortcutItem) -> Bool {
        guard let type = ShortcutIdentifier(fullType: shortcutItem.type) else { return false }

        switch type {
        case .hotelsNearMe:
            LinkHandler.sharedInstance.activeAppShortcut = .hotelNearMe
            return true

        case .search:
            LinkHandler.sharedInstance.activeAppShortcut = .search(criteria: nil, searchTerm: nil)
            return true

        case .hotelsNearLocation:
            return handleLocationShortcut(shortcutItem)
        }
    }

    private func handleLocationShortcut(_ shortcutItem: UIApplicationShortcutItem) -> Bool {
        if let hotelCode = shortcutItem.userInfo?["hotelCode"] as? String,
           let hotelBrand = shortcutItem.userInfo?["hotelBrand"] as? String,
           let brand = HotelBrand(rawValue: hotelBrand) {
            LinkHandler.sharedInstance.activeAppShortcut =
                .hotelDetails(hotelCode: hotelCode, hotelBrand: brand, criteria: nil)

            return true
        }

        if let coordinate = coordinate(from: shortcutItem.userInfo) {
            let suggestion = PISuggestion(coordinate: coordinate)
            suggestion.aCustomTitle = shortcutItem.localizedTitle

            LinkHandler.sharedInstance.activeAppShortcut =
                .hotelsNearLocation(suggestion: suggestion, criteria: nil)

            return true
        }

        return false
    }

    // MARK: - Default Activation

    private func handleDefaultActivation() {
        switch UIApplication.topViewController() {
        case let map as MapListContainerViewController:
            map.presenter?.sortDidChange(with: map.presenter?.sortType ?? .distance)
        default:
            break
        }
    }

    // MARK: - Deep Links

    func handle(_ url: URL) {
        AppsFlyerLib.shared().handleOpen(url, options: [:])
        Assurance.startSession(url: url)
    }

    func handle(_ userActivity: NSUserActivity) {
        guard userActivity.activityType == NSUserActivityTypeBrowsingWeb,
              userActivity.webpageURL != nil else {
            return
        }

        AppsFlyerLib.shared().continue(userActivity, restorationHandler: nil)
    }

    // MARK: - Shortcuts Setup

    func updateShortcutItems() {
        let manager = RecentSuggestionsManager(dataSource: UserDefaults.standard)
        UIApplication.shared.shortcutItems = defaultShortcutItems + manager.recentSearchesShortcutItems
    }

    private var defaultShortcutItems: [UIApplicationShortcutItem] {
        [
            UIMutableApplicationShortcutItem(
                type: ShortcutIdentifier.hotelsNearMe.type,
                localizedTitle: PILocalizedString("findHotelsNearMeShortcutTitle", comment: ""),
                localizedSubtitle: nil,
                icon: UIApplicationShortcutIcon(type: .location),
                userInfo: nil
            ),
            UIMutableApplicationShortcutItem(
                type: ShortcutIdentifier.search.type,
                localizedTitle: PILocalizedString("searchShortcutTitle", comment: ""),
                localizedSubtitle: nil,
                icon: UIApplicationShortcutIcon(type: .search),
                userInfo: nil
            )
        ]
    }

    // MARK: - Environment / Debug UI

    private func observeEnvironmentChanges() {
        observerManager.observe(name: .multiVariantTestsDidLoad) { [weak self] _ in
            self?.routerDidChange()
        }

        #if DEV
        observerManager.observe(name: .webserviceConfigurationDidChange) { [weak self] _ in
            self?.routerDidChange()
        }
        #endif
    }

    @objc private func routerDidChange() {
        debugButtonRouter.update(in: window)
    }

    // MARK: - Helpers

    private func coordinate(from dict: [String: Any]?) -> CLLocationCoordinate2D? {
        guard let lat = dict?["latitude"] as? CLLocationDegrees,
              let lng = dict?["longitude"] as? CLLocationDegrees else {
            return nil
        }

        return CLLocationCoordinate2D(latitude: lat, longitude: lng)
    }
}
