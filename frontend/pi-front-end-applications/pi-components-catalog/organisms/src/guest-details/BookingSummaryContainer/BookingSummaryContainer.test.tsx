import '@testing-library/jest-dom';
import { fireEvent } from '@testing-library/react';
import { Area } from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import BookingSummaryContainer, { type Props } from './BookingSummaryContainer.component';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    invalidateQueries: jest.fn(),
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => mockedMutationRequest(),
}));

const mockedMutationRequest = jest.fn();

const mockUseMutationResponse = {
  mutation: {
    mutate: jest.fn(),
  },
  data: {
    cancelReservation: {
      basketReference: 'AKU2084403',
    },
  },
  onSuccess: jest.fn(),
  isSuccess: true,
  isError: false,
};

const bkngMockData = {
  bookingInformation: {
    hotelId: 'MANOLD',
    totalCost: 59,
    currencyCode: 'GBP',
    bookingFlowId: 'booking-a1',
    infoMessages: [
      '<p>Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival</p>\n',
    ],
    reservationByIdList: [
      {
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-08-29',
          departureDate: '2022-08-30',
          ratePlanCode: 'STANDARD',
          rateExtraInfo: { rateName: 'standard' },
          roomExtraInfo: { roomType: 'Double', roomName: 'Premier Plus double' },
          accessibleRoom: { isAccessible: false, phoneNumber: '0333 321 1315' },
        },
      },
    ],
    upgradeToFlex: { amount: 10, currency: 'EUR', flexRateCode: 'GB' },
  },
};
const hiMockData = { hotelName: 'hotelNameText', hotelAddress: ['addr1'] };
const pcksMockData = {
  meals: [
    {
      allergyInfoLabel: 'Allergy & nutrition info',
      allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
      currency: 'GBP',
      description: '<p>Add our unlimited breakfast</p>',
      id: 'BFADBF',
      imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
      name: 'Premier Inn Breakfast',
      price: 110.99,
      order: 1,
      freeBreakfastOption: true,
      freeBreakfastCode: 'BFCHDF',
      freeBreakfastMaxPerMeal: 2,
    },
    {
      allergyInfoLabel: 'Allergy & nutrition info',
      allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
      currency: 'GBP',
      description: '<p>A lighter start with tasty pastries</p>',
      id: 'BFADCT',
      imageSrc: '/content/dam/global/restaurants/Global/continental-breakfast-booking.png',
      name: 'Continental Breakfast',
      price: 9,
      order: 2,
      freeBreakfastOption: false,
      freeBreakfastCode: '',
      freeBreakfastMaxPerMeal: 2,
    },
    {
      allergyInfoLabel: null,
      allergyInfoSrc: null,
      currency: 'GBP',
      description: '<p>Save up to 20% off with our Meal Deal</p>',
      id: 'MDP',
      imageSrc: '/content/dam/global/restaurants/Global/meal-deal-booking.png',
      name: 'Meal Deal',
      price: 26.99,
      order: 3,
      freeBreakfastOption: true,
      freeBreakfastCode: 'BFCHDF',
      freeBreakfastMaxPerMeal: 2,
    },
  ],
  mealsKids: [
    {
      allergyInfoLabel: null,
      allergyInfoSrc: null,
      currency: null,
      description: '<p>Up to two kids eat breakfast for free</p>',
      id: 'BFCHDF',
      imageSrc: '/content/dam/global/restaurants/Global/child-breakfast.jpg',
      name: 'Free breakfast for kids',
      order: 0,
    },
  ],
  extrasItems: [
    {
      currency: 'GBP',
      description: 'Early check-in from 11am',
      id: 'HSCKIN',
      imageSrc: '/content/dam/global/extras/early-check-in.png',
      name: 'Early check-in',
      order: 1,
      price: 10,
    },
    {
      currency: 'GBP',
      description: 'Check out at 2pm',
      id: 'HSCOU2',
      imageSrc: '/content/dam/global/extras/late-checkout.png',
      name: 'Late checkout',
      order: 3,
      price: 10,
    },
    {
      currency: 'GBP',
      description: 'Ultimate Wi-Fi package',
      id: 'FI24HR',
      imageSrc: '/content/dam/global/extras/ultimate-wifi.png',
      name: 'Ultimate Wi-Fi',
      order: 1,
      price: 5,
    },
  ],
  roomSelection: [
    {
      reservationId: '1',
      packagesSelection: [],
    },
  ],
  roomSelectionAmendExtras: [],
};

const basketReferenceId = 'MANOLD3918343';

// Scenario 1: No extras selected
// Standard rate: £100 | Flex rate: £160 | Upgrade difference: £60
const pcksMockDataNoExtras = {
  ...pcksMockData,
  roomSelection: [
    {
      reservationId: '1',
      packagesSelection: [], // No meals, no extras
    },
  ],
};

const bkngMockDataNoExtras = {
  bookingInformation: {
    hotelId: 'MANOLD',
    totalCost: 100, // Standard rate only
    currencyCode: 'GBP',
    bookingFlowId: 'booking-a1',
    infoMessages: [],
    reservationByIdList: [
      {
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-08-29',
          departureDate: '2022-08-30',
          ratePlanCode: 'STANDARD',
          rateExtraInfo: { rateName: 'standard' },
          roomExtraInfo: { roomType: 'Double', roomName: 'Premier Plus double' },
          accessibleRoom: { isAccessible: false, phoneNumber: '0333 321 1315' },
        },
      },
    ],
    upgradeToFlex: { amount: 160, currency: 'GBP', flexRateCode: 'FLEX' }, // Flex rate: £160 (fixed)
  },
};

// Scenario 2: Single extra (£10 late checkout)
// Standard rate: £100 + £10 extra = £110 | Flex rate: £160 | Upgrade difference: £60 (same)
const pcksMockDataSingleExtra = {
  ...pcksMockData,
  roomSelection: [
    {
      reservationId: '1',
      packagesSelection: [
        { id: 'HSCOU2', noOfSelections: 1 }, // Only the extra, no meals
      ],
    },
  ],
};

const bkngMockDataSingleExtra = {
  bookingInformation: {
    hotelId: 'MANOLD',
    totalCost: 110, // Standard £100 + £10 extra
    currencyCode: 'GBP',
    bookingFlowId: 'booking-a1',
    infoMessages: [],
    reservationByIdList: [
      {
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-08-29',
          departureDate: '2022-08-30',
          ratePlanCode: 'STANDARD',
          rateExtraInfo: { rateName: 'standard' },
          roomExtraInfo: { roomType: 'Double', roomName: 'Premier Plus double' },
          accessibleRoom: { isAccessible: false, phoneNumber: '0333 321 1315' },
        },
      },
    ],
    upgradeToFlex: { amount: 160, currency: 'GBP', flexRateCode: 'FLEX' }, // Flex rate: £160 (same)
  },
};

// Scenario 3: Multiple extras (£10 early check-in + £10 late checkout = £20 total)
// Standard rate: £100 + £20 extras = £120 | Flex rate: £160 | Upgrade difference: £60 (same)
const pcksMockDataMultipleExtras = {
  ...pcksMockData,
  roomSelection: [
    {
      reservationId: '1',
      packagesSelection: [
        { id: 'HSCKIN', noOfSelections: 1 }, // Extra 1, no meals
        { id: 'HSCOU2', noOfSelections: 1 }, // Extra 2
      ],
    },
  ],
};

const bkngMockDataMultipleExtras = {
  bookingInformation: {
    hotelId: 'MANOLD',
    totalCost: 120, // Standard £100 + £20 extras (£10 + £10)
    currencyCode: 'GBP',
    bookingFlowId: 'booking-a1',
    infoMessages: [],
    reservationByIdList: [
      {
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-08-29',
          departureDate: '2022-08-30',
          ratePlanCode: 'STANDARD',
          rateExtraInfo: { rateName: 'standard' },
          roomExtraInfo: { roomType: 'Double', roomName: 'Premier Plus double' },
          accessibleRoom: { isAccessible: false, phoneNumber: '0333 321 1315' },
        },
      },
    ],
    upgradeToFlex: { amount: 160, currency: 'GBP', flexRateCode: 'FLEX' }, // Flex rate: £160 (same)
  },
};

const mockProps: Props = {
  packages: pcksMockData,
  bkngData: bkngMockData,
  hiData: hiMockData,
  biQueryInput: '',
  basketReferenceId: basketReferenceId,
  variant: 'desktop',
  t: () => {
    return 'default';
  },
  language: 'en',
  taxesMessage: '',
  area: Area.CCUI,
};

describe('BookingSummaryContainer', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockUseRouter.mockReturnValue({
      locale: 'en',
    });
    mockedMutationRequest.mockReturnValue(mockUseMutationResponse);
  });

  it('should render BookingSummaryContainer', () => {
    const { getByTestId } = render(<BookingSummaryContainer {...mockProps} />);
    expect(getByTestId('BookingSummaryContainer')).toBeInTheDocument();
  });

  it('should have upgrade to flex button', async () => {
    const { getByTestId } = render(<BookingSummaryContainer {...mockProps} />);
    const button = getByTestId('UpgradeToFlex-Button');
    expect(button).toBeInTheDocument();
  });

  it('should NOT have upgrade to flex button', async () => {
    const { queryByTestId } = render(
      <BookingSummaryContainer {...mockProps} isSoftBundlesVisible={true} />
    );
    const button = queryByTestId('UpgradeToFlex-Button');
    expect(button).not.toBeInTheDocument();
  });

  it('should use latest upgrade-to-flex data after rerender', async () => {
    const mutate = jest.fn();
    mockedMutationRequest.mockReturnValue({
      ...mockUseMutationResponse,
      mutation: {
        mutate,
      },
    });

    const initialBkngData = {
      ...bkngMockData,
      bookingInformation: {
        ...bkngMockData.bookingInformation,
        upgradeToFlex: {
          amount: null,
          currency: null,
          flexRateCode: null,
        },
      },
    };

    const updatedBkngData = {
      ...bkngMockData,
      bookingInformation: {
        ...bkngMockData.bookingInformation,
        upgradeToFlex: {
          amount: 10,
          currency: 'GBP',
          flexRateCode: 'FLEXRATE',
        },
      },
    };

    const { rerender, getByTestId, queryByTestId } = render(
      <BookingSummaryContainer {...mockProps} bkngData={initialBkngData} />
    );

    expect(queryByTestId('UpgradeToFlex-Button')).not.toBeInTheDocument();

    rerender(<BookingSummaryContainer {...mockProps} bkngData={updatedBkngData} />);

    fireEvent.click(getByTestId('UpgradeToFlex-Button'));

    expect(mutate).toHaveBeenCalledWith(
      expect.objectContaining({
        rateCode: 'FLEXRATE',
        currency: 'GBP',
      })
    );
  });

  describe('Upgrade to Flex amount calculation with extras', () => {
    it('should display upgrade to flex amount of £60 when no extras are selected', () => {
      const { getByTestId } = render(
        <BookingSummaryContainer
          {...mockProps}
          packages={pcksMockDataNoExtras}
          bkngData={bkngMockDataNoExtras}
        />
      );

      // Verify the upgrade amount is displayed on screen
      const upgradeAmountElement = getByTestId('UpgradeToFlex-CostForCancel-Message');
      expect(upgradeAmountElement).toHaveTextContent('£60.00');
    });

    it('should display upgrade to flex amount of £60 when 1 extra (£10) is selected', () => {
      const { getByTestId } = render(
        <BookingSummaryContainer
          {...mockProps}
          packages={pcksMockDataSingleExtra}
          bkngData={bkngMockDataSingleExtra}
        />
      );

      // Verify the upgrade amount is displayed on screen (NOT affected by extra)
      const upgradeAmountElement = getByTestId('UpgradeToFlex-CostForCancel-Message');
      expect(upgradeAmountElement).toHaveTextContent('£60.00');
    });

    it('should display upgrade to flex amount of £60 when multiple extras (£20 total) are selected', () => {
      const { getByTestId } = render(
        <BookingSummaryContainer
          {...mockProps}
          packages={pcksMockDataMultipleExtras}
          bkngData={bkngMockDataMultipleExtras}
        />
      );

      // Verify the upgrade amount is displayed on screen (NOT affected by extras)
      const upgradeAmountElement = getByTestId('UpgradeToFlex-CostForCancel-Message');
      expect(upgradeAmountElement).toHaveTextContent('£60.00');
    });
  });
});
