//
//  UserDetailsPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

enum UserDetailScope {
    case bookingFlow
    case bookingFlowEditing
    case userPreferences
    case amendFlow
}

protocol UserDetailsViewProtocol: AnyObject {
    var parentNavigationController: UINavigationController? { get }

    func showCancelButton()
    func setTitle(_ title: String?)
    func loadViewModel(conf: UserDetailsConfiguration)
    func setSalutation(_ salutation: String?, indexPath: IndexPath)
    func setSalutationForFirstGuest(_ salutation: String?)
    func reload()
    func endEditing()
    func addAddressManually()
    func showErrorMessage(title: String, message: String, error: Error?, handler: ((UIAlertAction) -> Void)?)
    func showErrorMessage(title: String, error: Error)
    func showErrorFor(row: FormekaModelRow)
    func validateViewModel() throws
    func getViewModelValues() -> JsonDictionary?
    func getErrorForRowTagged(_ tag: String) -> Error?
    func updateBookerStayer(isStaying: Bool)
    func userDidChange()
    func toggleFormLock(locked: Bool, submitButtonTitle: String?)
    func loadMarketingModel(model: UserMarketingModel)
    func updateMarketingSection(model: UserMarketingModel?)
    func reloadFirstRoomSection(shouldShow: Bool)
}

final class UserDetailsPresenter {
    weak var view: UserDetailsViewProtocol?

    var reviewBookDelegate: UserDetailsRouterDelegate?
    var router: UserDetailsRouterProtocol?
    var interactor: UserDetailsInteractorProtocol?

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    var configuration: UserDetailsConfiguration {
        UserDetailsConfiguration(
            scope: scope,
            user: interactor?.user,
            roomConfigurations: roomConfigurations,
            marketingModel: messageConfigs.marketingModel,
            tripPurposeModel: messageConfigs.tripPurposeModel
        )
    }

    private var messageConfigs: UserDetailsMessageConfigurations {
        let shouldSuppressEmailSection = interactor?.suppressEmailSection ?? false
        let isMarketingSwitchEnabled = interactor?.isMarketingSwitchEnabled ?? false
        let bookingContext = BookingDetailsMessageContext(bookingDetails: interactor?.bookingDetails ?? .sharedInstance)

        return UserDetailsMessageConfigurations(
            suppressEmailSection: shouldSuppressEmailSection,
            isMarketingSwitchEnabled: isMarketingSwitchEnabled,
            bookingContext: bookingContext,
            scope: scope
        )
    }

    private var isUserDetailsComplete: Bool {
        guard let values = view?.getViewModelValues(),
              let title = values[Step1Row.salutation.rawValue + Constants.bookerSuffix] as? String,
              let firstName = values[Step1Row.firstName.rawValue + Constants.bookerSuffix] as? String,
              let lastName = values[Step1Row.lastName.rawValue + Constants.bookerSuffix] as? String,
              let emailAddress = values[Step1Row.emailAddress.rawValue + Constants.bookerSuffix] as? String,
              let contactNumber = values[Step1Row.contactNumber.rawValue + Constants.bookerSuffix] as? String else {
            return false
        }

        return [title, firstName, lastName, emailAddress, contactNumber]
            .map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
            .allSatisfy { !$0.isEmpty }
    }

    private var shouldShowFirstRoom: Bool {
        isUserLoggedIn || isUserDetailsComplete
    }

    private var roomConfigurations: [RoomConfig] {
        rooms.enumerated().map { index, room in
            let shouldShowRoomInfo = index == 0 ? shouldShowFirstRoom : true
            return RoomConfig(
                user: room.leadGuest,
                roomIndex: index,
                totalRooms: rooms.count,
                isBookerStaying: bookerIsStaying,
                shouldShowRoomInfo: shouldShowRoomInfo
            )
        }
    }

    private var isUserLoggedIn: Bool {
        UserSessionManager.sharedInstance.currentUser != nil
    }

    private var scope: UserDetailScope {
        interactor?.scope ?? .bookingFlow
    }
}

// MARK: - Notifications

private extension UserDetailsPresenter {
    @objc func userDidChange(notification: Notification) {
        interactor?.reloadRooms()

        view?.userDidChange()
        view?.loadViewModel(conf: configuration)
        view?.reload()
    }

    @objc func marketingPreferencesDidChange(notification: Notification) {
        if let user = UserSessionManager.sharedInstance.currentUser {
            interactor?.updateMarketingValues(optIn: user.optedIn(for: .premierInn), suppress: user.suppressMarketingBox)
            view?.updateMarketingSection(model: messageConfigs.marketingModel)

            // ^^ ... Bug has got to do with something here ... ^^

            var params: [String: Any] = [:]
            params[PIAnalytics.Keys.bfSuppressMarketingBox] = "\(user.suppressMarketingBox)"
            params[PIAnalytics.Keys.bfOptedInToMarketing] = "\(user.optedIn(for: .premierInn) ?? false)"
            AnalyticsManager.shared.trackAction(PIAnalytics.Action.marketingSuppressionLoggedIn, userInfo: params)
        }
    }

    @objc func guestsDidChange(notification: Notification) {
        interactor?.reloadRooms()

        view?.loadViewModel(conf: configuration)
        view?.reload()
    }
}

// MARK: - Conformances

extension UserDetailsPresenter: UserDetailsPresenterProtocol {
    var user: User? {
         interactor?.user
    }

    var rooms: [Room] {
        interactor?.rooms ?? []
    }

    var bookerIsStaying: Bool {
        interactor?.bookerIsStaying ?? true
    }

    var purpose: TripPurpose? {
        get {
            interactor?.purpose
        }
        set {
            interactor?.purpose = newValue
        }
    }

    var cityTaxRequired: Bool {
        interactor?.cityTaxRequired ?? false
    }

    var purposeMessages: [TripPurposeMessageModel]? {
        guard let purpose = purpose else {
            return messageConfigs.tripPurposeModel?.leisureMessages
        }

        return purpose == .leisure ?
        messageConfigs.tripPurposeModel?.leisureMessages :
        messageConfigs.tripPurposeModel?.businessMessages
    }

    var firstRoomConfig: RoomConfig? {
        roomConfigurations.first
    }

    func countryChanged(for country: String, email: String) {
        interactor?.updateCountry(code: country, email: email) { [weak self] in
            guard let self else { return }
            DispatchQueue.main.async {
                self.view?.updateMarketingSection(model: self.messageConfigs.marketingModel)
            }
        }
    }

    func updatePurpose(_ purpose: TripPurposeSelections?) {
        switch purpose {
        case .leisure:
            interactor?.purpose = .leisure
        case .business:
            interactor?.purpose = .business
        default:
            interactor?.purpose = nil
        }
    }

    func viewIsReady() {
        getMarketingPreferences(
            for: configuration.user?.emailAddress,
            brands: .premierInn,
            isBusiness: configuration.user?.isBusiness
        )

        if scope == .bookingFlowEditing {
            view?.showCancelButton()
        }

        view?.setTitle(PILocalizedString("userDetailsScreenTitle"))

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(userDidChange(notification:)),
            name: .userDidChange,
            object: nil
        )
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(guestsDidChange(notification:)),
            name: .guestsDidChange,
            object: nil
        )

        AnalyticsManager.shared.log(event: FirebaseAnalytics.Event.addPaymentInfo, parameters: nil)

        updateMarketingRowIfPreviousBookingDetailsAvailable()
        view?.loadViewModel(conf: configuration)
    }

    func userDetailsDidChange() {
        let didChange = interactor?.updateUserDetailsCompletionState(shouldShowFirstRoom) ?? false
        guard didChange else { return }

        view?.reloadFirstRoomSection(shouldShow: shouldShowFirstRoom)
    }

    func bookerIsStayingDidChange(isStaying: Bool) {
        interactor?.bookerIsStayingDidChange(isStaying: isStaying)

        view?.updateBookerStayer(isStaying: isStaying)
    }

    func updateNewsletterRow(email: String, country: String) {
        interactor?.updateAnonymousNewsLetter(email: email, country: country, completion: { [weak self] in
            guard let self else { return }
            DispatchQueue.main.async {
                self.view?.updateMarketingSection(model: self.messageConfigs.marketingModel)
                self.view?.reload()
            }
        })
    }

    private func getMarketingPreferences(
        for email: String?,
        brands: MarketingBrandCode,
        isBusiness: Bool?
    ) {
        guard scope == .userPreferences,
              let email,
              let isBusiness else {
            return
        }
        interactor?.getMarketingPreferences(
            for: email,
            and: brands,
            isBusiness: isBusiness,
        ) { [weak self] success in
            guard success else {
                return
            }
            self?.view?.updateMarketingSection(
                model: self?.messageConfigs.marketingModel
            )
        }
    }

    private func updateMarketingRowIfPreviousBookingDetailsAvailable() {
        guard let email = configuration.user?.emailAddress,
              let country = configuration.user?.address?.country?.isoCode else {
            return
        }

        updateNewsletterRow(email: email, country: country)
    }

    func salutationRowDidTap(indexPath: IndexPath) {
        router?.presentSalutationPicker(for: indexPath)
    }

    func updateMarketing() {
        guard let interactor = interactor,
              let values = view?.getViewModelValues(),
              let email = values[Step1Row.emailAddress.rawValue + Constants.bookerSuffix] as? String,
              let optIn = values[Step1Row.marketing.rawValue] as? Bool else { return }

        let country = values[CountryActionableRow.country.rawValue] as? Country
        let isoCodeCountry = country?.isoCode ?? "GB"

        interactor.updateMarketingPreference(
            optin: optIn,
            emailAddress: email,
            doubleOptIn: isoCodeCountry == "DE",
            isoCountryCode: isoCodeCountry,
            completion: nil
        )
    }

    func didTapDeleteAccount() {
        router?.goToDeleteAccount()
    }

    func submitButtonDidTap() {
        guard let interactor else { return }

        UserDetailsSubmissionCoordinator(
            view: view,
            router: router,
            reviewBookDelegate: reviewBookDelegate,
            interactor: interactor,
            getConfiguration: { [weak self] in
                self?.configuration
            },
            updateMarketing: { [weak self] in
                self?.updateMarketing()
            }
        )
        .submitButtonDidTap()
    }

    func loginButtonDidTap() {
        NotificationCenter.default.removeObserver(self, name: .marketingPreferencesDidChange, object: nil)
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(marketingPreferencesDidChange(notification:)),
            name: .marketingPreferencesDidChange,
            object: nil
        )
        router?.presentLogin()
    }

    func setSalutation(_ salutation: String?, at indexPath: IndexPath) {
        view?.setSalutation(salutation, indexPath: indexPath)

        if let isGuest = interactor?.bookerIsStaying, isGuest == true {
            view?.setSalutationForFirstGuest(salutation)
        }

        userDetailsDidChange()
        view?.reload()
    }

    func userIsStayingForBusiness() {}
}
