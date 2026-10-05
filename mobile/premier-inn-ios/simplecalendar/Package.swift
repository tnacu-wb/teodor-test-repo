// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "SimpleCalendar",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "SimpleCalendar",
            targets: ["SimpleCalendar"]
        ),
    ],
    targets: [
        .target(
            name: "SimpleCalendar",
            path: "SimpleCalendar",
            sources: ["Source"],
            resources: [
                .process("Xibs")
            ]
        ),
        .testTarget(
            name: "SimpleCalendarTests",
            dependencies: ["SimpleCalendar"],
            path: "./SimpleCalendarTests"
        )
    ],
    swiftLanguageModes: [.v5]
)
