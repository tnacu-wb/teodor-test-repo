//
//  CardDetailsRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 16/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork

// TODO: - Remove logic pertaining to this as part of next cleanup ticket

protocol CardDetailsRouterDelegate: AnyObject {
    func updatedStoredCard()
    func newSavedCard()
}
