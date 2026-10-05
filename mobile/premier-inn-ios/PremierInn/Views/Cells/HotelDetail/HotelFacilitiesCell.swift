//
//  HotelFacilitiesCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 17/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class HotelFacilitiesCell: UITableViewCell {
    @IBOutlet weak var collectionView: UICollectionView!

    var viewModel: HotelFacilitiesViewModel? {
        didSet {
            collectionView.registerCellForNib(with: HotelFacilityCell.self)

            if viewModel != nil {
                collectionView.reloadData()
            }
        }
    }

    private let requestManager = RequestsManager()

    override func layoutSubviews() {
        super.layoutSubviews()

        if let flowLayout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout {
            flowLayout.itemSize = CGSize(width: 24, height: 24)
            flowLayout.minimumLineSpacing = 8
            flowLayout.minimumInteritemSpacing = 8
        }
    }
}

class HotelFacilitiesViewModel {
    private(set) var facilities: [Facility]

    init(facilities: [Facility]) {
        self.facilities = facilities
    }
}

extension HotelFacilitiesCell: UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        viewModel?.facilities.count ?? 0
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell: HotelFacilityCell = collectionView.dequeueCell(for: indexPath) else {
            return UICollectionViewCell()
        }
        cell.icon.image = nil

        if let facility = viewModel?.facilities[indexPath.row] {
            cell.icon.image = UIImage(named: facility.code) ?? UIImage(named: "UNKNOWN")
            cell.icon.tintColor = .BasePurple
        }

        return cell
    }
}
