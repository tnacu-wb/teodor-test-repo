//
//  CiolReviewAndPayViewControllerTests.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class CiolReviewAndPayViewControllerTests: XCTestCase {

    var viewController: CiolReviewAndPayViewController!
    var mockEventHandler: MockCiolReviewAndPayEventHandler!
    
    override func setUp() {
        super.setUp()
        viewController = CiolReviewAndPayViewController()
        mockEventHandler = MockCiolReviewAndPayEventHandler()
        viewController.eventHandler = mockEventHandler
    }
    
    override func tearDown() {
        viewController = nil
        mockEventHandler = nil
        super.tearDown()
    }
    
    func testViewDidLoad_CallsPresenterViewIsReady() {
        // Act
        viewController.viewDidLoad()
        
        // Assert
        XCTAssertTrue(mockEventHandler.isViewIsReadyCalled, "viewDidLoad should call viewIsReady on the presenter")
    }
    
    func testTapOnPayCTA() {
        viewController.buttonDidTap()
        
        XCTAssertTrue(mockEventHandler.isHandlePayButtonTapCalled, "buttonDidTap should call handlePayButtonTap on the presenter")
    }
    
    func testViewModel() {
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

        let ciolConfirmationDetails = CiolConfirmationDetails(bookerFirstName: "Tester",
                                                              hotelBrand: .premierInn,
                                                              ciolStartFlow: .myBookings,
                                                              hotelImage: nil,
                                                              analyticsInfo: [:],
                                                              stay: stay)
        
        let formattedAddress = [PILocalizedString("ciolSameAsBookersAddress"), "W1D DCS"].filter { !$0.isEmpty }.joined(separator: "\n")

        let pibaVM = CiolReviewAndPayInteractor.CIOLPIBAUnavailableViewModel(
            message: "Mock",
            shouldShow: false
        )

        let testViewModel = CiolReviewAndPayInteractor.ViewModel(bookingSummaryViewModel: bookingSummary,
                                                                 priceBreakdownViewModel: priceBreakdown,
                                                                 formattedBillingAddress: formattedAddress,
                                                                 billingAddress: StoredAddressModel(),
                                                                 confirmationDetails: ciolConfirmationDetails,
                                                                 isBillingFieldOn: false,
                                                                 isGermanHotel: false,
                                                                 deRegCardPaymentInformationMessage: NSAttributedString(),
        paymentMethodPIBAUnavailable: pibaVM)

        let viewModel = viewController.tableViewModel(with: testViewModel)
        XCTAssertEqual(viewModel.sections.count, 3)
        
        let summarySection = viewModel.sections[0]
        XCTAssertNil(summarySection.header)
        XCTAssertEqual(summarySection.rows.count, 1)
        
        let paymentSection = viewModel.sections[1]
        XCTAssertNotNil(paymentSection.header)
        XCTAssertEqual(summarySection.rows.count, 1)
        
        let billingSection = viewModel.sections[2]
        XCTAssertNotNil(paymentSection.header)
        XCTAssertEqual(summarySection.rows.count, 1)
    }
    
    func testShowCountriesView() {
        mockEventHandler.showCountriesView(indexPath: IndexPath())

        XCTAssertTrue(mockEventHandler.isShowCountriesCalled)
    }
    
    func testShowPostcodePicker() {
        mockEventHandler.showPostcodePicker(with: "")

        XCTAssertTrue(mockEventHandler.isShowPostcodeCalled)
    }
    
    func testUpdateAddress() {
        mockEventHandler.updateAddress(with: .postCode(""))

        XCTAssertTrue(mockEventHandler.isUpdateAddressCalled)
    }
    
    func testShowBillingAddressField() {
        mockEventHandler.showBillingAddressField(true)

        XCTAssertTrue(mockEventHandler.isBillingAddressCalled)
    }
}

// Mock Presenter
class MockCiolReviewAndPayEventHandler: ReviewAndPayViewEventHandler {
    func showCountriesView(indexPath: IndexPath) {
        isShowCountriesCalled = true
    }
    
    func showPostcodePicker(with postCode: String?) {
        isShowPostcodeCalled = true
    }
    
    func updateAddress(with addressLine: AddressLineType) {
        isUpdateAddressCalled = true
    }
    
    func showBillingAddressField(_ show: Bool) {
        isBillingAddressCalled = true
    }
    
    var isViewIsReadyCalled = false
    var isHandlePayButtonTapCalled = false
    var isThreeCIPageSetUp = false
    var isUpdateSelectedPMCalled = false
    var isShowCountriesCalled = false
    var isShowPostcodeCalled = false
    var isUpdateAddressCalled = false
    var isBillingAddressCalled = false
    
    func viewIsReady() {
        isViewIsReadyCalled = true
    }
    
    func handlePayButtonTap() {
        isHandlePayButtonTapCalled = true
    }
    

    func setupThreeCIpage(for response: CCCPPaymentResponse) {
        isThreeCIPageSetUp = true
    }

    func updateSelectedPaymentMethod(paymentViewModel: PaymentMethodViewModelType) {
        isUpdateSelectedPMCalled = true
    }

    func didPop() {

    }
    
    func trackPriceBreakdownTapAnalytics() { }
}
