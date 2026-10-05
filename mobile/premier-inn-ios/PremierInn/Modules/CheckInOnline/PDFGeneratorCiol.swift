//
//  PDFGeneratorCiol.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 13.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import PDFKit

enum PDFGeneratorError: Error {
    case generationFailed
    case pdfWriteFailed
    case encodingFailed
}

struct PDFBookingDetails {
    var transactionID: String
    let reservationID: String
    let profileID: String
    let hotelName: String
    let hotelAddress: String
    let arrivalDate: String
    let departureDate: String
    let guestList: [Guest]
}

class PDFGeneratorCiol {
    struct Result {
        let data: String
        let fileName: String
    }
    private let pdfBookingDetails: PDFBookingDetails

    private let topInset: CGFloat = 20
    private let leftInset: CGFloat = 40
    private let rightInset: CGFloat = 40
    private let bodyLineSpacing: CGFloat = 25
    private var lastItemYValue: CGFloat = 0

    private let headerAttributes = [NSAttributedString.Key.font: UIFont.Heading4_Bold()]
    private let bodyAttributes = [NSAttributedString.Key.font: UIFont.Body()]

    init(pdfBookingDetails: PDFBookingDetails) {
        self.pdfBookingDetails = pdfBookingDetails
    }

    func generateCiolPDF() throws -> Result {
        guard let pdfDocument = PDFDocument(data: generatePDFData()) else {
            throw PDFGeneratorError.generationFailed
        }
        let documentNumber = Int.random(in: 100000...999999)
        let fileName = "REG_RES\(pdfBookingDetails.reservationID)_\(documentNumber)_\(pdfBookingDetails.profileID).pdf"
        let outputURL = FileManager.default.temporaryDirectory.appendingPathComponent(fileName)

        let isDocumentWritten = pdfDocument.write(to: outputURL)
        guard isDocumentWritten else { throw PDFGeneratorError.pdfWriteFailed }

        do {
            let pdfData = try Data(contentsOf: outputURL)
            let base64String = pdfData.base64EncodedString()
            return .init(data: base64String, fileName: fileName)
        } catch {
            throw PDFGeneratorError.encodingFailed
        }
    }

    private func generatePDFData() -> Data {
        let pdfMetaData = [
            kCGPDFContextCreator: "Check-in online",
            kCGPDFContextAuthor: "premier inn"
        ]
        let format = UIGraphicsPDFRendererFormat()
        format.documentInfo = pdfMetaData as [String: Any]

        let pageWidth: CGFloat = 595
        let pageHeight: CGFloat = 842
        let pageRect = CGRect(x: 0, y: 0, width: pageWidth, height: pageHeight)

        let renderer = UIGraphicsPDFRenderer(bounds: pageRect, format: format)

        let data = renderer.pdfData { (context) in
            context.beginPage()

            // Title
            addTitle(pageRect: pageRect, title: PILocalizedString("ciolPDFRegCard"))
            addBookingDetails(pdfBookingDetails: pdfBookingDetails, context: context, pageWidth: pageWidth)
            // Guest Details
            pdfBookingDetails.guestList.enumerated().forEach { index, guest in
                switch guest.type {
                case .lead:
                    addLeadGuest(guest: guest, context: context, pageWidth: pageWidth)
                case .additional:
                    if lastItemYValue > 650 {
                        context.beginPage()
                        lastItemYValue = 0
                    }
                    addGuest(guest: guest, index: index)
                }
            }
        }
        return data
    }

    private func addTitle(pageRect: CGRect, title: String) {
        let attributedTitle = NSAttributedString(
            string: title,
            attributes: [NSAttributedString.Key.font: UIFont.Heading3_Bold()]
        )
        let titleStringSize = attributedTitle.size()
        let titleStringRect = CGRect(
            x: (pageRect.width - titleStringSize.width) / 2.0,
            y: topInset,
            width: titleStringSize.width,
            height: titleStringSize.height
        )
        attributedTitle.draw(in: titleStringRect)
    }

    private func addBookingDetails(
        pdfBookingDetails: PDFBookingDetails,
        context: UIGraphicsPDFRendererContext,
        pageWidth: CGFloat
    ) {
        lastItemYValue = topInset + 30
        let bookingTitle = PILocalizedString("ciolPDFYourBooking")
        bookingTitle.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: headerAttributes)

        lastItemYValue += 25
        addSeparator(context: context, pageWidth: pageWidth)

        lastItemYValue += 15
        let transactionID = "\(PILocalizedString("ciolPDFTransactionID")): \(pdfBookingDetails.transactionID)"
        transactionID.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let hotelName = "\(PILocalizedString("ciolPDFHotelName")): \(pdfBookingDetails.hotelName)"
        hotelName.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let hotelAddress = "\(PILocalizedString("ciolPDFHotelAddress")): \(pdfBookingDetails.hotelAddress)"
        hotelAddress.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let arrivalDate = "\(PILocalizedString("ciolPDFArrivalDate")): \(pdfBookingDetails.arrivalDate)"
        arrivalDate.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let departureDate = "\(PILocalizedString("ciolPDFDepartureDate")): \(pdfBookingDetails.departureDate)"
        departureDate.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += 40
        addSeparator(context: context, pageWidth: pageWidth)
    }

    private func addLeadGuest(
        guest: Guest,
        context: UIGraphicsPDFRendererContext,
        pageWidth: CGFloat
    ) {
        guard let guestFirstName = guest.firstName,
              let guestLastName = guest.lastName,
              !guestFirstName.isEmpty,
              !guestLastName.isEmpty else {
            return
        }

        lastItemYValue += 20
        let leadGuestTitle = PILocalizedString("ciolPDFGuestDetails")
        leadGuestTitle.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: headerAttributes)

        lastItemYValue += 30

        let firstName = "\(PILocalizedString("ciolPDFGuestFirstName")): \(guestFirstName)"
        firstName.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let lastName = "\(PILocalizedString("ciolPDFGuestLastName")): \(guestLastName)"
        lastName.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let homeAddress = "\(PILocalizedString("ciolPDFGuestHomeAddress")): \(guest.composedAddress)"
        homeAddress.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let postCode = "\(PILocalizedString("ciolPDFGuestPostcode")): \(guest.postCode)"
        postCode.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let city = "\(PILocalizedString("ciolPDFGuestCity")): \(guest.city)"
        city.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let country = "\(PILocalizedString("ciolPDFGuestCountry")): \(guest.countryName)"
        country.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let dob = "\(PILocalizedString("ciolPDFGuestDOB")): \(guest.dateOfBirth)"
        dob.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let nationality = "\(PILocalizedString("ciolPDFGuestNationality")): \(guest.nationality)"
        nationality.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let passportNumber = "\(PILocalizedString("ciolPDFGuest1PassportNumber")): \(guest.passport ?? "")"
        passportNumber.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += 40
        addSeparator(context: context, pageWidth: pageWidth)

        lastItemYValue += 20
        if pdfBookingDetails.guestList.count > 1 {
            let additionalGuestTitle = PILocalizedString("ciolPDFAdditionalGuests")
            additionalGuestTitle.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: headerAttributes)
        }
    }

    private func addGuest(guest: Guest, index: Int) {
        guard let guestFirstName = guest.firstName,
              let guestLastName = guest.lastName,
              !guestFirstName.isEmpty,
              !guestLastName.isEmpty else {
            return
        }

        lastItemYValue += 30
        let guestTitle = "\(PILocalizedString("ciolPDFGuest")) \(index)"
        guestTitle.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: headerAttributes)

        lastItemYValue += 30
        let firstName = "\(PILocalizedString("ciolPDFGuestFirstName")): \(guestFirstName)"
        firstName.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let lastName = "\(PILocalizedString("ciolPDFGuestLastName")): \(guestLastName)"
        lastName.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let dob = "\(PILocalizedString("ciolPDFGuestDOB")): \(guest.dateOfBirth)"
        dob.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)

        lastItemYValue += bodyLineSpacing
        let nationality = "\(PILocalizedString("ciolPDFGuestNationality")): \(guest.nationality)"
        nationality.draw(at: CGPoint(x: leftInset, y: lastItemYValue), withAttributes: bodyAttributes)
    }

    private func addSeparator(context: UIGraphicsPDFRendererContext, pageWidth: CGFloat) {
        context.cgContext.move(to: CGPoint(x: leftInset, y: lastItemYValue))
        context.cgContext.addLine(to: CGPoint(x: pageWidth - rightInset, y: lastItemYValue))
        context.cgContext.setStrokeColor(UIColor.TintL1.cgColor)
        context.cgContext.setLineWidth(1.0)
        context.cgContext.strokePath()
    }
}
