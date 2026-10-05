//
//  HotelDetailsView+Carousel.swift
//  PremierInn
//
//  Created by Nick Jones on 12/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

extension HotelDetailsViewController {
    func carouselSection(withCarouselViewModel carouselViewModel: CarouselViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        let carouselImageURLs = carouselViewModel.carouselRoundelDesigns
            .compactMap { $0.url?.sizedImageURL(withSize: .large) }

        if carouselImageURLs.isNotEmpty {
            rows.append(FormekaModelRow(
                tag: HotelDetailRow.hotelDetailCarouselCell.rawValue,
                cellSetup: { [unowned self] indexPath, _, table in
                guard let cell: CarouselCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.currentIndex = currentImageIndex
                cell.urls = carouselImageURLs
                cell.roundelDesigns = carouselViewModel.carouselRoundelDesigns
                cell.delegate = self
                cell.messagingFlagLabel.isHidden = true
                cell.shouldShowBanner = false

                cell.imageView?.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.hotelDetailsPageHotelImage

                if carouselViewModel.shouldShowBannerRubber {
                    setUpBannerRubber(with: cell)
                    return cell
                }

                if let messageFlagText = carouselViewModel.carouselMessageFlagText {
                    cell.messagingFlagLabel.text = messageFlagText
                    cell.messagingFlagLabel.textColor = carouselViewModel.carouselMessageFlagTextColor
                    cell.messagingFlagLabel.backgroundColor = carouselViewModel.carouselMessageFlagBackgroundColor
                    cell.messagingFlagLabel.isHidden = false
                    cell.messagingFlagLabel?.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails
                        .hotelDetailsNewHotelBanner
                }

                return cell
            }
            ))
        }

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func setUpBannerRubber(with cell: CarouselCell) {
        guard let viewModel = self.viewModel else { return }

        cell.shouldShowBanner = true

        cell.bannerBackgroundColor = viewModel.carouselViewModel.carouselBannerRubberBackgroundColour
        cell.bannerImage = viewModel.carouselViewModel.carouselBannerRubberImage
    }
}

extension HotelDetailsViewController: CarouselCellDelegate {
    var cellMargin: CGFloat? {
        0
    }

    var sectionInsets: UIEdgeInsets? {
        UIEdgeInsets.zero
    }

    func didSelectCarouselImage(at index: Int) {
        eventHandler.showCarousel(index: index, andDelegate: self)
    }

    func carouselCurrentIndexDidChange(index: Int) {
        // this changes the index for all carousels in the same view
        currentImageIndex = index
    }
}

extension HotelDetailsViewController: FullScreenImageViewerRouterDelegate {
    func fullscreenImageSetDidChangePicture(atIndex index: Int) {
        currentImageIndex = index
    }

    func fullscreenImageSetCloseButtonDidTap(viewController: UIViewController) {
        viewController.dismiss(animated: true)
    }
}
