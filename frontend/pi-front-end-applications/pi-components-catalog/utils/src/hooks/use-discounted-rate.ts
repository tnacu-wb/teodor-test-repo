'use client';

import { useFeatureToggle } from '.';
import {
  FT_PI_DISCOUNT_RATE,
  SEARCH_COMPANY_BY_ID,
  BOOKING_CHANNEL,
  RateExtraInfo,
  SITE_LEISURE,
  GET_STATIC_CONTENT,
  Area,
  AmendHotelAvailabilityData,
} from '@whitbread-eos/api';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';

import useGetCountryLanguage from './use-get-country-language';
import {
  useHotelAvailabilityDiscountRate,
  useHotelRatesInformationDiscountRate,
} from './use-hotel-availability';
import { useQueryRequest } from './use-request';

type OfferConfigDataType = {
  cellCode: string;
  maxRooms: number;
  numberOfNights: number;
  page: string;
  corpId?: string;
  ratePlanCode?: string;
}[];

interface propsType {
  hotelId: string;
  hotelBrand: string;
  arrival?: string;
  departure?: string;
  rooms: any;
  offers: any;
}
interface RateInfo {
  ratePlanCode: string;
  rateName: string;
  isCorporateDiscountAvailable?: boolean;
  rateTags?: string[];
}
interface RatesInformation {
  rateClassifications: RateInfo[];
}
interface HotelInformation {
  ratesInformation?: RatesInformation;
}
interface CompanyProfile {
  name: string;
}
interface CompanyData {
  companyProfile?: CompanyProfile;
}
interface Offer {
  corpId: string;
  ratePlanCode: string;
}

interface BookingDataType {
  upgradeToFlex?: { flexRateCode?: string };
  reservationByIdList: {
    roomStay: {
      ratePlanCode: string;
      rateExtraInfo: RateExtraInfo;
    };
  }[];
}

export function getRatePlanCode(offers: Offer[], targetCorpId?: string): string | undefined {
  const offer = offers?.find((offer: Offer) => offer?.corpId === targetCorpId);
  return offer?.ratePlanCode;
}

export const getCorporateDiscountRatePlanCode = (
  ratePlans: { isCorporateDiscountAvailable?: boolean; ratePlanCode: string }[]
) => {
  const planWithDiscount = ratePlans?.find((plan) => plan?.isCorporateDiscountAvailable === true);
  return planWithDiscount ? planWithDiscount?.ratePlanCode : '';
};

export function updateRateInfoWithCorporateDiscount(
  dataHotelInformations: HotelInformation,
  companyData?: CompanyData,
  ratePlanCode?: string
): void {
  dataHotelInformations?.ratesInformation?.rateClassifications?.forEach((rateInfo: RateInfo) => {
    if (rateInfo?.ratePlanCode === ratePlanCode) {
      rateInfo.rateName = companyData?.companyProfile?.name ?? '';
      rateInfo.isCorporateDiscountAvailable = true;
    }
  });
}

export function useForDiscountedRateFlag() {
  const { [FT_PI_DISCOUNT_RATE]: isDiscountRateEnabled } = useFeatureToggle();
  return !!isDiscountRateEnabled;
}

export function useForDiscountedRateMicroSite() {
  const {
    query: { CORPID },
  } = useRouter();
  return useForDiscountedRateFlag() && !!CORPID;
}

export function useDiscountRateInfoHotelAvailability({
  hotelId,
  hotelBrand,
  arrival,
  departure,
  rooms,
  offers,
}: propsType) {
  const {
    query: { CORPID },
  } = useRouter();

  const isDiscountRateEnabled = useForDiscountedRateMicroSite();

  const {
    isLoading: isLoadingCompanyData,
    isError: isErrorCompanyData,
    data: companyData,
  } = useQueryRequest(
    ['searchCompanyById', CORPID],
    SEARCH_COMPANY_BY_ID,
    {
      corpId: CORPID,
    },
    { enabled: !!CORPID && isDiscountRateEnabled, retry: false }, //if unleash flag for discount rate is enabled, then call this API
    undefined,
    true
  );

  const hotelAvailabilityQueryEnabled = !!(companyData && !isLoadingCompanyData);

  const {
    isLoading: isLoadingHotelAvailabilities,
    isError: isErrorHotelAvailabilities,
    data: dataHotelAvailabilityData,
    error: errorHotelAvailabilities,
  } = useHotelAvailabilityDiscountRate({
    hotelId,
    hotelBrand,
    arrival,
    departure,
    rooms,
    channel: BOOKING_CHANNEL.PI,
    queryEnabled: hotelAvailabilityQueryEnabled, //if unleash flag for discount rate is enabled nad valid companyData is present, then call this API
    companyId: companyData?.companyProfile?.companyId,
  });

  const rateInformationQueryEnabled =
    !isErrorHotelAvailabilities &&
    !errorHotelAvailabilities &&
    !isLoadingHotelAvailabilities &&
    !!dataHotelAvailabilityData;

  const {
    isLoading: isLoadingHotelInformations,
    isError: isErrorHotelInformations,
    data: dataHotelRateInformations,
    error: errorHotelInformations,
  } = useHotelRatesInformationDiscountRate(
    hotelId,
    hotelBrand,
    dataHotelAvailabilityData,
    BOOKING_CHANNEL.PI,
    rateInformationQueryEnabled //if unleash flag for discount rate is enabled nad valid companyData, dataHotelAvailabilityData is present, then call this API
  );

  const ratePlanCode = getRatePlanCode(offers as any, CORPID as string);

  updateRateInfoWithCorporateDiscount(dataHotelRateInformations, companyData, ratePlanCode);

  const dataHotelAvailability = dataHotelAvailabilityData;

  const hotelAvailabilityResponse = {
    isLoadingHotelAvailability: isLoadingHotelInformations || isLoadingHotelAvailabilities,
    isErrorHotelAvailability: isErrorHotelInformations || isErrorHotelAvailabilities,
    dataHotelAvailability: dataHotelRateInformations &&
      dataHotelAvailability && { ...dataHotelRateInformations, ...(dataHotelAvailability as any) },
    errorHotelAvailability: errorHotelInformations || errorHotelAvailabilities,
  };

  return {
    hotelAvailabilityResponse,
    dataHotelAvailability,
    isDiscountRateLoading:
      isLoadingHotelInformations || isLoadingCompanyData || isLoadingHotelAvailabilities,
    isDiscountRateError:
      isErrorHotelInformations || isErrorCompanyData || isErrorHotelAvailabilities,
  };
}

export function useUpdateRateName(bookingData: BookingDataType, language: string, country: string) {
  const { t } = useTranslation();
  let HDPPath = '';
  if (typeof window !== 'undefined') HDPPath = localStorage.getItem('HDPPath') as string;
  const urlParams = new URLSearchParams(HDPPath);

  const { data: dataHeaderInformation } = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      language,
      country,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );
  const offer = dataHeaderInformation?.headerInformation?.content?.global?.offers;
  const isDiscountRateEnabled = useForDiscountedRateFlag();
  const CORPID = urlParams.get('CORPID');
  if (!CORPID || !offer) return;
  const ratePlanCodes = getRatePlanCode(offer, CORPID);

  if (isDiscountRateEnabled)
    bookingData.reservationByIdList.forEach((reservation) => {
      if (reservation.roomStay.ratePlanCode === ratePlanCodes) {
        if (bookingData?.upgradeToFlex?.flexRateCode) {
          //Remove updateToFlex button on ancillary and guest details page.
          bookingData.upgradeToFlex.flexRateCode = '';
        }
        reservation.roomStay.rateExtraInfo.rateName = t(
          `promotion.discountrate.${ratePlanCodes}.ratename`
        );
      }
    });
}

export function getMatchedOffer(
  offersConfigData?: OfferConfigDataType,
  targetRatePlanCode?: string
) {
  return targetRatePlanCode
    ? offersConfigData?.find((offer: any) => offer?.ratePlanCode === targetRatePlanCode)
    : null;
}

export function useGetDiscountRateComapnyId(hotelAvailabilityParams: AmendHotelAvailabilityData) {
  let comapanyId = '';
  const isDiscountRateFeatureEnabled = useForDiscountedRateFlag();

  const { language, country } = useGetCountryLanguage();

  const { data: headerInformationData } = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      language,
      country,
      site: SITE_LEISURE,
      businessBooker: false,
      enabled: isDiscountRateFeatureEnabled,
    }
  );

  const matchedOffer = getMatchedOffer(
    headerInformationData?.headerInformation?.content?.global?.offers,
    hotelAvailabilityParams?.rateCode
  );

  const corpId = matchedOffer?.corpId;

  const companyIdQueryEnabled =
    isDiscountRateFeatureEnabled &&
    hotelAvailabilityParams?.channel?.toLowerCase() === Area.PI.toLowerCase() &&
    !!headerInformationData &&
    !!corpId;

  const {
    isLoading: isLoadingCompanyData,
    isError: isErrorCompanyData,
    data: companyData,
  } = useQueryRequest(
    ['searchCompanyById', corpId],
    SEARCH_COMPANY_BY_ID,
    {
      corpId: corpId,
    },
    { enabled: !!corpId && companyIdQueryEnabled, retry: false },
    undefined,
    true
  );

  if (!!companyData && !isLoadingCompanyData && !isErrorCompanyData && companyIdQueryEnabled) {
    comapanyId = companyData?.companyProfile?.companyId;
  }

  return comapanyId;
}

export function useGetDiscountRateReservationData(
  offersConfigData: OfferConfigDataType | undefined,
  bookingConfirmationData: { reservationByIdList: any },
  variant: string
) {
  const { t } = useTranslation();
  const isDiscountRateFeatureEnabled = useForDiscountedRateFlag();

  if (!isDiscountRateFeatureEnabled) {
    return {
      isDiscountRate: false,
      maxRooms: 0,
      matchedDiscountRate: '',
      roomLimitMessage: '',
      unAvailableMessage: '',
    };
  }

  const discountRateReservation = bookingConfirmationData?.reservationByIdList?.find(
    (room: { roomStay: { ratePlanCode: string } }) => {
      return (
        offersConfigData?.some(
          (offer: any) => offer?.ratePlanCode === room?.roomStay?.ratePlanCode
        ) &&
        isDiscountRateFeatureEnabled &&
        [Area.PI, Area.CCUI].includes(variant as any)
      );
    }
  );

  const matchedOffer = getMatchedOffer(
    offersConfigData,
    discountRateReservation?.roomStay?.ratePlanCode
  );

  return discountRateReservation && matchedOffer
    ? {
        isDiscountRate: true,
        maxRooms: Number(matchedOffer?.maxRooms) || 0,
        matchedDiscountRate: matchedOffer?.ratePlanCode || '',
        roomLimitMessage: matchedOffer?.ratePlanCode
          ? t(`promotion.discountrate.${matchedOffer?.ratePlanCode}.roomlimit`, {
              maxRooms: matchedOffer?.maxRooms,
            })
          : '',
        unAvailableMessage: matchedOffer?.ratePlanCode
          ? t(`promotion.discountrate.${matchedOffer?.ratePlanCode}.unavailable`)
          : '',
      }
    : {
        isDiscountRate: false,
        maxRooms: 0,
        matchedDiscountRate: '',
        roomLimitMessage: '',
        unAvailableMessage: '',
      };
}
