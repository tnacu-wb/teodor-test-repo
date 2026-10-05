//
//  VenueCellModule.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 18/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

enum VenueCellModule {
    static let cellReuseIdentifier = String(describing: VenueCellView.self)

    static func build(
        collectionView: UICollectionView,
        indexPath: IndexPath,
        dataProvider: VenueCellViewModelDataProviding,
        onImageCarouselTap: @escaping () -> Void
    ) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(
            withReuseIdentifier: cellReuseIdentifier,
            for: indexPath
        )

        cell.contentConfiguration = UIHostingConfiguration {
            VenueCellView(
                index: indexPath.row,
                dataProvider: dataProvider,
                onImageCarouselTap: onImageCarouselTap
            )
        }

        return cell
    }
}
