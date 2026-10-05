import type {
  HIRoomTypeInfoResponse,
  HIRoomType,
  ReservationRoomType,
  UserChoice,
} from '@whitbread-eos/api';

import { getPmsRoomTypeLabel, getRoomLabel, getTotalReservationAmount } from './Basket.helpers';

const mockRoomTypeInfoResponse: HIRoomTypeInfoResponse = {
  errorRoomTypeInformation: '',
  isLoadingRoomTypeInformation: false,
  isErrorRoomTypeInformation: false,
  dataRoomTypeInformation: {
    roomTypeInformation: {
      roomTypes: [
        {
          roomTypeCode: ['STDWIN'],
          roomCategory: 'Standard',
          roomLabel: 'Standard Room',
          roomDescription: 'A standard room with basic amenities',
          roomImage: '/path/to/image.jpg',
          groupId: 'standard',
        },
        {
          roomTypeCode: ['WINCMB'],
          roomCategory: 'Twin',
          roomLabel: 'Twin Room',
          roomDescription: 'A room with two beds',
          roomImage: '/path/to/twin-image.jpg',
          groupId: 'twin',
        },
        {
          roomTypeCode: ['FMTHRE'],
          roomCategory: 'Family',
          roomLabel: 'Family Room',
          roomDescription: 'A spacious family room',
          roomImage: '/path/to/family-image.jpg',
          groupId: 'family',
        },
      ],
    },
  },
};

const mockReservationRoomTypes: ReservationRoomType[] = [
  {
    roomLabelCode: 'STDWIN',
    pmsRoomType: 'STDWIN',
  },
  {
    roomLabelCode: 'WINCMB',
    pmsRoomType: 'WINCMB',
  },
];

const mockUserChoice = [
  {
    pmsRoomType: 'STDWIN',
    roomType: {
      label: 'Bath',
    },
  },
  {
    pmsRoomType: 'WINCMB',
    roomType: {
      label: 'Shower',
    },
  },
];

const mockTwinRoomLabels = ['Improved Twin Room', 'Standard Twin Room'];

describe('Basket.helpers', () => {
  describe('getPmsRoomTypeLabel', () => {
    it('should return the correct room label for a valid pmsRoomType', () => {
      const result = getPmsRoomTypeLabel(mockRoomTypeInfoResponse, 'STDWIN');
      expect(result).toBe('Standard Room');
    });

    it('should return the correct room label when roomTypeCode includes the pmsRoomType', () => {
      const result = getPmsRoomTypeLabel(mockRoomTypeInfoResponse, 'WINCMB');
      expect(result).toBe('Twin Room');
    });

    it('should return undefined for an invalid pmsRoomType', () => {
      const result = getPmsRoomTypeLabel(mockRoomTypeInfoResponse, 'INVALID');
      expect(result).toBeUndefined();
    });

    it('should return undefined when roomTypeInformation is missing', () => {
      const emptyResponse: HIRoomTypeInfoResponse = {
        errorRoomTypeInformation: '',
        isLoadingRoomTypeInformation: false,
        isErrorRoomTypeInformation: false,
        dataRoomTypeInformation: undefined,
      };
      const result = getPmsRoomTypeLabel(emptyResponse, 'STDWIN');
      expect(result).toBeUndefined();
    });
  });

  describe('getRoomLabel', () => {
    describe('when noRoomTypeSearch is true', () => {
      it('should return combined pmsRoomType label and user choice label', () => {
        const result = getRoomLabel(
          0,
          true,
          mockRoomTypeInfoResponse,
          mockUserChoice,
          mockReservationRoomTypes,
          mockTwinRoomLabels
        );
        expect(result).toBe('Standard Room, Bath');
      });

      it('should return correct label for second room index', () => {
        const result = getRoomLabel(
          1,
          true,
          mockRoomTypeInfoResponse,
          mockUserChoice,
          mockReservationRoomTypes,
          mockTwinRoomLabels
        );
        expect(result).toBe('Twin Room, Shower');
      });

      it('should handle undefined roomType label gracefully', () => {
        const userChoiceWithUndefined = [
          {
            pmsRoomType: 'STDWIN',
            roomType: undefined,
            roomNumber: 1,
          },
        ];
        const result = getRoomLabel(
          0,
          true,
          mockRoomTypeInfoResponse,
          userChoiceWithUndefined,
          mockReservationRoomTypes,
          mockTwinRoomLabels
        );
        expect(result).toContain('Standard Room');
      });
    });

    describe('when noRoomTypeSearch is false', () => {
      it('should return standard room label from getStandardRoomLabel', () => {
        const result = getRoomLabel(
          0,
          false,
          mockRoomTypeInfoResponse,
          mockUserChoice,
          mockReservationRoomTypes,
          mockTwinRoomLabels
        );
        expect(result).toBe('Standard Room');
      });

      it('should return correct label for second room', () => {
        const result = getRoomLabel(
          1,
          false,
          mockRoomTypeInfoResponse,
          mockUserChoice,
          mockReservationRoomTypes,
          mockTwinRoomLabels
        );
        expect(result).toBe('Twin Room');
      });

      it('should handle twinroom selections', () => {
        const twinroomSelections = ['twobeds', 'onebed'];
        const result = getRoomLabel(
          0,
          false,
          mockRoomTypeInfoResponse,
          mockUserChoice,
          mockReservationRoomTypes,
          mockTwinRoomLabels,
          twinroomSelections
        );
        // When twinroomSelection is 'twobeds', it should return the first label
        expect(result).toBe('Improved Twin Room');
      });

      it('should return null when reservationRoomType is undefined', () => {
        const result = getRoomLabel(
          5, // Out of bounds index
          false,
          mockRoomTypeInfoResponse,
          mockUserChoice,
          mockReservationRoomTypes,
          mockTwinRoomLabels
        );
        expect(result).toBeNull();
      });
    });

    describe('edge cases', () => {
      it('should handle empty userChoice array', () => {
        const result = getRoomLabel(
          0,
          true,
          mockRoomTypeInfoResponse,
          [],
          mockReservationRoomTypes,
          mockTwinRoomLabels
        );
        expect(result).toContain('undefined');
      });

      it('should handle empty reservationRoomTypes array', () => {
        const result = getRoomLabel(
          0,
          false,
          mockRoomTypeInfoResponse,
          mockUserChoice,
          [],
          mockTwinRoomLabels
        );
        expect(result).toBeNull();
      });

      it('should handle undefined twinroomSelections', () => {
        const result = getRoomLabel(
          0,
          false,
          mockRoomTypeInfoResponse,
          mockUserChoice,
          mockReservationRoomTypes,
          mockTwinRoomLabels,
          undefined
        );
        expect(result).toBeTruthy();
      });
    });
  });

  describe('getTotalReservationAmount', () => {
    const createRoom = (pmsRoomType: string, totalNetAmount: number) =>
      ({
        pmsRoomType,
        roomPriceBreakdown: { totalNetAmount },
      }) as HIRoomType['rooms'][number];

    const createRoomType = (rooms: HIRoomType['rooms'][number][]) =>
      ({
        roomType: 'DB',
        adults: 2,
        children: 0,
        cotRequested: false,
        rooms,
      }) as HIRoomType;

    const mockRoomTypes: HIRoomType[] = [
      createRoomType([createRoom('PPLDBL', 120), createRoom('DOUBLE', 95)]),
      createRoomType([createRoom('PPLDBL', 130), createRoom('DOUBLE', 100)]),
    ];

    describe('when noRoomTypeSearch is true', () => {
      it('should sum totalNetAmount matching userChoice pmsRoomType per room', () => {
        const userChoice = [
          {
            roomNumber: 1,
            pmsRoomType: 'PPLDBL',
            roomType: { label: 'Double', code: 'DB', id: 'DB' },
          },
          {
            roomNumber: 2,
            pmsRoomType: 'DOUBLE',
            roomType: { label: 'Double', code: 'DB', id: 'DB' },
          },
        ] as UserChoice[];

        const result = getTotalReservationAmount(mockRoomTypes, true, userChoice, []);

        expect(result).toBe(120 + 100);
      });

      it('should return 0 when pmsRoomType does not match any room', () => {
        const userChoice = [
          {
            roomNumber: 1,
            pmsRoomType: 'NONEXISTENT',
            roomType: { label: 'X', code: 'DB', id: 'DB' },
          },
        ] as UserChoice[];

        const result = getTotalReservationAmount([mockRoomTypes[0]], true, userChoice, []);

        expect(result).toBe(0);
      });

      it('should handle empty userChoice gracefully', () => {
        const result = getTotalReservationAmount(mockRoomTypes, true, [], []);

        expect(result).toBe(0);
      });
    });

    describe('when noRoomTypeSearch is false', () => {
      it('should sum totalNetAmount using roomsIndexes', () => {
        const roomsIndexes = [0, 1]; // PPLDBL for room 1, DOUBLE for room 2

        const result = getTotalReservationAmount(mockRoomTypes, false, [], roomsIndexes);

        expect(result).toBe(120 + 100);
      });

      it('should use correct index per roomType', () => {
        const roomsIndexes = [1, 0]; // DOUBLE for room 1, PPLDBL for room 2

        const result = getTotalReservationAmount(mockRoomTypes, false, [], roomsIndexes);

        expect(result).toBe(95 + 130);
      });

      it('should return 0 when roomsIndexes point out of bounds', () => {
        const roomsIndexes = [5, 10];

        const result = getTotalReservationAmount(mockRoomTypes, false, [], roomsIndexes);

        expect(result).toBe(0);
      });
    });

    describe('edge cases', () => {
      it('should return 0 when roomTypes is undefined', () => {
        const result = getTotalReservationAmount(undefined, false, [], []);

        expect(result).toBe(0);
      });

      it('should return 0 when roomTypes is empty', () => {
        const result = getTotalReservationAmount([], true, [], []);

        expect(result).toBe(0);
      });

      it('should handle a single room type correctly', () => {
        const singleRoomType = [createRoomType([createRoom('PPLDBL', 75)])];
        const userChoice = [
          {
            roomNumber: 1,
            pmsRoomType: 'PPLDBL',
            roomType: { label: 'Double', code: 'DB', id: 'DB' },
          },
        ] as UserChoice[];

        const result = getTotalReservationAmount(singleRoomType, true, userChoice, []);

        expect(result).toBe(75);
      });
    });
  });
});
