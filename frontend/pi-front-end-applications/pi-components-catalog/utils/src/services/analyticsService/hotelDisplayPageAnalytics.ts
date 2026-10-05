import { DAY_TOMORROW, HIAvailabilityRates } from '@whitbread-eos/api';
import { add, differenceInDays, format } from 'date-fns';

import { PromoActionsType } from '../../getters';
import { isDateValid } from '../../validators';
import analytics from './analytics';

const updateHotelDisplayPageAnalytics = ({
  multiSearchParams,
  hotelName,
  hotelId,
  hotelLabel,
  hotelAvailability,
  hotelFacilityIcons,
  isMetaArrivalDayKeywordFlagEnabled,
  dataHotelAvailability,
  promoActions,
  isPromoBoxVisible,
}: {
  multiSearchParams: {
    [key: string]: string;
  };
  hotelName: string;
  hotelId: string;
  hotelLabel: string[];
  hotelAvailability: string;
  hotelFacilityIcons: string[];
  isMetaArrivalDayKeywordFlagEnabled?: boolean;
  dataHotelAvailability?: HIAvailabilityRates;
  promoActions?: PromoActionsType;
  isPromoBoxVisible: boolean;
}) => {
  const urlPromo = multiSearchParams?.PROMOID;
  const bannerPromo = promoActions?.promoState?.code;
  const promoType = promoActions?.promoState?.type;
  const PromoId = urlPromo ? (urlPromo === bannerPromo ? urlPromo : '') : (bannerPromo ?? '');
  const roomRatesList = dataHotelAvailability?.hotelAvailability?.roomRates ?? [];
  const matchedRate = roomRatesList?.find((rate) => rate.promotionCode === PromoId);

  const promoDetails = dataHotelAvailability?.ratesInformation?.rateClassifications?.find(
    (item) => item?.rateClassification === matchedRate?.ratePlanCode
  );

  const rateName = promoDetails?.rateTags
    ? `${promoDetails?.rateTags?.[0]} ${promoDetails?.rateName}`
    : '';

  const eligibility = roomRatesList?.some((rate) => rate?.promotionCode === PromoId);

  const isStartDateValid = isDateValid(
    Number(multiSearchParams.ARRdd),
    Number(multiSearchParams.ARRmm),
    Number(multiSearchParams.ARRyyyy)
  );

  const addValidDate = (arrivalDay: string) => {
    if (
      isMetaArrivalDayKeywordFlagEnabled &&
      typeof arrivalDay !== 'undefined' &&
      arrivalDay?.toLowerCase() === DAY_TOMORROW
    ) {
      return add(new Date(), { days: 1 });
    } else {
      return new Date();
    }
  };

  const startDate = isStartDateValid
    ? new Date(
        Number(multiSearchParams.ARRyyyy),
        Number(multiSearchParams.ARRmm) - 1,
        Number(multiSearchParams.ARRdd)
      )
    : (addValidDate(multiSearchParams?.ARRdd) as Date);
  const endDate = add(startDate, { days: Number(multiSearchParams.NIGHTS) });
  const searchWeekdayFrom = format(startDate, 'eee')?.toLowerCase();
  const searchWeekdayTo = format(endDate, 'eee')?.toLowerCase();

  const searchData = Object.keys(multiSearchParams)?.reduce(
    (acc, currentValue) => {
      if (currentValue.includes('CHILD')) {
        return {
          ...acc,
          numberOfChildren: acc.numberOfChildren + Number(multiSearchParams[currentValue]),
        };
      }
      if (currentValue.includes('ADULT')) {
        return {
          ...acc,
          numberOfAdults: acc.numberOfAdults + Number(multiSearchParams[currentValue]),
        };
      }
      if (currentValue.includes('INTTYP')) {
        const value = acc.roomType.length
          ? `${acc.roomType}:${multiSearchParams[currentValue]}`
          : multiSearchParams[currentValue];
        return {
          ...acc,
          roomType: !acc.roomType?.includes(multiSearchParams[currentValue]) ? value : acc.roomType,
        };
      }
      return {
        ...acc,
      };
    },
    {
      numberOfAdults: 0,
      numberOfChildren: 0,
      roomType: '',
    }
  );

  const roomAvailability =
    dataHotelAvailability?.hotelAvailability?.roomRates[0]?.roomTypes[0]?.rooms
      ?.map((room) => {
        return {
          roomsAvailable: room?.numberOfRoomsAvailable,
          price: room?.roomPriceBreakdown?.totalNetAmount,
        };
      })
      ?.sort((a, b) => a.price - b.price) ?? [];

  const rdata = roomAvailability?.map((x) =>
    btoa(String(x.roomsAvailable).split('').reverse().join('')).replaceAll('=', '')
  );
  const promotions = {
    promoName: rateName,
    promoCode: PromoId,
    eligibility: eligibility,
    promoJourney: eligibility,
    promoBoxVisible: isPromoBoxVisible,
  };

  const uniquePromotions = {
    promoName: promoType,
    promoCode: PromoId,
    promoBoxVisible: isPromoBoxVisible,
  };
  const promotionsAnalytics = promoType === 'UNIQUE' ? uniquePromotions : promotions;
  analytics.update({
    analyticsDataSearchResult: {
      searchCheckInDate: format(startDate, 'dd/MM/yyyy'),
      searchCheckOutDate: format(endDate, 'dd/MM/yyyy'),
      searchDaysToCheckIn: differenceInDays(new Date(startDate), new Date()),
      searchNumberOfAdults: searchData.numberOfAdults,
      searchNumberOfChildren: searchData.numberOfChildren,
      searchNumberOfGuests: searchData.numberOfAdults + searchData.numberOfChildren,
      searchNumberOfNights: Number(multiSearchParams.NIGHTS),
      searchNumberOfRooms: Number(multiSearchParams?.ROOMS),
      searchResults: 1,
      searchResultsDisplayed: [
        {
          bookingSystem: 'Opera',
          hotelAvailability: hotelAvailability ?? '',
          hotelCode: hotelId.toLowerCase(),
          hotelDistance: 0,
          hotelFacilityIcons: hotelFacilityIcons ?? [],
          hotelLabel: hotelLabel ?? ['no label'],
          imageLabel: '',
          priceFrom: '',
          voucherCode: '',
        },
      ],
      searchRoomType: searchData.roomType?.toLowerCase(),
      searchTerm: hotelName,
      searchType: 'list view',
      searchWeekdayFrom,
      searchWeekdayFromTo: `${searchWeekdayFrom}-${searchWeekdayTo}`,
      searchWeekdayTo,
      mapLoaded: false,
    },
    discountTags: rateName,
    promo: promotionsAnalytics,
  });

  analytics.updateEncodedAnalytics(rdata);
};

export default updateHotelDisplayPageAnalytics;
