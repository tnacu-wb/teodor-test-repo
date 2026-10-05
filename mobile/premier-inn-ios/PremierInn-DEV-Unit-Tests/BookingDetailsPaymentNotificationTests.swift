//
//  BookingDetailsPaymentNotificationTests.swift
//  PremierInnTests
//
//  Created by Emil Vaklinov on 09/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class BookingDetailsPaymentNotificationTests: XCTestCase {
    
    var bookingDetails: BookingDetails!
    
    override func setUp() {
        super.setUp()
        bookingDetails = BookingDetails()
    }
    
    override func tearDown() {
        bookingDetails = nil
        super.tearDown()
    }
    
    // MARK: - Test for duplicate notification fix
    
    func testInvalidMethodViewModelsWithMultipleReasonsShowsOnlyFirstReason() {
        // A payment method with multiple disabled reasons
        guard let mockCard = createMockCard(last4: "0028"),
              let paymentOptionWithMultipleReasons = createMockPaymentOption(
                card: mockCard,
                enabled: false,
                reasons: ["CARD_NOT_ACCEPTED_AT_HOTEL", "PIBA_UK_ALLOWED_ONLY_IN_UK"]
              ),
              let enabledPaymentOption = createMockPaymentOption(
                card: nil,
                enabled: true,
                reasons: nil
              ) else {
            XCTFail("Failed to create mock payment options")
            return
        }
        
        // Set up BookingDetails with payment methods
        BookingDetails.sharedInstance.paymentMethods = [paymentOptionWithMultipleReasons, enabledPaymentOption]
        BookingDetails.sharedInstance.primaryPaymentMethod = enabledPaymentOption
        
        let sut = BookingDetails.sharedInstance.paymentMethodsViewModel
        
        // Should only show ONE notification (the first reason)
        XCTAssertEqual(sut.invalidOptionsMessage?.count, 1, "Should only show one notification per disabled payment method")
        
        // Verify it's the first reason
        if let firstMessage = sut.invalidOptionsMessage?.first?.message {
            XCTAssertTrue(firstMessage.contains("0028"), "Message should contain the card last 4 digits")
            XCTAssertTrue(firstMessage.contains("not accepted at this hotel"), "Should show the first reason")
        } else {
            XCTFail("Expected at least one invalid options message")
        }
    }
    
    func testInvalidMethodViewModelsWithSingleReasonShowsOneNotification() {
        // A payment method with a single disabled reason
        guard let mockCard = createMockCard(last4: "1234"),
              let paymentOptionWithSingleReason = createMockPaymentOption(
                card: mockCard,
                enabled: false,
                reasons: ["CARD_EXPIRED_BEFORE_DEPARTURE"]
              ),
              let enabledPaymentOption = createMockPaymentOption(
                card: nil,
                enabled: true,
                reasons: nil
              ) else {
            XCTFail("Failed to create mock payment options")
            return
        }
        
        BookingDetails.sharedInstance.paymentMethods = [paymentOptionWithSingleReason, enabledPaymentOption]
        BookingDetails.sharedInstance.primaryPaymentMethod = enabledPaymentOption
        
        let sut = BookingDetails.sharedInstance.paymentMethodsViewModel
        
        // Should show exactly one notification
        XCTAssertEqual(sut.invalidOptionsMessage?.count, 1, "Should show one notification for single reason")
    }
    
    func testInvalidMethodViewModelsWithMultipleDisabledCardsShowsOneNotificationPerCard() {
        // Multiple disabled payment methods, each with multiple reasons
        guard let mockCard1 = createMockCard(last4: "0028"),
              let mockCard2 = createMockCard(last4: "5678"),
              let disabledOption1 = createMockPaymentOption(
                card: mockCard1,
                enabled: false,
                reasons: ["CARD_NOT_ACCEPTED_AT_HOTEL", "PIBA_UK_ALLOWED_ONLY_IN_UK"]
              ),
              let disabledOption2 = createMockPaymentOption(
                card: mockCard2,
                enabled: false,
                reasons: ["CARD_EXPIRED_BEFORE_DEPARTURE", "CARD_NOT_VALID_FOR_RATE"]
              ),
              let enabledPaymentOption = createMockPaymentOption(
                card: nil,
                enabled: true,
                reasons: nil
              ) else {
            XCTFail("Failed to create mock payment options")
            return
        }
        
        BookingDetails.sharedInstance.paymentMethods = [disabledOption1, disabledOption2, enabledPaymentOption]
        BookingDetails.sharedInstance.primaryPaymentMethod = enabledPaymentOption
        
        let sut = BookingDetails.sharedInstance.paymentMethodsViewModel
        
        // Should show exactly TWO notifications (one per disabled card)
        XCTAssertEqual(sut.invalidOptionsMessage?.count, 2, "Should show one notification per disabled payment method")
    }
    
    func testInvalidMethodViewModelsWithNoDisabledMethodsShowsNoNotifications() {
        // Only enabled payment methods
        guard let enabledPaymentOption = createMockPaymentOption(
            card: nil,
            enabled: true,
            reasons: nil
        ) else {
            XCTFail("Failed to create mock payment option")
            return
        }
        
        BookingDetails.sharedInstance.paymentMethods = [enabledPaymentOption]
        BookingDetails.sharedInstance.primaryPaymentMethod = enabledPaymentOption
        
        let sut = BookingDetails.sharedInstance.paymentMethodsViewModel
        
        // Should show no notifications
        XCTAssertEqual(sut.invalidOptionsMessage?.count, 0, "Should show no notifications when all methods are enabled")
    }
    
    // MARK: - Helper Methods
    
    private func createMockCard(last4: String) -> Card? {
        let cardDict: [String: Any] = [
            "token": "mock_token_\(last4)",
            "expiryMonth": "12",
            "expiryYear": "25",
            "type": "VI",
            "logoSrc": "/content/dam/global/booking/VC.jpg",
            "cardHolderName": "Test User",
            "cardType": "LEISURE_STORED_CARD",
            "cnpRequired": false
        ]
        
        guard let jsonData = try? JSONSerialization.data(withJSONObject: cardDict, options: []),
              let card = try? JSONDecoder().decode(Card.self, from: jsonData) else {
            return nil
        }
        return card
    }
    
    private func createMockPaymentOption(card: Card?, enabled: Bool, reasons: [String]?) -> PaymentOption? {
        var paymentDict: [String: Any] = [
            "name": "Test Payment Method",
            "type": "SAVED_CARD",
            "logoSrc": NSNull(),
            "order": 1,
            "enabled": enabled,
            "clientToken": NSNull(),
            "cnpOptionAvailable": false,
            "acceptedCardTypes": NSNull(),
            "paymentOptions": [],
            "subType": NSNull()
        ]
        
        if let card = card {
            let cardDict: [String: Any] = [
                "token": card.token,
                "expiryMonth": card.expiryMonth,
                "expiryYear": card.expiryYear,
                "type": card.type.cardCode,
                "logoSrc": card.logoUrl,
                "cardHolderName": card.cardholderName,
                "cardType": card.cardType,
                "cnpRequired": card.cnpRequired
            ]
            paymentDict["card"] = cardDict
        } else {
            paymentDict["card"] = NSNull()
        }
        
        if let reasons = reasons {
            paymentDict["reasons"] = reasons
        } else {
            paymentDict["reasons"] = []
        }
        
        guard let jsonData = try? JSONSerialization.data(withJSONObject: paymentDict, options: []),
              let paymentOption = try? JSONDecoder().decode(PaymentOption.self, from: jsonData) else {
            return nil
        }
        return paymentOption
    }
}
