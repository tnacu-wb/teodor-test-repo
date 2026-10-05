//
//  RoomKeyInstructionsInteractor.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 25.06.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import PassKit

final class RoomKeyInstructionsInteractor: InstructionsInteractorProtocol {
    // MARK: - Properties

    var viewModel: InstructionsViewModel
    var pass: PKPass?

    var isPassAddedToWallet: Bool {
        guard let pass else { return false }

        return passManager.contains(pass)
    }

    private let passManager: PassManagerProtocol
    private let dataProvider: InstructionsDataProvider
    private weak var presenter: InstructionsViewEventHandler?

    // MARK: - Init

    init(viewModel: InstructionsViewModel,
         dataProvider: InstructionsDataProvider = RequestsManager(),
         presenter: InstructionsViewEventHandler,
         passManager: PassManagerProtocol = PassManager()) {
        self.viewModel = viewModel
        self.dataProvider = dataProvider
        self.presenter = presenter
        self.passManager = passManager
        self.pass = viewModel.savedPass
    }

    // MARK: - Methods

    func fetchWalletPass() {
        guard let reservationDetails = viewModel.reservationDetails,
              let isQRCodeEnabled = viewModel.isQRCodeEnabled,
              viewModel.savedPass == nil else {
            presenter?.walletPassFetchCompleted(with: .failure(InstructionsWalletPassError.invalidData))
            return
        }

        presenter?.toggleLoadingIndicator(isLoading: true)

        dataProvider.loadWalletPass(with: reservationDetails,
                                    isQRCodeEnabled: isQRCodeEnabled) { [weak self] data, error in
            guard let self else { return }
            presenter?.toggleLoadingIndicator(isLoading: false)

            guard error == nil,
                  let data else {
                presenter?.walletPassFetchCompleted(with: .failure(InstructionsWalletPassError.fetchFailed))
                return
            }

            do {
                let pass = try passManager.makePass(from: data)
                self.pass = pass
                presenter?.walletPassFetchCompleted(with: .success(pass))
            } catch {
                presenter?.walletPassFetchCompleted(with: .failure(InstructionsWalletPassError.invalidData))
            }
        }
    }
}

// MARK: - InstructionsDataProvider

extension RequestsManager: InstructionsDataProvider { }
