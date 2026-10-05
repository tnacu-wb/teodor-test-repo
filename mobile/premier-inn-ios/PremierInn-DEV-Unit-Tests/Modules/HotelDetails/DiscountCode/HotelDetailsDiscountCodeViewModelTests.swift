//
//  HotelDetailsDiscountCodeViewModelTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 22/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import Testing
import SimpleNetwork
@testable import PremierInn

// MARK: - HotelDetailsDiscountCodeViewModelTests

@MainActor
final class HotelDetailsDiscountCodeViewModelTests {

    private var sut: HotelDetailsDiscountCodeViewModel!

    // MARK: - ctaButtonTapped tests

    @Test("cta button when discount code is valid")
    func testCtaButtonTappedWhenDiscountCodeValid() async {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .success)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {})
        sut.discountCodeText = textFieldValue

        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` initially")

        // WHEN `ctaButtonTapped` is called
        await sut.ctaButtonTapped()

        // THEN
        #expect(sut.textFieldViewState == .success("someStringHere"), "Should be equal to .success")
        #expect(sut.validDiscountCode == textFieldValue, "Should be equal to \(textFieldValue)")
        #expect(sut.previouslyValidatedDiscountCode == textFieldValue, "Should be equal to \(textFieldValue)")
        #expect(sut.validDiscountCodeType == "UNIQUE", "Should be equal to `UNIQUE`")
        #expect(sut.discountCodeText.isEmpty, "The TextField should be cleared")
        #expect(sut.isDiscountCodeValid == true, "Should be true")
        #expect(sut.ctaText == "Continue", "Should be equal to `Continue` when discount code is valid")
        #expect(sut.isCtaButtonDisabled == false, "CTA button should be enabled")
        #expect(sut.isLoading == false, "Should be false")
    }

    @Test("cta button when discount code is invalid")
    func testCtaButtonTappedWhenDiscountCodeInvalid() async {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .error)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        let mockedAnalyticsManager = MockAnalyticsManager()

        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )

        sut.discountCodeText = textFieldValue

        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` initially")

        // WHEN `ctaButtonTapped` is called
        await sut.ctaButtonTapped()

        // THEN
        #expect(sut.textFieldViewState == .error("someStringHere"), "Should be equal to .error")
        #expect(sut.validDiscountCode == nil, "Should be nil")
        #expect(sut.previouslyValidatedDiscountCode == nil, "Should be nil")
        #expect(sut.discountCodeText == textFieldValue, "The TextField should not be cleared")
        #expect(sut.isDiscountCodeValid == false, "Should be false")
        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` when invalid code")
        #expect(sut.isCtaButtonDisabled == false, "CTA button should be enabled")
        #expect(sut.isLoading == false, "Should be false")

        // THEN (analytics)
        #expect(mockedAnalyticsManager.actions.contains(PIAnalytics.Action.hotelDetailsDiscountCodeBox),
                "Should contain analytics action")
    }

    @Test("cta button when network error")
    func testCtaButtonTappedWhenNetworkError() async {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .failure(error: NSError())
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        let mockedAnalyticsManager = MockAnalyticsManager()
        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )
        sut.discountCodeText = textFieldValue

        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` initially")

        // WHEN `ctaButtonTapped` is called
        await sut.ctaButtonTapped()

        // THEN
        #expect(sut.textFieldViewState == .error(PILocalizedString("hotelDetailsDiscountCodeGenericError")),
                "Should be equal to \(PILocalizedString("hotelDetailsDiscountCodeGenericError"))")
        #expect(sut.validDiscountCode == nil, "Should be nil")
        #expect(sut.previouslyValidatedDiscountCode == nil, "Should be nil")
        #expect(sut.discountCodeText == textFieldValue, "The TextField should not be cleared")
        #expect(sut.isDiscountCodeValid == false, "Should be false")
        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` when invalid code")
        #expect(sut.isCtaButtonDisabled == false, "CTA button should be enabled")
        #expect(sut.isLoading == false, "Should be false")

        // THEN (analytics)
        #expect(mockedAnalyticsManager.actions.contains(PIAnalytics.Action.hotelDetailsDiscountCodeBox),
                "Should contain analytics action")
    }

    @Test("cta button response received but the promotionInformation object is nil")
    func testCtaButtonTappedWhenNilResponse() async {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: nil)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        let mockedAnalyticsManager = MockAnalyticsManager()
        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )
        sut.discountCodeText = textFieldValue

        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` initially")

        // WHEN `ctaButtonTapped` is called
        await sut.ctaButtonTapped()

        // THEN
        #expect(sut.textFieldViewState == .error(PILocalizedString("hotelDetailsDiscountCodeGenericError")),
                "Should be equal to \(PILocalizedString("hotelDetailsDiscountCodeGenericError"))")
        #expect(sut.validDiscountCode == nil, "Should be nil")
        #expect(sut.previouslyValidatedDiscountCode == nil, "Should be nil")
        #expect(sut.discountCodeText == textFieldValue, "The TextField should not be cleared")
        #expect(sut.isDiscountCodeValid == false, "Should be false")
        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` when invalid code")
        #expect(sut.isCtaButtonDisabled == false, "CTA button should be enabled")
        #expect(sut.isLoading == false, "Should be false")

        // THEN (analytics)
        #expect(mockedAnalyticsManager.actions.contains(PIAnalytics.Action.hotelDetailsDiscountCodeBox),
                "Should contain analytics action")
    }

    @Test("cta button when user tries to apply another discount code when one already validated")
    func testCtaButtonTappedWhenOneDiscountCodeAlreadyApplied() async {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .success)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        let mockedAnalyticsManager = MockAnalyticsManager()
        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )
        sut.discountCodeText = textFieldValue

        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` initially")

        // WHEN `ctaButtonTapped` is called
        await sut.ctaButtonTapped()

        // THEN
        #expect(sut.textFieldViewState == .success("someStringHere"), "Should be equal to .success")
        #expect(sut.validDiscountCode == textFieldValue, "Should be equal to \(textFieldValue)")
        #expect(sut.discountCodeText.isEmpty, "The TextField should be cleared")
        #expect(sut.isDiscountCodeValid == true, "Should be true")
        #expect(sut.ctaText == "Continue", "Should be equal to `Continue` code is valid")
        #expect(sut.isCtaButtonDisabled == false, "CTA button should be enabled")
        #expect(sut.isLoading == false, "Should be false")

        // AND WHEN user tries to enter another discount code
        sut.discountCodeText = "SomeOtherCode"
    
        // AND WHEN taps the cta button
        await sut.ctaButtonTapped()

        // THEN
        #expect(sut.textFieldViewState == .error(PILocalizedString("hotelDetailsDiscountCodeMoreThanOneCodeError")),
                "Should be equal to .error(\(PILocalizedString("hotelDetailsDiscountCodeMoreThanOneCodeError"))")
        #expect(sut.discountCodeText == "SomeOtherCode", "The TextField should not be cleared")
        #expect(sut.isCtaButtonDisabled == false, "CTA button should be enabled")
        #expect(sut.isLoading == false, "Should be false")

        // THEN (analytics)
        #expect(mockedAnalyticsManager.actions.contains(PIAnalytics.Action.hotelDetailsDiscountCodeBox),
                "Should contain analytics action")
    }

    @Test("When user clicks on cta button once they validate the code, the modal should be dismissed")
    func testCtaButtonTappedWhenCodeIsValid() async {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .success)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {})
        sut.discountCodeText = textFieldValue

        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` initially")

        // WHEN `ctaButtonTapped` is called
        await sut.ctaButtonTapped()

        // THEN
        #expect(sut.textFieldViewState == .success("someStringHere"), "Should be equal to .success")
        #expect(sut.validDiscountCode == textFieldValue, "Should be equal to \(textFieldValue)")
        #expect(sut.discountCodeText.isEmpty, "The TextField should be cleared")
        #expect(sut.isDiscountCodeValid == true, "Should be true")
        #expect(sut.ctaText == "Continue", "Should be equal to `Continue` code is valid")
        #expect(sut.isCtaButtonDisabled == false, "CTA button should be enabled")
        #expect(sut.isLoading == false, "Should be false")
    
        // AND WHEN taps the cta button
        await sut.ctaButtonTapped()

        // THEN
        #expect(sut.ctaText == "Continue", "Should be equal to `Continue`")
        #expect(sut.isCtaButtonDisabled == false, "CTA button should be enabled")
        #expect(sut.isLoading == false, "Should be false")
        #expect(sut.dismissModal == true, "Should be true")
    }

    // MARK: - validateDiscountCode tests

    @Test("validateDiscountCode when success")
    func testValidateDiscountCodeWhenSuccess() async {
        // GIVEN sut with mocked result and use case
        // User first validates the discount code in the HotelDetailsDiscountCodeView
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .success)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {})
        sut.discountCodeText = textFieldValue

        #expect(sut.ctaText == "Apply", "Should be equal to `Apply` initially")

        // WHEN `ctaButtonTapped` is called
        await sut.ctaButtonTapped()

        // THEN
        #expect(sut.textFieldViewState == .success("someStringHere"), "Should be equal to .success")
        #expect(sut.validDiscountCode == textFieldValue, "Should be equal to \(textFieldValue)")
        #expect(sut.previouslyValidatedDiscountCode == textFieldValue, "Should be equal to \(textFieldValue)")
        #expect(sut.validDiscountCodeType == "UNIQUE", "Should be equal to `UNIQUE`")
        #expect(sut.discountCodeText.isEmpty, "The TextField should be cleared")
        #expect(sut.isDiscountCodeValid == true, "Should be true")
        #expect(sut.ctaText == "Continue", "Should be equal to `Continue` when discount code is valid")
        #expect(sut.isCtaButtonDisabled == false, "CTA button should be enabled")
        #expect(sut.isLoading == false, "Should be false")

        // AND WHEN `validateDiscountCode` is called when user changes dates in HDP
        // WHEN `validateDiscountCode` is called
        sut.validateDiscountCode { code, codeType in
            // THEN
            #expect(self.sut.textFieldViewState == .success("someStringHere"), "Should be equal to .success")
            #expect(self.sut.validDiscountCode == textFieldValue, "Should be equal to \(textFieldValue)")
            #expect(code == textFieldValue, "Should be equal to textFieldValue")
            #expect(codeType == "UNIQUE", "Should be equal to `UNIQUE`")
        }
    }

    @Test("validateDiscountCode when discount code is invalid")
    func testValidateDiscountCodeWhenDiscountCodeInvalid() {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .error)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        let mockedAnalyticsManager = MockAnalyticsManager()

        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )
        sut.discountCodeText = textFieldValue

        // WHEN `validateDiscountCode` is called
        sut.validateDiscountCode { code, codeType in
            // THEN
            #expect(code == nil , "Should be nil")
            #expect(codeType == nil , "Should be nil")
            #expect(self.sut.textFieldViewState == .error("someStringHere"), "Should be equal to .error")
            #expect(self.sut.validDiscountCode == nil, "Should be nil")
            #expect(self.sut.previouslyValidatedDiscountCode == nil, "Should be nil")
            #expect(self.sut.isDiscountCodeValid == false, "Should be false")
        }

        // THEN (analytics)
        #expect(mockedAnalyticsManager.actions.contains(PIAnalytics.Action.hotelDetailsDiscountCodeBox),
                "Should contain analytics action")
    }

    @Test("validateDiscountCode when network error")
    func testValidateDiscountCodeWhenNetworkError() async {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .failure(error: NSError())
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        let mockedAnalyticsManager = MockAnalyticsManager()
        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )
        sut.discountCodeText = textFieldValue

        // WHEN `validateDiscountCode` is called
        sut.validateDiscountCode { code, codeType in
            // THEN
            #expect(code == nil, "Should be nil")
            #expect(codeType == nil, "Should be nil")
            #expect(self.sut.textFieldViewState == .error(PILocalizedString("hotelDetailsDiscountCodeGenericError")),
                    "Should be equal to \(PILocalizedString("hotelDetailsDiscountCodeGenericError"))")
            #expect(self.sut.validDiscountCode == nil, "Should be nil")
            #expect(self.sut.previouslyValidatedDiscountCode == nil, "Should be nil")
        }

        // THEN (analytics)
        #expect(mockedAnalyticsManager.actions.contains(PIAnalytics.Action.hotelDetailsDiscountCodeBox),
                "Should contain analytics action")
    }

    @Test("validateDiscountCode when no response received but the promotionInformation object is nil")
    func testValidateDiscountCodeWhenNilResponse() async {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: nil)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        let mockedAnalyticsManager = MockAnalyticsManager()
        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )
        sut.discountCodeText = textFieldValue

        // WHEN `validateDiscountCode` is called
        sut.validateDiscountCode { code, codeType in
            // THEN
            #expect(code == nil, "Should be nil")
            #expect(codeType == nil, "Should be nil")
            #expect(self.sut.textFieldViewState == .error(PILocalizedString("hotelDetailsDiscountCodeGenericError")),
                    "Should be equal to \(PILocalizedString("hotelDetailsDiscountCodeGenericError"))")
            #expect(self.sut.validDiscountCode == nil, "Should be nil")
            #expect(self.sut.previouslyValidatedDiscountCode == nil, "Should be nil")
        }

        // THEN (analytics)
        #expect(mockedAnalyticsManager.actions.contains(PIAnalytics.Action.hotelDetailsDiscountCodeBox),
                "Should contain analytics action")
    }

    // MARK: - isCtaButtonDisabled tests

    @Test("CTA button should be disabled when textfield is empty")
    func testIsCtaButtonDisabled() {
        // GIVEN sut with mocked result and use case
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .success)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {})

        // AND WHEN textfield is empty
        sut.discountCodeText = ""

        // WHEN `isCtaButtonDisabled` is called
        let result = sut.isCtaButtonDisabled

        // THEN
        #expect(result == true)
    }

    // MARK: - tapVoucherTagCloseButton tests

    @Test("test action for voucher tag close button")
    func testTapVoucherTagCloseButton() async {
        // GIVEN sut with mocked result and use case
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .success)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {})
        sut.discountCodeText = textFieldValue

        // WHEN `ctaButtonTapped` is called, voucher is valid
        await sut.ctaButtonTapped()

        // THEN voucher is valid
        #expect(sut.discountCodeText.isEmpty, "The TextField should be cleared")
        #expect(sut.isDiscountCodeValid == true, "Should be true")

        // AND WHEN user clicks on the `x` on voucher tag to remove the discount code
        sut.tapVoucherTagCloseButton()

        // THEN
        #expect(sut.textFieldViewState == nil, "Should be nil")
        #expect(sut.validDiscountCode == nil, "Should be nil")
        #expect(sut.previouslyValidatedDiscountCode == nil, "Should be nil")
        #expect(sut.validDiscountCodeType == nil, "Should be nil")
        #expect(sut.discountCodeText.isEmpty, "Should be empty")
    }

    // MARK: - handleModalClosed tests

    @Test("test when modal is dismissed that the closure is called")
    func testHandleModalClosed() {
        // GIVEN sut with mocked result and use case
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .success)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {
            // THEN
            #expect(true, "Closure should be called")
        })

        // WHEN handleModalClosed is called
        sut.handleModalClosed()
    }

    @Test("test when modal is dismissed and invalid code, state is reset")
    func testHandleModalClosedWithInvalidCode() {
        let mockedResult: Result<ValidateDiscountCodeResult?> = .failure(error: NSError())
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)

        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {
            #expect(true, "Closure should be called")
        })

        sut.handleModalClosed()

        #expect(sut.textFieldViewState == nil, "Should be nil")
        #expect(sut.discountCodeText == "", "Should be nil")
    }
    
    // MARK: - Analytics tests

    @Test("trackScreenLoad sends promo box expand analytics")
    func testTrackScreenLoad() {
        let mockedAnalyticsManager = MockAnalyticsManager()
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: .success(result: .success))
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)

        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )

        sut.trackScreenLoad()

        #expect(mockedAnalyticsManager.states.contains(PIAnalytics.Action.promoBoxExpand))
    }

    // MARK: - isPasteLikelyUsed tests

    @Test("isPasteLikelyUsed returns pasted when multiple chars are added from empty")
    func testIsPasteLikelyUsedWhenMultipleCharactersAreInserted() {
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: .success(result: .success))
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {})

        sut.handleTextInputNotification(newValue: "ABCD123", previousValue: "")

        #expect(sut.promoInputType == .pasted)
    }

    @Test("isPasteLikelyUsed returns typed when one character is appended")
    func testIsPasteLikelyUsedWhenSingleCharacterIsInserted() {
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: .success(result: .success))
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {})

        sut.handleTextInputNotification(newValue: "ABCD1234", previousValue: "ABCD123")

        #expect(sut.promoInputType == .typed)
    }

    @Test("isPasteLikelyUsed returns pasted when same-length text is replaced")
    func testIsPasteLikelyUsedWhenSameLengthTextIsReplaced() {
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: .success(result: .success))
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        sut = .init(validatePromoCodeUseCaseFactory: mockedUseCaseFactory, onDismiss: {})

        // Simulates: had "FX10R", selected all, pasted "FX20R"
        sut.handleTextInputNotification(newValue: "FX20R", previousValue: "FX10R")

        #expect(sut.promoInputType == .pasted)
    }

    @Test("cta button when invalid code includes typed promoInputType in analytics")
    func testCtaButtonTappedInvalidCodeIncludesTypedPromoInputType() async {
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .error)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        let mockedAnalyticsManager = MockAnalyticsManager()

        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )

        sut.handleTextInputNotification(newValue: "F", previousValue: "")
        sut.discountCodeText = textFieldValue

        await sut.ctaButtonTapped()

        let promoInputType = mockedAnalyticsManager.userInfos.last?[PIAnalytics.Keys.promoInputType]
        #expect(promoInputType == "typed")
    }

    @Test("cta button when invalid code includes pasted promoInputType in analytics")
    func testCtaButtonTappedInvalidCodeIncludesPastedPromoInputType() async {
        let textFieldValue = "FX20RU"
        let mockedResult: Result<ValidateDiscountCodeResult?> = .success(result: .error)
        let mockedUseCase = MockValidateDiscountCodeUseCase(mockResult: mockedResult)
        let mockedUseCaseFactory = MockHotelDetailsDiscountCodeUseCaseFactory(mockedUseCase: mockedUseCase)
        let mockedAnalyticsManager = MockAnalyticsManager()

        sut = .init(
            validatePromoCodeUseCaseFactory: mockedUseCaseFactory,
            analytics: mockedAnalyticsManager,
            onDismiss: {}
        )

        sut.discountCodeText = textFieldValue
        sut.handleTextInputNotification(newValue: textFieldValue, previousValue: "")

        await sut.ctaButtonTapped()

        let promoInputType = mockedAnalyticsManager.userInfos.last?[PIAnalytics.Keys.promoInputType]
        #expect(promoInputType == "pasted")
    }    
}
