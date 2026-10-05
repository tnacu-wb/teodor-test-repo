//
//  PayWithNewCardView.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/03/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

class PayWithNewCardView: CreditCardsListCell {
    @IBOutlet weak var methodType: UILabel! {
        didSet {
            methodType.font = .Body_Semibold()
        }
    }
    @IBOutlet override weak var collectionView: UICollectionView! {
       didSet {
        collectionView.registerCellForNib(with: CreditCardImageCell.self)
        collectionView.delegate = self
        collectionView.dataSource = self
       }
    }
    @IBOutlet weak var radioButton: RadioButtonView!
    @IBOutlet weak var separatorView: UIView! {
        didSet {
            separatorView.backgroundColor = .greyBorder
        }
    }

    var action: (() -> Void)? {
        didSet {
            addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(userDidTapOnView)))
        }
    }

    @objc private func userDidTapOnView() {
        action?()
    }
}
