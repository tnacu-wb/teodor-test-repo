//
//  DiscountOfferView.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 25/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SwiftUI

class DiscountOfferView: UIView {
    @IBOutlet weak var contentView: UIView! {
        didSet {
            contentView.backgroundColor = .clear
        }
    }
    @IBOutlet weak var numberLabel: UILabel! {
        didSet {
            numberLabel.font = UIFont.Heading1_ExtraBold(122)
            numberLabel.textColor = .Tint11
        }
    }
    @IBOutlet weak var percentageLabel: UILabel! {
        didSet {
            percentageLabel.font = UIFont.Heading1_ExtraBold(65)
            percentageLabel.textColor = .Tint11
        }
    }
    @IBOutlet weak var offLabel: UILabel! {
        didSet {
            offLabel.font = UIFont.Heading1_ExtraBold(39)
            offLabel.textColor = .Tint11
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        setUp()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setUp()
    }

    func setUp() {
        Bundle.main.loadNibNamed(String(describing: DiscountOfferView.self), owner: self, options: nil)
        addSubview(contentView)
    }

    func configure(number: String, percentage: String, off: String) {
        numberLabel.text = number
        percentageLabel.text = percentage
        offLabel.text = off
    }
}

struct RepresentedDiscountOfferView: UIViewRepresentable {
    typealias UIViewType = DiscountOfferView

    var number: String
    var percentage: String
    var off: String

    func makeUIView(context: Context) -> DiscountOfferView {
        let view = DiscountOfferView()

        view.configure(
            number: number,
            percentage: percentage,
            off: off
        )

        return view
    }

    func updateUIView(_ uiView: DiscountOfferView, context: Context) {
    }
}

#Preview {
    RepresentedDiscountOfferView(
        number: "25",
        percentage: "50%",
        off: "25"
    )
}
