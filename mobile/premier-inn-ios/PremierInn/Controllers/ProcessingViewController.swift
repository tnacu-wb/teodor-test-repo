//
//  ProcessingViewController.swift
//  PremierInn
//
//  Created by Freddie Parks on 03/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import SimpleNetwork

class ProcessingViewController: BaseViewController {
    @IBOutlet weak var premierInnLogo: UIImageView! {
        didSet {
            premierInnLogo.image = UIImage(named: "piLeisureLogo")
            premierInnLogo.contentMode = .scaleAspectFit
        }
    }
    @IBOutlet weak var loader: UIActivityIndicatorView!
    private var requestsManager = RequestsManager()
    private var shouldLoadRemoteConfiguration = true

    override var screenName: String { PIAnalytics.StateNames.splash }
    override var screenType: String { PIAnalytics.StateTypes.home }
    override var shouldShowReachability: Bool { false }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .BasePurple
        loader.color = .white
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        if shouldLoadRemoteConfiguration {
            preloadRemoteSettings {
                self.trackAccessibilityValues()

                // Unmask all fields except sensitive ones if feature flag is on, this is a fail safe to make sure we do not track sensitive data
                if SettingsManager.sharedInstance.featureContentsquareUnmask {
                    ContentsquareConfig.defaultMask(isMasked: false)
                }

                // reset the app incentive home popover before Home loads
                SettingsManager.sharedInstance.hasSeenAppIncentive = false

                // Setup MVTManager and load the tests
                MVTManager.sharedInstance.provider = AdobeMVTResourceProvider()

                MVTManager.sharedInstance.load(tests: MVTConfig.tests) {
                    self.present(self.homeController, animated: true)
                    self.shouldLoadRemoteConfiguration = false

                    NotificationCenter.default.post(name: .multiVariantTestsDidLoad, object: nil)

                    self.postSettingsSetup()
                }
            }
        }
    }

    private func preloadRemoteSettings(completion: @escaping () -> Void) {
        SettingsManager.sharedInstance.fetchRemoteConfig {
            completion()
        }
	}

    private func postSettingsSetup() {
        DispatchGroupManager.sharedInstance.countriesDispatchGroup.enter()
        Country.refreshCountries {
            DispatchGroupManager.sharedInstance.countriesDispatchGroup.leave()
        }

        if SettingsManager.sharedInstance.shouldAttemptAutoLogin == true {
            DispatchGroupManager.sharedInstance.countriesDispatchGroup.notify(queue: .main) {
                self
                    .refreshExistingUserSessionIfAny(shouldAttemptLogin: SettingsManager.sharedInstance
                    .shouldAttemptAutoLogin)
            }
        }

        SettingsManager.sharedInstance.setUpBusinessRules()

        if !ProcessInfo.isRunningUnitTests {
            DispatchGroupManager.sharedInstance.appShortcutsDispatchGroup.leave()
        }
    }

    private func refreshExistingUserSessionIfAny(shouldAttemptLogin: Bool) {
        let savedBusiness = UserDefaults.standard.bool(forKey: .storedBusinessKey)
        let usernameKey: String = { savedBusiness ? .storedBusinessUsernameKey : .storedUsernameKey }()
        guard let emailAddress = UserDefaults.standard.string(forKey: usernameKey), let credentials = User.storedCredentials(
            for: emailAddress,
            business: savedBusiness
        ) else { return }

        requestsManager.getUser(userId: credentials.username, isBusiness: credentials.business) { result in
            DispatchQueue.main.async {
                switch result.handle() {
                case .sessionExpired?:
                    if shouldAttemptLogin {
                        self.requestsManager.autoLogin { _ in
                            self.refreshExistingUserSessionIfAny(shouldAttemptLogin: false)
                        }
                    }
                case .generic(let error)?:
                    if shouldAttemptLogin {
                        self.requestsManager.autoLogin { _ in
                            self.refreshExistingUserSessionIfAny(shouldAttemptLogin: false)
                        }
                    }
                    print("refreshExistingUserSessionIfAny: " + error.localizedDescription)
                case .none:
                    break
                }
                if !credentials.business {
                    self.requestsManager.refreshStays(
                        for: UserSessionManager.sharedInstance.currentUser,
                        shouldAttemptLogin: false
                    ) { _ in }
                }
                if credentials.business,
                   let companyId = UserSessionManager.sharedInstance.currentUser?.companyId {
					let sensorData = AkamaiProtection.sensorData

					self.requestsManager.getCompany(companyId: companyId, sensorData: sensorData) { company, _ in
                        guard let company = company else { return self.handleCompanyFailure() }
                        self.requestsManager.refreshStays(
                            for: UserSessionManager.sharedInstance.currentUser,
                            shouldAttemptLogin: false
                        ) { _ in }
                        UserSessionManager.sharedInstance.currentUser?.company = company
                        BookingDetails.sharedInstance.update(with: company)
                        NotificationCenter.default.post(name: .userDidChange, object: nil)
                    }
                }
            }
        }
    }

    private func trackAccessibilityValues() {
        // Track the users accessibility traits they are using, tracked at app launch
        if UIAccessibility.isVoiceOverRunning {
            AnalyticsManager.shared.log(event: FirebaseAnalytics.Event.isVoiceOverEnabled, parameters: nil)
        }

        if UIAccessibility.isBoldTextEnabled {
            AnalyticsManager.shared.log(event: FirebaseAnalytics.Event.isBoldTextEnabled, parameters: nil)
        }

        if UIAccessibility.isReduceMotionEnabled {
            AnalyticsManager.shared.log(event: FirebaseAnalytics.Event.isReduceMotionEnabled, parameters: nil)
        }
    }

    func handleCompanyFailure() {
        // We need to do this as if the company call fails we do not want them logged in so we need to log them out, as we need them logged in to get the company theres no way to avoid a log in and then log out scenario
        UserSessionManager.sharedInstance.piUserLoggedOut()
    }

    private var homeController: UITabBarController {
        let homeController: UIViewController = {
            if UIDevice.current.userInterfaceIdiom == .pad,
               let controller = UIStoryboard(name: "Main~ipad", bundle: nil)
               .instantiateViewController(
                   withIdentifier: String(describing: UITabBarController.self)
               ) as? UITabBarController,
               let homeController = controller.viewControllers?.first {
                switch homeController {
                case let navigationController as UINavigationController:
                    return navigationController.viewControllers.first ?? homeController
                default:
                    return homeController
                }
            } else {
                let homeController = HomeRouter.build()

                return homeController
            }
        }()

        homeController.tabBarItem.title = PILocalizedString("tabbarItemSearch", comment: "Tabbar item: search")
        homeController.tabBarItem.image = #imageLiteral(resourceName: "search")
        homeController.tabBarItem.accessibilityIdentifier = AccessibilityIdentifiers.Home.searchTabBarItem

        let tabBarController = UITabBarController()
        tabBarController.modalPresentationStyle = .fullScreen
        tabBarController.modalTransitionStyle = .crossDissolve
        tabBarController.view.backgroundColor = .BasePurple
        tabBarController.viewControllers = {
            let myBookingsController = ReservationsRouter.build()
            myBookingsController.tabBarItem.title = PILocalizedString(
                "tabbarItemMyBookings",
                comment: "Tabbar item: my bookings"
            )
            myBookingsController.tabBarItem.image = #imageLiteral(resourceName: "bookings")
            myBookingsController.tabBarItem.accessibilityIdentifier = AccessibilityIdentifiers.Home.bookingsTabBarItem

            let myAccountController = AccountModule.build()
            myAccountController.tabBarItem.title = PILocalizedString("tabbarItemAccount", comment: "Tabbar item: account")
            myAccountController.tabBarItem.image = #imageLiteral(resourceName: "account")
            myAccountController.tabBarItem.accessibilityIdentifier = AccessibilityIdentifiers.Home.accountTabBarItem

            // MARK: - Order and Pay Text
            let service = ChooseTableService()
            let presenter = ChooseTablePresenter(service: service)
            let orderAndPayController = ChooseTableTableViewController(presenter: presenter)
            orderAndPayController.tabBarItem.title = "Order & Pay"
            orderAndPayController.tabBarItem.image = #imageLiteral(resourceName: "HRS").af.imageScaled(to: #imageLiteral(resourceName: "account").size)

            var tabOptions = [
                PINavigationController(rootViewController: homeController),
                MyBookingsNavigationController(rootViewController: myBookingsController),
                UINavigationController(rootViewController: myAccountController)
            ]

            if SettingsManager.sharedInstance.orderAndPay {
                tabOptions.insert(UINavigationController(rootViewController: orderAndPayController), at: 2)
            }

            return tabOptions
        }()

        // Janky hack to add accessibility identifiers 🤦🏾‍♀️
        // Apparently setting accessibility identifier of UIViewController.tabBarItem does not work 🤤
        let accessibilityIdentifiers = [
            AccessibilityIdentifiers.Home.searchTabBarItem,
            AccessibilityIdentifiers.Home.bookingsTabBarItem,
            AccessibilityIdentifiers.Home.accountTabBarItem
        ]

        for (index, subview) in tabBarController.tabBar.subviews.enumerated() where subview is UIControl {
            guard accessibilityIdentifiers.indices.contains(index) else { continue }
            subview.accessibilityIdentifier = accessibilityIdentifiers[index]
        }
        // EOJH

        return tabBarController
    }
}
