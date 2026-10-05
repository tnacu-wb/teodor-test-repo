//
//  ChooseTableTableViewCell.swift
//  PremierInn
//
//  Created by Simon Antoine on 17/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

protocol ChooseTableTableViewCellProtocol {
    func set(title: String)
}

final class ChooseTableTableViewCell: UITableViewCell {
    @IBOutlet weak var titleLabel: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()

        self.backgroundColor = .TintL4
        self.contentView.backgroundColor = .TintL4
    }
}

extension ChooseTableTableViewCell: ChooseTableTableViewCellProtocol {
    func set(title: String) {
        self.titleLabel.text = title
    }
}
