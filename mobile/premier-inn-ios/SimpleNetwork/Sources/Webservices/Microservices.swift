//
//  Microservices.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

typealias CheckInOnlineParameters = (
    sessionId: String,
    confirmationNumber: String,
    paymentCard: PaymentCard?,
    billingAddress: Address?,
    cvv: String,
    useStoredCard: Bool,
    paymentConfirmationEmail: String
)

enum MicroservicesError: LocalizedError {
    case decodeError

    var localizedDescription: String { String(describing: self) }
    var errorDescription: String? { String(describing: self) }
}

enum PIPutURLAndJSONCombinedParamsEncodingError: LocalizedError {
    case missingURL
    case missingJSON

    var errorDescription: String? { String(describing: self) }
}

enum CompanyError: LocalizedError {
    case genericError
    case parseError
    case decodeError

    var errorDescription: String? { String(describing: self) }
}

enum MarketingError: LocalizedError {
    case missingBrandCodes

    var errorDescription: String? { String(describing: self) }
}

enum DashboardComponentError: LocalizedError {
    case genericError
    case parseError
    case decodeError

    var errorDescription: String? { String(describing: self) }
}

// swiftlint:disable file_length

// Need this special ParameterEncoding because Alamofire default
// implementation adds [] for arrays params while Microservice 
// does not expect that.
// IE: sort=DISTANCE&sort=PRICE instead of sort[]=DISTANCE&sort[]=PRICE
struct PIURLEncoding: ParameterEncoding {
    public static var `default`: PIURLEncoding { PIURLEncoding() }

    func encode(_ urlRequest: URLRequestConvertible, with parameters: Parameters?) throws -> URLRequest {
        var encodedRequest = try URLEncoding().encode(urlRequest, with: parameters)

        let urlString = encodedRequest.url?.absoluteString
        guard let fixedString = urlString?.replacingOccurrences(of: "%5B%5D", with: "") else {
            return encodedRequest
        }

        guard let url = URL(string: fixedString) else {
            return encodedRequest
        }

        encodedRequest.url = url

        return encodedRequest
    }
}

struct PIPutURLAndJSONCombinedParamsEncoding: ParameterEncoding {
    public static var `default`: PIPutURLAndJSONCombinedParamsEncoding { PIPutURLAndJSONCombinedParamsEncoding() }

    func encode(_ urlRequest: URLRequestConvertible, with parameters: Parameters?) throws -> URLRequest {
        guard let urlParams = parameters?["url"] as? [String: Any] else {
            throw PIPutURLAndJSONCombinedParamsEncodingError.missingURL
        }

        guard let jsonParams = parameters?["json"] as? [String: Any] else {
            throw PIPutURLAndJSONCombinedParamsEncodingError.missingJSON
        }

        var encodedRequest = try URLEncoding().encode(urlRequest, with: urlParams)

        guard let url = encodedRequest.url else { return encodedRequest }

        encodedRequest.httpMethod = HTTPMethod.put.rawValue
        encodedRequest = try JSONEncoding().encode(encodedRequest, with: jsonParams)

        encodedRequest.url = url

        return encodedRequest
    }
}

private extension String {
    static let addressesKey = "addresses"
    static let sessionKey = "sessionId"
    static let ciolPaymentAllowedKey = "paymentAllowed"
    static let ciolPaymentRequiredKey = "paymentRequired"
    static let ciolOutstandingAmountKey = "outstandingAmount"
    static let ciolUpsellItemsKey = "upsellItemsAvailable"
    static let successKey = "success"
    static let sessionIdKey = "session-id"
    static let authorizationKey = "Authorization"
    static let paymentRequiredKey = "requiresPayment"
    static let checkInComplete = "checkInComplete"
    static let companyId = "company-id"
    static let employeeId = "employee-id"
    static let doorKey = "key"
}

class Microservices: Webservice {
    private let requestsManager = RequestsManager()
    private var userSessionToken: String? {
        get {
            UserDefaults.standard.string(forKey: .sessionKey)
        }
        set {
            UserDefaults.standard.set(newValue, forKey: .sessionKey)
        }
    }

    private func resource<T>(
        for action: WebserviceAction? = nil,
        url: URL,
        parameters: PIDictionary? = nil,
        data: Data? = nil,
        method: HTTPMethod,
        encoding: ParameterEncoding,
        headers: [String: String]? = nil,
        authCredentials: AuthCredentials? = nil,
        parse: @escaping (Any) throws -> T?
    ) -> Resource<T> {
        var version: String?
        if let action = action {
            version = Router.versioning?[action]
        }
        let parameters = LanguageManager.sharedInstance.addLocalisationParameters(parameters: parameters)
        let headers = addHeaders(headers: headers)

        return Resource(
            url: url,
            parameters: parameters,
            data: data,
            method: method,
            encoding: encoding,
            headers: headers,
            authCredentials: authCredentials,
            version: version,
            parse: parse
        )
    }
}

private extension Microservices {
    func addHeaders(headers: [String: String]?) -> [String: String]? {
        guard let restOrigin = Router.originHost else { return headers }

        var finalHeaders = ["origin": restOrigin]
        headers?.forEach({ (key: String, value: String) in
            finalHeaders[key] = value
        })

        return finalHeaders
    }
}

extension Microservices: WebserviceProtocol {
    func getWalletPass(
        with reservationDetails: ReservationDetails,
        excludeBarcode: Bool
    ) throws -> Resource<Data> {
        let bookingReference = reservationDetails.reservationId
        let surname = reservationDetails.surname
        let arrivalDateString = reservationDetails.arrivalDate.parameterString

        let path = "/v1/hotel-wallet"

        guard let url = baseURL?.appendingPathComponent(path) else {
            throw WebserviceError.invalidPath(path)
        }

        guard bookingReference.isEmpty == false else {
            throw ReservationError.missingReservationIdentifier
        }

        guard surname.isEmpty == false else {
            throw ReservationError.missingLastName
        }

        guard arrivalDateString.isEmpty == false else {
            throw ReservationError.missingArrivalDate
        }

        let channel: Channel = reservationDetails.business == true ? .BB : .PI

        return resource(
            url: url,
            parameters: [
                "reservationNumber": bookingReference,
                "lastName": surname,
                "arrivalDate": arrivalDateString,
                "excludeBarcode": excludeBarcode,
                "channel": channel.rawValue
            ],
            method: .get,
            encoding: URLEncoding.default
        ) { data -> Data in
            guard let data = data as? Data else {
                throw RequestsManagerError.unexpectedResponseError
            }

            guard let passData = Data(base64Encoded: data) else {
                throw RequestsManagerError.unexpectedResponseError
            }

            return passData
        }
    }

    func getDashboardComponents(
        surname: String?,
        arrival: Date?,
        reservationId: String?,
        isBusiness: Bool,
        recentSearchesFlag: Bool
    ) throws -> Resource<[DashboardComponent]> {
        let path = "/dashboard"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        var urlComps = URLComponents(url: url, resolvingAgainstBaseURL: false)
        let isBusinessString: String = { isBusiness == true ? "true" : "false" }()

        urlComps?.queryItems = [URLQueryItem(name: "business", value: isBusinessString)]

        guard let finalUrl = urlComps?.url else { throw WebserviceError.invalidPath(path) }

        let parameters = Microservices.getDashboardParameters(
            reservationId: reservationId,
            surname: surname,
            arrival: arrival,
            recentSearchesFlag: recentSearchesFlag
        )

        let headers: [String: String]? = {
            var headers = [String: String]()

            if let idToken = UserSessionManager.sharedInstance.idToken {
                headers[.authorizationKey] = "Bearer \(idToken)"
            }

            return headers
        }()

        return resource(
            url: finalUrl,
            parameters: parameters,
            method: .get,
            encoding: URLEncoding.default,
            headers: headers
        ) { data in
            guard let dashboardDictionary = data as? [PIDictionary]
                else { throw RequestsManagerError.unexpectedResponseError }
            do {
                let dashboardData = try JSONSerialization.data(withJSONObject: dashboardDictionary, options: .prettyPrinted)

                let decoder = JSONDecoder()
                return try decoder.decode([DashboardComponent].self, from: dashboardData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    // MARK: - User Management

    func getUser(userId: String, isBusiness: Bool = false, completion: @escaping (Result<Resource<User>>) -> Void) {
        let path = "/customers/hotels/" + userId

        guard let url = baseURL?.appendingPathComponent(path) else {
            completion(.failure(error: WebserviceError.invalidPath(path)))
            return
        }

        var urlComps = URLComponents(url: url, resolvingAgainstBaseURL: false)
        urlComps?.queryItems = [URLQueryItem(name: "business", value: (isBusiness ? "true" : "false"))]

        guard let finalUrl = urlComps?.url else {
            completion(.failure(error: WebserviceError.invalidPath(path)))
            return
        }
        guard UserSessionManager.sharedInstance.idToken?.isEmpty == false else {
            completion(.failure(error: RequestsManagerError.missingToken))
            return
        }

        let headers = [
            .authorizationKey: "Bearer \(String(describing: UserSessionManager.sharedInstance.idToken))",
            "hotel-brand": "PI"
        ]

        completion(.success(result: resource(
            url: finalUrl,
            method: .get,
            encoding: URLEncoding.default,
            headers: headers
        ) { data in
            guard let dictionary = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }

            return try User(dictionary: dictionary, sessionId: dictionary["sessionId"] as? String)
        }))
    }

	func getCompany(companyId: String, sensorData: String) throws -> Resource<Company> {
        let path = "/company/" + companyId

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let headers = try {
            guard let idToken = UserSessionManager.sharedInstance.idToken else { throw RequestsManagerError.missingToken }

            return [String.authorizationKey: "Bearer \(idToken)",
                    Constants.akamaiSensorDataKey: sensorData]
        }()

        return resource(
            url: url,
            parameters: nil,
            method: .get,
            encoding: URLEncoding.default,
            headers: headers
        ) { data -> Company? in
            guard let data = data as? PIDictionary else {
                throw RequestsManagerError.unexpectedResponseError
            }

            guard var requestCompanyDictionary = data["requestedCompany"] as? PIDictionary else {
                throw ResponseParserError.keyNotFound(.addressesKey)
            }

            if let allowCentralCreditCard = data["allowCentralCreditCard"] as? Bool {
                requestCompanyDictionary["allowCentralCreditCard"] = allowCentralCreditCard
            }
            if let companyCellCodes = data["companyCellCodes"] as? [PIDictionary] {
                requestCompanyDictionary["companyCellCodes"] = companyCellCodes
            }

            guard let companyData = try? JSONSerialization.data(
                withJSONObject: requestCompanyDictionary,
                options: .prettyPrinted
            ) else { throw CompanyError.parseError }

            do {
                let decoder = JSONDecoder()
                return try decoder.decode(Company.self, from: companyData)
            } catch {
                throw CompanyError.decodeError
            }
        }
    }

    func logout() throws {
        userSessionToken = nil
    }

	func savePaymentCard(for user: User, sensorData: String) throws -> Resource<Bool> {
        guard let email = user.emailAddress else { throw UserError.missingUserIdentifier }

        let jsonParameters = try Microservices.savePaymentCardParams(with: user)

        return try updateUser(with: email, isBusiness: user.company != nil, and: jsonParameters, sensorData: sensorData)
    }

	func updateUserAdditionalGuests(user: User, sensorData: String) throws -> Resource<Bool> {
        guard let email = user.emailAddress else { throw UserError.missingUserIdentifier }
        let parameters = try Microservices.additionalGuestsParams(with: user)

        return try updateUser(with: email, isBusiness: user.company != nil, and: parameters, sensorData: sensorData)
    }

	func updateFoodPreference(user: User, sensorData: String) throws -> Resource<Bool> {
        guard let email = user.emailAddress else { throw UserError.missingUserIdentifier }
        let parameters = try Microservices.getFoodPreferencesDictionary(with: user)

        return try updateUser(with: email, isBusiness: user.company != nil, and: parameters, sensorData: sensorData)
    }

	func updateRoomPreference(user: User, sensorData: String) throws -> Resource<Bool> {
        guard let email = user.emailAddress else { throw UserError.missingUserIdentifier }
        let parameters = try Microservices.getRoomPreferencesDictionary(with: user)

        return try updateUser(with: email, isBusiness: user.company != nil, and: parameters, sensorData: sensorData)
    }

	func updateUserDetails(user: User, sensorData: String) throws -> Resource<Bool> {
		guard let email = user.emailAddress else { throw UserError.missingUserIdentifier }
        let parameters = try Microservices.userDetailsParams(with: user)

        return try updateUser(with: email, isBusiness: user.company != nil, and: parameters, sensorData: sensorData)
	}

	func deletePaymentCard(for user: User, sensorData: String) throws -> Resource<Bool> {
		guard let email = user.emailAddress else { throw UserError.missingUserIdentifier }
        let parameters = try Microservices.deleteCardParams(with: user)

        return try updateUser(with: email, isBusiness: user.company != nil, and: parameters, sensorData: sensorData)
	}

	func changePassword(
	    user: User,
	    existingPassword: String,
	    newPassword: String,
	    sensorData: String
	) throws -> Resource<Bool> {
        guard let email = user.emailAddress else { throw UserError.missingUserIdentifier }
        let parameters = try Microservices.changePasswordParams(
            with: user,
            currentPassword: existingPassword,
            newPassword: newPassword
        )

        return try updateUser(with: email, isBusiness: user.company != nil, and: parameters, sensorData: sensorData)
    }

	func register(registerParameters: RegisterParameters, sensorData: String) throws -> Resource<Bool> {
        let path = "/customers/hotels"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let parameters = try Microservices.registerParameters(registerParameters: registerParameters)

		let headers: [String: String] = [Constants.akamaiSensorDataKey: sensorData]

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: headers
        ) { data -> Bool in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let success = data["success"] as? Bool else { throw RequestsManagerError.unexpectedResponseError }

            return success
        }
    }

    func forgotPassword(emailAddress: String, isBusiness: Bool) throws -> Resource<Bool> {
        let path = "/auth/hotels/forgot-password"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let parameters = ["username": emailAddress]
        let headers = isBusiness ? [Constants.bookingChannelKey: Constants.businessBookingChannel] : nil

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: headers
        ) { data -> Bool in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let success = data[.successKey] as? Bool else { throw ResponseParserError.keyNotFound(.successKey) }

            return success
        }
    }

	private func updateUser(
	    with emailAddress: String,
	    isBusiness: Bool = false,
	    and params: PIDictionary,
	    sensorData: String
	) throws -> Resource<Bool> {
        let path = "/customers/hotels/\(emailAddress)"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let headers: [String: String]? = try {
            var headers = [String: String]()

            if let idToken = UserSessionManager.sharedInstance.idToken {
                headers[.authorizationKey] = "Bearer \(idToken)"
            }
			headers[Constants.akamaiSensorDataKey] = sensorData

            guard !headers.isEmpty else { throw WebserviceError.missingUserSession }
            return headers
        }()

        let urlParameters = ["business": isBusiness ? "true" : "false"]
        let parameters: PIDictionary = ["url": urlParameters, "json": params]

        return resource(
            url: url,
            parameters: parameters,
            method: .get,
            encoding: PIPutURLAndJSONCombinedParamsEncoding.default,
            headers: headers
        ) { data -> Bool in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let success = data[.successKey] as? Bool else { throw ResponseParserError.keyNotFound(.successKey) }

            return success
        }
    }

     func deleteUser() throws -> Resource<Bool> {
         guard let emailAddress = UserSessionManager.sharedInstance.currentUser?.emailAddress else {
             throw UserError.missingUserIdentifier
         }

         let path = "/customers/hotels/\(emailAddress)"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let headers: [String: String]? = try {
            var headers = [String: String]()

            if let idToken = UserSessionManager.sharedInstance.idToken {
                headers[.authorizationKey] = "Bearer \(idToken)"
                headers[Constants.bookingChannelKey] = Constants.bookingChannel
            }

            guard !headers.isEmpty else { throw WebserviceError.missingUserSession }
            return headers
        }()

        return resource(
            url: url,
            parameters: nil,
            method: .delete,
            encoding: URLEncoding.default,
            headers: headers
        ) { data -> Bool in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let success = data[.successKey] as? Bool else { throw ResponseParserError.keyNotFound(.successKey) }

            return success
        }
    }

    // MARK: - Booking and Payment

    func completePayment(with sessionId: String, and paRes: String) throws -> Resource<Bool> {
        let path = "/payment/checkin/completeV2"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let parameters = ["paRes": paRes, "sessionId": sessionId]

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let success = data[.checkInComplete] as? Bool else { return false }

            return success
        }
    }

    // MARK: - Check-In Online

    func startCheckInOnlineSession(with params: StartCheckInRequestParameters) throws
        -> Resource<CheckInOnlineSessionResponse> {
        let path = "/checkin/hotels"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let checkInSessionManager = SimpleStorageManager<CheckInSession>.init(dataSource: UserDefaults.standard)
        let sessionId: String? = checkInSessionManager.items.first(where: { $0.identifier == params.confirmationNumber })?
            .sessionId

        let parameters = Microservices.startCheckInOnlineParameters(
            withConfirmationNumber: params.confirmationNumber,
            surname: params.surname,
            arrivalDate: params.arrivalDate,
            guestHistoryNumber: params.guestHistoryNumber,
            sessionId: sessionId
        )

        return resource(
            for: .startCheckInOnlineSession,
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let sessionIdentifier = data[.sessionKey] as? String
                else { throw ResponseParserError.keyNotFound(.sessionKey) }
            guard let paymentAllowed = data[.ciolPaymentAllowedKey] as? Bool
                else { throw ResponseParserError.keyNotFound(.ciolPaymentAllowedKey) }
            guard let paymentRequired = data[.ciolPaymentRequiredKey] as? Bool
                else { throw ResponseParserError.keyNotFound(.ciolPaymentRequiredKey) }
            guard let paymentDic = data[.ciolOutstandingAmountKey] as? PIDictionary
                else { throw ResponseParserError.keyNotFound(.ciolOutstandingAmountKey) }
            let cost = try Cost(dictionary: paymentDic)
            guard let upsellItemsDic = data[.ciolUpsellItemsKey] as? [PIDictionary]
                else { throw ResponseParserError.keyNotFound(.ciolUpsellItemsKey) }
            let upsellItemsAvailable = upsellItemsDic.compactMap { try? UpsellItem(dictionary: $0) }
            let reservation = try Reservation(dictionary: data)

            let checkInSession = CheckInSession(confirmationNumber: params.confirmationNumber, sessionId: sessionIdentifier)
            _ = checkInSessionManager.remove(checkInSession)
            try? checkInSessionManager.add(checkInSession)

            return (
                sessionIdentifier,
                upsellItemsAvailable,
                paymentAllowed,
                paymentRequired,
                cost,
                reservation.cancelableText
            )
        }
    }

    func addCheckInOnlineGuestDetails(with params: AddCheckInOnlineGuestDetailsParameters) throws -> Resource<Bool> {
        let path = "/checkin/hotels/\(params.sessionID)/guests"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let parameters = Microservices.checkInOnlineAddGuestDetailsParameters(
            withConfirmationNumber: params.confirmationNumber,
            businessTrip: params.asBusinessTrip,
            booker: params.booker,
            and: params.roomsAndNextDestinations
        )

        return resource(url: url, parameters: parameters, method: .put, encoding: JSONEncoding.default) { _ in
            true
        }
    }

    func addCheckInOnlineUpsells(
        withSessionID sessionID: String,
        confirmationNumber: String,
        andUpsells upsells: [UpsellItem]
    ) throws -> Resource<CheckInOnlineAddUpsellsResponse> {
        let path = "/checkin/hotels/\(sessionID)/upsells"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let parameters = Microservices.checkInOnlineUpsellParameters(
            withConfirmationNumber: confirmationNumber,
            andUpsells: upsells
        )

        return resource(url: url, parameters: parameters, method: .put, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let sessionIdentifier = data[.sessionKey] as? String
                else { throw ResponseParserError.keyNotFound(.sessionKey) }
            guard let paymentRequired = data["requiresPayment"] as? Bool
                else { throw ResponseParserError.keyNotFound(.paymentRequiredKey) }

            if paymentRequired == false {
                self.removeCheckInSession(with: confirmationNumber)
            }

            return (sessionIdentifier, paymentRequired)
        }
    }

    func closeCheckInOnlineSession(withSessionID sessionID: String) throws -> Resource<Bool> {
        let path = "/checkin/hotels/" + sessionID

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        return resource(url: url, method: .delete, encoding: URLEncoding.default) { _ in
            true
        }
    }

    func checkInOnlinePayment(
        with sessionId: String,
        confirmationNumber: String,
        paymentDetails: PaymentDetails
    ) throws -> Resource<CheckInPaymentResponse> {
        let path = "/payment/checkin"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let checkInOnlineParameters = CheckInOnlineParameters(
            sessionId: sessionId,
            confirmationNumber: confirmationNumber,
            paymentCard: paymentDetails.card,
            billingAddress: paymentDetails.address,
            cvv: paymentDetails.cvv,
            useStoredCard: paymentDetails.useStoredCard,
            paymentConfirmationEmail: paymentDetails.confirmationEmailAddress
        )
        let parameters = try Microservices.checkInPaymentParameters(parameters: checkInOnlineParameters)

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: nil
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }

            let response = try CheckInPaymentResponse(dictionary: data)

            if response.threeDSecureRequired == false {
                self.removeCheckInSession(with: confirmationNumber)
            }

            return response
        }
    }

    private func removeCheckInSession(with confirmationNumber: String) {
        let checkInSessionManager = SimpleStorageManager<CheckInSession>(dataSource: UserDefaults.standard)
        if let checkInSession = checkInSessionManager.items.first(where: { $0.identifier == confirmationNumber }) {
            _ = checkInSessionManager.remove(checkInSession)
        }
    }

    // MARK: - Door Key

    func getDoorKey(
        reservationDetails: ReservationDetails,
        deviceId: String,
        authenticationCode: String?
    ) throws -> Resource<String> {
        let path = "/keys"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        let headers: [String: String]? = try {
            var headers = [String: String]()

            if let idToken = UserSessionManager.sharedInstance.idToken {
                headers[.authorizationKey] = "Bearer \(idToken)"
            }

            guard !headers.isEmpty else { throw WebserviceError.missingUserSession }
            return headers
        }()

        let parameters = try Microservices.getDoorKeyParameters(
            reservationDetails: reservationDetails,
            deviceId: deviceId,
            authenticationCode: authenticationCode
        )

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: headers
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let sessionIdentifier = data[.doorKey] as? String else { throw ResponseParserError.keyNotFound(.doorKey) }

            return sessionIdentifier
        }
    }

    // MARK: - Snowdrop

    func suggestions(searchTerm: String) throws -> Resource<[PISuggestion]> {
        func getURL() throws -> URL {
            // this may only work with the old REST baseURL
            let path = "/v1/autocomplete"

            guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

            var urlComps = URLComponents(url: url, resolvingAgainstBaseURL: false)
            urlComps?.queryItems = [
                URLQueryItem(name: "input", value: searchTerm),
                URLQueryItem(name: "hotels.limit", value: "5"),
                URLQueryItem(name: "gplaces[components]", value: "country:uk|country:de")
            ]

            guard let finalUrl = urlComps?.url else { throw WebserviceError.invalidPath(path) }

            return finalUrl
        }

        return resource(
            url: try getURL(),
            parameters: nil,
            method: .get,
            encoding: URLEncoding.default
        ) { data in
                guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }

                let properties = data["properties"] as? [PIDictionary] ?? []
                let places = data["places"] as? [PIDictionary] ?? []
                let managedPlaces = data["managedPlaces"] as? [PIDictionary] ?? []

                var suggestions: [PISuggestion] = []

                suggestions.append(contentsOf: properties.compactMap { PISuggestion(propertyDictionary: $0) })
                suggestions.append(contentsOf: places.compactMap { PISuggestion(placeDictionary: $0) })
                suggestions.append(contentsOf: managedPlaces.compactMap { PISuggestion(placeDictionary: $0) })

                return suggestions
        }
    }
}

extension Microservices {
    static func hotelAvailabilityParameters(bookingDetails: BookingDetails) -> PIDictionary? {
        var params = PIDictionary()

        params["arrival"] = bookingDetails.criteria.arrivalDate.parameterString
        params["departure"] = bookingDetails.criteria.checkOutDate?.parameterString
        params["type"] = bookingDetails.criteria.rooms.map { $0.type.code }.joined(separator: ",")
        params["rooms"] = bookingDetails.criteria.rooms.count
        params["adults"] = bookingDetails.criteria.rooms.map { String($0.adults) }.joined(separator: ",")
        params["children"] = bookingDetails.criteria.rooms.map { String($0.children) }.joined(separator: ",")
        params["cot"] = bookingDetails.criteria.rooms.map { String($0.cotRequired) }.joined(separator: ",")

        if bookingDetails.employeeRatesEnabled {
            params["cellCodes"] = "EMP01"
        } else if let cellCodes = UserSessionManager.sharedInstance.currentUser?.company?.companyCellCodes {
            params["cellCodes"] = cellCodes.compactMap { $0.description }.joined(separator: ",")
        }

        // BB fields
        if let companyId = UserSessionManager.sharedInstance.currentUser?.companyId,
           let employeeId = UserSessionManager.sharedInstance.currentUser?.business?.employeeId {
            params["companyId"] = companyId
            params["employeeId"] = employeeId
        }

        // Additional params compared to legacy parameters
        let supportedLanguage = LanguageManager.supportedLanguage

        if UserSessionManager.sharedInstance.currentUser?.isBusiness == true {
            params[Constants.bookingChannelKey] = Constants.businessBookingChannel
        } else if supportedLanguage == SupportedLanguage.german {
            params[Constants.bookingChannelKey] = Constants.deBookingChannel
        } else {
            params[Constants.bookingChannelKey] = Constants.bookingChannel
        }

        params[Constants.country] = supportedLanguage.countryCode
        params[Constants.language] = supportedLanguage.rawValue
        params["upsellFormat"] = "PH"

        return params
    }

    static func checkInPaymentParameters(parameters: CheckInOnlineParameters) throws -> PIDictionary? {
        var dict = PIDictionary()
        dict["sessionId"] = parameters.sessionId
        dict["confirmationNumber"] = parameters.confirmationNumber
        dict["paymentCard"] = try getPaymentCard(
            card: parameters.paymentCard,
            address: parameters.billingAddress,
            useStoredCard: parameters.useStoredCard,
            cv2: parameters.cvv
        )
        dict["paymentConfirmationEmail"] = parameters.paymentConfirmationEmail

        return dict
    }

	// MARK: - Helpers

    static func getPaymentCard(
        card: PaymentCard?,
        address: Address?,
        useStoredCard: Bool,
        prepaymentRequired: Bool = true,
        cv2: String?
    ) throws -> PIDictionary {
        guard let address = address ?? card?.address else { throw BookingError.missingCardAddress }

        var dictionary = PIDictionary()

        dictionary["useExistingCard"] = useStoredCard

        if let card = card, useStoredCard == false {
            dictionary["cardNumber"] = card.cardNumber
            dictionary["cardType"] = card.cardType.cardCode
            dictionary["expiryDate"] = card.expiryDate?.creditCardDateFormat
            dictionary["cardholderName"] = card.cardholderName
        }

        if let cardType = card?.cardType.cardCode {
            // doing this for ciol payment
            dictionary["cardType"] = cardType
        }

        dictionary["billingAddress"] = getAddressDictionary(address: address)
        dictionary["prepaymentRequired"] =
            prepaymentRequired // This looks like it has to be always true if we want to attempt a payment
        dictionary["cardSecurityCode"] = cv2

        return dictionary
    }

    static func getAddressDictionary(address: Address, shouldCapitalisePostCode: Bool = false) -> PIDictionary {
        var dict = PIDictionary()
        dict["companyName"] = address.companyName
        dict["line1"] = address.line1
        dict["line2"] = address.line2
        dict["line3"] = address.line3
        dict["line4"] = address.line4
        dict["line5"] = address.line5
        dict["countryCode"] = address.country?.isoCode
        dict["type"] = address.type?.rawValue ?? AddressType.home.rawValue

        // Horror and desperation
        if shouldCapitalisePostCode {
            dict["postCode"] = address.postcode
        } else {
            dict["postcode"] = address.postcode
        }

        return dict
    }

    static func startCheckInOnlineParameters(
        withConfirmationNumber confirmationNumber: String,
        surname: String,
        arrivalDate: Date,
        guestHistoryNumber: String?,
        sessionId: String?
    ) -> PIDictionary {
        var dict = PIDictionary()
        dict["confirmationNumber"] = confirmationNumber
        dict["guestSurname"] = surname
        dict["arrivalDate"] = arrivalDate.parameterString

        if let guestHistoryNumber = guestHistoryNumber {
            dict["guestHistoryNumber"] = guestHistoryNumber
        }

        if let sessionId = sessionId {
            dict["sessionId"] = sessionId
        }

        return dict
    }

    static func checkInOnlineAddGuestDetailsParameters(
        withConfirmationNumber confirmationNumber: String,
        businessTrip: Bool,
        booker: User,
        and roomsAndNextDestinations: [RoomAndNextDestinationModel]
    ) -> PIDictionary {
        var dict = PIDictionary()
        dict["confirmationNumber"] = confirmationNumber
        dict["businessTrip"] = businessTrip

        dict["booker"] = getBookerDict(booker: booker)

        dict["guests"] = getGuestsDict(roomsAndNextDestinations: roomsAndNextDestinations)

        return dict
    }

    private static func getBookerDict(booker: User) -> PIDictionary {
        var 📚 = PIDictionary()
        📚["emailAddress"] = booker.emailAddress
        📚["firstName"] = booker.firstName
        📚["lastName"] = booker.lastName
        📚["mobileNumber"] = booker.contactNumber

        if let countryCode = booker.country?.mappedCode {
            📚["nationality"] = countryCode
        }

        let title = try? Title(title: booker.title ?? "").rawValue
        📚["title"] = title ?? booker.title

        if let address = booker.address {
            📚["address"] = getAddressDictionary(address: address)
        }

        return 📚
    }

    private static func getGuestsDict(roomsAndNextDestinations: [RoomAndNextDestinationModel]) -> [PIDictionary] {
        var guests = [PIDictionary]()

        for (index, roomAndNextDestination) in roomsAndNextDestinations.enumerated() {
            guard let leadGuestDetails = roomAndNextDestination.room.leadGuest else { continue }

            var guestDictionary = PIDictionary()

            if let passportNumber = leadGuestDetails.passport?.number {
                guestDictionary["passportNumber"] = passportNumber
                let poi: String = {
                    guard let countryName = leadGuestDetails.country?.name else { return "" }
                    guard countryName.count > Constants.placeOfIssueMaxLength else { return countryName }
                    let idx = countryName.index(countryName.startIndex, offsetBy: Constants.placeOfIssueMaxLength)

                    return String(countryName[..<idx])
                }()
                guestDictionary["placeOfIssue"] = poi
            }

            if let nextDestination = roomAndNextDestination.nextDestination {
                guestDictionary["nextDestination"] = nextDestination
            }

            if let address = leadGuestDetails.address {
                guestDictionary["address"] = getAddressDictionary(address: address)
            }

            guestDictionary["emailAddress"] = leadGuestDetails.emailAddress
            guestDictionary["firstName"] = leadGuestDetails.firstName
            guestDictionary["lastName"] = leadGuestDetails.lastName

            if let guestHistoryNumber = leadGuestDetails.guestHistoryNumber {
                guestDictionary["guestHistoryNumber"] = guestHistoryNumber
            }

            guestDictionary["mobileNumber"] = leadGuestDetails.contactNumber

            if let countryCode = leadGuestDetails.country?.mappedCode {
                guestDictionary["nationality"] = countryCode
            }

            guestDictionary["roomId"] = roomAndNextDestination.room.roomId
            guestDictionary["roomNumber"] = index

            let title = try? Title(title: leadGuestDetails.title ?? "").rawValue
            guestDictionary["title"] = title ?? leadGuestDetails.title

            guests.append(guestDictionary)
        }

        return guests
    }

    static func checkInOnlineUpsellParameters(
        withConfirmationNumber confirmationNumber: String,
        andUpsells upsells: [UpsellItem]
    ) -> PIDictionary {
        var dict = PIDictionary()
        dict["confirmationNumber"] = confirmationNumber

        let upsellsDicts: [PIDictionary] = upsells.compactMap {
            guard let 🥞 = $0.quantity, let 📅 = $0.postingDate, let 🚪 = $0.roomId,
                  let 💰 = $0.individualCost else { return nil }

            let unitCost: [String: Any] = [
                "amount": 💰.amount.decimalValue,
                "currency": 💰.currencyCode
            ]
            var 💸 = PIDictionary()
            💸["quantity"] = 🥞
            💸["postingDate"] = 📅
            💸["roomId"] = 🚪
            💸["code"] = $0.code
            💸["unitCost"] = unitCost

            return 💸
        }

        dict["upsells"] = upsellsDicts

        return dict
    }

    private static func getDashboardParameters(
        reservationId: String?,
        surname: String?,
        arrival: Date?,
        recentSearchesFlag: Bool
    ) -> PIDictionary {
        var parameters = PIDictionary()

        if let reservationId = reservationId, let surname = surname, let arrival = arrival {
            parameters["confirmationNumber"] = reservationId
            parameters["surname"] = surname
            parameters["arrivalDate"] = arrival.parameterString
        }

        parameters["hasRecentSearches"] = recentSearchesFlag ? "true" : "false"

        if let email = UserSessionManager.sharedInstance.currentUser?.emailAddress {
            parameters["customer-id"] = email
        }

        return parameters
    }

    private static func getDoorKeyParameters(
        reservationDetails: ReservationDetails,
        deviceId: String,
        authenticationCode: String?
    ) throws -> PIDictionary {
        var parameters = PIDictionary()

        parameters["deviceId"] = deviceId

        parameters["confirmationNumber"] = reservationDetails.reservationId
        parameters["arrivalDate"] = reservationDetails.arrivalDate.parameterString

        parameters["roomNumber"] = "101"

        var guestDictionary = PIDictionary()
        // TODO: change later
        guestDictionary["firstName"] = reservationDetails.surname
        guestDictionary["lastName"] = reservationDetails.surname
        parameters["guest"] = guestDictionary

        if let authenticationCode = authenticationCode {
            parameters["authenticationCode"] = authenticationCode
        }

        return parameters
    }
}
