//
//  MockPreStayPresenter.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 18/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import PremierInn

final class MockPreStayPresenter: PreStayInteractorOutputProtocol {

    private(set) var goToGuestDetailsDERegCardCalled = false
    private(set) var showCityTaxDisclaimerCalled = false

    func preStayChecksCompleted(ciolUpsellInputParams: CiolUpsellInputParams) { }
    
    func editBookingDetails(inputParams: EditDetailsInputParams) { }
    
    func reloadData(with viewModel: any PreStayViewModel) { }
    
    func goToPayment(with inputParams: CiolReviewAndPayInputParams) { }
    
    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails) { }
    
    func goDirectlyToCompletion(ciolConfirmationDetails: CiolConfirmationDetails) {}

    func goToGuestDetailsDERegCard(input: any GuestDetailsInputBlueprint) {
        goToGuestDetailsDERegCardCalled = true
    }

    func viewOccasions() { }

    func reloadPriceBreakdown(priceModel: any CIOLPriceBreakdownViewModelProtocol) { }

    func showCityTaxDisclaimer() {
        showCityTaxDisclaimerCalled = true
    }
}
