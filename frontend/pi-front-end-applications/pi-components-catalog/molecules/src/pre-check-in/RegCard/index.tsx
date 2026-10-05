import { format, isDate } from 'date-fns';
import { PDFDocument, PDFFont, RGB, rgb, StandardFonts } from 'pdf-lib';

import { DATE_FORMAT } from '../../utils/constants';
import { GuestDetailKeys } from '../common';
import { Dependent, Nationality, ReviewDataType } from '../types';

type Label = {
  label: string;
  value: string;
};

type Field = {
  heading?: string;
  subHeading?: string;
  labels: Label[];
};

export const generateRegCard = async ({
  t,
  transactionid,
  data,
  hotelAddress,
  signatureUrl,
  nationalities,
}: {
  t: (id: string) => string;
  transactionid: string;
  data?: Partial<ReviewDataType>;
  hotelAddress: string;
  signatureUrl?: string;
  nationalities: Nationality[];
}) => {
  const doc = await PDFDocument.create();

  const font = await doc.embedFont(StandardFonts.Helvetica);
  const fontBold = await doc.embedFont(StandardFonts.HelveticaBold);

  const {
    hotelName = '',
    arrivalDate,
    departureDate,
    firstName = '',
    lastName = '',
    address = '',
    postalCode = '',
    city = '',
    dateOfBirth,
    nationality = { label: '', value: '' },
    passport = '',
    country = '',
    noOfRooms = 0,
    roomNo = 0,
    dependents = [],
  } = data || {};
  let page = doc.addPage([600, 800]);

  // Get the width and height of the first page
  const { width, height } = page.getSize();

  // Heading
  const textWidth = font.widthOfTextAtSize(t('precheckin.regcard.title'), 14);
  font.heightAtSize(14);
  const textX = (width - textWidth) / 2;
  const textY = height - 30;
  page.drawText(t('precheckin.regcard.title'), {
    x: textX,
    y: textY,
    size: 14,
    font: fontBold,
  });

  page.drawLine({
    start: { x: 50, y: height - 45 },
    end: { x: 550, y: height - 45 },
    color: rgb(0.5, 0.5, 0.5),
    thickness: 0.6,
  });

  const formatDate = (date: Date | null, formatStr: string): string =>
    date ? format(date, formatStr) : '';

  const getLabel = (value: string, key: keyof Nationality) => {
    const nationality = nationalities.find((item) => item.value === value);
    return nationality ? nationality[key] : '';
  };
  const nationalityLabel = getLabel(nationality?.value, 'label');
  const countryLabel = getLabel(country, 'countryName');

  const createLabel = (label: string, value: string): Label => ({ label, value });

  const bookingDetailsLabels: Label[] = [
    createLabel('precheckin.yourbooking.details.transactionid', transactionid),
    createLabel('precheckin.yourbooking.details.hotelname', hotelName),
    createLabel('precheckin.regcard.hoteladdress', hotelAddress),
    createLabel(
      'precheckin.yourbooking.details.arrivaldate',
      arrivalDate ? formatDate(arrivalDate, 'do MMM yyyy zzz') : ''
    ),
    createLabel(
      'precheckin.yourbooking.details.departuredate',
      departureDate ? formatDate(departureDate, 'do MMM yyyy zzz') : ''
    ),
  ];

  const leadGuestDetailsLabels: Label[] = [
    createLabel('precheckin.regcard.firstname', firstName),
    createLabel('precheckin.regcard.lastname', lastName),
    createLabel('precheckin.regcard.homeaddress', address),
    createLabel('precheckin.regcard.postcode', postalCode),
    createLabel('precheckin.regcard.city', city),
    createLabel('precheckin.regcard.country', countryLabel),
    createLabel(
      'precheckin.additionalfields.dateofbirth',
      dateOfBirth ? getFormattedDate(dateOfBirth) : ''
    ),
    createLabel('precheckin.additionalfields.nationalities', nationalityLabel),
    createLabel('precheckin.details.passport', passport),
  ];

  const fields: Field[] = [
    {
      heading: t('precheckin.yourbooking'),
      labels: bookingDetailsLabels,
    },
    {
      heading:
        noOfRooms > 1
          ? `${t('precheckin.leadguest.title')} ${roomNo + 1}`
          : t('precheckin.yourdetails.title'),
      labels: leadGuestDetailsLabels,
    },
  ];

  const calculateY = (index = 0, startY = 0, lineHeight = 0) => startY - index * lineHeight;
  const startY = 760;
  const lineHeight = 10;
  let index = 0;

  const addNewPage = () => {
    if (index > 65) {
      page = doc.addPage([600, 800]);
      index = 0;
    }
  };

  // Dependent details
  if (dependents.length) {
    fields.push({ heading: t('precheckin.additionalguests.title'), labels: [] });
    fields.push(...generateDependentsData(t, dependents));
  }
  fields.forEach(({ heading, labels, subHeading }, fieldIndex) => {
    addNewPage();
    const drawText = ({
      text,
      size,
      font,
      x,
      yOffset,
      color,
    }: {
      text: string;
      size: number;
      font: PDFFont;
      x: number;
      yOffset: number;
      color?: RGB;
    }) => {
      page.drawText(text, {
        x,
        y: calculateY(index++, startY, lineHeight) + yOffset,
        size,
        font,
        color: color ?? rgb(0.1, 0.1, 0.1),
      });
    };

    const drawHeadingOrSubHeading = (
      text: string | undefined,
      size: number,
      yOffsetIncrement: number
    ) => {
      if (text) {
        drawText({ text, size, font: fontBold, x: 50, yOffset: 0 });
        index += yOffsetIncrement;
      }
    };

    drawHeadingOrSubHeading(heading, 14, labels.length ? 2.2 : 0);
    drawHeadingOrSubHeading(subHeading, 14, 1.3);

    labels.forEach(({ label, value }, labelIndex) => {
      if (value) {
        drawText({
          text: t(label) + ':',
          size: 12,
          font,
          x: 50,
          yOffset: 0,
          color: rgb(0.2, 0.2, 0.2),
        });
        drawText({
          text: value,
          size: 11,
          font,
          x: 200,
          yOffset: +lineHeight,
          color: rgb(0.3, 0.3, 0.3),
        });
      }
      if (labelIndex === labels.length - 1) index += 1.2;
    });

    if (labels.length && (!subHeading || fieldIndex === fields.length - 1)) {
      const yPos = calculateY(index, startY, lineHeight) + 10;
      page.drawLine({
        start: { x: 50, y: yPos },
        end: { x: 550, y: yPos },
        thickness: 0.5,
        color: rgb(0.8, 0.8, 0.8),
      });
    }
    if (subHeading) index += 0.7;
    else index += 1.7;
  });

  // Signature
  if (signatureUrl) {
    addNewPage();
    page.drawText(t('precheckin.yoursignature.title'), {
      x: 50,
      y: calculateY(index, startY, lineHeight),
      size: 13,
      font: fontBold,
    });
    index += 1.5;

    const signatureImage = await doc.embedPng(signatureUrl);

    const imageY = calculateY(index, startY, lineHeight) - signatureImage.height / 2;
    page.drawImage(signatureImage, {
      x: 50,
      y: imageY,
      width: signatureImage.width / 2,
      height: signatureImage.height / 2,
    });
  }
  const pdfBytes = await doc.save();
  return Buffer.from(pdfBytes).toString('base64');
};

const generateDependentsData = (t: (id: string) => string, dependents: Dependent[]) =>
  dependents
    .map(({ firstname, lastname, dateofbirth, ...args }) => ({
      firstname,
      lastname,
      dateofbirth,
      ...args,
    }))
    .map((guest, index) => {
      const guestDetails = Object.entries(guest)
        .filter(([, value]) => value)
        .map(([key, value]) => getGuestDetailsObject(key as GuestDetailKeys, value));

      return guestDetails.length
        ? { subHeading: `${t('precheckin.guest')} ${index + 1}`, labels: guestDetails }
        : { heading: '', labels: [] };
    })
    .filter(Boolean);

const getGuestDetailsObject = (key: GuestDetailKeys, value: any) => {
  const detailsMap = {
    [GuestDetailKeys.firstName]: value,
    [GuestDetailKeys.lastName]: value,
    [GuestDetailKeys.passport]: value,
    [GuestDetailKeys.dateOfBirth]: getFormattedDate(value),
    [GuestDetailKeys.nationality]: value?.label,
  };
  let label = '';
  switch (key) {
    case 'dateofbirth':
      label = `precheckin.additionalfields.${key}`;
      break;
    case 'passport':
      label = `precheckin.details.${key}`;
      break;
    case 'nationality':
      label = 'precheckin.additionalfields.nationalities';
      break;
    default:
      label = `precheckin.regcard.${key}`;
  }
  return { label, value: (detailsMap[key] as string) || '' };
};

const getFormattedDate = (date?: Date) => (date && isDate(date) ? format(date, DATE_FORMAT) : '');
