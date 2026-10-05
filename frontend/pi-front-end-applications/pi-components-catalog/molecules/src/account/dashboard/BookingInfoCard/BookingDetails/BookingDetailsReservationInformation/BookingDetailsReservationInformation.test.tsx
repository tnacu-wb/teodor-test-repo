import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { PurposeOfStay, GuaranteeCodes } from '@whitbread-eos/api';

import type { Props } from './BookingDetailsReservationInformation.component';
import BookingDetailsReservationInformationComponent, {
  getPaymentTypeText,
} from './BookingDetailsReservationInformation.component';

const props = {
  sourcePms: 'OPERA',
  rateType: 'Flex',
  reasonForStay: PurposeOfStay.BUSINESS,
  distBookingChannel: 'dist',
  gdsReferenceNumber: 'gds',
  isRepeatBooking: true,
  t: (value: string) => value,
} as Props;

describe('BookingDetailsReservationInformation', () => {
  it('it should render the BookingDetailsReservationInformation and render Opera as source', () => {
    props.sourcePms = 'Opera';
    const { getByText } = render(<BookingDetailsReservationInformationComponent {...props} />);

    expect(getByText('Opera')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsReservationInformation and render Standard as rate type', () => {
    props.rateType = 'Standard';
    const { getByText } = render(<BookingDetailsReservationInformationComponent {...props} />);

    expect(getByText(': Standard')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsReservationInformation with a bart reservation', () => {
    props.isBart = true;
    const { getByText } = render(<BookingDetailsReservationInformationComponent {...props} />);

    expect(getByText(': Standard')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsReservationInformation with Purpose of stay business', () => {
    const { getByText } = render(<BookingDetailsReservationInformationComponent {...props} />);

    expect(getByText(': booking.reason.business')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsReservationInformation with Purpose of stay leisure', () => {
    props.reasonForStay = PurposeOfStay.LEISURE;
    const { getByText } = render(<BookingDetailsReservationInformationComponent {...props} />);

    expect(getByText(': booking.reason.leisure')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsReservationInformation with distBookingChannel', () => {
    const { getByText } = render(<BookingDetailsReservationInformationComponent {...props} />);

    expect(getByText(': Dist')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsReservationInformation with gdsReference', () => {
    const { getByText } = render(<BookingDetailsReservationInformationComponent {...props} />);

    expect(getByText(': Gds')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsReservationInformation with repeatBooking false', () => {
    props.isRepeatBooking = false;
    const { getByText } = render(<BookingDetailsReservationInformationComponent {...props} />);

    expect(getByText(': Gds').style).toHaveProperty('fontSize', '');
  });
});

describe('getPaymentTypeText', () => {
  const t = (id: string) => id;

  it('returns accountToCompany for ACCOUNT_TO_COMPANY guarantee code', () => {
    expect(getPaymentTypeText(GuaranteeCodes.ACCOUNT_TO_COMPANY, undefined, t)).toBe(
      'ccui.manageBooking.paymentType.accountToCompany'
    );
  });

  it('returns nonGuaranteed for RESERVE_WITHOUT_CARD guarantee code', () => {
    expect(getPaymentTypeText(GuaranteeCodes.RESERVE_WITHOUT_CARD, undefined, t)).toBe(
      'ccui.manageBooking.paymentType.nonGuaranteed'
    );
  });

  it('returns PIBA for BU and BD payment methods', () => {
    expect(getPaymentTypeText('', 'BU', t)).toBe('ccui.manageBooking.paymentType.PIBA');
    expect(getPaymentTypeText('', 'BD', t)).toBe('ccui.manageBooking.paymentType.PIBA');
  });

  it('returns Paypal for PP payment method', () => {
    expect(getPaymentTypeText('', 'PP', t)).toBe('ccui.manageBooking.paymentType.Paypal');
    expect(getPaymentTypeText('', 'DPP', t)).toBe('ccui.manageBooking.paymentType.Paypal');
  });

  it('returns creditDebit for all credit/debit codes', () => {
    const codes = ['VA', 'CVA', 'MC', 'DMC', 'AX', 'DVA'];
    codes.forEach((code) => {
      expect(getPaymentTypeText('', code, t)).toBe('ccui.manageBooking.paymentType.creditDebit');
    });
  });

  it('returns other for unknown payment method', () => {
    expect(getPaymentTypeText('', 'XYZ', t)).toBe('ccui.manageBooking.paymentType.other');
  });

  it('returns other for null paymentType and paymentMethod', () => {
    expect(getPaymentTypeText(null as any, null as any, t)).toBe(
      'ccui.manageBooking.paymentType.other'
    );
  });

  it('returns other for undefined paymentType and paymentMethod', () => {
    expect(getPaymentTypeText(undefined as any, undefined as any, t)).toBe(
      'ccui.manageBooking.paymentType.other'
    );
  });
});
