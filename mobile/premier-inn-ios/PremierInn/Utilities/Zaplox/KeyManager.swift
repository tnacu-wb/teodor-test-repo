// swiftlint:disable comment_spacing
////
////  KeyManager.swift
////  PremierInn
////
////  Created by Georgios Aikaterinakis on 09/09/2021.
////  Copyright © 2021 Whitbread. All rights reserved.
////
//
//import ZDK
//
//protocol KeyManagerType {
//
//    // Properties
//    var zdkManager: ZaploxManager? { get }
//    var deviceID: String? { get }
//
//    // Lifecycle
//    func setup()
//}
//
//class KeyManager: KeyManagerType {
//
// MARK: - Singleton
//
//    static let shared = KeyManager()
//
// MARK: - Properties
//
//    var zdkManager: ZaploxManager?
//    var deviceID: String?
//
// MARK: - Lifecycle
//
//    func setup() {
//
//        ZDK().setup(withBrandName: Constants.ZaploxConstants.brandName, restURL: Constants.ZaploxConstants.url) { statusCode, manager, error in
//
//            print("@@ statusCode: " + String(describing: statusCode.rawValue))
//
//            guard error == nil else {
//                print("@@ error: " + (error?.message() ?? ""))
//                return
//            }
//
//            self.zdkManager = manager as? ZaploxManager
//            self.deviceID = self.zdkManager?.deviceZuid()
//
//            print("@@ deviceID: " + (self.deviceID ?? ""))
//        }
//    }
//
//    func fetchKey(with keyId: String) {
//
//        guard let zdkManager = zdkManager else { return }
//
//        let key = zdkManager.key(byZuid: keyId)
//
//        key?.unlock { success, error in
//
//            if error != nil {
//
//                print(error.debugDescription)
//                return
//            }
//
//            guard success else {
//
//                print("Something went wrong")
//                return
//            }
//
//            print("Door should be unlocked now!")
//        }
//    }
//}
// swiftlint:enable comment_spacing
