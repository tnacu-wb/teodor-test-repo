import { SilentSubstitutionLocalStorage } from '@whitbread-eos/api';

import { getLocalStorageMock } from '../getters/getLocalStorageMock';
import { isArrivalDateWithinSetHours, updateSilentSubstLocalStorage } from './hotel-details';

describe('isArrivalDateWithinSetHours', () => {
  it('should return false if the date is more than 3 days from now', () => {
    expect(isArrivalDateWithinSetHours('2099-10-01', 72)).toEqual(false);
  });
});

const localStorageMock = getLocalStorageMock();

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});

describe('updateSilentSubstLocalStorage', () => {
  const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';
  afterEach(() => {
    localStorageMock.clear();
  });

  test('it should update localStorage with the new record', () => {
    const basketReference = 'basket123';
    const roomsLabelsForSilentSubst = ['label1', 'label2'];

    updateSilentSubstLocalStorage(basketReference, roomsLabelsForSilentSubst);

    const storedData = localStorageMock.getItem(SILENT_SUBSTITUTION_STORAGE_KEY);
    const silentSubstitutionData: Record<string, SilentSubstitutionLocalStorage> = JSON.parse(
      storedData!
    );

    expect(silentSubstitutionData).toHaveProperty(basketReference);
    expect(silentSubstitutionData[basketReference].value).toEqual(roomsLabelsForSilentSubst);
  });

  test('it should delete expired items from localStorage and add the new one', () => {
    const basketReference = 'expiredBasket';
    const roomsLabelsForSilentSubst = ['expiredLabel'];

    // Set up an expired item in localStorage
    const expiredRecord: SilentSubstitutionLocalStorage = {
      value: roomsLabelsForSilentSubst,
      expire: new Date().getTime() - 1000,
    };
    localStorageMock.setItem(
      SILENT_SUBSTITUTION_STORAGE_KEY,
      JSON.stringify({ [basketReference]: expiredRecord })
    );

    updateSilentSubstLocalStorage('newBasket', ['newLabel']);

    const storedData = localStorageMock.getItem(SILENT_SUBSTITUTION_STORAGE_KEY);
    const silentSubstitutionData: Record<string, SilentSubstitutionLocalStorage> = JSON.parse(
      storedData!
    );

    expect(silentSubstitutionData).not.toHaveProperty(basketReference);
    expect(silentSubstitutionData).toHaveProperty('newBasket');
    expect(silentSubstitutionData['newBasket'].value).toEqual(['newLabel']);
  });
});
