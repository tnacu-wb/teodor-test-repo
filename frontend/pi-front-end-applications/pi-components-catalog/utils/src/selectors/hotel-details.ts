import {
  ReservationRoomType,
  SilentSubstitutionLocalStorage,
  SILENT_SUBSTITUTION_STORAGE_KEY,
} from '@whitbread-eos/api';
import { differenceInHours } from 'date-fns';

export function isArrivalDateWithinSetHours(arrivalDate: string, setHours: number) {
  // When calculating the difference in hours, we compare with the current date starting at 0:00 AM
  const currentDate = new Date();
  currentDate.setHours(0, 0, 0, 0);
  const arrival = new Date(arrivalDate);
  arrival.setHours(0, 0, 0, 0);
  return differenceInHours(arrival, currentDate) <= setHours;
}

export function updateSilentSubstLocalStorage(
  basketReference: string,
  roomsLabelsForSilentSubst: ReservationRoomType[]
) {
  let silentSubstitutionData: Record<string, SilentSubstitutionLocalStorage> = {};
  const storedData = window.localStorage.getItem(SILENT_SUBSTITUTION_STORAGE_KEY);
  const currentTime = new Date().getTime();

  if (storedData) {
    silentSubstitutionData = JSON.parse(storedData);

    Object.keys(silentSubstitutionData).forEach((key) => {
      const item = silentSubstitutionData[key];
      if (item.expire && item.expire < currentTime) {
        delete silentSubstitutionData[key];
      }
    });
  }

  const expirationTime = 30 * 60 * 1000;
  const newRecord: SilentSubstitutionLocalStorage = {
    value: roomsLabelsForSilentSubst,
    expire: currentTime + expirationTime,
  };
  silentSubstitutionData[basketReference] = newRecord;

  window.localStorage.setItem(
    SILENT_SUBSTITUTION_STORAGE_KEY,
    JSON.stringify(silentSubstitutionData)
  );
}
