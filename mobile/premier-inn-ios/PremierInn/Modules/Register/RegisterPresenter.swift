//
//  RegisterPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 02/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka

class RegisterPresenter {
    weak var view: RegisterViewInput?
    var interactor: RegisterInteractorInput?
    var router: RegisterRouterInput?
}

extension RegisterPresenter: RegisterPresenterInput {
    var registerTracking: RegisterTracking {
        interactor?.registerTracking ?? ("", "")
    }

    func viewIsReady() {
        view?.setup(withTitle: PILocalizedString("registerTitle", comment: "Register screen: main title"))
        view?.loadMarketingOptIn(with: interactor?.marketingPreferenceViewModel)
        view?.reloadViewModel()
    }

    func submitForm(viewModel: FormekaViewModel?) {
        guard let interactor = interactor else { return }
        guard let viewModel = viewModel else { return }

        view?.stopEditing()

        do {
            try viewModel.validate()

            let registerParameters = try RegisterInteractor.registerParameters(values: viewModel.values)

            view?.lock()
            view?.updateSubmitButton(isLoading: true)

            interactor.performRegistration(with: registerParameters) { (error) in
                self.view?.unlock()
                self.view?.updateSubmitButton(isLoading: false)

                if let error = error {
                    self.view?.show(error: error)
                } else {
                    let dataToTrack = [PIAnalytics.Keys.emailOptIn: registerParameters.marketingOptIn]
                    AnalyticsManager.shared.trackAction(PIAnalytics.Action.accountCreated, userInfo: dataToTrack)

                    if self.interactor?.shouldUpdateBiometricSettings == true {
                        self.view?.askForBiometricActivation {}
                    } else {
                        self.router?.completeRegister()
                    }
                }
            }
        } catch {
            if let error = error as? RowValidatorError, let indexPath = viewModel.indexPath(for: error.row) {
                view?.focusFormRow(at: indexPath)
            } else {
                print(error.localizedDescription)
                view?.presentAlertWith(
                    title: PILocalizedString("registerScreenErrorTitle", comment: "Register screen: error title"),
                    error: error
                )
            }
        }
    }

    func dismissRegisterDidTap() {
        router?.dismissRegisterView()
    }

    func biometricActivationCompleted(activated: Bool) {
        interactor?.biometricStatusChanged(enabled: activated)
        router?.dismissRegisterView()
    }

    func salutationRowDidTap() {
        router?.selectedSalutation { [weak self] salutation in
            guard let salutation = salutation else { return }
            self?.view?.selected(salutation: salutation)
        }
    }
}
