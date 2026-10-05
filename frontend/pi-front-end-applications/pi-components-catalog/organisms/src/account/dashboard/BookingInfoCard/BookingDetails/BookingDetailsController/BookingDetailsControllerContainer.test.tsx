import '@testing-library/jest-dom';
import { Area } from '@whitbread-eos/api';
import * as React from 'react';

import { render } from '../../../../../utils/test-utils';
import BookingDetailsControllerContainer, { Props } from './BookingDetailsController.container';

const props: Props = {
  hotelId: 'MANOLD',
  basketReference: 'AWM-94ac4bac-6c53-4ba8-94fb-feb77b221f05',
  bookingReference: 'AWM0427238',
  getBookingStatus: () => null,
  bookingStatus: 'Reserved',
  bookingType: undefined,
  idvData: {
    personalInformation: {
      bookerName: 'Cristi Alex',
      guestName: 'Cristi Alex',
      address: '6 Brushfield Street',
      postcode: 'E1 6AN',
      telephoneNumber: '+4475472837153',
      cardUsedToMakeBooking: 'XXXXXXXXXXXX1103',
    },
    bookingInformation: {
      reservationNumber: {
        value: 'AWM0427238',
        partOfSearch: false,
      },
      hotelName: 'Manchester Old Trafford',
      arrivalDate: '2023-09-19',
      departureDate: '2023-09-20',
      emailAddress: 'alexcristi@yahoo.com',
    },
    dpaStatus: {
      dpaPassed: false,
      dpaOverride: false,
      eCnpPassword: '',
    },
  },
  defaultDataFromBooking: {
    acceptFutureMailing: false,
    addressLine1: '6 Brushfield Street',
    addressLine2: '',
    addressLine3: '',
    addressLine4: '',
    postcodeAddress: 'E1 6AN',
    addressSelection: 'HOME',
    cityName: '',
    companyName: '',
    countryCode: '',
    email: 'alexcristi@yahoo.com',
    firstName: 'Alex',
    landline: '',
    lastName: 'Cristi',
    postalCode: 'E1 6AN',
    phone: '+4475472837153',
    reasonForStay: 'LEI',
    manualAddressToggle: '',
    title: 'Mr',
    basketReferenceId: 'AWM-94ac4bac-6c53-4ba8-94fb-feb77b221f05',
    leadGuest: [
      {
        firstName: 'Alex',
        lastName: 'Cristi',
        stayInThisRoom: false,
      },
    ],
  },
  dpaInfo: {
    dpaPassed: false,
    dpaOverride: false,
  },
  overridenUserInfo: {
    reservationOverrideReasons: {
      reasonName: '',
      callerName: '',
      managerName: '',
      reasonCode: '',
    },
    reservationOverridden: false,
  },
  isAmendSuccessful: false,
  rateType: 'FLEXRATE',
  arrivalDate: '2023-09-19',
  bookingSurname: 'Cristi',
  isAmendPage: false,
};

const mockCustomLocale = jest.fn();

const mockQueryRequestResponse = {
  refetch: () => null,
  isLoading: false,
  isError: false,
  error: 'Error Msg',
  data: {
    manageBooking: {
      isCancellable: false,
      isAmendable: false,
      isRuleCompliant: true,
      aemLabelKey: 'Amends are not available within 24hours before stay date',
    },
  } as any,
};

const mockUseQueryRequest = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockUseQueryRequest(),
}));

jest.mock('./BookingDetailsController.component.tsx', () => {
  const BookingDetailsControllerMock = () => <div data-testid="BookingDetailsControllerComp-id" />;
  BookingDetailsControllerMock.displayName = 'BookingDetailsControllerComponent';
  return BookingDetailsControllerMock;
});

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('BookingDetails Container', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockUseQueryRequest.mockReturnValue(mockQueryRequestResponse);
    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });
  });

  it('should render for PI', async () => {
    const { getByTestId } = render(<BookingDetailsControllerContainer {...props} />);
    expect(getByTestId('BookingDetailsControllerComp-id')).toBeInTheDocument();
  });

  it('should render for CCUi', async () => {
    const modifiedProps = {
      ...props,
      area: Area.CCUI,
      dpaInfo: {
        dpaPassed: true,
        dpaOverride: true,
      },
      overridenUserInfo: {
        reservationOverrideReasons: {
          reasonName: '',
          callerName: '',
          managerName: '',
          reasonCode: '',
        },
        reservationOverridden: true,
      },
    };

    const { getByTestId } = render(<BookingDetailsControllerContainer {...modifiedProps} />);
    expect(getByTestId('BookingDetailsControllerComp-id')).toBeInTheDocument();
  });

  it('should render loading', async () => {
    mockUseQueryRequest.mockReturnValue({ ...mockQueryRequestResponse, isLoading: true });

    const { getByText } = render(<BookingDetailsControllerContainer {...props} />);
    expect(getByText('booking.loading')).toBeInTheDocument();
  });

  it('should render error notification', async () => {
    mockUseQueryRequest.mockReturnValue({ ...mockQueryRequestResponse, isError: true });

    const { getByText } = render(<BookingDetailsControllerContainer {...props} />);
    expect(getByText('Error Msg')).toBeInTheDocument();
  });

  it('should render when isRemovePIIDataFromLocalStorageEnabled is true', async () => {
    const { getByTestId } = render(
      <BookingDetailsControllerContainer {...props} isRemovePIIDataFromLocalStorageEnabled={true} />
    );
    expect(getByTestId('BookingDetailsControllerComp-id')).toBeInTheDocument();
  });
});
