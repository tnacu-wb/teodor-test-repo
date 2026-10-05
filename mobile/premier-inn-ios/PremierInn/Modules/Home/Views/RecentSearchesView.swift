//
//  RecentSearchesView.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/09/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

typealias IconOptions = (image: UIImage, tintColor: UIColor)

protocol RecentSearchesViewDelegate: AnyObject {
    func selectedRecentSearch(at index: Int)
}

enum SearchesComponentType {
    case recent
    case past
}
