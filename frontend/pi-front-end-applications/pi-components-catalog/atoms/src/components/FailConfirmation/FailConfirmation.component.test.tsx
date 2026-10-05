import '@testing-library/jest-dom';
import { useRouter } from 'next/router';
import React from 'react';

import { render } from '../../utils/test-utils';
import FailConfirmation from './FailConfirmation.component';

const mockCustomLocale = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
}));
jest.mock('next/router', () => ({
  useRouter: jest.fn(),
}));

const mockFailConfData = {
  t: (key: string) => {
    switch (key) {
      case 'booking.confirmation.sorry':
        return "We're sorry, we can't confirm your booking right now";
      case 'errors.booking.generic':
        return "We're sorry, we can't confirm your booking right now";
      case 'nonguaranteed.booking.secureBookingError':
        return 'Your booking could not be guaranteed. Please try again.';
      case 'booking.confirmation.updateEmailMessage':
        return "We'll send an email to [emailAddress] within the next 30 minutes with an update.";
      case 'ccui.booking.confirmation.reference':
        return 'Reference';
      case 'booking.confirmation.paymentMessage':
        return 'Your prepayment will be automatically refunded if your booking is unable to be confirmed';
      case 'ccui.booking.confirmation.bookingErrorMessage.reservedWithCard.PID':
        return 'After 30 minutes ask guest to check online OR call back into CC OR email Contact Centre on kontakt@whitbread.com.';
      case 'ccui.booking.confirmation.bookingErrorMessage.reservedWithCard.PI':
        return 'After 30 minutes ask guest to check online OR call back into CC.';
      case 'ccui.booking.confirmation.bookingErrorMessage.payNow':
        return 'After 30 minutes ask guest to check online OR call back into CC. Any payment already made will be automatically refunded if this booking is unable to be confirmed.';
      default:
        return 'default';
    }
  },
  data: {
    emailAddress: '[emailAddress]',
    bookingReference: 'AWM4719551',
    notificationText:
      'Your prepayment will be automatically refunded if your booking is unable to be confirmed',
    paymentOption: 'PAY_ON_ARRIVAL',
    language: 'en',
  },
};

describe('FailConfirmation', () => {
  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue({ query: {} });
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    jest.clearAllMocks();
  });
  it('renders title with generic sorry when secure-booking is not set', () => {
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);
    expect(getByTestId('FailConfirmation-Title-Name')).toHaveTextContent(
      mockFailConfData.t('booking.confirmation.sorry')
    );
  });

  it('renders title with secure booking error when secure-booking=true', () => {
    (useRouter as jest.Mock).mockReturnValue({ query: { 'secure-booking': 'true' } });
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);
    expect(getByTestId('FailConfirmation-Title-Name')).toHaveTextContent(
      mockFailConfData.t('nonguaranteed.booking.secureBookingError')
    );
  });

  it('should render correctly with default values', () => {
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('FailConfirmation-Title-Name')).toBeInTheDocument();
    expect(getByTestId('FailConfirmation-Title-Name')).toHaveTextContent(
      mockFailConfData.t('errors.booking.generic')
    );
  });

  it('should render the title correctly; when failed', () => {
    mockFailConfData.data.sendMail = true;
    mockFailConfData.data.isCcui = false;

    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('FailConfirmation-Title-Name')).toBeInTheDocument();
    expect(getByTestId('FailConfirmation-Title-Name')).toHaveTextContent(
      mockFailConfData.t('errors.booking.generic')
    );
  });

  it('should render the email correctly; when failed', () => {
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('FailConfirmation-Email')).toBeInTheDocument();
    expect(getByTestId('FailConfirmation-Email')).toHaveTextContent(
      mockFailConfData.t('booking.confirmation.updateEmailMessage')
    );
  });

  it('should render the alert data correctly; when failed', () => {
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('AlertDescription')).toBeInTheDocument();
    expect(getByTestId('AlertDescription')).toHaveTextContent(
      'Your prepayment will be automatically refunded if your booking is unable to be confirmed'
    );
  });

  it('should render the label correctly; when failed', () => {
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('FailConfirmation-Label')).toBeInTheDocument();
    expect(getByTestId('FailConfirmation-Label')).toHaveTextContent('Reference');
  });

  it('should render the reservation id correctly; when failed', () => {
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('FailConfirmation-Id')).toBeInTheDocument();
    expect(getByTestId('FailConfirmation-Id')).toHaveTextContent(
      mockFailConfData.data.bookingReference
    );
  });

  it('should render the notification section correctly', () => {
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('FailConfirmation-Notification')).toBeInTheDocument();
  });

  it('should render `Reference` label correctly', () => {
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('FailConfirmation-Label')).toBeInTheDocument();
    expect(getByTestId('FailConfirmation-Label')).toHaveTextContent(
      mockFailConfData.t('ccui.booking.confirmation.reference')
    );
  });

  it('should render correct text for email if ccui false and paymentoption is pay now', () => {
    mockFailConfData.data.isCcui = false;
    mockFailConfData.data.paymentOption = 'PAY_NOW';
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('FailConfirmation-Email')).toBeInTheDocument();
    expect(getByTestId('FailConfirmation-Email')).toHaveTextContent(
      mockFailConfData.t('booking.confirmation.paymentMessage')
    );
  });

  it('should render booking error msg if is german hotel and no send mail', () => {
    mockFailConfData.data.isGermanHotel = true;
    mockFailConfData.data.sendMail = false;

    const { getByTestId, queryByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(queryByTestId('FailConfirmation-Email')).toBeNull();
    expect(getByTestId('FailConfirmation-BookingErrorMsgPOA')).toBeInTheDocument();
    expect(getByTestId('FailConfirmation-BookingErrorMsgPOA')).toHaveTextContent(
      mockFailConfData.t('ccui.booking.confirmation.bookingErrorMessage.reservedWithCard.PID')
    );
  });

  it('should render pre-payment message correctly on ccui', () => {
    mockFailConfData.data.isCcui = true;
    mockFailConfData.data.sendMail = true;
    mockFailConfData.data.paymentOption = 'PAY_NOW';
    const { getByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(getByTestId('FailConfirmation-Email')).toBeInTheDocument();
    expect(getByTestId('FailConfirmation-Email')).toHaveTextContent(
      mockFailConfData.t('ccui.booking.confirmation.bookingErrorMessage.payNow')
    );
  });

  it('should not render sendEmail comp on ccui is sendMail is not enabled', () => {
    mockFailConfData.data.isCcui = true;
    mockFailConfData.data.sendMail = false;
    const { queryByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(queryByTestId('FailConfirmation-Email')).not.toBeInTheDocument();
    expect(queryByTestId('FailConfirmation-Email')).toBeNull();

    expect(queryByTestId('FailConfirmation-BookingErrorMsgPOA')).toBeInTheDocument();
    expect(queryByTestId('FailConfirmation-BookingErrorMsgPOA')).not.toBeNull();
  });

  it('should not render comp if data is null', () => {
    mockFailConfData.data = null;

    const { queryByTestId } = render(<FailConfirmation {...mockFailConfData} />);

    expect(queryByTestId('FailConfirmation-Email')).toBeNull();
  });
});
