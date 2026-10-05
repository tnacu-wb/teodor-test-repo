import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import '@testing-library/jest-dom/extend-expect';
import { FT_MOBILE_PREREGISTERED_REPURPOSE } from '@whitbread-eos/api';
import { useRouter } from 'next/router';
import React from 'react';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import PreCheckInPage from './page.pi';

const mockRouter = {
  route: '/',
  pathname: '',
  query: { bookingReference: 'GBH2260924', reservationId: '1685621' },
  asPath: '',
  push: jest.fn(),
  replace: jest.fn(),
  reload: jest.fn(),
  back: jest.fn(),
  prefetch: jest.fn(),
  beforePopState: jest.fn(),
  events: {
    on: jest.fn(),
    off: jest.fn(),
    emit: jest.fn(),
  },
  isFallback: false,
};

export const getCountriesData = {
  data: {
    countries: {
      countries: [
        {
          countryCode: 'AT',
          countryCodeLegacy: 'A',
          countryName: 'Austria',
          dialingCode: '+43',
          flagSrc: '',
          passportRequired: true,
          nationality: 'Austrian',
        },
        {
          countryCode: 'GB',
          countryCodeLegacy: 'GB',
          countryName: 'United Kingdom (the)',
          dialingCode: '+44',
          flagSrc: '',
          passportRequired: true,
          nationality: 'British, UK',
        },
        {
          countryCode: 'DE',
          countryCodeLegacy: 'D',
          countryName: 'Germany',
          dialingCode: '+49',
          flagSrc: '',
          passportRequired: true,
          nationality: 'German',
        },
        {
          countryCode: 'RO',
          countryCodeLegacy: 'RO',
          countryName: 'Romania',
          dialingCode: '+40',
          flagSrc: '',
          passportRequired: false,
          nationality: 'Romanian',
        },
      ],
    },
  },
  isLoading: false,
  isError: false,
  isSuccess: true,
  error: {
    message: 'error country selection',
  },
};

const mockCustomLocale = jest.fn();
const mockUseFeatureToggle = jest.fn();

const mockedFindBookingResponse = {
  findBooking: {
    cookieName: 'pi.single-booking',
    minutesTillExpiry: '30',
    redirectBase: '/gb/en/account/dashboard',
    ref: 'GBH6569756',
    sourcePms: 'Opera',
    token: 'SJSk5SkJg/8S8ymiq9vwfy9ro',
    basketReference: 'GBH-c7defab8-3b90-4690-bb1b-c49dbc8b1c5a',
    bookingConfirmation: {
      bookingReference: '123ABC',
      reservationId: '12345678',
    },
  },
};

const today = new Date();
const tomorrow = new Date(today);
tomorrow.setDate(today.getDate() + 1);

const mockRoomResponse = {
  bookingConfirmation: {
    reservationByIdList: [
      {
        reservationId: '1685621',
        billing: {
          address: {
            addressLine1: 'Porz Avenue',
            postalCode: 'LU5 5XE',
          },
          title: 'Mr',
          telephone: '+4412345678',
          firstName: 'Manjunath',
          lastName: 'SS',
          email: 'manju1@gmail.com',
        },
        reservationGuestList: [
          {
            givenName: 'John',
            surName: 'Doe',
            email: 'john@gmail.com',
            nameTitle: 'Mr',
            additionalDetails: {
              dob: '2024-06-04',
              passportNumber: 'XXXXXXXXIA',
              nationality: 'DE',
            },
            address: {
              addressType: 'HOME',
              addressLine1: 'Ashdown House, Destinations Place',
              addressLine2: 'London Gatwick Airport',
              addressLine4: 'Holborn',
              countryCode: 'GB',
              postalCode: 'RH6 0NP',
            },
          },
        ],
        gdsReferenceNumber: null,
        reservationStatus: 'Reserved',
        roomStay: {
          arrivalDate: today.toDateString(),
          departureDate: tomorrow.toDateString(),
          childrenNumber: 0,
          adultsNumber: 1,
          roomType: 'DOUBLE',
          roomExtraInfo: {
            roomName: 'Double room',
          },
        },
      },
    ],
    hotelId: 'STUAIR',
    hotelName: 'Stuttgart Airport Messe',
    bookingFlowId: 'booking-ct-a1',
    rateMessage: '<p>Flex: Amend or cancel up to 1pm on arrival day</p>\n',
    bookingReference: 'GBH2260924',
    basketReference: 'GBH-6c0f1bad-d515-4225-b4d7-637086043d76',
  },
};

const mockHotelInformation = {
  hotelInformation: {
    address: {
      addressLine1: 'Europa-Allee 44',
      addressLine2: 'Frankfurt/Main',
      addressLine3: '',
      addressLine4: null,
      postalCode: '60327',
      country: 'Germany',
    },
    hotelId: 'FRAMTI',
    hotelOpeningDate: '',
    name: 'Frankfurt Messe',
    brand: 'PID',
  },
};

jest.mock('@tanstack/react-query', () => {
  const original = jest.requireActual('@tanstack/react-query');
  const queryClient = new original.QueryClient();

  queryClient.fetchQuery = jest.fn().mockImplementation(({ queryKey }) => {
    if (queryKey[0] === 'FindBooking') {
      return Promise.resolve(mockedFindBookingResponse);
    } else if (queryKey[0] === 'getBookingConfirmation') {
      return Promise.resolve(mockRoomResponse);
    } else if (queryKey[0] === 'GetHotelInformation') {
      return Promise.resolve(mockHotelInformation);
    } else {
      return original.QueryClient.prototype.fetchQuery.apply(queryClient, [{ queryKey }]);
    }
  });

  return {
    ...original,
    QueryClient: jest.fn(() => queryClient),
  };
});

function mockUseQueryRequest(queryKey) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    switch (key) {
      case 'GetCountries':
        return getCountriesData;
      case 'getCountriesWithNationality':
        return getCountriesData;
      default:
        return {};
    }
  }
}

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  useMutationRequest: () => mockMutationResponse,
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: mockUseQueryRequest,
  useFeatureSwitch: () => true,
  useFeatureToggle: () => mockUseFeatureToggle(),

  graphQLRequest: jest.fn(),
  getFindBookingToken: () => ({
    basketReference: 'GBH-c7defab8-3b90-4690-bb1b-c49dbc8b1c5a',
  }),
}));

const mockMutationResponse = {
  mutation: {
    mutate: jest.fn(),
  },
  isSuccess: false,
  isError: false,
  error: { message: '' },
  isLoading: false,
  data: {},
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: jest.fn(),
}));

jest.mock('./common', () => {
  const actual = jest.requireActual('./common');

  return {
    ...actual,
    handleBooking: jest.fn(
      ({
        rooms,
        setBookingConfirmation,
        setDisplayMultiRoomSection,
        setFormDetails,
        setDisplayPersonalDetailsSection,
      }) => {
        rooms.current = [
          {
            reservationId: '1685621',
            bookingReference: 'GBH2260924',
            hotelName: 'Stuttgart Airport Messe',
            arrivalDate: today.toDateString(),
            departureDate: tomorrow.toDateString(),
            deRegCardCompleted: false,
            preCheckInStatus: false,
            firstName: 'John',
            surname: 'Doe',
            lastName: 'Doe',
            email: 'john@gmail.com',
            country: 'GB',
          },
        ];

        setBookingConfirmation({
          bookingReference: 'GBH2260924',
          basketReference: 'GBH-6c0f1bad-d515-4225-b4d7-637086043d76',
        });

        setDisplayMultiRoomSection(false);
        setDisplayPersonalDetailsSection(true);

        setFormDetails({
          firstName: 'John',
          surname: 'Doe',
          lastName: 'Doe',
          email: 'john@gmail.com',
          bookingReference: 'GBH2260924',
          reservationId: '1685621',
          arrivalDate: today.toDateString(),
          departureDate: tomorrow.toDateString(),
          country: 'GB',
          deRegCardCompleted: false,
          preCheckInStatus: false,
        });
      }
    ),
  };
});

describe('PreCheckInPage single room scenarios', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    window.HTMLElement.prototype.scrollIntoView = jest.fn();

    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    mockUseFeatureToggle.mockReturnValue({
      [FT_MOBILE_PREREGISTERED_REPURPOSE]: false,
    });

    mockMutationResponse.isSuccess = false;
    mockMutationResponse.isError = false;
    mockMutationResponse.isLoading = false;
    mockMutationResponse.data = {};

    (useRouter as jest.Mock).mockReturnValue(mockRouter);
  });

  it('should render PreCheckInPage skeleton for single room with error', async () => {
    mockMutationResponse.isError = true;

    const { queryByText } = render(<PreCheckInPage />);

    await waitFor(() => expect(queryByText('precheckin.title')).toBeInTheDocument());
  });

  it('should render PreCheckInPage skeleton for single room while loading', async () => {
    mockMutationResponse.isLoading = true;

    const { queryByText } = render(<PreCheckInPage />);

    await waitFor(() => expect(queryByText('precheckin.title')).toBeInTheDocument());
  });

  it('should handle if basketReference is empty', async () => {
    const queryClient = new QueryClient();

    jest.spyOn(queryClient, 'fetchQuery').mockResolvedValueOnce({
      findBooking: {
        cookieName: 'pi.single-booking',
        minutesTillExpiry: '30',
        redirectBase: '/gb/en/account/dashboard',
        ref: 'GBH0117027',
        sourcePms: 'Opera',
        token:
          'aIqpsTOfDfvsIhEqH6F3ObPl3bF4CL5hVtvGg+FbBQBA30DL7rH2J6vV0hJdTniPa9GL8izvEFfmulaqJxA1KfFAxHEVmWl0V+kbT2y7jgFQnxs=',
        basketReference: 'GBH-75dc4c6e-95c8-4d88-997f-c5697bb35996',
      },
    });

    const { queryByText } = render(<PreCheckInPage />);

    await waitFor(() => expect(queryByText('precheckin.title')).toBeInTheDocument());
  });

  it('should handle if findBooking is empty', async () => {
    const queryClient = new QueryClient();

    jest.spyOn(queryClient, 'fetchQuery').mockResolvedValueOnce({
      findBooking: undefined,
    });

    const { queryByText } = render(<PreCheckInPage />);

    await waitFor(() => expect(queryByText('precheckin.title')).toBeInTheDocument());
  });

  it('should render PreCheckInPage skeleton for single room while loading', async () => {
    const { queryByText, getByTestId, getByRole } = render(<PreCheckInPage />);

    await waitFor(() => expect(queryByText('precheckin.title')).toBeInTheDocument());

    fireEvent.input(getByTestId('PreCheckInPage-Form-DateOfBirth-SingleDatePicker'), {
      target: { value: '04 Jun 1994' },
    });

    fireEvent.change(getByRole('combobox'), {
      target: { value: 'DE' },
    });

    fireEvent.keyDown(getByRole('combobox'), {
      key: 'Enter',
      code: 'Enter',
    });

    fireEvent.click(getByTestId('reg-form-submit-btn'));

    fireEvent.submit(getByTestId('PreCheckInPage-preCheckInRegistrationForm'));
  });

  it('should use deRegCardCompleted when mobile pre-registered repurpose is enabled', async () => {
    mockUseFeatureToggle.mockReturnValue({
      [FT_MOBILE_PREREGISTERED_REPURPOSE]: true,
    });

    const { queryByText } = render(<PreCheckInPage />);

    await waitFor(() => expect(queryByText('precheckin.title')).toBeInTheDocument());

    await waitFor(() => expect(queryByText('precheckin.subheading')).toBeInTheDocument());

    expect(mockUseFeatureToggle).toHaveBeenCalled();

    expect(mockUseFeatureToggle.mock.results[0].value[FT_MOBILE_PREREGISTERED_REPURPOSE]).toBe(
      true
    );
  });

  it('should use preCheckInStatus when mobile pre-registered repurpose is disabled', async () => {
    mockUseFeatureToggle.mockReturnValue({
      [FT_MOBILE_PREREGISTERED_REPURPOSE]: false,
    });

    const { queryByText } = render(<PreCheckInPage />);

    await waitFor(() => expect(queryByText('precheckin.title')).toBeInTheDocument());

    await waitFor(() => expect(queryByText('precheckin.subheading')).toBeInTheDocument());

    expect(mockUseFeatureToggle).toHaveBeenCalled();

    expect(mockUseFeatureToggle.mock.results[0].value[FT_MOBILE_PREREGISTERED_REPURPOSE]).toBe(
      false
    );
  });
});
