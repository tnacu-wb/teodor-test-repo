//
//  HasNavigationBarWithoutSeparator.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 23.05.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//


protocol HasNavigationBarWithoutSeparator {}

// Define modal controllers that should not have a navigation bar separator
extension UpsellsViewController: HasNavigationBarWithoutSeparator {}


protocol HasThemedNavigationBar {}
extension WebViewController: HasThemedNavigationBar {}
