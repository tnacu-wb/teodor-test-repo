//
//  PhotoPageContainerView.swift
//  PremierInn
//
//  Created by Freddie Parks on 03/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol PhotoPageContainerViewProtocol: AnyObject {
    func tappedPhotoContainer(atIndex index: Int)
    func swipedPhotoContainer(atIndex index: Int)
}

protocol PhotoInteractionDelegate: AnyObject {
    func tappedPhoto()
}

class PhotoPageContainerView: UIView {
    private var photoPageViewController: PhotoPageViewController

    weak var delegate: PhotoPageContainerViewProtocol?

    override init(frame: CGRect) {
        photoPageViewController = PhotoPageViewController(controllers: [], startIndex: 0)

        super.init(frame: frame)

        setup()
    }

    required init?(coder aDecoder: NSCoder) {
        photoPageViewController = PhotoPageViewController(controllers: [], startIndex: 0)

        super.init(coder: aDecoder)

        setup()
    }

    private func setup() {
        photoPageViewController.view.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        photoPageViewController.view.frame = bounds
        photoPageViewController.carouselDelegate = self
        addSubview(photoPageViewController.view)
    }

    func update(
        with roundelDesigns: [RoundelDesign],
        slideBackgroundColor: UIColor?,
        startIndex: Int,
        imageSize: URLImageSize
    ) {
        var index = 1

        photoPageViewController.controllers = roundelDesigns.map { roundelDesign -> HotelPhotoViewController in
            let controller = HotelPhotoViewController(withImageURL: roundelDesign.url?.sizedImageURL(withSize: imageSize))
            controller.view.backgroundColor = slideBackgroundColor
            controller.interactionDelegate = self
            controller.imageView.isAccessibilityElement = true
            controller.imageView.accessibilityIdentifier = "hotelImage\(index)"

            guard let text = roundelDesign.text else { return controller }
            guard let backgroundColor = roundelDesign.backgroundColor else { return controller }
            guard let foregroundColor = roundelDesign.foregroundColor else { return controller }

            let circledSquareLabel = CircledSquareLabel(
                lengthAndWidth: 100,
                position: CGPoint(x: 10, y: (controller.imageView.frame.size.height / 2) - 100),
                andText: text,
                textColor: foregroundColor,
                backgroundColor: backgroundColor,
                andShouldEmboldenText: roundelDesign.shouldEmbolden
            )

            controller.imageView.addSubview(circledSquareLabel)
            controller.roundel = circledSquareLabel

            index += 1

            return controller
        }

        photoPageViewController.setCurrentPageIndex(startIndex)
    }
}

extension PhotoPageContainerView: PhotoPageViewControllerDelegate {
    func photoPageViewControllerCurrentPhotoDidChange(currentIndex: Int) {
        delegate?.swipedPhotoContainer(atIndex: currentIndex)
    }
}

extension PhotoPageContainerView: PhotoInteractionDelegate {
    func tappedPhoto() {
        delegate?.tappedPhotoContainer(atIndex: photoPageViewController.currentIndex)
    }
}
