import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES, Scheme } from '@whitbread-eos/api';

import { StatementsTable } from './statements-table';

const mockProps = {
  locale: LOCALES.EN,
  token: {} as string,
  account: {
    tetheredUserGuid: '123',
    schemeCustomerId: 123,
    scheme: 'GB' as Scheme,
  },
  icons: { icon: 'test' },
};

const mockResponse = {
  response: {
    invoices: Array.from({ length: 20 }).map((_, i) => ({
      statementDate: `2025-${String(3 - Math.floor(i / 10)).padStart(2, '0')}-${String(
        24 - (i % 10)
      ).padStart(2, '0')}`,
      invoiceNo: `${123 + i}`,
      broughtForward: { amount: 37 + i, currencyCode: 'GBP', currencySymbol: '£' },
      paymentsReceived: { amount: 37 + i, currencyCode: 'GBP', currencySymbol: '£' },
      overdueBalance: { amount: i, currencyCode: 'GBP', currencySymbol: '£' },
      invoiceValue: { amount: 139 + i, currencyCode: 'GBP', currencySymbol: '£' },
      statementBalance: { amount: 139 + i, currencyCode: 'GBP', currencySymbol: '£' },
      fileAutoID: 123 + i,
    })),
  },
  pagingResult: { toRecord: 5, totalRecordCount: 20, fromRecord: 1, lastPage: 4 },
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCountryLanguageByLocale: jest.fn(() => ({ language: 'en' })),
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getSearchParams: () => Promise.resolve(new URLSearchParams()),
    viewCustomerInvoices: () => mockResponse,
    getCommonIcons: () => ({}),
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

jest.mock('~components/innBusiness/DataTable', () => {
  return {
    DataTable: ({ columns }: any) => {
      columns.forEach((column: any) => {
        if (column.render) {
          column.render('', {});
        }
      });
      return null;
    },
  };
});

describe('StatementsTable Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render StatementsTable component', async () => {
    const { getByTestId } = render(await StatementsTable(mockProps));

    expect(getByTestId('StatementsTable-container')).toBeInTheDocument();
  });

  it('should set invoices count correctly in Analytics component', async () => {
    render(await StatementsTable(mockProps));

    expect(window.analyticsData.innBusiness?.invoices).toBe(20);
  });
});
