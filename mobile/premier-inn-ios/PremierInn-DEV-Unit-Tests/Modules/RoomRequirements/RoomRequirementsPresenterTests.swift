//
//  RoomRequirementsPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 18/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockView: RoomRequirementsViewInput {
    
    var reloadDidCall = false
    var provideSmallHapticFeedbackDidCall = false
    var toggleLoadingDidCall = false
    
    var parentNavigationController: UINavigationController? { return nil }
    
    func reload(with: RoomRequirementsModel) {
        
        reloadDidCall = true
    }
    
    func provideSmallHapticFeedback() {
        
        provideSmallHapticFeedbackDidCall = true
    }
    
    func toggleLoading(isLoading: Bool) {
        
        toggleLoadingDidCall = true
    }
}

private class MockInteractor: RoomRequirementsInteractorInput {
    
    var saveChangesDidCall = false
    var currentNumberOfAdultsDidCall = false
    var currentNumberOfChildrenDidCall = false
    var cotCurrentlyRequiredDidCall = false
    var currentRoomTypeDidCall = false
    var availableTypesDidCall = false
    var roomRequirementsTrackingDidCall = false
    
    var roomRequirementsTracking: RoomRequirementsTracking {
        roomRequirementsTrackingDidCall = true
        return ("", "")
    }
    
    func saveChanges(shouldAttemptLogin: Bool, completion: @escaping (Result<Bool>) -> Void) {
        
        saveChangesDidCall = true
        
        completion(.success(result: true))
    }
    
    var model: RoomRequirementsModel {
        return RoomRequirementsModel(adults: 1, children: 0, shouldIncludeCot: false, roomType: .double)
    }
    
    var currentNumberOfAdults: Int {
        get {
            return 1
        }
        set {
            currentNumberOfAdultsDidCall = true
        }
    }
    
    var currentNumberOfChildren: Int {
        get {
            return 0
        }
        set {
            currentNumberOfChildrenDidCall = true
        }
    }
    
    var cotCurrentlyRequired: Bool {
        get {
            return false
        }
        set {
            cotCurrentlyRequiredDidCall = true
        }
    }
    
    var currentRoomType: RoomType {
        get {
            currentRoomTypeDidCall = true
            return .double
        }
        set {
            currentRoomTypeDidCall = true
        }
    }
    
    var availableTypes: [RoomType] {
        get {
            availableTypesDidCall = true
            
            return [.double]
        }
        set {
            
        }
    }
}

private class MockRouter: RoomRequirementsRouterInput {
    
    var removeModuleDidCall = false
    var errorOccuredWhenUpdatingPreferencesDidCall = false
    var showRoomTypePickerDidCall = false
    
    func removeModule() {
        
        removeModuleDidCall = true
    }
    
    func errorOccuredWhenUpdatingPreferences() {
        
        errorOccuredWhenUpdatingPreferencesDidCall = true
    }
    
    func showRoomTypePicker(selectedType: RoomType, availableTypes: [RoomType], delegate: RoomTypeSelectRouterDelegate?) {
        
        showRoomTypePickerDidCall = true
    }
}

class RoomRequirementsPresenterTests: XCTestCase {
    
    private var view: MockView!
    private var interactor: MockInteractor!
    private var router: MockRouter!
    private var presenter: RoomRequirementsPresenter!
    
    override func setUp() {
        
        view = MockView()
        interactor = MockInteractor()
        router = MockRouter()
        
        presenter = RoomRequirementsPresenter()
        presenter.view = view
        presenter.interactor = interactor
        presenter.router = router
    }
    
    override func tearDown() {
        
        presenter = nil
        view = nil
        
        super.tearDown()
    }
    
    func testViewIsReady() {
        
        presenter.viewIsReady()
        XCTAssertTrue(view.reloadDidCall)
    }
    
    func testTracking() {
        
        _ = presenter.roomRequirementsTracking
        XCTAssertTrue(interactor.roomRequirementsTrackingDidCall)
    }
    
    func testSaveChanges() {
        
        presenter.saveChanges()
        XCTAssertTrue(view.toggleLoadingDidCall)
        XCTAssertTrue(interactor.saveChangesDidCall)
        XCTAssertTrue(router.removeModuleDidCall)
    }
    
    func testRoomTypeSelection() {
        
        presenter.roomTypeDidSelect()
        XCTAssertTrue(interactor.currentRoomTypeDidCall)
        XCTAssertTrue(interactor.availableTypesDidCall)
        XCTAssertTrue(router.showRoomTypePickerDidCall)
    }
    
    func testCotChanged() {
        
        presenter.cotRequirementChanged(toRequired: false)
        XCTAssertTrue(interactor.cotCurrentlyRequiredDidCall)
    }
    
    func testAdultsChanges() {
        
        presenter.adultRequirementsChanged(to: 2)
        XCTAssertTrue(view.provideSmallHapticFeedbackDidCall)
        XCTAssertTrue(interactor.currentNumberOfAdultsDidCall)
        XCTAssertTrue(view.reloadDidCall)
    }
    
    func testChildrenChanges() {
        
        presenter.childrenRequirementsChanged(to: 2)
        XCTAssertTrue(view.provideSmallHapticFeedbackDidCall)
        XCTAssertTrue(interactor.currentNumberOfChildrenDidCall)
        XCTAssertFalse(interactor.cotCurrentlyRequiredDidCall)
    }
    
    func testChildrenChanges_ResetCot() {
        
        presenter.childrenRequirementsChanged(to: 0)
        XCTAssertTrue(view.provideSmallHapticFeedbackDidCall)
        XCTAssertTrue(interactor.currentNumberOfChildrenDidCall)
    }
    
    func testRoomTypeChanges() {
        
        presenter.roomTypeSelectViewControllerDidUpdate(roomType: RoomType.double)
        XCTAssertTrue(interactor.currentRoomTypeDidCall)
        XCTAssertTrue(view.reloadDidCall)
    }
}
