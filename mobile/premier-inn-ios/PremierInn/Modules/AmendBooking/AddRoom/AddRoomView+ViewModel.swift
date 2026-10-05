//
//  AddRoomView+ViewModel.swift
//  PremierInn
//
//  Created by Nick Jones on 20/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

extension AddRoomView {
    enum LeadGuestDetailRows: String {
        case title
        case firstName
        case lastName
    }

    enum AmendRoomRows: String {
        case cancel
    }

    func loadViewModel() {
        viewModel = FormekaViewModel(sections: viewModelSections())
        viewModel?.delegate = self

        table?.delegate = viewModel
        table?.dataSource = viewModel
        table?.reloadData()
    }

    private func viewModelSections() -> [FormekaModelSection] {
        let sections: [FormekaModelSection] = {
            var sections: [FormekaModelSection] = []

            if presenter?.shouldShowRoomPickerLayout ?? true {
                let room = presenter?.room ?? Room()
                sections.append(roomSection(with: room))
            } else {
                sections = [roomDetailsSection(), leadGuestDetailsSection()]

                if presenter?.cancellable ?? false {
                    sections.append(cancelSection())
                }
            }

            return sections
        }()

        return sections
    }

    private func roomDetailsSection() -> FormekaModelSection {
        presenter?.userHasCheckedAvailability()

        var rows: [FormekaModelRow] = []

        let roomNumberText: String = {
            guard let roomNumber = presenter?.roomNumber else { return "" }

            return "\(roomNumber)"
        }()

        if let substitutions = presenter?.substitutions {
            rows.append(subsittutionsAlertRow(withSubstitutions: substitutions))
        }

        rows.append(roomDescriptionDetailRow(
            withRoomDescription: presenter?.guestsAndRoomTypeDescription ?? "",
            roomNumber: roomNumberText,
            andPriceText: presenter?.roomPriceDescription ?? "£9999.00",
            isAmendableRoom: presenter?.roomsAreAmendable ?? true
        ))

        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: nil
        )
    }

    private func subsittutionsAlertRow(withSubstitutions substitutions: [RoomSubstitution]) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { [weak self] indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.attributedText = self?.substitutionText(forSubstitutions: substitutions)

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.paragraphSpacing = 12

            cell.icon.tintColor = .sea

            cell.content.boldenText(exceptTextInRange: nil, font: .Body(), boldFont: .Body_Semibold())
            cell.hiddenSeparatorLocations = [.top, .bottom]

            cell.accessibilityIdentifier = substitutions.count > 1 ? AccessibilityIdentifiers.HotelDetails
                .roomSubstitutionHeader : AccessibilityIdentifiers.HotelDetails.multipleRoomSubstitutionHeader

            return cell
        })
    }

    private func substitutionText(forSubstitutions substitutions: [RoomSubstitution]) -> NSAttributedString {
        let boldAttributes: [NSAttributedString.Key: Any] = [NSAttributedString.Key.font: UIFont.Body_Semibold()]

        let subText = NSMutableAttributedString(string: PILocalizedString(
            "hotelDetailsSubstitutionTitle",
            comment: "Hotel details: substitution title"
        ))
        subText.style(
            text: PILocalizedString(
                "hotelDetailsSubstitutionTitleBold",
                comment: "Hotel details: substitution title bold text"
            ),
            withAttributes: boldAttributes
        )

        switch substitutions.count {
        case 1:
            guard let original = substitutions.first?.desired?.localizedName.capitalized,
                  let replacement = substitutions.first?.substitutedRoomsConcatenated?.capitalized else { break }

            let message = String(
                format: PILocalizedString(
                    "hotelDetailsSubstitutionMessage",
                    comment: "Hotel details: substitution message format (don't remove placeholders)"
                ),
                original,
                replacement
            )
            let attributedMessage = NSMutableAttributedString(string: message)

            subText.append(attributedMessage)

            subText.style(text: original, withAttributes: boldAttributes)
            subText.style(text: replacement, withAttributes: boldAttributes)

            return NSAttributedString(attributedString: subText)

        default:
            for room in substitutions {
                guard let roomName = room.roomName?.capitalized,
                      let desiredRoomType = room.desired?.localizedName.capitalized,
                      let replacementRoomType = room.substitutedRoomsConcatenated?.capitalized else { continue }

                let message = String(
                    format: PILocalizedString(
                        "hotelDetailsSubstitutionMessageMultipleRooms",
                        comment: "Hotel details: substitution message format for multiple rooms (don't remove placeholders)"
                    ),
                    roomName,
                    desiredRoomType,
                    replacementRoomType
                )
                subText.append(NSAttributedString(string: message))
                subText.style(text: roomName, withAttributes: boldAttributes)
                subText.style(text: desiredRoomType, withAttributes: boldAttributes)
                subText.style(text: replacementRoomType, withAttributes: boldAttributes)
            }
        }

        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 2

        subText.style(text: subText.string, withAttributes: [NSAttributedString.Key.paragraphStyle: paragraphStyle])

        return NSAttributedString(attributedString: subText)
    }

    private func roomDescriptionDetailRow(
        withRoomDescription roomDescription: String,
        roomNumber: String,
        andPriceText priceText: String,
        isAmendableRoom: Bool
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { [weak self] indexPath, _, table in
            guard let cell: AddRoomDetailsRow = table.dequeueCell(for: indexPath) else { return nil }

            cell.roomNumber.text = PILocalizedString("Room", comment: "") + " \(roomNumber)"
            cell.roomNumber.textColor = .BasePurple

            cell.changeLabel.text = PILocalizedString(
                "amendBookingAddRoomChangeButtonTitle",
                comment: "Amend Booking Add Room Change Room Button Title"
            )
            cell.changeLabel.textColor = .BasePurple
            cell.changeLabel.isHidden = !isAmendableRoom

            cell.adultsChildrenAndRoomDescription.text = roomDescription
            cell.adultsChildrenAndRoomDescription.textColor = .TintD1

            cell.price.text = priceText
            cell.price.textColor = .TintD1

            cell.wholeCellButton.isAccessibilityElement = false
            cell.accessibilityTraits = .button

            if isAmendableRoom {
                let changeRoomCriteriaGestureRecognizer = UITapGestureRecognizer(
                    target: self,
                    action: #selector(self?.changeRoomCriteria)
                )
                cell.wholeCellButton.addGestureRecognizer(changeRoomCriteriaGestureRecognizer)
            }

            return cell
        })
    }

    @objc func changeRoomCriteria() {
        presenter?.changeRoomCriteriaTapped()
    }

    func leadGuestDetailsSection() -> FormekaModelSection {
        var nameTraits = FormekaTextFieldTraits()
        nameTraits.autocapitalizationType = .words

        var rows: [FormekaModelRow] = []

        rows.append(
            buttonRow(
                tag: LeadGuestDetailRows.title.rawValue,
                title: PILocalizedString("userDetailsTitleLabel"),
                validators: [.required],
                value: presenter?.leadGuestTitle,
                action: presenter?.guestsAreAmendable == true ? { [weak self] _, _ in
                    self?.presenter?.salutationRowDidTap()
                } : nil
            )
        )

        rows.append(textFieldRow(
            name: LeadGuestDetailRows.firstName.rawValue,
            title: PILocalizedString("userDetailsFirstNameLabel"),
            value: presenter?.leadGuestFirstName,
            traits: nameTraits,
            inlineValidators: [.name],
            onBlurValidators: [StringLengthValidator(range: 1...30)],
            maxNumberOfCharacters: 30,
            inputAccessoryView: nil,
            onChange: { [weak self] in
                if let newFirstName = self?.viewModel?.row(named: LeadGuestDetailRows.firstName.rawValue)?.value as? String {
                    self?.presenter?.firstNameDidChange(to: newFirstName)
                }
            },
            isReadOnly: presenter?.guestsAreAmendable == false,
            maskField: true
        ))

        rows.append(textFieldRow(
            name: LeadGuestDetailRows.lastName.rawValue,
            title: PILocalizedString("userDetailsLastNameLabel"),
            value: presenter?.leadGuestLastName,
            traits: nameTraits,
            inlineValidators: [.name],
            onBlurValidators: [StringLengthValidator(range: 1...30)],
            onChange: { [weak self] in
                if let newLastName = self?.viewModel?.row(named: LeadGuestDetailRows.lastName.rawValue)?.value as? String {
                    self?.presenter?.lastNameDidChange(to: newLastName)
                }
            },
            isReadOnly: presenter?.guestsAreAmendable == false,
            maskField: true
        ))

        let sectionHeader = leadGuestDetailsHeader(title: PILocalizedString("userDetailsBookerStayerSectionTitle"))
        return FormekaModelSection(header: sectionHeader, rows: rows, footer: nil)
    }

    func roomSection(with room: Room) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { [weak self] (indexPath, _, table) in
            guard let cell: AdultsStepperCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.detail?.textColor = .slateGrey
            cell.setup(
                with: RangeManager(range: 1...Constants.Config.maxNumberOfAdults, currentStep: room.adults),
                formatString: PILocalizedString("%d")
            )

            return cell
        }))

        rows.append(FormekaModelRow(
            tag: String(describing: ChildrenStepperCell.self) + "\(String(describing: index))",
            cellSetup: { [weak self] (indexPath, _, table) in
            guard let cell: ChildrenStepperCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.detail?.textColor = .slateGrey
            cell.setup(
                with: RangeManager(range: 0...Constants.Config.maxNumberOfChildren, currentStep: room.children),
                formatString: PILocalizedString("%d")
            )

            return cell
        }
        ))

        if room.children > 0 {
            rows.append(cotSwitchRow(cotRequired: room.cotRequired))
        }

        rows.append(
            FormekaModelRow(
                tag: String(describing: NextGenRoomTypeSelectorCell.self) + "\(String(describing: index))",
                cellSetup: { (indexPath, _, table) in
                    guard let cell: NextGenRoomTypeSelectorCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.roomTypeSelectorButton?.isEnabled = true
                    cell.accessibilityTraits = .button
                    cell.roomTypeLabel.text = room.roomName ?? room.type.name

                    return cell
                },
                didSelect: { [weak self] _, _ in
                    self?.presenter?.roomTypeButtonDidSelect()
                }
            )
        )

        let roomNumberText: String = {
            guard let roomNumber = presenter?.roomNumber else { return "" }

            return "\(roomNumber)"
        }()

        return FormekaModelSection(
            header: roomHeader(title: PILocalizedString("Room", comment: "") + " \(roomNumberText)"),
            rows: rows,
            footer: nil
        )
    }

    private func cancelSection() -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(tag: AmendRoomRows.cancel.rawValue, cellSetup: { [weak self] indexPath, _, table in
            guard let cell: CancelButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.backgroundColor = .BaseWhite

            let roomNumberText: String = {
                guard let roomNumber = self?.presenter?.roomNumber else { return "" }
                return "\(roomNumber)"
            }()

            cell.cancelButton.setTitle(
                String.localizedStringWithFormat(PILocalizedString("amendBookingCancelRoomButtonTitle"), roomNumberText),
                for: .normal
            )

            cell.cancelButton.backgroundColor = .BaseWhite
            cell.cancelButton.setTitleColor(.paleRed, for: .normal)
            cell.cancelButton.layer.borderColor = UIColor.paleRed.cgColor
            cell.cancelButton.layer.borderWidth = 1

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }


    private func leadGuestDetailsHeader(title: String?) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 70, viewSetup: {  _, table in
            guard let view: ActionableHeader = table.headerFooterView() else { return nil }

            view.heading.text = title
            view.heading.textColor = .TintD1
            view.heading.font = .Heading3_Semibold()
            view.heading.accessibilityTraits.insert(.header)
            view.actionButton.isHidden = true

            return view
        })
    }

    private func roomHeader(title: String?) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 66, viewSetup: { _, table in
            guard let view: ActionableHeader = table.headerFooterView() else { return nil }

            view.heading.text = title
            view.heading.textColor = .BasePurple
            view.heading.font = .Heading1_Semibold()

            view.actionButton.isHidden = true

            return view
        })
    }

    private func simpleFooter(lineColor: UIColor) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 10, viewSetup: { _, table in
            let view: SimpleFooter? = table.headerFooterView()
            view?.contentView.backgroundColor = .whiteTwo
            view?.lineView.backgroundColor = lineColor

            return view
        })
    }

    func cotSwitchRow(cotRequired: Bool) -> FormekaModelRow {
        FormekaModelRow(
            tag: String(describing: NextGenCotSelectorCell.self),
            cellSetup: { [weak self] (indexPath, _, table) in
            guard let cell: NextGenCotSelectorCell = table.dequeueCell(for: indexPath) else { return nil }

            let includeCotString = PILocalizedString("criteriaIncludeCotSwitchLabel")
            let mutableString = NSMutableAttributedString(
                string: includeCotString,
                attributes: [.foregroundColor: UIColor.TintD1, .font: UIFont.Heading4_Semibold()]
            )
            let yearsOld = PILocalizedString("infantYearsRange")
            let mutableYearsOld = NSMutableAttributedString(
                string: yearsOld,
                attributes: [.foregroundColor: UIColor.TintD1, .font: UIFont.Body()]
            )
            mutableString.append(mutableYearsOld)
            cell.titleLabel.attributedText = NSAttributedString(attributedString: mutableString)

            cell.delegate = self
            cell.cotSelectorSwitch.isOn = cotRequired

            return cell
        }
        )
    }
}

extension AddRoomView: RoomTypeSelectRouterDelegate {
    func roomTypeSelectViewControllerDidUpdate(roomType: SelectableRoomType) {
        guard let roomType = roomType as? RoomType else { return }
        presenter?.roomTypeDidSelect(roomType)
    }
}

extension AddRoomView: ErrorAndPhoneViewProtocol {
    func callCustomerService() {
        presenter?.callCustomerServiceButtonDidTap()
    }
}

extension AddRoomView: CustomStepperCellDelegate {
    func customStepperCellDidChangeValue(cell: CustomStepperCell, value: Int) {
        switch cell {
        case is AdultsStepperCell:
            presenter?.numberOfAdultsDidChange(value: value)

        case is ChildrenStepperCell:
            presenter?.numberOfChildrenDidChange(value: value)

        default:
            break
        }
    }

    func customStepperCellDidFailChangingValue(cell: CustomStepperCell, error: NSError) {
        switch cell {
        case is AdultsStepperCell, is ChildrenStepperCell:
            if let indexPath = table.indexPath(for: cell) {
                presenter?.numberOfPeopleNotAllowed(at: indexPath, error: error)
            }

        default:
            break
        }
    }
}

extension AddRoomView: CotSelectorCellDelegate {
    func cotSwitchDidChange(cell: CotSelectorCell) {
        presenter?.cotValueDidChange(value: cell.cotSelectorSwitch.isOn)
    }
}
