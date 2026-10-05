//
//  RequestsManager+User.swift
//  PremierInn
//
//  Created by Marcello Mascia on 22/05/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

public typealias InitiateSaveCardDetails = (cardType: CCCPPaymentType, cnpRequired: Bool, memorableWord: String?)
public typealias InitiateSaveCardParameters = (billingAddress: Address, cardDetails: InitiateSaveCardDetails)

public extension RequestsManager {
    func login(
        withUsername username: String,
        password: String,
        isBusiness: Bool = false,
        completion: @escaping (Result<Bool>) -> Void
    ) {
        Router.current.login(username: username, password: password, isBusiness: isBusiness, completion: completion)
    }

	func logout() {
		do {
			try Router.current.logout()
		} catch {
			print(error)
		}
	}

    func getUser(userId: String, isBusiness: Bool = false, completion: @escaping (Result<User>) -> Void) {
        let completionHandler: (Result<Resource<User>>) -> Void = { result in
            switch result {
            case .success(let resource):
                self.load(resource: resource) { user, error in
                    if let user = user {
                        completion(.success(result: user))
                    } else if let error = error {
                        completion(.failure(error: error))
                    } else {
                        completion(.failure(error: RequestsManagerError.missingUser))
                    }
                }

            case .failure(let error):
                completion(.failure(error: error))
            }
        }

        Router.current.getUserCredentials(isBusiness: isBusiness) { result in
            switch result {
            case .success:

                Router.current.getUser(userId: userId, isBusiness: isBusiness, completion: completionHandler)

            case .failure(let error):
                completion(.failure(error: error))
            }
        }
    }

	func getCompany(
	    companyId: String,
	    sensorData: String,
	    completion: @escaping (_ company: Company?, _ error: Error?) -> Void
	) {
        do {
            let resource = try Router.current.getCompany(companyId: companyId, sensorData: sensorData)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func getStays(completion: @escaping (_ results: [Stay]?, _ error: Error?) -> Void) {
		do {
			let resource = try Router.current.getStays()

			load(resource: resource, completion: completion)
		} catch {
			completion(nil, error)
		}
	}

	func register(
	    withRegisterParameters registerParameters: RegisterParameters,
	    sensorData: String,
	    completion: @escaping (_ success: Bool?, _ error: Error?) -> Void
	) {
        do {
            let resource = try Router.current.register(registerParameters: registerParameters, sensorData: sensorData)

            load(resource: resource, completion: completion)
        } catch {
            completion(false, error)
        }
    }

    func forgotPassword(
        withEmailAddress emailAddress: String,
        isBusiness: Bool = false,
        completion: @escaping (_ success: Bool, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.forgotPassword(emailAddress: emailAddress, isBusiness: isBusiness)

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

	func savePaymentCard(
	    for user: User,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	) {
        do {
            let resource = try Router.current.savePaymentCard(for: user, sensorData: sensorData)

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

    func initiateSaveCard(
        initiateSaveCardParameters: InitiateSaveCardParameters,
        completion: @escaping (_ paymentRequiredDetails: CCCPPaymentProviderResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.initiateSaveCard(initiateSaveCardParameters: initiateSaveCardParameters)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

	func changePassword(
	    user: User,
	    existingPassword: String,
	    newPassword: String,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	) {
        do {
			let resource = try Router.current.changePassword(
			    user: user,
			    existingPassword: existingPassword,
			    newPassword: newPassword,
			    sensorData: sensorData
			)
            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

	func deletePaymentCard(
	    for user: User,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	) {
		do {
			let resource = try Router.current.deletePaymentCard(for: user, sensorData: sensorData)

			load(resource: resource) { result, error in
				completion(result ?? false, error)
			}
		} catch {
			completion(false, error)
		}
	}

	func updateUserDetails(
	    for user: User,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	) {
		do {
			let resource = try Router.current.updateUserDetails(user: user, sensorData: sensorData)

			load(resource: resource) { result, error in
				completion(result ?? false, error)
			}
		} catch {
			completion(false, error)
		}
	}

    func deleteUser(completion: @escaping (_ success: Bool, _ error: Error?) -> Void) {
        do {
            let resource = try Router.current.deleteUser()

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

	func updateFoodPreference(
	    for user: User,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	) {
        do {
			let resource = try Router.current.updateFoodPreference(user: user, sensorData: sensorData)

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

	func updateRoomPreference(
	    for user: User,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	) {
        do {
			let resource = try Router.current.updateRoomPreference(user: user, sensorData: sensorData)

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

	func updateUserAdditionalGuests(
	    for user: User,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	) {
        do {
            let resource = try Router.current.updateUserAdditionalGuests(user: user, sensorData: sensorData)

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

    func anonymousNewsletterPreferences(
        for email: String,
        countryOfResidence: String,
        completion: @escaping (_ success: AnonymousNewsletterPreferences?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.anonymousNewsletterPreferences(
                email: email,
                countryOfResidence: countryOfResidence
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
