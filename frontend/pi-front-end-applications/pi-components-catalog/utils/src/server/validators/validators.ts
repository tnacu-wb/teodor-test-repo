import { URLParams } from '@whitbread-eos/api';
import { addDays, differenceInDays, isAfter } from 'date-fns';

export { validateRoomOccupancyConditions, isInnBusinessApp } from '../../helpers/roomHelpers';

export const isHotelOpeningSoon = (hotelOpeningDate: string, selectedDate: Date) => {
  const openingSoonDate =
    hotelOpeningDate !== '' && new Date(JSON.parse(JSON.stringify(hotelOpeningDate)));
  return openingSoonDate && differenceInDays(openingSoonDate, selectedDate) > 0;
};

export function isMoreThan364DaysInFuture(startDate: Date, today: Date) {
  const dayInFuture = addDays(today, 364);

  return isAfter(startDate, dayInFuture);
}

export function validateRoomOccupancy(roomIndex: number, searchParamsObj: Record<string, string>) {
  const adultKey = `${URLParams.adult}${roomIndex}`;
  const childKey = `${URLParams.child}${roomIndex}`;
  const cotKey = `${URLParams.cot}${roomIndex}`;
  const roomTypeKey = `${URLParams.roomType}${roomIndex}`;

  const isAdultMissing = searchParamsObj[adultKey] === undefined;
  const isChildMissing = searchParamsObj[childKey] === undefined;
  const isCotMissing = searchParamsObj[cotKey] === undefined;
  const isRoomTypeMissing = searchParamsObj[roomTypeKey] === undefined;
  if (isAdultMissing || isChildMissing || isCotMissing || isRoomTypeMissing) {
    return true;
  }
  const isAdultValid = !isNaN(Number(searchParamsObj[adultKey]));
  const isChildValid = !isNaN(Number(searchParamsObj[childKey]));
  const isCotValid = !isNaN(Number(searchParamsObj[cotKey]));
  const isRoomTypeValid = searchParamsObj[roomTypeKey] !== '';

  const allValid = isAdultValid && isChildValid && isCotValid && isRoomTypeValid;
  return !allValid;
}

export const isPIBACardType = (cardType = '') => {
  const PIBACardTypes = ['AT', 'PI', 'BD', 'PE'];
  return PIBACardTypes.includes(cardType);
};
