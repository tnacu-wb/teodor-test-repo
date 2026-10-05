//
//  CheckInOnlineGuestDetailsInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 18/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

typealias LeadGuestAndRoomIndex = (leadGuest: LeadGuest, roomIndex: Int)

struct LeadGuest {
    var user: User?
    var nextDestination: String?
}

protocol CheckInOnlineGuestDetailsInteractorProtocol {
    var viewModel: CheckInOnlineGuestDetailsViewModel { get }
    var screenTitle: String { get }
    var leadGuestAndRoomIndex: LeadGuestAndRoomIndex? { get }

    func update(leadGuestWith formOutput: CheckInOnlineGuestDetailsViewOutput)
    func update(leadGuest country: Country)
    func update(leadGuest title: String)
}

class CheckInOnlineGuestDetailsInteractor {
    private var leadGuest: LeadGuest?
    private let roomIndex: Int
    private let hasAdditionalGuest: Bool
    private let bookerAddress: Address

    init(with leadGuest: LeadGuest?, roomIndex: Int = 0, hasAdditionalGuest: Bool = false, bookerAddress: Address) {
        self.leadGuest = leadGuest
        self.roomIndex = roomIndex
        self.hasAdditionalGuest = hasAdditionalGuest
        self.bookerAddress = bookerAddress
    }
}

extension CheckInOnlineGuestDetailsInteractor: CheckInOnlineGuestDetailsInteractorProtocol {
    private struct ViewModel: CheckInOnlineGuestDetailsViewModel {
        let titleRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let firstNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let lastNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let emailRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let contactNumberRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let nationalityRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let passportNumberRowModel: CheckInOnlineGuestDetailsViewFormRowModel?
        let nextDestinationRowModel: CheckInOnlineGuestDetailsViewFormRowModel?
        let carRegistrationRowModel: CheckInOnlineGuestDetailsViewFormRowModel

        let useBookerAddress: Bool
        let bookerAddressSummary: String
        let address: Address?

        let shouldShowAdditionalGuestRows: Bool
        let additionalGuestTitleRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let additionalGuestFirstNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel
        let additionalGuestLastNameRowModel: CheckInOnlineGuestDetailsViewFormRowModel

        let shouldShowPassportAndNextDestinationRows: Bool
        let carRegistrationSectionTitle: String
        let carRegistrationSectionDescription: String
    }

    private struct RowViewModel: CheckInOnlineGuestDetailsViewFormRowModel {
        let title: String
        let placeholder: String
        let value: Any?
    }

    var viewModel: CheckInOnlineGuestDetailsViewModel {
        var addressComponents: [String] = [
            bookerAddress.line1 ?? "",
            bookerAddress.line2 ?? "",
            bookerAddress.postcode ?? ""
        ]
        addressComponents.removeAll(where: { $0.isEmpty })
        let useBookerAddress: Bool = {
            guard let address = leadGuest?.user?.address else { return true }
            return address == bookerAddress
        }()

        return ViewModel(
            titleRowModel: RowViewModel(
                title: PILocalizedString("userDetailsTitleLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.title
            ),
            firstNameRowModel: RowViewModel(
                title: PILocalizedString("userDetailsFirstNameLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.firstName
            ),
            lastNameRowModel: RowViewModel(
                title: PILocalizedString("userDetailsLastNameLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.lastName
            ),
            emailRowModel: RowViewModel(
                title: PILocalizedString("userDetailsEmailLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.emailAddress
            ),
            contactNumberRowModel: RowViewModel(
                title: PILocalizedString("userDetailsContactNumberLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.contactNumber
            ),
            nationalityRowModel: RowViewModel(
                title: PILocalizedString("userDetailsNationalityLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.country
            ),
            passportNumberRowModel: RowViewModel(
                title: PILocalizedString("userDetailsPassportLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.passport?.number
            ),
            nextDestinationRowModel: RowViewModel(
                title: PILocalizedString("Next destination", comment: ""),
                placeholder: "",
                value: leadGuest?.nextDestination
            ),
            carRegistrationRowModel: RowViewModel(
                title: PILocalizedString("userDetailsCarRegistrationLabel", comment: ""),
                placeholder: PILocalizedString("userDetailsOptional", comment: "User details form: optional placeholder"),
                value: leadGuest?.user?.carRegistration
            ),
            useBookerAddress: useBookerAddress,
            bookerAddressSummary: addressComponents.joined(separator: "\n"),
            address: leadGuest?.user?.address,
            shouldShowAdditionalGuestRows: hasAdditionalGuest,
            additionalGuestTitleRowModel: RowViewModel(
                title: PILocalizedString("userDetailsTitleLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.additionalGuests?.first?.title
            ),
            additionalGuestFirstNameRowModel: RowViewModel(
                title: PILocalizedString("userDetailsFirstNameLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.additionalGuests?.first?.firstName
            ),
            additionalGuestLastNameRowModel: RowViewModel(
                title: PILocalizedString("userDetailsLastNameLabel", comment: ""),
                placeholder: "",
                value: leadGuest?.user?.additionalGuests?.first?.lastName
            ),
            shouldShowPassportAndNextDestinationRows: leadGuest?.user?.country?.passportRequired ?? false,
            carRegistrationSectionTitle: PILocalizedString("userDetailsCarRegistrationLabel", comment: ""),
            carRegistrationSectionDescription: PILocalizedString("ciolNoOnsiteParkingGaranteeMessage", comment: "")
        )
    }

    var screenTitle: String {
        String(format: PILocalizedString("userDetailsCheckInTitle", comment: ""), (roomIndex + 1))
    }

    var leadGuestAndRoomIndex: LeadGuestAndRoomIndex? {
        guard let leadGuest = leadGuest else { return nil }
        return (leadGuest, roomIndex)
    }

    func update(leadGuestWith formOutput: CheckInOnlineGuestDetailsViewOutput) {
        leadGuest?.user?.title = formOutput.title
        leadGuest?.user?.firstName = formOutput.firstName
        leadGuest?.user?.lastName = formOutput.lastName
        leadGuest?.user?.emailAddress = formOutput.email
        leadGuest?.user?.contactNumber = formOutput.contactNumber
        leadGuest?.nextDestination = formOutput.nextDestination
        leadGuest?.user?.carRegistration = formOutput.carRegistration

        if let country = formOutput.nationality as? Country {
            leadGuest?.user?.country = country
            if country.passportRequired == false {
                leadGuest?.user?.passport = nil
            }
        }

        if hasAdditionalGuest, let title = formOutput.additionalGuestTitle,
           let firstName = formOutput.additionalGuestFirstName, let lastName = formOutput.additionalGuestLastName {
            leadGuest?.user?.additionalGuests = [AdditionalGuest(
                title: title,
                firstName: firstName,
                lastName: lastName,
                nationality: nil,
                email: nil
            )]
        }

        if formOutput.useBookerAddress {
            leadGuest?.user?.address = bookerAddress
        } else {
            if let postcode = formOutput.postcode, let line1 = formOutput.addressLine1,
               let countryCode = formOutput.country?.code {
                leadGuest?.user?.address = try? Address(dictionary: [
                    "line1": line1,
                    "line2": formOutput.addressLine2 ?? "",
                    "line3": formOutput.addressLine3 ?? "",
                    "countryCode": countryCode,
                    "postcode": postcode
                ]
                )
            }
        }

        if let countryOfIssue = leadGuest?.user?.passport?.countryOfIssue ?? leadGuest?.user?.country?.name,
           let passportNumber = formOutput.passportNumber {
            leadGuest?.user?.passport = Passport(number: passportNumber, countryOfIssue: countryOfIssue)
        }
    }

    func update(leadGuest country: Country) {
        leadGuest?.user?.country = country
    }

    func update(leadGuest title: String) {
        leadGuest?.user?.title = title
    }
}
