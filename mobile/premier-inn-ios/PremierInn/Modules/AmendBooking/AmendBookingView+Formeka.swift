//
//  AmendBookingView+Formeka.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/10/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import UIKit

enum AmendBookingRow: String {
    case cancelButton = "CancelButton"
    case banner
}

extension AmendBookingView {
    func viewModelSections(with amendViewModel: AmendBookingViewModel) -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        sections.append(amendTitleSection(with: amendViewModel.hotelName, hotelImage: amendViewModel.hotelImageUrl))

        if let bannerMessage = amendViewModel.bannerMessage, bannerMessage.isNotEmpty {
			sections.append(
				amendBannerSection(
				    tag: AmendBookingRow.banner.rawValue,
				    message: bannerMessage,
				    style: .info
				)
			)
		}

		sections.append(editDatesSection(
		    datesSummary: amendViewModel.datesSummary,
		    amendable: amendViewModel.shouldShowChangeDatesButton
		))

        if let amendUpsellsModel = amendViewModel.amendUpsellsModel {
			if amendViewModel.shouldShowUpsellsRemovedMessage {
				var attributedString = AttributedString(
				    PILocalizedString("amendUpsellsRemovedTitle"),
				    attributes: AttributeContainer([.font: UIFont.BodySmall_Semibold()])
				)
				attributedString.append(AttributedString("\n"))
				attributedString.append(AttributedString(
				    PILocalizedString("amendUpsellsRemovedDescription"),
				    attributes: AttributeContainer([.font: UIFont.BodySmall()])
				)
				)

				sections.append(
					amendBannerSection(
					    tag: AmendBookingRow.banner.rawValue,
					    attributedMessage: NSAttributedString(attributedString),
					    style: .alert
					)
				)
			}
            sections.append(mealAndExtrasSection(
                with: amendUpsellsModel,
                amendable: amendViewModel.shouldShowChangeUpsellsButton
            ))
        }

        sections.append(roomSectionTitle())

        for (roomNumber, roomViewModel) in amendViewModel.rooms.enumerated() {
            // rooms added in the current amend flow can be edited regardless of restrictions
            let amendable = amendViewModel.shouldShowEditRoomButton || !roomViewModel.existingRoom

            sections.append(roomSection(with: roomViewModel, andRoomNumber: roomNumber + 1, amendable: amendable))
        }

        if amendViewModel.shouldShowAddRoomButton {
            sections.append(separatorSection)
            sections.append(addAdditionalRoomSection(
                isCancelButtonPresent: amendViewModel.shouldShowCancelBookingButton
            ))
        }

        if amendViewModel.shouldShowCancelBookingButton {
            sections.append(cancelSection)
        }

        return sections
    }

    private func editDatesSection(datesSummary: String, amendable: Bool) -> FormekaModelSection {
        let amendAction: TableRowCellSelection? = {
            guard amendable else { return nil }
            return { [weak self] _, _ in
                self?.eventHandler?.editDatesButtonDidTap()
            }
        }()

        return amendDetailSection(
            with: PILocalizedString("datesSectionTitle"),
            and: datesSummary,
            using: amendAction,
            actionName: PILocalizedString("hotelDetailsFullyBookedEditDates")
        )
    }

    private func mealAndExtrasSection(with amendUpsellsModel: AmendUpsellsModel, amendable: Bool) -> FormekaModelSection {
        let amendAction: TableRowCellSelection? = {
            guard amendable else { return nil }
            return { [weak self] _, _ in
                self?.eventHandler?.editUpsellsDidTap()
            }
        }()

        return amendDetailSection(
            with: amendUpsellsModel.title,
            and: amendUpsellsModel.description,
            using: amendAction,
            actionName: amendUpsellsModel.actionTitle
        )
    }

    private func amendTitleSection(with title: String, hotelImage: URL?) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationHotelInfoCell = table.dequeueCell(for: indexPath) else { return nil }

            if let hotelImage {
                cell.hotelImageView.setImage(with: hotelImage)
            }
            cell.hotelNameLabel.text = title
            cell.hiddenSeparatorLocations = [.bottom]
            cell.selectionStyle = .none
            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

	private func amendBannerSection(
	    tag: String,
	    message: String? = nil,
	    attributedMessage: NSAttributedString? = nil,
	    style: NotificationStyle
	) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        let bannerRow = FormekaModelRow.iconInfoRow(
            tag: AmendBookingRow.banner.rawValue,
            text: message,
            attributedString: attributedMessage,
            style: style
        )

        rows.append(bannerRow)

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func amendDetailSection(
        with title: String,
        and description: String?,
        using action: TableRowCellSelection?,
        actionName: String?
    ) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(subtitleCell(with: title, and: description))

        if let action = action {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: SimpleActionCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.action.textColor = UIColor.grape
                cell.action.font = .Action3()
                cell.action.text = actionName
                cell.leadingConstraint.constant = 16.0

                return cell
            }, didSelect: action))
        }

        rows.append(separatorRow)

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func roomSectionTitle() -> FormekaModelSection {
        var rows: [FormekaModelRow] = []
        rows.append(subtitleCell(with: PILocalizedString("Guests and Rooms"), and: nil))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func roomSection(
        with amendRoomViewModel: AmendBookingRoomViewModel,
        andRoomNumber roomNumber: Int,
        amendable: Bool
    ) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.font = .Body_Semibold()
            cell.content.text = PILocalizedString("Room", comment: "") + " \(amendRoomViewModel.roomNumber)"
            cell.content.accessibilityIdentifier = "titleRoomNumber" + String(describing: roomNumber)
            return cell
        }))

        rows
            .append(sectionDetailRow(with: "\(amendRoomViewModel.roomDescription)\n\(amendRoomViewModel.leadGuest)"
            .capitalized))

        if let breakfastDescriptions = amendRoomViewModel.mealDescription {
            rows.append(sectionDetailRow(
                with: breakfastDescriptions,
                accessibilityIdentifier: "mealsCellAcc" + "\(String(describing: roomNumber))"
            ))
        }

        if let extraDescription = amendRoomViewModel.extraDescription {
            rows.append(subtitleCell(
                with: PILocalizedString("Extras", comment: "Extras section title"),
                and: extraDescription,
                and: 18,
                accessibilityIdentifier: "extrasCellAcc" + "\(String(describing: roomNumber))"
            ))
        }

        if amendable {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: SimpleActionCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.action.textColor = UIColor.grape
                cell.action.font = .Action3()
                cell.action.text = PILocalizedString("ChangeGuestsFor") + PILocalizedString("Room") + " \(amendRoomViewModel.roomNumber)"

                return cell
            }, didSelect: { [weak self] _, _ in
                self?.editRoomButtonDidTap(forRoomNumber: roomNumber)
            }))
        }

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func addAdditionalRoomSection(isCancelButtonPresent: Bool) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(
            tag: String(describing: AddRoomCell.self),
            cellSetup: { [weak self] (indexPath, _, table) in
            guard let cell: AddRoomCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.buttonLeadingConstraint.constant = 16.0
            cell.buttonTrailingConstraint.constant = 16.0
            cell.buttonHeightConstraint.constant = 56.0
            cell.addButton.backgroundColor = .BaseWhite
            cell.contentView.backgroundColor = .BaseWhite

            cell.addButton.configurationUpdateHandler = { button in
                var config = UIButton.Configuration.plain()
                config.title = PILocalizedString("addRoomButtonTitle", comment: "Add room button title")
                config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                    var newAttribute = attribute
                    newAttribute.font = UIFont.Button1()
                    return newAttribute
                }
                config.contentInsets = NSDirectionalEdgeInsets(top: 10, leading: 20, bottom: 10, trailing: 20)
                config.baseForegroundColor = .BasePurple
                config.baseBackgroundColor = .BaseWhite
                button.configuration = config
            }

            cell.addButton.layer.borderColor = UIColor.BasePurple.cgColor

            return cell
        }
        ))

        let footer = isCancelButtonPresent
        ? nil
        : footer(height: 100)

        return FormekaModelSection(
            header: self.footer(height: 20),
            rows: rows,
            footer: footer
        )
    }

    private var cancelSection: FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(
            tag: AmendBookingRow.cancelButton.rawValue,
            cellSetup: { [weak self] indexPath, _, table in
            guard let cell: CancelButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.backgroundColor = .BaseWhite
            cell.buttonHeightConstraint.constant = 56.0
            cell.buttonLeadingConstraint.constant = 16.0
            cell.cancelButton.backgroundColor = .BaseWhite
            cell.cancelButton.setTitleColor(.paleRed, for: .normal)
            cell.cancelButton.layer.borderColor = UIColor.paleRed.cgColor
            cell.cancelButton.layer.borderWidth = 1

            return cell
        }
        ))

        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: footer(height: 100)
        )
    }

    private func footer(height: CGFloat) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, _ in
            let footerView = UITableViewHeaderFooterView(frame: CGRect.zero)
            footerView.contentView.backgroundColor = .BaseWhite

            return footerView
        })
    }

    private func sectionDetailRow(with string: String, accessibilityIdentifier: String? = nil) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.accessibilityIdentifier = accessibilityIdentifier
            cell.messageTopConstraint.constant = 2

            cell.content.textColor = UIColor.TintD1
            cell.content.font = .Body()

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 8

            let attributedString = NSMutableAttributedString(
                string: string,
                attributes: [NSAttributedString.Key.paragraphStyle: paragraphStyle]
            )

            cell.content.attributedText = NSAttributedString(attributedString: attributedString)
            ContentsquareConfig.mask(view: cell)
            return cell
        })
    }

    private var separatorSection: FormekaModelSection {
        FormekaModelSection(header: nil, rows: [separatorRow], footer: nil)
    }

    private var separatorRow: FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BottomBorderCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.paddingStyle = .paddedGap(left: 16, right: -16)
            cell.accessibilityElementsHidden = true
            return cell
        })
    }

    private func subtitleCell(
        with title: String,
        and string: String?,
        and topMargin: CGFloat = 20,
        accessibilityIdentifier: String = ""
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SimpleSubtitleCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.accessibilityIdentifier = accessibilityIdentifier

            cell.topConstraint.constant = topMargin

            cell.title.textColor = .BasePurple
            cell.title.font = .Heading2_Bold()
            cell.title.text = title

            cell.title.accessibilityIdentifier = accessibilityIdentifier + "title"
            cell.subtitle.accessibilityIdentifier = accessibilityIdentifier + "subtitle"

            if let string = string {
                cell.bottomConstraint.constant = 0

                cell.subtitle.textColor = .TintD1
                cell.subtitle.font = .Body()

                let paragraphStyle = NSMutableParagraphStyle()
                paragraphStyle.lineSpacing = 8

                let attributedString = NSMutableAttributedString(
                    string: string,
                    attributes: [NSAttributedString.Key.paragraphStyle: paragraphStyle]
                )

                cell.subtitle.attributedText = NSAttributedString(attributedString: attributedString)
            } else {
                cell.subtitle.text = nil
                cell.bottomConstraint.constant = 0
            }

            return cell
        })
    }
}
