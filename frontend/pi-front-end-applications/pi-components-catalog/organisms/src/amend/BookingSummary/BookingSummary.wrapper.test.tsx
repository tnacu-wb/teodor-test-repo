import '@testing-library/jest-dom';
import { BookingConfirmationType, SummaryOfPaymentsType } from '@whitbread-eos/api';
import React from 'react';

import {
  mockedStayDatesLabels,
  mockBookingSummaryLabels,
  mockedSummaryOfPaymentsLabels,
  mockRoomsAndGuestsLabels,
} from '../../mockData/mockResponse';
import { fireEvent, render, waitFor } from '../../utils/test-utils';
import BookingSummaryWrapper from './BookingSummary.wrapper';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureSwitch: () => true,
  useScreenSize: () => ({ isLessThanMobile: false, isLessThanLg: true }),
}));

const mockRoomPackages = [
  {
    nrAdults: 1,
    nrChildren: 0,
    roomType: 'LOWDBL',
    roomName: 'Accessible double bedroom with a lowered bath',
    selectedMeals: {
      adultsMeals: [],
      childrenMeals: [],
    },
    selectedExtrasList: {
      packagesSelection: [{ id: 'HSCOU2', noOfSelections: 1 }],
      reservationId: '721554',
      price: 0,
    },
    accessibleRoom: {
      isAccessible: true,
      phoneNumber: '0333 321 1262',
    },
    reservationId: '721554',
  },
  {
    nrAdults: 1,
    nrChildren: 0,
    roomType: 'DOUBLE',
    roomName: 'Double room',
    selectedExtrasList: {
      packagesSelection: [
        { packagesList: ['HSCKIN'], reservationId: '1798695', price: 10 },
        { packagesList: ['HSCOU2'], reservationId: '1798696', price: 10 },
        { packagesList: ['FI24HR'], reservationId: '1798697', price: 10 },
        { packagesList: ['DBPROS'], reservationId: '1798698', price: 10 },
      ],
      reservationId: '721554',
      price: 0,
    },
    selectedMeals: {
      adultsMeals: [
        {
          title: 'Continental Breakfast',
          id: 'BFADCT',
          price: 9.99,
          noSelections: 1,
        },
      ],
      childrenMeals: [],
    },
    accessibleRoom: {
      isAccessible: true,
      phoneNumber: '0333 321 1262',
    },
    reservationId: '721555',
  },
];

const mockBookingInformation: BookingConfirmationType = {
  bookingFlowId: 'booking-a1',
  hotelId: 'LONEUS',
  hotelName: 'London Euston',
  currencyCode: 'GBP',
  totalCost: 199.95,
  previousTotal: 0,
  newTotal: 199.95,
  channel: 'PI',
  companyId: 2455921,
  reservationByIdList: [
    {
      reservationId: '720981',
      reservationGuestList: [
        {
          givenName: 'Tilica',
          surName: 'Franaru',
          nameTitle: 'Prof',
          email: '',
        },
      ],
      billing: {
        address: {
          addressLine1: 'London Road North',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'LOWESTOFT',
          companyName: 'United Reformed Church',
          country: 'GB',
          postalCode: 'NR32 1HB',
        },
        email: 'cristiadrian@mailinator.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-04-01',
        departureDate: '2024-04-06',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'DOUBLE',
          roomName: 'Double room',
          groupId: 'double',
        },
        accessibleRoom: {
          phoneNumber: '0333 321 1262',
          isAccessible: true,
        },
        roomPrice: 75,
      },
    },
    {
      reservationId: '721541',
      reservationGuestList: [
        {
          givenName: 'Cristi',
          surName: 'Normal',
          nameTitle: 'Mr',
          email: 'cristiadrian@mailinator.com',
        },
      ],
      billing: {
        address: {
          addressLine1: 'London Road North',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'LOWESTOFT',
          companyName: 'United Reformed Church',
          country: 'GB',
          postalCode: 'NR32 1HB',
        },
        email: 'cristiadrian@mailinator.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-04-01',
        departureDate: '2024-04-06',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'LOWDBL',
          roomName: 'Accessible double bedroom with a lowered bath',
          groupId: 'accessible',
        },
        accessibleRoom: {
          phoneNumber: '0333 321 1262',
          isAccessible: true,
        },
        roomPrice: 75,
      },
    },
  ],
};

const mockSummaryOfPayments: SummaryOfPaymentsType = {
  charitable: 0,
  previousTotal: 199.95,
  balancePaid: 0,
  payOnArrival: 199.95,
  refund: 0,
  nonRefundable: 0,
  totalCost: 199.95,
  balanceAuthorised: 0,
  paymentOptions: {
    payNow: false,
    payOnArrival: false,
  },
  paymentCardDetails: {
    cardNumberMasked: '',
    token: '',
    expirationDate: '',
    cardType: '',
    cardHolderName: '',
    cardNumberLast4Digits: '',
    cardLogoSrc: '',
    cardName: '',
  },
};

const mockBookingSummaryProps = {
  language: 'en',
  bookingInformation: mockBookingInformation,
  roomsPackages: mockRoomPackages,
  originalArrivalDate: '2024-04-01',
  originalDepartureDate: '2024-04-06',
  stayDatesLabels: mockedStayDatesLabels,
  roomsAndGuestsLabels: mockRoomsAndGuestsLabels,
  bookingSummaryLabels: mockBookingSummaryLabels,
  summaryOfPayments: mockSummaryOfPayments,
  summaryOfPaymentsLabels: mockedSummaryOfPaymentsLabels,
  isConfirmButtonEnabled: true,
  onConfirmChanges: jest.fn(),
  handleRedirectToAmendPayment: jest.fn(),
  isRedirectToAmendPaymentEnabled: true,
  hideConfirmButton: false,
  variant: 'ccui',
  setEmailCallback: jest.fn(),
};

describe('Booking Summary Wrapper tests', () => {
  it('should find redirect to amend payment button', () => {
    const { getByText } = render(<BookingSummaryWrapper {...mockBookingSummaryProps} />);
    expect(getByText('Continue to payment')).toBeInTheDocument();
  });
  it('should trigger redirect to amend payment function', () => {
    mockBookingSummaryProps.isConfirmButtonEnabled = true;

    const { getByText } = render(<BookingSummaryWrapper {...mockBookingSummaryProps} />);

    const button = getByText('Continue to payment', { selector: 'button' });

    expect(button).toBeInTheDocument();
    expect(button).toBeEnabled();

    fireEvent.click(button);

    expect(mockBookingSummaryProps.handleRedirectToAmendPayment).toBeCalledTimes(1);
  });
  it('should find confirm changes button', () => {
    jest.clearAllMocks();
    mockBookingSummaryProps.isRedirectToAmendPaymentEnabled = false;
    mockBookingSummaryProps.hideConfirmButton = false;
    mockBookingSummaryProps.isConfirmButtonEnabled = true;
    const { getByText } = render(<BookingSummaryWrapper {...mockBookingSummaryProps} />);
    expect(getByText('Confirm changes')).toBeInTheDocument();
  });
  it('should render the nameTitle uppercaseFirst', () => {
    mockBookingSummaryProps.isRedirectToAmendPaymentEnabled = false;
    const { getByText } = render(<BookingSummaryWrapper {...mockBookingSummaryProps} />);

    expect(getByText('Prof Tilica Franaru')).toBeInTheDocument();
  });
  it('should not render the nameTitle if it is null or undefined', () => {
    mockBookingSummaryProps.isRedirectToAmendPaymentEnabled = false;
    const { getByText } = render(
      <BookingSummaryWrapper
        {...mockBookingSummaryProps}
        bookingInformation={{
          ...mockBookingInformation,
          reservationByIdList: [
            {
              reservationId: '720981',
              reservationGuestList: [
                {
                  givenName: 'Tilica',
                  surName: 'Franaru',
                  nameTitle: null,
                  email: null,
                },
              ],
              billing: {
                address: {
                  addressLine1: 'London Road North',
                  addressLine2: '',
                  addressLine3: '',
                  addressLine4: 'LOWESTOFT',
                  companyName: 'United Reformed Church',
                  country: 'GB',
                  postalCode: 'NR32 1HB',
                },
                email: 'cristiadrian@mailinator.com',
              },
              roomStay: {
                adultsNumber: 1,
                childrenNumber: 0,
                arrivalDate: '2024-04-01',
                departureDate: '2024-04-06',
                ratePlanCode: 'FLEXRATE',
                roomExtraInfo: {
                  roomType: 'DOUBLE',
                  roomName: 'Double room',
                  groupId: 'double',
                },
                accessibleRoom: {
                  phoneNumber: '0333 321 1262',
                  isAccessible: true,
                },
                roomPrice: 75,
              },
            },
            {
              reservationId: '721541',
              reservationGuestList: [
                {
                  givenName: 'Cristi',
                  surName: 'Normal',
                  nameTitle: 'Mr',
                  email: 'cristiadrian@mailinator.com',
                },
              ],
              billing: {
                address: {
                  addressLine1: 'London Road North',
                  addressLine2: '',
                  addressLine3: '',
                  addressLine4: 'LOWESTOFT',
                  companyName: 'United Reformed Church',
                  country: 'GB',
                  postalCode: 'NR32 1HB',
                },
                email: 'cristiadrian@mailinator.com',
              },
              roomStay: {
                adultsNumber: 1,
                childrenNumber: 0,
                arrivalDate: '2024-04-01',
                departureDate: '2024-04-06',
                ratePlanCode: 'FLEXRATE',
                roomExtraInfo: {
                  roomType: 'LOWDBL',
                  roomName: 'Accessible double bedroom with a lowered bath',
                  groupId: 'accessible',
                },
                accessibleRoom: {
                  phoneNumber: '0333 321 1262',
                  isAccessible: true,
                },
                roomPrice: 75,
              },
            },
          ],
        }}
      />
    );
    expect(getByText('Tilica Franaru')).toBeInTheDocument();
  });

  it('should make the button disabled when isConfirmButtonEnabled is false', () => {
    jest.clearAllMocks();
    mockBookingSummaryProps.isRedirectToAmendPaymentEnabled = true;
    mockBookingSummaryProps.isConfirmButtonEnabled = false;

    const { getByText, getAllByRole } = render(
      <BookingSummaryWrapper {...mockBookingSummaryProps} />
    );

    const button = getByText('Continue to payment', { selector: 'button' });
    expect(getAllByRole('button')[0]).toBeInTheDocument();
    expect(button).toBeInTheDocument();
    expect(button).toBeDisabled();
  });
  it('should show ECI/LCO Information notification', () => {
    jest.clearAllMocks();

    const { getByTestId } = render(
      <BookingSummaryWrapper {...mockBookingSummaryProps} showEciLcoNotification={true} />
    );

    expect(getByTestId('amend-booking-summary-Notification-Info-EciLco')).toBeInTheDocument();
  });
  it('should show ECI/LCO Information in BookingSummary', () => {
    jest.clearAllMocks();

    const { getByTestId } = render(
      <BookingSummaryWrapper
        {...mockBookingSummaryProps}
        extrasItemsPrices={{ eciPrice: 10, lcoPrice: 10 }}
      />
    );

    expect(getByTestId('amend-booking-summary-room-0-extras-HSCOU2')).toBeInTheDocument();
  });
  it('should call handleAmendSubmit when isRedirectToPaymentEnabled is false and display the modal', async () => {
    jest.clearAllMocks();
    mockBookingSummaryProps.isRedirectToAmendPaymentEnabled = false;
    mockBookingSummaryProps.isConfirmButtonEnabled = true;

    const { getByRole, getByTestId } = render(
      <BookingSummaryWrapper {...mockBookingSummaryProps} />
    );

    const button = getByRole('button', { name: 'Confirm changes' });
    fireEvent.click(button);
    await waitFor(() => {
      expect(getByTestId('ModalBody')).toBeInTheDocument();
      expect(mockBookingSummaryProps.onConfirmChanges).not.toBeCalled();
    });
    expect(button).toBeInTheDocument();
  });

  it('should call onConfirmChanges when isRedirectToPaymentEnabled is false and channel is pi or bb', async () => {
    jest.clearAllMocks();
    mockBookingSummaryProps.isRedirectToAmendPaymentEnabled = false;
    mockBookingSummaryProps.isConfirmButtonEnabled = true;

    const { getByRole, queryByTestId } = render(
      <BookingSummaryWrapper {...mockBookingSummaryProps} variant="pi" />
    );

    const button = getByRole('button', { name: 'Confirm changes' });
    fireEvent.click(button);
    await waitFor(() => {
      expect(queryByTestId('ModalBody')).not.toBeInTheDocument();
      expect(mockBookingSummaryProps.onConfirmChanges).toBeCalled();
    });
    expect(button).toBeInTheDocument();
  });

  it('should close the modal if the user pressed the close button', async () => {
    jest.clearAllMocks();
    mockBookingSummaryProps.isRedirectToAmendPaymentEnabled = false;
    mockBookingSummaryProps.isConfirmButtonEnabled = true;

    const { getByRole, getByTestId } = render(
      <BookingSummaryWrapper {...mockBookingSummaryProps} />
    );

    const button = getByRole('button', { name: 'Confirm changes' });
    fireEvent.click(button);
    await waitFor(() => {
      const closeModalBtn = getByTestId('ModalCloseButton');
      fireEvent.click(closeModalBtn);
    });
    await waitFor(() => {
      expect(getByTestId('ModalBody')).not.toBeVisible();
    });
    expect(button).toBeInTheDocument();
  });
});
