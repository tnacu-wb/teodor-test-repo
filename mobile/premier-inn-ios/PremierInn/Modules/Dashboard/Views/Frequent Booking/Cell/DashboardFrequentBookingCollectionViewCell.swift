//
//  DashboardFrequentBookingCollectionViewCell.swift
//  PremierInn
//
//  Created by Simon Antoine on 12/03/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

protocol DashboardFrequentBookingCollectionViewCellDelegate: AnyObject {
    func selectDatePressed(with identifier: String)
}

class DashboardFrequentBookingCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var hotelPlaceHolder: UIImageView!
    @IBOutlet weak var hotelName: UILabel! {
        didSet {
            hotelName.textColor = .TintD1
            hotelName.font = UIFont.Body_Semibold()
        }
    }
    @IBOutlet weak var selectDatesButton: UIButton! {
        didSet {
            selectDatesButton.titleLabel?.font = UIFont.Action1()
            selectDatesButton.setTitleColor(.ColourDL5, for: .normal)
            selectDatesButton.titleLabel?.textColor = .ColourDL5
            selectDatesButton.setTitle(PILocalizedString("frequentBookingSelectDates"), for: .normal)
        }
    }
    @IBOutlet weak var separatorView: UIView!

    weak var delegate: DashboardFrequentBookingCollectionViewCellDelegate?
    var hotelCode: String?

    override func awakeFromNib() {
        super.awakeFromNib()
        self.layer.borderWidth = 0.5
        self.layer.borderColor = UIColor.lightGray.cgColor

        self.layer.cornerRadius = 4.0

        self.layer.masksToBounds = true
        self.clipsToBounds = true

        self.separatorView.backgroundColor = .lightGray
    }

    @IBAction func buttonTapped(_ sender: Any) {
        guard let hotelCode = hotelCode else { return }

        delegate?.selectDatePressed(with: hotelCode)
    }
}
