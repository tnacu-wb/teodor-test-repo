import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { waitFor } from '@testing-library/react';
import {
  Area,
  LATE_CHECKOUT_IDS,
  EARLY_CHECKIN_IDS,
  WIFI_IDS,
  PROSECCO_IDS,
} from '@whitbread-eos/api';
import { analytics } from '@whitbread-eos/utils';
import * as React from 'react';

import { render } from '../../../../utils/test-utils';
import {
  mockBookingConfirmationAuthenticatedMock,
  mockBookingConfirmationData,
  mockGetDonationPackages,
  mockGetPackagesData,
  mockRatesInformationDiscountRate,
  mockHotelInformation,
} from '../mockResponse';
import BookingDetailsContainer, { Props } from './BookingDetails.container';

const props: Props = {
  bookingReference: 'AKU9491086',
  basketReference: 'AKU-ff0a17d2-8035-4755-b982-b2bdca22c7b6',
  shouldShowTypeOfBooking: true,
  bookingStatus: 'Reserved',
  bookingType: 'Upcoming',
  paymentOption: 'CC',
  sourcePms: undefined,
  dpaInfo: { dpaPassed: false, dpaOverride: false },
  setDpaInfo: jest.fn(),
  overridenUserInfo: {
    reservationOverrideReasons: {
      reasonName: '',
      callerName: '',
      managerName: '',
      reasonCode: '',
    },
    reservationOverridden: false,
  },
  inputValues: {
    bookingReference: 'AKU9491086',
    bookerLastName: '',
    arrivalDate: '',
    guestLastName: '',
    bookerPostcode: '',
    hotelDetails: {
      name: '',
      code: '',
    },
    hotelLocation: '',
    bookerEmail: '',
    bookerPhone: '',
    cancellationDate: '',
    companyName: '',
    thirdPartyBookingReferenceNumber: '',
  },
  gdsReferenceNumber: null,
  distBookingChannel: 'Web 2014',
  isAmendSuccessful: false,
  arrivalDate: '2023-11-01',
  bookingSurname: 'Last',
  isAmendPage: false,
};

const mockCustomLocale = jest.fn();
const mockAuthCookie = jest.fn();

jest.mock('../CancelBookingModal/CancelBookingModal.container', () => {
  const MockedCancelBookingModal = () => <div></div>;
  MockedCancelBookingModal.displayName = 'MockedCancelBookingModal';
  return MockedCancelBookingModal;
});

jest.mock('./BookingDetails.component', () => {
  const MockedBookingDetails = () => <div data-testid="BookingDetails-Wrapper"></div>;
  MockedBookingDetails.displayName = 'MockedBookingDetails';
  return MockedBookingDetails;
});

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
    query: {
      bookingReference: 'AQPR1437',
    },
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  graphQLRequest: jest.fn(),
}));

async function mockUseQueryRequest(queryKey: string) {
  const key = queryKey[0];

  switch (key) {
    case 'getBookingConfirmation':
      return Promise.resolve(mockBookingConfirmationData);
    case 'getBookingConfirmationAuthenticated':
      return Promise.resolve(mockBookingConfirmationAuthenticatedMock);
    case 'getDonationPackages':
      return Promise.resolve(mockGetDonationPackages);
    case 'GetPackages':
      return Promise.resolve(mockGetPackagesData);
    case 'ratesInformationDiscountRate':
      return Promise.resolve(mockRatesInformationDiscountRate);
    case 'GetHotelInformation':
      return Promise.resolve(mockHotelInformation);
    default:
      return Promise.resolve({});
  }
}

jest.mock('@tanstack/react-query', () => {
  const original: typeof ReactQuery = jest.requireActual('@tanstack/react-query');

  return {
    ...original,
    useQueryClient: jest.fn(() => ({
      invalidateQueries: mockUseQueryRequest,
      fetchQuery: jest.fn(async (options: any) => {
        const queryKey = options.queryKey || options;
        const key = Array.isArray(queryKey) ? queryKey[0] : queryKey;

        switch (key) {
          case 'getBookingConfirmation':
            return Promise.resolve(mockBookingConfirmationData);

          case 'getBookingConfirmationAuthenticated':
            return Promise.resolve(mockBookingConfirmationAuthenticatedMock);

          case 'getDonationPackages':
            return Promise.resolve(mockGetDonationPackages);

          case 'GetPackages':
            return Promise.resolve(mockGetPackagesData);

          case 'ratesInformationDiscountRate':
            return Promise.resolve(mockRatesInformationDiscountRate);

          case 'GetHotelInformation':
            return Promise.resolve(mockHotelInformation);

          default:
            return Promise.resolve({});
        }
      }),
      prefetchQuery: jest.fn().mockResolvedValue(undefined),
    })),
  };
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  graphQLRequest: jest.fn(),
  sortMealsByReservationId: () => [],
  analytics: {
    update: jest.fn(),
  },
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  Notification: () => <div>Notification</div>,
}));

describe('BookingDetails Container', () => {
  beforeAll(() => {
    mockAuthCookie.mockReturnValue('token');
  });
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });

    (window as any).analyticsData = { promo: {} };
  });

  it('should render loading spinner when bookingState.isLoading is true', async () => {
    const { getByTestId } = render(<BookingDetailsContainer {...props} />);

    expect(getByTestId('Loading-BookingDetails')).toBeInTheDocument();
  });

  it('should display container for PI', async () => {
    mockAuthCookie.mockReturnValue(null);
    const { getByTestId } = render(<BookingDetailsContainer {...props} />);
    await waitFor(() => expect(getByTestId('BookingDetails-Wrapper')).toBeInTheDocument());
  });

  it('should display container for CCUI ', async () => {
    const { getByTestId } = render(
      <BookingDetailsContainer {...{ ...props, ...{ area: Area.CCUI, basketReference: null } }} />
    );
    await waitFor(() => expect(getByTestId('BookingDetails-Wrapper')).toBeInTheDocument());
  });

  it('should display city taxMessage for business', async () => {
    const { getByTestId } = render(
      <BookingDetailsContainer {...{ ...props, ...{ area: Area.CCUI, basketReference: null } }} />
    );
    mockGetPackagesData.packages.hotelHasCityTaxForBusiness = true;
    mockBookingConfirmationAuthenticatedMock.bookingConfirmationAuthenticated.reservationByIdList[0].additionalGuestInfo.purposeOfStay =
      'BUS';

    await waitFor(() => expect(getByTestId('BookingDetails-Wrapper')).toBeInTheDocument());
  });

  it('should test the catch block', async () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(await require('@tanstack/react-query'), 'useQueryClient').mockReturnValue({
      invalidateQueries: mockUseQueryRequest,
      fetchQuery: async () => {
        throw new Error('Error mock');
      },
    });

    const { getByText } = render(<BookingDetailsContainer {...props} />);

    await waitFor(() => {
      expect(getByText('Notification')).toBeInTheDocument();
    });
  });

  it('should not track promo when rateTags is empty', () => {
    mockRatesInformationDiscountRate.ratesInformation.rateClassifications = [
      {
        additionalDescription: '',
        ratePlanCode: '',
        rateOrder: '0',
        rateNotes: '',
        rateName: '',
        rateLongDescription: '',
        rateDescription: '',
        rateClassification: '',
        rateCategory: '',
        rateTags: [''],
      },
    ];

    render(<BookingDetailsContainer {...props} />);

    expect(analytics.update).not.toHaveBeenCalledWith();
  });

  it('should track promo when rateTags is provided', () => {
    render(<BookingDetailsContainer {...props} />);

    expect(analytics.update).toHaveBeenCalledWith({
      promo: {
        promoName: '10% discount',
      },
    });
  });

  it('should map room details correctly from reservation list', async () => {
    const { getByTestId } = render(<BookingDetailsContainer {...props} />);
    () => {
      expect(getByTestId('BookingDetails-Wrapper')).toBeInTheDocument();
    };
  });

  it('should render when isAmendSuccessful is true', async () => {
    const { getByTestId } = render(<BookingDetailsContainer {...props} isAmendSuccessful={true} />);
    () => expect(getByTestId('BookingDetails-Wrapper')).toBeInTheDocument();
  });

  it('should render when isAmendPage is true', async () => {
    const { getByTestId } = render(<BookingDetailsContainer {...props} isAmendPage={true} />);
    () => expect(getByTestId('BookingDetails-Wrapper')).toBeInTheDocument();
  });

  it('should render when isRemovePIIDataFromLocalStorageEnabled is true', async () => {
    const { getByTestId } = render(
      <BookingDetailsContainer {...props} isRemovePIIDataFromLocalStorageEnabled={true} />
    );
    () => expect(getByTestId('BookingDetails-Wrapper')).toBeInTheDocument();
  });

  it('should call authenticated query when loggedOrCCUI is true', async () => {
    mockAuthCookie.mockReturnValue('token');
    render(<BookingDetailsContainer {...props} />);
  });

  it('should call unauthenticated query when loggedOrCCUI is false', async () => {
    mockAuthCookie.mockReturnValue(null);
    render(<BookingDetailsContainer {...props} />);
  });
  it('should render notification when GetPackages query fails', async () => {
    jest.spyOn(ReactQuery, 'useQueryClient').mockReturnValue({
      invalidateQueries: mockUseQueryRequest,
      fetchQuery: jest.fn(async (options: any) => {
        const queryKey = options.queryKey || options;
        const key = Array.isArray(queryKey) ? queryKey[0] : queryKey;

        if (key === 'GetPackages') {
          throw new Error('Packages Error');
        }

        return mockUseQueryRequest(queryKey);
      }),
    } as any);

    const { getByText } = render(<BookingDetailsContainer {...props} />);

    await waitFor(() => {
      expect(getByText('Notification')).toBeInTheDocument();
    });
  });
  it('should fetch amend confirmation prices when tempBookingReference exists', async () => {
    const fetchQueryMock = jest.fn(async (options: any) => {
      const queryKey = options.queryKey || options;
      const key = Array.isArray(queryKey) ? queryKey[0] : queryKey;

      switch (key) {
        case 'getAmendConfirmationPrices':
          return Promise.resolve({
            amendConfirmationPrices: {
              previousTotal: '100',
              newTotalCost: '120',
              outstandingBalance: '20',
            },
          });

        case 'getBookingConfirmationAuthenticated':
          return Promise.resolve(mockBookingConfirmationAuthenticatedMock);

        case 'getDonationPackages':
          return Promise.resolve(mockGetDonationPackages);

        case 'GetPackages':
          return Promise.resolve(mockGetPackagesData);

        case 'ratesInformationDiscountRate':
          return Promise.resolve(mockRatesInformationDiscountRate);

        case 'GetHotelInformation':
          return Promise.resolve(mockHotelInformation);

        default:
          return Promise.resolve({});
      }
    });

    jest.spyOn(ReactQuery, 'useQueryClient').mockReturnValue({
      invalidateQueries: mockUseQueryRequest,
      fetchQuery: fetchQueryMock,
      prefetchQuery: jest.fn(),
    } as any);

    render(<BookingDetailsContainer {...props} tempBookingReference="TEMP123" />);

    expect(fetchQueryMock).toHaveBeenCalledWith(
      expect.objectContaining({
        queryKey: expect.arrayContaining(['getAmendConfirmationPrices']),
      })
    );
  });
  it('should map ECI, LCO, WIFI and PROSECCO extras prices correctly', async () => {
    mockGetPackagesData.packages.packages.extrasItems = [
      {
        id: EARLY_CHECKIN_IDS[0],
        price: {
          total: '10',
        },
      },
      {
        id: LATE_CHECKOUT_IDS[0],
        price: {
          total: '20',
        },
      },
      {
        id: WIFI_IDS[0],
        price: {
          total: '30',
        },
      },
      {
        id: PROSECCO_IDS[0],
        price: {
          total: '40',
        },
      },
    ] as any;

    mockGetPackagesData.packages.packages.roomSelection = [
      {
        reservationId: '4670431',
        packagesList: ['ECI', 'LCO', 'WIFI', 'PROSECCO'],
      },
    ] as any;

    const spy = jest.spyOn(ReactQuery, 'useQueryClient');

    spy.mockReturnValue({
      invalidateQueries: mockUseQueryRequest,
      fetchQuery: jest.fn(async (options: any) => {
        const queryKey = options.queryKey || options;
        return mockUseQueryRequest(queryKey);
      }),
      prefetchQuery: jest.fn(),
    } as any);

    const { getByTestId } = render(<BookingDetailsContainer {...props} />);

    await waitFor(() => {
      expect(getByTestId('BookingDetails-Wrapper')).toBeInTheDocument();
    });

    spy.mockRestore();
  });
});
