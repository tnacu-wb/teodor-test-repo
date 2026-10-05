//
//  TodayViewController.swift
//  TakeMeBack
//
//  Created by Marcello Mascia on 25/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import MapKit
import NotificationCenter

final class TodayViewController: UIViewController {
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var logo: UIImageView! {
        didSet {
            logo.contentMode = .scaleAspectFit
        }
    }

    @IBOutlet weak var name: UILabel! {
        didSet {
            name.text = currentHotelName
            name.font = .Heading3_Semibold()
            name.textColor = .black
        }
    }

    @IBOutlet weak var checkInOutDate: UILabel! {
        didSet {
            checkInOutDate.text = currentCheckInOutDate
            checkInOutDate.font = .Body()
            checkInOutDate.textColor = .black
        }
    }

    @IBOutlet weak var checkInTitle: UILabel! {
        didSet {
            checkInTitle.text = NSLocalizedString("widgetArrivalTimeLabel", comment: "")
            checkInTitle.font = UIFont.Body_Semibold()
            checkInTitle.textColor = .black
        }
    }

    @IBOutlet weak var checkOutTime: UILabel! {
        didSet {
            checkOutTime.text = NSLocalizedString("hotelDetailsCheckInTime", comment: "")
            checkOutTime.font = UIFont.Body()
            checkOutTime.textColor = .black
        }
    }

    private var currentHotelName: String? {
        let userDefaults = UserDefaults(suiteName: AppExtensionConstants.CurrentReservation.groupContainerName)

        guard let dict = userDefaults?.dictionary(forKey: AppExtensionConstants.CurrentReservation.currentHotelKey)
            else { return nil }
        guard let name = dict[AppExtensionConstants.CurrentReservation.name] as? String else { return nil }

        return name
    }

    private var currentCheckInOutDate: String? {
        let userDefaults = UserDefaults(suiteName: AppExtensionConstants.CurrentReservation.groupContainerName)

        guard let dict = userDefaults?.dictionary(forKey: AppExtensionConstants.CurrentReservation.currentHotelKey)
            else { return nil }
        guard let datesString = dict[AppExtensionConstants.CurrentReservation.datesString] as? String else { return nil }

        return datesString
    }

    @IBOutlet weak var button: UIButton!

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        guard let currentHotelName = currentHotelName else {
            self.updateView(bookingExists: false)
            return
        }
        self.updateView(bookingExists: !currentHotelName.isEmpty)
    }

    @IBAction func actionButtonDidTap(_ sender: UIButton) {
        extensionContext?.open(URL(string: "premierinn://reservationDetails")!, completionHandler: nil)
    }

    func updateView(bookingExists: Bool) {
        messageLabel.text = bookingExists ? "" : NSLocalizedString(
            "No upcoming booking, Tap to search and book hotels using Premier Inn app.",
            comment: ""
        )
        logo.isHidden = !bookingExists
        name.isHidden = !bookingExists
        checkInOutDate.isHidden = !bookingExists
        checkInTitle.isHidden = !bookingExists
        checkOutTime.isHidden = !bookingExists
    }
}

extension TodayViewController: NCWidgetProviding {
    func widgetPerformUpdate(completionHandler: (@escaping (NCUpdateResult) -> Void)) {
        let userDefaults = UserDefaults(suiteName: AppExtensionConstants.CurrentReservation.groupContainerName)
        if let dict = userDefaults?.dictionary(forKey: AppExtensionConstants.CurrentReservation.currentHotelKey),
           let name = dict[AppExtensionConstants.CurrentReservation.name] as? String,
           let datesString = dict[AppExtensionConstants.CurrentReservation.datesString] as? String {
            if self.name.text != name || checkInOutDate.text != datesString {
                completionHandler(.newData)
            } else {
                completionHandler(.noData)
            }
        } else {
            completionHandler(.failed)
        }
    }
}
