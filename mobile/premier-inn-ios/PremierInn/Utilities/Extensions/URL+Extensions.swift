//
//  URL+Extensions.swift
//  PremierInn
//
//  Created by Freddie Parks on 07/11/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import UIKit

private enum URLImageSizeConstants {
    static let tiny = CGSize(width: 80, height: 80)
    static let small = CGSize(width: 120, height: 120)
    static let medium = CGSize(width: 480, height: 320)
    static let large = CGSize(width: 800, height: 600)
    static let massive = CGSize(width: 1920, height: 1200)
}

private enum URLImageSizeFormats {
    static let jpg = "%@.piimage.thumbnail.%.0f.%.0f.jpg"
    static let png = "%@.piimage.thumbnail.%.0f.%.0f.png"
}

enum URLImageSize {
    case tiny
    case small
    case medium
    case large
    case massive
}

extension URL {
    func sizedImageURL(withSize size: URLImageSize) -> URL? {
        let imageSize: CGSize = {
            switch size {
            case .tiny:
                return URLImageSizeConstants.tiny
            case .small:
                return URLImageSizeConstants.small
            case .medium:
                return URLImageSizeConstants.medium
            case .large:
                return URLImageSizeConstants.large
            case .massive:
                return URLImageSizeConstants.massive
            }
        }()

        var urlString = self.absoluteString
        if urlString.hasPrefix("http://") {
            urlString = urlString.replacingOccurrences(of: "http://", with: "https://")
        }

        return URL(string: String(format: URLImageSizeFormats.jpg, urlString, imageSize.width, imageSize.height))
    }

    var queryParams: [String: String] {
        var paramsDic: [String: String] = [:]
        guard let queryString = query else { return paramsDic }
        guard let paramsString = queryString.components(separatedBy: "?").last else { return paramsDic }
        let params = paramsString.components(separatedBy: "&")

        for param in params {
            let keyAndValue = param.components(separatedBy: "=")
            guard let key = keyAndValue.first, let value = keyAndValue.last else { continue }
            paramsDic[key] = value
        }

        return paramsDic
    }
}
