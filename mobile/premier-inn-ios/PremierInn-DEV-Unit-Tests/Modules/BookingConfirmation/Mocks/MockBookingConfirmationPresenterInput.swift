//
//  MockBookingConfirmationPresenterInput.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit
import PassKit
@testable import PremierInn

final class MockBookingConfirmationPresenterInput: BookingConfirmationPresenterInput {
    private(set) var isHotelFetchCalled = false

    func viewIsReady() { }
    
    func reloadViewModel() { }
    
    func callHotelButtonDidTap() { }
    
    func redirectToWeb() { }
    
    func cancelNavigationButtonDidTap() { }
    
    func showDirections(withSender sender: UIView) { }
    
    func addToCalendar() { }
    
    func addToWallet() { }
    
    func hotelInfoDidTap() { }
    
    func priceBreakdownDidTap() { }
    
    func amendDidTap() { }
    
    func faqDidTap(url: URL?) { }
    
    func parkingInfoDidTap() { }
    
    func openExistingWalletDidTap() { }
    
    func passFetchSuccessful(with pass: PKPass?) { }
    
    func passFetchFailed(with error: (any Error)?) { }
    
    func passExistsAlready() { }
    
    func hotelFetched() {
        isHotelFetchCalled = true
    }
    
    func hotelFetchError(error: (any Error)?) { }
    
    func tapOnCheckIn() { }
    
    func addKeyToWalletTap() { }
    
    func viewKeyInWallet() { }
    
    func howYourKeyWorksDidTap() { }
    
    func tapOnCheckOut() { }
    
    func instructionsDidTap() { }
    
    func resendInvoiceDidTap() { }
}
