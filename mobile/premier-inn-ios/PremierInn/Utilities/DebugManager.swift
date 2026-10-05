//
//  DebugManager.swift
//  PremierInn
//
//  Created by Marcello Mascia on 29/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

class DebugManager {
	static let sharedInstance = DebugManager()

	var isSwizzlingLocation = false

	func overrideLocationManager(_ active: Bool) {
		isSwizzlingLocation = active

		let originalMethod = class_getInstanceMethod(LocationManager.self, #selector(LocationManager.findUserLocation))
		let replacedMethod = class_getInstanceMethod(LocationManager.self, #selector(LocationManager.overriddenFindUserLocation))

		if active {
			method_exchangeImplementations(originalMethod!, replacedMethod!)
		} else {
			method_exchangeImplementations(replacedMethod!, originalMethod!)
		}
	}
}

extension LocationManager {
	@objc func overriddenFindUserLocation() {
		coreLocationManager.delegate = nil

		let fakeError = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey: "Fake error"])
		locationManager(coreLocationManager, didFailWithError: fakeError)

		debugPrint("overridden-FindUserLocation")
	}
}
