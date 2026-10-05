'use client';

/* eslint-disable @typescript-eslint/no-explicit-any */
import type { HIRoomRate, PromoKind } from '@whitbread-eos/api';
import {
  HOTEL_AVAILABILITY_BB_QUERY,
  HOTEL_AVAILABILITY_CCUI_QUERY,
  HOTEL_AVAILABILITY_QUERY,
  RATE_INFORMATION_BB_QUERY,
  RATE_INFORMATION_CCUI_QUERY,
  BOOKING_SUBCHANNEL,
  PROMO_CODE_COOKIE,
  RatePlanCodes,
  HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY,
  DISCOUNT_RATE_INFORMATION_QUERY,
} from '@whitbread-eos/api';

import { getHotelAvailabilityQueryKey } from '../getters';
import { getLoggedInUserInfo } from '../getters/auth';
import { getCookie } from '../helpers/cookies';
import useCustomLocale from './use-custom-locale';
import { useQueryRequest } from './use-request';

export function useHotelAvailability(
  hotelId: string,
  hotelBrand: string,
  arrival: string | undefined,
  departure: string | undefined,
  rooms: any,
  channel: string,
  empOfferCodes?: string[] | [],
  accessToken?: string,
  landingPromoCode?: string,
  promoKind?: PromoKind,
  softBundle?: string,
  rateName?: string,
  roomClass?: string,
  isPromoBox?: boolean
) {
  const { country, language } = useCustomLocale();

  const queryEnabled = arrival !== undefined && departure !== undefined;

  const { isLoading, isFetching, isError, data, error } = useQueryRequest(
    getHotelAvailabilityQueryKey(
      language,
      country,
      hotelBrand,
      hotelId,
      arrival,
      departure,
      rooms,
      empOfferCodes,
      landingPromoCode as string,
      promoKind,
      softBundle,
      rateName,
      roomClass,
      isPromoBox
    ),
    HOTEL_AVAILABILITY_QUERY,
    {
      hotelId,
      arrival,
      departure,
      rooms,
      brand: hotelBrand?.toLowerCase(),
      language,
      country,
      bookingChannel: {
        channel,
        language: language?.toUpperCase(),
        subchannel: BOOKING_SUBCHANNEL.WEB,
      },
      channel,
      ratePlanCodes: empOfferCodes,
      promotionCode: landingPromoCode || getCookie(PROMO_CODE_COOKIE),
      promoKind: promoKind,
      softBundle,
      rateName,
      roomClass,
      isPromoBox,
    },
    {
      enabled: !!queryEnabled,
      ...(isPromoBox && {
        placeholderData: (previousData: any) => previousData,
      }),
    },
    accessToken
  );

  return { isLoading, isFetching, isError, data, error };
}

export function useHotelAvailabilityBB(
  hotelId: string,
  hotelBrand: string,
  arrival: string | undefined,
  departure: string | undefined,
  rooms: any,
  channel: string,
  accessToken?: string,
  landingPromoCode?: string,
  promoKind?: PromoKind,
  rateName?: string,
  roomClass?: string,
  isPromoBox?: boolean
) {
  let userData = {
    profile: {
      accessLevel: '',
      companyId: '',
      employeeId: '',
      isBusiness: '',
      sessionId: '',
    },
    operaCompanyId: '',
  };
  if (accessToken) userData = getLoggedInUserInfo(accessToken);
  const companyId = userData?.operaCompanyId;
  const { country, language } = useCustomLocale();

  const queryEnabled = arrival !== undefined && departure !== undefined;

  const { data, isLoading, isFetching, isError, error } = useQueryRequest(
    [
      'hotelAvailabilityBB',
      language,
      country,
      hotelBrand.toLowerCase(),
      hotelId,
      companyId,
      arrival,
      departure,
      JSON.stringify(rooms),
      channel,
      landingPromoCode,
      promoKind,
      rateName,
      roomClass,
      isPromoBox,
    ],
    HOTEL_AVAILABILITY_BB_QUERY,
    {
      hotelId,
      arrival,
      departure,
      rooms,
      brand: hotelBrand.toLowerCase(),
      language,
      country,
      companyId,
      bookingChannel: {
        channel: channel,
        language: language?.toUpperCase(),
        subchannel: BOOKING_SUBCHANNEL.WEB,
      },
      promotionCode: landingPromoCode,
      promoKind: promoKind,
      rateName,
      roomClass,
      isPromoBox,
    },
    {
      enabled: !!queryEnabled,
      ...(isPromoBox && {
        placeholderData: (previousData: any) => previousData,
      }),
    },
    accessToken
  );

  const roomRates: HIRoomRate[] = data?.hotelAvailability?.roomRates;
  if (roomRates?.length > 0) {
    const filteredRatePlanCodes = getFilteredRatePlans(roomRates);
    const filteredRoomRates = roomRates.filter(({ ratePlanCode }) =>
      filteredRatePlanCodes.includes(ratePlanCode)
    );
    data.hotelAvailability.roomRates = filteredRoomRates;
  }
  return { isLoading, isFetching, isError, data, error };
}

export function useHotelAvailabilityCCUI(
  hotelId: string,
  hotelBrand: string,
  arrival: string | undefined,
  departure: string | undefined,
  rooms: any,
  channel: string,
  queryEnabled: boolean,
  companyId?: string,
  ccuiLandingPromoCode?: string,
  promoKind?: PromoKind,
  rateName?: string,
  roomClass?: string,
  isPromoBox?: boolean
) {
  const { country, language } = useCustomLocale();

  const { data, isLoading, isFetching, isError, error } = useQueryRequest(
    [
      'hotelAvailabilityCCUI',
      language,
      country,
      hotelBrand.toLowerCase(),
      hotelId,
      companyId,
      arrival,
      departure,
      JSON.stringify(rooms),
      channel,
      ccuiLandingPromoCode,
      promoKind,
      rateName,
      roomClass,
      isPromoBox,
    ].filter(Boolean),
    HOTEL_AVAILABILITY_CCUI_QUERY,
    {
      hotelId,
      arrival,
      departure,
      rooms,
      brand: hotelBrand.toLowerCase(),
      language,
      country,
      companyId,
      bookingChannel: {
        channel: channel,
        language: language?.toUpperCase(),
        subchannel: BOOKING_SUBCHANNEL.WEB,
      },
      promotionCode: ccuiLandingPromoCode,
      promoKind: promoKind,
      rateName,
      roomClass,
      isPromoBox,
    },
    {
      enabled: !!queryEnabled,
      ...(isPromoBox && {
        placeholderData: (previousData: any) => previousData,
      }),
    }
  );

  if (data?.hotelAvailability?.roomRates) {
    data.hotelAvailability.roomRates = data.hotelAvailability.roomRates.filter(
      (rate: HIRoomRate) => rate.ratePlanCode !== RatePlanCodes.BUSIFLEX
    );
  }
  return { isLoading, isFetching, isError, data, error };
}

export function useHotelRatesInformationBB(
  hotelId: string,
  hotelBrand: string,
  dataHotelAvailability: any,
  channel: string,
  accessToken?: string
) {
  const { country, language } = useCustomLocale();

  const ratePlanCodes = dataHotelAvailability?.hotelAvailability?.roomRates.map(
    (roomRate: HIRoomRate) => roomRate.ratePlanCode
  );

  const {
    data: dataRatesInformation,
    isLoading,
    isError,
    error,
  } = useQueryRequest(
    [
      'ratesInformationBB',
      language,
      country,
      hotelBrand.toLowerCase(),
      hotelId,
      channel,
      ratePlanCodes,
    ],
    RATE_INFORMATION_BB_QUERY,
    {
      hotelId,
      brand: hotelBrand?.toLowerCase(),
      language,
      country,
      channel,
      ratePlans: ratePlanCodes?.toString(),
    },
    undefined,
    accessToken
  );

  const hotelData = {
    ...dataHotelAvailability,
    ...dataRatesInformation,
  };

  return { isLoading, isError, data: hotelData, error };
}

export function useHotelRatesInformationCCUI(
  hotelId: string,
  hotelBrand: string,
  dataHotelAvailability: any,
  channel: string,
  queryEnabled: boolean
) {
  const { country, language } = useCustomLocale();
  const ratePlanCodes = dataHotelAvailability?.hotelAvailability?.roomRates.map(
    (roomRate: HIRoomRate) => roomRate.ratePlanCode
  );

  const {
    data: dataRatesInformation,
    isLoading,
    isError,
    error,
  } = useQueryRequest(
    [
      'ratesInformationCCUI',
      language,
      country,
      hotelBrand.toLowerCase(),
      hotelId,
      channel,
      ratePlanCodes,
    ],
    RATE_INFORMATION_CCUI_QUERY,
    {
      hotelId,
      brand: hotelBrand?.toLowerCase(),
      language,
      country,
      channel,
      ratePlans: ratePlanCodes?.toString(),
    },
    { enabled: queryEnabled }
  );

  const hotelData = {
    ...dataHotelAvailability,
    ...dataRatesInformation,
  };

  return { isLoading, isError, data: hotelData, error };
}

const getFilteredRatePlans = (roomRates: HIRoomRate[]) => {
  const ratePlanCodes = roomRates.map((roomRate: HIRoomRate) => roomRate.ratePlanCode);
  const isHavingBusAndFlexRate: boolean =
    Array.isArray(ratePlanCodes) &&
    ratePlanCodes.indexOf(RatePlanCodes.BUSIFLEX) > -1 &&
    ratePlanCodes.indexOf(RatePlanCodes.FLEXRATE) > -1;

  return isHavingBusAndFlexRate
    ? ratePlanCodes.filter((planCode: string) => planCode !== RatePlanCodes.FLEXRATE)
    : ratePlanCodes;
};

function getRatePlanCodes(dataHotelAvailability: any): string[] | undefined {
  return dataHotelAvailability?.hotelAvailability?.roomRates.map(
    (roomRate: HIRoomRate) => roomRate.ratePlanCode
  );
}

export function useHotelRatesInformationDiscountRate(
  hotelId: string,
  hotelBrand: string,
  dataHotelAvailability: any,
  channel: string,
  queryEnabled: boolean
) {
  const { country, language } = useCustomLocale();
  const ratePlanCodes = getRatePlanCodes(dataHotelAvailability);

  const {
    data: dataRatesInformation,
    isLoading,
    isError,
    error,
  } = useQueryRequest(
    [
      'ratesInformationDiscountRate',
      language,
      country,
      hotelBrand.toLowerCase(),
      hotelId,
      channel,
      ratePlanCodes,
    ],
    DISCOUNT_RATE_INFORMATION_QUERY,
    {
      hotelId,
      brand: hotelBrand.toLowerCase(),
      language,
      country,
      channel,
      ratePlans: ratePlanCodes?.toString(),
    },
    { enabled: queryEnabled }
  );

  const hotelData = {
    ...dataHotelAvailability,
    ...dataRatesInformation,
  };

  return { isLoading, isError, data: hotelData, error };
}

export type HotelAvailabilityDiscountRateParams = {
  hotelId: string;
  hotelBrand: string;
  arrival?: string;
  departure?: string;
  rooms: any;
  channel: string;
  queryEnabled: boolean;
  companyId?: string;
};

export function useHotelAvailabilityDiscountRate({
  hotelId,
  hotelBrand,
  arrival,
  departure,
  rooms,
  channel,
  queryEnabled,
  companyId,
}: HotelAvailabilityDiscountRateParams) {
  const { country, language } = useCustomLocale();

  const queryParams = {
    hotelId,
    arrival,
    departure,
    rooms,
    brand: hotelBrand.toLowerCase(),
    language,
    country,
    companyId,
    bookingChannel: {
      channel: channel,
      language: language?.toUpperCase(),
      subchannel: BOOKING_SUBCHANNEL.WEB,
    },
  };

  const { data, isLoading, isError, error } = useQueryRequest(
    [
      'hotelAvailabilityDiscountRate',
      language,
      country,
      hotelBrand.toLowerCase(),
      hotelId,
      companyId,
      arrival,
      departure,
      JSON.stringify(rooms),
      channel,
    ],
    HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY,
    queryParams,
    { enabled: queryEnabled }
  );

  return { isLoading, isError, data, error };
}
