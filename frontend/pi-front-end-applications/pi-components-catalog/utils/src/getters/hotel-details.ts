/* eslint-disable @typescript-eslint/no-explicit-any */
import {
  DeRoomClass,
  HIRoomClass,
  HIRoomClassCode,
  HIRoomRate,
  HIRoomType,
  HIRoom,
  HITwinRoomPrice,
  RoomClass,
  RoomClassCodes,
  ROOM_TYPE,
  RoomTypeCodeMapped,
  twinRoomImprovedSpecialRequests,
  twinRoomStandardSpecialRequests,
  ACCESSIBLE_ROOM_TYPES,
  ACCESSIBLE_BARRIER_ROOM_TYPES,
  HotelBrand,
  RoomClassLabelByCode,
  ACCESSIBLE_ROOM_TYPE,
  roomTypeMapping,
  wetBathRoomTypes,
  loweredBathRoomTypes,
  PREMIER_PLUS_ACCESSIBLE,
  ACCESSIBLE,
  type Language,
  GET_DASHBOARD_BASKET,
} from '@whitbread-eos/api';
import type {
  Faq,
  HIRateClassification,
  HIRoomCode,
  PromoKind,
  SingleHotelAvailability,
} from '@whitbread-eos/api';
import { add, differenceInDays, format } from 'date-fns';
import { ParsedUrlQuery } from 'querystring';

import type { PromotionsInformation } from '../getters';
import { MAX_ROOMS_SEARCH_LIMIT } from '../global-constants';
import { executeGraphQLQuery } from '../server';
import { staticHotelInformationIB, getHotelInformationIB } from '../server/getters/getters';
import { isDateValid } from '../validators';

export type SingleHotelAvailabilityWithPromotions = SingleHotelAvailability & {
  promotionsInformation?: PromotionsInformation;
};

type GetHotelBrandParams = {
  language: Language;
  brand?: string;
  slug?: string | string[];
  basketReference?: string;
};

export function getAvailabilityParamsFromUrl(url: string, isNoRoomTypeSearchEnabled = false) {
  const urlParams = new URLSearchParams(url);
  const year = Number(urlParams.get('ARRyyyy'));
  const month = Number(urlParams.get('ARRmm'));
  const day = Number(urlParams.get('ARRdd'));
  const numberOfNights = Number(urlParams.get('NIGHTS'));
  const numberOfRooms = Number(urlParams.get('ROOMS'));
  const cellCodes = urlParams.get('CELLCODES');
  const empOfferCodes = (cellCodes?.split(',') as string[] | []) || [];
  const corpId = urlParams.get('CORPID');
  const promoId = urlParams.get('PROMOID');

  const arrivalDate = isDateValid(day, month, year) ? new Date(year, month - 1, day) : new Date();
  const arrival = format(arrivalDate, 'yyyy-MM-dd');
  const departure = format(add(arrivalDate, { days: numberOfNights }), 'yyyy-MM-dd');
  const rooms: any[] = [];
  for (let idx = 0; idx < numberOfRooms && idx < MAX_ROOMS_SEARCH_LIMIT; idx++) {
    rooms.push({
      adultsNumber: Number(urlParams.get(`ADULT${idx + 1}`)),
      childrenNumber: Number(urlParams.get(`CHILD${idx + 1}`)),
      roomType: isNoRoomTypeSearchEnabled ? '' : urlParams.get(`INTTYP${idx + 1}`),
      cotRequired: urlParams.get(`COT${idx + 1}`) === '1',
    });
  }
  return {
    arrival,
    departure,
    rooms,
    empOfferCodes,
    corpId,
    promoId,
    numberOfNights,
  };
}

export function getStaticHotelInformationQueryDateDataFromUrl(url: string) {
  const queryString = url?.includes('?') ? (url.split('?')[1] ?? '') : url;
  const urlParams = new URLSearchParams(queryString);
  const year = Number(urlParams.get('ARRyyyy'));
  const month = Number(urlParams.get('ARRmm'));
  const day = Number(urlParams.get('ARRdd'));
  const numberOfNights = Number(urlParams.get('NIGHTS'));

  const hasValidStayDates =
    isDateValid(day, month, year) && Number.isFinite(numberOfNights) && numberOfNights > 0;

  if (!hasValidStayDates) {
    return {
      staticHotelInformationQueryKeyDates: [] as string[],
      staticHotelInformationQueryPayloadDates: {} as {
        stayStartDate?: string;
        stayEndDate?: string;
      },
    };
  }

  const arrivalDate = new Date(year, month - 1, day);
  const stayStartDate = format(arrivalDate, 'yyyy-MM-dd');
  const stayEndDate = format(add(arrivalDate, { days: numberOfNights }), 'yyyy-MM-dd');

  return {
    staticHotelInformationQueryKeyDates: [stayStartDate, stayEndDate],
    staticHotelInformationQueryPayloadDates: {
      stayStartDate,
      stayEndDate,
    },
  };
}

export function getHotelAvailabilityQueryKey(
  language: string,
  country: string,
  brand: string,
  hotelId: string,
  arrival: string | undefined,
  departure: string | undefined,
  rooms: any,
  empOfferCodes: string[] | [] = [],
  landingPagePromoCode: string,
  promoKind?: PromoKind,
  softBundle?: string,
  rateName?: string,
  roomClass?: string,
  isPromoBox?: boolean
) {
  const hotelAvailabilityQueryKey = [
    'hotelAvailability',
    language,
    country,
    brand?.toLowerCase(),
    hotelId,
    arrival,
    departure,
    JSON.stringify(rooms),
    JSON.stringify(empOfferCodes),
  ];

  if (landingPagePromoCode) {
    hotelAvailabilityQueryKey.push(landingPagePromoCode);
  }

  if (promoKind) {
    hotelAvailabilityQueryKey.push(promoKind);
  }

  if (softBundle) {
    hotelAvailabilityQueryKey.push(softBundle);
  }

  if (rateName) {
    hotelAvailabilityQueryKey.push(rateName);
  }
  if (roomClass) {
    hotelAvailabilityQueryKey.push(roomClass);
  }

  if (isPromoBox) {
    hotelAvailabilityQueryKey.push(isPromoBox as any);
  }

  return hotelAvailabilityQueryKey;
}

export function isHotelOpeningSoon(hotelOpeningDate?: string, arrival?: string) {
  if (!hotelOpeningDate || !arrival) {
    return false;
  }
  try {
    return differenceInDays(new Date(hotelOpeningDate), new Date(arrival)) > 0;
  } catch (e) {
    return false;
  }
}

export function getAvailableRoomTypes(roomTypes: HIRoomType[] = []) {
  return Array.from(new Set(roomTypes?.map((type) => type?.roomType)));
}

export function getAllRoomTypesFromRates(roomRates: HIRoomRate[]) {
  const allRoomTypes: HIRoomType[] = [];
  roomRates.forEach((roomRate: HIRoomRate) =>
    roomRate?.roomTypes?.forEach((room: HIRoomType) => {
      allRoomTypes.push(room);
    })
  );
  return allRoomTypes;
}

export function getRoomClassByRoomClassCode(
  roomClassCode: HIRoomClassCode,
  language: string
): HIRoomClass {
  return language === 'en' ? RoomClass[roomClassCode] : DeRoomClass[roomClassCode];
}

export function getRateClassification(
  ratePlanCode: string,
  rateClassifications: HIRateClassification[] = []
) {
  return rateClassifications.find(
    (rate: HIRateClassification) =>
      rate?.rateClassification === ratePlanCode || rate?.ratePlanCode === ratePlanCode
  );
}

export function getRoomRatesThatMatchRoomClassifications(
  rateClassifications: (HIRateClassification | undefined)[],
  roomRates: HIRoomRate[] = []
) {
  return [...roomRates].sort((roomRate1, roomRate2) => {
    const rate1Order = rateClassifications.find(
      (el) => el?.rateClassification === roomRate1?.ratePlanCode
    )?.rateOrder;

    const rate2Order = rateClassifications.find(
      (el) => el?.rateClassification === roomRate2?.ratePlanCode
    )?.rateOrder;

    return rate1Order && rate2Order ? +rate1Order - +rate2Order : 0;
  });
}

export function getAvailabilityRoomClassIndexes(roomRates: HIRoomRate[] = []) {
  const availableRoomTypes = getAvailableRoomTypes(getAllRoomTypesFromRates(roomRates));
  const { STANDARD_ROOM } = RoomClassCodes;
  return availableRoomTypes?.length > 1
    ? ([STANDARD_ROOM] as HIRoomClassCode[])
    : roomRates[0]?.roomTypes[0].rooms?.map((room) => room?.roomClass);
}

// Return pmsRoomTypes and specialRequests arrays for non-Twin roomTypes
// For Twin roomTypes - find first instance of room that has specialRequest from twinRoomSelections and get the specialRequests array and pmsRoomType for it
export function getSelectedPMSRoomTypesAndSpecialRequests(
  twinroomSelections: Array<unknown>,
  basketDetailsState: any
) {
  const selectedPMSRoomTypes: string[] = [];
  const selectedSpecialRequests: string[][] = [];
  let roomIndex = 0;

  basketDetailsState.selectedRate?.roomTypes?.forEach((roomType: HIRoomType) => {
    // non-TWIN roomType rooms - jut return first pmsRoomType and first specialRequests array
    if (roomType?.roomType !== ROOM_TYPE.TWIN) {
      roomIndex++;
      selectedSpecialRequests.push(roomType?.rooms?.[0]?.specialRequests);
      return selectedPMSRoomTypes.push(roomType?.rooms?.[0]?.pmsRoomType);
    }

    // get string twobeds or doublsofa based on roomIndex
    const matchTwinRoomSelection = twinroomSelections[roomIndex];

    const isTwoBeds = roomType?.rooms?.find(
      (roomItem) => twinRoomImprovedSpecialRequests.includes(roomItem?.specialRequests[0]) // one of 'TW2S'
    ) as HIRoom;
    const isDoubleSofa = roomType?.rooms?.find(
      (roomItem) => twinRoomStandardSpecialRequests.includes(roomItem?.specialRequests[0]) // one of 'TWDS'
    ) as HIRoom;

    if (matchTwinRoomSelection === ROOM_TYPE.TWIN_TWO_BEDS && isTwoBeds?.specialRequests?.length) {
      selectedSpecialRequests.push(isTwoBeds?.specialRequests);
      selectedPMSRoomTypes.push(isTwoBeds?.pmsRoomType);
    }

    if (
      matchTwinRoomSelection === ROOM_TYPE.TWIN_DOUBLE_SOFA &&
      isDoubleSofa?.specialRequests?.length
    ) {
      selectedSpecialRequests.push(isDoubleSofa?.specialRequests);
      selectedPMSRoomTypes.push(isDoubleSofa?.pmsRoomType);
    }
    roomIndex++;
  });

  return { selectedPMSRoomTypes, selectedSpecialRequests };
}

export function getTwinRoomSelectionPrices(selectedRate: HIRoomRate) {
  return selectedRate?.roomTypes
    ?.find((selectedType: HIRoomType) => selectedType?.roomType === ROOM_TYPE.TWIN)
    ?.rooms?.map((room: HIRoom) => {
      //Check if special requests have TW2S or TWDS
      const hasTwinOptions = (element: string) =>
        element === twinRoomImprovedSpecialRequests[0] ||
        element === twinRoomStandardSpecialRequests[0];
      const typeIndex = room.specialRequests.findIndex(hasTwinOptions);
      return {
        twinRoomType: room?.specialRequests[typeIndex],
        currencyCode: room?.roomPriceBreakdown?.currencyCode,
        price: room?.roomPriceBreakdown?.totalNetAmount,
      };
    })
    ?.filter((value: HITwinRoomPrice, index: number, self: HITwinRoomPrice[]) => {
      return (
        self.findIndex((room: HITwinRoomPrice) => room?.twinRoomType === value?.twinRoomType) ===
        index
      );
    }) as HITwinRoomPrice[];
}

export function getRoomTypesFromQuery(
  routerQuery: ParsedUrlQuery,
  mapping: RoomTypeCodeMapped,
  roomLabel: string,
  maxRooms?: number
) {
  const roomTypes: string[] = [];
  const numberOfRooms = parseInt(routerQuery['ROOMS'] as string) || 1;
  const roomsUpperBound = Math.min(maxRooms || MAX_ROOMS_SEARCH_LIMIT, MAX_ROOMS_SEARCH_LIMIT);

  for (let i = 1; i <= numberOfRooms && i <= roomsUpperBound; i++) {
    const inttypKey = `INTTYP${i}`;

    const queryValue = routerQuery[inttypKey] as string;
    roomTypes.push(mapping[queryValue] + ' ' + roomLabel);
  }

  return roomTypes;
}

export function isFaqValidForRender(
  faq: Faq | Record<string, never> | null | undefined,
  isFaqEnabled: boolean
) {
  return faq && !!Object.keys(faq).length && !!faq?.faqItems?.length && isFaqEnabled;
}

//formats the data
export function getAccessibleRoomData(
  roomTypes: HIRoomType[] | undefined,
  chooseRoomTypePage = false
) {
  return roomTypes?.map((roomType: HIRoomType) => {
    const accessibleRooms = roomType?.rooms?.filter((room: HIRoom) => {
      if (chooseRoomTypePage) {
        return ACCESSIBLE_BARRIER_ROOM_TYPES.includes(room?.pmsRoomType);
      }
      return isAccessibleRoomType(room?.pmsRoomType);
    });
    return {
      ...roomType,
      rooms: accessibleRooms?.length ? accessibleRooms : roomType?.rooms,
      isRoomAccessible: !!accessibleRooms?.length,
    };
  });
}

export function isAccessibleRoomType(roomType: string) {
  return ACCESSIBLE_ROOM_TYPES?.includes(roomType);
}

export function getPIBrandText(t: (s: string, values?: object) => string, brand: string) {
  return brand === HotelBrand.PI || brand === HotelBrand.PID
    ? ` ${t('search.new.premierInn')} `
    : ' ';
}

type RoomClassLabelKeys = keyof typeof RoomClassLabelByCode;

export function isPremierPlusAccessibleRoomByCode(
  roomClassCode: string,
  roomType: string,
  isPremPlusAccFeatureFlag: boolean
): boolean {
  return (
    roomClassCode === RoomClassCodes.PREMIER_PLUS_ROOM &&
    roomType === ACCESSIBLE_ROOM_TYPE &&
    isPremPlusAccFeatureFlag
  );
}

export interface RoomClassItem {
  code: string;
  title: string;
}

const staticHotelData = (
  roomClassConfiguration: RoomClassItem[],
  roomClassCode: string
): string | RoomClassItem[] => {
  const hotelData = roomClassConfiguration.filter((item) =>
    item?.code == roomClassCode ? item?.title : ''
  );
  if (hotelData?.length) {
    return hotelData.map((item) => item.title).join(', ');
  }
  return hotelData;
};

export function getRoomClassByCodeAndType(
  roomClass: string,
  roomClassCode: string,
  roomType: string,
  roomTypes: HIRoomCode[],
  t: any,
  isPremPlusAccFeatureFlag: boolean,
  roomClassConfiguration: RoomClassItem[] | undefined
) {
  const staticHotel = staticHotelData(roomClassConfiguration ?? [], roomClassCode);

  return staticHotel && staticHotel.length > 0
    ? staticHotel
    : RoomClassLabelByCode[roomClassCode as RoomClassLabelKeys]
      ? t(RoomClassLabelByCode[roomClassCode as RoomClassLabelKeys])
      : roomClass;
}

export function isPremierPlusAccessibleRoomByText(roomClass: string, language: string) {
  const matchStr = (language === 'en' ? RoomClass.PP : DeRoomClass.PP)?.toLowerCase();
  return roomClass?.toLowerCase()?.includes(matchStr);
}

export function getSelectedRoomClassCode(roomClassName: string, language: string) {
  const roomClassObject = language === 'en' ? RoomClass : DeRoomClass;
  return Object.keys(roomClassObject).find(
    (key) => roomClassObject[key as keyof typeof roomClassObject] === roomClassName
  );
}

export const createRoomTypeMapping = (t: (key: string) => string) => {
  const accessibleDouble = t('accessible.double');
  const accessibleTwin = t('accessible.twin');

  return {
    [accessibleDouble]: {
      [ROOM_TYPE.LOWERED]: ROOM_TYPE.LOWERED_DOUBLE,
      [ROOM_TYPE.WET]: ROOM_TYPE.WET_DOUBLE,
      premierPlus: {
        [ROOM_TYPE.LOWERED]: ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE,
        [ROOM_TYPE.WET]: ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE,
      },
    },
    [accessibleTwin]: {
      [ROOM_TYPE.LOWERED]: ROOM_TYPE.LOWERED_TWIN,
      [ROOM_TYPE.WET]: ROOM_TYPE.WET_TWIN,
    },
  };
};

export function getPMSRoomTypeByRoomTypeAndBathroom(
  t: (key: string) => string,
  isPremPlusAccFeatureFlag: boolean,
  accessibleRoomIndex: number,
  roomTypeSelections: string[],
  bathroomSelections: unknown[],
  isPremierPlusAccessibleRoomSelected: boolean
) {
  const roomType = roomTypeSelections[accessibleRoomIndex];
  const bathRoomType = bathroomSelections[accessibleRoomIndex];

  const mapping = createRoomTypeMapping(t);

  if (isPremPlusAccFeatureFlag) {
    return isPremierPlusAccessibleRoomSelected
      ? mapping[roomType]?.premierPlus?.[bathRoomType as string]
      : mapping[roomType]?.[bathRoomType as string];
  } else {
    return isPremierPlusAccessibleRoomSelected
      ? roomTypeMapping[roomType]?.premierPlus?.[bathRoomType as string]
      : roomTypeMapping[roomType]?.[bathRoomType as string];
  }
}

export function getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
  t: (key: string) => string,
  isPremPlusAccFeatureFlag: boolean,
  roomTypeSelections: string[],
  bathroomSelections: Array<unknown>,
  basketDetailsState: any,
  language: string
) {
  const selectedPMSRoomTypes: string[] = [];
  const selectedSpecialRequests: string[][] = [];
  let accessibleRoomIndex = 0;

  const isPremierPlus = isPremierPlusAccessibleRoomByText(basketDetailsState.roomClass, language);

  basketDetailsState.selectedRate?.roomTypes?.forEach((roomType: HIRoomType) => {
    if (roomType?.roomType !== ACCESSIBLE_ROOM_TYPE) {
      accessibleRoomIndex++;
      const accessibleRooms = roomType?.rooms?.filter((room) =>
        isAccessibleRoomType(room?.pmsRoomType)
      );
      const selectedRoom = accessibleRooms?.[0] || roomType?.rooms?.[0];
      selectedSpecialRequests.push(selectedRoom?.specialRequests);
      selectedPMSRoomTypes.push(selectedRoom?.pmsRoomType);
      return;
    }

    const isPremierPlusAccessibleRoomSelected =
      isPremierPlus && roomType?.roomType === ACCESSIBLE_ROOM_TYPE;
    const pmsRoomType = getPMSRoomTypeByRoomTypeAndBathroom(
      t,
      isPremPlusAccFeatureFlag,
      accessibleRoomIndex,
      roomTypeSelections,
      bathroomSelections,
      isPremierPlusAccessibleRoomSelected
    );
    accessibleRoomIndex++;
    if (!pmsRoomType) {
      return;
    }

    const room = roomType?.rooms?.find((room) => {
      return room?.pmsRoomType === pmsRoomType;
    }) as HIRoom;

    // In case lowered bathroom is chosen, do not send WETR special request
    if (loweredBathRoomTypes.includes(pmsRoomType)) {
      selectedSpecialRequests.push(
        room?.specialRequests?.filter((specialRequest) => specialRequest !== 'WETR')
      );
    }

    // In case wet room is chosen, do not send LOWB special request
    if (wetBathRoomTypes?.includes(pmsRoomType)) {
      selectedSpecialRequests.push(
        room?.specialRequests?.filter((specialRequest) => specialRequest !== 'LOWB')
      );
    }

    selectedPMSRoomTypes.push(pmsRoomType);
  });

  return { selectedPMSRoomTypes, selectedSpecialRequests };
}

export function getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
  roomTypeSelections: string[],
  accessibleRoomTypeSelections: Array<unknown>,
  basketDetailsState: any,
  roomAvailability: any[]
) {
  const selectedPMSRoomTypes: string[] = [];
  const selectedSpecialRequests: string[][] = [];
  let accessibleRoomIndex = 0;
  basketDetailsState.selectedRate?.roomTypes?.forEach((roomType: HIRoomType) => {
    if (roomType?.roomType !== ACCESSIBLE_ROOM_TYPE) {
      accessibleRoomIndex++;
      const accessibleRooms = roomType?.rooms?.filter((room) =>
        isAccessibleRoomType(room?.pmsRoomType)
      );
      const selectedRoom = accessibleRooms?.[0] || roomType?.rooms?.[0];
      selectedSpecialRequests.push(selectedRoom?.specialRequests);
      selectedPMSRoomTypes.push(selectedRoom?.pmsRoomType);
      return;
    }
    const pmsRoomType = getPMSRoomTypeByRoomType(
      accessibleRoomIndex,
      roomTypeSelections,
      accessibleRoomTypeSelections,
      roomAvailability,
      roomType?.rooms
    );
    accessibleRoomIndex++;
    if (!pmsRoomType) {
      return;
    }
    const room = roomType?.rooms?.find((room) => room?.pmsRoomType === pmsRoomType) as HIRoom;

    if (room?.specialRequests?.length) selectedSpecialRequests.push(room?.specialRequests);
    selectedPMSRoomTypes.push(pmsRoomType);
  });

  return { selectedPMSRoomTypes, selectedSpecialRequests };
}

export function getPMSRoomTypeByRoomType(
  accessibleRoomIndex: number,
  roomTypeSelections: string[],
  accessibleRoomTypeSelections: unknown[],
  roomAvailability: {
    availableCount: number;
    code: 'BRFDBL' | 'LOWDBL' | 'WETDBL' | 'LOWTWN' | 'DOUBLE' | 'SINGLE' | 'FMTRPL';
  }[],
  rooms: HIRoom[]
) {
  const roomType = roomTypeSelections[accessibleRoomIndex];
  const accessibleRoomType = accessibleRoomTypeSelections[accessibleRoomIndex];

  const room = rooms.find(
    ({ specialRequests }) =>
      (accessibleRoomType === ROOM_TYPE.STANDARD_ACCESSIBLE &&
        (specialRequests.includes('LOWB') || specialRequests.includes('WETR'))) ||
      specialRequests.includes('BFRE')
  );

  return (
    roomAvailability?.find(
      ({ code, availableCount }) => room?.pmsRoomType === code && availableCount > 0
    )?.code || roomTypeMapping[roomType]?.[accessibleRoomType as string]
  );
}

export function getRoomClassTextForGAllery(
  basketDetailsState: any,
  language: string,
  isPremPlusAccFeatureFlag: boolean
) {
  const roomClasscode = getSelectedRoomClassCode(basketDetailsState?.roomClass, language) || '';
  const roomType = basketDetailsState?.selectedRate?.roomTypes[0]?.roomType || '';

  const isPremierPlus = isPremierPlusAccessibleRoomByCode(
    roomClasscode,
    roomType,
    isPremPlusAccFeatureFlag
  );
  return isPremierPlus ||
    (isPremPlusAccFeatureFlag && roomClasscode === RoomClassCodes.PREMIER_PLUS_ROOM)
    ? PREMIER_PLUS_ACCESSIBLE
    : ACCESSIBLE;
}

export function filterRoomsByRoomClass(
  roomTypesData: HIRoomType[] = [],
  allowedRoomClass: string,
  isPremPlusAccFeatureFlag: boolean
) {
  if (!allowedRoomClass || !isPremPlusAccFeatureFlag) {
    return roomTypesData;
  }

  return roomTypesData
    ?.map((roomGroup: HIRoomType) => ({
      ...roomGroup,
      rooms: roomGroup?.rooms?.filter(
        (room: { roomClass: string }) => room?.roomClass === allowedRoomClass
      ),
    }))
    .filter((roomGroup: HIRoomType) => roomGroup?.rooms?.length > 0); // remove room groups with no rooms left
}

export function isItBarrierFree(
  selectedRoomCodes: HIRoomType[],
  barrierFreeRoomsSpecialRequestCodes: string[],
  selectedRoomClassCode: string
): boolean {
  return selectedRoomCodes?.some((group) =>
    group?.rooms?.some(
      (room) =>
        room?.roomClass === selectedRoomClassCode &&
        room.specialRequests?.some((code) => barrierFreeRoomsSpecialRequestCodes?.includes(code))
    )
  );
}
//promobox utils
export type PromoState = {
  code: string;
  type?: string;
  error: string;
  success: string;
  isApplied: boolean;
  isOpen: boolean;
  shouldShowRemoveButton: boolean;
  rateName?: string;
  roomClass?: string;
  isPromoBox?: boolean;
};

export type PromoActionsType = {
  shouldShowRemoveButton: boolean;
  promotionBannerData?: PromotionsInformation;
  appliedPromoCode: React.MutableRefObject<string>;
  searchQuery: {
    arrival: string;
    departure: string;
    country: string;
    language: string;
    hotelBrand: HotelBrand;
    roomRates: HIRoomRate[];
  };
  setPromoState: React.Dispatch<React.SetStateAction<PromoState>>;
  promoState: PromoState;
  handleRemovePromoCode?: () => void;
  isFetching?: boolean;
};

export function hasMatchingPromotionCode(promoCode: string, roomRates: HIRoomRate[]): boolean {
  if (!promoCode || !Array.isArray(roomRates)) return false;
  return roomRates?.some((item) => item?.promotionCode === promoCode);
}

export function getActivePromotionCode(
  promotionBannerData: PromotionsInformation,
  promoId: string
): string {
  const banner = promotionBannerData;
  const bannerCode = banner?.promotionCode;
  const isWithinWindow = banner?.isWithinPromoWindow;
  const isPromoEnabled = banner?.showPromo;

  if (promoId && isPromoEnabled && promoId === bannerCode && isWithinWindow) {
    return promoId;
  }

  if (isPromoEnabled && isWithinWindow && bannerCode) {
    return bannerCode;
  }

  return '';
}

export function getRoomRatesWithRateInformation(
  roomRates: HIRoomRate[],
  rateClassifications: HIRateClassification[]
): HIRoomRate[] {
  if (typeof window === 'undefined') {
    return roomRates;
  }

  const { hostname } = window?.location || {};
  const roomRatesWithRateInformation: HIRoomRate[] = [];

  for (const roomRate of roomRates) {
    const rateClassification = rateClassifications?.find(
      (el) =>
        el?.rateClassification === roomRate?.ratePlanCode ||
        el?.ratePlanCode === roomRate?.ratePlanCode
    ) as HIRateClassification;

    // DNRQ-47724: hide any rates that do no have rate name & description, and log a warning
    if (!rateClassification?.rateName || !rateClassification?.rateDescription) {
      if (/localhost|www\.((qa\w+)|dev|dit|sit)\.premierinn\.digital/.test(hostname)) {
        console.log(
          `[HDP] No rate name or description for rate with rate plan code: ${roomRate?.ratePlanCode}`
        );
      }

      continue;
    }

    roomRatesWithRateInformation.push(roomRate);
  }

  return roomRatesWithRateInformation;
}

export const getDashboardBasket = async (basketReference: string) => {
  return await executeGraphQLQuery(
    GET_DASHBOARD_BASKET,
    {
      basketReference,
    },
    (result: any) => result?.data?.basket
  );
};

export const getBrandForUnleashContext = async ({
  language,
  brand,
  slug,
  basketReference,
}: GetHotelBrandParams): Promise<string | undefined> => {
  if (brand) {
    return brand;
  }
  // HDP flow
  if (slug) {
    const slugPath = Array.isArray(slug) ? slug.join('/') : slug;
    const hotelInformation = await staticHotelInformationIB(`/hotels/${slugPath}`, language);
    return hotelInformation?.brand;
  }
  // Ancillaries flow
  if (basketReference) {
    const bookingInfo = await getDashboardBasket(basketReference);
    if (!bookingInfo?.hotelId) {
      return undefined;
    }

    const hotelInformation = await getHotelInformationIB(bookingInfo.hotelId, language);
    return hotelInformation?.brand;
  }
  return undefined;
};

export function extractPromoBoxData(
  isPromotionsInHotelAvailabilityEnabled: boolean,
  hotelAvailability?: SingleHotelAvailabilityWithPromotions,
  promotionBannerData?: PromotionsInformation
): PromotionsInformation | undefined {
  const latestPromotionBanner = isPromotionsInHotelAvailabilityEnabled
    ? hotelAvailability?.promotionsInformation
    : promotionBannerData;

  const source = latestPromotionBanner ?? promotionBannerData;

  if (!source) {
    return undefined;
  }

  return {
    ...source,
    promoBox: latestPromotionBanner?.promoBox ?? promotionBannerData?.promoBox,
  };
}
