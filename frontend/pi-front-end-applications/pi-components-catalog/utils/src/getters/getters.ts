import {
  BART_ROOM_TYPE_LABELS_OBJ,
  BCReservationListItem,
  ReservationById,
  Country,
  CountryWithIso,
  DonationPackage,
  DpaInfo,
  HIRoomType,
  LanguageEnum,
  PackageSelection,
  RoomSelection,
  RoomTypeLabelCode,
  SearchBookingsSessionStorage,
  SearchRoomType,
  SearchSummaryLabels,
  ShortCountry,
  SOURCE_SYSTEM,
  Packages,
  BartRoomStay,
  ReservationDetails,
  Rooms,
  PackageDetails,
  MealsSelection,
  RoomDetails,
  ExtrasPackages,
  ExtrasId,
  PREMIER_PLUS_ROOM_TYPES,
  ExtrasItem,
  BookingInformation,
  type Claims,
  DAY_TODAY,
  DAY_TOMORROW,
  EARLY_CHECKIN_IDS,
  LATE_CHECKOUT_IDS,
  PROSECCO_IDS,
  WIFI_IDS,
} from '@whitbread-eos/api';
import { add, differenceInDays, format } from 'date-fns';
import getConfig from 'next/config';
import React from 'react';

import { formatCurrency, formatPrice } from '../formatters';
import { decodeFromBase64 } from '../helpers';

export function getNightsNumber(arrivalDate: string, departureDate: string): number {
  const arrival = new Date(`${arrivalDate}T00:00:00`);
  const departure = new Date(`${departureDate}T23:59:59`);
  return differenceInDays(departure, arrival);
}

export function getNoOfDaysInYear(year: number): number {
  return (year % 4 === 0 && year % 100 > 0) || year % 400 == 0 ? 366 : 365;
}

export function getDistanceUnitBasedOnLocale(locale: string) {
  return locale === LanguageEnum.ENGLISH ? 'MILES' : 'KILOMETERS';
}

export function getDistanceUnitBasedOnLocaleSb(locale: string) {
  return locale === LanguageEnum.ENGLISH ? 'mi' : 'km';
}

export function getCardEnding(cardToken: string | undefined) {
  return cardToken?.substring(cardToken?.length - 4);
}

export function getCentrallyStoredCardBillingAddress(
  cardToken: string | undefined,
  storedCards: {
    cardToken: string;
    billingAddress: Record<string, string>;
  }[]
) {
  if (cardToken) {
    const storedCard = storedCards.find((item) => item.cardToken === cardToken);
    return storedCard?.billingAddress ?? {};
  }
  return {};
}

export function getSecureTwoURL() {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  if (Object.keys(publicRuntimeConfig).includes('NEXT_PUBLIC_SECURE2_URL')) {
    return publicRuntimeConfig.NEXT_PUBLIC_SECURE2_URL;
  } else {
    return typeof window !== 'undefined'
      ? window.location.hostname.replace(/^www/gim, 'https://secure2')
      : '';
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function getSortedCountries(data: any) {
  if (!data || data?.countries.length === 0) {
    return;
  }

  const sortByCountryLegend = (a: { countryLegend: string }, b: { countryLegend: string }) =>
    a.countryLegend > b.countryLegend ? 1 : -1;
  const sortCountries = (countries: []) => countries?.sort(sortByCountryLegend);
  const sortedCountries = sortCountries(data.countries);

  return sortedCountries.filter((value, index, self) => {
    const currentIndex = self.findIndex((c) => c['countryCodeISO'] === value['countryCodeISO']);
    return index === currentIndex;
  });
}

export function getSortedCountriesByCurrentLang(data: Country[], currentLang: string) {
  if (!data || data?.length === 0) {
    return [];
  }

  const currentCountrySite =
    currentLang === LanguageEnum.GERMAN ? ShortCountry.DE : ShortCountry.GB;
  const sortCountriesByCurrentLang = (a: Country, b: Country) => {
    // Keep UK or DE always on top

    if (a.countryCode === currentCountrySite) {
      return -1;
    }

    if (b.countryCode === currentCountrySite) {
      return 1;
    }

    if (a.countryName < b.countryName) {
      return -1;
    }

    if (a.countryName > b.countryName) {
      return 1;
    }

    return 0;
  };

  return data.sort(sortCountriesByCurrentLang);
}

export function getSortedCountriesByCurrentLangCcui(data: CountryWithIso[], currentLang: string) {
  if (!data || data?.length === 0) {
    return [];
  }

  const currentCountrySite =
    currentLang === LanguageEnum.GERMAN ? ShortCountry.DE : ShortCountry.GB;
  const sortCountriesByCurrentLang = (a: CountryWithIso, b: CountryWithIso) => {
    // Keep UK or DE always on top

    if (a.countryCodeISO === currentCountrySite) {
      return -1;
    }

    if (b.countryCodeISO === currentCountrySite) {
      return 1;
    }

    if (a.countryLegend < b.countryLegend) {
      return -1;
    }

    if (a.countryLegend > b.countryLegend) {
      return 1;
    }

    return 0;
  };
  return data.sort(sortCountriesByCurrentLang);
}

export function getMaxValueFromRoomStays(
  data: ReservationById[],
  field: 'adultsNumber' | 'childrenNumber'
): number {
  return data?.reduce((cur: number, val: ReservationById) => {
    if (!val?.roomStay) return cur; // Check for null or undefined values and handle accordingly
    const fieldValue = val.roomStay[field]; // Access the field value
    if (fieldValue !== undefined && fieldValue !== null) {
      // Ensure field value is not null or undefined
      return Math.max(cur, fieldValue); // Use Math.max to get the maximum value
    } else {
      return cur; // If field value is null or undefined, return current maximum
    }
  }, 0); // Set initial maximum to 0
}

export function getMaxValueFromRoomStaysBC(
  data: BCReservationListItem[],
  field: 'adultsNumber' | 'childrenNumber'
): number {
  return data?.reduce((cur: number, val: BCReservationListItem) => {
    return cur < val.roomStay[field] ? val.roomStay[field] : cur;
  }, 0);
}

export function getMaxValueFromHDPRoomTypes(
  data: HIRoomType[],
  field: 'adults' | 'children'
): number {
  return data?.reduce((cur: number, val: HIRoomType) => {
    return cur < val[field] ? val[field] : cur;
  }, 0);
}

export function getTotalPeople(rooms: SearchRoomType[]) {
  return rooms.reduce(
    (acc: { adults: number; children: number }, cur: SearchRoomType) => {
      acc.adults += +cur.adults;
      acc.children += +cur.children;
      return acc;
    },
    { adults: 0, children: 0 }
  );
}

export function getPlaceholderString(
  obj: { adults: number; children: number; rooms: number },
  labels: SearchSummaryLabels
) {
  const { adult, adults, child, children, room, rooms } = labels;

  const adultsLabel = obj.adults === 1 ? adult : adults;
  const childrenObj = obj.children === 1 ? child : children;
  const childrenLabel = obj.children === 0 ? '' : childrenObj;
  const roomsLabel = obj.rooms === 1 ? room : rooms;

  return obj.children === 0
    ? `${obj.adults} ${adultsLabel}, ${obj.rooms} ${roomsLabel}`
    : `${obj.adults} ${adultsLabel}, ${obj.children} ${childrenLabel}, ${obj.rooms} ${roomsLabel}`;
}

export function getRoomsPlaceholder(rooms: SearchRoomType[], labels: SearchSummaryLabels) {
  if (!rooms?.length) return;

  const totalRooms = rooms?.length;
  const totalPeople = getTotalPeople(rooms);

  const placeholderStr = getPlaceholderString({ ...totalPeople, rooms: totalRooms }, labels);
  return placeholderStr;
}

export function getSelectedDonationPackage(
  donationPackages: DonationPackage[],
  roomSelection: RoomSelection
) {
  return donationPackages.find((donationPack: DonationPackage) => {
    return (roomSelection?.packagesSelection ?? []).find(
      (pkg: PackageSelection) => pkg.id === donationPack.code
    );
  });
}

export const getIDVPassedStatus = (dpaInfoParam: DpaInfo) => {
  let idvPassedStatus = false;
  if (
    (dpaInfoParam.dpaPassed && !dpaInfoParam.dpaOverride) ||
    (!dpaInfoParam.dpaPassed && dpaInfoParam.dpaOverride)
  ) {
    idvPassedStatus = true;
  }
  return idvPassedStatus;
};

export function getBartRoomTypeLabels(
  BART_ROOM_TYPE_LABELS_OBJ: Record<string, string>,
  t: (arg: string) => string
) {
  const bartRoomTypeLabels = { ...BART_ROOM_TYPE_LABELS_OBJ };
  for (const key in bartRoomTypeLabels) {
    bartRoomTypeLabels[key] = t(bartRoomTypeLabels[key]);
  }

  return bartRoomTypeLabels;
}

export function getObjRoomLabelForRoomCodes(key: RoomTypeLabelCode) {
  const objRoomLabelForRoomCodes: Record<string, string> = {};

  key.roomTypeCode.forEach((code) => {
    objRoomLabelForRoomCodes[code] = key.roomLabel;
  });
  return objRoomLabelForRoomCodes;
}

export function getRoomTypeLabelsBySourceSystem(
  roomTypeLabels: RoomTypeLabelCode[],
  sourceSystem: string | undefined,
  t: (arg: string) => string
) {
  let roomTypeLabelsBySourceSystem: Record<string, string>;

  if (sourceSystem === SOURCE_SYSTEM.BART) {
    roomTypeLabelsBySourceSystem = getBartRoomTypeLabels(BART_ROOM_TYPE_LABELS_OBJ, t);
  } else {
    roomTypeLabelsBySourceSystem = roomTypeLabels.reduce(
      (obj, key) => Object.assign(obj, getObjRoomLabelForRoomCodes(key)),
      {}
    );
  }

  return roomTypeLabelsBySourceSystem;
}

export function getMealForCard(meal: PackageDetails) {
  return {
    title: meal?.description,
    id: meal?.packageCode,
    price: meal?.totalPrice?.amount,
    noSelections: meal?.noSelections ?? 0,
  };
}

export function getRoomDetailsForCard(
  reservationDetails: ReservationDetails,
  room: Rooms,
  bookedRoomType: string
) {
  const packagesListOfCodes: string[] = [];
  const extrasPackagesPerRoomWithPrices = {
    packagesList: [] as string[],
    priceEci: 0,
    priceLco: 0,
    priceWifi: 0,
    priceBOProsecco: 0,
  };
  const eciItem = room?.extrasItems?.find(
    (extrasItem: PackageDetails) =>
      extrasItem.packageCode &&
      EARLY_CHECKIN_IDS.includes(extrasItem.packageCode as (typeof EARLY_CHECKIN_IDS)[number])
  );

  const lcoItem = room?.extrasItems?.find(
    (extrasItem: PackageDetails) =>
      extrasItem.packageCode &&
      LATE_CHECKOUT_IDS.includes(extrasItem.packageCode as (typeof LATE_CHECKOUT_IDS)[number])
  );

  const wifiItem = room?.extrasItems?.find(
    (extrasItem: PackageDetails) =>
      extrasItem.packageCode &&
      WIFI_IDS.includes(extrasItem.packageCode as (typeof WIFI_IDS)[number])
  );

  const priceBOProseccoItem = room?.extrasItems?.find(
    (extrasItem: PackageDetails) =>
      extrasItem.packageCode &&
      PROSECCO_IDS.includes(extrasItem.packageCode as (typeof PROSECCO_IDS)[number])
  );

  room?.extrasItems?.forEach((extra: PackageDetails) => {
    packagesListOfCodes.unshift(extra.packageCode as string);

    extrasPackagesPerRoomWithPrices.packagesList = packagesListOfCodes;
    extrasPackagesPerRoomWithPrices.priceEci = eciItem?.totalPrice?.amount ?? 0;
    extrasPackagesPerRoomWithPrices.priceLco = lcoItem?.totalPrice?.amount ?? 0;
    extrasPackagesPerRoomWithPrices.priceWifi = wifiItem?.totalPrice?.amount ?? 0;
    extrasPackagesPerRoomWithPrices.priceBOProsecco = priceBOProseccoItem?.totalPrice?.amount ?? 0;
  });

  return {
    leadGuestName: `${room?.guest?.firstName} ${room?.guest?.lastName}`,
    roomType: bookedRoomType,
    roomPrice: room?.roomCost?.amount ?? 0,
    adultMealDescription:
      room?.adultsMeal?.map((meal: PackageDetails) => {
        return getMealForCard(meal);
      }) ?? ([] as MealsSelection[]),
    childrenMealDescription:
      room?.kidsMeal?.map((meal: PackageDetails) => {
        return getMealForCard(meal);
      }) ?? ([] as MealsSelection[]),
    mealPrice: 0,
    extrasPackageRoom: extrasPackagesPerRoomWithPrices,
    cot: room?.cot ?? false,
    noAdults: room?.adults ?? 1,
    noChildren: room?.children ?? 0,
    noNights: reservationDetails?.nights ?? 1,
  };
}

export function getRoomDetailsForBartCard(room: BartRoomStay, bookedRoomType: string) {
  return {
    adultsNumber: room?.adultsNumber,
    childrenNumber: room?.childrenNumber,
    arrivalDate: room?.arrivalDate,
    departureDate: room?.departureDate,
    packages: room?.packages,
    leadGuestName: `${room?.reservationGuest?.givenName} ${room?.reservationGuest?.surName}`,
    roomType: bookedRoomType,
    totalRoomCost: room?.totalRoomCost?.amount,
  };
}

export function getBookingDetailsForCard(
  reservationDetails: ReservationDetails,
  hotelName: string,
  bookedBy: string,
  guestSurname: string,
  roomTypeLabels: RoomTypeLabelCode[],
  t: (arg: string) => string,
  rateTags: string[]
) {
  const roomTypeLabelsBySourceSystem = getRoomTypeLabelsBySourceSystem(
    roomTypeLabels,
    reservationDetails.sourceSystem,
    t
  );

  return {
    shouldDisplayCityTaxMessage: reservationDetails?.hotelHasCityTaxForLeisure ?? false,
    hotelId: reservationDetails?.hotelCode ?? '',
    paymentOption: reservationDetails?.paymentOption ?? '',
    currencyCode: reservationDetails?.totalCost?.currency ?? '',
    totalCost: reservationDetails?.totalCost?.amount.toString() ?? '',
    previousTotal: reservationDetails?.previousTotal?.amount.toString() ?? '',
    balanceOutstanding: reservationDetails?.outstandingAmount?.amount.toString() ?? '',
    newTotal: reservationDetails?.newTotal?.amount.toString() ?? '',
    donationPkg: reservationDetails?.donationsPackage?.packageCode
      ? {
          code: reservationDetails?.donationsPackage?.packageCode,
          currency: reservationDetails?.donationsPackage?.totalPrice?.currency,
          unitPrice: reservationDetails?.donationsPackage?.totalPrice?.amount ?? 0,
        }
      : undefined,
    rateType: reservationDetails?.rateType ?? '',
    roomDetails:
      reservationDetails?.rooms?.map((room: Rooms) => {
        return getRoomDetailsForCard(
          reservationDetails,
          room,
          roomTypeLabelsBySourceSystem[room?.roomType ?? '']
        );
      }) ?? ([] as RoomDetails[]),
    cancellationInfoResponse: reservationDetails?.cancellationInfoResponse,
    bookedFor: `${reservationDetails?.rooms?.[0]?.guest?.firstName} ${reservationDetails?.rooms?.[0]?.guest?.lastName}`,
    arrivalDate: reservationDetails?.arrivalDate,
    noNights: reservationDetails?.nights,
    hotelName: hotelName,
    cardType: reservationDetails?.payment?.cardType,
    bookedBy: bookedBy,
    guestSurname: guestSurname,
    dinnerAllowance: reservationDetails?.dinnerAllowance,
    rateTags: rateTags,
  };
}

export function getSessionStorageValuesForBookings(): SearchBookingsSessionStorage {
  if (typeof window !== 'undefined') {
    const previousSearchValues = window.sessionStorage.getItem('ccuiPrevSearchCriteria');

    if (previousSearchValues) {
      const previousValuesDecoded = decodeFromBase64(previousSearchValues);
      const previousValues = JSON.parse(String(previousValuesDecoded));

      return {
        ...previousValues,
      };
    }
  }
  return {};
}

export const getMealQuantity = (packages: Packages, mealId: string, roomIndex: number) => {
  const item = packages?.roomSelection
    ? packages.roomSelection[roomIndex].packagesSelection?.filter((pck) => pck.id === mealId)
    : [];
  return item && item.length > 0 ? Number(item[0]?.noOfSelections) : 0;
};

export const getExtrasPackagePrice = (
  extrasId: string,
  language: string,
  packagesExtrasItems?: ExtrasPackages[]
) => {
  const extrasPackage = packagesExtrasItems?.find((extrasItem) => extrasItem?.id === extrasId);

  return extrasPackage && extrasPackage?.currency
    ? formatPrice(
        formatCurrency(extrasPackage.currency),
        extrasPackage?.price?.toFixed(2),
        language
      )
    : null;
};

export const extrasNamingCheck = (
  t: (x: string, y?: { [key: string]: string }) => string,
  extras?: string
) => {
  if (!extras) return '';

  if (EARLY_CHECKIN_IDS.includes(extras as (typeof EARLY_CHECKIN_IDS)[number])) {
    return t('ancillaries.extras.HSCKIN.name');
  }

  if (LATE_CHECKOUT_IDS.includes(extras as (typeof LATE_CHECKOUT_IDS)[number])) {
    return t('ancillaries.extras.HSCOU2.name');
  }

  if (WIFI_IDS.includes(extras as (typeof WIFI_IDS)[number])) {
    return t('ancillaries.extras.FI24HR.name');
  }

  if (PROSECCO_IDS.includes(extras as (typeof PROSECCO_IDS)[number])) {
    return t('ancillaries.extras.DBPROS.name');
  }

  return '';
};
export const filterWiFiForPlusRooms = (
  bookingInformation: BookingInformation,
  packages: Packages
) => {
  const isPlusRoomType = bookingInformation?.reservationByIdList
    ?.map((room: ReservationById) => room?.roomStay?.roomExtraInfo?.roomType)
    .some((roomType: string | undefined) => PREMIER_PLUS_ROOM_TYPES.includes(roomType as string));
  if (isPlusRoomType && packages?.extrasItems) {
    return packages.extrasItems.filter((item: ExtrasItem) => item.id !== ExtrasId.ULTIMATE_WIFI);
  }
  return packages.extrasItems;
};

// for meta query url (srp/hdp) - with today or tomorrow set for ARRdd, as only date
export const updateDateForMetaKeyword = (keyword: string) => {
  const date = keyword === 'today' ? new Date() : add(new Date(), { days: 1 });
  return {
    ARRdd: format(date, 'dd'),
    ARRmm: format(date, 'MM'),
    ARRyyyy: format(date, 'yyyy'),
  };
};

export const getTodayTomorrowDate = (
  ARRdd: string | string[] | undefined,
  ARRmm: string | string[] | undefined,
  ARRyyyy: string | string[] | undefined
) => {
  if (typeof ARRdd === 'string') {
    const lowerCaseARRdd = ARRdd?.toLowerCase();

    if (lowerCaseARRdd === DAY_TODAY || lowerCaseARRdd === DAY_TOMORROW) {
      const updatedDate = updateDateForMetaKeyword(lowerCaseARRdd);
      return {
        ARRdd: updatedDate.ARRdd,
        ARRmm: updatedDate.ARRmm,
        ARRyyyy: updatedDate.ARRyyyy,
      };
    }
  }
  return { ARRdd, ARRmm, ARRyyyy };
};

export const getUserAndRoles = (
  childrenComponent: React.ReactElement<any>
): { user?: Claims; roles?: string[] } => {
  const directUser = childrenComponent?.props?.user as Claims | undefined;
  const directRoles = childrenComponent?.props?.roles as string[] | undefined;

  let child: React.ReactElement<any> | null = null;

  const children = childrenComponent?.props?.children;

  if (React.isValidElement(children)) {
    child = children;
  } else if (Array.isArray(children)) {
    const found = children.find((c) => React.isValidElement(c));
    if (found && React.isValidElement(found)) {
      child = found;
    }
  }

  const nestedUser = child?.props?.user as Claims;
  const nestedRoles = child?.props?.roles as string[];

  return {
    user: nestedUser ?? directUser,
    roles: nestedRoles ?? directRoles,
  };
};
