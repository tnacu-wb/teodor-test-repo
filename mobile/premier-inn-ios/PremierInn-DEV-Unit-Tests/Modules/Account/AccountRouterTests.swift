//
//  AccountRouterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 25/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
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

private class MockPaymentMethodsDelegate: PaymentMethodsRouterDelegate {

    func selectedCard() {

    }
    
    func cardDelete() {

    }
}

private class MockCardDetailsDelegate: CardDetailsRouterDelegate {

    func updatedStoredCard() {

    }

    func newSavedCard() {

    }
}

private class MockAddNewCardRouterDelegate: AddNewCardRouterDelegate {

    func cardUpdated() {

    }
}

class AccountRouterTests: XCTestCase {
    
    fileprivate var mockNavigationController: MockNavigationController?
    fileprivate var mockViewController: MockViewController?
    fileprivate var mockPaymentMethodsDelegate: MockPaymentMethodsDelegate?
    fileprivate var mockCardDetailsDelegate: MockCardDetailsDelegate?
    fileprivate var mockAddNewCardRouterDelegate: MockAddNewCardRouterDelegate?

    var router: AccountRouter?

    private var user: User? {

        do {
            let user = try User(title: "mr", firstName: "test", lastName: "test")
            return user
        } catch {
            XCTFail("\(error)")
            return nil
        }
    }

    override func setUp() {
        super.setUp()
        
        mockViewController = MockViewController()
        guard let viewController = mockViewController else { return }
        
        mockNavigationController = MockNavigationController(rootViewController: viewController)
        let window = UIWindow()
        window.rootViewController = mockNavigationController
        window.makeKeyAndVisible()

        mockPaymentMethodsDelegate = MockPaymentMethodsDelegate()
        mockCardDetailsDelegate = MockCardDetailsDelegate()
        mockAddNewCardRouterDelegate = MockAddNewCardRouterDelegate()

        router = AccountRouter()
        router?.viewController = mockViewController
    }
    
    override func tearDown() {
        
        super.tearDown()
    }
    
    func testOpenRegister() {
        
        router?.openRegister(withCompletionDelegate: nil)
        
        XCTAssert(mockViewController?.presentViewControllerDidCall == true)
    }
    
    func testOpenLogin() {
        
        router?.openLogin()
        
        XCTAssert(mockViewController?.presentViewControllerDidCall == true)
    }
    
    func testOpenBookingPrefs() {
        
        router?.openBookingPreferences()
        
        XCTAssert(mockNavigationController?.pushDidCall == true)
    }
    
    func testOpenPassword() {
        
        router?.openChangePassword(withCompletionDelegate: nil)
        
        XCTAssert(mockNavigationController?.pushDidCall == true)
        XCTAssertTrue(mockNavigationController?.pushedViewController is ChangePasswordView)
    }

    // MARK: - Payment Methods

    func testOpenPaymentMethods_addNewCard() {

        guard let mockPaymentMethodsDelegate, let mockCardDetailsDelegate, let mockAddNewCardRouterDelegate else { return }

        let remoteConfig = MockRemoteConfig(featureAddNewCard: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        UserSessionManager.sharedInstance.piUserLoggedOut()

        router?.openPaymentMethods(paymentMethodDelegate: mockPaymentMethodsDelegate, cardDetailsDelegate: mockCardDetailsDelegate, addNewCardRouterDelegate: mockAddNewCardRouterDelegate)

        XCTAssert(mockNavigationController?.pushDidCall == true)
        XCTAssertTrue(mockNavigationController?.pushedViewController is AddNewCardView)
    }

    func testOpenPaymentMethods_paymentMethods_loggedInWithCards() {

        guard let mockPaymentMethodsDelegate, let mockCardDetailsDelegate, let mockAddNewCardRouterDelegate else { return }

        if let user = user {
            user.paymentPreference = PaymentPreference(dict: nil)
            user.paymentPreference?.card = PaymentCard.empty
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }

        router?.openPaymentMethods(paymentMethodDelegate: mockPaymentMethodsDelegate, cardDetailsDelegate: mockCardDetailsDelegate, addNewCardRouterDelegate: mockAddNewCardRouterDelegate)

        XCTAssert(mockNavigationController?.pushDidCall == true)
        XCTAssertTrue(mockNavigationController?.pushedViewController is PaymentMethodsViewController)
    }

    func testOpenPaymentMethods_paymentMethods_loggedInWithoutCards() {

        guard let mockPaymentMethodsDelegate, let mockCardDetailsDelegate, let mockAddNewCardRouterDelegate else { return }

        if let user = user {
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }

        let remoteConfig = MockRemoteConfig(featureAddNewCard: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        router?.openPaymentMethods(paymentMethodDelegate: mockPaymentMethodsDelegate, cardDetailsDelegate: mockCardDetailsDelegate, addNewCardRouterDelegate: mockAddNewCardRouterDelegate)

        XCTAssert(mockNavigationController?.pushDidCall == true)
        XCTAssertTrue(mockNavigationController?.pushedViewController is AddNewCardView)
    }

    // ---

    func testOpenUserDetails() {
        
        router?.openMyDetails(withCompletionDelegate: nil)
        
        XCTAssert(mockNavigationController?.pushDidCall == true)
        XCTAssertTrue(mockNavigationController?.pushedViewController is UserDetailsViewController)
    }
    
    func testBuild() {
        
        let viewController = AccountModule.build()
        
        XCTAssert(viewController is AccountViewController)
    }
}
