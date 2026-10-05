//
//  HotelDetailsView+FoodAndRestaurants.swift
//  PremierInn
//
//  Created by Nick Jones on 21/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension HotelDetailsViewController {
    func foodOptionsAndRestaurantSection(with viewModel: RestaurantAndFoodViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        if let disclaimer = viewModel.restaurantAndFoodCellViewModel.restaurantDisclaimer, disclaimer.isNotEmpty {
            rows.append(FormekaModelRow(cellSetup: { (indexPath, _, table) -> UITableViewCell? in
                guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.restaurantDisclaimer

                cell.topConstraint.constant = 0
                cell.hiddenSeparatorLocations = [.top, .bottom]
                cell.icon.image = #imageLiteral(resourceName: "UNKNOWN")
                cell.icon.tintColor = UIColor.sea

                cell.content.setupLabel(
                    text: disclaimer,
                    font: .BodySmall(),
                    lineHeightMultiple: 1.15,
                    textColor: .TintD1,
                    accessibilityIdentifier: "content"
                )

                cell.containerView.backgroundColor = .clear
                cell.containerView.layer.borderWidth = 1
                cell.containerView.layer.cornerRadius = 3
                cell.containerView.layer.borderColor = UIColor.TintL3.cgColor

                return cell
            }))
        }

        if viewModel.imageURLs.isNotEmpty {
            rows.append(FormekaModelRow(
                tag: HotelDetailRow.hotelRestaurantPhotosCell.rawValue,
                cellSetup: { indexPath, _, table in
                guard let cell: CarouselCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.urls = viewModel.imageURLs
                cell.roundelDesigns = []
                cell.messagingFlagLabel.isHidden = true
                cell.shouldShowBanner = false
                cell.bannerBackgroundColor = nil
                cell.bannerImage = nil

                return cell
            }
            ))
        }

        rows.append(restaurantInfoRow(with: viewModel.restaurantAndFoodCellViewModel))

        rows.append(foodContentRow(with: viewModel.foodContentViewModels))

        return FormekaModelSection(
            header: titleHeader(
                title: PILocalizedString(
                    "hotelDetailsRestaurantSectionTitle",
                    comment: "Hotel details: restaurant section title"
                ),
                font: .Heading2_Bold(),
                andColour: .TintD1,
                accessibilityIdentifier: AccessibilityIdentifiers.HotelDetails
                                                       .restaurantAndBarHeader
            ),
            rows: rows,
            footer: nil
        )
    }

    func restaurantInfoRow(with viewModel: RestaurantCellViewModel) -> FormekaModelRow {
        FormekaModelRow(
            tag: HotelDetailRow.foodOptionsAndRestaurantSegmentsCell.rawValue,
            cellSetup: { [unowned self] indexPath, _, table in
                guard let cell: HotelInformationSegmentsCell = table.dequeueCell(for: indexPath) else { return nil }

                var segments = [HotelInformationSegment(title: viewModel.restaurantTitle, content: "")]
                var selectedIndex = currentFoodSectionIndex

                if viewModel.restaurantFoodContentAvailable {
                    segments.insert(
                        HotelInformationSegment(title: PILocalizedString(
                            "hotelDetailsFoodOptions",
                            comment: "Hotel details: food options title"
                        ), content: "hello"),
                        at: 0
                    )
                } else {
                    selectedIndex = 0
                }

                cell.selectorData = SegmentSelectorData(segments: segments, selectedIndex: selectedIndex)
                cell.updateUI(
                    backgroundColor: viewModel.backgroundColour,
                    selectorColor: viewModel.selectorColor,
                    normalTextAttributes: viewModel.normalTextAttributes,
                    selectedTextAttributes: viewModel.selectedTextAttributes
                )

                cell.segmentControl.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.foodSegmentedControler

                cell.delegate = self

                return cell
            },
            cellWillDisplay: { cell, _, _ in
                if let segmentCell = cell as? HotelInformationSegmentsCell {
                    segmentCell.segmentControlValueDidChange(cell)
                }
        }
        )
    }

    func foodContentRow(with viewModels: [FoodContentViewModel]) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.foodContentCell.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FoodContentCell = table.dequeueCell(for: indexPath) else { return nil }

            let foodSectionIndex = self.currentFoodSectionIndex
            guard viewModels.indices.contains(foodSectionIndex) else { return cell }

            cell.foodContentStackView.subviews.forEach { $0.removeFromSuperview() }
            for viewModel in viewModels where viewModel.foodContentSectionIndex.rawValue == self.currentFoodSectionIndex {
                let restaurantFoodView = RestaurantBreakfastSectionView()
                restaurantFoodView.configure(with: viewModel)
                cell.foodContentStackView.addArrangedSubview(restaurantFoodView)
            }
            return cell
        })
    }

    func foodSection(with foodInfo: [FoodInfo]) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        for info in foodInfo {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.content.text = info.title
                cell.content.font = UIFont.Heading4_Semibold()
                cell.content.textColor = UIColor.TintD1
                cell.content.textAlignment = .left
                cell.padding = UIEdgeInsets(top: 0, left: 20, bottom: 10, right: 20)

                return cell
            }))

            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.content.text = info.body
                cell.content.font = UIFont.Body()
                cell.content.textColor = .TintD1
                cell.content.textAlignment = .left
                cell.padding = UIEdgeInsets(top: 0, left: 20, bottom: 15, right: 20)

                return cell
            }))
        }

        return FormekaModelSection(
            header: titleHeader(title: PILocalizedString(
                "hotelDetailsFoodOptions",
                comment: "Hotel details: food options title"
            )),
            rows: rows,
            footer: simpleFooterView(backgroundColor: .whiteTwo)
        )
    }

    func noFoodSection() -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.text = PILocalizedString(
                "hotelDetailsNoBreakfastTitle",
                comment: "Hotel details: no breakfast available title"
            )
            cell.content.font = UIFont.Heading4_Semibold()
            cell.content.textColor = UIColor.TintD1
            cell.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.noPremierInnBreakfastDetails
            cell.padding = UIEdgeInsets(top: 0, left: 20, bottom: 10, right: 20)

            return cell
        }))

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.text = PILocalizedString(
                "hotelDetailsNoBreakfastMessage",
                comment: "Hotel details: no breakfast available message"
            )
            cell.content.font = UIFont.Body()
            cell.content.textColor = UIColor.TintD1
            cell.padding = UIEdgeInsets(top: 0, left: 20, bottom: 15, right: 20)

            return cell
        }))

        return FormekaModelSection(
            header: titleHeader(title: PILocalizedString(
                "hotelDetailsRestaurantSectionTitle",
                comment: "Hotel details: restaurant section title"
            ), accessibilityIdentifier: AccessibilityIdentifiers.HotelDetails.restaurantAndBarHeader),
            rows: rows,
            footer: simpleFooterView(backgroundColor: .whiteTwo)
        )
    }
}
