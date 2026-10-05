//
//  AboutView.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 21/06/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import SwiftUI

struct AboutView: View {
    let viewModel: AboutViewModel

    var body: some View {
        ScrollView {
            Text(viewModel.text)
                .font(Font(UIFont.Body()))
                .padding(EdgeInsets(top: 16, leading: 20, bottom: 0, trailing: 20))
                .toolbar {
                    Text(viewModel.version)
                        .foregroundStyle(Color(viewModel.navigationTheme.foregroundColor))
                }
            Spacer()
        }
    }
}

#Preview {
    let viewModel = AboutViewModel()
    return AboutView(viewModel: viewModel)
}
