//
//  InstructionsViewModel+Instruction.swift
//  PremierInn
//
//  Created by Badea, Bogdan (Cognizant) on 22.05.2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

struct Instruction {
    // MARK: - Constants

    static let gettingDigitalKeySecondTitle = "digitalKeyGettingKeyBoldTwo"
    static let gettingDigitalKeySecondTitleDESite = "digitalKeyGettingKeyBoldTwoDESite"
    static let gettingDigitalKeyWithKioskMessageDESite = "digitalKeyGettingKeyWithKioskFullTextDESite"
    static let gettingDigitalKeyWithKioskMessage = "digitalKeyGettingKeyWithKioskFullText"
    static let gettingDigitalKeyNoKioskMessageDESite = "digitalKeyGettingKeyNoKioskFullTextDESite"
    static let gettingDigitalKeyNoKioskMessage = "digitalKeyGettingKeyNoKioskFullText"
    static let usingDigitalKeyEN = "digitalUsingKey"
    static let usingDigitalKeyDE = "digitalUsingKeyGerman"

    // MARK: - Localized Strings

    static let headToReception = PILocalizedString("RKI_HeadToReception")
    static let headToReceptionTime = PILocalizedString("RKI_HeadToReceptionTime")
    static let noKioskAvailable = PILocalizedString("RKI_NoKioskAvailable")
    static let noKioskAvailableMessage = PILocalizedString("RKI_NoKioskAvailableMessage")
    static let scanTheQRCode = PILocalizedString("RKI_ScanTheQRCode")
    static let keyCollectionFromKiosk = PILocalizedString("RKI_KeyCollectionFromKiosk")
    static let keyCollectionFromKioskMessage = PILocalizedString("RKI_KeyCollectionFromKioskMessage")
    static let roomKeyDownloadCheck = PILocalizedString("RKI_RoomKeyDownloadCheck")
    static let roomKeyDownloadCheckMessage = PILocalizedString("RKI_RoomKeyDownloadCheckMessage")
    static let roomKeyDownloadCheckAndSavedMessage = PILocalizedString("RKI_RoomKeyDownloadCheckAndSavedMessage")
    static let alreadyCheckedAndAddedMessage = PILocalizedString("RKI_AlreadyCheckedAndAddedMessage")
    static let alreadyCheckedAndSavedMessage = PILocalizedString("RKI_AlreadyCheckedAndSavedMessage")
    static let whyNotSkipTheQueue = PILocalizedString("RKI_WhyNotSkipTheQueue")
    static let whyNotSkipTheQueueMessage = PILocalizedString("RKI_WhyNotSkipTheQueueMessage")
    static let whyNotSkipTheQueueDownloadMessage = PILocalizedString("RKI_WhyNotSkipTheQueueDownloadMessage")
    static let storeKeyInIphone = PILocalizedString("RKI_StoreKeyInIphone")
    static let saveKeyToWalletMessage = PILocalizedString("RKI_SaveKeyToWalletMessage")
    static let checkinToSaveTimeMessage = PILocalizedString("RKI_CheckinToSaveTimeMessage")
    static let downloadKeyToSaveTime = PILocalizedString("RKI_DownloadKeyToSaveTime")
    static let gettingDigitalKeyTitle = PILocalizedString("digitalKeyGettingKeyBoldOne")
    static let pointOneText = PILocalizedString("digitalKeyUsingMyKeyTextOne")
    static let pointTwoText = PILocalizedString("digitalKeyUsingMyKeyTextTwo")
    static let pointThreeText = PILocalizedString("digitalKeyUsingMyKeyTextThree")
    static let pointFourText = PILocalizedString("digitalKeyUsingMyKeyTextFour")
    static let pointFiveText = PILocalizedString("digitalKeyUsingMyKeyTextFive")
    static let boldPointOneText = PILocalizedString("digitalKeyUsingMyKeyBoldTextOne")
    static let boldPointTwoText = PILocalizedString("digitalKeyUsingMyKeyBoldTextTwo")
    static let boldPointThreeText = PILocalizedString("digitalKeyUsingMyKeyBoldTextThree")
    static let boldPointFourText = PILocalizedString("digitalKeyUsingMyKeyBoldTextFour")
    static let boldPointFiveText = PILocalizedString("digitalKeyUsingMyKeyBoldTextFive")
    static let bookingConfirmationLabel = PILocalizedString("bookingConfirmationReferenceLabelTitle")
    static let qrCodeDisclaimer = PILocalizedString("qrCodeDisclaimer")
    static let ciolGetRoomKey = PILocalizedString("ciolGetRoomKey")
    static let digitalKeyGettingKeyTitle = PILocalizedString("digitalKeyGettingKeyTitle")
    static let digitalKeyUsingMyKeyTitle = PILocalizedString("digitalKeyUsingMyKeyTitle")

    // MARK: - Icons

    static let circularBgOfficesIcon = UIImage(named: "circularBgOfficesIcon")!
    static let circularBgSixtySecondsIcon = UIImage(named: "circularBgSixtySecondsIcon")!
    static let circularBgDigitalKeyIcon = UIImage(named: "circularBgDigitalKeyIcon")!
    static let circularBgLightBulbIcon = UIImage(named: "circularBgLightBulbIcon")!
    static let circularBgPhoneIcon = UIImage(named: "circularBgPhoneIcon")!
    static let gettingDigitalKeyImage = UIImage(named: "digitalGettingKey")
    static let qrCodePILogo = UIImage(named: "QRCodePILogo")

    // MARK: - SheetSize

    static let sheetSizeForRoomWithDownloadedDigitalKey: CGFloat = 300
    static let sheetSizeForRoomWithDigitalKey: CGFloat = 380
    static let sheetSizeForRoomWithoutQRCode: CGFloat = 200

    // MARK: - Properties

    let text: String
    let attribute: [NSAttributedString.Key: Any]
}
