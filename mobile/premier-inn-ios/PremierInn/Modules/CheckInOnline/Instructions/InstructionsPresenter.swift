//
//  RoomKeyInstructionsPresenter.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 25.06.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import PassKit

class InstructionsPresenter: NSObject {
    weak var view: InstructionsViewProtocol?
    var interactor: InstructionsInteractorProtocol?
    var router: InstructionsRouterProtocol?
}

extension InstructionsPresenter: InstructionsViewEventHandler {
    func addToWallet() {
        interactor?.fetchWalletPass()
    }

    func viewIsReady() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.reloadData(with: viewModel)
    }

    func close() {
        router?.close()
    }

    func viewExistingWalletPass() {
        guard let passUrl = interactor?.pass?.passURL else { return }
        router?.showExistingPass(url: passUrl)
    }

    func walletPassFetchCompleted(with result: Result<PKPass, Error>) {
        switch result {
        case .success(let pass):
            router?.showAddPassViewController(pass: pass)
        case .failure(let error):
            view?.showFailedWalletFetch(with: error)
        }
    }

    func toggleLoadingIndicator(isLoading: Bool) {
        view?.toggleLoadingIndicator(isLoading: isLoading)
    }

    func showUsingYourDigitalKeyAnimation() {
        guard interactor?.viewModel.type == .usingDigitalKey else { return }
        router?.showUsingYourDigitalKeyAnimation()
    }
}

extension InstructionsPresenter: PKAddPassesViewControllerDelegate {
    func addPassesViewControllerDidFinish(_ controller: PKAddPassesViewController) {
        controller.dismiss(animated: true) {
            if self.interactor?.isPassAddedToWallet == true {
                self.close()
            }
        }
    }
}
