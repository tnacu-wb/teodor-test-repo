//
//  UserDetailsInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

enum Step1Error: LocalizedError {
	case missingPurpose
	case missingBookerIsStayer
	case missingInteractor
	case missingUser

    var errorDescription: String? {
		switch self {
		case .missingUser:
			return PILocalizedString("accountLoginCellTitle", comment: "Account Login Cell Title")
		default:
			return String(describing: self)
		}
    }
}

protocol UserDetailsDataProvider {
    func getMarketingPreferences(
        for emailAddress: String,
        and brands: MarketingBrandCode,
        isBusiness: Bool,
        completion: @escaping (_ data: MarketingPreferences?, _ error: Error?) -> Void
    )
    func updateMarketingPreferences(
        for brands: [MarketingBrandCode],
        and emailAddress: String,
        optin: Bool,
        isoCountryCode: String,
        completion: @escaping (_ success: Bool, _ error: Error?) -> Void
    )
    func anonymousNewsletterPreferences(
        for email: String,
        countryOfResidence: String,
        completion: @escaping (_ success: AnonymousNewsletterPreferences?, _ error: Error?) -> Void
    )
    func performHoldBookingWithGuests(
        bookingDetails: BookingDetails,
        isCiolFlow: Bool,
        isRegCard: Bool,
        completion: @escaping (_ success: Bool, _ error: Error?) -> Void
    )
	func updateUserDetails(
	    for user: User,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	)
}

protocol UserDetailsInteractorProtocol: AnyObject {
    var scope: UserDetailScope { get }
	var user: User? { get }
	var bookerIsStaying: Bool { get }
	var rooms: [Room] { get }
	var purpose: TripPurpose? { get set }
    var wasUserDetailsComplete: Bool { get }
    var cityTaxRequired: Bool { get }
    var marketingOptInPreference: Bool? { get }
    var shouldShowCreateAccountSection: Bool { get }
    var bookingDetails: BookingDetails? { get }

    var suppressEmailSection: Bool { get }
    var isMarketingSwitchEnabled: Bool { get }

	func reloadRooms()
	func getBookerGuestsAndPurpose(from values: PIDictionary?) throws -> FormStep1Output
    func updateUser(with values: PIDictionary?, completion: @escaping (Error?) -> Void) throws
	func bookerIsStayingDidChange(isStaying: Bool)
    func updateMarketingPreference(
        optin: Bool,
        emailAddress: String,
        doubleOptIn: Bool,
        isoCountryCode: String,
        completion: (() -> Void)?
    )
    func getMarketingPreferences(
        for emailAddress: String,
        and brands: MarketingBrandCode,
        isBusiness: Bool,
        completion: @escaping (Bool) -> Void
    )
    func updateAnonymousNewsLetter(email: String, country: String, completion: @escaping () -> Void)
    func updateCountry(code: String, email: String, completion: @escaping () -> Void)
    func updateMarketingValues(optIn: Bool?, suppress: Bool)
    func updateUserDetailsCompletionState(_ isComplete: Bool) -> Bool
    func holdBookingWithGuests(
        output: FormStep1Output,
        isCiolFlow: Bool,
        completion: @escaping (_ success: Bool, _ error: Error?) -> Void
    )
}

extension RequestsManager: UserDetailsDataProvider {}

class UserDetailsInteractor {
	let bookingDetails: BookingDetails?

    var scope: UserDetailScope
    var bookerIsFirstRoomGuest: Bool = BookingDetails.sharedInstance.isBookerStaying ?? true
    var marketingOptInPreference: Bool? = UserSessionManager.sharedInstance.currentUser?.optedIn(for: .premierInn)
    var suppressEmailSection: Bool = false
    var wasUserDetailsComplete: Bool = false
    var registerDataManager: RegisterInteractorOutput?
    var loginDataManager: LoginInteractorOutput?

	private let loggedUser: User?

	private var tempRooms: [Room]
	private var bookerIsStayingUserAction: Bool?

    var userDetailsDataProvider: UserDetailsDataProvider? = RequestsManager()

    // MARK: - Init

    init(user: User?, bookingDetails: BookingDetails?, scope: UserDetailScope) {
        if scope == .userPreferences {
            self.bookingDetails = nil
            self.tempRooms = []
            self.loggedUser = user
            self.scope = scope
        } else {
            self.bookingDetails = bookingDetails
            self.tempRooms = UserDetailsInteractor.createDefaultRooms(with: bookingDetails)
            self.suppressEmailSection = UserSessionManager.sharedInstance.currentUser?.suppressMarketingBox ?? false
            self.loggedUser = user
            self.scope = scope
        }
    }

    private static func createDefaultRooms(with bookingDetails: BookingDetails?) -> [Room] {
        let rooms = bookingDetails?.criteria.rooms.map { (room) -> Room in
            let copiedRoom = Room()
            copiedRoom.leadGuest = room.leadGuest
            return copiedRoom
        }

        if rooms?.first?.leadGuest == nil {
            rooms?.first?.leadGuest = bookingDetails?.booker
        }

        return rooms ?? []
	}

	private static func address(withValues values: PIDictionary?) throws -> Address? {
		var addressDictionary = PIDictionary()

		if let postcode = values?[CountryActionableRow.postCode.rawValue] as? String {
			addressDictionary["postcode"] = postcode
		}
		if let line1 = values?[CountryActionableRow.addressLine1.rawValue] as? String {
			addressDictionary["line1"] = line1
		}
		if let line2 = values?[CountryActionableRow.addressLine2.rawValue] as? String {
			addressDictionary["line2"] = line2
		}
		if let line3 = values?[CountryActionableRow.addressLine3.rawValue] as? String {
			addressDictionary["line3"] = line3
		}
		if let country = values?[CountryActionableRow.country.rawValue] as? Country {
			addressDictionary["countryCode"] = country.isoCode
			addressDictionary["country"] = country.name
		}

        if let company = values?[GuestDetailsRow.companyName.rawValue] as? String {
            addressDictionary["companyName"] = company
        }

        if let type = values?[GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix] as? AddressType {
            addressDictionary["type"] = type
        }
		return try Address(dictionary: addressDictionary)
	}
}

extension UserDetailsInteractor: UserDetailsInteractorProtocol {
	var user: User? {
		loggedUser ?? bookingDetails?.booker
	}

    var rooms: [Room] {
        tempRooms
    }

    var bookerIsStaying: Bool {
        if let userAction = bookerIsStayingUserAction {
            return userAction
        }
        return bookerIsFirstRoomGuest
    }

	var purpose: TripPurpose? {
		get {
			bookingDetails?.purpose
		}
		set {
			bookingDetails?.purpose = newValue
		}
	}

    var cityTaxRequired: Bool {
        guard let cityTaxTotal = bookingDetails?.cityTaxTotal else { return false }
        guard cityTaxTotal.amount.doubleValue > 0 else { return false }
        guard let purpose = purpose else { return false }
        return purpose == .leisure ? bookingDetails?.hotel?.cityTaxForLeisure ?? false : bookingDetails?.hotel?
            .cityTaxForBusiness ?? false
    }

    var isMarketingSwitchEnabled: Bool {
        marketingOptInPreference ?? true
    }

    var shouldShowCreateAccountSection: Bool {
        UserSessionManager.sharedInstance.currentUser == nil
    }

    func updateAnonymousNewsLetter(email: String, country: String, completion: @escaping () -> Void) {
        getPreferencesForAnonUser(email: email, country: country, completion: completion)
    }

    func updateCountry(code: String, email: String, completion: @escaping () -> Void) {
        getPreferencesForAnonUser(email: email, country: code, completion: completion)
    }

    private func getPreferencesForAnonUser(email: String, country: String, completion: @escaping () -> Void) {
        guard (scope == .bookingFlow || scope == .bookingFlowEditing) && email.isNotEmpty else {
            completion()
            return
        }

        userDetailsDataProvider?.anonymousNewsletterPreferences(for: email, countryOfResidence: country) { preference, _ in
            // Currently not handling the error as it is silent, if the call fails we assume that we wont hide the checkbox
            guard let preference else {
                self.suppressEmailSection = false
                self.marketingOptInPreference = UserSessionManager.sharedInstance.currentUser?.optedIn(for: .premierInn)
                return completion()
            }

            self.suppressEmailSection = preference.suppressMarketingCheckbox == true
            self.marketingOptInPreference = preference.optIn == true

            var params: [String: Any] = [:]
            params[PIAnalytics.Keys.bfSuppressMarketingBox] = "\(preference.suppressMarketingCheckbox == true)"
            AnalyticsManager.shared.trackAction(PIAnalytics.Action.marketingSuppressionAnonymous, userInfo: params)

            completion()
        }
    }

    func updateMarketingValues(optIn: Bool?, suppress: Bool) {
        if let optIn {
            marketingOptInPreference = optIn
        }

        suppressEmailSection = suppress
    }


    func updateUserDetailsCompletionState(_ isComplete: Bool) -> Bool {
        let didUpdate = wasUserDetailsComplete != isComplete
        wasUserDetailsComplete = isComplete

        return didUpdate
    }

	func reloadRooms() {
        tempRooms = UserDetailsInteractor.createDefaultRooms(with: bookingDetails)
	}

	func bookerIsStayingDidChange(isStaying: Bool) {
        bookerIsFirstRoomGuest = isStaying
        bookerIsStayingUserAction = isStaying
        BookingDetails.sharedInstance.isBookerStaying = isStaying
        tempRooms.first?.leadGuest = isStaying ? bookingDetails?.booker : nil
	}

	func getBookerGuestsAndPurpose(from values: PIDictionary?) throws -> FormStep1Output {
		// Read booker's info from form values
		let title = values?[Step1Row.salutation.rawValue + Constants.bookerSuffix] as? String
		let firstName = values?[Step1Row.firstName.rawValue + Constants.bookerSuffix] as? String
		let lastName = values?[Step1Row.lastName.rawValue + Constants.bookerSuffix] as? String

        let booker: User = try {
            if let existingBooker = bookingDetails?.booker { return existingBooker }

            return try User(title: title, firstName: firstName, lastName: lastName)
        }()

        booker.title = title ?? booker.title
        booker.firstName = firstName ?? booker.firstName
        booker.lastName = lastName ?? booker.lastName
		booker.emailAddress = values?[Step1Row.emailAddress.rawValue + Constants.bookerSuffix] as? String
		booker.contactNumber = values?[Step1Row.contactNumber.rawValue + Constants.bookerSuffix] as? String
		booker.address = try UserDetailsInteractor.address(withValues: values)

        if let currentUser = UserSessionManager.sharedInstance.currentUser {
            booker.copyCompany(from: currentUser)
        }

        // store marketing opt in value
        self.bookingDetails?.marketingOptIn = values?[Step1Row.marketing.rawValue] as? Bool ?? true

		// Read guests' info from form values and return a list of Users
		var guests = [User]()

		for index in 0..<tempRooms.count {
			if index == 0 && bookerIsFirstRoomGuest {
				guests.append(booker)
                tempRooms.first?.leadGuest = booker
				continue
			}

			let title = values?[Step1Row.salutation.rawValue + "\(index)"] as? String
			let firstName = values?[Step1Row.firstName.rawValue + "\(index)"] as? String
			let lastName = values?[Step1Row.lastName.rawValue + "\(index)"] as? String
			let email = values?[Step1Row.emailAddress.rawValue + "\(index)"] as? String

			let guest = try User(title: title, firstName: firstName, lastName: lastName)
			guest.emailAddress = email

			guests.append(guest)
		}

        guard let purpose = purpose else { throw Step1Error.missingPurpose }

		return (booker: booker, guests: guests, purpose: purpose)
	}

    func updateUser(with values: PIDictionary?, completion: @escaping (Error?) -> Void) throws {
		guard let user = loggedUser else { throw Step1Error.missingUser }

		if let title = values?[Step1Row.salutation.rawValue + Constants.bookerSuffix] as? String {
			user.title = title
		}

		if let firstName = values?[Step1Row.firstName.rawValue + Constants.bookerSuffix] as? String {
			user.firstName = firstName
		}

		if let lastName = values?[Step1Row.lastName.rawValue + Constants.bookerSuffix] as? String {
			user.lastName = lastName
		}

		user.emailAddress = values?[Step1Row.emailAddress.rawValue + Constants.bookerSuffix] as? String
        user.contactNumber = values?[Step1Row.contactNumber.rawValue + Constants.bookerSuffix] as? String
        user.address = try UserDetailsInteractor.address(withValues: values)
		user.carRegistration = values?[Step1Row.carRegistration.rawValue] as? String
        user.country = values?[Step1Row.nationality.rawValue + Constants.bookerSuffix] as? Country
        user.passport = {
            guard let number = values?[Step1Row.passport.rawValue] as? String else { return nil }
            guard let countryOfIssue = user.country?.name else { return nil }

            return Passport(number: number, countryOfIssue: String(countryOfIssue.prefix(25)))
        }()

        UserSessionManager.sharedInstance.refreshUser { [weak self] success, error in
            guard success else { return completion(error) }

			let sensorData = AkamaiProtection.sensorData

			self?.userDetailsDataProvider?.updateUserDetails(for: user, sensorData: sensorData) { _, error in
                BookingDetails.sharedInstance.booker = nil
                BookingDetails.sharedInstance.meal = nil

                if let updatedEmail = user.emailAddress {
                    let usernameKey: String = {
                        user.isBusiness ? .storedBusinessUsernameKey : .storedUsernameKey
                    }()

                    guard let existingEmail = UserDefaults.standard.string(forKey: usernameKey) else {
                        return
                    }

                    guard var credentials = User.storedCredentials(
                        for: existingEmail,
                        business: user.isBusiness
                    ) else {
                        return
                    }

                    credentials.username = updatedEmail
                    User.saveCredentials(credentials)

                    UserDefaults.standard.set(updatedEmail, forKey: .storedUsernameKey)
                }

                UserSessionManager.sharedInstance.piLoggedIn(with: user)

                completion(error)
            }
        }
    }

    func getMarketingPreferences(
        for emailAddress: String,
        and brands: MarketingBrandCode,
        isBusiness: Bool,
        completion: @escaping (Bool) -> Void
    ) {
        userDetailsDataProvider?.getMarketingPreferences(
            for: emailAddress,
            and: brands,
            isBusiness: isBusiness
        ) { [weak self] marketingPrefs, _ in
            guard let marketingPrefs else {
                completion(false)
                return
            }
            self?.loggedUser?.marketingPreferences = marketingPrefs
            self?.marketingOptInPreference = self?.loggedUser?.optedIn(for: brands)
            completion(true)
        }
    }

    func updateMarketingPreference(
        optin: Bool,
        emailAddress: String,
        doubleOptIn: Bool,
        isoCountryCode: String,
        completion: (() -> Void)? = nil
    ) {
        guard optin != UserSessionManager.sharedInstance.currentUser?.optedIn(for: .premierInn) else {
            completion?()
            return
        }

        userDetailsDataProvider?.updateMarketingPreferences(
            for: [.premierInn],
            and: emailAddress,
            optin: optin,
            isoCountryCode: isoCountryCode
        ) { success, _ in
            guard success == true else {
                completion?()
                return
            }

            self.marketingOptInPreference = optin
            self.bookingDetails?.marketingOptIn = optin

            guard let user = UserSessionManager.sharedInstance.currentUser else {
                completion?()
                return
            }
            UserSessionManager.sharedInstance.piLoggedIn(with: user)

            completion?()
        }
    }

    func holdBookingWithGuests(
        output: FormStep1Output,
        isCiolFlow: Bool,
        completion: @escaping (_ success: Bool, _ error: Error?) -> Void
    ) {
        // refactor these into separate values and change the input parameters of the call (leadGuest, hotelCode and whatever else we need)
        let bookingDetails = BookingDetails.sharedInstance
        bookingDetails.booker = output.booker
        bookingDetails.purpose = output.purpose
        _ = zip(bookingDetails.criteria.rooms, output.guests).map { $0.0.leadGuest = $0.1 }

        userDetailsDataProvider?.performHoldBookingWithGuests(
            bookingDetails: bookingDetails,
            isCiolFlow: isCiolFlow,
            isRegCard: false,
            completion: completion
        )
    }

    private func handleLoginResult(
        _ result: Result<Bool>,
        credentials: AuthCredentials,
        completion: @escaping (Error?) -> Void
    ) {
        switch result {
        case .success:
            // Dont need to handle business yet
            UserDefaults.standard.set(credentials.username, forKey: .storedUsernameKey)
            SettingsManager.sharedInstance.shouldAttemptAutoLogin = true
            User.saveCredentials(credentials)

            loginDataManager?.getUser(
                userId: credentials.username,
                isBusiness: UserSessionManager.sharedInstance.currentUser?.isBusiness ?? false
            ) { result in
                DispatchQueue.main.async {
                    let error = result.handle()

                    // refresh value after login and getUser
                    self.marketingOptInPreference = UserSessionManager.sharedInstance.currentUser?.optedIn(for: .premierInn)
                    self.suppressEmailSection = UserSessionManager.sharedInstance.currentUser?.suppressMarketingBox == true

                    self.loginDataManager?.refreshStays(
                        for: UserSessionManager.sharedInstance.currentUser,
                        shouldAttemptLogin: false
                    ) {_ in }

                    completion(error)
                }
            }

        case .failure(let error):
            AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.remoteLoginError)

            DispatchQueue.main.async {
                completion(error)
            }
        }
    }
}
