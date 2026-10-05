//
//  HotelDetailsDiscountCodeViewModel.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 22/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

final class HotelDetailsDiscountCodeViewModel: ObservableObject {
    @Published var textFieldViewState: StandardTextFieldView.ViewState?
    @Published private(set) var isLoading = false
    @Published var discountCodeText = String()

    @Published var dismissModal = false

    /// The type of the validated discount code. Example: `GENERIC`, `UNIQUE`.
    private(set) var validDiscountCodeType: String?

    /// Stores the discount code after it has been verified by the backend.
    private(set) var validDiscountCode: String?

    /// Stores the discount code previously validated by the user.
    private(set) var previouslyValidatedDiscountCode: String?

    private(set) var promoInputType: PromoInputType?

    private let validatePromoCodeUseCaseFactory: HotelDetailsDiscountCodeUseCaseFactoryType
    private let analytics: AnalyticsType

    private let onDismiss: (() -> Void)?

    private let genericErrorMessage = PILocalizedString("hotelDetailsDiscountCodeGenericError")

    @MainActor
    var isCtaButtonDisabled: Bool {
        (isLoading || discountCodeText.isEmpty ) && !isDiscountCodeValid
    }

    @MainActor
    var isDiscountCodeValid: Bool {
        validDiscountCode?.isNotEmpty == true
    }

    @MainActor
    /// Show text as "Continue" if the discount code is successfully applied AND if the textfield is empty. Otherwise show text as "Apply".
    var ctaText: String {
        isDiscountCodeValid && discountCodeText.isEmpty
        ? PILocalizedString("bookingReviewContinueButtonTitle")
        : PILocalizedString("hotelDetailsDiscountCodeApplyText")
    }

    @MainActor
    private var shouldDismissModal: Bool {
        isDiscountCodeValid && discountCodeText.isEmpty
    }

    init(
        validatePromoCodeUseCaseFactory: HotelDetailsDiscountCodeUseCaseFactoryType =
            HotelDetailsDiscountCodeUseCaseFactory(),
        analytics: AnalyticsType = AnalyticsManager.shared,
        onDismiss: @escaping () -> Void
    ) {
        self.validatePromoCodeUseCaseFactory = validatePromoCodeUseCaseFactory
        self.analytics = analytics
        self.onDismiss = onDismiss
    }

    @MainActor
    func ctaButtonTapped() async {
        defer { isLoading = false }

        guard !shouldDismissModal else {
            dismissModal = true
            return
        }

        guard !isDiscountCodeValid else {
            let errorMessage = PILocalizedString("hotelDetailsDiscountCodeMoreThanOneCodeError")
            textFieldViewState = .error(errorMessage)
            trackError(errorString: errorMessage)
            return
        }

        guard !isLoading else { return }
        isLoading = true

        let validateDiscountCodeUseCase = validatePromoCodeUseCaseFactory
            .getValidateDiscountCodeUseCase(promotionCode: discountCodeText)

        let result = await validateDiscountCodeUseCase.execute()

        switch result {
        case .success(result: let validationResult):
            guard let validationResult, let status = validationResult.promoBoxStatus else {
                textFieldViewState = .error(genericErrorMessage)
                trackError(errorString: genericErrorMessage)
                return
            }
            if status == .success {
                validDiscountCodeType = validationResult.promoKind
                let message = validationResult.message ?? PILocalizedString("hotelDetailsDiscountCodeValidCode")
                textFieldViewState = .success(message)
                validDiscountCode = discountCodeText
                previouslyValidatedDiscountCode = discountCodeText
                discountCodeText = String()
                trackSuccess()
            } else {
                let errorMessage = validationResult.message ?? genericErrorMessage
                textFieldViewState = .error(errorMessage)
                trackError(errorString: errorMessage)
            }
        case .failure:
            textFieldViewState = .error(genericErrorMessage)
            trackError(errorString: genericErrorMessage)
        }
    }

    /// Called from non-async context, hence the use of completion handlers.
    func validateDiscountCode(completion: @escaping (String?, String?) -> Void) {
        let validateDiscountCodeUseCase = validatePromoCodeUseCaseFactory
            .getValidateDiscountCodeUseCase(promotionCode: previouslyValidatedDiscountCode ?? discountCodeText)

        validateDiscountCodeUseCase.execute { [weak self] result in
            guard let self else {
                self?
                    .textFieldViewState = .error(self?
                    .genericErrorMessage ?? PILocalizedString("hotelDetailsDiscountCodeGenericError"))
                completion(nil, nil)
                return
            }

            switch result {
            case .success(result: let validationResult):
                guard let validationResult, let status = validationResult.promoBoxStatus else {
                    textFieldViewState = .error(genericErrorMessage)
                    discountCodeText = previouslyValidatedDiscountCode ?? String()
                    validDiscountCode = nil
                    completion(nil, nil)
                    trackError(errorString: genericErrorMessage)
                    return
                }
                if status == .success {
                    validDiscountCodeType = validationResult.promoKind
                    let message = validationResult.message ?? PILocalizedString("hotelDetailsDiscountCodeValidCode")
                    textFieldViewState = .success(message)
                    validDiscountCode = previouslyValidatedDiscountCode
                    discountCodeText = String()
                    trackSuccess()
                    completion(validDiscountCode, validDiscountCodeType)
                } else {
                    let errorMessage = validationResult.message ?? genericErrorMessage
                    textFieldViewState = .error(errorMessage)
                    discountCodeText = previouslyValidatedDiscountCode ?? String()
                    validDiscountCode = nil
                    completion(nil, nil)
                    trackError(errorString: errorMessage)
                }
            case .failure:
                textFieldViewState = .error(genericErrorMessage)
                discountCodeText = previouslyValidatedDiscountCode ?? String()
                validDiscountCode = nil
                completion(nil, nil)
                trackError(errorString: genericErrorMessage)
            }
        }
    }

    func tapVoucherTagCloseButton() {
        textFieldViewState = nil
        validDiscountCode = nil
        previouslyValidatedDiscountCode = nil
        validDiscountCodeType = nil
        promoInputType = nil
        discountCodeText = String()
    }

    func handleTextInputNotification(newValue: String, previousValue: String) {
        guard newValue != previousValue, !newValue.isEmpty else { return }

        if isPasteLikelyUsed(in: newValue, previousValue: previousValue) {
            updatePromoInputType(.pasted)
        } else if promoInputType == nil {
            updatePromoInputType(.typed)
        }
    }

    private func updatePromoInputType(_ inputType: HotelDetailsDiscountCodeViewModel.PromoInputType) {
        promoInputType = inputType
    }

    private func resetErrorStateIfNeeded() {
        if validDiscountCode == nil {
            textFieldViewState = nil
            discountCodeText = String()
        }
    }

    func handleModalClosed() {
        resetErrorStateIfNeeded()
        onDismiss?()
    }

    /// This tries to detect a paste action by comparing the previous and new input values:
    /// Type: Users usually add one character at a time, and the newValue extends the previousValue (i.e., the old text is still a prefix of the new text). → return false.
    /// Paste or bulk change pattern: If multiple characters appear at once (i.e., full or partial replacement happened), it assumes a paste. → return true.
    private func isPasteLikelyUsed(in newValue: String, previousValue: String) -> Bool {
        let delta = newValue.count - previousValue.count
        return delta > 1 || (newValue != previousValue && !newValue.hasPrefix(previousValue))
    }
}

// MARK: - Analytics

extension HotelDetailsDiscountCodeViewModel: DefaultAnalyticsData {
    enum PromoInputType: String {
        case typed
        case pasted
    }

    func trackScreenLoad() {
        let dataDict = mergingDefaultValues(
            with: [PIAnalytics.Keys.promoBoxExpand: true],
            analytics: analytics
        )

        analytics.trackState(
            PIAnalytics.Action.promoBoxExpand,
            data: dataDict
        )
    }

    private func trackError(errorString: String) {
        var parameters: PIDictionary = [PIAnalytics.Keys.errorMessage: errorString]

        if let promoInputType {
            parameters[PIAnalytics.Keys.promoInputType] = promoInputType.rawValue
        }

        let dataDict = mergingDefaultValues(
            with: parameters,
            analytics: analytics
        )

        analytics.trackAction(
            PIAnalytics.Action.hotelDetailsDiscountCodeBox,
            userInfo: dataDict
        )
    }

    private func trackSuccess() {
        var parameters: PIDictionary = [:]

        if let promoInputType {
            parameters[PIAnalytics.Keys.promoInputType] = promoInputType.rawValue
        }

        let dataDict = mergingDefaultValues(
            with: parameters,
            analytics: analytics
        )

        analytics.trackAction(
            PIAnalytics.Action.hotelDetailsDiscountCodeBox,
            userInfo: dataDict
        )
    }
}
