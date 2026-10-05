//
//  VoucherTagView.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 26/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

struct VoucherTagView: View {
    private let imageName: String
    private let title: String

    private let tagImageSize: CGFloat
    private let closeImageSize: CGFloat

    private let shouldShowCloseButton: Bool
    private let closeAction: (() -> Void)?

    init(
        imageName: String = "tagIcon",
        title: String,
        tagImageSize: CGFloat = 13,
        closeImageSize: CGFloat = 10,
        shouldShowCloseButton: Bool = true,
        closeAction: (() -> Void)? = nil
    ) {
        self.imageName = imageName
        self.title = title
        self.tagImageSize = tagImageSize
        self.closeImageSize = closeImageSize
        self.shouldShowCloseButton = shouldShowCloseButton
        self.closeAction = closeAction
    }

    var body: some View {
        HStack {
            Image(.tagIconDeepTeal)
                .resizable()
                .frame(width: tagImageSize, height: tagImageSize)

            Text(title)
                .font(Font(UIFont.BodySmall_Semibold()))
                .foregroundStyle(Color(uiColor: .deepTeal))

            if shouldShowCloseButton {
                Image(systemName: "xmark")
                    .resizable()
                    .frame(width: closeImageSize, height: closeImageSize)
                    .foregroundStyle(Color(uiColor: .Tint1))
                    .onTapGesture { closeAction?() }
            }
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 5)
        .background(
            RoundedRectangle(cornerRadius: 4)
                .fill(Color(uiColor: .Tint3))
        )
    }
}
