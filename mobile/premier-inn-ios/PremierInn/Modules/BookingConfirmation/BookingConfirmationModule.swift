//
//  BookingConfirmationModule.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 05/07/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import EventKit
import PassKit

enum BookingConfirmationModule {
    static func build(summary: Stay, isBookingFlowEnd: Bool) -> UIViewController {
        let controller = BookingConfirmationViewController()

        controller.presenter = {
            let presenter = BookingConfirmationPresenter()
            presenter.view = controller

            let interactor = BookingConfirmationInteractor(summary: summary, isBookingFlowComplete: isBookingFlowEnd)
            interactor.dataProvider = RequestsManager()
            interactor.presenter = presenter

            presenter.interactor = interactor

            let router = BookingConfirmationRouter()
            router.viewController = controller
            router.presenter = presenter

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

protocol BookingConfirmationViewInput: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }

    func update(with viewModel: BookingConfirmationViewModel)
    func scrollToTop()
    func setScreenTitle(title: String)
    func showLoadingIndicator()
    func hideLoadingIndicator()
    func showError(title: String, message: String?, shouldDie: Bool)
    func promptUserToEnableCalendarAccess()
    func disableBackNavigation(shouldEnableGestureSwipe: Bool)
    func showCalendarPrompt(
        fullAccessAction: @escaping () -> Void,
        writeOnlyAction: @escaping () -> Void,
        cancelAction: @escaping () -> Void
    )
    func deselectRow(animated: Bool)
    func presentAlert(_ alertController: UIAlertController)
}

protocol BookingConfirmationPresenterInput {
    func viewIsReady()
    func reloadViewModel()
    func callHotelButtonDidTap()
    func redirectToWeb()
    func cancelNavigationButtonDidTap()
    func showDirections(withSender sender: UIView)
    func addToCalendar()
    func addToWallet()
    func hotelInfoDidTap()
    func priceBreakdownDidTap()
    func amendDidTap()
    func faqDidTap(url: URL?)
    func parkingInfoDidTap()
    func openExistingWalletDidTap()
    func passFetchSuccessful(with pass: PKPass?)
    func passFetchFailed(with error: Error?)
    func passExistsAlready()
    func hotelFetched()
    func hotelFetchError(error: Error?)
    func tapOnCheckIn()
    func addKeyToWalletTap()
    func viewKeyInWallet()
    func howYourKeyWorksDidTap()
    func tapOnCheckOut()
    func instructionsDidTap()
    func resendInvoiceDidTap()
}

protocol BookingConfirmationInteractorOutput: LabelsProvider {
    func cancelConnections()
    func loadHotel(with hotelCode: String, completion: @escaping (Hotel?, Error?) -> Void)
    func loadWalletPass(
        with reservationDetails: ReservationDetails,
        isQRCodeEnabled: Bool,
        completion: @escaping (Data?, Error?) -> Void
    )
    func findBookingSource(
        findBookingDetails: FindBookingDetails,
        completion: @escaping (FindBookingSource?, Error?) -> Void
    )
    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?,
        completion: @escaping (Reservation?, Error?) -> Void
    )
    func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool,
        completion: @escaping (_ response: ([UpsellItem], [UpsellItem], CityTaxResponse, GoshPackage?)?, _ error: Error?)
        -> Void
    )
    func resendInvoiceEmail(reservation: Reservation, completion: @escaping (Bool) -> Void)
    func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool,
        completion: @escaping (_ response: ConfirmPreCheckInOut?, _ error: Error?) -> Void
    )
    func getPromotionsInformation(
        criteria: PromotionsInformationCriteria,
        completion: @escaping (_ response: PromotionsInformation?, _ error: Error?) -> Void
    )
    func updateCiolStatus(
        payload: UpdateCiolStatusPayload,
        completion: @escaping (_ response: UpdateCiolStatusResponse?, _ error: Error?) -> Void
    )
    func ciolPaymentActions(
        basketReference: String,
        completion: @escaping (_ response: CiolPaymentActionsResponse?, _ error: Error?) -> Void
    )
}

protocol BookingConfirmationInteractorInput: Trackable {
    var bookingConfirmationViewModel: BookingConfirmationViewModel? { get }
    var isBookingFlowComplete: Bool { get }
    var isReservationCancelled: Bool { get }
    var summary: Stay { get }
    var reservation: Reservation? { get }
    var pass: PKPass? { get }
    var hotel: Hotel? { get }
    var preStayInputParams: PreStayInputParams? { get }
    var isOutOfDate: Bool { get set }
    var amendedStay: Bool { get set }
    var bookingDoesNotNeedToRefresh: Bool { get set }
    var roomKeyInstructionsModel: InstructionsViewModel? { get }
    var customAnalyticsParameters: PIDictionary? { get }

    func reloadStay()
    func fetchCalendarEvent(
        calendarPrompt: @escaping CalendarPrompt,
        completion: @escaping (EKEvent?, EKEventStore?, Error?) -> Void
    )

    func fetchHotel()
    func loadLatestDetails(completion: @escaping (_ success: Bool) -> Void)
    func fetchWalletPass(
        completion: @escaping (BookingConfirmationInteractor.WalletPassFetchResult) -> Void
    )
    func resendInvoice(completion: @escaping () -> Void)
    func performOnlineCheckout(completion: @escaping (_ success: Bool) -> Void)
    func updateCiolStatus(to ciolStatus: CiolStatus)
    func trackAction(action: String)
}

protocol BookingConfirmationRouterInput: AnyObject {
    func bookingFlowDidFinish()
    func selectedHotelInfo(hotel: Hotel?)
    func priceBreakdown(hotel: Hotel?, reservation: Reservation?)
    func amendAction(stay: Stay?, hotel: Hotel?)
    func selectedFaqs(url: URL?)
    func addToCalendar(event: EKEvent, store: EKEventStore)
    func addToWallet(pass: PKPass)
    func showDirections(hotel: Hotel?, withSender sender: UIView)
    func callHotel(number: String)
    func askForAppReview()
    func openWeb(url: URL)
    func startCheckInOnline(inputParams: PreStayInputParams)
    func startCheckOutOnline(checkOutDetails: CheckOutDetails)
    func showKeyPage(stay: Stay)
    func startOTP(stay: Stay, roomId: String)
    func openKeyInWalletDidTap(stay: Stay)
    func showRoomKeyInstructions(model: InstructionsViewModel)
}
