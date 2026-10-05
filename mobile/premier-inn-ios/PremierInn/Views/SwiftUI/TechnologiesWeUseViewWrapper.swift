//
//  TechnologiesWeUseViewWrapper.swift
//  PremierInn
//
//  Created by Santa Gurung on 16/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct TechnologiesWeUseViewWrapper: UIViewControllerRepresentable {
    typealias UIViewControllerType = TechnologiesWeUseViewController

    private let viewModel: TechnologiesWeUseViewModel

    init() {
        var fullyFormedHTMLString = ""

        if let url = Bundle.main.url(forResource: "gdpr", withExtension: "html"),
           let html = try? String(contentsOf: url, encoding: .utf8) {
            // do not use Firebase override strings on purpose
            let dataUsageMessage = NSLocalizedString("dataUsageMessage", comment: "GDPR data usage content")
            fullyFormedHTMLString = String(format: html, dataUsageMessage)
        }

        viewModel = TechnologiesWeUseViewModel(
            title: PILocalizedString("gdprDataPolicyTitle", comment: "GDPR data policy title"),
            html: fullyFormedHTMLString,
            screenName: PIAnalytics.StateNames.gdprHowWeUseData,
            screenType: PIAnalytics.StateTypes.gdprHowWeUseData
        )
    }

    func makeUIViewController(context: Context) -> TechnologiesWeUseViewController {
        TechnologiesWeUseViewController(with: viewModel)
    }

    func updateUIViewController(_ uiViewController: TechnologiesWeUseViewController, context: Context) {
        // hardcoded content. no update needed
    }
}
