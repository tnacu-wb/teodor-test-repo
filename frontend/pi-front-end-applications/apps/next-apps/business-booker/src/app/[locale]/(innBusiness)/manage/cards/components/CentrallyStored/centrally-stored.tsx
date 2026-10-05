import { LOCALES, PaymentCardInnB } from '@whitbread-eos/api';
import { Button, CardIcon, Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getTranslations,
  getSearchParams,
  getPathForLocale,
  getCountryLanguageByLocale,
  formatIBAssetsUrl,
  getPaymentCards,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';
import Link from 'next/link';

import { CardNoResults } from '~components/innBusiness/CardNoResults';
import {
  DataTable,
  DataTableCell,
  DataTableColumn,
  DataTableRow,
} from '~components/innBusiness/DataTable';

import { PageSubtitle } from '../../../employees/components/InnBusiness/page-subtitle';
import { Analytics } from '../Analytics/Analytics';
import { CentrallyStoredCardStatus } from './centrally-stored-card-status';

type Props = {
  locale?: LOCALES;
  token: string;
  companyId: string;
};

export async function CentrallyStored({ locale, token, companyId }: Props) {
  const baseDataTestId = 'CentrallyStoredTab';
  const { language } = getCountryLanguageByLocale(locale);

  const [{ t, translations }, searchParams] = await Promise.all([
    getTranslations(language, ['cards', 'icons']),
    getSearchParams(),
  ]);

  const icons = translations?.['icons'] ?? {};

  const getCentrallyStoredTablePage = async (pageIndex: number, pageSize: number) => {
    const result: PaymentCardInnB[] = await getPaymentCards(token, companyId);
    return result?.slice((pageIndex - 1) * pageSize, pageIndex * pageSize) ?? [];
  };

  const getCentrallyStoredTableTotal = async () => {
    const result: PaymentCardInnB[] = await getPaymentCards(token, companyId);
    return result?.length ?? 0;
  };

  const columns: DataTableColumn[] = [
    {
      id: 'cardLabel',
      label: t('cards.cardMgmt.columns.cardLabel'),
      headerClassName: cardLabelHeaderStyle,
    },
    {
      id: 'cardHolderName',
      label: t('cards.cardMgmt.columns.cardHolderName'),
      headerClassName: `${desktopOnlyStyleHeader} md:w-[25%]`,
      className: desktopOnlyStyle,
    },
    {
      id: 'cardNumber',
      label: t('cards.cardMgmt.columns.cardNumber'),
      headerClassName: cardNoHeaderStyle,
      className: cardNoStyle,

      render(cell: DataTableCell, row: DataTableRow) {
        return (
          <>
            <CardIcon type={row.cardType} className={cardIconStyle} icons={icons} />{' '}
            {cell.slice(-8)}
          </>
        );
      },
    },
    {
      id: 'expiryDate',
      label: t('cards.cardMgmt.columns.expiry'),
      headerClassName: `${desktopOnlyStyleHeader} md:w-[12%]`,
      className: desktopOnlyStyle,

      render(cell: DataTableCell) {
        const month = cell.slice(0, 2);
        const year = cell.slice(-2);

        return (
          <>
            {month}/{year}
          </>
        );
      },
    },
    {
      id: 'cardStatus',
      label: t('cards.cardMgmt.columns.cardStatus'),
      headerClassName: `${desktopOnlyStyleHeader} md:w-[18%] lg:w-[15%]`,
      className: desktopOnlyStyle,

      render(_cell: DataTableCell, row: DataTableRow) {
        return <CentrallyStoredCardStatus expiryDate={row.expiryDate} />;
      },
    },
    {
      id: 'actions',
      label: '',
      headerClassName: actionsHeaderStyle,
      className: actionsCellStyle,

      render(_cell: DataTableCell, row: DataTableRow) {
        return (
          <Link className={editStyle} href={getPathForLocale(locale, `manage/cards/${row.cardId}`)}>
            {t('cards.cardMgmt.columns.edit')}
          </Link>
        );
      },
    },
  ];

  return (
    <div data-testid={`${baseDataTestId}-container`}>
      <div className={textStyle}>{t('cards.cardMgmt.tabs.centrallyStored.description')}</div>

      <div className={tableActionsStyle}>
        <div className={tableActionsLeftStyle}>
          <PageSubtitle
            title={t('cards.cardMgmt.cardManagement.title')}
            icons={icons}
            message={t('cards.cardMgmt.cdhCards.tooltipMsg')}
          />
        </div>

        <Link href={getPathForLocale(locale, `manage/cards/add`)}>
          <Button
            variant="default"
            data-testid={`${baseDataTestId}-add-card-button`}
            className={buttonStyle}
          >
            <Image
              alt={t('cards.cardMgmt.newCard.label')}
              src={formatIBAssetsUrl(t('cards.cardMgmt.newCard.icon'))}
              width={22}
              height={18}
              className={buttonIconStyle}
            />
            {t('cards.cardMgmt.newCard.label')}
          </Button>
        </Link>
      </div>

      <div className={tableContainerStyle}>
        <DataTable
          analyticsComponent={Analytics}
          pageIndex={searchParams?.get('pageIndex')}
          columns={columns}
          getPage={getCentrallyStoredTablePage}
          getTotal={getCentrallyStoredTableTotal}
          noResultsComponent={
            <CardNoResults
              locale={locale}
              addCardUrl={getPathForLocale(locale, 'manage/cards/add')}
            />
          }
        />
      </div>
    </div>
  );
}

export function CentrallyStoredSkeleton() {
  return (
    <div data-testid="CentrallyStoredTab-Skeleton">
      <div className={`${tableActionsStyle} flex-col`}>
        <Skeleton className={'h-12 w-full mb-4'} />
        <div className="mt-6 relative w-full">
          <table className="text-base table-auto rounded-lg border border-separate border-spacing-0 border-lightGrey3 w-full font-normal text-4 mobile:table-fixed tablet:table-fixed">
            <thead className="bg-successTint px-4 py-2 h-10 font-normal">
              <tr>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:w-full tablet:w-full">
                  <Skeleton className="h-5 w-24" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:hidden tablet:hidden">
                  <Skeleton className="h-5 w-24" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate w-[160px]">
                  <Skeleton className="h-5 w-20" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:hidden tablet:hidden">
                  <Skeleton className="h-5 w-16" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:hidden tablet:hidden">
                  <Skeleton className="h-5 w-20" />
                </th>
                <th className="font-normal text-left px-4 mobile:truncate tablet:truncate mobile:w-[100px] tablet:w-[100px]">
                  <Skeleton className="h-5 w-10" />
                </th>
              </tr>
            </thead>{' '}
            <tbody>
              {[...Array(6)].map((_, i) => (
                <tr key={i}>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3">
                    <Skeleton className="h-5 w-24" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:hidden tablet:hidden">
                    <Skeleton className="h-5 w-24" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 flex items-center">
                    <Skeleton className="h-6 w-10 mr-2" />
                    <Skeleton className="h-5 w-14" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:hidden tablet:hidden">
                    <Skeleton className="h-5 w-12" />
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3 mobile:hidden tablet:hidden">
                    <div className="flex items-center">
                      <Skeleton className="inline-block h-3 w-3 rounded-md mr-2" />
                      <Skeleton className="h-5 w-12" />
                    </div>
                  </td>
                  <td className="px-4 h-10 border-t-0 py-0 border-b border-lightGrey3">
                    <Skeleton className="h-5 w-10" />
                  </td>
                </tr>
              ))}
              <tr>
                <td className="border-t border-lightGrey2 px-4 h-[60px]" colSpan={6}>
                  <div className="mx-auto flex w-full justify-center gap-2">
                    {[1, 2].map((_, i) => (
                      <Skeleton key={i} className="h-7 w-7 rounded-sm" />
                    ))}
                    <Skeleton className="h-7 w-7 rounded-sm ml-2" />
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

const textStyle = 'text-base font-normal';
const tableActionsStyle =
  'mt-12 flex justify-between items-center mobile:flex-col mobile:items-start';
const tableActionsLeftStyle = 'text-xl font-bold flex';
const tableContainerStyle = 'mt-6';
const cardIconStyle = 'mr-2';
const cardNoHeaderStyle =
  'w-[35%] md:w-[20%] lg:w-[18%] md:table-cell mobile:px-2 mobile:whitespace-normal mobile:break-words mobile:overflow-visible mobile:text-clip tablet:whitespace-normal ib-word-break';
const cardNoStyle = 'flex items-center';
const editStyle = 'text-secondaryColor underline';
const desktopOnlyStyleHeader =
  'hidden md:table-cell mobile:whitespace-normal tablet:whitespace-normal ib-word-break';
const desktopOnlyStyle = 'hidden md:table-cell truncate';
const buttonStyle = 'mobile:w-full mobile:mt-4';
const actionsHeaderStyle = 'w-[30%] md:w-[15%]';
const actionsCellStyle = 'mobile:whitespace-nowrap mobile:overflow-visible mobile:text-clip';
const cardLabelHeaderStyle =
  'w-[35%] md:w-[25%] mobile:whitespace-normal tablet:whitespace-normal ib-word-break';
const buttonIconStyle = 'mr-2';
