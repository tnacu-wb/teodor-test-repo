//
//  BARTDowntimeModal.swift
//  PremierInn
//
//  Created by Nick Jones on 12/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

enum BARTDowntimeHandler {
    static func handle(error: Error?, withParentNavigationController parentNavigationController: UINavigationController?) {
        guard canHandle(error: error) else { return }

        let modalView = BARTDowntimeModal()

        modalView.parentNavigationController = parentNavigationController
        parentNavigationController?.present(modalView, animated: true, completion: nil)
    }

    static func canHandle(
        error: Error?,
        withParentNavigationController parentNavigationController: UINavigationController?
    ) -> Bool {
        guard canHandle(error: error) else { return false }

        let modalView = BARTDowntimeModal()

        modalView.parentNavigationController = parentNavigationController
        parentNavigationController?.present(modalView, animated: true, completion: nil)

        return true
    }

    static func canHandle(error: Error?) -> Bool {
        guard let requestsManagerError = error as? RequestsManagerError else { return false }

        switch requestsManagerError {
        case .maintenanceMode:
            return true
        default:
            return false
        }
    }

    static func bartIsDown(parentNavigationController: UINavigationController?) {
        let modalView = BARTDowntimeModal()
        modalView.modalPresentationStyle = .fullScreen

        parentNavigationController?.present(modalView, animated: true, completion: nil)
    }
}

class BARTDowntimeModal: UIViewController {
    weak var parentNavigationController: UINavigationController?

    deinit {
        print("DEINIT: \(self)")
    }

    @IBOutlet weak var header: UILabel! {
        didSet {
            header.text = PILocalizedString("bartDowntimeModalHeaderTitle", comment: "BART downtime modal header")
            header.accessibilityTraits.insert(.header)
            header.font = .Heading1_Semibold()
        }
    }

    @IBOutlet weak var body: UILabel! {
        didSet {
            body.text = PILocalizedString("bartDowntimeModalBodyText", comment: "BART downtime modal body")
            body.font = .Body()
        }
    }

    @IBOutlet weak var makeBookingLabel: UILabel! {
        didSet {
            makeBookingLabel.text = PILocalizedString(
                "bartDowntimeModalMakeBookingText",
                comment: "BART downtime modal make booking title"
            )
            makeBookingLabel.font = .Body()
        }
    }

    @IBOutlet weak var existingBookingLabel: UILabel! {
        didSet {
            existingBookingLabel.text = PILocalizedString(
                "bartDowntimeModalExistingBookingText",
                comment: "BART downtime modal existing booking title"
            )
            existingBookingLabel.font = .Body()
        }
    }

    @IBOutlet weak var closeButton: RoundedCornersButton! {
        didSet {
            closeButton.setTitle(
                PILocalizedString("bartDowntimeModalCloseButtonTitle", comment: "BART downtime modal close button title"),
                for: .normal
            )
            closeButton.titleLabel?.font = .Button1()
            closeButton.backgroundColor = .Tint1
            closeButton.setTitleColor(.BaseWhite, for: .normal)
            // Bit of a hack but needed for now, if the button text is empty do not show it so users can not close the banner
            if PILocalizedString("bartDowntimeModalCloseButtonTitle").isEmpty {
                closeButton.isHidden = true
            } else {
                closeButton.isHidden = false
            }
        }
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        .lightContent
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        configureModalNavigationBar()
        view.backgroundColor = .TintL2
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(true, animated: animated)
    }

    @IBAction func close() {
        dismiss(animated: true) {
            self.parentNavigationController?.popToRootViewController(animated: true)
        }
    }
}
