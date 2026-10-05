//
//  HotelPhotoViewModel.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol HotelPhotoViewModelDelegate: AnyObject {
    var collectionView: UICollectionView { get }

    func currentImageDidChange(url: URL)
}

class HotelPhotoViewModel: NSObject {
    private var photoURLs: [URL]
    private let requestManager = RequestsManager()
    private var cellWidth = UIScreen.main.bounds.width
    private var infiniteSetupDone = false

    weak var delegate: HotelPhotoViewModelDelegate?

	deinit {
		cancelRequests()
	}

	init(urls: [URL]) {
        var infiniteUrls = urls
        if let first = urls.first, let last = urls.last {
            infiniteUrls.insert(last, at: 0)
            infiniteUrls.insert(first, at: urls.endIndex + 1)
        }

		self.photoURLs = infiniteUrls
	}

    func cancelRequests() {
        requestManager.cancelConnections()
    }
}

extension HotelPhotoViewModel: UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        photoURLs.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell: HotelPhotoCell = collectionView.dequeueCell(for: indexPath) else { return UICollectionViewCell() }

        cell.configure(photoURLs[indexPath.row])

        return cell
    }

    func collectionView(
        _ collectionView: UICollectionView,
        willDisplay cell: UICollectionViewCell,
        forItemAt indexPath: IndexPath
    ) {
        switch indexPath.item {
        case 0:
            if !infiniteSetupDone {
                infiniteSetupDone = true
                collectionView.scrollToItem(at: IndexPath(item: 1, section: 0), at: .left, animated: false)
                break
            }

        default:
            break
        }
    }
}

extension HotelPhotoViewModel: UICollectionViewDelegateFlowLayout {
    func collectionView(
        _ collectionView: UICollectionView,
        layout collectionViewLayout: UICollectionViewLayout,
        sizeForItemAt indexPath: IndexPath
    ) -> CGSize {
        CGSize(width: cellWidth, height: collectionView.frame.height)
    }
}

extension HotelPhotoViewModel: UIScrollViewDelegate {
    func scrollViewWillEndDragging(
        _ scrollView: UIScrollView,
        withVelocity velocity: CGPoint,
        targetContentOffset: UnsafeMutablePointer<CGPoint>
    ) {
        guard let collectionView = scrollView as? UICollectionView else { return }
        guard let flowLayout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout else { return }

        let pageWidth = collectionView.frame.width + flowLayout.minimumLineSpacing

        let currentOffset = scrollView.contentOffset.x
        let targetOffset = targetContentOffset.pointee.x
        var newTargetOffset: CGFloat = 0

        if targetOffset > currentOffset {
            newTargetOffset = ceil(currentOffset / pageWidth) * pageWidth
        } else {
            newTargetOffset = floor(currentOffset / pageWidth) * pageWidth
        }

        // Trim the value
        if newTargetOffset < 0 {
            newTargetOffset = 0
        } else if newTargetOffset > scrollView.contentSize.width {
            newTargetOffset = scrollView.contentSize.width
        }

        targetContentOffset.pointee.x = currentOffset

        scrollView.setContentOffset(CGPoint(x: newTargetOffset, y: 0), animated: true)
    }

    func scrollViewDidEndScrollingAnimation(_ scrollView: UIScrollView) {
        guard let collectionView = scrollView as? UICollectionView else { return }

        var center = collectionView.center
        center.x += collectionView.contentOffset.x

        guard let indexPath = collectionView.indexPathForItem(at: center) else { return }

        switch indexPath.item {
        case 0:
            collectionView.scrollToItem(at: IndexPath(item: photoURLs.endIndex - 2, section: 0), at: .right, animated: false)

        case photoURLs.count - 1:
            collectionView.scrollToItem(at: IndexPath(item: 1, section: 0), at: .left, animated: false)

        default:
            break
        }

        if indexPath.item < photoURLs.count {
            delegate?.currentImageDidChange(url: photoURLs[indexPath.item])
        }
    }
}
