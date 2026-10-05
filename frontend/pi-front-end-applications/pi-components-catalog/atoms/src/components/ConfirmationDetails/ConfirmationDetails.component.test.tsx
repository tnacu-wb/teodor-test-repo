import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import ConfirmationDetails, { Props } from './ConfirmationDetails.component';

const mockData: Props = {
  data: {
    confirmationDetails: {
      bookingReference: '12345ABC',
      hotelName: 'hotelName',
      hotelAddress: 'hotelAddress',
      hotelTel: 'hotelTel',
      roomReservationStartDate: '12/12/2023',
      roomReservationEndDate: '12/13/2023',
      leadGuestTitle: 'Mr.',
      leadGuestName: 'Sergio',
      rateType: 'FLEXRATE',
      paymentOption: 'PAY_NOW',
    },
  },
  currentLang: 'en',
  t: (key: string) => {
    switch (key) {
      case 'booking.confirmation.bookingReference':
        return 'Booking reference:';
      case 'booking.confirmation.tel':
        return 'Tel.';
      case 'booking.confirmation.paymentTaken':
        return 'Thank you, prepayment has been taken.';
      case 'booking.confirmation.reservedWithoutCard':
        return 'Thank you for your booking. We are looking forward to your visit!';
      case 'booking.confirmation.reservedWithCard':
        return 'Thank you, your payment will be taken on arrival';
      default:
        return 'default';
    }
  },
};

describe('ConfirmationDetails', () => {
  it('should render ConfirmationDetails correctly', function () {
    const { getByText } = render(<ConfirmationDetails {...mockData} />);

    expect(getByText('Booking reference:')).toBeInTheDocument();
    expect(
      getByText(mockData.data.confirmationDetails.bookingReference as string)
    ).toBeInTheDocument();
    expect(getByText(mockData.data.confirmationDetails.hotelName)).toBeInTheDocument();
    expect(getByText(mockData.data.confirmationDetails.hotelAddress)).toBeInTheDocument();
    expect(getByText(`Tel. ${mockData.data.confirmationDetails.hotelTel}`)).toBeInTheDocument();
  });

  it('should render ConfirmationDetails text correctly for pay on arrival', function () {
    mockData.data.confirmationDetails.paymentOption = 'PAY_ON_ARRIVAL';
    const { getByText } = render(<ConfirmationDetails {...mockData} />);

    expect(getByText('Thank you, your payment will be taken on arrival')).toBeInTheDocument();
  });

  it('should render ConfirmationDetails text correctly for rez without card', function () {
    mockData.data.confirmationDetails.paymentOption = 'RESERVE_WITHOUT_CARD';
    const { getByText } = render(<ConfirmationDetails {...mockData} />);

    expect(
      getByText('Thank you for your booking. We are looking forward to your visit!')
    ).toBeInTheDocument();
  });

  it('should render ConfirmationDetails text correctly for default payment option', function () {
    mockData.data.confirmationDetails.paymentOption = '';
    const { getByText } = render(<ConfirmationDetails {...mockData} />);

    expect(getByText('default')).toBeInTheDocument();
  });

  it('should render ConfirmationDetails correctly for de language', function () {
    mockData.currentLang = 'de';

    const { getByText } = render(<ConfirmationDetails {...mockData} />);

    expect(getByText('Booking reference:')).toBeInTheDocument();
  });
});
