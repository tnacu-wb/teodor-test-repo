'use client';

import { LOCALES } from '@whitbread-eos/api';
import { Table, TableHeader, TableRow, TableHead } from '@whitbread-eos/atoms/ui';
import { useTranslation, cn } from '@whitbread-eos/utils';
import { useMemo, useState, useEffect, ReactNode } from 'react';

import {
  DataTableColumn,
  DataTableRow,
  headerCellStyle,
  noHoverStyle,
} from '~components/innBusiness/DataTable';
import DataTableBody from '~components/innBusiness/DataTable/data-table-body';
import { TextWithInfoTooltip } from '~components/innBusiness/TextWithInfoTooltip';

import {
  formatAmount,
  getCurrencyCodeBasedOnCurrencySymbol,
} from '../../../statements/utils/format-amount';
import TransactionsExpandableRow from './transactions-expandable-row';

type TransactionsTableClientProps = {
  items: DataTableRow[];
  icons: Record<string, string>;
  locale: LOCALES;
  actions?: ReactNode;
};

const TransactionsTableClient: React.FC<TransactionsTableClientProps> = ({
  items,
  icons,
  locale,
  actions,
}) => {
  const baseDataTestId = 'TransactionsTable';
  const { t } = useTranslation('spending');
  const [isMobileView, setIsMobileView] = useState(false);

  useEffect(() => {
    const handleResize = () => {
      setIsMobileView(window.innerWidth < 1280);
    };
    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const columns = useMemo(() => createColumns(t, isMobileView, locale), [t, isMobileView, locale]);
  const noResultsComponent = useMemo(
    () =>
      renderNoResults(
        t('spending.transactions.noTransactions.title'),
        t('spending.transactions.noTransactions.description')
      ),
    [t]
  );

  return (
    <div data-testid={`${baseDataTestId}-container`} className="overflow-hidden">
      <div className="mb-6 flex items-start gap-4">
        <div className="min-w-0 flex-1">
          {renderSectionTitle(
            t('spending.transactions.outstanding.title'),
            t('spending.transactions.outstanding.tooltip'),
            `${baseDataTestId}-Title`,
            locale,
            icons
          )}
        </div>
        {actions ? <div className="shrink-0 ml-auto">{actions}</div> : null}
      </div>

      <div className="overflow-x-auto">
        {items.length === 0 ? (
          noResultsComponent
        ) : (
          <Table data-testid={baseDataTestId} className="mobile:table-fixed tablet:table-fixed">
            <TableHeader data-testid={`${baseDataTestId}-header`}>
              <TableRow data-testid={`${baseDataTestId}-row`} className={noHoverStyle}>
                {columns.map((column: DataTableColumn) => (
                  <TableHead
                    data-testid={`${baseDataTestId}-head-${column.id}`}
                    key={column.id}
                    className={cn(headerCellStyle, column.headerClassName)}
                  >
                    {column.label}
                  </TableHead>
                ))}
                <TableHead
                  data-testid={`${baseDataTestId}-head-expandable`}
                  className={cn(headerCellStyle, 'w-[50px]')}
                />
              </TableRow>
            </TableHeader>
            <DataTableBody
              baseDataTestId={baseDataTestId}
              rows={items}
              columns={columns}
              isExpendableWith="itemisations"
              isMobileView={isMobileView}
              expandableRowComponent={TransactionsExpandableRow}
            />
          </Table>
        )}
      </div>
    </div>
  );
};

export default TransactionsTableClient;

const renderSectionTitle = (
  title: string,
  info: string,
  testId: string,
  locale: LOCALES,
  icons: Record<string, string>
) => (
  <div className="flex flex-wrap items-center gap-2" data-testid={`${testId}-Container`}>
    <TextWithInfoTooltip
      baseDataTestId={testId}
      mainClassName={getSectionTitleClassName(title)}
      mainText={title}
      infoText={info}
      locale={locale}
      icons={icons}
      iconSize={26}
    />
  </div>
);

const createColumns = (
  t: (key: string) => string,
  isMobileView: boolean,
  locale: LOCALES
): DataTableColumn[] => [
  {
    id: 'date',
    label: t('spending.transactions.header.date'),
    headerClassName: dateColumnClass,
    className: dateColumnClass,
    render(_, row) {
      const date = new Date(row.date);
      const day = date.getDate().toString().padStart(2, '0');
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const year = date.getFullYear().toString().slice(-2);
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');

      return (
        <>
          <div className="sm:hidden space-y-0">
            <div>{`${day}/${month}/${year}`}</div>
            <div>{`${hours}:${minutes}`}</div>
          </div>
          <span className="hidden sm:block">{`${day}/${month}/${year} ${hours}:${minutes}`}</span>
        </>
      );
    },
  },
  {
    id: 'cardHolder',
    label: t('spending.transactions.header.cardHolder'),
    headerClassName: cardHolderColumnClass,
    className: cardHolderColumnClass,
    render(_, row) {
      const parts = String(row.cardHolder).split(' ');
      if (parts.length >= 4) {
        const firstPart = parts.slice(0, Math.ceil(parts.length / 2)).join(' ');
        const secondPart = parts.slice(Math.ceil(parts.length / 2)).join(' ');
        return (
          <div className="space-y-0">
            <div>{firstPart}</div>
            <div>{secondPart}</div>
          </div>
        );
      }
      return row.cardHolder;
    },
  },
  {
    id: 'cardNo',
    label: t('spending.transactions.header.cardNo'),
    headerClassName: cardNumberColumnHeaderClass,
    className: cardNumberColumnClass,
  },
  {
    id: 'location',
    label: t('spending.transactions.header.location'),
    headerClassName: locationColumnHeaderClass,
    className: locationColumnClass,
    render(_, row) {
      const parts = String(row.location).split(' ');
      const shouldSplit = isMobileView ? parts.length >= 2 : parts.length >= 4;
      if (shouldSplit) {
        const firstPart = parts.slice(0, Math.ceil(parts.length / 2)).join(' ');
        const secondPart = parts.slice(Math.ceil(parts.length / 2)).join(' ');
        return (
          <div className="space-y-0">
            <div>{firstPart}</div>
            <div>{secondPart}</div>
          </div>
        );
      }
      return row.location;
    },
  },
  {
    id: 'purchaseOrder',
    label: t('spending.transactions.header.purchaseOrder'),
    headerClassName: purchaseOrderColumnHeaderClass,
    className: purchaseOrderColumnClass,
  },
  {
    id: 'customerRef',
    label: t('spending.transactions.header.customerRef'),
    headerClassName: customerRefColumnHeaderClass,
    className: customerRefColumnClass,
  },
  {
    id: 'grossValue',
    label: t('spending.transactions.header.grossValue'),
    headerClassName: grossValueColumnClass,
    className: grossValueColumnClass,
    render(_, row) {
      const grossValue = row.grossValue;
      if (grossValue && typeof grossValue === 'object') {
        return formatAmount(
          grossValue.amount,
          getCurrencyCodeBasedOnCurrencySymbol(grossValue.currencySymbol || ''),
          locale
        );
      }
      return grossValue;
    },
  },
];

const renderNoResults = (title: string, description: string) => (
  <div className="flex w-full justify-start py-16 px-12">
    <div className="w-full text-left space-y-3 max-w-[28rem]">
      <h3 className="font-black text-2xl leading-7 text-secondaryColor">{title}</h3>
      <p className="text-secondaryColor text-opacity-70 leading-6 text-base">{description}</p>
    </div>
  </div>
);

const sectionTitle = 'font-bold text-xl leading-7 text-black break-words text-left';
const longTitleMobileClass = 'mobile:text-lg mobile:leading-6';

const getSectionTitleClassName = (title: string) => {
  const condensedLength = title.replace(/\s+/g, '').length;
  return condensedLength > 18 ? `${sectionTitle} ${longTitleMobileClass}` : sectionTitle;
};
const dateColumnClass = 'w-[180px] mobile:w-full';
const cardHolderColumnClass = 'w-[220px] mobile:hidden';
const cardNumberColumnHeaderClass = 'w-[140px] mobile:hidden';
const cardNumberColumnClass = 'w-[140px] mobile:hidden whitespace-nowrap';
const locationColumnHeaderClass = 'w-[200px] mobile:w-full';
const locationColumnClass = 'w-[200px] mobile:w-full break-words';
const purchaseOrderColumnHeaderClass = 'w-[160px] mobile:hidden';
const purchaseOrderColumnClass = 'w-[160px] mobile:hidden whitespace-nowrap';
const customerRefColumnHeaderClass = 'w-[160px] mobile:hidden';
const customerRefColumnClass = 'w-[160px] mobile:hidden whitespace-nowrap';
const grossValueColumnClass = 'w-[150px] mobile:w-full text-right pr-6 xl:pr-8';
