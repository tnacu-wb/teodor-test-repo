import '@testing-library/jest-dom';
import { screen, act } from '@testing-library/react';
import {
  Area,
  RoomTypeLabelCode,
  SOURCE_SYSTEM,
  BookingChannelCriteria,
  DISCOUNT_RATE_INFORMATION_QUERY,
  Channel,
} from '@whitbread-eos/api';
import { analytics, graphQLRequest } from '@whitbread-eos/utils';

import { render, waitFor } from '../../../utils/test-utils';
import BookingHistoryInfoCardContainer, {
  getDiscountRateQueryFn,
  getAnalyticsChannelId,
  getRateTags,
  shouldFetchDiscountRate,
} from './BookingHistoryInfoCard.container';

const mockMutationRequest = {
  mutation: {
    mutate: jest.fn(),
  },
  isSuccess: false,
};

const mockRoomTypeInformationQueryRequest = {
  isLoading: false,
  isError: false,
  error: null,
  data: {
    roomTypeInformation: {
      roomTypes: [
        {
          roomTypeCode: ['DB'],
          roomLabel: 'Double Room',
        },
        {
          roomTypeCode: ['FAM'],
          roomLabel: 'Family Room',
        },
      ],
    },
  },
};

const mockAuthToken = {
  token: 'mock-token',
  isAuth0Enabled: false,
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    query: '',
  }),
}));

// Track props passed to the child component
let bookingHistoryInfoCardProps: any = null;

jest.mock('./BookingHistoryInfoCard.component', () => {
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  const React = require('react');
  const actual = jest.requireActual('./BookingHistoryInfoCard.component');
  const ActualComponent = actual.default;
  return {
    __esModule: true,
    default: (props: any) => {
      bookingHistoryInfoCardProps = props;
      // Render the actual component
      return React.createElement(ActualComponent, props);
    },
  };
});

jest.mock('@whitbread-eos/utils', () => {
  const actualUtils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...actualUtils,
    analytics: {
      update: jest.fn(),
      track: jest.fn(),
    },
    useFeatureToggle: () => ({
      FT_PI_PROMO_CODE_LANDING_PAGE: true,
      FT_PI_PIB_BIC_DOWNLOAD_INVOICE: true,
    }),
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useQueryRequest: (queryKey: any[]) => {
      if (queryKey[0] === 'GetHotelInformation') {
        return mockHotelInfoQueryRequest;
      }
      if (queryKey[0] === 'getRoomTypeInformation') {
        return mockRoomTypeInformationQueryRequest;
      }
      if (queryKey[0] === 'ratesInformationDiscountRate') {
        return mockDiscountRateQueryRequest;
      }
      // Default to booking details
      return mockUseQueryRequest;
    },
    useMutationRequest: () => mockMutationRequest,
    useAuthToken: () => mockAuthToken,
    useAuth0User: () => ({
      user: null,
    }),
    graphQLRequest: jest.fn().mockResolvedValue({
      hotelInformation: {
        brand: 'premierinn',
      },
    }),
    downloadBookingInvoice: jest.fn(),
    downloadFromS3PreSignedUrl: jest.fn(),
  };
});

// Import the mocked functions after the mock is defined
// eslint-disable-next-line @typescript-eslint/no-require-imports
const { downloadBookingInvoice, downloadFromS3PreSignedUrl } = require('@whitbread-eos/utils');

const mockHotelInfoQueryRequest = {
  isLoading: false,
  isError: false,
  error: null,
  data: {
    hotelInformation: {
      brand: 'premierinn',
    },
  },
};

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  error: { message: 'Error!' },
  data: {
    bookingInfoCardDetails: {
      reservationDetails: {
        basketReference: 'UUID',
        bookingReference: 'BCQR231999',
        hotelCode: 'LONMON',
        arrivalDate: '2023-03-17',
        bookingStatus: 'UNARRIVED',
        departureDate: '2023-03-19',
        nights: 2,
        noOfRooms: 2,
        rateType: 'Flex',
        hotelHasCityTaxForLeisure: false,
        paymentOption: 'PAY_NOW',
        sourceSystem: 'BART',
        rateDescription: 'Pay on arrival.  Amend or cancel up to 1pm on arrival day. ',
        cancellationInfoResponse: {
          amendable: true,
          cancelable: true,
        },
        donationsPackage: {
          description: 'On-line Charitable Pledge',
          noSelections: 1,
          packageCode: '145',
          totalPrice: {
            amount: 5,
            currency: 'GBP',
          },
        },
        newTotal: {
          amount: 843,
          currency: 'GBP',
        },
        outstandingAmount: {
          amount: 0,
          currency: 'GBP',
        },
        prepaidAmount: {
          amount: 843,
          currency: 'GBP',
        },
        previousTotal: {
          amount: 843,
          currency: 'GBP',
        },
        refund: {
          amount: 0,
          currency: 'GBP',
        },
        rooms: [
          {
            adults: 2,
            adultsMeal: [
              {
                description: 'Premier Inn Breakfast',
                noSelections: 2,
                packageCode: '11',
                totalPrice: {
                  amount: 21,
                  currency: 'GBP',
                },
              },
            ],
            children: 0,
            cot: false,
            guest: {
              firstName: 'Firstt',
              lastName: 'Lastt',
              title: 'Mr',
            },
            kidsMeal: [],
            roomCost: {
              amount: 85,
              currency: 'GBP',
            },
            roomId: 'DB,1',
            roomType: 'DB',
          },
          {
            adults: 2,
            adultsMeal: [
              {
                description: 'Premier Inn Breakfast',
                noSelections: 1,
                packageCode: '11',
                totalPrice: {
                  amount: 10.5,
                  currency: 'GBP',
                },
              },
              {
                description: 'Continental Breakfast',
                noSelections: 1,
                packageCode: '12',
                totalPrice: {
                  amount: 8.5,
                  currency: 'GBP',
                },
              },
            ],
            children: 1,
            cot: false,
            guest: {
              firstName: 'Rischitor',
              lastName: 'Ovidiu',
              title: 'Mr',
            },
            kidsMeal: [
              {
                description: 'Free Child Breakfast',
                noSelections: 1,
                packageCode: '15',
                totalPrice: {
                  amount: 0,
                  currency: 'GBP',
                },
              },
            ],
            roomCost: {
              amount: 85,
              currency: 'GBP',
            },
            roomId: 'TB,1',
            roomType: 'FAM',
          },
        ],
        totalCost: {
          amount: 843,
          currency: 'GBP',
        },
      },
      checkInTime: '15:00:00',
      checkOutTime: '12:00:00',
    },
    ratesInformation: {
      rateClassifications: [
        {
          rateTags: [] as string[],
        },
      ],
    },
  },
  refetch: jest.fn(),
};

// Discount rate query request shares the same data reference
const mockDiscountRateQueryRequest = {
  isLoading: false,
  isError: false,
  error: null,
  data: mockUseQueryRequest.data,
};

const mockProps = {
  bookedBy: '',
  sourceSystem: SOURCE_SYSTEM.BART,
  area: Area.PI,
  basketReference: 'UUID',
  bookingReference: 'BJNR10440',
  bookingStatus: 'FUTURE',
  hotelId: 'OXFNOR',
  arrival: '2023-10-25',
  guestSurname: 'Tudor',
  onCancel: jest.fn(),
  invoiceRecordNumber: '15',
  invoiceItems: {
    sentInvoiceItems: ['BJNR10440'],
    setInvoiceItems: jest.fn(),
  },
  hotelName: 'Oxford Flowers Hotel',
  bookingChannel: {
    channel: Channel.Pi,
    subchannel: 'WEB',
    language: 'EN',
  } as BookingChannelCriteria,
  roomTypeLabels: [
    {
      roomTypeCode: ['PB'],
      roomLabel: 'Standard Extra room',
    },
    {
      roomTypeCode: ['DIS'],
      roomLabel: 'Accessible Room',
    },
    {
      roomTypeCode: ['RB'],
      roomLabel: 'Premier Plus Room',
    },
    {
      roomTypeCode: ['FAM'],
      roomLabel: 'Family Room',
    },
    {
      roomTypeCode: ['RB'],
      roomLabel: 'Premier Plus Room',
    },
    {
      roomTypeCode: ['SB'],
      roomLabel: 'Standard Room',
    },
    {
      roomTypeCode: ['TWIN'],
      roomLabel: 'Twin Room',
    },
    {
      roomTypeCode: ['LOWTWN'],
      roomLabel: 'Accessible twin bedroom with a lowered bath',
    },
    {
      roomTypeCode: ['WETTWN'],
      roomLabel: 'Accessible twin bedroom with level access shower room',
    },
    {
      roomTypeCode: ['WETDBL'],
      roomLabel: 'Accessible double bedroom with level access shower room',
    },
    {
      roomTypeCode: ['LOWDBL'],
      roomLabel: 'Accessible double bedroom with a lowered bath',
    },
    {
      roomTypeCode: ['FMQUAD'],
      roomLabel: 'Family Room',
    },
    {
      roomTypeCode: ['TWINRM'],
      roomLabel: 'Twin Room',
    },
    {
      roomTypeCode: ['SINGLE'],
      roomLabel: 'Standard Room',
    },
    {
      roomTypeCode: ['PPLDBL'],
      roomLabel: 'Premier Plus Room',
    },
    {
      roomTypeCode: ['EXTDBL'],
      roomLabel: 'Standard Extra room',
    },
    {
      roomTypeCode: ['FMTRPL'],
      roomLabel: 'Family Room',
    },
    {
      roomTypeCode: ['DOUBLE'],
      roomLabel: 'Double Room',
    },
    {
      roomTypeCode: ['ZPLDBL'],
      roomLabel: 'Double Room',
    },
  ] as RoomTypeLabelCode[],
};

describe('BookingHistoryInfoCard test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    bookingHistoryInfoCardProps = null;
    mockUseQueryRequest.isLoading = false;
    mockUseQueryRequest.isError = false;
    mockUseQueryRequest.data.bookingInfoCardDetails.reservationDetails.sourceSystem =
      SOURCE_SYSTEM.BART;
    mockRoomTypeInformationQueryRequest.isLoading = false;
    mockRoomTypeInformationQueryRequest.isError = false;
    mockRoomTypeInformationQueryRequest.error = null;
    mockRoomTypeInformationQueryRequest.data = {
      roomTypeInformation: {
        roomTypes: [
          {
            roomTypeCode: ['DB'],
            roomLabel: 'Double Room',
          },
          {
            roomTypeCode: ['FAM'],
            roomLabel: 'Family Room',
          },
        ],
      },
    };
  });

  it('should display loading status', () => {
    mockUseQueryRequest.isLoading = true;
    const { getByTestId } = render(<BookingHistoryInfoCardContainer {...mockProps} />);
    expect(getByTestId('Loading-BookingHistoryInfoCard')).toBeInTheDocument();
  });

  it('should display error message', () => {
    mockUseQueryRequest.isLoading = false;
    mockUseQueryRequest.isError = true;
    const { getByText } = render(<BookingHistoryInfoCardContainer {...mockProps} />);
    expect(getByText('Error!')).toBeInTheDocument();
  });

  it('should render BookingHistoryInfoCardContainer', () => {
    mockUseQueryRequest.isLoading = false;
    mockUseQueryRequest.isError = false;
    mockUseQueryRequest.data.bookingInfoCardDetails.reservationDetails.sourceSystem =
      undefined as any;
    const { getByTestId } = render(
      <BookingHistoryInfoCardContainer {...{ ...mockProps, sourceSystem: undefined }} />
    );
    expect(getByTestId('BookingInfoCardContainer')).toBeInTheDocument();
  });

  it('should display loading status while room type query is loading', () => {
    mockUseQueryRequest.isLoading = false;
    mockUseQueryRequest.isError = false;
    mockRoomTypeInformationQueryRequest.isLoading = true;

    const { getByTestId } = render(<BookingHistoryInfoCardContainer {...mockProps} />);

    expect(getByTestId('Loading-BookingHistoryInfoCard')).toBeInTheDocument();
  });

  it('should map room type labels from room type query response', () => {
    mockUseQueryRequest.data.bookingInfoCardDetails.reservationDetails.sourceSystem =
      SOURCE_SYSTEM.OPERA;
    mockRoomTypeInformationQueryRequest.isLoading = false;
    mockRoomTypeInformationQueryRequest.isError = false;
    mockRoomTypeInformationQueryRequest.data = {
      roomTypeInformation: {
        roomTypes: [
          {
            roomTypeCode: ['DB'],
            roomLabel: 'Double Room',
          },
          {
            roomTypeCode: ['FAM'],
            roomLabel: 'Family Room',
          },
        ],
      },
    };

    render(<BookingHistoryInfoCardContainer {...mockProps} />);

    expect(bookingHistoryInfoCardProps).not.toBeNull();
    expect(bookingHistoryInfoCardProps.bookingDetails.roomDetails[0].roomType).toBe('Double Room');
    expect(bookingHistoryInfoCardProps.bookingDetails.roomDetails[1].roomType).toBe('Family Room');
  });

  it('should fallback gracefully when room type query errors', () => {
    mockUseQueryRequest.data.bookingInfoCardDetails.reservationDetails.sourceSystem =
      SOURCE_SYSTEM.OPERA;
    mockRoomTypeInformationQueryRequest.isError = true;
    mockRoomTypeInformationQueryRequest.error = new Error('Room type error');
    mockRoomTypeInformationQueryRequest.data = undefined;

    const { getByTestId } = render(<BookingHistoryInfoCardContainer {...mockProps} />);

    expect(getByTestId('BookingInfoCardContainer')).toBeInTheDocument();
    expect(bookingHistoryInfoCardProps).not.toBeNull();
    expect(bookingHistoryInfoCardProps.bookingDetails.roomDetails[0].roomType).toBeUndefined();
    expect(bookingHistoryInfoCardProps.bookingDetails.roomDetails[1].roomType).toBeUndefined();
  });

  it('should fallback gracefully when room type labels are empty', () => {
    mockUseQueryRequest.data.bookingInfoCardDetails.reservationDetails.sourceSystem =
      SOURCE_SYSTEM.OPERA;
    mockRoomTypeInformationQueryRequest.isLoading = false;
    mockRoomTypeInformationQueryRequest.isError = false;
    mockRoomTypeInformationQueryRequest.data = {
      roomTypeInformation: {
        roomTypes: [],
      },
    };

    const { getByTestId } = render(<BookingHistoryInfoCardContainer {...mockProps} />);

    expect(getByTestId('BookingInfoCardContainer')).toBeInTheDocument();
    expect(bookingHistoryInfoCardProps).not.toBeNull();
    expect(bookingHistoryInfoCardProps.bookingDetails.roomDetails[0].roomType).toBeUndefined();
    expect(bookingHistoryInfoCardProps.bookingDetails.roomDetails[1].roomType).toBeUndefined();
  });
  it('should display the success notification when triggering the send invoice button is successfull', async () => {
    mockProps.bookingStatus = 'COMPLETED';
    mockProps.invoiceItems.sentInvoiceItems = [];
    mockMutationRequest.isSuccess = true;
    const { getByTestId } = render(<BookingHistoryInfoCardContainer {...mockProps} />);
    expect(getByTestId('BookingHistoryDetails-Notification-Invoice')).toBeInTheDocument();
  });
  it('should not display the success notification when triggering the send invoice button is not successfull', async () => {
    mockProps.bookingStatus = 'COMPLETED';
    mockProps.invoiceItems.sentInvoiceItems = [];
    mockMutationRequest.isSuccess = false;
    const { queryByTestId } = render(<BookingHistoryInfoCardContainer {...mockProps} />);
    expect(queryByTestId('BookingHistoryDetails-Notification-Invoice')).not.toBeInTheDocument();
  });

  it('does not call analytics.update if no rateTags', async () => {
    const props = { ...mockProps };
    render(<BookingHistoryInfoCardContainer {...props} />);

    await waitFor(() => {
      expect(analytics.update).not.toHaveBeenCalled();
    });
  });

  it('Update analytics when we have rate tags', async () => {
    mockUseQueryRequest.data.ratesInformation.rateClassifications[0].rateTags = ['10% discount'];
    render(<BookingHistoryInfoCardContainer {...mockProps} />);

    await waitFor(() => {
      expect(analytics.update).toHaveBeenCalled();
    });
  });

  it('does not call refetch or onCancel when isReadOnly is true', () => {
    const refetchMock = jest.fn();
    mockUseQueryRequest.refetch = refetchMock;

    render(<BookingHistoryInfoCardContainer {...{ ...mockProps, isReadOnly: true }} />);

    expect(refetchMock).not.toHaveBeenCalled();
    expect(mockProps.onCancel).not.toHaveBeenCalled();
  });

  it('renders ResendConfirmationModal when reservationDetails.bookingReference exists', () => {
    render(<BookingHistoryInfoCardContainer {...mockProps} />);
    expect(screen.getByTestId('BookingInfoCardContainer')).toBeInTheDocument();
  });

  it('passes bookedBy through bookingDetails to the child component', () => {
    const props = {
      ...mockProps,
      bookedBy: 'Test Booker',
    };

    render(<BookingHistoryInfoCardContainer {...props} />);

    expect(bookingHistoryInfoCardProps).not.toBeNull();
    expect(bookingHistoryInfoCardProps.bookingDetails.bookedBy).toBe('Test Booker');
  });

  it('updates invoiceItems when isSuccess is true', () => {
    const setInvoiceItemsMock = jest.fn();
    const props = {
      ...mockProps,
      invoiceItems: {
        sentInvoiceItems: [],
        setInvoiceItems: setInvoiceItemsMock,
      },
    };
    mockMutationRequest.isSuccess = true;

    render(<BookingHistoryInfoCardContainer {...props} />);

    expect(setInvoiceItemsMock).toHaveBeenCalledWith(
      expect.arrayContaining([props.bookingReference])
    );
  });

  it('should show loading state when hotelInfo is loading', () => {
    mockUseQueryRequest.isLoading = true;
    mockUseQueryRequest.isError = false;
    (mockUseQueryRequest as any).isLoadingHotelInfo = true;

    const { getByTestId } = render(<BookingHistoryInfoCardContainer {...mockProps} />);
    expect(getByTestId('Loading-BookingHistoryInfoCard')).toBeInTheDocument();
  });

  it('should show error when discount query fails', () => {
    mockUseQueryRequest.isLoading = false;
    mockUseQueryRequest.isError = true;
    (mockUseQueryRequest as any).isErrorDiscount = true;

    const { getByText } = render(<BookingHistoryInfoCardContainer {...mockProps} />);
    expect(getByText('Error!')).toBeInTheDocument();
  });

  it('queryFn resolves undefined when shouldFetchDiscountRate is false', async () => {
    const queryFn = getDiscountRateQueryFn(false, 'HEAPTI', 'pi', 'en', 'gb', 'pi', 'STDDIS10');
    await expect(queryFn()).resolves.toBeUndefined();
  });

  it('calls graphQLRequest when shouldFetchDiscountRate is true', async () => {
    const queryFn = getDiscountRateQueryFn(true, 'HEAPTI', 'pi', 'en', 'gb', 'PI', 'STDDIS10');

    await queryFn();

    expect(graphQLRequest).toHaveBeenCalledWith(DISCOUNT_RATE_INFORMATION_QUERY, {
      hotelId: 'HEAPTI',
      brand: 'pi',
      language: 'en',
      country: 'gb',
      channel: 'PI',
      ratePlans: 'STDDIS10',
    });
  });

  it('returns true if brand is truthy, promo enabled, and area is pi', () => {
    const brand = 'pi';
    const isPromoCodeLandingPageEnabled = true;
    const area = 'pi';
    expect(!!brand && isPromoCodeLandingPageEnabled && area?.toLowerCase() === 'pi').toBe(true);
  });

  it('returns false if brand is falsy', () => {
    const brand = '';
    const isPromoCodeLandingPageEnabled = true;
    const area = 'pi';
    expect(!!brand && isPromoCodeLandingPageEnabled && area?.toLowerCase() === 'pi').toBe(false);
  });

  it('returns false if promo code is disabled', () => {
    const brand = 'pi';
    const isPromoCodeLandingPageEnabled = false;
    const area = 'pi';
    expect(!!brand && isPromoCodeLandingPageEnabled && area?.toLowerCase() === 'pi').toBe(false);
  });

  it('returns false if area is not pi', () => {
    const brand = 'pi';
    const isPromoCodeLandingPageEnabled = true;
    const area = 'pid';
    expect(!!brand && isPromoCodeLandingPageEnabled && area?.toLowerCase() === 'pi').toBe(false);
  });

  it('returns false if area is undefined', () => {
    const brand = 'pi';
    const isPromoCodeLandingPageEnabled = true;
    const area = undefined;
    expect(!!brand && isPromoCodeLandingPageEnabled && area === 'pi').toBe(false);
  });

  it('should get rateTags from dataDiscount', () => {
    mockUseQueryRequest.data.ratesInformation.rateClassifications[0].rateTags = ['10% discount'];

    const rateTags =
      mockUseQueryRequest?.data?.ratesInformation?.rateClassifications[0]?.rateTags ?? [];
    expect(rateTags).toEqual(['10% discount']);
  });

  it('should get rateTags from dataDiscount', () => {
    mockUseQueryRequest.data.ratesInformation.rateClassifications[0].rateTags = [];

    const rateTags =
      mockUseQueryRequest?.data?.ratesInformation?.rateClassifications[0]?.rateTags ?? [];
    expect(rateTags).not.toEqual(['10% discount']);
  });

  it('returns true if brand exists, promo enabled, and area is "pi"', () => {
    expect(shouldFetchDiscountRate('pi', true, 'pi')).toBe(true);
  });

  it('returns true if area is lowercase "pi"', () => {
    expect(shouldFetchDiscountRate('pi', true, 'PI')).toBe(true);
  });

  it('returns false if brand is missing', () => {
    expect(shouldFetchDiscountRate('', true, 'PI')).toBe(false);
    expect(shouldFetchDiscountRate(undefined, true, 'PI')).toBe(false);
  });

  it('returns false if promo code is disabled', () => {
    expect(shouldFetchDiscountRate('pi', false, 'PI')).toBe(false);
  });

  it('returns false if area is not "pi"', () => {
    expect(shouldFetchDiscountRate('pi', true, 'pid')).toBe(false);
    expect(shouldFetchDiscountRate('pi', true, 'PiN')).toBe(false);
  });

  it('returns false if area is undefined', () => {
    expect(shouldFetchDiscountRate('pi', true, undefined)).toBe(false);
  });

  it('maps BB channel to PIB for analytics tracking', () => {
    expect(getAnalyticsChannelId(Channel.Bb)).toBe('PIB');
  });

  it('returns other channel values unchanged for analytics tracking', () => {
    expect(getAnalyticsChannelId(Channel.Pi)).toBe(Channel.Pi);
    expect(getAnalyticsChannelId(Channel.Ccui)).toBe(Channel.Ccui);
    expect(getAnalyticsChannelId(undefined)).toBeUndefined();
  });

  it('returns rateTags array if present', () => {
    const dataDiscount = {
      ratesInformation: {
        rateClassifications: [{ rateTags: ['10% discount'] }],
      },
    };
    expect(getRateTags(dataDiscount)).toEqual(['10% discount']);
  });

  it('returns empty array if dataDiscount is undefined', () => {
    expect(getRateTags(undefined)).toEqual([]);
  });

  it('returns empty array if ratesInformation is missing', () => {
    const dataDiscount = {};
    expect(getRateTags(dataDiscount)).toEqual([]);
  });

  it('returns empty array if rateClassifications is empty', () => {
    const dataDiscount = { ratesInformation: { rateClassifications: [] } };
    expect(getRateTags(dataDiscount)).toEqual([]);
  });

  it('returns empty array if rateTags is undefined', () => {
    const dataDiscount = { ratesInformation: { rateClassifications: [{}] } };
    expect(getRateTags(dataDiscount)).toEqual([]);
  });

  describe('handleDownloadInvoice', () => {
    beforeEach(() => {
      jest.clearAllMocks();
      bookingHistoryInfoCardProps = null;
      mockUseQueryRequest.isLoading = false;
      mockUseQueryRequest.isError = false;
      mockProps.bookingStatus = 'PAST';
      mockUseQueryRequest.data.bookingInfoCardDetails.reservationDetails.bookingStatus = 'PAST';
    });

    it('should successfully download invoice when response contains invoices', async () => {
      const mockInvoiceUrl = 'https://s3.amazonaws.com/invoice.pdf';
      const mockFileName = 'Invoice_BCQR231999_Smith.pdf';
      downloadBookingInvoice.mockResolvedValue({
        data: {
          invoices: [
            {
              url: mockInvoiceUrl,
              fileName: mockFileName,
            },
          ],
        },
        errors: undefined,
      });
      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;
      expect(handleDownloadInvoice).toBeDefined();

      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadBookingInvoice).toHaveBeenCalledWith(
          {
            bookingRef: ['BJNR10440'],
            lang: 'EN',
            channel: 'PI',
            hotelBrand: 'premierinn',
            subChannel: 'WEB',
          },
          'mock-token'
        );
        expect(analytics.track).toHaveBeenCalledWith('invoice_download', {
          bookingReference: 'BJNR10440',
          hotelCode: 'OXFNOR',
          channelID: 'PI',
        });
        expect(downloadFromS3PreSignedUrl).toHaveBeenCalledWith(mockInvoiceUrl, mockFileName);
        expect(bookingHistoryInfoCardProps.downloadInvoiceError).toBeUndefined();
      });
    });

    it('should handle error when no invoices are returned', async () => {
      downloadBookingInvoice.mockResolvedValue({
        data: {
          invoices: [],
        },
        errors: undefined,
      });
      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadBookingInvoice).toHaveBeenCalled();
        expect(analytics.track).toHaveBeenCalledWith('error_event', {
          error_message: expect.any(String),
          journey_name: 'Download Invoice',
        });
        expect(downloadFromS3PreSignedUrl).not.toHaveBeenCalled();
        expect(bookingHistoryInfoCardProps.downloadInvoiceError).toBe(
          'dashboard.bookings.downloadInvoiceError'
        );
      });
    });

    it('should handle error when downloadBookingInvoice throws exception', async () => {
      downloadBookingInvoice.mockRejectedValue(new Error('Network error'));
      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadBookingInvoice).toHaveBeenCalled();
        expect(analytics.track).toHaveBeenCalledWith('error_event', {
          error_message: expect.any(String),
          journey_name: 'Download Invoice',
        });
        expect(downloadFromS3PreSignedUrl).not.toHaveBeenCalled();
        expect(bookingHistoryInfoCardProps.downloadInvoiceError).toBe(
          'dashboard.bookings.downloadInvoiceError'
        );
      });
    });

    it('should handle error when response is null', async () => {
      downloadBookingInvoice.mockResolvedValue(null);
      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadBookingInvoice).toHaveBeenCalled();
        expect(analytics.track).toHaveBeenCalledWith('error_event', {
          error_message: expect.any(String),
          journey_name: 'Download Invoice',
        });
        expect(downloadFromS3PreSignedUrl).not.toHaveBeenCalled();
        expect(bookingHistoryInfoCardProps.downloadInvoiceError).toBe(
          'dashboard.bookings.downloadInvoiceError'
        );
      });
    });

    it('should handle GraphQL errors in response', async () => {
      downloadBookingInvoice.mockResolvedValue({
        data: null,
        errors: [
          {
            message: '{"code":"001","details":["Invalid language code"],"errorType":400}',
            path: ['downloadBookingInvoice'],
          },
        ],
      });
      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadBookingInvoice).toHaveBeenCalled();
        expect(analytics.track).toHaveBeenCalledWith('error_event', {
          error_message: expect.any(String),
          journey_name: 'Download Invoice',
        });
        expect(downloadFromS3PreSignedUrl).not.toHaveBeenCalled();
        expect(bookingHistoryInfoCardProps.downloadInvoiceError).toBe(
          'dashboard.bookings.downloadInvoiceError'
        );
      });
    });

    it('should clear error state on successful download', async () => {
      const mockInvoiceUrl = 'https://s3.amazonaws.com/invoice.pdf';
      const mockFileName = 'Invoice_BCQR231999_Smith.pdf';

      downloadBookingInvoice.mockResolvedValue({
        data: {
          invoices: [
            {
              url: mockInvoiceUrl,
              fileName: mockFileName,
            },
          ],
        },
        errors: undefined,
      });
      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadFromS3PreSignedUrl).toHaveBeenCalled();
        expect(bookingHistoryInfoCardProps.downloadInvoiceError).toBeUndefined();
      });
    });

    it('should set isDownloadingInvoice to true while downloading and false after completion', async () => {
      let resolveDownload: (value: any) => void;
      const downloadPromise = new Promise((resolve) => {
        resolveDownload = resolve;
      });

      downloadBookingInvoice.mockReturnValue(downloadPromise);
      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      // Before download
      expect(bookingHistoryInfoCardProps.isDownloadingInvoice).toBe(false);

      // Start download
      act(() => {
        handleDownloadInvoice();
      });

      // During download
      await waitFor(() => {
        expect(bookingHistoryInfoCardProps.isDownloadingInvoice).toBe(true);
      });

      // Complete download
      await act(async () => {
        resolveDownload({
          data: {
            invoices: [
              {
                url: 'https://s3.amazonaws.com/invoice.pdf',
                fileName: 'Invoice.pdf',
              },
            ],
          },
          errors: undefined,
        });
      });

      // After download
      await waitFor(() => {
        expect(bookingHistoryInfoCardProps.isDownloadingInvoice).toBe(false);
      });
    });

    it('should set isDownloadingInvoice to false after download completes', async () => {
      downloadBookingInvoice.mockResolvedValue({
        data: {
          invoices: [
            {
              url: 'https://s3.amazonaws.com/invoice.pdf',
              fileName: 'Invoice.pdf',
            },
          ],
        },
        errors: undefined,
      });
      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadFromS3PreSignedUrl).toHaveBeenCalled();
      });

      // After download completes, should be able to call again
      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadBookingInvoice).toHaveBeenCalledTimes(2);
      });
    });

    it('should set isDownloadingInvoice to false even when error occurs', async () => {
      downloadBookingInvoice.mockRejectedValue(new Error('Network error'));
      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadBookingInvoice).toHaveBeenCalled();
      });

      // Reset mock for second attempt
      downloadBookingInvoice.mockClear();
      downloadBookingInvoice.mockResolvedValue({
        data: {
          invoices: [
            {
              url: 'https://s3.amazonaws.com/invoice.pdf',
              fileName: 'Invoice.pdf',
            },
          ],
        },
        errors: undefined,
      });

      // After error, should be able to retry
      await act(async () => {
        await handleDownloadInvoice();
      });
      await waitFor(() => {
        expect(downloadBookingInvoice).toHaveBeenCalledTimes(1);
      });
    });

    it('should handle undefined brand value', async () => {
      // Mock hotel info with undefined brand
      mockHotelInfoQueryRequest.data = {
        hotelInformation: {
          brand: undefined as any,
        },
      };

      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });

      await waitFor(() => {
        // Should not call API when brand is undefined
        expect(downloadBookingInvoice).not.toHaveBeenCalled();
        // Should track error event
        expect(analytics.track).toHaveBeenCalledWith('error_event', {
          error_message: 'Error downloading invoice: Hotel brand information is missing',
          journey_name: 'Download Invoice',
        });
        // Should set error state
        expect(bookingHistoryInfoCardProps.downloadInvoiceError).toBe(
          'dashboard.bookings.downloadInvoiceError'
        );
      });

      // Restore mock
      mockHotelInfoQueryRequest.data = {
        hotelInformation: {
          brand: 'premierinn',
        },
      };
    });

    it('should handle undefined channel fields', async () => {
      const propsWithUndefinedChannel = {
        ...mockProps,
        bookingChannel: {
          channel: undefined,
          subchannel: undefined,
          language: 'EN',
        } as unknown as BookingChannelCriteria,
      };

      render(<BookingHistoryInfoCardContainer {...propsWithUndefinedChannel} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });

      await waitFor(() => {
        // Should not call API when channel is undefined
        expect(downloadBookingInvoice).not.toHaveBeenCalled();
        // Should track error event
        expect(analytics.track).toHaveBeenCalledWith('error_event', {
          error_message: 'Error downloading invoice: Booking channel is missing',
          journey_name: 'Download Invoice',
        });
        // Should set error state
        expect(bookingHistoryInfoCardProps.downloadInvoiceError).toBe(
          'dashboard.bookings.downloadInvoiceError'
        );
      });
    });

    it('should handle missing authentication token', async () => {
      // Override the mock to return no token
      mockAuthToken.token = undefined as any;

      render(<BookingHistoryInfoCardContainer {...mockProps} />);

      expect(bookingHistoryInfoCardProps).not.toBeNull();
      const handleDownloadInvoice = bookingHistoryInfoCardProps.handleDownloadInvoiceAction;

      await act(async () => {
        await handleDownloadInvoice();
      });

      await waitFor(() => {
        // Should not call API when token is undefined
        expect(downloadBookingInvoice).not.toHaveBeenCalled();
        // Should track error event
        expect(analytics.track).toHaveBeenCalledWith('error_event', {
          error_message: 'Error downloading invoice: Authentication token is missing',
          journey_name: 'Download Invoice',
        });
        // Should set error state
        expect(bookingHistoryInfoCardProps.downloadInvoiceError).toBe(
          'dashboard.bookings.downloadInvoiceError'
        );
      });

      // Restore the original mock
      mockAuthToken.token = 'mock-token';
    });
  });
});
