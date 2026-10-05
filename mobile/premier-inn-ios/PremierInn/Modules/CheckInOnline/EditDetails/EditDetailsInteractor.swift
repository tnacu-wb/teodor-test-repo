//
//  EditDetailsInteractor.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

final class EditDetailsInteractor {
    var salutationItems: [String]
    var editDetailsViewModel: EditDetailsModel
    var analyticsParams: PIDictionary

    init(
        salutationItems: [String] = Constants.CMS.salutations,
        editDetailsInputParams: EditDetailsInputParams
    ) {
        self.salutationItems = salutationItems
        self.editDetailsViewModel = editDetailsInputParams.editDetailsViewModel
        self.analyticsParams = editDetailsInputParams.analyticsParams
    }

    func updateModel(with editDetailsModel: EditDetailsModel) {
        if let title = editDetailsModel.title {
            editDetailsViewModel.title = title
        }
        if let firstName = editDetailsModel.firstName {
            editDetailsViewModel.firstName = firstName
        }
        if let lastName = editDetailsModel.lastName {
            editDetailsViewModel.lastName = lastName
        }
        if let passportNumber = editDetailsModel.passportNumber {
            editDetailsViewModel.passportNumber = passportNumber
        }
        if let dateOfBirth = editDetailsModel.dateOfBirth {
            editDetailsViewModel.dateOfBirth = dateOfBirth
        }
        if let address = editDetailsModel.address {
            editDetailsViewModel.address = address
        }
        if let emailAddress = editDetailsModel.emailAddress {
            editDetailsViewModel.emailAddress = emailAddress
        }
        if let phoneNumber = editDetailsModel.phoneNumber {
            editDetailsViewModel.phoneNumber = phoneNumber
        }
        if let country = editDetailsModel.country {
            editDetailsViewModel.country = country
        }
    }

    func updateAddress(_ address: Any?) {
        guard let address = address as? Address else { return }
        let newAddress = StoredAddressModel(with: address)
        self.editDetailsViewModel.address = newAddress
    }
}

extension EditDetailsInteractor: EditDetailsInteractorProtocol {
    var customAnalyticsParameters: PIDictionary? { analyticsParams }
}
