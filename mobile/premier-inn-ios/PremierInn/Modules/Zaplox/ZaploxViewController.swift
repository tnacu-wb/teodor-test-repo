//
//  ZaploxViewController.swift
//  PremierInn
//
//  Created by Simon Antoine on 13/09/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

class ZaploxViewController: UIViewController {
    @IBOutlet weak var cardView: UIView!
    override func viewDidLoad() {
        super.viewDidLoad()

        cardView.backgroundColor = .BasePurple

        cardView.layer.shadowColor = UIColor.black.cgColor
        cardView.layer.shadowOpacity = 1
        cardView.layer.shadowOffset = .zero
        cardView.layer.shadowRadius = 10
    }
}
