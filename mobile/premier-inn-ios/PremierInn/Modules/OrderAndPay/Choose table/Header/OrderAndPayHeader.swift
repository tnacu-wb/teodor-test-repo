//
//  OrderAndPayHeader.swift
//  PremierInn
//
//  Created by Simon Antoine on 17/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

protocol OrderAndPayHeaderProtocol {
    func set(image: UIImage)
    func set(text: String)
    func text(hide: Bool)
    func searchBar(hide: Bool)
    func set(searchBarDelegate: UISearchBarDelegate)
    func isSearchActive() -> Bool
}

final class OrderAndPayHeader: UIView {
    @IBOutlet weak var imageViewBanner: UIImageView!
    @IBOutlet weak var label: UILabel!
    @IBOutlet private weak var contentView: UIView!
    @IBOutlet private weak var searchBar: UISearchBar!
}

extension OrderAndPayHeader: OrderAndPayHeaderProtocol {
    func isSearchActive() -> Bool {
        self.searchBar.text?.isNotEmpty ?? false
    }

    func set(searchBarDelegate: UISearchBarDelegate) {
        self.searchBar.delegate = searchBarDelegate
    }

    func searchBar(hide: Bool) {
        self.searchBar.isHidden = hide
    }

    func set(image: UIImage) {
        self.imageViewBanner.image = image
    }

    func set(text: String) {
        self.label.text = text
    }

    func text(hide: Bool) {
        self.label.isHidden = hide
    }
}
