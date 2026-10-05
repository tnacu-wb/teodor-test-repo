//
//  UICollectionViewLayout+Extensions.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 19/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

extension UICollectionViewLayout {
    /// Creates a standard vertical compositional layout with full-width items and dynamic height.
    ///
    /// This layout is based on `UICollectionViewCompositionalLayout` and is suitable for
    /// lists where each item should:
    /// - Occupy the full available width
    /// - Have a dynamic height determined by its content (e.g. SwiftUI via `UIHostingConfiguration` e.g. `VenueCellView`)
    /// - Maintain consistent spacing and section insets
    ///
    /// - Important:
    ///   - This layout relies on `.estimated` height to allow views to self-size.
    ///   - Avoid using `sizeForItemAt` with this layout, as it will conflict with self-sizing.
    ///   - Ensure views inside cells expand to full width (e.g. `.frame(maxWidth: .infinity)` in SwiftUI).
    ///   - Avoid dynamic height changes after initial layout (e.g. image resizing) to prevent scroll jitter.
    ///
    /// - SeeAlso:
    ///   - [Apple Docs: UICollectionViewCompositionalLayout](https://developer.apple.com/documentation/uikit/uicollectionviewcompositionallayout)
    ///   - [Apple Docs: NSCollectionLayoutSection](https://developer.apple.com/documentation/uikit/nscollectionlayoutsection)
    ///   - [Apple Docs: UIHostingConfiguration](https://developer.apple.com/documentation/swiftui/uihostingconfiguration)
    static func verticalDynamicLayout(
        itemSpacing: CGFloat = ViewConstants.Spacing.small,
        sectionInsets: NSDirectionalEdgeInsets = .init(
            top: ViewConstants.Spacing.small,
            leading: ViewConstants.Spacing.medium,
            bottom: ViewConstants.Spacing.small,
            trailing: ViewConstants.Spacing.medium
        ),
        footerHeight: CGFloat? = nil
    ) -> UICollectionViewLayout {
        let itemSize = NSCollectionLayoutSize(
            widthDimension: .fractionalWidth(1.0),
            heightDimension: .estimated(1) // Dynamic height useful for SwiftUI view cells
        )

        let item = NSCollectionLayoutItem(layoutSize: itemSize)

        let group = NSCollectionLayoutGroup.vertical(
            layoutSize: itemSize,
            subitems: [item]
        )

        let section = NSCollectionLayoutSection(group: group)

        section.interGroupSpacing = itemSpacing
        section.contentInsets = sectionInsets

        if let footerHeight {
            let footerSize = NSCollectionLayoutSize(
                widthDimension: .fractionalWidth(1.0),
                heightDimension: .absolute(footerHeight)
            )

            let footer = NSCollectionLayoutBoundarySupplementaryItem(
                layoutSize: footerSize,
                elementKind: UICollectionView.elementKindSectionFooter,
                alignment: .bottom
            )

            section.boundarySupplementaryItems = [footer]
        }

        return UICollectionViewCompositionalLayout(section: section)
    }
}
