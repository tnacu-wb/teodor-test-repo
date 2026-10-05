import '@testing-library/jest-dom';
import { getBookingConfirmationMessage } from '@whitbread-eos/utils';
import React from 'react';

import { render } from '../../utils/test-utils';
import ThanksForBooking from './ThanksForBooking.component';

jest.mock('next/router', () => ({
  useRouter: () => ({
    query: {},
  }),
}));

jest.mock('@whitbread-eos/utils', () => {
  const originalModule = jest.requireActual('@whitbread-eos/utils');
  return {
    ...originalModule,
    getBookingConfirmationMessage: jest.fn(() => 'Mocked Confirmation Message'),
  };
});

const mockData = {
  t: (key: string) => {
    switch (key) {
      case 'booking.confirmation.thankyouForBookingWithoutCustomerName':
        return 'Thanks for your booking';
      case 'booking.confirmation.confirmationEmailMessage':
        return 'A confirmation email has been sent to';
      default:
        return 'default';
    }
  },
  currentLang: 'en',
  data: {
    thanksForBooking: {
      title: 'Mister',
      lastName: 'Sergio',
      firstName: 'Wade',
      emailAddress: 'sergio@mail.com',
    },
  },
};

describe('ThanksForBooking', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render ThanksForBooking correctly with mocked confirmation message', function () {
    const { getByText } = render(<ThanksForBooking {...mockData} />);

    expect(getBookingConfirmationMessage).toHaveBeenCalled(); // Assert it's called
    expect(getByText('Mocked Confirmation Message Wade')).toBeInTheDocument();
    expect(getByText('A confirmation email has been sent to')).toBeInTheDocument();
  });

  it("should not render confirmation email text if email doesn't exist", function () {
    const modifiedData = {
      ...mockData,
      data: {
        ...mockData.data,
        thanksForBooking: {
          ...mockData.data.thanksForBooking,
          emailAddress: '',
        },
      },
    };

    const { getByText, queryByText } = render(<ThanksForBooking {...modifiedData} />);

    expect(getByText('Mocked Confirmation Message Wade')).toBeInTheDocument();
    expect(queryByText('A confirmation email has been sent to')).not.toBeInTheDocument();
  });

  it('should also display the title if the language is de', function () {
    const modifiedData = {
      ...mockData,
      currentLang: 'de',
    };

    const { getByText } = render(<ThanksForBooking {...modifiedData} />);

    expect(getByText('Mocked Confirmation Message Mister Sergio')).toBeInTheDocument();
  });

  it('should return null when data is empty', function () {
    const modifiedData = {
      ...mockData,
      data: {
        thanksForBooking: null,
      },
    };

    const { queryByTestId } = render(<ThanksForBooking {...modifiedData} />);

    expect(queryByTestId('ThanksForBookingContainer')).toBeNull();
  });
});
