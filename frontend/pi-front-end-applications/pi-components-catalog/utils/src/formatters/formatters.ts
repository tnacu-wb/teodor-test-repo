import {
  Area,
  LanguageEnum,
  Currency,
  type BusinessNavItems,
  type SubNavCategoryItem,
  BB_MENU_IDS,
  SubNavCategory,
  BUSINESS_BOOKER_USER_ROLES,
  BookingDataReservationDetailsProps,
  ReservationById,
  DATE_TYPE,
  ReservationRoomType,
  AncillaryCloseout,
  Items,
  StaticContent,
  StayingGuest,
  Language,
  CountryCode,
  ShortCountry,
} from '@whitbread-eos/api';
import { format, parse, parseISO, subYears } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import parseHtml from 'html-react-parser';
import DOMPurify from 'isomorphic-dompurify';
import { parsePhoneNumberFromString } from 'libphonenumber-js';
import getConfig from 'next/config';

import { isStringValid, logicalOrOperator } from '../validators';
import { getPathForLocale } from './getPathForLocale';

export function getObjectFormatNumber(fullNumber: string, language: Language) {
  switch (language) {
    case CountryCode.EN:
      return {
        prefix: `+44`,
        phoneNumber: fullNumber,
        countryCode: ShortCountry.GB,
      };
    case CountryCode.DE:
      return {
        prefix: `+49`,
        phoneNumber: fullNumber,
        countryCode: ShortCountry.DE,
      };
  }
}

export function parsePhoneNumber(fullNumber: string, language: Language) {
  if (!fullNumber.includes('+')) {
    return getObjectFormatNumber(fullNumber, language);
  }

  const phoneDetails = parsePhoneNumberFromString(fullNumber);

  if (!phoneDetails) {
    return getObjectFormatNumber('', language);
  } else {
    return {
      prefix: `+${phoneDetails?.countryCallingCode}`,
      phoneNumber: phoneDetails?.nationalNumber,
      countryCode: phoneDetails?.country,
    };
  }
}

export function formatCurrency(currencyCode: string): string {
  switch (currencyCode) {
    case 'GBP':
      return Currency.GBP;
    case 'EUR':
      return Currency.EUR;
    default:
      return '';
  }
}

export function formatPrice(
  currency: string | undefined,
  price: number | string | undefined,
  language: string = LanguageEnum.ENGLISH
) {
  if (language === LanguageEnum.GERMAN && currency === Currency.EUR) {
    return `${price?.toString().replace('.', ',')}${currency}`;
  }
  return `${currency}${price}`;
}

// UK website: 1. UK => £00.00 2. DE + Ireland => €00.00
// DE website: 1. UL => £00.00 2. DE + Ireland => 00,00€
export function formatPriceWithDecimal(
  language: string,
  currency: string,
  price: number,
  decimals?: boolean
) {
  let priceWithDecimal = price % 1 === 0 ? price : price.toFixed(2);
  if (decimals) priceWithDecimal = price.toFixed(2);
  if (currency === Currency.GBP || language === LanguageEnum.ENGLISH) {
    return `${currency}${priceWithDecimal}`;
  }

  const updatedPrice = priceWithDecimal.toString().replace('.', ',');
  return `${updatedPrice} ${currency}`;
}

export function formatRatePrice(price: number) {
  return price - Math.floor(price) !== 0 ? price?.toFixed(2) : price;
}

export function getPriceValueWithDecimal(price: string) {
  // Strip non-numeric values
  const decimal = price?.toString?.().replace(/[^0-9.]+/g, '');

  if (!price || isNaN(Number(decimal)) || !decimal) {
    return 0.0;
  }

  //toFixed returns a string, so we convert it back to a number
  return Number(Number(decimal).toFixed(1));
}

export function formatDataTestId(prefix: string | null = '', dataTestId: string | null = '') {
  const previousString = isStringValid(prefix) ? `${prefix}-` : '';
  const testId = isStringValid(dataTestId) ? dataTestId : '';
  return `${previousString}${testId}`;
}

export function formatAssetsUrl(path: string, useBasicAuth = false) {
  const { publicRuntimeConfig = {} } = getConfig() || {};

  if (!path) return '';

  if (useBasicAuth) {
    return `${publicRuntimeConfig.NEXT_PUBLIC_ASSETS_URL}${path?.replace(
      publicRuntimeConfig.NEXT_PUBLIC_ASSETS_URL,
      ''
    )}`;
  }

  return `${publicRuntimeConfig.NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC}${path?.replace(
    publicRuntimeConfig.NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC,
    ''
  )}`;
}

export function formatInnerHTMLAssetUrls(innerHTML: string) {
  if (!innerHTML) {
    return '';
  }

  // return same string if no href attribute having a relative path is given
  if (!innerHTML.includes('href="/')) {
    return innerHTML;
  }

  // below regex matches globally patterns like: 'href="'
  // followed by a forward slash character and any characters that aren't double quotation marks (a relative path like '/example/doc')
  // followed by a dot character and 1 or more word characters (a file extension like '.pdf' or '.png')
  // but excludes .html files as they are site pages, not downloadable assets
  const hrefAttributePattern = /href="\/[^"]+?\.(?!html\b)\w+?"/g;
  return innerHTML.replace(
    hrefAttributePattern,
    (hrefAttribute) =>
      `href="${formatAssetsUrl(hrefAttribute.substring(6, hrefAttribute.length - 1))}"`
  );
}

export function formatDate(date: string, formatDate: string, language?: string) {
  // checking if received date has yyyy-MM-dd format
  // making sure the dates are all getting initially formatted in the same way
  // also using parseISO to make sure there are no timezone issues
  const isoDate = date.includes('-')
    ? parseISO(date)
    : parseISO(format(new Date(date), DATE_TYPE.YEAR_MONTH_DAY));
  return format(isoDate, formatDate, {
    locale: language === LanguageEnum.GERMAN ? de : enGB,
  });
}

export function checkDateFormatter(time: string, date: string, language: string) {
  if (language === LanguageEnum.ENGLISH) {
    return format(parseISO(`${date}T${time}`), 'ha - EEE d LLL yyyy', {
      locale: enGB,
    })
      .replace('AM', 'am')
      .replace('PM', 'pm');
  }

  return format(parseISO(`${date}T${time}`), 'H:mm - EEE d LLL yyyy', {
    locale: de,
  });
}

export function formatNextLocaleLink(
  pathName: string | undefined,
  nextLocale: string,
  currentLocale: string,
  area: Area
) {
  const homepage = `${nextLocale === LanguageEnum.ENGLISH ? '/gb/en/' : '/de/de/'}${
    area === Area.PI ? 'home.html' : ''
  }${area === Area.BB ? 'business-booker/home.html' : ''}`;
  const nextLocaleLink = nextLocale === LanguageEnum.ENGLISH ? '/gb' : '/de';

  pathName =
    nextLocale === LanguageEnum.GERMAN
      ? pathName?.replace('germany', 'deutschland')
      : pathName?.replace('deutschland', 'germany');

  const linkWithNextLocale = !pathName?.length ? '/gb/en' : `${nextLocaleLink}${pathName}`;

  // preserve the page if the user selects the current language
  if (currentLocale === nextLocale) {
    return linkWithNextLocale;
  }

  // Only some pages should preserve their path (SRP, the rest are redirected to homepage)
  // On HDP there are several issues if we preserve the path, so it was removed due to DNRQ-46951 ticket
  const pathPreservePages = new RegExp('home|search|pre-check-in');
  if (!pathName || !pathPreservePages.test(pathName)) {
    return homepage;
  }

  const linkWithNextCountry = linkWithNextLocale
    ?.replace('/gb/de', '/gb/en')
    ?.replace('/de/en', '/de/de');

  return linkWithNextCountry;
}

export function formatFindBookingToken(token: string) {
  if (isStringValid(token)) {
    return encodeURIComponent(token);
  }
  return token;
}

export function formatUrlTermsConditions(text: string) {
  if (!text) return '';
  const htmlSuffix = '.html';
  const htmlIndex = text.indexOf(htmlSuffix);
  if (htmlIndex === -1) return '';

  const slashIndex = text.indexOf('/');
  if (slashIndex === -1 || slashIndex > htmlIndex) return '';

  const path = text.slice(slashIndex, htmlIndex + htmlSuffix.length);
  const formattedPath = formatAssetsUrl(path);

  return text.slice(0, slashIndex) + formattedPath + text.slice(htmlIndex + htmlSuffix.length);
}

export function formatGuestTitleOptions(title = '') {
  const words = String(title).replace(/'/g, '').split(',');

  return words.map((option: string) => {
    return {
      id: option,
      label: option,
    };
  });
}

export function formatTextWithSpace(text: string) {
  return text.split(' ').join('-');
}

export function replaceWithEmptyString(receivedString?: string) {
  return receivedString?.replace(/[^\w]/g, '') ?? '';
}

export function uppercaseAndReplace(val: string, regex: RegExp, replaceStr: string) {
  return val.toUpperCase().replace(regex, replaceStr);
}

export function upperFirst(value: string): string {
  return `${value.charAt(0).toUpperCase()}${value.slice(1)}`;
}

export function upperOnlyFirst(value: string): string {
  return `${value.charAt(0).toUpperCase()}${value.slice(1).toLowerCase()}`;
}

export function swapKeysAndValues(obj: { key: string }) {
  const swapped = Object.entries(obj).map(([key, value]) => [value, key]);

  return Object.fromEntries(swapped);
}

export function formatForMobile(list: BusinessNavItems[]) {
  const firstLabels: BusinessNavItems[] = [];
  const lastLabels: BusinessNavItems[] = [];
  list.forEach((label) => {
    if (label.id === BB_MENU_IDS.COMPANY) {
      firstLabels.push({
        navTitle: label.navTitle,
        subNav: [],
        id: BB_MENU_IDS.COMPANY_NAME,
      });
      label.subNav?.[0].navOptions?.forEach((subLabel: SubNavCategoryItem) => {
        firstLabels.push({
          navTitle: subLabel.title,
          subNav: [
            {
              navOptions: (subLabel.subMenuLinks as SubNavCategoryItem[]) || [],
              title: subLabel.title,
            },
          ],
          id: BB_MENU_IDS.COMPANY,
          ...(subLabel.url && { url: subLabel.url }),
        });
      });
    } else {
      lastLabels.push(label);
    }
  });
  return [...firstLabels, ...lastLabels];
}
export function superRoleFilter(role: string, list: SubNavCategory[]) {
  if (role !== BUSINESS_BOOKER_USER_ROLES.SUPER) {
    return list?.map((item: SubNavCategory) => {
      const filtered = item?.navOptions?.filter((label: SubNavCategoryItem) => {
        return !label.superRole;
      });
      return { ...item, navOptions: filtered };
    });
  }
  return list;
}

export function langFilter(language: string, list: BusinessNavItems[]) {
  if (language === 'de') {
    return list?.filter((item: BusinessNavItems) => item.id !== BB_MENU_IDS.BUSINESS);
  }
  return list;
}

export function removeHtmlTags(inputString: string) {
  if (!inputString) return '';

  return DOMPurify.sanitize(inputString, { ALLOWED_TAGS: [] });
}

export function filterAncillaryCloseOutData(data: AncillaryCloseout) {
  if (!data?.items) {
    return [];
  }
  return data?.items?.filter((item: Items) => {
    if (item?.startDate && item?.endDate) {
      return (
        parse(item.endDate, 'dd/MM/yyyy', new Date()) >
        parse(item.startDate, 'dd/MM/yyyy', new Date())
      );
    }
  });
}

export function createReservationDetails(
  receivedArrivalDate: string | null,
  receivedDepartureDate: string | null,
  currencyCode: string,
  reservationByIdList: ReservationById[],
  noNights: number
): BookingDataReservationDetailsProps {
  const arrivalDate = logicalOrOperator(receivedArrivalDate, null);
  const departureDate = logicalOrOperator(receivedDepartureDate, null);
  const currency = currencyCode;
  const noRooms = reservationByIdList?.length;

  return {
    arrivalDate,
    departureDate,
    currency,
    noRooms,
    noNights,
  } as BookingDataReservationDetailsProps;
}

export function matchSilentSubstitutions(
  reservations: ReservationById[],
  substitutions: ReservationRoomType[]
): ReservationRoomType[] {
  const matchedSubstitutions = [] as ReservationRoomType[];
  const savedSubstitutions = [...substitutions];

  for (const room of reservations) {
    for (let i = 0; i < savedSubstitutions.length; i++) {
      const matched =
        savedSubstitutions[i]?.filtered !== true &&
        room?.roomStay?.adultsNumber === savedSubstitutions[i]?.adults &&
        room?.roomStay?.childrenNumber === savedSubstitutions[i]?.children &&
        room?.roomStay?.roomExtraInfo?.roomType === savedSubstitutions[i]?.pmsRoomType;

      if (matched) {
        const filteredSubstitution = {
          ...savedSubstitutions[i],
          filtered: true,
        };

        matchedSubstitutions.push(filteredSubstitution);
        savedSubstitutions[i] = filteredSubstitution;

        break;
      }
    }
  }

  return matchedSubstitutions;
}

export const getYearsAgoFromNow = (year: number) => subYears(new Date(), year); // Subtract n years from the current date

export function transformLabels(staticData: StaticContent) {
  const main = staticData?.labels?.main ? JSON.parse(staticData.labels.main) : {};
  const piBookings = staticData?.labels?.piBookings ? JSON.parse(staticData.labels.piBookings) : {};
  const booking = staticData?.labels?.booking ? JSON.parse(staticData.labels.booking) : {};
  const piPreCheckIn = staticData?.labels?.piPreCheckIn
    ? JSON.parse(staticData.labels.piPreCheckIn)
    : {};
  const piGroupBooking = staticData?.labels?.piGroupBooking
    ? JSON.parse(staticData.labels.piGroupBooking)
    : {};
  const extras = staticData?.labels?.extras ? JSON.parse(staticData.labels.extras) : {};
  const promotions = staticData?.labels?.promotions ? JSON.parse(staticData.labels.promotions) : {};

  return {
    ...main,
    ...piBookings,
    ...booking,
    ...piPreCheckIn,
    ...piGroupBooking,
    ...extras,
    ...promotions,
  };
}

export function renderSanitizedHtml(html: string) {
  const sanitizedHtml = DOMPurify.sanitize(html, { ADD_ATTR: ['target', 'style'] });
  const formattedHtml = formatInnerHTMLAssetUrls(sanitizedHtml);

  return parseHtml(formattedHtml);
}

export function formatBillingAddress(address: any, companyName?: string) {
  if (!address) {
    return null;
  }
  const addressObj = {
    addressLine1: address?.line1 ?? address?.addressLine1 ?? '',
    addressLine2: address?.line2 ?? address?.addressLine2 ?? '',
    addressLine3: address?.line3 ?? address?.addressLine3 ?? '',
    addressLine4: address?.line4 ?? address?.addressLine4 ?? '',
    cityName: address?.line4 ?? address?.addressLine4 ?? '',
    postalCode: address?.postCode ?? address?.postalCode ?? '',
    companyName: companyName ?? '',
    addressType: address?.addressType ?? address?.type ?? 'BUSINESS',
  };
  return { ...addressObj, country: address?.countryCode ?? address?.country ?? '' };
}

export function formatRequiredString(value: string) {
  return value?.replace(/\*/g, '') ?? '';
}

function formatGuestAddress(data: any, isSomeoneElseAndSingleRoomRedesign = false) {
  const leadGuest =
    data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign ? data.leadGuest[0] : data;
  return {
    cityName: leadGuest?.addressLine4,
    countryCode: leadGuest?.countryCode,
    addressLine1: leadGuest?.addressLine1,
    addressLine2: leadGuest?.addressLine2,
    addressLine3: leadGuest?.addressLine3,
    addressLine4: leadGuest?.addressLine4,
    postalCode: leadGuest?.postcodeAddress,
  };
}

export function formatAccompanyingGuest(guest: any, data: any) {
  const {
    accompanyingfirstName = undefined,
    accompanyinglastName = undefined,
    accompanyingtitle = undefined,
  } = data;

  return {
    ...guest,
    accompanyingGuestDetails: {
      title: accompanyingtitle,
      firstName: accompanyingfirstName,
      lastName: accompanyinglastName,
    },
  };
}

function formatLeadGuest(data: any, item: any, isAccompanyingGuestEnabled = false) {
  let guest: StayingGuest = {
    sameAsBooker: !!item.stayInThisRoom,
    stayingGuestDetails: {
      title: item.stayInThisRoom ? data.title : item.title,
      firstName: item.stayInThisRoom ? data.firstName : item.firstName,
      lastName: item.stayInThisRoom ? data.lastName : item.lastName,
      address: {
        cityName: item.stayInThisRoom ? data.addressLine4 : item.addressLine4,
        countryCode: item.stayInThisRoom ? data.countryCode : item.countryCode,
        addressLine1: item.stayInThisRoom ? data.addressLine1 : item.addressLine1,
        addressLine2: item.stayInThisRoom ? data.addressLine2 : item.addressLine2,
        addressLine3: item.stayInThisRoom ? data.addressLine3 : item.addressLine3,
        addressLine4: item.stayInThisRoom ? data.addressLine4 : item.addressLine4,
        postalCode: item.stayInThisRoom ? data.postcodeAddress : item.postcodeAddress,
      },
    },
  };

  if (isAccompanyingGuestEnabled && item) {
    guest = formatAccompanyingGuest(guest, item);
  }

  return guest;
}

export function formatGuests(
  data: any,
  isSomeoneElseAndSingleRoomRedesign = false,
  isAccompanyingGuestEnabled = false
) {
  const stayingGuests: any = [];
  if (data.leadGuest && data.leadGuest.length > 1) {
    data.leadGuest.map((item: any) => {
      const guest = formatLeadGuest(data, item, isAccompanyingGuestEnabled);
      stayingGuests.push(guest);
    });
  } else {
    let guest: StayingGuest = {
      sameAsBooker: data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign ? false : true,
      stayingGuestDetails: {
        title:
          data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign
            ? data.leadGuest[0].title
            : data.title,
        firstName:
          data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign
            ? data.leadGuest[0].firstName
            : data.firstName,
        lastName:
          data.bookingForSomeoneElse || isSomeoneElseAndSingleRoomRedesign
            ? data.leadGuest[0].lastName
            : data.lastName,
        address: formatGuestAddress(data, isSomeoneElseAndSingleRoomRedesign),
      },
    };

    if (isAccompanyingGuestEnabled && data?.leadGuest?.[0]) {
      guest = formatAccompanyingGuest(guest, data.leadGuest[0]);
    }

    stayingGuests.push(guest);
  }

  return stayingGuests;
}

export { getPathForLocale };
