//
//  main.swift
//
//  Created by Marcello Mascia on 26/11/2015.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

let argsRawPointer = UnsafeMutableRawPointer(CommandLine.unsafeArgv)
let args = argsRawPointer.bindMemory(to: UnsafeMutablePointer<Int8>.self, capacity: Int(CommandLine.argc))

if NSClassFromString("XCTestCase") == nil || NSClassFromString("EarlGreyImpl") != nil {
    UIApplicationMain(CommandLine.argc, CommandLine.unsafeArgv, nil, NSStringFromClass(AppDelegate.self))
} else {
    UIApplicationMain(CommandLine.argc, CommandLine.unsafeArgv, nil, NSStringFromClass(UnitTestsAppDelegate.self))
}
