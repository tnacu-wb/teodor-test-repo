//
//  PreStayModule.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation
import UIKit

enum PreStayModule {
    static func build(preStayInputParams: PreStayInputParams) -> UIViewController {
        let viewController = PreStayViewController()

        viewController.eventHandler = {
            let presenter = PreStayPresenter()
            let interactor = PreStayInteractor(preStayInputParams: preStayInputParams)
            let router = PreStayRouter()

            viewController.eventHandler = presenter

            presenter.view = viewController
            presenter.interactor = interactor
            presenter.router = router

            interactor.output = presenter

            router.viewController = viewController
            return presenter
        }()

        return viewController
    }
}

protocol PreStayViewModel {
    var email: String? { get }
    var contactNumber: String? { get }
    var formattedAddress: String { get }
    var bookingSummaryViewModel: BookingSummaryCIOLViewModelProtocol { get }
    var bookerViewModel: CIOLPersonViewModelProtocol { get }
    var roomsViewModel: [RoomGuestsViewModelProtocol] { get }
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol { get }
    var hotelBrand: HotelBrand? { get }
    var selectedPreference: HotelPreferenceViewModel? { get }
    var hotelPreferences: [HotelPreferenceViewModel]? { get }
    var isSpecialOccasionOn: Bool { get }
    var showOccasionError: Bool { get }
    var isDirect: Bool { get }
    var isThirdParty: Bool { get }

    var shouldSurfaceErrorMessages: Bool { get }
}

extension PreStayViewModel {
    var isThirdParty: Bool { !isDirect }

    var isMissingRequiredAddress: Bool {
        guard let address = bookerViewModel.address,
              let country = bookerViewModel.country else {
            return true
        }

        return country.title.isEmpty ||
               (address.postcode?.isEmpty ?? true) ||
               (address.line1?.isEmpty ?? true)
    }

    var isAddressPostcodeValid: Bool {
        guard isThirdParty else {
            return true
        }

        let postcode = bookerViewModel.address?.postcode
        let country = bookerViewModel.country?.country

        switch country {
        case .greatBritain:
            return postcode?.matches(Constants.Regex.ukPostcode) == true

        case .germany:
            return postcode?.matches(Constants.Regex.germanPostcode) == true

        default:
            return postcode != nil
        }
    }
}

protocol BookingSummaryCIOLViewModelProtocol {
    var image: URL? { get }
    var hotelName: String? { get }
    var duration: String? { get }
    var summary: String? { get }
}

enum GuestWarningMessageState {
    case nationalityRequiresConfirmation
    case nationalityAndPassportNumberRequired
    case none
}

protocol CIOLPersonViewModelProtocol {
    var title: String? { get }
    var firstName: String? { get }
    var lastName: String? { get }
    var country: CountryItem? { get }
    var address: Address? { get }
    var passportNumber: String? { get }
    var warningMessageState: GuestWarningMessageState { get }
    var formattedBookerInfo: String? { get }
}

protocol CIOLPriceBreakdownViewModelProtocol {
    var ctaTitle: String { get }
    var totalValue: String { get set }
    var displayTotalValue: Bool { get }
    var items: [CIOLPriceBreakdownItemViewModelProtocol] { get set }
}

protocol CIOLPriceBreakdownItemViewModelProtocol {
    var name: String { get }
    var value: Cost { get }
    var quantity: Int { get }
    var formattedName: String { get }
}

protocol RoomGuestsViewModelProtocol {
    var leadGuest: CIOLPersonViewModelProtocol { get }
    var accompanyingGuest: CIOLPersonViewModelProtocol? { get }
    var adultsCount: Int { get }
    var childrenCount: Int { get }
    var childrenCountDescription: String? { get }
    var roomID: String { get }
    var shouldShowSecondGuestView: Bool { get }

    var shouldShowChildrenCount: Bool { get }
    var leadGuestFullName: String { get }
    var secondGuestFullName: String? { get }
    var formattedChildrenCount: String? { get }
}

extension RoomGuestsViewModelProtocol {
    var shouldShowSecondGuestView: Bool {
        adultsCount > 1
    }

    var shouldShowChildrenCount: Bool {
        childrenCount > 0
    }

    var leadGuestFullName: String {
        leadGuest.formattedBookerInfo ?? ""
    }

    var secondGuestFullName: String? {
        accompanyingGuest?.formattedBookerInfo
    }

    var formattedChildrenCount: String? {
        String.localizedStringWithFormat(PILocalizedString("%d child(children)"), childrenCount)
     }
}

protocol PreStayInteractorProtocol: Trackable, PrestayDelegate {
    var viewModel: PreStayViewModel { get }
    var customAnalyticsParameters: PIDictionary? { get }
    var thirdPartyCityTax: Cost? { get }

    func goToEditDetails(flow: EditDetailsFlow)
    func performCheckIn(completion: @escaping (_ success: Bool, _ error: Error?) -> Void)
    func performPreStayChecks(completion: @escaping (Bool) -> Void)
    func getCiolUpsellInputParams(
        hotelPackages: [UpsellItem]?,
        bookedPackages: [UpsellItem]?,
        prestayInputParams: PreStayInputParams
    ) -> CiolUpsellInputParams?
    func updateViewModel(with editDetailsModel: EditDetailsModel)
    func isAllGuestDataComplete() -> Bool
    func getSpecialOccasions(completion: @escaping (_ success: Bool, _ error: Error?) -> Void)
    func viewOccasions()
    func updateSelectedPreference(with occasion: String)
    func updateSpecialOccasion(_ isOn: Bool)
    func trackSubmissionIfThirdPartyBooking()
    func confirmPreCheckIn(basketReference: String, completion: @escaping (Bool) -> Void)
    func resolvePreCheckinFlow(completion: @escaping (Bool) -> Void)
}

protocol PreStayRouterProtocol {
    func viewOccasions(hotelPreferences: [String], completion: ((String) -> Void)?)
    func goToUpsell(ciolUpsellInputParams: CiolUpsellInputParams, prestayDelegate: PrestayDelegate)
    func goToEditDetails(inputParams: EditDetailsInputParams)
    func goToPayment(inputParams: CiolReviewAndPayInputParams)
    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails)
    func goToRegCard(regCardInput: GuestDetailsInputBlueprint, prestayDelegate: PrestayDelegate)
}

protocol PreStayViewEventHandler {
    func handleContinueButtonTap()
    func showEditDetails(flow: EditDetailsFlow)
    func updateViewModel(with: EditDetailsModel)
    func handleSelectOccasion()
    func viewIsReady()
    func isSpecialOccasionOn(_ isOn: Bool)
}

protocol PreStayViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }

    func reloadData(with preStayViewModel: PreStayViewModel)
    func showLoadingIndicator()
    func hideLoadingIndicator()
    func showError(title: String, message: String?, shouldDie: Bool)
    func reloadPriceBreakdown(priceModel: CIOLPriceBreakdownViewModelProtocol)
}

protocol PreStayInteractorOutputProtocol: AnyObject {
    func preStayChecksCompleted(ciolUpsellInputParams: CiolUpsellInputParams)
    func editBookingDetails(inputParams: EditDetailsInputParams)
    func reloadData(with viewModel: PreStayViewModel)
    func goToPayment(with inputParams: CiolReviewAndPayInputParams)
    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails)
    func goDirectlyToCompletion(ciolConfirmationDetails: CiolConfirmationDetails)
    func goToGuestDetailsDERegCard(input: GuestDetailsInputBlueprint)
    func viewOccasions()
    func reloadPriceBreakdown(priceModel: CIOLPriceBreakdownViewModelProtocol)
    func showCityTaxDisclaimer()
}

protocol HotelPreferenceProtocol {
    var name: String? { get }
    var code: String { get }
}

enum CIOLStartFlow {
    case bookingConfirmation
    case myBookings
}

struct PreStayInputParams {
    var hotel: Hotel?
    var flow: CIOLStartFlow
    var stay: Stay
    var hotelAddress: String?
    var hotelImage: URL?
    var hotelName: String?
    var hotelCode: String
    var hotelBrand: HotelBrand?
    var reservationId: String?
    var bookingFlowId: String?
    var bookingReference: String?
    var arrivalDate: Date?
    var checkOutDate: Date?
    var adultsCountDescription: String
    var adultsCount: Int
    var childrenCountDescription: String
    var childrenCount: Int
    var nightsCountDescription: String
    var nightsCount: Int?
    var roomsCountDescription: String
    var rooms: [Room]
    var leadBookerTitle: String?
    var leadBookerFirstName: String?
    var leadBookerLastName: String?
    var email: String?
    var contactNumber: String?
    var address: Address?
    var outstandingBalance: Cost?
    var ciolPaymentActions: CiolPaymentActionsResponse?
    var selectedPreference: HotelPreferenceViewModel?
    var hotelPreferences: [HotelPreferenceViewModel]?
    var bookingPreferences: [HotelPreferenceViewModel]?
    var isSpecialOccasionOn = false
    var showSpecialOccasionError = false
    var isBusinessTrip: Bool
    var hasDERegCard: Bool
    var rateCode: String?
    var rateDescription: String?
    var isDirect: Bool

    var shouldSurfaceErrorMessages: Bool = false

    // Using these to store unique references to each guest based on their rows for validation
    var confirmedLeadGuestRows: Set<Int> = []
    var confirmedSecondGuestRows: Set<Int> = []

    mutating func updateOutstandingBalance(with newBalance: Cost) {
        self.outstandingBalance = newBalance
    }
}

struct HotelPreferenceViewModel: HotelPreferenceProtocol {
    var name: String?
    var code: String
    var type: String?
}
