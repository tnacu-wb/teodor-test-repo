import { CustomerAccountDetails, LOCALES } from '@whitbread-eos/api';
import { getTranslations, getCountryLanguageByLocale } from '@whitbread-eos/utils/server';
import { viewCustomerInvoices, getSearchParams } from '@whitbread-eos/utils/server';

import {
  DataTable,
  DataTableCell,
  DataTableColumn,
  DataTableRow,
} from '~components/innBusiness/DataTable';

import { formatAmount, getCurrencyCodeBasedOnCurrencySymbol } from '../../utils/format-amount';
import Analytics from '../Analytics/analytics';
import { DownloadStatementsButtons } from '../DownloadStatementsButtons/download-statements-buttons';
import { NoResultsStatements } from '../NoResultsStatements/no-results-statements';

type Props = {
  locale: LOCALES;
  token: string;
  account: CustomerAccountDetails;
  icons: Record<string, string>;
};

export async function StatementsTable({ locale, token, account, icons }: Props) {
  const baseDataTestId = 'StatementsTable';
  const { language } = getCountryLanguageByLocale(locale);

  const [{ t }, searchParams] = await Promise.all([
    getTranslations(language, ['spending']),
    getSearchParams(),
  ]);

  const today = new Date();
  const currentDate = today.toISOString().slice(0, 10);
  const threeYearsAgo = new Date(today);
  threeYearsAgo.setFullYear(today.getFullYear() - 3);
  const threeYearsAgoDate = threeYearsAgo.toISOString().slice(0, 10);

  const pageOneInvoicesResponse = await viewCustomerInvoices(
    token,
    account,
    threeYearsAgoDate,
    currentDate,
    1,
    15
  );

  const totalInvoicesNumber =
    pageOneInvoicesResponse && pageOneInvoicesResponse?.pagingResult?.totalRecordCount;

  const getTotalInvoicesCount = async () => totalInvoicesNumber;
  const getCurrentPageInvoices = async (pageIndex: number) => {
    if (pageIndex === 1) {
      return pageOneInvoicesResponse?.response?.invoices;
    }

    const result = await viewCustomerInvoices(
      token,
      account,
      threeYearsAgoDate,
      currentDate,
      pageIndex,
      15
    );

    return result?.response?.invoices || [];
  };

  const renderRowAmount = (row: DataTableRow, field: string) => {
    const rowAmount = row?.[field]?.amount;
    const rowCurrencySymbol = row?.[field]?.currencySymbol;
    return formatAmount(rowAmount, getCurrencyCodeBasedOnCurrencySymbol(rowCurrencySymbol), locale);
  };

  const columns: DataTableColumn[] = [
    {
      id: 'statementDate',
      label: t('spending.statementsInvoicesPayments.statements.table.header.date'),
      headerClassName: dateLabelHeaderStyle,
      render(_cell: DataTableCell, row: DataTableRow) {
        let formattedDate = '';

        if (row?.statementDate) {
          const [year, month, day] = row.statementDate.split('-');
          formattedDate = `${day}/${month}/${year.slice(-2)}`;
        }

        return formattedDate;
      },
    },
    {
      id: 'invoiceNo',
      label: t('spending.statementsInvoicesPayments.statements.table.header.invoiceNumber'),
      headerClassName: desktopOnlyStyle,
      className: desktopOnlyStyle,
    },
    {
      id: 'broughtForward',
      label: t('spending.statementsInvoicesPayments.statements.table.header.broughtForward'),
      headerClassName: desktopOnlyStyleTextRight,
      className: desktopOnlyStyleTextRight,
      render(_cell: DataTableCell, row: DataTableRow) {
        return renderRowAmount(row, 'broughtForward');
      },
    },
    {
      id: 'paymentsReceived',
      label: t('spending.statementsInvoicesPayments.statements.table.header.paid'),
      headerClassName: desktopOnlyStyleTextRight,
      className: desktopOnlyStyleTextRight,
      render(_cell: DataTableCell, row: DataTableRow) {
        return renderRowAmount(row, 'paymentsReceived');
      },
    },
    {
      id: 'overdueBalance',
      label: t('spending.statementsInvoicesPayments.statements.table.header.balanceDue'),
      headerClassName: desktopOnlyStyleTextRight,
      className: desktopOnlyStyleTextRight,
      render(_cell: DataTableCell, row: DataTableRow) {
        return renderRowAmount(row, 'overdueBalance');
      },
    },
    {
      id: 'invoiceValue',
      label: t('spending.statementsInvoicesPayments.statements.table.header.invoiceValue'),
      headerClassName: desktopOnlyStyleTextRight,
      className: desktopOnlyStyleTextRight,
      render(_cell: DataTableCell, row: DataTableRow) {
        return renderRowAmount(row, 'invoiceValue');
      },
    },
    {
      id: 'statementBalance',
      label: t('spending.statementsInvoicesPayments.statements.table.header.statementValue'),
      headerClassName: statementBalanceStyle,
      className: statementBalanceStyle,
      render(_cell: DataTableCell, row: DataTableRow) {
        return renderRowAmount(row, 'statementBalance');
      },
    },
    {
      id: 'statementDate',
      label: t('spending.statementsInvoicesPayments.statements.table.header.download'),
      headerClassName: downloadLabelHeaderStyle,
      render(_cell: DataTableCell, row: DataTableRow) {
        let formattedDate = '';

        if (row?.statementDate) {
          formattedDate = row.statementDate.replace(/-/g, '');
        }

        return (
          <DownloadStatementsButtons
            icons={icons}
            token={token}
            account={account}
            fileAutoID={row?.fileAutoID}
            statementDate={formattedDate}
            invoiceNo={row?.invoiceNo}
          />
        );
      },
    },
  ];

  return (
    <div data-testid={`${baseDataTestId}-container`}>
      <DataTable
        pageIndex={searchParams?.get('pageIndex')}
        columns={columns}
        getPage={getCurrentPageInvoices}
        getTotal={getTotalInvoicesCount}
        noResultsComponent={<NoResultsStatements />}
      />
      <Analytics invoices={totalInvoicesNumber} />
    </div>
  );
}

const desktopOnlyStyle = 'mobile:hidden tablet:w-full desktop:w-full';
const dateLabelHeaderStyle = 'mobile:w-full tablet:w-full desktop:w-full';
const downloadLabelHeaderStyle = 'mobile:w-full tablet:w-full desktop:w-full text-center';
const desktopOnlyStyleTextRight = 'mobile:hidden tablet:w-full desktop:w-full text-right';
const statementBalanceStyle = 'mobile:w-full tablet:w-full desktop:w-full text-right';
