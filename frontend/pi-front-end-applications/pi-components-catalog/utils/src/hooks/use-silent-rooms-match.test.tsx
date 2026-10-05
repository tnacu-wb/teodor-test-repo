import { getLocalStorageMock, bknReservationsMock } from '@whitbread-eos/utils';

import useSilentRoomsMatch from './use-silent-rooms-match';

const localStorageMock = getLocalStorageMock();
const mockUseFeatureSwitch = jest.fn();

jest.mock('./use-feature-switch', () => () => mockUseFeatureSwitch());

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});

const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';
describe('useSilentRoomsMatch method', () => {
  const initialLocalStorageMock = [
    {
      roomLabelCode: 'Double room',
      silentSubstitution: true,
      adults: 1,
      children: 0,
      pmsRoomType: 'DOUBLE',
    },
    {
      roomLabelCode: 'Family room',
      silentSubstitution: true,
      adults: 2,
      children: 1,
      pmsRoomType: 'FMTRPL',
    },
    {
      roomLabelCode: 'Twin room',
      silentSubstitution: true,
      adults: 2,
      children: 0,
      pmsRoomType: 'FMTRPL',
    },
    {
      roomLabelCode: 'Double room',
      silentSubstitution: true,
      adults: 2,
      children: 0,
      pmsRoomType: 'DOUBLE',
    },
  ];

  const filteredStorageMock = [
    {
      adults: 2,
      children: 0,
      filtered: true,
      pmsRoomType: 'DOUBLE',
      roomLabelCode: 'Double room',
      silentSubstitution: true,
    },
    {
      adults: 2,
      children: 0,
      filtered: true,
      pmsRoomType: 'FMTRPL',
      roomLabelCode: 'Twin room',
      silentSubstitution: true,
    },
    {
      adults: 2,
      children: 1,
      filtered: true,
      pmsRoomType: 'FMTRPL',
      roomLabelCode: 'Family room',
      silentSubstitution: true,
    },
    {
      adults: 1,
      children: 0,
      filtered: true,
      pmsRoomType: 'DOUBLE',
      roomLabelCode: 'Double room',
      silentSubstitution: true,
    },
  ];

  localStorageMock.setItem(
    SILENT_SUBSTITUTION_STORAGE_KEY,
    JSON.stringify({
      reservation123: {
        value: initialLocalStorageMock,
        expire: 123,
      },
    })
  );
  it('should return the correct value from matchedSubstitutions method', () => {
    mockUseFeatureSwitch.mockReturnValue(true);

    const result = useSilentRoomsMatch('reservation123', bknReservationsMock);

    expect(result).toEqual(filteredStorageMock);
  });

  it('should return the correct value from matchedSubstitutions method with already filtered rooms', () => {
    mockUseFeatureSwitch.mockReturnValue(true);

    localStorageMock.setItem(
      SILENT_SUBSTITUTION_STORAGE_KEY,
      JSON.stringify({
        reservation123: {
          value: filteredStorageMock,
          expire: 123,
        },
      })
    );

    const result = useSilentRoomsMatch('reservation123', bknReservationsMock);

    expect(result).toEqual(filteredStorageMock);
  });

  it('should return the correct value from matchedSubstitutions method with Feature Flag on false', () => {
    mockUseFeatureSwitch.mockReturnValue(false);

    const result = useSilentRoomsMatch('reservation123', bknReservationsMock);

    expect(result).toEqual([]);
  });

  it('should return the correct value from matchedSubstitutions method with no data from Local Storage', () => {
    mockUseFeatureSwitch.mockReturnValue(true);

    localStorageMock.setItem(SILENT_SUBSTITUTION_STORAGE_KEY, '');

    const result = useSilentRoomsMatch('reservation123', bknReservationsMock);

    expect(result).toEqual([]);
  });
});
