import '@testing-library/jest-dom/extend-expect';
import { render } from '@testing-library/react';
import { RegistrationRole, Scheme } from '@whitbread-eos/api';

import CardList from './card-list';

const mockAccount = {
  tetheredGuid: '73379a1f-7b4c-4d81-b059-c85f8db8dded',
  accountName: 'whibtread Digital',
  accountNumber: '3089503200100352',
  registrationRoles: ['ACCOUNT_HOLDER', 'CARD_HOLDER'] as RegistrationRole[],
  scheme: 'GB' as Scheme,
};

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  getCountryLanguageByLocale: jest.fn().mockReturnValue({ language: 'en' }),
  formatIBAssetsUrl: jest.fn((key: string) => `/${key}`),
  getPathForLocale: jest.fn((locale: string, url: string) => `/${locale}/${url}`),
  useTranslation: () => ({ t: (key: string) => key }),
  getAccountRegistrationRoleDetails: jest.fn((account) => ({
    isOnlyCardHolder:
      account?.registrationRoles?.length === 1 &&
      account?.registrationRoles?.includes('CARD_HOLDER'),
    isOnlyFinanceUser:
      account?.registrationRoles?.length === 1 &&
      account?.registrationRoles?.includes('FINANCE_USER'),
    isOnlyCostCenter:
      account?.registrationRoles?.length === 1 &&
      account?.registrationRoles?.includes('COST_CENTRE_USER'),
    isCardHolderAndFinanceUser:
      account?.registrationRoles?.length === 2 &&
      account?.registrationRoles?.includes('CARD_HOLDER') &&
      account?.registrationRoles?.includes('FINANCE_USER'),
  })),
}));

describe('InnBusinessPayCardList', () => {
  it('renders the CardList component', async () => {
    const { getByText } = render(<CardList {...{ account: mockAccount }} />);

    expect(getByText('spending.summary.reports')).toBeInTheDocument();
    expect(getByText('spending.summary.reports.description')).toBeInTheDocument();

    expect(getByText('spending.summary.create.reports')).toBeInTheDocument();
    expect(getByText('spending.summary.create.reports.description')).toBeInTheDocument();

    expect(getByText('spending.summary.scheduled.reports')).toBeInTheDocument();
    expect(getByText('spending.summary.scheduled.reports.description')).toBeInTheDocument();

    expect(getByText('spending.summary.linked.accounts')).toBeInTheDocument();
    expect(getByText('spending.summary.linked.accounts.description')).toBeInTheDocument();

    expect(getByText('spending.summary.statement.invoice')).toBeInTheDocument();
    expect(getByText('spending.summary.statement.invoice.description')).toBeInTheDocument();

    expect(getByText('spending.summary.transactions')).toBeInTheDocument();
    expect(getByText('spending.summary.transactions.description')).toBeInTheDocument();
  });

  it('renders the correct icons', async () => {
    const { getByAltText } = render(<CardList {...{ account: mockAccount }} />);

    expect(getByAltText('spending.summary.reports icon')).toHaveAttribute('src');

    expect(getByAltText('spending.summary.create.reports icon')).toHaveAttribute('src');

    expect(getByAltText('spending.summary.scheduled.reports icon')).toHaveAttribute('src');

    expect(getByAltText('spending.summary.linked.accounts icon')).toHaveAttribute('src');

    expect(getByAltText('spending.summary.statement.invoice icon')).toHaveAttribute('src');

    expect(getByAltText('spending.summary.transactions icon')).toHaveAttribute('src');
  });

  it('renders only card holder-specific cards when isOnlyCardHolder is true', async () => {
    const { queryByText } = render(
      <CardList
        {...{ account: { ...mockAccount, registrationRoles: [RegistrationRole.CardHolder] } }}
      />
    );

    expect(queryByText('spending.summary.reports')).toBeInTheDocument();
    expect(queryByText('spending.summary.create.reports')).toBeInTheDocument();
    expect(queryByText('spending.summary.scheduled.reports')).toBeInTheDocument();
    expect(queryByText('spending.summary.transactions')).toBeInTheDocument();

    expect(queryByText('spending.summary.linked.accounts')).not.toBeInTheDocument();
    expect(queryByText('spending.summary.statement.invoice')).not.toBeInTheDocument();
  });

  it('renders only card holder-specific cards when is cost center is true', async () => {
    const { queryByText } = render(
      <CardList
        {...{ account: { ...mockAccount, registrationRoles: [RegistrationRole.CostCentreUser] } }}
      />
    );

    expect(queryByText('spending.summary.reports')).toBeInTheDocument();
    expect(queryByText('spending.summary.create.reports')).toBeInTheDocument();
    expect(queryByText('spending.summary.scheduled.reports')).toBeInTheDocument();
    expect(queryByText('spending.summary.transactions')).toBeInTheDocument();
    expect(queryByText('spending.summary.statement.invoice')).toBeInTheDocument();

    expect(queryByText('spending.summary.linked.accounts')).not.toBeInTheDocument();
  });
  it('renders the statement and invoice card with internal link when isStatementsInvoicesOn is true', async () => {
    const { getByText } = render(
      <CardList
        {...{
          account: mockAccount,
          isStatementsInvoicesOn: true,
          locale: 'en',
          isTransactionsOn: true,
        }}
      />
    );

    const statementInvoiceCard = getByText('spending.summary.statement.invoice');
    expect(statementInvoiceCard).toBeInTheDocument();
    expect(statementInvoiceCard.closest('a')).toHaveAttribute(
      'href',
      '/en/spending/statements?account=73379a1f-7b4c-4d81-b059-c85f8db8dded'
    );
  });

  it('renders the statement and invoice card with external link when isStatementsInvoicesOn is false', async () => {
    const { getByText } = render(
      <CardList
        {...{
          account: mockAccount,
          isStatementsInvoicesOn: false,
        }}
      />
    );

    const statementInvoiceCard = getByText('spending.summary.statement.invoice');
    expect(statementInvoiceCard).toBeInTheDocument();
    expect(statementInvoiceCard.closest('a')).toHaveAttribute('href', 'Statements.aspx');
  });

  it('renders the transactions card with internal link when isTransactionsOn is true', async () => {
    const { getByText } = render(
      <CardList
        {...{
          account: mockAccount,
          isTransactionsOn: true,
          locale: 'en',
        }}
      />
    );

    const transactionsCard = getByText('spending.summary.transactions');
    expect(transactionsCard).toBeInTheDocument();
    expect(transactionsCard.closest('a')).toHaveAttribute(
      'href',
      '/en/spending/transactions?account=73379a1f-7b4c-4d81-b059-c85f8db8dded'
    );
  });

  it('renders the transactions card with external link when isTransactionsOn is false', async () => {
    const { getByText } = render(
      <CardList
        {...{
          account: mockAccount,
          isTransactionsOn: false,
        }}
      />
    );

    const transactionsCard = getByText('spending.summary.transactions');
    expect(transactionsCard).toBeInTheDocument();
    expect(transactionsCard.closest('a')).toHaveAttribute('href', 'Transactions.aspx');
  });

  it('renders the correct cards for PIBA Euro accounts', async () => {
    const { getByText, queryByText, getAllByDisplayValue } = render(
      <CardList
        {...{
          account: { ...mockAccount, scheme: 'DE' as Scheme },
          isPIBAEuro: true,
        }}
      />
    );

    expect(getAllByDisplayValue('ReportsList.aspx')).toHaveLength(2);

    expect(getByText('spending.summary.reports')).toBeInTheDocument();
    expect(getByText('spending.summary.reports.description')).toBeInTheDocument();

    expect(getByText('spending.summary.create.reports')).toBeInTheDocument();
    expect(getByText('spending.summary.create.reports.description')).toBeInTheDocument();

    expect(getByText('spending.summary.scheduled.reports')).toBeInTheDocument();
    expect(getByText('spending.summary.scheduled.reports.description')).toBeInTheDocument();

    expect(queryByText('spending.summary.linked.accounts')).not.toBeInTheDocument();
    expect(queryByText('spending.summary.linked.accounts.description')).not.toBeInTheDocument();

    expect(getByText('spending.summary.statement.invoice')).toBeInTheDocument();
    expect(getByText('spending.summary.statement.invoice.description')).toBeInTheDocument();

    expect(getByText('spending.summary.transactions')).toBeInTheDocument();
    expect(getByText('spending.summary.transactions.description')).toBeInTheDocument();
  });
});
