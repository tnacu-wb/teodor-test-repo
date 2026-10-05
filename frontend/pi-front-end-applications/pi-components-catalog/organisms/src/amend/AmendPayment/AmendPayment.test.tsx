import '@testing-library/jest-dom';
import { Area, UserType } from '@whitbread-eos/api';
import React from 'react';

import {
  mockedBookingConfirmationAmendData,
  mockedSelectedPaymentType,
  mockedSummaryOfPaymentsData,
} from '../../mockData/mockResponse';
import { render } from '../../utils/test-utils';
import AmendPayment from './AmendPayment.component';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),

  useRouter() {
    return {
      query: { reservationId: 'jhsd887' },
    };
  },
}));

const mockSetSelectedPaymentType = jest.fn();

const mockedProps = {
  isBillingAddressDisplayed: false,
  continueToNextStep: jest.fn(),
  selectedPaymentDetail: {
    type: 'PAY_NOW',
    order: 1,
    enabled: true,
  },
  setSelectedPaymentDetail: jest.fn(),
  selectedPaymentType: mockedSelectedPaymentType,
  setSelectedPaymentType: mockSetSelectedPaymentType.mockImplementation(
    () => mockedSelectedPaymentType
  ),
  hotelName: 'London Euston',
  errorMessagePayment: undefined as string | undefined,
  bookingConfirmation: mockedBookingConfirmationAmendData.bookingConfirmation,
  variant: Area.PI,
  userType: UserType.Leisure,
  amendSummary: mockedSummaryOfPaymentsData,
  isLoading: false,
};

describe('AmendPayment Component', () => {
  afterEach(() => jest.clearAllMocks());

  it('should display the notification to explain why user has these options', () => {
    const { getByTestId } = render(<AmendPayment {...mockedProps} />);
    expect(getByTestId('amend-payment-options-Alert')).toBeInTheDocument();
  });

  it('should display `do not go back` notification', () => {
    const { getByTestId } = render(<AmendPayment {...mockedProps} />);
    expect(getByTestId('amend-payment-do-not-go-back-Alert')).toBeInTheDocument();
  });

  it('should check the Pay now option if is enabled', () => {
    const { getByRole } = render(<AmendPayment {...mockedProps} />);

    expect(
      getByRole('radio', { name: 'paymentOptions.PAY_NOW paymentOptions.PAY_NOW_DESC' })
    ).toBeChecked();
  });

  it('should uncheck the Pay now option if is disabled and check the other option', () => {
    const { getByRole } = render(
      <AmendPayment
        {...mockedProps}
        selectedPaymentDetail={{
          type: 'PAY_ON_ARRIVAL',
          order: 1,
          enabled: true,
        }}
        amendSummary={{
          ...mockedSummaryOfPaymentsData,
          paymentOptions: { payNow: false, payOnArrival: true },
        }}
        selectedPaymentType={{
          ...mockedSelectedPaymentType,
          paymentOptions: [
            {
              type: 'PAY_NOW',
              order: 1,
              enabled: false,
            },
            {
              type: 'PAY_ON_ARRIVAL',
              order: 2,
              enabled: true,
            },
          ],
        }}
      />
    );

    expect(
      getByRole('radio', { name: 'paymentOptions.PAY_NOW paymentOptions.PAY_NOW_DESC' })
    ).not.toBeChecked();

    expect(
      getByRole('radio', {
        name: 'paymentOptions.PAY_ON_ARRIVAL paymentOptions.PAY_ON_ARRIVAL_DESC',
      })
    ).toBeChecked();

    expect(
      getByRole('radio', {
        name: 'paymentOptions.PAY_NOW paymentOptions.PAY_NOW_DESC',
      })
    ).toBeDisabled();
  });
});
