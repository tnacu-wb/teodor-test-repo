//
//  RangeManager.swift
//  PremierInn
//
//  Created by Marcello Mascia on 24/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

class RangeManager {
    private var range: CountableClosedRange<Int>

    var currentStep: Int

    init(range: CountableClosedRange<Int>, currentStep: Int) {
        self.range = range
        self.currentStep = currentStep
    }

    func isLowerLimit() -> Bool {
       self.currentStep == self.range.lowerBound
    }

    func isUpperLimit() -> Bool {
        self.currentStep == self.range.upperBound
    }

    func increase() throws -> Int {
		let last = range.upperBound
		currentStep += 1

		if currentStep > last {
			currentStep = last

			let userInfo = [NSLocalizedFailureReasonErrorKey: PILocalizedString(
				"maxRangeErrorMessage",
				comment: "Error message showed when the max range limit is reached"
			)]

			throw NSError(domain: PIError.domain, code: PIError.Code.maxRangeReached, userInfo: userInfo)
		}

		return currentStep
    }

    func decrease() throws -> Int {
		let first = range.lowerBound
		currentStep -= 1

		if currentStep < first {
			currentStep = first

			let userInfo = [NSLocalizedFailureReasonErrorKey: PILocalizedString(
				"minRangeErrorMessage",
				comment: "Error message showed when the min range limit is reached"
			)]

			throw NSError(domain: PIError.domain, code: PIError.Code.minRangeReached, userInfo: userInfo)
		}

		return currentStep
    }
}
