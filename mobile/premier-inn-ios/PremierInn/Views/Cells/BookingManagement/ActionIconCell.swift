//
//  ActionIconCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 04/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class ActionIconCell: BorderedContentViewCell {
    override func awakeFromNib() {
        super.awakeFromNib()

        accessoryView = UIImageView(image: UIImage(named: "discloseIndicator"))

        accessoryView?.subviews.forEach { $0.backgroundColor = .white }
    }

    override var textLabel: UILabel? {
        self.title
    }

    @IBOutlet weak var title: UILabel! {
        didSet {
            title.text = nil
            title.font = UIFont.Body()
            title.textColor = .TintD1
        }
    }
    @IBOutlet weak var icon: UIImageView! {
        didSet {
            icon.tintColor = .grape
        }
    }
    @IBOutlet weak var topConstraint: NSLayoutConstraint!
    @IBOutlet weak var bottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var trailingConstraint: NSLayoutConstraint!
    @IBOutlet weak var iconWidth: NSLayoutConstraint!
    @IBOutlet weak var iconHeight: NSLayoutConstraint!
    @IBOutlet weak var iconTopConstraint: NSLayoutConstraint!
    @IBOutlet var iconAspectRatio: NSLayoutConstraint!

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(false, animated: false)
    }
}
