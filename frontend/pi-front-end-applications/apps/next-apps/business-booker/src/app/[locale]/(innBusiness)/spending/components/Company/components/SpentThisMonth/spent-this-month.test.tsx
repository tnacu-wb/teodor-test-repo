import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { Currency, LOCALES } from '@whitbread-eos/api';

import { SpentThisMonth } from './spent-this-month';

const mockProps = {
  locale: LOCALES.EN,
  dataTestId: 'test123',
  currencyOrder: [Currency.GBP_NAME, Currency.EUR_NAME],
  currencyTotals: {
    GBP: 7000,
    EUR: 5735.92,
  },
  selectedCurrency: Currency.GBP_NAME,
  onSelectCurrency: jest.fn(),
};

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => {
    return {
      t: (str: string) => str,
    };
  },
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('SpentThisMonth', () => {
  it('should render the SpentThisMonth component', async () => {
    render(<SpentThisMonth {...mockProps} />);
    const container = await screen.findByTestId('test123-Spent-This-Month');
    expect(container).toBeInTheDocument();
  });

  it('should render both currency cards', async () => {
    render(<SpentThisMonth {...mockProps} />);
    expect(screen.getByTestId('test123-Spent-This-Month-Card-GBP')).toBeInTheDocument();
    expect(screen.getByTestId('test123-Spent-This-Month-Card-EUR')).toBeInTheDocument();
  });
});
