//
//  PreStayView+Formeka.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 09.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension PreStayViewController {
    func tableViewModel(with preStayViewModel: PreStayViewModel) -> FormekaViewModel {
        var sections = [FormekaModelSection]()

        sections.append(summarySection(with: preStayViewModel))
        sections.append(leadBookerSection(with: preStayViewModel))
        sections.append(emailSection(with: preStayViewModel))
        sections.append(phoneSection(with: preStayViewModel))
        sections.append(addressSection(with: preStayViewModel))
        if preStayViewModel.hotelBrand != .premierInnGermany {
            sections.append(roomGuestsDetails(with: preStayViewModel))
        }

        if let hotelPreferences = preStayViewModel.hotelPreferences, hotelPreferences.isNotEmpty {
            sections.append(specialOccasionSection(with: preStayViewModel))
        }
        return FormekaViewModel(sections: sections)
    }
}

private extension PreStayViewController {
    func summarySection(with preStayViewModel: PreStayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingSummaryCIOLCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.configure(with: preStayViewModel.bookingSummaryViewModel)
            cell.isUserInteractionEnabled = false
            ContentsquareConfig.mask(view: cell)
            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func leadBookerSection(with preStayViewModel: PreStayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: PreStayGuestDetailsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.shouldShowErrorMessage = false
            cell.didTapCell = nil
            cell.titleLabel.text = PILocalizedString("leadBooker")
            cell.valueLabel.text = preStayViewModel.bookerViewModel.formattedBookerInfo
            ContentsquareConfig.mask(view: cell)
            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func emailSection(with preStayViewModel: PreStayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: PreStayGuestDetailsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.shouldShowErrorMessage = false
            cell.errorMessageLabel.text = nil
            cell.valueLabel.text = nil
            cell.didTapCell = nil

            if preStayViewModel.isThirdParty {
                cell.didTapCell = { [weak self] in
                    self?.eventHandler?.showEditDetails(flow: .emailAddress)
                }

                if let email = preStayViewModel.email, !email.isEmpty {
                    cell.valueLabel.text = email
                } else {
                    if preStayViewModel.shouldSurfaceErrorMessages {
                        cell.valueLabel.text = nil
                        cell.errorMessageLabel.text = PILocalizedString("missingEmailErrorMessageThirdParty")
                        cell.shouldShowErrorMessage = true
                    }
                }
            } else {
                cell.valueLabel.text = preStayViewModel.email
            }


            cell.titleLabel.text = PILocalizedString("resetPasswordEmailLabel")
            ContentsquareConfig.mask(view: cell)
            return cell
        }))
        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func phoneSection(with preStayViewModel: PreStayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: PreStayGuestDetailsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.shouldShowErrorMessage = false
            cell.errorMessageLabel.text = nil
            cell.valueLabel.text = nil
            cell.didTapCell = nil

            if preStayViewModel.isThirdParty {
                cell.didTapCell = { [weak self] in
                    self?.eventHandler?.showEditDetails(flow: .phoneNumber)
                }

                if let contactNumber = preStayViewModel.contactNumber, !contactNumber.isEmpty {
                    cell.valueLabel.text = contactNumber
                } else {
                    if preStayViewModel.shouldSurfaceErrorMessages {
                        cell.valueLabel.text = nil
                        cell.errorMessageLabel.text = PILocalizedString("missingPhoneNumberErrorMessageThirdParty")
                        cell.shouldShowErrorMessage = true
                    }
                }
            } else {
                cell.valueLabel.text = preStayViewModel.contactNumber
            }

            cell.titleLabel.text = PILocalizedString("phone")
            ContentsquareConfig.mask(view: cell)
            return cell
        }))
        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func addressSection(with preStayViewModel: PreStayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { [weak self] indexPath, _, table in
            guard let cell: PreStayGuestDetailsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.shouldShowErrorMessage = false
            cell.errorMessageLabel.text = nil
            cell.valueLabel.text = nil
            cell.didTapCell = nil

            if preStayViewModel.isThirdParty {
                cell.didTapCell = { [weak self] in
                    self?.eventHandler?.showEditDetails(flow: .address)
                }

                if !preStayViewModel.formattedAddress.isEmpty {
                    cell.valueLabel.text = preStayViewModel.formattedAddress
                }

                self?.setErrorMessageLabelIfNeeded(
                    viewModel: preStayViewModel,
                    cell: cell
                )
            } else {
                cell.valueLabel.text = preStayViewModel.formattedAddress
            }

            cell.titleLabel.text = PILocalizedString("userDetailsAddressSectionTitle")
            ContentsquareConfig.mask(view: cell)
            return cell
        }))
        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func setErrorMessageLabelIfNeeded(
        viewModel: PreStayViewModel,
        cell: PreStayGuestDetailsCell
    ) {
        guard viewModel.shouldSurfaceErrorMessages else {
            return
        }

        let message: String?

        if viewModel.isMissingRequiredAddress {
            message = PILocalizedString("missingAddressErrorMessageThirdParty")
        } else if !viewModel.isAddressPostcodeValid {
            message = PILocalizedString("invalidAddressPostcodeErrorMessageThirdParty")
        } else {
            message = nil
        }

        if let message {
            cell.errorMessageLabel.text = message
            cell.shouldShowErrorMessage = true
        }
    }

    func specialOccasionSection(with preStayViewModel: PreStayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { [weak self] indexPath, _, table in
            guard let cell: SpecialOccasionCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.titleLabel.text = PILocalizedString("stayingForASpecialOccasion")
            cell.showOccasionAlert = preStayViewModel.showOccasionError
            cell.selectButton.setTitle(PILocalizedString("selectOccasionPreStay"), for: .normal)
            if let selectedPreference = preStayViewModel.selectedPreference {
                if let name = selectedPreference.name {
                    cell.selectButton.setTitle(name, for: .normal)
                }
            }

            cell.didToggleSpecialOccasion = { [weak self] isOn in
                self?.tableView.beginUpdates()
                self?.tableView.endUpdates()
                self?.eventHandler?.isSpecialOccasionOn(isOn)
            }

            cell.selectButton.addTarget(self, action: #selector(self?.selectOccasionDidTap), for: .touchUpInside)
            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func roomGuestsDetails(with preStayViewModel: PreStayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        let rooms = preStayViewModel.roomsViewModel

        for (index, room) in rooms.enumerated() {
            rows.append(FormekaModelRow(cellSetup: { [weak self] indexPath, _, table in
                guard let cell: PreStayRoomDetailsCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.titleLabel.text = rooms.count == 1 ? PILocalizedString("roomGuests") : String.localizedStringWithFormat(
                    PILocalizedString("Room %d guests"),
                    index + 1
                )

                cell.configure(viewModel: room)

                self?.handleErrorMessageToShow(cell: cell, room: room, viewModel: preStayViewModel)

                cell.leadGuestCallBack = { [weak self] in
                    self?.eventHandler?.showEditDetails(flow: .leadGuestTitleNameInfo(indexPath: indexPath))
                }

                cell.secondGuestCallBack = { [weak self] in
                    self?.eventHandler?.showEditDetails(flow: .secondGuestTitleNameInfo(indexPath: indexPath))
                }

                ContentsquareConfig.mask(view: cell)
                return cell
            }))
        }
        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func handleErrorMessageToShow(
        cell: PreStayRoomDetailsCell,
        room: RoomGuestsViewModelProtocol,
        viewModel: PreStayViewModel
    ) {
        let leadState = room.leadGuest.warningMessageState
        let secondState = room.accompanyingGuest?.warningMessageState
        let shouldShow = viewModel.shouldSurfaceErrorMessages

        cell.showLeadGuestErrorMessage = shouldShow && leadState != .none
        cell.showSecondGuestErrorMessage = shouldShow && secondState != GuestWarningMessageState.none

        cell.leadGuestErrorMessageLabel.text = errorMessage(
            for: leadState,
            isThirdParty: viewModel.isThirdParty,
            isLead: true
        )

        cell.secondGuestErrorMessageLabel.text = secondState
            .flatMap {
                errorMessage(
                    for: $0,
                    isThirdParty: viewModel.isThirdParty,
                    isLead: false
                )
            }
    }

    private func errorMessage(
        for state: GuestWarningMessageState,
        isThirdParty: Bool,
        isLead: Bool
    ) -> String? {
        switch state {
        case .nationalityRequiresConfirmation:
            return PILocalizedString("roomGuestNationalityConfirmationRequired")

        case .nationalityAndPassportNumberRequired:
            if isThirdParty {
                return PILocalizedString("roomGuestsErrorMessageThirdParty")
            } else {
                return isLead ?
                PILocalizedString("leadGuestMissingIDErrorMessage") :
                PILocalizedString("secondGuestMissingErrorMessage")
            }
        case .none:
            return nil
        }
    }

    @objc func selectOccasionDidTap() {
        eventHandler?.handleSelectOccasion()
    }
}
