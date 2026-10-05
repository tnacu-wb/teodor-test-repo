//
//  SimpleHeaderWithActionLabel.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/12/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

protocol SimpleHeaderWithActionLabelDelegate: AnyObject {
	func actionLabelDidTap(header: SimpleHeaderWithActionLabel)
}

class SimpleHeaderWithActionLabel: UITableViewHeaderFooterView {
    // MARK: - Views

	@IBOutlet weak var titleLabel: UILabel! {
		didSet {
			titleLabel.font = UIFont.Heading4_Semibold()
			titleLabel.textColor = UIColor.TintD2
		}
	}
	@IBOutlet weak var actionButton: ButtonWithBlocks! {
		didSet {
			actionButton.setTitle(nil, for: .normal)
			actionButton.setTitleColor(UIColor.Tint1, for: .normal)
			actionButton.titleLabel?.font = UIFont.Action1()
		}
	}

    // MARK: - Properties

	weak var delegate: SimpleHeaderWithActionLabelDelegate?

    // MARK: - Actions

	@IBAction func actionLabelDidTap(_ sender: UIButton) {
		delegate?.actionLabelDidTap(header: self)
	}
}
