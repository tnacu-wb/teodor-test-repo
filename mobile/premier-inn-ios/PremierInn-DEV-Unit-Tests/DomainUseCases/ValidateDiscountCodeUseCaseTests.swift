//
//  ValidateDiscountCodeUseCaseTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 06/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import Testing
import SimpleNetwork
@testable import PremierInn

// MARK: - ValidateDiscountCodeUseCaseTests

struct ValidateDiscountCodeUseCaseTests {

    // MARK: - sut

    private var sut: ValidateDiscountCodeUseCase!

    // MARK: - execute() async tests

    @Test("test execute when success")
    mutating func testExecuteWhenSuccess() async {
        // GIVEN sut with mocked service
        let mockedService = MockRequestManagerValidateDiscountCodeService(result: .success, error: nil)
        sut = .init(
            criteria: mockedCriteria,
            service: mockedService
        )

        // WHEN `execute()` is called
        let result: Result<ValidateDiscountCodeResult?> = await sut.execute()

        // THEN
        switch result {
        case .success(result: let validateResult):
            #expect(validateResult != nil)
        case .failure:
            Issue.record("Expected success but got failure")
        }
    }

    @Test("test execute when error")
    mutating func testExecuteWhenError() async {
        // GIVEN sut with mocked service
        let mockedError = NSError(domain: #function, code: 400)
        let mockedService = MockRequestManagerValidateDiscountCodeService(result: nil, error: mockedError)
        sut = .init(
            criteria: mockedCriteria,
            service: mockedService
        )

        // WHEN `execute()` is called
        let result: Result<ValidateDiscountCodeResult?> = await sut.execute()

        // THEN
        switch result {
        case .success:
            Issue.record("Expected failure but got success")
        case .failure(error: let error):
            // Assert we received the same error we injected
            let nsError = error as NSError
            #expect(nsError.domain == #function)
            #expect(nsError.code == 400)
        }
    }

    // MARK: - execute(completion:) tests

    @Test("test execute(completion:) when success")
    mutating func testExecuteCompletionWhenSuccess() async {
        // GIVEN sut with mocked service
        let mockedService = MockRequestManagerValidateDiscountCodeService(result: .success, error: nil)
        sut = .init(
            criteria: mockedCriteria,
            service: mockedService
        )

        // WHEN `execute(completion:)` is called
        let result: Result<ValidateDiscountCodeResult?> = await withCheckedContinuation { continuation in
            sut.execute { completionResult in
                continuation.resume(returning: completionResult)
            }
        }

        // THEN
        switch result {
        case .success(result: let validateResult):
            #expect(validateResult != nil)
        case .failure:
            Issue.record("Expected success but got failure")
        }
    }

    @Test("test execute(completion:) when error")
    mutating func testExecuteCompletionWhenError() async {
        // GIVEN sut with mocked service
        let mockedError = NSError(domain: #function, code: 400)
        let mockedService = MockRequestManagerValidateDiscountCodeService(result: nil, error: mockedError)
        sut = .init(
            criteria: mockedCriteria,
            service: mockedService
        )

        // WHEN `execute(completion:)` is called
        let result: Result<ValidateDiscountCodeResult?> = await withCheckedContinuation { continuation in
            sut.execute { completionResult in
                continuation.resume(returning: completionResult)
            }
        }

        // THEN
        switch result {
        case .success:
            Issue.record("Expected failure but got success")
        case .failure(error: let error):
            // Assert we received the same error we injected
            let nsError = error as NSError
            #expect(nsError.domain == #function)
            #expect(nsError.code == 400)
        }
    }
}

private extension ValidateDiscountCodeUseCaseTests {
    var mockedCriteria: PromotionsInformationCriteria {
        .init(
            brand: .premierInn,
            promotionCode: "FX20RU",
            bookingDate: Date(),
            stayStartDate: Date(),
            stayEndDate: Date(),
            basketReference: nil
        )
    }
}
