// swift-tools-version: 6.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "SimpleNetwork",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "SimpleNetwork",
            targets: ["SimpleNetwork"]
        )
    ],
    dependencies: [
        .package(url: "https://github.com/Alamofire/Alamofire.git", exact: "5.0.2"),
        .package(url: "https://github.com/Alamofire/AlamofireImage.git", exact: "4.0.0"),
        .package(url: "https://github.com/auth0/Auth0.swift.git", exact: "2.16.2")
    ],
    targets: [
        .target(
            name: "SimpleNetwork",
            dependencies: [
                .product(name: "Alamofire", package: "Alamofire"),
                .product(name: "AlamofireImage", package: "AlamofireImage"),
                .product(name: "Auth0", package: "Auth0.swift")
            ],
            path: "./Sources",
            resources: [
                .process("Mocks")
            ]
        ),
        .testTarget(
            name: "SimpleNetworkTests",
            dependencies: ["SimpleNetwork"],
            path: "SimpleNetworkTests",
            resources: [
                .process("Assets")
            ]
        )
    ],
    swiftLanguageModes: [.v5]
)
