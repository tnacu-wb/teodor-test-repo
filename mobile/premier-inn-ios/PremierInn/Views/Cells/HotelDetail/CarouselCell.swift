//
//  CarouselCell.swift
//  PF
//
//  Created by Marcello Mascia on 27/08/2018.
//  Copyright © 2018 Whitbread PLC. All rights reserved.
//

import UIKit

struct RoundelDesign {
    var url: URL?
    var backgroundColor: UIColor?
    var foregroundColor: UIColor?
    var text: String?
    var shouldEmbolden: Bool
}

private extension CGFloat {
	static let margin: CGFloat = 0
    static let pageInsetWidth: CGFloat = 4
    static let normalWidthMaxCarouselImageWidth: CGFloat = 334
}

protocol CarouselCellDelegate: AnyObject {
    var cellMargin: CGFloat? { get }
    var sectionInsets: UIEdgeInsets? { get }

	func didSelectCarouselImage(at index: Int)
	func carouselCurrentIndexDidChange(index: Int)
}

class CarouselCell: UITableViewCell {
	weak var delegate: CarouselCellDelegate?

    var shouldShowBanner: Bool = false
    var bannerBackgroundColor: UIColor?
    var bannerImage: UIImage?

	@IBOutlet var collectionView: UICollectionView! {
		didSet {
			collectionView.registerCellForNib(with: CarouselImageCell.self)
			collectionView.accessibilityElementsHidden = false
		}
	}
    @IBOutlet weak var messagingFlagLabel: UIPaddingLabel! {
        didSet {
            messagingFlagLabel.font = UIFont.BodySmall_Semibold()
            messagingFlagLabel.layer.cornerRadius = 2
            messagingFlagLabel.layer.masksToBounds = true
        }
    }
    @IBOutlet weak var pageIndicatorLabel: UIPaddingLabel! {
        didSet {
            pageIndicatorLabel.setupLabel(
            	font: .Subtext_Medium(),
            	lineHeightMultiple: 1.01,
            	textAlignment: .center,
            	textColor: .BaseWhite,
            	numberOfLines: 1,
            	accessibilityIdentifier: "pageIndicatorLabel"
            )
            pageIndicatorLabel.layer.cornerRadius = 4
            pageIndicatorLabel.clipsToBounds = true
            pageIndicatorLabel.isAccessibilityElement = false
        }
    }

    var currentIndex: Int = 0
	var urls: [URL] = [] {
		didSet {
			if let flowLayout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout {
                let margin = delegate?.cellMargin ?? .margin

                flowLayout.minimumLineSpacing = margin
				flowLayout.minimumInteritemSpacing = 0
                flowLayout.sectionInset = delegate?.sectionInsets ?? UIEdgeInsets(
                	top: margin,
                	left: margin,
                	bottom: margin,
                	right: margin
                )
			}
            configurePageIndicator(currentPage: 1)
			collectionView.decelerationRate = .fast
			collectionView.reloadData()
		}
	}
    var roundelDesigns: [RoundelDesign] = []

	private func accessibilityLabelForImage(at index: Int) -> String {
		String.localizedStringWithFormat(
			PILocalizedString("carouselAccessibilityImagePosition"),
			index + 1,
			urls.count
		)
	}

	private func accessibilityHintForImage(at index: Int) -> String {
		guard urls.count > 1 else {
			return PILocalizedString("carouselAccessibilitySingleImageHint")
		}

		if index == 0 {
			return PILocalizedString("carouselAccessibilitySwipeRightHint")
		}

		if index == urls.count - 1 {
			return PILocalizedString("carouselAccessibilitySwipeLeftHint")
		}

		return PILocalizedString("carouselAccessibilitySwipeHint")
	}

    private func configurePageIndicator(currentPage: Int) {
        pageIndicatorLabel.text = "\(currentPage) / \(urls.count)"
	}
}

extension CarouselCell: UICollectionViewDelegateFlowLayout {
	func collectionView(
		_ collectionView: UICollectionView,
		layout collectionViewLayout: UICollectionViewLayout,
		sizeForItemAt indexPath: IndexPath
	) -> CGSize {
        let dy = delegate?.sectionInsets?.top ?? .margin

        let insetSize = collectionView.frame.insetBy(dx: .pageInsetWidth, dy: dy).size

        if traitCollection.horizontalSizeClass == .regular {
            return CGSize(width: .normalWidthMaxCarouselImageWidth, height: insetSize.height)
        }
		return insetSize
	}
}

extension CarouselCell: UICollectionViewDelegate, UICollectionViewDataSource {
	func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
		urls.count
	}

	func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
		guard let cell: CarouselImageCell = collectionView.dequeueCell(for: indexPath) else { return UICollectionViewCell() }

        cell.imageView?.clipsToBounds = true
		cell.isAccessibilityElement = true
        cell.accessibilityTraits = [.image, .button]
		cell.accessibilityLabel = accessibilityLabelForImage(at: indexPath.row)
		cell.accessibilityHint = accessibilityHintForImage(at: indexPath.row)

		return cell
	}

	func collectionView(
		_ collectionView: UICollectionView,
		willDisplay cell: UICollectionViewCell,
		forItemAt indexPath: IndexPath
	) {
		guard let cell = cell as? CarouselImageCell else { return }

        cell.bannerContainer.isHidden = true

        if indexPath.row == 0 && indexPath.section == 0 && shouldShowBanner {
            cell.bannerContainer.isHidden = false
            cell.bannerContainer.backgroundColor = bannerBackgroundColor
            cell.bannerImageView.image = bannerImage
        }

		let url = urls[indexPath.row]

		cell.imageView.image = nil
		cell.imageView.setImage(with: url, transition: true)

        cell.viewWithTag(420)?.removeFromSuperview()

        guard roundelDesigns.isNotEmpty else {
            return
        }

        guard roundelDesigns[indexPath.row].text != nil else {
            return
        }

        guard let text = roundelDesigns[indexPath.row].text else { return }
        guard let backgroundColor = roundelDesigns[indexPath.row].backgroundColor else { return }
        guard let foregroundColor = roundelDesigns[indexPath.row].foregroundColor else { return }

        let circledSquareLabel = CircledSquareLabel(
        	lengthAndWidth: 100,
        	position: CGPoint(x: 10, y: 10),
        	andText: text,
        	textColor: foregroundColor,
        	backgroundColor: backgroundColor,
        	andShouldEmboldenText: roundelDesigns[indexPath.row].shouldEmbolden
        )
        circledSquareLabel.tag = 420

        cell.addSubview(circledSquareLabel)
	}

	func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        // ideally this would only work for the Hotel (top) images carousel since the full screen image viewer always shows all the images
		delegate?.didSelectCarouselImage(at: indexPath.row)
	}
}

extension CarouselCell: UIScrollViewDelegate {
	func scrollViewWillEndDragging(
		_ scrollView: UIScrollView,
		withVelocity velocity: CGPoint,
		targetContentOffset: UnsafeMutablePointer<CGPoint>
	) {
        let margin = delegate?.cellMargin ?? .margin

        let pageWidth: CGFloat = {
            if traitCollection.horizontalSizeClass == .regular {
                return .normalWidthMaxCarouselImageWidth + margin
            }
            return scrollView.frame.insetBy(dx: .pageInsetWidth, dy: 0).size.width + margin
        }()
		let newPage: CGFloat = {
			if velocity.x == 0 {
				return floor((targetContentOffset.pointee.x - pageWidth / 2) / pageWidth) + 1
			}

			let result = CGFloat(velocity.x > 0 ? currentIndex + 1 : currentIndex - 1)

			return min(max(0, result), ceil(scrollView.contentSize.width / pageWidth) - 1)
		}()

		currentIndex = Int(newPage)
		delegate?.carouselCurrentIndexDidChange(index: currentIndex)
		targetContentOffset.pointee = CGPoint(x: CGFloat(newPage * pageWidth) - margin / 2, y: targetContentOffset.pointee.y)
	}

    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let currentIndex = scrollView.contentOffset.x / scrollView.frame.size.width
        let pageNumber = Int(ceil(currentIndex)) + 1

        configurePageIndicator(currentPage: pageNumber)
    }
}
