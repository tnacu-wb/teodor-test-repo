//
//  RoomKeyInstructionsModule.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 25.06.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import PassKit
import SimpleNetwork

enum InstructionsModule {
    static func build(model: InstructionsViewModel) -> UIViewController {
        let viewController = InstructionsViewController()

        viewController.eventHandler = {
            let presenter = InstructionsPresenter()
            presenter.view = viewController

            let interactor = RoomKeyInstructionsInteractor(viewModel: model, presenter: presenter)
            presenter.interactor = interactor

            let router = InstructionsRouter()
            router.viewController = viewController
            router.presenter = presenter

            presenter.router = router
            return presenter
        }()
        return viewController
    }
}

protocol InstructionsInteractorProtocol {
    var pass: PKPass? { get }
    var viewModel: InstructionsViewModel { get }
    var isPassAddedToWallet: Bool { get }
    func fetchWalletPass()
}

protocol InstructionsViewEventHandler: AnyObject {
    func viewIsReady()
    func close()
    func addToWallet()
    func viewExistingWalletPass()
    func walletPassFetchCompleted(with result: Swift.Result<PKPass, Error>)
    func toggleLoadingIndicator(isLoading: Bool)
    func showUsingYourDigitalKeyAnimation()
}

protocol InstructionsRouterProtocol {
    func close()
    func showAddPassViewController(pass: PKPass)
    func showExistingPass(url: URL)
    func showUsingYourDigitalKeyAnimation()
}

protocol InstructionsViewProtocol: AnyObject {
    func reloadData(with viewModel: InstructionsViewModel)
    func toggleLoadingIndicator(isLoading: Bool)
    func showFailedWalletFetch(with error: Error)
}

protocol InstructionsDataProvider {
    func loadWalletPass(
        with reservationDetails: ReservationDetails,
        isQRCodeEnabled: Bool,
        completion: @escaping (Data?, Error?) -> Void
    )
}
