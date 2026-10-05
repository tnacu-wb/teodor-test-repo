//
//  OneTimePasswordModule.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 29/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import PassKit

enum OneTimePasswordModule {
    static func buildController(
        stay: Stay,
        otpEmail: String,
        roomId: String,
        delegate: KeyAddedProtocol?
    ) -> UIViewController {
        let controller = OneTimePasswordView()
        controller.presenter = {
            let presenter = OneTimePasswordPresenter()
            presenter.view = controller
            presenter.keyAddedDelegate = delegate

            let interactor = OneTimePasswordInteractor(stay: stay, otpEmail: otpEmail, roomId: roomId)
            presenter.interactor = interactor

            let router = OneTimePasswordRouter()
            router.viewController = controller

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

enum PassProvisioningError: Error, LocalizedError {
    case roomNotAllocated
    case missingProvisioningData
    case invalidProvisioningData
    case passCantBeAdded
}

protocol OneTimePasswordInteractorInput {
	var provisioningCredentialIdentifier: String? { get }
    var stay: Stay { get set }
    var customAnalyticsParameters: PIDictionary? { get }
    var roomId: String { get }
    func validate(withCode code: String, completion: @escaping (Bool?, Error?) -> Void)
    func otpResend(completion: @escaping (Bool?, Error?) -> Void)
    func fetchKeyProvisioningData(otpCode: String) async -> DigitalKeyProvisionResponse?
    func updateStayWithPassId(with id: String)
    func provisionAppleWalletKey(otpCode: String) async throws -> PKAddShareablePassConfiguration
    func trackStateAnalyticsForPKAddSecurePass(pageName: String)
}

protocol OneTimePasswordPresenterInput {
    var screenName: String { get }
    var screenType: String { get }



    func viewIsReady()
    func submitButtonTapped()
    func userTappedEmailAddressRow(at indexPath: IndexPath)
    func formSubmissionErrorOccured(at rowIndex: IndexPath?)
    func genericFormSubmissionErrorOccured(with error: Error)
    func resendButtonTapped()
    func codeSubmitted(code: String)
    func updateStayWithPassId(with id: String)
}

protocol OneTimePasswordRouterInput {
    func goBackToKeyScreen()
    func presentGetKeyView(_ controller: UIViewController)
}

protocol OneTimePasswordViewInput: AnyObject {
    var tabBarIsHidden: Bool { get }
    var parentNavigationController: UINavigationController? { get }
    var customAnalyticsParameters: PIDictionary? { get set }
    func updateScreenTitle(with screenTitle: String)
    func registerRows()
    func loadModelForView()
    func validateForm()
    func scrollToAndFocusRow(at indexPath: IndexPath)
    func makeRowFirstResponder(at indexPath: IndexPath)
    func showAlert(with title: String, error: Error?)
    func showAlert(withTitle title: String, message: String)
    func resendButtonCountdown()
    func stopLoadingAnimation()
    func updateTableError(error: String?)
}
