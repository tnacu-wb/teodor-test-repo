//
//  AppBootstrapperTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

final class AppBootstrapperTests: XCTestCase {

    // MARK: - Tests

    func testStartRunsAllTasks() {
        // GIVEN: Multiple bootstrap tasks
        let task1 = MockBootstrapTask()
        let task2 = MockBootstrapTask()
        let task3 = MockBootstrapTask()

        let sut = AppBootstrapper(tasks: [task1, task2, task3])

        // WHEN: start is called
        sut.start()

        // THEN: all tasks are executed exactly once
        XCTAssertEqual(task1.runCallCount, 1)
        XCTAssertEqual(task2.runCallCount, 1)
        XCTAssertEqual(task3.runCallCount, 1)
    }

    func testStartExecutesTasksInOrder() {
        // GIVEN: Tasks that record execution order
        var executionOrder: [Int] = []

        let task1 = MockBootstrapTask {
            executionOrder.append(1)
        }

        let task2 = MockBootstrapTask {
            executionOrder.append(2)
        }

        let task3 = MockBootstrapTask {
            executionOrder.append(3)
        }

        let sut = AppBootstrapper(tasks: [task1, task2, task3])

        // WHEN: start is called
        sut.start()

        // THEN: tasks are executed in the expected order
        XCTAssertEqual(executionOrder, [1, 2, 3])
    }

    func testStartWithEmptyTasksDoesNothing() {
        // GIVEN: No tasks
        let sut = AppBootstrapper(tasks: [])

        // WHEN: start is called
        sut.start()

        // THEN: no crash and nothing happens
        XCTAssertTrue(true) // just verifying no side effects / crashes
    }

    func testStartRunsEachTaskOnlyOnce() {
        // GIVEN: A bootstrap task
        let task = MockBootstrapTask()
        let sut = AppBootstrapper(tasks: [task])

        // WHEN: start is called
        sut.start()

        // THEN: task is executed only once
        XCTAssertEqual(task.runCallCount, 1)
    }
}
