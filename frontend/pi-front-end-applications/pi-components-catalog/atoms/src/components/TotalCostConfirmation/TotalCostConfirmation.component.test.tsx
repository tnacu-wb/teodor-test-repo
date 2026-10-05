import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import TotalCostConfirmation from './TotalCostConfirmation.component';

const mockTotalCostData = {
  totalCostAmount: 118,
  currency: 'GBP',
  language: 'en',
  t: (key: string) => {
    if (key === 'booking.confirmation.totalCost') {
      return 'booking.confirmation.totalCost';
    } else {
      return 'Translation';
    }
  },
};

describe('TotalCost', () => {
  it('should render total cost component correctly', () => {
    const { getByTestId } = render(<TotalCostConfirmation {...mockTotalCostData} />);
    expect(getByTestId('confirmation-totalCost')).toBeInTheDocument();
  });

  it("doesn't render anything if there is no data", () => {
    const { container } = render(
      <TotalCostConfirmation {...{ ...mockTotalCostData, totalCostAmount: undefined }} />
    );

    expect(container.firstChild).toBeNull();
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
      const { getByTestId } = render(<TotalCostConfirmation {...mockTotalCostData} />);
      expect(getByTestId('totalCost-amount')).toHaveTextContent(symbol);
    }
  );
});
