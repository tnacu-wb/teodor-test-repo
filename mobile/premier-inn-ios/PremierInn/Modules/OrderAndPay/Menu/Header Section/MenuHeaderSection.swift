//
//  MenuHeaderSection.swift
//  PremierInn
//
//  Created by Simon Antoine on 26/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

class MenuHeaderSection: UIView {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var contentView: UIView!

    override init(frame: CGRect) {
        super.init(frame: frame)
        setUp()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setUp()
    }

    func setUp() {
        Bundle.main.loadNibNamed(String(describing: MenuHeaderSection.self), owner: self, options: nil)
        addSubview(contentView)
    }
}
