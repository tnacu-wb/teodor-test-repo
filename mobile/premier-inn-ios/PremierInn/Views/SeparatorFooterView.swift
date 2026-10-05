//
//  SeparatorView.swift
//  PremierInn
//
//  Created by andrei.cojocaru on 7/31/24.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

final class SeparatorFooterView: UITableViewHeaderFooterView {
    private let lineView: UIView = {
       let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = .TintL3
        return view
    }()

    init(
        reuseIdentifier: String? = "",
        lineHeight: CGFloat = 1
    ) {
        super.init(reuseIdentifier: reuseIdentifier)
        contentView.backgroundColor = .BaseWhite

        contentView.addSubview(lineView)

        NSLayoutConstraint.activate([
            lineView.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            lineView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            lineView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            lineView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            lineView.heightAnchor.constraint(equalToConstant: lineHeight)
        ])
    }

    required init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
