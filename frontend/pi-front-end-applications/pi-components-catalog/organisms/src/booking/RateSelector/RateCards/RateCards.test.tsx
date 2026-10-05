import '@testing-library/jest-dom';
import {
  type Channel,
  type HIAvailabilityRates,
  type HIRoomClassCode,
  type HIRoomType,
  type HIRoomTypeInfoResponse,
  BOOKING_CHANNEL,
  ROOM_TYPE,
} from '@whitbread-eos/api';
import { useFeatureToggle, useStaticHotelInformation, useCustomLocale } from '@whitbread-eos/utils';
import { analytics } from '@whitbread-eos/utils';

import { render } from '../../../utils/test-utils';
import RateCards from './RateCards.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getAvailableRoomTypes: jest.fn((roomTypes) => roomTypes),
  getRoomClassByRoomClassCode: jest.fn(() => 'Standard'),
  getRateClassification: jest.fn((ratePlanCode, classifications) =>
    classifications?.find((c) => c?.ratePlanCode === ratePlanCode)
  ),
  getRoomRatesThatMatchRoomClassifications: jest.fn((classifications, roomRates) => roomRates),
  useCustomLocale: jest.fn(),
  getRoomClassByCodeAndType: jest.fn(() => 'Standard Room'),
  useFeatureToggle: jest.fn(),
  useStaticHotelInformation: jest.fn(),
  analytics: {
    update: jest.fn(),
  },
}));

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  RateCard: jest.fn(() => <div data-testid="rate-card">RateCard</div>),
}));

const mockBasketData = {
  hotelId: 'TKINPT',
  arrival: '2022-06-25',
  departure: '2022-06-29',
  numberOfUnits: 1,
  numberOfNights: 4,
};

const createMockRoomRate = (
  ratePlanCode: string,
  roomClassCode: HIRoomClassCode,
  numberOfRoomsAvailable?: number | null,
  isSubstitution?: boolean,
  substitution?: string | null
) => ({
  ratePlanCode,
  roomTypes: [
    {
      roomType: ROOM_TYPE.DOUBLE,
      adults: 2,
      children: 0,
      cotRequested: false,
      rooms: [
        {
          pmsRoomType: 'BRFDBL',
          silentSubstitution: false,
          roomClass: roomClassCode,
          cotAvailable: false,
          numberOfRoomsAvailable,
          isSubstitution,
          substitution,
          roomPriceBreakdown: {
            totalNetAmount: 115.33,
            currencyCode: 'GBP',
            dailyPrices: [
              {
                date: '2022-08-13',
                netPrice: 75.0,
              },
              {
                date: '2022-08-14',
                netPrice: 40.33,
              },
            ],
          },
          specialRequests: ['TW2S'],
        },
      ],
    },
  ],
});

const createMockAvailabilityRates = (
  roomRates: any[] = [],
  rateClassifications: any[] = []
): HIAvailabilityRates => ({
  hotelAvailability: {
    hotelId: 'LONEUS',
    startDate: '2022-08-13',
    endDate: '2022-08-14',
    available: true,
    limitedAvailability: false,
    roomRates,
  },
  ratesInformation: {
    rateClassifications,
  },
});

const createMockRoomTypeInfo = (): HIRoomTypeInfoResponse => ({
  dataRoomTypeInformation: {
    loading: false,
    error: null,
    roomTypeInformation: [
      {
        code: ROOM_TYPE.DOUBLE,
        name: 'Double',
      },
    ],
  },
});

const createMockRoomTypes = (): HIRoomType[] => [
  {
    roomType: ROOM_TYPE.DOUBLE,
    adults: 2,
    children: 0,
    cotRequested: false,
    rooms: [],
  },
];

const defaultProps = {
  roomClassCodes: ['ST' as HIRoomClassCode],
  data: createMockAvailabilityRates(),
  allRoomTypes: createMockRoomTypes(),
  brand: 'premier-inn',
  channel: BOOKING_CHANNEL.WEB as Channel,
  selectedRoomClassAndRate: 'ST-FLEXRATE',
  setSelectedRoomClassAndRate: jest.fn(),
  roomTypeInformationResponse: createMockRoomTypeInfo(),
  isLessThanSm: false,
  isLessThanMd: false,
  basketData: mockBasketData,
};

describe('RateCards - roomsLeft logic', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useCustomLocale as jest.Mock).mockReturnValue({ language: 'en' });
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE: false,
      FT_PI_HDP_URGENCY_BANNER: false,
    });
    (useStaticHotelInformation as jest.Mock).mockReturnValue({
      roomClassConfiguration: [],
      roomConfiguration: {},
    });
  });

  it('should compute roomsLeft as null when numberOfRoomsAvailable is null', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', null)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].roomsLeft).toBeNull();
  });

  it('should compute roomsLeft as null when numberOfRoomsAvailable is 11', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 11)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].roomsLeft).toBeNull();
  });

  it('should compute roomsLeft as null when numberOfRoomsAvailable is 50', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 50)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].roomsLeft).toBeNull();
  });

  it('should compute roomsLeft as null when numberOfRoomsAvailable is 100', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 100)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].roomsLeft).toBeNull();
  });

  it('should compute roomsLeft as 10 when numberOfRoomsAvailable is 10', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 10)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].roomsLeft).toBe(10);
  });

  it('should compute roomsLeft as 1 when numberOfRoomsAvailable is 1', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 1)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].roomsLeft).toBe(1);
  });

  it('should compute roomsLeft as 5 when numberOfRoomsAvailable is 5', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 5)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].roomsLeft).toBe(5);
  });

  it('should compute roomsLeft as 0 when numberOfRoomsAvailable is 0', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 0)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].roomsLeft).toBe(0);
  });
});

describe('RateCards - substitution analytics fields', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useCustomLocale as jest.Mock).mockReturnValue({ language: 'en' });
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE: false,
      FT_PI_HDP_URGENCY_BANNER: false,
    });
    (useStaticHotelInformation as jest.Mock).mockReturnValue({
      roomClassConfiguration: [],
      roomConfiguration: {},
    });
  });

  it('should set isSubstitution to false and substitutionCode to null by default', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 5)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].isSubstitution).toBe(false);
    expect(analyticsCall.roomsOffered[0].substitutionCode).toBeNull();
  });

  it('should set isSubstitution to true when room is a substitution', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 5, true)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].isSubstitution).toBe(true);
  });

  it('should set substitutionCode when provided', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 5, true, 'SUB123')];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].substitutionCode).toBe('SUB123');
  });

  it('should have isSubstitution true and substitutionCode null when isSubstitution is true but code is not provided', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 5, true, null)];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].isSubstitution).toBe(true);
    expect(analyticsCall.roomsOffered[0].substitutionCode).toBeNull();
  });

  it('should have isSubstitution false and substitutionCode with value when isSubstitution is false', () => {
    const roomRates = [createMockRoomRate('FLEXRATE', 'ST', 5, false, 'SUB456')];
    const data = createMockAvailabilityRates(roomRates, [
      {
        rateClassification: 'FLEXRATE',
        ratePlanCode: 'FLEXRATE',
        rateName: 'Flex',
        rateDescription: 'Flexible rate',
        rateOrder: '1',
        rateTags: [],
      },
    ]);

    render(<RateCards {...defaultProps} data={data} />);

    expect(analytics.update).toHaveBeenCalled();
    const analyticsCall = (analytics.update as jest.Mock).mock.calls[0][0];
    expect(analyticsCall.roomsOffered[0].isSubstitution).toBe(false);
    expect(analyticsCall.roomsOffered[0].substitutionCode).toBe('SUB456');
  });
});
