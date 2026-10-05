import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import TotalCost from './TotalCost.component';

const mockTotalCostData = {
  totalCostAmount: 118,
  currency: 'GBP',
  language: 'en',
  cityTaxTotal: 5,
  isCityTaxBreakdownEnabled: true,
  donation: {
    description: 'On Line Charity Pledge £1',
    unitPrice: 1,
    totalQuantity: 1,
    computedPrice: 1,
  },
  selectedPaymentOption: 'PAY_ON_ARRIVAL',
  t: (key: string) => {
    switch (key) {
      case 'booking.confirmation.paymentTaken':
        return 'Thank you, prepayment has been taken';
      case 'ccui.booking.confirmation.paymentTaken':
        return 'Thank you, your prepayment has been taken.';
      case 'booking.confirmation.reservedWithCard':
        return 'Thank you, your payment will be taken on arrival';
      case 'ccui.booking.confirmation.reservedWithCard':
        return 'Thank you, your payment will be taken on arrival.';
      case 'booking.confirmation.reservedWithoutCard':
        return 'Thank you for your booking. We are looking forward to your visit!';
      case 'ccui.booking.confirmation.reservedWithoutCard':
        return 'Thank you for your booking. We are looking forward to your visit!';
      case 'ccui.booking.confirmation.accountToCompany':
        return 'Thank you. The payment will be invoiced to the company.';
      case 'booking.confirmation.paymentSummary':
        return 'Payment summary';
      default:
        return key;
    }
  },
  isCCUI: false,
  taxesMessage: 'Price includes taxes and fees',
};

describe('TotalCost', () => {
  it('should render total cost component correctly', () => {
    const { getByTestId } = render(<TotalCost {...mockTotalCostData} cityTaxTotal={0} />);
    expect(getByTestId('TotalCostConfirm-TaxesMessage')).toBeInTheDocument();
    expect(getByTestId('TotalCostConfirm-container')).toBeInTheDocument();
  });

  it.each`
    currency | language | symbol
    ${'GBP'} | ${'en'}  | ${'£'}
    ${'GBP'} | ${'de'}  | ${'£'}
    ${'EUR'} | ${'en'}  | ${'€'}
    ${'EUR'} | ${'de'}  | ${'€'}
  `(
    'total cost shows symbol "$symbol" for currency "$currency" and language "$language"',
    ({ currency, language, symbol }) => {
      mockTotalCostData.currency = currency;
      mockTotalCostData.language = language;
      const { getByTestId } = render(<TotalCost {...mockTotalCostData} />);
      expect(getByTestId('TotalCostConfirm-amount')).toHaveTextContent(symbol);
    }
  );

  it.each`
    paymentOption             | message
    ${'PAY_NOW'}              | ${'Thank you, prepayment has been taken'}
    ${'PAY_ON_ARRIVAL'}       | ${'Thank you, your payment will be taken on arrival'}
    ${'RESERVE_WITHOUT_CARD'} | ${'Thank you for your booking. We are looking forward to your visit!'}
    ${'ACCOUNT_COMPANY'}      | ${'Thank you. The payment will be invoiced to the company.'}
    ${'default'}              | ${'default'}
  `(
    'total cost shows for "$paymentOption" and CCUI = false the message "$message"',
    ({ paymentOption, message }) => {
      mockTotalCostData.selectedPaymentOption = paymentOption;

      const { getByTestId } = render(<TotalCost {...mockTotalCostData} />);
      expect(getByTestId('TotalCostConfirm-paymentMessage')).toHaveTextContent(message);
    }
  );

  it.each`
    paymentOption             | message
    ${'PAY_NOW'}              | ${'Thank you, your prepayment has been taken.'}
    ${'PAY_ON_ARRIVAL'}       | ${'Thank you, your payment will be taken on arrival.'}
    ${'RESERVE_WITHOUT_CARD'} | ${'Thank you for your booking. We are looking forward to your visit!'}
    ${'ACCOUNT_COMPANY'}      | ${'Thank you. The payment will be invoiced to the company.'}
    ${'default'}              | ${'default'}
  `(
    'total cost shows for "$paymentOption" and CCUI = true the message "$message"',
    ({ paymentOption, message }) => {
      mockTotalCostData.selectedPaymentOption = paymentOption;
      mockTotalCostData.isCCUI = true;
      const { getByTestId } = render(<TotalCost {...mockTotalCostData} />);
      expect(getByTestId('TotalCostConfirm-paymentMessage')).toHaveTextContent(message);
    }
  );

  it('should render Payment Summary tax cost component correctly', () => {
    const { getByTestId } = render(<TotalCost {...mockTotalCostData} />);
    const paymentSummaryLabel = getByTestId('paymentSummary-label');
    expect(paymentSummaryLabel).toBeInTheDocument();
  });
});
