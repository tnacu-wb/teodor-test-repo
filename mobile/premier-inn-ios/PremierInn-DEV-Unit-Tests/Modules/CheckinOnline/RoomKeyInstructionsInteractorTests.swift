//
//  RoomKeyInstructionsInteractorTests.swift
//  PremierInn
//
//  Created by Badea, Bogdan (Cognizant) on 03.06.2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
import PassKit
import SimpleNetwork

@testable import PremierInn

final class RoomKeyInstructionsInteractorTests: XCTestCase {
    private var sut: RoomKeyInstructionsInteractor!
    private var presenter: MockInstructionsViewEventHandler!
    private var dataProvider: MockInstructionsDataProvider!
    private var passManager: MockPassManager!

    // MARK: - Lifecycle

    override func tearDown() {
        sut = nil
        presenter = nil
        dataProvider = nil
        passManager = nil
        
        super.tearDown()
    }

    // MARK: - Tests

    func testFetchWalletPass_whenDataIsMissing_doesNotCallDataProvider() {
        // Arrange
        makeSUT()
        
        // Act
        sut.fetchWalletPass()
        
        // Assert
        XCTAssertFalse(dataProvider.loadWalletPassCalled)
        XCTAssertFalse(passManager.makePassCalled)
        XCTAssertTrue(presenter.toggleLoadingIndicatorValues.isEmpty)
        XCTAssertTrue(presenter.walletPassFetchCompletedResults.isNotEmpty)

        if let result = presenter.walletPassFetchCompletedResults.last {
            switch result {
            case .success(let pass):
                XCTFail("Expected to fail, got: \(pass)")
            case .failure(let error):
                XCTAssertEqual(error as? InstructionsWalletPassError, .invalidData)
            }
        }
    }
    
    func testFetchWalletPass_whenDataIsNil_callsDataProviderAndReturnsError() {
        // Arrange
        makeSUT(reservationDetails: makeReservationDetails(), isQRCodeEnabled: true)

        // Act
        dataProvider.response = (data: nil, error: InstructionsWalletPassError.fetchFailed)
        sut.fetchWalletPass()

        // Assert
        XCTAssertTrue(dataProvider.loadWalletPassCalled)
        XCTAssertFalse(passManager.makePassCalled)
        XCTAssertEqual(dataProvider.isQRCodeEnabled, true)
        XCTAssertTrue(presenter.toggleLoadingIndicatorValues.isNotEmpty)
        XCTAssertEqual(presenter.toggleLoadingIndicatorValues, [true, false])
        XCTAssertEqual(presenter.toggleLoadingIndicatorValues.count, 2)
        
        if let result = presenter.walletPassFetchCompletedResults.last {
            switch result {
            case .success(let pass):
                XCTFail("Expected to fail, got: \(pass)")
            case .failure(let error):
                XCTAssertEqual(error as? InstructionsWalletPassError, .fetchFailed)
                
            }
        }
    }
    
    func testFetchWalletPass_whenDataIsValid_callsDataProviderSuccessful() {
        // Arrange
        makeSUT(reservationDetails: makeReservationDetails(), isQRCodeEnabled: true)

        // Act
        dataProvider.response = (data: Data("pass-data".utf8),
                                 error: nil)
        sut.fetchWalletPass()

        // Assert
        XCTAssertTrue(dataProvider.loadWalletPassCalled)
        XCTAssertTrue(passManager.makePassCalled)
        XCTAssertEqual(dataProvider.isQRCodeEnabled, true)
        XCTAssertTrue(presenter.toggleLoadingIndicatorValues.isNotEmpty)
        XCTAssertEqual(presenter.toggleLoadingIndicatorValues, [true, false])
        XCTAssertEqual(presenter.toggleLoadingIndicatorValues.count, 2)
        XCTAssertEqual(presenter.walletPassFetchCompletedResults.count, 1)

        if let result = presenter.walletPassFetchCompletedResults.last {
            switch result {
            case .success:
                break
            case .failure(let error):
                XCTFail("Expected to succeed, got error: \(error)")
                
            }
        }
    }
    
    func testIsPassAddedToWallet_whenPassIsNil_returnsFalse() {
        // Arrange
        makeSUT()
        
        // Act
        sut.fetchWalletPass()
        
        // Assert
        XCTAssertFalse(sut.isPassAddedToWallet)
    }
}

// MARK: - Helpers

private extension RoomKeyInstructionsInteractorTests {
    func makeSUT(reservationDetails: ReservationDetails? = nil,
                 isQRCodeEnabled: Bool? = nil,
                 savedPass: PKPass? = nil) {
        presenter = MockInstructionsViewEventHandler()
        dataProvider = MockInstructionsDataProvider()
        passManager = MockPassManager()

        let viewModel = InstructionsViewModel(type: .roomKeyWithQRCode,
                                              reservationDetails: reservationDetails,
                                              isQRCodeEnabled: isQRCodeEnabled,
                                              savedPass: savedPass)
        
        sut = RoomKeyInstructionsInteractor(viewModel: viewModel,
                                            dataProvider: dataProvider,
                                            presenter: presenter,
                                            passManager: passManager)
    }
    
    func makeReservationDetails() -> ReservationDetails {
        ReservationDetails(reservationId: "ID1",
                           surname: "Doe",
                           arrivalDate: .now,
                           business: false,
                           token: "token")
    }
}

// MARK: - Mocks

private final class MockInstructionsViewEventHandler: InstructionsViewEventHandler {
    private(set) var viewIsReadyCalled = false
    private(set) var closeCalled = false
    private(set) var addToWalletCalled = false
    private(set) var viewExistingWalletPassCalled = false
    private(set) var showUsingYourDIgitalKeyAnimationCalledCalled = false

    private(set) var toggleLoadingIndicatorValues: [Bool] = []
    private(set) var walletPassFetchCompletedResults: [Swift.Result<PKPass, Error>] = []

    func viewIsReady() {
        viewIsReadyCalled = true
    }

    func close() {
        closeCalled = true
    }

    func addToWallet() {
        addToWalletCalled = true
    }

    func viewExistingWalletPass() {
        viewExistingWalletPassCalled = true
    }

    func walletPassFetchCompleted(with result: Swift.Result<PKPass, Error>) {
        walletPassFetchCompletedResults.append(result)
    }

    func toggleLoadingIndicator(isLoading: Bool) {
        toggleLoadingIndicatorValues.append(isLoading)
    }

    func showUsingYourDigitalKeyAnimation() {
        showUsingYourDIgitalKeyAnimationCalledCalled = true
    }
}

private final class MockInstructionsDataProvider: InstructionsDataProvider {
    private(set) var loadWalletPassCalled = false
    private(set) var reservationDetails: ReservationDetails?
    private(set) var isQRCodeEnabled: Bool?
    
    var response: (data: Data?, error: Error?)?

    func loadWalletPass(with reservationDetails: ReservationDetails,
                        isQRCodeEnabled: Bool,
                        completion: @escaping (Data?, (any Error)?) -> Void) {
        loadWalletPassCalled = true
        self.reservationDetails = reservationDetails
        self.isQRCodeEnabled = isQRCodeEnabled
        
        if let response {
            completion(response.data, response.error)
        }
    }
}
