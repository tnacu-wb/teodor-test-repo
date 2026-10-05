/* eslint-disable @typescript-eslint/no-explicit-any */
import type {
  SearchSuggestions,
  SearchRoomCodes,
  AcceptedRoomTypes,
  SearchRoomType,
} from '@whitbread-eos/api';
import { MAX_NIGHTS_CCUI } from '@whitbread-eos/api';
import { logicalOrOperator, swapKeysAndValues, isDateValid } from '@whitbread-eos/utils';
import { differenceInDays } from 'date-fns';
import { NextRouter } from 'next/router';

import {
  ERROR_FIELDS,
  ERROR_VALUES_TYPE,
  FIELDS,
  ERROR_KEYS,
  getRedirectWithDefaultsURL,
} from '../utilities/searchContainerHelpers';

type SearchRoomTypes = {
  [key in AcceptedRoomTypes]: string;
};
type VALIDATIONS_TYPE = {
  [key in ERROR_VALUES_TYPE]: boolean;
};

const isLocationValid = (items: SearchSuggestions | null) => {
  return logicalOrOperator(
    items === null,
    items?.properties?.length !== 0,
    items?.places?.length !== 0,
    items?.managedPlaces?.length !== 0
  );
};

const isArrivalDateAtLeastToday = (day: number, month: number, year: number) => {
  const today = new Date();
  const startDate = new Date(year, month - 1, day);

  return differenceInDays(startDate, today) >= 0;
};

const isArrivalDateInLimits = (
  day: number,
  month: number,
  year: number,
  maxArrivalDate: number
) => {
  const today = new Date();
  const startDate = new Date(year, month - 1, day);

  return differenceInDays(startDate, today) < maxArrivalDate;
};

const isNumberOfNightsValid = (nights: number, maxNights: number) => {
  return nights >= 1 && nights <= maxNights;
};

const isRoomOccupancyValid = (
  rooms: SearchRoomType[] | undefined,
  roomCodes: SearchRoomCodes,
  roomTypes: SearchRoomTypes,
  maxNights?: number,
  roomsOccupancyLimitation?: number
) => {
  const codes = Object.keys(roomCodes);
  if (!rooms) {
    return false;
  }
  if (rooms?.length < 1 || rooms?.length > (roomsOccupancyLimitation as number)) {
    return false;
  }
  const isValidRoomOccupancy = (room: SearchRoomType) => {
    if (
      (room.children > 0 && room.roomType !== roomTypes.family && maxNights !== MAX_NIGHTS_CCUI) ||
      (room.children === 0 &&
        room.roomType === roomTypes.family &&
        maxNights !== MAX_NIGHTS_CCUI) ||
      (room.roomType === roomTypes.twin && room.adults < 2 && maxNights !== MAX_NIGHTS_CCUI) ||
      (room.roomType === roomTypes.single && room.adults > 1)
    ) {
      return false;
    }

    return (
      (room.adults === 1 || room.adults === 2) &&
      room.children >= 0 &&
      room.children <= 2 &&
      room.roomType &&
      codes.includes(room.roomType)
    );
  };
  return rooms?.every(isValidRoomOccupancy);
};

const areOccupancyParamsURLValid = (
  params: string[],
  numberOfRooms: number,
  router: NextRouter,
  showMultipleRooms: boolean
) => {
  const numberRegex = /-?\d+/g;

  const invalidParam = params.find((param) => {
    const index = param.match(numberRegex)?.join('');
    const value = Number(router.query[param]);
    const hasCotError = (param.startsWith('COT') && (value < 0 || value > 1)) || isNaN(value);
    const selfBookerOnlyOneRoom = showMultipleRooms && numberOfRooms > 1;
    return (
      Number(index) > numberOfRooms || Number(index) < 1 || hasCotError || selfBookerOnlyOneRoom
    );
  });

  return invalidParam === undefined;
};

export function validateSearchData(
  ARRdd: number,
  ARRmm: number,
  ARRyyyy: number,
  NIGHTS: number,
  items: SearchSuggestions | null,
  dataTranslations: any,
  router: NextRouter,
  maxArrivalDate: number,
  maxNights: number,
  defaultRooms: SearchRoomType[] | undefined,
  showMultipleRooms?: boolean,
  roomsOccupancyLimitation?: number,
  isCcui?: boolean,
  isPriceFinderPage?: boolean
) {
  let key: keyof typeof ERROR_KEYS | undefined;
  let field: string | undefined = '';
  let URL = router.asPath;
  const queryParams = Object.keys(router.query);

  if (!isLocationValid(items)) {
    key = ERROR_KEYS.invalidLocation;
    field = ERROR_FIELDS[key];

    return {
      errorKey: key && field ? [key, field] : [undefined, undefined],
      url: URL,
    };
  }

  if (
    queryParams.includes('ARRdd') &&
    queryParams.includes('ARRmm') &&
    queryParams.includes('ARRyyyy')
  ) {
    const headerInformation = dataTranslations?.headerInformation;
    const roomCodes = headerInformation?.config?.roomCodes || {};
    const today = new Date();

    const numberOfRooms = router.query['ROOMS'];
    const roomOccupancyParams = queryParams.filter(
      (param) => param.startsWith('ADULT') || param.startsWith('CHILD') || param.startsWith('COT')
    );
    const validations = {
      [`${ERROR_KEYS.invalidLocation}`]: isLocationValid(items),
      [`${ERROR_KEYS.invalidDate}`]:
        (isDateValid(ARRdd, ARRmm, ARRyyyy) && isCcui) ||
        (isDateValid(ARRdd, ARRmm, ARRyyyy) && isNumberOfNightsValid(Number(NIGHTS), maxNights)),
      [`${ERROR_KEYS.arrivalDateInThePast}`]: isArrivalDateAtLeastToday(ARRdd, ARRmm, ARRyyyy),
      [`${ERROR_KEYS.arrivalDateInTheFuture}`]: isArrivalDateInLimits(
        ARRdd,
        ARRmm,
        ARRyyyy,
        maxArrivalDate
      ),
      [`${ERROR_KEYS.invalidNights}`]: isNumberOfNightsValid(Number(NIGHTS), maxNights),
      [`${ERROR_KEYS.invalidOccupancy}`]:
        areOccupancyParamsURLValid(
          roomOccupancyParams,
          Number(numberOfRooms),
          router,
          showMultipleRooms!
        ) &&
        isRoomOccupancyValid(
          defaultRooms,
          swapKeysAndValues(roomCodes),
          roomCodes,
          maxNights,
          roomsOccupancyLimitation
        ),
    } as VALIDATIONS_TYPE;

    key = Object.keys(validations).find(
      (k) => !validations[k as keyof VALIDATIONS_TYPE]
    ) as keyof VALIDATIONS_TYPE;
    field = key && ERROR_FIELDS[key];
    if (key) {
      let day = ARRdd;
      let month = ARRmm;
      let year = ARRyyyy;

      if (field === FIELDS.datepicker) {
        day = today.getDate();
        month = today.getMonth() + 1;
        year = today.getFullYear();
      }

      URL = getRedirectWithDefaultsURL(
        router,
        day,
        month,
        year,
        NIGHTS,
        showMultipleRooms,
        isPriceFinderPage
      );
    }
  }
  return {
    errorKey: key && field ? [key, field] : [undefined, undefined],
    url: URL,
  };
}
