//
//  InstructionsViewModel.swift
//  PremierInn
//
//  Created by Badea, Bogdan (Cognizant) on 20.05.2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import PassKit

struct InstructionsViewModel {
    // MARK: - FontAttributes

    private enum FontAttributes {
        static let body: [NSAttributedString.Key: UIFont] = [.font: .Body()]
        static let bodySmall = [NSAttributedString.Key.font: UIFont.BodySmall()]
        static let bodySmallBold = [NSAttributedString.Key.font: UIFont.BodySmall_Bold()]
        static let heading4Bold = [NSAttributedString.Key.font: UIFont.Heading4_Bold()]
    }

    // MARK: - Properties

    var type: InstructionsType
    var bookingReference: String?
    var reservationDetails: ReservationDetails?
    var isQRCodeEnabled: Bool?
    var savedPass: PKPass?
}

// MARK: - Properties

extension InstructionsViewModel {
    var title: String {
        switch type {
        case .roomKeyWithQRCode, .roomKeyWithoutQRCode, .roomWithQRCodeAndDigitalKey, .roomWithoutQRCodeButWithDigitalKey:
            return Instruction.ciolGetRoomKey
        case .gettingDigitalKey:
            return Instruction.digitalKeyGettingKeyTitle
        case .usingDigitalKey:
            return Instruction.digitalKeyUsingMyKeyTitle
        }
    }

    var titleLogo: UIImage? {
        switch type {
        case .gettingDigitalKey, .usingDigitalKey:
            return Instruction.qrCodePILogo
        default:
            return nil
        }
    }

    var isAddToAppleWalletHidden: Bool {
        hidesAppleWallet || savedPass != nil
    }

    var isViewInAppleWalletHidden: Bool {
        savedPass == nil
    }

    var disclaimer: String? {
        switch type {
        case .gettingDigitalKey, .usingDigitalKey, .roomKeyWithoutQRCode, .roomWithoutQRCodeButWithDigitalKey:
            return nil
        case .roomKeyWithQRCode, .roomWithQRCodeAndDigitalKey:
            return Instruction.qrCodeDisclaimer
        }
    }

    var formattedBookingReference: String? {
        guard let bookingReference else { return nil }
        return "\(Instruction.bookingConfirmationLabel): \(bookingReference)"
    }

    var imageContentMode: UIView.ContentMode {
        switch type {
        case .gettingDigitalKey, .usingDigitalKey:
            return UIView.ContentMode.scaleAspectFill
        case .roomKeyWithQRCode, .roomKeyWithoutQRCode, .roomWithQRCodeAndDigitalKey, .roomWithoutQRCodeButWithDigitalKey:
            return UIView.ContentMode.scaleAspectFit
        }
    }

    var qrImage: UIImage? {
        switch type {
        case .roomKeyWithQRCode, .roomWithQRCodeAndDigitalKey:
            guard let bookingReference else { return nil }
            return generateQRCode(from: bookingReference)
        case .roomKeyWithoutQRCode, .roomWithoutQRCodeButWithDigitalKey:
            return nil
        case .gettingDigitalKey:
            return Instruction.gettingDigitalKeyImage
        case .usingDigitalKey:
            let fileName = LanguageManager.supportedLanguage == .english
            ? Instruction.usingDigitalKeyEN
            : Instruction.usingDigitalKeyDE
            return UIImage(named: fileName)
        }
    }

    var size: InstructionsSheetSize {
        switch type {
        case .roomKeyWithQRCode, .roomWithQRCodeAndDigitalKey:
            return .fullSize
        case .roomWithoutQRCodeButWithDigitalKey(let isKeyDownloaded):
            let sheetHeight = isKeyDownloaded
                              ? Instruction.sheetSizeForRoomWithDownloadedDigitalKey
                              : Instruction.sheetSizeForRoomWithDigitalKey

            return .custom(height: sheetHeight)
        case .roomKeyWithoutQRCode, .gettingDigitalKey, .usingDigitalKey:
            return .custom(height: Instruction.sheetSizeForRoomWithoutQRCode)
        }
    }

    var topDescription: NSAttributedString? {
        switch type {
        case .roomKeyWithQRCode:
            return descriptionForRoomKeyWithQRCode(forTopInstructions: true)
        case .roomKeyWithoutQRCode:
            return nil
        case .usingDigitalKey:
            return descriptionForUsingDigitalKey()
        case .gettingDigitalKey(let isKioskAvailable, let isDESite):
            return descriptionGettingDigitalKey(isKioskAvailable: isKioskAvailable,
                                                isDESite: isDESite)
        case .roomWithQRCodeAndDigitalKey(let isKeyDownloaded):
            return descriptionForRoomWithQRCodeAndDigitalKey(forTopInstructions: true,
                                                             isKeyDownloaded: isKeyDownloaded)
        case .roomWithoutQRCodeButWithDigitalKey(let isKeyDownloaded):
            return descriptionForRoomWithoutQRCodeButWithDigitalKey(forTopInstructions: true,
                                                                    isKeyDownloaded: isKeyDownloaded)
        }
    }

    var bottomDescription: NSAttributedString? {
        switch type {
        case .roomKeyWithQRCode:
            return descriptionForRoomKeyWithQRCode(forTopInstructions: false)
        case .roomWithQRCodeAndDigitalKey(let isKeyDownloaded):
            return descriptionForRoomWithQRCodeAndDigitalKey(forTopInstructions: false,
                                                             isKeyDownloaded: isKeyDownloaded)
        case .roomWithoutQRCodeButWithDigitalKey(let isKeyDownloaded):
            return descriptionForRoomWithoutQRCodeButWithDigitalKey(forTopInstructions: false,
                                                                    isKeyDownloaded: isKeyDownloaded)
        case .roomKeyWithoutQRCode:
            return descriptionForRoomKeyWithoutQRCode()
        case .gettingDigitalKey, .usingDigitalKey:
            return nil
        }
    }
}

// MARK: - Helpers

private extension InstructionsViewModel {
    var hidesAppleWallet: Bool {
        switch type {
        case .roomKeyWithoutQRCode, .roomWithoutQRCodeButWithDigitalKey, .usingDigitalKey, .gettingDigitalKey:
            return true
        case .roomKeyWithQRCode, .roomWithQRCodeAndDigitalKey:
            return false
        }
    }

    func attributedString(from instruction: Instruction?, with highlights: [Instruction]) -> NSAttributedString? {
        guard let instruction else { return nil }

        let attributedString = NSMutableAttributedString(string: instruction.text, attributes: instruction.attribute)

        for highlight in highlights {
            attributedString.style(text: highlight.text, withAttributes: highlight.attribute)
        }

        return attributedString
    }

    func descriptionGettingDigitalKey(isKioskAvailable: Bool, isDESite: Bool) -> NSAttributedString? {
        var fullText: String
        let boldPointOneText = Instruction.gettingDigitalKeyTitle
        let subtitle = isDESite ? Instruction.gettingDigitalKeySecondTitleDESite : Instruction.gettingDigitalKeySecondTitle
        let boldPointTwoText = PILocalizedString(subtitle)

        let kioskText = isDESite
                        ? Instruction.gettingDigitalKeyWithKioskMessageDESite
                        : Instruction.gettingDigitalKeyWithKioskMessage
        let noKioskText = isDESite
                          ? Instruction.gettingDigitalKeyNoKioskMessageDESite
                          : Instruction.gettingDigitalKeyNoKioskMessage

        if isKioskAvailable {
            fullText = PILocalizedString(kioskText)
        } else {
            fullText = PILocalizedString(noKioskText)
        }

        let text = Instruction(text: fullText, attribute: FontAttributes.body)
        let highlights = [
            Instruction(text: boldPointOneText, attribute: FontAttributes.heading4Bold),
            Instruction(text: boldPointTwoText, attribute: FontAttributes.heading4Bold)
        ]

        return attributedString(from: text, with: highlights)
    }

    func descriptionForUsingDigitalKey() -> NSAttributedString? {
        let fullText = NSMutableAttributedString()

        let pointOneAttributed = NSMutableAttributedString.iconText(
            icon: Instruction.circularBgOfficesIcon,
            text: Instruction.pointOneText,
            boldText: Instruction.boldPointOneText
        )

        let pointTwoAttributed = NSMutableAttributedString.iconText(
            icon: Instruction.circularBgSixtySecondsIcon,
            text: Instruction.pointTwoText,
            boldText: Instruction.boldPointTwoText
        )

        let pointThreeAttributed = NSMutableAttributedString.iconText(
            icon: Instruction.circularBgDigitalKeyIcon,
            text: Instruction.pointThreeText,
            boldText: Instruction.boldPointThreeText
        )

        let pointFourAttributed = NSMutableAttributedString.iconText(
            icon: Instruction.circularBgLightBulbIcon,
            text: Instruction.pointFourText,
            boldText: Instruction.boldPointFourText
        )

        let pointFiveAttributed = NSMutableAttributedString.iconText(
            icon: Instruction.circularBgPhoneIcon,
            text: Instruction.pointFiveText,
            boldText: Instruction.boldPointFiveText
        )

        fullText.append(pointOneAttributed)
        fullText.append(pointTwoAttributed)
        fullText.append(pointThreeAttributed)
        fullText.append(pointFourAttributed)
        fullText.append(pointFiveAttributed)

        return fullText
    }

    func descriptionForRoomWithoutQRCodeButWithDigitalKey(forTopInstructions: Bool,
                                                          isKeyDownloaded: Bool) -> NSAttributedString? {
        let text = textForRoomWithoutQRCodeButWithDigitalKey(forTopInstructions: forTopInstructions,
                                                             isKeyDownloaded: isKeyDownloaded)
        let highlights = highlightsWithoutQRCodeButWithDigitalKey(forTopInstructions: forTopInstructions,
                                                                  isKeyDownloaded: isKeyDownloaded)

        return attributedString(from: text, with: highlights)
    }

    func textForRoomWithoutQRCodeButWithDigitalKey(forTopInstructions: Bool, isKeyDownloaded: Bool) -> Instruction? {
        var fullText: String

        if forTopInstructions {
            if isKeyDownloaded {
                fullText = Instruction.roomKeyDownloadCheckAndSavedMessage
            } else {
                fullText = Instruction.whyNotSkipTheQueueDownloadMessage
            }
        } else {
            if isKeyDownloaded {
                return nil
            } else {
                fullText = Instruction.headToReception
            }
        }

        return Instruction(text: fullText, attribute: FontAttributes.body)
    }

    func highlightsWithoutQRCodeButWithDigitalKey(forTopInstructions: Bool, isKeyDownloaded: Bool) -> [Instruction] {
        var highlights: [Instruction] = []

        if forTopInstructions {
            var digitalKeyTitle: String
            var digitalKeyInfoText: String

            if isKeyDownloaded {
                digitalKeyTitle = Instruction.roomKeyDownloadCheck
                digitalKeyInfoText = Instruction.alreadyCheckedAndSavedMessage
            } else {
                digitalKeyTitle = Instruction.whyNotSkipTheQueue
                digitalKeyInfoText = Instruction.downloadKeyToSaveTime

                highlights = [
                    Instruction(text: Instruction.storeKeyInIphone, attribute: FontAttributes.bodySmallBold),
                    Instruction(text: Instruction.saveKeyToWalletMessage, attribute: FontAttributes.bodySmall)
                ]
            }

            highlights += [
                Instruction(text: digitalKeyTitle, attribute: FontAttributes.bodySmallBold),
                Instruction(text: digitalKeyInfoText, attribute: FontAttributes.bodySmall)
            ]
        } else {
            if !isKeyDownloaded {
                highlights = [Instruction(text: Instruction.headToReceptionTime, attribute: FontAttributes.heading4Bold)]
            }
        }

        return highlights
    }

    func descriptionForRoomKeyWithoutQRCode() -> NSAttributedString? {
        let text = Instruction(text: Instruction.headToReception, attribute: FontAttributes.body)
        let highlights = [Instruction(text: Instruction.headToReceptionTime, attribute: FontAttributes.heading4Bold)]

        return attributedString(from: text, with: highlights)
    }

    func descriptionForRoomWithQRCodeAndDigitalKey(forTopInstructions: Bool,
                                                   isKeyDownloaded: Bool) -> NSAttributedString? {
        let text = textForRoomWithQRCodeAndDigitalKey(forTopInstructions: forTopInstructions,
                                                      isKeyDownloaded: isKeyDownloaded)
        let highlights = highlightsForRoomWithQRCodeAndDigitalKey(forTopInstructions: forTopInstructions,
                                                                  isKeyDownloaded: isKeyDownloaded)

        return attributedString(from: text, with: highlights)
    }

    func textForRoomWithQRCodeAndDigitalKey(forTopInstructions: Bool, isKeyDownloaded: Bool) -> Instruction? {
        var fullText: String
        var attribute: [NSAttributedString.Key: UIFont]

        if forTopInstructions {
            if isKeyDownloaded {
                fullText = Instruction.roomKeyDownloadCheckMessage
            } else {
                fullText = Instruction.whyNotSkipTheQueueMessage
            }

            attribute = FontAttributes.body
        } else {
            if isKeyDownloaded {
                return nil
            } else {
                fullText = Instruction.noKioskAvailableMessage
                attribute = FontAttributes.bodySmallBold
            }
        }

        return Instruction(text: fullText, attribute: attribute)
    }

    func highlightsForRoomWithQRCodeAndDigitalKey(forTopInstructions: Bool, isKeyDownloaded: Bool) -> [Instruction] {
        var highlights: [Instruction] = []

        if forTopInstructions {
            if isKeyDownloaded {
                highlights = [
                    Instruction(text: Instruction.roomKeyDownloadCheck, attribute: FontAttributes.bodySmallBold),
                    Instruction(text: Instruction.alreadyCheckedAndAddedMessage, attribute: FontAttributes.bodySmall)
                ]
            } else {
                highlights = [
                    Instruction(text: Instruction.whyNotSkipTheQueue, attribute: FontAttributes.bodySmallBold),
                    Instruction(text: Instruction.storeKeyInIphone, attribute: FontAttributes.bodySmallBold),
                    Instruction(text: Instruction.keyCollectionFromKiosk, attribute: FontAttributes.bodySmallBold),
                    Instruction(text: Instruction.checkinToSaveTimeMessage, attribute: FontAttributes.bodySmall),
                    Instruction(text: Instruction.saveKeyToWalletMessage, attribute: FontAttributes.bodySmall),
                    Instruction(text: Instruction.scanTheQRCode, attribute: FontAttributes.bodySmall)
                ]
            }
        } else {
            if !isKeyDownloaded {
                highlights = [
                    Instruction(text: Instruction.noKioskAvailable, attribute: FontAttributes.bodySmallBold),
                    Instruction(text: Instruction.headToReception, attribute: FontAttributes.bodySmall),
                    Instruction(text: Instruction.headToReceptionTime, attribute: FontAttributes.heading4Bold)
                ]
            }
        }

        return highlights
    }

    func descriptionForRoomKeyWithQRCode(forTopInstructions: Bool) -> NSAttributedString? {
        let fullText = forTopInstructions ? Instruction.keyCollectionFromKioskMessage : Instruction.noKioskAvailableMessage
        let smallBoldTitle = forTopInstructions ? Instruction.keyCollectionFromKiosk : Instruction.noKioskAvailable
        let smallBodyDescription = forTopInstructions ? Instruction.scanTheQRCode : Instruction.headToReception

        let text = Instruction(text: fullText, attribute: FontAttributes.body)
        let highlights: [Instruction] = [
            Instruction(text: smallBoldTitle, attribute: FontAttributes.bodySmallBold),
            Instruction(text: smallBodyDescription, attribute: FontAttributes.bodySmall),
            Instruction(text: Instruction.headToReceptionTime, attribute: FontAttributes.bodySmallBold)
        ]

        return attributedString(from: text, with: highlights)
    }
}
