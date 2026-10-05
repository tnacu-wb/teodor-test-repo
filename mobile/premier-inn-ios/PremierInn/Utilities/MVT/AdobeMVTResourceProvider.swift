//
//  AdobeMVTResourceProvider.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import AEPTarget

class AdobeMVTResourceProvider: MVTManagerProvider {
    private static let additionalParamsWeDontUnderstandButAreMandatory =
        ["at_property": "eb6ee237-1a95-a684-5933-3f421218ae19"]

    /* Here we build a target request, the name property is the name of the Content Location defined in the test target on the Adobe dashboard.
     We would use this and other request when we want to get one of the available options from a test target on a location, in this example we have "PI_iOS_LaunchConfig" as our location */

    /* What we have currently with our Adobe Mobile account is that we can only get a response from a single target on a particular location at any one time
     so for example if we have a "MapNoMap" target and a "NoHotelDetailsImages" target both pointing to "TestLocation" we'll only get back a result
     for one of those targets; According to Tom Pinney in analytics we should be able to get both results back in one response.*/

    func load(test: MVTest, completion: @escaping (Any?) -> Void) {
        guard let defaultValue = test.defaultValue as? String else { return }
        let request = TargetRequest(
            mboxName: test.identifier,
            defaultContent: defaultValue,
            targetParameters: nil
        ) {(_ content: String?) in
            guard let content = content else { return }
            print(content)

            let data = Data(content.utf8)

            do {
                if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                    if let value = json[test.tag] as? String {
                        return completion(value)
                    }
                }
            } catch let error as NSError {
                print("Unexpected format in A/B test with name: \(test.identifier) error: \(error.localizedDescription)")
            }

            completion(nil)
        }

        Target.retrieveLocationContent([request], with: nil)
    }

    /* Here we build a target completion request, the name property is the name of the Success Location defined in the test target the Adobe dashboard.
     We use this whenever we want to "close off" a test, so for our current implementation we call this when we complete a booking */

    // There's a lot of properties here that we aren't using but this is the method that the ADBMobile framework makes available to us so it's our go-to for now.

    func complete(test: MVTest) {
        // guard let completionIdentifier = test.completionIdentifier else { return }
//        guard let adobeRequest = ADBMobile.targetCreateOrderConfirmRequest(withName: completionIdentifier, orderId: nil, orderTotal: nil, productPurchasedId: nil, parameters: AdobeMVTResourceProvider.additionalParamsWeDontUnderstandButAreMandatory) else {
//            return
//        }

        // ADBMobile.targetLoad(adobeRequest, callback: nil)
    }
}
