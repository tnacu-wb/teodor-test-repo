//
//  SwiftUIContainerTableViewCell.swift
//  PremierInn
//
//  Created by Santa Gurung on 22/11/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit
import SwiftUI

class SwiftUIContainerTableViewCell<Content: View>: UITableViewCell {
    private var hostingController: UIHostingController<Content>?

    func configure(with swiftUIView: Content) {
        guard hostingController == nil else {
            hostingController?.rootView = swiftUIView
            return
        }
        hostingController = UIHostingController(rootView: swiftUIView)
        guard let hostingControllerView = hostingController?.view else { return }

        contentView.addSubview(hostingControllerView)

        hostingControllerView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            hostingControllerView.topAnchor.constraint(equalTo: contentView.topAnchor),
            hostingControllerView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor),
            hostingControllerView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            hostingControllerView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor)
        ])
    }
}
