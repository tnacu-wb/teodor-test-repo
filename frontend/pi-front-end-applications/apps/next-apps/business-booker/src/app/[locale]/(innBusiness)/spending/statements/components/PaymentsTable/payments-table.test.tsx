import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { getAccountPayments as mockGetAccountPayments } from '@whitbread-eos/utils/server';

import { PaymentsTable } from './payments-table';
import mockPaymentsTableClient from './payments-table-client';

jest.mock('@whitbread-eos/utils/server', () => ({
  getAccountPayments: jest.fn(),
  getCountryLanguageByLocale: jest.fn(() => ({ language: 'en' })),
  getCommonIcons: jest.fn(() => ({})),
}));

jest.mock('./payments-table-client', () => ({
  __esModule: true,
  default: jest.fn(() => <div data-testid="PaymentsTableClient" />),
}));

const mockToken = 'test-token';
const mockAccount = {
  accountNumber: '123456',
  tetheredGuid: 'guid-123',
} as any;
const mockLocale = LOCALES.EN;

describe('PaymentsTable', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders PaymentsTableClient with correct props and payments', async () => {
    const mockPayments = [{ id: 1 }, { id: 2 }];
    mockGetAccountPayments.mockResolvedValueOnce({ payments: mockPayments });

    render(
      await PaymentsTable({
        token: mockToken,
        account: mockAccount,
        locale: mockLocale,
      })
    );

    await waitFor(() => {
      expect(screen.getByTestId('PaymentsTable-Container')).toBeInTheDocument();
      expect(screen.getByTestId('PaymentsTableClient')).toBeInTheDocument();
    });

    expect(mockGetAccountPayments).toHaveBeenCalledWith(
      mockToken,
      mockAccount.accountNumber,
      1,
      16,
      mockAccount.tetheredGuid
    );

    expect(mockPaymentsTableClient).toHaveBeenCalledWith(
      expect.objectContaining({
        baseDataTestId: 'PaymentsTable',
        initialItems: mockPayments,
        token: mockToken,
        account: mockAccount,
        pageSize: 15,
        locale: mockLocale,
      }),
      undefined
    );
  });

  it('renders with empty payments if getAccountPayments returns undefined', async () => {
    mockGetAccountPayments.mockResolvedValueOnce(undefined);

    render(
      await PaymentsTable({
        token: mockToken,
        account: mockAccount,
        locale: mockLocale,
      })
    );

    await waitFor(() => {
      expect(screen.getByTestId('PaymentsTable-Container')).toBeInTheDocument();
      expect(screen.getByTestId('PaymentsTableClient')).toBeInTheDocument();
    });

    expect(mockPaymentsTableClient).toHaveBeenCalledWith(
      expect.objectContaining({
        initialItems: [],
      }),
      undefined
    );
  });
});
