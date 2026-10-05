//
//  MockCiolReviewAndPayView.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 30/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import SimpleNetwork
@testable import PremierInn

final class MockCiolReviewAndPayView: CiolReviewAndPayViewProtocol {
    var customAnalyticsParameters: PIDictionary?

    var isLoadingIndicatorDisplayed = false
    var isStartDisplayingActionLoadingElementsCalled = false
    var isStopDisplayingActionLoadingElementsCalled = false
    var isPriceBreakdownLoaded = false
    var isErrorDisplayed = false
    var isDataReloaded = false
    
    func showLoadingIndicator() {
        isLoadingIndicatorDisplayed = true
    }
    
    func hideLoadingIndicator() {
        isLoadingIndicatorDisplayed = false
    }
    
    func loadPriceBreakdown(with priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol) {
        isPriceBreakdownLoaded = true
    }
    
    func showError(title: String, message: String?, shouldDie: Bool) {
        isErrorDisplayed = true
    }
    
    func reloadData(with viewModel: CiolReviewAndPayViewModel) {
        isDataReloaded = true
    }
    
    func startDisplayingActionLoadingElements() {
        isStartDisplayingActionLoadingElementsCalled = true
    }
    
    func stopDisplayingActionLoadingElements() {
        isStopDisplayingActionLoadingElementsCalled = true
    }

    func showError(title: String, message: String?, action: @escaping (() -> Void)) {}
}
