//
//  FullScreenImageViewerViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

private class MockEventHandler: FullScreenImageViewerViewEventHandler {
    
    var viewIsReadyDidCall = false
    var closeDidCall = false
    var changeDidCall = false
    
    func viewIsReady() {
        
        viewIsReadyDidCall = true
    }
    
    func closeButtonDidTap() {
        
        closeDidCall = true
    }
    
    func imageSetDidChangePicture(atIndex index: Int) {
        
        changeDidCall = true
    }
}

class FullScreenImageViewerViewTests: XCTestCase {
    
    fileprivate var mockEventHandler: MockEventHandler?
    
    var view: FullscreenImageSetViewController?
    
    override func setUp() {
        super.setUp()
        
        mockEventHandler = MockEventHandler()
        
        view = FullscreenImageSetViewController()
        view?.eventHandler = mockEventHandler
    }
    
    override func tearDown() {
        
        view = nil
        
        super.tearDown()
    }
    
    func testViewReady() {
        
        view?.viewDidLoad()
        view?.viewWillAppear(false)
        
        XCTAssert(mockEventHandler?.viewIsReadyDidCall == true)
    }
    
    func testPhotoView() {
        
        view?.tappedPhotoContainer(atIndex: 0)
        view?.swipedPhotoContainer(atIndex: 1)
        
        XCTAssert(mockEventHandler?.changeDidCall == true)
    }
    
    func testClose() {
        
        view?.closeButtonDidTap(UIButton())
        
        XCTAssert(mockEventHandler?.closeDidCall == true)
    }
}
