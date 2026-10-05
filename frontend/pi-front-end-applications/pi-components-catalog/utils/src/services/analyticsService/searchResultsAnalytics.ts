import {
  FacilityItem,
  SearchedResultsAnalyticsLabelsConstants,
  SingleHotelAvailability,
  SRMultiSearchParamsType,
} from '@whitbread-eos/api';
import { differenceInDays, format } from 'date-fns';

import { upperOnlyFirst } from '../../formatters';
import { PromotionsInformation } from '../../getters';
import analytics from './analytics';

export const labelsConstants = {
  AVAILABLE_HOTEL: 'available',
  UNAVAILABLE_HOTEL: 'unavailable',
  SOLD_OUT_CONSTANT: 'sold out',
  LAST_FEW_ROOMS_CONSTANT: 'last few rooms',
  OPEN_SOON_CONSTANT: 'open soon',
  NO_LABEL_CONSTANT: 'no label',
  UNCHECKED_HOTEL: 'not checked',
  PREMIER_PLUS_LABEL: 'Premier Plus',
  PREMIER_PLUS_FACILITY_CODE: 'PRR',
  MLOS_LABEL: 'MLOS',
};

const updateSearchResultsAnalytics = ({
  isNewSearch,
  multiSearchParams,
  startDate,
  endDate,
  viewType,
  searchResults,
  searchResultsDisplayed,
  addedResults,
  promotionBannerData,
}: {
  isNewSearch: boolean;
  multiSearchParams: SRMultiSearchParamsType;
  startDate: Date | null;
  endDate: Date | null;
  viewType: string;
  searchResults: number;
  searchResultsDisplayed: SingleHotelAvailability[];
  addedResults?: number;
  promotionBannerData?: PromotionsInformation;
}) => {
  const LIST_VIEW = 'list view';
  const MAPPED_ROOM_TYPES: Record<string, string> = {
    DB: 'Double room',
    SB: 'Single room',
    FAM: 'Family room',
    TWIN: 'Twin room',
    DIS: 'Accessible room',
  };
  const searchDataReduce = multiSearchParams?.rooms?.reduce(
    (acc, current) => {
      return {
        numberOfAdults: acc?.numberOfAdults + current.adultsNumber,
        numberOfChildren: acc?.numberOfChildren + current.childrenNumber,
      };
    },
    {
      numberOfAdults: 0,
      numberOfChildren: 0,
    }
  );
  const searchWeekdayFrom = startDate && format(startDate, 'eee')?.toLowerCase();
  const searchWeekdayTo = endDate && format(endDate, 'eee')?.toLowerCase();
  const rdata = searchResultsDisplayed.map((hotel) =>
    btoa(
      String(hotel?.hotelAvailability?.numberOfRoomsAvailable ?? 0)
        .split('')
        .reverse()
        .join('')
    ).replaceAll('=', '')
  );

  const urlPromo = multiSearchParams?.promoId;
  const bannerPromo = promotionBannerData?.promotionCode;
  const bannerTitle = promotionBannerData?.promoBannerTitle;
  const PromotionType = promotionBannerData?.promoKind;

  let PromotionTitle: string | undefined;
  if (bannerTitle) {
    const start = bannerTitle.indexOf('<b>');
    const end = bannerTitle.indexOf('</b>', start + 3);
    if (start !== -1 && end !== -1) {
      PromotionTitle = bannerTitle.slice(start + 3, end);
    }
  }

  const PromotionCode = urlPromo ? (urlPromo === bannerPromo ? urlPromo : '') : (bannerPromo ?? '');

  analytics.update({
    analyticsDataSearchResult: {
      newSearch: isNewSearch,
      addedResults: addedResults ?? searchResultsDisplayed.length,
      searchCheckInDate: startDate && format(startDate, 'dd/MM/yyyy'),
      searchCheckOutDate: endDate && format(endDate, 'dd/MM/yyyy'),
      searchDaysToCheckIn: startDate && differenceInDays(new Date(startDate), new Date()),
      searchFilter: multiSearchParams.filters,
      searchNumberOfAdults: searchDataReduce.numberOfAdults,
      searchNumberOfChildren: searchDataReduce.numberOfChildren,
      searchNumberOfGuests: searchDataReduce.numberOfAdults + searchDataReduce.numberOfChildren,
      searchNumberOfNights: multiSearchParams.numberOfNights,
      searchNumberOfRooms: multiSearchParams?.rooms?.length,
      searchResults,
      searchResultsDisplayed:
        viewType === LIST_VIEW
          ? getSearchResults(searchResultsDisplayed, labelsConstants, startDate)
          : [],
      searchRoomType: multiSearchParams?.rooms
        .map((room) => MAPPED_ROOM_TYPES[room.type ?? ''])
        .join(':'),
      searchSort: multiSearchParams.sort.toLowerCase(),
      searchTerm: multiSearchParams.location.toLowerCase(),
      searchType: viewType,
      searchWeekdayFrom,
      searchWeekdayFromTo: `${searchWeekdayFrom}-${searchWeekdayTo}`,
      searchWeekdayTo,
    },
    promo: {
      promoName: PromotionTitle,
      promoCode: PromotionCode,
      eligibility: Boolean(PromotionCode),
      bannerType: PromotionType,
    },
  });
  analytics.updateEncodedAnalytics(rdata);
};

export function getSearchResults(
  searchResultsDisplayed: SingleHotelAvailability[],
  labelsConstants: SearchedResultsAnalyticsLabelsConstants,
  startDate?: Date | null
) {
  const { AVAILABLE_HOTEL, UNAVAILABLE_HOTEL } = labelsConstants;

  const hotelInformation = searchResultsDisplayed.map((hotel: SingleHotelAvailability) => {
    return {
      hotelResultPosition: searchResultsDisplayed.indexOf(hotel) + 1,
      hotelAvailability: hotel?.hotelAvailability?.available ? AVAILABLE_HOTEL : UNAVAILABLE_HOTEL,
      hotelCode: hotel.hotelId.toLowerCase(),
      bookingSystem: upperOnlyFirst(hotel?.hotelAvailability?.pmsSource ?? ''),
      hotelDistance: hotel?.hotelAvailability?.distance ?? 0,
      hotelFacilityIcons: (hotel?.hotelInformation?.hotelFacilities ?? []).map(
        (facility) => facility.icon
      ) as string[],
      hotelLabel: getHotelLabel(hotel, labelsConstants, startDate),
      hotelRates: [
        {
          cellCode: hotel?.hotelAvailability?.cellCode ?? '',
          currencyCode: `${hotel?.hotelAvailability?.lowestRoomRate?.currencyCode}`,
          description: '',
          lettingType: '',
          price: `${hotel?.hotelAvailability?.lowestRoomRate?.netTotal.toFixed(2)}`,
          rateCode: '',
          text: '',
        },
      ],
    };
  });
  return hotelInformation;
}

export function getHotelLabel(
  hotel: SingleHotelAvailability,
  labelsConstants: SearchedResultsAnalyticsLabelsConstants,
  startDate?: Date | null
) {
  const {
    LAST_FEW_ROOMS_CONSTANT,
    OPEN_SOON_CONSTANT,
    NO_LABEL_CONSTANT,
    PREMIER_PLUS_LABEL,
    PREMIER_PLUS_FACILITY_CODE,
    MLOS_LABEL,
  } = labelsConstants;
  const { limitedAvailability } = hotel?.hotelAvailability ?? {};
  const premierPlusBadge = hotel?.hotelInformation?.hotelFacilities?.find(
    (facility: FacilityItem) => facility.code === PREMIER_PLUS_FACILITY_CODE
  );
  const hasMlosRestriction = hotel?.hotelAvailability?.hasMlosRestriction ?? false;
  const messagingFlag = hotel?.hotelInformation?.messagingFlag?.text;

  const labels: string[] = [];

  if (messagingFlag && messagingFlag !== 'hub') {
    labels.push(messagingFlag);
  }

  if (hasMlosRestriction) {
    labels.push(MLOS_LABEL);
  }

  if (premierPlusBadge) {
    labels.push(PREMIER_PLUS_LABEL);
  }

  if (limitedAvailability === true) {
    labels.push(LAST_FEW_ROOMS_CONSTANT);
  }

  if (
    hotel?.hotelInformation?.hotelOpeningDate !== '' &&
    startDate &&
    differenceInDays(new Date(hotel?.hotelInformation?.hotelOpeningDate ?? ''), startDate) > 0
  ) {
    labels.push(OPEN_SOON_CONSTANT);
  }

  if (labels.length === 0) {
    labels.push(NO_LABEL_CONSTANT);
  }

  return labels;
}

export default updateSearchResultsAnalytics;
