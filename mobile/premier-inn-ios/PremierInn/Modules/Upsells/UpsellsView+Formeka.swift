//
//  UpsellsView+Formeka.swift
//  PremierInn
//
//  Created by Marcello Mascia on 05/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

extension UpsellsViewController {
    func tableViewModel(with upsellsViewModel: UpsellsViewModel) -> FormekaViewModel {
        var sections = [FormekaModelSection]()
        let upsellCloseOutState = upsellsViewModel.upsellCloseOutViewModel?.state
        let noUpsellsBannerShown = (upsellCloseOutState == .noUpsellsAvailable) ||
            (upsellCloseOutState == .isForcedNoUpsellsAvailable)

        if let allowancesViewModel = upsellsViewModel.upsellsAllowancesViewModel {
            sections.append(allowancesSection(with: allowancesViewModel))
        }

        if let section = getBannerSection(viewModel: upsellsViewModel.upsellCloseOutViewModel) {
            sections.append(section)
        }

        if noUpsellsBannerShown == false,
           upsellsViewModel.hasFoodUpsells,
           let section = mealsPhotosSection(
               restuarantMainImageUrl: upsellsViewModel.restaurantViewModel.restaurantMainImageUrl,
               restaurantImageUrls: upsellsViewModel.restaurantViewModel.restaurantImageUrls,
               sectionTitle: upsellsViewModel.restaurantViewModel.restaurantSectionTitle,
               allowanceMessage: upsellsViewModel.restaurantViewModel.restaurantAllowanceMessage
           ) {
            sections.append(section)
        }

        if let roomSections = mealsSections(roomsViewModels: upsellsViewModel.roomViewModels),
           noUpsellsBannerShown == false {
            sections.append(contentsOf: roomSections)
        }

        if noUpsellsBannerShown == false,
           upsellsViewModel.hasFoodUpsells,
           let menus = upsellsViewModel.restaurantViewModel.menuViewModels,
           let allergenInformation = upsellsViewModel.restaurantViewModel.allergenInformationViewModels {
            sections.append(additionalOptionsSection(with: menus, and: allergenInformation))
        }

        let extraUpsells = extraUpsellsSection(extraUpsellsRoomModels: upsellsViewModel.extraUpsellsModels)
        if extraUpsells.isNotEmpty {
            sections.append(contentsOf: [extraUpsellsHeadderSection()])
            sections.append(contentsOf: extraUpsells)
        }

        if let totalViewModel = upsellsViewModel.totalViewModel {
            sections.append(totalSection(with: totalViewModel))
        }
        // TODO: AB Test to remove when completed - Sticky Extras CTA
        if !shouldShowStickyCTA {
            sections.append(continueButtonSection())
        }

        return FormekaViewModel(sections: sections)
    }

    private func getBannerSection(viewModel: UpsellCloseOutBannerViewModel?) -> FormekaModelSection? {
        switch viewModel?.state {
        case .noUpsellsAvailable, .isForcedNoUpsellsAvailable:
            guard let message = viewModel?.message else { return nil }
            guard let title = viewModel?.title else { return nil }

            if let section = noUpsellsAvailableSection(title: title, message: message) {
                return section
            }
        case .upsellsFilteredAmend, .isForcedUpsellChange, .upsellsFilteredAndSelectionExists:
            guard let message = viewModel?.message else { return nil }
            return upsellsFilteredAlertSection(message: message)
        default:
            return nil
        }
        return nil
    }
}

private extension UpsellsViewController {
    func allowancesSection(with allowancesViewModel: UpsellsAllowancesViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SimpleSubtitleCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.title.text = allowancesViewModel.title
            cell.title.font = .Heading3_Semibold()
            cell.title.textColor = .TintD1
            cell.title.accessibilityIdentifier = AccessibilityIdentifiers.Upsells.businessAllowancesTitle

            cell.subtitle.text = allowancesViewModel.message
            cell.subtitle.font = .Body()
            cell.subtitle.textColor = .TintD1

            cell.topConstraint.constant = 16
            cell.bottomConstraint.constant = 16

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }))

        allowancesViewModel.allowances.forEach { allowance in
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.content.attributedText = allowance.allowanceDescription
                cell.content.accessibilityIdentifier = allowance.accessibilityIdentifier

                cell.contentContainer.backgroundColor = .TintL5
                cell.contentContainer.layer.borderWidth = 1
                cell.contentContainer.layer.borderColor = UIColor.Tint9.cgColor
                cell.contentContainer.layer.cornerRadius = 2

                cell.contentContainerTopConstraint.constant = 0
                cell.contentContainerBottomConstraint.constant = 8
                cell.contentContainerLeadingConstraint.constant = 16
                cell.contentContainerTrailingConstraint.constant = 16

                cell.messageTopConstraint.constant = 12
                cell.messageBottomConstraint.constant = 12
                cell.messageLeadingConstraint.constant = 16
                cell.messageTrailingConstraint.constant = 16

                cell.hiddenSeparatorLocations = [.top, .bottom]

                return cell
            }))
        }

        return FormekaModelSection(header: nil, rows: rows, footer: simpleFooter(lineColor: .TintL3, height: 24))
    }

    func mealsPhotosSection(
        restuarantMainImageUrl: URL? = nil,
        restaurantImageUrls: [URL]? = nil,
        sectionTitle: String,
        allowanceMessage: String? = nil
    ) -> FormekaModelSection? {
        var rows: [FormekaModelRow] = []

        if let allowanceMessage {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.content.text = allowanceMessage
                cell.content.font = .Body()
                cell.content.textColor = .TintD1
                cell.content.accessibilityIdentifier = AccessibilityIdentifiers.Upsells.businessAllowanceMealsMessage
                cell.messageTopConstraint.constant = 0

                return cell
            }))
        }

        if let restaurantImageUrls {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                let images = restaurantImageUrls.compactMap({ $0.sizedImageURL(withSize: .massive) })
                guard images.isNotEmpty else { return nil }

                guard let cell: CarouselCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.urls = images
                cell.contentView.backgroundColor = allowanceMessage == nil ? .BaseGrey : .BaseWhite
                cell.collectionView.backgroundColor = allowanceMessage == nil ? .BaseGrey : .BaseWhite
                cell.messagingFlagLabel.isHidden = true

                return cell
            }))
        }

        return FormekaModelSection(
            header: FormekaModelHeaderFooter(height: 68, viewSetup: { _, table in
                guard let header: TitleAndOptionalImageHeader = table.headerFooterView() else { return nil }
                header.contentView.backgroundColor = .BaseWhite
                header.backgroundColor = .BaseWhite

                header.titleLabel.font = .Heading1_Bold()
                header.titleLabel.textColor = .BasePurple
                header.titleLabel.text = sectionTitle
                header.titleLabel.accessibilityTraits.insert(.header)

                if let url = restuarantMainImageUrl {
                    header.imageView.setImage(with: url)
                } else {
                    header.imageView.image = nil
                }

                return header
            }),
            rows: rows,
            footer: nil
        )
    }

    func mealsInformationSection(bookingDetails: BookingDetails) -> FormekaModelSection? {
        guard let mealsInfo = bookingDetails.mealInformationText else { return nil }

        return FormekaModelSection(
            header: headerFooter(height: CGFloat.leastNormalMagnitude),
            rows: [
                FormekaModelRow(cellSetup: { indexPath, _, table in
                    guard let cell: MealsInformationCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.informationLabel.text = mealsInfo

                    return cell
                })
            ],
            footer: headerFooter(height: 10)
        )
    }

    func upsellsFilteredAlertSection(message: String) -> FormekaModelSection {
        let row = iconInfoRow(text: message, topPadding: 4, bottomPadding: 8, style: .alert)
        return FormekaModelSection(
            header: nil,
            rows: [row],
            footer: nil
        )
    }

    func iconInfoRow(
        text: String,
        topPadding: CGFloat,
        bottomPadding: CGFloat,
        style: NotificationStyle
    ) -> FormekaModelRow {
        // this row doesn't have a value so the notNil validator will be triggered
        let validators = style == .error ? [Validator.notNil] : []

        return FormekaModelRow(onBlurValidators: validators, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.hiddenSeparatorLocations = [.top, .bottom]

            let mutableAttributedString = NSMutableAttributedString(string: text)
            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 4

            mutableAttributedString.addAttribute(
                .paragraphStyle,
                value: paragraphStyle,
                range: NSRange(location: 0, length: mutableAttributedString.string.count)
            )

            cell.bottomConstraint.constant = bottomPadding
            cell.topConstraint.constant = topPadding

            cell.content.attributedText = NSAttributedString(attributedString: mutableAttributedString)
            cell.content.textColor = style == .error ? .Tint8 : .ColourDL1
            cell.content.font = .BodySmall()
            cell.content.backgroundColor = .clear

            cell.containerView.backgroundColor = style.background
            cell.containerView.layer.borderColor = style.tint.withAlphaComponent(0.4).cgColor
            cell.containerView.layer.cornerRadius = 4

            cell.icon.image = style.icon
            cell.icon.tintColor = style.tint

            cell.backgroundColor = .white

            return cell
        })
    }

    func noUpsellsAvailableSection(title: String, message: String) -> FormekaModelSection? {
        FormekaModelSection(
            header: nil,
            rows: [noRestaurantOptionRow(title: title, message: message)],
            footer: simpleFooter(lineColor: .TintL2)
        )
    }

    func mealsSections(roomsViewModels: [UpsellsRoomViewModel]) -> [FormekaModelSection]? {
        let sections: [FormekaModelSection] = roomsViewModels.enumerated().compactMap { roomIndex, roomViewModel in
            var rows = [FormekaModelRow]()

            if roomsViewModels.count > 1 {
                rows.append(titleRow(with: roomViewModel.name, and: roomViewModel.description))
            }

            if roomViewModel.collapsed {
                if let upsellSummary = roomViewModel.upsellsSummary {
                    rows.append(upsellChosenRow(with: roomIndex, and: upsellSummary))
                } else {
                    rows.append(noUpsellChosenRow(with: roomIndex))
                }

                rows.append(separatorRow())
            } else if let upsellItems = roomViewModel.upsellItems {
                for (mealIndex, itemViewModel) in upsellItems.enumerated() {
                    rows.append(mealOptionRow(for: itemViewModel, roomIndex: roomIndex, mealIndex: mealIndex))

                    if let infoMessage = itemViewModel.infoMessage {
                        rows.append(infoMessageRow(with: infoMessage))
                    }

                    rows.append(separatorRow())
                }
            }

            return FormekaModelSection(header: nil, rows: rows, footer: nil)
        }

        return sections
    }

    private func infoMessageRow(with message: String) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.text = message
            cell.content.textColor = .BaseWhite
            cell.content.font = .Body()

            cell.icon.tintColor = .BaseWhite

            cell.topConstraint.constant = 8
            cell.bottomConstraint.constant = 16

            return cell
        })
    }

    private func additionalOptionsSection(
        with menus: [MenuInfo],
        and allergenInformation: [AllergenInformationViewModel]
    ) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: RestaurantLinksCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.menusAction = { [weak self] in
                let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
                for menu in menus where !menu.name.isEmpty {
                    alertController.addAction(
                        UIAlertAction(
                            title: menu.name,
                            style: .default,
                            handler: { _ in
                                self?.openURLInSafari(url: menu.url)
                            }
                        )
                    )
                }
                alertController.addAction(UIAlertAction(title: PILocalizedString("Cancel"), style: .cancel, handler: nil))

                if UIDevice.current.userInterfaceIdiom == .pad {
                    alertController.modalPresentationStyle = .popover
                    alertController.popoverPresentationController?.sourceView = cell
                    alertController.popoverPresentationController?.sourceRect = cell.bounds
                }

                self?.present(alertController, animated: true, completion: nil)
            }

            cell.allergyAction = { [weak self] in
                let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
                for info in allergenInformation where !info.name.isEmpty {
                    alertController.addAction(
                        UIAlertAction(
                            title: info.name,
                            style: .default,
                            handler: { _ in
                                self?.openURLInSafari(url: info.url)
                            }
                        )
                    )
                }
                alertController.addAction(UIAlertAction(title: PILocalizedString("Cancel"), style: .cancel, handler: nil))

                if UIDevice.current.userInterfaceIdiom == .pad {
                    alertController.modalPresentationStyle = .popover
                    alertController.popoverPresentationController?.sourceView = cell
                    alertController.popoverPresentationController?.sourceRect = cell.bounds
                }

                self?.present(alertController, animated: true, completion: nil)
            }

            return cell
        }))

        rows.append(separatorRow())

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func totalSection(with totalViewModel: UpsellsTotalViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(contentsOf: totalViewModel.breakdownRows.map {
            breakDownRow(title: $0.title, value: $0.value)
        })

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingReviewAdditionsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.titleLabel.isHidden = !totalViewModel.showBreakdownTotalCost
            cell.titleLabel.text = totalViewModel.breakdownTotalCostLabel
            cell.titleLabel.font = .Heading2_Semibold()

            cell.valueLabel.text = totalViewModel.breakdownTotalCostValue
            cell.valueLabel.font = .Heading1_Semibold()

            cell.contentView.backgroundColor = .BaseWhite
            cell.hiddenSeparatorLocations = [.top, .bottom]

            cell.titleLabelTopConstraint.constant = 10.0

            return cell
        }))

        if let cardUrls = totalViewModel.paymentCardImageUrls {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: CreditCardsListCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.cardUrls = cardUrls

                return cell
            }))
        }

        return FormekaModelSection(header: headerFooter(height: 20), rows: rows, footer: nil)
    }

    private func separatorRow(backgroundColour: UIColor = .BaseWhite) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BottomBorderCell = table.dequeueCell(for: indexPath) else { return nil }
            return cell
        })
    }

    private func titleRow(with title: NSAttributedString, and summary: String) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SimpleSubtitleCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.topConstraint.constant = 24
            cell.bottomConstraint.constant = 10
            cell.solidSeparator.isHidden = true

            cell.title.attributedText = title
            cell.title.textColor = .BasePurple
            cell.title.font = .Heading1_Bold()

            cell.subtitle.font = UIFont.Body()
            // FIXME: no greyishBrown in the style
            cell.subtitle.textColor = .greyishBrown
            cell.subtitle.text = summary

            return cell
        })
    }

    private func upsellChosenRow(with roomIndex: Int, and summary: String) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: UpsellChosenSummaryCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.summary.text = summary
            cell.buttonAction = { [weak self] in
                self?.eventHandler?.selected(change: roomIndex)
            }

            return cell
        }, didSelect: { _, _ in
            // Please add action here, if desired.... 🤤
        })
    }

    private func noUpsellChosenRow(with roomIndex: Int) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: NoUpsellChosenCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.noUpsellDescription.text = PILocalizedString("noMealsChosen")
            cell.addUpsellButton.setTitle(PILocalizedString("Add meals"), for: .normal)
            cell.addButtonTitle = PILocalizedString("Add")

            cell.buttonAction = { [weak self] in
                self?.eventHandler?.selected(change: roomIndex)
            }

            return cell
        }, didSelect: { _, _ in
            // Please add action here, if desired.... 🤤
        })
    }
}

private extension UpsellsViewController {
    func headerFooter(height: CGFloat) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, _ in
            let footerView = UITableViewHeaderFooterView(frame: CGRect.zero)
            footerView.contentView.backgroundColor = .BaseWhite

            return footerView
        })
    }

    func mealOptionRow(
        for optionViewModel: UpsellItemViewModel,
        roomIndex: Int = 0,
        mealIndex: Int = 0,
        includeSeparator: Bool = false
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: MealOptionCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.mealTitle.text = optionViewModel.title
            cell.mealDescription.attributedText = optionViewModel.description
            cell.update(with: optionViewModel.note)

            cell.mealTitle.accessibilityIdentifier = "breakfastTitleAcc" + String(describing: indexPath.row)
            cell.priceDescription.accessibilityIdentifier = "breakfastPriceAcc" + String(describing: indexPath.row)

            cell.priceDescription.attributedText = optionViewModel.costSummary

            cell.delegate = self

            cell.plusButton.isHidden = optionViewModel.isHidden
            cell.minusButton.isHidden = optionViewModel.isHidden
            cell.plusButton.isEnabled = !optionViewModel.isHidden
            cell.minusButton.isEnabled = !optionViewModel.isHidden

            cell.stepperChanged = { value in
                cell.minusButton.isEnabled = value > 0
                self.stepperValueDidChange(value: value, roomIndex: roomIndex, mealId: mealIndex)
            }

            cell.setup(
                with: RangeManager(
                    range: 0...(optionViewModel.maxSelected ?? 1),
                    currentStep: optionViewModel.quantity ?? 0
                ),
                formatString: PILocalizedString("%d")
            )

            let roomIndexLocalised = (roomIndex + 1).localisedString

            cell.plusButton.accessibilityHint = cell.plusButton.fakeDisabled ? String.localizedStringWithFormat(
                PILocalizedString("maxMealAdditionAccessibilityValue"),
                roomIndexLocalised
            ) : String.localizedStringWithFormat(
                PILocalizedString("addMealAccessibilityValue"),
                optionViewModel.title,
                roomIndexLocalised
            )
            cell.minusButton.accessibilityHint = cell.minusButton.isEnabled ? String.localizedStringWithFormat(
                PILocalizedString("removeMealAccessibilityValue"),
                optionViewModel.title,
                roomIndexLocalised
            ) : String.localizedStringWithFormat(PILocalizedString("minMealAdditionAccessibilityValue"), roomIndexLocalised)

            return cell
            })
    }

    func noRestaurantOptionRow(title: String, message: String) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: NoRestaurantCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.titleLabel.text = title
            cell.descriptionLabel.text = message

            return cell
        }, didSelect: { _, _ in
            // Please add action here, if desired.... 🤤
        })
    }

    func breakDownRow(
        title: String?,
        value: String?,
        titleAccessibilityIdentifier: String? = nil,
        andValueAccessibilityIdentifier valueAccessibilityIdentifier: String? = nil
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingReviewAdditionsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.titleLabel.isHidden = false
            cell.titleLabel.text = title
            cell.titleLabel.font = UIFont.Body()
            cell.titleLabel.accessibilityIdentifier = titleAccessibilityIdentifier

            cell.valueLabel.text = value
            cell.valueLabel.font = UIFont.Heading2_Semibold()
            cell.valueLabel.accessibilityIdentifier = valueAccessibilityIdentifier

            cell.contentView.backgroundColor = .BaseWhite
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        })
    }

    private func simpleFooter(lineColor: UIColor, height: CGFloat = 10) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, table in
            let view: SimpleFooter? = table.headerFooterView()
            view?.contentView.backgroundColor = .BaseGrey
            view?.lineView.backgroundColor = lineColor

            return view
        })
    }
}

extension UpsellsViewController {
    private func noExtraUpsellChosenRow(with roomIndex: Int) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: NoUpsellChosenCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.noUpsellDescription.text = PILocalizedString("noExtrasChosen")
            cell.addUpsellButton.setTitle(PILocalizedString("Add extras"), for: .normal)
            cell.addButtonTitle = PILocalizedString("Add")
            cell.buttonAction = { [weak self] in
                self?.eventHandler?.selectedExtraUpsell(change: roomIndex)
            }
            return cell
        })
    }

    private func extraUpsellChosenRow(with roomIndex: Int, and summary: String) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: UpsellChosenSummaryCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.summary.text = summary
            cell.buttonAction = { [weak self] in
                self?.eventHandler?.selectedExtraUpsell(change: roomIndex)
            }
            return cell
        })
    }

    private func extraUpsellsHeadderSection() -> FormekaModelSection {
        let header = FormekaModelHeaderFooter(
            height: 68,
            viewSetup: { _, table in
                guard let header: TitleAndOptionalImageHeader = table.headerFooterView() else { return nil }
                header.contentView.backgroundColor = .BaseWhite
                header.backgroundColor = .BaseWhite
                header.titleLabel.font = .Heading1_Bold()
                header.titleLabel.textColor = .BasePurple
                header.titleLabel.accessibilityTraits.insert(.header)
                header.titleLabel.text = PILocalizedString("upsellsExtrasHeaderTitle")
                header.imageView.image = nil

                return header
            }
        )
        return FormekaModelSection(header: header, rows: [], footer: nil)
    }

    private func extraUpsellsSection(extraUpsellsRoomModels: [UpsellsRoomViewModel]) -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        for (roomIndex, extraUpsellsRoomModel) in extraUpsellsRoomModels.enumerated() {
            var rows = [FormekaModelRow]()

            guard extraUpsellsRoomModel.upsellItems?.count ?? 0 > 0 else { continue }

            if extraUpsellsRoomModels.count > 1 {
                rows.append(titleRow(with: extraUpsellsRoomModel.name, and: extraUpsellsRoomModel.description))
            }

            if extraUpsellsRoomModel.collapsed {
                if let upsellSummary = extraUpsellsRoomModel.upsellsSummary {
                    rows.append(extraUpsellChosenRow(with: roomIndex, and: upsellSummary))
                } else {
                    rows.append(noExtraUpsellChosenRow(with: roomIndex))
                }

                rows.append(separatorRow())
            } else if let upsellItemViewModels = extraUpsellsRoomModel.upsellItems {
                for (upsellIndex, upsellItemViewModel) in upsellItemViewModels.enumerated() {
                    let extraUpsellRow = extraUpsellRow(
                        for: upsellItemViewModel,
                        roomIndex: roomIndex,
                        upsellIndex: upsellIndex
                    )
                    rows.append(extraUpsellRow)

                    rows.append(separatorRow())
                }
            }

            sections.append(FormekaModelSection(header: nil, rows: rows, footer: nil))
        }

        return sections
    }

    private func extraUpsellRow(
        for optionViewModel: UpsellItemViewModel,
        roomIndex: Int = 0,
        upsellIndex: Int = 0
    ) -> FormekaModelRow {
        FormekaModelRow(
            cellSetup: { indexPath, _, table in
                guard let cell: ExtraUpsellCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.titleLabel.text = optionViewModel.title
                cell.descriptionLabel.attributedText = optionViewModel.description
                cell.priceLabel.attributedText = optionViewModel.costSummary
                cell.updateSwitchState(toggle: optionViewModel.selected)
                cell.toggled = { [weak self] toggledOn in
                    self?.eventHandler?.toggledExtraUpsell(toggle: toggledOn, upsellIndex: upsellIndex, in: roomIndex)
                }
                return cell
            }
        )
    }

    // TODO: AB Test to remove when completed - Sticky Extras CTA - Creates inline "View full breakdown" + "Continue" button section for variant
    // This section replaces the sticky footer when shouldShowStickyCTA = false
    private func continueButtonSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        let breakdownTextRow = FormekaModelRow(cellSetup: { [weak self] _, _, _ in
            guard let self = self else { return nil }
            let cell = UITableViewCell(style: .default, reuseIdentifier: "BreakdownTextCell")
            cell.selectionStyle = .none
            cell.backgroundColor = .BaseWhite
            cell.contentView.subviews.forEach { $0.removeFromSuperview() }
            let breakdownButton = UIButton(type: .system)
            breakdownButton.setTitle(PILocalizedString("footerViewBreakdown"), for: .normal)
            breakdownButton.setTitleColor(.BasePurple, for: .normal)
            breakdownButton.titleLabel?.font = .BodySmall()
            breakdownButton.contentHorizontalAlignment = .left
            breakdownButton.translatesAutoresizingMaskIntoConstraints = false
            breakdownButton.addTarget(self, action: #selector(breakdownButtonDidTap(_:)), for: .touchUpInside)
            cell.contentView.addSubview(breakdownButton)
            NSLayoutConstraint.activate([
                breakdownButton.leadingAnchor.constraint(equalTo: cell.contentView.leadingAnchor, constant: 16),
                breakdownButton.trailingAnchor.constraint(equalTo: cell.contentView.trailingAnchor, constant: -16),
                breakdownButton.topAnchor.constraint(equalTo: cell.contentView.topAnchor, constant: 16),
                breakdownButton.bottomAnchor.constraint(equalTo: cell.contentView.bottomAnchor, constant: -8),
                breakdownButton.heightAnchor.constraint(equalToConstant: 30)
            ])
            return cell
        })

        rows.append(breakdownTextRow)

        let continueButtonRow = FormekaModelRow(cellSetup: { [weak self] _, _, _ in
            guard let self = self else { return nil }
            let cell = UITableViewCell(style: .default, reuseIdentifier: "ContinueButtonCell")
            cell.selectionStyle = .none
            cell.backgroundColor = .BaseWhite
            cell.contentView.subviews.forEach { $0.removeFromSuperview() }
            let button = RoundedCornersButton(type: .system)
            button.setTitle(PILocalizedString("Continue"), for: .normal)
            button.accessibilityLabel = PILocalizedString("upsellsContinueToNextSteps")
            button.setTitleColor(.BaseWhite, for: .normal)
            button.backgroundColor = .Tint1
            button.titleLabel?.font = .Button1()
            button.cornerRadius = 4
            button.translatesAutoresizingMaskIntoConstraints = false
            button.addTarget(self, action: #selector(continueButtonDidTap(_:)), for: .touchUpInside)
            cell.contentView.addSubview(button)
            NSLayoutConstraint.activate([
                button.leadingAnchor.constraint(equalTo: cell.contentView.leadingAnchor, constant: 16),
                button.trailingAnchor.constraint(equalTo: cell.contentView.trailingAnchor, constant: -16),
                button.topAnchor.constraint(equalTo: cell.contentView.topAnchor, constant: 0),
                button.bottomAnchor.constraint(equalTo: cell.contentView.bottomAnchor, constant: -16),
                button.heightAnchor.constraint(equalToConstant: 44)
            ])
            return cell
        })
        rows.append(continueButtonRow)
        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }
}
