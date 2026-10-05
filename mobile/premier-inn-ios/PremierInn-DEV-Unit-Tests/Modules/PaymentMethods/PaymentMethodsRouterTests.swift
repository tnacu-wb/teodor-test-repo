//
//  PaymentMethodsRouterTests.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 09/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import UIKit
import SimpleNetwork

@testable import PremierInn

private class MockNavigationController: UINavigationController {

    var pushDidCall = false

    var pushedViewController: UIViewController?

    override func pushViewController(_ viewController: UIViewController, animated: Bool) {

        pushDidCall = true
        pushedViewController = viewController

        super.pushViewController(viewController, animated: animated)
    }
}

private class MockViewController: UIViewController {

    var presentViewControllerDidCall = false

    override func present(_ viewControllerToPresent: UIViewController, animated flag: Bool, completion: (() -> Void)? = nil) {

        presentViewControllerDidCall = true
    }
}

private class MockPaymentMethodsRouterDelegate: PaymentMethodsRouterDelegate {

    func selectedCard() {

    }

    func cardDelete() {

    }
}

class PaymentMethodsRouterTests: XCTestCase {

    fileprivate var mockNavigationController: MockNavigationController?
    fileprivate var mockViewController: MockViewController?
    fileprivate var mockPaymentMethodsRouterDelegate: MockPaymentMethodsRouterDelegate?

    var router: PaymentMethodsRouter?

    override func setUp() {
        super.setUp()

        mockViewController = MockViewController()
        guard let viewController = mockViewController else { return }

        mockNavigationController = MockNavigationController(rootViewController: viewController)
        let window = UIWindow()
        window.rootViewController = mockNavigationController
        window.makeKeyAndVisible()

        router = PaymentMethodsRouter()
        router?.viewController = mockViewController
        router?.delegate = mockPaymentMethodsRouterDelegate
    }

    override func tearDown() {

        super.tearDown()
    }

    func testEditCard_AddNewCard_on() {

        let remoteConfig = MockRemoteConfig(featureAddNewCard: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        router?.edit(card: PaymentCard.empty)

        XCTAssert(mockNavigationController?.pushDidCall == true)
        XCTAssertTrue(mockNavigationController?.pushedViewController is AddNewCardView)
    }

    func testAddNewCard_AddNewCard_on() {

        let remoteConfig = MockRemoteConfig(featureAddNewCard: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        router?.addNewCard()

        XCTAssert(mockNavigationController?.pushDidCall == true)
        XCTAssertTrue(mockNavigationController?.pushedViewController is AddNewCardView)
    }
}
