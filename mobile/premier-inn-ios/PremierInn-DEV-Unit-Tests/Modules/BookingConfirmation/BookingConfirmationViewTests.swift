//
//  BookingConfirmationViewTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 11/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import PassKit
import SimpleNetwork
@testable import PremierInn

private final class MockPresenter: BookingConfirmationPresenterInput {

    static let identifier = "fake-indentifier"
    var phoneNumber: String? = nil
    var configureCellDidCall = false
    var viewIsReadyDidCall = false
    var callHotelDidTap = false
    var cancelNavigationDidCall = false
    var showDirectionDidCall = false
    var addToCalendarDidCall = false
    var eventExistDidCall = false
    var addToWalletDidCall = false
    var cityTaxFormButtonDidTapDidCall = false
    var hotelInfoDidTapDidCall = false
    var priceBreakdownDidTapDidCall = false
    var amendDidTapDidCall = false
    var faqDidTapDidCall = false
    var checkInOnlineDidTapDidCall = false
    var passFetchSuccessfulDidCall = false
    var passFetchFailedDidCall = false
    var passExistsAlreadyDidCall = false
    var hotelFetchedDidCall = false
    var hotelFetchErrorDidCall = false
    var redirectToWebForOperaDidCall = false
    var tapOnCheckInDidCall = false
    var parkingInformationDidCall = false
    var instructionsDidCall = false
    var tapOnCheckOutDidCall = false

    var eventExistsInCalendar: Bool {

        eventExistDidCall = true
        return false
    }

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func reloadViewModel() {
        // Intentional empty
    }

    func openExistingWalletDidTap() {

    }

    func imHereDidTap() {

    }

    func addKeyToWalletTap() {

    }

    func viewKeyInWallet() {

    }

    func howYourKeyWorksDidTap() {}

    func callHotelButtonDidTap() {

        callHotelDidTap = true
    }

    func redirectToWeb() {

        redirectToWebForOperaDidCall = true
    }

    func cancelNavigationButtonDidTap() {

        cancelNavigationDidCall = true
    }

    func showDirections(withSender sender: UIView) {

        showDirectionDidCall = true
    }

    func addToCalendar() {

        addToCalendarDidCall = true
    }

    func addToWallet() {

        addToWalletDidCall = true
    }
    
    func cityTaxFormButtonDidTap() {

        cityTaxFormButtonDidTapDidCall = true
    }

    func hotelInfoDidTap() {

        hotelInfoDidTapDidCall = true
    }

    func priceBreakdownDidTap() {

        priceBreakdownDidTapDidCall = true
    }

    func amendDidTap() {

        amendDidTapDidCall = true
    }

    func faqDidTap(url: URL?) {

        faqDidTapDidCall = true
    }

    func checkInOnlineDidTap() {

        checkInOnlineDidTapDidCall = true
    }

    func passFetchSuccessful(with pass: PKPass?) {

        passFetchSuccessfulDidCall = true
    }

    func passFetchFailed(with error: Error?) {

        passFetchFailedDidCall = true
    }

    func passExistsAlready() {

        passExistsAlreadyDidCall = true
    }

    func hotelFetched() {

        hotelFetchedDidCall = true
    }

    func hotelFetchError(error: Error?) {

        hotelFetchErrorDidCall = true
    }
    
    func tapOnCheckIn() {
        tapOnCheckInDidCall =  true
    }
    
    func parkingInfoDidTap() {
        parkingInformationDidCall = true
    }
    
    func instructionsDidTap() {
        instructionsDidCall = true
    }

    func resendInvoiceDidTap() {

    }
    
    func tapOnCheckOut() {
        tapOnCheckOutDidCall = true
    }
}

final class BookingConfirmationViewTests: XCTestCase {

    fileprivate var presenter: MockPresenter!
    private var viewController: BookingConfirmationViewController!
    private var analytics: MockAnalyticsManager!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        presenter = MockPresenter()

        viewController = BookingConfirmationViewController()
        viewController.presenter = presenter
        analytics = MockAnalyticsManager()
        viewController.analytics = analytics
    }
    
    override func tearDown() {

        presenter = nil
        viewController = nil
        analytics = nil

        super.tearDown()
    }


    private var hotel: Hotel? {
        guard let hotelFileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json") else { return nil }
        guard let hotelData = try? Data(contentsOf: hotelFileURL) else { return nil }
        guard let hotelJSON = try? JSONSerialization.jsonObject(with: hotelData, options: .allowFragments) as? PIDictionary else { return nil }
        guard let hotel = try? Hotel(dictionary: hotelJSON) else { return nil }

        return hotel
    }

    private func getStay(amendable: Bool? = false,
                         cancelled: Bool? = false,
                         arrivalDateString: String? = nil,
                         checkOutDateString: String? = nil) -> Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = arrivalDateString ?? "2017-10-09"
        dictionary["amendable"] = amendable
        dictionary["cancelled"] = cancelled
        dictionary["checkOutDate"] = checkOutDateString ?? "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"
        dictionary["basketStatus"] = "PRE_CHECKED_IN"

        return try! Stay(dictionary: dictionary)
    }

     private func bookingConfirmationViewModelParams(summary: Stay,
                                                     passSaved: Bool? = false) -> BookingConfirmationViewModelParams {

         return BookingConfirmationViewModelParams(hotel!,
                                                   summary: summary,
                                                   reservation: nil,
                                                   bookedUpsells: nil,
                                                   isCheckInOnlineAvailable: false,
                                                   isCheckOutOnlineAvailable: false,
                                                   shouldShowBannerInfo: false,
                                                   shouldShowInfo: false,
                                                   passSaved: passSaved ?? false,
                                                   isAmended: false,
                                                   isOutOfDate: false,
                                                   resendInvoiceStatus: .notRequested)
     }

    // MARK: - Tests

    func testView_whenViewAppears_invokesTrackState() {

        UserSessionManager.sharedInstance.piUserLoggedOut()
        viewController.beginAppearanceTransition(true, animated: false)
        viewController.endAppearanceTransition()

        let state = analytics.states.first

        XCTAssertEqual(state, "iOS:PI:UK: Booking Details")
    }

    func testViewDidAppear() {

        viewController.viewDidAppear(true)
        XCTAssertTrue(presenter.viewIsReadyDidCall)
    }
    
    //    func testLoginView() {
    //
    //        let table = UITableView(frame: CGRect(x: 0, y: 0, width: 320, height: 500))
    //        table.delegate = viewController
    //        table.dataSource = controller
    //        table.register(UITableViewCell.self, forCellReuseIdentifier: MockPresenter.identifier)
    //
    //        controller.viewDidLoad()
    //        controller.cancelButtonDidTap()
    //        controller.selectedAddToCalendar()
    //        controller.addToWalletButtonDidTap()
    //        _ = controller.eventExistsInCalendar
    //
    //        XCTAssertTrue(presenter.viewIsReadyDidCall)
    //        XCTAssertTrue(presenter.cancelNavigationDidCall)
    //        XCTAssertTrue(presenter.numberOfSectionsDidCall)
    //        XCTAssertTrue(presenter.numberOfItemsDidCall)
    //        XCTAssertTrue(presenter.identifierForCellDidCall)
    //        XCTAssertTrue(presenter.titleForCellDidCall)
    //        XCTAssertTrue(presenter.subtitleForCellDidCall)
    //        XCTAssertTrue(presenter.didTapOnCellDidCall)
    //        XCTAssertTrue(presenter.callHotelDidTap)
    //        XCTAssertTrue(presenter.showDirectionDidCall)
    //        XCTAssertTrue(presenter.addToCalendarDidCall)
    //        XCTAssertTrue(presenter.eventExistDidCall)
    //        XCTAssertTrue(presenter.addToWalletDidCall)
    //    }

    func testCheckedInStatus() {
        let stay = getStay(amendable: true, checkOutDateString: DateFormatter.parameterFormatter.string(from: Date()))
        let params = makeMockParams(stay: stay)

        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)

        let section = viewModel.sections.first!
        let row = section.rows.first!
        XCTAssertEqual(row.tag, BookingConfirmationRowType.status.rawValue)
        
    }

    func testHotelInfoBannerAmendTrueFlagTrue() {

        let stay = getStay(amendable: true)
        let params = makeMockParams(stay: stay)

        let remoteConfig = MockRemoteConfig(shouldShowAmendBanner: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)


        let section = viewModel.sections.first!
        let row = section.rows[safe: 0]
        XCTAssertEqual(row?.tag, BookingConfirmationRowType.hotelInfoAlert.rawValue)
    }

    func testHotelInfoBannerAmendTrueFlagFalse() {

        let stay = getStay(amendable: true)
        let params = makeMockParams(stay: stay)


        let remoteConfig = MockRemoteConfig(shouldShowAmendBanner: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)


        let section = viewModel.sections.first!
        let row = section.rows.first!
        XCTAssertFalse(row.tag == BookingConfirmationRowType.hotelInfoAlert.rawValue)

    }

    func testHotelInfoBannerAmendFalseFlagTrue() {

        let stay = getStay(amendable: false)

        let params = makeMockParams(stay: stay)

        let remoteConfig = MockRemoteConfig(shouldShowAmendBanner: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)


        let section = viewModel.sections.first!
        let row = section.rows.first!
        XCTAssertFalse(row.tag == BookingConfirmationRowType.hotelInfoAlert.rawValue)
    }

    func testHotelInfoBannerAmendFalseFlagFalse() {
        let stay = getStay(amendable: false)
        let params = makeMockParams(stay: stay)

        let remoteConfig = MockRemoteConfig(shouldShowAmendBanner: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)


        let section = viewModel.sections.first!
        let row = section.rows.first!
        XCTAssertFalse(row.tag == BookingConfirmationRowType.hotelInfoAlert.rawValue)
    }

    func testErrorBannerShowsFailedConfirmation() {
        let stay = getStay(amendable: false)
        let params = BookingConfirmationViewModelParams(
            hotel!,
            summary: stay,
            reservation: nil,
            bookedUpsells: nil,
            isCheckInOnlineAvailable: false,
            isCheckOutOnlineAvailable: false,
            shouldShowBannerInfo: false,
            shouldShowInfo: false,
            passSaved: false,
            isAmended: false,
            isOutOfDate: true,
            resendInvoiceStatus: .notRequested
        )


        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)


        let section = viewModel.sections.first!
        let row = section.rows.first!
        XCTAssertTrue(row.tag == BookingConfirmationRowType.error.rawValue)
    }

    func testErrorBannerDoesNotShowSuccessConfirmation() {
        let stay = getStay(amendable: false)
        let params = makeMockParams(stay: stay)

        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)


        let section = viewModel.sections.first!
        let row = section.rows.first!
        XCTAssertFalse(row.tag == BookingConfirmationRowType.error.rawValue)
    }

//     Add to Wallet
    func testAddToWalletButton() {

        var remoteConfig = MockRemoteConfig(appleWalletEnabled: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let today = Date()
        guard let dateInTwoDays = Calendar.current.date(byAdding: .day, value: 2, to: today),
              let dateInFourDays = Calendar.current.date(byAdding: .day, value: 4, to: today),
              let dateFourDaysAgo = Calendar.current.date(byAdding: .day, value: -4, to: today),
              let dateTwoDaysAgo = Calendar.current.date(byAdding: .day, value: -2, to: today) else {
            XCTFail("failed to set up arrival and departure dates")
            return
        }

        let dateInTwoDaysString = DateFormatter.parameterFormatter.string(from: dateInTwoDays)
        let dateInFourDaysString = DateFormatter.parameterFormatter.string(from: dateInFourDays)
        let dateFourDaysAgoString = DateFormatter.parameterFormatter.string(from: dateFourDaysAgo)
        let dateTwoDaysAgoString = DateFormatter.parameterFormatter.string(from: dateTwoDaysAgo)

        // happy path
        var stay = getStay(cancelled: false,
                           arrivalDateString: dateInTwoDaysString,
                           checkOutDateString: dateInFourDaysString)

        var params = bookingConfirmationViewModelParams(summary: stay,
                                                        passSaved: false)

        var testViewModel = BookingConfirmationViewModel.createFrom(params)!

        XCTAssertTrue(testViewModel.shouldShowAppleWallet)
        XCTAssertFalse(testViewModel.shouldShowPassExistsButton)

        var viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)

        var section = viewModel.sections.first!
        let row = section.rows[safe: 5]
        XCTAssertTrue(row?.tag == BookingConfirmationRowType.wallet.rawValue)
        XCTAssertTrue(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.wallet.rawValue }))
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.passExists.rawValue }))

        // cancelled booking
        stay = getStay(cancelled: true,
                       arrivalDateString: dateInTwoDaysString,
                       checkOutDateString: dateInFourDaysString)

        params = bookingConfirmationViewModelParams(summary: stay,
                                                    passSaved: false)

        testViewModel = BookingConfirmationViewModel.createFrom(params)!

        XCTAssertFalse(testViewModel.shouldShowAppleWallet)
        XCTAssertFalse(testViewModel.shouldShowPassExistsButton)

        viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        section = viewModel.sections.first!
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.wallet.rawValue }))
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.passExists.rawValue }))

        // pass saved already
        stay = getStay(cancelled: false,
                       arrivalDateString: dateInTwoDaysString,
                       checkOutDateString: dateInFourDaysString)

        params = bookingConfirmationViewModelParams(summary: stay,
                                                    passSaved: true)

        testViewModel = BookingConfirmationViewModel.createFrom(params)!

        XCTAssertFalse(testViewModel.shouldShowAppleWallet)
        XCTAssertTrue(testViewModel.shouldShowPassExistsButton)

        viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        section = viewModel.sections.first!
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.wallet.rawValue }))
        XCTAssertTrue(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.passExists.rawValue }))

        // past booking
        stay = getStay(cancelled: false,
                       arrivalDateString: dateFourDaysAgoString,
                       checkOutDateString: dateTwoDaysAgoString)

        params = bookingConfirmationViewModelParams(summary: stay,
                                                    passSaved: false)

        testViewModel = BookingConfirmationViewModel.createFrom(params)!

        XCTAssertFalse(testViewModel.shouldShowAppleWallet)
        XCTAssertFalse(testViewModel.shouldShowPassExistsButton)

        viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        section = viewModel.sections.first!
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.wallet.rawValue }))
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.passExists.rawValue }))

        // ongoing booking
        stay = getStay(cancelled: false,
                       arrivalDateString: dateTwoDaysAgoString,
                       checkOutDateString: dateInTwoDaysString)

        params = bookingConfirmationViewModelParams(summary: stay,
                                                    passSaved: false)

        testViewModel = BookingConfirmationViewModel.createFrom(params)!

        XCTAssertTrue(testViewModel.shouldShowAppleWallet)
        XCTAssertFalse(testViewModel.shouldShowPassExistsButton)

        viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        section = viewModel.sections.first!
        XCTAssertTrue(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.wallet.rawValue }))
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.passExists.rawValue }))

        // feature flag off
        remoteConfig = MockRemoteConfig(appleWalletEnabled: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        stay = getStay(cancelled: false,
                       arrivalDateString: dateInTwoDaysString,
                       checkOutDateString: dateInFourDaysString)

        params = bookingConfirmationViewModelParams(summary: stay,
                                                    passSaved: false)

        testViewModel = BookingConfirmationViewModel.createFrom(params)!

        XCTAssertFalse(testViewModel.shouldShowAppleWallet)
        XCTAssertFalse(testViewModel.shouldShowPassExistsButton)

        viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        section = viewModel.sections.first!
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.wallet.rawValue }))
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.passExists.rawValue }))

        // pre checked out

        remoteConfig = MockRemoteConfig(appleWalletEnabled: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        stay = getStay(cancelled: false,
                       arrivalDateString: dateInTwoDaysString,
                       checkOutDateString: dateInFourDaysString)
        stay.basketStatus = .preCheckedOut

        params = bookingConfirmationViewModelParams(summary: stay,
                                                    passSaved: false)

        testViewModel = BookingConfirmationViewModel.createFrom(params)!

        XCTAssertFalse(testViewModel.shouldShowAppleWallet)
        XCTAssertFalse(testViewModel.shouldShowPassExistsButton)

        viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        section = viewModel.sections.first!
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.wallet.rawValue }))
        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.passExists.rawValue }))

    }
    
    func testShowParkingInformationRow() {
        let stay = getStay(amendable: false)
        
        let params = BookingConfirmationViewModelParams(
            hotel!,
            summary: stay,
            reservation: nil,
            bookedUpsells: nil,
            isCheckInOnlineAvailable: false,
            isCheckOutOnlineAvailable: false,
            shouldShowBannerInfo: false,
            shouldShowInfo: false,
            passSaved: false,
            isAmended: false,
            isOutOfDate: true,
            resendInvoiceStatus: .notRequested
        )
        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        let section = viewModel.sections.first!

        XCTAssertFalse(section.rows.contains(where: { $0.tag == BookingConfirmationRowType.parkingInformation.rawValue }))
    }
    
    func testTapOnCheckOutDidCall() {
        presenter.tapOnCheckOut()
        XCTAssertTrue(presenter.tapOnCheckOutDidCall)
    }

    func testResendInvoiceSuccessfulBanner() {

        let stay = getStay(amendable: false)

        let params = BookingConfirmationViewModelParams(
            hotel!,
            summary: stay,
            reservation: nil,
            bookedUpsells: nil,
            isCheckInOnlineAvailable: false,
            isCheckOutOnlineAvailable: false,
            shouldShowBannerInfo: false,
            shouldShowInfo: false,
            passSaved: false,
            isAmended: false,
            isOutOfDate: false,
            resendInvoiceStatus: .success
        )


        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        XCTAssertEqual(testViewModel.resendInvoiceInfoViewModel!.message!.string, "Your invoice was sent to APPS")
        XCTAssertEqual(testViewModel.resendInvoiceInfoViewModel!.notificationStyle, NotificationStyle.success)


        let section = viewModel.sections.first!
        let row = section.rows[1]
        XCTAssertTrue(row.tag == BookingConfirmationRowType.resendBanner.rawValue)

    }

    func testResendInvoiceFailedBanner() {

        let stay = getStay(amendable: false)

        let params = BookingConfirmationViewModelParams(
            hotel!,
            summary: stay,
            reservation: nil,
            bookedUpsells: nil,
            isCheckInOnlineAvailable: false,
            isCheckOutOnlineAvailable: false,
            shouldShowBannerInfo: false,
            shouldShowInfo: false,
            passSaved: false,
            isAmended: false,
            isOutOfDate: false,
            resendInvoiceStatus: .failed
        )


        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)

        XCTAssertEqual(testViewModel.resendInvoiceInfoViewModel!.message!.string, "Oops! Something went wrong – please try again")
        XCTAssertEqual(testViewModel.resendInvoiceInfoViewModel!.notificationStyle, NotificationStyle.error)


        let section = viewModel.sections.first!
        let row = section.rows[1]
        XCTAssertTrue(row.tag == BookingConfirmationRowType.resendBanner.rawValue)
    }

    func testResendInvoiceNoBanner() {

        let stay = getStay(amendable: false)
        let params = makeMockParams(stay: stay)

        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let viewModel = viewController.bookingConfirmationViewModel(with: testViewModel)

        XCTAssertNil(testViewModel.resendInvoiceInfoViewModel)


        let section = viewModel.sections.first!
        let row = section.rows[1]
        XCTAssertFalse(row.tag == BookingConfirmationRowType.resendBanner.rawValue)
    }

    func testCreateFrom_whenPreCheckedIn_setsBookingStatusToCheckIn() {
        // Arrange
        let stay = makeUpcomingStay()
        stay.basketStatus = .preCheckedIn
        stay.stayBookingStatus = nil

        let params = makeMockParams(stay: stay)

        // Act
        let viewModel = BookingConfirmationViewModel.createFrom(params)

        // Assert
        XCTAssertEqual(viewModel?.bookingCheckStatus, .checkIn)
    }

    func testCreateFrom_whenPreCheckedOut_setsBookingStatusToCheckOut() {
        // Arrange
        let stay = makeUpcomingStay()
        stay.basketStatus = .preCheckedOut

        let params = makeMockParams(stay: stay)

        // Act
        let viewModel = BookingConfirmationViewModel.createFrom(params)

        // Assert
        XCTAssertEqual(viewModel?.bookingCheckStatus, .checkOut)
    }

    func testCreateFrom_whenPreCheckedOutIsNil_setsBookingStatusToCheckIn() {
        // Arrange
        let stay = makeUpcomingStay()
        stay.basketStatus = nil
        stay.stayBookingStatus = .checkedIn

        let params = makeMockParams(stay: stay)

        // Act
        let viewModel = BookingConfirmationViewModel.createFrom(params)

        // Assert
        XCTAssertEqual(viewModel?.bookingCheckStatus, .checkIn)
    }
    
    // MARK: - Key Limit Message UI Tests
    
    func testKeyLimitMessageRowIsPresentWhenConditionsMet() {
        // Skip test if not running on iPhone (digital keys are phone-only)
        guard UIDevice.current.userInterfaceIdiom == .phone else {
            return
        }
        
        // Arrange: Enable digital keys feature flag
        let remoteConfig = MockRemoteConfig(featureDigitalKeys: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        
        // Arrange: German hotel with digital keys enabled and pass in wallet
        let germanHotel = makeGermanHotel()
        let stay = makeStayWithDigitalKey(isDigitalKeyEnabled: true, userHasPassInWallet: true)
        let params = BookingConfirmationViewModelParams(
            germanHotel,
            summary: stay,
            reservation: nil,
            bookedUpsells: nil,
            isCheckInOnlineAvailable: false,
            isCheckOutOnlineAvailable: false,
            shouldShowBannerInfo: false,
            shouldShowInfo: false,
            passSaved: false,
            isAmended: false,
            isOutOfDate: false,
            resendInvoiceStatus: .notRequested
        )
        
        // Act
        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let formekaViewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        
        // Assert
        let firstSection = formekaViewModel.sections.first!
        let keyLimitRow = firstSection.rows.first { $0.tag == BookingConfirmationRowType.keyLimitMessage.rawValue }
        
        XCTAssertNotNil(keyLimitRow, "Key limit message row should be present in the table view")
    }
    
    func testKeyLimitMessageRowIsAbsentWhenDigitalKeysDisabled() {
        // Arrange: German hotel with digital keys DISABLED
        let germanHotel = makeGermanHotel()
        let stay = makeStayWithDigitalKey(isDigitalKeyEnabled: false, userHasPassInWallet: true)
        let params = BookingConfirmationViewModelParams(
            germanHotel,
            summary: stay,
            reservation: nil,
            bookedUpsells: nil,
            isCheckInOnlineAvailable: false,
            isCheckOutOnlineAvailable: false,
            shouldShowBannerInfo: false,
            shouldShowInfo: false,
            passSaved: false,
            isAmended: false,
            isOutOfDate: false,
            resendInvoiceStatus: .notRequested
        )
        
        // Act
        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let formekaViewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        
        // Assert
        let firstSection = formekaViewModel.sections.first!
        let keyLimitRow = firstSection.rows.first { $0.tag == BookingConfirmationRowType.keyLimitMessage.rawValue }
        
        XCTAssertNil(keyLimitRow, "Key limit message row should NOT be present when digital keys are disabled")
    }
    
    func testKeyLimitMessageRowIsAbsentForUKHotel() {
        // Arrange: UK hotel with digital keys enabled
        let stay = makeStayWithDigitalKey(isDigitalKeyEnabled: true, userHasPassInWallet: true)
        let params = makeMockParams(stay: stay)
        
        // Act
        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let formekaViewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        
        // Assert
        let firstSection = formekaViewModel.sections.first!
        let keyLimitRow = firstSection.rows.first { $0.tag == BookingConfirmationRowType.keyLimitMessage.rawValue }
        
        XCTAssertNil(keyLimitRow, "Key limit message row should NOT be present for UK hotels")
    }
    
    func testKeyLimitMessageRowIsAbsentWhenNoPassInWallet() {
        // Arrange: German hotel with digital keys enabled but NO pass in wallet
        let germanHotel = makeGermanHotel()
        let stay = makeStayWithDigitalKey(isDigitalKeyEnabled: true, userHasPassInWallet: false)
        let params = BookingConfirmationViewModelParams(
            germanHotel,
            summary: stay,
            reservation: nil,
            bookedUpsells: nil,
            isCheckInOnlineAvailable: false,
            isCheckOutOnlineAvailable: false,
            shouldShowBannerInfo: false,
            shouldShowInfo: false,
            passSaved: false,
            isAmended: false,
            isOutOfDate: false,
            resendInvoiceStatus: .notRequested
        )
        
        // Act
        let testViewModel = BookingConfirmationViewModel.createFrom(params)!
        let formekaViewModel = viewController.bookingConfirmationViewModel(with: testViewModel)
        
        // Assert
        let firstSection = formekaViewModel.sections.first!
        let keyLimitRow = firstSection.rows.first { $0.tag == BookingConfirmationRowType.keyLimitMessage.rawValue }
        
        XCTAssertNil(keyLimitRow, "Key limit message row should NOT be present when user has no pass in wallet")
    }
}

private extension BookingConfirmationViewTests {
    func makeMockParams(stay: Stay) -> BookingConfirmationViewModelParams{
         return BookingConfirmationViewModelParams(
            hotel!,
            summary: stay,
            reservation: nil,
            bookedUpsells: nil,
            isCheckInOnlineAvailable: false,
            isCheckOutOnlineAvailable: false,
            shouldShowBannerInfo: false,
            shouldShowInfo: false,
            passSaved: false,
            isAmended: false,
            isOutOfDate: false,
            resendInvoiceStatus: .notRequested
        )
    }
    
    func makeUpcomingStay() -> Stay {
        let today = Date()
        let arrival = Calendar.current.date(byAdding: .day, value: 2, to: today)!
        let checkout = Calendar.current.date(byAdding: .day, value: 4, to: today)!
        
        return getStay(arrivalDateString: DateFormatter.parameterFormatter.string(from: arrival),
                       checkOutDateString: DateFormatter.parameterFormatter.string(from: checkout))
        
    }
    
    func makeGermanHotel() -> Hotel {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "BERLNR"
        dictionary["hotelName"] = "Premier Inn Berlin"
        dictionary["hotelLatitude"] = 52.520008
        dictionary["hotelLongitude"] = 13.404954
        dictionary["brand"] = "PID"
        dictionary["address"] = [
            "addressLine1": "Test Street 1",
            "city": "Berlin",
            "postcode": "10115",
            "country": "Germany"
        ]
        
        return try! Hotel(dictionary: dictionary)
    }
    
    func makeStayWithDigitalKey(
        isDigitalKeyEnabled: Bool,
        userHasPassInWallet: Bool
    ) -> Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "BERLNR"
        dictionary["hotelName"] = "Premier Inn Berlin"
        dictionary["hotelLatitude"] = 52.520008
        dictionary["hotelLongitude"] = 13.404954
        dictionary["lastName"] = "TestUser"
        dictionary["identifier"] = "TEST123456"
        dictionary["arrivalDate"] = "2026-07-01"
        dictionary["checkOutDate"] = "2026-07-03"
        dictionary["isDigitalKey"] = isDigitalKeyEnabled
        dictionary["basketStatus"] = "PRE_CHECKED_IN"
        
        // Set digitalKeyIdentifier if we want the pass in wallet
        if userHasPassInWallet {
            dictionary["digitalKeyIdentifier"] = "TEST_KEY_ID_123"
        }
        
        let stay = try! Stay(dictionary: dictionary)
        
        // Mock the keyManager to return the desired value
        if userHasPassInWallet {
            stay.keyManager = MockPassManager(isInPassManager: true)
        }
        
        return stay
    }
}
