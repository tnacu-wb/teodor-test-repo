//
//  CreditCardsListCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 16/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import Formeka

class CreditCardsListCell: SimpleSeparatorsCell {
    @IBOutlet weak var collectionView: UICollectionView! {
        didSet {
            collectionView.registerCellForNib(with: CreditCardImageCell.self)
        }
    }
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.text = PILocalizedString("acceptedCardsTitle", comment: "Credit card list accepted cards title")
            titleLabel.textColor = .ColourDL1
            titleLabel.font = UIFont.BodySmall()
        }
    }

    var cardUrls: [URL]? {
        didSet {
            collectionView.reloadData()
        }
    }
}

extension CreditCardsListCell: UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        cardUrls?.count ?? 0
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell: CreditCardImageCell = collectionView.dequeueCell(for: indexPath)
            else { return UICollectionViewCell() }

        cell.imageView.image = nil

        if let url = cardUrls?[indexPath.row] {
            cell.imageView.setImage(with: url, transition: true) { error in
                if error != nil {
                    cell.imageView.image = UIImage(named: "imageLoadError")
                }
            }
        } else {
            cell.imageView.image = nil
        }

        return cell
    }
}
