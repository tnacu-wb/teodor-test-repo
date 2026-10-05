//
//  GDPR.swift
//  PremierInn
//
//  Created by Marcello Mascia on 23/04/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka

extension FormekaViewController {
	func gdprInfoSection(text: String, highlight: String, link: String) -> FormekaModelSection {
        let row = FormekaModelRow(
            cellSetup: { indexPath, _, table in
                guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath)
                    else { return UITableViewCell() }

                cell.icon.image = #imageLiteral(resourceName: "shield")
                cell.icon.contentMode = .scaleAspectFit
                cell.icon.tintColor = .BaseWhite
                cell.content.attributedText = {
                    let htmlStrippedText = text.htmlStripped()

                    let string = NSMutableAttributedString(
                        string: htmlStrippedText,
                        attributes: [
                            .font: UIFont.BodySmall(),
                            .foregroundColor: UIColor.BaseWhite
                        ]
                    )

                    htmlStrippedText.ranges(of: highlight).forEach { (range) in
                        string.addAttribute(.foregroundColor, value: UIColor.BaseWhite, range: range)
                        string.addAttribute(.font, value: UIFont.BodySmall_Semibold(), range: range)
                    }

                    return string
                }()

                cell.contentView.backgroundColor = .ColourLD1
                cell.containerView.backgroundColor = .Tint2
                cell.containerView.layer.borderColor = UIColor.Tint2.cgColor
                cell.containerView.layer.cornerRadius = 0

                return cell
            },
            didSelect: { _, _ in
				guard let url = URL(string: link) else { return }
                self.openURLInSafari(url: url)
        }
        )

        return FormekaModelSection(header: nil, rows: [row], footer: footer(title: nil, height: 10))
    }

    func gdprFooterRow() -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SimpleSeparatorsCell = table.dequeueCell(for: indexPath) else { return UITableViewCell() }

            cell.selectionStyle = .none
            cell.hiddenSeparatorLocations = [.top, .bottom]
            cell.contentView.backgroundColor = .clear
            cell.backgroundColor = .clear
            cell.textLabel?.numberOfLines = 0
            cell.textLabel?.attributedText = {
                let text = PILocalizedString("privacyGenericFooter", comment: "GDPR Footer message").htmlStripped()

                let attributedString = NSMutableAttributedString(
                    string: text,
                    attributes: [
                        .font: UIFont.BodySmall(),
                        .foregroundColor: UIColor.ColourDL1,
                        NSAttributedString.Key.paragraphStyle: {
                            let style = NSMutableParagraphStyle()
                            style.lineSpacing = 3
                            style.alignment = .center

                            return style
                        }()
                    ]
                )

                text.ranges(of: PILocalizedString("privacyGenericFooterHighlightedText", comment: "")).forEach { (range) in
                    attributedString.addAttributes([
                        .font: UIFont.BodySmall_Medium(),
                        .foregroundColor: UIColor.ColourDL5
                    ], range: range)
                }

                return attributedString
            }()
            cell.textLabel?.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.privacyPolicyLink

            return cell
        }, didSelect: { _, _ in
            guard let url = Constants.privacyPolicyUrl else { return }
            self.openURLInSafari(url: url)
        })
    }
}

extension BaseViewController {
    func gdprShieldSection(for table: UITableView, backgroundColor: UIColor = .ColourLD6) -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        rows.append(FormekaModelRow(
            tag: Constants.horribleGDPRBannerRowCellTag,
            cellSetup: { indexPath, _, table in
                guard let cell: GDPRBannerHeaderRow = table.dequeueCell(for: indexPath) else { return UITableViewCell() }
                cell.contentView.backgroundColor = backgroundColor
                cell.hiddenSeparatorLocations = [.top, .bottom]

                cell.informationLabel.accessibilityLabel = AccessibilityIdentifiers.Shared.privacyDataPolicyButton

                return cell
            },
            didSelect: { [unowned self] _, _ in
                openPrivacyPolicyExternalLink()
            }
        )
        )

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }
}


extension UIViewController {
    func technologiesWeUseViewController(withEventHandler eventHandler: GDPRInterstitialEventHandler?)
        -> TechnologiesWeUseViewController? {
        guard let url = Bundle.main.url(forResource: "gdpr", withExtension: "html") else { return nil }
        guard let html = try? String(contentsOf: url, encoding: .utf8) else { return nil }

        // do not use Firebase override strings on purpose
        let dataUsageMessage = NSLocalizedString("dataUsageMessage", comment: "GDPR data usage content")
        let fullyFormedHTMLString = String(format: html, dataUsageMessage)

        let hasAcceptedGDPRChanges = UserDefaults.standard.bool(forKey: Constants.hasAcceptedGDPRChanges)

        let viewModel = TechnologiesWeUseViewModel(
            title: PILocalizedString("gdprDataPolicyTitle", comment: "GDPR data policy title"),
            html: fullyFormedHTMLString,
            screenName: PIAnalytics.StateNames.gdprHowWeUseData,
            screenType: PIAnalytics.StateTypes.gdprHowWeUseData
        )
        let controller = TechnologiesWeUseModule.build(
            withUserHavingAcceptedTermsAndConditions: hasAcceptedGDPRChanges,
            andViewModel: viewModel,
            and: eventHandler
        )
        controller.hidesBottomBarWhenPushed = true

        return controller
    }

    @objc private func gdprHeaderBannerOpenPrivacyPolicy() {
        openPrivacyPolicyExternalLink()
    }
}

extension UIViewController: WebViewControllerDelegate {
    @objc func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true, completion: nil)
    }
}
