//
//  User+Extensions.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 21/08/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import AuthenticationServices

extension User {
    private static let server = "www.premierinn.com"
    private static let groupContainerName = "group.com.whitbread.premierInn"

    // account format is "username£businessFlag"
    private static func queryDict(account: String?, data: Data?) -> [String: Any] {
        var dict: [String: Any] = [:]
        dict[kSecClass as String] = kSecClassInternetPassword
        dict[kSecAttrAccessGroup as String] = groupContainerName
        dict[kSecAttrAccessible as String] = kSecAttrAccessibleWhenUnlocked
        dict[kSecAttrSynchronizable as String] = true
        dict[kSecAttrServer as String] = server

        if let account = account {
            dict[kSecAttrAccount as String] = account
        }

        if let data = data {
            dict[kSecValueData as String] = data
        }

        return dict
    }

    public static func saveCredentials(_ credentials: AuthCredentials) {
        guard let password = credentials.password.data(using: .utf8) else { return }
        let business = (credentials.business == true ? "true" : "false")

        let account = credentials.username
        var query = queryDict(account: account, data: password)

        var item: CFTypeRef?
        var status = SecItemCopyMatching(query as CFDictionary, &item)
        // look for deprecated credentials and migrate them to the new format - save is complete in that case
        if status != errSecItemNotFound && migrateCredentialsTo(credentials) == errSecSuccess { return }

        // otherwise continue to save a new entry
        query = queryDict(account: String.init(format: "%@£%@", account, business), data: password)
        status = SecItemAdd(query as CFDictionary, nil)

        switch status {
        case errSecSuccess:
            break
        case errSecDuplicateItem:
            updateCredentials(credentials)
        default:
            print("Unable to save credentials: \(status)")
        }
    }

    private static func migrateCredentialsTo(_ credentials: AuthCredentials) -> OSStatus? {
        guard let password = credentials.password.data(using: .utf8) else { return nil }
        let business = (credentials.business == true ? "true" : "false")

        let account = credentials.username
        let query = queryDict(account: account, data: password)

        let attributes: [String: Any] = [
            kSecAttrAccount as String: String.init(format: "%@£%@", account, business),
            kSecValueData as String: password
        ]

        let status = SecItemUpdate(query as CFDictionary, attributes as CFDictionary)

        switch status {
        case errSecItemNotFound:
            print("Unable to update credentials: not found")
        case errSecSuccess:
            break
        case errSecDuplicateItem:
            print("Unable to update credentials: duplicate")
        default:
            print("Unable to update credentials: \(status)")
        }

        return status
    }

    private static func updateCredentials(_ credentials: AuthCredentials) {
        guard let password = credentials.password.data(using: .utf8) else { return }
        let business = (credentials.business == true ? "true" : "false")

        let account = credentials.username
        let query = queryDict(account: String.init(format: "%@£%@", account, business), data: password)

        let attributes: [String: Any] = [
            kSecAttrAccount as String: String.init(format: "%@£%@", account, business),
            kSecValueData as String: password
        ]

        let status = SecItemUpdate(query as CFDictionary, attributes as CFDictionary)

        switch status {
        case errSecItemNotFound:
            print("Unable to update credentials: not found")
        case errSecSuccess:
            break
        case errSecDuplicateItem:
            print("Unable to update credentials: duplicate")
        default:
            print("Unable to update credentials: \(status)")
        }
    }

    public static func deprecatedStoredCredentials(for account: String?) -> AuthCredentials? {
        guard let account = account else { return nil }

        var query = queryDict(account: account, data: nil)
        query[kSecMatchLimit as String] = kSecMatchLimitOne
        query[kSecReturnAttributes as String] = true
        query[kSecReturnData as String] = true

        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)
        guard status == errSecSuccess else { return nil }

        guard let existingItem = item as? [String: Any],
              let retrievedAccount = existingItem[kSecAttrAccount as String] as? String,
              let passwordData = existingItem[kSecValueData as String] as? Data,
              let password = String(data: passwordData, encoding: .utf8)
        else {
                print("Unexpected password data")
                return nil
        }

        return (username: retrievedAccount, password: password, business: false)
    }

    public static func storedCredentials(for account: String?, business: Bool) -> AuthCredentials? {
        guard let account = account else { return nil }
        let businessString = (business == true ? "true" : "false")

        var query = queryDict(account: String.init(format: "%@£%@", account, businessString), data: nil)
        query[kSecMatchLimit as String] = kSecMatchLimitOne
        query[kSecReturnAttributes as String] = true
        query[kSecReturnData as String] = true

        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)

        switch status {
        case errSecItemNotFound:
            print("Stored credentials not found for \(account)")

            // try for old credentials
            return deprecatedStoredCredentials(for: account)
        case errSecSuccess:
            break
        default:
            print("Unable to read credentials for \(account): \(status)")
            return nil
        }

        guard let existingItem = item as? [String: Any],
              let passwordData = existingItem[kSecValueData as String] as? Data,
              let password = String(data: passwordData, encoding: .utf8),
              let accountTokens = (existingItem[kSecAttrAccount as String] as? String)?.split(separator: "£"),
              let retrievedAccountSubstring = accountTokens.first,
              let retrievedBusiness = accountTokens.last == "true" ? true : false
        else {
                print("Unexpected password data")
                return nil
        }

        let retrievedAccount = String(retrievedAccountSubstring)

        return (username: retrievedAccount, password: password, business: retrievedBusiness)
    }

    public static func removedStoredCredentials() {
        let query = queryDict(account: nil, data: nil)
        let status = SecItemDelete(query as CFDictionary)

        if status != errSecSuccess {
            print("Unable to delete credentials: \(status)")
        }
    }
}

// TODO: For theese to work correctly we need site association loop (app-server-app) to be configured properly
// At the moment we still don't have the associated file on premierinn.com...
public extension User {
    static func checkSafariCredentials(delegate: ASAuthorizationControllerDelegate) {
        let passwordProvider = ASAuthorizationPasswordProvider()
        let request = passwordProvider.createRequest()
        let authorizationController = ASAuthorizationController(authorizationRequests: [request])
        authorizationController.delegate = delegate
        authorizationController.performRequests()
    }

    static func saveSafariCredentials(_ credentials: AuthCredentials) {
        SecAddSharedWebCredential(
            server as CFString,
            credentials.username as CFString,
            credentials.password as CFString?
        ) { (error) in
            if let error = error {
                print(error)
            }
        }
    }
}

extension User: NSCopying {
    public func copy(with zone: NSZone? = nil) -> Any {
        guard let user = try? User(title: title, firstName: firstName, lastName: lastName) else { return "" }

        user.additionalGuests = additionalGuests
        user.address = address
        user.bookingPreference = bookingPreference
        user.carRegistration = carRegistration
        user.contactNumber = contactNumber
        user.country = country
        user.emailAddress = emailAddress
        user.paymentPreference = paymentPreference
        user.passport = passport
        user.guestHistoryNumber = guestHistoryNumber
        user.isBusiness = isBusiness
        user.marketingPreferences = marketingPreferences
        user.guestHistoryCreation = guestHistoryCreation
        user.totalStays = totalStays
        user.isAccompanyingGuest = isAccompanyingGuest
        user.dob = dob

        return user
    }
}
