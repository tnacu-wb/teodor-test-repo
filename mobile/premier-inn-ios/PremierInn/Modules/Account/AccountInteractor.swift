//
//  AccountInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 21/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork

protocol AccountDataProvider {
    func getUser(userId: String, isBusiness: Bool, completion: @escaping (Result<User>) -> Void)
    func autoLogin(completion: @escaping (User?) -> Void)
}

extension RequestsManager: AccountDataProvider {}

class AccountInteractor {
    private var isLoading = false
    var accountDataProvider: AccountDataProvider? = RequestsManager()

    private var user: User? {
        UserSessionManager.sharedInstance.currentUser
    }
}

extension MyAccountLink: CustomAccountLinkViewModel {
    var title: String {
        ctaTitle
    }
}

extension AccountInteractor: AccountInteractorProtocol {
    private struct ViewModel: AccountViewModel {
        let username: String?
        let email: String?
        let business: (isBusiness: Bool, companyName: String?)
        let userLoggedIn: Bool
        let customLinks: [CustomAccountLinkViewModel]
        let shouldShowPaymentMethods: Bool
    }

    var accountCreatedMessage: String {
        guard let registeredUserFirstName = user?.firstName else { return "" }

        return String.localizedStringWithFormat(
            PILocalizedString(
                "userPreferenceUserRegisteredBannerTitle",
                comment: "User preference user registered banner title"
            ),
            registeredUserFirstName
        )
    }

    var detailsChangedMessage: String {
        PILocalizedString(
            "accountPreferencesUserDetailsUpdatedSuccessBannerTitle",
            comment: "Your personal details have been successfully updated"
        )
    }

    var deletedCardMessage: String {
        PILocalizedString("cardDeletedSuccessBannerTitle", comment: "Your saved card has successfully been deleted")
    }

    var savedCardMessage: String {
        PILocalizedString("cardSavedSuccessBannerTitle", comment: "Your saved card has successfully been deleted")
    }

    var updatedCardMessage: String {
        PILocalizedString("Your payment details have been saved")
    }

    var passwordChangedMessage: String {
        PILocalizedString(
            "accountPreferencesPasswordUpdatedSuccessBannerTitle",
            comment: "Account preferences password updated success banner title"
        )
    }

    var accountViewModel: AccountViewModel? {
        let userDetails: (username: String?, email: String?) = {
            guard let user = user else { return (nil, nil) }
            return ("\(PILocalizedString("welcome")) \(user.firstName ?? "") \(user.lastName ?? "")", user.emailAddress)
        }()

        let customLinks: [CustomAccountLinkViewModel] = SettingsManager.sharedInstance.myAccountLinks.filter { $0.isActive }
        let shouldShowPaymentMethods: Bool = {
            guard user?.emailAddress != nil else { return false }
            guard user?.business?.employeeId != nil else { return true }

            return user?.centrallyStoredBusinessCard != nil || user?.paymentPreference?.card != nil
        }()

        return ViewModel(
            username: userDetails.username,
            email: userDetails.email,
            business: (user?.isBusiness == true, user?.company?.companyDetails?.companyName),
            userLoggedIn: user?.emailAddress != nil,
            customLinks: customLinks,
            shouldShowPaymentMethods: shouldShowPaymentMethods
        )
    }

    func userLoggedOut() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        SettingsManager.sharedInstance.shouldAttemptAutoLogin = false
        RequestsManager.removeStays()
    }

    func updateUser(shouldAttemptLogin: Bool = true, completion: @escaping () -> Void) {
        guard isLoading == false else { return }

        guard let user else {
            completion()
            return
        }

        isLoading = true

        accountDataProvider?.getUser(userId: user.emailAddress ?? "", isBusiness: user.isBusiness) { result in
            self.isLoading = false

            switch result {
            case .success(let user):

                UserSessionManager.sharedInstance.currentUser?.paymentPreference = user.paymentPreference

            case .failure:

                break
            }

            completion()
        }
    }
}
