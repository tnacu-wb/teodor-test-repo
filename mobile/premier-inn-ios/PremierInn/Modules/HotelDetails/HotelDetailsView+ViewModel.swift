//
//  HotelDetailsView+ViewModel.swift
//  PremierInn
//
//  Created by Nick Jones on 12/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension HotelDetailsViewController {
    func registerCellsFor(_ table: UITableView) {
        table.registerCellNib(with: HotelMoreInfoCell.self)
        table.registerCellNib(with: HotelLocationInfoCell.self)
        table.registerCellNib(with: HotelFewRoomsCell.self)
        table.registerCellNib(with: HotelDetailsCriteriaSummaryCell.self)
        table.registerCellNib(with: ErrorHDPCell.self)
        table.registerCellNib(with: RateCell.self)
        table.registerCellNib(with: HotelDetailUserActionErrorCell.self)
        table.registerCellNib(with: HotelCheckAvailabilityCell.self)
        table.registerCellNib(with: HDPInfoCellTableViewCell.self)
        table.registerCellNib(with: HDPInfoBannerCell.self)
        table.registerCellNib(with: HotelInformationSegmentsCell.self)
        table.registerCellNib(with: FoodContentCell.self)
        table.registerCellNib(with: RoomContentCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: CallHotelCell.self)
        table.registerCellNib(with: HotelDetailNameAddressCell.self)
        table.registerCellNib(with: HotelDetailSeparatorCell.self)
        table.registerCellNib(with: HotelBadgesCell.self)
        table.registerCellNib(with: TripAdvisorCell.self)
        table.registerCellNib(with: LocationDetailsCell.self)
        table.registerCellNib(with: HotelFacilitiesCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: CheckInOutHDPCell.self)
        table.registerCellNib(with: MealsInformationCell.self)
        table.registerCellNib(with: GDPRBannerHeaderRow.self)
        table.registerCellNib(with: CarouselCell.self)
        table.registerCellNib(with: HotelAccessibilityInfoCell.self)
        table.registerCellNib(with: ReservationListActionCell.self)
        table.registerCellNib(with: ActionIconCell.self)
        table.registerCellNib(with: DottedSeparatorCell.self)
        table.registerCellNib(with: TripAdvisorSummaryCell.self)
        table.registerCellNib(with: AlertMessageCell.self)
        table.registerCellNib(with: ImageWithTextCell.self)
        table.registerHeaderFooterNib(with: SimpleFooter.self)
        table.registerHeaderFooterNib(with: SimpleHeaderWithActionLabel.self)
        table.registerHeaderFooterNib(with: TripAdvisorHeader.self)
    }

    var tableViewModelScructure: FormekaViewModel? {
        guard let viewModel = self.viewModel else { return nil }

        var sections = [FormekaModelSection]()

        sections.append(carouselSection(withCarouselViewModel: viewModel.carouselViewModel))
        sections.append(titleAndSummarySection(with: viewModel.titleAndSummaryViewModel))

        if let substitutionText = viewModel.substitutionRowText {
            sections.append(substitutionWarningRow(withText: substitutionText))
        }

        if viewModel.shouldShowErrorSection {
            sections.append(errorsSection())
        }

        if viewModel.shouldShowBBNotAvailableSection {
            sections.append(bbNotAvailableSection())
        }

        sections.append(importantInfoSection())

        if viewModel.shouldShowAddADiscountCodeSection {
            sections.append(discountCodeSection(viewModel: viewModel.discountCodeViewModel))
        }

        if viewModel.shouldShowCompanyDetails {
            sections.append(businessDetailSection())
        }

        if viewModel.shouldShowCotNotAvailableMessageSection {
            sections.append(cotMessageSection())
        }

        if viewModel.shouldShowRatesSection {
            for rateSectionViewModel in viewModel.rateSectionViewModels {
                sections.append(rateSections(with: rateSectionViewModel, andShowAction: true))
            }
        }

        if let accessViewModel = viewModel.accessibilityInfoViewModel {
            sections.append(accessibilityInfoSection(with: accessViewModel))
        }

        sections.append(mapSection(with: viewModel.mapSectionViewModel))
        sections.append(additionalInfoSection(
            with: viewModel.additionalInfoAndFacilities,
            shouldShowParkingInfo: viewModel.shouldShowParkingSection,
            and: viewModel.additionalInfoAndFacilities.lettingTypeToShowInRoomInfo
        ))

        if viewModel.shouldShowRoomSection {
            sections.append(roomSection(with: viewModel.roomSectionViewModel))
        }

        if viewModel.shouldShowFoodSection {
            if viewModel.foodAndRestaurantViewModel.foodInfo.isEmpty {
                currentFoodSectionIndex = 1
            }

            if viewModel.shouldShowRestaurantInformation {
                sections.append(foodOptionsAndRestaurantSection(with: viewModel.foodAndRestaurantViewModel))
            } else {
                sections.append(foodSection(with: viewModel.foodAndRestaurantViewModel.foodInfo))
            }
        } else {
            sections.append(noFoodSection())
        }

//        if viewModel.shouldShowParkingSection {
//            if let parkingSection = parkingSection(with: viewModel.parkingSectionViewModel) { sections.append(parkingSection) }
//        }

        if let tripAdvisorViewModel = viewModel.tripAdvisorViewModel {
            sections.append(tripAdvisorSection(with: tripAdvisorViewModel))
        }

        sections.append(phoneCallSection(callSectionViewModel: viewModel.callSectionViewModel))

        if tableView != nil {
            sections.append(gdprShieldSection(for: tableView))
        }

        return FormekaViewModel(sections: sections)
    }
}

extension HotelDetailsViewController {
    private func getBBErrorHotelsNearbyButtonTitle() -> String {
        if self.navigationController?.viewControllers.first(where: { $0 is MapListContainerViewController }) != nil {
            return PILocalizedString("showHotelsNearbyTitle")
        }
        return PILocalizedString("returnToSearchButtonTitle")
    }

    private func phoneCallSection(callSectionViewModel: CallSectionViewModel? = nil) -> FormekaModelSection {
        FormekaModelSection(
            header: nil,
            rows: [FormekaModelRow(
                tag: HotelDetailRow.callHotelCell.rawValue,
                cellSetup: { [unowned self] indexPath, _, table in
                guard let cell: CallHotelCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.contentView.backgroundColor = .BaseWhite
                cell.callChargeInformationLabel.text = callSectionViewModel?.costDescription
                cell.callHotelButton.addTarget(self, action: #selector(callHotelButtonDidTap), for: .touchUpInside)
                cell.callHotelButton.setTitle(callSectionViewModel?.callDescription, for: .normal)
                cell.callHotelButton.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.callUsButton

                return cell
            }
            )],
            footer: nil
        )
    }

    @objc func callHotelButtonDidTap() {
        eventHandler.callHotelDidTap()
    }

    private func cotMessageSection() -> FormekaModelSection {
        let row = FormekaModelRow(tag: HotelDetailRow.cotNotAvailableMessage.rawValue,
                                  cellSetup: { indexPath, _, table in
            guard let cell: ErrorHDPCell = table.dequeueCell(for: indexPath) else { return nil }

            let message = PILocalizedString("cotNotAvailableMessage")
            cell.styleUIForWarning(with: message)

            return cell
        })

        return FormekaModelSection(header: nil, rows: [row], footer: nil)
    }

    private func errorsSection() -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(tag: HotelDetailRow.errorCell.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: ErrorHDPCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.titleLabel.text = PILocalizedString(
                "hotelDetailsFullyBooked",
                comment: "Hotel details: fully booked message"
            )

            return cell
        }))

        rows.append(FormekaModelRow(
            tag: HotelDetailRow.hotelDetailUserActionErrorCell.rawValue,
            cellSetup: { [weak self] indexPath, _, table in
            guard let cell: HotelDetailUserActionErrorCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.ctaButton1.setTitle(
                PILocalizedString("showHotelsNearbyTitle", comment: "Show me hotels nearby button title"),
                for: .normal
            )
            cell.ctaButton1.actionTouchUp {
                cell.activityIndicator.startAnimating()
                cell.ctaButton1.setTitle(nil, for: .normal)
                self?.eventHandler.showHotelsNearbyButtonTapped()
            }
            cell.didTapCtaButton2 = { [weak self] in
                    self?.datesButtonTapped()
            }
            return cell
        }
        ))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func bbNotAvailableSection() -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(tag: HotelDetailRow.errorCell.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: ErrorHDPCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.titleLabel.text = PILocalizedString("hotelDetailsBBUnavailableMessage")

            return cell
        }))

        rows.append(FormekaModelRow(
            tag: HotelDetailRow.hotelDetailUserActionErrorCell.rawValue,
            cellSetup: { [weak self] indexPath, _, table in
            guard let cell: HotelDetailUserActionErrorCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.ctaButton1.setTitle(self?.getBBErrorHotelsNearbyButtonTitle(), for: .normal)
            cell.ctaButton1.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.bbLogoutButton
            cell.ctaButton1.actionTouchUp {
                // This just goes back to the home screen, need custom functionality for SRP
                cell.activityIndicator.startAnimating()
                cell.ctaButton1.setTitle(nil, for: .normal)
                self?.eventHandler.showHotelsNearbyButtonTapped()
            }

            cell.ctaButton2.setTitle(
                PILocalizedString("hotelDetailsBBLogout", comment: "Show me hotels nearby button title"),
                for: .normal
            )
            cell.didTapCtaButton2 = { [weak self] in
                    self?.logOutButtonClicked()
            }
            return cell
        }
        ))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func businessDetailSection() -> FormekaModelSection {
        let row = FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: ImageWithTextCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.topLabel.text = PILocalizedString("bookingAsLabel")
            cell.companyNameLabel.text = self.viewModel?.companyName
            cell.containerView.backgroundColor = .BaseWhite

            return cell
        })
        return FormekaModelSection(header: nil, rows: [row], footer: separatorFooterView())
    }

    private func importantInfoSection() -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        if let viewModel = viewModel?.titleAndSummaryViewModel.importantInfoViewModel {
            rows.append(
                FormekaModelRow(
                    tag: HotelDetailRow.availabilityUnchecked.rawValue,
                    cellSetup: { indexPath, _, table in
                        guard let cell: HDPInfoCellTableViewCell = table.dequeueCell(for: indexPath) else { return nil }

                        cell.setHeight(nil)
                        cell.titleLabel.text = viewModel.importantInfoShortSummary
                        cell.iconImageView.image = viewModel.leadingIconImage
                        return cell
                    },
                    didSelect: { [weak self] _, _ in
                        self?.eventHandler.hotelMoreInformationButtonDidTap()
                    }
                )
            )
        }
        if viewModel?.titleAndSummaryViewModel.shouldShowCoronavirusMessaging ?? false {
            rows.append(coronavirusMessagingRow(text: viewModel?.titleAndSummaryViewModel.announcementText))
        }

        let footerView = viewModel?.shouldShowAddADiscountCodeSection == true ? spacingView() : separatorFooterView()

        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: footerView
        )
    }

    private func discountCodeSection(viewModel: HotelDetailsDiscountCodeViewModel?) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(
            FormekaModelRow(
                tag: HotelDetailRow.discountCodeCell.rawValue,

                cellSetup: { [weak self] indexPath, _, table in
                    guard let cell: HDPInfoCellTableViewCell = table.dequeueCell(for: indexPath) else {
                        return nil
                    }

                    cell.setHeight(nil)
                    cell.iconImageView.image = UIImage(named: "tagIcon")
                    cell.titleLabel.text = self?.getDiscountCellTitleText(from: viewModel)
                    return cell
                },

                didSelect: { [weak self] _, _ in
                    self?.eventHandler.discountCodeButtonDidTap()
                }
            )
        )

        return FormekaModelSection(
            header: spacingView(),
            rows: rows,
            footer: separatorFooterView()
        )
    }

    private func coronavirusMessagingRow(text: String?) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.coronavirusMessaging.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: HDPInfoBannerCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.titleLabel.text = text ?? ""
            return cell
        })
    }

    func separatorFooterView(height: CGFloat = 32) -> FormekaModelHeaderFooter? {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, _ in
            let footerView = SeparatorFooterView(lineHeight: 1)
            return footerView
        })
    }

    private func spacingView(height: CGFloat = 8) -> FormekaModelHeaderFooter? {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, _ in
            SeparatorFooterView(lineHeight: 0)
        })
    }
}

extension HotelDetailsViewController: CriteriaSummaryCellDelegate {
    func datesButtonTapped() {
        eventHandler.showCalendarDidTap()
    }

    func guestsAndRoomsButtonTapped() {
        eventHandler.showGuestsAndRoomsDidTap()
    }
}

private extension HotelDetailsViewController {
    func getDiscountCellTitleText(from viewModel: HotelDetailsDiscountCodeViewModel?) -> String {
        if let viewModel, viewModel.isDiscountCodeValid {
            PILocalizedString("hotelDetailsDiscountCodeAddedCodeTitle")
        } else {
            PILocalizedString("hotelDetailsDiscountCodeTitle")
        }
    }
}
