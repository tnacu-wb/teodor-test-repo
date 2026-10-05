//
//  KeyModule.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 28/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

enum KeyModule {
    static func createModule(
        stay: Stay,
        disableBackButton: Bool = false,
        howYourKeyWorks: Bool = false
    ) -> UIViewController {
        let view = KeyView()
        let presenter = KeyPresenter()
        let interactor = KeyInteractor(
            stay: stay,
            disableBackButton: disableBackButton,
            howYourKeyWorks: howYourKeyWorks,
            notificationManager: NotificationManager.shared
        )
        let router = KeyRouter()
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

// TODO: Can remove this when removing the KeyMessaging Module
enum RoomAllocationState {
    case userIsNotInRange
    case userIsTooEarly
    case userIsValid
    case roomBeingPrepared

    var titleLabel: String? {
        switch self {
        case .userIsNotInRange:
            return PILocalizedString("allocationMessagingTooFarTitle")
        case .userIsTooEarly:
            return PILocalizedString("allocationMessagingTooEarlyTitle")
        case .userIsValid:
            return nil
        case .roomBeingPrepared:
            return PILocalizedString("roomNotReadyMessageTitle")
        }
    }

    var descriptionLabel: String? {
        switch self {
        case .userIsNotInRange:
            return PILocalizedString("allocationMessagingTooFarDescription")
        case .userIsTooEarly:
            return PILocalizedString("allocationMessagingTooEarlyDescription")
        case .userIsValid:
            return nil
        case .roomBeingPrepared:
            return PILocalizedString("roomNotReadyMessageDescription")
        }
    }
}

protocol KeyInteractorInputProtocol: AnyObject {
    var presenter: KeyInteractorOutputProtocol? { get set }
    var stay: Stay { get }
    var roomId: String? { get }
    var disableBackButton: Bool { get }
    var viewModel: KeyDetailsViewModel { get }
    func allocateAndCheckInBooking(completion: @escaping (Bool?) -> Void)
    func trackStateAnalytics()
    func trackActionAnalytics(action: String)
    func setup(completion: @escaping (Bool?) -> Void)
    func instructionsModel(for type: InstructionsType) -> InstructionsViewModel?
    func updateCiolStatus()
    func loadNotificationPermissionStatus()
}

protocol KeyInteractorOutputProtocol: AnyObject {
    func updateViewModel(with viewModel: KeyDetailsViewModel)
}

protocol KeyRouterProtocol: AnyObject {
    func openMFAScreen(stay: Stay, roomId: String, keyAddedDelegate: KeyAddedProtocol?)
    func openPassInWallet(id: String)
    func openMessagingScreen(type: RoomAllocationState, stay: Stay)
    func closeButtonClicked(digitalKeyIdentifier: String?)
    func instructionsDidTap(model: InstructionsViewModel)
}

protocol CheckedInViewModel {
    var roomNumber: String? { get }
}

protocol KeyDetailsInfoRowsDisplayable {
    var infoRows: [KeyInfoCellViewModel]? { get }
}

protocol KeyDetailsTextDisplayable {
    var title: String { get }
    var description: String { get }
}

protocol KeyDetailsViewModel: KeyDetailsInfoRowsDisplayable, KeyDetailsTextDisplayable {
    var keyState: KeyState { get }
    var checkedInModel: CheckedInViewModel? { get }
    var shouldGoToUsingYourKeyInstructions: Bool { get }
}

protocol KeyAddedProtocol {
    func didFinishAddingKey()
}

protocol KeyPresenterProtocol: AnyObject {
    var view: KeyViewProtocol? { get set }
    var interactor: KeyInteractorInputProtocol? { get set }
    var router: KeyRouterProtocol? { get set }

    func viewIsReady()
    func addToAppleWalletDidTap()
    func startRoomAllocation(completion: @escaping () -> Void)
    func viewInAppleWallet()
    func closeButtonClicked()
    func instructionsDidTap(model: InstructionsViewModel)
}

protocol KeyInfoCellViewModel {
    var title: String { get }
    var description: String { get }
    var content: CiolInformationModel? { get }
    var instructionsModel: InstructionsViewModel? { get }
    var tapAction: (() -> Void)? { get }
}

enum KeyState {
    case digitalKeyReady
    case beforeCheckInTime
    case waitingForAllocation
    case checkedIn

    var analyticsValue: String {
        switch self {
        case .digitalKeyReady:
            return PIAnalytics.StateNames.digitalKeyAtHotel
        case .waitingForAllocation:
            return PIAnalytics.StateNames.digitalKeyRoomBeingPrepared
        case .beforeCheckInTime, .checkedIn:
            return PIAnalytics.StateNames.digitalKeyCompletion
        }
    }
}

protocol KeyViewProtocol: AnyObject {
    var presenter: KeyPresenterProtocol? { get set }
    func showLoadingIndicator()
    func hideLoadingIndicator()
    func updateView(with viewModel: KeyDetailsViewModel)
    func disableBackNavigation()
    func showAlert(with title: String, and message: String, completion: @escaping () -> Void)
}
