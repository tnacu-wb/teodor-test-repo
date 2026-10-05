import '@testing-library/jest-dom';
import { act, render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { YourSpendingClient } from './your-spending-client';

const mockGetMonthCurrencyTotals = jest.fn();
const mockGetCurrencyOrder = jest.fn();

jest.mock('../utils/spending-currency', () => ({
  getMonthCurrencyTotals: (...args: unknown[]) => mockGetMonthCurrencyTotals(...args),
  getCurrencyOrder: (...args: unknown[]) => mockGetCurrencyOrder(...args),
}));

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (str: string) => str,
  }),
}));

let spentThisMonthProps: any;
let spendOverTimeProps: any;

jest.mock('../SpendOverTime/spend-over-time', () => ({
  SpendOverTime: (props: any) => {
    spendOverTimeProps = props;
    return <div data-testid="SpendOverTime" />;
  },
}));

jest.mock('../Company/components/SpentThisMonth/spent-this-month', () => ({
  SpentThisMonth: (props: any) => {
    spentThisMonthProps = props;
    return <div data-testid="SpentThisMonth" />;
  },
}));

describe('YourSpendingClient', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    spentThisMonthProps = undefined;
    spendOverTimeProps = undefined;
  });

  it('passes computed currency totals and order, and defaults selected currency', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 100, EUR: 50 });
    mockGetCurrencyOrder.mockReturnValue(['GBP', 'EUR']);

    render(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
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
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
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
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spendOverTimeProps.selectedCurrency).toBe('GBP');

    rerender(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
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
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spendOverTimeProps.showSpendingTooltip).toBe(true);
  });

  it('passes tooltipText with the correct translation key', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 100, EUR: 50 });
    mockGetCurrencyOrder.mockReturnValue(['GBP', 'EUR']);

    render(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spendOverTimeProps.tooltipText).toBe('spending.reporting.employee.tooltip.text');
  });

  it('passes showSpendingTooltip as false when explicitly disabled', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 100 });
    mockGetCurrencyOrder.mockReturnValue(['GBP']);

    render(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
        showSpendingTooltip={false}
      />
    );

    expect(spendOverTimeProps.showSpendingTooltip).toBe(false);
  });

  it('passes locale to both child components', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({ EUR: 250 });
    mockGetCurrencyOrder.mockReturnValue(['EUR']);

    render(
      <YourSpendingClient
        locale={LOCALES.DE}
        dataTestId="YourSpendingTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="EUR"
      />
    );

    expect(spentThisMonthProps.locale).toBe(LOCALES.DE);
    expect(spendOverTimeProps.locale).toBe(LOCALES.DE);
  });

  it('passes icons to both child components', () => {
    const mockIcons = {
      'icon.flag.country.gb': '/gb-flag.svg',
      'icon.flag.country.de': '/de-flag.svg',
    };
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 100 });
    mockGetCurrencyOrder.mockReturnValue(['GBP']);

    render(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
        spending={[]}
        icons={mockIcons}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spentThisMonthProps.icons).toEqual(mockIcons);
    expect(spendOverTimeProps.icons).toEqual(mockIcons);
  });

  it('passes dataTestId to both child components', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 100 });
    mockGetCurrencyOrder.mockReturnValue(['GBP']);

    render(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="CustomTestId"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spentThisMonthProps.dataTestId).toBe('CustomTestId');
    expect(spendOverTimeProps.dataTestId).toBe('CustomTestId');
  });

  it('passes spending data to SpendOverTime', () => {
    const mockSpending = [
      { year: 2025, month: '4', bookingValue: 500, bookingCurrency: 'GBP' },
      { year: 2025, month: '3', bookingValue: 300, bookingCurrency: 'EUR' },
    ];
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 500, EUR: 300 });
    mockGetCurrencyOrder.mockReturnValue(['GBP', 'EUR']);

    render(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
        spending={mockSpending}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spendOverTimeProps.spending).toEqual(mockSpending);
  });

  it('falls back to defaultCurrencyCode when currencyOrder is empty', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({});
    mockGetCurrencyOrder.mockReturnValue([]);

    render(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="EUR"
      />
    );

    expect(spentThisMonthProps.selectedCurrency).toBe('EUR');
    expect(spendOverTimeProps.selectedCurrency).toBe('EUR');
  });

  it('handles empty currency totals', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({});
    mockGetCurrencyOrder.mockReturnValue([]);

    render(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(spentThisMonthProps.currencyTotals).toEqual({});
    expect(spentThisMonthProps.currencyOrder).toEqual([]);
  });

  it('renders both SpentThisMonth and SpendOverTime components', () => {
    mockGetMonthCurrencyTotals.mockReturnValue({ GBP: 100 });
    mockGetCurrencyOrder.mockReturnValue(['GBP']);

    const { getByTestId } = render(
      <YourSpendingClient
        locale={LOCALES.EN}
        dataTestId="YourSpendingTab"
        spending={[]}
        icons={{}}
        defaultCurrencyCode="GBP"
      />
    );

    expect(getByTestId('SpentThisMonth')).toBeInTheDocument();
    expect(getByTestId('SpendOverTime')).toBeInTheDocument();
  });
});
