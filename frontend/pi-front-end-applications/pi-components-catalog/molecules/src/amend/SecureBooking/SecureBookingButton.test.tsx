import '@testing-library/jest-dom';
import { BC_RESERVATION_STATUS, Area, paymentOptions, BASKET_STATUS } from '@whitbread-eos/api';
import {
  useFeatureToggle,
  useCustomLocale,
  encodeToBase64,
  validateArrivalDate,
  shouldDisplaySecureBooking,
} from '@whitbread-eos/utils';
import { useRouter } from 'next/router';

import { userEvent, render, screen } from '../../utils/test-utils';
import SecureBookingButton from './SecureBookingButton.component';

// Mocks
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(),
  useCustomLocale: jest.fn(),
  encodeToBase64: jest.fn(),
  validateArrivalDate: jest.fn().mockReturnValue(true),
  shouldDisplaySecureBooking: jest.fn().mockReturnValue(true),
  useQueryRequest: jest.fn().mockImplementation((key) => {
    if (key[0] === 'GetHotelInformation') {
      return {
        isLoading: false,
        data: { hotelInformation: { brand: 'PID' } },
      };
    }
    if (key[0] === 'GetBookingInformation') {
      return {
        data: { bookingInformation: { bookingFlowId: 'booking-a1' } },
      };
    }
    return {};
  }),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: jest.fn(),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

describe('SecureBookingButton', () => {
  const mockReplace = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    (useRouter as jest.Mock).mockReturnValue({ push: mockReplace });
    (useCustomLocale as jest.Mock).mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    (useFeatureToggle as jest.Mock).mockReturnValue({
      ['release_pi_bb_non_guaranteed_reminder']: true,
    });
    (encodeToBase64 as jest.Mock).mockImplementation((str) => Buffer.from(str).toString('base64'));
    (validateArrivalDate as jest.Mock).mockImplementation(() => true);
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => true);
    delete (window as any).location;
    (window as any).location = { href: '' };
  });

  const baseMockData = {
    arrivalDate: new Date(Date.now() + 86400000).toString(), // tomorrow
    basketReference: 'GAA-2303a0f9-a0ff-4752-a261-7646fc9c237a',
    bookingReference: 'GAA-2303a0f9-a0ff-4752-a261-7646fc9c237a',
    area: Area.PI,
    paymentOption: paymentOptions.RESERVE_WITHOUT_CARD,
    bookingStatus: BC_RESERVATION_STATUS.RESERVED,
    isAmendable: true,
    isCancellable: true,
    hotelInfo: {
      hotelId: 'FRAMTI',
      bookingFlowId: 'booking-ct-a1',
    },
  };

  it('should render button when all conditions are met', () => {
    render(<SecureBookingButton data={baseMockData} />);
    const button = screen.getByTestId('SecureBooking-Button');
    expect(button).toBeInTheDocument();
    expect(button).toHaveTextContent('nonguaranteed.booking.buttonlabel');
  });

  it('should call handleOnClick and redirect with encoded URL on button click', () => {
    render(<SecureBookingButton data={baseMockData} />);
    const button = screen.getByTestId('SecureBooking-Button');
    userEvent.click(button);

    expect(window.location.href).toBe(
      '/gb/en/booking-ct-a1/payment?reservationId=GAA-2303a0f9-a0ff-4752-a261-7646fc9c237a&secure-booking=true'
    );
  });

  it('should call handleOnClick and redirect with encoded URL on button click and isBusinessBooker is true', () => {
    render(<SecureBookingButton data={{ ...baseMockData, area: Area.BB }} />);
    const button = screen.getByTestId('SecureBooking-Button');
    userEvent.click(button);

    expect(window.location.href).toBe(
      `/gb/en/business-booker/booking-business/payment?reservationId=GAA-2303a0f9-a0ff-4752-a261-7646fc9c237a&secure-booking=true`
    );
  });

  it('should not render the button if release_pi_bb_non_guaranteed_reminder is disabled', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => false);

    render(<SecureBookingButton data={baseMockData} />);
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).not.toBeInTheDocument();
  });

  it('should not render the button if paymentOption is not RESERVE_WITHOUT_CARD', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => false);
    render(
      <SecureBookingButton
        data={{
          ...baseMockData,
          paymentOption: paymentOptions.PAY_NOW,
        }}
      />
    );
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).not.toBeInTheDocument();
  });

  it('should render the button if paymentOption is not RESERVE_WITHOUT_CARD and bokingStatus is PAY_PENDING', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => true);
    render(
      <SecureBookingButton
        data={{
          ...baseMockData,
          paymentOption: paymentOptions.PAY_NOW,
          bookingStatus: BASKET_STATUS.PAY_PENDING,
        }}
      />
    );
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).toBeInTheDocument();
  });

  it('should render the button if area PI and bookingFlowId is empty', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => true);
    render(
      <SecureBookingButton
        data={{
          ...baseMockData,
          paymentOption: paymentOptions.PAY_NOW,
          bookingStatus: BASKET_STATUS.PAY_PENDING,
          area: Area.PI,
          hotelInfo: {
            bookingFlowId: '',
            hotelId: 'FRAMTI',
          },
        }}
      />
    );
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).toBeInTheDocument();
  });

  it('should not render the feature flag is off', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => false);
    render(
      <SecureBookingButton
        data={{
          ...baseMockData,
        }}
      />
    );
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).not.toBeInTheDocument();
  });

  it('should not render the button if area CCUI', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => false);
    render(
      <SecureBookingButton
        data={{
          ...baseMockData,
          paymentOption: paymentOptions.PAY_NOW,
          bookingStatus: BASKET_STATUS.PAY_PENDING,
          area: Area.CCUI,
        }}
      />
    );
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).not.toBeInTheDocument();
  });

  it('should not render the button if bookingStatus is CANCELLED', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => false);
    render(
      <SecureBookingButton
        data={{
          ...baseMockData,
          bookingStatus: BC_RESERVATION_STATUS.CANCELLED,
        }}
      />
    );
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).not.toBeInTheDocument();
  });

  it('should not render the button if arrival date is in the past', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => false);
    const yesterday = new Date(Date.now() - 86400000).toISOString(); // yesterday

    (validateArrivalDate as jest.Mock).mockImplementation(() => false);
    render(
      <SecureBookingButton
        data={{
          ...baseMockData,
          arrivalDate: yesterday,
        }}
      />
    );
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).not.toBeInTheDocument();
  });

  it('should not render the button if arrivalDate is missing', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => false);
    (validateArrivalDate as jest.Mock).mockImplementation(() => false);
    render(
      <SecureBookingButton
        data={{
          ...baseMockData,
          arrivalDate: '',
        }}
      />
    );
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).not.toBeInTheDocument();
  });

  it('should not render the button if arrivalDate is invalid', () => {
    (shouldDisplaySecureBooking as jest.Mock).mockImplementation(() => false);
    (validateArrivalDate as jest.Mock).mockImplementation(() => false);
    render(
      <SecureBookingButton
        data={{
          ...baseMockData,
          arrivalDate: 'invalid-date',
        }}
      />
    );
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).not.toBeInTheDocument();
  });

  it('should only call window.location once on click', () => {
    render(<SecureBookingButton data={baseMockData} />);
    const button = screen.getByTestId('SecureBooking-Button');
    userEvent.click(button);
    expect(window.location.href).toBe(
      '/gb/en/booking-ct-a1/payment?reservationId=GAA-2303a0f9-a0ff-4752-a261-7646fc9c237a&secure-booking=true'
    );
  });

  it('should use correct locale from useCustomLocale for redirect', () => {
    (useCustomLocale as jest.Mock).mockReturnValue({
      language: 'fr',
      country: 'fr',
    });

    render(<SecureBookingButton data={baseMockData} />);
    const button = screen.getByTestId('SecureBooking-Button');
    userEvent.click(button);

    expect(window.location.href).toBe(
      '/fr/fr/booking-ct-a1/payment?reservationId=GAA-2303a0f9-a0ff-4752-a261-7646fc9c237a&secure-booking=true'
    );
  });

  it('should show correct translated label on the button', () => {
    render(<SecureBookingButton data={{ ...baseMockData, area: Area.BB }} />);
    const button = screen.getByTestId('SecureBooking-Button');
    expect(button).toHaveTextContent('nonguaranteed.booking.buttonlabel');
  });

  it('should render the button if isBusinessBooker is true', () => {
    render(<SecureBookingButton data={{ ...baseMockData, area: Area.BB }} />);
    const button = screen.queryByTestId('SecureBooking-Button');
    expect(button).toBeInTheDocument();
  });
});
