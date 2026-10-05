//
//  KeyModule.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 01/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import Foundation
import SimpleNetwork
import UIKit

enum KeyMessagingModule {
    static func createModule(
        allocationState: RoomAllocationState,
        stay: Stay
    ) -> UIViewController {
        let view = KeyMessagingView()
        let presenter = KeyMessagingPresenter()
        let interactor = KeyMessagingInteractor(messagingFlow: allocationState, stay: stay)
        let router = KeyMessagingRouter()

        router.viewController = view
        router.presenter = presenter
        view.presenter = presenter
        presenter.view = view
        presenter.interactor = interactor
        presenter.router = router
        interactor.presenter = presenter

        return view
    }
}

protocol KeyMessagingRouterProtocol: AnyObject {}

protocol KeyMessagingViewProtocol: AnyObject {
    var presenter: KeyMessagingPresenterProtocol? { get set }
    func updateView(with viewModel: KeyMessagingViewModel)
    func showMapDirections(directionsViewModel: DirectionsViewModel)
}

protocol KeyMessagingPresenterProtocol: AnyObject {
    var view: KeyMessagingViewProtocol? { get set }
    var interactor: KeyMessagingInteractorInputProtocol? { get set }
    var router: KeyMessagingRouterProtocol? { get set }
    func viewDidLoad()
    func mapButtonDidClick()
}

protocol KeyMessagingViewModel {
    var title: String { get }
    var messaging: String { get }
    var shouldShowDirectionsButton: Bool { get }
}

protocol KeyMessagingInteractorInputProtocol: AnyObject {
    var presenter: KeyMessagingInteractorOutputProtocol? { get set }
    var viewModel: KeyMessagingViewModel { get }
    var stay: Stay { get }
    func trackStateAnalytics()
}

protocol KeyMessagingInteractorOutputProtocol: AnyObject {
}
