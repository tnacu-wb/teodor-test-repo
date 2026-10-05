import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { BOOK_MUTATION, type Channel, ROOM_TYPE } from '@whitbread-eos/api';
import React from 'react';

import { fireEvent, render } from '../../utils/test-utils';
import RateSelector from './RateSelector.container';
import {
  mockHotelAvailabilityData,
  mockRoomTypeInformation,
  mockGlobalConfig,
} from './mocksRateSelectorData';

const mockCustomLocale = jest.fn();

const mockedMutationRequest = {
  mutation: {
    mutate: jest.fn(),
  },
  isSuccess: true,
};

const mockUseMutationRequest = jest.fn();
const mockUseAuthToken = jest.fn(() => ({
  token: 'the-auth-token',
  isAuth0Enabled: false,
  isLoading: false,
}));

jest.mock('./.', () => ({
  RateSelectorComponent: () => <div />,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useForDiscountedRateMicroSite: jest.fn().mockReturnValue(true),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  useMutationRequest: (...args: unknown[]) => {
    mockUseMutationRequest(...args);
    return mockedMutationRequest;
  },
  useAuthToken: () => mockUseAuthToken(),
  useQueryRequest: () =>
    jest.fn().mockImplementation((queryKey: string | any[]) => {
      let queryKeyValue = queryKey;
      if (Array.isArray(queryKey)) {
        queryKeyValue = queryKey[0];
      }
      if (queryKeyValue === 'getRoomTypeInformation') {
        return mockRoomTypeInformation;
      }
      if (queryKeyValue === 'getGlobalConfig') {
        return mockGlobalConfig;
      }
      return {};
    }),
  useStaticHotelInformation: () => ({
    brand: 'PI',
    hotelId: 'MANOLD',
    bookingFlow: {
      bookingFlowItems: [],
    },
    contactDetails: {
      email: 'manold@premier-inn.com',
    },
    accessibilityInfo: {
      header: 'Accessibility header',
    },
  }),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  RateCard: () => <div />,
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  basketData: {
    hotelId: 'TKINPT',
    arrival: '2022-06-25',
    departure: '2022-06-29',
    numberOfAdults: 1,
    numberOfChildren: 0,
    reservationRoomTypes: ROOM_TYPE.DOUBLE,
    numberOfUnits: 1,
  },
  data: mockHotelAvailabilityData,
};

const mockedProps = {
  channel: 'PI' as Channel,
  variant: 'PI',
  hotelAvailabilityResponse: {
    isLoadingHotelAvailability: mockResponse.isLoading,
    isErrorHotelAvailability: mockResponse.isError,
    errorHotelAvailability: mockResponse.error,
    dataHotelAvailability: mockResponse.data,
  },
  globalConfigResponse: {
    isLoadingGlobalConfig: mockGlobalConfig.isLoading,
    isErrorGlobalConfig: mockGlobalConfig.isError,
    errorGlobalConfig: mockGlobalConfig.error,
    dataGlobalConfig: mockGlobalConfig.data,
  },
  queryClient: new QueryClient(),
  isHotelOpeningSoon: false,
  isParentAnalytics: true,
  arrival: '2023-04-14',
  departure: '2023-04-15',
  numberOfUnits: 1,
  numberOfNights: 1,
  isLessThanSm: true,
  isLessThanMd: true,
  isLessThanLg: false,
  prevReservationId: '',
  isSilentSubstitution: true,
  mappedRoomLabels: {
    DIS: 'Accessible',
    DB: 'Double',
    FAM: 'Family',
    SB: 'Single',
    TWIN: 'Twin',
  },
};

describe('RateSelector  CCUI', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
    });
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      query: {
        INTTYP1: 'DB',
      },
    });
    mockUseAuthToken.mockReturnValue({
      token: 'the-auth-token',
      isAuth0Enabled: false,
      isLoading: false,
    });
  });

  it('should pass the resolved auth token into the booking mutation for PI variant', function () {
    mockedProps.variant = 'PI';

    render(<RateSelector {...mockedProps} />);

    const bookMutationCall = mockUseMutationRequest.mock.calls.find(
      ([gqlTemplateString]) => gqlTemplateString === BOOK_MUTATION
    );
    expect(bookMutationCall?.[2]).toBe('the-auth-token');
  });

  it('should not pass a token into the booking mutation while the auth token is still loading', function () {
    mockedProps.variant = 'PI';
    mockUseAuthToken.mockReturnValue({
      token: undefined,
      isAuth0Enabled: true,
      isLoading: true,
    });

    render(<RateSelector {...mockedProps} />);

    const bookMutationCall = mockUseMutationRequest.mock.calls.find(
      ([gqlTemplateString]) => gqlTemplateString === BOOK_MUTATION
    );
    expect(bookMutationCall?.[2]).toBeUndefined();
  });

  it('should call mockMutation', function () {
    mockedProps.prevReservationId = 'MAH-2c91aa7b-8011-4d25-93e6-6ce464e0dde0';
    mockedProps.variant = 'CCUI';
    const { getByTestId } = render(<RateSelector {...mockedProps} />);

    const bookNowButton = getByTestId('hdp_basketBookNowButton');
    fireEvent.click(bookNowButton);

    expect(mockedMutationRequest.mutation.mutate).toBeCalledTimes(1);
  });
});
