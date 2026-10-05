//
//  NotificationFeedbackManager.swift
//  PremierInn
//
//  Created by Nick Jones on 12/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import CoreHaptics

public enum FeedbackStyle: Int {
    case success
    case warning
    case error
}

public enum ExperimentalFeedback {
    case tripleTake
}

/**
 A singleton object which handles the storage and exectuion of notification feedback


 **NOTE:**
 **Initialisation of this object is privately confined to ensure singleton only access**

 */
class NotificationFeedbackManager {
    static let shared: NotificationFeedbackManager = NotificationFeedbackManager()

    private init() {}

    func provideFeedback(for feedbackStyle: FeedbackStyle) {
        let feedbackType: UINotificationFeedbackGenerator.FeedbackType

        switch feedbackStyle {
        case .success:
            feedbackType = UINotificationFeedbackGenerator.FeedbackType.success
        case .warning:
            feedbackType = UINotificationFeedbackGenerator.FeedbackType.warning
        case .error:
            feedbackType = UINotificationFeedbackGenerator.FeedbackType.error
        }

        NotificationFeedbackGeneratorAvailabilityProxy.shared.notificationOccured(with: feedbackType)
    }

    func provideLightTapFeedback() {
        NotificationFeedbackGeneratorAvailabilityProxy.shared.provideLightTapFeedback()
    }

    func runExperimentalFeedback(ofType type: ExperimentalFeedback) {
        switch type {
        case .tripleTake:
            ExperimentalFeedbackGeneratorAvailabilityProxy.shared.runTripleTakeHapticFeedback()
        }
    }

    func stopExperimentalFeedback() {
        ExperimentalFeedbackGeneratorAvailabilityProxy.shared.stopExperimentalFeedback()
    }
}


/**
 Used to manage a shared ```UINotificationFeedbackGenerator``` which is onlyavailable in iOS 10 or higher.

 The use of this object is to enclose the availability checks within this class and, further up, within
 ```NotificationFeedbackManager```.

 This prevents calls to ```NotificationFeedbackManager``` from always requiring an OS version check and
 instead the OS version condition checks are made within ```NotificationFeedbackManager``` functions themselves

 */
private class NotificationFeedbackGeneratorAvailabilityProxy {
    static let shared: NotificationFeedbackGeneratorAvailabilityProxy = NotificationFeedbackGeneratorAvailabilityProxy()

    private var feedbackGenerator: UINotificationFeedbackGenerator?
    private var tapFeedback: UIImpactFeedbackGenerator?

    init() {
        feedbackGenerator = UINotificationFeedbackGenerator()

        tapFeedback = UIImpactFeedbackGenerator(style: .light)
        tapFeedback?.prepare()
    }

    func notificationOccured(with notificationType: UINotificationFeedbackGenerator.FeedbackType) {
        feedbackGenerator?.notificationOccurred(notificationType)
    }

    func provideLightTapFeedback() {
        tapFeedback?.impactOccurred()
    }
}

/**
Used to manage a shared ```UINotificationFeedbackGenerator``` and in adition the use of custom AHAP files which are only available in iOS 13 or higher.

The use of this object is to enclose the availability checks within this class and, further up, within
```NotificationFeedbackManager```.

This prevents calls to ```NotificationFeedbackManager``` from always requiring an OS version check and
instead the OS version condition checks are made within ```NotificationFeedbackManager``` functions themselves

*/
@available(iOS 13.0, *)
private class ExperimentalFeedbackGeneratorAvailabilityProxy {
    static let shared: ExperimentalFeedbackGeneratorAvailabilityProxy = ExperimentalFeedbackGeneratorAvailabilityProxy()
    private var hapticEngine: CHHapticEngine?

    init() {
        do { try hapticEngine = CHHapticEngine() } catch { return }
    }

    func stopExperimentalFeedback() {
        hapticEngine?.stop(completionHandler: nil)
    }

    /*
     _______   _       _        _______    _          _    _
    |__   __| (_)     | |      |__   __|  | |        | |  | |
       | |_ __ _ _ __ | | ___     | | __ _| | _____  | |__| | ___ _ __ ___
       | | '__| | '_ \| |/ _ \    | |/ _` | |/ / _ \ |  __  |/ _ | '__/ _ \
       | | |  | | |_) | |  __/    | | (_| |   |  __/ | |  | |  __| | |  __/
       |_|_|  |_| .__/|_|\___|    |_|\__,_|_|\_\___| |_|  |_|\___|_|  \___|
                | |
                |_|
    */
    func runTripleTakeHapticFeedback() {
        guard let path = Bundle.main.path(forResource: "TripleTakeHaptic", ofType: "ahap") else { return }

        do {
            try hapticEngine?.start()
            try hapticEngine?.playPattern(from: URL(fileURLWithPath: path))
        } catch {
            return
        }
    }
}
