//
//  SettingsManager+Events.swift
//  PremierInn
//
//  Created by Freddie Parks on 17/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import EventKit

typealias CalendarPrompt = (
    _ fullAccess: @escaping () -> Void,
    _ writeOnly: @escaping () -> Void,
    _ cancel: @escaping () -> Void
) -> Void

enum CalendarAccessType {
    case fullAccess
    case writeOnly
}

extension SettingsManager {
    func emptyEventForBooking(
        showCalendarPrompt: @escaping CalendarPrompt,
        completion: @escaping (_ store: EKEventStore, _ event: EKEvent?, _ error: Error?) -> Void
    ) {
        let eventStore = EKEventStore()

        switch EKEventStore.authorizationStatus(for: .event) {
        case .authorized, .fullAccess:
            completion(eventStore, self.event(forStore: eventStore), nil)

        case .writeOnly:
            if #available(iOS 17.0, *), !SettingsManager.sharedInstance.isCalendarPromptShown {
                showInitialPrompt(showCalendarPrompt: showCalendarPrompt, eventStore: eventStore, completion: completion)
            } else {
                completion(eventStore, self.event(forStore: eventStore), nil)
            }

        case .denied:
            completion(eventStore, nil, EventPermissionsError.permissionDenied)

        case .notDetermined:
            if #available(iOS 17.0, *), !SettingsManager.sharedInstance.isCalendarPromptShown {
                showInitialPrompt(showCalendarPrompt: showCalendarPrompt, eventStore: eventStore, completion: completion)
            } else {
                eventStore.requestAccess(to: .event) { granted, _ in
                    self.handleRequestedAccess(granted: granted, eventStore: eventStore, completion: completion)
                }
            }

        case .restricted:
            completion(eventStore, nil, EventPermissionsError.permissionRestricted)

        @unknown default:
            completion(eventStore, nil, EventPermissionsError.unknown)
        }
    }

    @available(iOS 17, *)
    private func showInitialPrompt(
        showCalendarPrompt: @escaping CalendarPrompt,
        eventStore: EKEventStore,
        completion: @escaping (_ store: EKEventStore, _ event: EKEvent?, _ error: Error?) -> Void
    ) {
        showCalendarPrompt {
            eventStore.requestFullAccessToEvents { granted, _ in
                self.handleRequestedAccess(granted: granted, eventStore: eventStore, completion: completion)
            }
        } _: {
            eventStore.requestWriteOnlyAccessToEvents { granted, _ in
                self.handleRequestedAccess(granted: granted, eventStore: eventStore, completion: completion)
            }
        } _: {
            SettingsManager.sharedInstance.isCalendarPromptShown = false
        }

        SettingsManager.sharedInstance.isCalendarPromptShown = true
    }

    private func handleRequestedAccess(
        granted: Bool,
        eventStore: EKEventStore,
        completion: @escaping (_ store: EKEventStore, _ event: EKEvent?, _ error: Error?) -> Void
    ) {
        DispatchQueue.main.async {
            if granted {
                completion(eventStore, self.event(forStore: eventStore), nil)
            } else {
                completion(eventStore, nil, EventPermissionsError.userDidNotGrantAccess)
            }
        }
    }

    func calendar(containsEventWithBookingRefererence reference: String, startDate: Date, endDate: Date) -> Bool {
        let eventStore = EKEventStore()
        let predicate = eventStore.predicateForEvents(withStart: startDate, end: endDate, calendars: nil)
        let events = eventStore.events(matching: predicate)

        return events.contains { $0.title?.contains(reference) ?? false }
    }

    private func event(forStore store: EKEventStore) -> EKEvent {
        let event = EKEvent(eventStore: store)
        event.calendar = store.defaultCalendarForNewEvents

        return event
    }
}
