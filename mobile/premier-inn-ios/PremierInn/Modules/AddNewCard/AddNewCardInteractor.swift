//
//  AddNewCardInteractor.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 03/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

protocol AddNewCardDataProvider {
    func initiateSaveCard(
        initiateSaveCardParameters: InitiateSaveCardParameters,
        completion: @escaping (_ paymentRequiredDetails: CCCPPaymentProviderResponse?, _ error: Error?) -> Void
    )
}

extension RequestsManager: AddNewCardDataProvider {}

enum InitiateSaveCardError: LocalizedError {
    case missingPaymentId
    case missingHTML
    case missingResponse
    case unknown

    var errorDescription: String? {
        switch self {
        case .missingPaymentId, .missingHTML:
            return PILocalizedString("cardDetailsUnableToSaveCardDetails")
        default:
            return String(describing: self)
        }
    }
}

class AddNewCardInteractor {
    private var selectedPaymentType: CCCPPaymentType
    private var cnpRequiredIsSelected: Bool

    var addNewCardDataProvider: AddNewCardDataProvider? = RequestsManager()

    public init() {
        self.selectedPaymentType = .CARD
        self.cnpRequiredIsSelected = false
    }
}

extension AddNewCardInteractor: AddNewCardInteractorProtocol {
    var addNewCardViewModel: AddNewCardViewModel? {
        guard let address = UserSessionManager.sharedInstance.currentUser?.address else { return nil }

        let billingAddressViewModel = BillingAddressViewModel(
            addressRequirements: AddressSectionRequirements(
                address: address,
                storedAddress: address,
                shouldShowAddressSwitch: true,
                useStoredAddressSwitchDescription: address.shortDecription.string,
                addressSwitchInitialState: true,
                shouldShowAddressForm: false,
                shouldShowHeader: true,
                shouldShowFooter: false,
                shouldShowAddressSummary: false
            )
        )

        let paymentMethodsViewModels = paymentMethodsViewModels
        let paymentMethodsViewModel = PaymentMethodsViewModel(
            title: PILocalizedString("addNewCardPaymentSectionTitle"),
            paymentMethods: paymentMethodsViewModels,
            shouldShowCNP: shouldShowCNP,
            cnpEnabled: shouldShowCNP && cnpRequiredIsSelected
        )

        return ViewModel(
            billingAddressViewModel: billingAddressViewModel,
            paymentMethodsViewModel: paymentMethodsViewModel
        )
    }

    func selected(paymentType: String) {
        if let newPaymentType = CCCPPaymentType(rawValue: paymentType) {
            selectedPaymentType = newPaymentType
        }
    }

    func setCnpRequired(toggle: Bool) {
        cnpRequiredIsSelected = toggle
    }

    func initiateAddNewCard(
        values: PIDictionary,
        memorableWord: String?,
        completion: @escaping (ThreeCiPageParams?) -> Void
    ) {
        guard let address = billingAddress(using: values) else {
            completion(nil)
            return
        }

        let initiateSaveCardDetails = InitiateSaveCardDetails(
            cardType: selectedPaymentType,
            cnpRequired: cnpRequiredIsSelected,
            memorableWord: memorableWord
        )
        let initiateSaveCardParameters = InitiateSaveCardParameters(
            billingAddress: address,
            cardDetails: initiateSaveCardDetails
        )

        addNewCardDataProvider?.initiateSaveCard(initiateSaveCardParameters: initiateSaveCardParameters) { response, error in
            guard let response, error == nil else { return completion(nil) }

            do {
                let threeCWebViewParams = try self.threeCWebViewParams(for: response)

                completion(threeCWebViewParams)
            } catch {
                return completion(nil)
            }
        }
    }
}

private extension AddNewCardInteractor {
    private struct ViewModel: AddNewCardViewModel {
        let billingAddressViewModel: AddNewCardBillingAddressViewModel
        let paymentMethodsViewModel: AddNewCardPaymentMethodsViewModel?
    }

    private struct BillingAddressViewModel: AddNewCardBillingAddressViewModel {
        let addressRequirements: AddressSectionRequirements
    }

    private struct PaymentMethodsViewModel: AddNewCardPaymentMethodsViewModel {
        let title: String
        let paymentMethods: [AddNewCardPaymentMethodViewModel]
        let shouldShowCNP: Bool
        let cnpEnabled: Bool
    }

    private struct PaymentMethodViewModel: AddNewCardPaymentMethodViewModel {
        let name: String
        let type: CCCPPaymentType
        let imageUrls: [URL]?
        let isCNPAvailable: Bool
        let selected: Bool
    }

#warning ("hardcoded payment methods / card types")
    private var paymentMethodsViewModels: [PaymentMethodViewModel] {
        var paymentMethods: [PaymentMethodViewModel] = []

        let cardTypeURLs = [
            "/content/dam/global/booking/Mastercard.jpg",
            "/content/dam/global/booking/AX.jpg",
            "/content/dam/global/booking/dinersclub.jpg",
            "/content/dam/global/booking/Visa_Debit.jpg",
            "/content/dam/global/booking/Electron_white_v.jpg",
            "/content/dam/global/booking/maestro.jpg",
            "/content/dam/global/booking/MD.jpg",
            "/content/dam/global/booking/VC.jpg"
        ]

        paymentMethods.append(PaymentMethodViewModel(
            name: PILocalizedString("addNewCardPaymentTypeCard"),
            type: .CARD,
            imageUrls: cardTypeURLs.compactMap({ URL(string: Constants.creditCardImagesBaseUrl + $0) }),
            isCNPAvailable: false,
            selected: {
                selectedPaymentType == .CARD
            }()
        )
        )

        let pibaTypeURLs = ["/content/dam/global/booking/Business_Account.jpg"]

        paymentMethods.append(PaymentMethodViewModel(
            name: PILocalizedString("addNewCardPaymentTypePiba"),
            type: .PIBA,
            imageUrls: pibaTypeURLs.compactMap({ URL(string: Constants.creditCardImagesBaseUrl + $0) }),
            isCNPAvailable: true,
            selected: {
                selectedPaymentType == .PIBA
            }()
        )
        )

        return paymentMethods
    }

    private var shouldShowCNP: Bool {
        guard let selectedPaymentMethod = paymentMethodsViewModels.first(where: { $0.selected == true })
            else { return false }

        return selectedPaymentMethod.isCNPAvailable == true
    }

    private func billingAddress(using values: PIDictionary) -> Address? {
        if let storedbillingAddressOption = values[Step2Row.billingAddressSwitch.rawValue] as? Bool,
           storedbillingAddressOption == false, let address = AddressSectionDataProvider.address(for: values) {
            return address
        }

        return UserSessionManager.sharedInstance.currentUser?.address
    }
}

// MARK: iFrame methods

extension AddNewCardInteractor {
    private func threeCWebViewParams(for response: CCCPPaymentProviderResponse) throws -> ThreeCiPageParams {
        guard let htmlString = response.htmlString else { throw InitiateSaveCardError.missingHTML }

        return ThreeCiPageParams(
            html: htmlString,
            trackingParams: nil,
            allowedEvents: nil
        )
    }
}

private extension Address {
    var shortDecription: NSAttributedString {
        let shortAddressParams: (prefix: String, postcode: String) = {
            (PILocalizedString("cardDetailsBillingAddressSwitchLabel"), postcode ?? "")
        }()

        let shortAddress = "\(shortAddressParams.prefix) (\(shortAddressParams.postcode))"
        let mutableShortAddress = NSMutableAttributedString(string: shortAddress)

        if let range = shortAddress.ranges(of: shortAddressParams.postcode).first {
            mutableShortAddress.addAttribute(.font, value: UIFont.Body_Semibold(), range: range)
        }
        return NSAttributedString(attributedString: mutableShortAddress)
    }
}
