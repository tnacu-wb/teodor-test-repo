//
//  HotelDetailsView+Extensions.swift
//  PremierInn
//
//  Created by Nick Jones on 26/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import UIKit

private enum HotelDetailsAnimationError: LocalizedError {
    case numberOfPointBeforeAnimationLowerThanZero
    case scrollViewTooShort(String)

    var errorDescription: String? { String(describing: self) }
}

extension HotelDetailsViewController: FormekaViewModelDelegate {
    func scrollViewDidScroll(scrollView: UIScrollView) {
        if UIDevice.current.userInterfaceIdiom == .pad { return }

        let titleOffset: CGFloat = {
            guard let hotelNameCell: FlexibleTextContentCell = tableViewModel?.cell(
                forRowNamed: HotelDetailRow.hotelDetailNameAddressCell.rawValue,
                table: tableView
            ) else { return 0 }
            let hotelNameVerticalPosition = hotelNameCell.convert(hotelNameCell.content.frame.origin, to: nil).y

            guard let navigationBar = navigationController?.navigationBar,
                  let navigationBarContainer = navigationBar.superview else { return 0 }
            let navigationBarBottomPosition = navigationBarContainer.convert(
                CGPoint(x: 0, y: navigationBar.frame.maxY),
                to: nil
            ).y

            return ((hotelNameVerticalPosition - navigationBarBottomPosition - 12) + navigationBar.frame.height)
        }()

        titleView.subviews.first(where: { $0 is UILabel })?.frame.origin.y = titleOffset >= 0 ? titleOffset : 0
    }

    func stickyHeader(scrollView: UIScrollView) {
        guard let imageView = topSectionImageView else { return }

        if scrollView.contentOffset.y < 0 {
            if imageView.superview == nil {
                navBarAnimationHelper.cacheTopImageHeight = imageView.frame.height
                view.insert(imageView, .above(tableView))
            }

            imageView.frame.size.height = navBarAnimationHelper.cacheTopImageHeight + abs(scrollView.contentOffset.y)
        } else {
            if imageView.superview != nil {
                imageView.removeFromSuperview()
                navBarAnimationHelper.cachedTopImageView = nil
            }
        }
    }

    func continueButtonScroll(scrollView: UIScrollView) {
        let viewHeight: CGFloat = continueView.frame.height

        guard let indexPath = tableViewModel?.indexPath(forRowNamed: HotelDetailRow.rateCell.rawValue) else { return }

        let rect = tableView.rectForRow(at: indexPath)

        let threshold: CGFloat = rect.origin.y

        guard presenter.continueViewCanShowAtBottom == true else {
            continueViewTopConstraint.constant = 0
            return
        }

        guard scrollView.contentOffset.y > threshold else {
            continueViewTopConstraint.constant = 0
            return
        }

        let verticalOffset = (scrollView.contentOffset.y - threshold) / viewHeight
        let progress = (0 ... 1).clamp(verticalOffset * 0.7) // 0.7 is an arbitrary percentage to simulate parallax effect...

        let bottom = linearInterpolation(start: 0, end: viewHeight, percent: progress)

        continueViewTopConstraint.constant = bottom
    }

    private var topSectionImageView: UIImageView? {
        guard let cachedTopImageView = navBarAnimationHelper.cachedTopImageView else {
            let indexPath = IndexPath(row: 0, section: 0)

            guard let cell = tableView.cellForRow(at: indexPath) else { return nil }

            let frame = cell.frame

            UIGraphicsBeginImageContextWithOptions(cell.frame.size, false, 0)
            cell.drawHierarchy(in: frame, afterScreenUpdates: false)
            let grabbedImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()

            guard let image = grabbedImage else { return nil }

            var restAbsoluteRect = tableView.convert(cell.frame, to: view.superview)
            restAbsoluteRect.origin.y = 0

            let imageView = UIImageView(image: image)
            imageView.frame = restAbsoluteRect
            imageView.clipsToBounds = true
            imageView.contentMode = .scaleAspectFill

            navBarAnimationHelper.cachedTopImageView = imageView

            return imageView
        }

        return cachedTopImageView
    }


    // MARK: Animation progress


    private func animationProgress(
        _ scrollView: UIScrollView,
        waitBeforeAnimation: CGFloat,
        animationLength: CGFloat
    ) throws -> CGFloat {
        if waitBeforeAnimation <= 0 { throw HotelDetailsAnimationError.numberOfPointBeforeAnimationLowerThanZero }

        let contentHeightNeeded = scrollView.frame.height + waitBeforeAnimation + animationLength

        if scrollView.contentSize
           .height <
           contentHeightNeeded {
            throw HotelDetailsAnimationError
            .scrollViewTooShort("Need \(contentHeightNeeded - scrollView.contentSize.height) more points") }

        let currentVerticalOffset = scrollView.contentOffset.y - waitBeforeAnimation
        let percentageVerticalOffset = currentVerticalOffset / animationLength

        let progress = (0 ... 1).clamp(percentageVerticalOffset)

        return progress
    }

    private func buttonTintColorWithProgress(_ progress: CGFloat) -> UIColor {
        let red = cachedRgbaForPIPurple.red
        let green = cachedRgbaForPIPurple.green
        let blue = cachedRgbaForPIPurple.blue

        let finalRed: CGFloat = 1
        let finalGreen: CGFloat = 1
        let finalBlue: CGFloat = 1

        let newRed = linearInterpolation(start: red, end: finalRed, percent: progress)
        let newGreen = linearInterpolation(start: green, end: finalGreen, percent: progress)
        let newBlue = linearInterpolation(start: blue, end: finalBlue, percent: progress)

        return UIColor(red: newRed, green: newGreen, blue: newBlue, alpha: 1)
    }


    // MARK: Animations


    func navigationBarAnimationWithProgress(_ progress: CGFloat) {
        // Fake navigation bar
        fakeNavigationBarTopConstraint.constant = linearInterpolation(
            start: -navBarAnimationHelper.pointsBeforeTriggeringAnimation,
            end: navBarAnimationHelper.pointsBeforeTriggeringAnimation - fakeNavigationBar.frame.height,
            percent: progress
        )

        // Status bar
        shouldShowStatusBar = progress == 1
    }

    func backButtonAnimationWithProgress(_ progress: CGFloat) {
        // Background color
        backButton.backgroundColor = UIColor.BaseWhite.withAlphaComponent(1 - progress)

        // Tint color
        backButton.tintColor = buttonTintColorWithProgress(progress)

        // Top constraint
        backButtonLeftConstraint.constant = linearInterpolation(start: 10, end: -11, percent: progress)

        backButtonLabel.alpha = 1 * progress
        backButtonLabel.setTitleColor(backButton.tintColor, for: .normal)
    }

    func shareButtonAnimationWithProgress(_ progress: CGFloat) {
        // To be implemented
    }
}
