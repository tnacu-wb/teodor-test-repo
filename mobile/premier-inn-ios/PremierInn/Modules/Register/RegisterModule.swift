//
//  RegisterModule.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 14/11/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import Foundation

protocol RegisterViewInput: AnyObject {
    func setup(withTitle: String)
    func reloadViewModel()
    func stopEditing()
    func focusFormRow(at: IndexPath)
    func presentAlertWith(title: String, error: Error?)
    func show(error: Error)
    func lock()
    func unlock()
    func askForBiometricActivation(completion: @escaping () -> Void)
    func updateSubmitButton(isLoading: Bool)
    func selected(salutation: String)
    func loadMarketingOptIn(with viewModel: MarketingPreferenceViewModelType?)
}

protocol RegisterPresenterInput {
    var registerTracking: RegisterTracking { get }

    func viewIsReady()
    func submitForm(viewModel: FormekaViewModel?)
    func dismissRegisterDidTap()
    func biometricActivationCompleted(activated: Bool)
    func salutationRowDidTap()
}

protocol RegisterInteractorInput {
    var marketingPreferenceViewModel: MarketingPreferenceViewModelType { get }
    var registerTracking: RegisterTracking { get }
    var shouldUpdateBiometricSettings: Bool { get }

    func performRegistration(with registerParameters: RegisterParameters, completion: @escaping (_ error: Error?) -> Void)
    func biometricStatusChanged(enabled: Bool)
}

protocol RegisterInteractorOutput {
	func register(
	    withRegisterParameters registerParameters: RegisterParameters,
	    sensorData: String,
	    completion: @escaping (_ success: Bool?, _ error: Error?) -> Void
	)
    func login(
        withUsername username: String,
        password: String,
        isBusiness: Bool,
        completion: @escaping (Result<Bool>) -> Void
    )
    func getUser(
        userId: String,
        isBusiness: Bool,
        completion: @escaping (Result<User>) -> Void
    )
    func refreshStays(
        for user: User?,
        shouldAttemptLogin: Bool,
        completion: @escaping (Bool?) -> Void
    )
    func logout()
}

extension RequestsManager: RegisterInteractorOutput {}

protocol RegisterRouterInput {
    func selectedSalutation(completion: @escaping (_ salutation: String?) -> Void)
    func dismissRegisterView()
    func completeRegister()
}

protocol RegisterCompletionInput {
    func userWasRegistered()
}

protocol MarketingPreferenceViewModelType {
    var isOptIn: Bool { get }
}
