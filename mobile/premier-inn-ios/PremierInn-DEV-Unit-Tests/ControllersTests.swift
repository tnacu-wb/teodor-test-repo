//
//  ControllersTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation
import SimpleNetwork
@testable import PremierInn

class ControllersTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func controllerChecks(_ controller: UIViewController) {

        controller.viewDidLoad()
        controller.viewDidAppear(true)
        controller.viewDidAppear(false)
        controller.viewWillAppear(true)
        controller.viewWillAppear(false)
        controller.viewDidDisappear(true)
        controller.viewDidDisappear(false)
        controller.viewWillDisappear(true)
        controller.viewWillDisappear(false)
    }

    func testAdditionalInfoViewController() {

        let fileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json")!
        let	data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary

        do {
            _ = try Hotel(dictionary:jsonDictionary)

            let controller = AdditionalInfoViewController()
            XCTAssertNotNil(controller)
        } catch {
            XCTFail("\(error)")
        }
    }

    func testPhotoPageViewControllerNextController() {

        let controllers = [UIViewController(), UIViewController()]

        let photoPageController = PhotoPageViewController(controllers: controllers, startIndex: 0)

        XCTAssertEqual(photoPageController.currentIndex, 0)

        let nextController1 = photoPageController.followingController(controllers.first!, direction: .forward)
        XCTAssertEqual(nextController1, controllers.last)

        let nextController2 = photoPageController.followingController(controllers.last!, direction: .forward)
        XCTAssertEqual(nextController2, controllers.first)

        let nextController3 = photoPageController.followingController(controllers.first!, direction: .reverse)
        XCTAssertEqual(nextController3, controllers.last)

        let nextController4 = photoPageController.followingController(controllers.last!, direction: .forward)
        XCTAssertEqual(nextController4, controllers.first)

        let photoPageController2 = PhotoPageViewController(controllers: controllers, startIndex: 1)
        photoPageController2.viewDidLoad()

        XCTAssertEqual(photoPageController2.currentIndex, 1)
    }

    func testAlertController() {

        let timeoutController = TimeoutErrorAlertController()
        timeoutController.headingText = "Blah"
        timeoutController.messageText = "BlahBlah"
        timeoutController.controllerSetup = { controller in
            controller.view.backgroundColor = .red
        }
        timeoutController.viewDidLoad()

        XCTAssert(timeoutController.view.tintColor == .BasePurple)

        if let contentController = timeoutController.value(forKey: "contentViewController") as? UIViewController {
            XCTAssert(contentController.view.backgroundColor == .red)
        }
    }
}
