//
//  RoomTypeCharacteristicCell.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 10/06/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class RoomTypeCharacteristicCell: UITableViewCell {
    @IBOutlet weak var characteristicTitle: UILabel!
    @IBOutlet weak var characteristicDescription: UILabel!
    @IBOutlet weak var characteristicTickImageView: UIImageView!

    override func awakeFromNib() {
        super.awakeFromNib()

        self.setSelected(true, animated: false)     // otherwise the tick is not shown ?
    }
}
