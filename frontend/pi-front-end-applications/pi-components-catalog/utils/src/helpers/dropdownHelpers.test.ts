import type { AcceptedRoomCodes, RoomTypeLabels, SearchRoomCodes } from '@whitbread-eos/api';

import {
  createOptionsAdultsChildrenDropdown,
  getAdultsChildrenOptions,
  getMappedRooms,
  getRoomTypeOptions,
} from './dropdownHelpers';

const mockRoomOccupanciesObj = {
  roomOccupancyLimitations: {
    roomOccupancies: [
      {
        adultsNumber: 2,
        childrenNumber: 1,
        acceptedRoomTypes: ['FAM', 'TWIN'] as AcceptedRoomCodes[],
      },
      {
        adultsNumber: 1,
        childrenNumber: 0,
        acceptedRoomTypes: ['DB', 'DIS', 'SB'] as AcceptedRoomCodes[],
      },
      {
        adultsNumber: 3,
        childrenNumber: 1,
        acceptedRoomTypes: ['FAM', 'TWIN'] as AcceptedRoomCodes[],
      },
    ],
  },
};

const mockRoomOccupancies = mockRoomOccupanciesObj.roomOccupancyLimitations.roomOccupancies;

const mockRoomTypeOccupancies = {
  single: 'SB',
  double: 'DB',
  family: 'FAM',
  accessible: 'DIS',
  twin: 'TWIN',
} as RoomTypeLabels;

const mockRoomCodes = {
  SB: 'single',
  DB: 'double',
  FAM: 'family',
  DIS: 'accessible',
  TWIN: 'twin',
} as SearchRoomCodes;

const mockRoomTranslations = {
  SB: 'Single room',
  DB: 'Double room',
  FAM: 'Family room',
  DIS: 'Accessible room',
  TWIN: 'Twin room',
};

describe('dropdownHelpers getters', () => {
  describe('createOptionsAdultsChildrenDropdown Method', () => {
    it('should return an array of correct mappings for adults children dropdown', () => {
      const testArr = [1, 2, 3, 4];
      const testLabelSingular = 'adult';
      const testLabelPlural = 'adults';
      const expectedOutput = [
        {
          id: 1,
          label: '1 adult',
        },
        {
          id: 2,
          label: '2 adults',
        },
        {
          id: 3,
          label: '3 adults',
        },
        {
          id: 4,
          label: '4 adults',
        },
      ];
      expect(
        createOptionsAdultsChildrenDropdown(testArr, testLabelSingular, testLabelPlural)
      ).toEqual(expectedOutput);
    });
  });

  describe('getAdultsChildrenOptions Method', () => {
    it('should return a sorted and filtered array of the adults numbers for each room occupancy', () => {
      const testOption = 'adultsNumber';
      const result = getAdultsChildrenOptions(mockRoomOccupanciesObj, testOption);
      expect(result).toEqual([1, 2, 3]);
    });

    it('should return a sorted and filtered array of the children numbers for each room occupancy', () => {
      const testOption = 'childrenNumber';
      const result = getAdultsChildrenOptions(mockRoomOccupanciesObj, testOption);
      expect(result).toEqual([0, 1]);
    });
  });

  describe('getRoomTypeOptions Method', () => {
    it('should return an array of correct room type options for dropdown', () => {
      const testRoom = { adults: 2, children: 1 };
      const result = getRoomTypeOptions(
        testRoom,
        mockRoomOccupancies,
        mockRoomTypeOccupancies,
        mockRoomCodes
      );
      const expectedOutput = [
        { id: 'FAM', label: 'FAM', code: 'FAM' },
        { id: 'TWIN', label: 'TWIN', code: 'TWIN' },
      ];
      expect(result).toEqual(expectedOutput);
    });

    it('should return an empty array if no room matches the given occupancies array', () => {
      const testRoom = { adults: 1, children: 1 };
      const result = getRoomTypeOptions(
        testRoom,
        mockRoomOccupancies,
        mockRoomTypeOccupancies,
        mockRoomCodes
      );
      const expectedOutput = [];
      expect(result).toEqual(expectedOutput);
    });
  });

  describe('getMappedRooms Method', () => {
    it('should return an object of room codes maped to their translation labels', () => {
      const result = getMappedRooms(mockRoomCodes, mockRoomTranslations);
      const expectedOutput = {
        'Accessible room': 'accessible',
        'Double room': 'double',
        'Family room': 'family',
        'Single room': 'single',
        'Twin room': 'twin',
      };
      expect(result).toEqual(expectedOutput);
    });
  });
});
