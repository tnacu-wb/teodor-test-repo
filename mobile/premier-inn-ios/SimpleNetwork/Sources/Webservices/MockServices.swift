//
//  MockServices.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 22/10/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation


class MockServices: Webservice {
    private var userSessionToken: String?

    static func getJsonDictionary(fileName: String) -> PIDictionary? {
        guard let fileURL = Bundle.simpleNetworkResources.url(forResource: fileName, withExtension: "json")
            else { print("Missing file: \(fileName).json"); return nil }
        guard let data = try? Data(contentsOf: fileURL)
            else { print("Unabled to get content of file: \(fileName).json"); return nil }

        do {
            return try JSONSerialization.jsonObject(with: data, options: .allowFragments) as? PIDictionary
        } catch {
            print(error)
            return nil
        }
    }
}

extension MockServices: WebserviceProtocol {
    // addressLookup

    // MARK: - User

    func login(username: String, password: String, isBusiness: Bool = false, completion: @escaping (Result<Bool>) -> Void) {
        userSessionToken = "AFAKEUSERSESSIONTOKEN"
        completion(.success(result: true))
    }

    func getUser(userId: String, completion: @escaping (Result<Resource<User>>) -> Void) {
        let path = "200"

        guard let url = baseURL?.appendingPathComponent(path) else {
            completion(.failure(error: WebserviceError.invalidPath(path)))
            return
        }

        guard let token = userSessionToken else {
            completion(.failure(error: ReservationError.missingToken))
            return
        }

        guard token.isEmpty == false else {
            completion(.failure(error: ReservationError.missingToken))
            return
        }

        let resource = Resource<User>(url: url, parameters: nil, method: .get, encoding: PIURLEncoding.default) { _ in
            guard let dict = MockServices.getJsonDictionary(fileName: "userDetails") else { return nil }

            do {
                return try User(dictionary: dict, sessionId: dict["sessionId"] as? String)
            } catch {
                throw RequestsManagerError.unexpectedResponseError
            }
        }

        completion(.success(result: resource))
    }

    func logout() throws {
        // Nothing to do on Microservices
    }

    // savePaymentCard

    // updateUserAdditionalGuests

    // updateFoodPreference

    // updateRoomPreference

    // updateUserDetails

    // deletePaymentCard

    // register

    // forgotPassword

    // changePassword

    // MARK: - Booking and Payment

    // releaseBooking

    // MARK: - Other stuff

    func suggestions(searchTerm: String) throws -> Resource<[PISuggestion]> {
        let path = "200"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        return Resource(url: url, parameters: nil, method: .get, encoding: PIURLEncoding.default) { _ in
            guard let dict = MockServices.getJsonDictionary(fileName: "suggestions") else { return nil }
            guard let suggestions = dict["suggestions"] as? [PIDictionary]
                else { throw ResponseParserError.keyNotFound("suggestions") }

            return suggestions.map { PISuggestion(dictionary: $0) }
        }
    }

    // MARK: - Door Key

    func getDoorKey(
        reservationDetails: ReservationDetails,
        deviceId: String,
        authenticationCode: String?
    ) throws -> Resource<String> {
        let path = "200"

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        return Resource(url: url, parameters: nil, method: .get, encoding: PIURLEncoding.default) { _ in
            ""
        }
    }
}
