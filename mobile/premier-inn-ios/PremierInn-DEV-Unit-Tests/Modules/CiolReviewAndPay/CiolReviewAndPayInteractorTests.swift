//
//  CiolReviewAndPayInteractorTests.swift
//  PremierInnTests
//
//  Created by Muresan, Andreea (Cognizant) on 02.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
@testable import SimpleNetwork

final class CiolReviewAndPayInteractorTests: XCTestCase {

    private var paymentActions: CiolPaymentActionsResponse?

    var interactor: CiolReviewAndPayInteractor!

    var stay: Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-09"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"
        return try! Stay(dictionary: dictionary)
    }

    override func setUp() {
        super.setUp()
        
        var paymentMethods: [PaymentOption]?
        
        if let url = Bundle(for: type(of: self)).url(forResource: "availablePaymentMethods", withExtension: "json") {
            do {
                let data = try Data(contentsOf: url)
                if let paymentMethodsDict = try JSONSerialization.jsonObject(with: data, options: []) as? [PIDictionary] {
                    let paymentMethodsDictMapped = PaymentMethodsMapper.map(from: paymentMethodsDict)
                    
                    let paymentMethodsData = try JSONSerialization.data(withJSONObject: paymentMethodsDictMapped, options: .prettyPrinted)

                    let response = try JSONDecoder().decode(PaymentMethodsResponse.self, from: paymentMethodsData)
                    paymentMethods = response.paymentMethods
                }
            } catch {
                print(error)
            }
        }
        
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil,
                                                                           hotelName: nil,
                                                                           duration: nil,
                                                                           summary: nil)
        let outstandingBalanceItem = PreStayInteractor.CIOLPriceBreakdownItemViewModel(name: "Outstanding balance",
                                                                                       value: Cost(amount: 313.96,
                                                                                                   currencyCode: "GBP"),
                                                                                       quantity: 1)
        
        let priceBreakdown = PreStayInteractor.CIOLPriceBreakdownViewModel(ctaTitle: "Continue",
                                                                           totalValue: "£313.96",
                                                                           items: [outstandingBalanceItem])
        
        let address = try? Address(dictionary: ["addressline1": "Royal Victoria Dock",
                                                "addressline2": "2 Festoon Way",
                                                "addressline3": "London",
                                                "countryCode": "GB",
                                                "postcode": "E16 1SJ"])
        
        let inputParams = CiolReviewAndPayInputParams(ciolFlow: .myBookings,
                                                      bookerFirstName: "Testus",
                                                      bookingReference: "",
                                                      hotelBrand: .premierInn,
                                                      bookingSummaryViewModel: bookingSummary,
                                                      priceBreakdownViewModel: priceBreakdown,
                                                      address: address,
                                                      defaultAnalyticsParams: PIDictionary(),
                                                      additionalAnalyticsParams: PIDictionary(),
                                                      stay: stay,
                                                      showBannerMessage: false,
                                                      ciolPaymentActions: paymentActions)
        interactor = CiolReviewAndPayInteractor(inputParams: inputParams)
        interactor.bookingDetails = {
            let bookingDetails = BookingDetails.sharedInstance
            
            bookingDetails.booker = try? User(title: "Mr", firstName: "Test", lastName: "McTester")
            bookingDetails.booker?.address = nil
            bookingDetails.paymentMethods = paymentMethods
            return bookingDetails
        }()
    }
    
    override func tearDown() {
        paymentActions = nil
        interactor = nil
        BookingDetails.sharedInstance.reset()
        super.tearDown()
    }
    
    func testSelectPaymentMethod_NewCreditDebitCard() {
        interactor.updateSelectedPaymentMethod(paymentViewModel: BookingDetails.BDPaymentMethodViewModel(type: .newCreditDebitCard,
                                                                                                         imageUrls: [],
                                                                                                         maskedPAN: "",
                                                                                                         cardholder: "",
                                                                                                         expiry: nil,
                                                                                                         selected: true,
                                                                                                         cardName: "VISA",
                                                                                                         acceptedCardAccessibilityLabel: ""))
        XCTAssertEqual(interactor.viewModel.selectedPaymentMethod?.paymentMethodType, PaymentMethodType.newCreditDebitCard)
    }

    func testSelectPaymentMethod_PIBA() {
        interactor.updateSelectedPaymentMethod(paymentViewModel: BookingDetails.BDPaymentMethodViewModel(type: .newBAC,
                                                                                                         imageUrls: [],
                                                                                                         maskedPAN: "",
                                                                                                         cardholder: "",
                                                                                                         expiry: nil,
                                                                                                         selected: true,
                                                                                                         cardName: "PIBA",
                                                                                                         acceptedCardAccessibilityLabel: ""))
        XCTAssertEqual(interactor.viewModel.selectedPaymentMethod?.paymentMethodType, PaymentMethodType.newBAC)
    }
    
    func testSelectPaymentMethod_PayPal() {
        interactor.updateSelectedPaymentMethod(paymentViewModel: BookingDetails.BDPaymentMethodViewModel(type: .paypal,
                                                                                                         imageUrls: [],
                                                                                                         maskedPAN: "",
                                                                                                         cardholder: "",
                                                                                                         expiry: nil,
                                                                                                         selected: true,
                                                                                                         cardName: "PAYPAL",
                                                                                                         acceptedCardAccessibilityLabel: ""))
        XCTAssertEqual(interactor.viewModel.selectedPaymentMethod?.paymentMethodType, PaymentMethodType.paypal)
    }

    func testSelectPaymentMethod_ApplePay() {
        interactor.updateSelectedPaymentMethod(paymentViewModel: BookingDetails.BDPaymentMethodViewModel(type: .applePay,
                                                                                                         imageUrls: [],
                                                                                                         maskedPAN: "",
                                                                                                         cardholder: "",
                                                                                                         expiry: nil,
                                                                                                         selected: true,
                                                                                                         cardName: "APPLE",
                                                                                                         acceptedCardAccessibilityLabel: ""))
        XCTAssertEqual(interactor.viewModel.selectedPaymentMethod?.paymentMethodType, PaymentMethodType.applePay)
    }
    
    func testFormattedBillingAddress() {
        XCTAssertEqual(interactor.viewModel.formattedBillingAddress, "Same as booker's address" + "\nE16 1SJ" )
    }

    // MARK: - PIBA Detection Tests

    func testCheckPIBAPaymentMethodExists_WithPIBASubtype_returnsTrue() {
        let pibaOption = createPaymentOption(subType: "PIBAGB", type: "card")

        interactor.checkPIBAPaymentMethodExists(paymentMethods: [pibaOption])
        XCTAssertTrue(interactor.doesUserHavePIBACard)
    }

    func testCheckPIBAPaymentMethodExists_WhenPrimaryPaymentIsNewBAC_returnsTrue() {
        let mockCard = createCard(
            cardCode: Constants.PaymentCardCodes.businessCard
        )
        let paymentOption = createPaymentOption(
            subType: Constants.PIBAEuro.subType,
            card: mockCard
        )

        BookingDetails.sharedInstance.primaryPaymentMethod = paymentOption
        interactor.checkPIBAPaymentMethodExists(paymentMethods: [])
        XCTAssertTrue(interactor.doesUserHavePIBACard)
    }

    func testCheckPIBAPaymentMethodExists_WhenNoPIBA_returnsFalse() {
        let normalOption = createPaymentOption(subType: "OTHER", type: "card")

        BookingDetails.sharedInstance.primaryPaymentMethod = normalOption
        interactor.checkPIBAPaymentMethodExists(paymentMethods: [normalOption])
        XCTAssertFalse(interactor.doesUserHavePIBACard)
    }

    // MARK: - Third party price breakdown tests

    func testPriceBreakdownForThirdPartyBookingWhenCityTaxAvailable() {
        let inputParams = createInputParams(paymentActions: .cityTaxResponse)

        interactor = CiolReviewAndPayInteractor(inputParams: inputParams)

        let result = interactor.viewModel.priceBreakdownViewModel

        XCTAssertEqual(result.ctaTitle, PILocalizedString("ciolContinueToPay"))
        XCTAssertEqual(result.totalValue, "£13.00")
        XCTAssertEqual(result.items.first?.name, PILocalizedString("ciolCityTaxDisclaimerTitle"))
        XCTAssertEqual(result.items.first?.value, CiolPaymentActionsResponse.cityTaxResponse.cityTax)
        XCTAssertEqual(result.items.first?.quantity, 1)
    }

    func testPriceBreakdownForThirdPartyBookingWhenOutstandingBalanceAvailable() {
        let inputParams = createInputParams(paymentActions: .outstandingBalanceResponse)

        interactor = CiolReviewAndPayInteractor(inputParams: inputParams)

        let result = interactor.viewModel.priceBreakdownViewModel

        XCTAssertEqual(result.ctaTitle, PILocalizedString("Continue"))
        XCTAssertEqual(result.totalValue, "£133.00")
        XCTAssertEqual(result.items.first?.name, PILocalizedString("preStayOutstandingBalance"))
        XCTAssertEqual(result.items.first?.value, CiolPaymentActionsResponse.outstandingBalanceResponse.outstandingBalance)
        XCTAssertEqual(result.items.first?.quantity, 1)
    }
    // MARK: - PIBA CP Warning Message Tests

    func testCheckPIBAPaymentMethodExistsForPIBACPWithFeatureFlagEnabledShouldNotShowWarning() {
        // Arrange
        var stayDict = stay.dictionary
        stayDict["paymentOption"] = "PIBA_CP"
        let pibaCPStay = try! Stay(dictionary: stayDict)

        let inputParams = CiolReviewAndPayInputParams(
            ciolFlow: .myBookings,
            bookerFirstName: "Testus",
            bookingReference: "",
            hotelBrand: .premierInn,
            bookingSummaryViewModel: PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil),
            priceBreakdownViewModel: PreStayInteractor.CIOLPriceBreakdownViewModel(ctaTitle: "Continue", totalValue: "£313.96", items: []),
            address: nil,
            defaultAnalyticsParams: PIDictionary(),
            additionalAnalyticsParams: PIDictionary(),
            stay: pibaCPStay,
            showBannerMessage: false
        )
        let testInteractor = CiolReviewAndPayInteractor(inputParams: inputParams)

        // Act - Mock SettingsManager with feature flag enabled
        let mockRemoteConfig = MockRemoteConfig()
        mockRemoteConfig.featurePIBACPEnabled = true
        SettingsManager.sharedInstance.piRemoteConfig = mockRemoteConfig
        
        let pibaOption = createPaymentOption(subType: "PIBAGB")
        testInteractor.checkPIBAPaymentMethodExists(paymentMethods: [pibaOption])

        // Assert
        XCTAssertFalse(testInteractor.doesUserHavePIBACard, "PIBA unavailable warning should not show for PIBA CP when feature flag is enabled")
    }

    func testCheckPIBAPaymentMethodExistsForPIBACPWithFeatureFlagDisabledShouldShowWarning() {
        // Arrange
        var stayDict = stay.dictionary
        stayDict["paymentOption"] = "PIBA_CP"
        let pibaCPStay = try! Stay(dictionary: stayDict)

        let inputParams = CiolReviewAndPayInputParams(
            ciolFlow: .myBookings,
            bookerFirstName: "Testus",
            bookingReference: "",
            hotelBrand: .premierInn,
            bookingSummaryViewModel: PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil),
            priceBreakdownViewModel: PreStayInteractor.CIOLPriceBreakdownViewModel(ctaTitle: "Continue", totalValue: "£313.96", items: []),
            address: nil,
            defaultAnalyticsParams: PIDictionary(),
            additionalAnalyticsParams: PIDictionary(),
            stay: pibaCPStay,
            showBannerMessage: false
        )
        let testInteractor = CiolReviewAndPayInteractor(inputParams: inputParams)

        // Act - Mock SettingsManager with feature flag disabled
        let mockRemoteConfig = MockRemoteConfig()
        mockRemoteConfig.featurePIBACPEnabled = false
        SettingsManager.sharedInstance.piRemoteConfig = mockRemoteConfig
        
        let pibaOption = createPaymentOption(subType: "PIBAGB")
        testInteractor.checkPIBAPaymentMethodExists(paymentMethods: [pibaOption])

        // Assert - When feature flag is disabled, PIBA CP bookings should show warning
        XCTAssertTrue(testInteractor.doesUserHavePIBACard, "PIBA unavailable warning should show for PIBA CP when feature flag is disabled")
    }

    func testCheckPIBAPaymentMethodExistsForNonPIBACPShouldShowWarning() {
        // Arrange
        var stayDict = stay.dictionary
        stayDict["paymentOption"] = "CC"
        let nonPIBAStay = try! Stay(dictionary: stayDict)

        let inputParams = CiolReviewAndPayInputParams(
            ciolFlow: .myBookings,
            bookerFirstName: "Testus",
            bookingReference: "",
            hotelBrand: .premierInn,
            bookingSummaryViewModel: PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil),
            priceBreakdownViewModel: PreStayInteractor.CIOLPriceBreakdownViewModel(ctaTitle: "Continue", totalValue: "£313.96", items: []),
            address: nil,
            defaultAnalyticsParams: PIDictionary(),
            additionalAnalyticsParams: PIDictionary(),
            stay: nonPIBAStay,
            showBannerMessage: false
        )
        let testInteractor = CiolReviewAndPayInteractor(inputParams: inputParams)

        // Act
        let pibaOption = createPaymentOption(subType: "PIBAGB")
        testInteractor.checkPIBAPaymentMethodExists(paymentMethods: [pibaOption])

        // Assert
        XCTAssertTrue(testInteractor.doesUserHavePIBACard, "PIBA unavailable warning should show for non-PIBA CP")
    }

    func testCheckPIBAPaymentMethodExistsForEuroPIBAShouldShowWarning() {
        // Arrange
        var stayDict = stay.dictionary
        stayDict["paymentOption"] = "CC"
        let nonPIBAStay = try! Stay(dictionary: stayDict)

        let inputParams = CiolReviewAndPayInputParams(
            ciolFlow: .myBookings,
            bookerFirstName: "Testus",
            bookingReference: "",
            hotelBrand: .premierInn,
            bookingSummaryViewModel: PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil),
            priceBreakdownViewModel: PreStayInteractor.CIOLPriceBreakdownViewModel(ctaTitle: "Continue", totalValue: "£313.96", items: []),
            address: nil,
            defaultAnalyticsParams: PIDictionary(),
            additionalAnalyticsParams: PIDictionary(),
            stay: nonPIBAStay,
            showBannerMessage: false
        )
        let testInteractor = CiolReviewAndPayInteractor(inputParams: inputParams)

        // Act
        let pibaEuroOption = createPaymentOption(subType: "PIBADE")
        testInteractor.checkPIBAPaymentMethodExists(paymentMethods: [pibaEuroOption])

        // Assert
        XCTAssertTrue(testInteractor.doesUserHavePIBACard, "PIBA unavailable warning should show for Euro PIBA")
    }
}

extension CiolReviewAndPayInteractorTests {
    private func createPaymentOption(
        subType: String?,
        type: String = "card",
        card: Card? = nil
    ) -> PaymentOption {
        PaymentOption(
            name: "Test",
            type: type,
            logoSrc: nil,
            order: 0,
            acceptedCardTypes: nil,
            enabled: true,
            cnpOptionAvailable: true,
            card: card,
            paymentOptions: nil,
            reasons: nil,
            clientToken: nil,
            subType: subType
        )
    }

    private func createCard(
        cardCode: String = Constants.PaymentCardCodes.businessCard
    ) -> Card {
        let cardType = CardType(
            cardCode: cardCode,
            cardName: ""
        )
        return Card(
            token: "",
            expiryMonth: "",
            expiryYear: "",
            type: cardType,
            logoUrl: "",
            cardholderName: "",
            cardType: "",
            cnpRequired: false
        )
    }

    func createInputParams(paymentActions: CiolPaymentActionsResponse?) -> CiolReviewAndPayInputParams {
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(
            image: nil,
            hotelName: nil,
            duration: nil,
            summary: nil
        )

        let outstandingBalanceItem = PreStayInteractor.CIOLPriceBreakdownItemViewModel(
            name: "Outstanding balance",
            value: Cost(
                amount: 313.96,
                currencyCode: "GBP"
            ),
            quantity: 1
        )
        
        let priceBreakdown = PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: "Continue",
            totalValue: "£313.96",
            items: [outstandingBalanceItem]
        )
        
        let address = try? Address(
            dictionary: [
                "addressline1": "Royal Victoria Dock",
                "addressline2": "2 Festoon Way",
                "addressline3": "London",
                "countryCode": "GB",
                "postcode": "E16 1SJ"
            ]
        )
        
        return CiolReviewAndPayInputParams(
            ciolFlow: .myBookings,
            bookerFirstName: "Testus",
            bookingReference: "",
            hotelBrand: .premierInn,
            bookingSummaryViewModel: bookingSummary,
            priceBreakdownViewModel: priceBreakdown,
            address: address,
            defaultAnalyticsParams: PIDictionary(),
            additionalAnalyticsParams: PIDictionary(),
            stay: stay,
            showBannerMessage: false,
            ciolPaymentActions: paymentActions
        )
    }
}
