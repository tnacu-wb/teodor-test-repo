import '@testing-library/jest-dom';
import { act, render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { CompanySpendingClient } from './company-spending-client';

const mockGetMonthCurrencyTotals = jest.fn();
const mockGetCurrencyOrder = jest.fn();

jest.mock('../utils/spending-currency', () => ({
  getMonthCurrencyTotals: (...args: unknown[]) => mockGetMonthCurrencyTotals(...args),
  getCurrencyOrder: (...args: unknown[]) => mockGetCurrencyOrder(...args),
}));

let spentThisMonthProps: any;
let spendOverTimeProps: any;

jest.mock('../SpendOverTime/spend-over-time', () => ({
  SpendOverTime: (props: any) => {
    spendOverTimeProps = props;
    return <div data-testid="SpendOverTime" />;
  },
}));

jest.mock('./components/SpentThisMonth/spent-this-month', () => ({
  SpentThisMonth: (props: any) => {
    spentThisMonthProps = props;
    return <div data-testid="SpentThisMonth" />;
  },
}));

describe('CompanySpendingClient', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    spentThisMonthProps = undefined;
    spendOverTimeProps = undefined;
  });

  it('passes computed currency totals and order, and defaults selected currency', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 100, EUR: 50 });
    mockGetCurrencyOrder.mockReturnValue(['GBP', 'EUR']);

    render(
      <CompanySpendingClient
        locale={LOCALES.EN}
        dataTestId="CompanyTab"
        spending={[]}
        icons={{
          'icon.flag.country.eu': '/eu-flag.svg',
          'icon.flag.country.gb': '/gb-flag.svg',
        }}
        defaultCurrencyCode="GBP"
        showSpendingTooltip
      />
    );

    expect(spentThisMonthProps.currencyTotals).toEqual({ GBP: 100, EUR: 50 });
    expect(spentThisMonthProps.currencyOrder).toEqual(['GBP', 'EUR']);
    expect(spentThisMonthProps.selectedCurrency).toBe('GBP');
    expect(spentThisMonthProps.icons).toEqual({
      'icon.flag.country.eu': '/eu-flag.svg',
      'icon.flag.country.gb': '/gb-flag.svg',
    });

    expect(spendOverTimeProps.selectedCurrency).toBe('GBP');
    expect(spendOverTimeProps.defaultCurrencyCode).toBe('GBP');
  });

  it('updates selected currency when user selects another currency', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 100, EUR: 50 });
    mockGetCurrencyOrder.mockReturnValue(['GBP', 'EUR']);

    render(
      <CompanySpendingClient
        locale={LOCALES.EN}
        dataTestId="CompanyTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    act(() => {
      spentThisMonthProps.onSelectCurrency('EUR');
    });

    expect(spendOverTimeProps.selectedCurrency).toBe('EUR');
  });

  it('resets selected currency when currency order changes', () => {
    mockGetMonthCurrencyTotals
      .mockReturnValueOnce({ GBP: 100, EUR: 0 })
      .mockReturnValueOnce({ GBP: 0, EUR: 200 });
    mockGetCurrencyOrder.mockReturnValueOnce(['GBP', 'EUR']).mockReturnValueOnce(['EUR', 'GBP']);

    const { rerender } = render(
      <CompanySpendingClient
        locale={LOCALES.EN}
        dataTestId="CompanyTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spendOverTimeProps.selectedCurrency).toBe('GBP');

    rerender(
      <CompanySpendingClient
        locale={LOCALES.EN}
        dataTestId="CompanyTab"
        spending={[{ year: 2025, month: '1', bookingValue: 200, bookingCurrency: 'EUR' } as any]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spendOverTimeProps.selectedCurrency).toBe('EUR');
  });

  it('defaults showSpendingTooltip to true when not provided', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 0, EUR: 0 });
    mockGetCurrencyOrder.mockReturnValue(['GBP', 'EUR']);

    render(
      <CompanySpendingClient
        locale={LOCALES.EN}
        dataTestId="CompanyTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spendOverTimeProps.showSpendingTooltip).toBe(true);
  });
});
