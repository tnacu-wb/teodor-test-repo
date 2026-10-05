//
//  UIViewController+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 10/09/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import MessageUI
import SimpleNetwork
import SafariServices

protocol CustomModalPresentable {
    var isCustomModalPresentationEnabled: Bool { get set }
}

extension UIViewController {
    func openAboutPage() {
        let controller = AboutRouter.build()
        controller.hidesBottomBarWhenPushed = true

        navigationController?.pushViewController(controller, animated: true)
    }

    func openAdditionalGuests() {
        let controller = AdditionalGuestsRouter.buildController()
        controller.hidesBottomBarWhenPushed = true

        navigationController?.pushViewController(controller, animated: true)
    }

    func showInitialCalendarPrompt(
        fullAccessAction: @escaping () -> Void,
        writeOnlyAction: @escaping () -> Void,
        cancelAction: @escaping () -> Void
    ) {
        let alertController = UIAlertController(
            title: PILocalizedString("calendarPromptTitle"),
            message: PILocalizedString("calendarPromptMessage"),
            preferredStyle: .alert
        )
        let cancelAction = UIAlertAction(title: PILocalizedString("Cancel"), style: .cancel) { _ in
            cancelAction()
        }
        let fullAccess = UIAlertAction(title: PILocalizedString("fullAccess"), style: .default) { _ in
            fullAccessAction()
        }
        let writeOnly = UIAlertAction(title: PILocalizedString("addOnly"), style: .default) { _ in
            writeOnlyAction()
        }
        alertController.addAction(cancelAction)
        alertController.addAction(fullAccess)
        alertController.addAction(writeOnly)

        navigationController?.present(alertController, animated: true)
    }

    func presentNonFullScreenViewController(_ viewController: UIViewController, animated: Bool) {
        viewController.view.frame = view.bounds

        addChild(viewController)
        view.addSubview(viewController.view)
    }
}

extension UIViewController {
    func openURLInSafari(url: URL) {
        guard UIApplication.shared.canOpenURL(url) else { return }

        let safariController = SFSafariViewController(url: url)
        present(safariController, animated: true)
    }

    func openURL(url: URL) {
        guard UIApplication.shared.canOpenURL(url) else { return }

        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }

    func openPrivacyPolicyExternalLink() {
        guard let url = Constants.privacyPolicyUrl else { return }

        openURLInSafari(url: url)
    }

    func openFAQExternalLink(url: URL? = nil) {
        guard let url = url ?? Constants.faqUrl else { return }

        openURLInSafari(url: url)
    }

    func openContactUsExternalLink() {
        // Get user language
        var language: String { Locale.current.language.languageCode?.identifier ?? "n/a" }

        guard let url = language == "de" ? Constants.germanFeedbackUrl : Constants.contactUsUrl else { return }

        openURLInSafari(url: url)
    }

    func openTermsAndConditionsExternalLink() {
        guard let url = Constants.termsAndConditionsUrl else { return }

        openURLInSafari(url: url)
    }

    func openAllergyInformationExternalLink() {
        guard let url = Constants.allergyInformationUrl else { return }

        openURLInSafari(url: url)
    }

    func openAboutBusinessBookerExternalLink() {
        guard let url = Constants.aboutBusinessBookerUrl else { return }

        openURLInSafari(url: url)
    }

    func showDirectionOptions(forHotel hotel: Hotel, withSender sender: UIView) {
        guard UIApplication.shared.canOpenURL(Constants.googleMapsURL) else {
            UIApplication.shared.openAppleMapsDirections(to: hotel)
            return
        }

        let controller = UIAlertController(
            title: PILocalizedString("directionsOptionsAlertTitle", comment: "Directions options alert title"),
            message: PILocalizedString("directionsOptionsAlertMessage", comment: "Directions options alert message"),
            preferredStyle: .actionSheet
        )
        controller.addAction(UIAlertAction(
            title: PILocalizedString(
                "directionsOptionsAlertActionCancel",
                comment: "Directions options alert action: cancel"
            ),
            style: .cancel,
            handler: nil
        ))
        controller.addAction(UIAlertAction(
            title: PILocalizedString(
                "directionsOptionsAlertActionAppleMaps",
                comment: "Directions options alert action: Apple Maps"
            ),
            style: .default
        ) { _ in
            UIApplication.shared.openAppleMapsDirections(to: hotel)
        })
        controller.addAction(UIAlertAction(
            title: PILocalizedString(
                "directionsOptionsAlertActionGoogleMaps",
                comment: "Directions options alert action: Google Maps"
            ),
            style: .default
        ) { _ in
            UIApplication.shared.openGoogleMapsDirections(to: hotel)
        })

        if UIDevice.current.userInterfaceIdiom == .pad {
            controller.modalPresentationStyle = .popover
            controller.popoverPresentationController?.sourceView = sender
            controller.popoverPresentationController?.sourceRect = sender.bounds
        }

        present(controller, animated: true)
    }

    func showMapsDirectionsOptions(with viewModel: DirectionsViewModel) {
        guard UIApplication.shared.canOpenURL(viewModel.googleMapsURL) else {
            guard let appleMapsPlacemark = viewModel.appleMapsPlacemark else { return }

            UIApplication.shared.openAppleMapsDirections(to: appleMapsPlacemark)
            return
        }

        let controller = UIAlertController(
            title: viewModel.optionsTitle,
            message: viewModel.optionsMessage,
            preferredStyle: .actionSheet
        )
        controller.addAction(UIAlertAction(title: viewModel.cancelButtonTitle, style: .cancel, handler: nil))
        controller.addAction(UIAlertAction(title: viewModel.appleMapsButtonTitle, style: .default) { _ in
            guard let placemark = viewModel.appleMapsPlacemark else { return }
            UIApplication.shared.openAppleMapsDirections(to: placemark)
        })
        controller.addAction(UIAlertAction(title: viewModel.googleMapsButtonTitle, style: .default) { _ in
            UIApplication.shared.openGoogleMapsDirections(to: viewModel.coordinates)
        })

        if UIDevice.current.userInterfaceIdiom == .pad {
            controller.modalPresentationStyle = .popover
            controller.popoverPresentationController?.sourceView = view
            controller.popoverPresentationController?.sourceRect = view.bounds
        }

        present(controller, animated: true)
    }

    func showCallHotelAlert(
        number: String,
        title: String = PILocalizedString("phoneCallAlertTitle", comment: "Phone call alert title"),
        message: String? = nil
    ) {
        guard let url = URL(string: "tel:" + number.replacingOccurrences(of: " ", with: "")) else { return }

        guard UIApplication.shared.canOpenURL(url) else {
            print("Device cannot call number: \(number)")
            return
        }

        let message = message ?? number

        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(
            title: PILocalizedString("phoneCallAlertCancel", comment: "Phone call alert cancel"),
            style: .cancel,
            handler: nil
        ))
        controller.addAction(UIAlertAction(
            title: PILocalizedString("phoneCallAlertAction", comment: "Phone call alert action"),
            style: .default
        ) { _ in
            self.openURL(url: url)
        })

        AnalyticsManager.shared.trackAction(PIAnalytics.Action.callUs, userInfo: nil)

        present(controller, animated: true)
    }
}

extension UIViewController: MFMailComposeViewControllerDelegate {
    public func mailComposeController(
        _ controller: MFMailComposeViewController,
        didFinishWith result: MFMailComposeResult,
        error: Error?
    ) {
        controller.dismiss(animated: true)
    }
}

extension MFMailComposeViewController {
    override open var preferredStatusBarStyle: UIStatusBarStyle { .lightContent }
    override open var childForStatusBarStyle: UIViewController? { nil }
}

// MARK: - AlertViewController Generic

extension UIViewController {
    func displayGenericErrorAlert() {
        displayCustomMessageErrorAlert(viewTitle: "Oops", viewMessage: "Something went wrong. Try again.", buttonTitle: "Ok")
    }

    func displayCustomMessageErrorAlert(viewTitle: String, viewMessage: String, buttonTitle: String) {
        displayCustomMessageAndButtonErrorAlert(
            viewTitle: viewTitle,
            viewMessage: viewMessage,
            buttonsAction: [UIAlertAction(title: buttonTitle, style: .default, handler: nil)]
        )
    }

    func displayCustomMessageAndButtonErrorAlert(viewTitle: String, viewMessage: String, buttonsAction: [UIAlertAction]) {
        let alert = UIAlertController(title: viewTitle, message: viewMessage, preferredStyle: .alert)
        for action in buttonsAction {
            alert.addAction(action)
        }
        self.present(alert, animated: true)
    }
}


extension UIViewController {
    func configureModalNavigationBar() {
        guard isModal() else {
            return
        }
        configureNavigationBar(navigationController: navigationController)
    }

    func configureNavigationBar(navigationController: UINavigationController?) {
        guard let navigationController = navigationController else { return }
        let navigationHasSeparator = !(self is HasNavigationBarWithoutSeparator)
        let shouldUseDefaultTheme = self is HasThemedNavigationBar

        if shouldUseDefaultTheme {
            navigationController.updateBarVisuals(withBottomBorder: true, theme: .premierInn)
        } else {
            navigationController.updateBarVisuals(withBottomBorder: navigationHasSeparator, theme: .light)
        }
    }

    var currentNavigationTheme: UINavigationController.NavigationBarColours {
        guard isModal() else { return .premierInn }
        let shouldUseDefaultTheme = self is HasThemedNavigationBar
        return shouldUseDefaultTheme ?
            .premierInn :
            .light
    }
}
