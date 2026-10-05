//
//  VenueFullyBookedCellModule.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 02/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

enum VenueFullyBookedCellModule {
    static let cellReuseIdentifier = String(describing: VenueFullyBookedCellView.self)

    static func build(
        collectionView: UICollectionView,
        indexPath: IndexPath,
        dataProvider: VenueFullyBookedCellViewModelDataProviding,
        onChangeDatesTap: @escaping () -> Void
    ) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(
            withReuseIdentifier: cellReuseIdentifier,
            for: indexPath
        )

        cell.contentConfiguration = UIHostingConfiguration {
            VenueFullyBookedCellView(
                dataProvider: dataProvider,
                onChangeDatesTap: onChangeDatesTap
            )
        }

        return cell
    }
}
