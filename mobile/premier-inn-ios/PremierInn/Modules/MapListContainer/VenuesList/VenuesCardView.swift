//
//  VenuesCardView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 13/04/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class VenuesCardsViewController: UIViewController {
	var presenter: ListPresenterProtocol?

	@IBOutlet weak var collectionView: UICollectionView! {
		didSet {
			collectionView.registerCellForNib(with: VenueCardCell.self)

			if let layout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout {
				layout.sectionInset = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: Constants.cardCellSize.width.halved)
				layout.itemSize = Constants.cardCellSize
			}
		}
	}

	deinit {
        print("DEINIT: \(self)")
	}

	override func viewDidLoad() {
        super.viewDidLoad()

		view.backgroundColor = .clear

        presenter?.listViewIsReady()
    }

	// MARK: - Utilities
	private func getLandingHorizontalOffset(
		targetOffset: CGFloat,
		collectionView: UICollectionView,
		layout: UICollectionViewFlowLayout
	) -> CGFloat {
		let pageWidth: CGFloat = Constants.cardCellSize.width + layout.minimumLineSpacing
		let currentOffset = collectionView.contentOffset.x + layout.sectionInset.left
		var newTargetOffset: CGFloat {
			if targetOffset > currentOffset {
				return ceil(currentOffset / pageWidth) * pageWidth
			}

			return floor(currentOffset / pageWidth) * pageWidth
		}

		return clamp(value: newTargetOffset, lower: 0, upper: collectionView.contentSize.width)
	}

	private func indexPathForScrolledItem(collectionView: UICollectionView) -> IndexPath? {
		let size = Constants.cardCellSize
		let point = CGPoint(x: collectionView.contentOffset.x + size.width * 0.5, y: size.height * 0.5)

		return collectionView.indexPathForItem(at: point)
	}
}

extension VenuesCardsViewController: ListViewProtocol {
	func reload() {
		if collectionView != nil {
			collectionView.reloadData()
		}
	}

	func mapWillGoFullScreen() {
	}

	func mapDidGoFullScreen() {
	}

	func scrollToItemAt(indexPath: IndexPath, andSelect selectCell: Bool?) {
		// Animating the offset this way will not trigger scrollview methods
        UIView.animate(
        	withDuration: .ocd,
        	animations: {
                self.collectionView.contentOffset.x = (Constants.cardCellSize.width + 10) * CGFloat(indexPath.item)
            },
        	completion: { _ in
            if selectCell ?? false {
                self.selectCellAt(indexPath: indexPath)
            }
		}
        )
	}

	func willReturnToListView() {
	}

	func selectCellAt(indexPath: IndexPath) {
		collectionView.selectItem(at: indexPath, animated: true, scrollPosition: [])
	}

	func showErrorMessage(message: String?, bottomConstraint: CGFloat) {
	}

	func removeErrorMessage() {
	}

    func selectedListCell() -> VenueCell? {
        guard let indexPath = collectionView.indexPathsForSelectedItems?.first else { return nil }

        return collectionView.cellForItem(at: indexPath) as? VenueCell
    }
}

extension VenuesCardsViewController: UICollectionViewDelegate, UICollectionViewDataSource {
	func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        collectionView.layoutIfNeeded()

		return presenter?.numberOfVenues() ?? 0
	}

	func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
		if let cell: VenueCardCell = collectionView.dequeueCell(for: indexPath) {
			return cell
		}

		return UICollectionViewCell()
	}

	func collectionView(
		_ collectionView: UICollectionView,
		willDisplay cell: UICollectionViewCell,
		forItemAt indexPath: IndexPath
	) {
		guard let hotel = presenter?.hotel(for: indexPath) else { return }

		(cell as? VenueCardCell)?.setup(with: hotel)
	}

	func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
		presenter?.didSelectItem(at: indexPath, mapVisible: true)
	}
}

extension VenuesCardsViewController: UIScrollViewDelegate {
	func scrollViewWillEndDragging(
		_ scrollView: UIScrollView,
		withVelocity velocity: CGPoint,
		targetContentOffset: UnsafeMutablePointer<CGPoint>
	) {
		guard let collectionView = scrollView as? UICollectionView else { return }
		guard let flowLayout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout else { return }

		targetContentOffset.pointee.x = getLandingHorizontalOffset(
			targetOffset: targetContentOffset.pointee.x,
			collectionView: collectionView,
			layout: flowLayout
		)
	}

	func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
		guard let collectionView = scrollView as? UICollectionView else { return }
		guard let indexPath = indexPathForScrolledItem(collectionView: collectionView) else { return }

		presenter?.listDidScroll(to: indexPath)
	}
}
