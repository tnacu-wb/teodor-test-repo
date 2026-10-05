import '@testing-library/jest-dom';

import { render, screen, waitFor } from '../../utils/test-utils';
import BookingConfirmation from './BookingConfirmation';

// Mock the useQueryRequestRestaurants hook and its response
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequestRestaurants: jest.fn(),
}));

// Custom type definition for the mock
const mockUseQueryRequest = jest.requireMock('@whitbread-eos/utils');

const setupById = {
  adults: 2,
  areaId: null,
  areaName: null,
  bookingReference: 'S9W2625773049130',
  braintreeCustomerId: null,
  cancelLink:
    'https://testevents-widget.liveres.co.uk/portal.html?siteId=b62be154-a301-49bb-9f00-edcee8a6fe9f&eventId=04bf44b0-91fe-4a68-b158-4758cd4756f2&styleSheetUrl=https%3a%2f%2fwww.beefeater.co.uk%2f%2fetc.clientlibs%2frestaurants%2fbeefeater%2fclientlibs%2fclientlib-external-liveres-lib.css',
  children: 0,
  consent: {
    consentStatement: false,
    email: false,
    phone: false,
    postal: false,
    privacyStatement: true,
    profiling: false,
    pushNotification: false,
    sms: false,
    termsAndConditions: false,
  },
  date: '2023-09-27',
  editLink:
    'https://testevents-widget.liveres.co.uk/portal.html?siteId=b62be154-a301-49bb-9f00-edcee8a6fe9f&eventId=04bf44b0-91fe-4a68-b158-4758cd4756f2&styleSheetUrl=https%3a%2f%2fwww.beefeater.co.uk%2f%2fetc.clientlibs%2frestaurants%2fbeefeater%2fclientlibs%2fclientlib-external-liveres-lib.css',
  emailAddress: 'wsss@dfcs.df',
  firstname: 'ws',
  id: '04bf44b0-91fe-4a68-b158-4758cd4756f2',
  lastname: 'saqw',
  name: null,
  occasionId: '30b545b7-bd1b-43ae-bc96-4520b04be8e6',
  occasionName: 'Table Booking (Whitbread WEB)',
  siteName: 'Whitbread Demo',
  siteId: 'b62be154-a301-49bb-9f00-edcee8a6fe9f',
  siteTimezone: 'Europe/London',
  specialRequest:
    'Number of highChar is required 0, wheelchair is not required and any other special request ',
  telephoneNumber: '12323232323',
  time: '10:00',
  turnTimeMinutes: 75,
};

const setupLocationData = () => ({
  locations: [
    {
      googleMapURL:
        'https://www.google.com/maps/place/The+Millfield+Beefeater/@53.9802955,-1.1351364,17z/data=!3m1!4b1!4m5!3m4!1s0x4879314e140566c1:0x6bed277edd338f22!8m2!3d53.9802735!4d-1.1329342',
      path: '/en-gb/locations/new-york',
      title: 'New York Location',
      contactInfo: '123 Main St, New York, NY',
    },
    {
      googleMapURL:
        'https://www.google.com/maps/place/The+Millfield+Beefeater/@53.9802955,-1.1351364,17z/data=!3m1!4b1!4m5!3m4!1s0x4879314e140566c1:0x6bed277edd338f22!8m2!3d53.9802735!4d-1.1329342',
      path: '/en-gb/locations/los-angeles',
      title: 'Los Angeles Location',
      contactInfo: '456 Elm St, Los Angeles, CA',
    },
  ],
});

const setupLabels = () => ({
  label: [
    {
      key: 'reservationform.booking.confirmation.msg',
      value: 'Booking Confirmed',
    },
  ],
});

describe('Session Form Component', () => {
  it('renders booking confirmation loading page', () => {
    mockUseQueryRequest.useQueryRequestRestaurants.mockReturnValue({
      data: null,
      isLoading: true,
      isError: false,
      error: null,
    });
    render(
      <BookingConfirmation
        restaurantBrandNameForAemApi={''}
        location={''}
        subLocation={''}
        eventId="event-123"
        enquiryId=""
      />
    );

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });
  it('renders enquiry send page', () => {
    mockUseQueryRequest.useQueryRequestRestaurants
      .mockReturnValueOnce({
        data: {
          label: [
            {
              key: 'reservationform.enquiry.confirmation.msg',
              value: 'Booking Enquiry Sent',
            },
          ],
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          enquiryById: {
            ...setupById,
          },
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          ...setupLocationData(),
        },
        isLoading: false,
        isError: false,
        error: null,
      });
    render(
      <BookingConfirmation
        restaurantBrandNameForAemApi={''}
        location={''}
        subLocation={''}
        eventId=""
        enquiryId="enquiry123"
      />
    );
    expect(screen.getByText('Booking Enquiry Sent')).toBeInTheDocument();
  });
  it('renders booking confirmation page', async () => {
    mockUseQueryRequest.useQueryRequestRestaurants
      .mockReturnValueOnce({
        data: {
          ...setupLabels(),
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          eventById: {
            ...setupById,
          },
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          ...setupLocationData(),
        },
        isLoading: false,
        isError: false,
        error: null,
      });
    render(
      <BookingConfirmation
        restaurantBrandNameForAemApi={''}
        location={''}
        subLocation={''}
        eventId="event-123"
        enquiryId=""
      />
    );
    await waitFor(() => {
      expect(screen.getByText('Booking Confirmed')).toBeInTheDocument();
    });
  });
  it('not renders booking confirmation page if eventById and equiryById is null ', async () => {
    mockUseQueryRequest.useQueryRequestRestaurants
      .mockReturnValueOnce({
        data: {
          ...setupLabels(),
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: null,
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          ...setupLocationData(),
        },
        isLoading: false,
        isError: false,
        error: null,
      });
    render(
      <BookingConfirmation
        restaurantBrandNameForAemApi={''}
        location={''}
        subLocation={''}
        eventId=""
        enquiryId=""
      />
    );
    await waitFor(() => {
      expect(screen.getByText('Booking Confirmed')).toBeInTheDocument();
    });
  });

  it('should display error notification when fetching event fails', () => {
    mockUseQueryRequest.useQueryRequestRestaurants
      .mockReturnValueOnce({
        data: {
          label: [
            {
              key: 'reservationform.booking.details.failure',
              value: 'Failed to load booking details',
            },
          ],
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: null,
        isLoading: false,
        isError: true,
        error: {
          response: {
            errors: [{ message: 'Failed to fetch event' }],
          },
        },
      })
      .mockReturnValueOnce({
        data: setupLocationData(),
        isLoading: false,
        isError: false,
        error: null,
      });

    render(
      <BookingConfirmation
        restaurantBrandNameForAemApi={''}
        location={''}
        subLocation={''}
        eventId="event-123"
        enquiryId=""
      />
    );

    expect(screen.getByText('Failed to load booking details')).toBeInTheDocument();
  });

  it('should display error notification when fetching enquiry fails', () => {
    mockUseQueryRequest.useQueryRequestRestaurants
      .mockReturnValueOnce({
        data: {
          label: [
            {
              key: 'reservationform.enquiry.details.failure',
              value: 'Failed to load enquiry details',
            },
          ],
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: null,
        isLoading: false,
        isError: true,
        error: {
          response: {
            errors: [{ message: 'Failed to fetch enquiry' }],
          },
        },
      })
      .mockReturnValueOnce({
        data: setupLocationData(),
        isLoading: false,
        isError: false,
        error: null,
      });

    render(
      <BookingConfirmation
        restaurantBrandNameForAemApi={''}
        location={''}
        subLocation={''}
        eventId=""
        enquiryId="enquiry-123"
      />
    );

    expect(screen.getByText('Failed to load enquiry details')).toBeInTheDocument();
  });

  it('should display "adult" (singular) when adults is 1', async () => {
    mockUseQueryRequest.useQueryRequestRestaurants
      .mockReturnValueOnce({
        data: setupLabels(),
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          eventById: {
            ...setupById,
            adults: 1,
          },
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: setupLocationData(),
        isLoading: false,
        isError: false,
        error: null,
      });

    render(
      <BookingConfirmation
        restaurantBrandNameForAemApi={''}
        location={''}
        subLocation={''}
        eventId="event-123"
        enquiryId=""
      />
    );

    await waitFor(() => {
      expect(screen.getByText(/1 adult/i)).toBeInTheDocument();
    });
  });

  it('should display "adults" (plural) when adults is greater than 1', async () => {
    mockUseQueryRequest.useQueryRequestRestaurants
      .mockReturnValueOnce({
        data: setupLabels(),
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          eventById: {
            ...setupById,
            adults: 3,
          },
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: setupLocationData(),
        isLoading: false,
        isError: false,
        error: null,
      });

    render(
      <BookingConfirmation
        restaurantBrandNameForAemApi={''}
        location={''}
        subLocation={''}
        eventId="event-123"
        enquiryId=""
      />
    );

    await waitFor(() => {
      expect(screen.getByText(/3 adults/i)).toBeInTheDocument();
    });
  });

  it('should not display adults when adults is 0', async () => {
    mockUseQueryRequest.useQueryRequestRestaurants
      .mockReturnValueOnce({
        data: setupLabels(),
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          eventById: {
            ...setupById,
            adults: 0,
          },
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: setupLocationData(),
        isLoading: false,
        isError: false,
        error: null,
      });

    render(
      <BookingConfirmation
        restaurantBrandNameForAemApi={''}
        location={''}
        subLocation={''}
        eventId="event-123"
        enquiryId=""
      />
    );

    await waitFor(() => {
      expect(screen.queryByText(/adult/i)).not.toBeInTheDocument();
    });
  });
});
