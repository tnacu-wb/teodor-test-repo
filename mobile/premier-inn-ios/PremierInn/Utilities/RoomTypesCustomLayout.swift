//
//  RoomTypesCustomLayout.swift
//  PremierInn
//
//  Created by Santa Gurung on 25/02/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import UIKit

class RoomTypesCustomLayout: UICollectionViewLayout {
    private var itemAttributes = [UICollectionViewLayoutAttributes]()
    private var contentHeight: CGFloat = 0
    private var contentWidth: CGFloat = 0
    private let itemSpacing: CGFloat = 16
    private let peekWidth: CGFloat = 20

    override var collectionViewContentSize: CGSize {
        CGSize(width: contentWidth, height: contentHeight)
    }

    override func prepare() {
        guard let collectionView = collectionView else { return }

        let itemsCount = collectionView.numberOfItems(inSection: 0)
        let height = collectionView.frame.height
        let width = collectionView.frame.width - (2 * itemSpacing) - peekWidth

        var xOffset: CGFloat = itemSpacing
        let yOffset: CGFloat = 0

        itemAttributes.removeAll()

        if itemsCount == 1 {
            xOffset = (collectionView.frame.width - width) / 2
            contentWidth = collectionView.frame.width
        }

        for item in 0..<itemsCount {
            let indexPath = IndexPath(item: item, section: 0)

            let frame = CGRect(
                x: xOffset,
                y: yOffset,
                width: width,
                height: height
            )

            let attributes = UICollectionViewLayoutAttributes(forCellWith: indexPath)
            attributes.frame = frame
            itemAttributes.append(attributes)

            contentWidth = frame.maxX + itemSpacing
            contentHeight = height
            xOffset += width + itemSpacing
        }
    }

    override func layoutAttributesForItem(at indexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
        itemAttributes[indexPath.item]
    }

    override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        var visibleLayoutAttributes: [UICollectionViewLayoutAttributes] = []

        for attributes in itemAttributes where attributes.frame.intersects(rect) {
            visibleLayoutAttributes.append(attributes)
        }
        return visibleLayoutAttributes
    }
}
