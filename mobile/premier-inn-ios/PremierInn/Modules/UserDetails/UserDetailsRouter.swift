//
//  UserDetailsRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

typealias FormStep1Output = (booker: User, guests: [User], purpose: TripPurpose)

enum Step1Row: String {
	case bookerIsGuest
	case salutation
	case firstName
	case lastName
	case emailAddress
	case contactNumber
	case guestSummary
	case nationality
	case carRegistration
    case submitButton
    case passport
    case marketing
    case createAccount
    case password
    case acceptablePassword
    case dateOfBirth
    case address1
    case address2
    case address3
    case postcode
    case city
    case country
}

enum GuestDetailsRow: String {
	case addressType
	case purpose
	case salutation
	case firstName
	case lastName
	case contactNumber
	case emailAddress
	case companyName
    case taxInformationBox
}

enum TripPurposeSelections: SelectableType, FormekaValue {
    case leisure
    case business

    var displayName: String { title }

    var title: String {
        switch self {
        case .leisure:
            return PILocalizedString("tripPurposeLeisure")
        case .business:
            return PILocalizedString("tripPurposeBusiness")
        }
    }

    var accessibilityIdentifier: String {
        switch self {
        case .leisure:
            return AccessibilityIdentifiers.UserDetails.tripPurposeLeisure
        case .business:
            return AccessibilityIdentifiers.UserDetails.tripPurposeBusiness
        }
    }
}

protocol UserDetailsRouterProtocol: AnyObject {
	func presentLogin()
	func presentSalutationPicker(for indexPath: IndexPath)
	func continueToNextScreen(output: FormStep1Output)
    func showAddressPickerScreen(with searchTerm: SearchTerm, completion: @escaping (Address?) -> Void)
    func goBack()
    func goBackToMyAccount()
    func goToDeleteAccount()
}

protocol UserDetailsRouterDelegate: AnyObject {
    func goBackToUserDetailsScreen()
    func showError(title: String, message: String, handler: @escaping (UIAlertAction) -> Void)
	func userDetailsDidFinish(with bookingDetails: BookingDetails, output: FormStep1Output, sender: UIViewController)
    func userDetailsWereUpdated()
}

extension UserDetailsRouterDelegate {
    func goBackToUserDetailsScreen() { }
    func showError(title: String, message: String, handler: @escaping (UIAlertAction) -> Void) { }
}

class UserDetailsRouter {
	private weak var viewController: UserDetailsViewController?
	private weak var delegate: UserDetailsRouterDelegate?
    private weak var deleteAccountDelegate: DeleteAccountDelegate?

    static func buildController(
    	bookingDetails: BookingDetails?,
    	loggedUser: User?,
    	scope: UserDetailScope,
    	delegate: UserDetailsRouterDelegate?,
    	deleteAccountDelegate: DeleteAccountDelegate? = nil
    ) -> UIViewController {
		let controller = UserDetailsViewController(nibName: String(describing: FormekaViewController.self), bundle: nil)
		controller.presenter = {
			let presenter = UserDetailsPresenter()
			presenter.view = controller

            let interactor = UserDetailsInteractor(
            	user: loggedUser,
            	bookingDetails: bookingDetails,
            	scope: scope
            )

            let requestsManager = RequestsManager()
            interactor.registerDataManager = requestsManager
            interactor.loginDataManager = requestsManager

			presenter.interactor = interactor

			let router = UserDetailsRouter()
			router.viewController = controller
			router.delegate = delegate
            router.deleteAccountDelegate = deleteAccountDelegate
			presenter.router = router
            presenter.reviewBookDelegate = delegate

			return presenter
		}()

		return controller
	}

    private func showBusinessCardQuestions(questionsAndAnswers: [BusinessCardQuestionAndAnswer], sender: UIViewController) {
        let controller = BusinessCardQuestionsRouter.build(with: questionsAndAnswers, isModal: false)
        controller.delegate = self

        sender.navigationController?.pushViewController(controller, animated: true)
    }

    private func showReviewAndBook(bookingDetails: BookingDetails, sender: UIViewController) {
        let controller = ReviewAndBookRouter.build(with: bookingDetails)

        sender.navigationController?.pushViewController(controller, animated: true)
    }
}

extension UserDetailsRouter: UserDetailsRouterProtocol {
    func goBack() {
        delegate?.userDetailsWereUpdated()

        guard let viewController = self.viewController else { return }

        if let userDetailsVC = viewController.navigationController?.viewControllers
           .first(where: { $0 is UserDetailsViewController }) {
            viewController.navigationController?.popToViewController(userDetailsVC, animated: true)
            return
        }
        viewController.navigationController?.popViewController(animated: true)
    }

    func goBackToMyAccount() {
        guard let viewController = self.viewController else { return }

        delegate?.userDetailsWereUpdated()
        viewController.navigationController?.popViewController(animated: true)
    }

    func goToDeleteAccount() {
        let deleteAccountVC = DeleteAccountRouter.buildController(delegate: deleteAccountDelegate)
        viewController?.navigationController?.pushViewController(deleteAccountVC, animated: true)
    }

    func presentLogin() {
		guard let viewController = viewController else { return }
        let userBusiness = UserDefaults.standard.bool(forKey: .storedBusinessKey)

		LoginRouter().presentLoginInterface(
			from: viewController,
			asBusinessLogin: userBusiness,
			comingFromSplashScreen: false,
			andIsFromBookingFlow: true
		)
	}

    func continueToNextScreen(output: FormStep1Output) {
		guard let viewController = viewController else { return }

        BookingDetails.sharedInstance.updateDefaultCardForBooking()

        if delegate != nil {
            delegate?.userDetailsDidFinish(with: BookingDetails.sharedInstance, output: output, sender: viewController)
        } else {
            userDetailsDidFinish(with: BookingDetails.sharedInstance, output: output, sender: viewController)
        }
	}

	func presentSalutationPicker(for indexPath: IndexPath) {
		guard let viewController = viewController else { return }

		let viewModel = SalutationsListViewModel(salutations: Constants.CMS.salutations)

		let controller = ListViewController(viewModel: viewModel, invertedColours: true)
		controller.textFieldPlaceholder = PILocalizedString("salutationListPlaceholder", comment: "Salutation list placeholder")
		controller.textFieldValue = nil
		controller.shouldDelaySearchRequest = false
		controller.selectedObjectOutput = { [weak self] sender, salutation in
			sender.dismiss(animated: true)

			self?.viewController?.presenter?.setSalutation(salutation as? String, at: indexPath)
		}
		controller.cancelButtonDidTap = { sender in
			sender.dismiss(animated: true)
		}

		viewController.present(controller, animated: true)
	}

    func showAddressPickerScreen(with searchTerm: String, completion: @escaping (Address?) -> Void) {
        let controller = ListViewController(viewModel: PostCodeLookupViewModel(), invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString(
        	"postCodeSearchInputPlaceholder",
        	comment: "Post code search input placeholder"
        )
        controller.textFieldValue = searchTerm
        controller.minimumSearchRequestCharacters = Constants.minimumCharactersForPostCodeLookup
        controller.shouldUppercaseTextFieldInput = true
        controller.shouldShowKeyboardOnLoad = true
        controller.selectedObjectOutput = { sender, address in
            sender.dismiss(animated: true)

            guard let address = address as? Address else {
                completion(nil)
                return
            }

            completion(address)
        }
        controller.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true, completion: nil)
        }

        viewController?.present(controller, animated: true, completion: nil)
    }
}

extension UserDetailsRouter: UserDetailsRouterDelegate {
    func userDetailsWereUpdated() {
        // Here we don't need to react to user's data being saved
    }

    func userDetailsDidFinish(with bookingDetails: BookingDetails, output: FormStep1Output, sender: UIViewController) {
        bookingDetails.booker = output.booker
        bookingDetails.purpose = output.purpose
        bookingDetails.criteria.addGuests(output.guests)

        showReviewAndBook(bookingDetails: bookingDetails, sender: sender)
    }
}

extension UserDetailsRouter: BusinessCardQuestionsDelegate {
    func submitBusinessCardQuestions(questions: [BusinessCardQuestionAndAnswer]) {
        BookingDetails.sharedInstance.businessCardQuestionsAndAnswers = questions

        guard let viewController = viewController else { return }

        showReviewAndBook(bookingDetails: BookingDetails.sharedInstance, sender: viewController)
    }
}
