import {
  BCReservationListItem,
  HotelBrand,
  PurposeOfStay,
  BIReservationGuest,
} from '@whitbread-eos/api';

import { formatCurrency, formatPrice } from '../formatters';

function isSameAsBooker(
  guest: BIReservationGuest,
  bookerFirstName: string,
  bookerLastName: string,
  bookerTitle: string
) {
  const { givenName: firstName, surName: lastName, nameTitle: title } = guest;
  return firstName === bookerFirstName && lastName === bookerLastName && title === bookerTitle;
}

function isBookingForSomeoneElse(
  isSingleRoomBooking: boolean,
  guestsList: BIReservationGuest[][],
  bookerFirstName: string,
  bookerLastName: string,
  bookerTitle: string
) {
  if (!isSingleRoomBooking) {
    return false;
  }

  const leadGuestInRoom = guestsList[0].find(
    (guest: BIReservationGuest) => !guest.isAccompanyingGuest
  );

  if (!leadGuestInRoom) {
    return false;
  }

  const leadGuestSameAsBooker = isSameAsBooker(
    leadGuestInRoom,
    bookerFirstName,
    bookerLastName,
    bookerTitle
  );
  return !leadGuestSameAsBooker;
}

function fetchLeadGuests(
  isSingleRoomBooking: boolean,
  guestsList: BIReservationGuest[][],
  bookerFirstName: string,
  bookerLastName: string,
  bookerTitle: string
) {
  const leadGuestsArray = [];
  for (const roomguests of guestsList) {
    let leadGuestForRoom = {};
    for (const guest of roomguests) {
      if (!guest.isAccompanyingGuest) {
        const sameAsBooker = isSameAsBooker(guest, bookerFirstName, bookerLastName, bookerTitle);
        if (!sameAsBooker) {
          leadGuestForRoom = {
            ...leadGuestForRoom,
            firstName: guest.givenName,
            lastName: guest.givenName === '' ? '' : guest.surName,
            title: guest.nameTitle,
          };
        }
        if (!isSingleRoomBooking) {
          leadGuestForRoom = { ...leadGuestForRoom, stayInThisRoom: sameAsBooker };
        }
      } else {
        leadGuestForRoom = {
          ...leadGuestForRoom,
          accompanyingtitle: guest.nameTitle,
          accompanyingfirstName: guest.givenName,
          accompanyinglastName: guest.surName,
        };
      }
    }
    if (Object.keys(leadGuestForRoom).length > 0) {
      leadGuestsArray.push(leadGuestForRoom);
    }
  }

  return leadGuestsArray;
}

function isBookingAlreadyCompleted(reservations: BCReservationListItem[]): boolean {
  const firstRoomLeadGuest = reservations[0]?.reservationGuestList?.find(
    (guest: BIReservationGuest) => !guest.isAccompanyingGuest
  );
  return Boolean(
    firstRoomLeadGuest?.givenName && firstRoomLeadGuest?.surName && firstRoomLeadGuest?.nameTitle
  );
}

export function getCityTaxMessages(
  hotelHasCityTaxForLeisure: boolean | undefined,
  hotelHasCityTaxForBusiness: boolean | undefined,
  reasonForStay: string,
  t: (s: string, values?: object) => string,
  currencyCode: string,
  language: string,
  totalCost: number
) {
  const messages = {
    mainBanner: '',
    secondaryBanner: '',
    summaryText: '',
    confPageBusinessNotif: '',
  };
  const totalPrice = formatPrice(formatCurrency(currencyCode), totalCost?.toFixed(2), language);

  if (hotelHasCityTaxForLeisure && !hotelHasCityTaxForBusiness) {
    // If you're staying for Leisure, you will need to pay Local tax.
    messages.mainBanner = t('booking.reason.citytax.leisure.message.noExempt');
    messages.confPageBusinessNotif = t('booking.confirmation.cityTaxExemptNotification');

    if (reasonForStay === 'LEI') {
      // With local taxes, your new total is
      messages.secondaryBanner = `${t(
        'booking.reason.citytax.notification.price.included'
      )} ${totalPrice}`;

      messages.summaryText = t('booking.overview.includeCityTax');
    }
    return messages;
  }

  if (hotelHasCityTaxForLeisure && hotelHasCityTaxForBusiness) {
    // City tax is applicable to all stays at this hotel, without displaying new total second notification
    messages.mainBanner = t('booking.reason.citytax.all.message');
    messages.confPageBusinessNotif = t('booking.confirmation.cityTaxExemptNotification');

    // Preventing the appearance of includeCityTax message if there isn't a reasonForStay
    if (reasonForStay) {
      messages.summaryText = t('booking.overview.includeCityTax');
    }

    return messages;
  }

  if (!hotelHasCityTaxForLeisure && hotelHasCityTaxForBusiness) {
    // If you're staying for work you will need to pay Local tax
    messages.mainBanner = t('booking.reason.citytax.notification.message.noExempt');

    if (reasonForStay === 'BUS') {
      // With local taxes, your new total is
      messages.secondaryBanner = `${t(
        'booking.reason.citytax.notification.price.included'
      )} ${totalPrice}`;

      messages.summaryText = t('booking.overview.includeCityTax');
    }
    return messages;
  }

  // if (!hotelHasCityTaxForLeisure && !hotelHasCityTaxForBusiness)
  return messages;
}

export function getDefaultDataFromBooking(
  reservations: BCReservationListItem[],
  formDetails: any,
  basketReference: string,
  currentLang: string,
  brand: HotelBrand,
  isGermanHotel?: boolean,
  isMultiRoomRedesignEnabled?: boolean
) {
  const alreadyCompleted = isBookingAlreadyCompleted(reservations);

  const setCurrentLanguage = (currentLang: string) => {
    return currentLang === 'en' ? 'GB' : 'DE';
  };

  let defaultData = {
    reasonForStay: HotelBrand.HUB === brand ? PurposeOfStay.LEISURE : '',
    title: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    landline: '',
    companyName: '',
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    addressLine4: '',
    postalCode: '',
    // sets manualAddress link to toggle home/business radios (or hone address fields - de hotel, multiRoomRedesignEnabled) to open or not
    manualAddressToggle: currentLang === 'de' && !isMultiRoomRedesignEnabled ? 'manualAddress' : '',
    cityName: '',
    postcodeAddress: '',
    // For De hotel - set Home address as default
    addressSelection: isGermanHotel && isMultiRoomRedesignEnabled ? 'HOME' : '',
    countryCode: setCurrentLanguage(currentLang),
    acceptFutureMailing: false,
    bookingForSomeoneElse: false,
    basketReferenceId: '',
    leadGuest: [],
    billing_countryCode: setCurrentLanguage(currentLang),
    billing_companyName: '',
    billing_addressLine1: '',
    billing_addressLine2: '',
    billing_addressLine3: '',
    billing_addressLine4: '',
    billing_cityName: '',
    billing_postalCode: '',
    whoBookerIsTabs: 'MYSELF',
  };

  if (
    !alreadyCompleted &&
    formDetails?.updated &&
    formDetails?.basketReferenceId === basketReference
  ) {
    return {
      ...defaultData,
      ...formDetails,
      basketReferenceId: basketReference,
      leadGuest: formDetails?.leadGuest ?? defaultData.leadGuest,
    };
  }

  if (alreadyCompleted) {
    const billingData = reservations[0]?.billing;
    const reasonForStay = reservations[0]?.additionalGuestInfo?.purposeOfStay;
    const guestsList = reservations?.map((reservation) => reservation.reservationGuestList);

    const isSingleRoomBooking = reservations.length === 1;
    const bookerFirstName = formDetails.firstName || billingData?.firstName;
    const bookerLastName = formDetails.lastName || billingData?.lastName;
    const bookerTitle = formDetails.title || billingData?.title;
    const bookingForSomeoneElse = isBookingForSomeoneElse(
      isSingleRoomBooking,
      guestsList,
      bookerFirstName,
      bookerLastName,
      bookerTitle
    );

    const leadGuestsArray = fetchLeadGuests(
      isSingleRoomBooking,
      guestsList,
      bookerFirstName,
      bookerLastName,
      bookerTitle
    );

    defaultData = {
      acceptFutureMailing: false,
      addressLine1: formDetails.addressLine1 || (billingData?.address?.addressLine1 ?? ''),
      addressLine2: formDetails.addressLine2 || (billingData?.address?.addressLine2 ?? ''),
      addressLine3: formDetails.addressLine3 || (billingData?.address?.addressLine3 ?? ''),
      addressLine4: formDetails.addressLine4 || (billingData?.address?.addressLine4 ?? ''),
      postcodeAddress: formDetails.postcodeAddress || (billingData?.address?.postalCode ?? ''),
      addressSelection:
        formDetails.addressSelection || (billingData?.address?.companyName ? 'BUSINESS' : 'HOME'),
      cityName:
        formDetails.cityName ||
        billingData?.address?.cityName ||
        formDetails.addressLine4 ||
        billingData?.address?.addressLine4 ||
        '',
      companyName: formDetails.companyName || (billingData?.address?.companyName ?? ''),
      countryCode: formDetails.countryCode || (billingData?.address?.country ?? ''),
      email: formDetails.email || billingData?.email,
      firstName: formDetails.firstName || billingData?.firstName,
      landline: formDetails.landline || (billingData?.landline ?? ''),
      lastName: formDetails.lastName || billingData?.lastName,
      postalCode: formDetails.postalCode || (billingData?.address?.postalCode ?? ''),
      phone: formDetails.phone || (billingData?.telephone ?? ''),
      reasonForStay: formDetails.reasonForStay || (reasonForStay ?? ''),
      manualAddressToggle:
        formDetails.manualAddressToggle ||
        (currentLang === 'de' && !isMultiRoomRedesignEnabled
          ? (formDetails.addressSelection ?? 'manualAddress')
          : ''),
      title: formDetails.title || billingData?.title,
      bookingForSomeoneElse: formDetails.bookingForSomeoneElse || bookingForSomeoneElse,
      basketReferenceId: basketReference,
      billing_countryCode: formDetails.billing_countryCode || setCurrentLanguage(currentLang),
      billing_companyName: formDetails.companyName,
      billing_addressLine1: formDetails.addressLine1,
      billing_addressLine2: formDetails.addressLine2,
      billing_addressLine3: formDetails.addressLine3,
      billing_addressLine4: formDetails.addressLine4,
      billing_cityName: formDetails.billing_cityName,
      billing_postalCode: formDetails.postalCode,
      whoBookerIsTabs: 'MYSELF',
      leadGuest: formDetails?.leadGuest?.[0]?.firstName ? formDetails?.leadGuest : leadGuestsArray,
    };
  }
  return defaultData;
}

export const checkIsBookingForSomeoneElse = (reservations: BCReservationListItem[]) => {
  const guestsList = reservations?.map((reservation) => reservation.reservationGuestList);
  const isSingleRoomBooking = reservations.length === 1;
  const bookerFirstName = reservations[0]?.billing?.firstName ?? '';
  const bookerLastName = reservations[0]?.billing?.lastName ?? '';
  const bookerTitle = reservations[0]?.billing?.title ?? '';

  return isBookingForSomeoneElse(
    isSingleRoomBooking,
    guestsList,
    bookerFirstName,
    bookerLastName,
    bookerTitle
  );
};

export const mapBookingInformationForReuseBooking = (
  basketReference: string,
  reservations: BCReservationListItem[],
  language: string,
  isMultiRoomRedesignEnabled?: boolean
) => {
  const manualAddressToggle =
    (language === 'de' || reservations[0]?.billing?.address?.addressLine1) &&
    !isMultiRoomRedesignEnabled
      ? 'manualAddress'
      : '';
  const billingData = reservations[0]?.billing;

  return {
    reasonForStay: reservations[0]?.additionalGuestInfo?.purposeOfStay ?? '',
    acceptFutureMailing: false,
    manualAddressToggle: manualAddressToggle,
    addressSelection: billingData?.address?.companyName ? 'BUSINESS' : 'HOME',
    whoBookerIsTabs: 'MYSELF',
    addressLine1: billingData?.address?.addressLine1 ?? '',
    addressLine2: billingData?.address?.addressLine2 ?? '',
    addressLine3: billingData?.address?.addressLine3 ?? '',
    addressLine4: billingData?.address?.addressLine4 ?? '',
    postcodeAddress: billingData?.address?.postalCode ?? '',
    cityName: billingData?.address?.cityName || billingData?.address?.addressLine4 || '',
    companyName: billingData?.address?.companyName ?? '',
    countryCode: billingData?.address?.country ?? '',
    email: billingData?.email ?? '',
    firstName: billingData?.firstName ?? '',
    landline: billingData?.landline ?? '',
    lastName: billingData?.lastName ?? '',
    postalCode: billingData?.address?.postalCode ?? '',
    phone: billingData?.telephone ?? '',
    title: billingData?.title ?? '',
    basketReferenceId: basketReference,
  };
};
