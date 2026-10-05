//
//  CiolUpsellView+Formeka.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 09.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit
import Formeka

extension CiolUpsellViewController {
    func getCellSetup(id: String, isSelected: Bool) -> CiolUpsellCellSetup? {
        eventHandler?.getCellSetup(id: id, isSelected: isSelected)
    }

    func tableViewModel(with ciolUpsellViewModel: CiolUpsellViewModelProtocol) -> FormekaViewModel {
        var sections = [FormekaModelSection]()

        if ciolUpsellViewModel.shouldShowCloseoutMessage {
            let infoRowSection = iconInfoSection(
                text: PILocalizedString("ciolUpsellCloseoutMessage"),
                style: .info
            )
            sections.append(infoRowSection)
        }
        if !ciolUpsellViewModel.selectedUpsells.isEmpty {
            sections.append(selectedUpsellsSection(
                with: ciolUpsellViewModel.selectedUpsells,
                upsellsAddOnEnabled: ciolUpsellViewModel.upsellsAddOnEnabled
            ))
        }
        if !ciolUpsellViewModel.unselectedUpsells.isEmpty {
            let mealUpsells = ciolUpsellViewModel.unselectedUpsells.filter { $0.isFoodUpsell }
            let comfortUpsells = ciolUpsellViewModel.unselectedUpsells.filter { !$0.isFoodUpsell }

            if !mealUpsells.isEmpty {
                sections.append(availableUpsellsSection(
                    with: mealUpsells,
                    title: PILocalizedString("ciolUpsellFoodDiningTitle")))
            }
            if !comfortUpsells.isEmpty {
                sections.append(availableUpsellsSection(
                    with: comfortUpsells,
                    title: PILocalizedString("ciolUpsellComfortTitle")
                ))
            }
        } else if !ciolUpsellViewModel.upsellsAddOnEnabled {
            // Show informational Add-ons section when upsells cannot be added (e.g., PIBA CNP)
            sections.append(addonsInfoSection())
        }

        return FormekaViewModel(sections: sections)
    }

    func iconInfoSection(
        text: String,
        leadingPadding: CGFloat = ViewConstants.LegacyPadding.none,
        trailingPadding: CGFloat = ViewConstants.LegacyPadding.none,
        topPadding: CGFloat = ViewConstants.LegacyPadding.none,
        BottomPadding: CGFloat = ViewConstants.LegacyPadding.none,
        style: NotificationStyle
    ) -> FormekaModelSection {
        let infoRow = FormekaModelRow(
            cellSetup: { indexPath, _, table in
                guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.hiddenSeparatorLocations = [.top, .bottom]
                cell.leadingConstraint.constant = leadingPadding
                cell.trailingConstraint.constant = trailingPadding
                cell.topConstraint.constant = topPadding
                cell.bottomConstraint.constant = BottomPadding

                cell.content.textColor = .ColourDL1
                cell.content.text = text
                cell.content.font = .BodySmall()
                cell.content.backgroundColor = .clear

                cell.containerView.backgroundColor = style.background
                cell.containerView.layer.borderColor = style.tint.withAlphaComponent(0.4).cgColor
                cell.containerView.layer.cornerRadius = 4

                cell.icon.image = style.icon
                cell.icon.tintColor = style.tint

                cell.backgroundColor = .white
                return cell
            }
        )
        return FormekaModelSection(header: nil, rows: [infoRow], footer: nil)
    }

    private func availableUpsellsSection(
        with upsells: [CiolUpsellItemViewModelProtocol],
        title: String
    ) -> FormekaModelSection {
        let rows = upsells.map { upsell in
            FormekaModelRow(cellSetup: { [weak self] indexPath, _, table in
                guard let self,
                      let cell: CiolUpsellItemCell = table.dequeueCell(for: indexPath),
                      let setup = getCellSetup(id: upsell.id, isSelected: false) else { return UITableViewCell() }

                cell.configureAvailable(with: upsell, setup: setup)
                cell.selectionStyle = .none
                return cell
            }, didSelect: { [weak self] indexPath, _ in
                guard upsell.enabled else {
                    return
                }
                self?.tableView.deselectRow(at: indexPath, animated: true)

                self?.eventHandler?.handleUpsellsRowTapped(upsell)
            })
        }

        let header = FormekaModelHeaderFooter(height: 40, viewSetup: { _, _ in
            let headerView = UITableViewHeaderFooterView()
            let label = UILabel()
            label.text = title
            label.font = UIFont.Heading2_ExtraBold()
            label.textColor = .BaseBlack
            label.translatesAutoresizingMaskIntoConstraints = false
            label.accessibilityTraits.insert(.header)
            headerView.contentView.addSubview(label)
                        NSLayoutConstraint.activate([
                            label.leadingAnchor.constraint(equalTo: headerView.contentView.leadingAnchor, constant: 16),
                            label.trailingAnchor.constraint(equalTo: headerView.contentView.trailingAnchor, constant: -16),
                            label.centerYAnchor.constraint(equalTo: headerView.contentView.centerYAnchor)
                        ])

            return headerView
        })

        return FormekaModelSection(header: header, rows: rows, footer: nil)
    }

    private func addonsInfoSection() -> FormekaModelSection {
            let infoRow = FormekaModelRow(cellSetup: { _, _, _ in
                let cell = UITableViewCell(style: .default, reuseIdentifier: "InfoCell")
                cell.textLabel?.text = nil
                // Create custom label with proper constraints
                let label = UILabel()
                label.text = PILocalizedString("ciolAddonsInfoText")
                label.font = UIFont.Body()
                label.textColor = .BaseBlack
                label.numberOfLines = 0
                label.lineBreakMode = .byWordWrapping
                label.translatesAutoresizingMaskIntoConstraints = false
                cell.contentView.addSubview(label)
                NSLayoutConstraint.activate([
                    label.topAnchor.constraint(equalTo: cell.contentView.topAnchor, constant: 0),
                    label.leadingAnchor.constraint(equalTo: cell.contentView.leadingAnchor, constant: 16),
                    label.trailingAnchor.constraint(equalTo: cell.contentView.trailingAnchor, constant: -16),
                    label.bottomAnchor.constraint(equalTo: cell.contentView.bottomAnchor, constant: -16)
                ])
                // Configure cell
                cell.selectionStyle = .none
                cell.backgroundColor = .clear
                return cell
            }, didSelect: nil)
            let header = FormekaModelHeaderFooter(height: 40, viewSetup: { _, _ in
                let headerView = UITableViewHeaderFooterView()
                let label = UILabel()
                label.text = PILocalizedString("ciolUpsellFoodDiningTitle")
                label.font = UIFont.Heading2_ExtraBold()
                label.textColor = .BaseBlack
                label.translatesAutoresizingMaskIntoConstraints = false

                headerView.contentView.addSubview(label)
                NSLayoutConstraint.activate([
                    label.leadingAnchor.constraint(equalTo: headerView.contentView.leadingAnchor, constant: 16),
                    label.trailingAnchor.constraint(equalTo: headerView.contentView.trailingAnchor, constant: -16),
                    label.centerYAnchor.constraint(equalTo: headerView.contentView.centerYAnchor)
                ])

                return headerView
            })

            return FormekaModelSection(header: header, rows: [infoRow], footer: nil)
        }

        private func selectedUpsellsSection(
            with upsells: [CiolUpsellItemViewModelProtocol],
            upsellsAddOnEnabled: Bool
        ) -> FormekaModelSection {
        let rows = upsells.map { upsell in
            FormekaModelRow(cellSetup: { [weak self] indexPath, _, table in
                guard let self,
                      let cell: CiolUpsellItemCell = table.dequeueCell(for: indexPath),
                      let setup = getCellSetup(id: upsell.id, isSelected: true) else { return UITableViewCell() }
                cell.configureSelected(with: upsell, setup: setup)
                cell.selectionStyle = .none
                return cell
            }, didSelect: { [weak self] indexPath, _ in
                self?.tableView.deselectRow(at: indexPath, animated: true)
                // Disable tap for pre-booked items - they cannot be modified during check-in
                guard !upsell.isPrebooked else {
                    return
                }
                // Disable tap for PIBA bookings where upsells cannot be modified
                guard upsellsAddOnEnabled else {
                    return
                }
                guard upsell.enabled else {
                    return
                }
                self?.eventHandler?.handleUpsellsRowTapped(upsell)
            })
        }

        let header = FormekaModelHeaderFooter(height: 40, viewSetup: { _, _ in
            let headerView = UITableViewHeaderFooterView()
            let label = UILabel()
            label.text = "\(PILocalizedString("ciolUpsellSelectedTitle"))(\(upsells.count))"
            label.font = UIFont.Heading2_ExtraBold()
            label.textColor = .BaseBlack
            label.translatesAutoresizingMaskIntoConstraints = false
            label.accessibilityTraits.insert(.header)

            headerView.contentView.addSubview(label)
                        NSLayoutConstraint.activate([
                            label.leadingAnchor.constraint(equalTo: headerView.contentView.leadingAnchor, constant: 16),
                            label.trailingAnchor.constraint(equalTo: headerView.contentView.trailingAnchor, constant: -16),
                            label.centerYAnchor.constraint(equalTo: headerView.contentView.centerYAnchor)
                        ])

            return headerView
        })

        return FormekaModelSection(header: header, rows: rows, footer: nil)
    }
}
